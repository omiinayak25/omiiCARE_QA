# Demo Guide — omiiCARE_QA

> **Purpose.** A step-by-step script for showing omiiCARE_QA **1.0.0** live: start
> the stack, log in, register a patient, book an appointment, trigger the
> double-booking `422`, read a FHIR Patient, open Swagger, and point at the
> quality dashboards. Follow it top to bottom for a clean ~10-minute walkthrough.

## Scope

- **In scope:** the exact commands, URLs, credentials, and click-path for a live
  demo, plus the talking point at each step.
- **Out of scope:** the narrative framing ([PORTFOLIO_GUIDE.md](PORTFOLIO_GUIDE.md)),
  interview Q&A ([INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md)), and deployment to
  shared environments ([DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)). All data is
  **synthetic and PHI-safe**; the platform makes **no certification claims**.

## Responsibilities

| Audience | How to use this guide |
|----------|-----------------------|
| Presenter | Run the steps in order; read the "say this" column |
| Reviewer following along | Reproduce locally with the same commands |

---

## 1. Prerequisites

| Need | Detail |
|------|--------|
| JDK | Java 21 (LTS) |
| Node | Node 22 (for the frontend) |
| Docker | Docker + Compose plugin (for the infra stack) |
| Ports free | `8080` (backend), `5173` (frontend), plus infra ports |
| Demo login | `demo.admin` / `Admin@12345` |

> The fastest demo needs only the backend (H2, `dev` profile) and the frontend.
> The Docker stack in Step 2a is optional and only required for the dashboards
> in Step 9.

## 2. Start the Stack

### 2a. (Optional) Infrastructure — for dashboards

```bash
./scripts/start.sh
```

Brings up Postgres, Grafana, Prometheus, MailHog, MinIO, Keycloak, WireMock, and
SonarQube, waits for Postgres to be healthy, and prints every service URL.

### 2b. Backend (`:8080`)

```bash
# H2 in-memory, fast startup — no external DB required
mvn -B -ntp -pl apps/backend -am spring-boot:run -Dspring-boot.run.profiles=dev
```

Verify health:

```bash
curl http://localhost:8080/actuator/health      # {"status":"UP"}
```

### 2c. Frontend (`:5173`)

```bash
cd apps/frontend && npm ci && npm run dev
```

Open `http://localhost:5173`.

| Say this | "Two stacks: a Spring Boot healthcare service and the React SUT that exercises it. Both reactors are green." |
|----------|---|

## 3. Log In

1. Open `http://localhost:5173`.
2. Enter **`demo.admin`** / **`Admin@12345`** and submit.
3. The portal renders the admin landing for the user's RBAC role.

Behind the scenes: `POST /api/v1/auth/login` returns a JWT; the client stores it
and calls `GET /api/v1/auth/me` to hydrate the session.

| Say this | "Auth is JWT-based with role-scoped authorities; every API call is authorized by `@PreAuthorize`." |
|----------|---|

## 4. Register a Patient

1. Navigate to **Patients → New**.
2. Fill the synthetic record (name, DOB, gender, contact).
3. Submit.

Behind the scenes: `POST /api/v1/patients` (requires `patient:write`). The list
view supports search + pagination via `GET /api/v1/patients?page=&size=&search=`.

| Say this | "All data is synthetic and PHI-safe. Creating a patient writes an audit entry." |
|----------|---|

## 5. Book an Appointment

1. Go to **Appointments → Book**.
2. Pick the patient from Step 4, a provider, and a start/end time.
3. Submit.

Behind the scenes: `POST /api/v1/appointments` (requires `appointment:write`)
returns `201 Created`.

| Say this | "Booking enforces business rules in the domain layer, not the controller." |
|----------|---|

## 6. Trigger the Double-Booking 422 (BR-APPT-001)

1. Book a **second** appointment for the **same provider** whose time **overlaps**
   the one from Step 5.
2. The API rejects it.

Expected response — **HTTP 422**, RFC 7807 `ProblemDetail`, error code
**`OMII-422`**:

