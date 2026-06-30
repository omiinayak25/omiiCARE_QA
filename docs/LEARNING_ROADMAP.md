# Learning Roadmap — omiiCARE_QA

> **Purpose.** Turn this repository into a structured curriculum. It maps repo
> areas to QA/SDET competencies, sequences them from foundations to architecture,
> and ties each stage to a target role and concrete artifacts to study or build.
> Use it to grow deliberately, not just to browse.

## Scope

- **In scope:** competency stages, the repo areas that teach each one, hands-on
  exercises, and the role each stage supports.
- **Out of scope:** how to present the work ([PORTFOLIO_GUIDE.md](PORTFOLIO_GUIDE.md)),
  interview rehearsal ([INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md)), and navigation
  ([REPOSITORY_TOUR.md](REPOSITORY_TOUR.md)).

## Responsibilities

| Audience | How to use this roadmap |
|----------|-------------------------|
| Learner | Work the stages in order; do the exercises, not just the reading |
| Mentor | Assign a stage per sprint; review the exercise output |

---

## How to use this roadmap

1. Start at Stage 1 even if you are experienced — it grounds you in *this* system.
2. For each stage: read the **Study** docs, open the **Where in repo** code, then
   complete the **Exercise**.
3. Track yourself against the **Target role** column; you are "ready" for a role
   when you can do every exercise up to and including its stage unaided.

## Stage Map

| Stage | Competency | Target role |
|-------|-----------|-------------|
| 1 | Functional QA + the SUT | Junior / QA Analyst |
| 2 | Manual QE discipline | Senior QA / QA Analyst II |
| 3 | API + UI automation | Automation Engineer |
| 4 | SDET: code-level testing | SDET I/II |
| 5 | Advanced quality (perf/security/a11y/visual/contract) | SDET II/III |
| 6 | CI/CD + release engineering | SDET III / QA in DevOps |
| 7 | Architecture + governance | QA Architect / Principal QA |
| 8 | AI-native QE (opt-in) | QE Innovator |

---

## Stage 1 — Functional QA & the System Under Test

- **Competency:** read requirements, run the app, find/verify a bug.
- **Study:** [DEMO_GUIDE.md](DEMO_GUIDE.md), [README.md](../README.md),
  [BUSINESS_RULES.md](BUSINESS_RULES.md).
- **Where in repo:** `apps/frontend/`, run backend `:8080` + frontend `:5173`.
- **Exercise:** complete the full [DEMO_GUIDE.md](DEMO_GUIDE.md) flow; write a
  manual test case for BR-APPT-001 (double-booking → 422).

## Stage 2 — Manual Quality Engineering Discipline

- **Competency:** test plans, RTM, suites, defect lifecycle, release sign-off.
- **Study:** [MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md), [RTM.md](RTM.md),
  [TEST_DATA_STRATEGY.md](TEST_DATA_STRATEGY.md), [RISK_ANALYSIS.md](RISK_ANALYSIS.md).
- **Where in repo:** `manual-testing/` (requirements, test-cases, suites, signoff).
- **Exercise:** add a test suite for the Appointment module and trace each case
  back to a requirement in the RTM.

## Stage 3 — API & UI Automation

- **Competency:** automate against APIs and the UI; understand the adapter pattern.
- **Study:** [TEST_STRATEGY.md](TEST_STRATEGY.md), [TEST_PYRAMID.md](TEST_PYRAMID.md),
  [ADR 0004](architecture/adr/0004-resource-adapter-layer-automation.md).
- **Where in repo:** `automation/restassured/`, `automation/playwright/`,
  `automation/selenium/`, `automation/bdd/`, `automation/shared/core/`.
- **Exercise:** add a Rest Assured test that asserts the `422`/`OMII-422` body for
  a double-booking, and a Playwright test for the same flow via the UI.

## Stage 4 — SDET: Code-Level Testing

- **Competency:** read/extend backend code; write unit + slice tests; understand
  Clean Architecture seams.
- **Study:** [ARCHITECTURE.md](../ARCHITECTURE.md),
  [DESIGN_PATTERNS.md](DESIGN_PATTERNS.md),
  [ADR 0003](architecture/adr/0003-clean-architecture-ddd-backend.md),
  [CODING_STANDARDS.md](CODING_STANDARDS.md).
- **Where in repo:** `apps/backend/src/main/java/com/omiicare/qa/appointment/`,
  `.../shared/error/ErrorCode.java`, backend test sources.
