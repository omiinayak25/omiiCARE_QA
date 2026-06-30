# Patient Management — Manual Test Cases

**Module:** Patients
**Project:** omiiCARE_QA — Milestone 6
**Endpoints under test:** `GET /api/v1/patients?q=&page=&size=`, `GET /api/v1/patients/{id}`, `POST /api/v1/patients`, `PUT /api/v1/patients/{id}`, `DELETE /api/v1/patients/{id}`
**Permissions:** `patient:read` (list/get), `patient:write` (create/update/delete)
**Frontend under test:** Patients page (`add-patient`, `patient-firstName`, `patient-lastName`, `patient-dob`, `patient-gender`, `patient-save`, `patients-table`, `patient-search`, `patients-empty`)

---

## TC-PAT-001 — Register a valid patient (happy path)

| Field | Value |
|---|---|
| **ID** | TC-PAT-001 |
| **Title** | Create patient returns 201 with auto-generated unique MRN |
| **Module** | Patients |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | Authenticated with `patient:write` |
| **Test Data** | firstName=`Aanya`, lastName=`Sharma`, dob=`1990-04-15`, gender=`female` |
| **Steps** | 1. Open Patients page. 2. Click `add-patient`. 3. Fill `patient-firstName`, `patient-lastName`, `patient-dob`, `patient-gender`. 4. Click `patient-save`. |
| **Expected Result** | `POST /api/v1/patients` returns `201`. Response includes an `id` and an auto-generated `mrn`. New row appears in `patients-table`. |
| **Postconditions** | Patient persisted with unique MRN |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-001 |
| **Risk Level** | High |

---

## TC-PAT-002 — Missing firstName fails validation

| Field | Value |
|---|---|
| **ID** | TC-PAT-002 |
| **Title** | Create patient without firstName returns 400 OMII-400 ProblemDetail |
| **Module** | Patients |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `patient:write` |
| **Test Data** | firstName=``, lastName=`Sharma`, dob=`1990-04-15`, gender=`female` |
| **Steps** | 1. Submit `POST /api/v1/patients` with blank firstName. |
| **Expected Result** | `400` with `errorCode = OMII-400` and a ProblemDetail listing the `firstName` field error. No patient created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-002 |
| **Risk Level** | Medium |

---

## TC-PAT-003 — Missing lastName fails validation

| Field | Value |
|---|---|
| **ID** | TC-PAT-003 |
| **Title** | Create patient without lastName returns 400 OMII-400 |
| **Module** | Patients |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `patient:write` |
| **Test Data** | firstName=`Aanya`, lastName=``, dob=`1990-04-15`, gender=`female` |
| **Steps** | 1. Submit `POST /api/v1/patients` with blank lastName. |
| **Expected Result** | `400` with `errorCode = OMII-400`; ProblemDetail references `lastName`. No record created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-002 |
| **Risk Level** | Medium |

---

## TC-PAT-004 — Missing date of birth fails validation

| Field | Value |
|---|---|
| **ID** | TC-PAT-004 |
| **Title** | Create patient without DOB returns 400 OMII-400 |
| **Module** | Patients |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `patient:write` |
| **Test Data** | firstName=`Aanya`, lastName=`Sharma`, dob=``, gender=`female` |
| **Steps** | 1. Submit `POST /api/v1/patients` with empty dob. |
| **Expected Result** | `400 OMII-400`; ProblemDetail references `dateOfBirth`. No record created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-002 |
| **Risk Level** | Medium |

---

## TC-PAT-005 — Future date of birth rejected (boundary)

| Field | Value |
|---|---|
| **ID** | TC-PAT-005 |
| **Title** | DOB in the future is rejected with 400 OMII-400 |
| **Module** | Patients |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `patient:write`; today = 2026-06-30 |
| **Test Data** | firstName=`Future`, lastName=`Born`, dob=`2030-01-01`, gender=`male` |
| **Steps** | 1. Submit `POST /api/v1/patients` with a future dob. |
| **Expected Result** | `400 OMII-400`; ProblemDetail flags `dateOfBirth` as not in the past. No record created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-002 |
| **Risk Level** | Medium |

---

## TC-PAT-006 — Invalid gender value rejected

| Field | Value |
|---|---|
| **ID** | TC-PAT-006 |
| **Title** | Unsupported gender code returns 400 OMII-400 |
| **Module** | Patients |
| **Priority** | P3 |
| **Severity** | Low |
| **Preconditions** | Authenticated with `patient:write` |
| **Test Data** | firstName=`Test`, lastName=`User`, dob=`1990-04-15`, gender=`zzz` |
| **Steps** | 1. Submit `POST /api/v1/patients` with an invalid gender. |
| **Expected Result** | `400 OMII-400`; ProblemDetail references `gender`. No record created. |
| **Postconditions** | No record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-002 |
| **Risk Level** | Low |

---

## TC-PAT-007 — MRN is unique across patients

| Field | Value |
|---|---|
| **ID** | TC-PAT-007 |
| **Title** | Two created patients receive distinct auto-generated MRNs |
| **Module** | Patients |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `patient:write` |
| **Test Data** | Patient A and Patient B with different names |
| **Steps** | 1. Create Patient A (TC-PAT-001 data). 2. Create Patient B with different name/dob. 3. Compare the two `mrn` values. |
| **Expected Result** | Both return `201`; the two MRNs are different and non-empty. |
| **Postconditions** | Two patients persisted with unique MRNs |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-003 |
| **Risk Level** | High |

---

## TC-PAT-008 — List patients (paginated)

