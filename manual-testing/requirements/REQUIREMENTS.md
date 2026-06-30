# Requirements Catalogue — omiiCARE_QA (Milestone 6)

> **Living requirements baseline.** This is the enumerated, testable requirement
> catalogue for the **implemented** omiiCARE_QA system. Every requirement carries a
> stable ID, statement, priority, source, and acceptance criteria so that each maps
> forward to manual test cases (`TC-*`) and automated suites through the
> [RTM](../rtm/RTM.md). Business rules trace to
> [docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md); the strategy that governs
> verification is [docs/TEST_STRATEGY.md](../../docs/TEST_STRATEGY.md).

## Purpose

- Enumerate the requirements that the v1.0 implemented surface must satisfy.
- Give every requirement acceptance criteria precise enough to author a positive
  and a negative test case.
- Distinguish **Implemented** requirements (testable now) from **(Future)**
  requirements that are documented in the business rules but not yet built.

## Scope of the Implemented System (Ground Truth)

| Area | Implemented surface |
|------|---------------------|
| Auth | `POST /api/v1/auth/login`, `POST /api/v1/auth/refresh`, `GET /api/v1/auth/me`; JWT; demo login `demo.admin / Admin@12345` |
| Patients | `GET/POST/PUT/DELETE /api/v1/patients`, `?q=` search, pagination |
| Providers | `GET /api/v1/providers` |
| Appointments | `GET/POST /api/v1/appointments`, `POST /api/v1/appointments/{id}/cancel` |
| FHIR | `GET /api/v1/fhir/Patient/{id}` |
| RBAC permissions | `patient:read/write`, `appointment:read/write`, `audit:read`, `admin:manage` |
| Business rules enforced | BR-APPT-001 no double-booking (422), BR-APPT-002 end-after-start, BR-APPT-004 already-cancelled |
| Errors | RFC 7807 ProblemDetail with `errorCode` (e.g. `OMII-400`) |
| Frontend | Login, Dashboard, Patients (list/search/register dialog), Appointments (list/book dialog), 403, 404; stable `data-testid` selectors |

## ID Conventions

| Prefix | Type | Example |
|--------|------|---------|
| `BR-*` | Business rule (see [BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md)) | `BR-APPT-003` |
| `FR-AUTH-*` / `FR-PAT-*` / `FR-APPT-*` / `FR-FHIR-*` | Functional requirement | `FR-PAT-002` |
| `NFR-*` | Non-functional requirement | `NFR-REL-001` |
| `SEC-*` | Security requirement | `SEC-AUTH-001` |
| `A11Y-*` | Accessibility requirement | `A11Y-UI-001` |
| `PERF-*` | Performance requirement | `PERF-API-001` |
| `FHIR-*` | FHIR R4 conformance | `FHIR-PAT-001` |
| `HL7-*` | HL7 v2 conformance | `HL7-ADT-001` |

Priority scale: **Critical / High / Medium / Low** (risk-driven, per
[RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md)).

---

## 1. Business Requirements (BR-*)

Sourced verbatim from [docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md). Only
rules whose enforcement is observable on the implemented surface are testable now;
the remainder are marked **(Future)** and held for later milestones.

