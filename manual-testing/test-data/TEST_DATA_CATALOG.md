# Test Data Catalog

> **Purpose.** Authoritative inventory of synthetic, PHI-safe test data for omiiCARE_QA: the seeded DEMO dataset (roles, tenant, hospital, departments, users, providers, patients, appointment), recommended additional datasets, and the `PatientFactory` generator. **No real PHI is ever used, seeded, or generated.**

## Purpose

Defines every reusable data fixture so cycles are reproducible and traceable to their source SQL or generator. It is the contract between manual testers and the automation module on what "known" data exists.

## Scope

- **In scope:** Flyway repeatable seeds and the automation `PatientFactory`; recommended boundary/negative datasets.
- **Out of scope:** Performance bulk-load datasets (see `../../quality/performance/`) and exact boundary tables (see [Boundary Data Sets](BOUNDARY_DATA_SETS.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Owns this catalog and the PHI-safe guarantee |
| Backend Engineer | Maintains the Flyway seeds it documents |
| Automation Engineer | Maintains `PatientFactory` / `SyntheticPatient` |

---

## 1. PHI-Safety Guarantee

| Rule | Implementation |
|------|----------------|
| No real patient data | All names/dates/contacts are fictional |
| Non-routable email | All emails use `@demo.example` / `@omiicare.example` (RFC 2606 reserved) |
| Reserved phone block | `+1-555-01xx` (555 exchange is fictional in US) |
| Generated data is synthetic | `PatientFactory` uses Datafaker (fictional name corpus) |
| Future-dated appointments | Demo appointment uses `2027-01-15` to avoid "now" coupling |

## 2. Seeded DEMO Data (Flyway repeatable seeds)

Source: `apps/backend/src/main/resources/db/seed/R__seed_010_reference_and_demo.sql` and `R__seed_020_clinical_demo.sql`. Loaded on `dev`, `test`, `local`, `docker`, `qa` profiles (never `stage`/`prod`).

### 2.1 Tenant & Hospital

| Entity | Code | Name | Status |
|--------|------|------|--------|
| Tenant | `DEMO` | omiiCARE Demo Health Network | ACTIVE |
| Hospital | `DEMO-GEN` | omiiCARE General Hospital (tz UTC) | ACTIVE |

### 2.2 Departments (5, under DEMO-GEN)

| Code | Name |
|------|------|
| CARD | Cardiology |
| RADI | Radiology |
| LABM | Laboratory Medicine |
| PHAR | Pharmacy |
| EMER | Emergency |

### 2.3 RBAC Roles (12)

| Code | Name | Code | Name |
|------|------|------|------|
| SUPER_ADMIN | Super Admin | RADIOLOGIST | Radiologist |
| HOSPITAL_ADMIN | Hospital Admin | PHARMACIST | Pharmacist |
| DOCTOR | Doctor | BILLING_STAFF | Billing Staff |
| NURSE | Nurse | INSURANCE_STAFF | Insurance Staff |
| RECEPTIONIST | Receptionist | PATIENT | Patient |
| LAB_TECHNICIAN | Lab Technician | AUDITOR | Auditor |

### 2.4 Demo User

| Field | Value |
|-------|-------|
| Username | `demo.admin` |
| Email | `demo.admin@omiicare.example` |
| Full name | Demo Administrator |
| Tenant | `DEMO` |
| Status | ACTIVE, email verified |
| Login password (dev) | `Admin@12345` (set in M3 auth) |

### 2.5 Providers (2, under DEMO-GEN)

| Code | First | Last | Specialty | Status |
|------|-------|------|-----------|--------|
| DR-001 | Alice | Cardwell | Cardiology | ACTIVE |
| DR-002 | Brian | Rayner | Radiology | ACTIVE |

### 2.6 Patients (3)

| MRN | First | Last | DOB | Gender | Email | Phone |
|-----|-------|------|-----|--------|-------|-------|
| MRN-0001 | John | Public | 1985-04-12 | MALE | john.public@demo.example | +1-555-0101 |
| MRN-0002 | Jane | Sample | 1990-09-30 | FEMALE | jane.sample@demo.example | +1-555-0102 |
| MRN-0003 | Sam | Tester | 1978-01-05 | OTHER | sam.tester@demo.example | +1-555-0103 |

### 2.7 Appointment (1, BOOKED)

| Field | Value |
|-------|-------|
| Patient | MRN-0001 (John Public) |
| Provider | DR-001 (Alice Cardwell) |
| Start → End | 2027-01-15 09:00:00 → 09:30:00 (30 min) |
| Status | BOOKED |
| Reason | Routine cardiology follow-up |

## 3. Data Model Reference

| Field | Type / Constraint | Notes |
|-------|-------------------|-------|
| `mrn` | VARCHAR(40), UNIQUE(tenant_id, mrn) | Format `MRN-####` |
| `first_name` / `last_name` | VARCHAR(100), NOT NULL | — |
| `date_of_birth` | DATE, NOT NULL | ISO 8601 `YYYY-MM-DD` |
| `gender` | VARCHAR(20), default UNKNOWN | MALE / FEMALE / OTHER / UNKNOWN |
| `email` | VARCHAR(200), nullable | `@demo.example` in fixtures |
| `phone` | VARCHAR(40), nullable | `+1-555-01xx` in fixtures |
| `patient.status` | VARCHAR(20), default ACTIVE | ACTIVE / INACTIVE |
| `appointment.status` | VARCHAR(20), default BOOKED | BOOKED / COMPLETED / CANCELLED / NOSHOW |
| `scheduled_start/end` | TIMESTAMP, NOT NULL | Duration = end − start |

## 4. Generator — `PatientFactory`

| Attribute | Value |
|-----------|-------|
| Class | `com.omiicare.qa.automation.core.generators.PatientFactory` |
| Path | `automation/src/test/java/com/omiicare/qa/automation/core/generators/PatientFactory.java` |
| Output | `SyntheticPatient(firstName, lastName, dateOfBirth, gender, email, phone)` (record) |
| Library | Datafaker `net.datafaker:datafaker` 2.4.1, `Faker(Locale.ENGLISH)` |

| Field | Generation rule |
|-------|-----------------|
| firstName / lastName | `faker.name().firstName()` / `.lastName()` |
| dateOfBirth | `LocalDate.now().minusYears(1..95)` → `yyyy-MM-dd` |
| gender | random of MALE / FEMALE / OTHER / UNKNOWN |
| email | `(first.last@demo.example)` lowercased, spaces stripped |
| phone | `faker.numerify("+1-555-0#-###")` |

## 5. Recommended Additional Datasets

| Set | Purpose | Reference |
|-----|---------|-----------|
| Boundary ages (DOB) | Min/max age, today, future DOB (invalid) | [Boundary Data Sets](BOUNDARY_DATA_SETS.md) §1 |
| Name length limits | 1-char and 100-char names; 101 (reject) | §2 |
| MRN format | Valid `MRN-####`, malformed, duplicate-in-tenant | §3 |
| Unicode / i18n names | Accents, CJK, RTL, emoji | §2 |
| Invalid emails | Missing `@`, double `@`, trailing dot | §6 |
| Appointment overlap | Back-to-back, overlapping, zero-duration | §4 |
| Pagination sizes | size 0 / 1 / 100 / 101 | §5 |

---

## Related Documents

- [Boundary Data Sets](BOUNDARY_DATA_SETS.md)
- [Execution Guide](../execution/EXECUTION_GUIDE.md)
- [Healthcare QA Guide](../knowledge-base/HEALTHCARE_QA_GUIDE.md)
- [docs/TEST_DATA_STRATEGY.md](../../docs/TEST_DATA_STRATEGY.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
