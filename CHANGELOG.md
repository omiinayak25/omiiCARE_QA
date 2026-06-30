# Changelog

All notable changes to **omiiCARE_QA** are documented in this file.

The format is based on [Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
See [VERSIONING.md](VERSIONING.md) for the versioning policy.

## [Unreleased]

### Added

- Changes accumulating toward the next release.

## [0.2.0] - 2026-06-30

Milestone 2 — Enterprise Infrastructure & Environment Foundation. The bootable
technical foundation every future module reuses. No healthcare modules, business
logic, authentication, or automation by design.

### Added

- **Maven reactor:** root `pom.xml` (Spring Boot 3.3 parent, Java 21, centralized
  dependency & plugin management, `quality` profile) and the `apps/backend` module.
- **Bootable backend shell:** `OmiiCareQaApplication` with Actuator health/metrics,
  Prometheus endpoint, and Micrometer→OpenTelemetry tracing — no domain logic yet.
- **Profile-driven configuration:** `application.yml` plus `dev` (H2), `local`,
  `docker`, `test`, `qa`, `stage`, and `prod` (PostgreSQL) profiles. Database
  selection is configuration-only — no code change to switch.
- **Observability foundation:** `CorrelationIdFilter` propagating correlation/request
  IDs into the MDC and response headers; `logback-spring.xml` structured logging with
  correlation/trace/span IDs and rotation.
- **Flyway migrations:** `V1` baseline platform schema (tenancy, identity, RBAC, audit)
  and a portable, idempotent repeatable seed (`R__`) loading 12 roles, permissions, and
  a synthetic PHI-safe DEMO tenant/hospital/departments/admin.
- **Docker Compose stack:** PostgreSQL, Redis, Mailpit, MinIO, Keycloak (realm import),
  WireMock, Prometheus, Grafana (provisioned datasource + dashboard), and SonarQube —
  with health checks, named volumes, and a shared network.
- **Developer scripts:** cross-platform `setup`, `start`, `stop`, `reset`, and
  `health-check` (`.sh` + `.bat`) with a shared bash helper library.
- **Code-quality tooling:** Checkstyle, PMD, SpotBugs, Spotless, and JaCoCo wired via
  the `quality` profile (report-only in M2; enforced in M8), plus `.pre-commit-config.yaml`.
- **Reusable CI structure:** `_reusable-*` GitHub Actions (build, test, lint, security,
  docs) composed by an entry `ci.yml` — structure only; full pipelines arrive in M8.
- **Database module:** migration naming standards, rollback strategy, database changelog,
  seed-data catalog, and operational helper scripts under `database/`.
- **Environment templates:** root `.env.example` and `infrastructure/docker/.env.example`.

### Verified

- `mvn -Pquality verify` builds cleanly; backend boots on the `test`/`dev` profiles;
  Flyway applies the baseline schema and seed; smoke tests (context load, schema, seed)
  pass. Docker stack configs validate but are not run here (Docker not installed).

## [0.1.0] - 2026-06-30

Milestone 1 — Foundation, Architecture & Governance. Documentation-only baseline;
no application, API, or automation code by design.

### Added

- **Canonical specification & facts:** `MASTER_PROJECT_SPECIFICATION.md` as the
  in-repo source of truth and `docs/PROJECT_METADATA.md` as the canonical fact sheet
  (identity, technology matrix, environments, roles, healthcare standards, versioning anchor).
- **Enterprise architecture:** `ARCHITECTURE.md` describing the target system —
  monorepo composition, Clean Architecture + DDD backend, modular React frontend,
  adapter-centric automation, cross-cutting seams, and data/quality architecture.
- **Roadmap:** `ROADMAP.md` defining the 10-milestone path to `1.0.0` with explicit
  deliverables, fences, gates, and post-1.0 future versions.
- **Governance documents:** `README.md`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`
  (Contributor Covenant v2.1), `SECURITY.md` (policy + healthcare compliance scope),
  `VERSIONING.md` (SemVer policy + milestone mapping), and this `CHANGELOG.md`.
- **ADR structure:** Architecture Decision Record framework under
  `docs/architecture/adr/` with an index and recording conventions.
- **GitHub configuration:** issue templates, pull-request template, and reusable
  GitHub Actions workflow structure under `.github/`.
- **Code ownership:** `CODEOWNERS` assigning default and path-specific ownership to
  the single maintainer with notes on future team handles.
- **Repository standards:** `.editorconfig`, `.gitattributes`, and `.gitignore`
  establishing consistent formatting, line-ending, and ignore rules.
- **Monorepo skeleton:** the documented directory layout (`apps/`, `database/`,
  `infrastructure/`, `automation/`, `manual-testing/`, `quality/`, `ai/`, `docs/`,
  `.github/`) per `PROJECT_STRUCTURE.md`.
- **Development & AI workflows:** governance for how contributors and AI agents work,
  plus the cross-document consistency baseline.
- **License:** MIT license with a healthcare-data notice.

[Unreleased]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/omiinayak25/omiiCARE_QA/releases/tag/v0.1.0
