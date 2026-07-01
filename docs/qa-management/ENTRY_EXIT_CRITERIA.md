# Entry & Exit Criteria

> **Scope of this document.** The single normative source for the **entry
> criteria** (preconditions that must hold before a test activity may begin) and
> **exit criteria** (conditions that must hold before a test activity may be
> declared complete) across every test **level** and every test **phase** of the
> OpenMRS-primary healthcare QA portfolio. It refines — never contradicts — the
> [Master Test Plan](MASTER_TEST_PLAN.md), the [Release Test Plan](RELEASE_TEST_PLAN.md),
> the [Sprint Test Plan](SPRINT_TEST_PLAN.md), and inherits gate thresholds from
> [QUALITY_GATES.md](../QUALITY_GATES.md) and [DEFINITION_OF_DONE.md](../DEFINITION_OF_DONE.md).
> Traceability flows through [requirements-catalog.md](../requirements/requirements-catalog.md)
> (472 `REQ-<PREFIX>-NNN`) and [RTM.csv](../../manual-testing/rtm/RTM.csv).
>
> **System under test.** PRIMARY: OpenMRS Reference Application (`o2.openmrs.org`).
> Portable to OpenEMR, HAPI FHIR, SMART Health IT, and the in-house omiiCARE app
> via the **Resource Adapter Layer (RAL)**. All test data is synthetic and
> PHI-safe. **Performance (PERF) and security (SEC) suites run ONLY against
> owned/local environments — NEVER against public OpenMRS demo hosts.**
>
> **Standards in force.** FHIR R4, HL7 v2.x, WCAG 2.1 AA, OWASP ASVS / Top 10,
> HIPAA-like PHI handling. **Patient-safety and PHI-integrity criteria are
> first-class, blocking, and never waivable.**

---

## 1. How to Read This Document

- **Entry criteria** answer: *"May we start?"* Failing entry criteria is a
  **blocked** state, not a **failed** state — the test activity does not run.
- **Exit criteria** answer: *"May we stop and declare success?"* Failing exit
  criteria means the activity continues or escalates to go/no-go.
- **Gate verdicts** are tri-state: **GREEN** (all blocking criteria met),
  **AMBER** (only advisory/non-blocking criteria open — proceed with a logged
  waiver), **RED** (one or more blocking criteria open — stop).
- **Blocking vs advisory.** Patient-safety, PHI, security-critical, and
  data-integrity criteria are always blocking. Cosmetic/Low findings are advisory.
- **Waivers.** Any AMBER proceed requires a waiver record: ID, criterion,
  justification, risk owner, expiry. No patient-safety or PHI criterion is waivable.

### 1.1 Severity & Priority Vocabulary (shared across all gates)

| Severity | Meaning | Default disposition |
|---|---|---|
| **Critical / S1** | Patient-safety, PHI leak, data loss/corruption, auth bypass, system down | Blocks every gate; immediate stop |
| **High / S2** | Major function broken, no safe workaround, standards violation (FHIR/HL7/WCAG) | Blocks system/regression/release exit |
| **Medium / S3** | Function impaired, workaround exists | Blocks release only if count > threshold |
| **Low / S4** | Cosmetic, minor, doc | Advisory; tracked, never blocking |

| Priority | Modules (prefix) | Regression tier |
|---|---|---|
| **P1** | AUTH REG SRCH PDASH VISIT VITAL CLIN APPT ORDLAB PHARM RBAC DATA FHIR SEC | Full |
| **P2** | HL7 A11Y RPT PERF NOTIF BILL | Targeted |
| **P3** | TELE | Smoke + spot |

---

## 2. Entry & Exit Criteria per **Test Level**

The portfolio runs five test levels. Each level has distinct entry/exit gates;
a lower level's exit is (partly) a higher level's entry.

### 2.1 Level Overview

