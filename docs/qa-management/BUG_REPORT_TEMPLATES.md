# Bug Report Templates — OpenMRS-Primary Healthcare QA Portfolio

> Document ID: **QA-BUG-001**. Version 1.0. Generated 2026-07-01. Status: **Baselined for review**.
> System Under Test (SUT): **OpenMRS** (reference: https://o2.openmrs.org), portable to **OpenEMR, HAPI FHIR, SMART Health IT, omiiCARE** via the **Resource Adapter Layer (RAL)**.

This document standardizes how defects are **captured, classified, triaged, and traced** across the portfolio. Every defect rolls up to a `REQ-<PREFIX>-NNN` requirement via the [RTM](../../manual-testing/rtm/RTM.csv) and links back to the failing `TC-<PREFIX>-NNNN` case in `manual-testing/test-cases/openmrs/`. Templates below are copy-paste ready; each ends with a **filled example** drawn from a real module.

**Referenced artifacts:** [Master Test Plan](./MASTER_TEST_PLAN.md) · [Master Test Strategy](./MASTER_TEST_STRATEGY.md) · [Risk-Based Testing Strategy](./RISK_BASED_TESTING_STRATEGY.md) · [QA Metrics](./QA_METRICS.md) · [Requirements Catalog](../requirements/requirements-catalog.md) · [Risk Register](../reverse-engineering/RISK_REGISTER.md) · [NFR](../reverse-engineering/NFR.md) · [FHIR mapping](../reverse-engineering/FHIR_MAPPING.md) · [HL7 mapping](../reverse-engineering/HL7_MAPPING.md).

---

## 1. Governing Principles

| # | Principle | Implication for a bug report |
|---|---|---|
| 1 | **Patient safety is first-class.** | Any defect that could cause wrong/missing clinical data, mis-dose, mis-ID, or delayed care is auto-escalated to the **Patient-Safety Incident** template (§5) regardless of frequency. |
| 2 | **PHI never enters a defect record.** | Steps, screenshots, payloads, and logs are **de-identified**. Use synthetic IDs (`PT-SYN-0001`), never real names/MRNs/DOBs/clinical values. Redaction is mandatory before attachment. |
| 3 | **Traceability is mandatory.** | Every defect cites `REQ-<PREFIX>-NNN`, the failing `TC-<PREFIX>-NNNN`, the **module prefix**, and the **Source System** (OpenMRS / OpenEMR / HAPI / SMART / omiiCARE / RAL). |
| 4 | **Owned environments only for PERF/SEC.** | Performance and security defects are reproduced **only** on owned/local environments (RefApp sandbox, dockerized HAPI, local omiiCARE). `o2.openmrs.org` is never load- or attack-tested. |
| 5 | **Reproducibility before opinion.** | A defect without deterministic steps + observed-vs-expected + evidence is a *triage note*, not a defect. Intermittent issues record a frequency ratio (e.g., 3/10). |
| 6 | **One defect, one root cause.** | Split multi-symptom reports. Link siblings via `Relates-To`. |

---

## 2. Severity & Priority Scales (shared by all templates)

**Severity** = technical/clinical impact. **Priority** = fix urgency. They are set independently; a low-frequency defect can be S1/P1 on patient-safety grounds.

| Severity | Definition (healthcare-weighted) | Example |
|---|---|---|
| **S1 – Critical** | Patient-safety risk, PHI exposure, data loss/corruption, total outage, or security breach. | Allergy dropped on transfer; wrong patient banner; auth bypass. |
| **S2 – High** | Core workflow blocked, no safe workaround; standards non-conformance (FHIR R4 / HL7 v2 / WCAG 2.1 AA P1). | Cannot save vitals; FHIR `Patient` rejects valid R4 payload. |
| **S3 – Medium** | Workflow degraded, workaround exists. | Search pagination resets filter; non-blocking validation gap. |
| **S4 – Low** | Cosmetic, copy, minor UX. | Label truncation; tooltip typo. |

| Priority | Fix SLA (links to [QA Metrics](./QA_METRICS.md) M-09 MTTR) | Trigger |
|---|---|---|
| **P1** | ≤ 24h | All S1; any safety/PHI/security; release-gate blocker. |
| **P2** | ≤ 3 business days | S2 with no workaround; standards conformance. |
| **P3** | ≤ current sprint | S3. |
| **P4** | Backlog / next release | S4. |

**Defect state model:** `New → Triaged → Assigned → In-Progress → Fixed → In-Verification → Closed` · side states `Rejected`, `Duplicate`, `Deferred`, `Cannot-Reproduce`, `Reopened`. State transitions mirror the lifecycle in [Master Test Strategy](./MASTER_TEST_STRATEGY.md).

---

## 3. Common Header Block (every template inherits this)

| Field | Required | Notes |
|---|---|---|
| Bug ID | auto | `BUG-<PREFIX>-NNNN` (prefix = failing module). |
| Title | yes | `[Module] Concise observable problem (≤ 90 chars)`. |
| Module / Prefix | yes | One of: AUTH REG SRCH PDASH VISIT VITAL CLIN APPT ORDLAB PHARM RBAC DATA RPT FHIR HL7 SEC A11Y PERF NOTIF BILL TELE. |
| Source System | yes | OpenMRS (primary) / OpenEMR / HAPI FHIR / SMART / omiiCARE / RAL. |
| Requirement ID(s) | yes | `REQ-<PREFIX>-NNN`. |
| Test Case ID(s) | yes | `TC-<PREFIX>-NNNN` that detected it. |
| Severity / Priority | yes | From §2. |
| Environment | yes | Build/version, browser/OS, env name (RefApp-sandbox / hapi-local / omiicare-dev). |
| Reporter / Date | yes | Tester + ISO date. |
| Frequency | yes | Always / Intermittent (n/N) / Once. |
| PHI Redaction Confirmed | yes | ☑ — attachments de-identified per §1.2. |
| Patient-Safety Flag | yes | Yes/No — Yes routes to §5 as well. |

---

## 4. Standard Bug Template

**Fields**

| Field | Description |
|---|---|
| (Header block §3) | All header fields. |
| Preconditions | Account/role, data state, feature flags. |
| Steps to Reproduce | Numbered, deterministic, one action per line. |
| Test Data | Synthetic IDs only. |
| Expected Result | Cite the requirement/spec clause. |
| Actual Result | Observed behavior, verbatim error text. |
| Evidence | Screenshot/video/log refs (redacted). |
| Root-Cause Hypothesis | Optional tester note. |
| Workaround | Exists? Describe, or "None". |
| Regression? | New / Pre-existing / Reopened (link prior Bug ID). |
| Relates-To / Blocks | Linked defects, stories. |

**Filled example**

| Field | Value |
|---|---|
| Bug ID | BUG-VITAL-0042 |
| Title | [VITAL] Vitals form discards pulse value when BP fields fail validation |
| Module / Prefix | VITAL |
| Source System | OpenMRS |
| Requirement ID(s) | REQ-VITAL-014 |
| Test Case ID(s) | TC-VITAL-0117 |
| Severity / Priority | S2 / P2 |
| Environment | RefApp 2.13.0 (sandbox), Chrome 126 / Win11, env=RefApp-sandbox |
| Reporter / Date | A. Nayar / 2026-06-28 |
| Frequency | Always |
| PHI Redaction Confirmed | ☑ |
| Patient-Safety Flag | No (data-loss UX; no incorrect value persisted) |
| Preconditions | Logged in as Clinician; active visit open for PT-SYN-0007. |
| Steps | 1. Open Capture Vitals. 2. Enter Pulse=72. 3. Enter Systolic=300 (out of range). 4. Click Save. |
| Test Data | Pulse=72; Systolic=300; Diastolic=80; Patient=PT-SYN-0007. |
| Expected | Form blocks save, shows BP range error, **retains** Pulse=72 (REQ-VITAL-014: partial input preserved on validation failure). |
| Actual | Save blocked correctly, but on error re-render all fields cleared; Pulse re-entry required. |
| Evidence | vid_VITAL0042_redacted.mp4; console clean. |
| Workaround | Re-enter all values. |
| Regression? | New. |
| Relates-To | Story PORT-318; Relates-To BUG-VITAL-0039. |

---

## 5. Patient-Safety Incident Template

> **Highest-priority template. Auto S1/P1.** Used when a defect could plausibly cause clinical harm: wrong-patient association, dropped allergy/problem/med, mis-dose, mis-route, unit/decimal error, stale or missing data shown to a clinician, or care delay. Notify the QA Lead + Clinical Safety Officer within **1 hour**. Maps to [Risk Register](../reverse-engineering/RISK_REGISTER.md) hazards.

**Fields (extends §3)**

| Field | Description |
|---|---|
| Hazard Category | Wrong-patient / Wrong-data / Missing-data / Wrong-dose / Delay / Decision-support failure. |
| Clinical Scenario | Realistic care context where harm could occur. |
| Severity of Harm (potential) | Catastrophic / Major / Moderate / Minor (per risk matrix). |
| Likelihood | Frequent / Probable / Occasional / Remote. |
| Risk Score | Harm × Likelihood → Risk Register ID. |
| Affected Data Element(s) | e.g., AllergyIntolerance, MedicationRequest.dose, Patient identity. |
| Detection Point | Where in workflow the unsafe state surfaces. |
| Contained? | Was unsafe data persisted / surfaced to a clinician? |
| Immediate Mitigation | Guardrail, flag-off, banner, or block deployed. |
| Linked Hazard (Risk Register) | RISK-NNN. |
| Regulatory/Compliance note | HIPAA-like exposure? Reportable internally? |

**Filled example**

| Field | Value |
|---|---|
| Bug ID | BUG-CLIN-0008 |
| Title | [CLIN] Allergy list not propagated to new encounter after patient merge |
| Module / Prefix | CLIN |
| Source System | OpenMRS → RAL → omiiCARE |
| Requirement ID(s) | REQ-CLIN-022, REQ-DATA-011 |
| Test Case ID(s) | TC-CLIN-0045, TC-DATA-0061 |
| Severity / Priority | S1 / P1 |
| Environment | RefApp 2.13.0 (sandbox) + omiicare-dev via RAL; env=owned |
| Reporter / Date | R. Khanna / 2026-06-29 |
| Frequency | Intermittent (2/5, when surviving record has 0 allergies) |
| PHI Redaction Confirmed | ☑ |
| Patient-Safety Flag | **Yes** |
| Hazard Category | Missing-data (active allergy not shown). |
| Clinical Scenario | Two duplicate records merged; surviving record had no allergy on file, dropped record carried "Penicillin – anaphylaxis". Post-merge encounter shows no allergies; clinician could order a penicillin-class drug. |
| Severity of Harm | **Catastrophic** (potential anaphylaxis). |
| Likelihood | Occasional. |
| Risk Score | High → RISK-014. |
| Affected Data Element(s) | AllergyIntolerance.code, .criticality, .reaction. |
| Detection Point | First encounter created after merge; ORDLAB/PHARM order screen. |
| Contained? | Not surfaced to a real clinician (synthetic data, owned env). |
| Immediate Mitigation | Merge feature flag disabled in dev; allergy-reconciliation gate proposed pre-merge. |
| Linked Hazard | RISK-014 (allergy loss on identity operations). |
| Regulatory note | No PHI exposure; internally reportable as Class-A safety defect. |

---

## 6. Security Bug Template

> Reproduced **only on owned/local environments** (§1.4). Never run against `o2.openmrs.org`. Classify per **OWASP Top 10**; assess PHI/HIPAA-like exposure. Restrict visibility to the security group; do not paste live tokens, payloads with PHI, or exploit chains in plain text — reference a secured evidence vault.

**Fields (extends §3)**

| Field | Description |
|---|---|
| OWASP Category | A01–A10 (e.g., A01 Broken Access Control, A07 Auth Failures). |
| CWE ID | e.g., CWE-639, CWE-79. |
| Attack Vector | Authn/Authz / Injection / XSS / IDOR / SSRF / Misconfig. |
| Auth Context | Role/token used; cross-tenant? |
| PHI Exposure | None / Potential / Confirmed — record count only, never values. |
| CVSS (est.) | Base score + vector string. |
| Proof of Concept | Redacted, vaulted reference. |
| Affected Endpoint/Surface | API path / UI route. |
| Remediation Recommendation | Control to add. |
| Disclosure Restriction | Security-group only. |

**Filled example**

| Field | Value |
|---|---|
| Bug ID | BUG-SEC-0019 |
| Title | [SEC] IDOR: clinician can read another patient's encounters via REST id enumeration |
| Module / Prefix | SEC (surface: CLIN REST) |
| Source System | OpenMRS (owned RefApp sandbox) |
| Requirement ID(s) | REQ-SEC-007, REQ-RBAC-004 |
| Test Case ID(s) | TC-SEC-0033 |
| Severity / Priority | S1 / P1 |
| Environment | RefApp 2.13.0, env=RefApp-sandbox (owned), no public targets |
| Reporter / Date | M. Iqbal / 2026-06-27 |
| Frequency | Always |
| PHI Redaction Confirmed | ☑ (counts only) |
| Patient-Safety Flag | No (confidentiality, not safety) |
| OWASP Category | A01 Broken Access Control. |
| CWE ID | CWE-639 (Authorization Bypass Through User-Controlled Key). |
| Attack Vector | IDOR via direct object reference. |
| Auth Context | Authenticated Clinician with no care-relationship to target. |
| PHI Exposure | Potential — 1 synthetic record's encounters returned; 0 real PHI. |
| CVSS (est.) | 7.7 High · AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:N/A:N. |
| PoC | vaulted: vault://sec/BUG-SEC-0019 (request/response redacted). |
| Affected Endpoint | `GET /ws/rest/v1/encounter?patient={uuid}` — UUID not authorization-checked. |
| Remediation | Enforce care-relationship/location scoping in REST authz filter; add RBAC test TC-RBAC-00xx. |
| Disclosure Restriction | Security group only until patched. |

---

## 7. API / FHIR Defect Template

> For REST and **FHIR R4** / **HL7 v2** conformance defects routed through the **RAL**. Capture request/response (redacted), spec clause, and the exact field path. Cite [FHIR mapping](../reverse-engineering/FHIR_MAPPING.md) / [HL7 mapping](../reverse-engineering/HL7_MAPPING.md).

**Fields (extends §3)**

| Field | Description |
|---|---|
| Interface Type | REST / FHIR R4 / HL7 v2 message. |
| Resource / Message | e.g., FHIR `Patient`, `Observation`; HL7 `ADT^A01`, `ORU^R01`. |
| Operation | GET/POST/PUT/search; HL7 trigger event. |
| Spec Clause | FHIR R4 element + cardinality; HL7 segment/field (e.g., PID-3). |
| Field Path | JSON path / segment-field (e.g., `Observation.valueQuantity.unit`, `OBX-5`). |
| Request (redacted) | Method + path + body. |
| Response / Message (redacted) | Status + body / ACK code. |
| Conformance Gap | Expected per spec vs actual. |
| Terminology/Code System | URI correctness (LOINC/SNOMED/RxNorm/UCUM). |
| Adapter Layer? | Bug in RAL mapping vs upstream SUT. |
| Backward-Compat Impact | Breaks existing consumers? |

**Filled example**

| Field | Value |
|---|---|
| Bug ID | BUG-FHIR-0027 |
| Title | [FHIR] Observation.valueQuantity emits blank UCUM unit code for blood pressure |
| Module / Prefix | FHIR (source: VITAL) |
| Source System | HAPI FHIR (hapi-local) via RAL ← OpenMRS |
| Requirement ID(s) | REQ-FHIR-019, REQ-VITAL-014 |
| Test Case ID(s) | TC-FHIR-0052 |
| Severity / Priority | S2 / P2 |
| Environment | HAPI 7.2 docker (hapi-local, owned), RAL build 0.9.4 |
| Reporter / Date | S. Patel / 2026-06-26 |
| Frequency | Always |
| PHI Redaction Confirmed | ☑ |
| Patient-Safety Flag | No (interop conformance; value present, unit code missing) |
| Interface Type | FHIR R4. |
| Resource / Message | `Observation` (vital-signs profile). |
| Operation | `GET /fhir/Observation/{id}`. |
| Spec Clause | R4 `Observation.valueQuantity.system` SHALL be `http://unitsofmeasure.org`; `.code` SHALL be UCUM. |
| Field Path | `valueQuantity.code` (empty), `valueQuantity.unit`="mmHg". |
| Request | `GET /fhir/Observation/OBS-SYN-9001` (Accept: application/fhir+json). |
| Response | 200; body has `"unit":"mmHg"`, `"system":"http://unitsofmeasure.org"`, `"code":""`. |
| Conformance Gap | `.code` must be `mm[Hg]` (UCUM); blank code fails US-Core validation and downstream unit-aware logic. |
| Terminology | UCUM URI correct; code value blank — RAL mapping omits UCUM code. |
| Adapter Layer? | **Yes** — RAL unit-mapping table missing UCUM entry; OpenMRS concept is correct. |
| Backward-Compat Impact | SMART apps relying on `.code` break; validator rejects bundle. |

---

## 8. Accessibility Defect Template

> Conformance to **WCAG 2.1 AA**. Capture the **Success Criterion**, conformance level, and assistive-tech context. P1 maps to keyboard/focus/contrast/name-role-value SCs per the A11Y suite.

**Fields (extends §3)**

| Field | Description |
|---|---|
| WCAG SC | e.g., 2.1.1 Keyboard, 1.4.3 Contrast, 4.1.2 Name/Role/Value. |
| Conformance Level | A / AA. |
| Disability Impact | Vision / Motor / Cognitive / Hearing. |
| Assistive Tech | NVDA/JAWS/VoiceOver + browser; keyboard-only. |
| Element / Component | Control, landmark, dialog. |
| Expected (SC text) | What the SC requires. |
| Actual | Observed AT behavior. |
| Automated Tool Ref | axe/Lighthouse rule id (if any). |
| Manual Confirmation | Tester + AT used. |
| Remediation | ARIA/markup/contrast fix. |

**Filled example**

| Field | Value |
|---|---|
| Bug ID | BUG-A11Y-0014 |
| Title | [A11Y] Appointment cancel modal traps keyboard focus; Esc and tab cycle fail |
| Module / Prefix | A11Y (surface: APPT) |
| Source System | OpenMRS |
| Requirement ID(s) | REQ-A11Y-002 |
| Test Case ID(s) | TC-A11Y-0003, TC-A11Y-0004 |
| Severity / Priority | S2 / P1 (P1 per WCAG AA keyboard criticality) |
| Environment | RefApp 2.13.0, Chrome 126 + NVDA 2024.2 / Win11 |
| Reporter / Date | L. Gomez / 2026-06-28 |
| Frequency | Always |
| PHI Redaction Confirmed | ☑ |
| Patient-Safety Flag | No |
| WCAG SC | 2.1.2 No Keyboard Trap; 2.4.3 Focus Order. |
| Conformance Level | A / AA. |
| Disability Impact | Motor + Vision (keyboard + screen-reader users). |
| Assistive Tech | NVDA 2024.2, keyboard-only. |
| Element | "Cancel appointment" confirmation dialog. |
| Expected | Focus contained in modal; Esc closes; on close focus returns to trigger (REQ-A11Y-002). |
| Actual | Tab cycles into page behind modal; Esc does nothing; focus lost after close. |
| Automated Tool Ref | axe `aria-dialog-name` passes; trap not auto-detectable → manual. |
| Manual Confirmation | L. Gomez via NVDA. |
| Remediation | Add focus trap, `aria-modal="true"`, Esc handler, restore focus to invoker. |

---

## 9. Performance Defect Template

> Reproduced **only on owned/local environments** (§1.4). Tie every number to an [NFR](../reverse-engineering/NFR.md) target. Record percentiles, load profile, and resource saturation — not single readings.

**Fields (extends §3)**

| Field | Description |
|---|---|
| NFR Target | The breached NFR (e.g., search p95 ≤ 2s @ 50 vusers). |
| Operation Under Test | Endpoint/transaction. |
| Load Profile | vusers, ramp, duration, think-time. |
| Observed Metric | p50/p95/p99, throughput, error %. |
| Breach Delta | Observed vs target. |
| Resource Saturation | CPU/mem/DB/connection-pool/GC. |
| Bottleneck Hypothesis | Query, N+1, lock, GC, index. |
| Environment Spec | Owned host sizing (so results are reproducible). |
| Test Tool / Script | k6/JMeter script ref. |
| Trend | First seen / regressed since build. |

**Filled example**

| Field | Value |
|---|---|
| Bug ID | BUG-PERF-0011 |
| Title | [PERF] Patient search p95 latency 4.8s exceeds 2s NFR at 50 concurrent users |
| Module / Prefix | PERF (surface: SRCH) |
| Source System | OpenMRS (RefApp-sandbox, owned) |
| Requirement ID(s) | REQ-PERF-003, REQ-SRCH-008 |
| Test Case ID(s) | TC-PERF-0007 |
| Severity / Priority | S2 / P2 |
| Environment | env=RefApp-sandbox (owned), 4 vCPU/8GB, MySQL 8 local; **public site never tested** |
| Reporter / Date | D. Roy / 2026-06-25 |
| Frequency | Always (3/3 runs) |
| PHI Redaction Confirmed | ☑ (synthetic dataset 50k patients) |
| Patient-Safety Flag | No (degraded care speed; monitored) |
| NFR Target | Name search p95 ≤ 2.0s @ 50 vusers (NFR-PERF-003). |
| Operation | `GET /ws/rest/v1/patient?q=` (partial name). |
| Load Profile | 50 vusers, 30s ramp, 10 min, 1s think-time. |
| Observed Metric | p50 1.6s, **p95 4.8s**, p99 7.1s, throughput 38 rps, errors 0%. |
| Breach Delta | p95 +2.8s (+140%) over target. |
| Resource Saturation | DB CPU 95%; one full-table scan; pool exhausted at 48 conns. |
| Bottleneck Hypothesis | Missing index on `person_name.given_name`; LIKE '%term%' non-sargable. |
| Environment Spec | 4 vCPU/8GB; documented for repro. |
| Test Tool | k6 script `k6/search_load.js`. |
| Trend | Regressed since build 2.13.0 (was p95 1.9s in 2.12.x). |

---

## 10. Defect Field → Module Mapping (quick reference)

| Template | Primary prefixes | Key standards lens |
|---|---|---|
| Standard (§4) | REG SRCH PDASH VISIT VITAL CLIN APPT ORDLAB PHARM RPT NOTIF BILL TELE | Functional/Negative/Boundary |
| Patient-Safety (§5) | CLIN PHARM ORDLAB VITAL REG DATA | Risk Register hazard linkage |
| Security (§6) | SEC AUTH RBAC | OWASP Top 10, HIPAA-like, CVSS |
| API/FHIR (§7) | FHIR HL7 (sources: VITAL CLIN REG ORDLAB) | FHIR R4, HL7 v2, UCUM/LOINC/SNOMED/RxNorm |
| Accessibility (§8) | A11Y (all UI surfaces) | WCAG 2.1 AA |
| Performance (§9) | PERF (all surfaces) | NFR targets, owned-env only |

---

## 11. Definition of Done for a Bug Report

A report is **Triage-Ready** only when all hold:

- [ ] Title is observable and scoped to one root cause.
- [ ] `REQ-<PREFIX>-NNN` and `TC-<PREFIX>-NNNN` cited; Source System named.
- [ ] Deterministic steps + Expected (with spec clause) + Actual + Evidence.
- [ ] Severity/Priority set per §2; Patient-Safety flag evaluated.
- [ ] **PHI Redaction Confirmed** — synthetic IDs only, attachments de-identified.
- [ ] PERF/SEC reproduced on **owned environments only**.
- [ ] Correct specialized template used (§5–§9) where applicable.
- [ ] Linked to RTM entry and any sibling/blocking defects.

> Reports failing any box are returned as **Triage Notes**, not logged defects, per [Master Test Strategy](./MASTER_TEST_STRATEGY.md).
