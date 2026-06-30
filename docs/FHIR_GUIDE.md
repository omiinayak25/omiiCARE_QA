# FHIR Guide

> **How omiiCARE_QA models HL7 FHIR R4.** This guide defines which FHIR resources
> the platform maps, how each maps to an internal entity, the code-system URIs
> used, example resource JSON, the validation approach, and the conformance scope.
> Mapping is introduced in Milestone 3 and validated in Milestone 7. omiiCARE_QA
> makes **no formal certification claims** (see [SECURITY.md](../SECURITY.md)).
> Facts defer to [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

Give backend (M3), automation/contract-testing (M5/M7), and integration (M3)
engineers a single, correct reference for the FHIR R4 surface so that internal
data can be exposed and exchanged as standards-conformant resources.

## Scope

- **In scope:** the FHIR R4 resources mapped, resource→entity mapping, code-system
  URIs, example resources, search params, validation approach, conformance scope.
- **Out of scope:** real interoperability with external production FHIR servers
  (stubbed via adapters — see [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)), and
  any certification claim.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Healthcare Architect | Keep resource mappings clinically and structurally correct |
| Backend Engineer (M3) | Implement resource serialization over internal entities |
| QA Architect (M7) | Validate resources against FHIR R4 profiles |

---

## 1. Standard & Version

- **FHIR R4** (4.0.1) is the target version.
- Resources are exposed under `/api/v1/fhir/` (see
  [API_BLUEPRINT.md](API_BLUEPRINT.md) §9); a `CapabilityStatement` is served at
  `/api/v1/fhir/metadata`.
- FHIR is **additive** over the internal model; it does not replace the REST API.

## 2. Mapped Resources & Internal-Entity Mapping

| FHIR Resource | Internal entity | Notes |
|---------------|-----------------|-------|
| `Patient` | `patient` | Demographics, MRN as `identifier` |
| `Practitioner` | `provider` / `app_user` | Clinician identity |
| `Organization` | `tenant` / `hospital_network` / `branch` | Org hierarchy |
| `Appointment` | `appointment` | Scheduling |
| `Encounter` | `encounter` | Visit/clinical context |
| `Condition` | `diagnosis` | ICD-10 / SNOMED coded |
| `Observation` | `observation` / `lab_result` / `vital_sign` | LOINC coded |
| `Medication` | `medication` | RxNorm-like coded |
| `MedicationRequest` | `prescription` | Prescribing |
| `MedicationDispense` | `dispense` | Dispensing |
| `Procedure` | `rad_order` / billable procedures | CPT / SNOMED coded |
| `DiagnosticReport` | `lab_result` set / `rad_report` | Grouped results/report |
| `Coverage` | `coverage` | Insurance |
| `Immunization` | (immunization records, M3) | Vaccination history |
| `CarePlan` | (care-plan records, M3) | Longitudinal plan |

## 3. Code System URIs

| Code system | URI | Used in |
|-------------|-----|---------|
| LOINC | `http://loinc.org` | `Observation.code`, lab/vital codes |
| SNOMED CT | `http://snomed.info/sct` | `Condition.code`, `Procedure.code`, findings |
| ICD-10 | `http://hl7.org/fhir/sid/icd-10` | `Condition.code` (diagnosis) |
| ICD-10-CM | `http://hl7.org/fhir/sid/icd-10-cm` | US-clinical diagnosis variant |
| CPT | `http://www.ama-assn.org/go/cpt` | `Procedure.code`, billing |
| RxNorm | `http://www.nlm.nih.gov/research/umls/rxnorm` | `Medication.code` |
| UCUM (units) | `http://unitsofmeasure.org` | `Observation.valueQuantity.system` |
| FHIR admin gender | `http://hl7.org/fhir/administrative-gender` | `Patient.gender` |
| MRN identifier type | `http://terminology.hl7.org/CodeSystem/v2-0203` | `Patient.identifier.type` |

## 4. Example Resources

### Minimal `Patient`

```json
{
  "resourceType": "Patient",
  "id": "8f2c0a1b-1111-4aaa-9bbb-222233334444",
  "identifier": [
    {
      "type": {
        "coding": [
          { "system": "http://terminology.hl7.org/CodeSystem/v2-0203", "code": "MR" }
        ]
      },
      "system": "urn:omiicare:tenant:demo:mrn",
      "value": "MRN-000123"
    }
  ],
  "active": true,
  "name": [ { "use": "official", "family": "Synthetic", "given": ["Avery"] } ],
  "gender": "female",
  "birthDate": "1990-04-12"
}
```

### Minimal `Observation` (LOINC, with UCUM unit and reference range)

```json
{
  "resourceType": "Observation",
  "id": "obs-hba1c-001",
  "status": "final",
  "code": {
    "coding": [ { "system": "http://loinc.org", "code": "4548-4", "display": "Hemoglobin A1c" } ]
  },
  "subject": { "reference": "Patient/8f2c0a1b-1111-4aaa-9bbb-222233334444" },
  "effectiveDateTime": "2026-06-29T08:30:00Z",
  "valueQuantity": { "value": 5.6, "unit": "%", "system": "http://unitsofmeasure.org", "code": "%" },
  "referenceRange": [ { "low": { "value": 4.0, "unit": "%" }, "high": { "value": 5.6, "unit": "%" } }]
}
```

## 5. Search Parameters

Standard FHIR search parameters are supported on read endpoints (M3), scoped to
the caller's tenant (BR-TENANT-001):

| Resource | Supported params (representative) |
|----------|-----------------------------------|
| `Patient` | `identifier`, `family`, `given`, `birthdate`, `gender` |
| `Appointment` | `patient`, `practitioner`, `date`, `status` |
| `Encounter` | `patient`, `date`, `status`, `class` |
| `Observation` | `patient`, `code`, `date`, `category` |
| `Condition` | `patient`, `code`, `clinical-status` |
| `MedicationRequest` | `patient`, `status`, `intent` |
| `Coverage` | `patient`, `payor`, `status` |

Results are returned as a FHIR `Bundle` of type `searchset` with paging links.

## 6. Validation Approach (Milestone 7)

| Layer | Validation |
|-------|------------|
| Structural | Validate against the FHIR R4 base StructureDefinitions (resource shape, cardinality, datatypes) |
| Terminology | Confirm `coding.system` URIs match §3 and codes resolve in the standards tables (see [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md) §7) |
| Profile | Validate against project conformance profiles where declared (§7) |
| Tooling | HAPI FHIR validator / official FHIR validator, run in contract tests against the HAPI FHIR adapter target (see [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)) |

Validation is a Milestone 7 deliverable; M3 produces conformant resources, M7
proves conformance.

## 7. Profiles & Conformance Scope

- v1.0 targets **base FHIR R4 resources**; project-specific constraints (e.g. MRN
  identifier slicing on `Patient`) are declared as lightweight profiles in
  `docs/fhir/profiles/` when needed.
- A `CapabilityStatement` (`/api/v1/fhir/metadata`) advertises the supported
  resources, interactions (read/search), and search parameters.
- US Core and other published implementation guides are **out of scope** for v1.0
  and noted as future work.

## 8. Disclaimer

omiiCARE_QA models FHIR R4 conformance for **educational and portfolio purposes
only**, using synthetic, PHI-safe data exclusively (BR-CONS-005). It makes **no
formal certification or regulatory-compliance claims**. See
[MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §9 and
[SECURITY.md](../SECURITY.md).

## Examples

- A `GET /api/v1/fhir/Observation?patient=8f2c…&code=4548-4` returns a
  `searchset` `Bundle` of HbA1c observations for that patient, tenant-scoped.
- A `Condition` derived from `diagnosis` carries both an ICD-10 coding
  (`http://hl7.org/fhir/sid/icd-10`) and, where mapped, a SNOMED CT coding.

## Future Enhancements

- US Core profile conformance and additional resources (`AllergyIntolerance`,
  `ServiceRequest`, `DocumentReference`).
- FHIR Subscriptions and Bulk Data ($export) — post-1.0 (see [ROADMAP.md](../ROADMAP.md)).
- SMART on FHIR app launch via the SMART Health IT adapter target.

## Dependencies

- Internal entities mapped: [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md).
- FHIR endpoints: [API_BLUEPRINT.md](API_BLUEPRINT.md) §9.
- External FHIR targets/stubs: [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md).
- HL7 v2 cross-mapping: [HL7_GUIDE.md](HL7_GUIDE.md).

## References

- HL7 FHIR R4 (4.0.1) specification.
- LOINC, SNOMED CT, ICD-10, CPT, RxNorm, UCUM.
- [ARCHITECTURE.md](../ARCHITECTURE.md) §8 Data architecture (additive standards mapping).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Healthcare Architect | Initial FHIR R4 usage & mapping guide (Milestone 1) |
