# UAT Plan & Documentation — OpenMRS-Primary Healthcare QA Portfolio

> **Purpose.** This document governs **User Acceptance Testing (UAT)**: the final,
> business-owned validation that the system delivers real clinical and operational
> value before it is accepted for production. Where the
> [Master Test Plan](./MASTER_TEST_PLAN.md) and
> [Release Test Plan](./RELEASE_TEST_PLAN.md) prove the system is *built right*
> (verification, QA-owned), this plan proves we *built the right thing*
> (validation, **business-owned**). UAT executes against
> **business scenarios**, not test cases, and is signed off by named role
> representatives — not by the QA team.
>
> **System under test.** PRIMARY: OpenMRS Reference Application (`o2.openmrs.org`).
> Portable to OpenEMR, HAPI FHIR, SMART Health IT, and the in-house **omiiCARE**
> app via the **Resource Adapter Layer (RAL)**. UAT runs on a dedicated,
> **owned/local UAT environment** — never the public demo host.
>
> **Standards in force.** FHIR R4, HL7 v2, WCAG 2.1 AA, OWASP ASVS/Top 10,
> HIPAA-like PHI handling. **Patient safety and PHI integrity are blocking,
> first-class acceptance criteria** — a single confirmed patient-safety or PHI
> defect blocks sign-off regardless of overall pass rate.
>
> **Baseline.** 472 requirements (`REQ-<PREFIX>-NNN`, 21 modules), traced through
> [requirements-catalog.md](../requirements/requirements-catalog.md) and
> [RTM.csv](../../manual-testing/rtm/RTM.csv); 1,349+ manual test cases
> (target ~4,000 at scale-out).

---

## 1. UAT Objectives

| # | Objective | Success measure |
|---|---|---|
| O1 | Confirm end-to-end **business workflows** succeed for each operational role | 100% of P1 business scenarios `Accepted` |
| O2 | Validate **patient-safety** controls behave correctly in real workflows | Zero open Critical safety defects (e.g. dosing, allergy, double-book) |
| O3 | Confirm **PHI handling** is correct and minimum-necessary | Zero open Critical/High PHI defects; audit trail visible |
| O4 | Validate **role-appropriate access** (clinician/nurse/clerk/admin) | RBAC scenarios pass; no privilege leakage observed |
| O5 | Confirm **standards-bound data** (FHIR R4, HL7 v2) is usable downstream | FHIR/HL7 acceptance scenarios `Accepted` |
| O6 | Confirm **accessibility** of core journeys for assistive-tech users | WCAG 2.1 AA acceptance scenarios pass on critical paths |
| O7 | Confirm the system is **fit for the business**, usable, and trustworthy | Business sign-off obtained from all role representatives |

**Out of scope for UAT** (covered earlier in the lifecycle, QA-owned): unit/integration/API
contract testing, performance/load testing, deep security/penetration testing, code-level
defects with no business-observable symptom. UAT *consumes* the confidence those gates
produced; it does not repeat them.

**Entry to UAT (all must hold):**

- Release build promoted to UAT env; version recorded in `PROJECT_METADATA.md`.
- All P1 system-test suites passed; [Quality Gates](../QUALITY_GATES.md) green.
- Zero open Critical/High defects on in-scope P1 modules.
- RTM coverage ≥ 95% on P1 requirements; FHIR/HL7 contracts frozen.
- UAT environment, synthetic data set, and role accounts provisioned and smoke-passed.
- UAT scenario pack reviewed and approved by Business Owner.

**Exit from UAT (all must hold):**

- 100% of P1 business scenarios `Accepted`; ≥ 95% of P2 `Accepted`.
- Zero open Critical defects; zero open High defects without an approved waiver.
- All confirmed patient-safety and PHI defects resolved and re-verified.
- Sign-off captured from every role representative + Business Owner (§7).

---

## 2. UAT Approach

- **Scenario-based, role-driven.** Each scenario is a realistic day-in-the-life task a
  named role performs, tracing to one or more `REQ-<PREFIX>-NNN` and module test cases.
- **Black-box, business language.** Participants test through the UI as their real role;
  acceptance is judged against business outcomes, not internal steps.
- **Exploratory overlay.** After scripted scenarios, each role spends a time-boxed
  exploratory session (charters per role) to surface usability/trust issues scripts miss.
