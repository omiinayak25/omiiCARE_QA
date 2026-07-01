# Release Checklists — OpenMRS-Primary Healthcare QA

> **Purpose.** Operational, signed-off checklists that gate every release of the
> OpenMRS-primary QA portfolio. Where the
> [Release Test Plan](./RELEASE_TEST_PLAN.md) defines *what must pass*, this
> document defines *the exact step-by-step actions, owners, and evidence* required
> to move a build from code-freeze to GA and to recover if it fails. It enforces
> the [Quality Gates](../QUALITY_GATES.md), traces through
> [RTM.csv](../../manual-testing/rtm/RTM.csv) and the
> [requirements catalog](../requirements/requirements-catalog.md) (472 reqs,
> `REQ-<PREFIX>-NNN`), and inherits risk priorities from the
> [Risk Register](../reverse-engineering/RISK_REGISTER.md).
>
> **System under test.** PRIMARY: OpenMRS Reference Application (`o2.openmrs.org`).
> Portable to OpenEMR, HAPI FHIR, SMART Health IT, and in-house omiiCARE via the
> **Resource Adapter Layer (RAL)**. All data is synthetic and PHI-safe.
> **Performance and security checks run ONLY against owned/local environments —
> never against public OpenMRS demo hosts.**
>
> **Standards in force.** FHIR R4, HL7 v2, WCAG 2.1 AA, OWASP ASVS/Top 10,
> HIPAA-like PHI handling. Patient-safety and PHI integrity are blocking, first-class
> concerns at every checkpoint below.

---

## 1. How to Use This Document

- Each checklist is a **gate**. A gate is **PASS** only when every blocking item
  is checked with linked evidence; a single blocking **FAIL** halts the release.
- Every item has an **Owner role**, an **Evidence** artifact, and a **Blocking?**
  flag. Advisory items are tracked but do not stop the release on their own.
- Evidence is attached to the release record (`docs/qa-management/` release folder
  or the Jira release ticket) and is auditable.
- Checklist legend: `[ ]` not started · `[~]` in progress · `[x]` passed ·
  `[!]` failed/blocked.

### 1.1 Release timeline (gate map)

| Phase | Checklist | Window | Gate owner | Exit gate |
|---|---|---|---|---|
| T-5d | §2 Pre-Release | Code freeze → hardening | QA Lead | Readiness sign-off |
| T-0 | §3 Release / Cutover | Deploy day | Release Manager | Go-live sign-off |
| T+0..T+3d | §4 Post-Release | After go-live | QA Lead + SRE | Stabilization sign-off |
| Any | §5 Smoke Verification | Every deploy (all envs) | QA on-call | Smoke green |
| T-2d/T-0 | §6 Data & Migration | Pre + during cutover | DBA + QA | Migration verified |
| T-1d | §7 Rollback Readiness | Before go-live | Release Manager | Rollback rehearsed |
| T-1d/T-0 | §8 Compliance & Audit | Before sign-off | Compliance/QA | Compliance sign-off |
| T-2d | §9 Docs & Versioning | Pre-release | Tech Writer + QA | Docs frozen |

---

## 2. Pre-Release Checklist (T-5d → code freeze)

**Gate owner:** QA Lead · **Exit:** Release Readiness Sign-off (§2.5)

### 2.1 Scope & traceability

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 2.1.1 | Release scope frozen; all in-scope `REQ-<PREFIX>-NNN` listed in release ticket | QA Lead | Release ticket scope table | Yes |
| 2.1.2 | RTM coverage ≥ 100% for all **P1** requirements across 21 modules | QA Lead | [RTM.csv](../../manual-testing/rtm/RTM.csv) coverage export | Yes |
| 2.1.3 | Every in-scope story has linked test cases (`TC-<PREFIX>-NNNN`) and acceptance criteria mapped | QA | RTM trace + USER_STORIES doc | Yes |
| 2.1.4 | No P1 requirement with 0 executed test cases | QA Lead | RTM gap report | Yes |
| 2.1.5 | Change set diffed vs prior baseline; impacted modules identified for regression tier | QA Lead | Impact analysis note | Yes |

