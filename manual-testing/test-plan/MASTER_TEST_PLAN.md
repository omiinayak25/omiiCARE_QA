# Master Test Plan (Manual) — omiiCARE_QA v1.0 (Milestone 6)

> **IEEE 829-style master test plan** for the manual verification of the
> implemented omiiCARE_QA release. It refines the parent
> [docs/MASTER_TEST_PLAN.md](../../docs/MASTER_TEST_PLAN.md), executes the manual
> [TEST_STRATEGY.md](../test-strategy/TEST_STRATEGY.md), traces through the
> [RTM](../rtm/RTM.md), and prioritizes by
> [docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md).

## 1. Test Plan Identifier

`OMII-MTP-v1.0-M6` — Manual Master Test Plan, omiiCARE_QA v1.0.0.

## 2. Introduction

omiiCARE_QA's SUT for v1.0 is the omiiCARE web application plus its REST and FHIR
APIs over a PHI-safe synthetic dataset. This plan governs all **manual** test
activity for the implemented modules: Authentication/RBAC, Patients, Appointments,
Providers, and FHIR Patient read. No real integrations or real patient data are
exercised.

## 3. Test Items

| Item | Surface | Responsibility |
|------|---------|----------------|
| Auth API | `/auth/login`, `/auth/refresh`, `/auth/me` | API, security manual |
| Patients API | `GET/POST/PUT/DELETE /patients`, `?q=`, pagination | API, functional manual |
| Providers API | `GET /providers` | API manual |
| Appointments API | `GET/POST /appointments`, `/{id}/cancel` | API, functional manual |
| FHIR API | `GET /fhir/Patient/{id}` | API, conformance manual |
| Frontend | Login, Dashboard, Patients, Appointments, 403/404 | UI/E2E, a11y manual |
| Error contract | RFC 7807 ProblemDetail + `errorCode` | Cross-cutting manual |

## 4. Features To Be Tested

| Module | Representative coverage | Priority |
|--------|-------------------------|----------|
| Authentication & RBAC | Login (valid/invalid/malformed), refresh, `/auth/me`, token rejection, default-deny 403 | Critical |
| Patient | Register (valid/future-DOB/missing), list, search, retrieve, update, soft-delete, pagination | Critical |
| Appointment | Book, double-booking reject, end-after-start reject, cancel, re-cancel reject, list | High |
| Providers | List for booking selection | Medium |
| FHIR | Patient read, R4 shape, gender/birthDate value sets, unknown-ID 404, authz | Critical |
| Frontend | Login routing, patient list/search/register dialog, appointment list/book dialog, 403/404 | High |
| Error/security contract | ProblemDetail consistency, no stack-trace leak, credential non-enumeration | Critical |

## 5. Features NOT To Be Tested in v1.0

Per [docs/MASTER_TEST_PLAN.md](../../docs/MASTER_TEST_PLAN.md) §4 and the
**(Future)** items in [REQUIREMENTS.md](../requirements/REQUIREMENTS.md):

- HL7 v2 messaging (HL7-*).
- Full appointment/encounter/billing/lab/radiology/pharmacy/insurance lifecycles.
- Multi-tenant cross-tenant isolation (single-tenant surface in v1.0).
- Consent enforcement and PHI read-audit logging.
- Native mobile, Kubernetes, production cloud, real third-party integrations,
  real PHI, formal HIPAA certification.

## 6. Approach

- **Levels & types:** [TEST_STRATEGY.md](../test-strategy/TEST_STRATEGY.md) §2–§3.
- **Manual execution:** REST client for API items; browser for UI/E2E items.
- **Traceability:** every executed case maps to a [RTM](../rtm/RTM.md) row.
- **Risk weighting:** R-01, R-05, R-03 paths in both smoke and regression.
- **Hand-off to automation:** stable regression cases reference
  `PatientApiE2ETest`, `LoginUiE2ETest`, `RunCucumberTest`.

## 7. Pass / Fail Criteria