- **Multi-system note.** UAT is run primarily against OpenMRS. When a target deployment
  uses OpenEMR / HAPI FHIR / SMART / omiiCARE, the **same scenario pack** is re-run through
  the **RAL**; only adapter-specific deltas get new scenarios. Acceptance criteria are
  written against business outcomes so they remain portable across adapters.

| Technique mapped into UAT | Where applied |
|---|---|
| Functional / end-to-end | Every business scenario |
| Negative & boundary | Safety scenarios (past-date booking, overdose, duplicate patient) |
| State-transition | Visit lifecycle, appointment status machine (`REQ-APPT-009`) |
| Decision-table | Eligibility/consent/role gating (`REQ-NOTIF`, `REQ-RBAC`) |
| Accessibility (WCAG 2.1 AA) | Critical-path screen-reader/keyboard walkthroughs |
| Exploratory | Per-role charters after scripted runs |

---

## 3. Business Scenarios by Role

Each scenario has an ID `UAT-<ROLE>-NN`, a business goal, traced requirements/modules,
and a binary outcome (`Accepted` / `Rejected`). Acceptance criteria are in §4.

### 3.1 Registration Clerk (R-CLERK)

| ID | Business scenario | Traces | Priority |
|---|---|---|---|
| UAT-CLERK-01 | Register a brand-new walk-in patient with full demographics | REQ-REG-001..010 · REG | P1 |
| UAT-CLERK-02 | Search for and avoid creating a duplicate of an existing patient | REQ-SRCH-001 · REQ-REG dup-check · SRCH/REG | P1 |
| UAT-CLERK-03 | Start a visit and check a patient in for their appointment | REQ-VISIT-001 · REQ-APPT-009 · VISIT/APPT | P1 |
| UAT-CLERK-04 | Book, reschedule, and cancel an appointment with reason capture | REQ-APPT-001/006/007 · APPT | P1 |
| UAT-CLERK-05 | Attempt an action outside the clerk's authority (e.g. prescribe) — must be blocked | REQ-RBAC-012 · RBAC | P1 |

### 3.2 Nurse (R-NURSE)

| ID | Business scenario | Traces | Priority |
|---|---|---|---|
| UAT-NURSE-01 | Capture a full vitals set during a visit; out-of-range values flagged | REQ-VITAL-001.. · VITAL | P1 |
| UAT-NURSE-02 | Record an allergy and a condition on the patient record | REQ-CLIN (allergy/condition) · CLIN | P1 |
| UAT-NURSE-03 | View patient dashboard: latest vitals, active problems, allergies surface clearly | REQ-PDASH-001.. · PDASH | P1 |
| UAT-NURSE-04 | Attempt to add a clinical **diagnosis** — must be blocked (nurse cannot Dx) | REQ-RBAC-012 · RBAC | P1 |
| UAT-NURSE-05 | Acknowledge a critical-vital notification respecting consent/min-necessary PHI | REQ-NOTIF · REQ-VITAL · NOTIF | P2 |

### 3.3 Clinician / Doctor (R-DOCTOR)

| ID | Business scenario | Traces | Priority |
|---|---|---|---|
| UAT-CLIN-01 | Open an encounter, record a diagnosis, and write a clinical note | REQ-CLIN-001.. · CLIN | P1 |
| UAT-CLIN-02 | Place a lab order and review the resulting result when posted | REQ-ORDLAB-001.. · ORDLAB | P1 |
| UAT-CLIN-03 | Prescribe a medication; **allergy/interaction warning fires** for a contraindication | REQ-PHARM (safety) · PHARM | P1 |
| UAT-CLIN-04 | Attempt an unsafe dose beyond max — system blocks/warns | REQ-PHARM boundary · PHARM | P1 |
| UAT-CLIN-05 | Confirm encounter data exports correctly as **FHIR R4** for downstream use | REQ-FHIR-001.. · FHIR | P1 |
| UAT-CLIN-06 | Complete the visit and verify state transition to `Completed` | REQ-VISIT · REQ-APPT-009 · VISIT/APPT | P1 |

### 3.4 Administrator (R-ADMIN)