| Field | Value |
|---|---|
| **ID** | TC-PAT-008 |
| **Title** | GET /patients returns a paginated collection |
| **Module** | Patients |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `patient:read`; at least one patient exists |
| **Test Data** | `GET /api/v1/patients?page=0&size=10` |
| **Steps** | 1. Call `GET /api/v1/patients?page=0&size=10`. |
| **Expected Result** | `200` with a paginated payload (content array + pagination metadata). Created patients are present in `patients-table`. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-004 |
| **Risk Level** | Medium |

---

## TC-PAT-009 — Search patients by name

| Field | Value |
|---|---|
| **ID** | TC-PAT-009 |
| **Title** | Search via q filters the patient list by name |
| **Module** | Patients |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `patient:read`; patient "Aanya Sharma" exists |
| **Test Data** | `GET /api/v1/patients?q=Aanya` / UI `patient-search`=`Aanya` |
| **Steps** | 1. Enter `Aanya` in `patient-search` (or call API with `q=Aanya`). |
| **Expected Result** | `200`; results contain matching patient(s) only. `patients-table` filtered accordingly. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-005 |
| **Risk Level** | Medium |

---

## TC-PAT-010 — Search by MRN

| Field | Value |
|---|---|
| **ID** | TC-PAT-010 |
| **Title** | Search by a known MRN returns the matching patient |
| **Module** | Patients |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `patient:read`; MRN captured from TC-PAT-001 |
| **Test Data** | `GET /api/v1/patients?q=<MRN>` |
| **Steps** | 1. Search using the MRN value. |
| **Expected Result** | `200`; the exact patient is returned. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-005 |
| **Risk Level** | Low |

---

## TC-PAT-011 — Search with no matches shows empty state

| Field | Value |
|---|---|
| **ID** | TC-PAT-011 |
| **Title** | Non-matching search renders the empty state |
| **Module** | Patients |
| **Priority** | P3 |
| **Severity** | Low |
| **Preconditions** | Authenticated with `patient:read` |
| **Test Data** | `patient-search`=`zzzznomatch` |
| **Steps** | 1. Search for a string that matches nothing. |
| **Expected Result** | `200` with empty content; UI shows `patients-empty`. No table rows. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-005 |
| **Risk Level** | Low |

---

## TC-PAT-012 — Pagination boundary (size limit / page beyond range)

| Field | Value |
|---|---|
| **ID** | TC-PAT-012 |
| **Title** | Page beyond available data returns empty content; size is honored |
| **Module** | Patients |
| **Priority** | P3 |
| **Severity** | Low |
| **Preconditions** | Authenticated with `patient:read`; fewer than 100 patients exist |
| **Test Data** | `?page=99&size=5` and `?page=0&size=2` |
| **Steps** | 1. Call `GET /patients?page=99&size=5`. 2. Call `GET /patients?page=0&size=2`. |
| **Expected Result** | Step 1: `200` empty content (no error). Step 2: `200` with at most 2 items per page. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-004 |
| **Risk Level** | Low |

---

## TC-PAT-013 — Get patient by id

| Field | Value |
|---|---|
| **ID** | TC-PAT-013 |
| **Title** | GET /patients/{id} returns the full patient record |
| **Module** | Patients |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `patient:read`; patient id from TC-PAT-001 |
| **Test Data** | `GET /api/v1/patients/{id}` |
| **Steps** | 1. Call `GET /api/v1/patients/{id}` with a valid id. |
| **Expected Result** | `200` with the patient's fields (name, dob, gender, mrn) matching what was created. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-004 |
| **Risk Level** | Medium |

---

## TC-PAT-014 — Get non-existent patient returns 404

| Field | Value |
|---|---|
| **ID** | TC-PAT-014 |
| **Title** | GET /patients/{unknownId} returns 404 OMII-404 |
| **Module** | Patients |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `patient:read` |
| **Test Data** | `GET /api/v1/patients/00000000-0000-0000-0000-000000000000` |
| **Steps** | 1. Call GET with an id that does not exist. |
| **Expected Result** | `404` with `errorCode = OMII-404` (Resource not found). |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-004 |
| **Risk Level** | Medium |

---

## TC-PAT-015 — Update patient (PUT)

| Field | Value |
|---|---|
| **ID** | TC-PAT-015 |
| **Title** | PUT /patients/{id} updates editable fields |
| **Module** | Patients |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `patient:write`; existing patient id |
| **Test Data** | Updated lastName=`Verma`, gender unchanged |
| **Steps** | 1. Call `PUT /api/v1/patients/{id}` with modified payload. 2. Re-fetch via `GET /patients/{id}`. |
| **Expected Result** | PUT returns `200`. Re-fetch reflects the updated value. MRN remains unchanged. |
| **Postconditions** | Patient record updated |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-006 |
| **Risk Level** | Medium |

---

## TC-PAT-016 — Delete patient & permission-denied create

| Field | Value |
|---|---|
| **ID** | TC-PAT-016 |
| **Title** | DELETE removes the patient; create without patient:write is forbidden |
| **Module** | Patients |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | (a) Authenticated with `patient:write` for delete; (b) a token WITHOUT `patient:write` for the negative step |
| **Test Data** | `DELETE /api/v1/patients/{id}`; `POST /api/v1/patients` from read-only token |
| **Steps** | 1. As writer, `DELETE /api/v1/patients/{id}`. 2. `GET /patients/{id}` → expect 404. 3. As read-only user, attempt `POST /api/v1/patients`. |
| **Expected Result** | Step 1: `200/204`. Step 2: `404 OMII-404`. Step 3: `403 OMII-403` (missing `patient:write`); no patient created. |
| **Postconditions** | Patient removed; no unauthorized creation |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-PAT-007, FR-RBAC-001 |
| **Risk Level** | High |

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
