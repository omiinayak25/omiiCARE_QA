# Test Data & Environment Management — OpenMRS-Primary Healthcare QA Portfolio

> Document ID: **QA-TDEM-001**. Version 1.0. Generated 2026-07-01. Status: **Baselined for review**.
> System Under Test (SUT): **OpenMRS** (reference: https://o2.openmrs.org), portable to **OpenEMR, HAPI FHIR, SMART Health IT, omiiCARE** via the **Resource Adapter Layer (RAL)**.
> Governs test-data provisioning and environment topology for **472 requirements** (`REQ-<PREFIX>-NNN`) traced by **1,349+ manual test cases** (scaling toward ~4,000) across **21 modules**.

This document is subordinate to the [Master Test Plan](MASTER_TEST_PLAN.md) (QA-MTP-001) and complements the repository-level [Test Data Strategy](../TEST_DATA_STRATEGY.md) and [Environment Guide](../ENVIRONMENT_GUIDE.md). Where those define *principles*, this document defines the *operational mechanics* the QA team executes per cycle: data generators, fixtures, the environment matrix, seeding/reset procedures, masking stance, FHIR/HL7 sample data, isolation, and public-demo usage rules.

**Referenced artifacts:** [SRS](../reverse-engineering/SRS.md), [NFR](../reverse-engineering/NFR.md), [Data Dictionary](../reverse-engineering/DATA_DICTIONARY.md), [FHIR Mapping](../reverse-engineering/FHIR_MAPPING.md), [HL7 Mapping](../reverse-engineering/HL7_MAPPING.md), [RBAC Matrix](../reverse-engineering/RBAC_MATRIX.md), [Risk Register](../reverse-engineering/RISK_REGISTER.md), [Requirements Catalog](../requirements/requirements-catalog.md), [RTM](../../manual-testing/rtm/RTM.csv).

---

## 1. Purpose, Scope & First Principles

The first and inviolable rule: **never real PHI**. Every record in every environment is synthetic, schema-valid, standards-aware, and visibly fake. The second rule: **public sites are read-only demos** — no performance or security testing, no destructive operations, no bulk writes against `https://o2.openmrs.org` or any third-party hosted instance. All write-heavy, perf, and security work runs exclusively against **owned local/containerized environments**.

| # | Principle | Enforces |
|---|---|---|
| TD-P1 | **No real PHI, ever.** Only synthesized or templated data is permitted in repo or runtime. | REQ-SEC-*, REQ-DATA-*, HIPAA-like |
| TD-P2 | **Obviously fake identifiers.** MRNs, names, SSNs use reserved fake ranges (`SYN-*`, `999-xx-xxxx`). | TD-P1 |
| TD-P3 | **Schema-valid by construction.** Data satisfies OpenMRS DB constraints + FHIR R4/HL7 v2 profiles. | REQ-FHIR-*, REQ-HL7-* |
| TD-P4 | **Deterministic by default.** Seeded generation → reproducible runs; random mode logs its seed. | RTM reproducibility |
| TD-P5 | **Self-contained & cleaned up.** Each test owns its data or relies on a known baseline; restores state. | Isolation |
| TD-P6 | **Backend-agnostic.** One canonical dataset definition feeds all RAL backends; config selects volume/target. | RAL portability |
| TD-P7 | **Public = demo-only.** No perf/security/bulk-write/destructive ops against shared hosted systems. | §10 |

---

## 2. Synthetic / PHI-Safe Data Strategy

Data is **synthesized, never de-identified.** Because no real records ever enter the pipeline, classic masking/anonymization of production data is *out of workflow* (see §7). Synthetic patients are drawn from reserved-fake namespaces and tagged so they can be located and purged.

| Class | Source / library | Fake-marking convention | Standards target |
|---|---|---|---|
| Demographics | Faker-style provider (seeded) | Names from a curated synthetic pool; MRN `SYN-{8 digits}` | FHIR `Patient` |
| Identifiers | Reserved test ranges | SSN `999-xx-xxxx`, NPI `9999999999`, phone `+1-555-01xx` | FHIR `Identifier` |
| Coded clinical data | Public terminology subsets | ICD-10, CPT, LOINC, SNOMED CT from licensed test value sets only | FHIR `CodeableConcept` |
| Free-text notes | Template library | Marked `[SYNTHETIC NOTE]` header; no copied clinical narrative | FHIR `Observation`/`Encounter` |
| Tag/extension | Security label | Every synthetic resource carries `meta.tag = SYNTHETIC` | FHIR `Meta.tag` |

A pre-commit and CI **PHI-leak scanner** (regex + entropy heuristics) rejects any candidate matching real-SSN/real-MRN patterns, real email domains, or DICOM pixel data. A finding here is a **build-blocking defect**, not a warning.

---

## 3. Data Generators & Builders

Generation is layered so each test level pulls the smallest sufficient dataset.

| Mechanism | Scope | Determinism | Realized in | Example use |
|---|---|---|---|---|
| **Factories** | Single domain object with sane defaults, per-field overridable | Seeded | Automation tier | `patientFactory({ gender: 'F', age: 0 })` for neonate vitals boundary |
| **Builders** | Complex aggregates (visit→encounter→obs graphs, FHIR bundles) | Seeded | Automation tier | `visitBuilder().withVitals().withAllergy('penicillin')` |
| **Seed generators** | Bulk environment baseline (N patients, M providers) | Seeded by env | DB seed scripts | 500-patient QA baseline |
| **Volume generators** | Scaled synthetic datasets for `perf` (owned infra only) | Seeded, parameterized by N | Perf harness | 1M patients / 10M observations |
| **Template fixtures** | Canonical FHIR/HL7 payloads | Static, versioned | `fixtures/` | ADT^A01, ORU^R01, FHIR transaction Bundle |
| **Faker providers** | Realistic-but-synthetic names/addresses/dates | Seeded | Shared lib | Address, DOB, contact |

All generators emit through the **RAL canonical model** (Patient, Visit, Encounter, Obs, Order, …), so the same definition materializes correctly against OpenMRS, OpenEMR, HAPI FHIR, SMART Health IT, or omiiCARE. The generator records the seed and RAL adapter version in run metadata for reproducibility.

---

## 4. Fixtures & Reference Datasets

Curated, versioned datasets back manual and automated suites. Manual fixtures live alongside `manual-testing/test-cases/openmrs/`; canonical payloads live in a shared `fixtures/` tree.

| Dataset | Size (QA baseline) | Drives modules | Notes |
|---|---|---|---|
| Core patient cohort | 500 patients | REG, SRCH, PDASH, VISIT, CLIN | Mixed age/gender/locale; 10% with allergies |
| Provider/location roster | 40 providers, 8 locations | AUTH, RBAC, APPT | Role-mapped to RBAC matrix |
| Appointment schedule | 2 weeks, 6 services | APPT, NOTIF, TELE | Includes conflict/no-show edge slots |
| Orders & labs | 300 orders | ORDLAB, PHARM | LOINC-coded results incl. abnormal-flag boundaries |
| Billing/claims | 150 invoices, 80 claims | BILL | Synthetic payers/plans/member IDs |
| Edge & boundary pack | curated | ALL | §8 |
| FHIR/HL7 sample pack | curated | FHIR, HL7 | §6 |

Each dataset has a manifest (`dataset.yml`) describing intent, owning module, schema version, and refresh cadence. Datasets are re-versioned whenever the OpenMRS schema or a standards profile changes (drift control).

---

## 5. Environment Matrix

Environments are tiered from fast/ephemeral to production-like. **Performance and security testing are confined to `local`, `docker`, and `perf` (all owned).** No higher environment connects to a lower-environment data store.

| Env | Profile | Backend / DB | Data set | Reset cadence | Perf/Sec allowed | Primary users |
|---|---|---|---|---|---|---|
| Development | `dev` | OpenMRS embedded / H2 | Minimal seed (~20 patients) | On startup | No | Developers |
| Local | `local` | OpenMRS RefApp + PostgreSQL (Docker) | Full PHI-safe seed (500) | On demand | **Yes (owned)** | Dev / QA |
| Docker | `docker` | Full containerized OpenMRS + FHIR + HL7 listener | Full PHI-safe seed | Per stack up/down | **Yes (owned)** | Dev / QA |
| Test (CI) | `test` | Testcontainers (OpenMRS + Postgres) | Deterministic fixtures | Per test/run | No (functional only) | CI / SDETs |
| QA | `qa` | Dedicated OpenMRS + PostgreSQL | Curated QA baseline | Per cycle (start) | No | QA team |
| Stage | `stage` | Prod-like OpenMRS cluster | Prod-like synthetic (scaled) | Pre-release | Limited (owned, scheduled) | Release/QA |
| Performance | `perf` | Isolated OpenMRS + volume DB | Volume-scaled synthetic | Per perf run | **Yes (owned, isolated)** | Performance QA |
| Demo | `demo` | OpenMRS or hosted RefApp | Curated demo set | Manual | No (read-only on hosted) | Portfolio/demo |

> Public reference instance `https://o2.openmrs.org` is treated as a **`demo`-class read-only** target governed by §10 — never `perf`, never `qa`.

**RAL backend matrix (portability):** the same canonical dataset is materialized per backend; secondary backends are exercised at least once per release.

| Backend | Adapter target | Data load path | Coverage tier |
|---|---|---|---|
| OpenMRS | REST `/ws/rest/v1`, FHIR `/ws/fhir2/R4` | Full seed + builders | Primary (every cycle) |
| HAPI FHIR | FHIR R4 transaction bundles | FHIR fixture pack | Secondary (per release) |
| OpenEMR | REST + FHIR R4 | Mapped subset | Secondary (per release) |
| SMART Health IT | FHIR R4 sandbox (owned/sandbox) | FHIR fixture pack | Interop spot-check |
| omiiCARE | In-house API via RAL | Canonical builders | Secondary (per release) |

---

## 6. FHIR R4 & HL7 v2 Sample Data

Interoperability suites (FHIR, HL7 modules) depend on a versioned, profile-validated payload pack. Every payload carries the `SYNTHETIC` security tag and validates against the relevant Implementation Guide before use.

**FHIR R4 sample pack** — drives `REQ-FHIR-*`:

| Resource / artifact | Validates | Boundary/negative variants |
|---|---|---|
| `Patient` | identifiers, name, gender, birthDate | missing required, bad gender code, future birthDate |
| `Observation` (vitals, LOINC) | code system URI, valueQuantity + UCUM units | wrong unit, value at reference boundary, missing code |
| `Encounter`/`Appointment` | status state machine, period | invalid status transition, past period |
| `MedicationRequest` | dosage, code | dose overflow, unknown drug code |
| transaction `Bundle` | referential integrity across resources | dangling reference, duplicate fullUrl |
| `OperationOutcome` | error-path conformance | asserted on every negative case |

Validation uses the HL7 FHIR validator against R4 + project IG. Code-system URIs (LOINC `http://loinc.org`, SNOMED `http://snomed.info/sct`, ICD-10) are asserted exactly — a wrong URI is a defect.

**HL7 v2 sample pack** — drives `REQ-HL7-*`:

| Message | Trigger | Key segments asserted | Negative variant |
|---|---|---|---|
| ADT^A01 | Admit | MSH, PID, PV1 | missing PID-3, malformed MSH-9 |
| ADT^A04 | Register | MSH, PID, PV1 | bad encoding chars |
| ORM^O01 | New order | MSH, PID, ORC, OBR | missing ORC, invalid OBR-4 code |
| ORU^R01 | Result | MSH, PID, OBR, OBX | OBX unit mismatch, abnormal-flag boundary |

HL7 messages are byte-exact fixtures (CR segment terminators preserved) so segment/field parsing is deterministic. ADT/ORM/ORU samples map to the canonical model via the [HL7 Mapping](../reverse-engineering/HL7_MAPPING.md).

---

## 7. Masking / Anonymization Stance

Because the pipeline **never ingests real PHI**, there is nothing real to mask — the strategy is **synthesis, not de-identification.** This stance is recorded so it cannot quietly erode:

- No production export, snapshot, or "scrubbed" copy is ever permitted as a data source. Generate synthetically instead.
- If a future need for production-realistic data arises, the answer is a richer *generator*, not a masking job.
- Should any externally supplied dataset ever be evaluated, it must pass the §2 PHI-leak scanner and carry `SYNTHETIC` tags before acceptance; otherwise it is rejected as a defect.
- Logs, screenshots, and defect attachments must also be PHI-safe by construction (synthetic data in, synthetic data out), satisfying `REQ-SEC-*` audit/PHI controls.

---

## 8. Edge, Boundary & Negative Datasets

A curated pack drives negative/boundary/decision-table/state-transition coverage across modules:

| Category | Examples | Modules |
|---|---|---|
| Field limits | min/max name length, max-length MRN, numeric vitals limits | REG, VITAL |
| Empty / null / whitespace | blank required fields, whitespace-only search | REG, SRCH |
| Invalid codes | bad ICD-10/CPT/LOINC/SNOMED, malformed FHIR bundle, broken HL7 segment | FHIR, HL7, ORDLAB |
| Date edges | leap year, TZ boundary, far-future/past, DST transition | APPT, VISIT |
| Localization | RTL text, multi-byte/Unicode, locale date formats | REG, A11Y |
| Authorization edges | role boundary, cross-tenant/cross-patient access attempt | RBAC, SEC |
| Patient-safety edges | allergy/interaction trigger, neonate/geriatric dosing, unit confusion (mg vs mL) | CLIN, PHARM, VITAL |

Patient-safety edge data is **first-class**: wrong-patient, allergy-conflict, and unit-mismatch datasets are mandatory fixtures, not optional extras.

---

## 9. Seeding, Reset & Isolation

**Seeding** — applied at environment/cycle start:

| Env | Seed mechanism | Trigger |
|---|---|---|
| `dev` | In-memory seed loader | Application startup |
| `test` (CI) | Testcontainers init + factory fixtures | Per test run |
| `local`/`docker` | SQL/REST seed script + builder bootstrap | `make seed` / compose up |
| `qa`/`stage` | Versioned baseline restore | Cycle start / pre-release |
| `perf` | Volume generator (parameter N) | Per perf run |

**Reset** — restore to known-good:

- **Transactional rollback** for integration tests (Testcontainers/H2) — fastest, leaves no residue.
- **Truncate + reseed** for `qa` baseline restore between cycles.
- **Tear-down + recreate** for `docker`/`perf` (volumes dropped, stack rebuilt) — guarantees zero drift.
- **Tagged purge**: any orphaned `SYNTHETIC`-tagged record is reaped by a scheduled cleanup job.

**Isolation** guarantees parallel runs never collide:

- Each environment has its **own DB, credentials, and endpoints**; lower envs never read higher-env stores.
- Parallel automated runs use **namespaced/tenant-scoped data** (run-id prefix on synthetic MRNs).
- Docker Compose uses **dedicated networks and volumes per stack**; no cross-talk.
- Test data created by a run is owned by that run and torn down by it (TD-P5).
- RAL backends are isolated per portability run so OpenMRS state never leaks into HAPI/OpenEMR/omiiCARE assertions.

---

## 10. Public-Demo Usage Rules (Hosted / Shared Systems)

The public OpenMRS reference instance (`https://o2.openmrs.org`) and any third-party hosted system (SMART sandbox, HAPI public server) are governed by strict rules. Violating these is a process defect.

| Rule | Allowed | Forbidden |
|---|---|---|
| Purpose | Functional smoke, UI exploration, screenshot/demo, read navigation | Performance/load/stress; security/pen testing; fuzzing |
| Writes | Light, clearly-tagged synthetic records you clean up | Bulk writes, volume seeding, destructive deletes |
| Credentials | Provided demo accounts only | Credential brute-force, privilege-escalation attempts |
| Data | Synthetic, `SYNTHETIC`-tagged, minimal | Any real PHI; any data you cannot purge |
| Rate | Human-paced, respectful | Automated high-rate hammering |

All **performance** (`REQ-PERF-*`) and **security** (`REQ-SEC-*`) testing runs **only against owned `local`/`docker`/`perf` environments**, never against public or shared infrastructure. This is restated as a hard exit-gate in the [Master Test Plan](MASTER_TEST_PLAN.md).

---

## 11. Roles & Cycle Checklist

| Role | Data/environment responsibility |
|---|---|
| QA Architect | Owns this document, the PHI-safety guarantee, and the public-usage policy |
| SDET (automation) | Maintains factories/builders/volume generators and the RAL load path |
| QA Engineer (manual) | Curates fixtures, edge/boundary packs, FHIR/HL7 samples |
| DevOps | Provisions environments, seed/reset scripts, isolation (networks/volumes) |
| QA Lead | Verifies data + environment readiness as a per-cycle **entry criterion** |

**Per-cycle readiness checklist (entry gate):**

1. Target environment provisioned, profile validated, secrets present (fail-fast).
2. Correct baseline seed applied and version recorded.
3. PHI-leak scanner green; all synthetic records carry `SYNTHETIC` tag.
4. FHIR/HL7 sample pack validates against current IG/profiles.
5. RAL adapter version pinned and logged.
6. Reset/teardown verified working (dry run).
7. No `perf`/`sec` work pointed at any public/shared target.

---

## 12. Risks & Mitigations

| ID | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| TDE-R1 | Real PHI leaks into repo/runtime | Low | Critical | TD-P1/P2, pre-commit + CI PHI scanner, `SYNTHETIC` tags |
| TDE-R2 | Perf/sec test hits public OpenMRS | Low | High | §10 policy, env-target validation, owned-infra-only gate |
| TDE-R3 | Dataset drifts from schema/profile | Medium | Medium | Manifest versioning, schema-drift detection, re-seed on change |
| TDE-R4 | Cross-run data collisions in parallel CI | Medium | Medium | Run-id namespacing, transactional rollback, tenant scoping |
| TDE-R5 | RAL backend state bleed between portability runs | Medium | Medium | Per-backend isolation, teardown-recreate, tagged purge |
| TDE-R6 | Stale/orphaned synthetic data accumulates | Medium | Low | Scheduled tagged-purge reaper job |

---

## 13. Dependencies & References

- **Parent:** [Master Test Plan](MASTER_TEST_PLAN.md) (QA-MTP-001); repository [Test Data Strategy](../TEST_DATA_STRATEGY.md) and [Environment Guide](../ENVIRONMENT_GUIDE.md).
- **Standards:** FHIR R4 + project IG, HL7 v2.x, WCAG 2.1 AA, OWASP ASVS/Top-10, HIPAA-like PHI controls.
- **Mappings:** [FHIR Mapping](../reverse-engineering/FHIR_MAPPING.md), [HL7 Mapping](../reverse-engineering/HL7_MAPPING.md), [Data Dictionary](../reverse-engineering/DATA_DICTIONARY.md), [RBAC Matrix](../reverse-engineering/RBAC_MATRIX.md).
- **Traceability:** [Requirements Catalog](../requirements/requirements-catalog.md), [RTM](../../manual-testing/rtm/RTM.csv), `manual-testing/test-cases/openmrs/`.

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-07-01 | QA Architect | Initial Test Data & Environment Management document (OpenMRS-primary, RAL-portable) |
