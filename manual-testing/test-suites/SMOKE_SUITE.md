# Smoke Test Suite

**Project:** omiiCARE_QA — Milestone 6
**Purpose:** Verify the critical happy path of the system is functional before deeper testing or release promotion.
**Trigger:** Every build/deploy to a test environment; pre-merge gate.
**Estimated duration:** ~20 minutes (manual).
**Stop rule:** Any P1 smoke case failing blocks the build — halt and raise a blocker bug.

---

## Entry Criteria
- Backend and frontend deployed and reachable.
- Seed user `demo.admin` / `Admin@12345` provisioned.
- Database migrated to the Milestone 6 schema.

## Exit / Pass Criteria
- All 8 smoke cases below PASS.
- No `OMII-500` responses observed during the run.
- A patient and an appointment can be created and read end-to-end.

---

## Run Order

| Order | TC ID | Title | Module | Priority | Pass Criteria |
|---|---|---|---|---|---|
| 1 | TC-AUTH-001 | Valid login → access+refresh + Dashboard | Authentication | P1 | 200; tokens issued; lands on `dashboard` |
| 2 | TC-AUTH-008 | Protected endpoint without token → 401 | Authentication | P1 | 401 OMII-401 |
| 3 | TC-PAT-001 | Create patient (auto MRN) | Patients | P1 | 201; unique MRN; row in `patients-table` |
| 4 | TC-PAT-008 | List patients (paginated) | Patients | P1 | 200; created patient present |
| 5 | TC-APPT-001 | Book valid appointment | Appointments | P1 | 201; row in `appointments-table` |
| 6 | TC-APPT-002 | Double-booking → 422 BR-APPT-001 | Appointments | P1 | 422 OMII-422 ruleId BR-APPT-001 |
| 7 | TC-FHIR-001 | FHIR Patient read | FHIR | P1 | 200; resourceType Patient |
| 8 | TC-ADMIN-005 | SUPER_ADMIN full access | RBAC | P1 | All representative calls succeed |

---

## Notes
- Run sequentially; cases 3–7 depend on a valid session from case 1.
- Capture the created patient id and MRN for reuse in cases 4, 5, and 7.
- If case 1 fails, abort the suite — nothing downstream is testable.

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
