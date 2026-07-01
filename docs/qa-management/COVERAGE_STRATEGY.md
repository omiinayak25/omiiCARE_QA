# Coverage Strategy — omiiCARE_QA

> **Status:** v1.0.0 baseline. Coverage is **report-only** today and hardens to
> **enforced (blocking)** on the schedule below. See
> [`../QUALITY_GATES.md`](../QUALITY_GATES.md) for how the coverage gate is wired
> into CI and when it becomes blocking.

This document defines **what** we measure for code coverage, **per which
module/layer**, **how** it is collected, aggregated and reported, and the
**gating policy** that moves coverage from advisory to enforced.

---

## 1. Scope & Tooling

| Surface | Module / Path | Tool | Report format | Profile / Command |
|---|---|---|---|---|
| Backend (Spring Boot 3, Java 21) | `apps/backend` | **JaCoCo** | `exec` + HTML + XML | `mvn -pl apps/backend -Pquality test` → `apps/backend/target/site/jacoco/` |
| Automation framework (unit) | `automation` | **JaCoCo** | `exec` + HTML + XML | `mvn -pl automation test` (98 unit tests) → `automation/target/site/jacoco/` |
| AI module | `ai` | **JaCoCo** | `exec` + HTML + XML | `mvn -pl ai -Pquality test` |
| Frontend (React + Vite, TS) | `apps/frontend` | **Vitest + V8 coverage** *(planned)* | `lcov` + HTML + JSON-summary | `npm run test:coverage` *(planned)* → `apps/frontend/coverage/` |

Notes:
- Backend follows DDD layering (`domain`, `application`, `api`, `infrastructure`
  packages per bounded context, e.g. `com.omiicare.qa.patient.{domain,api,...}`).
  Coverage targets are set **per layer**, not as a single repo number.
- The **automation** module's coverage measures the **test framework itself**
  (Resource Adapter Layer, generators, FHIR/HL7/DB helpers) — not the SUT.
  SUT/browser tests (`ui-e2e`/`api-e2e`/`bdd`, run via `-Pe2e`) are **excluded**
  from unit-coverage numbers; they contribute to functional coverage (RTM), not
  line coverage.
- Frontend coverage tooling (Vitest + `@vitest/coverage-v8` + jsdom) is **(planned)**;
  current `apps/frontend` scripts are `build` / `lint` / `typecheck` only.
  Until landed, the frontend coverage gate is **N/A (not yet measured)**.

---

## 2. Coverage Targets (per module / layer)

Targets are expressed as **line / branch** percentages. They are **goals today**
and become **enforced minimums** per the gating schedule in §5.

### Backend — `apps/backend`

| Layer | Packages (per bounded context) | Line | Branch | Rationale |
|---|---|---|---|---|
| **Domain** | `*.domain` (entities, value objects, domain services) | **90%** | **85%** | Pure business logic, no I/O — highest testability & risk |
| **Application** | `*.application` (use-cases, orchestration) | **85%** | **80%** | Coordinates domain; high behavioral value |
| **API** | `*.api` (controllers, DTOs, mappers) | **80%** | **70%** | Thin layer; covered also by `api-e2e` |
| **Infrastructure** | `*.infrastructure` (repos, adapters, config) | **80%** | **70%** | I/O-bound; integration-tested, some glue excluded |

### Automation framework — `automation` (`com.omiicare.qa.automation`)

| Component | Line | Branch | Rationale |
|---|---|---|---|
| **Core** (`core.adapter` Resource Adapter Layer, `core.generators`) | **75%** | **70%** | Framework backbone; correctness here protects every test |
| Assertions/validators (`fhir/`, `hl7/`, `db/`) | **75%** | **70%** | Validation logic must be self-tested |
| Reporting / env / glue | best-effort | — | Excluded from blocking gate (see §3 exclusions) |

### Frontend — `apps/frontend` *(planned)*

| Layer | Line | Branch | Rationale |
|---|---|---|---|
| Hooks / utils / domain logic | **85%** | **80%** | Pure logic, high value |
| Components | **75%** | **70%** | Render + interaction via Testing Library |
| App shell / config / generated | excluded | — | Low value, see exclusions |

### AI module — `ai`

| Component | Line | Branch |
|---|---|---|
| Core logic | **75%** | **70%** |

---

## 3. Exclusions

Excluded from coverage denominators (keeps targets meaningful):

- **Generated code**, MapStruct/Lombok-generated members, OpenAPI stubs.
- **DTOs / records** that are pure data carriers with no logic.
- **Config classes** (`*Config`, `*Application`, `@Configuration` bootstrap).
- **Backend:** `*.config`, `*Application.java`, generated mappers.
- **Automation:** reporting glue (Allure/Extent wiring), `env` bootstrap,
  step-definition glue that only delegates.
