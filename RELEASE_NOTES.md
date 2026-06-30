# Release Notes — omiiCARE_QA v1.0.0 (2026-06-30)

> **Enterprise Healthcare Quality Engineering Platform — first stable release.**
> This is the **`1.0.0`** general-availability release, closing **Milestone 10
> (Production Hardening & Release)**. From this tag forward the full Semantic
> Versioning contract takes effect (see [VERSIONING.md](VERSIONING.md)).

| Field | Value |
|-------|-------|
| Version | `1.0.0` |
| Date | 2026-06-30 |
| Tag | `v1.0.0` |
| Repository | <https://github.com/omiinayak25/omiiCARE_QA.git> |
| Maintainer | [@omiinayak25](https://github.com/omiinayak25) |
| License | MIT (with healthcare-data notice — see [LICENSE](LICENSE)) |
| Data posture | Synthetic, PHI-safe data only — **no formal certification claims** |
| Milestones complete | 10 / 10 |

---

## 1. What This Release Is

omiiCARE_QA is a single enterprise monorepo that demonstrates the **full spectrum
of healthcare quality engineering** — a working healthcare web application and
backend (the System Under Test) wrapped in automation, manual QA assets, advanced
quality engineering, CI/CD, and an AI-native QE layer. It is built for portfolio,
education, and reference use; it models **HIPAA-like** privacy practices and
**FHIR R4 / HL7** conformance concepts without claiming formal compliance.

For orientation, see [README.md](README.md),
[docs/PORTFOLIO_GUIDE.md](docs/PORTFOLIO_GUIDE.md), and
[docs/INTERVIEW_GUIDE.md](docs/INTERVIEW_GUIDE.md). The full change history is in
[CHANGELOG.md](CHANGELOG.md); the architecture is in [ARCHITECTURE.md](ARCHITECTURE.md).

---

## 2. Highlights by Area

### 2.1 Backend (`apps/backend` — Java 21 / Spring Boot 3)

| Capability | Detail |
|------------|--------|
| Authentication & RBAC | JWT access/refresh with rotation; permission-based `@PreAuthorize`; `/api/v1/auth/{login,refresh,me}` |
| Healthcare domain | Patient (CRUD + paginated search), Provider (read-only directory), Appointment (booking + `/{id}/cancel`) |
| Business rules | `BR-APPT-001` no double-booking → **HTTP 422**; appointment validity rules `BR-APPT-002/004` |
| FHIR R4 facade | Read-only `GET /api/v1/fhir/Patient/{id}` returning `application/fhir+json` |
| Error contract | RFC 7807 `ProblemDetail` with `OMII-4xx` error codes and correlation id |
| Cross-cutting frameworks | Centralized audit, validation, and exception handling via `@RestControllerAdvice` |
| Persistence | Flyway migrations; H2 (`dev`/`test`) ↔ PostgreSQL (`local`/`docker`/`qa`/`stage`/`prod`), configuration-only switch |
| API docs | OpenAPI / Swagger UI with a Bearer-JWT security scheme |

### 2.2 Frontend (`apps/frontend` — React 18 + TypeScript + Vite + MUI)

| Capability | Detail |
|------------|--------|
| App shell | Vite + React 18 + strict TypeScript + Material UI v6; light/dark theme; top-level `ErrorBoundary`; i18next (no hardcoded strings) |
| Auth flow | `AuthContext` over an Axios client that attaches the bearer token and transparently refreshes once on a 401 |
| Routing & guards | `AppRouter` with `ProtectedRoute` enforcing authentication and per-route permissions |
| Role-based portals | Login, role-aware Dashboard, Patients (paginated + search + register), Appointments (booking surfacing the double-booking rule), 404/403 |
| Testability | Stable `data-testid` selectors throughout so automation needs no app changes |

### 2.3 Automation (`automation`)

| Capability | Detail |
|------------|--------|
| UI / API / BDD | Playwright + Selenium UI, Rest Assured API, Cucumber/Gherkin BDD on JUnit 5 |
| Resource-adapter layer | `ResourceAdapter`/`HttpResourceAdapter`/`AdapterFactory` targeting Local omiiCARE, OpenMRS, OpenEMR, HAPI FHIR, SMART Health IT, OpenFDA, DummyJSON, Restful Booker — switching targets is config-only |
| Configuration core | `FrameworkConfig` (system properties → env → `framework.properties`) and a `TargetSystem` registry |
| Test data | `PatientFactory` producing PHI-safe synthetic patients |
| Execution model | Framework unit tests run on every build; live-SUT suites are tagged and excluded by default (re-included via `-Pe2e`) |

### 2.4 Manual QA (`manual-testing`)

| Capability | Detail |
|------------|--------|
| Coverage | ~46 documents: requirements, [docs/RTM.md](docs/RTM.md), test pyramid, master/sprint/release plans |
| Test cases | ~62 executable cases across Authentication, Patient, Appointment, FHIR, Admin/Audit (positive, negative, boundary) |
| Suites | Smoke, Regression, Negative/Boundary, Exploratory (charter-based), plus UI/API checklists |
| Process assets | Defect lifecycle + RCA taxonomy, risk register, release/go-live/rollback checklists, UAT plan, QA metrics catalog, synthetic PHI-safe data catalog |

### 2.5 Advanced Quality Engineering (`quality`)

| Dimension | Detail |
|-----------|--------|
| Performance | k6 load/stress scripts, JMeter plan, Gatling simulation — **owned-infrastructure-only** rule throughout |
| Security | OWASP ZAP baseline + authenticated-scan guide, Dependency-Check, OWASP Top 10 → omiiCARE mapping, `SEC-TC-*` cases |
| Accessibility | axe-core Playwright specs, Lighthouse CI, WCAG 2.1 AA mapping |
| Visual | Playwright visual specs (light/dark) + baseline/approval workflow |
| Database | Referential-integrity and migration-validation SQL, `DB-TC-*` cases |
| Contract | JSON Schemas (Patient, ApiResponse envelope, FHIR R4 Patient) with `CT-*` cases |
| Chaos & resilience | `CHAOS-*` experiment catalog, `RES-*` retry/circuit-breaker/timeout patterns |
| Observability | Prometheus alert rules + Grafana QE dashboard |
| Compliance | HIPAA-like, FHIR/HL7, WCAG/OWASP checklists mapped to implemented controls |

### 2.6 CI/CD (`.github`)

| Capability | Detail |
|------------|--------|
| Reusable workflows | `_reusable-backend`, `_reusable-frontend`, `_reusable-quality`, `_reusable-docker` invoked via `workflow_call` |
| Entry pipelines | `ci.yml` (PR/push gate), `nightly.yml` (full build + e2e + Trivy), `release.yml` (`v*` tag → images → GitHub Release) |
| Security automation | CodeQL (Java + JS/TS), dependency review, Dependabot |
| Detail | See [docs/CI_CD_GUIDE.md](docs/CI_CD_GUIDE.md) and [docs/QUALITY_GATES.md](docs/QUALITY_GATES.md) |

### 2.7 AI-Native QE (`ai`)

| Capability | Detail |
|------------|--------|
| Provider abstraction | `AiProvider` + config-driven `AiProviderFactory` (`local`/`claude`/`openai`); **disabled by default**, network-free build |
| Guardrails | `PromptGuardrails` redacts secrets/credentials and refuses unsafe input — last line of defense for PHI-safe prompting |
| Assistant | `FailureAnalysisAssistant` (logs/stack trace → root cause, evidence, next steps, confidence); output always marked AI-assisted |
| Prompt library | 14 reusable, reviewable, versioned templates under `ai/prompts/` |

---

## 3. What's Included

- **Reactor build:** root Maven reactor with `apps/backend`, `automation`, and
  `ai` modules; React/Vite frontend built and linted separately.
- **Documentation set:** root governance docs plus the full `docs/` library
  (architecture, API, FHIR/HL7, testing, performance, security, CI/CD, and the
  release artifacts introduced here).
- **Infrastructure:** Docker Compose stack (PostgreSQL, Redis, Mailpit, MinIO,
  Keycloak, WireMock, Prometheus, Grafana, SonarQube) under `infrastructure/`.
- **Database module:** Flyway migrations, seed catalog, and operational scripts.
- **Synthetic data only:** every dataset and demo credential is PHI-safe.

---

## 4. How to Run

> Full prerequisites and first-run steps are in
> [docs/UPGRADE_GUIDE.md](docs/UPGRADE_GUIDE.md); the compatibility matrix is in
> [docs/COMPATIBILITY_MATRIX.md](docs/COMPATIBILITY_MATRIX.md).

**Prerequisites:** Java 21 (LTS), Node 22, Maven 3.9+, and Docker (for the full
containerized stack). The backend defaults to embedded H2 on the `dev` profile,
so the backend and frontend can run without Docker.

```bash
# Backend (dev profile → embedded H2, no Docker required)
mvn -pl apps/backend -am spring-boot:run \
  -Dspring-boot.run.profiles=dev
# → http://localhost:8080  | Swagger: /swagger-ui.html  | Health: /actuator/health

# Frontend (separate terminal)
cd apps/frontend && npm ci && npm run dev
# → http://localhost:5173  (Vite dev proxy → backend :8080)

# Full containerized stack (PostgreSQL + services)
docker compose -f infrastructure/docker/docker-compose.yml up -d
```

**Demo login:** `demo.admin` / `Admin@12345` (synthetic SUPER_ADMIN; non-prod
bootstrap only).

---

## 5. Verification Status

| Module | Command | Result |
|--------|---------|--------|
| Backend | `mvn -pl apps/backend test` | 9 tests green (JWT, auth→patient→FHIR flow, validation Problem Details, double-booking rule, boot/schema/seed) |
| Automation | `mvn -pl automation test` | 5 framework unit tests green; e2e/UI/BDD suites compile and are excluded by default |
| AI | `mvn -pl ai test` | 9 unit tests green (prompt rendering, provider selection, guardrails, assistant enabled/disabled/secret-refusal) |
| Frontend | `npm run build` / `npm run lint` | Build (tsc strict + Vite) and lint both pass cleanly |
| Reactor | full reactor build | Backend + automation + ai build and install |

All milestone gates are met and the Definition of Done
([docs/DEFINITION_OF_DONE.md](docs/DEFINITION_OF_DONE.md)) is satisfied for the
`1.0.0` scope.

---

## 6. Known Limitations (Summary)

This release is deliberately bounded to the v1.0 scope. The most relevant
limitations:

- The **full stack requires Docker**, which is not exercised in the build
  environment (configs validate but the stack is not started there).
- **End-to-end / UI / BDD suites require a running SUT** and are excluded from the
  default build.
- **Quality scans** (k6, JMeter, Gatling, ZAP, Lighthouse, axe, Trivy) require
  their respective tools installed.
- **FHIR support is a read-only Patient facade** — no write operations or
  additional resources.
- **Single-node** deployment only; microservices, Kubernetes, distributed DB,
  cloud, and native mobile are post-1.0.

The complete, severity-rated list with workarounds and plans is in
[docs/KNOWN_ISSUES.md](docs/KNOWN_ISSUES.md). The forward roadmap is in
[ROADMAP.md](ROADMAP.md).

---

## 7. Upgrade & Migration

This is the first stable release, so there is **no upgrade from a prior stable
version**. Pre-1.0 `0.x` tags were milestone checkpoints. See
[docs/UPGRADE_GUIDE.md](docs/UPGRADE_GUIDE.md) for installation and forward
compatibility, and [docs/MIGRATION_NOTES.md](docs/MIGRATION_NOTES.md) for the
baseline migration posture.

---

## 8. Thanks

Thanks to the maintainer [@omiinayak25](https://github.com/omiinayak25) and to
every contributor (human and AI-assisted) who delivered the ten-milestone path to
`1.0.0`. This release stands on the open-source ecosystem — Spring Boot, React,
Vite, MUI, Playwright, Selenium, Rest Assured, Cucumber, JMeter, k6, Gatling,
OWASP, axe-core, Lighthouse, Prometheus, Grafana, and the HL7/FHIR community —
with gratitude.

---

## References

- [CHANGELOG.md](CHANGELOG.md) · [VERSIONING.md](VERSIONING.md) · [ROADMAP.md](ROADMAP.md)
- [ARCHITECTURE.md](ARCHITECTURE.md) · [docs/CI_CD_GUIDE.md](docs/CI_CD_GUIDE.md)
- [docs/KNOWN_ISSUES.md](docs/KNOWN_ISSUES.md) · [docs/UPGRADE_GUIDE.md](docs/UPGRADE_GUIDE.md) · [docs/MIGRATION_NOTES.md](docs/MIGRATION_NOTES.md) · [docs/COMPATIBILITY_MATRIX.md](docs/COMPATIBILITY_MATRIX.md)
- [docs/PORTFOLIO_GUIDE.md](docs/PORTFOLIO_GUIDE.md) · [docs/INTERVIEW_GUIDE.md](docs/INTERVIEW_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Release Engineer | Initial (Milestone 10) |
