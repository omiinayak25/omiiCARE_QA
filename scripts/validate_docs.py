#!/usr/bin/env python3
"""omiiCARE_QA — Documentation Validation Gate.

Pure Python 3 standard-library validator (no third-party deps) that hardens the
documentation deliverables for the v1.0.0 release. It enforces:

  1. PRESENCE   — every required reverse-engineering, qa-management,
                  requirements-catalog and RTM artifact exists on disk.
  2. CSV SHAPE  — manual-testing/.../ALL_TEST_CASES.csv has exactly 17 columns
                  on every row and more than 4000 data rows.
  3. TRACEABILITY — every test-case Requirement_ID resolves to a requirement in
                  the requirements catalog (0 untraced), and every RTM row has
                  at least one linked test case (0 gaps).
  4. LINKS      — every internal relative Markdown link (*.md targets) inside
                  the docs/ tree resolves to an existing file.

Exit code:
  0  -> all checks PASS
  1  -> one or more checks FAIL (non-zero so CI blocks the merge/release)
  2  -> validator could not run (e.g. repo root not found)

Usage:
  python3 scripts/validate_docs.py              # run from repo root
  python3 scripts/validate_docs.py --root /path/to/repo
  python3 scripts/validate_docs.py --strict-links   # treat anchor-only links too

Companion doc: docs/qa-management/DOCUMENTATION_VALIDATION.md
"""

import argparse
import csv
import os
import re
import sys

# --------------------------------------------------------------------------- #
# Configuration — REAL repo paths (see docs/ and manual-testing/ in the tree). #
# --------------------------------------------------------------------------- #

REVERSE_ENGINEERING = [
    "API_BLUEPRINT.md",
    "ARCHITECTURE.md",
    "ASSUMPTIONS_AND_OPEN_QUESTIONS.md",
    "BRD.md",
    "DATA_DICTIONARY.md",
    "FHIR_MAPPING.md",
    "FIELD_DICTIONARY.md",
    "FRD.md",
    "HL7_MAPPING.md",
    "NAVIGATION_MAP.md",
    "NFR.md",
    "RBAC_MATRIX.md",
    "README.md",
    "RISK_REGISTER.md",
    "SRS.md",
    "USE_CASES.md",
    "USER_STORIES_AND_ACCEPTANCE_CRITERIA.md",
    "VALIDATION_MATRIX.md",
]

QA_MANAGEMENT = [
    "BUG_REPORT_TEMPLATES.md",
    "DEFECT_MANAGEMENT_PROCESS.md",
    "ENTRY_EXIT_CRITERIA.md",
    "MASTER_TEST_PLAN.md",
    "MASTER_TEST_STRATEGY.md",
    "QA_ESTIMATION.md",
    "QA_KNOWLEDGE_BASE.md",
    "QA_METRICS.md",
    "README.md",
    "RELEASE_CHECKLISTS.md",
    "RELEASE_TEST_PLAN.md",
    "RISK_BASED_TESTING_STRATEGY.md",
    "SPRINT_TEST_PLAN.md",
    "TEST_AUTOMATION_STRATEGY.md",
    "TEST_DATA_AND_ENVIRONMENT_MANAGEMENT.md",
    "UAT_PLAN.md",
]

REQUIREMENTS_CATALOG = "docs/requirements/requirements-catalog.md"
RTM_CSV = "manual-testing/rtm/RTM.csv"
RTM_MD = "manual-testing/rtm/RTM.md"
ALL_TEST_CASES_CSV = "manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv"

EXPECTED_TC_COLUMNS = 17
MIN_TC_ROWS = 4000  # data rows, header excluded

# Canonical Requirement_ID shape used across catalog / RTM / test cases.
# Module token may mix letters & digits (e.g. A11Y, HL7v2, FHIR2): REQ-<MOD>-<NNN>.
REQ_ID_RE = re.compile(r"REQ-[A-Za-z0-9]+-[0-9]+")

