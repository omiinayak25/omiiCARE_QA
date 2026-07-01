#!/usr/bin/env python3
"""
regression_impact.py — map changed modules/paths -> impacted test-case IDs.

Given a set of changed code paths or module tokens, this tool resolves the
blast radius into concrete manual test-case IDs (TC-*) and the requirements
they trace (REQ-*), using the REAL repo artifacts:

  - manual-testing/rtm/RTM.csv                          (REQ -> TC mapping)
  - manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv (TC -> Module/Req)

Module resolution is prefix/keyword based (std-lib only, no network):
  1. Explicit module tokens (e.g. AUTH, APPT, FHIR) match the TC/REQ infix.
  2. File paths are mapped to module tokens via a small, editable keyword map
     plus a generic path-segment heuristic, so changed source files resolve
     to the modules whose tests should re-run.

Output: the impacted TC IDs (the regression set to schedule), grouped by
module, plus the requirements at risk. Optional --explain drafts a short
risk note via the pluggable AI stub (offline by default).

Usage:
  python3 ai/qe/regression_impact.py --paths apps/backend/.../AuthController.java
  python3 ai/qe/regression_impact.py --modules AUTH,APPT --json
  python3 ai/qe/regression_impact.py --git-range HEAD~1..HEAD   # uses `git diff`
"""
from __future__ import annotations

import argparse
import json
import subprocess
import sys
from collections import defaultdict
from pathlib import Path
from typing import Dict, List, Set

from _common import (ALL_TEST_CASES_CSV, RTM_CSV, ai_complete, eprint, md_table,
                     read_csv_dicts, resolve, split_ids, tc_prefix)

# Editable keyword -> module-token map. Extend as the SUT grows. Keys are
# matched case-insensitively as substrings of the changed path.
# Values MUST be real module tokens present in the repo's TC/REQ IDs
# (see manual-testing/test-cases/openmrs/*.csv). A path may map to several.
PATH_KEYWORD_MAP = {
    "auth": "AUTH", "login": "AUTH", "session": "AUTH",
    "security": "SEC", "rbac": "RBAC",
    "patient": "REG", "register": "REG", "registration": "REG",
    "search": "SRCH", "identif": "IDENT",
    "appointment": "APPT", "schedul": "SCHED2",
    "encounter": "ENC", "visit": "VISIT", "vital": "VITAL",
    "billing": "BILL", "invoice": "BILL", "payment": "BILL", "insur": "INS",
    "allerg": "ALLERG2", "drug": "DRUG", "pharm": "PHARM",
    "order": "ORDLAB", "prescrib": "DRUG", "lab": "LAB2", "radiolog": "RAD",
    "fhir": "FHIR", "hl7": "HL7", "rest": "RESTAPI", "api": "RESTAPI",
    "consent": "CONSENT", "audit": "AUDIT2", "death": "DEATH",
    "dashboard": "DASH2", "config": "CONFIG", "clinical": "CLIN",
    "immun": "IMM", "program": "PROG", "provider": "PROV", "referr": "REFER",
    "portal": "PORTAL", "mobile": "MOBILE", "notif": "NOTIF",
    "report": "RPT", "telehealth": "TELE", "tele": "TELE",
    "a11y": "A11Y", "accessib": "A11Y",
    "frontend": "PORTAL", "react": "PORTAL", "vite": "PORTAL",
}


def changed_paths_from_git(git_range: str, root: Path) -> List[str]:
    try:
        out = subprocess.run(
            ["git", "-C", str(root), "diff", "--name-only", git_range],
            capture_output=True, text=True, timeout=30)
    except (OSError, subprocess.SubprocessError) as e:
        eprint(f"WARN: git diff failed ({e}); pass --paths instead")
        return []
    if out.returncode != 0:
        eprint(f"WARN: git diff returned {out.returncode}: {out.stderr.strip()}")
        return []
    return [ln.strip() for ln in out.stdout.splitlines() if ln.strip()]


def paths_to_modules(paths: List[str]) -> Dict[str, Set[str]]:
    """Return module-token -> set(matched paths). Unmatched -> '_UNMAPPED'."""
    resolved: Dict[str, Set[str]] = defaultdict(set)
    for p in paths:
        low = p.lower()
        hit = False
        for kw, mod in PATH_KEYWORD_MAP.items():
            if kw in low:
                resolved[mod].add(p)
                hit = True
        if not hit:
            resolved["_UNMAPPED"].add(p)
    return resolved


