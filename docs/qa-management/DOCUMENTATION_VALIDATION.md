# Documentation Validation Gate

> Tool: [`scripts/validate_docs.py`](../../scripts/validate_docs.py)
> Runtime: **Python 3 standard library only** (no `pip install`, runs on Python 3.x / CI Node-22 images that ship Python 3).
> Purpose: a deterministic, zero-dependency quality gate that proves the
> omiiCARE_QA documentation deliverables are complete, well-formed, and fully
> traceable before a release is cut.

---

## 1. Why this exists

omiiCARE_QA ships a large documentation estate that is part of the product
contract for the v1.0.0 release:

- `docs/reverse-engineering/` — 18 artifacts (BRD/SRS/FRD/NFR/use-cases/user
  stories/RBAC/navigation/data + field dictionaries/validation matrix/diagrams/
  ERD/API blueprint/FHIR + HL7 mapping/risk register/architecture).
- `docs/qa-management/` — 16 artifacts (master strategy & plan, release/sprint
  plans, risk-based testing, estimation, defect management, metrics, entry/exit
  criteria, checklists, UAT, knowledge base, bug templates, automation strategy,
  test data & environment management).
- `docs/requirements/requirements-catalog.md` — the catalog of requirements.
- `manual-testing/rtm/RTM.csv` + `RTM.md` — the Requirements Traceability Matrix.
- `manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv` — the consolidated
  17-column test-case export (4,187 manual cases across 66 modules).

Documentation drift — a renamed file, a dropped requirement, a test case that
points at a Requirement_ID nobody defined — is invisible to a normal build but
breaks audit traceability (a hard requirement for healthcare QA). This gate
makes that drift a **failing check** instead of a silent regression.

---

## 2. What it checks

| # | Check | Rule | Fails when |
|---|-------|------|-----------|
| 1 | **Required documents present** | All 18 reverse-engineering + 16 qa-management docs, the requirements catalog, `RTM.csv`, `RTM.md`, and `ALL_TEST_CASES.csv` exist | Any required artifact is missing |
| 2 | **Test-case CSV shape** | `ALL_TEST_CASES.csv` has exactly **17 columns on every row** and **> 4000 data rows** | Header ≠ 17 cols, any row ≠ 17 cols, or ≤ 4000 data rows |
| 3 | **Traceability — 0 untraced** | Every `Requirement_ID` referenced by a test case is defined in `requirements-catalog.md` | Any test-case Requirement_ID is absent from the catalog |
| 4 | **RTM — 0 gaps** | Every RTM row links ≥ 1 test case (`TestCase_Count` > 0 and `TestCase_IDs` non-empty) | Any requirement has no linked test case |
| 5 | **Internal Markdown links resolve** | Every relative `[text](TARGET)` link under `docs/` points at an existing file | Any internal `.md` link target does not exist on disk |

CSV parsing uses the Python `csv` module, so quoted commas and embedded
newlines inside the 17 columns are handled correctly (a naive `split(",")`
would miscount columns and produce false failures).

The canonical Requirement_ID shape is `REQ-<MODULE>-<NNN>` where the module
token may mix letters and digits (e.g. `AUTH`, `A11Y`, `HL7v2`, `FHIR2`).

---

## 3. How to run

```bash
# From the repository root:
python3 scripts/validate_docs.py

# Explicit root (useful in CI working directories):
python3 scripts/validate_docs.py --root "$GITHUB_WORKSPACE"

# Also validate non-.md relative link targets (images, directories):
python3 scripts/validate_docs.py --strict-links
```

The script auto-detects the repository root by walking upward until it finds a
directory containing both `docs/` and `manual-testing/`, so it can be invoked
from any subdirectory.

### Exit codes

| Code | Meaning |
|------|---------|
| `0` | All checks PASS — safe to release |
| `1` | One or more checks FAIL — gate blocks the merge/release |
| `2` | Validator could not run (repo root not found) |

A machine-readable `RESULT: PASS` / `RESULT: FAIL` line is printed last, and a
per-check `[PASS]`/`[FAIL]` block with actionable detail (offending IDs, line
numbers, broken link source → target) precedes it.

---

## 4. Sample output

```
========================================================================
  omiiCARE_QA — Documentation Validation Summary
========================================================================
  [PASS] Required documents present
  [PASS] ALL_TEST_CASES.csv shape (17 cols, >4000 rows)
  [FAIL] Traceability: 0 untraced test cases
         31 test-case Requirement_ID(s) are NOT defined in the catalog ...
  [PASS] RTM: 0 coverage gaps
  [FAIL] Internal Markdown links resolve
         5 broken internal link(s) across 100 docs ...
------------------------------------------------------------------------
  RESULT: FAIL  (2/5 checks failed)
========================================================================
```

> The gate is intentionally strict: it reports real, pre-existing
> documentation defects (e.g. an empty catalog table section, or a link to a
> renamed file) rather than rubber-stamping the docs. A green run is a genuine
> 100%-traceability, 0-gap, 0-broken-link guarantee.

---

## 5. CI integration (planned)

This gate is designed to run as a fast, dependency-free job. A dedicated
workflow can invoke it on every pull request that touches `docs/**` or
`manual-testing/**`:

```yaml
# .github/workflows/docs-validation.yml (planned)
name: docs-validation
on:
  pull_request:
    paths:
      - "docs/**"
      - "manual-testing/**"
      - "scripts/validate_docs.py"
jobs:
  validate-docs:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-python@v5
        with:
          python-version: "3.x"
      - name: Validate documentation
        run: python3 scripts/validate_docs.py
```

Because the validator is pure standard library, no `requirements.txt` or
virtual-env step is needed; it executes on the default runner Python.

> NOTE: this workflow file is marked **(planned)** — add it under a distinct
> name that does not overwrite the existing omiiCARE CI workflows in
> `.github/workflows/`.

---

## 6. Maintenance

- When a new required doc is added to `docs/reverse-engineering/` or
  `docs/qa-management/`, append its filename to the corresponding list at the
  top of `scripts/validate_docs.py`.
- The expected test-case column count (`EXPECTED_TC_COLUMNS = 17`) and minimum
  row floor (`MIN_TC_ROWS = 4000`) are constants near the top of the script;
  bump them deliberately if the export schema changes.
- The Requirement_ID pattern (`REQ_ID_RE`) is the single source of truth for ID
  shape across the catalog, RTM, and test cases — keep all three aligned with it.
