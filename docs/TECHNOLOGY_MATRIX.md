# Technology Matrix — omiiCARE_QA

> **Purpose.** One reference table per layer: the technology, its version, why it
> was chosen, and the governing ADR. This guide is **consistent with**
> [PROJECT_METADATA.md](PROJECT_METADATA.md) §3, which remains the canonical
> version registry — when a version changes, change it there first, then mirror
> it here.

## Scope

- **In scope:** layer × technology × version × rationale × ADR reference.
- **Out of scope:** feature status ([FEATURE_MATRIX.md](FEATURE_MATRIX.md)) and
  the canonical version source ([PROJECT_METADATA.md](PROJECT_METADATA.md) §3).

## Responsibilities

| Owner | Responsibility |
|-------|----------------|
| QA Architect | Keep rationale + ADR columns accurate; mirror versions from metadata §3 |
| Maintainer (`omiinayak25`) | Approve version bumps in [PROJECT_METADATA.md](PROJECT_METADATA.md) first |

---

## 1. Backend

| Technology | Version | Why chosen | ADR |
|------------|---------|------------|-----|
| Java | 21 (LTS) | Long-term support, records/pattern-matching, virtual threads | [0005](architecture/adr/0005-java-spring-boot-backend.md) |
| Spring Boot | 3.x | Mature ecosystem, auto-config, first-class testing | [0005](architecture/adr/0005-java-spring-boot-backend.md) |
| Spring Security + JWT | 3.x | Standard, stateless auth with method-level RBAC | [0005](architecture/adr/0005-java-spring-boot-backend.md) |
| Spring Data JPA / Hibernate | 3.x | Repository abstraction, portable across H2/PostgreSQL | [0003](architecture/adr/0003-clean-architecture-ddd-backend.md) |
| Flyway | — | Versioned, forward-only migrations; same scripts on H2 + PostgreSQL | [0007](architecture/adr/0007-flyway-database-migrations.md) |
| MapStruct | — | Compile-time DTO mapping, no reflection cost | [0003](architecture/adr/0003-clean-architecture-ddd-backend.md) |
| Lombok | — | Boilerplate reduction, used only where justified | [0003](architecture/adr/0003-clean-architecture-ddd-backend.md) |
| OpenAPI / springdoc | — | Self-documenting contract at `/swagger-ui.html` | [0005](architecture/adr/0005-java-spring-boot-backend.md) |
| Maven | — | Reactor multi-module build, dependency management | [0001](architecture/adr/0001-monorepo-structure.md) |
| JUnit 5 + Mockito | — | Standard unit/integration testing | [0003](architecture/adr/0003-clean-architecture-ddd-backend.md) |

Clean Architecture + DDD organize the domain so business rules (e.g. BR-APPT-001)
live in services, not controllers — see [0003](architecture/adr/0003-clean-architecture-ddd-backend.md).

## 2. Database

| Technology | Version | Why chosen | ADR |
|------------|---------|------------|-----|
| H2 (embedded) | — | Zero-setup `dev`/`test`; instant startup for demos and CI | [0002](architecture/adr/0002-hybrid-database-h2-postgresql.md) |
| PostgreSQL | — | Production-grade RDBMS for `local`/`docker`/`qa`/`stage`/`prod` | [0002](architecture/adr/0002-hybrid-database-h2-postgresql.md) |
| Flyway | — | One migration set drives both engines; config-only switch | [0007](architecture/adr/0007-flyway-database-migrations.md) |

The H2 ↔ PostgreSQL split is switched **by Spring profile only** — see
[ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md) and [FAQ.md](FAQ.md).

## 3. Frontend

| Technology | Version | Why chosen | ADR |
|------------|---------|------------|-----|
| React | 18+ | Component model, ecosystem, hiring-friendly | [0006](architecture/adr/0006-react-typescript-vite-frontend.md) |
| TypeScript | — | Type-safe contracts shared with the API | [0006](architecture/adr/0006-react-typescript-vite-frontend.md) |
| Vite | — | Fast dev server + build (`tsc --noEmit && vite build`) | [0006](architecture/adr/0006-react-typescript-vite-frontend.md) |
| Material UI (MUI) | — | Accessible, themeable component kit (WCAG-aware) | [0006](architecture/adr/0006-react-typescript-vite-frontend.md) |
| React Router | — | Role-based routing for portals | [0006](architecture/adr/0006-react-typescript-vite-frontend.md) |
| TanStack Query | — | Server-state caching/fetching | [0006](architecture/adr/0006-react-typescript-vite-frontend.md) |
| Axios | — | HTTP client with interceptors for JWT | [0006](architecture/adr/0006-react-typescript-vite-frontend.md) |
| React Hook Form + Zod | — | Performant forms with schema validation | [0006](architecture/adr/0006-react-typescript-vite-frontend.md) |
| i18next | — | Internationalization | [0006](architecture/adr/0006-react-typescript-vite-frontend.md) |

