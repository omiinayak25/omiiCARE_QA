# Master Test Plan — OpenMRS-Primary Healthcare QA Portfolio

> IEEE 829-style Master Test Plan (MTP). Document ID: **QA-MTP-001**. Version 1.0.
> Generated 2026-07-01. Status: **Baselined for review**.
> System Under Test (SUT): **OpenMRS** (reference: https://o2.openmrs.org), portable to **OpenEMR, HAPI FHIR, SMART Health IT, omiiCARE** via the **Resource Adapter Layer (RAL)**.

This plan governs all dynamic and static test activity across the portfolio. It is the parent of module-level test plans, the [RTM](../../manual-testing/rtm/RTM.csv), and the [requirements catalog](../requirements/requirements-catalog.md) (472 requirements, `REQ-<PREFIX>-NNN`). Subordinate test design and execution detail live in `manual-testing/test-cases/openmrs/` (1,349+ cases today, scaling toward ~4,000).

---

## 1. Test Plan Identifier & References

| Item | Value |
|---|---|
| Plan ID | QA-MTP-001 v1.0 |
| Parent program | Enterprise Healthcare QA Portfolio |
| Primary SUT | OpenMRS 2.x Reference Application (RefApp) |
| Portability targets | OpenEMR, HAPI FHIR, SMART Health IT, omiiCARE (all via RAL) |
| Standards baseline | FHIR R4, HL7 v2.x, WCAG 2.1 AA, OWASP ASVS/Top-10, HIPAA-like PHI controls |

**Referenced artifacts:** [BRD](../reverse-engineering/BRD.md), [SRS](../reverse-engineering/SRS.md), [FRD](../reverse-engineering/FRD.md), [NFR](../reverse-engineering/NFR.md), [Use Cases](../reverse-engineering/USE_CASES.md), [Architecture](../reverse-engineering/ARCHITECTURE.md), [RBAC Matrix](../reverse-engineering/RBAC_MATRIX.md), [FHIR Mapping](../reverse-engineering/FHIR_MAPPING.md), [HL7 Mapping](../reverse-engineering/HL7_MAPPING.md), [Risk Register](../reverse-engineering/RISK_REGISTER.md), [Requirements Catalog](../requirements/requirements-catalog.md), [RTM](../../manual-testing/rtm/RTM.csv).

---

## 2. Introduction & Objectives

The portfolio validates a clinical EMR across functional, interoperability, security, accessibility, performance, and data-integrity dimensions, while preserving **vendor portability**: one canonical test suite must execute against ≥5 backends through the RAL, which abstracts each backend behind a canonical domain model (Patient, Visit, Encounter, Obs, Order, etc.).

**Primary objectives**
1. Verify every P1/P2 requirement in the catalog is covered by ≥1 traced test case (RTM completeness ≥ 99% for P1, ≥ 95% for P2).
2. Protect **patient safety** (allergy/interaction surfacing, wrong-patient prevention, vitals unit correctness, age/dosing integrity) and **PHI confidentiality** as first-class exit gates.
3. Confirm standards conformance: FHIR R4 resource/coding correctness, HL7 v2 segment fidelity, WCAG 2.1 AA, OWASP controls.
4. Demonstrate RAL portability — the same canonical assertions pass on OpenMRS and at least one secondary backend per release.

---

## 3. Test Items (What Is Being Tested)

| Layer | Test items | Modules |
|---|---|---|
| UI / clinical workflows | OpenMRS RefApp web UI: login, registration, search, dashboard, visits, vitals, clinical notes, appointments, orders, pharmacy | AUTH, REG, SRCH, PDASH, VISIT, VITAL, CLIN, APPT, ORDLAB, PHARM |
| API | OpenMRS REST (`/ws/rest/v1/*`) and FHIR R4 (`/ws/fhir2/R4/*`) | DATA, FHIR |
| Interoperability | FHIR R4 resources/bundles; HL7 v2 ADT/ORM/ORU messaging | FHIR, HL7 |
| Cross-cutting | RBAC, security/PHI, accessibility, performance, reporting, notifications, billing, telehealth | RBAC, SEC, A11Y, PERF, RPT, NOTIF, BILL, TELE |
| Adapter | RAL canonical-model mappings per backend | (all, via portability suite) |

Configuration items under test: SUT build/version, RAL adapter version, reference dataset (seeded patients/visits), and the standards profile pack (FHIR IG, HL7 message profiles).

---

## 4. Features In / Out of Scope

### 4.1 In Scope
- All 21 modules and 472 requirements (`REQ-<PREFIX>-NNN`) traced in the RTM.
- Positive, negative, boundary, decision-table, state-transition, pairwise, and exploratory coverage of clinical workflows.
- FHIR R4 read/search/create conformance and HL7 v2 message round-trips against OpenMRS and RAL-mapped backends.
- Accessibility (WCAG 2.1 AA) audit of all primary screens.
- Security testing (RBAC, authn/authz, injection, PHI-leak) **against owned/local environments only**.
- Performance/load **against owned/local environments only**.

### 4.2 Out of Scope
- **No performance, load, fuzzing, or security/penetration testing against any public site** (including `o2.openmrs.org`). Such tests run exclusively on owned or locally-provisioned environments.
- Third-party infrastructure (cloud provider SLAs, network hardware), OS/browser internals.
- OpenMRS core source-code defects unrelated to configured behavior (reported upstream, not gated here).
- Backend-native admin tooling not surfaced through the RAL canonical model.
- Non-functional tuning/optimization (we measure against SLOs; we do not tune the SUT).

---

## 5. Approach per Module

Risk drives depth: **Critical (20–25)** → exhaustive + adversarial + negative; **High (12–19)** → deep functional + boundary + security; **Medium/Low** → core functional + representative negative. Counts below are current traced manual cases.

| Module | Reqs | Cases | Primary techniques | Key risk focus |
|---|---|---|---|---|
| AUTH | 23 | 63 | Functional, negative, state-transition, security | Session fixation/spoofing (RISK-SEC-03), lockout |
| REG | 25 | 94 | Boundary, decision-table, negative, pairwise | Duplicate patient ID (RISK-DATA-01), birthdate→dosing (RISK-SAFE-05) |
| SRCH | 24 | 64 | Functional, boundary, injection-negative | Wrong-patient selection (RISK-SAFE-02), SQLi (RISK-SEC-05) |
| PDASH | 15 | 64 | Functional, state-transition, A11Y | Context confusion (RISK-SAFE-02), concurrency (NFR-REL-006) |
| VISIT | 23 | 67 | State-transition, decision-table, concurrency | Visit/encounter merge data loss (RISK-DATA-02) |
| VITAL | 29 | 67 | Boundary, decision-table, unit-conversion | Unit/range mis-capture (RISK-SAFE-03) |
| CLIN | 24 | 68 | Functional, decision-table, exploratory | Clinical note integrity, coding correctness |
| APPT | 19 | 68 | State-transition, boundary, pairwise | Double-booking, slot/state correctness |
| ORDLAB | 31 | 64 | Decision-table, negative, state-transition | Allergy/interaction not surfaced (RISK-SAFE-01) |
| PHARM | 22 | 58 | Decision-table, boundary, negative | Dose/interaction safety, dispense integrity |
| RBAC | 26 | 78 | Decision-table (RBAC matrix), negative, pairwise | Privilege escalation (RISK-SEC-01) |
| DATA | 17 | 47 | API functional, schema, DB-state | Orphaned obs (RISK-DATA-04), referential integrity |
| RPT | 19 | 63 | Functional, boundary, data-reconciliation | Report/data mismatch, aggregation correctness |
| FHIR | 34 | 90 | API, schema/profile, conformance, negative | R4 field/coding mis-map (RISK-INTEROP-01) |
| HL7 | 33 | 57 | Message-profile, round-trip, negative | Segment fidelity, ACK/NACK handling |
| SEC | 15 | 68 | OWASP-driven, injection, PHI-leak, authz | PHI in logs/URLs (RISK-SEC-04), unauth access (RISK-SEC-02) |
| A11Y | 15 | 58 | WCAG 2.1 AA audit, keyboard, screen-reader | Keyboard traps, contrast, accessible names |
| PERF | 17 | 43 | Load, latency-percentile, soak | SLO breach (NFR-PERF-001..009) |
| NOTIF | 25 | 44 | State-transition, functional, negative | Delivery correctness, no PHI in payloads |
| BILL | 22 | 76 | Decision-table, boundary, reconciliation | Charge accuracy, financial integrity |
| TELE | 14 | 45 | Functional, state-transition, exploratory | Session establishment, consent capture |

**RAL portability approach:** each canonical assertion is authored once against the domain model and parameterized by adapter. Per release, the full suite runs on OpenMRS (gating) and a smoke+interop subset runs on at least one secondary backend (HAPI FHIR for FHIR module, OpenEMR for clinical workflows).

---

## 6. Pass / Fail Criteria

### 6.1 Item-level (per test case)
- **Pass:** actual result matches expected, no Critical/High deviation, no PHI leak observed, no patient-safety hazard triggered.
- **Fail:** any expected-vs-actual mismatch, defect raised, or safety/PHI guardrail violated.
- **Blocked:** precondition/environment/data unavailable.

### 6.2 Module-level exit
| Criterion | Threshold |
|---|---|
| P1 test cases executed | 100% |
| P2 test cases executed | ≥ 95% |
| Pass rate (executed) | ≥ 98% |
| Open Critical defects | 0 |
| Open High defects | 0 (or formally risk-accepted by CMO/SEC owner) |
| RTM coverage (P1) | 100% requirements with ≥1 passing case |

### 6.3 Release (GA) exit — patient-safety & PHI gates (hard blockers)
- **Zero** open Critical defects across the portfolio.
- All `RISK-SAFE-*` and `RISK-SEC-*` mitigation cases executed and passing.
- FHIR R4 conformance and HL7 round-trip suites green on OpenMRS.
- WCAG 2.1 AA: no open Level-A or Level-AA violations on primary screens.
- Performance SLOs met on local reference environment (§2.1 NFR).

---

## 7. Suspension & Resumption Criteria

**Suspend a module/cycle when:**
- A Critical defect blocks a core workflow (e.g., login, registration, order placement) for >50% of planned cases.
- A patient-safety guardrail (allergy surfacing, wrong-patient, vitals unit) regresses — **immediate suspend + escalate to CMO**.
- A confirmed PHI leak (logs/URLs/errors) is found — **immediate suspend + escalate to SEC owner**.
- Test environment instability, corrupted reference data, or RAL adapter unavailability invalidates >20% of results.
- Build is rejected at smoke (BVT) gate.

**Resume when:**
- Blocking defect is fixed, re-tested, and verified; impacted cases re-baselined.
- Environment/data restored and a clean smoke (BVT) pass is recorded.
- Safety/PHI escalations are closed or explicitly risk-accepted by the accountable owner.
- Affected RTM rows re-linked and regression scope agreed.

---

## 8. Test Deliverables

| Deliverable | Owner | Cadence |
|---|---|---|
| Master Test Plan (this doc) + module test plans | QA Lead | Per release / on change |
| Test cases (`manual-testing/test-cases/openmrs/*.csv`) | QA Engineers | Continuous |
| RTM (`manual-testing/rtm/RTM.csv`) | QA Lead | Per cycle |
| Defect reports (severity, repro, PHI/safety flag) | QA Engineers | Continuous |
| Test execution / cycle reports | QA Lead | Per cycle |
| FHIR/HL7 conformance reports | Interop QA | Per release |
| WCAG 2.1 AA accessibility audit report | A11Y QA | Per release |
| Security test report (OWASP, local only) | Security QA | Per release |
| Performance/SLO report (local only) | Performance QA | Per release |
| Test summary report & release sign-off | QA Lead | At GA |

---

## 9. Schedule & Milestones

Indicative per-release cycle (timeboxed; adjust to sprint calendar).

| # | Milestone | Entry | Exit | Duration |
|---|---|---|---|---|
| M1 | Test planning & RTM baseline | Requirements baselined | MTP + RTM approved | Week 1 |
| M2 | Test design (new/changed) | Approved plan | Cases authored & peer-reviewed | Weeks 1–2 |
| M3 | Environment & data readiness | Build available | Smoke (BVT) green | Week 2 |
| M4 | Functional + boundary execution | M3 done | Module exit met | Weeks 2–4 |
| M5 | Interop (FHIR/HL7) + RBAC + A11Y | M4 substantially done | Conformance green | Weeks 3–4 |
| M6 | Security + Performance (local only) | Stable build | SLO + OWASP gates met | Weeks 4–5 |
| M7 | Regression + defect verification | Fixes delivered | Zero Critical/High | Week 5 |
| M8 | Test summary & sign-off | All gates met | GA approval | Week 5 |

---

## 10. Environment Needs

| Concern | Requirement |
|---|---|
| Reference SUT | OpenMRS RefApp pinned build; matched RAL adapter version |
| Secondary backends | OpenEMR, HAPI FHIR, SMART Health IT sandbox, omiiCARE — local/owned instances for portability runs |
| Test data | Synthetic-only seeded patients/visits/obs; **no real PHI**; deterministic IDs for repeatability |
| API endpoints | `/ws/rest/v1/*` and `/ws/fhir2/R4/*` reachable on local instance |
| Interop tooling | HL7 v2 message simulator/validator; FHIR validator with R4 IG profiles |
| A11Y tooling | Axe/WAVE-class scanner + screen reader (NVDA/JAWS/VoiceOver), keyboard-only rigs |
| Performance | **Owned/local load environment only** — never public sites. Server-side percentile capture at RAL (p50/p95/p99, 5-min rolling) |
| Security | **Owned/local only.** Isolated network; PHI-leak detection on logs/URLs/error bodies |
| Browsers | Current Chrome, Firefox, Edge, Safari (latest 2 majors) |

> **Hard rule:** Performance and security tests execute **only** against owned/local environments. `o2.openmrs.org` is used for reference/exploration of functional behavior only — never as a load or attack target.

---

## 11. Responsibilities (RACI summary)

| Role | Responsibility |
|---|---|
| QA Lead | MTP ownership, RTM, scheduling, exit/sign-off, summary reports |
| QA Engineers | Test design/execution per module, defect reporting |
| Interop QA | FHIR R4 / HL7 v2 conformance, RAL portability suite |
| Security QA (SEC owner) | OWASP/RBAC/PHI testing (local), accountable for `RISK-SEC-*` gates |
| Accessibility QA | WCAG 2.1 AA audits |
| Performance QA | Local SLO load/soak testing |
| Clinical SME / CMO | Patient-safety acceptance, accountable for `RISK-SAFE-*` gates |
| DBA | Data-integrity verification (`RISK-DATA-*`) |
| Dev Lead | Defect triage/fix, environment build delivery |
| Product Owner | Scope decisions, risk acceptance |

---

## 12. Risks & Contingencies

| Risk | Likelihood/Impact | Contingency |
|---|---|---|
| RAL adapter drift across backends | Med / High | Pin adapter+SUT versions; portability smoke gate before full run |
| Patient-safety regression (`RISK-SAFE-*`) | Med / Critical | Immediate suspend, CMO escalation, dedicated safety regression pack |
| PHI leak (`RISK-SEC-04`) | Med / High | Suspend, SEC escalation, log/URL scrubbing verification re-test |
| Privilege escalation (`RISK-SEC-01`, Critical 20) | Med / Critical | Exhaustive RBAC decision-table; release blocker until closed |
| FHIR/HL7 coding mis-map (`RISK-INTEROP-01`) | Med / High | Profile-validated conformance suite; reject on schema/coding fail |
| Environment instability / data corruption | Med / Med | Re-seed deterministic data; quarantine results; re-baseline on clean BVT |
| Schedule compression | Med / Med | Risk-based prioritization (P1 + Critical-risk first); descope Low-risk Medium items with PO sign-off |
| Scaling 1,349→~4,000 cases dilutes review | Med / Med | Peer-review gate per module; template-driven authoring; RTM completeness checks |
| Test-only-local constraint limits realism | Low / Med | Production-like local sizing; documented gap; no public-target substitution permitted |

---

## 13. Approvals

| Role | Name | Signature | Date |
|---|---|---|---|
| QA Lead | | | |
| Dev Lead | | | |
| Security Owner (SEC) | | | |
| Clinical SME / CMO | | | |
| Product Owner | | | |

> Sign-off certifies that exit criteria (§6), patient-safety/PHI gates, and standards-conformance gates have been met for the release under test, and that all performance/security testing was confined to owned/local environments.
