# Database Blueprint

> **Normalized schema blueprint for omiiCARE_QA.** This document defines the
> target relational model, tenancy and audit strategy, indexing and constraint
> conventions, Flyway migration standards, PHI-safe seed plan, and the
> standards-mapping tables — all *designed in Milestone 1, implemented in
> Milestone 2/3*. No DDL is created in Milestone 1. Facts defer to
> [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

Give the database (M2) and backend (M3) teams an unambiguous, normalized schema
blueprint that is portable across the profile-driven hybrid database (H2 in
`dev`/`test`, PostgreSQL elsewhere) and that natively supports multi-tenancy,
auditability, and healthcare standards mapping.

## Scope

- **In scope:** entity/attribute/relationship overview, tenancy columns, audit
  tables, index/constraint/unique-key strategy, Flyway naming and rollback,
  PHI-safe seed plan, FHIR/HL7/ICD-10/CPT/LOINC/SNOMED mapping tables.
- **Out of scope:** physical tuning per cloud provider, partitioning at scale,
  and any real DDL (Milestone 2+).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Database Engineer (M2) | Author Flyway migrations matching this blueprint |
| Backend Engineer (M3) | Map JPA entities 1:1 to these tables |
| QA Architect | Verify constraints/seeds via DB tests (M7) |
| Healthcare Architect | Keep standards-mapping tables clinically correct |

---

## 1. Design Principles

- **Third normal form** as the baseline; controlled denormalization only with a
  recorded reason.
- **Portability first:** use types available in both H2 and PostgreSQL. Prefer
  `UUID` primary keys, `TIMESTAMP WITH TIME ZONE` (`timestamptz`), `NUMERIC` for
  money, `VARCHAR` with explicit lengths, and `BOOLEAN`. Avoid PostgreSQL-only
  types in core tables; isolate any such use behind repeatable views.
- **Profile-driven, code-free DB switch:** the same migrations run on H2 and
  PostgreSQL; differences are handled by Flyway placeholders and `H2`/`postgresql`
  vendor-specific scripts where unavoidable — never by application code (see
  [ARCHITECTURE.md](../ARCHITECTURE.md) §8).
- **Surrogate keys:** every table has a UUID surrogate PK; natural keys become
  unique constraints.
- **Soft delete + audit:** clinical/identity/financial rows are never hard
  deleted (BR-IDENT-005); they carry status and audit columns.

## 2. Standard Columns (every tenant-scoped table)

| Column | Type | Notes |
|--------|------|-------|
| `id` | UUID | Primary key (surrogate) |
| `tenant_id` | UUID | FK → `tenant.id`; mandatory; filtered on every query (BR-TENANT-001) |
| `branch_id` | UUID | FK → `branch.id`; nullable for tenant-level rows |
| `created_at` | timestamptz | Set on insert |
| `created_by` | UUID | Actor user id |
| `updated_at` | timestamptz | Set on update |
| `updated_by` | UUID | Actor user id |
| `version` | BIGINT | Optimistic locking (`@Version`) |
| `status` | VARCHAR(32) | Lifecycle/soft-delete state where applicable |

## 3. Entity Catalogue (ER Overview)

> Cardinality notation: `1—*` one-to-many, `*—*` many-to-many (via join table),
> `1—1` one-to-one.

### Tenancy & Security

| Entity | Key attributes | Relationships |
|--------|----------------|---------------|
| `tenant` | name, legal_name, status | `1—*` hospital_network |
| `hospital_network` | tenant_id, name | `1—*` branch |
| `branch` | network_id, name, address, timezone | `1—*` department, `1—*` user |
| `department` | branch_id, name, type | `1—*` provider |
| `app_user` | tenant_id, email, password_hash, status | `*—*` role |
| `role` | tenant_id (nullable=global), name | `*—*` permission |
| `permission` | code, description | `*—*` role |
| `user_role` | user_id, role_id | join (`app_user` `*—*` `role`) |
| `role_permission` | role_id, permission_id | join (`role` `*—*` `permission`) |

### Identity & Clinical

| Entity | Key attributes | Relationships |
|--------|----------------|---------------|
| `patient` | mrn, family_name, given_name, dob, gender, status | `1—*` appointment, encounter, coverage, allergy |
| `provider` | user_id, department_id, npi_like, specialty | `1—*` appointment, encounter |
| `patient_allergy` | patient_id, substance_code, reaction, severity | belongs to `patient` |
| `appointment` | patient_id, provider_id, branch_id, visit_type, start_at, end_at, status | `1—1` encounter (optional) |
| `encounter` | patient_id, provider_id, appointment_id, class, start_at, end_at, status | `1—*` diagnosis, observation, prescription, lab_order, rad_order |
| `diagnosis` | encounter_id, icd10_code, rank, type | belongs to `encounter` |
| `observation` | encounter_id, loinc_code, value, unit, ref_range, abnormal_flag | belongs to `encounter` |
| `clinical_note` | encounter_id, author_id, body, signed_at | append-only (BR-ENC-004) |
| `vital_sign` | encounter_id, type, value, unit, recorded_at | belongs to `encounter` |

### Medications, Labs, Radiology

| Entity | Key attributes | Relationships |
|--------|----------------|---------------|
| `medication` | code (RxNorm-like), name, form, is_controlled, schedule | reference data |
| `prescription` | encounter_id, patient_id, prescriber_id, medication_id, dose, route, frequency, duration, refills_authorized, status | `1—*` dispense |
| `dispense` | prescription_id, pharmacist_id, quantity, dispensed_at | belongs to `prescription` |
| `lab_order` | encounter_id, ordering_provider_id, loinc_code, priority, status | `1—*` lab_result |
| `lab_result` | lab_order_id, technician_id, loinc_code, value, unit, ref_range, abnormal_flag, verified_at | belongs to `lab_order` |
| `rad_order` | encounter_id, ordering_provider_id, modality, cpt_code, indication, status | `1—1` rad_report |
| `rad_report` | rad_order_id, radiologist_id, findings, snomed_code, finalized_at | belongs to `rad_order` |

### Billing & Insurance

| Entity | Key attributes | Relationships |
|--------|----------------|---------------|
| `invoice` | patient_id, encounter_id, total, balance, status | `1—*` invoice_line, `1—*` payment, `1—*` claim |
| `invoice_line` | invoice_id, cpt_code, icd10_code, description, qty, unit_price, amount | belongs to `invoice` |
| `payment` | invoice_id, amount, method, settled_at | `1—*` refund |
| `refund` | payment_id, amount, reason, refunded_at | belongs to `payment` |
| `payer` | name, plan_catalog | reference data |
| `coverage` | patient_id, payer_id, plan, member_id, relationship, rank, effective_from, effective_to | `1—*` claim |
| `claim` | invoice_id, coverage_id, status, submitted_at | `1—*` claim_line |
| `claim_line` | claim_id, cpt_code, icd10_code, amount, adjudication_status, reason_code | belongs to `claim` |

### Consent, Notification, Audit

| Entity | Key attributes | Relationships |
|--------|----------------|---------------|
| `consent` | patient_id, scope, status, granted_at, withdrawn_at | belongs to `patient` |
| `notification` | recipient_id, channel, template, status, sent_at | delivery log (BR-NOTIF-004) |
| `audit_event` | actor_id, action, entity_type, entity_id, before, after, correlation_id, occurred_at | append-only (BR-AUDIT-001) |
| `access_log` | actor_id, patient_id, purpose_of_use, outcome, occurred_at | PHI read log (BR-AUDIT-002) |

### ER Overview (ASCII)

```
tenant 1─* hospital_network 1─* branch 1─* department 1─* provider
branch 1─* app_user *─* role *─* permission
patient 1─* appointment 1─1 encounter 1─* diagnosis
                              encounter 1─* prescription 1─* dispense
                              encounter 1─* lab_order   1─* lab_result
                              encounter 1─* rad_order   1─1 rad_report
patient 1─* coverage *─ payer ;  encounter 1─* invoice 1─* invoice_line
invoice 1─* payment 1─* refund ;  invoice 1─* claim 1─* claim_line
patient 1─* consent ;  (audit_event, access_log, notification span all)
```

## 4. Indexing, Constraints & Unique Keys

| Concern | Strategy |
|---------|----------|
| Primary keys | UUID surrogate on every table |
| Tenancy index | Composite index leading with `tenant_id` on every tenant-scoped table; most lookups also include `branch_id` |
| Foreign keys | Declared with `ON DELETE RESTRICT` (soft-delete model; no cascading data loss) |
| Unique — MRN | `UNIQUE (tenant_id, mrn)` (BR-IDENT-001) |
| Unique — user | `UNIQUE (tenant_id, email)`; global Super Admin via separate constraint (BR-TENANT-004) |
| Unique — coverage | `UNIQUE (patient_id, payer_id, member_id, effective_from)` |
| No-overlap (appt) | Enforced in application + integration test; exclusion-constraint pattern documented for PostgreSQL, app-checked on H2 (BR-APPT-003) |
| Check constraints | `dob <= current_date`; `invoice.total >= 0`; `payment.amount > 0`; status values constrained to enumerated sets |
| Hot-path indexes | `appointment(provider_id, start_at)`, `encounter(patient_id, status)`, `lab_result(lab_order_id)`, `audit_event(tenant_id, occurred_at)`, `access_log(patient_id, occurred_at)` |
| Optimistic locking | `version` column on mutable entities |

## 5. Flyway Migration Standard

| Type | Naming | Use |
|------|--------|-----|
| Versioned | `V<major>_<minor>__<snake_desc>.sql` (e.g. `V1_0__baseline_schema.sql`) | Forward, immutable, ordered schema/data changes |
| Repeatable | `R__<snake_desc>.sql` (e.g. `R__views_reporting.sql`) | Views, functions, idempotent reference refreshes; re-run on checksum change |
| Vendor split | `V<n>__<desc>.sql` shared; `db/migration/{h2,postgresql}/` for the rare vendor-specific statement | Keep DB-portability while isolating divergence |

Rules:
- One logical change per migration; migrations are **never edited after merge**
  (a new migration corrects a prior one).
- Placeholders (`${tenant_seed}`) feed environment-specific values without code.
- Migrations run automatically on startup in `dev`/`docker`; gated in
  `qa`/`stage`/`prod` (validate, then migrate via release pipeline).
- Layout: `database/src/main/resources/db/migration/`.

## 6. Seed Data Plan (PHI-safe)

> All seed data is synthetic and PHI-safe (BR-CONS-005). Real PHI is never used.

| Domain | Repeatable/Versioned | Representative volume |
|--------|----------------------|-----------------------|
| Roles & permissions | `R__seed_rbac.sql` | 12 roles, full permission catalogue, role_permission map |
| Tenants & networks | `V*__seed_tenancy.sql` | 2 tenants, 3 networks, 5 branches, departments |
| Users & providers | `V*__seed_users.sql` | 1 user per role per branch; synthetic emails `@example.test` |
| Patients | `V*__seed_patients.sql` | ~50 synthetic patients (faker-style names, valid past DOBs, fake MRNs) |
| Appointments | `V*__seed_appointments.sql` | ~150 spanning past/present/future, mixed statuses |
| Encounters & clinical | `V*__seed_encounters.sql` | encounters with ICD-10 diagnoses, LOINC observations, vitals |
| Medications & Rx | `V*__seed_medications.sql` | reference meds incl. controlled; sample prescriptions + dispenses |
| Labs & radiology | `V*__seed_diagnostics.sql` | LOINC lab orders/results, CPT radiology orders/reports |
| Insurance | `V*__seed_insurance.sql` | payers, plans, coverages (valid/expired) |
| Billing | `V*__seed_billing.sql` | invoices/lines/payments/claims across statuses |
| Audit logs | `R__seed_audit_samples.sql` | sample audit/access events for query tests |

Seeds are loaded in `dev`/`docker`/`demo`/`training`; production-like profiles
load only structural reference data unless a curated dataset is selected.

## 7. Standards-Mapping Tables

> Additive mapping tables let new standards arrive without refactoring core
> tables (see [ARCHITECTURE.md](../ARCHITECTURE.md) §8). Each is reference data.

| Table | Columns | Code system URI |
|-------|---------|-----------------|
| `code_icd10` | code, display, category | `http://hl7.org/fhir/sid/icd-10` |
| `code_cpt` | code, display, section | CPT (AMA; licensed terminology) |
| `code_loinc` | code, display, component, unit | `http://loinc.org` |
| `code_snomed` | code, display, semantic_tag | `http://snomed.info/sct` |
| `code_rxnorm` | code, display | `http://www.nlm.nih.gov/research/umls/rxnorm` |
| `fhir_resource_map` | internal_entity, fhir_resource, notes | maps `patient`→`Patient`, etc. (see [FHIR_GUIDE.md](FHIR_GUIDE.md)) |
| `hl7_segment_map` | message_type, segment, internal_field | maps ADT/ORU fields (see [HL7_GUIDE.md](HL7_GUIDE.md)) |

Coded columns on clinical tables (`diagnosis.icd10_code`, `observation.loinc_code`,
`rad_order.cpt_code`, etc.) FK to these reference tables to guarantee valid codes.

## 8. Rollback & Changelog Strategy

| Aspect | Approach |
|--------|----------|
| Forward-only by default | Flyway migrations are forward-only; correction = new migration |
| Undo migrations | Paired `U<version>__<desc>.sql` scripts authored for high-risk DDL where Flyway Teams undo is available; otherwise a documented manual rollback runbook accompanies the migration |
| Pre-deploy snapshot | `qa`/`stage`/`prod` take a DB backup/snapshot before migrate (release pipeline, M8) |
| Validation | `flyway validate` runs in CI; checksum drift fails the build |
| Changelog | Schema changes recorded in [CHANGELOG.md](../CHANGELOG.md) under the milestone; each migration header comments the rule/ADR it satisfies |
| Repeatable safety | Repeatable scripts must be idempotent (`CREATE OR REPLACE`) so re-runs are safe |

## 9. Profile-Driven H2 ↔ PostgreSQL Note

The same Flyway migration set runs unchanged on both engines. H2 runs in
PostgreSQL-compatibility mode for `dev`/`test`; PostgreSQL serves
`local`/`docker`/`qa`/`stage`/`prod`. Vendor-specific constructs (e.g. PostgreSQL
exclusion constraints for appointment overlap) live in `db/migration/postgresql/`
with an application-level equivalent guaranteeing the same invariant on H2. No
application code branches on the database engine — switching is configuration only
(active Spring profile + datasource properties).

## Examples

- **Migration sequence:** `V1_0__baseline_schema.sql` (tables + constraints) →
  `V1_1__standards_code_tables.sql` → `R__seed_rbac.sql` → `V1_2__seed_core.sql`.
- **Tenancy filter:** a query for appointments always resolves through
  `WHERE tenant_id = :ctxTenant`, served by the leading composite index.

## Future Enhancements

- Time-series partitioning of `audit_event`/`access_log` at scale (post-1.0).
- Optional per-tenant schema or database isolation (v2.0 scale-out).
- Generated ER diagram (PlantUML/dbml) committed and refreshed in CI.

## Dependencies

- Facts/profiles: [PROJECT_METADATA.md](PROJECT_METADATA.md).
- Rules enforced by constraints: [BUSINESS_RULES.md](BUSINESS_RULES.md).
- Standards detail: [FHIR_GUIDE.md](FHIR_GUIDE.md), [HL7_GUIDE.md](HL7_GUIDE.md).
- API surface over this schema: [API_BLUEPRINT.md](API_BLUEPRINT.md).

## References

- [ARCHITECTURE.md](../ARCHITECTURE.md) §8 Data architecture.
- Flyway documentation (migration naming, repeatable, undo).
- Healthcare code systems: ICD-10, CPT, LOINC, SNOMED CT, RxNorm.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Senior Backend Engineer | Initial normalized schema blueprint (Milestone 1) |