| ID | Business scenario | Traces | Priority |
|---|---|---|---|
| UAT-ADMIN-01 | Create a user, assign a role, verify the new user's effective access | REQ-RBAC-001.. · RBAC | P1 |
| UAT-ADMIN-02 | Retire a provider; confirm they vanish from order/booking dropdowns but history is preserved | REQ-RBAC-003/026 · RBAC | P1 |
| UAT-ADMIN-03 | Attempt to disable the **last administrator** — must be blocked | REQ-RBAC-025 · RBAC | P1 |
| UAT-ADMIN-04 | Review the **audit trail** for a PHI access/edit event | REQ-SEC (audit) · SEC | P1 |
| UAT-ADMIN-05 | Generate an operational report and confirm figures match underlying records | REQ-RPT-001.. · RPT | P2 |
| UAT-ADMIN-06 | Confirm an inbound **HL7 v2** ADT/ORU message is ingested and reflected in the patient record | REQ-HL7-001.. · HL7 | P2 |

### 3.5 Cross-cutting acceptance scenarios

| ID | Scenario | Traces | Priority |
|---|---|---|---|
| UAT-A11Y-01 | Complete registration + vitals capture keyboard-only with a screen reader | REQ-A11Y-001/007/012 · A11Y | P1 |
| UAT-SEC-01 | Confirm a logged-out / unauthorized user cannot reach PHI (session + RBAC) | REQ-AUTH · REQ-RBAC · AUTH/RBAC | P1 |
| UAT-DATA-01 | Edit then re-open a patient record; data persists, no silent loss, audited | REQ-DATA-001.. · DATA | P1 |
| UAT-TELE-01 | Conduct a telehealth visit and confirm encounter/notes captured | REQ-TELE-001.. · TELE | P2 |
| UAT-BILL-01 | Generate a charge for a service and confirm it reconciles to the visit | REQ-BILL-001.. · BILL | P2 |

---

## 4. Acceptance Criteria

UAT acceptance is **binary per scenario** and is judged by the role representative against
the business outcome. A scenario is `Accepted` only when **all** its criteria pass.

### 4.1 Universal acceptance criteria (apply to every scenario)

| AC | Criterion |
|---|---|
| UAC-1 | The business goal is achieved through the UI by the intended role, with no workaround. |
| UAC-2 | No data is silently lost, corrupted, or duplicated (`REQ-DATA-*`). |
| UAC-3 | **Patient-safety** controls behave correctly (warnings fire, unsafe actions blocked). |
| UAC-4 | **PHI** is shown only on a need-to-know basis; no over-exposure (minimum-necessary). |
| UAC-5 | The action is recorded in the audit trail where PHI is read/written (`REQ-SEC-*`). |
| UAC-6 | Role boundaries hold — the actor cannot exceed their authority (`REQ-RBAC-*`). |
| UAC-7 | Error/validation messages are clear, recoverable, and non-destructive (`REQ-A11Y-008`). |

### 4.2 Representative scenario-level criteria

| Scenario | Pass when… |
|---|---|
| UAT-CLERK-02 | Duplicate-detection surfaces the existing patient **before** a new record is created. |
| UAT-CLERK-04 | Past-date booking is rejected (`REQ-APPT-003`); cancel captures reason + releases slot (`REQ-APPT-007`). |
| UAT-NURSE-01 | Out-of-range vital is visibly flagged; value persists to dashboard (`REQ-PDASH-*`). |
| UAT-NURSE-04 | Nurse is denied diagnosis entry with a clear, non-leaking message (`REQ-RBAC-012`). |
| UAT-CLIN-03 | Allergy/interaction warning fires for the contraindicated drug and requires acknowledgement. |
| UAT-CLIN-04 | Dose above max is blocked or hard-warned; no silent acceptance of an unsafe order. |
| UAT-CLIN-05 | Exported resource validates against **FHIR R4**; code systems/URIs correct (`REQ-FHIR-*`). |
| UAT-ADMIN-02 | Retired provider absent from active dropdowns; historical references intact. |
| UAT-ADMIN-03 | Disabling the last admin is blocked with an explanatory message (`REQ-RBAC-025`). |
| UAT-ADMIN-06 | HL7 v2 ADT/ORU parsed; patient/result reflected without manual fix-up (`REQ-HL7-*`). |
| UAT-A11Y-01 | Entire journey completable keyboard-only; all controls have accessible names (`REQ-A11Y-012`). |

> **Patient-safety / PHI override.** Any confirmed Critical patient-safety or PHI defect
> **fails the scenario and blocks UAT sign-off**, even if every other criterion passes and
> the overall scenario pass-rate target is met.

---

## 5. UAT Environment & Test Data

### 5.1 Environment

