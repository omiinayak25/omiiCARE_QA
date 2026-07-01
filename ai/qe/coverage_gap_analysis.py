#!/usr/bin/env python3
"""
coverage_gap_analysis.py — flag thin / risky test coverage.

Reads the REAL repo artifacts:
  - manual-testing/rtm/RTM.csv          (Requirement_ID, Module, Priority,
                                         Risk, TestCase_Count, TestCase_IDs)
  - docs/requirements/requirements-catalog.md  (sanity cross-check on totals)

Reports, with zero network and std-lib only:
  - Requirements with thin coverage (fewer tests than a risk-based threshold).
  - Per-module coverage roll-up (avg tests/req, count of thin reqs).
  - High-risk / P1 requirements that are under-covered (the dangerous gaps).

Risk-based thresholds (configurable via CLI):
  High risk   -> expect >= 3 test cases
  Medium risk -> expect >= 2 test cases
  Low risk    -> expect >= 1 test case

The optional AI step (--explain) drafts a short remediation note per gap via
the pluggable stub in _common.ai_complete (offline by default).

Exit code: 0 if no gaps above the failing threshold, 2 if gaps found
(useful as a CI quality gate). Use --soft to always exit 0.

Usage:
  python3 ai/qe/coverage_gap_analysis.py
  python3 ai/qe/coverage_gap_analysis.py --module AUTH --format md
  python3 ai/qe/coverage_gap_analysis.py --json --soft
"""
from __future__ import annotations

import argparse
import json
import sys
from collections import defaultdict
from pathlib import Path
from typing import Dict, List

from _common import (RTM_CSV, REQUIREMENTS_CATALOG, ai_complete, eprint,
                     md_table, read_csv_dicts, resolve, split_ids)

DEFAULT_THRESHOLDS = {"High": 3, "Medium": 2, "Low": 1}


def expected_for(risk: str, thresholds: Dict[str, int]) -> int:
    return thresholds.get((risk or "").strip().title(), 1)


def load_rtm(root: Path) -> List[Dict[str, str]]:
    rows = read_csv_dicts(resolve(RTM_CSV, root))
    norm = []
    for r in rows:
        ids = split_ids(r.get("TestCase_IDs", ""))
        # Prefer the explicit count column, fall back to parsed IDs.
        try:
            count = int(r.get("TestCase_Count", "") or 0)
        except ValueError:
            count = 0
        if not count:
            count = len(ids)
        norm.append({
            "req": r.get("Requirement_ID", "").strip(),
            "module": r.get("Module", "").strip(),
            "title": r.get("Requirement_Title", "").strip(),
            "priority": (r.get("Priority", "") or "").strip(),
            "risk": (r.get("Risk", "") or "").strip(),
            "count": count,
            "ids": ids,
        })
    return norm


def analyze(rows, thresholds):
    gaps = []
    per_module = defaultdict(lambda: {"reqs": 0, "tests": 0, "thin": 0})
    for r in rows:
        exp = expected_for(r["risk"], thresholds)
        m = per_module[r["module"]]
        m["reqs"] += 1
        m["tests"] += r["count"]
        if r["count"] < exp:
            m["thin"] += 1
            gaps.append({**r, "expected": exp, "deficit": exp - r["count"]})
    # Rank gaps: highest deficit first, then P1/High to the top.
    pri_rank = {"P1": 0, "P2": 1, "P3": 2, "P4": 3}
    risk_rank = {"High": 0, "Medium": 1, "Low": 2}
    gaps.sort(key=lambda g: (-g["deficit"],
                             risk_rank.get(g["risk"].title(), 9),
                             pri_rank.get(g["priority"].upper(), 9)))
    return gaps, per_module


def catalog_total(root: Path) -> int:
    """Best-effort cross-check against the catalog header line."""
    p = resolve(REQUIREMENTS_CATALOG, root)
    if not p.is_file():
        return -1
    for line in p.read_text(encoding="utf-8").splitlines()[:10]:
        for tok in line.replace(",", "").split():
            if tok.isdigit() and int(tok) > 100:
                return int(tok)
    return -1


