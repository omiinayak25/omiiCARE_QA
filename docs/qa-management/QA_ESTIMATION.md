# QA Estimation

**Document type:** QA Management — Estimation & Capacity Planning
**Primary system under test:** OpenMRS (https://o2.openmrs.org)
**Portability targets:** OpenEMR, HAPI FHIR, SMART Health IT, in-house omiiCARE — via the Resource Adapter Layer (RAL)
**Owner:** Principal QA Engineer
**Status:** Baseline (calibrate every sprint against actuals)
**Related:** `docs/MASTER_TEST_PLAN.md`, `docs/TEST_STRATEGY.md`, `docs/TEST_PYRAMID.md`, `docs/RISK_ANALYSIS.md`, `manual-testing/rtm/RTM.csv`, `docs/requirements/requirements-catalog.md`

---

## 1. Purpose & Scope

This document defines how QA effort is **estimated, sized, and capacity-planned** across the healthcare QA portfolio. It covers manual design/execution, automation, and specialty testing (FHIR R4, HL7 v2, WCAG 2.1 AA, OWASP, HIPAA-like). Estimates are anchored to concrete, measurable baselines already in the repo:

| Anchor | Value | Source |
|---|---|---|
| Requirements traced | 472 (REQ-`<PREFIX>`-NNN) | `docs/requirements/requirements-catalog.md` |
| Manual test cases authored | 1,349 across 21 modules | `manual-testing/test-cases/openmrs/` |
| Target test-case inventory | ~4,000 | portfolio roadmap |
| Modules | 21 (AUTH…TELE) | RTM |

**Estimation is patient-safety-weighted:** modules touching clinical decisions, medication, orders, and PHI carry a complexity multiplier so they are never under-estimated. Performance and security estimates assume execution **only against owned/local environments**, never public OpenMRS or partner endpoints.

---

## 2. Estimation Techniques

Three complementary techniques are used; each estimate is produced by **at least two** and reconciled. Divergence > 20% triggers a re-scope review.

### 2.1 Three-Point (PERT)

Used for net-new work with uncertainty (new module, new RAL adapter, exploratory charters).

```
E = (O + 4M + P) / 6          (expected effort)
SD = (P - O) / 6              (std deviation / risk band)
```

- **O** = optimistic (everything known, stable env)
- **M** = most likely (normal friction)
- **P** = pessimistic (flaky env, defect churn, blocked deps)

Report **E ± SD**. Confidence interval for a release = sum of E ± sqrt(Σ SD²) (variances add, not SDs).

### 2.2 Test-Case-Point (TCP)

Primary technique for **sized, catalogued** work. Each test case is weighted by complexity and test-design technique, then converted to effort via a calibrated execution rate.

| Complexity | Drivers | TCP weight | Authoring (min) | Execution (min) |
|---|---|---|---|---|
| Simple | Functional happy-path, 1 system, ≤3 steps | 1 | 12 | 6 |
| Medium | Negative / boundary / decision-table, validation, RBAC | 2 | 22 | 11 |
| Complex | State-transition, pairwise, multi-step clinical flow | 3 | 38 | 18 |
| Very Complex | FHIR/HL7 conformance, cross-system RAL, security, perf, a11y audit | 5 | 60 | 28 |

**Technique multipliers** (applied on top of base weight): Pairwise ×1.3, State-Transition ×1.3, FHIR/HL7 conformance ×1.6, Security ×1.5, Accessibility (AT-assisted) ×1.4, Database/data-integrity ×1.3.

### 2.3 Percentage-of-Development (%-of-Dev)

Top-down sanity check for release planning before the catalogue is fully built.

| Work type | QA as % of dev effort |
|---|---|
| Standard CRUD feature (REG, SRCH, APPT) | 35–40% |
| Clinical / patient-safety (CLIN, ORDLAB, PHARM, VITAL) | 55–65% |
| Interop (FHIR, HL7, RAL adapter) | 60–70% |
| Cross-cutting (SEC, A11Y, PERF) | 45–55% |
| Bug-fix / maintenance | 25–30% |

---

## 3. Calibration Baselines (Assumptions)

All estimates below derive from these calibrated rates. **Re-calibrate quarterly** against RTM actuals.

| Parameter | Value | Notes |
|---|---|---|
| Productive QA hours / day | 6.0 | 8h minus standups, triage, admin |
| Productive days / sprint (2 wk) | 9 | 10 minus ceremonies |
| Manual execution velocity | ~30 simple-equiv cases / day | blended, mature build |
| First-pass authoring velocity | ~18 cases / day | new module, no template |
| Defect logging overhead | +12% of execution | reproduce, evidence, retest |
| Regression cycle factor | 0.35 × full suite / release | risk-based subset |
| Environment stability | 90% uptime (local) | below this, apply +15% PERT P |
| Test data ready before exec | Yes | see `docs/TEST_DATA_STRATEGY.md` |
| Automation maintenance | 15% of automated suite size / quarter | UI churn |
| Flake budget | ≤2% post-quarantine | gate for CI trust |

---

## 4. Worked Estimates Per Module

Counts are current authored cases (`ALL_TEST_CASES.csv`, 1,349) scaled to the ~4,000 target. Complexity mix is module-typical; effort uses TCP rates (§2.2) plus technique multipliers, validated against PERT.

### 4.1 Authoring + First Execution (current catalogue, 1,349 cases)

| Module | Cur. cases | Dominant techniques | Avg TCP wt | Author (h) | Exec (h) | Safety/PHI weight |
|---|---|---|---|---|---|---|
| AUTH | 63 | Functional, Negative, Security | 2.4 | 25 | 12 | High (PHI gate) |
| REG | 94 | Functional, Boundary, Decision-Table | 2.0 | 35 | 17 | High (identity) |
| SRCH | 64 | Functional, Boundary, Pairwise | 1.9 | 22 | 11 | Medium |
| PDASH | 64 | Functional, State-Transition | 2.1 | 24 | 12 | Medium |
| VISIT | 67 | State-Transition, Decision-Table | 2.5 | 30 | 15 | High (clinical) |
| VITAL | 67 | Boundary, Decision-Table | 2.4 | 28 | 14 | High (patient-safety) |
| CLIN | 68 | State-Transition, Decision-Table | 3.0 | 38 | 18 | Critical (CDS) |
| APPT | 68 | State-Transition, Pairwise | 2.3 | 27 | 13 | Medium |
| ORDLAB | 64 | State-Transition, Decision-Table | 3.0 | 36 | 17 | Critical (orders) |
| PHARM | 58 | Decision-Table, Boundary, Negative | 3.2 | 35 | 16 | Critical (meds) |
| RBAC | 78 | Decision-Table, Negative, Security | 2.6 | 34 | 16 | High (access) |
| DATA | 47 | Database, Boundary, Negative | 2.7 | 22 | 10 | High (integrity) |
| RPT | 63 | Functional, Boundary | 1.9 | 21 | 11 | Medium |
| FHIR | 90 | FHIR R4 conformance, Pairwise | 4.0 | 70 | 34 | High (interop) |
| HL7 | 57 | HL7 v2 conformance, State-Transition | 3.8 | 42 | 20 | High (interop) |
| SEC | 68 | Security (OWASP), Negative | 3.5 | 48 | 22 | Critical (PHI) |
| A11Y | 58 | Accessibility (WCAG 2.1 AA) | 2.8 | 32 | 17 | High (compliance) |
| PERF | 43 | Performance (load/soak) | 4.2 | 38 | 26 | High (local-only) |
| NOTIF | 44 | State-Transition, Negative | 2.2 | 16 | 8 | Medium |
| BILL | 76 | Decision-Table, Boundary | 2.6 | 32 | 15 | High (financial/PHI) |
| TELE | 45 | Functional, State-Transition, Security | 2.7 | 20 | 10 | High (PHI in transit) |
| **Total** | **1,349** | — | **~2.7** | **~645 h** | **~314 h** | — |

**Interpretation:** Initial authoring of the existing 1,349-case catalogue ≈ **645 h** (~108 productive person-days); one full execution cycle ≈ **314 h** (~52 days), before the +12% defect overhead (→ ~352 h) and regression factor.

### 4.2 Scaling to ~4,000-case Target

| Phase | Cases added | Author effort | Exec/cycle | Cumulative author |
|---|---|---|---|---|
| Current baseline | 1,349 | 645 h (done) | 314 h | 645 h |
| Expansion wave 1 (deepen P1/P2) | +1,200 | ~520 h | +260 h | 1,165 h |
| Expansion wave 2 (RAL multi-system) | +900 | ~470 h | +210 h | 1,635 h |
| Expansion wave 3 (edge/exploratory→scripted) | +550 | ~210 h | +120 h | 1,845 h |
| **~4,000 target** | **~4,000** | **~1,845 h** | **~900 h/cycle** | — |

RAL multi-system expansion is costed once per **adapter** (OpenEMR, HAPI FHIR, SMART Health IT, omiiCARE): conformance cases are reused via the adapter abstraction; only adapter-boundary and system-specific deltas are net-new (~25% of a module's cases per target system).

---

## 5. Specialty Estimation Notes

| Area | Estimation nuance | Multiplier |
|---|---|---|
| FHIR R4 (REQ-FHIR-NNN) | Each resource type needs CRUD + search-param + conformance + negative; use HAPI validator. Profile validation doubles assertion count. | ×1.6 |
| HL7 v2 (REQ-HL7-NNN) | ADT/ORM/ORU/SIU message-type matrix; ACK/NAK paths; field-31 segment edge cases. | ×1.6 |
| Security (REQ-SEC-NNN) | OWASP Top 10 mapped per endpoint; auth/z, injection, PHI leakage. **Local/owned env only.** | ×1.5 |
| Accessibility (REQ-A11Y-NNN) | WCAG 2.1 AA; AT-assisted (screen reader, keyboard-only) cases are 2× manual time. | ×1.4 |
| Performance (REQ-PERF-NNN) | Script + baseline + soak + analysis. **Local/owned env only**, never public o2.openmrs.org. | ×1.5 |
| Patient-safety (CLIN/ORDLAB/PHARM/VITAL) | Mandatory negative + boundary + decision-table coverage; no "happy-path only" sign-off. | +complexity tier |

---

## 6. Automation ROI

### 6.1 Model

```
Break-even runs  N = C_auto / (C_manual − C_maint_per_run)
Cumulative ROI   = (N_runs × C_manual) − (C_auto + N_runs × C_maint_per_run)
```

Where `C_auto` = one-time build cost, `C_manual` = manual exec cost/run, `C_maint_per_run` = amortized maintenance.

### 6.2 Per-layer assumptions (aligned to `docs/TEST_PYRAMID.md`)

| Layer | Build cost / case | Manual exec / case | Maint / case / run | Best-fit modules |
|---|---|---|---|---|
| API / FHIR / HL7 (service) | 1.5× manual | 11 min | 0.4 min | FHIR, HL7, ORDLAB, RBAC, DATA |
| UI (E2E, Selenium/Playwright) | 4.0× manual | 13 min | 1.6 min | REG, APPT, CLIN, PDASH |
| Security (DAST automated) | 2.5× manual | 22 min | 0.8 min | SEC, AUTH |
| Accessibility (axe-core) | 1.8× manual | 17 min | 0.5 min | A11Y |

### 6.3 Worked ROI (per 100-case slice, runs per release × ~24 releases/yr)

| Layer | C_auto (h) | C_manual/run (h) | Break-even (runs) | Cumulative saving @ 24 runs (h) |
|---|---|---|---|---|
| API/FHIR/HL7 | 27.5 | 18.3 | ~2 | ~389 |
| UI E2E | 86.7 | 21.7 | ~7 | ~360 |
| Security DAST | 91.7 | 36.7 | ~3 | ~698 |
| A11Y axe | 50.8 | 28.3 | ~2 | ~470 |

**ROI priority order:** automate **API/FHIR/HL7 first** (lowest break-even, highest interop regression value), then **Security DAST** and **A11Y** (high per-run manual cost), and reserve **UI E2E** for the top patient-safety journeys only (highest maintenance, slowest break-even). Target automation mix: **60% service / 25% security+a11y / 15% UI** — keeps the pyramid healthy and maintenance within the 15%/quarter budget.

### 6.4 What stays manual

Exploratory charters, first-pass of new clinical flows, usability, visual/clinical-judgement validation, and any test whose maintenance > manual run cost. These are **not** counted toward automation ROI.

---

## 7. Effort Summary Tables

### 7.1 Single release (risk-based regression, current 1,349-case catalogue)

| Activity | Basis | Effort (h) |
|---|---|---|
| Test design / updates (delta) | ~10% catalogue churn | 65 |
| Risk-based regression exec | 0.35 × 314 h | 110 |
| Defect logging + retest | +12% | 13 |
| Specialty (FHIR/HL7/SEC/A11Y/PERF) gates | fixed per release | 70 |
| Reporting / sign-off / RTM update | fixed | 16 |
| **Per-release QA total** | — | **~274 h** |

At 6 productive h/day → **~46 person-days**, i.e. ~2.5 QA engineers over a 2-week sprint (9 productive days each).

### 7.2 Annual rollup (~24 releases, with automation maturing)

| Quarter | Auto coverage | Manual exec/release | Automation build+maint | Net QA h/release |
|---|---|---|---|---|
| Q1 | 15% | 233 | 60 | ~293 |
| Q2 | 35% | 178 | 70 | ~248 |
| Q3 | 55% | 124 | 65 | ~189 |
| Q4 | 65% | 96 | 55 | ~151 |

Automation pulls steady-state per-release effort from ~293 h → ~151 h (**~48% reduction**) by Q4 while expanding the catalogue toward 4,000 cases.

---

## 8. Capacity Planning

### 8.1 Capacity formula

```
Sprint capacity (h) = Engineers × 9 days × 6 h × (1 − overhead_buffer)
Required engineers   = Sprint demand (h) / (9 × 6 × (1 − buffer))
```

`overhead_buffer = 15%` (triage, ceremonies, env issues).

### 8.2 Role mix for ~4,000-case program

| Role | FTE | Primary responsibility |
|---|---|---|
| Principal QA (lead) | 0.5 | Estimation, risk, gates, RTM ownership |
| Manual QA (clinical-domain) | 3 | CLIN/ORDLAB/PHARM/VITAL/VISIT design+exec |
| Automation SDET (service) | 2 | API/FHIR/HL7/RAL adapters |
| Automation SDET (UI/E2E) | 1 | Patient-safety UI journeys |
| Security + A11Y specialist | 1 | SEC (OWASP), A11Y (WCAG 2.1 AA), local PERF |
| **Total** | **7.5 FTE** | — |

### 8.3 Demand vs capacity (steady-state sprint)

| | Hours |
|---|---|
| Available (7.5 FTE × 9 × 6 × 0.85) | ~344 |
| Demand (per-release QA, Q2 baseline) | ~248 |
| Buffer for defect surges / exploratory | ~96 |
| Utilization | ~72% |

72% target utilization leaves slack for defect-fix retest spikes and unplanned safety-critical investigation — deliberately **not** 100%, because under-buffered QA in a clinical system trades schedule for patient-safety risk.

### 8.4 RAL multi-system capacity impact

Each new target system (OpenEMR, HAPI FHIR, SMART Health IT, omiiCARE) adds **~470 h authoring + ~210 h/cycle** (§4.2). At 1 adapter/quarter, allocate **+1.0 SDET FTE** during that quarter; conformance reuse keeps it from being a full re-test of all 21 modules.

---

## 9. Assumptions & Constraints (Estimation Risks)

| # | Assumption | If violated |
|---|---|---|
| 1 | Local/owned envs ≥90% available | PERF/SEC slip; apply +15% PERT P |
| 2 | Test data ready pre-execution | +10% exec per affected module |
| 3 | Performance & security run only on owned/local envs (never public) | Hard constraint — non-negotiable |
| 4 | RAL abstraction stable; conformance cases reusable | Multi-system cost ×2–3 if adapters leak |
| 5 | Requirements stable post-freeze (472 baseline) | Re-estimate via PERT on delta |
| 6 | Flake ≤2% after quarantine | CI trust lost; manual fallback re-adds cost |
| 7 | Automation maintenance ≤15%/quarter | ROI break-even pushes out |
| 8 | Defect-fix turnaround ≤2 days | Regression backlog inflates per-release h |

---

## 10. Governance & Re-Estimation

- **Cadence:** Re-calibrate velocity/TCP rates **every sprint** against RTM actuals; re-baseline §3 **quarterly**.
- **Trigger for re-estimate:** >20% variance between techniques, scope change >10%, env stability <90%, or any new RAL target.
- **Source of truth for actuals:** `manual-testing/rtm/RTM.csv` (case counts, priority, risk) + execution logs.
- **Estimate audit trail:** every release estimate records technique inputs (O/M/P or TCP weights), assumptions invoked, and post-release actual for calibration.

> Estimates are **risk-adjusted, not optimistic**. In a patient-safety / PHI context, the cost of an under-estimate is missed clinical defects — so safety-critical modules (CLIN, ORDLAB, PHARM, VITAL, SEC) are always sized at the **most-likely-to-pessimistic** band, never optimistic.
