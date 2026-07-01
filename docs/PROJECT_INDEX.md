# omiiCARE_QA — Project Index (Master Table of Contents)

> **Automation architecture:** [Layered Enterprise Test Automation Architecture](qa-management/TEST_AUTOMATION_ARCHITECTURE.md) — the permanent framework standard.

> **Start here:** [PROJECT_POLICY.md](PROJECT_POLICY.md) — standing policy: OpenMRS (o2.openmrs.org) is the permanent primary System Under Test; this repo is an enterprise QA/QE platform, not a custom app.

> **The single entry point** for the omiiCARE_QA enterprise healthcare QA platform.
> Every major asset — reverse-engineering docs, QA management, requirements, RTM,
> manual test suite, automation framework, quality assets, AI-QE, CI/CD, and
> portfolio guides — is linked from here.
>
> **Primary reference SUT:** OpenMRS (`https://o2.openmrs.org`) ·
> **Local SUT:** omiiCARE (Spring Boot 3 / Java 21 + React/Vite) ·
> **Toolchain:** Java 21 · Maven 3.9 · Node 22 ·
> **Release:** v1.0.0 — see [Release Readiness](./RELEASE_READINESS_1.0.0.md).

---

## 0. Start Here

| If you want to… | Go to |
|-----------------|-------|
| Understand the whole repo in one page | This file |
| Check release status / go-no-go | [`docs/RELEASE_READINESS_1.0.0.md`](./RELEASE_READINESS_1.0.0.md) |
| Read the project pitch | [`README.md`](../README.md) · [`MASTER_PROJECT_SPECIFICATION.md`](../MASTER_PROJECT_SPECIFICATION.md) |
| See the architecture | [`ARCHITECTURE.md`](../ARCHITECTURE.md) · [`docs/architecture/`](./architecture/) |
| Run tests | [§5 Automation](#5-automation-framework) · [§6 Quality Assets](#6-quality-engineering-assets) |
| Tour the repo layout | [`PROJECT_STRUCTURE.md`](../PROJECT_STRUCTURE.md) · [`docs/REPOSITORY_TOUR.md`](./REPOSITORY_TOUR.md) |
| Present this for a portfolio/interview | [§9 Portfolio & Interview](#9-portfolio--interview) |

---

## 1. Repository Map

```
omiiCARE_QA/
├── apps/                 backend (Spring Boot 3) + frontend (React/Vite)
├── automation/           Java QE framework (Playwright/Selenium/RestAssured/Cucumber/JUnit5/TestNG)
│   └── playwright/       Node @playwright/test smoke suites (omiiCARE + OpenMRS)
├── ai/                   AI-native Quality Engineering
├── quality/             performance / security / accessibility / visual / contract / chaos / ...
├── manual-testing/       4,187 manual test cases + RTM + plans
├── docs/                reverse-engineering / qa-management / requirements / guides
├── infrastructure/       Docker Compose stack (Postgres/Redis/Keycloak/WireMock/Grafana/...)
├── scripts/             setup / start / stop / health-check
├── database/ · config/ · postman/
└── .github/workflows/    CI/CD
```

Reference: [`PROJECT_STRUCTURE.md`](../PROJECT_STRUCTURE.md) · [`docs/REPOSITORY_TOUR.md`](./REPOSITORY_TOUR.md) · [`docs/REPOSITORY_STANDARDS.md`](./REPOSITORY_STANDARDS.md)

---

## 2. Reverse-Engineering Documentation
`docs/reverse-engineering/` — 22 docs, ~10k lines, 78 Mermaid diagrams.

| Doc | Description |
|-----|-------------|
| [`README.md`](./reverse-engineering/README.md) | Index of the RE corpus |
| [`BRD.md`](./reverse-engineering/BRD.md) | Business Requirements |
| [`SRS.md`](./reverse-engineering/SRS.md) | Software Requirements Spec |
| [`FRD.md`](./reverse-engineering/FRD.md) | Functional Requirements |
| [`NFR.md`](./reverse-engineering/NFR.md) | Non-Functional Requirements |
| [`USE_CASES.md`](./reverse-engineering/USE_CASES.md) | Use cases |
| [`USER_STORIES_AND_ACCEPTANCE_CRITERIA.md`](./reverse-engineering/USER_STORIES_AND_ACCEPTANCE_CRITERIA.md) | Stories + AC |
| [`RBAC_MATRIX.md`](./reverse-engineering/RBAC_MATRIX.md) | Roles & permissions |
| [`NAVIGATION_MAP.md`](./reverse-engineering/NAVIGATION_MAP.md) | Navigation map |
| [`DATA_DICTIONARY.md`](./reverse-engineering/DATA_DICTIONARY.md) · [`FIELD_DICTIONARY.md`](./reverse-engineering/FIELD_DICTIONARY.md) | Data & field dictionaries |
| [`VALIDATION_MATRIX.md`](./reverse-engineering/VALIDATION_MATRIX.md) | Validation rules |
| [`ARCHITECTURE.md`](./reverse-engineering/ARCHITECTURE.md) · [`diagrams/`](./reverse-engineering/diagrams/) | Architecture + diagrams/ERD |
| [`API_BLUEPRINT.md`](./reverse-engineering/API_BLUEPRINT.md) | API surface |
| [`FHIR_MAPPING.md`](./reverse-engineering/FHIR_MAPPING.md) · [`HL7_MAPPING.md`](./reverse-engineering/HL7_MAPPING.md) | FHIR / HL7 v2 mappings |
| [`RISK_REGISTER.md`](./reverse-engineering/RISK_REGISTER.md) | Risk register |
| [`ASSUMPTIONS_AND_OPEN_QUESTIONS.md`](./reverse-engineering/ASSUMPTIONS_AND_OPEN_QUESTIONS.md) | Assumptions / open questions |

---

## 3. QA Management Documentation
`docs/qa-management/` — strategy, plans, governance.

| Doc | Description |
|-----|-------------|
| [`README.md`](./qa-management/README.md) | Index |
| [`MASTER_TEST_STRATEGY.md`](./qa-management/MASTER_TEST_STRATEGY.md) · [`MASTER_TEST_PLAN.md`](./qa-management/MASTER_TEST_PLAN.md) | Master strategy & plan |
| [`RELEASE_TEST_PLAN.md`](./qa-management/RELEASE_TEST_PLAN.md) · [`SPRINT_TEST_PLAN.md`](./qa-management/SPRINT_TEST_PLAN.md) | Release & sprint plans |
| [`RISK_BASED_TESTING_STRATEGY.md`](./qa-management/RISK_BASED_TESTING_STRATEGY.md) | Risk-based testing |
| [`QA_ESTIMATION.md`](./qa-management/QA_ESTIMATION.md) | Estimation |
| [`DEFECT_MANAGEMENT_PROCESS.md`](./qa-management/DEFECT_MANAGEMENT_PROCESS.md) · [`BUG_REPORT_TEMPLATES.md`](./qa-management/BUG_REPORT_TEMPLATES.md) | Defect mgmt & templates |
| [`QA_METRICS.md`](./qa-management/QA_METRICS.md) | Metrics |
| [`ENTRY_EXIT_CRITERIA.md`](./qa-management/ENTRY_EXIT_CRITERIA.md) · [`RELEASE_CHECKLISTS.md`](./qa-management/RELEASE_CHECKLISTS.md) | Entry/exit & checklists |
| [`UAT_PLAN.md`](./qa-management/UAT_PLAN.md) | UAT plan |
| [`COVERAGE_STRATEGY.md`](./qa-management/COVERAGE_STRATEGY.md) | Coverage strategy |
| [`TEST_AUTOMATION_STRATEGY.md`](./qa-management/TEST_AUTOMATION_STRATEGY.md) | Automation strategy |
| [`TEST_DATA_AND_ENVIRONMENT_MANAGEMENT.md`](./qa-management/TEST_DATA_AND_ENVIRONMENT_MANAGEMENT.md) | Test data & env |
| [`SONARQUBE_AND_QUALITY_GATES.md`](./qa-management/SONARQUBE_AND_QUALITY_GATES.md) | Quality gates |
| [`RELEASE_AUTOMATION_AND_VERSIONING.md`](./qa-management/RELEASE_AUTOMATION_AND_VERSIONING.md) | Release automation & SemVer |
| [`QA_KNOWLEDGE_BASE.md`](./qa-management/QA_KNOWLEDGE_BASE.md) · [`DOCUMENTATION_VALIDATION.md`](./qa-management/DOCUMENTATION_VALIDATION.md) | KB & doc validation |

---

## 4. Requirements, Traceability & Manual Test Suite

### 4.1 Requirements
- [`docs/requirements/requirements-catalog.md`](./requirements/requirements-catalog.md) — **1,795 requirements** across 66 modules, 100% traced.

### 4.2 Requirements Traceability Matrix (RTM)
- [`manual-testing/rtm/RTM.md`](../manual-testing/rtm/RTM.md) · [`RTM.csv`](../manual-testing/rtm/RTM.csv) — **0 gaps, 0 untraced**.
- Repo-level summary: [`docs/RTM.md`](./RTM.md).

### 4.3 Manual Test Suite
`manual-testing/` — **4,187 manual test cases · 66 modules** (17-column CSV).

| Asset | Path |
|-------|------|
| Overview | [`manual-testing/README.md`](../manual-testing/README.md) |
| All test cases (OpenMRS) | [`manual-testing/test-cases/openmrs/`](../manual-testing/test-cases/openmrs/) |
| Consolidated CSV | [`manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv`](../manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv) |
| Plans / strategy / suites | `manual-testing/test-plan/` · `test-strategy/` · `test-suites/` |
| Execution / signoff / UAT | `manual-testing/execution/` · `signoff/` · `uat/` |
| Defects & bug templates | `manual-testing/bug-reports/` · `bug-templates/` |
| Risk / metrics / KB | `manual-testing/risk-analysis/` · `metrics/` · `knowledge-base/` |
| Checklists / training / test data | `manual-testing/checklists/` · `training/` · `test-data/` |

---

## 5. Automation Framework
`automation/` (package `com.omiicare.qa.automation`) — see [`automation/README.md`](../automation/README.md) and [`automation/AUTOMATION_FRAMEWORK.md`](../automation/AUTOMATION_FRAMEWORK.md).

### 5.1 Capabilities
- **UI:** Playwright (Java), Selenium · **API:** RestAssured · **BDD:** Cucumber · **Runners:** JUnit5, TestNG
- **Portability:** Resource Adapter Layer (`core.adapter`) — OpenMRS / OpenEMR / HAPI FHIR / SMART / omiiCARE
- **Data:** PHI-safe generators (`core.generators`, Datafaker)
- **Domain validation:** FHIR assertions (`fhir/`), HL7 v2 (`hl7/`), DB testing (`db/`)
- **Reporting:** Allure + Extent · **A11y:** axe via Playwright · **Env management** built in

### 5.2 Node Playwright Smoke Suites — `automation/playwright/`
- omiiCARE smoke — `tests/` — **5/5 PASS** (headed)
- OpenMRS smoke — `tests-openmrs/` — **5/5 PASS** (headed vs `o2.openmrs.org`): launch / login / nav / register-patient CRUD / logout
- Artifacts: `artifacts-openmrs/screenshots/step-1..5`, `trace.zip`, HTML report (`playwright-report-openmrs/`)

### 5.3 How to Run
```bash
# Default unit suite — 98 tests PASS
mvn -pl automation test

# Tagged SUT/browser E2E (ui-e2e / api-e2e / bdd)
mvn -pl automation -Pe2e test

# Node smoke — omiiCARE
cd automation/playwright && npx playwright test

# Node smoke — OpenMRS reference SUT
cd automation/playwright && npx playwright test --config=playwright.openmrs.config.ts
```

---

## 6. Quality Engineering Assets
`quality/` — see [`quality/README.md`](../quality/README.md).

| Area | Path | Key assets |
|------|------|-----------|
| Performance | [`quality/performance/`](../quality/performance/) | k6 (`login-load.js`, `appointment-stress.js`, `openmrs-smoke.js`), JMeter (`omiicare-load-test.jmx`, `openmrs-plan.jmx`), Gatling |
| Security | [`quality/security/`](../quality/security/) | ZAP baseline (`zap/run-zap-baseline.sh`, configs), `OWASP_TOP10_MAPPING.md`, dependency-check, `SECURITY_TEST_CASES.md` |
| Accessibility | [`quality/accessibility/`](../quality/accessibility/) | axe/Playwright specs, Lighthouse config |
| Visual | [`quality/visual/`](../quality/visual/) | Playwright visual regression |
| Contract | [`quality/contract-testing/`](../quality/contract-testing/) | JSON schemas (FHIR patient, response envelope) |
| Database | [`quality/database-testing/`](../quality/database-testing/) | data-integrity SQL & cases |
| Chaos / Resilience | [`quality/chaos/`](../quality/chaos/) · [`quality/resilience/`](../quality/resilience/) | experiments |
| Observability | [`quality/observability/`](../quality/observability/) | monitoring assets |
| Compliance | [`quality/compliance/`](../quality/compliance/) | FHIR/HL7, HIPAA-like checklist, WCAG/OWASP baseline |

> **Hard rule:** performance & security tests run **only on owned/local environments**.
> Never load or actively attack `o2.openmrs.org` (ZAP against the reference SUT is
> passive baseline only).

---

## 7. AI-Native Quality Engineering
`ai/` — see [`ai/README.md`](../ai/README.md).

| Asset | Path |
|-------|------|
| Coverage-gap analysis (Python, std-lib) | [`ai/qe/coverage_gap_analysis.py`](../ai/qe/coverage_gap_analysis.py) |
| Prompt evaluation | [`ai/evaluation/PROMPT_EVALUATION.md`](../ai/evaluation/PROMPT_EVALUATION.md) |
| Agents / providers (scaffolded, **planned**) | `ai/agents/` · `ai/providers/` |
| Knowledge / templates / reporting | `ai/knowledge/` · `ai/templates/` · `ai/reporting/` |
| Repo-level AI guides | [`docs/AI_QUALITY_ENGINEERING.md`](./AI_QUALITY_ENGINEERING.md) · [`docs/AI_DEVELOPMENT_WORKFLOW.md`](./AI_DEVELOPMENT_WORKFLOW.md) · [`docs/AI_DEVELOPMENT_RULES.md`](./AI_DEVELOPMENT_RULES.md) |

---

## 8. CI/CD & Infrastructure

### 8.1 GitHub Actions — `.github/workflows/`
| Workflow | Purpose |
|----------|---------|
| [`qa-ci.yml`](../.github/workflows/qa-ci.yml) | PR / push CI gate |
| [`qa-nightly-e2e.yml`](../.github/workflows/qa-nightly-e2e.yml) | Nightly E2E |
| [`qa-quality-gate.yml`](../.github/workflows/qa-quality-gate.yml) | SonarQube / quality gate |
| [`qa-release.yml`](../.github/workflows/qa-release.yml) | Release automation |
| [`ci.yml`](../.github/workflows/ci.yml) · [`nightly.yml`](../.github/workflows/nightly.yml) · [`release.yml`](../.github/workflows/release.yml) | omiiCARE app CI/nightly/release |
| [`codeql.yml`](../.github/workflows/codeql.yml) · [`dependency-review.yml`](../.github/workflows/dependency-review.yml) | Security scanning |
| `_reusable-*.yml` | Reusable backend/frontend/docker/quality jobs |

Reference: [`docs/CI_CD_GUIDE.md`](./CI_CD_GUIDE.md) · [`docs/QUALITY_GATES.md`](./QUALITY_GATES.md)

### 8.2 Infrastructure & Scripts
- Docker Compose stack: [`infrastructure/docker/docker-compose.yml`](../infrastructure/docker/docker-compose.yml) — Postgres / Redis / MailHog / MinIO / Keycloak / WireMock / Prometheus / Grafana / SonarQube.
- Monitoring: [`infrastructure/monitoring/`](../infrastructure/monitoring/)
- Scripts: [`scripts/`](../scripts/) — `setup` · `start` · `stop` · `health-check` (`.sh`/`.bat`), `validate_docs.py`.
- Deployment & environments: [`docs/DEPLOYMENT_GUIDE.md`](./DEPLOYMENT_GUIDE.md) · [`docs/ENVIRONMENT_GUIDE.md`](./ENVIRONMENT_GUIDE.md)

---

## 9. Portfolio & Interview
- [`docs/PORTFOLIO_GUIDE.md`](./PORTFOLIO_GUIDE.md) · [`docs/portfolio/PORTFOLIO_GUIDE.md`](./portfolio/PORTFOLIO_GUIDE.md)
- [`docs/INTERVIEW_GUIDE.md`](./INTERVIEW_GUIDE.md) · [`docs/DEMO_GUIDE.md`](./DEMO_GUIDE.md)
- [`docs/LEARNING_ROADMAP.md`](./LEARNING_ROADMAP.md) · [`docs/FEATURE_MATRIX.md`](./FEATURE_MATRIX.md) · [`docs/TECHNOLOGY_MATRIX.md`](./TECHNOLOGY_MATRIX.md)
- [`docs/OPEN_SOURCE_READINESS.md`](./OPEN_SOURCE_READINESS.md)

---

## 10. Governance, Standards & Release Engineering
| Topic | Doc |
|-------|-----|
| Versioning (SemVer 2.0.0) | [`VERSIONING.md`](../VERSIONING.md) · [`docs/API_VERSIONING_POLICY.md`](./API_VERSIONING_POLICY.md) |
| Branching / commits | [`docs/BRANCHING_STRATEGY.md`](./BRANCHING_STRATEGY.md) · [`CONTRIBUTING.md`](../CONTRIBUTING.md) |
| Coding standards / patterns | [`docs/CODING_STANDARDS.md`](./CODING_STANDARDS.md) · [`docs/DESIGN_PATTERNS.md`](./DESIGN_PATTERNS.md) |
| Definition of Done | [`docs/DEFINITION_OF_DONE.md`](./DEFINITION_OF_DONE.md) |
| Security policy | [`SECURITY.md`](../SECURITY.md) · [`docs/SECURITY_TESTING_GUIDE.md`](./SECURITY_TESTING_GUIDE.md) |
| Test pyramid / strategy | [`docs/TEST_PYRAMID.md`](./TEST_PYRAMID.md) · [`docs/TEST_STRATEGY.md`](./TEST_STRATEGY.md) |
| Risk analysis | [`docs/RISK_ANALYSIS.md`](./RISK_ANALYSIS.md) |
| Known issues | [`docs/KNOWN_ISSUES.md`](./KNOWN_ISSUES.md) |
| Release process | [`docs/RELEASE_READINESS_1.0.0.md`](./RELEASE_READINESS_1.0.0.md) · [`RELEASE_NOTES.md`](../RELEASE_NOTES.md) · [`CHANGELOG.md`](../CHANGELOG.md) · [`ROADMAP.md`](../ROADMAP.md) |
| Community | [`CODE_OF_CONDUCT.md`](../CODE_OF_CONDUCT.md) · [`CODEOWNERS`](../CODEOWNERS) · [`CONTRIBUTORS.md`](../CONTRIBUTORS.md) · [`LICENSE`](../LICENSE) |

---

*Generated for v1.0.0 · 2026-07-01. All data is synthetic / PHI-safe. Items marked
**(planned)** are future-state. Performance & security tests run on owned/local
environments only.*
