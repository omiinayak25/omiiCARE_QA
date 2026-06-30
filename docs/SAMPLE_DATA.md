# Sample Data — omiiCARE_QA 1.0.0

This document describes the **synthetic, PHI-safe demo dataset** that ships with omiiCARE_QA and is loaded into the database by Flyway repeatable seed migrations, plus the runtime `PatientFactory` used to generate fresh test patients.

> **PHI safety:** every value below is fictional. Names are placeholder-style ("John Public", "Jane Sample"), e-mails use the non-routable `@demo.example` domain, and phone numbers use the reserved `555-01xx` range. No real patient information exists anywhere in this dataset. See [`docs/TEST_DATA_STRATEGY.md`](TEST_DATA_STRATEGY.md).

## How it is loaded

The seed lives in [`apps/backend/src/main/resources/db/seed/`](../apps/backend/src/main/resources/db/seed) and is applied by Flyway as **repeatable** migrations (`R__`). It is only on the Flyway path for non-production profiles (`dev`, `local`, `docker`, `test`, `qa`); `prod` never seeds demo data. Schema itself comes from the versioned migrations `V1__baseline_platform_schema.sql` and `V2__clinical_core_schema.sql`. The same seed runs unchanged on **H2** (PostgreSQL compatibility mode) and **PostgreSQL**.

| Seed file | Loads |
|-----------|-------|
| `R__seed_010_reference_and_demo.sql` | 12 RBAC roles, core permissions, DEMO tenant, DEMO-GEN hospital, 5 departments, the `demo.admin` user |
| `R__seed_020_clinical_demo.sql` | Providers DR-001/DR-002, patients MRN-0001..0003, one booked appointment |

Every `INSERT` is guarded by `WHERE NOT EXISTS`, so re-running the repeatable seed on a persistent database is **idempotent** (no duplicates). The numeric prefix guarantees `010` runs before `020`, so the clinical seed always finds the tenant/hospital it depends on.

## Reference data

### RBAC roles (12)

| Code | Name |
|------|------|
| `SUPER_ADMIN` | Super Admin |
| `HOSPITAL_ADMIN` | Hospital Admin |
| `DOCTOR` | Doctor |
| `NURSE` | Nurse |
| `RECEPTIONIST` | Receptionist |
| `LAB_TECHNICIAN` | Lab Technician |
| `RADIOLOGIST` | Radiologist |
| `PHARMACIST` | Pharmacist |
| `BILLING_STAFF` | Billing Staff |
| `INSURANCE_STAFF` | Insurance Staff |
| `PATIENT` | Patient |
| `AUDITOR` | Auditor |

### Core permissions

`patient:read`, `patient:write`, `appointment:read`, `appointment:write`, `prescription:write`, `billing:read`, `billing:write`, `audit:read`, `admin:manage`.

Authorization across the API is **permission-based** (e.g. `@PreAuthorize("hasAuthority('patient:write')")`), not role-name based, so access does not depend on hardcoded role names.

## Organization (synthetic)

| Entity | Code | Value |
|--------|------|-------|
| Tenant | `DEMO` | omiiCARE Demo Health Network (`ACTIVE`) |
| Hospital | `DEMO-GEN` | omiiCARE General Hospital (timezone `UTC`, `ACTIVE`) |
| Departments (5) | `CARD`, `RADI`, `LABM`, `PHAR`, `EMER` | Cardiology, Radiology, Laboratory Medicine, Pharmacy, Emergency |

## Users

| Username | E-mail | Full name | Tenant | Status |
|----------|--------|-----------|--------|--------|
| `demo.admin` | `demo.admin@omiicare.example` | Demo Administrator | `DEMO` | `ACTIVE` |

> **Demo login:** `demo.admin` / `Admin@12345`. The Flyway seed inserts the user
> row only (with an unset password). In non-production profiles (`dev`, `local`,
> `docker`, `test`, `qa`) the `DataInitializer` bootstrap then grants `demo.admin`
> the **SUPER_ADMIN** role (which holds every permission) and sets the working
> bcrypt password — so the demo account can exercise the full API. It is never
> active in `stage`/`prod`. Use this account for the Postman collection and the
> `curl` walkthrough in [`docs/examples/README.md`](examples/README.md).

