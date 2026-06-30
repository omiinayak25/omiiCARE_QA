# Repository Tour — omiiCARE_QA

> **Purpose.** A guided, navigation-first tour of the omiiCARE_QA monorepo. Where
> [PORTFOLIO_GUIDE.md](PORTFOLIO_GUIDE.md) sells the story and
> [INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md) rehearses the conversation, this
> document answers one question fast: *"If I want to see **X**, where do I look?"*
> It is the map you keep open while exploring.

## Scope

- **In scope:** the top-level layout, a suggested reading order, the location of
  every major capability, and a "see X → look at Y" lookup table.
- **Out of scope:** canonical facts and versions (owned by
  [PROJECT_METADATA.md](PROJECT_METADATA.md)), target architecture rationale
  ([ARCHITECTURE.md](../ARCHITECTURE.md)), and the live walkthrough
  ([DEMO_GUIDE.md](DEMO_GUIDE.md)). This is the **1.0.0** release: the backend,
  frontend, automation, and AI modules are built and their reactors are green.

## Responsibilities

| Audience | How to use this tour |
|----------|----------------------|
| New contributor | Read the order in §2, then jump via the lookup in §4 |
| Reviewer / hiring manager | Use §4 to land directly on evidence |
| Returning maintainer | Use §3 as the "where does this live" index |

---

## 1. Top-Level Map

| Path | What lives here | Module owner doc |
|------|-----------------|------------------|
| `apps/backend/` | Java 21 / Spring Boot 3 service: REST + FHIR APIs, JWT/RBAC, audit, validation, exception framework | [ARCHITECTURE.md](../ARCHITECTURE.md) |
| `apps/frontend/` | React 18 + TS + Vite + MUI — the System Under Test (role-based portals) | [UI_UX_SPECIFICATION.md](UI_UX_SPECIFICATION.md) |
| `automation/` | Rest Assured / Playwright / Selenium / Cucumber over a shared resource-adapter layer | `automation/README.md` |
| `manual-testing/` | ~46 docs: requirements, RTM, cases, suites, defects, release/UAT | `manual-testing/README.md` |
| `quality/` | Performance, security, a11y, visual, contract, chaos, resilience, observability, compliance, DB testing | `quality/README.md` |
| `ai/` | Provider-abstracted AI assistants, prompt library, guardrails (opt-in, **off by default**) | `ai/README.md` |
| `infrastructure/` | Docker Compose stack (Postgres, Redis, MailHog, MinIO, Keycloak, WireMock, Prometheus, Grafana, SonarQube) | `infrastructure/README.md` |
| `database/` | Flyway migrations, schema docs, PHI-safe seeds, restore/backup scripts | `database/README.md` |
| `scripts/` | `start` / `stop` / `reset` / `setup` / `health-check` (`.sh` + `.bat`) | `scripts/README.md` |
| `.github/` | Reusable GitHub Actions workflows, issue/PR templates, quality gates | [CI_CD_GUIDE.md](CI_CD_GUIDE.md) |
| `docs/` | Governance, blueprints, ADRs, and these portfolio/DevEx guides | this file |
| `postman/` | Postman collection for the REST API | `postman/` |
| `config/` | Shared cross-module configuration | `config/` |

Root-level governance: [README.md](../README.md),
[MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md),
[ARCHITECTURE.md](../ARCHITECTURE.md), [ROADMAP.md](../ROADMAP.md),
[CHANGELOG.md](../CHANGELOG.md), [PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md).

## 2. Suggested Reading Order

| Step | Read | Why |
|------|------|-----|
| 1 | [README.md](../README.md) | The front door — pitch, map, status |
| 2 | [PROJECT_METADATA.md](PROJECT_METADATA.md) | Canonical facts, versions, environments |
| 3 | [ARCHITECTURE.md](../ARCHITECTURE.md) | How the pieces fit; testability seams |
| 4 | [architecture/adr/README.md](architecture/adr/README.md) | The decisions, with alternatives |
| 5 | [FEATURE_MATRIX.md](FEATURE_MATRIX.md) | What is built, where, and how it is tested |
| 6 | [TECHNOLOGY_MATRIX.md](TECHNOLOGY_MATRIX.md) | Each technology and why it was chosen |
| 7 | [DEMO_GUIDE.md](DEMO_GUIDE.md) | Run it live end to end |
| 8 | [TEST_STRATEGY.md](TEST_STRATEGY.md) + [TEST_PYRAMID.md](TEST_PYRAMID.md) | The quality philosophy |
| 9 | [PORTFOLIO_GUIDE.md](PORTFOLIO_GUIDE.md) + [INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md) | How to present it |

## 3. Where Each Capability Lives

### Backend (`apps/backend/src/main/java/com/omiicare/qa/`)

| Capability | Package |
|------------|---------|
| Authentication, login/refresh/me | `security/auth/`, `security/jwt/` |
| RBAC authorities (`patient:read`, `appointment:write`, …) | `security/`, `config/security/` |
| Patient domain + CRUD/search API | `patient/` (`PatientController.java`) |
| Provider domain + API | `provider/` (`ProviderController.java`) |
| Appointment domain + booking/cancel | `appointment/` (`AppointmentController.java`, `AppointmentService.java`) |
| FHIR R4 Patient read facade | `fhir/` (`FhirPatientController.java`, `FhirPatientMapper.java`) |
| Audit logging | `audit/` (`AuditService.java`, `AuditLogEntity.java`) |
| Multi-tenant context | `shared/tenant/TenantContext.java` |
| Error model (RFC 7807, `OMII-*` codes) | `shared/error/` (`ErrorCode.java`) |
| Shared API envelope, paging | `shared/api/` |
| Observability config | `config/observability/` |
| Flyway migrations / seeds | `apps/backend/src/main/resources/db/` |

