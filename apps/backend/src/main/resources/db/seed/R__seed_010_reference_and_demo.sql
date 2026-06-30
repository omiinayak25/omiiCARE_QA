-- =============================================================================
-- Repeatable seed — reference data (roles, permissions) + a PHI-safe demo tenant.
-- Loaded only in profiles whose flyway.locations include classpath:db/seed
-- (dev, local, docker, test, qa). Idempotent and portable: every INSERT is
-- guarded by WHERE NOT EXISTS so re-running this repeatable migration on a
-- persistent database never produces duplicates.
--
-- All data here is SYNTHETIC and PHI-safe. No real patient information.
-- =============================================================================

-- --- Roles (the 12 RBAC roles) ---
INSERT INTO role (code, name, description)
SELECT v.code, v.name, v.description FROM (
    SELECT 'SUPER_ADMIN'     AS code, 'Super Admin'     AS name, 'Platform-wide administration across tenants' AS description UNION ALL
    SELECT 'HOSPITAL_ADMIN', 'Hospital Admin', 'Administration within a hospital/tenant' UNION ALL
    SELECT 'DOCTOR',         'Doctor',         'Clinical provider' UNION ALL
    SELECT 'NURSE',          'Nurse',          'Nursing staff' UNION ALL
    SELECT 'RECEPTIONIST',   'Receptionist',   'Front-desk and scheduling' UNION ALL
    SELECT 'LAB_TECHNICIAN', 'Lab Technician', 'Laboratory operations' UNION ALL
    SELECT 'RADIOLOGIST',    'Radiologist',    'Radiology operations' UNION ALL
    SELECT 'PHARMACIST',     'Pharmacist',     'Pharmacy operations' UNION ALL
    SELECT 'BILLING_STAFF',  'Billing Staff',  'Billing and invoicing' UNION ALL
    SELECT 'INSURANCE_STAFF','Insurance Staff','Insurance verification and claims' UNION ALL
    SELECT 'PATIENT',        'Patient',        'Patient self-service' UNION ALL
    SELECT 'AUDITOR',        'Auditor',        'Read-only audit and compliance access'
) v
WHERE NOT EXISTS (SELECT 1 FROM role r WHERE r.code = v.code);

-- --- Core permissions (representative; expanded with endpoints in M3) ---
INSERT INTO permission (code, description)
SELECT v.code, v.description FROM (
    SELECT 'patient:read'       AS code, 'View patient records' AS description UNION ALL
    SELECT 'patient:write',     'Create/update patient records' UNION ALL
    SELECT 'appointment:read',  'View appointments' UNION ALL
    SELECT 'appointment:write', 'Create/update appointments' UNION ALL
    SELECT 'prescription:write','Create prescriptions' UNION ALL
    SELECT 'billing:read',      'View billing' UNION ALL
    SELECT 'billing:write',     'Create/update billing' UNION ALL
    SELECT 'audit:read',        'View audit logs' UNION ALL
    SELECT 'admin:manage',      'Manage tenant configuration and users'
) v
WHERE NOT EXISTS (SELECT 1 FROM permission p WHERE p.code = v.code);

-- --- Demo tenant / hospital / departments (synthetic) ---
INSERT INTO tenant (code, name, status)
SELECT 'DEMO', 'omiiCARE Demo Health Network', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tenant WHERE code = 'DEMO');

INSERT INTO hospital (tenant_id, code, name, timezone, status)
SELECT t.id, 'DEMO-GEN', 'omiiCARE General Hospital', 'UTC', 'ACTIVE'
FROM tenant t
WHERE t.code = 'DEMO'
  AND NOT EXISTS (SELECT 1 FROM hospital h WHERE h.code = 'DEMO-GEN' AND h.tenant_id = t.id);

INSERT INTO department (tenant_id, hospital_id, code, name)
SELECT h.tenant_id, h.id, v.code, v.name
FROM hospital h
CROSS JOIN (
    SELECT 'CARD' AS code, 'Cardiology' AS name UNION ALL
    SELECT 'RADI', 'Radiology' UNION ALL
    SELECT 'LABM', 'Laboratory Medicine' UNION ALL
    SELECT 'PHAR', 'Pharmacy' UNION ALL
    SELECT 'EMER', 'Emergency'
) v
WHERE h.code = 'DEMO-GEN'
  AND NOT EXISTS (
      SELECT 1 FROM department d WHERE d.hospital_id = h.id AND d.code = v.code);

-- --- Demo admin user (synthetic; real credentials/auth wired in Milestone 3) ---
-- password_hash is a non-functional placeholder until the auth module (M3) sets
-- a real bcrypt hash via the user-management flow.
INSERT INTO app_user (tenant_id, username, email, password_hash, full_name, status, email_verified)
SELECT t.id, 'demo.admin', 'demo.admin@omiicare.example', 'NOT_SET_PENDING_M3_AUTH',
       'Demo Administrator', 'ACTIVE', TRUE
FROM tenant t
WHERE t.code = 'DEMO'
  AND NOT EXISTS (SELECT 1 FROM app_user u WHERE u.username = 'demo.admin' AND u.tenant_id = t.id);
