# Test Strategy

> **Master quality blueprint.** This is the highest-level quality-engineering
> document for omiiCARE_QA. It declares *why* we test, *what* we test, *at which
> levels*, *with which tools*, and *who owns each layer*. Every subordinate
> document — the [Test Pyramid](TEST_PYRAMID.md), the
> [Master Test Plan](MASTER_TEST_PLAN.md), the
> [Test Data Strategy](TEST_DATA_STRATEGY.md), the [RTM](RTM.md), and the
> [Risk Analysis](RISK_ANALYSIS.md) — refines a slice of this strategy.
> Facts (technologies, environments, roles, standards) defer to
> [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

Establish a single, durable, milestone-agnostic test strategy that:

- Translates product and architectural goals into measurable quality objectives.
- Defines the full taxonomy of test levels and test types and what each proves.
- Maps each layer of the system to the right framework, environment, and owner.
- Sets objective entry, exit, suspension, and resumption criteria so testing
  starts, stops, and resumes on evidence rather than opinion.
- Anchors defect management, risk-based prioritization, and metrics so quality
  is governed, not improvised.

This strategy is **documentation only** in Milestone 1; the automation that
realizes it arrives in Milestone 5, the manual assets in Milestone 6, and the
advanced quality frameworks in Milestone 7.

## Scope

- **In scope:** quality objectives; test levels and types; per-layer approach;
  environment strategy; entry/exit/suspension/resumption criteria; tooling map;
  defect-management overview; risk-based testing; metrics overview; roles and
  responsibilities; shift-left posture; the automation-versus-manual split.
- **Out of scope:** step-by-step test cases (Milestone 6), framework internals
  (Milestone 5), and per-release schedules (the
  [Master Test Plan](MASTER_TEST_PLAN.md) and release plans from Milestone 6).
- **System Under Test (SUT):** the omiiCARE healthcare web application, its REST
  and FHIR APIs, and its database, per [ARCHITECTURE.md](../ARCHITECTURE.md).

## Responsibilities

| Role | Strategy responsibility |
|------|-------------------------|
| QA Architect | Owns this strategy; keeps levels, tooling, and metrics authoritative |
| QA Lead | Operationalizes the strategy into plans, schedules, and staffing |
| SDET III | Designs framework seams and adapter-layer test approach for each layer |
| SDET II | Implements layer-appropriate automated tests against the strategy |
| Senior QA Engineer | Authors manual, exploratory, and risk-driven coverage |
| All contributors | Apply shift-left practices; never bypass quality gates |

---

## 1. Quality Objectives

| # | Objective | How it is measured | Target |
|---|-----------|--------------------|--------|
| QO-1 | Functional correctness across all modules | Requirement coverage via [RTM](RTM.md); pass rate | 100% critical reqs traced; ≥98% pass |
| QO-2 | Interoperability fidelity (FHIR R4 / HL7 v2) | Contract + schema validation pass rate | 100% of mapped resources validate |
| QO-3 | Privacy & security posture (HIPAA-like) | Security findings; audit-log coverage | Zero unmitigated Critical/High |
| QO-4 | Accessibility (WCAG 2.1 AA) | axe-core / Lighthouse violations | Zero serious/critical a11y violations |
| QO-5 | Performance within budgets | Latency/throughput vs budgets (owned infra) | Meets per-endpoint SLO budgets |
| QO-6 | Regression safety | Defect leakage; regression suite pass | Leakage <5%; suite green pre-release |
| QO-7 | Maintainable, trustworthy automation | Flaky %; mutation score; automation ROI | Flaky <2%; mutation ≥70% on core |

Objectives are deliberately measurable; the [metrics overview](#10-metrics-overview)
binds each to a tracked indicator.

## 2. Test Levels (Pyramid Layers)

The [Test Pyramid](TEST_PYRAMID.md) is canonical for proportions; this strategy
states intent per level.

| Level | Proves | Primary owner | Primary tooling |
|-------|--------|---------------|-----------------|
| Unit | Smallest behavioral units in isolation | SDET II + developers | JUnit 5, Mockito |
| Component | A component/slice (e.g. one service or React feature) | SDET II | JUnit 5, React Testing Library |
| Integration | Collaboration across modules, DB, adapters | SDET II/III | JUnit 5, Testcontainers, Rest Assured |
| Contract | Provider/consumer + FHIR/HL7 schema conformance | SDET III | Rest Assured, JSON Schema, FHIR validator |
| API | Black-box REST/FHIR behavior end-to-end | SDET II | Rest Assured, JUnit 5/TestNG |
| UI / E2E | User journeys through the SUT | SDET II/III | Playwright, Selenium, Cucumber |

## 3. Test Types (Categories)

Every category below is in scope across the v1.0 roadmap; depth phases in by
milestone. Each maps to one or more levels above.

| Category | What it covers | Realized |
|----------|----------------|----------|
| Smoke | Build is alive: critical paths reachable | M5 |
| Sanity | Narrow, targeted re-verification after a change | M5/M6 |
| Regression | Previously working behavior still works | M5/M6 |
| Functional | Feature behaves per requirement | M5/M6 |
| Integration | Inter-module/adapter collaboration | M5 |
| System | Whole assembled SUT meets requirements | M5/M6 |
| End-to-end (E2E) | Complete real-world user journeys | M5 |
| API | REST/FHIR endpoint behavior and errors | M5 |
| Database | Schema, constraints, migrations, data integrity | M7 |
| Accessibility | WCAG 2.1 AA conformance | M7 |
| Visual | Pixel/layout regression | M7 |
| Cross-browser | Consistent behavior across the browser matrix | M5/M7 |
| Cross-environment | Behavior parity across dev/test/qa/stage | M5 |
| Localization (l10n/i18n) | Language, format, RTL correctness | M5/M6 |
| Negative | Invalid input/error handling | M5/M6 |
| Boundary | Edge values at limits | M5/M6 |
| Exploratory | Unscripted, risk-guided discovery | M6 |
| Recovery | Failover, restart, data recovery | M7 |
| Contract | Provider/consumer + standards schema | M5/M7 |
| Compatibility | OS/device/version matrix | M5/M7 |

**Browser matrix:** Chrome, Edge, Firefox, Safari, WebKit, Mobile Chrome,
Mobile Safari — exercised by cross-browser, visual, and compatibility testing.

## 4. Test Approach per Layer

| Layer | Approach | Data | Key tactic |
|-------|----------|------|------------|
| Unit | Fast, isolated, mock collaborators; TDD where practical | In-memory fixtures | Branch + mutation coverage on core logic |
| Component | Slice tests with real component, mocked edges | Builders/factories | Stable selectors / public contracts only |
| Integration | Real DB (Testcontainers/H2) + real adapters where safe | Seed datasets | Verify wiring, transactions, audit writes |
| Contract | Schema + provider/consumer pacts; FHIR/HL7 validators | Canonical bundles/messages | Fail on schema or code-system drift |
| API | Black-box via Rest Assured; positive + negative + boundary | Synthetic generators | Assert status, Problem Details, payload shape |
| UI / E2E | Page Object Model + Adapter layer; journeys per RBAC role | Seeded environment state | Resource-adapter targeting (see §5) |

The **Resource Adapter Layer** (Milestone 5) lets API and UI tests target Local
omiiCARE, OpenMRS, OpenEMR, HAPI FHIR, SMART Health IT, OpenFDA, DummyJSON, or
Restful Booker by configuration only — test code never names the system.

## 5. Environment Strategy

Profiles are canonical in [PROJECT_METADATA.md](PROJECT_METADATA.md) §4. Testing
maps to them as follows:

| Environment | Profile | QA use | Stability expectation |
|-------------|---------|--------|-----------------------|
| Development | `dev` | Author/debug; fast feedback (H2) | Volatile; no gating |
| Test | `test` | CI execution of unit→API (H2/Testcontainers) | Reproducible per run |
| QA | `qa` | Functional, system, regression validation | Stable; release-candidate signal |
| Stage | `stage` | Pre-production E2E, smoke, perf smoke | Production-like; change-frozen near release |

Cross-environment tests verify parity; configuration — never code — switches the
target. Performance load runs only on **owned** infrastructure (`perf` profile),
never against third-party public targets.

## 6. Entry, Exit, Suspension & Resumption Criteria

### Entry criteria (a test cycle may begin when)
- Requirements for the items under test are baselined and traced in the [RTM](RTM.md).
- The target environment is healthy (health checks green) and seeded with PHI-safe data.
- The build deploys and smoke tests pass.
- Test data, test cases, and automation for the scope are available and reviewed.

### Exit criteria (a test cycle is complete when)
- 100% of planned tests are executed (or explicitly deferred with rationale).
- All Critical and High defects are resolved or formally accepted (see [Risk Analysis](RISK_ANALYSIS.md)).
- Pass rate, coverage, and leakage meet the [quality objectives](#1-quality-objectives).
- The [RTM](RTM.md) shows no untested critical requirement.

### Suspension criteria (testing pauses when)
- A blocking (Critical) defect prevents meaningful further execution.
- The environment is unstable, unavailable, or unrepresentative.
- >40% of executed tests in a cycle fail, indicating a systemic problem.
- Required test data or a dependency adapter is unavailable.

### Resumption criteria (testing restarts when)
- The blocking defect is fixed and verified, and a regression check passes.
- Environment health is restored and re-seeded.
- Affected suites are re-baselined.

## 7. Test Data Strategy (Pointer)

All test data is **synthetic and PHI-safe** — never real patient data. Generators,
factories, builders, and seed datasets arrive in Milestone 5; the principles,
categories, refresh/cleanup, and compliance stance are defined in the
[Test Data Strategy](TEST_DATA_STRATEGY.md).

## 8. Tooling Map

| Layer / concern | Framework | Source |
|-----------------|-----------|--------|
| Unit / component (backend) | JUnit 5, Mockito | M5 |
| Integration | JUnit 5, Testcontainers, Rest Assured | M5 |
| API automation | Rest Assured | M5 |
| UI automation | Playwright, Selenium | M5 |
| BDD | Cucumber / Gherkin | M5 |
| Runners | JUnit 5, TestNG | M5 |
| Reporting | Allure, Extent Reports | M5 |
| Performance | JMeter, k6, Gatling | M7 |
| Security | OWASP ZAP, Dependency-Check | M7 |
| Accessibility | axe-core, Lighthouse | M7 |
| Visual | Playwright visual comparisons | M7 |
| Contract / schema | JSON Schema, FHIR validator | M5/M7 |

## 9. Defect Management Overview

| Aspect | Standard |
|--------|----------|
| Severity | Critical / High / Medium / Low (impact-based) |
| Priority | P1–P4 (urgency-based, set with the QA Lead) |
| Lifecycle | New → Triaged → In Progress → Fixed → Verified → Closed (or Rejected/Deferred) |
| Required fields | Steps, expected/actual, environment, build, evidence, traced requirement |
| Leakage tracking | Each escaped defect is root-caused and feeds shift-left |

Full templates and workflow are delivered in Milestone 6 under `manual-testing/`.

## 10. Risk-Based Testing

Effort is allocated by **risk exposure (probability × impact)**, per the
[Risk Analysis](RISK_ANALYSIS.md). High-exposure areas — wrong-patient handling,
medication interactions, billing/claims accuracy, consent, audit completeness,
authentication/authorization — receive the deepest, earliest, and most frequent
testing. Low-exposure areas receive proportionate coverage. Residual risk is
explicitly accepted by the QA Lead and recorded.

## 11. Metrics Overview

| Metric | Question it answers |
|--------|---------------------|
| Pass / fail rate | Is the build healthy? |
| Defect density | How buggy is each module? |
| Defect leakage | How much escapes a test stage? |
| Coverage | How much is exercised (code + requirement)? |
| MTTD | How fast do we detect defects? |
| MTTR | How fast do we resolve them? |
| Flaky % | How trustworthy is automation? |
| Automation ROI | Is automation paying off? |
| Mutation score | Do tests actually catch faults? |

Targets bind to the [quality objectives](#1-quality-objectives); dashboards are
built in Milestone 6 and instrumented further in Milestone 7.

## 12. Roles & Responsibilities

| Role | Core responsibilities |
|------|-----------------------|
| QA Architect | Strategy, pyramid balance, tooling decisions, testability seams |
| QA Lead | Planning, scheduling, staffing, defect triage, release sign-off |
| SDET III | Framework architecture, adapter layer, contract/integration design, mentoring |
| SDET II | Automated test implementation across unit→API→UI |
| Senior QA Engineer | Manual, exploratory, negative/boundary, risk-driven coverage |

## 13. Shift-Left

- Quality enters at requirement definition: every requirement gets acceptance
  criteria and an [RTM](RTM.md) row before build.
- Unit, component, and contract tests are authored alongside (or before) code.
- Static analysis, security scanning, and accessibility checks run in CI gates
  (see [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md)).
- Defects are pushed to the cheapest stage that can catch them, reducing MTTD
  and leakage.

## 14. Automation vs Manual Split

| Suited to automation | Suited to manual |
|----------------------|------------------|
| Regression, smoke, sanity | Exploratory, usability |
| API/contract/data-driven | Ad-hoc and first-pass new features |
| Cross-browser/cross-env matrices | Complex visual/UX judgment |
| Deterministic, repeatable journeys | Risk-driven discovery |

Target steady state: the bulk of *executions* are automated (unit→API heavy),
while manual effort concentrates on exploratory and judgment-based testing.
Proportions are governed by the [Test Pyramid](TEST_PYRAMID.md), not by headcount.

## Examples

- *Entry gate:* A regression cycle on the `qa` environment cannot start until
  smoke passes and the [RTM](RTM.md) confirms all in-scope requirements are
  traced — preventing wasted effort on an unhealthy build.
- *Risk-driven depth:* Because "wrong-patient association" carries the highest
  exposure in the [Risk Analysis](RISK_ANALYSIS.md), patient-context tests are
  authored at unit, integration, API, and E2E levels, not UI alone.

## Future Enhancements

- AI-assisted test selection and failure triage (Milestone 9).
- Predictive flaky-test quarantine wired into CI quality gates.
- Coverage-to-risk heatmaps generated automatically from the RTM and risk register.

## Dependencies

- Anchored by [PROJECT_METADATA.md](PROJECT_METADATA.md) and
  [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md).
- Refined by [TEST_PYRAMID.md](TEST_PYRAMID.md),
  [MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md),
  [TEST_DATA_STRATEGY.md](TEST_DATA_STRATEGY.md), [RTM.md](RTM.md), and
  [RISK_ANALYSIS.md](RISK_ANALYSIS.md).
- Realized by Milestones 5–7 per [ROADMAP.md](../ROADMAP.md).

## References

- [ARCHITECTURE.md](../ARCHITECTURE.md) §9 (Quality Architecture)
- [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md)
- ISTQB Foundation & Advanced syllabi; IEEE 829 test documentation.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial master test strategy (Milestone 1) |