| Level | Owner | Primary assets | Entry depends on | Exit feeds |
|---|---|---|---|---|
| **Component / Unit** | Dev | Adapter/RAL units, validators | Code merged, build green | Integration entry |
| **Integration / API** | QA + Dev | DATA, FHIR, HL7 test cases | Component exit, env up | System entry |
| **System / E2E** | QA | 1,349 manual cases, 21 modules | Integration exit, full env | Regression entry |
| **Acceptance / UAT** | Business + QA | Use-case scenarios, RTM | System exit, release candidate | Release sign-off |
| **Non-functional (PERF/SEC/A11Y)** | Specialist QA | PERF SEC A11Y suites | System stable, **local env only** | Release go/no-go |

### 2.2 Component / Unit Level

| | Criterion | Blocking |
|---|---|---|
| **Entry** | Code merged to integration branch; `mvn verify` / `vite build` green | Yes |
| | RAL adapter contract version pinned for target SUT (OpenMRS default) | Yes |
| | Unit test data fixtures synthetic, PHI-free | Yes |
| **Exit** | 100% unit tests pass, 0 errors/0 failures | Yes |
| | Coverage ≥ 70% line on changed modules (per QUALITY_GATES §3 roadmap) | Advisory→Blocking |
| | No new Critical/High static-analysis (CodeQL/SpotBugs) findings | Yes (security-critical) |
| | All validators (FHIR R4 field, HL7 v2 segment) unit-verified | Yes |

### 2.3 Integration / API Level

| | Criterion | Blocking |
|---|---|---|
| **Entry** | Component exit GREEN; target environment reachable; RAL adapter deployed | Yes |
| | REST/FHIR base URLs, OAuth/session tokens provisioned for SUT | Yes |
| | DATA, FHIR, HL7 contract suites loaded and parameterized for SUT | Yes |
| **Exit** | 100% P1 DATA + FHIR API cases pass; ≥ 95% overall integration cases pass | Yes |
| | FHIR R4 responses validate against profiles; code-system URIs correct | Yes |
| | HL7 v2 ACK/NAK handled; MSH/PID/PV1 mapping verified | Yes |
| | Zero S1/S2 open on REQ-DATA-*, REQ-FHIR-*, REQ-HL7-* | Yes |
| | Adapter parity check: same logical case passes on ≥ 2 SUTs OR documented gap | Advisory |

### 2.4 System / E2E Level

| | Criterion | Blocking |
|---|---|---|
| **Entry** | Integration exit GREEN; full OpenMRS RefApp env seeded with synthetic cohort | Yes |
| | All 21 modules' test cases ready in RTM; data dependencies resolved | Yes |
| | Smoke phase (§4.1) passed on the build under test | Yes |
| **Exit** | ≥ 98% P1 cases pass; ≥ 95% P2; ≥ 90% P3 | Yes |
| | Zero open S1; zero open S2 on P1 modules | Yes |
| | Patient-safety scenarios (VITAL, ORDLAB, PHARM, CLIN) 100% pass | Yes (non-waivable) |
| | RBAC negative cases (least-privilege, escalation) 100% pass | Yes |
| | Every executed case linked to a `REQ-<PREFIX>-NNN` in RTM | Yes |

### 2.5 Acceptance / UAT Level

| | Criterion | Blocking |
|---|---|---|
| **Entry** | System exit GREEN; release candidate build frozen; UAT env = prod-like | Yes |
| | Business-signed use-case scenarios prepared from `USE_CASES.md` | Yes |
| | Known-issues list published; open S3/S4 disclosed to business owner | Yes |
| **Exit** | 100% business-critical use cases accepted by business owner | Yes |
| | Zero open S1/S2; S3 count ≤ release threshold (§5.3) | Yes |
| | Sign-off recorded (business owner + QA lead + product) | Yes |

### 2.6 Non-functional Level (PERF / SEC / A11Y)

