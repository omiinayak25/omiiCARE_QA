# Data Integrity Test Cases — omiiCARE_QA

> **Milestone 7 — Reliability & Data QA.** DB-level test cases for the Flyway
> schema (V1 baseline + V2 clinical core). Run against H2 (test) or PostgreSQL
> (Docker). SQL assertions referenced below live in
> [`sql/integrity-checks.sql`](sql/integrity-checks.sql) and
> [`sql/migration-validation.sql`](sql/migration-validation.sql).

**Legend** — Pri: P0 critical / P1 high / P2 medium. Type: CRUD, RI (referential
integrity), UQ (unique), CON (constraint), IDX (index), MIG (migration), TX
(transaction), AUD (audit), TEN (tenant scoping).

| ID | Pri | Type | Title | Steps | Expected |
|----|-----|------|-------|-------|----------|
| DB-TC-001 | P1 | CRUD | Patient insert applies defaults | INSERT patient with only required cols (tenant_id, mrn, first/last name, dob). | Row created; `gender='UNKNOWN'`, `status='ACTIVE'`, `created_at`/`updated_at` set to CURRENT_TIMESTAMP. |
| DB-TC-002 | P2 | CRUD | Patient update advances updated_at | UPDATE patient SET phone where id=X; compare updated_at before/after. | `updated_at` >= previous value; targeted columns changed; others unchanged. |
| DB-TC-003 | P1 | CRUD | Patient soft-delete via status | UPDATE patient SET status='INACTIVE'. | Row remains; status changed; referencing appointments unaffected. |
| DB-TC-004 | P0 | RI | Appointment requires existing patient | INSERT appointment with patient_id not in patient. | INSERT rejected by `fk_appt_patient`. `orphan_appointment_patient` returns 0 rows. |
| DB-TC-005 | P0 | RI | Appointment requires existing provider | INSERT appointment with non-existent provider_id. | Rejected by `fk_appt_provider`. `orphan_appointment_provider` returns 0 rows. |
| DB-TC-006 | P0 | RI | Cannot delete referenced patient | DELETE patient that has appointments. | DELETE rejected by FK (RESTRICT default); patient retained. |
| DB-TC-007 | P1 | RI | Department requires existing hospital | INSERT department with bad hospital_id. | Rejected by `fk_department_hospital`. |
| DB-TC-008 | P1 | RI | RBAC join integrity | INSERT user_role with non-existent role_id. | Rejected by `fk_user_role_role`. `user_role_dangling_role` returns 0 rows. |
| DB-TC-009 | P0 | UQ | Duplicate MRN per tenant rejected | INSERT two patients, same tenant_id + mrn. | Second INSERT rejected by `uq_patient_mrn`. `duplicate_mrn_per_tenant` returns 0 rows. |
| DB-TC-010 | P1 | UQ | Same MRN allowed across tenants | INSERT patients with identical mrn but different tenant_id. | Both succeed (MRN is unique per tenant, not global). |
| DB-TC-011 | P1 | UQ | Duplicate username/email per tenant rejected | INSERT duplicate (tenant_id, username) and (tenant_id, email). | Rejected by `uq_user_username` / `uq_user_email`. |
| DB-TC-012 | P1 | UQ | Duplicate provider code per tenant rejected | INSERT two providers same (tenant_id, code). | Rejected by `uq_provider_code`. |
| DB-TC-013 | P0 | CON | NOT NULL enforced on patient required cols | INSERT patient with NULL last_name. | Rejected by NOT NULL constraint. |
| DB-TC-014 | P2 | CON | Appointment end-before-start guard (app layer) | Attempt to book appointment with scheduled_end < scheduled_start. | App rejects (400). `appointment_end_before_start` returns 0 rows. |
| DB-TC-015 | P2 | CON | Status enum drift detection | Direct-SQL set patient.status='FOO'. | `patient_unknown_status` returns the row → defect flagged. |
| DB-TC-016 | P0 | TEN | No cross-tenant appointment | Build appointment whose patient and provider belong to different tenants. | App rejects; `appointment_patient_tenant_mismatch` and `..._provider_..` return 0 rows. |
| DB-TC-017 | P0 | TEN | Department tenant matches hospital | Verify department.tenant_id == parent hospital.tenant_id for all rows. | `department_tenant_mismatch` returns 0 rows. |
| DB-TC-018 | P0 | TEN | Tenant-scoped query isolation | Query patients for tenant A. | No tenant B rows returned; result set fully within tenant A. |
| DB-TC-019 | P1 | IDX | All expected indexes present | Run step 7 of migration-validation.sql. | Zero missing-index rows; all `idx_*` exist. |
| DB-TC-020 | P2 | IDX | Tenant query uses index | EXPLAIN a `WHERE tenant_id=? AND last_name=?` query on patient. | Plan uses `idx_patient_last_name` (or `idx_patient_tenant`), not a full scan. |
| DB-TC-021 | P0 | MIG | Flyway history is clean | Run migration-validation.sql steps 1–4. | V1 and V2 present with success=true; no failed/duplicate rows. |
| DB-TC-022 | P1 | MIG | Expected tables exist | Run step 5. | Zero missing-table rows (all 12 tables present). |
| DB-TC-023 | P1 | MIG | Checksums unchanged | Re-run `flyway:validate` against applied schema. | Validate passes; no checksum mismatch (migrations are immutable). |
| DB-TC-024 | P1 | MIG | Fresh-DB re-apply (rollback proxy) | Drop schema, re-run `flyway:migrate` on empty DB. | All migrations re-apply cleanly; schema identical; integrity-checks all 0 rows. |
| DB-TC-025 | P0 | TX | Atomic multi-row write rolls back | In one transaction insert patient then force error before commit. | Neither row persists; no partial patient. |
| DB-TC-026 | P1 | TX | Concurrent MRN insert race | Two transactions insert same (tenant, mrn) concurrently. | Exactly one commits; the other fails on unique constraint. |
| DB-TC-027 | P0 | AUD | Mutation writes one audit row | Create a patient via API. | Exactly one `audit_log` row with action, entity_type='Patient', entity_id, correlation_id populated. |
| DB-TC-028 | P0 | AUD | Audit completeness | Run audit assertion (step 9). | `audit_missing_required_fields` and `audit_bad_timestamp` return 0 rows. |
| DB-TC-029 | P1 | AUD | Audit is append-only | Attempt UPDATE/DELETE on audit_log via app path. | No app path mutates existing audit rows; history preserved. |
| DB-TC-030 | P2 | AUD | Audit correlation links to request | Issue request with X-Correlation-Id header; create entity. | audit_log.correlation_id matches the request correlation id. |

## Execution

```bash
# Postgres
docker exec -i omiicare-postgres psql -U omiicare -d omiicare \
  -v ON_ERROR_STOP=1 < quality/database-testing/sql/integrity-checks.sql
docker exec -i omiicare-postgres psql -U omiicare -d omiicare \
  < quality/database-testing/sql/migration-validation.sql
```

Healthy result: every assertion query returns **0 rows** (FK-coverage and history
overview queries are informational and return rows by design).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Reliability Engineer | Initial (Milestone 7) |
