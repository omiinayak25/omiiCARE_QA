# QA Onboarding Guide

> **Purpose.** First-week ramp for a QA engineer joining omiiCARE_QA — repo tour, how to run backend/frontend/automation, where the QA assets live, and a first-week checklist. Everything uses synthetic, PHI-safe data.

## Purpose

Get a new QA engineer from clone to first executed cycle quickly and safely, with the right mental model of the system and where to find each asset.

## Scope

- **In scope:** Environment setup, running the SUT and automation, asset map, first-week tasks.
- **Out of scope:** HR/access provisioning and product roadmap detail (see `ROADMAP.md`).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Assigns a buddy and reviews first-week deliverables |
| New QA Engineer | Completes the checklist and the first cycle |

---

## 1. The System in One Minute

omiiCARE_QA is a multi-tenant healthcare platform with implemented **auth/RBAC**, **patient**, **appointment**, and a **FHIR R4 read facade**. Backend is **Java 21 / Spring Boot 3.3.4**; frontend is **React 18 / TypeScript / Vite**. There is a polyglot **automation module** (Rest Assured, Playwright, Cucumber). Infrastructure runs via **Docker Compose** (Postgres, Redis, Keycloak, WireMock, MinIO, Prometheus, Grafana, SonarQube). Twelve RBAC roles gate access; a `DEMO` tenant is seeded with demo users, providers, and patients.

## 2. Repository Tour

| Path | What's there |
|------|--------------|
| `apps/backend/` | Spring Boot API, Flyway migrations (`db/migration`) and seeds (`db/seed`) |
| `apps/frontend/` | React + Vite UI |
| `automation/` | Rest Assured / Playwright / Cucumber suites, `PatientFactory` |
| `infrastructure/docker/` | `docker-compose.yml` for the service stack |
| `scripts/` | `setup`, `start`, `stop`, `reset`, `health-check` |
| `database/` | DB docs, seeds catalog, migration standards |
| `manual-testing/` | All QA assets (this folder) |
| `quality/` | Performance, security, accessibility, contract, etc. |
| `docs/` | Strategy, test plan, RTM, business rules, ADRs |

## 3. One-Time Setup

```bash
# From repo root: /home/ttpl-lnv14-017/Downloads/omiiCARE_QA
./scripts/setup.sh   # checks Java 21+, Maven, Node, Docker; creates .env; builds backend; installs frontend deps
```

## 4. Run It

```bash
./scripts/start.sh          # bring up Docker services
./scripts/health-check.sh   # confirm all services PASS

# Backend (PostgreSQL via Docker)
mvn -pl apps/backend spring-boot:run -Dspring-boot.run.profiles=docker   # http://localhost:8080

# Frontend
cd apps/frontend && npm run dev                                          # http://localhost:5173
```

| Resource | URL |
|----------|-----|
| Backend health | `http://localhost:8080/actuator/health` |
| API docs (Swagger) | `http://localhost:8080/swagger-ui.html` |
| Frontend | `http://localhost:5173` |
| Demo login | `demo.admin` / `Admin@12345` |

## 5. Run the Automation

```bash
mvn -pl automation test          # framework/unit tests only (no SUT needed)
mvn -pl automation -Pe2e test    # full E2E: API (Rest Assured) + UI (Playwright) + BDD (Cucumber), SUT must be up
mvn -Pquality                    # Spotless + JaCoCo coverage → target/site/jacoco/
```

Reports: `automation/target/surefire-reports/`.

## 6. Where the QA Assets Live

| Need | Go to |
|------|-------|
| How to run a cycle | [`../execution/EXECUTION_GUIDE.md`](../execution/EXECUTION_GUIDE.md) |
| Per-case log template | [`../execution/TEST_EXECUTION_LOG_TEMPLATE.md`](../execution/TEST_EXECUTION_LOG_TEMPLATE.md) |
| Metric definitions | [`../metrics/QA_METRICS.md`](../metrics/QA_METRICS.md) |
| Cycle report template | [`../metrics/TEST_EXECUTION_REPORT_TEMPLATE.md`](../metrics/TEST_EXECUTION_REPORT_TEMPLATE.md) |
| Test data (what's seeded) | [`../test-data/TEST_DATA_CATALOG.md`](../test-data/TEST_DATA_CATALOG.md) |
| Boundary/edge data | [`../test-data/BOUNDARY_DATA_SETS.md`](../test-data/BOUNDARY_DATA_SETS.md) |
| Healthcare testing | [`../knowledge-base/HEALTHCARE_QA_GUIDE.md`](../knowledge-base/HEALTHCARE_QA_GUIDE.md) |
| Terms & acronyms | [`../knowledge-base/GLOSSARY.md`](../knowledge-base/GLOSSARY.md) |
| Flaky/failure patterns | [`../knowledge-base/COMMON_FAILURE_PATTERNS.md`](../knowledge-base/COMMON_FAILURE_PATTERNS.md) |
| How we work | [`../knowledge-base/BEST_PRACTICES.md`](../knowledge-base/BEST_PRACTICES.md) |
| Hard-won lessons | [`../knowledge-base/LESSONS_LEARNED.md`](../knowledge-base/LESSONS_LEARNED.md) |
| Strategy / plan / RTM | [`../../docs/TEST_STRATEGY.md`](../../docs/TEST_STRATEGY.md), [`../../docs/MASTER_TEST_PLAN.md`](../../docs/MASTER_TEST_PLAN.md), [`../rtm/`](../rtm/) |

## 7. First-Week Checklist

| Day | Task | Done when |
|-----|------|-----------|
| 1 | Clone, run `setup.sh`, start stack, health-check PASS | All services green |
| 1 | Read the Glossary and this guide | Can explain SUT, RBAC, FHIR, MRN |
| 2 | Log in to the UI as `demo.admin`; explore patient/appointment | Booked the demo data path |
| 2 | Read Test Data Catalog + Boundary Data Sets | Knows seeded MRNs and edge values |
| 3 | Run `mvn -pl automation test`, then `-Pe2e` against the SUT | Both runs complete; reads surefire reports |
| 3 | Read Healthcare QA Guide + Best Practices | Understands PHI-safe + audit discipline |
| 4 | Execute a small manual smoke cycle; fill the execution log | Log has real rows |
| 4 | Raise one practice defect using the bug template | Defect filed with build SHA + synthetic data |
| 5 | Produce a Test Execution Report for the smoke cycle | Report has a GO/NO-GO recommendation |
| 5 | Review with buddy; read Lessons Learned | Buddy sign-off |

---

## Related Documents

- [Execution Guide](../execution/EXECUTION_GUIDE.md)
- [QA Metrics Catalog](../metrics/QA_METRICS.md)
- [Test Data Catalog](../test-data/TEST_DATA_CATALOG.md)
- [Glossary](../knowledge-base/GLOSSARY.md)
- [README.md](../../README.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