| | Criterion | Blocking |
|---|---|---|
| **Entry** | System stable; **owned/local environment provisioned (NEVER public host)** | Yes (compliance) |
| | PERF: load profile, SLAs, monitoring baselined; SEC: scope + rules of engagement signed | Yes |
| | A11Y: axe-core/AT toolchain ready; target pages enumerated | Yes |
| **Exit** | PERF: p95 latency & throughput meet NFR SLAs; no resource leak over soak | Yes |
| | SEC: zero open Critical/High (OWASP Top 10 / ASVS); PHI never logged in clear | Yes (non-waivable) |
| | A11Y: zero WCAG 2.1 AA Level-A/AA blockers on P1 flows; keyboard-only operable | Yes |

---

## 3. Entry & Exit Criteria per **Phase**

Phases are time-boxed campaigns that compose the levels above. Order:
**Smoke → System → Regression → UAT → Release**.

### 3.1 Phase Sequencing & Gate Map

| Phase | Trigger | Level(s) exercised | Exit gate verdict required |
|---|---|---|---|
| Smoke | New build deployed | Integration + thin E2E | GREEN to admit build |
| System | Smoke GREEN | System / E2E (all 21 modules) | GREEN to enter Regression |
| Regression | Code freeze / RC built | System + targeted re-run | GREEN to enter UAT |
| UAT | Regression GREEN | Acceptance | GREEN to enter Release |
| Release | UAT GREEN + go/no-go | Final verification + NFR sign-off | GREEN to ship |

---

## 4. Phase Detail

### 4.1 Smoke (Build Acceptance) Phase

Fast (~30–45 min) confidence check that a build is worth deeper testing.

**Entry criteria**

- [ ] Build deployed to test environment; version recorded in `PROJECT_METADATA.md`.
- [ ] Health endpoints up (OpenMRS REST `/ws/rest/v1/session`, FHIR `/fhir/metadata`).
- [ ] RAL adapter responds to a contract ping for the active SUT.
- [ ] Synthetic login credentials and seed patient cohort present.

**Smoke test set (one P1 path per critical module)**

| Module | Smoke check | Linked REQ |
|---|---|---|
| AUTH | Login + session establish + logout | REQ-AUTH-001 |
| REG | Register one synthetic patient | REQ-REG-001 |
| SRCH | Find that patient by identifier/name | REQ-SRCH-001 |
| VISIT | Start a visit | REQ-VISIT-001 |
| VITAL | Capture one vitals set | REQ-VITAL-001 |
| CLIN | Create one encounter/observation | REQ-CLIN-001 |
| ORDLAB | Place one lab order | REQ-ORDLAB-001 |
| FHIR | `GET Patient/{id}` returns valid R4 | REQ-FHIR-001 |
| DATA | REST create→read round-trips | REQ-DATA-001 |

**Exit criteria**

- [ ] **100% of smoke cases pass** (zero tolerance — any failure = build rejected).
- [ ] No S1/S2 raised during smoke.
- [ ] Environment telemetry nominal (no error storms in logs; no PHI in logs).
- [ ] Verdict GREEN → build admitted to System phase. Any RED → build bounced to dev.

### 4.2 System (Functional E2E) Phase

Full functional validation across all 21 modules.

**Entry criteria**

- [ ] Smoke phase GREEN on this exact build.
- [ ] All in-scope `REQ-<PREFIX>-NNN` mapped to executable cases in RTM.
- [ ] Test data: synthetic cohort covers boundary/decision-table/state-transition needs.
- [ ] Test design techniques applied per module (Functional/Negative/Boundary/
      Decision-Table/State-Transition/Pairwise/Exploratory).

**Exit criteria**

| # | Criterion | Threshold | Blocking |
|---|---|---|---|
| 1 | P1 module pass rate | ≥ 98% | Yes |
| 2 | P2 module pass rate | ≥ 95% | Yes |
| 3 | P3 module pass rate | ≥ 90% | Yes |
| 4 | Open S1 defects | 0 | Yes |
| 5 | Open S2 on P1 modules | 0 | Yes |
| 6 | Patient-safety cases (VITAL/ORDLAB/PHARM/CLIN) | 100% pass | Yes (non-waivable) |
| 7 | RBAC negative/least-privilege cases | 100% pass | Yes |
| 8 | FHIR R4 / HL7 v2 conformance cases | 100% P1 pass | Yes |
| 9 | RTM coverage of in-scope requirements | 100% executed | Yes |
| 10 | Exploratory charters completed & logged | All chartered modules | Advisory |