## Providers

| id | Code | Name | Specialty | Status |
|----|------|------|-----------|--------|
| 1 | `DR-001` | Alice Cardwell | Cardiology | `ACTIVE` |
| 2 | `DR-002` | Brian Rayner | Radiology | `ACTIVE` |

## Patients (synthetic)

| id | MRN | Name | Date of birth | Gender | E-mail | Phone |
|----|-----|------|---------------|--------|--------|-------|
| 1 | `MRN-0001` | John Public | 1985-04-12 | `MALE` | `john.public@demo.example` | `+1-555-0101` |
| 2 | `MRN-0002` | Jane Sample | 1990-09-30 | `FEMALE` | `jane.sample@demo.example` | `+1-555-0102` |
| 3 | `MRN-0003` | Sam Tester | 1978-01-05 | `OTHER` | `sam.tester@demo.example` | `+1-555-0103` |

## Appointment (booked)

| Patient | Provider | Scheduled start | Scheduled end | Status | Reason |
|---------|----------|-----------------|---------------|--------|--------|
| `MRN-0001` (John Public) | `DR-001` (Alice Cardwell) | 2027-01-15 09:00 UTC | 2027-01-15 09:30 UTC | `BOOKED` | Routine cardiology follow-up |

> The slot sits well in the future so the demo appointment stays valid over time and exercises the same path that **BR-APPT-001** (no double-booking) guards. Attempting to book DR-001 into an overlapping window returns HTTP 422 / `OMII-422`. See [`docs/BUSINESS_RULES.md`](BUSINESS_RULES.md).

## PatientFactory — generated test data

For automated tests that need **fresh, unique** patients rather than the fixed seed rows, the automation layer provides a `PatientFactory` (Factory/Builder pattern over [Datafaker](https://www.datafaker.net/)) at [`automation/src/test/java/com/omiicare/qa/automation/core/generators/PatientFactory.java`](../automation/src/test/java/com/omiicare/qa/automation/core/generators/PatientFactory.java).

`PatientFactory.newPatient()` returns an immutable `SyntheticPatient` record with PHI-safe fields:

| Field | Generation rule |
|-------|-----------------|
| `firstName` | Datafaker `name().firstName()` |
| `lastName` | Datafaker `name().lastName()` |
| `dateOfBirth` | today minus 1..95 years (always in the past — satisfies the API's `@Past` rule) |
| `gender` | one of `MALE` / `FEMALE` / `OTHER` / `UNKNOWN` |
| `email` | `firstname.lastname@demo.example` (lowercased, non-routable test domain) |
| `phone` | reserved `+1-555-0x-xxx` pattern |

Key safety properties:

- **Non-routable contact data** — e-mails use the `@demo.example` test domain and phones use the reserved `555` exchange, so generated records can never reach a real person.
- **Validation-friendly** — DOB is always in the past and gender is always a valid value, so generated patients pass `CreatePatientRequest` validation.
- **Fresh per call** — field *values* vary per invocation (only the *shape* is fixed), which suits independent, parallel test data.

## Related docs

- [`docs/examples/README.md`](examples/README.md) — sample payloads + `curl` walkthrough
- [`postman/README.md`](../postman/README.md) — Postman collection (run Login first)
- [`docs/TEST_DATA_STRATEGY.md`](TEST_DATA_STRATEGY.md) · [`docs/DATABASE_BLUEPRINT.md`](DATABASE_BLUEPRINT.md) · [`docs/BUSINESS_RULES.md`](BUSINESS_RULES.md)
- [`ARCHITECTURE.md`](../ARCHITECTURE.md) · [`docs/PORTFOLIO_GUIDE.md`](PORTFOLIO_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | SDET | Initial (Milestone 10) |