| ID | Statement (summary) | Priority | Source | Acceptance Criteria | Status |
|----|---------------------|----------|--------|---------------------|--------|
| BR-IDENT-002 | Registration captures legal name, DOB, sex/gender; DOB MUST NOT be in the future | Critical | BR-IDENT-002 | POST patient with future DOB → 422 `OMII-400`; valid DOB → 201 | Implemented |
| BR-IDENT-005 | A patient record MUST NOT be hard-deleted; DELETE is a soft state change | High | BR-IDENT-005 | DELETE patient → record deactivated, still retrievable by ID/audit, not in default active list | Implemented |
| BR-IDENT-001 | MRN unique per tenant | Critical | BR-IDENT-001 | Duplicate MRN within tenant → rejected | (Future) |
| BR-IDENT-003 | Duplicate-detection (name+DOB+contact) before create | High | BR-IDENT-003 | Candidate surface on near-duplicate | (Future) |
| BR-APPT-001 | Appointment references valid patient + provider within tenant | Critical | BR-APPT-001 | POST appointment with unknown patient/provider → 404/422 | Implemented |
| BR-APPT-002 | Appointment end MUST be after start | Critical | BR-APPT-002 | `end <= start` → 422 `OMII-400`; `end > start` → 201 | Implemented |
| BR-APPT-003 | Provider MUST NOT be double-booked (overlap) | Critical | BR-APPT-003 | Overlapping BOOKED slot for same provider → 422 (no-double-booking); non-overlap → 201 | Implemented |
| BR-APPT-006 | Cancellation records reason + actor; slot released | High | BR-APPT-006 | POST cancel with reason → appointment CANCELLED, slot bookable again | Implemented |
| BR-APPT-004-IMPL | An already-cancelled appointment MUST NOT be cancelled again | High | BR-APPT (impl) | POST cancel on CANCELLED appointment → 422 already-cancelled | Implemented |
| BR-APPT-005 | Reschedule re-runs availability + double-booking checks | High | BR-APPT-005 | Reschedule into overlap → rejected | (Future) |
| BR-APPT-009 | Lifecycle BOOKED→CHECKED_IN→IN_PROGRESS→COMPLETED; illegal transitions rejected | High | BR-APPT-009 | Illegal transition → rejected | (Future) |
| BR-RBAC-001 | Every API op requires authenticated principal + explicit permission; default deny | Critical | BR-RBAC-001 | Unauthenticated request → 401; missing permission → 403 | Implemented |
| BR-RBAC-002 | Authorization is permission-based, checked per endpoint | Critical | BR-RBAC-002 | `patient:write` required for POST/PUT/DELETE patients | Implemented |
| BR-CONS-004 | Every read of PHI MUST be access-logged | High | BR-CONS-004 | GET patient emits access-audit event (actor, patient, result) | (Future) |
| BR-AUDIT-003 | Auth events (login success/failure, refresh, permission denial) audited | High | BR-AUDIT-003 | Login failure + permission denial produce audit records | (Future) |
| BR-TENANT-002 | Request MUST NOT read/write another tenant's data | Critical | BR-TENANT-002 | Cross-tenant patient fetch → 403/404 | (Future) |
| BR-CONS-005 | All data synthetic and PHI-safe | Critical | BR-CONS-005 | No real PHI present in any environment | Implemented (policy) |

---

## 2. Functional Requirements — Authentication (FR-AUTH-*)

| ID | Statement | Priority | Source | Acceptance Criteria |
|----|-----------|----------|--------|---------------------|
| FR-AUTH-001 | The system authenticates valid credentials and issues a JWT access token | Critical | `POST /auth/login`, BR-RBAC-001 | `demo.admin/Admin@12345` → 200 + access token (+ refresh); token decodes with subject and permissions |
| FR-AUTH-002 | The system rejects invalid credentials without issuing a token | Critical | `POST /auth/login`, SEC | Wrong password → 401, no token, ProblemDetail; message does not reveal which field failed |
| FR-AUTH-003 | The system rejects malformed/empty login payloads | High | `POST /auth/login` | Missing username or password → 422 `OMII-400` ProblemDetail |
| FR-AUTH-004 | A valid refresh token mints a new access token | High | `POST /auth/refresh` | Valid refresh token → 200 + new access token |
| FR-AUTH-005 | An invalid/expired refresh token is rejected | High | `POST /auth/refresh` | Tampered/expired refresh token → 401 |
| FR-AUTH-006 | `GET /auth/me` returns the authenticated principal and permissions | High | `GET /auth/me` | With valid bearer → 200 + identity + permission set; without → 401 |
| FR-AUTH-007 | Protected endpoints reject requests with no/invalid bearer token | Critical | RBAC | No `Authorization` header → 401; garbage token → 401 |
| FR-AUTH-008 | UI login screen authenticates and routes to the Dashboard | Critical | Frontend Login | Valid login via UI → Dashboard renders; invalid → inline error, stays on login |