### 4.3 Regression Phase

Confirms the release candidate has not regressed prior behavior.

**Entry criteria**

- [ ] Code freeze declared; RC build tagged; RAL contract frozen at freeze date.
- [ ] System phase GREEN on the RC.
- [ ] Regression tiers assigned (Full = P1 modules; Targeted = P2; Smoke+spot = P3).
- [ ] Defects fixed since last cycle have linked verification cases ("fixed-bug" retest set).

**Regression scope by tier**

| Tier | Modules | Selection rule |
|---|---|---|
| Full | AUTH REG SRCH PDASH VISIT VITAL CLIN APPT ORDLAB PHARM RBAC DATA FHIR SEC | All P1 cases re-run |
| Targeted | HL7 A11Y RPT PERF NOTIF BILL | Risk-ranked + changed-area cases |
| Smoke + spot | TELE | Smoke path + spot checks on changed code |

**Exit criteria**

- [ ] All Full-tier P1 cases re-executed; pass rate ≥ 99%.
- [ ] 100% of fixed-defect retests pass (no defect reopened).
- [ ] Zero new S1/S2 introduced vs prior baseline (no regression).
- [ ] Targeted/spot tiers complete per selection rule; no S1/S2 open.
- [ ] Flaky-test quarantine reviewed; no quarantined case masks a real S1/S2.
- [ ] Verdict GREEN → admit to UAT.

### 4.4 UAT (User Acceptance) Phase

Business validates the RC against real-world use cases.

**Entry criteria**

- [ ] Regression GREEN; RC promoted to a prod-like UAT environment.
- [ ] Use-case acceptance scenarios derived from `USE_CASES.md` and signed by business.
- [ ] Known-issues register published; residual S3/S4 disclosed to UAT owner.
- [ ] UAT participants provisioned with role-scoped accounts (RBAC honored).

**Exit criteria**

- [ ] 100% business-critical use cases accepted.
- [ ] Zero open S1/S2; S3 within release threshold (§5.3).
- [ ] Each rejected scenario either fixed+retested or formally deferred with owner.
- [ ] UAT sign-off captured (business owner + QA lead + product manager).

### 4.5 Release Phase

Final gate before shipping the cut.

**Entry criteria**

- [ ] UAT GREEN; release notes drafted; rollback plan documented and rehearsed.
- [ ] All CI quality gates (QUALITY_GATES.md gates 1–12) GREEN/AMBER-with-waiver.
- [ ] NFR sign-offs collected (PERF/SEC/A11Y exit met — local-only evidence attached).
- [ ] DoD (every change + every milestone) satisfied for all shipped work.

**Exit / Go-Live criteria**

- [ ] Go/no-go board records explicit GO with named approvers.
- [ ] Zero open S1/S2; PHI and patient-safety criteria all GREEN and unwaived.
- [ ] Final smoke on the exact release artifact passes 100%.
- [ ] Traceability complete: every shipped REQ has passing evidence in RTM.
- [ ] Backout/rollback verified executable; on-call & monitoring armed.

---

## 5. Quality Gates & Thresholds (consolidated)

### 5.1 Cross-Phase Exit Gate Summary

| Phase | Pass-rate gate | S1 open | S2 open | PHI/safety | Verdict to proceed |
|---|---|---|---|---|---|
| Smoke | 100% smoke | 0 | 0 | GREEN | GREEN |
| System | P1 ≥98 / P2 ≥95 / P3 ≥90 | 0 | 0 on P1 | GREEN | GREEN |
| Regression | Full P1 ≥99; retests 100% | 0 | 0 | GREEN | GREEN |
| UAT | Biz-critical 100% accepted | 0 | 0 | GREEN | GREEN |
| Release | Final smoke 100% | 0 | 0 | GREEN (unwaived) | GO |

