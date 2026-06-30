# Admin & Audit — Manual Test Cases

**Module:** Admin / Audit / RBAC
**Project:** omiiCARE_QA — Milestone 6
**Permissions:** `audit:read`, `admin:manage`, plus the action permissions that trigger audit events (`patient:write`, `appointment:write`).
**Roles:** SUPER_ADMIN holds all permissions.

---

## TC-ADMIN-001 — Login is captured in the audit trail

| Field | Value |
|---|---|
| **ID** | TC-ADMIN-001 |
| **Title** | A successful login produces an audit event readable with audit:read |
| **Module** | Audit |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Auditor token with `audit:read`; `demo.admin` available |
| **Test Data** | Login as `demo.admin` / `Admin@12345` |
| **Steps** | 1. Perform a successful login (TC-AUTH-001). 2. As a user with `audit:read`, query the audit log. |
| **Expected Result** | An audit entry exists for the login action, recording actor, action, and timestamp. |
| **Postconditions** | Audit record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUDIT-001 |
| **Risk Level** | High |

---

## TC-ADMIN-002 — Patient creation is audited

| Field | Value |
|---|---|
| **ID** | TC-ADMIN-002 |
| **Title** | Creating a patient produces a corresponding audit event |
| **Module** | Audit |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | `patient:write` to create; `audit:read` to verify |
| **Test Data** | Create patient (TC-PAT-001 data) |
| **Steps** | 1. Create a patient via `POST /api/v1/patients`. 2. Query the audit log as an auditor. |
| **Expected Result** | An audit entry records the patient-create action, the actor, and the affected resource id. |
| **Postconditions** | Audit record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUDIT-002 |
| **Risk Level** | High |

---

## TC-ADMIN-003 — Appointment booking is audited

| Field | Value |
|---|---|
| **ID** | TC-ADMIN-003 |
| **Title** | Booking an appointment produces a corresponding audit event |
| **Module** | Audit |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | `appointment:write` to book; `audit:read` to verify |
| **Test Data** | Book appointment (TC-APPT-001 data) |
| **Steps** | 1. Book an appointment via `POST /api/v1/appointments`. 2. Query the audit log as an auditor. |
| **Expected Result** | An audit entry records the appointment-create action, actor, and resource id. |
| **Postconditions** | Audit record persisted |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUDIT-002 |
| **Risk Level** | Medium |

---

## TC-ADMIN-004 — Audit log read denied without audit:read

| Field | Value |
|---|---|
| **ID** | TC-ADMIN-004 |
| **Title** | Reading the audit log without audit:read returns 403 |
| **Module** | Audit / RBAC |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated token WITHOUT `audit:read` |
| **Test Data** | Audit-log read request |
| **Steps** | 1. Authenticate as a role lacking `audit:read`. 2. Attempt to query the audit log. |
| **Expected Result** | `403 OMII-403`. No audit data returned. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-RBAC-001 |
| **Risk Level** | High |

---

## TC-ADMIN-005 — SUPER_ADMIN holds all permissions (RBAC matrix)

| Field | Value |
|---|---|
| **ID** | TC-ADMIN-005 |
| **Title** | SUPER_ADMIN can perform read/write/audit/admin actions |
| **Module** | RBAC |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | Authenticated as SUPER_ADMIN (`demo.admin`) |
| **Test Data** | One representative call per permission family |
| **Steps** | 1. `GET /patients` (patient:read). 2. `POST /patients` (patient:write). 3. `GET /appointments` (appointment:read). 4. `POST /appointments` (appointment:write). 5. Read audit log (audit:read). |
| **Expected Result** | All five succeed (`200`/`201`). No `403` for SUPER_ADMIN. |
| **Postconditions** | Test data created may need cleanup |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-RBAC-002 |
| **Risk Level** | High |

---

## TC-ADMIN-006 — Least-privilege RBAC matrix enforcement

| Field | Value |
|---|---|
| **ID** | TC-ADMIN-006 |
| **Title** | Read-only role is blocked from every write/audit action |
| **Module** | RBAC |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Authenticated token with read permissions only (`patient:read`, `appointment:read`), no write/audit |
| **Test Data** | Write payloads for patient and appointment; audit-log read |
| **Steps** | 1. `GET /patients` → allowed. 2. `POST /patients` → blocked. 3. `POST /appointments` → blocked. 4. Read audit log → blocked. |
| **Expected Result** | Step 1: `200`. Steps 2–4: `403 OMII-403`. No data created or exposed. |
| **Postconditions** | No unauthorized changes |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-RBAC-001 |
| **Risk Level** | High |

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