```json
{
  "type": "about:blank",
  "title": "Business rule violation",
  "status": 422,
  "code": "OMII-422",
  "rule": "BR-APPT-001",
  "detail": "Provider already has an appointment overlapping the requested time"
}
```

Reproduce on the API directly:

```bash
TOKEN=$(curl -s http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo.admin","password":"Admin@12345"}' | jq -r '.data.accessToken')

curl -i http://localhost:8080/api/v1/appointments \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"patientId":1,"providerId":1,"startTime":"2026-07-01T10:00:00","endTime":"2026-07-01T10:30:00"}'
# Repeat the same call → 422 OMII-422 / BR-APPT-001
```

| Say this | "BR-APPT-001 — no double-booking — is enforced with an overlap query and surfaced as a clean, documented `422`, not a 500." |
|----------|---|

## 7. View a FHIR R4 Patient

```bash
curl -s http://localhost:8080/api/v1/fhir/Patient/1 \
  -H "Authorization: Bearer $TOKEN" -H 'Accept: application/fhir+json' | jq .
```

Returns a FHIR R4 `Patient` resource with `application/fhir+json`.

| Say this | "Interoperability is real: the same patient is exposed as a standards-compliant FHIR R4 resource via a read facade." See [FHIR_GUIDE.md](FHIR_GUIDE.md). |
|----------|---|

## 8. Open Swagger / OpenAPI

Open `http://localhost:8080/swagger-ui.html`. Walk the grouped endpoints (Auth,
Patients, Providers, Appointments, FHIR) and "Try it out" on `GET /patients`.

| Say this | "The contract is self-documenting and generated from annotations — see [API_BLUEPRINT.md](API_BLUEPRINT.md)." |
|----------|---|

## 9. Mention the Quality Dashboards

| Surface | Where |
|---------|-------|
| Grafana / Prometheus | Started in Step 2a; config in `quality/observability/` |
| Allure / Extent reports | Produced by `automation/` runs |
| JaCoCo coverage | `apps/backend/target/site/jacoco/` |
| Performance / security / a11y / visual | `quality/` (see [REPOSITORY_TOUR.md](REPOSITORY_TOUR.md) §3) |

| Say this | "Beyond functional tests there are performance, security, accessibility, visual, contract, chaos, and observability layers — all in `quality/`." |
|----------|---|

## 10. Shut Down

```bash
# Ctrl-C the backend and frontend, then:
./scripts/stop.sh        # stops the infra stack
```

## Demo Cheat-Sheet

| Step | Action | Endpoint / URL |
|------|--------|----------------|
| 2 | Start | `scripts/start.sh`, `:8080`, `:5173` |
| 3 | Log in | `POST /api/v1/auth/login` |
| 4 | New patient | `POST /api/v1/patients` |
| 5 | Book | `POST /api/v1/appointments` |
| 6 | Double-book → 422 | `POST /api/v1/appointments` (overlap) |
| 7 | FHIR | `GET /api/v1/fhir/Patient/{id}` |
| 8 | Swagger | `/swagger-ui.html` |
| 9 | Dashboards | Grafana + `quality/` |

## Examples

- *5-minute version:* Steps 2b/2c → 3 → 5 → 6 → 8. The `422` and Swagger are the
  highest-signal moments.
- *API-only version:* run the `curl` blocks in Steps 6–7 against a backend started
  with the `dev` profile.

## Future Enhancements

- Record a screencast/GIF of Steps 3–8 and embed it in [PORTFOLIO_GUIDE.md](PORTFOLIO_GUIDE.md).
- Provide a one-command `make demo` that starts backend + frontend together.

## Dependencies

- Relies on `scripts/start.sh`, the backend in `apps/backend`, and the frontend in
  `apps/frontend`.
- Complements [REPOSITORY_TOUR.md](REPOSITORY_TOUR.md) and [FEATURE_MATRIX.md](FEATURE_MATRIX.md).

## References

- [API_BLUEPRINT.md](API_BLUEPRINT.md) · [FHIR_GUIDE.md](FHIR_GUIDE.md) ·
  [BUSINESS_RULES.md](BUSINESS_RULES.md)
- [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md) · [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial (Milestone 10) |