### 5.2 Defect-Density Release Thresholds

| Severity | Release block threshold | Rationale |
|---|---|---|
| S1 / Critical | 0 (any open blocks) | Patient safety / PHI / data loss |
| S2 / High | 0 open on P1 modules | No safe workaround on core flows |
| S3 / Medium | ≤ 10 open total, none on P1 patient-safety flows | Bounded, disclosed, owner-deferred |
| S4 / Low | No limit; tracked in KNOWN_ISSUES.md | Advisory only |

### 5.3 Suspension & Resumption Criteria

**Suspend a phase when:** environment is down > 2h; smoke fails after deploy;
an S1 is found that blocks > 30% of remaining cases; PHI exposure detected in any
artifact/log; or PERF/SEC was inadvertently pointed at a non-owned host (**stop
immediately, purge, report**).

**Resume when:** the blocking condition is resolved and re-verified, a fresh
smoke passes 100%, and the affected REQs are re-baselined in RTM.

---

## 6. Multi-System (RAL) Entry/Exit Considerations

The same logical test must hold across SUTs reached through the Resource Adapter
Layer. Entry/exit add adapter-parity dimensions.

| SUT | Entry addition | Exit addition |
|---|---|---|
| OpenMRS (primary) | RefApp O2 build pinned | All gates above apply in full |
| OpenEMR | Adapter mapping table complete | P1 parity ≥ 95% or documented gap |
| HAPI FHIR | FHIR R4 profile set loaded | 100% P1 FHIR cases pass |
| SMART Health IT | SMART launch/scopes configured | Auth + scope cases pass |
| omiiCARE (in-house) | Build + RAL contract aligned | Full system + NFR exit met |

**RAL exit rule.** A requirement is "portable-verified" only when it passes on
OpenMRS **and** at least one secondary SUT, **or** the gap is logged in the
adapter parity register with a risk owner.

---

## 7. Roles, Evidence & Auditability

| Role | Owns | Gate authority |
|---|---|---|
| QA Lead | Phase entry/exit verdicts; waiver log | Declares GREEN/AMBER/RED |
| Test Engineers | Execution, RTM updates, defect raising | Recommend verdict |
| Security QA | SEC exit (local-only); PHI checks | Veto on S1/S2 security |
| Accessibility QA | A11Y exit (WCAG 2.1 AA) | Veto on AA blockers |
| Performance QA | PERF exit (local-only SLAs) | Veto on SLA breach |
| Business Owner | UAT acceptance | Co-sign UAT/Release |
| Release Manager | Release go/no-go board | Records GO/NO-GO |

**Evidence retained per gate:** RTM snapshot, defect export (by severity),
test-run logs (PHI-scrubbed), NFR reports (local env, attached), waiver records,
and signed go/no-go minutes. All evidence is auditable for HIPAA-like review.

---

## 8. Master Entry/Exit Checklist (printable)

**Before any phase starts (Entry):**
- [ ] Prior phase verdict GREEN (or this is Smoke)
- [ ] Build/version recorded; RAL contract version known
- [ ] Environment up & health-checked; synthetic PHI-free data seeded
- [ ] In-scope REQs mapped in RTM; test design techniques assigned
- [ ] PERF/SEC scoped to **owned/local environment only**

**Before any phase closes (Exit):**
- [ ] Pass-rate thresholds met for the phase (§5.1)
- [ ] Zero open S1; zero open S2 on P1 modules
- [ ] Patient-safety + PHI criteria GREEN and unwaived
- [ ] FHIR R4 / HL7 v2 / WCAG 2.1 AA / OWASP criteria met where in scope
- [ ] RTM updated; defects triaged; waivers (if any) logged with owner+expiry
- [ ] Verdict recorded with approver names

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-07-01 | Principal QA Engineer | Initial entry/exit criteria for OpenMRS-primary QA portfolio |