# Markdown inline link:  [text](target)   — capture the target.
MD_LINK_RE = re.compile(r"\[[^\]]*\]\(([^)]+)\)")


# --------------------------------------------------------------------------- #
# Result accumulation                                                         #
# --------------------------------------------------------------------------- #

class Results:
    def __init__(self):
        self.checks = []  # list of (name, passed: bool, detail: str)

    def add(self, name, passed, detail=""):
        self.checks.append((name, bool(passed), detail))

    @property
    def failed(self):
        return [c for c in self.checks if not c[1]]

    def print_summary(self):
        print("")
        print("=" * 72)
        print("  omiiCARE_QA — Documentation Validation Summary")
        print("=" * 72)
        for name, passed, detail in self.checks:
            tag = "PASS" if passed else "FAIL"
            line = "  [{:>4}] {}".format(tag, name)
            print(line)
            if detail:
                for d in detail.splitlines():
                    print("         {}".format(d))
        print("-" * 72)
        total = len(self.checks)
        nfail = len(self.failed)
        if nfail == 0:
            print("  RESULT: PASS  ({}/{} checks green)".format(total, total))
        else:
            print("  RESULT: FAIL  ({}/{} checks failed)".format(nfail, total))
        print("=" * 72)


# --------------------------------------------------------------------------- #
# Helpers                                                                      #
# --------------------------------------------------------------------------- #

def find_repo_root(start):
    """Walk upward until a directory containing both docs/ and manual-testing/."""
    cur = os.path.abspath(start)
    while True:
        if os.path.isdir(os.path.join(cur, "docs")) and os.path.isdir(
            os.path.join(cur, "manual-testing")
        ):
            return cur
        parent = os.path.dirname(cur)
        if parent == cur:
            return os.path.abspath(start)
        cur = parent


def read_csv_rows(path):
    """Return list of rows using the csv module (handles quoted commas/newlines)."""
    with open(path, newline="", encoding="utf-8") as fh:
        return list(csv.reader(fh))


# --------------------------------------------------------------------------- #
# Checks                                                                       #
# --------------------------------------------------------------------------- #

def check_required_docs(root, results):
    missing = []
    for name in REVERSE_ENGINEERING:
        p = os.path.join(root, "docs", "reverse-engineering", name)
        if not os.path.isfile(p):
            missing.append(os.path.relpath(p, root))
    for name in QA_MANAGEMENT:
        p = os.path.join(root, "docs", "qa-management", name)
        if not os.path.isfile(p):
            missing.append(os.path.relpath(p, root))
    for rel in (REQUIREMENTS_CATALOG, RTM_CSV, RTM_MD, ALL_TEST_CASES_CSV):
        if not os.path.isfile(os.path.join(root, rel)):
            missing.append(rel)

    expected = len(REVERSE_ENGINEERING) + len(QA_MANAGEMENT) + 4
    if missing:
        results.add(
            "Required documents present",
            False,
            "{} of {} required artifacts missing:\n{}".format(
                len(missing), expected, "\n".join(missing)
            ),
        )
    else:
        results.add(
            "Required documents present",
            True,
            "All {} required artifacts found "
            "(18 reverse-engineering, 16 qa-management, catalog, RTM.csv/md, "
            "ALL_TEST_CASES.csv).".format(expected),
        )


