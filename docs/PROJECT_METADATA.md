# Project Metadata

> **Canonical fact sheet.** This document is the single source of truth for
> project-level facts: names, versions, the technology matrix, environments,
> roles, and standards. Every other document defers to the values declared
> here. When a fact changes, change it **here first**, then propagate.

## Purpose

Provide one authoritative, machine-and-human-readable registry of the
foundational facts about omiiCARE_QA so that documentation never disagrees with
itself. This file is read at the start of every working session.

## Scope

- **In scope:** project identity, repository configuration, technology
  decisions, environment names, role taxonomy, healthcare standards, and the
  versioning anchor.
- **Out of scope:** rationale and prose (see [ARCHITECTURE.md](../ARCHITECTURE.md)
  and the guides in `docs/`).

## Responsibilities

| Owner | Responsibility |
|-------|----------------|
| QA Architect | Keep the technology matrix and environment list authoritative |
| Maintainer (`omiinayak25`) | Approve any change to identity/versioning fields |
| All contributors | Reference, never contradict, these values |

---

## 1. Project Identity

| Field | Value |
|-------|-------|
| Product name | omiiCARE_QA |
| Tagline | Enterprise Healthcare Quality Engineering Platform |
| Repository name | `omiiCARE_QA` |
| Repository URL | `https://github.com/omiinayak25/omiiCARE_QA.git` |
| Current version | `0.9.0` (pre-release; AI-native QE) |
| Target stable release | `1.0.0` |
| License | MIT (with healthcare-data notice — see [LICENSE](../LICENSE)) |
| Status | Active — Milestones 1–9 complete; Milestone 10 (Release 1.0.0) next |
| Spec version baseline | Master Build Prompt v1.1 (2026-06-30) |

## 2. Git Configuration

| Field | Value |
|-------|-------|
| Git username | `omiinayak25` |
| Git email | `nayakawadiomkar258@gmail.com` |
| Long-lived branches | `main`, `develop` |
| Default branch | `main` |
| Commit convention | Conventional Commits (see [CONTRIBUTING.md](../CONTRIBUTING.md)) |

## 3. Technology Matrix

> Each choice has (or will have) an ADR in `docs/architecture/adr/`.

### Backend (Milestone 3)
| Layer | Technology |
|-------|-----------|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 3.x |
| Security | Spring Security, JWT |
| Persistence | Spring Data JPA, Hibernate |
| Migrations | Flyway |
| Mapping | MapStruct |
| Boilerplate | Lombok (only where justified) |
| API docs | OpenAPI / Swagger (springdoc) |
| Build | Maven |
| Test | JUnit 5, Mockito |

### Database (Milestone 2)
| Profile | Database |
|---------|----------|
| `dev` | H2 (embedded) |
| `local` / `docker` | PostgreSQL (Docker Compose) |
| `qa` / `stage` / `prod` | External PostgreSQL (Azure / RDS / Cloud SQL / Supabase / Neon / self-hosted) |

### Frontend (Milestone 4)
| Concern | Technology |
|---------|-----------|
| Library | React 18+ |
| Language | TypeScript |
| Build | Vite |
| Routing | React Router |
| Server state | TanStack Query |
| HTTP | Axios |
| UI kit | Material UI (preferred) |
| Forms | React Hook Form + Zod |
| Client state | Redux Toolkit (only where appropriate) |
| i18n | i18next |
| Dates | Day.js |
| Charts | Chart.js or Apache ECharts |

### Automation & Quality (Milestones 5 & 7)
| Concern | Technology |
|---------|-----------|
| UI automation | Playwright, Selenium |
| API automation | Rest Assured |
| BDD | Cucumber / Gherkin |
| Runners | JUnit 5, TestNG |
| Reporting | Allure, Extent Reports |
| Logging | SLF4J + Logback |
| Data/IO | Jackson, Apache POI, OpenCSV |
| Validation | JSON Schema, Awaitility |
| Performance | Apache JMeter, k6, Gatling |
| Security | OWASP ZAP, OWASP Dependency-Check |
| Accessibility | axe-core, Lighthouse |
| Visual | Playwright visual comparisons |

