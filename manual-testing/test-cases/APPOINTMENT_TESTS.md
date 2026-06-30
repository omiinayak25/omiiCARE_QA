# Appointment Scheduling — Manual Test Cases

**Module:** Appointments
**Project:** omiiCARE_QA — Milestone 6
**Endpoints under test:** `GET /api/v1/appointments`, `POST /api/v1/appointments`, `POST /api/v1/appointments/{id}/cancel`
**Permissions:** `appointment:read`, `appointment:write`
**Business rules:** BR-APPT-001 (no overlapping active booking for the same provider), BR-APPT-002 (end after start), BR-APPT-004 (cannot cancel an already-cancelled appointment); `scheduledStart` must be in the future.
**Frontend under test:** Appointments page (`book-appointment`, `appointment-start`, `appointment-end`, `appointment-save`, `appointment-conflict`, `appointments-table`)

---

## TC-APPT-001 — Book a valid appointment (happy path)

| Field | Value |
|---|---|
| **ID** | TC-APPT-001 |
| **Title** | Booking a future, non-overlapping appointment returns 201 |
| **Module** | Appointments |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | Authenticated with `appointment:write`; valid patient + provider exist |
| **Test Data** | patientId=<valid>, providerId=<valid>, scheduledStart=`2026-07-01T10:00:00Z`, scheduledEnd=`2026-07-01T10:30:00Z` |
| **Steps** | 1. Open Appointments page. 2. Click `book-appointment`. 3. Set `appointment-start` and `appointment-end`. 4. Click `appointment-save`. |
| **Expected Result** | `POST /api/v1/appointments` returns `201` with the created appointment. New row in `appointments-table`. No `appointment-conflict`. |
| **Postconditions** | Appointment persisted in active state |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-APPT-001 |
| **Risk Level** | High |

---

## TC-APPT-002 — Overlapping provider booking rejected (BR-APPT-001)

| Field | Value |
|---|---|
| **ID** | TC-APPT-002 |
| **Title** | Double-booking the same provider for an overlapping slot returns 422 BR-APPT-001 |
| **Module** | Appointments |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | Authenticated with `appointment:write`; an active appointment exists for provider P at 10:00–10:30 |
| **Test Data** | providerId=P, scheduledStart=`2026-07-01T10:15:00Z`, scheduledEnd=`2026-07-01T10:45:00Z` (overlaps) |
| **Steps** | 1. Book the first appointment (TC-APPT-001). 2. Book a second for the same provider overlapping the first. |
| **Expected Result** | `422` with `errorCode = OMII-422` and `ruleId = BR-APPT-001`. UI surfaces `appointment-conflict`. Second appointment NOT created. |
| **Postconditions** | Only the first appointment exists |
| **Automation Status** | Candidate |
| **Requirement Ref** | BR-APPT-001 |
| **Risk Level** | High |

---

## TC-APPT-003 — Adjacent (non-overlapping) booking allowed (boundary)

| Field | Value |
|---|---|
| **ID** | TC-APPT-003 |
| **Title** | Back-to-back slot touching the previous end is permitted |
| **Module** | Appointments |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `appointment:write`; provider P booked 10:00–10:30 |
| **Test Data** | providerId=P, scheduledStart=`2026-07-01T10:30:00Z`, scheduledEnd=`2026-07-01T11:00:00Z` |
| **Steps** | 1. Book provider P for 10:00–10:30. 2. Book provider P for 10:30–11:00. |
| **Expected Result** | `201`. No BR-APPT-001 violation (slots touch but do not overlap). |
| **Postconditions** | Two adjacent appointments exist |
| **Automation Status** | Candidate |
| **Requirement Ref** | BR-APPT-001 |
| **Risk Level** | Medium |

---

## TC-APPT-004 — End before start rejected (BR-APPT-002)

| Field | Value |
|---|---|
| **ID** | TC-APPT-004 |
| **Title** | scheduledEnd earlier than scheduledStart returns 422 BR-APPT-002 |
| **Module** | Appointments |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `appointment:write`; valid patient + provider |
| **Test Data** | scheduledStart=`2026-07-01T11:00:00Z`, scheduledEnd=`2026-07-01T10:00:00Z` |
| **Steps** | 1. Submit `POST /api/v1/appointments` with end before start. |
| **Expected Result** | `422` with `errorCode = OMII-422`, `ruleId = BR-APPT-002` ("Appointment end must be after its start"). Not created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | BR-APPT-002 |
| **Risk Level** | High |

---

## TC-APPT-005 — Equal start and end rejected (boundary, BR-APPT-002)

| Field | Value |
|---|---|
| **ID** | TC-APPT-005 |
| **Title** | Zero-duration appointment (start == end) is rejected |
| **Module** | Appointments |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `appointment:write` |
| **Test Data** | scheduledStart=`2026-07-01T10:00:00Z`, scheduledEnd=`2026-07-01T10:00:00Z` |
| **Steps** | 1. Submit `POST /api/v1/appointments` with identical start and end. |
| **Expected Result** | `422 OMII-422` `ruleId = BR-APPT-002` (end must be strictly after start). Not created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | BR-APPT-002 |
| **Risk Level** | Medium |

---

## TC-APPT-006 — Past start time rejected (validation)