---

## 3. Functional Requirements — Patients (FR-PAT-*)

| ID | Statement | Priority | Source | Acceptance Criteria |
|----|-----------|----------|--------|---------------------|
| FR-PAT-001 | The system lists patients with pagination | High | `GET /patients` | 200 + paged collection; page/size honoured; total count present |
| FR-PAT-002 | The system registers a new patient with valid demographics | Critical | `POST /patients`, BR-IDENT-002 | Valid name + DOB + sex → 201 + created resource with ID |
| FR-PAT-003 | The system rejects registration with a future DOB | Critical | `POST /patients`, BR-IDENT-002 | Future DOB → 422 `OMII-400` ProblemDetail |
| FR-PAT-004 | The system rejects registration with missing required fields | High | `POST /patients` | Missing name/DOB → 422 `OMII-400` |
| FR-PAT-005 | The system retrieves a single patient by ID | High | `GET /patients/{id}` | Known ID → 200 + resource; unknown ID → 404 |
| FR-PAT-006 | The system updates an existing patient | High | `PUT /patients/{id}` | Valid update → 200 + updated fields persisted |
| FR-PAT-007 | The system soft-deletes (deactivates) a patient | High | `DELETE /patients/{id}`, BR-IDENT-005 | DELETE → 200/204, patient no longer in active list, history retained |
| FR-PAT-008 | The system searches patients by free-text query | High | `GET /patients?q=` | `q` matching name/MRN returns matching subset; no match → empty page |
| FR-PAT-009 | The UI lists, searches, and registers patients via dialog | High | Frontend Patients | List renders rows; search filters; register dialog submits and refreshes list |
| FR-PAT-010 | Patient writes require `patient:write`; reads require `patient:read` | Critical | RBAC, BR-RBAC-002 | Caller lacking `patient:write` → 403 on POST/PUT/DELETE |

---

## 4. Functional Requirements — Appointments (FR-APPT-*)

| ID | Statement | Priority | Source | Acceptance Criteria |
|----|-----------|----------|--------|---------------------|
| FR-APPT-001 | The system lists appointments | High | `GET /appointments` | 200 + collection; filters honoured where provided |
| FR-APPT-002 | The system books an appointment for a valid patient + provider | Critical | `POST /appointments`, BR-APPT-001 | Valid request → 201 + BOOKED appointment |
| FR-APPT-003 | The system rejects an appointment whose end is not after start | Critical | `POST /appointments`, BR-APPT-002 | `end <= start` → 422 `OMII-400` |
| FR-APPT-004 | The system rejects double-booking the same provider | Critical | `POST /appointments`, BR-APPT-003 | Overlapping slot for same provider → 422 (no-double-booking); non-overlap → 201 |
| FR-APPT-005 | The system cancels an appointment and records the cancellation | High | `POST /appointments/{id}/cancel`, BR-APPT-006 | Cancel a BOOKED appointment → 200, status CANCELLED |
| FR-APPT-006 | The system rejects cancelling an already-cancelled appointment | High | `POST /appointments/{id}/cancel` | Cancel a CANCELLED appointment → 422 already-cancelled |
| FR-APPT-007 | The system lists providers for selection | Medium | `GET /providers` | 200 + provider collection used by booking flow |
| FR-APPT-008 | The UI books and lists appointments via dialog | High | Frontend Appointments | Book dialog submits valid appointment; list reflects it; conflict surfaces error |
| FR-APPT-009 | Appointment writes require `appointment:write`; reads require `appointment:read` | Critical | RBAC, BR-RBAC-002 | Caller lacking `appointment:write` → 403 on POST/cancel |

---

## 5. Functional Requirements — FHIR (FR-FHIR-*)