**A case passes** when actual = expected, including HTTP status, ProblemDetail
`errorCode`, payload shape, and UI state, with no unhandled error.

**A cycle passes** when:

- ≥98% of planned cases pass; 100% of critical-path cases pass.
- No open Critical or High defect on an in-scope feature.
- RTM shows no untested critical requirement.
- Defect leakage < 5%.

**A cycle fails** when any of the above is unmet → triage and possible suspension.

## 8. Suspension & Resumption

Per [TEST_STRATEGY.md](../test-strategy/TEST_STRATEGY.md) §5: suspend on a Critical
blocker, unstable environment, >40% failure rate, or missing seed data; resume on a
verified fix, restored/re-seeded environment, and re-baselined suites.

## 9. Test Deliverables

| Deliverable | Location |
|-------------|----------|
| Requirements catalogue | [../requirements/REQUIREMENTS.md](../requirements/REQUIREMENTS.md) |
| RTM | [../rtm/RTM.md](../rtm/RTM.md) |
| Manual test cases | [../test-cases/](../test-cases/) |
| Test suites (smoke/regression) | [../test-suites/](../test-suites/) |
| Sprint & release plans | [SPRINT_TEST_PLAN.md](SPRINT_TEST_PLAN.md), [RELEASE_TEST_PLAN.md](RELEASE_TEST_PLAN.md) |
| Estimation | [../estimation/TEST_ESTIMATION.md](../estimation/TEST_ESTIMATION.md) |
| Defect reports/templates | [../bug-reports/](../bug-reports/), [../bug-templates/](../bug-templates/) |
| Metrics | [../metrics/](../metrics/) |
| Sign-off | [../signoff/](../signoff/) |

## 10. Environment Needs

| Environment | Profile | Purpose | DB |
|-------------|---------|---------|----|
| Test | `test` | Fix confirmation, re-runs | H2 / Testcontainers |
| QA | `qa` | Functional, system, regression, sign-off | PostgreSQL |

Each health-checked and seeded with PHI-safe data before a cycle.

## 11. Schedule (Milestone-Aligned)

| Phase | Focus | Target |
|-------|-------|--------|
| M6.1 | Requirements + RTM baselined | Complete |
| M6.2 | Manual case authoring (TC-AUTH/PAT/APPT/FHIR) | This milestone |
| M6.3 | Smoke + functional execution on `qa` | This milestone |
| M6.4 | Regression + exploratory + defect triage | This milestone |
| M6.5 | Metrics, sign-off, release plan execution | This milestone |

## 12. Staffing / Roles

| Role | Allocation |
|------|------------|
| QA Lead | Planning, triage, sign-off |
| Senior QA Engineer | Manual + exploratory + negative/boundary execution |
| SDET II/III | Regression automation that retires manual cases |
| QA Architect | Approach/risk review |

## 13. Risks & Contingencies

| Risk | Contingency |
|------|-------------|
| Environment instability (R-12) | Suspend §8; re-seed and re-baseline |
| Synthetic data drift (R-13) | Regenerate seeds; re-baseline |
| RBAC bypass found (R-05) | Suspend cycle; security review; block release |
| Coverage gap on critical req (R-17) | Add cases before exit gate |
| Booking-integrity defect (R-03) | Freeze booking; re-verify boundary set |

## 14. Approvals

| Approver | Approves |
|----------|----------|
| QA Architect | Approach and risk alignment |
| QA Lead | Plan adoption, cycle entry/exit, release sign-off |
| Maintainer | Release-1.0.0 transition |

## Dependencies

- Parent: [docs/MASTER_TEST_PLAN.md](../../docs/MASTER_TEST_PLAN.md)
- Strategy/RTM/Reqs: [../test-strategy/TEST_STRATEGY.md](../test-strategy/TEST_STRATEGY.md),
  [../rtm/RTM.md](../rtm/RTM.md), [../requirements/REQUIREMENTS.md](../requirements/REQUIREMENTS.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
