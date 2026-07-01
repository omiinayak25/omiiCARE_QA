# QA Knowledge Base — OpenMRS-Primary Healthcare QA Portfolio

> **The team's single source of truth for "how do I…?"** This is the operational
> knowledge base (KB) for the enterprise healthcare QA portfolio. Where the
> [Master Test Strategy](MASTER_TEST_STRATEGY.md) declares *why* and *what*, this
> KB tells an engineer *how* — runbooks, a healthcare-domain primer, glossary,
> FAQ, troubleshooting, and onboarding.
>
> **SUT (primary):** OpenMRS Reference Application — legacy **O2** (`https://o2.openmrs.org`),
> modern demo **O3** (`https://o3.openmrs.org`).
> **Portability targets:** OpenEMR, HAPI FHIR, SMART Health IT, in-house **omiiCARE** — all
> reached through the **Resource Adapter Layer (RAL)**.
> **Anchored to:** 472 requirements (`REQ-<PREFIX>-NNN`,
> [`requirements-catalog.md`](../requirements/requirements-catalog.md)), 1,349+ manual cases
> ([`manual-testing/test-cases/openmrs/`](../../manual-testing/test-cases/openmrs/)),
> the [RTM](../../manual-testing/rtm/RTM.csv), and the
> [reverse-engineering specs](../reverse-engineering/).
> **Standards:** FHIR R4 (4.0.1), HL7 v2.x, WCAG 2.1 AA, OWASP ASVS / Top 10, HIPAA-like.

- **Document ID:** QAM-KB-001 · **Owner:** Principal QA Engineer · **Status:** Living document
- **Date:** 2026-07-01 · **Review cadence:** per sprint (additive); per release (full pass)
- **Convention:** Statements beyond verified OpenMRS behavior are tagged **(Assumption)**.

> **Ethics / scope guard (non-negotiable).** Performance and security tests run **ONLY**
> against owned or local environments (local OpenMRS SDK build, owned omiiCARE, self-hosted
> HAPI). They are **never** run against public demo sites (`o2/o3.openmrs.org`) or any
> third-party host. **Zero real PHI** in any test asset, log, screenshot, or fixture — ever.

---

## Table of Contents

