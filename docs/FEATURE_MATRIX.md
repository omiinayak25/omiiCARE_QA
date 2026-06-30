# Feature Matrix — omiiCARE_QA

> **Purpose.** One table-driven view of every capability in omiiCARE_QA **1.0.0**:
> what it is, its status, where it is implemented, and how it is tested. Use it to
> confirm coverage at a glance and to jump straight to the implementing code.

## Scope

- **In scope:** capability × status × implementation location × test evidence,
  across backend, frontend, automation, manual QA, advanced quality, CI/CD, and AI.
- **Out of scope:** technology versions and rationale ([TECHNOLOGY_MATRIX.md](TECHNOLOGY_MATRIX.md)),
  navigation ([REPOSITORY_TOUR.md](REPOSITORY_TOUR.md)), and the live run
  ([DEMO_GUIDE.md](DEMO_GUIDE.md)).

## Status legend

| Symbol | Meaning |
|--------|---------|
| Done | Implemented and exercised by tests in the 1.0.0 release |
| Read-only | Implemented as a read/facade slice (not full CRUD) |
| Opt-in | Present but disabled by default |

---

## 1. Backend — Identity, Domain & APIs

| # | Capability | Status | Where implemented | Tests / evidence |
|---|------------|--------|-------------------|------------------|
| 1 | JWT auth (login / refresh / me) | Done | `security/auth/AuthController.java`, `security/jwt/` | backend unit + automation (Rest Assured) |
| 2 | RBAC authorities (`patient:*`, `appointment:*`, …) | Done | `@PreAuthorize` on each controller; `config/security/` | negative-path API tests (401/403) |
| 3 | Patient CRUD + search + pagination | Done | `patient/PatientController.java`, `patient/domain/` | backend unit + API automation |
| 4 | Provider directory | Done | `provider/ProviderController.java` | backend unit + API automation |
| 5 | Appointment booking + cancel | Done | `appointment/AppointmentController.java` (`/{id}/cancel`) | backend unit + API automation |
| 6 | Business rule BR-APPT-001 (no double-booking → 422) | Done | `appointment/AppointmentService.java` | unit test + [DEMO_GUIDE.md](DEMO_GUIDE.md) §6 |
| 7 | FHIR R4 Patient read facade | Read-only | `fhir/FhirPatientController.java`, `fhir/FhirPatientMapper.java` | contract/schema tests in `quality/contract-testing/` |
| 8 | Audit logging | Done | `audit/AuditService.java`, `audit/AuditLogEntity.java` | DB-integrity tests, `quality/database-testing/` |
| 9 | Multi-tenant context | Done | `shared/tenant/TenantContext.java` | scoping assertions in API automation |
| 10 | RFC 7807 errors (`OMII-4xx`/`5xx`) | Done | `shared/error/ErrorCode.java`, exception framework | error-path API tests |
| 11 | Validation framework | Done | Bean Validation on DTOs; `shared/api/` | 400/`OMII-400` tests |
| 12 | OpenAPI / Swagger | Done | springdoc; `/swagger-ui.html` | [API_BLUEPRINT.md](API_BLUEPRINT.md) |
| 13 | Flyway migrations (H2 ↔ PostgreSQL) | Done | `apps/backend/src/main/resources/db/`, `database/migrations/` | migration apply in CI |

## 2. Frontend — Portals (System Under Test)

| # | Capability | Status | Where implemented | Tests / evidence |
|---|------------|--------|-------------------|------------------|
| 14 | Login + JWT session | Done | `apps/frontend/src/auth/` | Playwright/Selenium UI automation |
| 15 | Role-based routing / portals | Done | `apps/frontend/src/routes/`, `pages/` | UI automation, [UI_UX_SPECIFICATION.md](UI_UX_SPECIFICATION.md) |
| 16 | Patient management screens | Done | `apps/frontend/src/pages/` | UI + visual automation |
| 17 | Appointment scheduling UI | Done | `apps/frontend/src/pages/` | UI automation incl. double-booking path |
| 18 | API client (Axios + typed contracts) | Done | `apps/frontend/src/api/`, `types/` | build (`tsc --noEmit`) + lint (0 warnings) |
| 19 | i18n + MUI theme | Done | `apps/frontend/src/i18n/`, `theme/` | build + a11y checks |

## 3. Automation Platform

| # | Capability | Status | Where implemented | Tests / evidence |
|---|------------|--------|-------------------|------------------|
| 20 | Resource-adapter layer (test through adapters, not URLs) | Done | `automation/shared/core/`, `automation/resources/` | [ADR 0004](architecture/adr/0004-resource-adapter-layer-automation.md) |
| 21 | API automation (Rest Assured) | Done | `automation/restassured/` | reactor: 5 automation tests green |
| 22 | UI automation (Playwright, Selenium) | Done | `automation/playwright/`, `automation/selenium/` | Allure / Extent reports |
| 23 | BDD (Cucumber / Gherkin) | Done | `automation/bdd/` | scenario runs |
| 24 | Reporting (Allure, Extent) | Done | `automation/shared/reporting/` | report artifacts |

