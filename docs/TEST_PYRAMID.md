# Test Pyramid

> **Balance and ownership model.** This document defines the shape of testing for
> omiiCARE_QA: how much of each kind of test we invest in, what each layer
> deliberately does *and does not* cover, who owns it, and why the proportions
> matter for maintainability. It operationalizes the levels named in the
> [Test Strategy](TEST_STRATEGY.md) and the
> [ARCHITECTURE.md](../ARCHITECTURE.md) §9 quality model.

## Purpose

- Make the intended distribution of tests explicit and defensible.
- Assign each band a clear coverage boundary so layers do not duplicate effort.
- Tie proportions to maintainability — the higher and slower the test, the fewer
  we keep and the more selectively we use it.
- Name and forbid anti-patterns (notably the inverted "ice-cream cone").

This is **documentation only** in Milestone 1. Automated bands are realized in
Milestone 5; specialized bands (performance, security, accessibility, visual) in
Milestone 7; the manual band's assets in Milestone 6.

## Scope

- **In scope:** the functional pyramid (unit → component → integration → contract
  → API → UI) plus four explicit cross-cutting bands (manual, performance,
  security, accessibility); per-band coverage, exclusions, owner, tooling,
  proportion, speed, and cost; anti-patterns.
- **Out of scope:** individual test cases (Milestone 6) and framework internals
  (Milestone 5).

## Responsibilities

| Role | Pyramid responsibility |
|------|------------------------|
| QA Architect | Owns the shape; defends proportions against erosion |
| QA Lead | Monitors actual vs target distribution; flags drift |
| SDET III | Owns integration, contract, and framework-heavy bands |
| SDET II | Owns unit, component, API, and UI automation bands |
| Senior QA Engineer | Owns the manual / exploratory band |

---

## 1. The Pyramid

```
                          ▲  slower • costlier • fewer
                         /U\         UI / E2E  (~10%)
                        /---\        API       (~15%)
                       / API \       contract  (~10%)
                      /-------\      integration (~20%)
                     / INTEG.  \     component  (~15%)
                    /-----------\    unit       (~30%)
                   /  UNIT BASE   \  ▼  faster • cheaper • many
                  +---------------+

   Cross-cutting bands (run across the stack, not stacked on it):
   [ Manual / Exploratory ] [ Performance ] [ Security ] [ Accessibility ]
```

The functional bands stack: most tests are fast, isolated, and cheap at the
base; few are slow, integrated, and expensive at the top. The four cross-cutting
bands apply specialized lenses across the whole stack rather than sitting on a
single rung.

## 2. Functional Bands

### Unit
- **Covers:** smallest units (a method, a pure function, a value object,
  invariants) in full isolation.
- **Does NOT cover:** wiring, the database, HTTP, real adapters, or cross-module flow.
- **Owner:** SDET II (with developers). **Tooling:** JUnit 5, Mockito.
- **Proportion:** ~30%. **Speed/cost:** milliseconds / very cheap.

### Component
- **Covers:** one component or slice (a single service, a React feature) with its
  immediate logic, edges mocked.
- **Does NOT cover:** inter-component integration or end-user journeys.
- **Owner:** SDET II. **Tooling:** JUnit 5, React Testing Library.
- **Proportion:** ~15%. **Speed/cost:** fast / cheap.

### Integration
- **Covers:** collaboration across modules, real database (Testcontainers/H2),
  transactions, audit writes, adapter wiring.
- **Does NOT cover:** browser/UI behavior or full multi-step business journeys.
- **Owner:** SDET II/III. **Tooling:** JUnit 5, Testcontainers, Rest Assured.
- **Proportion:** ~20%. **Speed/cost:** moderate / moderate.

### Contract
- **Covers:** provider/consumer agreements and FHIR R4 / HL7 v2 schema and
  code-system conformance.
- **Does NOT cover:** business-logic correctness beyond the contract shape.
- **Owner:** SDET III. **Tooling:** Rest Assured, JSON Schema, FHIR validator.
- **Proportion:** ~10%. **Speed/cost:** fast–moderate / cheap.

### API
- **Covers:** black-box REST/FHIR behavior — status codes, Problem Details
  errors, payloads, positive/negative/boundary cases.
- **Does NOT cover:** rendering, layout, or client-side logic.
- **Owner:** SDET II. **Tooling:** Rest Assured, JUnit 5 / TestNG.
- **Proportion:** ~15%. **Speed/cost:** moderate / moderate.

