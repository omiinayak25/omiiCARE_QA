# Master Test Plan

> **IEEE 829-style master test plan** for omiiCARE_QA v1.0. It states what will be
> tested, how, when, by whom, and under which pass/fail rules, across the whole
> path to release 1.0.0. It implements the [Test Strategy](TEST_STRATEGY.md),
> adopts the [Test Pyramid](TEST_PYRAMID.md) balance, draws data from the
> [Test Data Strategy](TEST_DATA_STRATEGY.md), traces through the [RTM](RTM.md),
> and prioritizes by the [Risk Analysis](RISK_ANALYSIS.md). Facts defer to
> [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

Provide the single coordinating plan that governs all test activity for v1.0:
scope, approach, criteria, deliverables, environment, schedule, staffing, risks,
and approvals. Sprint-level and release-level plans (Milestone 6, living under
`manual-testing/`) refine this master plan; they never contradict it.

## Scope

- **In scope:** test items; features to be tested by module; features not tested
  in v1.0; approach; pass/fail criteria; suspension/resumption; deliverables;
  environment needs; milestone-aligned schedule; staffing; risks and
  contingencies; approvals.
- **Out of scope:** individual test-case steps (Milestone 6) and framework
  internals (Milestone 5).

## Responsibilities

| Role | Plan responsibility |
|------|---------------------|
| QA Lead | Owns this plan; schedules, staffs, and signs off cycles |
| QA Architect | Validates approach and tooling against the strategy and pyramid |
| SDET III | Owns integration/contract/framework test items |
| SDET II | Owns unit→API→UI automated test items |
| Senior QA Engineer | Owns manual, exploratory, and risk-driven items |
| Maintainer | Approves release-gate transitions |

---

## 1. Introduction

omiiCARE_QA is an enterprise healthcare quality-engineering monorepo whose
primary SUT is the omiiCARE web application plus its REST and FHIR APIs and
database. This plan covers verification and validation from Milestone 5 (when
automation begins) through Milestone 10 (release 1.0.0), with manual assets
authored in Milestone 6. All data is synthetic and PHI-safe; no real integrations
or real patient data are exercised.

## 2. Test Items

| Item | Origin milestone | Test responsibility |
|------|------------------|---------------------|
| Backend REST APIs (`/api/v1/`) | M3 | API, integration, contract |
| FHIR R4 APIs and resource mappings | M3 | Contract, API, integration |
| HL7 v2 message handling | M3 | Contract, integration |
| Database schema, migrations, seeds | M2 | Database, integration |
| React frontend portals (12 RBAC roles) | M4 | UI/E2E, accessibility, visual |
| Authentication / authorization (JWT, RBAC) | M3 | Security, API, E2E |
| Automation framework + adapter layer | M5 | Framework self-tests |
| Infrastructure / environments | M2 | Smoke, recovery, cross-environment |

## 3. Features To Be Tested (by Module)

| Module | Representative coverage | Priority (risk-driven) |
|--------|-------------------------|------------------------|
| Authentication & RBAC | Login, token refresh, role-based access, lockout, audit | Critical |
| Patient | Registration, search, demographics, patient-context integrity | Critical |
| Appointment | Booking, reschedule, cancel, conflicts, availability | High |
| Billing | Invoicing, charge capture, totals, adjustments | Critical |
| Lab | Orders, results entry, LOINC-coded observations, ranges | High |
| Radiology | Order/report metadata, study association | High |
| Pharmacy | Prescriptions, dispensing, drug-interaction checks | Critical |
| Insurance | Eligibility, claims, adjudication outcomes | Critical |
| FHIR | Resource CRUD, bundle validation, code-system URIs | Critical |
| HL7 | Message parse/build, segment integrity | High |
| Admin / Super Admin | Tenant config, user management, audit review | High |

Coverage depth follows the [Test Pyramid](TEST_PYRAMID.md); priority follows the
[Risk Analysis](RISK_ANALYSIS.md).

## 4. Features NOT To Be Tested in v1.0

Per [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §3,
these are roadmap-only and out of scope for v1.0 testing:

- Native Android/iOS applications.
- Kubernetes deployment and a microservices split.
- Distributed database behavior.
- Production cloud deployment.
- Real hospital / payment-gateway / insurance-provider integrations (only
  stubbed adapters are tested).
- Real patient data (only synthetic, PHI-safe data is used).
- Formal HIPAA certification or medical-device compliance validation.

## 5. Approach

- **Levels & types:** the full taxonomy in [TEST_STRATEGY.md](TEST_STRATEGY.md) §2–§3.
- **Balance:** governed by the [TEST_PYRAMID.md](TEST_PYRAMID.md).
- **Targeting:** the Resource Adapter Layer (M5) lets API/UI tests hit Local
  omiiCARE, OpenMRS, OpenEMR, HAPI FHIR, SMART Health IT, OpenFDA, DummyJSON, or
  Restful Booker by configuration only.
- **Browser matrix:** Chrome, Edge, Firefox, Safari, WebKit, Mobile Chrome,
  Mobile Safari.
- **Reporting:** Allure and Extent Reports.
- **Automation/manual split:** per [TEST_STRATEGY.md](TEST_STRATEGY.md) §14.

## 6. Pass / Fail Criteria

**An individual test passes** when actual results match expected results,
including correct status, payload, and audit side-effects, with no unhandled error.

**A test cycle passes** when:

- ≥98% of planned tests pass and 100% of critical-path tests pass.
- No open Critical or High defect on an in-scope feature.
- Requirement coverage in the [RTM](RTM.md) shows no untested critical requirement.
- Defect leakage from the cycle is <5%.

**A cycle fails** when any of the above is unmet; failing cycles trigger triage
and, if warranted, suspension (§7).

## 7. Suspension & Resumption Criteria

**Suspend** when: a Critical defect blocks execution; the environment is unstable
or unrepresentative; >40% of executed tests fail; or required test data/adapters
are unavailable.

**Resume** when: the blocking defect is fixed and regression-verified; the
environment is healthy and re-seeded; and affected suites are re-baselined.
(Aligned with [TEST_STRATEGY.md](TEST_STRATEGY.md) §6.)

## 8. Deliverables

| Deliverable | Milestone | Location |
|-------------|-----------|----------|
| Automation framework + adapters | M5 | `automation/` |
| Requirements, RTM, manual cases, suites | M6 | `manual-testing/` |
| Sprint plans, release plans | M6 | `manual-testing/` |
| Defect reports + metrics dashboards | M6 | `manual-testing/` |
| Performance/security/a11y/visual suites | M7 | `quality/` |
| Test summary & release-readiness reports | M7–M10 | `quality/`, `docs/` |

## 9. Environment Needs

| Environment | Profile | Purpose | DB |
|-------------|---------|---------|----|
| Test | `test` | CI execution of unit→API | H2 / Testcontainers |
| QA | `qa` | Functional, system, regression | External PostgreSQL |
| Stage | `stage` | Pre-production E2E, smoke, perf smoke | External PostgreSQL |
| Performance | `perf` | Load/stress (owned infra only) | configurable |

Each must be health-checked and seeded with PHI-safe data before a cycle starts
(see [TEST_DATA_STRATEGY.md](TEST_DATA_STRATEGY.md)).

## 10. Schedule (Milestone-Aligned)

| Milestone | Test focus | Status (2026-06-30) |
|-----------|------------|---------------------|
| M1 | Strategy & plan documentation (this set) | 🟦 In progress |
| M2 | Infrastructure smoke, DB-profile, recovery readiness | ⬜ Not started |
| M3 | Backend unit, integration, contract foundations | ⬜ Not started |
| M4 | Frontend testability, a11y readiness | ⬜ Not started |
| M5 | Automation framework, smoke/API/UI suites | ⬜ Not started |
| M6 | Manual cases, RTM, suites, defect/metrics process | ⬜ Not started |
| M7 | Performance, security, a11y, visual, contract, chaos | ⬜ Not started |
| M8 | CI/CD quality gates, coverage trends | ⬜ Not started |
| M9 | AI-assisted test capabilities | ⬜ Not started |
| M10 | Full audit, release-readiness sign-off (v1.0.0) | ⬜ Not started |

Sprint and release plans live under `manual-testing/` from Milestone 6 and
refine these milestone bands.

## 11. Staffing / Roles

| Role | Allocation focus |
|------|------------------|
| QA Architect | Strategy, pyramid, framework architecture review |
| QA Lead | Planning, triage, sign-off across all cycles |
| SDET III | Integration/contract/framework + adapter layer |
| SDET II | Unit→API→UI automation implementation |
| Senior QA Engineer | Manual, exploratory, negative/boundary coverage |

## 12. Risks & Contingencies

| Risk | Contingency |
|------|-------------|
| Public adapter targets (OpenMRS, HAPI FHIR, etc.) unavailable | Fall back to Local omiiCARE / WireMock stubs |
| Environment instability blocks a cycle | Suspend per §7; re-seed and re-baseline |
| Flaky UI tests erode trust | Quarantine, root-cause, push checks down the pyramid |
| Synthetic data drifts from schema | Regenerate via factories/seeds (M5); version datasets |
| Schedule slip in an upstream milestone | Re-sequence dependent cycles; protect critical-path coverage |

Full register and exposure scoring: [RISK_ANALYSIS.md](RISK_ANALYSIS.md).

## 13. Approvals

| Approver | Approves |
|----------|----------|
| QA Architect | Test approach and tooling alignment |
| QA Lead | Plan adoption, cycle entry/exit, release sign-off |
| Maintainer | Milestone-gate and release-1.0.0 transition |

## Examples

- *Module priority:* Pharmacy is Critical because drug-interaction errors carry
  high exposure ([RISK_ANALYSIS.md](RISK_ANALYSIS.md)); it receives unit→E2E
  coverage and is gated at every cycle.
- *Out-of-scope enforcement:* A request to load-test a public HAPI FHIR endpoint
  is rejected — performance load runs only on owned `perf` infrastructure (§9).

## Future Enhancements

- Auto-generate the test summary report from Allure/Extent + RTM data.
- Link each module's coverage to a live GitHub project board (M8).
- Risk-weighted scheduling that re-orders cycles as the risk register changes.

## Dependencies

- Implements [TEST_STRATEGY.md](TEST_STRATEGY.md); balanced by
  [TEST_PYRAMID.md](TEST_PYRAMID.md).
- Consumes [TEST_DATA_STRATEGY.md](TEST_DATA_STRATEGY.md), [RTM.md](RTM.md),
  [RISK_ANALYSIS.md](RISK_ANALYSIS.md).
- Sequenced by [ROADMAP.md](../ROADMAP.md).

## References

- IEEE 829 Standard for Software Test Documentation.
- [ARCHITECTURE.md](../ARCHITECTURE.md), [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial IEEE 829-style master test plan (Milestone 1) |
