# Release Test Plan — v1.0.0

> **Scope of this document.** A *release-scoped* test plan governing the single
> v1.0.0 cut. It refines — never contradicts — the [Master Test Plan](../MASTER_TEST_PLAN.md),
> draws prioritization from the [Risk Register](../reverse-engineering/RISK_REGISTER.md)
> and [RISK_ANALYSIS.md](../RISK_ANALYSIS.md), traces coverage through
> [RTM.csv](../../manual-testing/rtm/RTM.csv), and enforces the
> [Quality Gates](../QUALITY_GATES.md). Where the Master Test Plan defines *what
> is tested across the path to release*, this plan defines *what must pass for
> THIS release to ship, on which environments, under which go/no-go rules, and how
> we roll back if it does not.*
>
> **System under test.** PRIMARY: OpenMRS Reference Application (`o2.openmrs.org`).
> Designed for portability to OpenEMR, HAPI FHIR, SMART Health IT, and the in-house
> omiiCARE app via the **Resource Adapter Layer (RAL)**. All data is synthetic and
> PHI-safe. **Performance and security suites run ONLY against owned/local
> environments — never against public OpenMRS demo hosts.**
>
> **Standards in force.** FHIR R4, HL7 v2, WCAG 2.1 AA, OWASP ASVS/Top 10,
> HIPAA-like PHI handling. Patient safety and PHI integrity are first-class,
> blocking concerns throughout.

---

## 1. Release Identity

| Field | Value |
|---|---|
| Release | v1.0.0 (first GA cut of the OpenMRS-primary QA portfolio) |
| Release type | Major / GA — full regression required |
| Code freeze | T-5 business days before target ship |
| Test window | 10 business days (regression + hardening + go/no-go) |
| Requirements baseline | 472 requirements, `REQ-<PREFIX>-NNN`, 21 modules |
| Test asset baseline | 1,349 manual test cases (target ~4,000 at scale-out) |
| Primary SUT version | OpenMRS RefApp O2 pinned build (recorded in `PROJECT_METADATA.md`) |
| Adapter | RAL contract version frozen at freeze date |

---

## 2. Release Scope

### 2.1 Modules in scope (all 21)

| Prefix | Module | Test cases | Release priority | Regression tier |
|---|---|---:|---|---|
| AUTH | Authentication / session | 63 | P1 | Full |
| REG | Patient Registration | 94 | P1 | Full |
| SRCH | Patient Search | 64 | P1 | Full |
| PDASH | Patient Dashboard | 64 | P1 | Full |
| VISIT | Visit Management | 67 | P1 | Full |
| VITAL | Vitals Capture | 67 | P1 | Full |
| CLIN | Clinical / Encounters | 68 | P1 | Full |
| APPT | Appointment Scheduling | 68 | P1 | Full |
| ORDLAB | Orders / Lab | 64 | P1 | Full |
| PHARM | Pharmacy / Medication | 58 | P1 | Full |
| RBAC | Roles / Authorization | 78 | P1 | Full |
| DATA | Data Integrity / REST | 47 | P1 | Full |
| FHIR | FHIR R4 APIs | 90 | P1 | Full |
| HL7 | HL7 v2 messaging | 57 | P2 | Targeted |
| SEC | Security | 68 | P1 | Full (local only) |
| A11Y | Accessibility (WCAG 2.1 AA) | 58 | P2 | Targeted |
| RPT | Reporting | 63 | P2 | Targeted |
| PERF | Performance | 43 | P2 | Targeted (local only) |
| NOTIF | Notifications | 44 | P2 | Targeted |
| BILL | Billing | 76 | P2 | Targeted |
| TELE | Telehealth | 45 | P3 | Smoke + spot |
| | **Total** | **1,349** | | |

### 2.2 In scope for v1.0.0

- Full functional regression of all P1 modules (patient-safety and PHI critical
  path: AUTH → REG → SRCH → PDASH → VISIT → VITAL → CLIN → ORDLAB → PHARM).
- Full RBAC matrix re-verification (REQ-RBAC-*) and authorization scope checks on
  every entry point (vertical and horizontal access controls).
- FHIR R4 conformance: resource shape, code-system URIs, value sets, `total`/paging
  on search bundles (REQ-FHIR-*, REQ-APPT-018).
- HL7 v2 inbound message handling + ACK/NAK behavior for ADT and SIU (REQ-HL7-*,
  REQ-APPT-019) — targeted regression of changed message paths.
- Security regression (OWASP Top 10 / ASVS) on owned environments only.
- Accessibility conformance spot-check of changed UI (WCAG 2.1 AA, P1 criteria).
- Cross-system smoke via RAL against at least one secondary adapter (HAPI FHIR or
  omiiCARE local) to prove portability of the contract.

