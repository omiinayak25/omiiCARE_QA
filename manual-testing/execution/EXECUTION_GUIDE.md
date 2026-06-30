# Test Execution Guide

> **Purpose.** Step-by-step procedure to execute a manual test cycle for omiiCARE_QA — prepare the environment, seed data, run smoke then regression, log results, raise defects, and report — and how that cycle ties into the automation module (`mvn -pl automation -Pe2e test`). All test data is synthetic and PHI-safe.

## Purpose

Gives any QA engineer a repeatable runbook so cycles are consistent regardless of who executes them. It binds the manual cycle to the real infrastructure scripts and the automation suites already in the repository.

## Scope

- **In scope:** Manual execution of auth, patient, appointment, and FHIR flows against `dev`/`qa`; triggering the automated E2E suites; logging and reporting.
- **Out of scope:** Authoring new test cases (see `../test-cases/`), performance/security runs (see `../../quality/`).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Engineer | Executes the cycle, logs results, raises defects |
| QA Lead | Approves environment readiness and the final report |
| Automation Engineer | Maintains the E2E suites invoked in Step 6 |

---

## 1. Pre-flight Checklist

| Check | How |
|-------|-----|
| Toolchain present (Java 21+, Maven, Node, Docker) | `./scripts/setup.sh` (first time only) |
| `.env` created | Created by `setup.sh` from `.env.example` |
| Branch / build identified | Record git short SHA for the report |

## 2. Prepare the Environment

```bash
# From repo root: /home/ttpl-lnv14-017/Downloads/omiiCARE_QA
./scripts/start.sh        # Docker Compose: Postgres, Redis, Keycloak, WireMock, MinIO, Prometheus, Grafana, SonarQube
./scripts/health-check.sh # PASS/FAIL table across all services + backend /actuator/health
```

`start.sh` waits up to 120s for PostgreSQL health, then prints the backend run command and service URLs. Do not proceed until `health-check.sh` reports the critical services (Postgres, backend) as PASS.

## 3. Start the Application Under Test

| Component | Command | URL |
|-----------|---------|-----|
| Backend (PostgreSQL) | `mvn -pl apps/backend spring-boot:run -Dspring-boot.run.profiles=docker` | `http://localhost:8080` |
| Backend (H2, quick) | `mvn -pl apps/backend spring-boot:run` | `http://localhost:8080` |
| Frontend | `cd apps/frontend && npm run dev` | `http://localhost:5173` |

Health: `http://localhost:8080/actuator/health` · API docs: `http://localhost:8080/swagger-ui.html`.
Demo login: `demo.admin` / `Admin@12345`.

## 4. Seed Data

Seeding is automatic and idempotent. On `docker`/`qa` profiles, Flyway applies versioned migrations (`db/migration`) then repeatable seeds (`db/seed`):

| Seed | Loads |
|------|-------|
| `R__seed_010_reference_and_demo.sql` | 12 roles, permissions, tenant `DEMO`, hospital `DEMO-GEN`, 5 departments, user `demo.admin` |
| `R__seed_020_clinical_demo.sql` | Providers DR-001/DR-002, patients MRN-0001/0002/0003, one BOOKED appointment |

Repeatable seeds re-run on checksum change and are guarded by `WHERE NOT EXISTS`, so re-running never duplicates rows. To wipe and re-seed from scratch: `./scripts/reset.sh --yes` then `./scripts/start.sh`. See [Test Data Catalog](../test-data/TEST_DATA_CATALOG.md) for exact values.

## 5. Execute — Smoke First, Then Regression

1. **Smoke (must pass before regression):** auth login, patient list/search, patient create, appointment booking, FHIR `GET /api/v1/fhir/Patient/{id}`. If any smoke case fails, stop and raise a blocker — do not run regression.
2. **Regression:** execute the planned suite from `../test-suites/` per module. Record each case in the execution log as you go.

Log every case in [`TEST_EXECUTION_LOG_TEMPLATE.md`](TEST_EXECUTION_LOG_TEMPLATE.md): TC ID, tester, date, result, defect ref, notes.

## 6. Run the Automated E2E Suites (ties manual cycle to automation)

The automation module excludes E2E by default; the `e2e` profile enables them. With backend + frontend running:

```bash
# Framework unit tests only (no SUT needed) — sanity of the harness
mvn -pl automation test

# Full E2E against the running SUT: API (Rest Assured), UI (Playwright), BDD (Cucumber)
mvn -pl automation -Pe2e test
```

| Suite | Tag | Tool |
|-------|-----|------|
| API E2E | `@api-e2e` | Rest Assured 5.5.0 |
| UI E2E | `@ui-e2e` | Playwright 1.47.0 (Chromium, headless) |
| BDD | `@bdd` | Cucumber 7.20.1 |

Results land in `automation/target/surefire-reports/`. Feed pass/fail and runtime into the metrics dashboard (M-01, M-15). For coverage (M-09): `mvn -Pquality` → `target/site/jacoco/`.

## 7. Raise Defects

For every failure, raise a defect using the templates in [`../bug-templates/`](../bug-templates/), file it under [`../bug-reports/`](../bug-reports/), and record the defect ID in the execution log row. Include: build SHA, environment/profile, steps, expected vs actual, severity, and synthetic data used (never real PHI). Set the defect's origin stage to drive the leakage metric (M-03).

## 8. Report

Produce a [Test Execution Report](../metrics/TEST_EXECUTION_REPORT_TEMPLATE.md) for the cycle: counts, defects, coverage, top risks, and GO/NO-GO recommendation. Compute metrics per [QA Metrics](../metrics/QA_METRICS.md).

## 9. Tear Down

```bash
./scripts/stop.sh          # preserves volumes (data kept for next cycle)
./scripts/reset.sh --yes   # full wipe: down -v + mvn clean (only when a clean slate is required)
```

---

## Related Documents

- [Test Execution Log Template](TEST_EXECUTION_LOG_TEMPLATE.md)
- [Test Execution Report Template](../metrics/TEST_EXECUTION_REPORT_TEMPLATE.md)
- [QA Metrics Catalog](../metrics/QA_METRICS.md)
- [Test Data Catalog](../test-data/TEST_DATA_CATALOG.md)
- [Common Failure Patterns](../knowledge-base/COMMON_FAILURE_PATTERNS.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