- **Exercise:** write a JUnit test proving `AppointmentService` rejects an
  overlapping interval, and a negative-auth test (403 on missing authority).

## Stage 5 — Advanced Quality Engineering

- **Competency:** performance, security, accessibility, visual, contract testing.
- **Study:** [PERFORMANCE_GUIDE.md](PERFORMANCE_GUIDE.md),
  [SECURITY_TESTING_GUIDE.md](SECURITY_TESTING_GUIDE.md),
  [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md),
  [FHIR_GUIDE.md](FHIR_GUIDE.md).
- **Where in repo:** `quality/performance/`, `quality/security/`,
  `quality/accessibility/`, `quality/visual/`, `quality/contract-testing/`.
- **Exercise:** run a k6 load profile against `GET /api/v1/patients`; run an
  axe-core scan on the patient screen; validate the FHIR Patient against the
  schema in `quality/contract-testing/`.

## Stage 6 — CI/CD & Release Engineering

- **Competency:** reusable pipelines, quality gates, semantic-version releases.
- **Study:** [CI_CD_GUIDE.md](CI_CD_GUIDE.md), [QUALITY_GATES.md](QUALITY_GATES.md),
  [VERSIONING.md](../VERSIONING.md), [BRANCHING_STRATEGY.md](BRANCHING_STRATEGY.md).
- **Where in repo:** `.github/workflows/`.
- **Exercise:** add a new check to a `_reusable-*.yml` workflow and explain how it
  maps to a Definition-of-Done item in [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md).

## Stage 7 — Architecture & Governance

- **Competency:** reason about system design, write ADRs, design for testability.
- **Study:** [architecture/adr/README.md](architecture/adr/README.md),
  [PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md),
  [ADR 0001](architecture/adr/0001-monorepo-structure.md),
  [ADR 0008](architecture/adr/0008-documentation-first-governance.md).
- **Where in repo:** `docs/architecture/`, root governance docs.
- **Exercise:** draft an ADR (using `0000-adr-template.md`) for a hypothetical
  change — e.g. adding an event bus — with real alternatives and tradeoffs.

## Stage 8 — AI-Native QE (opt-in)

- **Competency:** apply LLM assistance with guardrails; keep it optional.
- **Study:** [AI_DEVELOPMENT_RULES.md](AI_DEVELOPMENT_RULES.md),
  [AI_DEVELOPMENT_WORKFLOW.md](AI_DEVELOPMENT_WORKFLOW.md),
  `ai/documentation/AI_SECURITY_GUARDRAILS.md`.
- **Where in repo:** `ai/providers/`, `ai/prompts/`, `ai/quality/`.
- **Exercise:** with `omii.ai.enabled=false`, confirm the platform is fully
  functional; then describe one safe AI-assisted QE task and its guardrail.

## Competency → Repo Quick Index

| Competency | Primary repo area |
|------------|-------------------|
| Functional testing | `apps/frontend/`, [DEMO_GUIDE.md](DEMO_GUIDE.md) |
| Manual QE | `manual-testing/` |
| API automation | `automation/restassured/` |
| UI automation | `automation/playwright/`, `automation/selenium/` |
| BDD | `automation/bdd/` |
| Backend/code testing | `apps/backend/` |
| Performance | `quality/performance/` |
| Security | `quality/security/` |
| Accessibility | `quality/accessibility/` |
| Contract/FHIR | `quality/contract-testing/`, `apps/backend/.../fhir/` |
| CI/CD | `.github/workflows/` |
| Architecture | `docs/architecture/` |
| AI-assisted QE | `ai/` |

## Examples

- *Targeting an SDET II role:* complete Stages 1–5; the Stage 4 and 5 exercises are
  your strongest evidence.
- *Targeting QA Architect:* complete through Stage 7 and author at least one ADR.

## Future Enhancements

- Add a self-assessment checklist with a score per stage.
- Link each exercise to a starter branch/issue template.

## Dependencies

- Builds on [REPOSITORY_TOUR.md](REPOSITORY_TOUR.md) and
  [FEATURE_MATRIX.md](FEATURE_MATRIX.md).
- Feeds presentation in [PORTFOLIO_GUIDE.md](PORTFOLIO_GUIDE.md).

## References

- [TEST_STRATEGY.md](TEST_STRATEGY.md) · [TEST_PYRAMID.md](TEST_PYRAMID.md)
- [architecture/adr/README.md](architecture/adr/README.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial (Milestone 10) |