### Infrastructure & DevOps (Milestones 2 & 8)
| Concern | Technology |
|---------|-----------|
| Containers | Docker, Docker Compose |
| Services | PostgreSQL, Redis, MailHog, MinIO, Keycloak, WireMock |
| Monitoring | Prometheus, Grafana |
| Code quality | SonarQube, Spotless, Checkstyle, PMD, SpotBugs |
| Coverage | JaCoCo |
| Tracing | OpenTelemetry |
| CI/CD | GitHub Actions |

### AI Platform (Milestone 9)
| Concern | Technology |
|---------|-----------|
| Providers | Claude, OpenAI, local LLMs (provider-abstracted) |
| Selection | Configuration-driven, never vendor-coupled |

## 4. Environment Matrix

| Environment | Spring profile | Purpose | Database |
|-------------|----------------|---------|----------|
| Development | `dev` | Local coding, fast startup | H2 |
| Local | `local` | Enterprise local w/ real services | PostgreSQL (Docker) |
| Docker | `docker` | Full containerized stack | PostgreSQL (Docker) |
| Test | `test` | Automated test execution | H2 / Testcontainers |
| QA | `qa` | QA validation | External PostgreSQL |
| Stage | `stage` | Pre-production | External PostgreSQL |
| Production | `prod` | Production | External PostgreSQL |
| Demo | `demo` | Curated demo dataset | configurable |
| Training | `training` | Onboarding/teaching | configurable |
| Performance | `perf` | Load/stress (owned infra only) | configurable |
| Disaster Recovery | `dr` | Failover/restore drills | configurable |

Feature flags gate experimental features per environment (see
[docs/ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md)).

## 5. Role Taxonomy (RBAC)

Super Admin · Hospital Admin · Doctor · Nurse · Receptionist · Lab Technician ·
Radiologist · Pharmacist · Billing Staff · Insurance Staff · Patient · Auditor.

Frontend portals map to these roles (see
[docs/UI_UX_SPECIFICATION.md](UI_UX_SPECIFICATION.md)).

## 6. Healthcare Standards (anticipated, phased in)

| Standard | Use | Introduced |
|----------|-----|-----------|
| FHIR R4 | Interoperability resource model | M3 mapping → M7 validation |
| HL7 v2 | Legacy messaging concepts | M3 mapping → M7 validation |
| ICD-10 | Diagnosis coding | M3 |
| CPT | Procedure coding | M3 |
| LOINC | Lab observation coding | M3 |
| SNOMED CT | Clinical terminology | M3 |

> **Compliance note:** omiiCARE_QA models *HIPAA-like* privacy practices and
> standards conformance for education and portfolio purposes. It makes **no
> formal certification claims**. See [SECURITY.md](../SECURITY.md).

## 7. Versioning Anchor

Semantic Versioning 2.0.0. See [VERSIONING.md](../VERSIONING.md). Current line:
`0.x` (pre-1.0, breaking changes allowed); `1.0.0` is the first stable release
(Milestone 10).

## Examples

- A new doc needing the backend language must state **"Java 21"** — the value in
  §3, not an independently chosen version.
- A test environment reference must use a profile name from §4 (e.g. `qa`),
  never an ad-hoc name like "staging2".

## Future Enhancements

- Generate a machine-readable `project-metadata.json` from this file in CI.
- Add a documentation-lint rule that fails the build when a doc declares a
  technology version that disagrees with §3.

## Dependencies

- Consumed by every document in the repository.
- Drives [ROADMAP.md](../ROADMAP.md) milestone scoping.

## References

- [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md)
- [ARCHITECTURE.md](../ARCHITECTURE.md)
- [VERSIONING.md](../VERSIONING.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial metadata baseline (Milestone 1) |