def build_indexes(root: Path):
    """Build module-token -> {tc_ids, req_ids} from RTM + ALL_TEST_CASES."""
    rtm = read_csv_dicts(resolve(RTM_CSV, root))
    cases = read_csv_dicts(resolve(ALL_TEST_CASES_CSV, root))

    tc_by_module: Dict[str, Set[str]] = defaultdict(set)
    req_by_module: Dict[str, Set[str]] = defaultdict(set)
    tc_to_req: Dict[str, str] = {}

    for c in cases:
        tc = (c.get("TC_ID") or "").strip()
        if not tc:
            continue
        mod = tc_prefix(tc)
        tc_by_module[mod].add(tc)
        req = (c.get("Requirement_ID") or "").strip()
        if req:
            tc_to_req[tc] = req
            req_by_module[mod].add(req)

    # RTM also carries the authoritative REQ->TC list; fold it in so we cover
    # any TC referenced by a requirement even if absent from the flat export.
    for r in rtm:
        req = (r.get("Requirement_ID") or "").strip()
        mod = tc_prefix(req)
        if req:
            req_by_module[mod].add(req)
        for tc in split_ids(r.get("TestCase_IDs", "")):
            tc_by_module[mod].add(tc)
            tc_to_req.setdefault(tc, req)
    return tc_by_module, req_by_module, tc_to_req


def main(argv=None) -> int:
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    src = ap.add_argument_group("change source (combine freely)")
    src.add_argument("--paths", nargs="*", default=[],
                     help="Changed file paths (space-separated)")
    src.add_argument("--modules", default="",
                     help="Comma-separated module tokens, e.g. AUTH,APPT")
    src.add_argument("--git-range",
                     help="Resolve changed paths via `git diff --name-only`")
    ap.add_argument("--json", action="store_true")
    ap.add_argument("--format", choices=["text", "md"], default="text")
    ap.add_argument("--explain", action="store_true",
                    help="AI-stub risk note for the impacted set")
    ap.add_argument("--root", help="Override repo root")
    args = ap.parse_args(argv)

    root = Path(args.root).resolve() if args.root else resolve(RTM_CSV).parents[2]

    paths = list(args.paths)
    if args.git_range:
        paths += changed_paths_from_git(args.git_range, root)

    requested_modules = {m.strip().upper() for m in args.modules.split(",") if m.strip()}
    path_modules = paths_to_modules(paths)
    for m in path_modules:
        if m != "_UNMAPPED":
            requested_modules.add(m)

    if not requested_modules and not path_modules.get("_UNMAPPED"):
        eprint("No modules/paths given. Use --paths, --modules, or --git-range.")
        return 1

    try:
        tc_by_module, req_by_module, tc_to_req = build_indexes(root)
    except FileNotFoundError as e:
        eprint(f"ERROR: {e}")
        return 1

    impacted = {}
    total_tc: Set[str] = set()
    total_req: Set[str] = set()
    for mod in sorted(requested_modules):
        tcs = sorted(tc_by_module.get(mod, set()))
        reqs = sorted(req_by_module.get(mod, set()))
        impacted[mod] = {"test_cases": tcs, "requirements": reqs,
                         "source_paths": sorted(path_modules.get(mod, set()))}
        total_tc.update(tcs)
        total_req.update(reqs)

    unmapped = sorted(path_modules.get("_UNMAPPED", set()))
    unknown_mods = [m for m in requested_modules if not tc_by_module.get(m)]

    payload = {
        "changed_paths": paths,
        "modules_impacted": sorted(requested_modules),
        "total_test_cases": len(total_tc),
        "total_requirements": len(total_req),
        "impacted": impacted,
        "unmapped_paths": unmapped,
        "modules_with_no_tests": unknown_mods,
    }
    if args.explain:
        payload["risk_note"] = ai_complete(
            f"Changed modules {sorted(requested_modules)} impact "
            f"{len(total_tc)} test cases / {len(total_req)} requirements. "
            "Draft a one-line regression-risk note and a recommended run tag.",
            system="You are a release QA lead choosing a regression slice.")

    if args.json:
        print(json.dumps(payload, indent=2))
        return 0

    print(f"# Regression Impact — {len(total_tc)} test case(s), "
          f"{len(total_req)} requirement(s) across "
          f"{len(impacted)} module(s)\n")
    headers = ["Module", "#TC", "#REQ", "Sample TC IDs"]
    rows = [[m, len(d["test_cases"]), len(d["requirements"]),
             ", ".join(d["test_cases"][:4]) + (" ..." if len(d["test_cases"]) > 4 else "")]
            for m, d in impacted.items()]
    print(md_table(headers, rows) if args.format == "md" else _text_table(rows))
    if unmapped:
        print(f"\nUnmapped changed paths (extend PATH_KEYWORD_MAP): {len(unmapped)}")
        for p in unmapped[:10]:
            print(f"  - {p}")
    if unknown_mods:
        print(f"\nRequested modules with no test cases found: {unknown_mods}")
    if payload.get("risk_note"):
        print(f"\nRisk note: {payload['risk_note'].splitlines()[-1]}")
    print("\nSuggested run: mvn -pl automation -Pe2e test  "
          "(filter by the impacted module tags above)")
    return 0


def _text_table(rows):
    out = [f"{'MODULE':<10}{'#TC':>6}{'#REQ':>6}  SAMPLE TC IDs"]
    for m, ntc, nreq, sample in rows:
        out.append(f"{m:<10}{ntc:>6}{nreq:>6}  {sample}")
    return "\n".join(out)


if __name__ == "__main__":
    sys.exit(main())
