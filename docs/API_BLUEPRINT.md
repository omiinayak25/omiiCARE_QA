# API Blueprint

> **REST API design standards & resource catalogue for omiiCARE_QA.** This
> document fixes the conventions every backend endpoint (Milestone 3) follows:
> base path, naming, verbs, status codes, response envelopes, RFC 7807 errors,
> pagination/filtering/sorting/search, idempotency, validation, auth headers,
> rate-limit signalling, and a representative endpoint catalogue. Designed in
> Milestone 1; no controller code is written yet. Facts defer to
> [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

Provide one consistent API contract so that clients, the frontend (M4), the
automation platform (M5), and contract tests (M7) can depend on stable,
predictable behaviour across every healthcare domain.

## Scope

- **In scope:** REST conventions, error model, common query parameters, headers,
  validation approach, and a per-domain endpoint catalogue with example payloads.
- **Out of scope:** FHIR resource detail (see [FHIR_GUIDE.md](FHIR_GUIDE.md)),
  HL7 messaging (see [HL7_GUIDE.md](HL7_GUIDE.md)), and version lifecycle policy
  (see [API_VERSIONING_POLICY.md](API_VERSIONING_POLICY.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| API/Backend Engineer (M3) | Implement endpoints to these conventions |
| QA Architect (M5/M7) | Author API + contract tests against this contract |
| Frontend Engineer (M4) | Consume the documented envelopes and errors |

---

## 1. Base Path & Resource Naming

- All endpoints are served under **`/api/v1/`** (URI versioning; see
  [API_VERSIONING_POLICY.md](API_VERSIONING_POLICY.md)).
- Resources are **plural nouns**, lowercase, hyphenated: `/patients`,
  `/appointments`, `/lab-orders`, `/rad-reports`.
- Sub-resources express ownership: `/patients/{patientId}/coverages`,
  `/encounters/{encounterId}/prescriptions`.
- Identifiers in paths are UUIDs. No verbs in paths; actions that are not pure
  CRUD use a sub-resource command form: `POST /appointments/{id}/cancel`.

## 2. HTTP Verbs & Status Conventions

| Verb | Use | Success status |
|------|-----|----------------|
| `GET` | Read a resource or collection | `200 OK` |
| `POST` | Create a resource / invoke a command | `201 Created` (with `Location`) / `200 OK` |
| `PUT` | Full replace (idempotent) | `200 OK` |
| `PATCH` | Partial update | `200 OK` |
| `DELETE` | Soft delete / deactivate | `204 No Content` |

| Status | Meaning in omiiCARE_QA |
|--------|------------------------|
| `400` | Malformed request / validation failure (Problem Details with field errors) |
| `401` | Missing/invalid authentication |
| `403` | Authenticated but not permitted (RBAC or cross-tenant — BR-TENANT-002) |
| `404` | Resource not found *within the caller's tenant* |
| `409` | Conflict (e.g. double-booking BR-APPT-003, optimistic-lock version mismatch) |
| `422` | Semantically invalid (business-rule violation that is not a field error) |
| `429` | Rate limit exceeded |
| `500/503` | Server / dependency failure |

## 3. Response Envelopes

### Success envelope

```json
{
  "data": { },
  "meta": {
    "requestId": "b6c1f0e2-1a3d-4f6a-9b2e-0c1d2e3f4a5b",
    "timestamp": "2026-06-30T09:15:00Z"
  }
}
```

Collections place items in `data` (array) and paging in `meta.page`:

```json
{
  "data": [ ],
  "meta": {
    "requestId": "…",
    "timestamp": "2026-06-30T09:15:00Z",
    "page": { "number": 0, "size": 20, "totalElements": 137, "totalPages": 7 }
  }
}
```

### Error envelope — RFC 7807 Problem Details

Errors use `application/problem+json` and the [RFC 7807](https://www.rfc-editor.org/rfc/rfc7807)
shape extended with an application `code`, `requestId`, and field `errors`:

```json
{
  "type": "https://omiicare.qa/problems/appointment-overlap",
  "title": "Provider already booked",
  "status": 409,
  "detail": "Provider is already booked for an overlapping time range.",
  "instance": "/api/v1/appointments",
  "code": "APPT_DOUBLE_BOOKING",
  "requestId": "b6c1f0e2-1a3d-4f6a-9b2e-0c1d2e3f4a5b",
  "errors": []
}
```

### Error code catalogue (representative)

| `code` | HTTP | Trigger |
|--------|------|---------|
| `VALIDATION_FAILED` | 400 | One or more field constraints failed (see `errors[]`) |
| `UNAUTHENTICATED` | 401 | No/invalid bearer token |
| `ACCESS_DENIED` | 403 | RBAC permission missing (BR-RBAC-001) |
| `CROSS_TENANT_DENIED` | 403 | Resource belongs to another tenant (BR-TENANT-002) |
| `RESOURCE_NOT_FOUND` | 404 | Unknown id within tenant |
| `APPT_DOUBLE_BOOKING` | 409 | Overlapping provider appointment (BR-APPT-003) |
| `VERSION_CONFLICT` | 409 | Optimistic-lock mismatch |
| `RX_ALLERGY_CONTRAINDICATION` | 422 | Drug–allergy hard stop without override (BR-RX-002) |
| `INVOICE_BALANCE_EXCEEDED` | 422 | Payment exceeds outstanding balance (BR-BILL-004) |
| `COVERAGE_INACTIVE` | 422 | Claim against expired coverage (BR-INS-003) |
| `RATE_LIMITED` | 429 | Quota exceeded |
| `INTERNAL_ERROR` | 500 | Unhandled server fault |

## 4. Pagination, Filtering, Sorting, Search

| Concern | Convention |
|---------|------------|
| Pagination | `?page=0&size=20` (zero-based; `size` capped at 100); page metadata in `meta.page` |
| Sorting | `?sort=startAt,desc&sort=lastName,asc` (field,direction; repeatable) |
| Filtering | `?status=BOOKED&providerId=…&from=2026-06-01&to=2026-06-30` (whitelisted fields only) |
| Search | `?q=` for free-text search on documented searchable fields per resource |
| Field selection | `?fields=id,mrn,givenName` (sparse fieldsets, optional) |

Unknown or non-whitelisted query parameters return `400 VALIDATION_FAILED`.

## 5. Idempotency

- Unsafe creation endpoints (`POST` that creates resources or triggers payments)
  accept an **`Idempotency-Key`** request header (client-generated UUID).
- The server stores the key + first response for a retention window; a repeated
  key returns the original result instead of creating a duplicate.
- `PUT`/`DELETE` are inherently idempotent. Required on payment creation and
  appointment booking to make retries safe.

## 6. Validation Approach

- **Syntactic:** Bean Validation (`jakarta.validation`) on request DTOs;
  failures aggregate into the `errors[]` array of the Problem Details body.
- **Semantic / business:** enforced in the application layer against
  [BUSINESS_RULES.md](BUSINESS_RULES.md); violations return `409`/`422` with the
  matching `code`.
- A single centralized exception-handling layer (M3) maps exceptions to Problem
  Details; controllers contain no try/catch (see [ARCHITECTURE.md](../ARCHITECTURE.md) §4).

### Field-error shape

```json
{ "field": "dateOfBirth", "code": "PastOrPresent", "message": "must not be in the future" }
```

## 7. Authentication & Authorization Headers

| Header | Direction | Purpose |
|--------|-----------|---------|
| `Authorization: Bearer <jwt>` | request | Authenticates the principal (JWT issued via auth/IdP, M3) |
| `X-Tenant-Id` | request (optional) | Disambiguates tenant for Super Admin; otherwise derived from the token |
| `Idempotency-Key` | request | Safe-retry key on creation endpoints |
| `X-Request-Id` / `traceparent` | request/response | Correlation/trace propagation (OpenTelemetry) |

Every request is authenticated (default deny, BR-RBAC-001) and authorized by
permission (BR-RBAC-002). Tenant context is bound from the token and enforced on
every query (BR-TENANT-001).

## 8. Rate-Limiting Headers

| Header | Meaning |
|--------|---------|
| `X-RateLimit-Limit` | Requests permitted in the window |
| `X-RateLimit-Remaining` | Requests left in the current window |
| `X-RateLimit-Reset` | Epoch seconds when the window resets |
| `Retry-After` | Seconds to wait (sent with `429`) |

## 9. Endpoint Catalogue (Representative)

> Illustrative, not exhaustive; full surface is realized in M3.

### Patients

| Method | Path | Purpose | Key roles |
|--------|------|---------|-----------|
| `POST` | `/api/v1/patients` | Register patient (dup-check BR-IDENT-003) | Receptionist |
| `GET` | `/api/v1/patients` | Search/list patients | Clinical/Admin |
| `GET` | `/api/v1/patients/{id}` | Get patient (access-logged BR-AUDIT-002) | Clinical/Admin |
| `PATCH` | `/api/v1/patients/{id}` | Update demographics | Receptionist |
| `POST` | `/api/v1/patients/{id}/merge` | Merge duplicate (BR-IDENT-004) | Hospital Admin |

### Appointments

| Method | Path | Purpose | Key roles |
|--------|------|---------|-----------|
| `POST` | `/api/v1/appointments` | Book (availability + double-booking checks) | Receptionist |
| `POST` | `/api/v1/appointments/{id}/reschedule` | Reschedule (re-check) | Receptionist |
| `POST` | `/api/v1/appointments/{id}/cancel` | Cancel with reason | Receptionist/Patient |
| `POST` | `/api/v1/appointments/{id}/check-in` | Check in | Receptionist |
| `POST` | `/api/v1/appointments/{id}/no-show` | Mark no-show (BR-APPT-008) | Receptionist |

### Encounters

| Method | Path | Purpose | Key roles |
|--------|------|---------|-----------|
| `POST` | `/api/v1/encounters` | Open encounter from check-in | Doctor/Nurse |
| `POST` | `/api/v1/encounters/{id}/diagnoses` | Add ICD-10 diagnosis | Doctor |
| `POST` | `/api/v1/encounters/{id}/complete` | Complete (requires diagnosis BR-ENC-003) | Doctor |

### Prescriptions

| Method | Path | Purpose | Key roles |
|--------|------|---------|-----------|
| `POST` | `/api/v1/encounters/{id}/prescriptions` | Prescribe (allergy/interaction checks) | Doctor |
| `POST` | `/api/v1/prescriptions/{id}/dispense` | Dispense | Pharmacist |
| `POST` | `/api/v1/prescriptions/{id}/refill` | Refill (cap-checked BR-RX-005) | Pharmacist |

### Labs

| Method | Path | Purpose | Key roles |
|--------|------|---------|-----------|
| `POST` | `/api/v1/encounters/{id}/lab-orders` | Order tests (LOINC) | Doctor |
| `POST` | `/api/v1/lab-orders/{id}/results` | Enter result | Lab Technician |
| `POST` | `/api/v1/lab-results/{id}/verify` | Verify result | Lab Technician |

### Billing

| Method | Path | Purpose | Key roles |
|--------|------|---------|-----------|
| `POST` | `/api/v1/invoices` | Create invoice from coded activity | Billing Staff |
| `POST` | `/api/v1/invoices/{id}/payments` | Record payment (balance-checked) | Billing Staff |
| `POST` | `/api/v1/invoices/{id}/claims` | Generate claim | Insurance Staff |

### FHIR

| Method | Path | Purpose | Key roles |
|--------|------|---------|-----------|
| `GET` | `/api/v1/fhir/Patient/{id}` | Patient as FHIR R4 | Clinical/Admin |
| `GET` | `/api/v1/fhir/Observation?patient={id}` | Observations as FHIR | Clinical |
| `GET` | `/api/v1/fhir/metadata` | CapabilityStatement (scope in [FHIR_GUIDE.md](FHIR_GUIDE.md)) | Public/Admin |

## 10. Example Request/Response Blocks

### Book an appointment

`POST /api/v1/appointments` with `Authorization: Bearer …` and `Idempotency-Key`:

```json
{
  "patientId": "8f2c…",
  "providerId": "1a4b…",
  "branchId": "c33d…",
  "visitType": "OFFICE_VISIT",
  "startAt": "2026-07-02T10:00:00Z",
  "endAt": "2026-07-02T10:30:00Z"
}
```

`201 Created`, `Location: /api/v1/appointments/9d12…`:

```json
{
  "data": {
    "id": "9d12…",
    "status": "BOOKED",
    "patientId": "8f2c…",
    "providerId": "1a4b…",
    "startAt": "2026-07-02T10:00:00Z",
    "endAt": "2026-07-02T10:30:00Z"
  },
  "meta": { "requestId": "…", "timestamp": "2026-06-30T09:20:00Z" }
}
```

### Validation failure on patient registration

`POST /api/v1/patients` → `400`, `application/problem+json`:

```json
{
  "type": "https://omiicare.qa/problems/validation",
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields are invalid.",
  "instance": "/api/v1/patients",
  "code": "VALIDATION_FAILED",
  "requestId": "…",
  "errors": [
    { "field": "dateOfBirth", "code": "PastOrPresent", "message": "must not be in the future" },
    { "field": "givenName", "code": "NotBlank", "message": "must not be blank" }
  ]
}
```

## 11. OpenAPI / Swagger Location

- Spec served by springdoc at **`/v3/api-docs`** (JSON) and **`/v3/api-docs.yaml`**.
- Interactive Swagger UI at **`/swagger-ui/index.html`**.
- The generated contract is exported to `docs/api/openapi.yaml` in CI (M8) for
  contract testing (M7).

## Examples

- A `429` response carries `Retry-After` and `X-RateLimit-Reset` so clients can
  back off deterministically.
- A Super Admin listing patients across tenants supplies `X-Tenant-Id`; all other
  roles have tenant bound from the token (BR-TENANT-001).

## Future Enhancements

- Conditional requests (`ETag`/`If-None-Match`) for cache efficiency.
- Bulk endpoints and async job-status resources for large operations.
- GraphQL/gRPC read surfaces (post-1.0, see [ROADMAP.md](../ROADMAP.md)).

## Dependencies

- Facts/roles: [PROJECT_METADATA.md](PROJECT_METADATA.md).
- Rules driving status/error codes: [BUSINESS_RULES.md](BUSINESS_RULES.md).
- Data model behind resources: [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md).
- Versioning of this surface: [API_VERSIONING_POLICY.md](API_VERSIONING_POLICY.md).

## References

- [RFC 7807](https://www.rfc-editor.org/rfc/rfc7807) Problem Details.
- [ARCHITECTURE.md](../ARCHITECTURE.md) §4 Backend architecture.
- OpenAPI 3 / springdoc documentation.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Senior API Engineer | Initial REST API design standards & catalogue (Milestone 1) |
