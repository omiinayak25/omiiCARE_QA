#!/usr/bin/env python3
"""
bug_report_drafter.py — turn a failure record into a filled bug template.

Renders a defect using the REAL repo template structure:
  manual-testing/bug-templates/BUG_REPORT_TEMPLATE.md
(the `OMII-BUG-NNNN` markdown table + Preconditions/Steps/Expected/Actual/
Evidence/Logs/Suspected-Root-Cause sections).

Input is a single failure record as JSON (a stdin object, a --json-file, or
one of the grouped objects emitted by failure_triage.py --json). Fields are
mapped to template fields with safe defaults; the AI seam (offline stub by
default) drafts the summary and suspected-root-cause lines.

Std-lib only, no network. Synthetic/PHI-safe data only — never paste real
patient data into a defect.

Usage:
  python3 ai/qe/failure_triage.py --json | \
      python3 ai/qe/bug_report_drafter.py --from-triage
  python3 ai/qe/bug_report_drafter.py --json-file failure.json
  echo '{"test":"...","message":"...","module":"appointment"}' | \
      python3 ai/qe/bug_report_drafter.py
"""
from __future__ import annotations

import argparse
import json
import sys
from datetime import datetime, timezone
from pathlib import Path
from typing import Dict, List

from _common import ai_complete, eprint, resolve, tc_prefix

# Map module token / keyword -> the template's Module / Component vocabulary.
MODULE_VOCAB = {
    "AUTH": "auth", "PAT": "patient", "APPT": "appointment",
    "ENC": "encounter", "BILL": "billing", "FHIR": "FHIR",
    "HL7": "FHIR", "UI": "frontend", "A11Y": "frontend",
}

SEVERITY_BY_RISK = {"High": "S2", "Medium": "S3", "Low": "S4"}
PRIORITY_BY_RISK = {"High": "P1", "Medium": "P2", "Low": "P3"}


def derive_module(rec: Dict) -> str:
    raw = (rec.get("module") or "").strip()
    if raw:
        token = raw.upper() if len(raw) <= 6 else ""
        if token in MODULE_VOCAB:
            return MODULE_VOCAB[token]
        low = raw.lower()
        for tok, vocab in MODULE_VOCAB.items():
            if tok.lower() in low or vocab in low:
                return vocab
        return raw
    # Try to read the module from a TC id or suite/test path.
    for field in ("test_case_ref", "tc_id", "test", "suite"):
        val = rec.get(field) or ""
        for part in str(val).replace("::", " ").split():
            pref = tc_prefix(part)
            if pref in MODULE_VOCAB:
                return MODULE_VOCAB[pref]
    return "unknown"


def steps_from_record(rec: Dict) -> List[str]:
    explicit = rec.get("steps")
    if isinstance(explicit, list) and explicit:
        return [str(s) for s in explicit]
    suite = rec.get("suite", "")
    test = rec.get("test", "")
    return [
        f"Run the failing test: {suite}::{test}".strip(": "),
        "Use the documented environment/seed data for this module.",
        "Observe the assertion/exception below.",
    ]


