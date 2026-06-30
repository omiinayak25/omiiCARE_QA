# Changelog

All notable changes to **omiiCARE_QA** are documented in this file.

The format is based on [Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
See [VERSIONING.md](VERSIONING.md) for the versioning policy.

## [Unreleased]

### Added

- Changes accumulating toward the next release.

## [0.4.0] - 2026-06-30

Milestone 4 — Enterprise Frontend Platform & Healthcare Portals. A
production-quality React/TypeScript/Vite web application — the primary System
Under Test — wired to the backend. No test tooling (that is Milestone 5).

### Added

- **App shell:** Vite + React 18 + TypeScript (strict) + Material UI v6 with a
  light/dark enterprise theme persisted via a ColorMode context, a top-level
  ErrorBoundary, and i18n (i18next) with no hardcoded strings.
- **Authentication:** `AuthContext` (login/logout, current user, permission
  checks) over an Axios client that attaches the bearer token and transparently
  refreshes it once on a 401.
- **Routing & guards:** `AppRouter` with `ProtectedRoute` enforcing authentication
  and per-route permissions (redirecting to `/login` or `/unauthorized`).
- **Portals/pages:** Login, Dashboard (role-aware), Patients (paginated list +
  search + register dialog), Appointments (list + booking dialog surfacing the
  backend's double-booking rule), plus 404/403 screens.
- **Testability:** stable `data-testid` selectors throughout, permission-aware
  navigation, and consistent DOM so the Milestone 5 framework needs no app changes.
- **Tooling:** ESLint flat config (type-aware), `npm run build`/`lint`/`typecheck`,
  Vite dev proxy to the backend, and `.env.example`.

### Verified

- `npm run build` (tsc strict + vite) and `npm run lint` both pass cleanly.

## [0.3.0] - 2026-06-30

Milestone 3 — Enterprise Healthcare Platform Core (Backend). A working,
tested backend vertical exercising the full Clean Architecture, authentication,
and the audit/validation/exception frameworks. No frontend or automation yet.

### Added

- **Shared kernel:** `ApiResponse`/`PageResponse` envelopes, `BaseEntity` (audit
  timestamps), `TenantContext`, canonical `ErrorCode`s, and a typed exception
  hierarchy (`ApiException`, `ResourceNotFoundException`, `BusinessRuleException`,
  `ConflictException`).
- **Centralized exception framework:** `@RestControllerAdvice` translating every
  error into an RFC 7807 `ProblemDetail` with error code, correlation id, and
  per-field validation details — controllers never handle exceptions.
- **Authentication & RBAC:** JWT access/refresh tokens with rotation
  (`JwtService`), `SecurityConfig` (stateless, method security), `AppUserPrincipal`
  exposing roles + permissions, and `/api/v1/auth/{login,refresh,me}` endpoints.
  Permission-based authorization (`@PreAuthorize`) with no hardcoded role checks.
- **Audit framework:** `AuditService` recording actions with actor, tenant, and
  correlation/request ids in its own transaction.
- **Healthcare modules:** Patient (CRUD + paginated search), Provider (read-only
  directory), and Appointment (booking with the no-double-booking business rule
  BR-APPT-001 and validity rules BR-APPT-002/004) — each entity→repository→
  service→DTO→MapStruct mapper→controller, all tenant-scoped.
- **FHIR R4 read facade:** `GET /api/v1/fhir/Patient/{id}` mapping the internal
  model to a FHIR Patient resource (`application/fhir+json`).
- **Persistence:** Flyway `V2` clinical schema (patient/provider/appointment with
  FKs, unique keys, indexes) and synthetic PHI-safe clinical seed data.
- **API documentation:** OpenAPI/Swagger UI with a Bearer-JWT security scheme.
- **Non-prod bootstrap:** `DataInitializer` granting SUPER_ADMIN its permissions
  and provisioning the synthetic `demo.admin` login.

### Verified

- `mvn -pl apps/backend test` — 9 tests green: JWT unit tests, the auth→patient→
  FHIR integration flow, validation Problem Details, and the appointment
  double-booking rule, plus the M2 boot/schema/seed smoke tests.

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

[Unreleased]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.4.0...HEAD
[0.4.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/omiinayak25/omiiCARE_QA/releases/tag/v0.1.0
