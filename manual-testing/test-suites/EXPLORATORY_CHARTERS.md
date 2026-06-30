# Exploratory Testing Charters (Session-Based)

**Project:** omiiCARE_QA — Milestone 6
**Method:** Session-Based Test Management (SBTM). Each charter is a time-boxed, focused investigation. Record findings, bugs, questions, and new test-case ideas in the notes.

**Charter template**

```
Charter:    <mission — explore X to discover Y>
Areas:      <features / endpoints / data in scope>
Duration:   <time box, e.g. 60 min>
Tester:     <name>          Date: <date>          Build: <id>
Setup:      <preconditions / accounts / data>
Notes:      <observations, paths taken>
Bugs:       <IDs + one-liners>
Issues/Q:   <open questions, environment problems>
New cases:  <candidate TC ideas to formalize>
Coverage:   <% of charter explored; what was NOT covered>
```

---

## CH-01 — Patient Registration Robustness

| Field | Value |
|---|---|
| **Charter** | Explore patient registration with unusual names, dates, genders, and Unicode to discover validation gaps and persistence issues. |
| **Areas** | `POST /api/v1/patients`, Patients page form (`patient-firstName/lastName/dob/gender/save`), MRN generation. |
| **Duration** | 60 min |
| **Setup** | `patient:write` session; clean patient table preferred. |
| **Notes (seed ideas)** | Unicode/emoji names; leading/trailing spaces; very old DOB (1900); DOB = today; duplicate identical patients (does each get a unique MRN?); rapid double-submit of the form (race / duplicate). |
| **Reference cases** | TC-PAT-001, TC-PAT-002..007, TC-PAT-017 |

---

## CH-02 — Appointment Scheduling Conflicts

| Field | Value |
|---|---|
| **Charter** | Explore overlap, adjacency, and timezone handling around provider booking to stress BR-APPT-001/002. |
| **Areas** | `POST /api/v1/appointments`, cancel endpoint, Appointments page (`appointment-start/end/save/conflict`). |
| **Duration** | 60 min |
| **Setup** | `appointment:write`; one provider with several existing bookings. |
| **Notes (seed ideas)** | Exact-touch boundaries (end==next start); appointment fully containing another; identical start/end; different timezones for start vs end; cancel then rebook the freed slot; concurrent booking of the same slot. |
| **Reference cases** | TC-APPT-001..005, TC-APPT-010, TC-APPT-011 |

---

## CH-03 — RBAC Boundaries

| Field | Value |
|---|---|
| **Charter** | Probe the permission model to find any action reachable without the required permission, and confirm 401 vs 403 are returned correctly. |
| **Areas** | All write/audit endpoints across patients, appointments, audit; SUPER_ADMIN vs read-only vs auditor. |
| **Duration** | 75 min |
| **Setup** | Tokens for SUPER_ADMIN, write-only, read-only, auditor. |
| **Notes (seed ideas)** | Read-only token on every write route; auditor on patient/appointment writes; missing vs invalid vs expired token (expect 401 not 403); does any endpoint leak data in the 403 body? cross-check the permission matrix end to end. |
| **Reference cases** | TC-AUTH-008/009/012, TC-PAT-016, TC-APPT-013, TC-ADMIN-004/005/006 |

---

## CH-04 — FHIR Mapping Fidelity

| Field | Value |
|---|---|
| **Charter** | Explore the FHIR Patient projection to confirm field mapping fidelity and contract correctness. |
| **Areas** | `GET /api/v1/fhir/Patient/{id}`, content type, resourceType, identifier, name, gender. |
| **Duration** | 45 min |
| **Setup** | `patient:read`; patients with varied gender casing and name characters. |
| **Notes (seed ideas)** | Mixed-case gender → lowercased? gender values outside FHIR set; patient with special characters in name; identifier carries MRN with correct value; content type is exactly `application/fhir+json`; unknown id → 404 shape. |
| **Reference cases** | TC-FHIR-001..006 |

---

## CH-05 — Session & Token Handling

| Field | Value |
|---|---|
| **Charter** | Explore the token lifecycle — issue, refresh rotation, reuse, expiry, logout — to find session-fixation or stale-token weaknesses. |
| **Areas** | `/auth/login`, `/auth/refresh`, `/auth/me`, logout, client token storage. |
| **Duration** | 60 min |
| **Setup** | Fresh login; ability to observe network/token values. |
| **Notes (seed ideas)** | Reuse an old refresh token after rotation; refresh with an access token by mistake; access protected route with expired access token; logout then replay a captured token; two parallel sessions for the same user. |
| **Reference cases** | TC-AUTH-005/006/010/011 |

---

## CH-06 — Error Contract & ProblemDetail Consistency

| Field | Value |
|---|---|
| **Charter** | Explore error responses across modules to confirm a consistent ProblemDetail shape and correct OMII-* error codes. |
| **Areas** | Validation (400), auth (401), forbidden (403), not-found (404), business-rule (422) responses. |
| **Duration** | 45 min |
| **Setup** | Any authenticated session plus negative inputs from the negative/boundary suite. |
| **Notes (seed ideas)** | Every error returns a structured ProblemDetail; codes match the contract (OMII-400/400-1/401/401-1/403/404/409/422); 422 carries the correct `ruleId`; no stack traces or OMII-500 leak to the client. |
| **Reference cases** | NEGATIVE_BOUNDARY_SUITE references |

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