def check_test_case_csv(root, results):
    path = os.path.join(root, ALL_TEST_CASES_CSV)
    if not os.path.isfile(path):
        results.add("ALL_TEST_CASES.csv shape (17 cols, >4000 rows)", False,
                    "File not found: {}".format(ALL_TEST_CASES_CSV))
        return
    rows = read_csv_rows(path)
    if not rows:
        results.add("ALL_TEST_CASES.csv shape (17 cols, >4000 rows)", False,
                    "File is empty.")
        return

    header = rows[0]
    data = rows[1:]
    bad_cols = [i + 2 for i, r in enumerate(data) if len(r) != EXPECTED_TC_COLUMNS]
    # (+2 => 1-based line number accounting for header on line 1)

    detail_lines = [
        "Columns in header: {} (expected {}).".format(len(header), EXPECTED_TC_COLUMNS),
        "Data rows: {} (minimum {}).".format(len(data), MIN_TC_ROWS),
    ]
    ok = True
    if len(header) != EXPECTED_TC_COLUMNS:
        ok = False
    if bad_cols:
        ok = False
        sample = ", ".join(str(x) for x in bad_cols[:10])
        detail_lines.append(
            "{} row(s) do not have {} columns (line #s): {}{}".format(
                len(bad_cols), EXPECTED_TC_COLUMNS, sample,
                " ..." if len(bad_cols) > 10 else "",
            )
        )
    if len(data) <= MIN_TC_ROWS:
        ok = False
        detail_lines.append("Row count {} is not greater than {}.".format(
            len(data), MIN_TC_ROWS))

    results.add("ALL_TEST_CASES.csv shape (17 cols, >4000 rows)", ok,
                "\n".join(detail_lines))


def _catalog_req_ids(root):
    path = os.path.join(root, REQUIREMENTS_CATALOG)
    with open(path, encoding="utf-8") as fh:
        text = fh.read()
    return set(REQ_ID_RE.findall(text))


def _tc_req_ids(root):
    """Requirement_ID is column index 14 (15th column) of ALL_TEST_CASES.csv."""
    path = os.path.join(root, ALL_TEST_CASES_CSV)
    rows = read_csv_rows(path)
    header = rows[0]
    try:
        idx = header.index("Requirement_ID")
    except ValueError:
        idx = 14
    ids = set()
    for r in rows[1:]:
        if len(r) > idx and r[idx].strip():
            ids.add(r[idx].strip())
    return ids


def check_traceability(root, results):
    if not (
        os.path.isfile(os.path.join(root, REQUIREMENTS_CATALOG))
        and os.path.isfile(os.path.join(root, ALL_TEST_CASES_CSV))
    ):
        results.add("Traceability: 0 untraced test cases", False,
                    "Catalog or test-case CSV missing; cannot evaluate.")
        return

    catalog_ids = _catalog_req_ids(root)
    tc_ids = _tc_req_ids(root)
    untraced = sorted(i for i in tc_ids if i not in catalog_ids)

    if untraced:
        sample = ", ".join(untraced[:15])
        results.add(
            "Traceability: 0 untraced test cases", False,
            "{} test-case Requirement_ID(s) are NOT defined in the catalog "
            "(untraced):\n{}{}\nCatalog defines {} requirement IDs; test "
            "cases reference {}.".format(
                len(untraced), sample, " ..." if len(untraced) > 15 else "",
                len(catalog_ids), len(tc_ids),
            ),
        )
    else:
        results.add(
            "Traceability: 0 untraced test cases", True,
            "All {} test-case Requirement_IDs resolve to catalog requirements "
            "(0 untraced).".format(len(tc_ids)),
        )


def check_rtm_gaps(root, results):
    path = os.path.join(root, RTM_CSV)
    if not os.path.isfile(path):
        results.add("RTM: 0 coverage gaps", False, "RTM.csv missing.")
        return
    rows = read_csv_rows(path)
    header = rows[0]
    data = rows[1:]
    try:
        c_count = header.index("TestCase_Count")
        c_ids = header.index("TestCase_IDs")
    except ValueError:
        c_count, c_ids = 5, 6

    gaps = []
    for r in data:
        rid = r[0] if r else "?"
        count_raw = r[c_count].strip() if len(r) > c_count else ""
        ids_raw = r[c_ids].strip() if len(r) > c_ids else ""
        zero_count = count_raw in ("", "0")
        no_ids = ids_raw == ""
        if zero_count or no_ids:
            gaps.append(rid)

    if gaps:
        sample = ", ".join(gaps[:15])
        results.add(
            "RTM: 0 coverage gaps", False,
            "{} requirement(s) have no linked test cases (gaps):\n{}{}".format(
                len(gaps), sample, " ..." if len(gaps) > 15 else "",
            ),
        )
    else:
        results.add(
            "RTM: 0 coverage gaps", True,
            "All {} RTM requirements have >=1 linked test case (0 gaps).".format(
                len(data)),
        )


