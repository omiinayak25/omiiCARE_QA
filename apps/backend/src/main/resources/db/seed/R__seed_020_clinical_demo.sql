-- =============================================================================
-- Repeatable seed — synthetic clinical demo data (patients, providers,
-- appointments) for the DEMO tenant. PHI-safe and fictional. Idempotent via
-- WHERE NOT EXISTS guards. Loaded only where db/seed is on the Flyway path.
-- Flyway runs repeatable migrations in description order, so the numeric prefix
-- (020 > 010) guarantees this runs AFTER R__seed_010_reference_and_demo, which
-- creates the tenant/hospital this seed depends on. Guards keep it idempotent.
-- =============================================================================

-- --- Providers (doctors) ---
INSERT INTO provider (tenant_id, hospital_id, code, first_name, last_name, specialty, status)
SELECT t.id, h.id, v.code, v.first_name, v.last_name, v.specialty, 'ACTIVE'
FROM tenant t
JOIN hospital h ON h.tenant_id = t.id AND h.code = 'DEMO-GEN'
CROSS JOIN (
    SELECT 'DR-001' AS code, 'Alice'  AS first_name, 'Cardwell' AS last_name, 'Cardiology' AS specialty UNION ALL
    SELECT 'DR-002', 'Brian', 'Rayner', 'Radiology'
) v
WHERE t.code = 'DEMO'
  AND NOT EXISTS (
      SELECT 1 FROM provider p WHERE p.tenant_id = t.id AND p.code = v.code);

-- --- Patients (synthetic) ---
INSERT INTO patient (tenant_id, mrn, first_name, last_name, date_of_birth, gender, email, phone, status)
SELECT t.id, v.mrn, v.first_name, v.last_name, v.dob, v.gender, v.email, v.phone, 'ACTIVE'
FROM tenant t
CROSS JOIN (
    SELECT 'MRN-0001' AS mrn, 'John'  AS first_name, 'Public' AS last_name, DATE '1985-04-12' AS dob, 'MALE'   AS gender, 'john.public@demo.example'  AS email, '+1-555-0101' AS phone UNION ALL
    SELECT 'MRN-0002', 'Jane',  'Sample', DATE '1990-09-30', 'FEMALE', 'jane.sample@demo.example', '+1-555-0102' UNION ALL
    SELECT 'MRN-0003', 'Sam',   'Tester', DATE '1978-01-05', 'OTHER',  'sam.tester@demo.example',  '+1-555-0103'
) v
WHERE t.code = 'DEMO'
  AND NOT EXISTS (
      SELECT 1 FROM patient p WHERE p.tenant_id = t.id AND p.mrn = v.mrn);

-- --- A booked appointment (synthetic, fixed future-ish demo timestamps) ---
INSERT INTO appointment (tenant_id, patient_id, provider_id, scheduled_start, scheduled_end, status, reason)
SELECT pat.tenant_id, pat.id, prov.id,
       TIMESTAMP '2027-01-15 09:00:00', TIMESTAMP '2027-01-15 09:30:00',
       'BOOKED', 'Routine cardiology follow-up'
FROM patient pat
JOIN provider prov ON prov.tenant_id = pat.tenant_id AND prov.code = 'DR-001'
WHERE pat.mrn = 'MRN-0001'
  AND NOT EXISTS (
      SELECT 1 FROM appointment a
      WHERE a.patient_id = pat.id AND a.provider_id = prov.id
        AND a.scheduled_start = TIMESTAMP '2027-01-15 09:00:00');