| ID | Statement | Priority | Source | Acceptance Criteria |
|----|-----------|----------|--------|---------------------|
| FR-FHIR-001 | The system returns a FHIR R4 Patient resource by ID | Critical | `GET /fhir/Patient/{id}` | Known ID → 200 + `resourceType:"Patient"` with `id`, `name`, `birthDate`, `gender` |
| FR-FHIR-002 | The FHIR Patient endpoint returns 404 for an unknown ID | High | `GET /fhir/Patient/{id}` | Unknown ID → 404 (FHIR `OperationOutcome` or ProblemDetail) |
| FR-FHIR-003 | The FHIR Patient endpoint requires `patient:read` | Critical | RBAC | Caller lacking `patient:read` → 403 |

---

## 6. Non-Functional Requirements (NFR-*)

| ID | Statement | Priority | Source | Acceptance Criteria |
|----|-----------|----------|--------|---------------------|
| NFR-REL-001 | API errors are returned as RFC 7807 ProblemDetail with a stable `errorCode` | High | Impl (ProblemDetail) | Every 4xx returns `type`, `title`, `status`, `detail`, `errorCode` (e.g. `OMII-400`) |
| NFR-REL-002 | Validation failures return consistent field-level detail | High | Impl | 422 responses identify the offending field(s) |
| NFR-USE-001 | UI exposes stable `data-testid` selectors for automatable elements | Medium | Frontend | Login, Patients, Appointments controls carry deterministic `data-testid` |
| NFR-COMP-001 | Frontend behaves consistently across the supported browser matrix | Medium | TEST_STRATEGY §3 | Login + core flows render identically on Chrome/Edge/Firefox |
| NFR-MAINT-001 | Endpoints are versioned under `/api/v1/` | Medium | Impl | All implemented routes are under `/api/v1/` |
| NFR-AVAIL-001 | A health/readiness signal confirms the build is alive before a cycle | Medium | TEST_STRATEGY §6 | Health check green precondition to entry |

---

## 7. Security Requirements (SEC-*)

| ID | Statement | Priority | Source | Acceptance Criteria |
|----|-----------|----------|--------|---------------------|
| SEC-AUTH-001 | Authentication issues a signed JWT; tampered tokens are rejected | Critical | BR-RBAC-001 | Modified token signature → 401 |
| SEC-AUTH-002 | Failed-login responses do not leak which credential was wrong | High | OWASP A07 | Wrong user vs wrong password → indistinguishable message |
| SEC-AUTHZ-001 | Default-deny: missing permission yields 403, not data | Critical | BR-RBAC-001/002 | Authenticated but unauthorised caller → 403 ProblemDetail, no payload |
| SEC-AUTHZ-002 | RBAC permissions are enforced per endpoint (`patient:*`, `appointment:*`, `audit:read`, `admin:manage`) | Critical | RBAC | Each endpoint enforces its declared permission |
| SEC-INPUT-001 | Input validation rejects malformed/oversized/injection-style payloads | High | OWASP A03 | Malformed JSON / unexpected types → 422, no stack trace leaked |
| SEC-ERR-001 | Error responses do not expose stack traces or internal details | High | OWASP A05 | 4xx/5xx bodies contain ProblemDetail only |
| SEC-SESS-001 | Access tokens expire and require refresh; refresh is independently validated | High | `POST /auth/refresh` | Expired access token → 401; refresh path validated separately |
| SEC-TENANT-001 | Cross-tenant access is denied | Critical | BR-TENANT-002 | Cross-tenant read/write → 403/404 (**Future** — single-tenant in v1.0 surface) |

---

## 8. Accessibility Requirements (A11Y-*)

| ID | Statement | Priority | Source | Acceptance Criteria |
|----|-----------|----------|--------|---------------------|
| A11Y-UI-001 | Login and core screens meet WCAG 2.1 AA for contrast and labels | Medium | QO-4 | No serious/critical axe violations on Login/Dashboard/Patients/Appointments |
| A11Y-UI-002 | Forms expose accessible labels and error associations | Medium | WCAG 2.1 AA | Each input has a programmatic label; errors linked via `aria-describedby` |
| A11Y-UI-003 | Primary flows are keyboard-operable | Medium | WCAG 2.1 AA | Login, search, register, book completable by keyboard only |
| A11Y-UI-004 | Focus order and visible focus are correct on dialogs | Medium | WCAG 2.1 AA | Register/Book dialogs trap and restore focus; focus is visible |

