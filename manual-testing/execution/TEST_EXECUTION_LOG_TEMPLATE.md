# Test Execution Log Template

> **Purpose.** Per-cycle, per-case execution log. Copy to `manual-testing/execution/logs/EXEC_LOG_<cycle-id>.md`, fill the header, and add one row per test case as you execute. Feeds pass/fail, velocity, and defect-leakage metrics. All data synthetic and PHI-safe.

## Purpose

The execution log is the row-level evidence behind the cycle's [Test Execution Report](../metrics/TEST_EXECUTION_REPORT_TEMPLATE.md). Each row is auditable: who ran what, when, with what result and which defect.

## Scope

- **In scope:** One execution cycle, one build, one environment.
- **Out of scope:** Defect detail (link the defect ID), metric rollups (computed in the report).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Engineer | Records every executed case in real time |
| QA Lead | Reviews completeness before sign-off |

---

## Log Header

| Field | Value |
|-------|-------|
| Cycle / Sprint | `<M6-Cycle-n>` |
| Build / Commit | `<git short SHA>` |
| Environment | `<dev / qa / stage>` |
| Date(s) | `2026-06-30` |
| Lead tester | `<name>` |

## Execution Log

| TC ID | Title | Tester | Date | Result | Defect Ref | Notes |
|-------|-------|--------|------|--------|-----------|-------|
| `TC-AUTH-001` | Login as demo.admin | `<name>` | 2026-06-30 | Pass | — | Token issued, 15-min expiry |
| `TC-PAT-014` | Search patient by MRN | `<name>` | 2026-06-30 | Fail | `BUG-101` | MRN-0002 not returned on page 2 |
| `TC-APPT-007` | Book appointment, no overlap | `<name>` | 2026-06-30 | Pass | — | DR-001 slot 09:00–09:30 |
| `TC-FHIR-003` | GET FHIR Patient by id | `<name>` | 2026-06-30 | Blocked | `BUG-102` | Backend 503 during run |
| | | | | | | |

**Result values:** `Pass` · `Fail` · `Blocked` · `Skipped` · `Not Run`.

## Cycle Tally

| Result | Count |
|--------|-------|
| Pass | `<n>` |
| Fail | `<n>` |
| Blocked | `<n>` |
| Skipped | `<n>` |
| Not Run | `<n>` |
| **Total** | `<n>` |

---

## Related Documents

- [Execution Guide](EXECUTION_GUIDE.md)
- [Test Execution Report Template](../metrics/TEST_EXECUTION_REPORT_TEMPLATE.md)
- [QA Metrics Catalog](../metrics/QA_METRICS.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