### UI / E2E
- **Covers:** complete user journeys per RBAC role through the assembled SUT.
- **Does NOT cover:** exhaustive field/branch permutations (push those down to
  API/unit) — UI proves *journeys*, not *permutations*.
- **Owner:** SDET II/III. **Tooling:** Playwright, Selenium, Cucumber.
- **Proportion:** ~10%. **Speed/cost:** slow / expensive.

## 3. Cross-Cutting Bands

These run **across** the stack and are sized by risk, not by a fixed percentage
of the functional total.

| Band | Covers | Does NOT cover | Owner | Tooling | Speed / cost |
|------|--------|----------------|-------|---------|--------------|
| Manual / Exploratory | Usability, ad-hoc discovery, first-pass new features, risk-guided probing | Repeatable regression (automate it) | Senior QA | Charters, session notes | Slow / human cost |
| Performance | Latency, throughput, scalability under load (owned infra only) | Functional correctness | SDET III | JMeter, k6, Gatling | Slow / infra cost |
| Security | OWASP top-10 surface, authz/authn, dependency CVEs, audit gaps | Functional behavior | SDET III | OWASP ZAP, Dependency-Check | Moderate / moderate |
| Accessibility | WCAG 2.1 AA conformance | Pixel-perfect visual fidelity | SDET II | axe-core, Lighthouse | Fast / cheap |

Visual testing (Playwright comparisons) and cross-browser checks ride on the UI
band across the browser matrix: Chrome, Edge, Firefox, Safari, WebKit, Mobile
Chrome, Mobile Safari.

## 4. Target Proportions

| Band | Target share of functional suite |
|------|----------------------------------|
| Unit | ~30% |
| Component | ~15% |
| Integration | ~20% |
| Contract | ~10% |
| API | ~15% |
| UI / E2E | ~10% |

Shares are guidance for the *functional* suite; cross-cutting bands are
additional and risk-sized. The QA Lead tracks actual versus target and raises
drift in retrospectives.

## 5. Anti-Patterns

| Anti-pattern | Symptom | Why it hurts | Correction |
|--------------|---------|--------------|------------|
| Ice-cream cone | Mostly UI/manual, few unit tests | Slow, flaky, expensive, hard to localize failures | Push coverage down to unit/API |
| Cupcake | Heavy at every layer; same case tested everywhere | Duplicated maintenance, slow suites | De-duplicate; assert each fact once, at the lowest layer |
| Hourglass | Many unit + many UI, thin middle | Integration/contract gaps escape to E2E | Invest in integration and contract bands |
| Test-after only | No shift-left; tests written post-hoc | High leakage, weak design feedback | Author unit/contract tests with the code |

## 6. Proportions and Maintainability

- **Lower bands are durable.** Unit and component tests are fast, isolated, and
  rarely flaky, so a large base keeps the suite trustworthy and cheap to run on
  every commit.
- **Higher bands are brittle.** UI/E2E tests are slow and sensitive to layout,
  timing, and data; keeping them few and journey-focused limits maintenance cost
  and flaky %.
- **Assert each fact once.** A boundary condition proven at the unit layer is not
  re-proven at the UI layer; this minimizes duplicated maintenance when behavior
  changes.
- **Flaky budget.** The pyramid shape is what keeps flaky % under the
  [Test Strategy](TEST_STRATEGY.md) target — an inverted pyramid blows it.

## Examples

- *Pushing a check down:* An invalid-NPI validation is proven exhaustively at the
  unit layer; the API layer asserts one representative rejection; the UI layer
  only confirms the user sees the error — three layers, no duplicated breadth.
- *Resisting erosion:* A PR that adds five new Playwright tests for logic already
  covered by API tests is sent back; the journeys are valuable, the duplicated
  permutations are not.

## Future Enhancements

- Automated pyramid-distribution report generated in CI from test metadata.
- Per-module pyramid dashboards highlighting bands that drift from target.
- Mutation-score gating on the unit band to keep the base meaningful, not merely large.

## Dependencies

- Operationalizes [TEST_STRATEGY.md](TEST_STRATEGY.md) and
  [ARCHITECTURE.md](../ARCHITECTURE.md) §9.
- Bands realized by Milestones 5–7 per [ROADMAP.md](../ROADMAP.md).
- Distribution feeds the metrics in the [Master Test Plan](MASTER_TEST_PLAN.md).

## References

- Mike Cohn, *Succeeding with Agile* (test pyramid).
- Martin Fowler, "TestPyramid" and "The Practical Test Pyramid".
- [PROJECT_METADATA.md](PROJECT_METADATA.md) §3 (tooling matrix).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial test pyramid and band model (Milestone 1) |