| Field | Value |
|---|---|
| **ID** | TC-APPT-006 |
| **Title** | scheduledStart in the past is rejected |
| **Module** | Appointments |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `appointment:write`; today = 2026-06-30 |
| **Test Data** | scheduledStart=`2026-06-29T09:00:00Z`, scheduledEnd=`2026-06-29T09:30:00Z` |
| **Steps** | 1. Submit `POST /api/v1/appointments` with a past start. |
| **Expected Result** | Request rejected (`400 OMII-400` validation for non-future `scheduledStart`). Not created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-APPT-002 |
| **Risk Level** | High |

---

## TC-APPT-007 — Booking against non-existent patient

| Field | Value |
|---|---|
| **ID** | TC-APPT-007 |
| **Title** | Appointment for an unknown patientId is rejected |
| **Module** | Appointments |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `appointment:write`; valid provider |
| **Test Data** | patientId=`<non-existent uuid>`, valid provider, future slot |
| **Steps** | 1. Submit `POST /api/v1/appointments` referencing a non-existent patient. |
| **Expected Result** | Error response (`404 OMII-404` or `400/422`); appointment NOT created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-APPT-003 |
| **Risk Level** | Medium |

---

## TC-APPT-008 — Booking against non-existent provider

| Field | Value |
|---|---|
| **ID** | TC-APPT-008 |
| **Title** | Appointment for an unknown providerId is rejected |
| **Module** | Appointments |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `appointment:write`; valid patient |
| **Test Data** | providerId=`<non-existent uuid>`, valid patient, future slot |
| **Steps** | 1. Submit `POST /api/v1/appointments` referencing a non-existent provider. |
| **Expected Result** | Error response (`404 OMII-404` or `400/422`); appointment NOT created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-APPT-003 |
| **Risk Level** | Medium |

---

## TC-APPT-009 — List appointments

| Field | Value |
|---|---|
| **ID** | TC-APPT-009 |
| **Title** | GET /appointments returns the appointment collection |
| **Module** | Appointments |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `appointment:read`; at least one appointment exists |
| **Test Data** | `GET /api/v1/appointments` |
| **Steps** | 1. Call `GET /api/v1/appointments`. |
| **Expected Result** | `200` with the collection; previously booked appointments appear in `appointments-table`. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-APPT-004 |
| **Risk Level** | Medium |

---

## TC-APPT-010 — Cancel an active appointment

| Field | Value |
|---|---|
| **ID** | TC-APPT-010 |
| **Title** | POST /appointments/{id}/cancel transitions an active appointment to cancelled |
| **Module** | Appointments |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `appointment:write`; an active appointment id |
| **Test Data** | `POST /api/v1/appointments/{id}/cancel` |
| **Steps** | 1. Book an appointment. 2. Call the cancel endpoint with its id. 3. Re-fetch and check status. |
| **Expected Result** | `200`; status becomes cancelled. |
| **Postconditions** | Appointment is cancelled |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-APPT-005 |
| **Risk Level** | Medium |

---

## TC-APPT-011 — Cancel an already-cancelled appointment (BR-APPT-004)

| Field | Value |
|---|---|
| **ID** | TC-APPT-011 |
| **Title** | Cancelling an already-cancelled appointment returns 422 BR-APPT-004 |
| **Module** | Appointments |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `appointment:write`; appointment already cancelled (TC-APPT-010) |
| **Test Data** | `POST /api/v1/appointments/{id}/cancel` (second call) |
| **Steps** | 1. Cancel an appointment. 2. Call cancel again on the same id. |
| **Expected Result** | `422 OMII-422` `ruleId = BR-APPT-004` ("Appointment is already cancelled"). State unchanged. |
| **Postconditions** | Appointment remains cancelled |
| **Automation Status** | Candidate |
| **Requirement Ref** | BR-APPT-004 |
| **Risk Level** | High |

---

## TC-APPT-012 — Cancel a non-existent appointment

| Field | Value |
|---|---|
| **ID** | TC-APPT-012 |
| **Title** | Cancel on an unknown appointment id returns 404 |
| **Module** | Appointments |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `appointment:write` |
| **Test Data** | `POST /api/v1/appointments/00000000-0000-0000-0000-000000000000/cancel` |
| **Steps** | 1. Call cancel with an id that does not exist. |
| **Expected Result** | `404 OMII-404`. No state change. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-APPT-005 |
| **Risk Level** | Low |

---

## TC-APPT-013 — Booking denied without appointment:write (RBAC)

| Field | Value |
|---|---|
| **ID** | TC-APPT-013 |
| **Title** | Creating an appointment without appointment:write returns 403 |
| **Module** | Appointments / RBAC |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Token WITHOUT `appointment:write` |
| **Test Data** | Valid future-slot payload |
| **Steps** | 1. Authenticate as a role lacking `appointment:write`. 2. Call `POST /api/v1/appointments`. |
| **Expected Result** | `403 OMII-403`; no appointment created. |
| **Postconditions** | No data change |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-RBAC-001 |
| **Risk Level** | High |

---

## TC-APPT-014 — Appointments require authentication

| Field | Value |
|---|---|
| **ID** | TC-APPT-014 |
| **Title** | Unauthenticated GET /appointments returns 401 |
| **Module** | Appointments / RBAC |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Backend running |
| **Test Data** | No `Authorization` header |
| **Steps** | 1. Call `GET /api/v1/appointments` with no bearer token. |
| **Expected Result** | `401 OMII-401`. No data returned. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-RBAC-001 |
| **Risk Level** | Medium |

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
