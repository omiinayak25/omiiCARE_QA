-- =============================================================================
-- integrity-checks.sql  —  omiiCARE_QA referential-integrity & constraint assertions
-- -----------------------------------------------------------------------------
-- Each query is an ASSERTION: it returns ZERO ROWS when the database is healthy.
-- Any returned row is a defect; the `assertion` column names the violated rule.
-- Portable across H2 (MODE=PostgreSQL) and PostgreSQL 16 — ANSI SQL only.
-- Run order is independent; queries do not mutate data.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- 1. Orphaned appointments — appointment must reference an existing patient,
--    provider, and tenant.
-- ---------------------------------------------------------------------------
SELECT 'orphan_appointment_patient' AS assertion, a.id
FROM appointment a
WHERE NOT EXISTS (SELECT 1 FROM patient p WHERE p.id = a.patient_id);

SELECT 'orphan_appointment_provider' AS assertion, a.id
FROM appointment a
WHERE NOT EXISTS (SELECT 1 FROM provider pr WHERE pr.id = a.provider_id);

SELECT 'orphan_appointment_tenant' AS assertion, a.id
FROM appointment a
WHERE NOT EXISTS (SELECT 1 FROM tenant t WHERE t.id = a.tenant_id);

-- ---------------------------------------------------------------------------
-- 2. Patients / providers / users / hospitals / departments without a tenant.
--    tenant_id is NOT NULL in schema; this catches dangling FK references.
-- ---------------------------------------------------------------------------
SELECT 'patient_without_tenant' AS assertion, p.id
FROM patient p
WHERE p.tenant_id IS NULL
   OR NOT EXISTS (SELECT 1 FROM tenant t WHERE t.id = p.tenant_id);

SELECT 'provider_without_tenant' AS assertion, pr.id
FROM provider pr
WHERE pr.tenant_id IS NULL
   OR NOT EXISTS (SELECT 1 FROM tenant t WHERE t.id = pr.tenant_id);

SELECT 'user_without_tenant' AS assertion, u.id
FROM app_user u
WHERE u.tenant_id IS NULL
   OR NOT EXISTS (SELECT 1 FROM tenant t WHERE t.id = u.tenant_id);

SELECT 'hospital_without_tenant' AS assertion, h.id
FROM hospital h
WHERE NOT EXISTS (SELECT 1 FROM tenant t WHERE t.id = h.tenant_id);

SELECT 'department_without_hospital' AS assertion, d.id
FROM department d
WHERE NOT EXISTS (SELECT 1 FROM hospital h WHERE h.id = d.hospital_id);

-- ---------------------------------------------------------------------------
-- 3. Tenant-scoping consistency — a child row's tenant must match its parent's.
--    department.tenant_id must equal its hospital's tenant_id.
--    appointment.tenant_id must equal both patient's and provider's tenant_id.
--    A mismatch is a cross-tenant data leak.
-- ---------------------------------------------------------------------------
SELECT 'department_tenant_mismatch' AS assertion, d.id
FROM department d
JOIN hospital h ON h.id = d.hospital_id
WHERE d.tenant_id <> h.tenant_id;

SELECT 'appointment_patient_tenant_mismatch' AS assertion, a.id
FROM appointment a
JOIN patient p ON p.id = a.patient_id
WHERE a.tenant_id <> p.tenant_id;

SELECT 'appointment_provider_tenant_mismatch' AS assertion, a.id
FROM appointment a
JOIN provider pr ON pr.id = a.provider_id
WHERE a.tenant_id <> pr.tenant_id;

-- ---------------------------------------------------------------------------
-- 4. Duplicate MRN per tenant — uq_patient_mrn (tenant_id, mrn) must hold.
--    This catches any duplicate that bypassed the constraint (e.g. via a
--    migration error or a disabled constraint).
-- ---------------------------------------------------------------------------
SELECT 'duplicate_mrn_per_tenant' AS assertion, p.tenant_id, p.mrn, COUNT(*) AS cnt
FROM patient p
GROUP BY p.tenant_id, p.mrn
HAVING COUNT(*) > 1;

-- Duplicate provider code per tenant — uq_provider_code (tenant_id, code).
SELECT 'duplicate_provider_code_per_tenant' AS assertion, pr.tenant_id, pr.code, COUNT(*) AS cnt
FROM provider pr
GROUP BY pr.tenant_id, pr.code
HAVING COUNT(*) > 1;

-- Duplicate username / email per tenant — uq_user_username / uq_user_email.
SELECT 'duplicate_username_per_tenant' AS assertion, u.tenant_id, u.username, COUNT(*) AS cnt
FROM app_user u
GROUP BY u.tenant_id, u.username
HAVING COUNT(*) > 1;

SELECT 'duplicate_user_email_per_tenant' AS assertion, u.tenant_id, u.email, COUNT(*) AS cnt
FROM app_user u
GROUP BY u.tenant_id, u.email
HAVING COUNT(*) > 1;

