# omiiCARE_QA

[![Build](https://img.shields.io/badge/build-passing-brightgreen?style=flat-square)](.github/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](LICENSE)
[![Version](https://img.shields.io/badge/version-1.0.0-brightgreen?style=flat-square)](CHANGELOG.md)
[![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=openjdk&logoColor=white)](pom.xml)
[![React](https://img.shields.io/badge/React-18-61DAFB?style=flat-square&logo=react&logoColor=black)](apps/frontend/package.json)
[![Docs](https://img.shields.io/badge/docs-passing-brightgreen?style=flat-square)](docs/)
[![Quality Gates](https://img.shields.io/badge/quality%20gates-enforced-success?style=flat-square)](docs/QUALITY_GATES.md)
[![SemVer](https://img.shields.io/badge/semver-2.0.0-informational?style=flat-square)](VERSIONING.md)
[![Conventional Commits](https://img.shields.io/badge/commits-conventional-yellow?style=flat-square)](CONTRIBUTING.md)

> **Enterprise Healthcare Quality Engineering Platform.** omiiCARE_QA is a single, modular monorepo that pairs a production-grade healthcare web application (the System Under Test) with a first-class quality-engineering platform that tests it across UI, API, database, performance, security, accessibility, visual, contract, chaos, resilience, observability, and compliance layers — all on synthetic, PHI-safe data. This is the **1.0.0 release**: all ten milestones are complete and the reactor builds green.

---

## Why This Exists

Quality engineering for clinical systems must be rigorous, auditable, and standards-aware — yet most reference projects show only a slice. omiiCARE_QA was built **documentation-first** to demonstrate, end to end, what world-class healthcare QE looks like when it is *designed* rather than improvised.

| Driver | What it means here |
|--------|--------------------|
| Healthcare is high-stakes | Standards-aware by design: FHIR R4, HL7, ICD-10, CPT, LOINC, SNOMED CT — with audit, validation, and consent frameworks baked into the backend. |
| QA is a first-class peer | The test platform is an equal of the application, not an afterthought — adapter-centric automation plus a ten-discipline `quality/` suite. |
| Architecture beats accident | Clean Architecture + DDD on the backend, modular React on the frontend, and a resource-adapter automation core make the system testable *by design*. |
| Portfolio-grade transparency | Everything is open, documented, and **PHI-safe**, so the repository can teach as well as run. See [docs/PORTFOLIO_GUIDE.md](docs/PORTFOLIO_GUIDE.md) and [docs/INTERVIEW_GUIDE.md](docs/INTERVIEW_GUIDE.md). |

## Feature Highlights

| Capability | Description |
|------------|-------------|
| Healthcare platform core | Java 21 / Spring Boot 3 domain — Patient, Provider, Appointment — with JWT auth, RBAC, and audit/validation/exception frameworks. |
| FHIR R4 Patient facade | Standards-conformant `GET /api/v1/fhir/Patient/{id}` exposing the internal model as an FHIR R4 resource. |
| Role-based React portals | React 18 + TypeScript + Vite + MUI SUT with permission-aware navigation and stable `data-testid` hooks for automation. |
| Adapter-centric automation | Rest Assured, Playwright, Selenium, and Cucumber unified over a shared resource-adapter layer. |
| Ten-discipline quality suite | Performance, security, accessibility, visual, database, contract, chaos, resilience, observability, and compliance testing. |
| AI-native QE | Provider-abstracted AI layer (Claude / OpenAI / local) with a prompt library and guardrails — configuration-driven, never vendor-coupled. |
| Manual QE assets | ~46 documents: requirements, RTM, test plans, suites, defect and release management. |
| RFC 7807 error contract | Structured `ProblemDetail` responses with stable `OMII-4xx` codes; business rule **BR-APPT-001** blocks double-booking with `422`. |
| Profile-driven environments | H2 ↔ PostgreSQL switch by configuration only; Flyway migrations, Docker Compose stack, and reusable GitHub Actions CI/CD. |

## Architecture at a Glance

```
                          ┌─────────────────────────────────────────────┐
                          │                 omiiCARE_QA                  │
                          │      Healthcare Quality Engineering Platform │
                          └─────────────────────────────────────────────┘
                                            │
   ┌────────────────────────────┬──────────┴───────────┬─────────────────────────────┐
   │                            │                       │                             │
┌──▼───────────────┐   ┌────────▼─────────┐   ┌─────────▼──────────┐   ┌──────────────▼─────────┐
│  apps/frontend   │   │   apps/backend   │   │     automation     │   │       quality          │
│  React 18 + Vite │   │ Java 21 / Spring │   │ RestAssured /      │   │ perf · security · a11y │
│  MUI · RBAC SUT  │◄──┤ Boot 3 · JWT/RBAC│◄──┤ Playwright /       │   │ visual · db · contract │
│  :5173           │   │ FHIR R4 · audit  │   │ Selenium / Cucumber│   │ chaos · resilience ·   │
│                  │   │ :8080            │   │ + resource adapters│   │ observability ·        │
└──────────────────┘   └────────┬─────────┘   └─────────┬──────────┘   │ compliance             │
                                │                       │              └────────────────────────┘
                       ┌────────▼─────────┐   ┌─────────▼──────────┐   ┌────────────────────────┐
                       │     database     │   │   manual-testing   │   │          ai            │
                       │ Flyway · H2 /    │   │ ~46 docs · RTM ·   │   │ provider abstraction · │
                       │ PostgreSQL seeds │   │ test plans/suites  │   │ prompt library ·       │
                       └──────────────────┘   └────────────────────┘   │ guardrails             │
                                                                       └────────────────────────┘
   ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
   │  infrastructure  │   │     .github      │   │  docs / scripts  │
   │ Docker Compose   │   │ CI/CD · quality  │   │ blueprints · ADRs│
   │ Postgres·Redis…  │   │ gates · releases │   │ dev helper CLIs  │
   └──────────────────┘   └──────────────────┘   └──────────────────┘

  Maven reactor modules: apps/backend · automation · ai
  Errors: RFC 7807 ProblemDetail (OMII-4xx)   |   Rule BR-APPT-001: no double-booking → 422
```

## Quick Start

> **Prerequisites:** Java 21, Maven, Node 22, and (optionally) Docker. `scripts/setup.sh`
> validates the toolchain and degrades gracefully when optional tooling is missing.

```bash
# 1. Clone
git clone https://github.com/omiinayak25/omiiCARE_QA.git
cd omiiCARE_QA

# 2. One-time environment setup (validates toolchain, bootstraps .env, builds backend)
./scripts/setup.sh                       # Windows: scripts\setup.bat

# 3. Run the backend (dev profile, in-memory H2 — no external database needed)
mvn -pl apps/backend spring-boot:run     # serves http://localhost:8080

# 4. Run the frontend (in a second terminal)
cd apps/frontend
npm install
npm run dev                              # serves http://localhost:5173, proxies /api -> :8080
```

Then open **http://localhost:5173** and sign in with the synthetic demo account:

| Field | Value |
|-------|-------|
| Username | `demo.admin` |
| Password | `Admin@12345` |

Explore the API directly via Swagger UI at **http://localhost:8080/swagger-ui.html**, and confirm
liveness at **http://localhost:8080/actuator/health**.

### Core API surface (`/api/v1/`)

| Area | Endpoints |
|------|-----------|
| Auth | `POST auth/login` · `POST auth/refresh` · `GET auth/me` |
| Patients | `patients` — CRUD, search, pagination |
| Providers | `providers` — directory + lookup |
| Appointments | `appointments` (+ `POST appointments/{id}/cancel`) — enforces **BR-APPT-001** (no double-booking → `422`) |
| FHIR R4 | `GET fhir/Patient/{id}` |

Full contract: [docs/API_BLUEPRINT.md](docs/API_BLUEPRINT.md) · FHIR mapping: [docs/FHIR_GUIDE.md](docs/FHIR_GUIDE.md) · business rules: [docs/BUSINESS_RULES.md](docs/BUSINESS_RULES.md).

## Module Map

| Module | Role | Charter |
|--------|------|---------|
| `apps/backend` | Spring Boot domain, REST + FHIR APIs, JWT/RBAC, audit | [apps/backend/README.md](apps/backend/README.md) |
| `apps/frontend` | React + Vite primary SUT, role-based portals | [apps/frontend/README.md](apps/frontend/README.md) |
| `automation` | UI/API/DB test platform + resource adapters | [automation/README.md](automation/README.md) |
| `manual-testing` | Requirements, RTM, cases, suites, defects (~46 docs) | [manual-testing/README.md](manual-testing/README.md) |
| `quality` | Performance, security, a11y, visual, db, contract, chaos, resilience, observability, compliance | [quality/README.md](quality/README.md) |
| `ai` | Provider-abstracted AI assistants, prompt library, guardrails | [ai/README.md](ai/README.md) |
| `database` | Flyway migrations, schema, PHI-safe seeds | [database/README.md](database/README.md) |
| `infrastructure` | Docker Compose stack (Postgres, Redis, MinIO, Keycloak, WireMock, Prometheus, Grafana, SonarQube) | [infrastructure/README.md](infrastructure/README.md) |
| `.github` | Reusable GitHub Actions, quality gates, release automation | [.github/workflows/README.md](.github/workflows/README.md) |
| `scripts` | Cross-platform developer helper CLIs (setup, start, stop, reset, health) | [scripts/README.md](scripts/README.md) |

The complete directory tree with the purpose of every folder lives in [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md).

## Tech Stack

Authoritative versions are owned by [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md). Summary:

| Layer | Technologies |
|-------|--------------|
| Backend | Java 21 (LTS), Spring Boot 3, Spring Security + JWT, Spring Data JPA / Hibernate, Flyway (H2 / PostgreSQL), MapStruct, OpenAPI, Maven, JUnit 5 + Mockito |
| Frontend | React 18, TypeScript, Vite, MUI, React Router, TanStack Query, Axios, React Hook Form + Zod, i18next |
| Automation | Rest Assured, Playwright, Selenium, Cucumber/Gherkin, Allure / Extent — over a shared resource-adapter layer |
| Quality | JMeter / k6 / Gatling, OWASP ZAP, axe-core, Lighthouse, contract & chaos tooling |
| Infra & DevOps | Docker / Docker Compose, PostgreSQL, Redis, MinIO, Keycloak, WireMock, Prometheus, Grafana, SonarQube, GitHub Actions, OpenTelemetry |
| AI | Provider-abstracted (Claude / OpenAI / local) — configuration-driven, never vendor-coupled |

## Testing

| Goal | Command |
|------|---------|
| Full reactor unit/integration tests | `mvn test` |
| Backend only | `mvn -pl apps/backend test` |
| End-to-end automation suite | `mvn -pl automation test -Pe2e` |
| Frontend build + lint | `cd apps/frontend && npm run build && npm run lint` |

Reactor health at 1.0.0: **backend 9 + automation 5 + ai 9** tests passing; frontend build and lint green.
The advanced disciplines each ship as a charter under [`quality/`](quality/README.md) — for example
[performance](quality/performance/README.md), [security](quality/security/README.md),
[accessibility](quality/accessibility/README.md), [visual](quality/visual/README.md),
[contract](quality/contract-testing/README.md), [database](quality/database-testing/README.md),
[chaos](quality/chaos/README.md), [resilience](quality/resilience/README.md),
[observability](quality/observability/README.md), and [compliance](quality/compliance/README.md).

Strategy and coverage targets: [docs/TEST_STRATEGY.md](docs/TEST_STRATEGY.md) ·
[docs/MASTER_TEST_PLAN.md](docs/MASTER_TEST_PLAN.md) · [docs/TEST_PYRAMID.md](docs/TEST_PYRAMID.md) ·
[docs/RTM.md](docs/RTM.md) · [docs/QUALITY_GATES.md](docs/QUALITY_GATES.md).

## Milestone Status — All 10 Complete

Full deliverables, fences, and gates live in [ROADMAP.md](ROADMAP.md).

| # | Milestone | Status |
|---|-----------|--------|
| 1 | Foundation, Architecture & Governance | ✅ Complete |
| 2 | Infrastructure & Environment Foundation | ✅ Complete |
| 3 | Healthcare Platform Core (Backend) | ✅ Complete |
| 4 | Frontend Platform & Portals | ✅ Complete |
| 5 | Quality Engineering Platform (Automation) | ✅ Complete |
| 6 | Manual Quality Engineering Assets | ✅ Complete |
| 7 | Advanced Quality Engineering | ✅ Complete |
| 8 | DevOps, CI/CD & Release Engineering | ✅ Complete |
| 9 | AI-Native Quality Engineering | ✅ Complete |
| 10 | Production Hardening & Release 1.0.0 | ✅ Complete |

## Documentation Index

| Area | Document |
|------|----------|
| Specification | [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md) |
| Canonical facts | [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md) |
| Architecture | [ARCHITECTURE.md](ARCHITECTURE.md) |
| Repository structure | [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) |
| Roadmap | [ROADMAP.md](ROADMAP.md) |
| Change log | [CHANGELOG.md](CHANGELOG.md) |
| API blueprint | [docs/API_BLUEPRINT.md](docs/API_BLUEPRINT.md) |
| FHIR / HL7 guides | [docs/FHIR_GUIDE.md](docs/FHIR_GUIDE.md) · [docs/HL7_GUIDE.md](docs/HL7_GUIDE.md) |
| Business rules | [docs/BUSINESS_RULES.md](docs/BUSINESS_RULES.md) |
| Test strategy & plan | [docs/TEST_STRATEGY.md](docs/TEST_STRATEGY.md) · [docs/MASTER_TEST_PLAN.md](docs/MASTER_TEST_PLAN.md) · [docs/RTM.md](docs/RTM.md) |
| Quality gates | [docs/QUALITY_GATES.md](docs/QUALITY_GATES.md) |
| CI/CD & deployment | [docs/CI_CD_GUIDE.md](docs/CI_CD_GUIDE.md) · [docs/DEPLOYMENT_GUIDE.md](docs/DEPLOYMENT_GUIDE.md) |
| Environments | [docs/ENVIRONMENT_GUIDE.md](docs/ENVIRONMENT_GUIDE.md) |
| Portfolio & interview | [docs/PORTFOLIO_GUIDE.md](docs/PORTFOLIO_GUIDE.md) · [docs/INTERVIEW_GUIDE.md](docs/INTERVIEW_GUIDE.md) |
| Versioning policy | [VERSIONING.md](VERSIONING.md) |

## Branching

The repository uses a **simple two-branch strategy** — only `main` and `develop` are permanent:

| Branch | Purpose |
|--------|---------|
| `main` | Production-ready, stable code (releasable, tagged) |
| `develop` | Active development / integration |

`feature/*` branches are short-lived and **deleted immediately after merging**; no other long-lived branches are kept. Details: [docs/BRANCHING_STRATEGY.md](docs/BRANCHING_STRATEGY.md) · [CONTRIBUTING.md](CONTRIBUTING.md).

## Contributing, Security & License

- **Contributing:** [CONTRIBUTING.md](CONTRIBUTING.md) · [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) · code ownership in [CODEOWNERS](CODEOWNERS).
- **Security:** report vulnerabilities and review the healthcare compliance scope in [SECURITY.md](SECURITY.md).
- **License:** released under the [MIT License](LICENSE). Maintained by [@omiinayak25](https://github.com/omiinayak25).

## Compliance Disclaimer

omiiCARE_QA models **HIPAA-like** privacy practices and **FHIR/HL7** standards conformance for
**educational and portfolio purposes only**. It uses **synthetic, PHI-safe data exclusively** and
makes **no formal HIPAA, medical-device, or other regulatory certification claims**. See
[SECURITY.md](SECURITY.md) for the full healthcare compliance scope.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Principal Engineer | Initial (Milestone 10) |
