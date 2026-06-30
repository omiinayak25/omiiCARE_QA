# Requirements Traceability Matrix (RTM) — omiiCARE_QA (Milestone 6)

> **Living, populated RTM.** This is the executable traceability matrix that links
> each requirement in [REQUIREMENTS.md](../requirements/REQUIREMENTS.md) to its
> manual test cases (`TC-*`), automated suites, specialized test types, and suite
> membership (smoke / regression). It is the Milestone 6 living realization of the
> framework defined in [docs/RTM.md](../../docs/RTM.md).

## Purpose

- Read **forward** (requirement → tests) to prove coverage; read **backward**
  (failing test → requirement at risk) to scope impact.
- Surface gaps: any critical requirement reaching an exit gate as `Gap` or
  `Partial` blocks release per
  [MASTER_TEST_PLAN.md](../test-plan/MASTER_TEST_PLAN.md) §6.

## Methodology

1. **Source of requirements:** [REQUIREMENTS.md](../requirements/REQUIREMENTS.md),
   itself traced to [docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md).
2. **Forward mapping:** each requirement is assigned ≥1 manual `TC-*` ID before
   build (shift-left, [TEST_STRATEGY.md](../test-strategy/TEST_STRATEGY.md)).
3. **Automated linkage:** automated coverage references concrete suites —
   `PatientApiE2ETest`, `LoginUiE2ETest`, `RunCucumberTest` (Milestone 5).
4. **Specialized columns:** API / DB / Sec / Perf / A11y mark which specialized
   test type also covers the row.
5. **Suite membership:** `Smoke?` and `Regression?` flag inclusion in those suites;
   high-exposure healthcare risks (R-01…R-05) appear in both.
6. **Status semantics:** `Traced` = ≥1 test mapped; `Covered` = all planned tests
   exist and pass; `Partial` = some types missing; `Gap` = none mapped.
7. **Cadence:** reviewed every cycle at the exit gate; updated whenever a
   requirement, case, or status changes.

### Column legend

| Column | Meaning |
|--------|---------|
| Req ID | Requirement from [REQUIREMENTS.md](../requirements/REQUIREMENTS.md) |
| Description | What the requirement asserts |
| Manual TC ID(s) | Manual case IDs authored under `manual-testing/test-cases/` |
| Automated TC | Automated suite/class (M5) |
| API/DB/Sec/Perf/A11y | ✓ where that specialized type covers the row |
| Smoke? / Regression? | Suite membership (Y/N) |
| Status | Traced / Partial / Covered / Gap |

---

## Traceability Matrix