def draft(rec: Dict, *, bug_no: str, build: str, env: str,
          reporter: str, cycle: str) -> str:
    module = derive_module(rec)
    risk = (rec.get("risk") or "High").title()
    sev = SEVERITY_BY_RISK.get(risk, "S2")
    pri = PRIORITY_BY_RISK.get(risk, "P1")
    test_label = f"{rec.get('suite','')}::{rec.get('test','')}".strip(": ")
    raw_msg = (rec.get("message") or rec.get("example_message") or "").strip()
    first_msg = raw_msg.splitlines()[0][:300] if raw_msg else "(no message captured)"

    summary = ai_complete(
        f"Test '{test_label}' failed with: {first_msg}. "
        "Write a one-line defect summary in the form "
        "'component — observed problem — condition'.",
        system="You are a QA engineer filing a precise defect.")
    summary_line = summary.splitlines()[-1].strip()

    rca = ai_complete(
        f"Failure: {first_msg} in {test_label}. "
        "Give a one-line suspected root cause and pick one RCA category from "
        "requirements|design|coding|data|config|environment|integration|test-gap.",
        system="You are an SDET root-causing a failure.")
    rca_line = rca.splitlines()[-1].strip()

    steps = steps_from_record(rec)
    expected = rec.get("expected") or "Test passes per the asserted behaviour/spec."
    actual = rec.get("actual") or f"{rec.get('status','failed')}: {first_msg}"
    tc_ref = rec.get("test_case_ref") or rec.get("tc_id") or "n/a"
    corr = rec.get("correlation_id") or "n/a"
    repro = rec.get("reproducibility") or ("Intermittent" if rec.get("flaky") else "Always")

    steps_md = "\n".join(f"{i+1}. {s}" for i, s in enumerate(steps))
    log_excerpt = "\n".join(raw_msg.splitlines()[:8]) or "(attach surefire/playwright log slice)"

    return f"""## {bug_no} — {summary_line}

| Field | Value |
|-------|-------|
| Defect ID | {bug_no} |
| Summary | {summary_line} |
| Module / Component | {module} |
| Environment | {env} |
| Build / Version | {build} |
| Severity | {sev} |
| Priority | {pri} |
| Status | New |
| Business Rule | n/a |
| Error code | n/a |
| Reproducibility | {repro} |
| Regression? | Unknown |
| Test Case Ref | {tc_ref} |
| Reporter | {reporter} |
| Assignee | unassigned |
| Found in cycle | {cycle} |

### Preconditions
- Synthetic seed data only (PHI-safe). Use the module's documented fixtures.
- Run on owned env `{env}` (never prod data; never load/attack o2.openmrs.org).

### Steps to Reproduce
{steps_md}

### Expected Result
{expected}

### Actual Result
{actual}

### Evidence
- Surefire/Playwright report: `{rec.get('source','<results file>')}`
- Attach: trace.zip / screenshots / request-response bodies as applicable.

### Logs / Correlation ID
- `X-Request-Id`: {corr}
- Log excerpt:
  ```
  {log_excerpt}
  ```

### Suspected Root Cause
{rca_line}

<!-- Generated by ai/qe/bug_report_drafter.py at {datetime.now(timezone.utc).isoformat()} -->
<!-- Template source: manual-testing/bug-templates/BUG_REPORT_TEMPLATE.md -->
"""


def load_input(args) -> List[Dict]:
    if args.json_file:
        data = json.loads(Path(args.json_file).read_text(encoding="utf-8"))
    else:
        raw = sys.stdin.read()
        if not raw.strip():
            eprint("No input. Pipe a JSON failure record or use --json-file.")
            sys.exit(1)
        data = json.loads(raw)

    if args.from_triage and isinstance(data, dict) and "groups" in data:
        # Expand failure_triage --json: take the first concrete test per group.
        recs = []
        for g in data["groups"]:
            base = {"message": g.get("example_message", ""),
                    "signature": g.get("signature", "")}
            sample = (g.get("tests") or ["?::?"])[0]
            suite, _, test = sample.partition("::")
            base.update({"suite": suite, "test": test, "status": "failed"})
            recs.append(base)
        return recs
    if isinstance(data, list):
        return data
    return [data]


def main(argv=None) -> int:
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--json-file", help="Read the failure record(s) from a file")
    ap.add_argument("--from-triage", action="store_true",
                    help="Input is failure_triage.py --json output")
    ap.add_argument("--start-no", type=int, default=1,
                    help="First OMII-BUG number (default 1)")
    ap.add_argument("--build", default="0.x / local",
                    help="Build/Version field (e.g. 1.0.0 / develop@SHA)")
    ap.add_argument("--env", default="local/docker",
                    choices=["dev", "local/docker", "qa", "stage"])
    ap.add_argument("--reporter", default="QA / SDET")
    ap.add_argument("--cycle", default="current sprint")
    ap.add_argument("--out", help="Write to file instead of stdout")
    args = ap.parse_args(argv)

    # Confirm the template exists (referenced, not parsed) for traceability.
    tmpl = resolve("manual-testing/bug-templates/BUG_REPORT_TEMPLATE.md")
    if not tmpl.is_file():
        eprint(f"NOTE: template not found at {tmpl}; using built-in field set.")

    try:
        records = load_input(args)
    except json.JSONDecodeError as e:
        eprint(f"ERROR: invalid JSON input: {e}")
        return 1

    drafts = []
    for i, rec in enumerate(records):
        bug_no = f"OMII-BUG-{args.start_no + i:04d}"
        drafts.append(draft(rec, bug_no=bug_no, build=args.build, env=args.env,
                            reporter=args.reporter, cycle=args.cycle))
    body = ("\n---\n".join(drafts)).rstrip() + "\n"

    if args.out:
        Path(args.out).write_text(body, encoding="utf-8")
        eprint(f"Wrote {len(drafts)} draft(s) -> {args.out}")
    else:
        sys.stdout.write(body)
    return 0


if __name__ == "__main__":
    sys.exit(main())