def main(argv=None) -> int:
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--module", help="Filter to one module (exact or substring)")
    ap.add_argument("--format", choices=["text", "md"], default="text")
    ap.add_argument("--json", action="store_true", help="Emit JSON instead")
    ap.add_argument("--explain", action="store_true",
                    help="Add an AI-stub remediation note per gap")
    ap.add_argument("--top", type=int, default=25, help="Max gaps to show")
    ap.add_argument("--high-only", type=int, default=2, metavar="DEFICIT",
                    help="Exit non-zero only if a gap's deficit >= this")
    ap.add_argument("--soft", action="store_true", help="Always exit 0")
    ap.add_argument("--root", help="Override repo root")
    args = ap.parse_args(argv)

    root = Path(args.root).resolve() if args.root else None
    try:
        rows = load_rtm(root)
    except FileNotFoundError as e:
        eprint(f"ERROR: {e}")
        return 1
    if root is None:
        root = resolve(RTM_CSV).parents[2]

    if args.module:
        key = args.module.lower()
        rows = [r for r in rows
                if key in r["module"].lower() or key == r["req"].split("-")[1:2].__str__()]
        rows = [r for r in rows if key in r["module"].lower()
                or key in r["req"].lower()] or rows

    gaps, per_module = analyze(rows, DEFAULT_THRESHOLDS)

    if args.explain:
        for g in gaps[:args.top]:
            g["remediation"] = ai_complete(
                f"Requirement {g['req']} ({g['risk']} risk, {g['priority']}) "
                f"has {g['count']} test(s), expected {g['expected']}. "
                f"Title: {g['title']}. Draft a one-line coverage remediation.",
                system="You are a risk-based QA lead.")

    payload = {
        "rtm_requirements": len(rows),
        "catalog_total_reported": catalog_total(root),
        "thresholds": DEFAULT_THRESHOLDS,
        "total_gaps": len(gaps),
        "modules_with_gaps": sum(1 for m in per_module.values() if m["thin"]),
        "gaps": gaps[:args.top],
        "module_rollup": {k: v for k, v in sorted(per_module.items())},
    }

    if args.json:
        print(json.dumps(payload, indent=2))
    else:
        _render(payload, args.format)

    worst = max((g["deficit"] for g in gaps), default=0)
    if args.soft:
        return 0
    return 2 if worst >= args.high_only else 0


def _render(p, fmt):
    print(f"# Coverage Gap Analysis (RTM: {p['rtm_requirements']} requirements)")
    rep = p["catalog_total_reported"]
    if rep > 0 and rep != p["rtm_requirements"]:
        print(f"  note: requirements-catalog reports {rep} requirements "
              f"(RTM rows: {p['rtm_requirements']}) — investigate delta")
    print(f"  thresholds (tests by risk): {p['thresholds']}")
    print(f"  total gaps: {p['total_gaps']} across "
          f"{p['modules_with_gaps']} module(s)\n")
    headers = ["Requirement", "Module", "Pri", "Risk", "Have", "Need", "Deficit"]
    rows = [[g["req"], g["module"][:28], g["priority"], g["risk"],
             g["count"], g["expected"], g["deficit"]] for g in p["gaps"]]
    if fmt == "md":
        print(md_table(headers, rows))
    else:
        print(f"{'REQUIREMENT':<18}{'PRI':<5}{'RISK':<8}{'HAVE':>5}{'NEED':>5}{'DEF':>5}  MODULE")
        for g in p["gaps"]:
            print(f"{g['req']:<18}{g['priority']:<5}{g['risk']:<8}"
                  f"{g['count']:>5}{g['expected']:>5}{g['deficit']:>5}  {g['module'][:30]}")
    for g in p["gaps"]:
        if g.get("remediation"):
            print(f"  ~ {g['req']}: {g['remediation'].splitlines()[-1]}")
    if not p["gaps"]:
        print("No coverage gaps above threshold. (RTM reports 0 untraced.)")


if __name__ == "__main__":
    sys.exit(main())
