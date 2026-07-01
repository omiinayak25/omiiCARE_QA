#!/usr/bin/env python3
"""
failure_triage.py — parse test results, group failures, draft RCA stubs.

Consumes the REAL result formats produced by this repo:
  - Maven Surefire XML  (automation/target/surefire-reports/*.xml,
                         apps/backend/target/surefire-reports/*.xml)
  - Playwright JSON reporter (automation/playwright/results*/results.json,
                         including the nested suites/specs/tests tree)

It auto-detects the format, extracts failures/errors, groups them by a
heuristic failure signature (exception type / first message line), and emits
a triage table plus a per-group RCA stub drafted through the pluggable AI
seam (offline 'stub' by default).

Std-lib only (xml.etree, json). No network. PHI-safe: only failure metadata
is read; no patient data is involved.

Exit code: 0 if no failures, 2 if any failure/error found (CI gate).
Use --soft to always exit 0.

Usage:
  python3 ai/qe/failure_triage.py                 # auto-discover results
  python3 ai/qe/failure_triage.py path/to/results.json --rca
  python3 ai/qe/failure_triage.py target/surefire-reports --json
"""
from __future__ import annotations

import argparse
import json
import re
import sys
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path
from typing import Dict, List, Optional

from _common import (BACKEND_SUREFIRE_DIR, PLAYWRIGHT_OMIICARE_JSON,
                     PLAYWRIGHT_OPENMRS_JSON, SUREFIRE_DIR, ai_complete, eprint,
                     md_table, resolve)

# A failure record is a flat dict: suite, test, status, type, message, source.


def _first_line(text: str) -> str:
    for ln in (text or "").splitlines():
        ln = ln.strip()
        if ln:
            return ln
    return ""


def signature(rec: Dict[str, str]) -> str:
    """Group key: exception class if present, else normalized first message."""
    msg = rec.get("message", "") or ""
    m = re.search(r"([A-Za-z_][\w.]*(?:Exception|Error|AssertionFailedError))", msg)
    if m:
        return m.group(1).split(".")[-1]
    line = _first_line(msg)
    # Strip volatile bits (numbers, hex, timestamps) so like failures group.
    line = re.sub(r"0x[0-9a-fA-F]+|\b\d+\b|@[0-9a-f]+", "#", line)
    return (line[:80] or rec.get("type") or "unknown").strip()


# --------------------------------------------------------------------------
# Surefire XML
# --------------------------------------------------------------------------
def parse_surefire_file(path: Path) -> List[Dict[str, str]]:
    recs: List[Dict[str, str]] = []
    try:
        root = ET.parse(path).getroot()
    except ET.ParseError as e:
        eprint(f"WARN: cannot parse {path}: {e}")
        return recs
    suites = [root] if root.tag == "testsuite" else root.iter("testsuite")
    for ts in suites:
        suite_name = ts.get("name", path.stem)
        for tc in ts.iter("testcase"):
            for kind in ("failure", "error"):
                node = tc.find(kind)
                if node is not None:
                    recs.append({
                        "suite": suite_name,
                        "test": tc.get("name", "?"),
                        "status": kind,
                        "type": node.get("type", ""),
                        "message": node.get("message") or (node.text or ""),
                        "source": str(path),
                    })
    return recs


# --------------------------------------------------------------------------
# Playwright JSON
# --------------------------------------------------------------------------
def parse_playwright_json(path: Path) -> List[Dict[str, str]]:
    recs: List[Dict[str, str]] = []
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as e:
        eprint(f"WARN: cannot parse {path}: {e}")
        return recs

    def walk(suite, trail):
        title = suite.get("title", "")
        new_trail = trail + [title] if title else trail
        for spec in suite.get("specs", []):
            for test in spec.get("tests", []):
                for res in test.get("results", []):
                    status = res.get("status", "")
                    if status in ("failed", "timedOut", "interrupted"):
                        err = res.get("error", {}) or {}
                        msg = err.get("message", "") or "".join(
                            e.get("message", "") for e in res.get("errors", []))
                        recs.append({
                            "suite": " > ".join(new_trail) or spec.get("file", ""),
                            "test": spec.get("title", "?"),
                            "status": status,
                            "type": "playwright",
                            "message": msg,
                            "source": str(path),
                        })
        for sub in suite.get("suites", []):
            walk(sub, new_trail)

    for top in data.get("suites", []):
        walk(top, [])
    return recs


