# API Functional Checklist

**Project:** omiiCARE_QA — Milestone 6
**Scope:** Backend REST + FHIR endpoint verification under `/api/v1`.
**Usage:** Tick each item per build. Assertions reference the OMII-* error-code contract.

---

## Authentication

| # | Check | Endpoint | Expected | Pass/Fail |
|---|---|---|---|---|
| 1 | Valid login issues access + refresh JWT | `POST /auth/login` | 200 + both tokens | |
| 2 | Wrong credentials rejected | `POST /auth/login` | 401 OMII-401-1 | |
| 3 | Refresh rotates token pair | `POST /auth/refresh` | 200 + new pair | |
| 4 | Old refresh token cannot be reused | `POST /auth/refresh` | 401 | |
| 5 | /me with valid token returns identity | `GET /auth/me` | 200 + principal | |
| 6 | /me without token rejected | `GET /auth/me` | 401 OMII-401 | |
| 7 | Malformed bearer token rejected | any protected | 401 | |

## Patients

| # | Check | Endpoint | Expected | Pass/Fail |
|---|---|---|---|---|
| 8 | Create valid patient | `POST /patients` | 201 + auto MRN | |
| 9 | Validation failure returns ProblemDetail | `POST /patients` | 400 OMII-400 | |
| 10 | MRN unique across patients | `POST /patients` | distinct MRNs | |
| 11 | List is paginated | `GET /patients?page=&size=` | 200 + metadata | |
| 12 | Search by q (name / MRN) | `GET /patients?q=` | 200 filtered | |
| 13 | Get by id | `GET /patients/{id}` | 200 | |
| 14 | Unknown id | `GET /patients/{id}` | 404 OMII-404 | |
| 15 | Update | `PUT /patients/{id}` | 200, MRN unchanged | |
| 16 | Delete | `DELETE /patients/{id}` | 200/204 | |
| 17 | Create without patient:write | `POST /patients` | 403 OMII-403 | |

## Providers

| # | Check | Endpoint | Expected | Pass/Fail |
|---|---|---|---|---|
| 18 | List providers (any authenticated) | `GET /providers` | 200 read-only | |
| 19 | Unauthenticated list | `GET /providers` | 401 | |

## Appointments

| # | Check | Endpoint | Expected | Pass/Fail |
|---|---|---|---|---|
| 20 | Book valid appointment | `POST /appointments` | 201 | |
| 21 | Overlap rejected | `POST /appointments` | 422 OMII-422 BR-APPT-001 | |
| 22 | End before start rejected | `POST /appointments` | 422 BR-APPT-002 | |
| 23 | Past start rejected | `POST /appointments` | 400 OMII-400 | |
| 24 | List appointments | `GET /appointments` | 200 | |
| 25 | Cancel active appointment | `POST /appointments/{id}/cancel` | 200 cancelled | |
| 26 | Cancel already-cancelled | `POST /appointments/{id}/cancel` | 422 BR-APPT-004 | |
| 27 | Book without appointment:write | `POST /appointments` | 403 OMII-403 | |

## FHIR

| # | Check | Endpoint | Expected | Pass/Fail |
|---|---|---|---|---|
| 28 | FHIR Patient read | `GET /fhir/Patient/{id}` | 200 resourceType Patient | |
| 29 | Content type | `GET /fhir/Patient/{id}` | application/fhir+json | |
| 30 | Gender lowercased | `GET /fhir/Patient/{id}` | lowercase gender | |
| 31 | Identifier carries MRN | `GET /fhir/Patient/{id}` | identifier = MRN | |
| 32 | Unknown id | `GET /fhir/Patient/{id}` | 404 OMII-404 | |

## RBAC & Audit

| # | Check | Expected | Pass/Fail |
|---|---|---|---|
| 33 | Unauthenticated → 401, authenticated-but-missing-permission → 403 | distinct codes | |
| 34 | SUPER_ADMIN can perform all action families | all succeed | |
| 35 | Audit log readable only with audit:read | 403 otherwise | |
| 36 | Login / patient-create / appointment-book produce audit events | events present | |

## Error Contract (cross-cutting)

| # | Check | Expected | Pass/Fail |
|---|---|---|---|
| 37 | All errors use ProblemDetail with an OMII-* code | structured | |
| 38 | No OMII-500 / stack trace leaked to client on negative paths | clean | |
| 39 | 422 responses include the correct ruleId | BR-APPT-* | |

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