### Frontend (`apps/frontend/src/`)

| Capability | Folder |
|------------|--------|
| Auth/session handling | `auth/` |
| Role-based routes | `routes/` |
| API client (Axios) | `api/` |
| Screens/portals | `pages/` |
| Shared components | `components/` |
| MUI theme | `theme/` |
| i18n | `i18n/` |
| TypeScript contracts | `types/` |

### Quality (`quality/`)

| Discipline | Folder | Lead doc |
|------------|--------|----------|
| Performance (JMeter/k6/Gatling) | `quality/performance/` | `EXECUTION_GUIDE.md` |
| Security (OWASP ZAP, Dependency-Check) | `quality/security/` | `OWASP_TOP10_MAPPING.md` |
| Accessibility (axe-core, Lighthouse) | `quality/accessibility/` | `EXECUTION_GUIDE.md` |
| Visual regression (Playwright) | `quality/visual/` | `README.md` |
| Contract / schema | `quality/contract-testing/` | `CONTRACT_TEST_CASES.md` |
| Chaos | `quality/chaos/` | `EXPERIMENTS.md` |
| Resilience | `quality/resilience/` | `RESILIENCE_TEST_CASES.md` |
| Observability | `quality/observability/` | `README.md` |
| Compliance (FHIR/HL7, HIPAA-like, WCAG/OWASP) | `quality/compliance/` | `HIPAA_LIKE_CHECKLIST.md` |
| Database integrity | `quality/database-testing/` | `DATA_INTEGRITY_TEST_CASES.md` |

## 4. "If You Want to See X, Look at Y"

| If you want to see… | Look at… |
|---------------------|----------|
| How login + JWT works | `apps/backend/.../security/auth/AuthController.java`; endpoints `POST /api/v1/auth/login`, `/refresh`, `GET /me` |
| How RBAC is enforced | `@PreAuthorize("hasAuthority('…')")` on each controller |
| The no-double-booking rule (BR-APPT-001) | `apps/backend/.../appointment/AppointmentService.java`; returns `422` / `OMII-422` |
| The full error code catalogue | `apps/backend/.../shared/error/ErrorCode.java` and [API_BLUEPRINT.md](API_BLUEPRINT.md) |
| FHIR R4 Patient output | `apps/backend/.../fhir/FhirPatientController.java` → `GET /api/v1/fhir/Patient/{id}` (`application/fhir+json`); [FHIR_GUIDE.md](FHIR_GUIDE.md) |
| Audit trail | `apps/backend/.../audit/AuditService.java` |
| Multi-tenant scoping | `apps/backend/.../shared/tenant/TenantContext.java` |
| How tests reach the SUT through adapters | `automation/shared/core/`, `automation/resources/`; [ADR 0004](architecture/adr/0004-resource-adapter-layer-automation.md) |
| API automation examples | `automation/restassured/` |
| UI automation (Playwright/Selenium) | `automation/playwright/`, `automation/selenium/` |
| BDD scenarios | `automation/bdd/` |
| The manual test catalogue + RTM | `manual-testing/`, [RTM.md](RTM.md) |
| How to run everything locally | [DEMO_GUIDE.md](DEMO_GUIDE.md), `scripts/start.sh` |
| The CI/CD pipeline | `.github/workflows/`, [CI_CD_GUIDE.md](CI_CD_GUIDE.md) |
| Why a decision was made | [architecture/adr/README.md](architecture/adr/README.md) |
| How to switch H2 ↔ PostgreSQL | [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md), [ADR 0002](architecture/adr/0002-hybrid-database-h2-postgresql.md), [FAQ.md](FAQ.md) |
| The OpenAPI / Swagger spec | run backend → `http://localhost:8080/swagger-ui.html` |
| AI assistants (and how to keep them off) | `ai/`, `ai/documentation/AI_CONFIGURATION.md` (`omii.ai.enabled=false` default) |

## Examples

- *"Show me where double-booking is rejected."* → §4 row "BR-APPT-001" →
  `AppointmentService.java`; the 422 path is demonstrated live in
  [DEMO_GUIDE.md](DEMO_GUIDE.md) §6.
- *"I only have 10 minutes."* → read §2 steps 1, 5, and 7, then run the demo.

## Future Enhancements

- Generate this map's path table from the live directory tree in CI to prevent drift.
- Add deep links to rendered Javadoc and the OpenAPI HTML once published.

## Dependencies

- Anchored by [PROJECT_METADATA.md](PROJECT_METADATA.md) and
  [PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md).
- Feeds [DEMO_GUIDE.md](DEMO_GUIDE.md), [FEATURE_MATRIX.md](FEATURE_MATRIX.md),
  and [LEARNING_ROADMAP.md](LEARNING_ROADMAP.md).

## References

- [README.md](../README.md) · [ARCHITECTURE.md](../ARCHITECTURE.md) ·
  [architecture/adr/README.md](architecture/adr/README.md)
- [PORTFOLIO_GUIDE.md](PORTFOLIO_GUIDE.md) · [INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial (Milestone 10) |
