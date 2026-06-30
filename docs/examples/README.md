# API Examples — omiiCARE_QA 1.0.0

Copy/paste-ready sample JSON payloads matching the real omiiCARE_QA backend contract (Java 21 / Spring Boot 3, endpoints under `/api/v1/`). Every example is **synthetic and PHI-safe** — no real patient data.

Success responses use the envelope `{ success, data, correlationId, timestamp }`. Errors are RFC 7807 `ProblemDetail` with stable `errorCode` values (`OMII-4xx`). For a one-click runnable version of these calls, import the [Postman collection](../../postman/README.md).

## Index

| File | Shape | Used by | Notes |
|------|-------|---------|-------|
| [`patient-create-request.json`](patient-create-request.json) | `CreatePatientRequest` | `POST /api/v1/patients` | `mrn` omitted → auto-generated. `firstName`, `lastName`, `dateOfBirth` (past), `gender` required. |
| [`patient-response.json`](patient-response.json) | `ApiResponse<PatientResponse>` | `POST /api/v1/patients` (201), `GET /api/v1/patients/{id}` | Envelope wrapping the created/fetched patient. |
| [`appointment-book-request.json`](appointment-book-request.json) | `BookAppointmentRequest` | `POST /api/v1/appointments` | `scheduledStart` must be in the future (ISO-8601 instant, `Z`). |
| [`fhir-patient-response.json`](fhir-patient-response.json) | FHIR R4 `Patient` | `GET /api/v1/fhir/Patient/{id}` | Bare FHIR resource (no envelope), `application/fhir+json`. MRN system `urn:omiicare:mrn`. |
| [`problem-detail-validation-error.json`](problem-detail-validation-error.json) | RFC 7807 `ProblemDetail` | any 400 validation failure | `errorCode` `OMII-400` + per-field `errors[]`. |

## Field reference

### CreatePatientRequest

| Field | Type | Required | Constraint |
|-------|------|----------|------------|
| `mrn` | string | no | max 40; auto-generated if absent |
| `firstName` | string | yes | not blank, max 100 |
| `lastName` | string | yes | not blank, max 100 |
| `dateOfBirth` | date (`YYYY-MM-DD`) | yes | must be in the past |
| `gender` | string | yes | `MALE` / `FEMALE` / `OTHER` / `UNKNOWN` |
| `email` | string | no | valid email |
| `phone` | string | no | max 40 |

### BookAppointmentRequest

| Field | Type | Required | Constraint |
|-------|------|----------|------------|
| `patientId` | number | yes | existing patient id |
| `providerId` | number | yes | existing provider id |
| `scheduledStart` | instant (`YYYY-MM-DDThh:mm:ssZ`) | yes | must be in the future |
| `scheduledEnd` | instant | yes | after `scheduledStart` |
| `reason` | string | no | max 500 |

> **BR-APPT-001 (no double-booking):** booking an overlapping slot for the same provider returns HTTP **422** / `OMII-422` (`ruleId` = `BR-APPT-001`). See [`docs/BUSINESS_RULES.md`](../BUSINESS_RULES.md).

## curl walkthrough — login → create patient → book appointment

Assumes the backend is running on `http://localhost:8080` (health: `GET /actuator/health`). Uses [`jq`](https://jqlang.github.io/jq/) to extract the token.

### 1. Login (capture the access token)

```bash
ACCESS_TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo.admin","password":"Admin@12345"}' \
  | jq -r '.data.accessToken')

echo "token: ${ACCESS_TOKEN:0:16}..."
```

### 2. Create a patient (returns 201; capture the new id)

```bash
PATIENT_ID=$(curl -s -X POST http://localhost:8080/api/v1/patients \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  --data @docs/examples/patient-create-request.json \
  | jq -r '.data.id')

echo "new patient id: $PATIENT_ID"
```

### 3. Book an appointment for that patient with provider DR-001 (id 1)

```bash
curl -s -X POST http://localhost:8080/api/v1/appointments \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -d "{
        \"patientId\": $PATIENT_ID,
        \"providerId\": 1,
        \"scheduledStart\": \"2027-02-10T14:00:00Z\",
        \"scheduledEnd\": \"2027-02-10T14:30:00Z\",
        \"reason\": \"Routine cardiology follow-up\"
      }" | jq '.data'
```

### Bonus — read a patient as FHIR R4

```bash
curl -s http://localhost:8080/api/v1/fhir/Patient/1 \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Accept: application/fhir+json' | jq '.'
```

## Related docs

- [`postman/README.md`](../../postman/README.md) — Postman collection (run Login first)
- [`docs/SAMPLE_DATA.md`](../SAMPLE_DATA.md) — the seeded synthetic dataset
- [`docs/API_BLUEPRINT.md`](../API_BLUEPRINT.md) · [`docs/FHIR_GUIDE.md`](../FHIR_GUIDE.md) · [`docs/BUSINESS_RULES.md`](../BUSINESS_RULES.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | SDET | Initial (Milestone 10) |
