# Database Testing — omiiCARE_QA

> **Milestone 7 — Reliability & Data QA.** This module validates the persistence
> layer of the omiiCARE backend (Spring Boot + Flyway) against both the in-memory
> **H2** (PostgreSQL compatibility mode) test database and the production-grade
> **PostgreSQL 16** instance defined in the Docker stack.

## Purpose

Verify that the data layer enforces every guarantee the application depends on:
correct CRUD behaviour, referential integrity, constraint/unique-key enforcement,
index presence, clean Flyway migrations, transactional consistency, complete and
immutable audit records, and strict per-tenant scoping.

## Scope of Validation

| Area | What is verified |
|------|------------------|
| **CRUD** | INSERT/SELECT/UPDATE/DELETE round-trips per entity; defaults applied (`status`, `created_at`, `email_verified`); `updated_at` advances on UPDATE. |
| **Referential integrity** | Every FK has a parent row. No orphaned `appointment`, `department`, `provider`, `user_role`, `role_permission`. |
| **Constraints** | `NOT NULL` columns reject NULL; CHECK-equivalent app rules (status enums) validated at app layer. |
| **Unique keys** | `uq_tenant_code`, `uq_hospital_code`, `uq_department_code`, `uq_user_username`, `uq_user_email`, `uq_role_code`, `uq_permission_code`, `uq_patient_mrn`, `uq_provider_code` enforced. |
| **Indexes** | All `idx_*` from V1/V2 exist and are used by tenant-scoped queries. |
| **Migration / seed / rollback** | Flyway `flyway_schema_history` is clean (no failed rows), checksums match, expected tables/indexes present. Rollback is validated by re-running migrations on a fresh schema. |
| **Transactions & consistency** | Multi-row writes are atomic; failed transaction rolls back fully; no partial appointment without patient+provider. |
| **Audit table** | Every mutating action produces exactly one `audit_log` row with `action`, `entity_type`, `entity_id`, `correlation_id`; audit rows are append-only. |
| **Tenant scoping** | No tenant-scoped row references a parent in a different tenant; queries never leak rows across `tenant_id`. |

## Schema Under Test

Tables (Flyway V1 + V2): `tenant`, `hospital`, `department`, `app_user`, `role`,
`permission`, `user_role`, `role_permission`, `audit_log`, `patient`, `provider`,
`appointment`. See
[`apps/backend/src/main/resources/db/migration`](../../apps/backend/src/main/resources/db/migration).

## Artifacts

| File | Purpose |
|------|---------|
| [`sql/integrity-checks.sql`](sql/integrity-checks.sql) | Portable assertion queries — each SELECT returns **0 rows when healthy**. |
| [`sql/migration-validation.sql`](sql/migration-validation.sql) | Flyway history + table/index existence checks. |
| [`DATA_INTEGRITY_TEST_CASES.md`](DATA_INTEGRITY_TEST_CASES.md) | Manual/automatable DB test cases `DB-TC-*`. |

## How to Run

### Against PostgreSQL (Docker stack)

```bash
# 1. Bring up Postgres (and friends)
docker compose -f infrastructure/docker/docker-compose.yml up -d postgres

# 2. Apply migrations (Flyway via the backend or Maven plugin)
mvn -q -pl apps/backend flyway:migrate

# 3. Run integrity assertions — any output means a violation
docker exec -i omiicare-postgres \
  psql -U omiicare -d omiicare -v ON_ERROR_STOP=1 \
  < quality/database-testing/sql/integrity-checks.sql

# 4. Validate migration state
docker exec -i omiicare-postgres \
  psql -U omiicare -d omiicare \
  < quality/database-testing/sql/migration-validation.sql
```

### Against H2 (test profile, in-memory)

H2 runs automatically inside the backend integration tests
(`@DataJpaTest` / `@SpringBootTest` with `spring.flyway.enabled=true`). The same
SQL files are portable to H2 in PostgreSQL mode and can be loaded via the H2
console or a JDBC test fixture:

```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:omiicare;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
spring.flyway.enabled=true
spring.jpa.hibernate.ddl-auto=validate
```

> **Interpretation rule:** for `integrity-checks.sql`, a healthy database returns
> **zero rows** from every assertion query. Any returned row is a defect — the
> `assertion` column names the violated invariant.

## Portability Notes

- SQL uses ANSI joins, `EXISTS`/`NOT EXISTS`, and standard aggregates only — no
  vendor-specific syntax — so it runs unchanged on H2 (PostgreSQL mode) and
  PostgreSQL 16.
- `information_schema` is used for structural checks (tables/columns/indexes are
  read from `information_schema.tables` / `.columns` / `.statistics` or
  `pg_indexes` where noted), both of which H2 and PostgreSQL expose.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Reliability Engineer | Initial (Milestone 7) |
