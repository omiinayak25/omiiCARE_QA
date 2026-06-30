# omiiCARE_QA — Postman Collection

Runnable API examples for the **omiiCARE_QA 1.0.0** backend (Java 21 / Spring Boot 3). Every endpoint lives under `/api/v1/`. Success bodies use the standard envelope `{ success, data, correlationId, timestamp }`; errors are [RFC 7807](https://www.rfc-editor.org/rfc/rfc7807) `ProblemDetail` payloads with stable `errorCode` values (`OMII-4xx`).

> All data referenced here is **synthetic and PHI-safe**. There is no real patient information anywhere in this collection.

## Files

| File | Purpose |
|------|---------|
| `omiiCARE_QA.postman_collection.json` | Postman v2.1 collection with 10 requests across Auth, Patients, Appointments, Providers, and FHIR. |

## Prerequisites

| Requirement | Value |
|-------------|-------|
| Backend running | `http://localhost:8080` (see [`infrastructure/`](../infrastructure) Docker Compose or run the `apps/backend` Spring Boot app) |
| Health check | `GET /actuator/health` returns `{"status":"UP"}` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Demo credentials | `demo.admin` / `Admin@12345` (synthetic) |
| Postman | v10+ (collection schema v2.1.0) |

## Import

1. Open Postman → **Import** → drag in `omiiCARE_QA.postman_collection.json` (or **Import → File**).
2. The collection ships with its own **collection variables**, so no separate environment is required to get started:

   | Variable | Default | Notes |
   |----------|---------|-------|
   | `baseUrl` | `http://localhost:8080` | Point at another host/port if needed. |
   | `accessToken` | _(empty)_ | Populated automatically by **Auth / Login**. |
   | `refreshToken` | _(empty)_ | Populated automatically by **Auth / Login**. |

## Usage — run Login first

The collection uses **Bearer-token auth on every request except Login**. The token is captured for you:

1. **Run `Auth / Login` first.** Its **test script** reads `data.accessToken` from the response and saves it to the `accessToken` collection variable (and `refreshToken`):

   ```javascript
   var json = pm.response.json();
   pm.collectionVariables.set("accessToken", json.data.accessToken);
   pm.collectionVariables.set("refreshToken", json.data.refreshToken);
   ```

2. Run any other request. Each sends `Authorization: Bearer {{accessToken}}` automatically.
3. The access token expires after **900 seconds (15 minutes)**. When you get an `OMII-401`, just re-run **Login**.

### Request order at a glance

| # | Request | Method | Path | Permission |
|---|---------|--------|------|------------|
| 1 | Auth / Login | POST | `/api/v1/auth/login` | — (public) |
| 2 | Auth / Me | GET | `/api/v1/auth/me` | authenticated |
| 3 | Patients / List | GET | `/api/v1/patients` | `patient:read` |
| 4 | Patients / Create | POST | `/api/v1/patients` | `patient:write` |
| 5 | Patients / Get | GET | `/api/v1/patients/{id}` | `patient:read` |
| 6 | Appointments / List | GET | `/api/v1/appointments` | `appointment:read` |
| 7 | Appointments / Book | POST | `/api/v1/appointments` | `appointment:write` |
| 8 | Appointments / Cancel | POST | `/api/v1/appointments/{id}/cancel` | `appointment:write` |
| 9 | Providers / List | GET | `/api/v1/providers` | authenticated |
| 10 | FHIR / Patient Read | GET | `/api/v1/fhir/Patient/{id}` | `patient:read` |

## Environment notes

- **Local vs. Docker** — set `baseUrl` to wherever the backend listens. With the bundled Docker Compose the backend is published on `:8080`.
- **Seeded ids** — the `Get`, `Cancel`, and `FHIR Read` requests use id `1`, which maps to the seeded DEMO records (patient `MRN-0001`, provider `DR-001`, the booked appointment). See [`docs/SAMPLE_DATA.md`](../docs/SAMPLE_DATA.md).
- **Business rule** — booking an overlapping slot for the same provider is rejected by `BR-APPT-001` (no double-booking) with HTTP **422** / `OMII-422`. See [`docs/BUSINESS_RULES.md`](../docs/BUSINESS_RULES.md).
- **Sample payloads** — copy/paste-ready request and response bodies live in [`docs/examples/`](../docs/examples/README.md).

## Related docs

- [`docs/examples/README.md`](../docs/examples/README.md) — example payloads + `curl` equivalents
- [`docs/SAMPLE_DATA.md`](../docs/SAMPLE_DATA.md) — the synthetic demo dataset
- [`docs/API_BLUEPRINT.md`](../docs/API_BLUEPRINT.md) — full API contract
- [`docs/FHIR_GUIDE.md`](../docs/FHIR_GUIDE.md) — FHIR R4 mapping
- [`ARCHITECTURE.md`](../ARCHITECTURE.md) · [`docs/PORTFOLIO_GUIDE.md`](../docs/PORTFOLIO_GUIDE.md) · [`docs/CI_CD_GUIDE.md`](../docs/CI_CD_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | SDET | Initial (Milestone 10) |