| Req ID | Description | Manual TC ID(s) | Automated TC | API/DB/Sec/Perf/A11y | Smoke? | Regression? | Status |
|--------|-------------|-----------------|--------------|----------------------|--------|-------------|--------|
| FR-AUTH-001 | Valid credentials authenticate, JWT issued | TC-AUTH-001 | LoginUiE2ETest, PatientApiE2ETest | API, Sec | Y | Y | Covered |
| FR-AUTH-002 | Invalid credentials rejected, no token | TC-AUTH-002 | LoginUiE2ETest | API, Sec | Y | Y | Covered |
| FR-AUTH-003 | Malformed/empty login payload → 422 | TC-AUTH-003 | RunCucumberTest | API, Sec | N | Y | Traced |
| FR-AUTH-004 | Valid refresh token mints new access token | TC-AUTH-004 | PatientApiE2ETest | API, Sec | N | Y | Traced |
| FR-AUTH-005 | Invalid/expired refresh token rejected | TC-AUTH-005 | PatientApiE2ETest | API, Sec | N | Y | Traced |
| FR-AUTH-006 | `GET /auth/me` returns principal + permissions | TC-AUTH-006 | PatientApiE2ETest | API, Sec | Y | Y | Covered |
| FR-AUTH-007 | Protected endpoint rejects missing/invalid bearer | TC-AUTH-007 | RunCucumberTest | API, Sec | Y | Y | Covered |
| FR-AUTH-008 | UI login routes to Dashboard; invalid stays w/ error | TC-AUTH-008 | LoginUiE2ETest | A11y | Y | Y | Covered |
| SEC-AUTH-001 | Tampered JWT rejected | TC-SEC-001 | RunCucumberTest | Sec, API | N | Y | Traced |
| SEC-AUTH-002 | Failed login does not leak which credential was wrong | TC-SEC-002 | LoginUiE2ETest | Sec | N | Y | Traced |
| SEC-AUTHZ-001 | Default-deny: missing permission → 403, no data | TC-SEC-003 | PatientApiE2ETest | Sec, API | Y | Y | Covered |
| SEC-AUTHZ-002 | RBAC enforced per endpoint | TC-SEC-004 | PatientApiE2ETest, RunCucumberTest | Sec, API | Y | Y | Covered |
| SEC-INPUT-001 | Malformed/injection payloads rejected (422) | TC-SEC-005 | RunCucumberTest | Sec, API | N | Y | Traced |
| SEC-ERR-001 | Errors expose no stack trace/internals | TC-SEC-006 | PatientApiE2ETest | Sec, API | N | Y | Traced |
| SEC-SESS-001 | Access tokens expire; refresh independently validated | TC-SEC-007 | PatientApiE2ETest | Sec, API | N | Y | Traced |
| BR-RBAC-001 | Auth required + explicit permission; default deny | TC-SEC-003, TC-AUTH-007 | PatientApiE2ETest | Sec, API | Y | Y | Covered |
| BR-RBAC-002 | Permission-based authz checked per endpoint | TC-SEC-004 | PatientApiE2ETest | Sec, API | Y | Y | Covered |
| FR-PAT-001 | List patients with pagination | TC-PAT-001 | PatientApiE2ETest | API, DB | Y | Y | Covered |
| FR-PAT-002 | Register patient with valid demographics | TC-PAT-002 | PatientApiE2ETest | API, DB | Y | Y | Covered |
| FR-PAT-003 | Reject registration with future DOB | TC-PAT-003 | PatientApiE2ETest, RunCucumberTest | API, DB | N | Y | Covered |
| FR-PAT-004 | Reject registration with missing required fields | TC-PAT-004 | RunCucumberTest | API | N | Y | Traced |
| FR-PAT-005 | Retrieve single patient by ID; unknown → 404 | TC-PAT-005 | PatientApiE2ETest | API, DB | Y | Y | Covered |
| FR-PAT-006 | Update existing patient | TC-PAT-006 | PatientApiE2ETest | API, DB | N | Y | Covered |
| FR-PAT-007 | Soft-delete (deactivate) patient | TC-PAT-007 | PatientApiE2ETest | API, DB | N | Y | Partial |
| FR-PAT-008 | Search patients by free-text query | TC-PAT-008 | PatientApiE2ETest | API, DB, Perf | Y | Y | Covered |
| FR-PAT-009 | UI list/search/register via dialog | TC-PAT-009 | LoginUiE2ETest | A11y | Y | Y | Covered |
| FR-PAT-010 | Patient writes require `patient:write` | TC-PAT-010 | PatientApiE2ETest | Sec, API | Y | Y | Covered |
| BR-IDENT-002 | DOB not in future; required demographics | TC-PAT-003 | PatientApiE2ETest | API, DB | N | Y | Covered |
| BR-IDENT-005 | Patient soft-delete only, history retained | TC-PAT-007 | PatientApiE2ETest | API, DB | N | Y | Partial |
| FR-APPT-001 | List appointments | TC-APPT-001 | PatientApiE2ETest | API | Y | Y | Covered |
| FR-APPT-002 | Book appointment for valid patient + provider | TC-APPT-002 | PatientApiE2ETest, RunCucumberTest | API, DB | Y | Y | Covered |
| FR-APPT-003 | Reject end-not-after-start (422) | TC-APPT-003 | RunCucumberTest | API | N | Y | Covered |
| FR-APPT-004 | Reject provider double-booking (422) | TC-APPT-004 | PatientApiE2ETest, RunCucumberTest | API, DB | Y | Y | Covered |
| FR-APPT-005 | Cancel appointment, record cancellation | TC-APPT-005 | PatientApiE2ETest | API, DB | N | Y | Covered |
| FR-APPT-006 | Reject cancelling already-cancelled appointment | TC-APPT-006 | RunCucumberTest | API | N | Y | Traced |
| FR-APPT-007 | List providers for selection | TC-APPT-007 | PatientApiE2ETest | API | Y | N | Traced |
| FR-APPT-008 | UI book/list appointments via dialog | TC-APPT-008 | LoginUiE2ETest | A11y | Y | Y | Covered |
| FR-APPT-009 | Appointment writes require `appointment:write` | TC-APPT-009 | PatientApiE2ETest | Sec, API | Y | Y | Covered |
| BR-APPT-002 | Appointment end after start | TC-APPT-003 | RunCucumberTest | API | N | Y | Covered |
| BR-APPT-003 | Provider not double-booked | TC-APPT-004 | PatientApiE2ETest, RunCucumberTest | API, DB | Y | Y | Covered |
| BR-APPT-006 | Cancellation records reason + actor; slot released | TC-APPT-005 | PatientApiE2ETest | API, DB | N | Y | Partial |
| FR-FHIR-001 | FHIR R4 Patient resource by ID | TC-FHIR-001 | PatientApiE2ETest | API | Y | Y | Covered |
| FR-FHIR-002 | FHIR Patient unknown ID → 404 | TC-FHIR-002 | RunCucumberTest | API | N | Y | Traced |
| FR-FHIR-003 | FHIR Patient endpoint requires `patient:read` | TC-FHIR-003 | PatientApiE2ETest | Sec, API | N | Y | Traced |
| FHIR-PAT-001 | Patient validates against FHIR R4 schema | TC-FHIR-004 | RunCucumberTest | API | Y | Y | Covered |
| FHIR-PAT-002 | `gender` uses administrative-gender value set | TC-FHIR-005 | RunCucumberTest | API | N | Y | Traced |
| FHIR-PAT-003 | `birthDate` is a valid, non-future FHIR date | TC-FHIR-006 | RunCucumberTest | API | N | Y | Traced |
| NFR-REL-001 | RFC 7807 ProblemDetail with stable `errorCode` | TC-SEC-006, TC-PAT-003 | PatientApiE2ETest | API | N | Y | Covered |
| NFR-USE-001 | Stable `data-testid` selectors present | TC-A11Y-001 | LoginUiE2ETest | A11y | N | N | Traced |
| A11Y-UI-001 | Core screens meet WCAG 2.1 AA contrast/labels | TC-A11Y-001 | — | A11y | N | N | Partial |
| A11Y-UI-003 | Primary flows keyboard-operable | TC-A11Y-002 | — | A11y | N | N | Traced |
| PERF-API-001 | Patient search within p95 budget | TC-PERF-001 | — | Perf | N | N | Traced |
| PERF-API-002 | Login within p95 budget | TC-PERF-002 | — | Perf | N | N | Traced |
| HL7-ADT-001 | ADT^A01 parses with intact segments (Future) | TC-HL7-001 | — | API | N | N | Gap |

---

## Coverage Summary

| Status | Count (approx.) | Meaning |
|--------|-----------------|---------|
| Covered | ~24 | All planned tests exist and pass |
| Partial | 4 | Some specialized test type still missing |
| Traced | ~24 | ≥1 test mapped; specialized layers pending |
| Gap | 1 | (Future) HL7 — no test mapped; not in v1.0 scope |

Critical-requirement rule: no `Critical`-priority requirement may exit a gate as
`Gap` or `Partial`. The single `Gap` row (HL7-ADT-001) is **(Future)** and
explicitly out of v1.0 scope per
[MASTER_TEST_PLAN.md](../test-plan/MASTER_TEST_PLAN.md) §4.

## Dependencies

- Requirements: [requirements/REQUIREMENTS.md](../requirements/REQUIREMENTS.md)
- Framework parent: [docs/RTM.md](../../docs/RTM.md)
- Gating: [test-plan/MASTER_TEST_PLAN.md](../test-plan/MASTER_TEST_PLAN.md)
- Risk overlay: [docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