### 2.2 Test execution & defects

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 2.2.1 | Full regression executed on all **Full-tier** modules (AUTH, REG, SRCH, PDASH, VISIT, VITAL, CLIN, APPT, ORDLAB, PHARM, RBAC, DATA, FHIR, SEC) | QA team | Run report | Yes |
| 2.2.2 | Targeted regression executed on P2 tiers (HL7, NOTIF, BILL, TELE, A11Y, PERF, RPT) | QA team | Run report | Yes |
| 2.2.3 | **0 open Critical/Sev-1 defects**; **0 open patient-safety defects** (CLIN/VITAL/ORDLAB/PHARM) | QA Lead | Defect dashboard | Yes |
| 2.2.4 | Open High/Sev-2 defects ≤ 5, each with documented risk acceptance | Release Mgr | Risk-acceptance log | Yes |
| 2.2.5 | All fixed defects have a verifying test case re-run and passed | QA | Defect→TC links | Yes |
| 2.2.6 | Exploratory charters completed for top-risk areas in [Risk Register](../reverse-engineering/RISK_REGISTER.md) | QA | Charter notes | Advisory |

### 2.3 Cross-cutting quality (per CLAUDE.md standards)

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 2.3.1 | **Security (SEC)** — OWASP Top-10 / ASVS suite green on **local env only**; no new High SAST (CodeQL) alerts; dependency review `fail-on-severity: high` clean | Sec QA | SECURITY_TESTING_GUIDE run + Security tab | Yes |
| 2.3.2 | **PHI handling** — no PHI in logs, URLs, error bodies, or analytics; minimum-necessary enforced on exports/notifications (`REQ-APPT-010`, NOTIF) | Sec QA | PHI scan report | Yes |
| 2.3.3 | **RBAC** — authorization matrix re-verified; no privilege escalation across roles (RBAC module, [RBAC_MATRIX](../reverse-engineering/RBAC_MATRIX.md)) | Sec QA | RBAC run report | Yes |
| 2.3.4 | **FHIR R4** — resource conformance, code-system URIs, and required fields validated against R4 profiles | FHIR QA | [FHIR_MAPPING](../reverse-engineering/FHIR_MAPPING.md) validation log | Yes |
| 2.3.5 | **HL7 v2** — message structure (ADT/ORM/ORU), segment/field correctness, ACK handling verified | Integration QA | [HL7_MAPPING](../reverse-engineering/HL7_MAPPING.md) log | Yes |
| 2.3.6 | **Accessibility (A11Y)** — WCAG 2.1 AA checks pass on changed screens; 0 new criticals (keyboard, focus, contrast, names) | A11Y QA | axe/manual report | Yes |
| 2.3.7 | **Performance (PERF)** — baseline load/latency within NFR thresholds on **local env only** | Perf QA | [NFR](../reverse-engineering/NFR.md) run | Yes |
| 2.3.8 | **RAL contract** — adapter contract tests green for OpenMRS; portability stubs (OpenEMR/HAPI/SMART/omiiCARE) unbroken | QA | RAL contract report | Yes |

### 2.4 Build, CI & environment

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 2.4.1 | All blocking [Quality Gates](../QUALITY_GATES.md) (1,2,4,5,9) green on release branch | DevOps | CI run URL | Yes |
| 2.4.2 | Release artifact built from frozen commit SHA; SHA recorded in `PROJECT_METADATA.md` | DevOps | Build manifest | Yes |
| 2.4.3 | OpenMRS RefApp O2 build pinned; RAL contract version frozen | DevOps | Version lock file | Yes |
| 2.4.4 | Staging mirrors production config (modules, flags, secrets sourcing) | SRE | Config diff | Yes |
| 2.4.5 | Docker images build (Gate 12) and pass image scan (no new Critical) | DevOps | Buildx + scan log | Yes |

### 2.5 Pre-release sign-off

