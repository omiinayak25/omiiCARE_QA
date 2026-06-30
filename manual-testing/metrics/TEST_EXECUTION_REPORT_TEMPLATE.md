# Test Execution Report Template

> **Purpose.** Reusable, copy-per-cycle report that summarizes a test execution run: build, environment, suites, pass/fail/blocked counts, defects, coverage, top risks, and a go/no-go recommendation. Pairs with the [Test Execution Log](../execution/TEST_EXECUTION_LOG_TEMPLATE.md) (detail) and [QA Metrics](QA_METRICS.md) (definitions).

## Purpose

Standardizes the artifact produced at the end of every smoke or regression cycle so stakeholders get the same structure each time. Copy this file to `manual-testing/metrics/reports/TER_<cycle-id>.md`, fill every field, and link it from the cycle's execution log. All data is synthetic and PHI-safe.

## Scope

- **In scope:** A single execution cycle against one build on one environment.
- **Out of scope:** Multi-cycle trend analysis (use the QA Metrics dashboard) and defect root-cause detail (use the defect report).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Engineer | Fills counts, defects, and observations |
| QA Lead | Reviews, sets the recommendation, signs off |

---

## 1. Report Header

| Field | Value |
|-------|-------|
| Report ID | `TER-<YYYYMMDD>-<n>` |
| Cycle / Sprint | `<e.g. M6-Cycle-3>` |
| Build / Commit | `<git short SHA / build tag>` |
| Environment | `<dev / qa / stage>` (Spring profile) |
| Backend version | `<from /actuator/info>` |
| Frontend version | `<package.json version>` |
| Date executed | `2026-06-30` |
| Tester(s) | `<names>` |
| Execution type | `<Smoke / Regression / Targeted>` |

## 2. Suites Run

| Suite | Type | Tool | Cases | Result |
|-------|------|------|-------|--------|
| Smoke – auth/patient/appointment | Manual + API | Rest Assured | `<n>` | `<pass/fail>` |
| Regression – patient module | Manual | — | `<n>` | `<pass/fail>` |
| API E2E | Automated | Rest Assured (`@api-e2e`) | `<n>` | `<pass/fail>` |
| UI E2E | Automated | Playwright (`@ui-e2e`) | `<n>` | `<pass/fail>` |
| BDD scenarios | Automated | Cucumber (`@bdd`) | `<n>` | `<pass/fail>` |

## 3. Result Summary

| Status | Count | % of Executed |
|--------|-------|---------------|
| Passed | `<n>` | `<%>` |
| Failed | `<n>` | `<%>` |
| Blocked | `<n>` | `<%>` |
| Not Run | `<n>` | — |
| **Total Planned** | `<n>` | — |

- **Pass rate (M-01):** `<passed / executed × 100>` %
- **Execution velocity (M-06):** `<executed / engineer-days>` cases/eng-day

## 4. Defects Raised

| Defect ID | Severity | Module | Summary | Status |
|-----------|----------|--------|---------|--------|
| `BUG-<n>` | Critical/High/Medium/Low | `<module>` | `<one line>` | Open/Fixed/Verified |

- New defects: `<n>` · Reopened: `<n>` · Closed this cycle: `<n>`

## 5. Coverage

| Metric | This Cycle | Target |
|--------|-----------|--------|
| Requirement coverage (M-04) | `<%>` | 100% Critical |
| Module coverage (M-07) | `<%>` | 100% in-scope |
| Code coverage (M-09, JaCoCo) | `<%>` | ≥ 70% core |
| Risk coverage (M-08) | `<%>` | 100% High/Critical |

## 6. Top Risks / Observations

| # | Risk / Observation | Impact | Action |
|---|--------------------|--------|--------|
| 1 | `<e.g. flaky UI login retry>` | `<High/Med/Low>` | `<owner + action>` |
| 2 | | | |

## 7. Recommendation

| Field | Value |
|-------|-------|
| Quality gate | `<MET / NOT MET>` |
| Recommendation | `<GO / NO-GO / GO WITH RISKS>` |
| Rationale | `<1–2 sentences>` |
| Sign-off | `<QA Lead name, date>` |

---

## Related Documents

- [QA Metrics Catalog](QA_METRICS.md)
- [Test Execution Log Template](../execution/TEST_EXECUTION_LOG_TEMPLATE.md)
- [Execution Guide](../execution/EXECUTION_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