# --------------------------------------------------------------------------
# Dispatch / discovery
# --------------------------------------------------------------------------
def parse_target(target: Path) -> List[Dict[str, str]]:
    if target.is_dir():
        recs: List[Dict[str, str]] = []
        for xml in sorted(target.glob("TEST-*.xml")):
            recs += parse_surefire_file(xml)
        for js in sorted(target.glob("**/results.json")):
            recs += parse_playwright_json(js)
        return recs
    if target.suffix == ".xml":
        return parse_surefire_file(target)
    if target.suffix == ".json":
        return parse_playwright_json(target)
    eprint(f"WARN: unrecognized result target: {target}")
    return []


def autodiscover(root: Path) -> List[Path]:
    candidates = [resolve(SUREFIRE_DIR, root), resolve(BACKEND_SUREFIRE_DIR, root),
                  resolve(PLAYWRIGHT_OPENMRS_JSON, root),
                  resolve(PLAYWRIGHT_OMIICARE_JSON, root)]
    return [c for c in candidates if c.exists()]


def main(argv=None) -> int:
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("targets", nargs="*",
                    help="Result files/dirs. Empty = auto-discover repo results.")
    ap.add_argument("--rca", action="store_true",
                    help="Draft an AI-stub RCA per failure group")
    ap.add_argument("--json", action="store_true")
    ap.add_argument("--format", choices=["text", "md"], default="text")
    ap.add_argument("--soft", action="store_true", help="Always exit 0")
    ap.add_argument("--root", help="Override repo root")
    args = ap.parse_args(argv)

    root = Path(args.root).resolve() if args.root else resolve(SUREFIRE_DIR).parents[2]

    targets = [Path(t) for t in args.targets] or autodiscover(root)
    if not targets:
        eprint("No result files found. Run tests first, or pass a path.")
        return 1

    all_recs: List[Dict[str, str]] = []
    for t in targets:
        all_recs += parse_target(t)

    groups: Dict[str, List[Dict[str, str]]] = defaultdict(list)
    for r in all_recs:
        groups[signature(r)].append(r)

    ordered = sorted(groups.items(), key=lambda kv: -len(kv[1]))
    out_groups = []
    for sig, recs in ordered:
        g = {
            "signature": sig,
            "count": len(recs),
            "tests": [f"{r['suite']}::{r['test']}" for r in recs][:20],
            "example_message": _first_line(recs[0].get("message", ""))[:300],
        }
        if args.rca:
            g["rca_stub"] = ai_complete(
                f"Failure signature '{sig}' hit {len(recs)} test(s). "
                f"Example: {g['example_message']}. "
                "Draft a one-line root-cause hypothesis + RCA category "
                "(requirements|design|coding|data|config|environment|"
                "integration|test-gap).",
                system="You are an SDET triaging a failing CI run.")
        out_groups.append(g)

    payload = {
        "sources": [str(t) for t in targets],
        "total_failures": len(all_recs),
        "groups": out_groups,
    }

    if args.json:
        print(json.dumps(payload, indent=2))
    else:
        print(f"# Failure Triage — {len(all_recs)} failure(s) in "
              f"{len(out_groups)} group(s)")
        print(f"  sources: {', '.join(str(t) for t in targets)}\n")
        if not all_recs:
            print("No failures or errors found. Green run.")
        else:
            headers = ["#", "Signature", "Count", "Example message"]
            rows = [[i + 1, g["signature"], g["count"], g["example_message"][:60]]
                    for i, g in enumerate(out_groups)]
            print(md_table(headers, rows) if args.format == "md"
                  else "\n".join(f"{i+1}. [{g['count']}] {g['signature']}  "
                                 f"— {g['example_message'][:70]}"
                                 for i, g in enumerate(out_groups)))
            for g in out_groups:
                if g.get("rca_stub"):
                    print(f"  RCA[{g['signature']}]: {g['rca_stub'].splitlines()[-1]}")

    if args.soft:
        return 0
    return 2 if all_recs else 0


if __name__ == "__main__":
    sys.exit(main())
