# Healthcare QA Guide

> **Purpose.** Domain guide for testing omiiCARE_QA as a healthcare system: FHIR/HL7 awareness, PHI handling in testing, consent and audit testing, and clinical terminology (ICD/CPT/LOINC/SNOMED) — tied to this project's auth/patient/appointment/FHIR modules. All testing uses synthetic, PHI-safe data only.

## Purpose

Healthcare software carries privacy, safety, and interoperability obligations that ordinary apps do not. This guide equips QA to test those concerns concretely against omiiCARE's implemented surface (REST APIs, the FHIR R4 read facade, RBAC, audit logging).

## Scope

- **In scope:** FHIR/HL7 correctness, PHI-safe practice, consent/audit verification, terminology checks for implemented modules.
- **Out of scope:** Formal HIPAA certification and clinical efficacy (not in QA's remit).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Ensures every cycle honors PHI-safety and audit checks |
| QA Engineers | Execute FHIR/terminology/consent cases |
| Compliance reviewer | Confirms audit-log coverage on sensitive actions |

---

## 1. FHIR / HL7 Awareness

omiiCARE exposes a **FHIR R4 read facade**: `GET /api/v1/fhir/Patient/{id}` returning `application/fhir+json`, mapping the internal patient model to the FHIR `Patient` resource.

| Check | What to verify |
|-------|----------------|
| Content type | Response is `application/fhir+json` |
| Resource type | Body has `"resourceType": "Patient"` |
| Identifier | Internal MRN maps to `identifier[]` with a stable system URI |
| Name mapping | `first_name`/`last_name` → `name[].given` / `name[].family` |
| Gender mapping | Internal MALE/FEMALE/OTHER/UNKNOWN → FHIR `gender` (male/female/other/unknown, lowercase) |
| BirthDate | `date_of_birth` → `birthDate` in `YYYY-MM-DD` |
| Not-found | Unknown id returns a FHIR `OperationOutcome`, not a raw stack trace |
| No PHI leak | Error bodies never echo full PHI |

HL7 v2 message handling is on the roadmap; when present, verify segment structure (MSH/PID/PV1), field separators, and that malformed messages are rejected gracefully.

## 2. PHI Handling in Testing

| Rule | Practice in omiiCARE_QA |
|------|--------------------------|
| Never use real PHI | Only seeded DEMO data + `PatientFactory` synthetic data |
| Non-routable contacts | `@demo.example` emails, `+1-555-01xx` phones |
| No PHI in logs/screenshots | Mask or use synthetic values in defect reports |
| No PHI in test names | Use MRN-0001 style identifiers, not real names |
| Data isolation | Tenant-scoped (`DEMO`); cross-tenant reads must be denied |
| Disposal | `./scripts/reset.sh --yes` wipes volumes between sensitive runs |

## 3. Consent & Audit Testing

| Area | What to test |
|------|--------------|
| RBAC enforcement | Each of the 12 roles can access only its permitted endpoints; `@PreAuthorize` denials return 403, not 500 |
| Least privilege | `AUDITOR` is read-only; `PATIENT` sees only own records |
| Audit logging | Sensitive actions (patient read/create, appointment booking, login) produce an audit entry with actor, action, timestamp, tenant |
| Audit integrity | Audit entries are append-only; no endpoint mutates/deletes them |
| Correlation | Audit/log entries carry correlation/trace/span IDs (MDC) for traceability |
| Cross-tenant | A `DEMO` user cannot read another tenant's patient (data segregation) |

## 4. Clinical Terminology

When clinical coding surfaces appear, verify codes resolve to the correct system and that system URIs are correct.

| System | Used for | Example check |
|--------|----------|---------------|
| ICD-10 | Diagnoses | Code maps to documented condition; invalid code rejected |
| CPT | Procedures | Procedure code valid and billable |
| LOINC | Lab observations | Observation `code` uses LOINC system URI |
| SNOMED CT | Clinical findings | Concept ID resolves; system URI correct |

For any coded element in a FHIR resource, assert the `system` URI and `code` pair are both present and consistent (no code without its system).

## 5. Healthcare Test Heuristics

- Treat every PHI field as sensitive even in synthetic form — practice the habit.
- Negative tests matter most: denied access, malformed FHIR, invalid codes, double-booking.
- Verify audit *and* function together: a successful action with no audit entry is a defect.
- Boundary DOBs and future dates frequently expose validation gaps (see [Boundary Data Sets](../test-data/BOUNDARY_DATA_SETS.md)).

---

## Related Documents

- [Glossary](GLOSSARY.md)
- [Test Data Catalog](../test-data/TEST_DATA_CATALOG.md)
- [Common Failure Patterns](COMMON_FAILURE_PATTERNS.md)
- [docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
