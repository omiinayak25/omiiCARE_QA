# Contract Test Cases — omiiCARE_QA

> **Milestone 7 — Reliability & Data QA.** Contract test cases `CT-*` covering
> the OpenAPI surface, the response envelope, the Patient payload, and the FHIR
> R4 Patient facade. Schemas live in [`schemas/`](schemas/).

**Legend** — Pri: P0/P1/P2. Type: OAS (OpenAPI), ENV (envelope), JS (JSON
Schema), FHIR, VER (version compatibility).

## API & Envelope Contracts

| ID | Pri | Type | Title | Steps | Expected |
|----|-----|------|-------|-------|----------|
| CT-001 | P0 | OAS | OpenAPI spec is published & parseable | GET `/v3/api-docs`. | 200; valid OpenAPI 3 JSON; parses without error. |
| CT-002 | P0 | OAS | No breaking change vs baseline | Diff live `/v3/api-docs` against committed baseline. | No removed fields/paths/codes; no narrowed enums; no newly-required request fields. |
| CT-003 | P1 | OAS | Documented error uses ProblemDetail | Inspect 4xx/5xx response components. | Errors typed as RFC 7807 ProblemDetail, not the success envelope. |
| CT-004 | P0 | ENV | Success response uses standard envelope | GET an existing patient. | Body validates against `api-response-envelope.schema.json`; `success=true`, `timestamp` present. |
| CT-005 | P0 | ENV | correlationId echoed | Send `X-Correlation-Id: abc`; call any endpoint. | `correlationId` in envelope equals `abc` (or a generated id when absent). |
| CT-006 | P1 | ENV | timestamp is ISO-8601 instant | Validate `timestamp` of any 2xx body. | Matches `date-time`; parseable as UTC instant. |
| CT-007 | P0 | JS | Patient payload conforms | GET `/api/v1/patients/{id}`. | `data` validates against `patient-response.schema.json`. |
| CT-008 | P1 | JS | Patient required fields present | Validate payload lacks no required key. | `id, mrn, firstName, lastName, dateOfBirth, gender, status` all present. |
| CT-009 | P1 | JS | gender restricted to enum | Inspect `gender`. | One of MALE/FEMALE/OTHER/UNKNOWN; any other value fails. |
| CT-010 | P2 | JS | Nullable email/phone accepted | Patient with no email/phone. | Validation passes (fields nullable); when present, email matches `email` format. |
| CT-011 | P1 | JS | No undeclared fields leak | Validate with `additionalProperties:false`. | Response contains only declared fields (no internal columns like password_hash). |
| CT-012 | P0 | JS | List response is enveloped array | GET `/api/v1/patients`. | `data` is an array; each item conforms to `patient-response.schema.json`. |
| CT-013 | P2 | VER | Additive field is non-breaking | Add optional field to response in a build. | Existing consumers/schemas still validate (additive = compatible). |
| CT-014 | P0 | VER | Removing a field is breaking | Simulate field removal in spec diff. | CT-002 fails → build blocked. |

## FHIR Resource Validation

| ID | Pri | Title | Steps | Expected |
|----|-----|-------|-------|----------|
| CT-015 | P0 | FHIR Patient resourceType | GET FHIR Patient facade (e.g. `/fhir/Patient/{id}`). | `resourceType == "Patient"`; validates against `fhir-patient.schema.json`. |
| CT-016 | P0 | FHIR Patient identifier present | Inspect resource. | At least one `identifier` with non-empty `value` (MRN mapped). |
| CT-017 | P1 | FHIR Patient name present | Inspect `name`. | At least one `name` entry; family/given map from last/first name. |
| CT-018 | P0 | FHIR gender enum lowercase | Inspect `gender`. | One of male/female/other/unknown (lowercase per FHIR). |
| CT-019 | P1 | FHIR birthDate format | Inspect `birthDate`. | Matches `YYYY`, `YYYY-MM`, or `YYYY-MM-DD`. |
| CT-020 | P2 | FHIR full-profile validation | Run HL7 FHIR validator against R4 Patient StructureDefinition. | No structural errors (warnings on optional elements acceptable). |

### FHIR Resource Coverage Matrix

The omiiCARE FHIR facade is being built incrementally. The table records which R4
resources have a contract today and which are planned.

| Resource | Status | Contract artifact / notes |
|----------|--------|---------------------------|
| Patient | **Implemented** | `schemas/fhir-patient.schema.json`; backed by `patient` table. |
| Practitioner | Future | Maps from `provider`. Schema to be added when the facade ships. |
| Appointment | Future | Maps from `appointment` (patient + provider + start/end + status). |
| Observation | Future | No backing table yet; clinical results module is later milestone. |
| Encounter | Future | Visit context; depends on encounter/visit modelling. |
| Condition | Future | Problem/diagnosis list; future clinical module. |
| Medication | Future | Pharmacy module; not yet modelled. |
| Procedure | Future | Procedure records; future clinical module. |
| Coverage | Future | Insurance/billing module; not yet modelled. |
| Organization | Future | Maps from `hospital`/`tenant`. |
| DiagnosticReport | Future | Depends on Observation; later milestone. |
| Immunization | Future | Vaccination records; future clinical module. |
| CarePlan | Future | Care management module; not yet modelled. |

> Only **Patient** is implemented in v1.0; all other resources are documented
> here as forward-looking contract placeholders so consumers know the intended
> FHIR surface. Their schemas will be added under `schemas/` as each module ships.

## Execution

```bash
# parse-check all schemas
for f in quality/contract-testing/schemas/*.json; do
  python3 -c "import json,sys; json.load(open(sys.argv[1]))" "$f" && echo "OK $f"
done
```

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Reliability Engineer | Initial (Milestone 7) |
