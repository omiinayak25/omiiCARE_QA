# Manual Test Strategy — omiiCARE_QA (Milestone 6)

> **Executable manual test strategy.** This is the operational strategy the QA team
> runs against the **implemented** omiiCARE_QA surface. It refines the parent
> [docs/TEST_STRATEGY.md](../../docs/TEST_STRATEGY.md) into concrete scope, levels,
> types, environments, criteria, roles, defect workflow, and metrics for manual
> verification. Requirements come from
> [REQUIREMENTS.md](../requirements/REQUIREMENTS.md); coverage is tracked in the
> [RTM](../rtm/RTM.md); priority follows
> [docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md).

## 1. Scope

### In scope (implemented surface)

| Module | Manual coverage |
|--------|-----------------|
| Authentication & RBAC | Login, refresh, `/auth/me`, token rejection, permission-based access |
| Patients | List, search, register, retrieve, update, soft-delete; pagination; validation |
| Appointments | List, book, cancel, double-booking, end-after-start, already-cancelled |
| Providers | List for selection |
| FHIR | `GET /fhir/Patient/{id}` resource shape and R4 conformance |
| Frontend | Login, Dashboard, Patients, Appointments, 403/404 pages |
| Cross-cutting | ProblemDetail error contract, security/authz, accessibility, perf smoke |

### Out of scope (this strategy)

- Documented-but-unimplemented rules marked **(Future)** in
  [REQUIREMENTS.md](../requirements/REQUIREMENTS.md) (HL7 v2, full lifecycle state
  machines, multi-tenant isolation, consent/audit read-logging) — held for later
  milestones.
- Items in [MASTER_TEST_PLAN.md](../test-plan/MASTER_TEST_PLAN.md) §4 (native
  mobile, k8s, production cloud, real third-party integrations, real PHI).
- Framework internals and automation implementation (parent doc / Milestone 5).

## 2. Test Levels

| Level | Manual role | Where applied |
|-------|-------------|---------------|
| Component (UI) | Manual exploratory of individual screens/dialogs | Login, Patients, Appointments dialogs |
| API (black-box) | Manual API verification via REST client | All `/api/v1/*` endpoints + ProblemDetail |
| System | End-to-end manual journeys through the assembled app | Login → register patient → book → cancel |
| Acceptance | Business-rule confirmation against BR-* | Double-booking, future-DOB, already-cancelled |

Manual effort concentrates on exploratory, judgment, negative/boundary, and
first-pass new-feature verification; deterministic regression migrates to
automation (parent §14).

## 3. Test Types

| Type | Applied to implemented surface |
|------|--------------------------------|
| Smoke | Login, list patients, book appointment, FHIR read reachable |
| Functional | Every FR-* acceptance criterion |
| Negative | Invalid credentials, future DOB, missing fields, double-booking, re-cancel |
| Boundary | DOB at today's date, appointment end == start, page size limits, search empty/exact |
| Security | Tampered token, default-deny 403, error-leak checks, credential-enumeration |
| Accessibility | WCAG 2.1 AA on core screens, keyboard operability, focus management |
| Performance (smoke) | Patient search / login / booking latency on owned infra |
| Regression | All previously passing FR-* + BR-* before release |
| Exploratory | Risk-guided session-based testing of high-exposure flows |

## 4. Environments

Profiles align with [docs/TEST_STRATEGY.md](../../docs/TEST_STRATEGY.md) §5 and
[MASTER_TEST_PLAN.md](../test-plan/MASTER_TEST_PLAN.md) §9.

| Environment | Profile | Manual use | DB | Stability |
|-------------|---------|------------|----|-----------|
| Development | `dev` | Author/debug cases; first-pass exploratory | H2 | Volatile, non-gating |
| Test | `test` | Confirm fixes; re-run failed cases | H2 / Testcontainers | Reproducible per run |
| QA | `qa` | Functional, system, regression execution; sign-off signal | PostgreSQL | Stable, release-candidate |

