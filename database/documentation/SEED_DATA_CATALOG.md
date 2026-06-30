# Seed Data Catalog

> **Catalog of all reference and demo seed data shipped with omiiCARE_QA.**
> Documents exactly what `R__seed_reference_and_demo.sql` loads, confirms it is
> synthetic / PHI-safe, and records the M2 vs M3 seed split.

## Purpose

Give QA, backend, and demo users a single authoritative inventory of seeded
rows — what exists, why, and that none of it is real PHI — so tests and demos
can rely on known fixtures.

## Scope

- **In scope:** roles, permissions, the demo tenant/hospital/departments, the
  demo admin user, the PHI-safe guarantee, and the M2/M3 seed boundary.
- **Out of scope:** clinical/billing seed data (M3), and the migration
  mechanics (see [MIGRATION_NAMING_STANDARDS.md](MIGRATION_NAMING_STANDARDS.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Database Engineer | Keep this catalog in sync with `R__seed_reference_and_demo.sql` |
| QA Engineer | Use these fixtures as the known baseline for DB tests |
| Healthcare Architect | Confirm seed data stays synthetic and PHI-safe |

---

## 1. Source & Safety

- **Source file:** `apps/backend/src/main/resources/db/seed/R__seed_reference_and_demo.sql`
- **Loaded in profiles:** dev, local, docker, test, qa (where `flyway.locations`
  includes `classpath:db/seed`). Excluded from production-like profiles.
- **PHI-safe:** every row is **SYNTHETIC**. No real patient information. Emails
  use the `.example` domain; the demo user's `password_hash` is a non-functional
  placeholder (`NOT_SET_PENDING_M3_AUTH`) until the M3 auth module sets a real
  bcrypt hash.
- **Idempotent:** every `INSERT` is guarded by `WHERE NOT EXISTS`, so re-running
  the repeatable migration never duplicates rows.

## 2. Roles (12)

| # | Code | Name | Description |
|---|------|------|-------------|
| 1 | `SUPER_ADMIN` | Super Admin | Platform-wide administration across tenants |
| 2 | `HOSPITAL_ADMIN` | Hospital Admin | Administration within a hospital/tenant |
| 3 | `DOCTOR` | Doctor | Clinical provider |
| 4 | `NURSE` | Nurse | Nursing staff |
| 5 | `RECEPTIONIST` | Receptionist | Front-desk and scheduling |
| 6 | `LAB_TECHNICIAN` | Lab Technician | Laboratory operations |
| 7 | `RADIOLOGIST` | Radiologist | Radiology operations |
| 8 | `PHARMACIST` | Pharmacist | Pharmacy operations |
| 9 | `BILLING_STAFF` | Billing Staff | Billing and invoicing |
| 10 | `INSURANCE_STAFF` | Insurance Staff | Insurance verification and claims |
| 11 | `PATIENT` | Patient | Patient self-service |
| 12 | `AUDITOR` | Auditor | Read-only audit and compliance access |

## 3. Permissions (9)

> Representative core set; expanded with endpoint-level permissions in M3.

| # | Code | Description |
|---|------|-------------|
| 1 | `patient:read` | View patient records |
| 2 | `patient:write` | Create/update patient records |
| 3 | `appointment:read` | View appointments |
| 4 | `appointment:write` | Create/update appointments |
| 5 | `prescription:write` | Create prescriptions |
| 6 | `billing:read` | View billing |
| 7 | `billing:write` | Create/update billing |
| 8 | `audit:read` | View audit logs |
| 9 | `admin:manage` | Manage tenant configuration and users |

## 4. Demo Tenant & Organization

| Entity | Code | Name | Notes |
|--------|------|------|-------|
| Tenant | `DEMO` | omiiCARE Demo Health Network | status `ACTIVE` |
| Hospital | `DEMO-GEN` | omiiCARE General Hospital | timezone `UTC`, status `ACTIVE` |

### Departments (5, under DEMO-GEN)

| # | Code | Name |
|---|------|------|
| 1 | `CARD` | Cardiology |
| 2 | `RADI` | Radiology |
| 3 | `LABM` | Laboratory Medicine |
| 4 | `PHAR` | Pharmacy |
| 5 | `EMER` | Emergency |

## 5. Demo User

| Field | Value |
|-------|-------|
| Username | `demo.admin` |
| Email | `demo.admin@omiicare.example` |
| Full name | Demo Administrator |
| Status | `ACTIVE`, `email_verified = TRUE` |
| Password hash | `NOT_SET_PENDING_M3_AUTH` (placeholder; real bcrypt set in M3) |

## 6. M2 vs M3 Seed Split

| Wave | Seeds | Status |
|------|-------|--------|
| **M2 (this catalog)** | Reference RBAC (roles, permissions) + demo tenant/hospital/departments/admin | Shipped |
| **M3 (roadmap)** | Synthetic patients, providers, appointments, encounters, clinical/lab/radiology, billing/insurance, and audit samples | Planned per [docs/DATABASE_BLUEPRINT.md](../../docs/DATABASE_BLUEPRINT.md) §6 |

M2 seeds the structural reference and a single demo org so the platform is
usable and testable; clinical and transactional synthetic data arrive with the
M3 entities.

## 7. References

- Source: `apps/backend/src/main/resources/db/seed/R__seed_reference_and_demo.sql`
- [DATABASE_CHANGELOG.md](DATABASE_CHANGELOG.md)
- [docs/DATABASE_BLUEPRINT.md](../../docs/DATABASE_BLUEPRINT.md) §6 Seed plan.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Database Engineer | Initial (Milestone 2) |
