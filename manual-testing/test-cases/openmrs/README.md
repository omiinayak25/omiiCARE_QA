# OpenMRS-Primary Manual Test Suite — 4187 Test Cases

> Generated 2026-07-01. Enterprise healthcare QA. Reference SUT **OpenMRS** (https://o2.openmrs.org); portable to OpenEMR, HAPI FHIR, SMART Health IT and omiiCARE via the Resource Adapter Layer.

- **4187 test cases** across **66 modules**, tracing to **1795 requirements** — 100% coverage, 0 gaps, 0 untraced ([catalog](../../../docs/requirements/requirements-catalog.md) · [RTM](../../rtm/RTM.md)).
- Master file: [ALL_TEST_CASES.csv](ALL_TEST_CASES.csv) — 17-column RFC CSV, importable to TestRail/Xray/Zephyr.

## Columns

`TC_ID | Module | Sub_Module | Title | Test_Type | Test_Level | Priority | Severity | Risk | Preconditions | Test_Steps | Test_Data | Expected_Result | Automation_Feasibility | Requirement_ID | Healthcare_Classification | Source_System`

## Modules

| Module | File | Test Cases | Requirements |
|---|---|---|---|
| Authentication & Session | [AUTH.csv](AUTH.csv) | 64 | 23 |
| Patient Registration | [REG.csv](REG.csv) | 95 | 25 |
| Find Patient Record / Search | [SRCH.csv](SRCH.csv) | 64 | 24 |
| Patient Dashboard & Demographics | [PDASH.csv](PDASH.csv) | 64 | 15 |
| Visits & Encounters | [VISIT.csv](VISIT.csv) | 68 | 23 |
| Vitals & Observations | [VITAL.csv](VITAL.csv) | 67 | 29 |
| Allergies, Conditions & Diagnoses | [CLIN.csv](CLIN.csv) | 68 | 24 |
| Appointment Scheduling | [APPT.csv](APPT.csv) | 68 | 19 |
| Orders, Laboratory & Radiology | [ORDLAB.csv](ORDLAB.csv) | 64 | 31 |
| Pharmacy & Medication Orders | [PHARM.csv](PHARM.csv) | 58 | 22 |
| Roles, Privileges & User Admin | [RBAC.csv](RBAC.csv) | 78 | 26 |
| Data Management & Integrity | [DATA.csv](DATA.csv) | 47 | 17 |
| Reporting & Audit Logging | [RPT.csv](RPT.csv) | 63 | 19 |
| FHIR R4 API | [FHIR.csv](FHIR.csv) | 90 | 34 |
| HL7 v2 Messaging | [HL7.csv](HL7.csv) | 57 | 33 |
| Security (functional/readiness, design-only) | [SEC.csv](SEC.csv) | 68 | 15 |
| Accessibility (WCAG 2.1 AA) | [A11Y.csv](A11Y.csv) | 58 | 15 |
| Performance Readiness (criteria/design) | [PERF.csv](PERF.csv) | 43 | 17 |
| Notifications & Alerts | [NOTIF.csv](NOTIF.csv) | 44 | 25 |
| Billing & Insurance | [BILL.csv](BILL.csv) | 76 | 22 |
| Telemedicine | [TELE.csv](TELE.csv) | 45 | 14 |
| Encounters & Clinical Forms | [ENC.csv](ENC.csv) | 78 | 39 |
| Care Programs & Enrollment | [PROG.csv](PROG.csv) | 70 | 23 |
| Drug Orders & Dispensing (deep) | [DRUG.csv](DRUG.csv) | 76 | 33 |
| Laboratory (deep) | [LAB2.csv](LAB2.csv) | 78 | 24 |
| Radiology & Imaging (deep) | [RAD.csv](RAD.csv) | 68 | 34 |
| Immunizations | [IMM.csv](IMM.csv) | 65 | 15 |
| Maternal & Antenatal Care | [MAT.csv](MAT.csv) | 65 | 31 |
| Patient Identifiers & MPI | [IDENT.csv](IDENT.csv) | 64 | 14 |
| Provider & Practitioner Management | [PROV.csv](PROV.csv) | 60 | 17 |
| Locations & Facilities | [LOCN.csv](LOCN.csv) | 56 | 27 |
| Concept Dictionary & Metadata Admin | [CONFIG.csv](CONFIG.csv) | 72 | 25 |
| Appointment Scheduling (deep) | [SCHED2.csv](SCHED2.csv) | 77 | 53 |
| Consent & Privacy | [CONSENT.csv](CONSENT.csv) | 58 | 22 |
| Billing (deep) | [BILL2.csv](BILL2.csv) | 75 | 40 |
| Insurance & Claims (deep) | [INS.csv](INS.csv) | 76 | 47 |
| REST API (deep, per resource) | [RESTAPI.csv](RESTAPI.csv) | 84 | 22 |
| FHIR R4 (deep: search/bundle/operations) | [FHIR2.csv](FHIR2.csv) | 96 | 43 |
| HL7 v2 (deep) | [HL7v2.csv](HL7v2.csv) | 66 | 58 |
| Security (OWASP deep, design-only) | [SECDEEP.csv](SECDEEP.csv) | 82 | 27 |
| Accessibility (per WCAG 2.1 AA SC) | [A11Y2.csv](A11Y2.csv) | 64 | 13 |
| Performance & Load (criteria, deep) | [PERF2.csv](PERF2.csv) | 54 | 27 |
| Responsive / Mobile Web | [MOBILE.csv](MOBILE.csv) | 55 | 15 |
| Cross-Browser & Compatibility | [COMPAT.csv](COMPAT.csv) | 50 | 28 |
| Usability & UX | [USAB.csv](USAB.csv) | 48 | 15 |
| End-to-End Patient Journeys | [E2E2.csv](E2E2.csv) | 75 | 75 |
| Integration (cross-module & external) | [INTEG.csv](INTEG.csv) | 68 | 36 |
| Smoke Pack | [SMOKE2.csv](SMOKE2.csv) | 50 | 31 |
| Sanity Pack | [SANITY2.csv](SANITY2.csv) | 45 | 26 |
| Regression Pack (high-value) | [REGR.csv](REGR.csv) | 84 | 28 |
| UAT Scenarios | [UAT2.csv](UAT2.csv) | 64 | 37 |
| Audit & Compliance (deep) | [AUDIT2.csv](AUDIT2.csv) | 58 | 22 |
| Notifications & Alerts (deep) | [NOTIF2.csv](NOTIF2.csv) | 50 | 21 |
| Telemedicine (deep) | [TELE2.csv](TELE2.csv) | 48 | 24 |
| Data Quality & Migration | [DATAQ.csv](DATAQ.csv) | 56 | 29 |
| Advanced Search & Filters | [SRCH2.csv](SRCH2.csv) | 54 | 19 |
| Dashboard Widgets (deep) | [DASH2.csv](DASH2.csv) | 60 | 55 |
| Reporting & Analytics (deep) | [REPT2.csv](REPT2.csv) | 64 | 35 |
| Death & Record Lifecycle | [DEATH.csv](DEATH.csv) | 44 | 17 |
| Inpatient / Ward Management | [INPT.csv](INPT.csv) | 62 | 44 |
| Triage & Emergency | [TRIAGE.csv](TRIAGE.csv) | 56 | 19 |
| Order Sets & Protocols | [ORDSET.csv](ORDSET.csv) | 50 | 22 |
| Allergies & Interactions (deep) | [ALLERG2.csv](ALLERG2.csv) | 54 | 25 |
| Pediatrics & Growth | [PEDS.csv](PEDS.csv) | 54 | 28 |
| Referrals & Transfers | [REFER.csv](REFER.csv) | 55 | 15 |
| Patient Portal & Self-Service | [PORTAL.csv](PORTAL.csv) | 50 | 23 |
| **TOTAL** | | **4187** | **1795** |

## Breakdowns

### Test Type / Technique

| Value | Count |
|---|---|
| Functional | 886 |
| Negative | 700 |
| Boundary | 383 |
| State Transition | 366 |
| FHIR | 340 |
| Security | 334 |
| Decision Table | 275 |
| Database | 190 |
| Accessibility | 155 |
| Pairwise | 134 |
| HL7 | 133 |
| API | 124 |
| Exploratory | 101 |
| Audit | 33 |
| Integration | 14 |
| Usability | 8 |
| Performance | 8 |
| E2E | 2 |
| Smoke | 1 |

### Test Level

| Value | Count |
|---|---|
| Regression | 2209 |
| Integration | 966 |
| System | 672 |
| Sanity | 157 |
| Smoke | 89 |
| UAT | 69 |
| E2E | 19 |
| Exploratory | 2 |
| Negative | 2 |
| Database | 1 |
| Performance | 1 |

### Priority

| Value | Count |
|---|---|
| P2 | 2047 |
| P1 | 1167 |
| P3 | 961 |
| P4 | 12 |

### Severity

| Value | Count |
|---|---|
| High | 2025 |
| Medium | 1367 |
| Critical | 629 |
| Low | 166 |

### Risk

| Value | Count |
|---|---|
| Medium | 2209 |
| High | 1452 |
| Low | 526 |

### Automation Feasibility

| Value | Count |
|---|---|
| Partially-Automatable | 1904 |
| Automatable | 1441 |
| Manual-Only | 842 |

### Source System

| Value | Count |
|---|---|
| OpenMRS | 2127 |
| Platform | 1158 |
| Standards | 902 |

### Healthcare Classification

| Value | Count |
|---|---|
| Data Integrity | 2322 |
| Clinical | 1479 |
| Patient Safety | 903 |
| Security | 720 |
| Audit | 714 |
| PHI/Privacy | 494 |
| Standards-FHIR | 476 |
| Regulatory/Compliance | 355 |
| Accessibility | 292 |
| Standards-HL7 | 287 |
| Billing | 278 |
| API | 47 |
| Usability | 26 |
| Database | 23 |
| Performance | 21 |
| Interoperability | 7 |
| Notifications | 3 |