| Role | Name | Decision (Go / No-Go) | Date |
|---|---|---|---|
| QA Lead | | | |
| Security Lead | | | |
| Compliance Officer | | | |
| Release Manager | | | |

> **Exit rule:** all §2.1–§2.4 blocking items `[x]` AND all four sign-offs **Go**.

---

## 3. Release / Cutover Checklist (T-0, deploy day)

**Gate owner:** Release Manager · **Exit:** Go-Live Sign-off (§3.4)

### 3.1 Pre-deploy gate

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 3.1.1 | §2 Pre-Release gate is PASS and unchanged since freeze (no new commits) | Release Mgr | Commit log | Yes |
| 3.1.2 | §6 Data & Migration dry-run succeeded on staging copy of prod data | DBA | Dry-run log | Yes |
| 3.1.3 | §7 Rollback rehearsed and time-boxed (RTO/RPO documented) | Release Mgr | Rollback drill log | Yes |
| 3.1.4 | Maintenance window communicated; downtime banner / read-only mode ready | SRE | Comms record | Yes |
| 3.1.5 | DB + filesystem backup taken immediately before cutover; restore point verified | DBA | Backup ID + checksum | Yes |
| 3.1.6 | On-call rota confirmed (QA, SRE, DBA, clinical SME reachable) | Release Mgr | Rota | Yes |

### 3.2 Deploy execution

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 3.2.1 | Deploy executed via approved pipeline (no manual hotfixes) to production | DevOps | Deploy log | Yes |
| 3.2.2 | DB migrations applied; migration exit code 0; row-count/integrity deltas within expected bounds | DBA | Migration report | Yes |
| 3.2.3 | Feature flags set to intended release state | Release Mgr | Flag snapshot | Yes |
| 3.2.4 | RAL adapter health endpoint returns healthy against OpenMRS backend | SRE | Health check | Yes |
| 3.2.5 | Audit logging confirmed active and writing (HIPAA-like) before traffic enabled | Compliance | Audit log sample | Yes |

### 3.3 Immediate post-deploy gate

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 3.3.1 | §5 Production smoke suite GREEN (all P1 critical paths) | QA on-call | Smoke report | Yes |
| 3.3.2 | Error rate, latency, CPU/memory within thresholds for 30 min | SRE | Dashboards | Yes |
| 3.3.3 | No PHI leakage in new error/log stream (spot scan) | Sec QA | Log scan | Yes |
| 3.3.4 | No spike in auth failures / 5xx after traffic enabled | SRE | Dashboards | Yes |

### 3.4 Go-live sign-off

| Role | Decision (Go-Live / Roll-back) | Time |
|---|---|---|
| Release Manager | | |
| QA on-call | | |
| SRE on-call | | |

> **Exit rule:** §3.1–§3.3 blocking `[x]` AND unanimous **Go-Live**. Any blocking
> FAIL → execute §7 Rollback immediately.

---

## 4. Post-Release Checklist (T+0 → T+3d)

**Gate owner:** QA Lead + SRE · **Exit:** Stabilization Sign-off (§4.3)

### 4.1 Stabilization monitoring (first 24–72h)

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 4.1.1 | Real-time error/latency/throughput within NFR for 24h; no sustained regression | SRE | Dashboards | Yes |
| 4.1.2 | Patient-safety modules (CLIN/VITAL/ORDLAB/PHARM) show no anomaly in prod telemetry | QA Lead | Telemetry review | Yes |
| 4.1.3 | FHIR/HL7 integration traffic flowing; no rise in rejected messages / failed ACKs | Integration QA | Interface monitor | Yes |
| 4.1.4 | Audit trail completeness verified for sample of prod transactions | Compliance | Audit sample | Yes |
| 4.1.5 | No PHI exposure incident reported or detected | Sec QA | Incident log | Yes |

