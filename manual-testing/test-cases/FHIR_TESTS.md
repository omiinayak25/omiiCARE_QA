# FHIR Patient Read — Manual Test Cases

**Module:** FHIR
**Project:** omiiCARE_QA — Milestone 6
**Endpoint under test:** `GET /api/v1/fhir/Patient/{id}`
**Contract:** Response media type `application/fhir+json`; body `resourceType = "Patient"`; `gender` lowercased; `identifier` carries the MRN; `name` mapped from first/last name.

---

## TC-FHIR-001 — Read a patient as a FHIR Patient resource

| Field | Value |
|---|---|
| **ID** | TC-FHIR-001 |
| **Title** | GET /fhir/Patient/{id} returns a FHIR Patient with resourceType Patient |
| **Module** | FHIR |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | Authenticated with `patient:read`; an existing patient id (from TC-PAT-001) |
| **Test Data** | `GET /api/v1/fhir/Patient/{id}` |
| **Steps** | 1. Create a patient and capture its id. 2. Call `GET /api/v1/fhir/Patient/{id}`. |
| **Expected Result** | `200` with JSON body where `resourceType = "Patient"` and `id` corresponds to the requested patient. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-FHIR-001 |
| **Risk Level** | High |

---

## TC-FHIR-002 — Response uses application/fhir+json content type

| Field | Value |
|---|---|
| **ID** | TC-FHIR-002 |
| **Title** | FHIR Patient read returns Content-Type application/fhir+json |
| **Module** | FHIR |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `patient:read`; existing patient id |
| **Test Data** | `GET /api/v1/fhir/Patient/{id}` |
| **Steps** | 1. Call the FHIR read endpoint. 2. Inspect the `Content-Type` response header. |
| **Expected Result** | `Content-Type` is `application/fhir+json` (not plain `application/json`). |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-FHIR-002 |
| **Risk Level** | Medium |

---

## TC-FHIR-003 — Name mapped from first/last name

| Field | Value |
|---|---|
| **ID** | TC-FHIR-003 |
| **Title** | FHIR name array reflects the stored given/family name |
| **Module** | FHIR |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `patient:read`; patient firstName=`Aanya`, lastName=`Sharma` |
| **Test Data** | `GET /api/v1/fhir/Patient/{id}` |
| **Steps** | 1. Read the FHIR Patient. 2. Inspect the `name` element. |
| **Expected Result** | `name[0].family` = `Sharma` and `name[0].given` includes `Aanya` (mapped from the source record). |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-FHIR-003 |
| **Risk Level** | Medium |

---

## TC-FHIR-004 — Identifier carries the MRN

| Field | Value |
|---|---|
| **ID** | TC-FHIR-004 |
| **Title** | FHIR identifier element contains the patient MRN |
| **Module** | FHIR |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Authenticated with `patient:read`; MRN captured at creation |
| **Test Data** | `GET /api/v1/fhir/Patient/{id}` |
| **Steps** | 1. Read the FHIR Patient. 2. Inspect the `identifier` array. |
| **Expected Result** | `identifier[].value` equals the patient's auto-generated MRN. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-FHIR-003 |
| **Risk Level** | Medium |

---

## TC-FHIR-005 — Gender is lowercased per FHIR contract

| Field | Value |
|---|---|
| **ID** | TC-FHIR-005 |
| **Title** | FHIR gender value is lowercased (e.g. female) |
| **Module** | FHIR |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated with `patient:read`; patient created with gender `Female`/`FEMALE` |
| **Test Data** | `GET /api/v1/fhir/Patient/{id}` |
| **Steps** | 1. Create a patient with a non-lowercase gender. 2. Read the FHIR Patient. 3. Inspect `gender`. |
| **Expected Result** | `gender` is lowercase (`female`/`male`/`other`/`unknown`), matching FHIR AdministrativeGender. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-FHIR-004 |
| **Risk Level** | High |

---

## TC-FHIR-006 — Unknown id and unauthenticated access

| Field | Value |
|---|---|
| **ID** | TC-FHIR-006 |
| **Title** | FHIR read returns 404 for unknown id and 401 when unauthenticated |
| **Module** | FHIR / RBAC |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Backend running |
| **Test Data** | `GET /api/v1/fhir/Patient/00000000-0000-0000-0000-000000000000`; same call without bearer token |
| **Steps** | 1. As an authenticated user, GET the FHIR Patient with an unknown id. 2. GET a valid FHIR Patient URL with NO `Authorization` header. |
| **Expected Result** | Step 1: `404 OMII-404`. Step 2: `401 OMII-401`. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-FHIR-001, FR-RBAC-001 |
| **Risk Level** | High |

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