def check_markdown_links(root, results, strict=False):
    """Verify internal relative Markdown links to *.md files resolve."""
    docs_root = os.path.join(root, "docs")
    broken = []
    scanned = 0
    for dirpath, _dirs, files in os.walk(docs_root):
        for fname in files:
            if not fname.endswith(".md"):
                continue
            fpath = os.path.join(dirpath, fname)
            scanned += 1
            try:
                with open(fpath, encoding="utf-8") as fh:
                    content = fh.read()
            except (OSError, UnicodeDecodeError):
                continue
            for target in MD_LINK_RE.findall(content):
                target = target.strip()
                # Skip external links, mailto, pure anchors, and images handled elsewhere.
                if re.match(r"^[a-zA-Z][a-zA-Z0-9+.-]*://", target):
                    continue
                if target.startswith(("#", "mailto:", "tel:", "data:")):
                    continue
                # Strip anchor / query fragment.
                path_part = target.split("#", 1)[0].split("?", 1)[0]
                if path_part == "":
                    continue
                # Only validate relative links that point at markdown docs
                # (and, when strict, any relative path target).
                is_md = path_part.lower().endswith(".md")
                if not is_md and not strict:
                    continue
                if os.path.isabs(path_part):
                    resolved = os.path.join(root, path_part.lstrip("/"))
                else:
                    resolved = os.path.normpath(os.path.join(dirpath, path_part))
                if not os.path.exists(resolved):
                    broken.append(
                        "{} -> {}".format(os.path.relpath(fpath, root), target)
                    )

    if broken:
        sample = "\n".join(broken[:20])
        results.add(
            "Internal Markdown links resolve", False,
            "{} broken internal link(s) across {} docs:\n{}{}".format(
                len(broken), scanned, sample,
                "\n ..." if len(broken) > 20 else "",
            ),
        )
    else:
        results.add(
            "Internal Markdown links resolve", True,
            "All internal relative Markdown links across {} docs resolve.".format(
                scanned),
        )


# --------------------------------------------------------------------------- #
# Entry point                                                                  #
# --------------------------------------------------------------------------- #

def main(argv=None):
    parser = argparse.ArgumentParser(
        description="omiiCARE_QA documentation validation gate."
    )
    parser.add_argument(
        "--root", default=None,
        help="Repository root (default: auto-detect upward from CWD).",
    )
    parser.add_argument(
        "--strict-links", action="store_true",
        help="Also validate non-.md relative link targets (e.g. images, dirs).",
    )
    args = parser.parse_args(argv)

    start = args.root or os.getcwd()
    root = find_repo_root(start)
    if not (
        os.path.isdir(os.path.join(root, "docs"))
        and os.path.isdir(os.path.join(root, "manual-testing"))
    ):
        sys.stderr.write(
            "ERROR: could not locate repo root (docs/ + manual-testing/) "
            "from '{}'.\n".format(start)
        )
        return 2

    print("omiiCARE_QA documentation validation — repo root: {}".format(root))

    results = Results()
    check_required_docs(root, results)
    check_test_case_csv(root, results)
    check_traceability(root, results)
    check_rtm_gaps(root, results)
    check_markdown_links(root, results, strict=args.strict_links)

    results.print_summary()
    return 1 if results.failed else 0


if __name__ == "__main__":
    sys.exit(main())