### 4.2 Verification & closeout

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 4.2.1 | Post-deploy regression sanity (top-risk `REQ` paths) re-run in prod-safe mode | QA | Sanity report | Yes |
| 4.2.2 | All release tickets transitioned to Done; PR ↔ Jira links complete | Release Mgr | Jira board | Advisory |
| 4.2.3 | RTM updated to reflect executed coverage for the release | QA Lead | RTM diff | Advisory |
| 4.2.4 | New prod defects triaged; hotfix vs next-release decided | QA Lead | Triage log | Yes |
| 4.2.5 | Release notes published; KNOWN_ISSUES.md updated | Tech Writer | Published notes | Advisory |
| 4.2.6 | Retrospective scheduled; metrics (escaped defects, MTTR, gate failures) captured | QA Lead | Retro doc | Advisory |

### 4.3 Stabilization sign-off

| Role | Decision (Stable / Watch / Roll-back) | Date |
|---|---|---|
| QA Lead | | |
| SRE Lead | | |
| Product Owner | | |

---

## 5. Smoke Verification Checklist (every deploy, all environments)

> Run on **dev → staging → prod** after each deploy. Target runtime ≤ 20 min,
> all manual or automated P1 critical paths. Any FAIL blocks promotion.

| # | Path | Module / REQ | Expected | Blocking? |
|---|---|---|---|---|
| 5.1 | App loads; login as clinician succeeds; session established | AUTH / `REQ-AUTH-*` | Dashboard renders, no console errors | Yes |
| 5.2 | Invalid login rejected; lockout / error correct (no PHI) | AUTH/SEC | Access denied, generic error | Yes |
| 5.3 | Register a synthetic patient; identifiers generated | REG / `REQ-REG-001` | Patient created, retrievable | Yes |
| 5.4 | Search patient by name/ID returns correct record | SRCH / `REQ-SRCH-*` | Match returned | Yes |
| 5.5 | Open patient dashboard; demographics + summary render | PDASH | Loads correctly | Yes |
| 5.6 | Start a visit; capture vitals | VISIT/VITAL / `REQ-VITAL-*` | Saved and displayed | Yes |
| 5.7 | Create clinical encounter / note | CLIN | Persisted to record | Yes |
| 5.8 | Book a future appointment | APPT / `REQ-APPT-001` | Booked, slot reserved | Yes |
| 5.9 | Place a lab order; place a medication order | ORDLAB/PHARM | Orders accepted | Yes |
| 5.10 | RBAC negative: low-priv role blocked from admin action | RBAC | 403 / hidden | Yes |
| 5.11 | FHIR `GET /Patient/{id}` returns valid R4 resource | FHIR / `REQ-FHIR-*` | 200, conformant JSON | Yes |
| 5.12 | HL7 ADT message processed; ACK returned | HL7 | ACK^AA | Yes |
| 5.13 | Audit log entry written for a PHI-access action | SEC/Compliance | Entry present, attributable | Yes |
| 5.14 | RAL health check against OpenMRS backend healthy | DATA/RAL | Healthy | Yes |

**Smoke gate:** ≥ 5.1–5.14 blocking all `[x]` → environment promotable.

---

## 6. Data & Migration Checklist (T-2d dry-run, T-0 live)

**Gate owner:** DBA + QA · **Exit:** Migration Verified

### 6.1 Pre-migration

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 6.1.1 | Full DB + filesystem (OpenMRS data dir) backup; checksum recorded | DBA | Backup ID | Yes |
| 6.1.2 | Restore from backup tested on isolated env (backup is usable) | DBA | Restore log | Yes |
| 6.1.3 | Migration scripts peer-reviewed; idempotent & re-runnable | DBA | Review record | Yes |
| 6.1.4 | Dry-run on prod-data **copy**; runtime measured for window planning | DBA | Dry-run log | Yes |
| 6.1.5 | Pre-migration baseline counts captured (patients, encounters, orders, obs) | DBA + QA | Baseline snapshot | Yes |

