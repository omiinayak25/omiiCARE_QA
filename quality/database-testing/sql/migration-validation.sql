-- =============================================================================
-- migration-validation.sql  —  omiiCARE_QA Flyway & schema-structure validation
-- -----------------------------------------------------------------------------
-- Validates that Flyway has applied the expected migrations cleanly and that
-- every expected table and index exists. Portable across H2 (MODE=PostgreSQL)
-- and PostgreSQL 16. Read-only.
--
-- Default Flyway history table is `flyway_schema_history`.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- 1. Flyway history overview — every applied migration, in order.
--    Expected: V1 baseline_platform_schema, V2 clinical_core_schema, success=true.
-- ---------------------------------------------------------------------------
SELECT installed_rank, version, description, type, checksum, success
FROM flyway_schema_history
ORDER BY installed_rank;

-- ---------------------------------------------------------------------------
-- 2. ASSERTION: no failed migrations. Returns ZERO rows when healthy.
-- ---------------------------------------------------------------------------
SELECT 'failed_migration' AS assertion, version, description
FROM flyway_schema_history
WHERE success = FALSE;

-- ---------------------------------------------------------------------------
-- 3. ASSERTION: expected versioned migrations are present.
--    Returns one row per MISSING expected version (zero rows = healthy).
-- ---------------------------------------------------------------------------
SELECT expected.version AS missing_version
FROM (
    SELECT '1' AS version
    UNION ALL SELECT '2'
) expected
WHERE NOT EXISTS (
    SELECT 1 FROM flyway_schema_history h
    WHERE h.version = expected.version AND h.success = TRUE
);

-- ---------------------------------------------------------------------------
-- 4. ASSERTION: no out-of-order / repeated installed_rank duplicates.
-- ---------------------------------------------------------------------------
SELECT 'duplicate_installed_rank' AS assertion, installed_rank, COUNT(*) AS cnt
FROM flyway_schema_history
GROUP BY installed_rank
HAVING COUNT(*) > 1;

-- ---------------------------------------------------------------------------
-- 5. ASSERTION: every expected TABLE exists.
--    Returns one row per MISSING table (zero rows = healthy).
-- ---------------------------------------------------------------------------
SELECT expected.t AS missing_table
FROM (
    SELECT 'tenant' AS t
    UNION ALL SELECT 'hospital'
    UNION ALL SELECT 'department'
    UNION ALL SELECT 'app_user'
    UNION ALL SELECT 'role'
    UNION ALL SELECT 'permission'
    UNION ALL SELECT 'user_role'
    UNION ALL SELECT 'role_permission'
    UNION ALL SELECT 'audit_log'
    UNION ALL SELECT 'patient'
    UNION ALL SELECT 'provider'
    UNION ALL SELECT 'appointment'
) expected
WHERE NOT EXISTS (
    SELECT 1 FROM information_schema.tables it
    WHERE LOWER(it.table_name) = expected.t
      AND it.table_type = 'BASE TABLE'
);

-- ---------------------------------------------------------------------------
-- 6. ASSERTION: key business columns exist on critical tables.
--    Returns one row per MISSING (table, column) pair (zero rows = healthy).
-- ---------------------------------------------------------------------------
SELECT expected.tbl AS table_name, expected.col AS missing_column
FROM (
    SELECT 'patient' AS tbl, 'mrn'           AS col
    UNION ALL SELECT 'patient',     'tenant_id'
    UNION ALL SELECT 'patient',     'date_of_birth'
    UNION ALL SELECT 'appointment', 'patient_id'
    UNION ALL SELECT 'appointment', 'provider_id'
    UNION ALL SELECT 'appointment', 'scheduled_start'
    UNION ALL SELECT 'audit_log',   'correlation_id'
    UNION ALL SELECT 'audit_log',   'entity_type'
    UNION ALL SELECT 'app_user',    'password_hash'
) expected
WHERE NOT EXISTS (
    SELECT 1 FROM information_schema.columns c
    WHERE LOWER(c.table_name)  = expected.tbl
      AND LOWER(c.column_name) = expected.col
);

-- ---------------------------------------------------------------------------
-- 7. ASSERTION: every expected INDEX exists.
--    information_schema.statistics (H2) and pg_indexes (PostgreSQL) both expose
--    index names. The portable check below reads index_name from
--    information_schema.statistics (available on H2); on PostgreSQL substitute
--    `SELECT indexname AS index_name, tablename AS table_name FROM pg_indexes`.
--    Returns one row per MISSING index (zero rows = healthy).
-- ---------------------------------------------------------------------------
SELECT expected.idx AS missing_index
FROM (
    SELECT 'idx_hospital_tenant'    AS idx
    UNION ALL SELECT 'idx_department_tenant'
    UNION ALL SELECT 'idx_user_tenant'
    UNION ALL SELECT 'idx_audit_tenant_time'
    UNION ALL SELECT 'idx_audit_entity'
    UNION ALL SELECT 'idx_patient_tenant'
    UNION ALL SELECT 'idx_patient_last_name'
    UNION ALL SELECT 'idx_provider_tenant'
    UNION ALL SELECT 'idx_appt_tenant'
    UNION ALL SELECT 'idx_appt_provider_time'
    UNION ALL SELECT 'idx_appt_patient'
) expected
WHERE NOT EXISTS (
    SELECT 1 FROM information_schema.statistics s
    WHERE LOWER(s.index_name) = expected.idx
);

-- PostgreSQL variant for step 7 (run this instead on PostgreSQL):
-- SELECT expected.idx AS missing_index
-- FROM ( ... same VALUES list ... ) expected
-- WHERE NOT EXISTS (
--     SELECT 1 FROM pg_indexes pi WHERE LOWER(pi.indexname) = expected.idx
-- );

-- ---------------------------------------------------------------------------
-- 8. ASSERTION: unique constraints present on tenant-scoped business keys.
--    Returns one row per MISSING unique constraint (zero rows = healthy).
-- ---------------------------------------------------------------------------
SELECT expected.c AS missing_unique_constraint
FROM (
    SELECT 'uq_patient_mrn'   AS c
    UNION ALL SELECT 'uq_provider_code'
    UNION ALL SELECT 'uq_user_username'
    UNION ALL SELECT 'uq_user_email'
    UNION ALL SELECT 'uq_tenant_code'
    UNION ALL SELECT 'uq_hospital_code'
    UNION ALL SELECT 'uq_department_code'
    UNION ALL SELECT 'uq_role_code'
    UNION ALL SELECT 'uq_permission_code'
) expected
WHERE NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints tc
    WHERE LOWER(tc.constraint_name) = expected.c
      AND tc.constraint_type = 'UNIQUE'
);