## 4. Automation & Advanced Quality

| Technology | Version | Why chosen | ADR |
|------------|---------|------------|-----|
| Playwright | — | Modern, reliable cross-browser UI automation | [0004](architecture/adr/0004-resource-adapter-layer-automation.md) |
| Selenium | — | Breadth of browser/grid coverage, industry baseline | [0004](architecture/adr/0004-resource-adapter-layer-automation.md) |
| Rest Assured | — | Fluent API testing in the JVM | [0004](architecture/adr/0004-resource-adapter-layer-automation.md) |
| Cucumber / Gherkin | — | Readable BDD specs for stakeholder alignment | [0004](architecture/adr/0004-resource-adapter-layer-automation.md) |
| TestNG / JUnit 5 | — | Runners for the automation suites | [0004](architecture/adr/0004-resource-adapter-layer-automation.md) |
| Allure / Extent | — | Rich, shareable test reports | [0004](architecture/adr/0004-resource-adapter-layer-automation.md) |
| JMeter / k6 / Gatling | — | Complementary load/stress tooling | [0001](architecture/adr/0001-monorepo-structure.md) |
| OWASP ZAP / Dependency-Check | — | DAST + dependency vulnerability scanning | [0001](architecture/adr/0001-monorepo-structure.md) |
| axe-core / Lighthouse | — | Automated accessibility + quality audits | [0001](architecture/adr/0001-monorepo-structure.md) |

The **resource-adapter layer** ([0004](architecture/adr/0004-resource-adapter-layer-automation.md))
lets tests target capabilities through adapters, keeping the suite environment-independent.

## 5. Infrastructure & DevOps

| Technology | Version | Why chosen | ADR |
|------------|---------|------------|-----|
| Docker / Docker Compose | — | One-command local stack parity | [0001](architecture/adr/0001-monorepo-structure.md) |
| PostgreSQL / Redis / MailHog / MinIO / Keycloak / WireMock | — | Realistic supporting services for full-stack testing | [0001](architecture/adr/0001-monorepo-structure.md) |
| Prometheus / Grafana | — | Metrics + dashboards (observability layer) | [0001](architecture/adr/0001-monorepo-structure.md) |
| SonarQube / Spotless / Checkstyle / PMD / SpotBugs | — | Static analysis + formatting gates | [0008](architecture/adr/0008-documentation-first-governance.md) |
| JaCoCo | — | Backend coverage reporting | [0008](architecture/adr/0008-documentation-first-governance.md) |
| OpenTelemetry | — | Distributed tracing | [0001](architecture/adr/0001-monorepo-structure.md) |
| GitHub Actions | — | Reusable CI/CD workflows + release automation | [0008](architecture/adr/0008-documentation-first-governance.md) |

CI/CD composition is detailed in [CI_CD_GUIDE.md](CI_CD_GUIDE.md).

## 6. AI Platform (opt-in, off by default)

| Technology | Version | Why chosen | ADR |
|------------|---------|------------|-----|
| Provider abstraction (Claude / OpenAI / local LLMs) | — | Configuration-driven, never vendor-coupled | [0001](architecture/adr/0001-monorepo-structure.md) |
| Master switch `omii.ai.enabled` | default `false` | Platform is fully functional with AI disabled | `ai/documentation/AI_CONFIGURATION.md` |

See [AI_DEVELOPMENT_RULES.md](AI_DEVELOPMENT_RULES.md) and the [FAQ.md](FAQ.md)
entry on whether AI is required (it is not).

## Examples

- *Choosing a version to cite in a new doc:* read [PROJECT_METADATA.md](PROJECT_METADATA.md)
  §3 (e.g. **Java 21**), then this matrix for the rationale and ADR.
- *Defending a choice in review:* open the ADR linked in the row.

## Future Enhancements

- Pin exact dependency versions from `pom.xml` / `package.json` into the version
  column via a CI doc-generation step.
- Add an ADR index column that resolves to ADR status (Accepted/Superseded).

## Dependencies

- Mirrors [PROJECT_METADATA.md](PROJECT_METADATA.md) §3 (canonical versions).
- ADR references resolve into [architecture/adr/](architecture/adr/README.md).

## References

- [ARCHITECTURE.md](../ARCHITECTURE.md) · [DESIGN_PATTERNS.md](DESIGN_PATTERNS.md)
- [FEATURE_MATRIX.md](FEATURE_MATRIX.md) · [CI_CD_GUIDE.md](CI_CD_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial (Milestone 10) |