1. [How to Use This KB](#1-how-to-use-this-kb)
2. [Runbook: Set Up Your Test Environment](#2-runbook-set-up-your-test-environment)
3. [Runbook: Run the Manual Suite](#3-runbook-run-the-manual-suite)
4. [Runbook: Run Automation](#4-runbook-run-automation)
5. [Runbook: Report a Defect](#5-runbook-report-a-defect)
6. [Runbook: Triage](#6-runbook-triage)
7. [Healthcare Domain Primer (FHIR / HL7 / Coding)](#7-healthcare-domain-primer-fhir--hl7--coding)
8. [Glossary](#8-glossary)
9. [FAQ](#9-faq)
10. [Troubleshooting](#10-troubleshooting)
11. [Onboarding — Your First Two Weeks](#11-onboarding--your-first-two-weeks)
12. [Quick Reference Card](#12-quick-reference-card)

---

## 1. How to Use This KB

| If you need to… | Go to | Companion doc |
|---|---|---|
| Stand up an environment | §2 | [ARCHITECTURE](../reverse-engineering/ARCHITECTURE.md) |
| Execute manual test cases | §3 | [test-cases README](../../manual-testing/test-cases/openmrs/README.md) |
| Run / write automation | §4 | [TEST_PYRAMID](../TEST_PYRAMID.md) (Assumption) |
| Log a bug correctly | §5 | [defect template](#54-defect-report-template) |
| Decide severity & priority | §6 | [RISK_BASED_TESTING_STRATEGY](RISK_BASED_TESTING_STRATEGY.md) |
| Understand a FHIR/HL7 term | §7–§8 | [FHIR_MAPPING](../reverse-engineering/FHIR_MAPPING.md), [HL7_MAPPING](../reverse-engineering/HL7_MAPPING.md) |
| Onboard a new tester | §11 | this doc |

**Golden rules.** (1) Every test traces to a `REQ-<PREFIX>-NNN`. (2) PHI is always
synthetic. (3) Patient-safety and PHI defects outrank cosmetic defects categorically.
(4) Perf/security only on owned environments. (5) When in doubt, the
reverse-engineering specs are the factual baseline; this KB is the procedure.

---

## 2. Runbook: Set Up Your Test Environment

### 2.1 Environment matrix

| Env | Purpose | URL / host | PHI | Perf/Sec allowed? |
|---|---|---|---|---|
| **Public demo (O2/O3)** | Exploratory, manual functional read-only learning | `o2.openmrs.org` / `o3.openmrs.org` | None (shared) | **No** |
| **Local OpenMRS SDK** | Full functional + perf + security + automation | `http://localhost:8080/openmrs` | Synthetic | **Yes** |
| **Self-hosted HAPI FHIR** | FHIR R4 conformance / adapter | `http://localhost:8090/fhir` | Synthetic | **Yes** |
| **omiiCARE (owned)** | In-house RAL target | per ops handbook | Synthetic | **Yes** |
| **OpenEMR / SMART Health IT** | Secondary RAL conformance | local container | Synthetic | **Yes** |

> Public demo data is shared and reset unpredictably; **never** rely on it for
> destructive, perf, or security tests, and never assume your test data persists.

### 2.2 Local OpenMRS via SDK (canonical setup)

1. **Prereqs:** Java 8/11 (per ref-app version), Maven 3.6+, Docker (for MySQL/MariaDB), 8 GB RAM free.
2. Install the SDK: `mvn org.openmrs.maven.plugins:openmrs-sdk-maven-plugin:setup-sdk`.
3. Create a server: `mvn openmrs-sdk:setup -DserverId=qa-local` → choose **Reference Application** distro.
4. Run: `mvn openmrs-sdk:run -DserverId=qa-local` → wait for `Started ... in N seconds`.
5. Open `http://localhost:8080/openmrs`, log in (`admin` / `Admin123` default — **change immediately**).
6. Load the **synthetic seed dataset** (registration fixtures, login locations, roles) per §2.4.
7. Verify health: `/openmrs/ws/fhir2/R4/metadata` returns a `CapabilityStatement` with `fhirVersion: 4.0.1`.

### 2.3 Accounts & roles (RBAC)

Provision the standard QA personas referenced throughout the suite. See
[RBAC_MATRIX](../reverse-engineering/RBAC_MATRIX.md).

| Persona | Role | Used by modules |
|---|---|---|
| `clinician1` | Clinician / Provider | VISIT, VITAL, CLIN, ORDLAB, PHARM |
| `registrar1` | Registration Clerk | REG, SRCH, PDASH, APPT |
| `pharmacist1` | Pharmacist | PHARM, ORDLAB |
| `admin` | System Administrator | RBAC, DATA, RPT, AUTH |
| `lowpriv1` | Minimal-privilege user | RBAC negative / SEC authorization |

### 2.4 Synthetic test data

- Generate via the **Test Data Strategy** (Assumption: `docs/TEST_DATA_STRATEGY.md`) — synthetic
  names, fake MRNs, fictional addresses. Use clearly-fake markers (e.g., surname `ZZTEST`).
- **Never** copy production or real-patient records. **Never** paste real SSNs, NIDs, or insurance IDs.
- Standard coding fixtures: a known LOINC (e.g., `8867-4` heart rate), SNOMED, ICD-10, RxNorm code
  set lives in the data dictionary ([DATA_DICTIONARY](../reverse-engineering/DATA_DICTIONARY.md)).

### 2.5 Environment readiness checklist

- [ ] App reachable; login works for all five personas.
- [ ] At least one **Login Location** (e.g., `Outpatient Clinic`) configured.
- [ ] FHIR R4 metadata returns `4.0.1`.
- [ ] Synthetic seed data loaded; no real PHI present.
- [ ] Audit logging enabled (RPT module depends on it).
- [ ] For perf/sec runs: confirmed env is **owned/local**, not a public demo.

---

## 3. Runbook: Run the Manual Suite

### 3.1 Where the cases live

`manual-testing/test-cases/openmrs/` holds **21 module CSVs** (one per prefix) plus
`ALL_TEST_CASES.csv` (importable into TestRail / Xray / Zephyr). **17 columns**:

`TC_ID | Module | Sub_Module | Title | Test_Type | Test_Level | Priority | Severity | Risk |
Preconditions | Test_Steps | Test_Data | Expected_Result | Automation_Feasibility |
Requirement_ID | Healthcare_Classification | Source_System`

### 3.2 Selecting what to run

| Run type | Selection rule | Typical size |
|---|---|---|
| **Smoke** | `Test_Level = Smoke` | ~40–60 cases |
| **Sanity (per build)** | Smoke + changed-module P1 | varies |
| **Regression** | All P1 + P2 in impacted modules (risk-ranked, see RBT) | hundreds |
| **Full** | Entire suite (1,349 today → ~4,000 target) | all |
| **Patient-safety gate** | CLIN, PHARM, ORDLAB, VITAL Negative + Boundary | mandatory pre-release |

Filter a CSV by column to build a run, e.g. all P1 AUTH cases:
`grep -E '"P1"' manual-testing/test-cases/openmrs/AUTH.csv`.

### 3.3 Execution procedure

1. Pick a build + environment (§2); record build ID.
2. Import the run into your test-management tool (or work the CSV directly).
3. For each case: satisfy **Preconditions** → follow **Test_Steps** (pipe-`|`-separated) →
   apply **Test_Data** → compare actual vs **Expected_Result**.
4. Mark **Pass / Fail / Blocked / Skipped**. Any Fail → file a defect (§5) and link the `TC_ID`.
5. Attach evidence (screenshot/HAR/log) — **scrub any PHI-shaped data first** even if synthetic.
6. On completion, update the [RTM](../../manual-testing/rtm/RTM.csv) execution status so coverage stays honest.

### 3.4 Test-type cheat sheet

| Test_Type | What it proves | Example modules |
|---|---|---|
| Functional | Happy-path behavior | all |
| Negative | Rejects bad input safely | AUTH, REG, PHARM |
| Boundary | Edges (min/max, dates, dose limits) | VITAL, PHARM, BILL |
| Decision-Table | Combinatorial business rules | RBAC, BILL, APPT |
| State-Transition | Lifecycle correctness | VISIT, ORDLAB, TELE |
| Pairwise | Efficient multi-factor coverage | REG, SRCH |
| Exploratory | Charter-based discovery | PDASH, CLIN |
| Accessibility | WCAG 2.1 AA | A11Y |
| Security | OWASP / authz (design-only on public) | SEC, RBAC |
| API / FHIR / HL7 | Contract & interop | FHIR, HL7, ORDLAB |
| Database | Integrity / audit | DATA, RPT |

---

## 4. Runbook: Run Automation

> Automation targets ≥70% of regression-eligible cases by the M5 milestone
> (Assumption — see strategy). The pyramid favors API/contract tests over UI.

### 4.1 Layers

| Layer | Tool (Assumption) | Scope | Speed |
|---|---|---|---|
| Unit / component | per-stack | adapter logic, validators | ms |
| **API / contract** | REST + FHIR validator | OpenMRS REST, FHIR R4, HL7 parse | s |
| UI / E2E | Playwright/Selenium | AUTH, REG, VISIT critical paths | min |
| Adapter-conformance | shared suite | same intent across RAL backends | s–min |
| Accessibility | axe-core | A11Y automated checks | s |

### 4.2 Running automation locally

1. Point the suite at your **local** OpenMRS (`http://localhost:8080/openmrs`) — never a public demo.
2. Configure backend via the RAL profile (`openmrs` | `hapi` | `omiicare` | `openemr`).
3. Run smoke: e.g. `npm run test:smoke` / `mvn -P smoke verify` (Assumption — per repo tooling).
4. Run full regression in CI on every PR; gate merges on green + coverage thresholds.
5. FHIR conformance: validate responses against the R4 StructureDefinitions and the
   [FHIR_MAPPING](../reverse-engineering/FHIR_MAPPING.md) Must-Support table.

### 4.3 What to automate vs. keep manual

- **Automate:** `Automation_Feasibility = Automatable` and stable, deterministic, high-frequency.
- **Keep manual:** exploratory charters, complex visual A11Y judgment, one-off migrations,
  cases marked `Manual-only` / `Semi-automatable`.

---

## 5. Runbook: Report a Defect

### 5.1 Before you file

- Reproduce **twice**; capture exact build, environment, persona, and steps.
- Check for duplicates (search by module + symptom).
- Identify the failing `TC_ID` and its `REQ-<PREFIX>-NNN`.

### 5.2 Severity (impact) — patient-safety weighted

| Severity | Definition | Healthcare examples |
|---|---|---|
| **Critical** | Patient-safety risk, PHI leak, data corruption, total outage | Wrong med dose accepted; allergy not shown; PHI in URL/log; FHIR `code` mis-mapped |
| **High** | Major function broken, no safe workaround | Cannot place lab order; visit won't save; auth bypass |
| **Medium** | Function impaired, workaround exists | Search slow/partial; non-blocking validation gap |
| **Low** | Cosmetic / minor | Label typo, alignment, copy |

### 5.3 Priority (urgency)

`P1` fix now (blocks release) · `P2` this sprint · `P3` backlog · `P4` opportunistic.
Severity ≠ priority; a Critical patient-safety defect is almost always P1.

### 5.4 Defect report template

```
Title:        [MODULE] concise symptom (not the cause)
Severity:     Critical | High | Medium | Low
Priority:     P1 | P2 | P3 | P4
Module / REQ: e.g. PHARM / REQ-PHARM-014
Failing TC:   TC-PHARM-00NN
Environment:  Local OpenMRS SDK | HAPI | omiiCARE  (Build: ___)
Persona/Role: clinician1 / Clinician
Preconditions: ...
Steps to reproduce: 1) ... 2) ... 3) ...
Expected:     (quote Expected_Result)
Actual:       (what happened)
Evidence:     screenshot / HAR / log  (PHI-scrubbed)
Patient-safety impact: yes/no + rationale
PHI exposure:          yes/no  (if yes → escalate immediately)
Standards impact:      FHIR R4 / HL7 v2 / WCAG / OWASP / HIPAA-like (if any)
```

### 5.5 Non-negotiables

- A defect with **PHI exposure** is escalated to the QA lead and security owner **immediately**,
  regardless of severity label, and evidence is sanitized before attaching.
- Patient-safety defects (CLIN/PHARM/ORDLAB/VITAL) are never silently downgraded.

---

## 6. Runbook: Triage

### 6.1 Triage cadence & roster

Daily 15-min triage (or per-build during release week). Roster: QA lead (facilitator),
dev lead, product owner, plus the security/clinical-safety owner when any Critical or
PHI/patient-safety defect is in the queue.

### 6.2 Triage decision flow

1. **Validate** — reproducible? enough info? If not → return to reporter (`Needs Info`).
2. **Classify** — confirm Severity + Priority per §5; apply the patient-safety weighting.
3. **Route** — assign owner + target build; link `REQ` and `TC_ID`.
4. **Decide** — Fix-now / Fix-this-sprint / Backlog / Won't-fix (needs PO + risk sign-off) /
   Duplicate / Not-a-bug.
5. **Gate check** — does this breach a release exit criterion? (0 Critical, ≤1 High — Assumption per strategy).

### 6.3 Auto-escalation rules

| Trigger | Action |
|---|---|
| PHI exposure | Immediate escalation; security owner engaged same day |
| Patient-safety (wrong dose/allergy/result) | P1; clinical-safety review before close |
| Auth bypass / broken authz (OWASP A01) | P1; SEC retest required |
| FHIR/HL7 contract break | Block interop sign-off until adapter retest green |
| WCAG 2.1 AA P1 (keyboard/contrast/labels) | Block A11Y exit gate |

---

## 7. Healthcare Domain Primer (FHIR / HL7 / Coding)

> Just enough domain to test confidently. Field-level truth lives in
> [FHIR_MAPPING](../reverse-engineering/FHIR_MAPPING.md) and
> [HL7_MAPPING](../reverse-engineering/HL7_MAPPING.md).

### 7.1 The clinical objects you test

| Concept | OpenMRS term | FHIR R4 resource | Why it matters |
|---|---|---|---|
| The patient | Patient | `Patient` | Identity, MRN, demographics — PHI-dense |
| A clinical contact | Visit / Encounter | `Encounter` | Anchors observations/orders |
| A measured value | Obs | `Observation` | Vitals, labs (LOINC-coded) |
| A diagnosis/problem | Condition | `Condition` | ICD-10 / SNOMED coded |
| An allergy | Allergy | `AllergyIntolerance` | **Patient-safety critical** |
| A prescription | Drug Order | `MedicationRequest` | Dose/route/frequency — safety critical (RxNorm) |
| A booking | Appointment | `Appointment` | Scheduling lifecycle |
| A clinician | Provider | `Practitioner` | Authorship / RBAC |

### 7.2 FHIR R4 essentials

- **Version:** R4 **4.0.1**; confirm via `CapabilityStatement` at `/openmrs/ws/fhir2/R4/metadata`.
- **Base URL:** OpenMRS `/openmrs/ws/fhir2/R4`; omiiCARE `/api/v1/fhir` (Assumption).
- **All reads/writes require auth** → unauthorized returns **401** (`REQ-FHIR-001`, `REQ-SEC-*`).
- **Bundles** wrap search/batch results; check `total`, `entry[]`, paging `link[rel=next]`.
- **CodeableConcept** carries `coding[].system` + `code` + `display` — the *system URI* must be
  exact (a wrong system = a silent mis-mapping = Critical).
- **Resource references** (`subject`, `encounter`) must resolve and point to the right patient.
- Test angles: schema validity, cardinality/Must-Support, code-system correctness, search params,
  auth/authz, referential integrity.

### 7.3 HL7 v2 essentials

OpenMRS ships no native v2 listener in the default RefApp; maps are anchored to verified
REST/FHIR elements and the HL7 v2.x standard (canonical **2.5.1**, configurable).

| Message | Trigger | What to verify |
|---|---|---|
| `ADT^A01` | Admit | `PID`/`PV1` patient + visit created |
| `ADT^A04` | Register | Registration mapped to OpenMRS patient |
| `ADT^A08` | Update | Demographic update applied, not duplicated |
| `ORM^O01` | Order | `ORC`/`OBR` → order created |
| `ORU^R01` | Result | `OBX` → observation with correct LOINC + value |

Key segments: `MSH` (header/encoding), `EVN` (event), `PID` (patient ID/demographics),
`PV1` (visit), `ORC`/`OBR` (order), `OBX` (result), `MSA`/`ERR` (ack/errors).
Encoding is pipe-and-hat: `|` field sep, `^` component sep. Always test the **ACK/NAK**
path and malformed-message rejection, not just the happy path.

### 7.4 Coding systems (get the `system` URI right)

| System | Used for | URI (canonical) |
|---|---|---|
| **LOINC** | Labs, vitals | `http://loinc.org` |
| **SNOMED CT** | Problems, findings | `http://snomed.info/sct` |
| **ICD-10** | Diagnoses (billing) | `http://hl7.org/fhir/sid/icd-10` |
| **RxNorm** | Medications | `http://www.nlm.nih.gov/research/umls/rxnorm` |
| **CVX** | Immunizations | `http://hl7.org/fhir/sid/cvx` |

A correct `display` with a wrong `code` or wrong `system` is still a defect — verify all three.

### 7.5 PHI & compliance (HIPAA-like)

- **18 HIPAA identifiers** (name, MRN, dates, contact, IDs, biometrics, etc.) must never appear in
  logs, URLs, error messages, analytics, or test evidence.
- Every PHI access must be **audited** (RPT module; `REQ-RPT-*`).
- Enforce **least privilege** (RBAC) and **minimum necessary** disclosure.
- All PHI in transit/at rest must be encrypted; verify on owned environments only.

---

## 8. Glossary

| Term | Meaning |
|---|---|
| **SUT** | System Under Test — here, primary OpenMRS |
| **RAL** | Resource Adapter Layer — abstraction letting one test intent run across OpenMRS/OpenEMR/HAPI/SMART/omiiCARE |
| **O2 / O3** | Legacy / modern OpenMRS reference-application UIs |
| **REQ-`<PREFIX>`-NNN** | Requirement ID, prefix = module (e.g. REQ-PHARM-014) |
| **TC-`<PREFIX>`-NNNN** | Test-case ID, traces to a REQ |
| **RTM** | Requirements Traceability Matrix (REQ ↔ TC ↔ status) |
| **Encounter** | A clinical interaction (FHIR `Encounter`; OpenMRS visit/encounter) |
| **Obs** | Observation (a coded measured value) |
| **CodeableConcept** | FHIR coded value: `system` + `code` + `display` |
| **ADT / ORM / ORU** | HL7 v2 message types (admit-discharge-transfer / order / result) |
| **MRN** | Medical Record Number (a PHI identifier) |
| **PHI / ePHI** | Protected Health Information (electronic) |
| **LOINC / SNOMED / ICD-10 / RxNorm / CVX** | Clinical code systems (§7.4) |
| **SMART on FHIR** | OAuth2-based app-authorization standard over FHIR |
| **US Core / IPS** | FHIR profile sets (US Core 3.1.1/6.x; International Patient Summary) |
| **WCAG 2.1 AA** | Web accessibility conformance target |
| **OWASP ASVS / Top 10** | Security verification standard / common-risk list |
| **Patient-safety defect** | Defect that can cause clinical harm (wrong dose/allergy/result) |
| **Smoke / Sanity / Regression** | Build-acceptance / focused / broad re-test scopes |
| **Pairwise** | Combinatorial technique covering all factor pairs |
| **Exit criteria** | Conditions to pass a quality gate / release |

---

## 9. FAQ

**Q. Can I run a quick perf or security check against o2.openmrs.org?**
No. Perf and security tests run **only** on owned/local environments. The public demos
are off-limits for load and intrusive testing — full stop.

**Q. The public demo lost my test patient. Bug?**
No. Demo data is shared and reset unpredictably. Use a local SDK build for anything
requiring persistence.

**Q. Where do I find the steps for a case?**
In the module CSV under `Test_Steps` (pipe-`|`-separated), with `Test_Data` and
`Expected_Result` alongside. See [test-cases README](../../manual-testing/test-cases/openmrs/README.md).

**Q. Severity vs. priority — what's the difference?**
Severity = impact (how bad). Priority = urgency (when to fix). A typo on a critical screen
can be Low/Med severity but P2. A wrong drug dose is Critical/P1.

**Q. A `display` text looks right but the test still failed on the `code`. Why?**
FHIR validation checks `system` + `code`, not just `display`. A mismatched code/system is a
real (often Critical) interop defect even if the label reads correctly.

**Q. Found something on a backend that isn't OpenMRS — does it count?**
Yes. The RAL means a defect on any target (HAPI/omiiCARE/OpenEMR) matters. Note the
`Source_System` and whether it's adapter-specific or shared.

**Q. Do I need real patient data to test realistically?**
Never. Synthetic data only. Zero-tolerance for real PHI in any asset.

**Q. My test isn't in the RTM. Is that OK?**
No — orphan tests/REQs are a traceability defect. Every TC maps to a REQ and vice versa.

**Q. Which OpenMRS UI — O2 or O3?**
Default to O2 (`o2.openmrs.org`) as the primary reference unless a case explicitly targets O3.

---

## 10. Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| Login fails for a valid user | No Login Location selected / user disabled | Configure a location; enable user (§2.3) |
| `401` on every FHIR call | Missing/expired auth (Basic/OAuth/SMART) | Re-authenticate; check token scope (`REQ-FHIR-001`) |
| FHIR metadata not `4.0.1` | Wrong distro / FHIR2 module not loaded | Rebuild SDK server with ref-app distro (§2.2) |
| `404` on `/ws/fhir2/R4/...` | Wrong base path or resource not enabled | Confirm base URL; check CapabilityStatement |
| HL7 message silently dropped | No native v2 listener in default RefApp | Use configured channel/adapter; verify `MSH` encoding |
| Search returns nothing | Synthetic data not seeded / wrong env | Reload seed data (§2.4); confirm env |
| Test data vanished | Using public demo (auto-reset) | Switch to local SDK |
| `display` ok but FHIR validation fails | Wrong `code`/`system` URI | Cross-check §7.4 + FHIR_MAPPING |
| Audit entry missing | Audit logging disabled | Enable audit (RPT depends on it) |
| A11Y axe passes but keyboard fails | Automated tools miss keyboard traps | Manual keyboard pass (`REQ-A11Y-001/002`) |
| Perf test blocked | Pointed at a public/non-owned host | Repoint to owned/local env — required |
| Intermittent UI E2E failures | Timing/async, shared demo state | Add waits; use isolated local data; quarantine flaky |
| PHI visible in a log/URL | Logging/handling defect | **Stop, escalate (§5.5), file Critical** |

---

## 11. Onboarding — Your First Two Weeks

### Week 1 — Orient

- **Day 1:** Read this KB end-to-end + the [Master Test Strategy](MASTER_TEST_STRATEGY.md).
  Internalize the ethics/scope guard and PHI zero-tolerance rule.
- **Day 2:** Stand up a local OpenMRS SDK server (§2.2); pass the readiness checklist (§2.5).
- **Day 3:** Skim the [reverse-engineering specs](../reverse-engineering/) — start with
  ARCHITECTURE, FHIR_MAPPING, HL7_MAPPING, RBAC_MATRIX, RISK_REGISTER.
- **Day 4:** Read 20–30 cases across AUTH, REG, PHARM, FHIR; learn the 17-column schema.
- **Day 5:** Execute a Smoke run against your local env; log results to the RTM.

### Week 2 — Contribute

- **Day 6–7:** Shadow a triage session; file one practice defect using the §5 template.
- **Day 8:** Run the patient-safety gate subset (CLIN/PHARM/ORDLAB/VITAL Negative+Boundary).
- **Day 9:** Run the automation smoke suite locally (§4.2); read a FHIR conformance test.
- **Day 10:** Take ownership of one module's regression slice; confirm REQ↔TC traceability.

**Definition of "ramped":** can set up an env unaided, execute and log a run, write a
correctly-classified defect, explain FHIR `CodeableConcept` + the PHI rule, and locate any
REQ/TC via the RTM.

---

## 12. Quick Reference Card

| Need | Answer |
|---|---|
| Primary SUT | OpenMRS O2 `https://o2.openmrs.org` (O3 demo `o3.openmrs.org`) |
| FHIR metadata | `/openmrs/ws/fhir2/R4/metadata` → `4.0.1` |
| Manual cases | `manual-testing/test-cases/openmrs/*.csv` (17 cols) |
| Traceability | `manual-testing/rtm/RTM.csv` |
| Requirements | `docs/requirements/requirements-catalog.md` (472 REQs) |
| Perf/Sec rule | **Owned/local environments ONLY** |
| PHI rule | **Synthetic only — zero real PHI, ever** |
| Severity order | Patient-safety / PHI / data-corruption > everything |
| Escalate now | PHI exposure, auth bypass, wrong dose/allergy/result |
| Code systems | LOINC / SNOMED / ICD-10 / RxNorm / CVX (verify system+code) |

---

*End of QA Knowledge Base (QAM-KB-001). Living document — extend per sprint, full pass per release.*
