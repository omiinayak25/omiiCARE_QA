# Negative & Boundary Test Suite

**Project:** omiiCARE_QA — Milestone 6
**Purpose:** Concentrate on invalid input, error contracts, unauthorized access, duplicates, and boundary values. These cases protect the system's failure behavior and error-code contract.
**Trigger:** Every release candidate; after any change to validation, error handling, or RBAC.

---

## Entry Criteria
- All referenced positive-path data and seed users available.
- Error-code contract (OMII-4xx/OMII-422) is the source of truth for assertions.

## Exit Criteria
- Every negative case returns the documented error code (never `200`, never `OMII-500`).
- No invalid record is persisted as a side effect of a rejected request.

---

## Referenced Negative & Boundary Cases (from module suites)

| TC ID | Category | Expected |
|---|---|---|
| TC-AUTH-002 | Invalid credentials | 401 OMII-401-1 |
| TC-AUTH-003 | Unknown user (no enumeration) | 401 OMII-401-1 |
| TC-AUTH-004 | Empty input | Blocked / 400 OMII-400 |
| TC-AUTH-006 | Reused rotated refresh token | 401 |
| TC-AUTH-009 | Malformed JWT | 401 |
| TC-AUTH-010 | Expired session | 401 then recover |
| TC-AUTH-012 | Authorized vs unauthorized | 403 OMII-403 |
| TC-PAT-002..006 | Field validation / future DOB / bad gender | 400 OMII-400 |
| TC-PAT-011 | No-match search | Empty state |
| TC-PAT-012 | Pagination boundary | 200 empty / size honored |
| TC-PAT-014 | Unknown patient id | 404 OMII-404 |
| TC-PAT-016 | Create without patient:write | 403 OMII-403 |
| TC-APPT-002 | Overlap | 422 BR-APPT-001 |
| TC-APPT-004 | End before start | 422 BR-APPT-002 |
| TC-APPT-005 | Zero duration | 422 BR-APPT-002 |
| TC-APPT-006 | Past start | 400 OMII-400 |
| TC-APPT-007/008 | Unknown patient/provider | 404/400/422 |
| TC-APPT-011 | Cancel cancelled | 422 BR-APPT-004 |
| TC-APPT-013 | Book without permission | 403 OMII-403 |
| TC-FHIR-006 | Unknown id / unauthenticated | 404 / 401 |
| TC-ADMIN-004 | Audit read denied | 403 OMII-403 |

---

## Additional Boundary Cases (new)

### TC-PAT-017 — First name at maximum length boundary

| Field | Value |
|---|---|
| **ID** | TC-PAT-017 |
| **Title** | Over-long firstName is rejected at the length boundary |
| **Module** | Patients |
| **Priority** | P3 |
| **Severity** | Low |
| **Preconditions** | Authenticated with `patient:write` |
| **Test Data** | firstName = a 300-character string; other fields valid |
| **Steps** | 1. Submit `POST /api/v1/patients` with an over-length firstName. |
| **Expected Result** | `400 OMII-400` length-constraint violation; no record created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-002 |
| **Risk Level** | Low |

### TC-PAT-018 — Negative / zero pagination parameters

| Field | Value |
|---|---|
| **ID** | TC-PAT-018 |
| **Title** | Invalid page/size values are handled gracefully |
| **Module** | Patients |
| **Priority** | P3 |
| **Severity** | Low |
| **Preconditions** | Authenticated with `patient:read` |
| **Test Data** | `?page=-1&size=0` and `?size=100000` |
| **Steps** | 1. Call `GET /patients?page=-1&size=0`. 2. Call `GET /patients?size=100000`. |
| **Expected Result** | Request is rejected (`400`) or normalized to a safe default page/size; never an `OMII-500`. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-004 |
| **Risk Level** | Low |

### TC-APPT-015 — DOB / appointment exactly at "now" boundary

| Field | Value |
|---|---|
| **ID** | TC-APPT-015 |
| **Title** | scheduledStart equal to current time treated as non-future |
| **Module** | Appointments |
| **Priority** | P3 |
| **Severity** | Low |
| **Preconditions** | Authenticated with `appointment:write` |
| **Test Data** | scheduledStart = current timestamp; valid end |
| **Steps** | 1. Submit `POST /api/v1/appointments` with start == now. |
| **Expected Result** | Consistent behavior with the "future start" rule (rejected as non-future, `400 OMII-400`); no flaky pass. |
| **Postconditions** | No record persisted |
| **Automation Status** | Manual |
| **Requirement Ref** | FR-APPT-002 |
| **Risk Level** | Low |

### TC-PAT-019 — Malformed JSON body

| Field | Value |
|---|---|
| **ID** | TC-PAT-019 |
| **Title** | Malformed JSON returns 400 OMII-400-1 (Malformed request) |
| **Module** | Patients |
| **Priority** | P3 |
| **Severity** | Low |
| **Preconditions** | Authenticated with `patient:write` |
| **Test Data** | Raw body = `{ "firstName": "A",` (truncated/invalid JSON) |
| **Steps** | 1. POST a syntactically invalid JSON body to `/api/v1/patients`. |
| **Expected Result** | `400` with `errorCode = OMII-400-1` (Malformed request). No record created. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-002 |
| **Risk Level** | Low |

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