### 2.3 Out of scope for v1.0.0

| Excluded | Reason | Tracked as |
|---|---|---|
| Live integrations / real EHR endpoints | PHI-safe policy; synthetic only | Deferred |
| Performance/security against public OpenMRS hosts | Prohibited (not owned) | Policy |
| Full A11Y audit of unchanged screens | Time-boxed; targeted to deltas | Backlog |
| Secondary adapters beyond one smoke target | RAL parity is post-1.0 | Roadmap |
| Load beyond NFR reference (200 concurrent) stress soak | Capacity test, separate cycle | NFR-SCAL backlog |

---

## 3. Regression Scope & Selection

Regression is **risk-weighted, not uniform**. Selection rules:

| Tier | Selection rule | Source of truth |
|---|---|---|
| **Full** | All P1 test cases for module + all cases tracing a Critical/High risk | RTM.csv `Risk`/`Priority` |
| **Targeted** | Cases tracing changed `REQ-*` IDs + their direct neighbors (state machines, decision tables) | Change log → RTM |
| **Smoke + spot** | Smoke suite + 1 case per P3 requirement | Section 4 |

**Mandatory full-regression triggers (any one forces Full tier on the module):**

- Change touches an authentication, authorization, or session control path (AUTH, RBAC, SEC).
- Change touches a clinical-data write path (VITAL, CLIN, ORDLAB, PHARM) — patient-safety class.
- Change alters a FHIR resource mapping, code-system URI, or HL7 segment parser.
- Change alters the RAL contract (affects every downstream system).
- A Critical/High defect was fixed in the module during this cycle (re-test + regression ring).

**Regression coverage target for v1.0.0:** ≥ 98% of P1 cases executed; 100% of
cases tracing a Critical-band risk (RISK-SEC-01 RBAC/privesc, vitals unit/range
integrity, PHI exposure) executed and passed.

---

## 4. Smoke & Sanity

### 4.1 Smoke suite (build acceptance — ~25 min, blocks all further testing)

Run on every promotion into a test environment. **Any failure = environment
rejected, no deeper testing proceeds.**

| # | Smoke check | Trace | Pass condition |
|---|---|---|---|
| S-01 | App + RAL health, DB reachable | infra | All health endpoints 200 |
| S-02 | Login as clinician role | REQ-AUTH-001 | Dashboard renders < NFR-PERF-001 (p95 ≤ 1.5 s e2e) |
| S-03 | Patient search returns a known synthetic record | REQ-SRCH-001 | First result page returned |
| S-04 | Open patient dashboard, widgets paint | REQ-PDASH-* | Diagnoses/Vitals/Visits/Allergies present |
| S-05 | Register a synthetic patient end-to-end | REQ-REG-001 | "Created Patient Record", ID generated |
| S-06 | Start visit + capture one vitals obs | REQ-VISIT-001, REQ-VITAL-* | Obs persisted, visible in Latest Observations |
| S-07 | REST read `GET /ws/rest/v1/patient/{uuid}` | REQ-DATA-* | 200, well-formed body |
| S-08 | FHIR read `GET /ws/fhir2/R4/Patient/{id}` | REQ-FHIR-* | Valid R4 resource, correct code systems |
| S-09 | RBAC negative: forbidden role blocked | REQ-RBAC-*, RISK-SEC-01 | 403 / access denied, audit entry written |
| S-10 | Audit log records a PHI read | REQ-SEC-* | Audit row present with actor + timestamp |

### 4.2 Sanity suite (post-fix focused — ~45 min)

Run after a targeted fix to confirm the change and its immediate blast radius
without a full regression. Composed of: the failing case(s), the state machine or
decision table they belong to (e.g., REQ-APPT-009 appointment status machine), and
the RBAC/audit guard on the touched entry point. Sanity green is a precondition to
re-entering the regression ring, not a substitute for it.

---

## 5. Entry & Exit Criteria

### 5.1 Entry criteria (to begin release test cycle)

- Code freeze declared; RAL contract version frozen and recorded.
- All CI Quality Gates green on the release candidate (build, typecheck, lint,
  SAST/CodeQL no-new-high, dependency-review no-new-high). See [QUALITY_GATES.md](../QUALITY_GATES.md).
- Smoke suite (4.1) passes on the Staging environment.
- RTM reconciled: every in-scope `REQ-*` has at least one executable test case.
- Test data seeded (synthetic, PHI-safe); known-issues list reviewed.

### 5.2 Exit criteria (release test cycle complete)

