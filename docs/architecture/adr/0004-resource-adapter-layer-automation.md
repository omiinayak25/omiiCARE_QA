# 0004. Resource adapter layer for the automation platform

## Status

Accepted — 2026-06-30

## Context / Problem

The quality-engineering platform (Milestone 5) must test multiple target systems
— the local omiiCARE app plus external/reference systems such as OpenMRS,
OpenEMR, HAPI FHIR, SMART Health IT, OpenFDA, DummyJSON, and Restful Booker — and
must run those tests across many environments (dev/test/local/docker/qa/stage/
prod). If test code embeds URLs, endpoints, and per-system request details
directly, then every environment change or new target forces edits across the
test suite, tests become brittle, and adding a target means duplicating logic. We
must decide how tests reference the systems they exercise so the suite stays
stable, portable, and extensible.

## Decision Drivers

- Environment independence: switching target environments must be configuration
  only.
- Extensibility: adding a new target system should mean adding code in one place,
  not editing many tests.
- Test stability: tests express intent (what to verify), not transport detail
  (where/how).
- Reuse: Playwright, Selenium, Rest Assured, and BDD layers share one model.
- Consistency with backend ports/adapters thinking
  ([0003](0003-clean-architecture-ddd-backend.md)).

## Alternatives Considered

### Alternative A — Resource adapter layer behind a common interface (chosen)
- **Pros:** every target implements a shared interface; tests call the interface,
  never the system; adding a target = adding an adapter; switching environments =
  configuration only; URLs/credentials/endpoints centralize in config; one model
  serves UI, API, and BDD layers.
- **Cons:** upfront design of the adapter contract; an abstraction layer to learn;
  the interface must be general enough across heterogeneous systems yet specific
  enough to be useful.

### Alternative B — Direct URLs/endpoints in test code
- **Pros:** quickest to write the first test; nothing to abstract.
- **Cons:** environment changes ripple across the whole suite; new targets force
  duplication; tests are brittle and coupled to transport; no reuse across
  frameworks; poor portfolio signal.

### Alternative C — Per-framework config files without a unifying interface
- **Pros:** centralizes URLs better than inline literals.
- **Cons:** still couples tests to system-specific call shapes; no single contract;
  Playwright/Selenium/Rest Assured/BDD each reinvent access; targets are not
  uniformly swappable.

## Decision

We will introduce a **Resource Adapter Layer**: each target system implements a
common adapter interface, and all tests interact with target systems through that
interface rather than through URLs or system-specific calls. Environment and
target selection is driven entirely by configuration. Shared `core`, `config`,
`drivers`, `reporting`, and related layers serve Playwright, Selenium, Rest
Assured, and BDD uniformly, so a real vendor system can replace a stub (WireMock)
without touching business or test logic.

## Consequences / Tradeoffs

**Positive**
- Tests are portable across environments with zero test-code changes.
- New target systems plug in as adapters; the suite scales by addition.
- Tests read as intent, improving stability and maintainability.
- A single, reusable model across all automation frameworks — strong portfolio signal.

**Negative / Accepted tradeoffs**
- The adapter contract must be designed carefully and may evolve as new target
  types appear; interface changes touch all adapters.
- A layer of indirection that contributors must learn and respect.
- Generality vs specificity tension in the interface must be managed deliberately.

## Future Impact

The adapter layer is the seam that lets the suite grow to many systems and
environments without rewrites, and lets stubbed externals (WireMock) be swapped
for real integrations later (post-1.0). It mirrors the backend's ports/adapters
approach, keeping the mental model consistent across the codebase, and supports
distributed test execution on the v2.0 roadmap.

## References

- [ARCHITECTURE.md](../../../ARCHITECTURE.md) §6 (Automation architecture)
- [ROADMAP.md](../../../ROADMAP.md) Milestone 5
- [0003](0003-clean-architecture-ddd-backend.md) (ports/adapters in the backend)
- [docs/DESIGN_PATTERNS.md](../../DESIGN_PATTERNS.md) (Adapter, Strategy, Factory)