### 6.2 Post-migration verification

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 6.2.1 | Migration completed exit-0; no partial state | DBA | Migration log | Yes |
| 6.2.2 | Row counts reconcile vs baseline (or deltas explained) | DBA + QA | Reconciliation | Yes |
| 6.2.3 | Referential integrity / FK constraints intact (no orphans) | DBA | Integrity check | Yes |
| 6.2.4 | Clinical data spot-check: 10 random patients — demographics, vitals, orders, allergies intact | QA | Spot-check sheet | Yes |
| 6.2.5 | FHIR/HL7 mappings still resolve post-migration (code systems, identifiers) | FHIR/Integration QA | Mapping check | Yes |
| 6.2.6 | No PHI corruption, truncation, or cross-patient leakage | Sec QA + QA | Validation report | Yes |
| 6.2.7 | Audit/history tables preserved (immutable trail not broken) | Compliance | Audit verify | Yes |
| 6.2.8 | Schema version recorded; rollback (down-migration) confirmed available | DBA | Version + down script | Yes |

> **Patient-safety rule:** any data discrepancy in CLIN/VITAL/ORDLAB/PHARM that
> cannot be explained is a **blocking Critical** — halt and roll back.

---

## 7. Rollback Readiness Checklist (T-1d rehearsal)

**Gate owner:** Release Manager · **Exit:** Rollback Rehearsed

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 7.1 | Rollback procedure documented step-by-step with commands | Release Mgr | Runbook | Yes |
| 7.2 | RTO and RPO defined and agreed with Product/Clinical | Release Mgr | Targets recorded | Yes |
| 7.3 | Previous known-good artifact (SHA + image tag) retained and deployable | DevOps | Artifact registry | Yes |
| 7.4 | DB down-migration / restore path tested on staging | DBA | Drill log | Yes |
| 7.5 | Rollback rehearsed end-to-end; elapsed time ≤ RTO | Release Mgr | Drill timing | Yes |
| 7.6 | Decision criteria explicit (which §3/§4 FAIL triggers rollback) | Release Mgr | Trigger matrix | Yes |
| 7.7 | Feature-flag kill-switch available for fast partial rollback | Release Mgr | Flag config | Advisory |
| 7.8 | Post-rollback smoke (§5) and data verification (§6.2) plan ready | QA | Plan link | Yes |
| 7.9 | Stakeholder + clinical comms template for rollback prepared | Release Mgr | Template | Advisory |

> **Rollback triggers (any one):** open patient-safety defect in prod · PHI
> exposure · audit logging down · sustained NFR breach (>30 min) · failed/partial
> migration · smoke FAIL on P1 path.

---

## 8. Compliance & Audit Sign-off Checklist (T-1d → T-0)

**Gate owner:** Compliance Officer / QA · **Exit:** Compliance Sign-off

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 8.1 | Audit logging active for all PHI create/read/update/delete; entries attributable (who/what/when) | Compliance | Audit config + sample | Yes |
| 8.2 | Audit trail is tamper-evident / append-only; retention policy met | Compliance | Policy + check | Yes |
| 8.3 | Access controls (RBAC) enforce minimum-necessary; no broken-object-level-auth | Sec QA | RBAC report | Yes |
| 8.4 | PHI minimization in notifications/exports/reports (NOTIF, RPT, BILL) verified | Compliance | Sample review | Yes |
| 8.5 | Data-at-rest and in-transit encryption confirmed (local/owned env config) | SRE | Config attestation | Yes |
| 8.6 | Consent flags honored (e.g., appointment reminders `REQ-APPT-010`) | QA | Consent test run | Yes |
| 8.7 | FHIR R4 / HL7 v2 conformance evidence archived for the release | FHIR/Integration QA | Conformance logs | Yes |
| 8.8 | WCAG 2.1 AA accessibility conformance statement updated for changed screens | A11Y QA | A11Y report | Yes |
| 8.9 | Security testing performed on owned/local env only (no public-host scans) — attested | Sec Lead | Scope attestation | Yes |
| 8.10 | Risk-acceptance log signed for any waived High/Medium issues | Release Mgr | Signed log | Yes |
| 8.11 | Regulatory/standards traceability (`REQ` ↔ standard ↔ test ↔ evidence) complete | Compliance | Traceability pack | Yes |

### 8.12 Compliance sign-off

