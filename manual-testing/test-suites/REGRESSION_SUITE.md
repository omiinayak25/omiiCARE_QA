# Regression Test Suite

**Project:** omiiCARE_QA — Milestone 6
**Purpose:** Full functional coverage across all modules to detect regressions introduced by changes.
**Trigger:** Before each release candidate; after any change touching auth, patients, appointments, FHIR, or RBAC.
**Estimated duration:** ~2.5 hours (manual, full pass).

---

## Entry Criteria
- Smoke suite has passed on the same build.
- Test users provisioned for SUPER_ADMIN, a write role, a read-only role, and an auditor.

## Exit Criteria
- All P1 and P2 cases executed; P1 pass rate 100%, P2 pass rate ≥ 95%.
- All defects logged with severity; no open Critical/High blocking release.

---

## Coverage by Module

### Authentication (run frequency: every release)
| TC ID | Title | Priority |
|---|---|---|
| TC-AUTH-001 | Valid login | P1 |
| TC-AUTH-002 | Wrong password → 401 OMII-401-1 | P1 |
| TC-AUTH-003 | Unknown username → 401 | P1 |
| TC-AUTH-004 | Empty fields blocked | P2 |
| TC-AUTH-005 | Refresh rotation | P1 |
| TC-AUTH-006 | Old refresh token rejected | P1 |
| TC-AUTH-007 | /auth/me valid | P1 |
| TC-AUTH-008 | /auth/me no token → 401 | P1 |
| TC-AUTH-009 | Malformed token → 401 | P1 |
| TC-AUTH-010 | Expired token + refresh recovery | P2 |
| TC-AUTH-011 | Logout terminates session | P2 |
| TC-AUTH-012 | Authenticated-unauthorized → 403 | P1 |

### Patients (run frequency: every release)
| TC ID | Title | Priority |
|---|---|---|
| TC-PAT-001 | Create valid patient | P1 |
| TC-PAT-002..006 | Field validation failures (firstName/lastName/dob/future-dob/gender) | P1–P3 |
| TC-PAT-007 | MRN uniqueness | P1 |
| TC-PAT-008 | List paginated | P1 |
| TC-PAT-009 | Search by name | P2 |
| TC-PAT-010 | Search by MRN | P2 |
| TC-PAT-011 | Empty search state | P3 |
| TC-PAT-012 | Pagination boundary | P3 |
| TC-PAT-013 | Get by id | P1 |
| TC-PAT-014 | Get unknown id → 404 | P2 |
| TC-PAT-015 | Update (PUT) | P1 |
| TC-PAT-016 | Delete + permission-denied create | P1 |

### Appointments (run frequency: every release)
| TC ID | Title | Priority |
|---|---|---|
| TC-APPT-001 | Book valid | P1 |
| TC-APPT-002 | Double-booking → BR-APPT-001 | P1 |
| TC-APPT-003 | Adjacent slot allowed | P2 |
| TC-APPT-004 | End before start → BR-APPT-002 | P1 |
| TC-APPT-005 | Zero duration → BR-APPT-002 | P2 |
| TC-APPT-006 | Past start rejected | P1 |
| TC-APPT-007 | Unknown patient | P2 |
| TC-APPT-008 | Unknown provider | P2 |
| TC-APPT-009 | List appointments | P1 |
| TC-APPT-010 | Cancel active | P1 |
| TC-APPT-011 | Cancel cancelled → BR-APPT-004 | P1 |
| TC-APPT-012 | Cancel unknown → 404 | P2 |
| TC-APPT-013 | Book denied without permission | P1 |
| TC-APPT-014 | Unauthenticated list → 401 | P2 |

### FHIR (run frequency: every release that touches mapping or patient schema)
| TC ID | Title | Priority |
|---|---|---|
| TC-FHIR-001 | FHIR Patient read | P1 |
| TC-FHIR-002 | fhir+json content type | P1 |
| TC-FHIR-003 | Name mapping | P2 |
| TC-FHIR-004 | Identifier = MRN | P2 |
| TC-FHIR-005 | Gender lowercased | P1 |
| TC-FHIR-006 | Unknown id / unauthenticated | P1 |

### Admin & Audit / RBAC (run frequency: every release)
| TC ID | Title | Priority |
|---|---|---|
| TC-ADMIN-001 | Login audited | P1 |
| TC-ADMIN-002 | Patient create audited | P1 |
| TC-ADMIN-003 | Appointment booking audited | P2 |
| TC-ADMIN-004 | Audit read denied without audit:read | P1 |
| TC-ADMIN-005 | SUPER_ADMIN full access | P1 |
| TC-ADMIN-006 | Least-privilege matrix | P1 |

---

## Suggested Frequency Matrix

| Suite scope | When |
|---|---|
| Full regression (all above) | Release candidate, major refactor |
| Module-targeted regression | Any PR touching that module |
| P1-only regression | Hotfix verification |

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
