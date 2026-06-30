# omiiCARE_QA

[![Build](https://img.shields.io/badge/build-pending--M8-lightgrey?style=flat-square)](.github/workflows)
[![License: MIT](https://img.shields.io/badge/license-MIT%20%2B%20healthcare%20notice-blue?style=flat-square)](LICENSE)
[![Version](https://img.shields.io/badge/version-0.1.0-orange?style=flat-square)](CHANGELOG.md)
[![Docs](https://img.shields.io/badge/docs-Milestone%201-brightgreen?style=flat-square)](docs/)
[![SemVer](https://img.shields.io/badge/semver-2.0.0-informational?style=flat-square)](VERSIONING.md)
[![Conventional Commits](https://img.shields.io/badge/commits-conventional-yellow?style=flat-square)](CONTRIBUTING.md)

> **Enterprise Healthcare Quality Engineering Platform** — a single, modular monorepo that pairs a production-grade healthcare web application (the System Under Test) with a first-class quality-engineering platform that tests it across UI, API, database, performance, security, accessibility, and visual layers.

## Purpose

omiiCARE_QA exists to demonstrate, end to end, what world-class healthcare quality engineering looks like when it is designed — not improvised. It is built **documentation-first**: architecture and governance are written before any line of application code, and every milestone follows *Design → Document → Review → Implement → Test → Refactor → Document*. This README is the front door; it orients new readers and links to the authoritative sources of truth.

## Scope

- **In scope:** the project pitch, why it exists, feature highlights, the monorepo map, the technology summary, current milestone status, and a complete documentation index.
- **Out of scope:** detailed architecture and per-module design (see [ARCHITECTURE.md](ARCHITECTURE.md) and the `docs/` blueprints). Facts such as versions and the technology matrix are owned by [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md) and are only summarized here.

## Why This Repo Exists

| Driver | What it means here |
|--------|--------------------|
| Healthcare is high-stakes | Quality engineering for clinical systems must be rigorous, auditable, and standards-aware (FHIR, HL7, ICD-10, CPT, LOINC, SNOMED CT). |
| QA deserves first-class status | The test platform is a peer of the application, not an afterthought bolted on at the end. |
| Architecture beats accident | Clean Architecture + DDD on the backend, modular React on the frontend, and an adapter-centric automation core make the system testable *by design*. |
| Portfolio-grade transparency | Everything is open, documented, and PHI-safe, so the repository can teach as well as work. |

## Feature Highlights

| Capability | Description | Arrives |
|------------|-------------|---------|
| Healthcare platform core | Spring Boot domain, REST + FHIR APIs, JWT/RBAC auth, audit/validation/exception frameworks | M3 |
| Role-based portals | React + Vite SUT for 12 RBAC roles, WCAG AA, responsive/PWA | M4 |
| Adapter-centric automation | Playwright, Selenium, Rest Assured, Cucumber over a common resource-adapter layer | M5 |
| Manual QE assets | Requirements, RTM, test plans, suites, defect & release management | M6 |
| Advanced quality | Performance, security, accessibility, visual, contract, chaos, observability | M7 |
| DevOps & CI/CD | Reusable GitHub Actions, quality gates, Docker, semantic-version automation | M8 |
| AI-native QE | Provider-abstracted (Claude / OpenAI / local) prompt library and assistants | M9 |
| Profile-driven environments | H2 ↔ PostgreSQL switch by configuration only across 11 environments | M2 |

## Monorepo Map

The full directory tree, with the purpose of every folder, lives in
[PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md). At a glance:

| Module | Role | Milestone |
|--------|------|-----------|
| `apps/backend` | Spring Boot domain, REST + FHIR APIs, auth, audit | M3 |
| `apps/frontend` | React + Vite primary SUT, role-based portals | M4 |
| `database` | Flyway migrations, schema, PHI-safe seeds | M2 |
| `infrastructure` | Docker Compose stack (Postgres, Redis, MinIO, Keycloak, WireMock, Prometheus, Grafana, SonarQube) | M2 |
| `automation` | UI/API/DB test platform + resource adapters | M5 |
| `manual-testing` | Requirements, RTM, cases, suites, defects | M6 |
| `quality` | Performance, security, a11y, visual, contract, chaos | M7 |
| `ai` | Optional provider-abstracted AI assistants | M9 |
| `.github` | Reusable GitHub Actions, issue/PR templates, quality gates | M8 |
| `docs` | Governance, blueprints, ADRs | M1+ |

## Tech Stack Summary

Authoritative versions live in [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md) §3. Summary:

- **Backend:** Java 21 (LTS), Spring Boot 3.x, Spring Security + JWT, Spring Data JPA / Hibernate, Flyway, MapStruct, OpenAPI, Maven, JUnit 5 + Mockito.
- **Frontend:** React 18+, TypeScript, Vite, React Router, TanStack Query, Axios, Material UI, React Hook Form + Zod, i18next.
- **Automation & Quality:** Playwright, Selenium, Rest Assured, Cucumber/Gherkin, Allure / Extent, JMeter / k6 / Gatling, OWASP ZAP, axe-core, Lighthouse.
- **Infrastructure & DevOps:** Docker / Docker Compose, PostgreSQL, Redis, MinIO, Keycloak, WireMock, Prometheus, Grafana, SonarQube, GitHub Actions, OpenTelemetry.
- **AI Platform:** provider-abstracted (Claude, OpenAI, local LLMs) — configuration-driven, never vendor-coupled.

## Quick Start

> **NOTE — Milestone 1 (Foundation, Architecture & Governance).** This repository is
> currently **documentation only**. There is intentionally **no application, API, or
> automation code** yet. Runnable infrastructure arrives in **Milestone 2**, the
> backend in **Milestone 3**, and the frontend in **Milestone 4**. See the milestone
> status table below and [ROADMAP.md](ROADMAP.md).

For now, the "quick start" is to read the governance set:

1. Read [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md) — what omiiCARE_QA is.
2. Read [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md) — the canonical fact sheet.
3. Read [ARCHITECTURE.md](ARCHITECTURE.md) — the target architecture.
4. Read [CONTRIBUTING.md](CONTRIBUTING.md) — how to work in this repo.

## Milestone Status

Full detail, deliverables, fences, and gates live in [ROADMAP.md](ROADMAP.md).

| # | Milestone | Status |
|---|-----------|--------|
| 1 | Foundation, Architecture & Governance | 🟦 In progress |
| 2 | Infrastructure & Environment Foundation | ⬜ Not started |
| 3 | Healthcare Platform Core (Backend) | ⬜ Not started |
| 4 | Frontend Platform & Portals | ⬜ Not started |
| 5 | Quality Engineering Platform (Automation) | ⬜ Not started |
| 6 | Manual Quality Engineering Assets | ⬜ Not started |
| 7 | Advanced Quality Engineering | ⬜ Not started |
| 8 | DevOps, CI/CD & Release Engineering | ⬜ Not started |
| 9 | AI-Native Quality Engineering | ⬜ Not started |
| 10 | Production Hardening & Release 1.0.0 | ⬜ Not started |

## Documentation Index

| Area | Document |
|------|----------|
| Specification | [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md) |
| Canonical facts | [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md) |
| Architecture | [ARCHITECTURE.md](ARCHITECTURE.md) |
| Repository structure | [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) |
| Roadmap | [ROADMAP.md](ROADMAP.md) |
| Contributing | [CONTRIBUTING.md](CONTRIBUTING.md) |
| Code of Conduct | [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) |
| Security policy | [SECURITY.md](SECURITY.md) |
| Versioning policy | [VERSIONING.md](VERSIONING.md) |
| Change log | [CHANGELOG.md](CHANGELOG.md) |
| Code ownership | [CODEOWNERS](CODEOWNERS) |
| License | [LICENSE](LICENSE) |

## Examples

- *New contributor:* start at this README, follow the Quick Start list, then open [CONTRIBUTING.md](CONTRIBUTING.md) before raising a pull request.
- *Reviewer checking scope:* confirm a proposed change belongs to the active milestone using the status table here and the fences in [ROADMAP.md](ROADMAP.md).

## Future Enhancements

- Replace placeholder badges with live build, coverage, and SonarQube quality-gate badges once CI lands (M8).
- Add an animated architecture overview and a hosted documentation site.
- Generate the milestone status table automatically from [ROADMAP.md](ROADMAP.md) in CI.

## Dependencies

- Summarizes [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md), [ARCHITECTURE.md](ARCHITECTURE.md), and [ROADMAP.md](ROADMAP.md).
- Links the full governance set under the repository root and `docs/`.

## References

- [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md)
- [SECURITY.md](SECURITY.md) · [VERSIONING.md](VERSIONING.md) · [CONTRIBUTING.md](CONTRIBUTING.md)

## Compliance Disclaimer

omiiCARE_QA models **HIPAA-like** privacy practices and **FHIR/HL7** standards
conformance for **educational and portfolio purposes only**. It uses **synthetic,
PHI-safe data exclusively** and makes **no formal HIPAA, medical-device, or other
regulatory certification claims**. See [SECURITY.md](SECURITY.md) for the full
healthcare compliance scope.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Principal Technical Writer | Initial project front door (Milestone 1) |