| Role | Decision (Approved / Rejected) | Date |
|---|---|---|
| Compliance Officer | | |
| Security Lead | | |
| QA Lead | | |

---

## 9. Documentation & Versioning Checklist (T-2d)

**Gate owner:** Tech Writer + QA · **Exit:** Docs Frozen

| # | Item | Owner | Evidence | Blocking? |
|---|---|---|---|---|
| 9.1 | Semantic version assigned (MAJOR.MINOR.PATCH); rationale recorded | Release Mgr | Version note | Yes |
| 9.2 | Release notes drafted: features, fixes, known issues, breaking changes | Tech Writer | Draft notes | Yes |
| 9.3 | `KNOWN_ISSUES.md` updated with open High/Medium + workarounds | QA | Updated file | Yes |
| 9.4 | API/FHIR contract changes documented; [API_VERSIONING_POLICY](../API_VERSIONING_POLICY.md) followed (no silent breaking change) | API QA | Versioning diff | Yes |
| 9.5 | RAL contract version bumped if adapter behavior changed; [COMPATIBILITY_MATRIX](../COMPATIBILITY_MATRIX.md) updated | QA | Matrix diff | Yes |
| 9.6 | `PROJECT_METADATA.md` updated (build SHA, OpenMRS pin, RAL version) | DevOps | Updated file | Yes |
| 9.7 | RTM and requirements catalog reflect any `REQ` changes in this release | QA Lead | RTM/catalog diff | Yes |
| 9.8 | Migration / upgrade notes ([MIGRATION_NOTES](../MIGRATION_NOTES.md), [UPGRADE_GUIDE](../UPGRADE_GUIDE.md)) current | Tech Writer | Updated files | Yes |
| 9.9 | Operator runbooks (deploy, rollback, smoke) committed and dated | SRE | Runbooks | Yes |
| 9.10 | Docs link-check (Gate 11) clean; no broken internal references | DevOps | lychee report | Advisory |
| 9.11 | Git tag + GitHub release created on frozen SHA matching the version | DevOps | Tag/release URL | Yes |
| 9.12 | CHANGELOG updated and consistent with release notes | Tech Writer | CHANGELOG diff | Yes |

---

## 10. Release Record (one per release)

| Field | Value |
|---|---|
| Release version | |
| Frozen commit SHA | |
| OpenMRS RefApp pin / RAL version | |
| Pre-release gate (§2) | PASS / FAIL |
| Go-live gate (§3) | PASS / FAIL |
| Stabilization gate (§4) | PASS / FAIL |
| Compliance sign-off (§8) | Approved / Rejected |
| Rollback executed? | No / Yes (reason) |
| Open Critical at ship | 0 (required) |
| Escaped defects (T+30d) | |
| Evidence bundle location | |

> **Definition of "Released":** §2, §3, §4, §6, §7, §8, §9 gates all PASS with
> signed evidence, **0 open Critical / patient-safety / PHI defects**, and a tagged
> artifact reproducible from the recorded SHA.

---

### Cross-references

- [Release Test Plan](./RELEASE_TEST_PLAN.md) · [Master Test Plan](./MASTER_TEST_PLAN.md) · [Quality Gates](../QUALITY_GATES.md)
- [Risk Register](../reverse-engineering/RISK_REGISTER.md) · [RISK_ANALYSIS.md](../RISK_ANALYSIS.md) · [NFR](../reverse-engineering/NFR.md)
- [RTM.csv](../../manual-testing/rtm/RTM.csv) · [requirements-catalog.md](../requirements/requirements-catalog.md)
- [FHIR_MAPPING](../reverse-engineering/FHIR_MAPPING.md) · [HL7_MAPPING](../reverse-engineering/HL7_MAPPING.md) · [RBAC_MATRIX](../reverse-engineering/RBAC_MATRIX.md)
- [DEFINITION_OF_DONE.md](../DEFINITION_OF_DONE.md) · [DEPLOYMENT_GUIDE.md](../DEPLOYMENT_GUIDE.md) · [SECURITY_TESTING_GUIDE.md](../SECURITY_TESTING_GUIDE.md)