| # | Exit criterion | Threshold |
|---|---|---|
| E-1 | P1 test execution | ≥ 98% executed, ≥ 99% pass |
| E-2 | Critical-band risk coverage | 100% executed and passed |
| E-3 | Open S1 (critical) defects | 0 |
| E-4 | Open S2 (high) defects | 0 unmitigated; any deferred require recorded waiver |
| E-5 | Patient-safety class defects (clinical write paths) | 0 open at any severity |
| E-6 | PHI exposure / authz defects (SEC, RBAC) | 0 open |
| E-7 | FHIR R4 / HL7 v2 conformance regressions | 0 open |
| E-8 | A11Y P1 WCAG 2.1 AA criteria on changed UI | 0 open failures |
| E-9 | Residual-risk acceptance signed for all deferred items | Recorded per release |

---

## 6. Defect Severity & Triage

| Sev | Definition | Examples | Release impact |
|---|---|---|---|
| **S1 Critical** | Patient harm, PHI breach, data loss, auth bypass, full outage | RBAC privesc (RISK-SEC-01), wrong vitals unit persisted, PHI leak in API/audit gap | **Hard block** |
| **S2 High** | Core function broken, no safe workaround; standards non-conformance | Booking double-book (REQ-APPT-004), FHIR resource malformed, HL7 ACK wrong | **Block unless waived** |
| **S3 Medium** | Function degraded with workaround | Reporting filter edge case, NOTIF delay | Ship with note |
| **S4 Low** | Cosmetic / minor | Label, spacing, copy | Ship |

Every new S1/S2 defect must be back-mapped to (or raise) a row in the
[Risk Register](../reverse-engineering/RISK_REGISTER.md). Triage runs daily during
the test window; go/no-go board reviews the standing S1/S2 list.

---

## 7. Go / No-Go Criteria

The release is **GO** only when **all** of the following hold; **any single NO-GO
condition blocks the release.**

| Dimension | GO condition | NO-GO trigger |
|---|---|---|
| Exit criteria | All E-1…E-9 met | Any unmet |
| Patient safety | 0 open defects on clinical write paths (VITAL/CLIN/ORDLAB/PHARM) | ≥ 1 open |
| PHI & security | 0 open SEC/RBAC defects; OWASP regression clean (local) | ≥ 1 open authz/PHI defect |
| Interop conformance | FHIR R4 + HL7 v2 suites pass; code-system URIs correct | Any conformance regression |
| Quality gates | All blocking CI gates green on the exact RC artifact | Any blocking gate red |
| Smoke on prod-like | Smoke (4.1) green on the promotion target | Any smoke failure |
| Rollback readiness | Rollback rehearsed, DB backup verified restorable | Untested rollback |
| Residual risk | All deferrals waived and signed | Unsigned Critical-band residual |

**Decision authority:** the Go/No-Go board (Section 9) records the verdict, the
evidence (RTM execution snapshot, defect ledger, gate run links), and any
conditions attached to a conditional GO.

---

## 8. Release Risks & Mitigations

Sourced from the [Risk Register](../reverse-engineering/RISK_REGISTER.md);
release-relevant top risks:

| Risk ID | Area | Risk | Exposure | Release-time mitigation / test |
|---|---|---|---|---|
| RISK-SEC-01 | Security/PHI | Broken RBAC / privilege escalation | 20 (Critical) | 100% RBAC matrix (REQ-RBAC-*) + negative authz on every entry point; audit-trail verification |
| RISK-CLIN-* | Patient safety | Wrong vitals unit/range persisted | 20 (Critical) | °F/°C, kg/lb round-trip; absolute + critical-range validation per LOINC concept (REQ-VITAL-*) |
| RISK-DATA-* | Data integrity | PHI exposure via API/log | High | Field-level PHI checks on REST/FHIR responses + log scrubbing (REQ-DATA-*, REQ-SEC-*) |
| RISK-FHIR-* | Interop | Malformed R4 / wrong code-system URI | High | FHIR conformance suite; value-set + URI assertions (REQ-FHIR-*) |
| RISK-HL7-* | Interop | Wrong ACK / segment parse on inbound | Medium | SIU/ADT message + ACK matrix (REQ-HL7-*, REQ-APPT-019) |
| RISK-RAL-* | Portability | RAL contract drift breaks downstream | High | Contract freeze + one secondary-adapter smoke (HAPI FHIR / omiiCARE local) |
| RISK-REL-01 | Delivery | Insufficient regression time after late fix | Medium | Code-freeze enforcement; sanity-before-ring rule; conditional-GO waiver path |

---

## 9. Sign-Off Matrix

A release ships only with **every required signature**. A withheld required
signature is an automatic NO-GO.

