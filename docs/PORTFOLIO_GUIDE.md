# Portfolio Guide

> **Purpose.** Present omiiCARE_QA as a portfolio piece that demonstrates
> enterprise-grade quality engineering. This guide gives the highlight reel, a
> role-to-evidence skills matrix, a demo outline, the technology matrix, the
> "what makes it enterprise-grade" case, talking points, links to key documents,
> and a suggested repo tour order.

## Scope

- **In scope:** how to showcase the repository to reviewers, hiring managers, and
  peers — narrative, evidence mapping, demo flow, and tour order.
- **Out of scope:** how to *discuss* it live in interviews (see
  [INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md)) and canonical facts (see
  [PROJECT_METADATA.md](PROJECT_METADATA.md)). The platform uses synthetic,
  PHI-safe data and makes no certification claims.

## Responsibilities

| Audience | How to use this guide |
|----------|-----------------------|
| You (portfolio owner) | Curate the story and demo order |
| Reviewer / hiring manager | Find evidence of competence quickly |
| Collaborator | Understand what to highlight when sharing the repo |

---

## 1. Highlight Reel

- **One monorepo, two platforms** — a healthcare web app *and* the enterprise QA
  platform that tests it, as first-class peers ([ADR 0001](architecture/adr/0001-monorepo-structure.md)).
- **Architecture before code** — Clean Architecture + DDD + Hexagonal backend, a
  modular React SUT, and an adapter-centric automation platform, all designed up
  front ([ARCHITECTURE.md](../ARCHITECTURE.md)).
- **Environment independence** — config-only switching across seven environments
  and a hybrid H2 ↔ PostgreSQL database ([ADR 0002](architecture/adr/0002-hybrid-database-h2-postgresql.md), [0004](architecture/adr/0004-resource-adapter-layer-automation.md)).
- **Governance as a feature** — ADRs, milestone fences/gates, a canonical metadata
  file, and DoD-mapped CI gates ([ADR 0008](architecture/adr/0008-documentation-first-governance.md)).
- **Full QA spectrum** — manual, automation, performance, security, accessibility,
  visual, contract/FHIR, chaos, observability, and AI-assisted testing on the roadmap.

## 2. Skills Matrix (role → evidence in repo)

| Role | Evidence |
|------|----------|
| Senior QA | M6 manual assets, RTM, test-case issue form, risk/release planning |
| Senior Automation Engineer | M5 Playwright/Selenium/Rest Assured/BDD + adapter layer + Allure/Extent |
| SDET II/III | Backend Clean Architecture ([ADR 0003](architecture/adr/0003-clean-architecture-ddd-backend.md)/[0005](architecture/adr/0005-java-spring-boot-backend.md)), M7 contract/FHIR, M8 CI/CD |
| QA Lead | [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md), fences/gates, PR + issue templates, metrics |
| QA Architect | Testability seams ([ARCHITECTURE.md](../ARCHITECTURE.md) §6–9), adapter + hybrid-DB ADRs |
| Principal QA | The whole system: monorepo strategy, ADR practice, roadmap to v2.0 |

## 3. Demo Guide Outline

1. **Open with the README and architecture diagram** — the C4 context and the
   "app + QA platform" framing.
2. **Show the ADR index** ([architecture/adr/](architecture/adr/README.md)) — prove
   decisions are recorded with alternatives and tradeoffs.
3. **Walk the roadmap** — ten fenced milestones to v1.0.0; explain a fence and a gate.
4. **Show governance artifacts** — PR template DoD checklist, issue forms, metadata.
5. **(Once built) live demo** — backend OpenAPI, a role portal, an automation run
   with Allure report, switching environments by configuration.
6. **Close with the v2.0 roadmap** — the seams already anticipated.

## 4. Technology Matrix (summary; canonical list in PROJECT_METADATA.md §3)

| Area | Technologies |
|------|--------------|
| Backend | Java 21, Spring Boot 3, Spring Security/JWT, JPA/Hibernate, Flyway, MapStruct, OpenAPI, Maven, JUnit 5/Mockito |
| Frontend | React 18, TypeScript, Vite, Material UI, React Router, TanStack Query, RHF + Zod, i18next |
| Database | H2 (dev/test) ↔ PostgreSQL (local/docker/qa/stage/prod), Flyway |
| Automation | Playwright, Selenium, Rest Assured, Cucumber/Gherkin, TestNG, Allure, Extent |
| Advanced QE | JMeter, k6, Gatling, OWASP ZAP, Dependency-Check, axe-core, Lighthouse |
| Infra/DevOps | Docker Compose, Redis, MinIO, Keycloak, WireMock, Prometheus, Grafana, SonarQube, GitHub Actions, OpenTelemetry |

## 5. What Makes It Enterprise-Grade

- **No demo code** — production-quality intent, milestone-fenced delivery.
- **Decision discipline** — every significant choice has an ADR with real alternatives.
- **Testability by design** — stable selectors, deterministic seams, adapters.
- **Quality gates** — DoD enforced in CI, mirrored by the PR checklist.
- **Standards awareness** — FHIR/HL7/ICD-10/CPT/LOINC/SNOMED, HIPAA-like privacy.
- **Forward seams** — multi-tenancy, eventing, observability anticipated, not bolted on.

## 6. Talking Points

- "I designed the architecture before writing application code, and recorded the
  reasoning as ADRs."
- "Tests target systems through adapters, not URLs, so the suite is
  environment-independent and extensible."
- "Governance is treated as a deliverable: fences prevent scope creep, gates make
  'done' objective."
- "Everything uses synthetic, PHI-safe data; the platform models HIPAA-like
  practices for education and makes no certification claims."

## 7. Links to Key Docs

- [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) — source of truth
- [ARCHITECTURE.md](../ARCHITECTURE.md) — target architecture
- [ROADMAP.md](../ROADMAP.md) — milestones, fences, gates
- [PROJECT_METADATA.md](PROJECT_METADATA.md) — canonical facts
- [architecture/adr/README.md](architecture/adr/README.md) — ADR index
- [INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md) — how to discuss it live

## 8. Suggested Repo Tour Order

1. [README.md](../README.md) — the front door.
2. [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) — mission and rules.
3. [ARCHITECTURE.md](../ARCHITECTURE.md) — the design.
4. [ROADMAP.md](../ROADMAP.md) — the plan and fences.
5. [architecture/adr/README.md](architecture/adr/README.md) — the decisions.
6. [PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md) — the map.
7. [.github/](../.github/PULL_REQUEST_TEMPLATE.md) — governance artifacts.
8. This guide and the [INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md) — how to present it.

## Examples

- Sharing the repo with a recruiter: send the README, then this guide's highlight
  reel and skills matrix mapped to the target role.
- A peer review: walk the tour order, pausing on the ADR index to show decision rigor.

## Future Enhancements

- Add screenshots/GIFs of the running app and Allure reports once M4/M5 land.
- Generate a one-page portfolio summary and badges (coverage, milestone progress) in M8.

## Dependencies

- Anchored by [PROJECT_METADATA.md](PROJECT_METADATA.md) and
  [ARCHITECTURE.md](../ARCHITECTURE.md).
- Complements [INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md).

## References

- [ROADMAP.md](../ROADMAP.md), [PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md)
- [architecture/adr/](architecture/adr/README.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial portfolio guide (Milestone 1) |
