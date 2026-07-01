# Sprint Test Plan — OpenMRS-Primary Healthcare QA

> **Scope:** Two-week sprint testing plan. Reusable **template** (Part A) plus a fully **worked example** (Part B).
> **System under test (SUT):** OpenMRS (https://o2.openmrs.org), portable to OpenEMR, HAPI FHIR, SMART Health IT and the in-house omiiCARE app via the **Resource Adapter Layer (RAL)**.
> **Anchors:** `docs/requirements/requirements-catalog.md` (472 reqs, `REQ-<PREFIX>-NNN`), `manual-testing/test-cases/openmrs/` (1,349+ cases, 21 modules), `manual-testing/rtm/RTM.csv`.
> **Standards:** FHIR R4, HL7 v2, WCAG 2.1 AA, OWASP ASVS/Top-10, HIPAA-like PHI controls.
> **Patient safety & PHI handling are first-class throughout.**
> **Environment rule:** Functional/A11y/FHIR/HL7 tests run against `o2.openmrs.org` (functional, non-PHI demo data only). **Performance and security/penetration tests run ONLY against owned/local environments — never public sites.**

---

## Part A — Sprint Test Plan Template

### A.1 Sprint Header (fill each sprint)

| Field | Value |
|---|---|
| Sprint ID / Name | `SPRINT-NNN` / *(theme)* |
| Dates | `YYYY-MM-DD` → `YYYY-MM-DD` (10 working days) |
| Sprint goal | *(one sentence, testable, patient-safety framed)* |
| Target SUT(s) | OpenMRS primary; *(RAL targets: OpenEMR / HAPI / SMART / omiiCARE)* |
| Release / milestone | *(e.g. R2026.3 hardening)* |
| QA lead / engineers | *(names)* |
| Modules in scope (prefixes) | *(subset of AUTH REG SRCH PDASH VISIT VITAL CLIN APPT ORDLAB PHARM RBAC DATA RPT FHIR HL7 SEC A11Y PERF NOTIF BILL TELE)* |
| Requirement IDs in scope | *(list of `REQ-<PREFIX>-NNN`)* |
| Entry gate signed off? | Y / N (see A.7) |

### A.2 Sprint Goal — quality criteria

A good sprint goal for this portfolio is **(a) testable**, **(b) traceable to `REQ-` ids**, and **(c) names the patient-safety or PHI risk it retires**. Avoid goals that cannot be demonstrated by a passing/failing test set.

- Good: *"Ship conflict-safe appointment booking (REQ-APPT-001/004/009) with no double-book and full state-machine coverage."*
- Weak: *"Improve scheduling."* (not testable, no `REQ-`, no risk named)

### A.3 Test Scope — in / out

| In scope | Out of scope (this sprint) |
|---|---|
| Stories accepted into the sprint backlog | Anything not on the committed board |
| New + regression cases for touched `REQ-` ids | Full 1,349-case regression (run in hardening/release sprint) |
| RAL contract checks for each ported adapter touched | Untouched downstream SUTs (OpenEMR/HAPI/SMART/omiiCARE) unless RAL changed |
| FHIR R4 / HL7 v2 conformance on changed resources/messages | Net-new standards not yet in requirements |
| A11y (WCAG 2.1 AA) on changed UI | Full-portfolio A11y audit |
| PERF/SEC **design + criteria** review; execution only on local env | PERF/SEC execution against public `o2.openmrs.org` (forbidden) |

### A.4 Test Design Techniques per work type

Pick techniques deliberately; record the chosen mix in each story's test charter.

| Work type | Primary techniques | Mandatory adds |
|---|---|---|
| New form/workflow (REG, VISIT, APPT) | Functional, Negative, Boundary, Decision-Table | A11y (WCAG 2.1 AA), Exploratory |
| State machines (APPT, ORDLAB, PHARM, VISIT) | State-Transition, Decision-Table | Negative (illegal transitions) |
| Search/filters (SRCH, PDASH) | Boundary, Pairwise, Negative | Performance criteria (local only) |
| API (FHIR R4, HL7 v2, RAL) | Functional, Negative, Boundary, Contract/Schema | FHIR validator, HL7 ACK/NAK checks |
| Auth/permissions (AUTH, RBAC, SEC) | Decision-Table, Negative | OWASP checks, audit-log assertion (RPT) |
| Data integrity (DATA, RPT) | Boundary, Decision-Table | DB-level assertions, audit trail |

### A.5 Estimation Model

Estimate test effort **per story**, not per sprint, then roll up. Use the rubric below; convert points→hours with the team's calibrated factor (default **1 pt ≈ 2.0 test-hours**, includes design + execution + defect logging + retest).

| Story test size | New cases | Design tech count | Risk | Test points |
|---|---|---|---|---|
| XS | 1–3 | 1–2 | Low | 1 |
| S | 4–8 | 2–3 | Low/Med | 2 |
| M | 9–16 | 3–4 | Med | 3 |
| L | 17–28 | 4–5 | High | 5 |
| XL | 29+ | 5+ | High/Safety | 8 |

**Risk multiplier** (applied to points): patient-safety or PHI-exposing story → **×1.5** (round up). Cross-SUT RAL story → **+1 pt** for adapter contract matrix.

### A.6 Capacity Planning

| Input | Formula / default |
|---|---|
| Engineers × days | `N_eng × 10 days` |
| Productive test-hours/day | `6.0` (10 days, meetings/standups deducted) |
| Raw capacity (test-hours) | `N_eng × 10 × 6.0` |
| Buffer for defect retest/exploratory | reserve **25%** |
| Net committable test-hours | `raw × 0.75` |
| Net committable test-points | `net_hours / 2.0` |

**Rule:** committed test-points ≤ net committable points. If over, de-scope lowest-risk, non-safety stories first (never drop a patient-safety/PHI story to fit capacity — re-plan dev scope instead).

### A.7 Entry Criteria (test-ready gate)

- [ ] Story has linked `REQ-<PREFIX>-NNN` id(s) and acceptance criteria.
- [ ] Build deployed to QA env; smoke (AUTH login + patient search) green.
- [ ] Test data seeded (non-PHI demo patients); RAL target reachable for ported adapters.
- [ ] Test charter + technique mix written; cases drafted in `manual-testing/test-cases/openmrs/<MODULE>.csv`.
- [ ] RTM rows reserved/updated in `manual-testing/rtm/RTM.csv`.

### A.8 Daily Flow (cadence)

| Day | Focus |
|---|---|
| D1 | Refine stories, finalize charters, draft cases, seed data, smoke build. |
| D2–D3 | Static review of FHIR/HL7/RAL contracts; design Decision-Table/State-Transition cases. |
| D3–D7 | Execute functional/negative/boundary + A11y on delivered stories; log defects daily. |
| D5–D9 | Retest fixes, exploratory charters, pairwise/API runs, RTM updates. |
| D9 | PERF/SEC **criteria** sign-off (execution local only); regression on impacted modules. |
| D10 | Exit-criteria check, report, RTM finalize, retro inputs. |

**Daily standup QA line:** *yesterday executed / defects opened / blocked-by-build / today's charter / at-risk `REQ-` ids.*

### A.9 Definition of Done (story-level QA DoD)

A story is **Done** only when ALL hold:

- [ ] Every linked `REQ-` id has ≥1 passing case at required priority (P1 fully covered).
- [ ] Negative + boundary cases executed; no open Critical/High defect.
- [ ] State machines: all legal transitions pass, all illegal transitions blocked.
- [ ] A11y: zero WCAG 2.1 AA blockers on changed UI (keyboard, labels, contrast, live regions).
- [ ] FHIR R4 resources validate; HL7 v2 messages ACK; RAL contract holds for each target touched.
- [ ] **Patient-safety:** no wrong-patient, wrong-dose, wrong-result, or lost-data path open.
- [ ] **PHI:** minimum-necessary respected; audit log (RPT) records the action; no PHI in logs/URLs/screenshots.
- [ ] Cases committed to module CSV; RTM rows updated; defects linked.

### A.10 Sprint Exit Criteria

| Criterion | Threshold |
|---|---|
| Committed P1 stories meeting DoD | **100%** |
| P1 requirement coverage (in-scope `REQ-` ids) | **100%** executed |
| P2 requirement coverage | **≥ 90%** executed |
| Open Critical defects | **0** |
| Open High defects | **0** (or risk-accepted, signed by QA lead + PO) |
| Patient-safety / PHI defects open | **0** (hard gate, no waiver) |
| RTM updated for all in-scope reqs | **100%** |
| Regression on impacted modules | green |
| PERF/SEC criteria reviewed (exec local only) | signed off |

---

## Part B — Worked Example: `SPRINT-014`

### B.1 Sprint Header

| Field | Value |
|---|---|
| Sprint ID / Name | `SPRINT-014` — "Safe Scheduling & Result Routing" |
| Dates | 2026-07-06 → 2026-07-17 (10 working days) |
| Sprint goal | **Deliver conflict-safe appointment lifecycle and result-routing such that no double-book, no wrong-patient result, and every action is audit-logged — verified against `REQ-APPT-*`, `REQ-ORDLAB-*`, `REQ-FHIR-*`, `REQ-HL7-*`.** |
| Target SUT(s) | OpenMRS (primary); RAL contract check vs HAPI FHIR for Appointment + DiagnosticReport |
| QA team | 3 engineers (1 lead) |
| Modules | APPT, ORDLAB, FHIR, HL7, RBAC, A11Y, RPT |

### B.2 Stories & Test Scope

| Story | `REQ-` ids | Techniques | Patient-safety / PHI | Module CSV |
|---|---|---|---|---|
| S-1 Book appointment (valid path) | REQ-APPT-001, 002, 005 | Functional, Boundary, Decision-Table, A11y | Wrong-provider/slot | APPT.csv |
| S-2 Prevent double-book / past-date | REQ-APPT-003, 004 | Negative, Boundary, Pairwise | Double-book = safety | APPT.csv |
| S-3 Appointment state machine | REQ-APPT-009 (8 cases) | State-Transition, Negative | Illegal transition | APPT.csv |
| S-4 Lab order → result routing | REQ-ORDLAB-* (result match) | Functional, Negative, Decision-Table | Wrong-patient result | ORDLAB.csv |
| S-5 FHIR Appointment + DiagnosticReport (R4) | REQ-FHIR-* | Contract/Schema, Negative | PHI minimum-necessary | FHIR.csv |
| S-6 HL7 v2 ORU^R01 result inbound | REQ-HL7-* | Functional, ACK/NAK, Boundary | Result integrity | HL7.csv |
| S-7 Audit logging of all above | REQ-RPT-* | Functional, DB assertion | PHI audit trail | RPT.csv |
| S-8 RBAC on scheduling actions | REQ-RBAC-* | Decision-Table, Negative (OWASP authz) | Least-privilege | RBAC.csv |

### B.3 Estimation (worked)

| Story | New cases | Tech count | Risk | Base pts | Safety ×1.5 | RAL +1 | Final pts |
|---|---|---|---|---|---|---|---|
| S-1 | 8 | 4 | Med | 3 | — | — | 3 |
| S-2 | 10 | 3 | High/Safety | 3 | 4.5→**5** | — | 5 |
| S-3 | 8 | 2 | High/Safety | 3 | 4.5→**5** | — | 5 |
| S-4 | 12 | 3 | High/Safety | 3 | 4.5→**5** | — | 5 |
| S-5 | 9 | 4 | Med/PHI | 3 | 4.5→**5** | +1 | **6** |
| S-6 | 8 | 3 | High/Safety | 3 | 4.5→**5** | — | 5 |
| S-7 | 6 | 2 | Med/PHI | 2 | 3→**3** | — | 3 |
| S-8 | 7 | 2 | High | 2 | — | — | 2 |
| **Total** | **68** | | | | | | **34 pts** |

### B.4 Capacity (worked)

| Input | Value |
|---|---|
| Engineers × days | 3 × 10 = 30 eng-days |
| Productive test-hours/day | 6.0 |
| Raw capacity | 30 × 6.0 = **180 test-hours** |
| Buffer (25%) reserved | 45 test-hours |
| Net committable | 135 test-hours |
| Net committable points (÷2.0) | **67.5 → 67 pts** |
| Committed | **34 pts** |
| Utilization | 34 / 67 ≈ **51%** committed; remainder absorbs exploratory + retest + cross-SUT |

> Comfortable fit. Headroom intentionally allocated to defect retest churn (scheduling/results historically high defect-density) and RAL/HAPI cross-checks (S-5).

### B.5 Daily Flow (worked)

| Day | Activity |
|---|---|
| D1 (Mon 07-06) | Charters for S-1..S-8; seed demo patients/providers/slots; smoke AUTH+SRCH on QA build. |
| D2 (07-07) | Review FHIR Appointment/DiagnosticReport profiles + HL7 ORU^R01 spec; draft contract cases (S-5/S-6). |
| D3 (07-08) | Execute S-1 (book) + A11y on booking form; draft S-3 state-transition matrix. |
| D4 (07-09) | Execute S-2 (double-book/past-date negatives, pairwise); start S-4 result routing. |
| D5 (07-10) | Execute S-3 full state machine (8 legal + illegal); log defects; S-4 wrong-patient negatives. |
| D6 (07-13) | Execute S-5 FHIR validation + S-6 HL7 ACK/NAK + boundary; retest D3–D5 fixes. |
| D7 (07-14) | Execute S-7 audit assertions (DB) + S-8 RBAC authz negatives; exploratory charter on scheduling. |
| D8 (07-15) | Retest fixes; RAL contract run vs HAPI for Appointment+DiagnosticReport; RTM update pass. |
| D9 (07-16) | Regression on APPT/ORDLAB/FHIR/HL7; PERF criteria review (local env only) for booking search latency. |
| D10 (07-17) | Exit-criteria check, defect triage, RTM finalize in `RTM.csv`, sprint test report + retro inputs. |

### B.6 Definition of Done — applied to S-2 (double-book)

- [x] REQ-APPT-003 & REQ-APPT-004 each have passing P1 cases.
- [x] Negatives: past-date booking rejected; concurrent same-slot rejected for provider/patient/location.
- [x] Boundary: slot start == now, slot end overlap by 1 min, adjacent slots allowed.
- [x] A11y: booking error announced via live region; no keyboard trap.
- [x] **Safety:** no path produces two confirmed appointments in one slot.
- [x] **PHI/audit:** rejected attempt logged (RPT) without leaking other-patient PHI.
- [x] Cases in `APPT.csv`; RTM rows updated; defects linked & closed.

### B.7 Sprint Exit Report (target vs actual)

| Metric | Target | Actual (illustrative) | Status |
|---|---|---|---|
| Committed P1 stories at DoD | 100% | 8/8 | PASS |
| In-scope P1 `REQ-` executed | 100% | 100% | PASS |
| In-scope P2 `REQ-` executed | ≥90% | 94% | PASS |
| Cases executed / planned | 68 | 68 | PASS |
| Open Critical defects | 0 | 0 | PASS |
| Open High defects | 0 (or waived) | 1 (HL7 timezone offset — risk-accepted, lead+PO) | CONDITIONAL |
| Patient-safety / PHI open | 0 | 0 | PASS (hard gate) |
| RTM updated | 100% | 100% | PASS |
| Regression (impacted modules) | green | green | PASS |
| PERF/SEC criteria reviewed (local exec only) | signed | signed | PASS |

**Exit decision:** Sprint goal met. One High (S-6 HL7 timezone offset on ORU^R01 OBR/OBX timestamps) risk-accepted with a fix carried to `SPRINT-015`; no patient-safety/PHI defects open, so the hard gate passes.

### B.8 Cross-SUT / RAL Note

For ported targets, the **Resource Adapter Layer** must satisfy the same `REQ-` contract: S-5 FHIR R4 cases and S-6 HL7 v2 cases were authored once and run against OpenMRS (primary) and re-run via RAL against **HAPI FHIR** for `Appointment` and `DiagnosticReport`. OpenEMR / SMART Health IT / omiiCARE adapters are **out of scope** this sprint (RAL for those resources unchanged) and tracked for a future portability sprint.

---

### Appendix — Reuse Checklist

1. Copy Part A tables into the new sprint doc; fill A.1.
2. Pull in-scope `REQ-` ids from `requirements-catalog.md`; reserve RTM rows.
3. Estimate per B.3 rubric; verify against capacity (B.4).
4. Run daily flow (A.8); enforce DoD (A.9) per story.
5. Gate release on exit criteria (A.10); patient-safety/PHI = no-waiver.