-- Global unique codes — tenant.code, role.code, permission.code.
SELECT 'duplicate_tenant_code' AS assertion, t.code, COUNT(*) AS cnt
FROM tenant t GROUP BY t.code HAVING COUNT(*) > 1;

SELECT 'duplicate_role_code' AS assertion, r.code, COUNT(*) AS cnt
FROM role r GROUP BY r.code HAVING COUNT(*) > 1;

SELECT 'duplicate_permission_code' AS assertion, pm.code, COUNT(*) AS cnt
FROM permission pm GROUP BY pm.code HAVING COUNT(*) > 1;

-- ---------------------------------------------------------------------------
-- 5. RBAC join-table integrity — no dangling user_role / role_permission rows.
-- ---------------------------------------------------------------------------
SELECT 'user_role_dangling_user' AS assertion, ur.user_id, ur.role_id
FROM user_role ur
WHERE NOT EXISTS (SELECT 1 FROM app_user u WHERE u.id = ur.user_id);

SELECT 'user_role_dangling_role' AS assertion, ur.user_id, ur.role_id
FROM user_role ur
WHERE NOT EXISTS (SELECT 1 FROM role r WHERE r.id = ur.role_id);

SELECT 'role_permission_dangling_role' AS assertion, rp.role_id, rp.permission_id
FROM role_permission rp
WHERE NOT EXISTS (SELECT 1 FROM role r WHERE r.id = rp.role_id);

SELECT 'role_permission_dangling_permission' AS assertion, rp.role_id, rp.permission_id
FROM role_permission rp
WHERE NOT EXISTS (SELECT 1 FROM permission pm WHERE pm.id = rp.permission_id);

-- ---------------------------------------------------------------------------
-- 6. NOT NULL / required-field completeness (defensive — schema enforces these,
--    but a bad backfill can leave business-required fields blank).
-- ---------------------------------------------------------------------------
SELECT 'patient_missing_required' AS assertion, p.id
FROM patient p
WHERE p.mrn IS NULL OR p.first_name IS NULL OR p.last_name IS NULL
   OR p.date_of_birth IS NULL OR p.gender IS NULL OR p.status IS NULL;

SELECT 'appointment_required_times' AS assertion, a.id
FROM appointment a
WHERE a.scheduled_start IS NULL OR a.scheduled_end IS NULL;

-- ---------------------------------------------------------------------------
-- 7. Domain/temporal sanity — appointment must not end before it starts;
--    patient must not be born in the future.
-- ---------------------------------------------------------------------------
SELECT 'appointment_end_before_start' AS assertion, a.id, a.scheduled_start, a.scheduled_end
FROM appointment a
WHERE a.scheduled_end < a.scheduled_start;

SELECT 'patient_dob_in_future' AS assertion, p.id, p.date_of_birth
FROM patient p
WHERE p.date_of_birth > CURRENT_DATE;

-- ---------------------------------------------------------------------------
-- 8. Status-enum sanity — only known status tokens are allowed (app-enforced;
--    asserted here to catch direct-SQL drift).
-- ---------------------------------------------------------------------------
SELECT 'patient_unknown_status' AS assertion, p.id, p.status
FROM patient p
WHERE p.status NOT IN ('ACTIVE', 'INACTIVE', 'DECEASED', 'MERGED');

SELECT 'appointment_unknown_status' AS assertion, a.id, a.status
FROM appointment a
WHERE a.status NOT IN ('BOOKED', 'ARRIVED', 'FULFILLED', 'CANCELLED', 'NOSHOW');

-- ---------------------------------------------------------------------------
-- 9. Audit completeness — every audit row must carry the minimum required
--    context (action + entity_type + correlation_id). Rows missing these are
--    non-compliant for HIPAA-style audit logging.
-- ---------------------------------------------------------------------------
SELECT 'audit_missing_required_fields' AS assertion, al.id
FROM audit_log al
WHERE al.action IS NULL OR al.action = ''
   OR al.entity_type IS NULL OR al.entity_type = ''
   OR al.correlation_id IS NULL OR al.correlation_id = '';

-- Audit timestamp present and not in the future.
SELECT 'audit_bad_timestamp' AS assertion, al.id, al.occurred_at
FROM audit_log al
WHERE al.occurred_at IS NULL OR al.occurred_at > CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 10. FK coverage report — informational. Lists declared foreign keys so a
--     reviewer can confirm every expected FK is present in the live schema.
--     (Returns rows by design; compare against the expected set in README.)
-- ---------------------------------------------------------------------------
SELECT 'declared_foreign_key' AS assertion,
       tc.table_name, kcu.column_name, tc.constraint_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
  ON tc.constraint_name = kcu.constraint_name
 AND tc.table_schema   = kcu.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
ORDER BY tc.table_name, kcu.column_name;