| Aspect | Specification |
|---|---|
| Environment | Dedicated **UAT** tier, isolated from DEV/SIT and from PROD |
| Hosting | **Owned/local** OpenMRS RefApp build (RAL configured for the target adapter) |
| Build | Release-candidate, pinned; version recorded in `PROJECT_METADATA.md` |
| Data residency | Synthetic only; no production PHI ever loaded |
| Standards endpoints | FHIR R4 base + HL7 v2 inbound channel enabled and smoke-tested |
| Access | Per-role accounts (clerk/nurse/doctor/admin); SSO/session as per AUTH config |
| Public-host rule | **Never** the public `o2.openmrs.org` demo; perf/security suites never run here |
| Reset cadence | Data restored to baseline snapshot at the start of each UAT cycle |

### 5.2 Test data (synthetic, PHI-safe)

| Data set | Contents | Source |
|---|---|---|
| Patients | New/walk-in, existing, near-duplicate, allergy-bearing, pediatric (dose boundaries) | [SAMPLE_DATA.md](../SAMPLE_DATA.md) / [TEST_DATA_STRATEGY.md](../TEST_DATA_STRATEGY.md) |
| Providers | Doctor, nurse, retired provider, multi-location provider | RBAC matrix seed |
| Schedules | Open/booked/conflicting slots, past-date slot (negative) | APPT seed |
| Orders/meds | Standard order, contraindicated drug pair, over-max dose | PHARM/ORDLAB seed |
| Standards payloads | Valid + malformed FHIR R4 resource, valid HL7 v2 ADT/ORU | FHIR/HL7 seed |
| Roles/users | One account per role with documented credentials (UAT-only) | RBAC seed |

- All identifiers are clearly fictitious; data set is versioned with the release.
- Each UAT participant uses **their own role account** — no shared admin logins — so the
  audit trail attributes actions correctly (supports UAC-5 / UAT-ADMIN-04).

---

## 6. Defect Handling

### 6.1 Severity & target response

| Severity | Definition (business view) | Sign-off impact | Target fix |
|---|---|---|---|
| **Critical** | Patient-safety or PHI exposure; data loss/corruption; role/security breach; P1 workflow blocked | **Blocks sign-off** — no waiver | Immediate; re-verify before exit |
| **High** | P1 workflow degraded with painful workaround; standards data unusable downstream | Blocks unless **Business Owner waiver** | Within the UAT cycle |
| **Medium** | Usability/clarity issue; P2 scenario affected; cosmetic data/labeling | Does not block; tracked | Next release acceptable |
| **Low** | Minor cosmetic / nice-to-have | Does not block | Backlog |

### 6.2 Workflow

1. **Log** — Reporter (role rep) raises a defect with: scenario ID, role, steps, expected
   vs actual, **synthetic** data refs (never real PHI), screenshots, traced `REQ-*`.
2. **Triage** — QA Lead + Business Owner agree severity within 1 business day.
3. **Fix & promote** — Dev fixes; build re-promoted to UAT with version bump recorded.
4. **Re-verify** — Original reporter re-runs the failed scenario; outcome flips to `Accepted` only on pass.
5. **Regression touch** — QA confirms the fix did not break adjacent P1 scenarios (RTM-guided).
6. **Close** — Defect closed with re-verification evidence linked.

> **PHI in defect reports.** Defect tickets, screenshots, and logs must contain only
> synthetic data. Any accidental real-PHI capture is itself a **Critical** defect and is
> redacted immediately. This mirrors the HIPAA-like, minimum-necessary stance enforced
> across the portfolio.

### 6.3 Metrics tracked per cycle

| Metric | Target |
|---|---|
| P1 scenarios `Accepted` | 100% |
| P2 scenarios `Accepted` | ≥ 95% |
| Open Critical defects at exit | 0 |
| Open High defects at exit (no waiver) | 0 |
| Patient-safety/PHI defects open at exit | 0 |
| Defect re-open rate | < 5% |
| Mean triage time | ≤ 1 business day |

---

## 7. Sign-off Template

UAT is **accepted** only when every section below is completed and signed. Sign-off is a
business decision; QA facilitates but does not sign on behalf of a role.

### 7.1 Acceptance summary

