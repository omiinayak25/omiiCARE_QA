# Test Data Strategy

> **Synthetic, PHI-safe data discipline.** This document governs how omiiCARE_QA
> creates, manages, refreshes, and disposes of test data across every
> environment. Its first and inviolable rule: **never real PHI**. It supports the
> [Test Strategy](TEST_STRATEGY.md), feeds the [Master Test Plan](MASTER_TEST_PLAN.md),
> and underpins the traceable coverage in the [RTM](RTM.md). Facts defer to
> [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

Define a single, compliant, repeatable approach to test data so that:

- No real patient information ever enters the repository or any environment.
- Tests at every level have deterministic, schema-valid, standards-aware data.
- Data is generated, versioned, refreshed, and cleaned up predictably.
- Edge and boundary datasets exist for negative and boundary testing.

This is **documentation only** in Milestone 1. The generators, factories,
builders, and seed datasets it describes are built in Milestone 5; manual
fixtures in Milestone 6; specialized perf/security datasets in Milestone 7.

## Scope

- **In scope:** data principles; generation mechanisms; data categories;
  cross-environment management; refresh/cleanup; masking/anonymization stance;
  deterministic versus random data; edge/boundary datasets; compliance note.
- **Out of scope:** the generator source code (Milestone 5) and specific seed
  values (delivered with the database module, Milestone 2, and automation,
  Milestone 5).

## Responsibilities

| Role | Data responsibility |
|------|---------------------|
| QA Architect | Owns the strategy and the PHI-safety guarantee |
| SDET III | Designs factories/builders/seed generators and the data platform |
| SDET II | Authors and maintains per-test datasets and fixtures |
| Senior QA Engineer | Curates edge/boundary and exploratory datasets |
| QA Lead | Verifies data readiness as an entry criterion per cycle |

---

## 1. Principles

| # | Principle |
|---|-----------|
| P1 | **Never real PHI.** Only synthetic data, generated or templated, is permitted. |
| P2 | **Schema-valid by construction.** Data satisfies DB constraints and standards mappings. |
| P3 | **Deterministic where it matters.** Seeded generation yields reproducible runs. |
| P4 | **Self-contained tests.** Each test creates/owns its data or relies on a known seed. |
| P5 | **Clean up after yourself.** Tests leave the environment as they found it. |
| P6 | **Versioned datasets.** Seed sets evolve with the schema and are tracked. |
| P7 | **Obviously fake.** Synthetic identifiers are visibly non-real (e.g. `SYN-*`). |

## 2. Data Generation

| Mechanism | Use | Realized |
|-----------|-----|----------|
| Factories | Object creation with sensible defaults, overridable per test | M5 |
| Builders | Fluent construction of complex aggregates (e.g. FHIR bundles) | M5 |
| Seed generators | Bulk, environment-level baseline datasets | M5 (with M2 DB seeds) |
| Faker-style providers | Realistic-but-synthetic names, addresses, dates | M5 |
| Template fixtures | Canonical FHIR/HL7 payloads for contract tests | M5 |
| Manual datasets | Curated CSV/JSON for manual cases | M6 |

Generation is deterministic by default (fixed seed) and switchable to randomized
for fuzz/exploratory runs (§7).

## 3. Data Categories

| Category | Contents | Standards mapping |
|----------|----------|-------------------|
| Patients | Demographics, MRNs (synthetic), contacts | FHIR Patient |
| Doctors / providers | Names, specialties, NPIs (synthetic) | FHIR Practitioner |
| Hospitals / tenants | Org units, locations, tenant scope | FHIR Organization/Location |
| Insurance | Payers, plans, member IDs (synthetic) | FHIR Coverage |
| Appointments | Slots, statuses, encounter links | FHIR Appointment/Encounter |
| Invoices | Charges, totals, adjustments | FHIR Invoice/ChargeItem |
| Claims | Submissions, adjudication outcomes | FHIR Claim/ClaimResponse |
| FHIR bundles | Composite resource graphs for contract tests | FHIR R4 Bundle |
| HL7 messages | ADT/ORM/ORU samples | HL7 v2 |
| Coded records | ICD-10, CPT, LOINC, SNOMED CT coded entries | Respective code systems |
| Lab results | Observations, reference ranges, abnormal flags | LOINC |
| Radiology metadata | Study/series/report descriptors (no real images) | FHIR ImagingStudy/DiagnosticReport |

All identifiers are synthetic and visibly fake (P7).

## 4. Data Management Across Environments

| Environment | Profile | Data source | Lifecycle |
|-------------|---------|-------------|-----------|
| Development | `dev` | Lightweight seeds (H2) | Recreated on startup |
| Test | `test` | Per-run factories + minimal seed | Created/torn down per run |
| QA | `qa` | Versioned baseline seed + per-case data | Refreshed per cycle |
| Stage | `stage` | Production-like synthetic dataset | Refreshed before release E2E |
| Performance | `perf` | Volume-scaled synthetic dataset (owned infra) | Generated per perf run |

The same dataset definitions drive all environments; only volume and refresh
cadence differ. Configuration — never code — selects the dataset.

## 5. Data Refresh & Cleanup

- **Refresh:** baseline seeds are re-applied at cycle start; volume datasets are
  regenerated per performance run.
- **Cleanup:** automated tests delete or roll back data they create (P5);
  integration tests prefer transactional rollback (Testcontainers/H2).
- **Isolation:** parallel runs use namespaced or tenant-scoped data to avoid
  collisions.
- **Drift control:** when the schema changes (Flyway), affected datasets are
  regenerated and re-versioned.

## 6. Masking / Anonymization Stance

Because omiiCARE_QA **never ingests real PHI**, masking or anonymization of real
data is *not part of the workflow* — there is nothing real to mask. The strategy
is **synthesis, not de-identification**. Should any future scenario require
production-like data, the rule is to generate it synthetically rather than copy
and mask real records. This stance is recorded so it cannot quietly erode.

## 7. Deterministic vs Random Data

| Mode | When used | Property |
|------|-----------|----------|
| Deterministic (seeded) | Regression, smoke, CI, contract | Same input → same data → reproducible failures |
| Randomized (fuzz) | Exploratory, robustness, boundary discovery | Wider coverage; failures are re-seeded for reproduction |

Default is deterministic; randomized runs always log their seed so any failure is
reproducible.

## 8. Edge & Boundary Datasets

Curated datasets exist specifically to drive negative and boundary testing:

- Minimum/maximum field lengths and numeric limits.
- Empty, null, and whitespace-only values.
- Invalid codes (bad ICD-10/CPT/LOINC/SNOMED), malformed FHIR bundles, broken HL7 segments.
- Date edge cases (leap years, time-zone boundaries, far-future/past).
- Localization edge cases (RTL text, multi-byte characters, locale-specific formats).
- Authorization edge cases (role boundaries, cross-tenant access attempts).

## 9. Compliance Note

omiiCARE_QA models **HIPAA-like** privacy practices for educational and portfolio
purposes and makes **no formal certification claims**
([MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §9). The
exclusive use of synthetic, PHI-safe data is the foundational control that makes
this posture credible. Any contribution introducing real or realistic-but-sourced
patient data is a defect and must be rejected. See [SECURITY.md](../SECURITY.md).

## Examples

- *Deterministic regression:* A claims-adjudication regression test seeds a fixed
  payer/plan/member set so a failure reproduces identically on every machine.
- *Boundary dataset:* A LOINC-coded lab result with a value exactly at the upper
  reference limit verifies the abnormal-flag boundary without real data.

## Future Enhancements

- AI-assisted synthetic-data generation for richer, standards-valid bundles (M9).
- A shared, versioned "data catalog" describing every dataset and its intent.
- Automated schema-drift detection that flags stale datasets in CI (M8).

## Dependencies

- Serves [TEST_STRATEGY.md](TEST_STRATEGY.md) and
  [MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md).
- Aligns with the database module and seeds (Milestone 2) and the test-data
  platform (Milestone 5).
- Standards mappings per [PROJECT_METADATA.md](PROJECT_METADATA.md) §6.

## References

- [ARCHITECTURE.md](../ARCHITECTURE.md) §8 (Data Architecture).
- [SECURITY.md](../SECURITY.md); HL7 FHIR R4 and HL7 v2 specifications.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial synthetic PHI-safe test data strategy (Milestone 1) |
