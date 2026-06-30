# Database Changelog

All notable schema and seed changes to the omiiCARE_QA database are recorded
here. Format follows [Keep a Changelog](https://keepachangelog.com/); this
database adheres to the Flyway versioning in
[MIGRATION_NAMING_STANDARDS.md](MIGRATION_NAMING_STANDARDS.md). Migrations are
forward-only — see [ROLLBACK_STRATEGY.md](ROLLBACK_STRATEGY.md).

Live migrations: `apps/backend/src/main/resources/db/migration` (schema) and
`apps/backend/src/main/resources/db/seed` (repeatable seeds).

## [Unreleased]

### Roadmap
- **M3 — Clinical & domain tables.** Upcoming `V2…`/`V3…` migrations introduce
  patient, provider, appointment, encounter, diagnosis, observation,
  prescription, lab/radiology, billing/insurance, consent, and access-log
  tables alongside the M3 backend entities, per
  [docs/DATABASE_BLUEPRINT.md](../../docs/DATABASE_BLUEPRINT.md) §3.

## [1.0] — 2026-06-30 — Milestone 2 (Baseline)

### Added — `V1__baseline_platform_schema.sql`

Baseline platform schema: tenancy, identity, RBAC, and audit. Portable across
H2 (PostgreSQL-compatibility mode) and PostgreSQL; owned by Flyway with
Hibernate in `validate` mode.

**Tenancy**

| Table | Purpose |
|-------|---------|
| `tenant` | Top-level customer / hospital network; unique `code`, lifecycle `status` |
| `hospital` | Organization (hospital) under a tenant; `timezone`, unique per `(tenant_id, code)` |
| `department` | Branch/department under a hospital; unique per `(hospital_id, code)` |

**Identity & RBAC**

| Table | Purpose |
|-------|---------|
| `app_user` | Tenant-scoped user; credentials, lockout, login tracking; unique username/email per tenant |
| `role` | Global RBAC role with unique `code` |
| `permission` | Granular permission with unique `code` |
| `user_role` | Join: users ↔ roles |
| `role_permission` | Join: roles ↔ permissions |

**Audit**

| Table | Purpose |
|-------|---------|
| `audit_log` | Append-style action log: actor, action, entity, before/after (`old_value`/`new_value`), correlation/request ids, IP |

**Indexes** — `idx_hospital_tenant`, `idx_department_tenant`,
`idx_user_tenant`, `idx_audit_tenant_time`, `idx_audit_entity` for the common
tenant-scoped and audit access paths.

### Added — `R__seed_reference_and_demo.sql` (repeatable)

Idempotent reference + synthetic demo seed (loaded only where
`flyway.locations` includes `classpath:db/seed`). All data is SYNTHETIC and
PHI-safe. See [SEED_DATA_CATALOG.md](SEED_DATA_CATALOG.md) for the full catalog.

- 12 RBAC roles, 9 representative permissions.
- DEMO tenant, DEMO-GEN hospital, 5 departments, and the `demo.admin` user.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Database Engineer | Initial (Milestone 2) |