All environments seeded with **synthetic, PHI-safe** data only (BR-CONS-005).
Demo credentials: `demo.admin / Admin@12345`.

## 5. Entry / Exit / Suspension / Resumption Criteria

### Entry

- Requirements baselined and traced in the [RTM](../rtm/RTM.md).
- Target environment health-checked green and seeded with PHI-safe data.
- Build deployed; smoke pass.
- Test cases for the scope authored and reviewed.

### Exit

- 100% of planned manual cases executed (or deferred with rationale).
- All Critical and High defects resolved or formally accepted.
- Pass rate ≥98%, 100% on critical-path cases.
- RTM shows no untested critical requirement.

### Suspension

- A Critical defect blocks further meaningful execution.
- Environment unstable/unavailable/unrepresentative.
- >40% of executed cases fail (systemic problem).
- Required seed data unavailable.

### Resumption

- Blocking defect fixed and regression-verified.
- Environment health restored and re-seeded.
- Affected suites re-baselined.

## 6. Roles & Responsibilities

| Role | Manual-testing responsibility |
|------|-------------------------------|
| QA Lead | Owns this strategy execution, scheduling, triage, sign-off |
| Senior QA Engineer | Authors and executes manual, exploratory, negative/boundary cases |
| SDET II/III | Maintains automated regression that retires manual cases |
| QA Architect | Validates depth-vs-risk alignment |

## 7. Defect Workflow

| Stage | Action |
|-------|--------|
| New | Logged with steps, expected/actual, env, build, evidence, traced Req ID |
| Triaged | Severity (Critical/High/Medium/Low) + priority (P1–P4) set with QA Lead |
| In Progress | Assigned; fix under way |
| Fixed | Dev marks fixed with the resolving change |
| Verified | QA re-tests on `test`/`qa`; runs sanity + targeted regression |
| Closed | Verified pass; RTM/coverage updated |
| Rejected/Deferred | Documented rationale; deferred items carry residual-risk note |

Severity guide: **Critical** = patient-safety/security/data-integrity blocker;
**High** = major function broken, no workaround; **Medium** = function broken with
workaround; **Low** = cosmetic/minor.

## 8. Metrics Overview

| Metric | Question | Target |
|--------|----------|--------|
| Pass/fail rate | Is the build healthy? | ≥98% pass, 100% critical-path |
| Defect density | Which module is buggiest? | Trend down per cycle |
| Defect leakage | How much escapes a stage? | <5% |
| Requirement coverage | Are all critical reqs traced? | 100% critical traced |
| MTTD / MTTR | Detect/resolve speed | Trend down |
| Re-open rate | Fix quality | <10% |

Dashboards live under [../metrics/](../metrics/).

## 9. Risk-Based Prioritization

Effort follows exposure ([docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md)).
For the implemented surface the priority zone is:

| Risk | Implemented manifestation | Manual depth |
|------|---------------------------|--------------|
| R-01 wrong-patient | Patient retrieve/search/update correctness | Smoke + regression; exhaustive |
| R-05 RBAC bypass | Default-deny 403, per-endpoint permission | Smoke + regression; every role path |
| R-03 booking integrity | Double-booking, end-after-start | Regression; full boundary set |
| R-04 audit/error contract | ProblemDetail + errorCode consistency | Regression |
| R-17 coverage gap | RTM reviewed every exit gate | Gate check |

High-exposure cases sit in both **smoke** and **regression** suites.

## Dependencies

- Parent strategy: [docs/TEST_STRATEGY.md](../../docs/TEST_STRATEGY.md)
- Requirements & RTM: [REQUIREMENTS.md](../requirements/REQUIREMENTS.md),
  [RTM.md](../rtm/RTM.md)
- Plans: [MASTER_TEST_PLAN.md](../test-plan/MASTER_TEST_PLAN.md),
  [SPRINT_TEST_PLAN.md](../test-plan/SPRINT_TEST_PLAN.md),
  [RELEASE_TEST_PLAN.md](../test-plan/RELEASE_TEST_PLAN.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