- **Frontend (planned):** `*.d.ts`, `vite.config.ts`, `main.tsx`, `**/*.stories.*`,
  test files, generated API clients.

> Exclusions are declared in the build (JaCoCo `excludes`, Vitest
> `coverage.exclude`) — **not** by deleting tests. Adding an exclusion requires
> reviewer sign-off in PR.

---

## 4. How Coverage Is Measured, Aggregated & Reported

### Measured
- **Java:** JaCoCo agent instruments bytecode during `test`; each module emits
  `target/jacoco.exec` → `report` goal renders HTML + `jacoco.xml`.
- **Frontend (planned):** Vitest runs with V8 coverage provider → `coverage/`
  (`lcov.info`, `coverage-summary.json`, HTML).

### Aggregated
- **Per-module first** (each reactor module owns its number — no single blended
  repo % that hides a weak module).
- A **roll-up summary** is produced in CI by parsing each `jacoco.xml` and the
  frontend `coverage-summary.json` into one Markdown table (line/branch per
  module + per backend layer). Aggregation is **report-time only**; gate
  decisions are evaluated **per module/layer** against §2.
- Cross-module aggregation uses the standard `report-aggregate` pattern *(planned
  for the reactor roll-up job)*; until then each module reports independently.

### Reported
- **CI artifacts:** `apps/backend/target/site/jacoco/`,
  `automation/target/site/jacoco/`, `ai/target/site/jacoco/`,
  `apps/frontend/coverage/` (planned) uploaded per run.
- **PR comment / job summary:** roll-up table posted to the run summary so
  reviewers see deltas without downloading artifacts.
- **SonarQube** (`infrastructure/docker` stack) ingests `jacoco.xml` + `lcov.info`
  for trend dashboards and quality-profile tracking *(SonarQube ingestion: planned)*.
- **Trend:** coverage history is tracked release-over-release in
  [`QA_METRICS.md`](./QA_METRICS.md).

---

## 5. Gating Policy (report-only → enforced)

Coverage hardens over time so the team is never blocked by a number it has not
yet earned. This mirrors the gate-hardening philosophy in
[`../QUALITY_GATES.md`](../QUALITY_GATES.md).

| Phase | When | Behavior | Failure effect |
|---|---|---|---|
| **P0 — Report-only** | v1.0.0 (now) | JaCoCo/Vitest run, reports published as artifacts + summary | Never fails build (advisory) |
| **P1 — Baseline freeze** | next minor *(planned)* | Record current % as the **floor** per module/layer; **no-regression** check | Build fails only if coverage **drops** below recorded floor |
| **P2 — Ratchet to target** | subsequent minors *(planned)* | Floors raised stepwise toward §2 targets (JaCoCo `check` rule enabled) | Build fails if below ratcheted minimum |
| **P3 — Enforced target** | v2.0.0 *(planned)* | Full §2 targets enforced as **blocking** | PR/merge blocked until met |

Rules:
- **No-regression always wins:** once a floor is recorded (P1+), a PR may not
  lower it, even while the absolute target is still being ratcheted.
- **New code stricter:** new/changed files are expected to meet the **target**
  (§2) immediately, even during P0–P2 ("new-code coverage"), via the diff
  coverage check *(planned in `_reusable-quality.yml`)*.
- Lowering a target or adding an exclusion requires QA lead approval in PR.
- The coverage gate maps to **Gate 3 (Backend coverage)** in
  [`../QUALITY_GATES.md`](../QUALITY_GATES.md); this document is its detailed
  spec and supersedes the one-line summary there for thresholds.

---

## 6. Local Developer Commands

```bash
# Backend coverage (HTML at apps/backend/target/site/jacoco/index.html)
mvn -pl apps/backend -Pquality test

# Automation framework coverage (98 unit tests; excludes -Pe2e SUT tests)
mvn -pl automation test

# AI module coverage
mvn -pl ai -Pquality test

# Frontend coverage (planned)
cd apps/frontend && npm run test:coverage   # -> apps/frontend/coverage/
```

> Performance & security suites are **not** part of code-coverage gating and run
> only on owned/local environments. Functional/requirements coverage is tracked
> separately via the RTM (`manual-testing/rtm/`), not by line coverage.

---

## 7. Related Documents
- [`../QUALITY_GATES.md`](../QUALITY_GATES.md) — CI gate wiring & blocking policy
- [`QA_METRICS.md`](./QA_METRICS.md) — coverage trend tracking
- [`TEST_AUTOMATION_STRATEGY.md`](./TEST_AUTOMATION_STRATEGY.md) — test pyramid & layers
- [`ENTRY_EXIT_CRITERIA.md`](./ENTRY_EXIT_CRITERIA.md) — release gates
