# QA Metrics Catalog

> **Purpose.** Single authoritative catalog of every quality metric tracked for omiiCARE_QA: definition, formula, target/threshold, data source, and cadence. Use it to instrument dashboards, gate releases, and report quality posture for Milestone 6.

## Purpose

This catalog standardizes how omiiCARE_QA measures product and process quality. It removes ambiguity in metric definitions so that backend (Spring Boot), frontend (React), and the automation module (Rest Assured / Playwright / Cucumber) all report against the same formulas. All values use synthetic, PHI-safe data only.

## Scope

- **In scope:** Execution, defect, coverage, automation-health, and lead-time metrics for auth, patient, appointment, and FHIR modules; the sample dashboard.
- **Out of scope:** Business KPIs, financials, infrastructure cost metrics, and any metric requiring real PHI.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Owns this catalog; sets targets; reviews trends each cycle |
| QA Engineers | Capture per-cycle values in the execution report and log |
| Automation Engineer | Wires JaCoCo, Surefire, and flaky-detection outputs into metrics |
| Engineering Lead | Acts on defect-leakage, MTTR, and mutation-score regressions |

---

## 1. Metric Definitions

| # | Metric | Definition | Formula | Target / Threshold | Data Source | Cadence |
|---|--------|-----------|---------|--------------------|-------------|---------|
| M-01 | Pass / Fail Rate | Share of executed test cases that passed | `passed / executed × 100` | ≥ 98% pass on release candidate | Execution log; Surefire reports | Per cycle |
| M-02 | Defect Density | Defects per unit of scope (per module / per KLOC) | `defects / size (module or KLOC)` | ≤ 1.0 defect / module / cycle | Defect tracker; module map | Per cycle |
| M-03 | Defect Leakage (Escaped Defects) | Defects found after a stage exits, relative to total | `defects_found_later / (defects_in_stage + defects_found_later) × 100` | < 5% leakage to UAT/prod | Defect tracker (found-in vs origin) | Per release |
| M-04 | Requirement Coverage | Requirements with at least one mapped, executed test | `requirements_with_passing_test / total_requirements × 100` | 100% of Critical requirements traced | RTM (`../rtm/`); execution log | Per cycle |
| M-05 | Automation Coverage | Test cases automated vs total candidate cases | `automated_cases / automatable_cases × 100` | ≥ 60% of regression by M6 | Test-case inventory; automation suite | Per cycle |
| M-06 | Execution Velocity | Test cases executed per engineer-day | `executed_cases / engineer_days` | ≥ 40 manual cases / engineer-day | Execution log | Per cycle |
| M-07 | Module Coverage | Modules exercised by at least one passing suite | `modules_with_passing_suite / total_modules × 100` | 100% of in-scope modules (auth, patient, appointment, FHIR) | Suite-to-module map | Per cycle |
| M-08 | Risk Coverage | High/Critical risks with a mapped mitigating test | `high_risks_with_test / total_high_risks × 100` | 100% of High/Critical risks | Risk register (`../risk-analysis/`) | Per release |
| M-09 | Code Coverage | Lines/branches exercised by automated tests | JaCoCo `covered_lines / total_lines × 100` | ≥ 70% line on core modules (ramping from baseline) | JaCoCo report | Per build |
| M-10 | Mutation Score | Injected faults killed by the test suite | `killed_mutants / total_mutants × 100` | ≥ 70% on core modules | Mutation run (PIT, when enabled) | Per release |
| M-11 | Flaky-Test % | Tests with non-deterministic outcomes | `flaky_tests / total_automated_tests × 100` | < 2% | Repeat-run analysis; CI history | Per build |
| M-12 | MTTD (Mean Time To Detect) | Avg time from defect introduction to detection | `Σ(detected_at − introduced_at) / defect_count` | ≤ 1 business day | Defect tracker; commit history | Per release |
| M-13 | MTTR (Mean Time To Resolve) | Avg time from defect open to verified fix | `Σ(resolved_at − reported_at) / defect_count` | ≤ 2 business days (Critical ≤ 1) | Defect tracker | Per release |
| M-14 | Automation ROI | Net time saved by automation vs investment | `(manual_time_saved − automation_cost) / automation_cost` | > 1.0 (positive) by M6 | Effort log; run counts | Per release |
| M-15 | Execution-Time Trend | Wall-clock time of the full suite over cycles | `Δ suite_runtime cycle-over-cycle` | No > 15% regression without cause | Surefire / CI timing | Per build |