---

## 9. Performance Requirements (PERF-*)

Measured only on **owned** infrastructure (`perf` profile); never against public
adapters. Budgets are v1.0 baselines for the implemented endpoints.

| ID | Statement | Priority | Source | Acceptance Criteria |
|----|-----------|----------|--------|---------------------|
| PERF-API-001 | Patient search responds within budget under nominal load | Medium | QO-5 | `GET /patients?q=` p95 ≤ 500 ms at baseline concurrency |
| PERF-API-002 | Login responds within budget | Medium | QO-5 | `POST /auth/login` p95 ≤ 600 ms at baseline concurrency |
| PERF-API-003 | Appointment booking responds within budget | Medium | QO-5 | `POST /appointments` p95 ≤ 700 ms at baseline concurrency |
| PERF-UI-001 | Dashboard first meaningful render within budget | Low | QO-5 | Dashboard interactive ≤ 3 s on reference hardware |

---

## 10. FHIR Conformance Requirements (FHIR-*)

| ID | Statement | Priority | Source | Acceptance Criteria |
|----|-----------|----------|--------|---------------------|
| FHIR-PAT-001 | The Patient resource validates against the FHIR R4 schema | Critical | `GET /fhir/Patient/{id}` | Response validates as R4 `Patient`; required elements present |
| FHIR-PAT-002 | Patient `gender` uses the FHIR `administrative-gender` value set | High | FHIR R4 | `gender` ∈ {male,female,other,unknown} |
| FHIR-PAT-003 | Patient `birthDate` is a valid FHIR date | High | FHIR R4 | `birthDate` matches `YYYY[-MM[-DD]]` and is not in the future |
| FHIR-PAT-004 | Code-system URIs (identifiers/coding) are correct where present | High | FHIR R4 | Identifier `system` URIs are well-formed and canonical |

---

## 11. HL7 v2 Conformance Requirements (HL7-*) — (Future)

HL7 v2 messaging is documented but not on the implemented v1.0 surface. Held for a
later milestone; listed so the RTM can carry traced-but-future rows.

| ID | Statement | Priority | Source | Acceptance Criteria | Status |
|----|-----------|----------|--------|---------------------|--------|
| HL7-ADT-001 | An `ADT^A01` message parses with intact segments (MSH, PID, PV1) | High | HL7 v2 | Round-trip parse preserves all segments and fields | (Future) |
| HL7-ORU-001 | An `ORU^R01` result message maps to observations without field loss | Medium | HL7 v2 | Parsed OBX values map 1:1 | (Future) |
| HL7-MSH-001 | Malformed MSH is rejected with a clear error, not silent acceptance | High | HL7 v2 | Bad MSH → parse error surfaced | (Future) |

---

## Traceability

Every row above maps to one or more `TC-*` manual cases and automated suites in the
[RTM](../rtm/RTM.md). Manual case families: `TC-AUTH-*`, `TC-PAT-*`, `TC-APPT-*`,
`TC-FHIR-*`, plus cross-cutting `TC-SEC-*`, `TC-A11Y-*`, `TC-PERF-*`. Automated
anchors: `PatientApiE2ETest`, `LoginUiE2ETest`, `RunCucumberTest`.

## Dependencies

- Business rules: [docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md)
- Strategy & risk: [docs/TEST_STRATEGY.md](../../docs/TEST_STRATEGY.md),
  [docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md)
- Traceability: [rtm/RTM.md](../rtm/RTM.md)
- Plans: [test-plan/MASTER_TEST_PLAN.md](../test-plan/MASTER_TEST_PLAN.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
