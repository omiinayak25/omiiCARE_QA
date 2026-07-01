# QA Metrics & KPIs — OpenMRS-Primary Healthcare QA Portfolio

> Document ID: **QA-MET-001**. Version 1.0. Generated 2026-07-01. Status: **Baselined for review**.
> System Under Test (SUT): **OpenMRS** (reference: https://o2.openmrs.org), portable to **OpenEMR, HAPI FHIR, SMART Health IT, omiiCARE** via the **Resource Adapter Layer (RAL)**.

This document defines the measurement system for the portfolio: every metric's **formula**, **target**, **data source**, **cadence**, and **dashboard** placement. It governs the [RTM](../../manual-testing/rtm/RTM.csv), the [requirements catalog](../requirements/requirements-catalog.md) (472 requirements, `REQ-<PREFIX>-NNN`), and the manual test suite (`manual-testing/test-cases/openmrs/`, 1,349+ cases today, scaling toward ~4,000 across 21 modules).

**Referenced artifacts:** [Master Test Plan](./MASTER_TEST_PLAN.md), [Master Test Strategy](./MASTER_TEST_STRATEGY.md), [Risk-Based Testing Strategy](./RISK_BASED_TESTING_STRATEGY.md), [Release Test Plan](./RELEASE_TEST_PLAN.md), [Quality Gates](../QUALITY_GATES.md), [Risk Register](../reverse-engineering/RISK_REGISTER.md), [NFR](../reverse-engineering/NFR.md).

---

## 1. Measurement Principles

| # | Principle | Implication |
|---|---|---|
| 1 | **Patient safety is the apex metric.** | Any defect with a patient-safety or PHI-exposure dimension is weighted Critical regardless of frequency. Escape rate for safety defects has a hard target of **0**. |
| 2 | **PHI never appears in a metric artifact.** | Dashboards, exports, and defect samples carry record *counts* and de-identified IDs only — never names, MRNs, DOBs, or clinical values. |
| 3 | **Traceability is mandatory.** | Every metric rolls up to `REQ-<PREFIX>-NNN` via the RTM. A metric that cannot be traced to a requirement is not reported. |
| 4 | **Owned environments only.** | PERF and SEC metrics are collected **only** against owned/local environments (RefApp sandbox, dockerized HAPI, local omiiCARE). The public `o2.openmrs.org` is **never** load- or attack-tested. |
| 5 | **Trend over point.** | Single readings are noise; decisions use rolling windows (sprint, release, 90-day). Every KPI is reported with direction and delta. |
| 6 | **Multi-system normalization.** | Metrics are computed per-SUT then normalized through the RAL so OpenMRS, OpenEMR, HAPI, SMART, and omiiCARE are comparable on one axis. |

---

## 2. Metric Catalog (Summary)

| ID | Metric | Target | Cadence | Tier |
|---|---|---|---|---|
| M-01 | Requirement Coverage | ≥ 98% P1/P2; 100% safety/PHI | Sprint | Release gate |
| M-02 | Test Case Coverage (design) | ≥ 95% executable | Sprint | Release gate |
| M-03 | Code/Branch Coverage (automation) | ≥ 80% line, ≥ 70% branch | Per-build | Trend |
| M-04 | Pass Rate | ≥ 97% (P1 100%) | Per-run | Release gate |
| M-05 | Defect Density | ≤ 1.5 / 100 TC; ≤ 0.4 / KLOC adapter | Sprint | Trend |
| M-06 | Defect Removal Efficiency (DRE) | ≥ 95% | Release | Release gate |
| M-07 | Defect Leakage | ≤ 5% | Release | Release gate |
| M-08 | Escape Rate | ≤ 3% overall; **0** safety/PHI | Release/90-day | Release gate |
| M-09 | MTTR (defect) | Critical ≤ 24h, High ≤ 3d | Weekly | SLA |
| M-10 | Automation % | ≥ 65% regression by FY-end | Sprint | Trend |
| M-11 | Flaky Rate | ≤ 2% | Per-build | Trend |
| M-12 | Cycle Time (defect) | ≤ 3.5d median | Weekly | Trend |
| M-13 | Test Execution Progress | 100% planned by code-freeze | Daily (test window) | Burndown |
| M-14 | Severity Distribution | Critical ≤ 5% of open | Weekly | Health |

---

## 3. Coverage Metrics

### 3.1 Requirement Coverage (M-01)

```
Requirement Coverage % = (Requirements with ≥1 linked, executed test case
                          / Total in-scope requirements) × 100
```

- **Source:** RTM.csv (`Requirement_ID` ↔ `TestCase_IDs`) joined to execution results.
- **Targets:** P1 ≥ 98%, P2 ≥ 95%, all **safety/PHI requirements = 100%** (no exceptions). Currently 472 requirements / 1,349 cases → designed coverage tracked per module below.
- **Watch:** *linked* ≠ *executed*. A requirement with a written-but-not-run case counts as covered for design (M-02) but **not** for M-01.

| Module (prefix) | Reqs | TCs (today) | Coverage target | Gate weight |
|---|---|---|---|---|
| AUTH, RBAC, SEC | high | per RTM | 100% (P1) | Critical |
| REG, SRCH, PDASH | high | per RTM | ≥ 98% | High |
| VISIT, VITAL, CLIN | high | per RTM | ≥ 98% | High |
| APPT, ORDLAB, PHARM | high | per RTM | ≥ 98% (safety) | Critical (PHARM) |
| FHIR, HL7, DATA | med | per RTM | ≥ 98% conformance | High |
| RPT, NOTIF, BILL, TELE | med | per RTM | ≥ 95% | Medium |
| A11Y, PERF | med | per RTM | ≥ 95% / criteria | High |

### 3.2 Test Case Coverage & Code Coverage

```
Test Case Coverage % = (Test cases marked Executable & ready / Total designed) × 100
Code Coverage %      = (Executed lines or branches / Total) × 100   [automation only]
```

- **M-02 target:** ≥ 95% of designed cases executable each sprint.
- **M-03 targets:** line ≥ 80%, branch ≥ 70% on the **Resource Adapter Layer** and automation harness (the only code we own). UI-driven E2E is excluded from code coverage; it is measured by requirement coverage instead.
- **Technique coverage** is tracked as a secondary lens — each high-risk requirement must carry ≥ 2 techniques from {Functional, Negative, Boundary, Decision-Table, State-Transition, Pairwise, Exploratory} plus the applicable specialty lens {A11Y, Security, API, Database, FHIR, HL7}.

---

## 4. Pass Rate & Execution (M-04, M-13)

```
Pass Rate %            = (Passed / (Passed + Failed)) × 100      [Blocked/Skipped excluded from denominator, reported separately]
First-Pass Yield %     = (Passed on first execution / Executed) × 100
Execution Progress %   = (Executed / Planned) × 100
```

| Metric | Target | Notes |
|---|---|---|
| Overall Pass Rate | ≥ 97% | P1 cases **must** be 100% to pass a release gate |
| First-Pass Yield | ≥ 90% | Low FPY signals unstable build or weak entry criteria |
| Blocked % | ≤ 5% | Sustained blocks trigger environment/RAL escalation |
| Execution Progress | 100% planned by code-freeze | Daily burndown during the test window |

- **Source:** test management tool / execution log, keyed by `TC-<PREFIX>-NNNN`.
- **Rule:** a P1 failure linked to a safety/PHI requirement is an automatic gate **stop**, independent of overall pass rate.

---

## 5. Defect Metrics

### 5.1 Defect Density (M-05)

```
Defect Density (test) = Valid defects found / (Test cases executed / 100)
Defect Density (code) = Valid defects / KLOC                 [RAL & automation code]
```

- **Targets:** ≤ 1.5 defects per 100 executed TC; ≤ 0.4 / KLOC for adapter code.
- High density in a module is a **risk signal**, not just a quality reading — it feeds the [Risk-Based Testing Strategy](./RISK_BASED_TESTING_STRATEGY.md) to re-prioritize depth (e.g., PHARM, ORDLAB, FHIR mapping).

### 5.2 Defect Removal Efficiency (M-06)

```
DRE % = (Defects found before release / (Defects found before + after release)) × 100
```

- **Target ≥ 95%**; safety-class DRE target **100%**.
- Computed per release and trended over the trailing 3 releases. DRE is the primary measure of how well the in-phase test effort is working.

### 5.3 Defect Leakage (M-07)

```
Leakage % = (Defects that escaped a phase / Total defects for that phase) × 100
```

- Measured **phase-to-phase** (unit→integration→system→UAT→prod). **Target ≤ 5%** per phase boundary.
- A leakage spike at a single boundary points at weak entry/exit criteria for that phase — see [Quality Gates](../QUALITY_GATES.md).

### 5.4 Escape Rate (M-08)

```
Escape Rate % = (Defects found in production / Total defects found this release) × 100
```

- **Target ≤ 3% overall; 0 for any safety or PHI defect.** A single PHI-exposure or patient-safety escape triggers a mandatory root-cause analysis (RCA) and a regression-case addition to the RTM before the next release.

### 5.5 Defect Severity & Aging

| Severity | Open-pool ceiling | Aging SLA (open) |
|---|---|---|
| Critical (safety/PHI, data loss, auth bypass) | 0 at gate | review daily |
| High | ≤ 3 at gate | ≤ 3 days |
| Medium | ≤ 10 | ≤ 10 days |
| Low | tracked | best-effort |

```
Defect Aging = now − defect_opened_timestamp     (per open defect)
Reopen Rate % = (Reopened defects / Closed defects) × 100   [target ≤ 5%]
```

---

## 6. Responsiveness Metrics

### 6.1 MTTR — Mean Time To Repair (M-09)

```
MTTR = Σ(resolved_at − reported_at) / count(resolved defects)     [per severity]
```

| Severity | MTTR target |
|---|---|
| Critical | ≤ 24 hours |
| High | ≤ 3 days |
| Medium | ≤ 10 days |
| Low | next planned sprint |

### 6.2 Cycle Time (M-12)

```
Defect Cycle Time = closed_at − opened_at                 (median reported, not mean)
Detection Lead Time = found_at − introduced_at            (where attributable)
```

- **Target:** median defect cycle time ≤ 3.5 days. Median is used to resist long-tail skew; the 85th percentile is reported alongside as a tail indicator.

---

## 7. Automation & Stability Metrics

### 7.1 Automation Coverage (M-10)

```
Automation % = (Automated test cases / Total automatable test cases) × 100
```

- **Target:** ≥ 65% of regression-suitable cases automated by FY-end; API/FHIR/HL7 layers prioritized (deterministic, high ROI) over UI E2E.
- **Excluded from denominator:** exploratory, one-off, and inherently-manual A11Y assistive-tech checks (screen-reader announcement validation) — these stay manual by design.

### 7.2 Flaky Rate (M-11)

```
Flaky Rate % = (Tests with inconsistent pass/fail on identical code / Total automated runs) × 100
```

- **Target ≤ 2%.** Any test exceeding 5% flake over a rolling 20 runs is **quarantined** (excluded from gate, ticketed) — a flaky safety test is worse than no test because it erodes trust in the gate.

```
Automation Health = Pass Rate × (1 − Flaky Rate)     [composite, target ≥ 0.95]
```

---

## 8. Standards-Conformance Metrics

PHI- and safety-adjacent quality is tracked as explicit conformance percentages, not just pass/fail counts.

| Lens | Metric | Formula | Target |
|---|---|---|---|
| FHIR R4 | Resource conformance | (Valid resources / Validated) × 100 | 100% on Patient, Encounter, Observation, MedicationRequest |
| HL7 v2 | Message conformance | (Conformant ADT/ORM/ORU msgs / Tested) × 100 | ≥ 99% (0 segment-loss defects) |
| A11Y (WCAG 2.1 AA) | Criterion pass | (AA criteria passed / Applicable) × 100 | 100% for P1 A11Y reqs |
| Security (OWASP) | Control coverage | (ASVS controls verified / In-scope) × 100 | 100% L1, ≥ 90% L2 — owned env only |
| HIPAA-like | Audit-log completeness | (PHI-access events logged / events) × 100 | 100% (no unlogged PHI access) |
| PERF (NFR) | SLA adherence | (Txns within NFR target / measured) × 100 | per [NFR](../reverse-engineering/NFR.md) — owned env only |

---

## 9. Dashboards

Three views; all PHI-free; refresh cadence noted.

### 9.1 Executive / Release-Readiness (refresh: per release + weekly)
- RAG status against all **release-gate** metrics (M-01, M-02, M-04, M-06, M-07, M-08).
- Open Critical/High count and aging; escape rate trend (trailing 3 releases).
- Requirement coverage heat map by the 21 module prefixes.
- Single go/no-go indicator derived from [Quality Gates](../QUALITY_GATES.md).

### 9.2 QA Engineering / Sprint (refresh: daily during test window)
- Execution burndown (M-13), pass rate & first-pass yield (M-04).
- Defect inflow/outflow, density by module (M-05), MTTR & cycle time (M-09, M-12).
- Automation % and flaky rate trend (M-10, M-11) with quarantine list.

### 9.3 Conformance & Safety (refresh: per sprint)
- FHIR/HL7/A11Y/Security/HIPAA conformance percentages (Section 8).
- Safety/PHI defect register: must show **0 escaped**; any non-zero is a top-of-dashboard red banner.
- Multi-SUT comparison (OpenMRS vs OpenEMR vs HAPI vs SMART vs omiiCARE) normalized via RAL.

---

## 10. Reporting Cadence

| Cadence | Report | Audience | Key metrics |
|---|---|---|---|
| Per-build | CI quality snapshot | Eng/QA | M-03, M-04, M-11, automation health |
| Daily (test window) | Execution burndown | QA lead | M-13, blocked %, inflow/outflow |
| Weekly | QA health report | QA + Eng mgmt | M-05, M-09, M-12, M-14, aging |
| Per-sprint | Sprint quality review | Team | M-01, M-02, M-10, conformance |
| Per-release | Release readiness & DRE | Stakeholders | M-06, M-07, M-08, gate RAG |
| 90-day | Trend & RCA review | Program | Escape trend, DRE trend, RCA actions |

---

## 11. Worked Example (illustrative)

Given a release window: 1,349 cases executed, 1,310 passed, 39 failed → **Pass Rate = 97.1%** (M-04 met). Pre-release valid defects = 86, production-found = 3 → **DRE = 86/89 = 96.6%** (M-06 met), **Escape Rate = 3/89 = 3.4%** (M-08 *miss* — investigate). Of the 3 escapes, 0 are safety/PHI → hard gate satisfied, soft target breached → RCA + regression cases added. Density = 86 / (1,349/100) = **6.4 / 100 TC** in this illustration → above threshold → feeds risk re-prioritization for the next cycle.

> Figures above are illustrative for formula demonstration only and are not actual results.

---

## 12. Governance

- **Owner:** Principal QA Engineer (this portfolio). **Reviewers:** Eng Lead, Compliance.
- **Metric change control:** any target change requires a versioned update here and a note in the release readiness report.
- **Data integrity:** metric pipelines are validated quarterly; a metric whose source cannot be reproduced is suspended until fixed.
- **No PHI rule:** any dashboard or export found to contain PHI is an immediate incident under [Quality Gates](../QUALITY_GATES.md), not a metric defect.

---

*End of QA-MET-001 v1.0.*