| Role | Sign-off responsibility | Required | Evidence reviewed |
|---|---|:---:|---|
| QA Lead | Exit criteria met; test cycle complete | ✅ | RTM execution snapshot, defect ledger |
| QA Architect | Approach/coverage consistent with strategy & risk | ✅ | Regression selection, risk coverage map |
| Security Lead | 0 open PHI/authz defects; OWASP regression clean | ✅ | SEC/RBAC results, CodeQL/SAST, audit checks |
| Clinical Safety Reviewer | 0 open patient-safety defects on clinical write paths | ✅ | VITAL/CLIN/ORDLAB/PHARM results |
| Interop Lead | FHIR R4 + HL7 v2 conformance pass | ✅ | FHIR/HL7 suite results |
| Accessibility Reviewer | WCAG 2.1 AA P1 criteria on changed UI | ✅ | A11Y delta results |
| Release Manager / Maintainer | Approves release-gate transition & rollback readiness | ✅ | Gate status, rollback rehearsal log |
| Product Owner | Accepts residual risk and deferrals | ✅ | Signed residual-risk register |

---

## 10. Environment Promotion

All environments are **owned/local**. Performance and security suites execute
**only** on DEV/STAGING/PRE-PROD — never against public OpenMRS demo hosts.

```
DEV  →  TEST  →  STAGING  →  PRE-PROD  →  PROD
 |        |         |            |           |
 unit/    full    smoke +     go/no-go     post-deploy
 API/     regr.   E2E +       rehearsal,   smoke +
 contract ring    A11Y/SEC    rollback     PHI-read
                  (local)     drill        audit check
```

| Stage | Purpose | Gate to enter | Suites run | Promotion rule |
|---|---|---|---|---|
| DEV | Build + fast feedback | CI gates green | Unit, API, contract, RAL self-tests | Auto on green |
| TEST | Functional regression | Smoke (4.1) green | Full/Targeted regression per Section 3 | QA Lead approves |
| STAGING | Prod-like verification | TEST exit met | Smoke + E2E + A11Y + SEC + PERF (local) | QA + Security approve |
| PRE-PROD | Release rehearsal | Staging exit met | Smoke + rollback drill + DR backup-restore verify | Go/No-Go board |
| PROD | Live | GO verdict signed | Post-deploy smoke + PHI-read audit verification | Release Manager executes |

**Promotion invariants:** each promotion re-runs the smoke suite on the target;
the exact same RC artifact is promoted (no rebuild between STAGING and PROD); the
RAL contract version is identical across the chain.

---

## 11. Rollback Plan

| Item | Specification |
|---|---|
| Trigger | Any post-deploy S1, PHI exposure, authz bypass, clinical-write corruption, or smoke failure on PROD |
| Decision authority | Release Manager + on-call QA Lead; Clinical Safety Reviewer consulted for patient-safety triggers |
| Method | Re-promote previous known-good artifact (blue/green or versioned redeploy); RAL pinned to prior contract |
| Data | DB migrations are forward-only — rollback uses verified backup restore; **RPO ≤ 15 min, RTO ≤ 1 h** (NFR-AVAIL-004) |
| Pre-req (tested in PRE-PROD) | Backup restore rehearsed and verified; rollback runbook executed once before GO |
| Verification after rollback | Run smoke suite (4.1) + PHI-read audit check on the restored version; confirm no orphaned/partial clinical writes |
| Communication | Status to Go/No-Go board + stakeholders; defect filed and back-mapped to a risk row |

A rollback that has **not** been rehearsed in PRE-PROD is a Section 7 NO-GO
condition by itself.

---

## 12. Deliverables

- Release test execution report (RTM snapshot: executed / pass / fail per module).
- Defect ledger with severity, status, risk back-mapping, and waivers.
- Signed Go/No-Go record (Section 7) and Sign-Off matrix (Section 9).
- Residual-risk acceptance register (signed).
- Rollback rehearsal log and DR backup-restore verification evidence.
- Conformance evidence: FHIR R4 + HL7 v2 suite results; WCAG 2.1 AA delta results.

---

## 13. References

- [Master Test Plan](../MASTER_TEST_PLAN.md) · [Test Strategy](../TEST_STRATEGY.md) · [Quality Gates](../QUALITY_GATES.md)
- [Risk Register](../reverse-engineering/RISK_REGISTER.md) · [RISK_ANALYSIS.md](../RISK_ANALYSIS.md) · [NFR](../reverse-engineering/NFR.md)
- [Requirements Catalog](../requirements/requirements-catalog.md) · [RTM.csv](../../manual-testing/rtm/RTM.csv)
- [RBAC Matrix](../reverse-engineering/RBAC_MATRIX.md) · [FHIR Mapping](../reverse-engineering/FHIR_MAPPING.md) · [HL7 Mapping](../reverse-engineering/HL7_MAPPING.md)