| Field | Value |
|---|---|
| Release / build under UAT | `<version from PROJECT_METADATA.md>` |
| UAT environment | `<UAT host / adapter: OpenMRS · OpenEMR · HAPI · SMART · omiiCARE>` |
| UAT cycle dates | `<start> – <end>` |
| P1 scenarios accepted | `__ / __ (100% required)` |
| P2 scenarios accepted | `__ / __ (≥95% required)` |
| Open Critical / High defects | `__ / __` |
| Patient-safety / PHI defects open | `__ (must be 0)` |
| Approved High-defect waivers | `<ids or "none">` |
| Overall recommendation | ☐ Accept ☐ Accept-with-conditions ☐ Reject |

### 7.2 Role sign-offs

| Role | Representative | Scenarios owned | Decision (Accept/Reject) | Date | Signature |
|---|---|---|---|---|---|
| Registration Clerk | | UAT-CLERK-01..05 | | | |
| Nurse | | UAT-NURSE-01..05 | | | |
| Clinician / Doctor | | UAT-CLIN-01..06 | | | |
| Administrator | | UAT-ADMIN-01..06 | | | |
| Accessibility rep | | UAT-A11Y-01 | | | |
| QA Lead (facilitator) | | All — evidence/RTM integrity | | | |
| **Business Owner** | | **Final acceptance** | | | |

### 7.3 Conditions / open items (if Accept-with-conditions)

| # | Condition | Owner | Due | Severity |
|---|---|---|---|---|
| | | | | |

---

## 8. Schedule

A standard UAT cycle is **10 business days**, aligned to the Release Test Plan window and
preceded by the QA-owned system-test pass.

| Day | Phase | Activities | Owner | Exit signal |
|---|---|---|---|---|
| T-3..T-1 | **Prep** | Provision UAT env, load synthetic data, smoke-test role logins, finalize scenario pack | QA Lead | Entry criteria (§1) met |
| Day 1 | **Kickoff** | Walkthrough of scenarios, roles, defect process; environment confirmed | All | Participants ready |
| Day 2–4 | **Scripted UAT — Wave 1** | Clerk + Nurse scenarios; daily defect triage | Clerk, Nurse reps | Wave-1 scenarios run |
| Day 5–7 | **Scripted UAT — Wave 2** | Clinician + Admin + cross-cutting (A11Y/SEC/DATA/FHIR/HL7) | Doctor, Admin, A11Y reps | Wave-2 scenarios run |
| Day 8 | **Exploratory** | Per-role exploratory charters; trust/usability probing | All reps | Charters complete |
| Day 9 | **Defect burn-down** | Fix re-verification, regression touch, waiver decisions | Dev + QA + Business | Exit criteria approaching |
| Day 10 | **Sign-off** | Final metrics review, go/no-go, capture §7 signatures | Business Owner | UAT accepted / rejected |

**Cadence & dependencies**

- Daily 15-min triage stand-up (QA Lead + Business Owner) throughout the cycle.
- A blocking Critical defect **pauses the clock** on affected scenarios until re-verified.
- If P1 acceptance < 100% at Day 10, the cycle extends (not waives) — patient-safety and
  PHI criteria are never waived; only Medium/Low items defer to backlog.
- Multi-system reruns (OpenEMR/HAPI/SMART/omiiCARE via RAL) are scheduled as **separate
  cycles** reusing this pack, with only adapter-delta scenarios added.

---

## 9. Traceability & References

- **Requirements:** [requirements-catalog.md](../requirements/requirements-catalog.md) — every UAT scenario traces to `REQ-<PREFIX>-NNN`.
- **RTM:** [RTM.csv](../../manual-testing/rtm/RTM.csv) — scenario ↔ requirement ↔ test-case coverage.
- **Reverse-engineering:** BRD / SRS / FRD / USE_CASES / [RBAC_MATRIX.md](../reverse-engineering/RBAC_MATRIX.md) / [RISK_REGISTER.md](../reverse-engineering/RISK_REGISTER.md) / FHIR_MAPPING / HL7_MAPPING.
- **QA management:** [MASTER_TEST_PLAN.md](./MASTER_TEST_PLAN.md) · [RELEASE_TEST_PLAN.md](./RELEASE_TEST_PLAN.md) · [RISK_BASED_TESTING_STRATEGY.md](./RISK_BASED_TESTING_STRATEGY.md) · [QUALITY_GATES.md](../QUALITY_GATES.md).
- **Data:** [TEST_DATA_STRATEGY.md](../TEST_DATA_STRATEGY.md) · [SAMPLE_DATA.md](../SAMPLE_DATA.md).

> **Document control.** Update this plan whenever the scenario pack, role set, environment,
> or exit criteria change. UAT scenarios are versioned with the release they validate.
