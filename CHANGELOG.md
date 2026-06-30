# Changelog

All notable changes to **omiiCARE_QA** are documented in this file.

The format is based on [Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
See [VERSIONING.md](VERSIONING.md) for the versioning policy.

## [Unreleased]

### Added

- Changes accumulating toward the next release.

## [1.0.0] - 2026-06-30

**First stable release.** Milestone 10 — Production Hardening, Portfolio
Excellence & Release 1.0.0. A complete enterprise Healthcare Quality Engineering
Platform: healthcare web app + REST/FHIR APIs, a full quality-engineering
ecosystem (automation, manual QA, performance/security/accessibility/visual/
contract/chaos/observability/compliance), CI/CD, and an optional AI platform —
all in one modular monorepo. Synthetic PHI-safe data only; no formal certification
is claimed.

### Added (Milestone 10)

- **World-class README** with quick start, architecture diagram, module map, and
  documentation index; release `RELEASE_NOTES.md`.
- **Release docs:** `docs/KNOWN_ISSUES.md`, `docs/UPGRADE_GUIDE.md`,
  `docs/MIGRATION_NOTES.md`, and `docs/COMPATIBILITY_MATRIX.md`.
- **Portfolio & developer-experience guides:** `docs/REPOSITORY_TOUR.md`,
  `docs/DEMO_GUIDE.md`, `docs/FEATURE_MATRIX.md`, `docs/TECHNOLOGY_MATRIX.md`,
  `docs/LEARNING_ROADMAP.md`, and `docs/FAQ.md`.
- **Runnable API examples & sample data:** a valid Postman v2.1 collection
  (`postman/`), example request/response/Problem-Details/FHIR payloads
  (`docs/examples/`), and `docs/SAMPLE_DATA.md`.
- **Open-source readiness:** `.github/FUNDING.yml`, `CONTRIBUTORS.md`,
  `docs/GITHUB_PROJECT_MANAGEMENT.md`, and `docs/OPEN_SOURCE_READINESS.md`.

### Changed / Fixed (Milestone 10)

- Audited the entire repository: full clean reactor build green (backend 9 +
  automation 5 + AI 9 tests) and frontend build + lint green; 0 broken internal
  links across all Markdown; no placeholders; all JSON valid.
- Made `JwtServiceTest` compiler-agnostic (typed authorities list) so it builds
  identically under javac and the IDE's Eclipse compiler.
- Reconciled documentation: Node pinned to 22 everywhere; clarified that the
  non-production `DataInitializer` grants `demo.admin` the SUPER_ADMIN role.
- Bumped all module versions to **1.0.0**.

### Release scope (Milestones 1–9, summarized)

- **Foundation & governance** (M1): architecture, ADRs, ~50 governance documents.
- **Infrastructure** (M2): Maven reactor, profile-driven H2/PostgreSQL, Flyway,
  Docker Compose stack, cross-platform scripts, code-quality tooling, OpenTelemetry.
- **Backend** (M3): JWT auth + RBAC, Patient/Provider/Appointment, FHIR Patient
  facade, audit/validation/exception frameworks, OpenAPI.
- **Frontend** (M4): React/TS/Vite SUT with role-based portals and stable selectors.
- **Automation** (M5): Rest Assured/Playwright/Selenium/Cucumber + resource-adapter layer.
- **Manual QA** (M6): requirements, RTM, plans, ~62 cases, defect/risk/release assets.
- **Advanced QE** (M7): performance, security, accessibility, visual, database,
  contract, chaos, resilience, observability, and compliance modules.
- **DevOps** (M8): reusable GitHub Actions, container images, quality gates, releases.
- **AI** (M9): provider abstraction, prompt library, guardrails — optional and reviewable.

## [0.9.0] - 2026-06-30

Milestone 9 — AI-Native Quality Engineering Platform. AI assists engineers and
never replaces judgement; every capability is optional, transparent, explainable,
and reviewable, and is disabled by default. No autonomous code modification or
self-merging.

### Added

- **`ai` Maven module** (added to the reactor): a network-free, vendor-neutral AI
  platform — the build and tests run offline with no provider SDK bundled.
- **Provider abstraction:** `AiProvider` with a config-driven `AiProviderFactory`
  selecting `local` (offline `LocalEchoProvider`), `claude`, or `openai`; hosted
  providers require a runtime API key and fail fast (no key → no network call).
- **Configuration:** `AiConfig` (enabled/provider/model/apiKey from system
  properties or env), disabled by default; defaults to a current Claude model.
- **Prompt engine:** `PromptTemplate` (`{{variable}}` rendering, fail-fast on
  missing variables) and `PromptLibrary` (classpath-loaded versioned templates).
- **Security guardrails:** `PromptGuardrails` detects and redacts secrets/
  credentials (API keys, JWTs, private keys, bearer tokens) and refuses to send
  unsafe input to a provider — last line of defense for PHI-safe prompting.
- **Assistant:** `FailureAnalysisAssistant` (logs/stack trace → probable root
  cause, evidence, next steps, confidence) that respects the enabled flag and
  screens input through the guardrails; output is always marked AI-assisted.
- **Prompt library** (`ai/prompts/`): 14 reusable, reviewable templates
  (test-case/BDD/API-test generation, bug drafting, RCA, failure analysis, SQL/
  FHIR payloads, requirement/coverage/regression/risk analysis, code review,
  documentation) + an index.
- **Docs:** AI capabilities, configuration, security guardrails, knowledge base,
  and prompt-evaluation rubric under `ai/documentation/`, `ai/knowledge/`,
  `ai/evaluation/`.

### Verified

- `mvn -pl ai test` — 9 unit tests green (prompt rendering, provider selection,
  guardrails, assistant enabled/disabled/secret-refusal). Full reactor (backend +
  automation + ai) builds. Reactor version 0.9.0.

## [0.8.0] - 2026-06-30

Milestone 8 — Enterprise DevOps, CI/CD & Release Engineering. A reusable,
production-grade GitHub Actions ecosystem, container images, quality gates, and
release automation. No AI enhancements yet (Milestone 9).

### Added

- **Container images:** multi-stage `apps/backend/Dockerfile` (Maven build → slim
  JRE, non-root, healthcheck) and `apps/frontend/Dockerfile` (Vite build → nginx
  with SPA fallback, security headers, API proxy), plus `.dockerignore` files.
- **Reusable workflows:** `_reusable-backend` (Maven verify + JaCoCo/surefire
  artifacts), `_reusable-frontend` (npm ci/lint/build + dist artifact),
  `_reusable-quality` (Checkstyle/PMD/SpotBugs/Spotless, advisory), and
  `_reusable-docker` (Buildx build, push gated on input).
- **Entry pipelines:** `ci.yml` (PR/push gate on main & develop), `nightly.yml`
  (scheduled full build + e2e + Trivy scan), and `release.yml` (`v*` tags →
  build/test → GHCR images → GitHub Release from the changelog).
- **Security automation:** `codeql.yml` (Java + JS/TS), `dependency-review.yml`,
  and `dependabot.yml` (Maven, npm, GitHub Actions, weekly).
- **Project automation:** path-based PR auto-labeling (`labeler.yml`).
- **Governance docs:** `docs/CI_CD_GUIDE.md`, `docs/BRANCHING_STRATEGY.md`, and
  `docs/QUALITY_GATES.md`; the workflows README documents the pipeline matrix.
- **README badges:** build, license, version, Java 21, React 18, docs, quality gates.

### Changed

- Replaced the Milestone 2 reusable-workflow skeletons with real pipelines and
  removed the now-superseded skeleton files (no dead workflows). Reactor 0.8.0.

## [0.7.0] - 2026-06-30

Milestone 7 — Advanced Quality Engineering Platform. Reusable frameworks and
representative suites across ten quality dimensions, integrated with the
automation platform and monitoring stack. Frameworks + representative suites, not
thousands of executions; no formal certification claims.

### Added

- **Performance** (`quality/performance/`): runnable k6 load and stress scripts,
  a JMeter test plan, a Gatling Java simulation, and execution/reporting guides —
  with the **owned-infrastructure-only** rule stated throughout.
- **Security** (`quality/security/`): OWASP ZAP baseline config + authenticated-
  scan guide + runner script, Dependency-Check setup + suppressions, an OWASP
  Top 10 → omiiCARE mapping, and `SEC-TC-*` cases (JWT tampering, IDOR/cross-tenant,
  SQLi, headers, CORS, rate limiting).
- **Accessibility** (`quality/accessibility/`): axe-core Playwright specs,
  Lighthouse CI config, and WCAG 2.1 AA success-criteria mapping.
- **Visual** (`quality/visual/`): Playwright visual specs (light/dark), baseline
  and approval workflow.
- **Database** (`quality/database-testing/`): referential-integrity and migration-
  validation SQL plus `DB-TC-*` cases against the real schema.
- **Contract** (`quality/contract-testing/`): JSON Schemas for the Patient
  response, the ApiResponse envelope, and the FHIR R4 Patient, with `CT-*` cases.
- **Chaos & resilience** (`quality/chaos/`, `quality/resilience/`): `CHAOS-*`
  experiment catalog and `RES-*` resilience patterns (retry/circuit-breaker/timeout).
- **Observability** (`quality/observability/`): Prometheus alert rules and a
  Grafana QE dashboard wired into the Milestone 2 stack.
- **Compliance** (`quality/compliance/`): HIPAA-like, FHIR/HL7, and WCAG/OWASP
  baseline checklists mapped to implemented controls (RBAC, audit, tenant isolation).

### Verified

- All JSON/YAML/k6-JS/bash artifacts pass syntax checks; no placeholders. Reactor
  version aligned to 0.7.0.

## [0.6.0] - 2026-06-30

Milestone 6 — Enterprise Manual Quality Engineering Assets. A complete manual
testing repository (~46 documents) traceable to requirements and the implemented
features. No performance/security/accessibility/visual automation (Milestone 7).

### Added

- **Requirements & traceability:** enumerated `REQUIREMENTS.md` (BR/FR/NFR/SEC/
  A11Y/PERF/FHIR/HL7 with IDs and acceptance criteria) and a populated `RTM.md`
  mapping requirements to manual TC IDs, automated suites, and specialized tests.
- **Test planning:** executable manual test strategy plus Master, Sprint, and
  Release test plans, and a Test-Case-Point + PERT estimation model.
- **Test cases & suites:** ~62 executable cases across Authentication, Patient,
  Appointment, FHIR, and Admin/Audit (positive, negative, boundary), grouped into
  Smoke, Regression, Negative/Boundary, and Exploratory (charter-based) suites,
  plus UI and API functional checklists.
- **Defect management:** report template, severity/priority matrix, defect
  lifecycle, RCA taxonomy, realistic sample defects, and a triage guide.
- **Risk & release:** risk register + matrix; release, go-live, rollback,
  deployment-validation, and production-verification checklists; UAT plan; QA
  sign-off template.
- **Metrics, data & knowledge:** QA metrics catalog + execution report/log
  templates; synthetic PHI-safe test-data catalog + boundary datasets; healthcare
  QA guide, glossary, common-failure patterns, best practices, lessons learned;
  and a QA onboarding guide.

### Verified

- All manual-testing documents carry Version History rows and contain no
  placeholders; test-case IDs are consistent for RTM linkage. Reactor version
  aligned to 0.6.0.

## [0.5.0] - 2026-06-30

Milestone 5 — Enterprise Quality Engineering Platform (Automation). A modular,
configuration-driven test framework with a pluggable adapter layer. Framework
unit tests run on every build; SUT-dependent suites are tagged and excluded by
default. No performance/security/accessibility/visual testing yet (Milestone 7).

### Added

- **Maven `automation` module** added to the reactor, with Rest Assured,
  Playwright, Selenium, Cucumber, Datafaker, and Awaitility dependencies and a
  surefire setup that excludes live-SUT suites by tag/class (re-included via `-Pe2e`).
- **Configuration core:** `FrameworkConfig` (layered: system properties → env →
  `framework.properties`) and `TargetSystem` registry of supported targets.
- **Resource-adapter layer:** `ResourceAdapter`/`HttpResourceAdapter`/`AdapterFactory`
  so tests target systems (Local omiiCARE, OpenMRS, OpenEMR, HAPI FHIR,
  SMART Health IT, OpenFDA, DummyJSON, Restful Booker) through interfaces — adding
  a target needs only a new adapter; switching environments is config-only.
- **Test-data generators:** `PatientFactory` producing PHI-safe synthetic patients.
- **Example suites:** Rest Assured API suite (auth, CRUD, JSON-schema validation),
  Playwright UI suite (login via stable `data-testid` selectors), and a Cucumber
  BDD feature + reusable step definitions + JUnit Platform runner.
- **Framework unit tests:** config resolution/override, adapter resolution and URL
  composition, and synthetic-data validity.

### Verified

- `mvn -pl automation test` — 5 framework unit tests green; e2e/UI/BDD suites
  compile and are excluded by default. Full reactor (`backend` + `automation`)
  builds and installs.

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

[Unreleased]: https://github.com/omiinayak25/omiiCARE_QA/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.9.0...v1.0.0
[0.9.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.8.0...v0.9.0
[0.8.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.7.0...v0.8.0
[0.7.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.6.0...v0.7.0
[0.6.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.5.0...v0.6.0
[0.5.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/omiinayak25/omiiCARE_QA/releases/tag/v0.1.0