### Formula Notes

- **Defect Leakage** is computed per origin stage (unit, integration, system, UAT). A defect's *origin* is the earliest stage that should have caught it; its *found-in* is where it was actually detected.
- **Automation Coverage (M-05)** counts only cases that are *automatable*; exploratory and one-off manual checks are excluded from the denominator.
- **Code Coverage (M-09)** is read from JaCoCo (`org.jacoco:jacoco-maven-plugin` 0.8.12). The repository gate starts low and is raised as the codebase matures; the catalog target (≥ 70%) is the M6 goal for core modules.
- **Automation ROI (M-14)**: `manual_time_saved = (manual_minutes_per_run − automated_minutes_per_run) × runs_per_release`; `automation_cost = build_minutes + maintenance_minutes` converted to the same unit.

---

## 2. Data Sources

| Source | Produces | Command / Location |
|--------|----------|--------------------|
| Surefire reports | Pass/fail counts, runtime | `automation/target/surefire-reports/` |
| JaCoCo | Code coverage | `mvn -Pquality` → `target/site/jacoco/` |
| Execution log | Manual results, defect refs | [`../execution/TEST_EXECUTION_LOG_TEMPLATE.md`](../execution/TEST_EXECUTION_LOG_TEMPLATE.md) |
| RTM | Requirement coverage | [`../rtm/`](../rtm/) |
| Risk register | Risk coverage | [`../risk-analysis/`](../risk-analysis/) |
| Defect tracker | Density, leakage, MTTD, MTTR | [`../bug-reports/`](../bug-reports/) |
| CI history | Flaky %, execution-time trend | GitHub Actions run history |

---

## 3. Sample Quality Dashboard (Example Values)

> Illustrative Milestone-6 cycle snapshot. Values are examples for format reference, not actual results.

| Metric | Value | Target | Status |
|--------|-------|--------|--------|
| M-01 Pass / Fail Rate | 98.6% (212 / 215) | ≥ 98% | PASS |
| M-02 Defect Density | 0.8 / module | ≤ 1.0 | PASS |
| M-03 Defect Leakage | 3.2% | < 5% | PASS |
| M-04 Requirement Coverage | 100% Critical | 100% | PASS |
| M-05 Automation Coverage | 58% | ≥ 60% | WATCH |
| M-06 Execution Velocity | 44 cases / eng-day | ≥ 40 | PASS |
| M-07 Module Coverage | 100% (auth, patient, appointment, FHIR) | 100% | PASS |
| M-08 Risk Coverage | 100% High/Critical | 100% | PASS |
| M-09 Code Coverage | 71% line (core) | ≥ 70% | PASS |
| M-10 Mutation Score | 68% | ≥ 70% | WATCH |
| M-11 Flaky-Test % | 1.4% | < 2% | PASS |
| M-12 MTTD | 0.7 day | ≤ 1 day | PASS |
| M-13 MTTR | 1.6 day | ≤ 2 days | PASS |
| M-14 Automation ROI | 2.3 | > 1.0 | PASS |
| M-15 Execution-Time Trend | +6% vs last cycle | < 15% | PASS |

**Status legend:** PASS = at/above target · WATCH = within 10% of target · FAIL = below threshold (triggers action).

---

## Related Documents

- [Test Execution Report Template](TEST_EXECUTION_REPORT_TEMPLATE.md)
- [Execution Guide](../execution/EXECUTION_GUIDE.md)
- [Test Execution Log Template](../execution/TEST_EXECUTION_LOG_TEMPLATE.md)
- [Common Failure Patterns](../knowledge-base/COMMON_FAILURE_PATTERNS.md)
- [docs/TEST_STRATEGY.md](../../docs/TEST_STRATEGY.md) §11 (metrics blueprint)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