## 4. Manual Quality Engineering

| # | Capability | Status | Where implemented | Tests / evidence |
|---|------------|--------|-------------------|------------------|
| 25 | Requirements + RTM | Done | `manual-testing/requirements/`, [RTM.md](RTM.md) | traceability matrix |
| 26 | Test plan / cases / suites | Done | `manual-testing/test-plan/`, `test-cases/`, `test-suites/` | [MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md) |
| 27 | Defect + release management | Done | `manual-testing/bug-reports/`, `release/`, `signoff/` | ~46 manual docs |
| 28 | UAT + risk analysis | Done | `manual-testing/uat/`, `risk-analysis/` | [RISK_ANALYSIS.md](RISK_ANALYSIS.md) |

## 5. Advanced Quality (`quality/`)

| # | Capability | Status | Where implemented | Tests / evidence |
|---|------------|--------|-------------------|------------------|
| 29 | Performance (JMeter / k6 / Gatling) | Done | `quality/performance/` | [PERFORMANCE_GUIDE.md](PERFORMANCE_GUIDE.md) |
| 30 | Security (OWASP ZAP, Dependency-Check) | Done | `quality/security/` | [SECURITY_TESTING_GUIDE.md](SECURITY_TESTING_GUIDE.md), `OWASP_TOP10_MAPPING.md` |
| 31 | Accessibility (axe-core, Lighthouse) | Done | `quality/accessibility/` | [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md) |
| 32 | Visual regression (Playwright) | Done | `quality/visual/` | baseline snapshots |
| 33 | Contract / schema | Done | `quality/contract-testing/` | `CONTRACT_TEST_CASES.md` |
| 34 | Chaos / resilience | Done | `quality/chaos/`, `quality/resilience/` | `EXPERIMENTS.md` |
| 35 | Observability | Done | `quality/observability/` (Grafana, Prometheus) | dashboards |
| 36 | DB integrity testing | Done | `quality/database-testing/` | `DATA_INTEGRITY_TEST_CASES.md` |
| 37 | Compliance (FHIR/HL7, HIPAA-like, WCAG/OWASP) | Done | `quality/compliance/` | `HIPAA_LIKE_CHECKLIST.md` |

## 6. CI/CD & DevOps

| # | Capability | Status | Where implemented | Tests / evidence |
|---|------------|--------|-------------------|------------------|
| 38 | Reusable GitHub Actions workflows | Done | `.github/workflows/` | [CI_CD_GUIDE.md](CI_CD_GUIDE.md) |
| 39 | Quality gates (build/test/lint/format/security/docs) | Done | `_reusable-*.yml` | [QUALITY_GATES.md](QUALITY_GATES.md) |
| 40 | CodeQL + dependency review | Done | `.github/workflows/codeql.yml`, `dependency-review.yml` | PR gate |
| 41 | Docker image build / tag / scan | Done | `_reusable-docker.yml`, Dockerfiles | GHCR publish on release |
| 42 | Semantic-version release automation | Done | `release.yml` (tag `v*`) | [VERSIONING.md](../VERSIONING.md), [CHANGELOG.md](../CHANGELOG.md) |

## 7. AI-Native QE (opt-in)

| # | Capability | Status | Where implemented | Tests / evidence |
|---|------------|--------|-------------------|------------------|
| 43 | Provider abstraction (Claude / OpenAI / local) | Opt-in | `ai/providers/`, `ai/src/main/` | reactor: 9 AI tests green |
| 44 | Prompt library + templates | Opt-in | `ai/prompts/`, `ai/templates/` | `ai/README.md` |
| 45 | Guardrails / security | Opt-in | `ai/quality/`, `ai/documentation/AI_SECURITY_GUARDRAILS.md` | guardrail tests |
| 46 | Master on/off switch | Opt-in | `omii.ai.enabled=false` default (`ai/.../AiConfig.java`) | platform fully functional with AI off |

## Examples

- *Coverage audit:* scan the **Tests / evidence** column for any row lacking
  evidence — none should in 1.0.0.
- *"Where is X tested?"* find the capability row, then open the linked folder.

## Future Enhancements

- Auto-generate rows 1–13 from controller annotations and link to live Javadoc.
- Add per-capability coverage percentages once JaCoCo thresholds become blocking.

## Dependencies

- Implementation locations mirror [REPOSITORY_TOUR.md](REPOSITORY_TOUR.md) §3–4.
- Technology choices live in [TECHNOLOGY_MATRIX.md](TECHNOLOGY_MATRIX.md).

## References

- [RTM.md](RTM.md) · [TEST_STRATEGY.md](TEST_STRATEGY.md) · [API_BLUEPRINT.md](API_BLUEPRINT.md)
- [ROADMAP.md](../ROADMAP.md) · [CHANGELOG.md](../CHANGELOG.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial (Milestone 10) |
