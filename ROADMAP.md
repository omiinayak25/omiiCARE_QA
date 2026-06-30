# Roadmap

> **Purpose.** The authoritative milestone-and-version plan for omiiCARE_QA. It
> defines what each milestone delivers, the explicit "do-not-build" fence that
> prevents scope creep, the gate that closes it, and the post-1.0 future. The
> [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md) summarizes
> this; the detail lives here.

## Scope

Milestones 1–10 (the path to v1.0.0) and the documented future versions
(v1.1 → v2.0). Day-to-day execution detail belongs in issues and sprint boards;
this is the strategic plan.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Maintainer | Approve milestone transitions; protect the fences |
| QA Architect | Keep deliverables and gates accurate as designs evolve |
| Contributors | Work only within the active milestone's scope |

---

## Status Legend

✅ Complete · 🟦 In progress · ⬜ Not started

## Milestone Plan (path to 1.0.0)

### ✅ Milestone 1 — Foundation, Architecture & Governance
**Goal:** Establish the complete architecture and governance baseline.
**Deliverables:** enterprise architecture; monorepo structure; all root and
`docs/` documentation; ADR structure; GitHub/issue/PR templates; `CODEOWNERS`;
`.editorconfig`/`.gitattributes`/`.gitignore`; roadmaps; development & AI
workflows; repository standards; cross-document consistency review.
**Fence:** Do **not** build any application, API, or automation code.
**Gate:** Every governance document exists, is internally consistent, and is
free of placeholders/TODOs.

### ✅ Milestone 2 — Enterprise Infrastructure & Environment Foundation
**Goal:** The technical foundation every module reuses.
**Deliverables:** environments (dev/test/local/docker/qa/stage/prod) + matrix &
feature flags; profile-driven hybrid DB (H2 ↔ PostgreSQL); Spring profile files;
Flyway migrations + repeatable + seeds + rollback docs; `database/` module;
Docker Compose (Postgres, Redis, MailHog, MinIO, Keycloak, WireMock, Grafana,
Prometheus, SonarQube) with health checks/volumes/networks; cross-platform
`setup/start/stop/reset/health-check` scripts; config/monitoring/logging/security
foundations; OpenTelemetry wiring; reusable GitHub Actions *structure* (not full
pipelines); Spotless/Checkstyle/PMD/SpotBugs/pre-commit.
**Fence:** No auth, healthcare modules, FHIR, REST APIs, automation, UI, or
business logic.
**Gate:** Infrastructure starts; all DB profiles work; Flyway + seeds load;
monitoring up; scripts pass; no duplicated configuration.

### ✅ Milestone 3 — Enterprise Healthcare Platform Core (Backend)
**Goal:** Backend foundation (Clean Architecture + DDD).
**Deliverables:** healthcare domain model; multi-tenant architecture;
event-driven seams (events, Outbox, DLQ — documented); background jobs;
file/document management (MinIO); search; notification framework; external
integration adapters (stubbed/WireMock); authentication (JWT/refresh/RBAC);
authorization; API standards (`/api/v1/`, Problem Details, pagination); API
versioning policy; audit/validation/exception frameworks; initial Flyway schema;
standards mapping (FHIR/HL7/ICD-10/CPT/LOINC/SNOMED); JUnit/Mockito test
foundation.
**Fence:** No frontend, automation framework, or perf/security/a11y/mobile testing.
**Gate:** Backend starts; auth works; OpenAPI loads; Flyway + seeds load;
frameworks work; unit tests pass.

### ✅ Milestone 4 — Enterprise Frontend Platform & Healthcare Portals
**Goal:** Production-quality React web app — the primary SUT.
**Deliverables:** modular architecture; authentication UI; role-based portals
(Patient, Doctor, Nurse, Reception, Lab, Radiology, Pharmacy, Billing, Insurance,
Admin, Super Admin); reusable component library; accessibility (WCAG AA);
responsive + PWA strategy; security (route/permission guards); API integration;
i18n + theming; testing-readiness (stable selectors).
**Fence:** No Playwright/Selenium/Rest Assured or any test assets.
**Gate:** App builds; auth integrates with backend; role nav + responsive
layouts work; a11y met; component library exists; portals functional.

### ✅ Milestone 5 — Enterprise Quality Engineering Platform (Automation)
**Goal:** The enterprise testing ecosystem — modular, config-driven,
environment-independent.
**Deliverables:** Playwright, Selenium, Rest Assured, BDD; architectural patterns
(POM, components, Builder/Factory/Strategy/Adapter, DI); shared core/config/
drivers/listeners/reporting/utilities/generators/assertions; resource adapter
layer (Local omiiCARE, OpenMRS, OpenEMR, HAPI FHIR, SMART Health IT, OpenFDA,
DummyJSON, Restful Booker); browser matrix; test-data platform; assertion
libraries; Allure/Extent reporting; centralized logging; flexible execution.
**Fence:** No thousands of test cases; no perf/security/a11y/visual yet.
**Gate:** All frameworks + adapter architecture + reporting + environment
switching operational.

