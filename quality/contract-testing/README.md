# Contract Testing — omiiCARE_QA

> **Milestone 7 — Reliability & Data QA.** This module guards the contracts
> between the omiiCARE backend and its consumers: the REST API surface
> (described by OpenAPI at `/v3/api-docs`), the standard response envelope, and
> the FHIR R4 Patient facade.

## Purpose

Catch breaking changes early by asserting that requests and responses conform to
agreed-upon schemas — independently of business-logic tests. A contract test
fails when the *shape* of an interaction drifts, even if the endpoint still
"works".

## What We Validate

| Layer | Source of truth | How |
|-------|-----------------|-----|
| **OpenAPI contract** | `/v3/api-docs` (springdoc) | Fetch the live spec; assert paths, status codes, and component schemas match the committed baseline. Diff for breaking changes (removed fields, narrowed enums, new required fields). |
| **Response envelope** | [`schemas/api-response-envelope.schema.json`](schemas/api-response-envelope.schema.json) | Validate every 2xx body has `{success:true, data, correlationId, timestamp}`. |
| **Patient payload** | [`schemas/patient-response.schema.json`](schemas/patient-response.schema.json) | Validate `data` of Patient endpoints against the JSON Schema. |
| **FHIR Patient** | [`schemas/fhir-patient.schema.json`](schemas/fhir-patient.schema.json) | Validate the FHIR facade output (`resourceType=Patient`, identifier, name, gender enum, birthDate). |
| **JSON Schema validation** | Draft 2020-12 | Each schema is a self-contained, parseable contract used by CI and by consumers. |

## Concepts

### OpenAPI contract validation
springdoc publishes the live spec at `/v3/api-docs`. CI fetches it and compares
against the committed baseline; any *breaking* change (field removed, type
changed, enum value dropped, response code removed, new required request field)
fails the build. Additive changes (new optional field, new endpoint) are allowed.

### JSON Schema validation
Responses are validated against Draft 2020-12 schemas in `schemas/`. The schemas
are derived from the backend records (`PatientResponse`, `ApiResponse`) so that
code and contract cannot silently diverge.

### FHIR resource validation
The FHIR R4 Patient facade output is validated structurally against
`fhir-patient.schema.json` (a minimal subset of the HL7 profile). For full
conformance, the resource can additionally be run through the HL7 FHIR validator
against the R4 `Patient` StructureDefinition.

### Consumer-driven contracts (CDC)
The conceptual model for omiiCARE: each consumer (e.g. a scheduling UI, an
integration partner) publishes the subset of the contract it actually depends on.
The provider verifies it satisfies the union of all consumer expectations before
release. Today this is realised as shared JSON Schemas checked into this
directory; a Pact-style broker is a future enhancement (see ROADMAP).

### Version compatibility
The API follows additive, backward-compatible evolution within a major version:
- **Allowed (minor):** add optional response field, add endpoint, widen enum on
  request, relax a constraint.
- **Breaking (major):** remove/rename field, narrow enum on response, change a
  field type, make an existing request field required, remove a status code.

## Artifacts

| File | Purpose |
|------|---------|
| [`schemas/patient-response.schema.json`](schemas/patient-response.schema.json) | Patient API response contract. |
| [`schemas/api-response-envelope.schema.json`](schemas/api-response-envelope.schema.json) | Standard success envelope contract. |
| [`schemas/fhir-patient.schema.json`](schemas/fhir-patient.schema.json) | Minimal FHIR R4 Patient contract. |
| [`CONTRACT_TEST_CASES.md`](CONTRACT_TEST_CASES.md) | Contract test cases `CT-*` incl. FHIR resource coverage. |

## How to Run

### Fetch & snapshot the OpenAPI spec

```bash
curl -s http://localhost:8080/v3/api-docs -o /tmp/api-docs.json
# diff against committed baseline (fail CI on breaking changes)
```

### Validate a response against a schema (Python, jsonschema)

```bash
python3 - <<'PY'
import json, urllib.request
from jsonschema import Draft202012Validator
schema = json.load(open("quality/contract-testing/schemas/patient-response.schema.json"))
body = json.load(urllib.request.urlopen("http://localhost:8080/api/v1/patients/1"))
Draft202012Validator(schema).validate(body["data"])
print("Patient payload conforms")
PY
```

### Verify all schemas parse

```bash
for f in quality/contract-testing/schemas/*.json; do python3 -c "import json,sys; json.load(open(sys.argv[1]))" "$f"; done
```

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Reliability Engineer | Initial (Milestone 7) |