### ✅ Milestone 6 — Enterprise Manual Quality Engineering Assets
**Goal:** A complete manual-testing repository another QA team could execute a
release from.
**Deliverables:** requirements + RTM; test pyramid; master/sprint/release plans;
manual test cases across all modules; specialized suites (smoke/sanity/
regression/negative/boundary/exploratory…); defect management; release/risk
management; metrics; knowledge base; PHI-safe test data.
**Fence:** No perf/security/a11y/visual automation; no large-scale automated
test implementation.
**Gate:** RTM, plans, suites, templates, risk/release docs, metrics, KB complete
and traceable.

### ✅ Milestone 7 — Advanced Quality Engineering Platform
**Goal:** Performance, security, accessibility, visual, database, contract,
chaos, resilience, observability, compliance.
**Deliverables:** JMeter/k6/Gatling performance framework (owned infra only);
OWASP ZAP security; axe/Lighthouse accessibility; Playwright visual; DB testing;
contract/FHIR validation; chaos architecture; resilience patterns; observability
(Prometheus/Grafana on the OTel foundation); compliance validation.
**Fence:** Reusable frameworks + representative suites — not thousands of
executions; no formal certification claims.
**Gate:** Frameworks operational; chaos documented; observability integrated.

### ✅ Milestone 8 — Enterprise DevOps, CI/CD & Release Engineering
**Goal:** Production-grade DevOps/CI/CD.
**Deliverables:** reusable GitHub Actions (build/lint/test/scan/release/deploy);
pipeline & branch strategy with protections; GitHub project management; quality
gates; SonarQube/SpotBugs/PMD/Checkstyle/Spotless/Dependency-Check; JaCoCo +
coverage trends; Docker image build/tag/scan; artifacts; semantic versioning
automation; documentation automation; notifications; secrets; environment
promotion; deployment docs; badges.
**Fence:** No AI enhancements yet.
**Gate:** Reusable workflows + quality gates operational; coverage + SonarQube +
Docker builds + release automation prepared.

### ✅ Milestone 9 — AI-Native Quality Engineering Platform
**Goal:** AI-first QA — optional, transparent, explainable, reviewable.
**Deliverables:** provider abstraction (Claude/OpenAI/local); prompt library;
AI capabilities (requirement analysis, test generation, failure analysis,
bug-report assistant, coverage/regression/risk analysis, documentation/code
review, healthcare awareness, execution reports); knowledge base; configuration;
AI security guardrails.
**Fence:** No autonomous code modification or self-merging.
**Gate:** Provider abstraction, prompt library, and assistants operational.

### ✅ Milestone 10 — Production Hardening, Portfolio Excellence & Release 1.0.0
**Goal:** Public release readiness — a full engineering audit.
**Deliverables:** architecture/code/performance/security/automation/observability
review; documentation audit; GitHub configuration; portfolio optimization;
sample data; developer-experience review; open-source readiness; v1.0.0 release
notes + future roadmap; final quality audit.
**Fence:** No new major features.
**Gate:** All builds/tests pass; docs synchronized; CI/CD + infra + monitoring
operational; reviews complete; no critical debt/placeholders/dead code; ready
for **v1.0.0**.

## Milestone Dependency Flow

```
M1 ─▶ M2 ─▶ M3 ─▶ M4 ─▶ M5 ─▶ M6 ─▶ M7 ─▶ M8 ─▶ M9 ─▶ M10 (v1.0.0)
docs   infra  backend frontend  auto   manual  adv-QE  CI/CD  AI    release
```
Each milestone's gate is the entry criterion for the next. Fences are
non-negotiable without a roadmap-amending ADR.

## Future Versions (post-1.0, roadmap-only)

| Version | Theme | Candidate scope |
|---------|-------|-----------------|
| v1.1 | Coverage depth | More test suites, additional FHIR resources, more portals polish |
| v1.2 | Observability & data | Richer dashboards, expanded synthetic datasets, more standards (e.g. NCPDP) |
| v2.0 | Scale-out | Microservices split, Kubernetes, distributed DB, cloud deployment, native mobile automation, distributed test execution, event-bus implementation, GraphQL/gRPC surfaces |

These map directly to the [Out of Scope for v1.0](MASTER_PROJECT_SPECIFICATION.md#3-out-of-scope-for-v10)
items, which v2.0 begins to address.

## Examples

- *Entry check:* Milestone 3 may begin only once Milestone 2's gate (DB profiles,
  Flyway, monitoring, scripts) is fully met.
- *Fence enforcement:* a PR adding a React component during Milestone 3 is
  rejected — frontend is Milestone 4.

## Future Enhancements

- Burn-up/burn-down and milestone-progress badges generated in CI (M8).
- Link each milestone to a GitHub Milestone and project board (M8).

## Dependencies

- Scoped by [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md).
- Each milestone realizes part of [ARCHITECTURE.md](ARCHITECTURE.md).
- Changes are logged in [CHANGELOG.md](CHANGELOG.md).

## References

- [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md)
- [docs/DEFINITION_OF_DONE.md](docs/DEFINITION_OF_DONE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Maintainer | Initial 10-milestone roadmap + future versions (Milestone 1) |
