# omiiCARE_QA — Portfolio Guide

> A single-page tour of what this repository demonstrates end-to-end, for hiring managers,
> tech leads, and OSS reviewers. Budget **10 minutes** to skim; **45 minutes** for the deep dive.

**Repository:** `omiiCARE_QA` — enterprise healthcare QA platform
**Release:** v1.0.0 (SemVer 2.0.0, Conventional Commits)
**Primary SUT under test:** OpenMRS (https://o2.openmrs.org) · **Local SUT:** omiiCARE (Spring Boot 3 / Java 21 + React/Vite)
**Portability:** OpenMRS · OpenEMR · HAPI FHIR · SMART · omiiCARE via a **Resource Adapter Layer**

---

## 1. What this repository proves (the one-paragraph version)

This is not a toy test project. It is a full QA engineering operating system for a regulated
(healthcare) domain: requirements were **reverse-engineered** from a running system into a
formal spec set, **4,187 manual test cases** were authored and traced to **1,795 requirements**
with **zero RTM gaps**, and a **multi-framework automation stack** (Playwright, Selenium,
RestAssured, Cucumber) exercises the same system across UI, API, FHIR, and HL7 v2 layers —
all wired into CI/CD with performance, security, and accessibility quality gates, plus an
emerging **AI Quality Engineering** module. The headline proof point: a **real, headed
end-to-end smoke suite passing 5/5 against the public OpenMRS demo**, captured with traces and
screenshots.

---

## 2. Headline metrics

| Metric | Value | Where to verify |
|---|---|---|
| Manual test cases | **4,187** across 66 modules | `manual-testing/test-cases/openmrs/` (17-col CSV + `ALL_TEST_CASES.csv`) |
| Requirements catalogued | **1,795** | `docs/requirements/requirements-catalog.md` |
| Requirements traceability | **0 gaps, 0 untraced** | `manual-testing/rtm/RTM.csv` · `RTM.md` |
| Reverse-engineering docs | **22 docs (~10k lines, 78 Mermaid diagrams)** | `docs/reverse-engineering/` |
| QA management docs | **15 docs** | `docs/qa-management/` |
| Automation unit tests | **98 PASS** (`mvn -pl automation test`) | `automation/` |
| OpenMRS headed E2E smoke | **5/5 PASS** vs o2.openmrs.org | `automation/playwright/tests-openmrs/` |
| omiiCARE headed smoke | **5/5 PASS** | `automation/playwright/tests/` |
| Modules covered (manual) | **66** | `manual-testing/test-cases/openmrs/` |

> All metrics above are present and verifiable in the repository today. Items elsewhere in this
> guide marked **(planned)** are future-state and not yet implemented.

---

## 3. Skills showcased

- **QA Architecture** — risk-based test strategy, master test plan, entry/exit criteria, estimation,
  defect lifecycle, metrics dashboards (`docs/qa-management/`, `docs/MASTER_TEST_PLAN.md`).
- **Requirements / Business Analysis** — reverse-engineering a live SUT into BRD/SRS/FRD/NFR,
  use cases, user stories, RBAC matrix, data & field dictionaries, validation matrix, ERD, and
  API/FHIR/HL7 mappings (`docs/reverse-engineering/`).
- **Test Design at scale** — 4,187 structured manual cases in a 17-column CSV schema, organized
  by 66 functional modules, fully traced (`manual-testing/`).
- **Multi-framework Automation** — Playwright (Java + Node), Selenium, RestAssured, Cucumber/BDD,
  JUnit 5, TestNG, all behind a **Resource Adapter Layer** (`core.adapter`) for SUT portability.
- **Healthcare interoperability testing** — FHIR resource assertions (`fhir/`), HL7 v2 message
  validation (`hl7/`).
- **Data engineering for QA** — PHI-safe synthetic generators (Datafaker, `core.generators`).
- **Non-functional QA** — k6 + JMeter performance, OWASP ZAP security baseline, axe/Playwright
  accessibility, visual regression (`quality/`).
- **DevOps / CI-CD** — Dockerized stack (Postgres, Redis, MailHog, MinIO, Keycloak, WireMock,
  Prometheus, Grafana, SonarQube) and GitHub Actions pipelines (`infrastructure/`, `.github/`).
- **AI Quality Engineering** — `ai/` module exploring AI-assisted test generation/triage.

---

## 4. How a reviewer should navigate the repo

A suggested reading order, fastest signal first:

1. **`README.md`** (root) — orientation and quick-start.
2. **This guide** — `docs/portfolio/PORTFOLIO_GUIDE.md`.
3. **Proof of execution** — open `automation/playwright/artifacts-openmrs/`:
   - `screenshots/step-1-login.png` … `step-5-logged-out.png` (the full headed E2E journey)
   - `trace.zip` (Playwright trace), `video/`, `diagnostics.json`
   - HTML report under `automation/playwright/playwright-report-openmrs/`
4. **Test design depth** — `manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv` and the
   per-module CSVs; then `manual-testing/rtm/RTM.md` to see traceability.
5. **Analytical rigor** — `docs/reverse-engineering/` (start with `README.md`, then `BRD.md` →
   `SRS.md` → `FRD.md` → `RBAC_MATRIX.md` → `FHIR_MAPPING.md` / `HL7_MAPPING.md`).
6. **QA process maturity** — `docs/qa-management/` (master strategy, risk-based plan, metrics).
7. **Automation internals** — `automation/` (Resource Adapter Layer, generators, fhir/, hl7/, db/).
8. **Non-functional & infra** — `quality/`, `infrastructure/docker/docker-compose.yml`, `scripts/`.

### Run it yourself

```bash
# 1. Automation unit tests (no SUT/browser needed) — expect 98 PASS
mvn -pl automation test

# 2. SUT/browser tests (tagged ui-e2e / api-e2e / bdd)
mvn -pl automation -Pe2e test

# 3. Node Playwright OpenMRS headed smoke — expect 5/5 PASS
#    (launch → login → nav → register-patient CRUD → logout)
cd automation/playwright && npx playwright test --config playwright.openmrs.config.ts --headed

# 4. omiiCARE local smoke — expect 5/5 PASS
cd automation/playwright && npx playwright test --headed
```

> **Hard rule respected:** performance and security tests run **only** against owned/local
> environments. The OpenMRS demo (o2.openmrs.org) is used for functional smoke testing **only**
> — it is never load-tested or attacked.

---

## 5. Standout artifacts (the "show me" list)

| Artifact | Why it stands out |
|---|---|
| **OpenMRS headed smoke 5/5** | Real CRUD journey on a public production-grade EMR, captured with trace + per-step screenshots. Proof, not promises. `automation/playwright/tests-openmrs/openmrs-smoke.spec.ts` |
| **98 automation unit tests** | The framework itself is tested — adapters, generators, assertions — so the harness is trustworthy. `mvn -pl automation test` |
| **22 reverse-engineering docs** | ~10k lines, 78 Mermaid diagrams: a full spec set rebuilt from a running system. `docs/reverse-engineering/` |
| **4,187 manual cases, 0 RTM gaps** | Scale + discipline: every case traces to a requirement, every requirement is covered. `manual-testing/`, `manual-testing/rtm/` |
| **Resource Adapter Layer** | One test suite, many EMRs (OpenMRS/OpenEMR/HAPI/SMART/omiiCARE). `automation` package `com.omiicare.qa.automation.core.adapter`. |
| **PHI-safe synthetic data** | Datafaker-based generators — zero real patient data, HIPAA-aware by design. `core.generators`. |
| **FHIR + HL7 v2 validation** | Interop tested at the wire format, not just the UI. `automation` `fhir/`, `hl7/`. |
| **Full Docker QA stack** | Keycloak, WireMock, Prometheus/Grafana, SonarQube — an enterprise environment in one compose file. `infrastructure/docker/docker-compose.yml` |

---

## 6. End-to-end flow this repo demonstrates

```
Live SUT (OpenMRS)
   │  reverse-engineer
   ▼
22 RE docs  ──►  1,795 requirements catalogue
   │                     │  derive
   ▼                     ▼
78 Mermaid        4,187 manual test cases (66 modules)
diagrams                 │  trace
                         ▼
                  RTM (0 gaps, 0 untraced)
                         │  automate
                         ▼
   Multi-framework automation (Playwright/Selenium/RestAssured/Cucumber)
   via Resource Adapter Layer  +  FHIR/HL7 validation  +  PHI-safe data
                         │  gate
                         ▼
   CI/CD  +  Performance (k6/JMeter, local-only)  +  Security (ZAP, local-only)
          +  Accessibility (axe)  +  Visual  +  AI QE module
                         │  prove
                         ▼
   Headed OpenMRS smoke 5/5  +  98 automation unit tests  (traces, screenshots, reports)
```

---

## 7. Tech stack at a glance

- **Languages/Runtimes:** Java 21, Maven 3.9, Node 22, Python 3 (std-lib tooling)
- **Backend SUT:** Spring Boot 3 (dev profile H2) · **Frontend SUT:** React + Vite
- **Automation:** Playwright (Java & Node), Selenium, RestAssured, Cucumber, JUnit 5, TestNG
- **Interop:** FHIR assertions, HL7 v2 validation
- **Reporting:** Allure + Extent (Java), Playwright HTML report (Node)
- **Non-functional:** k6, JMeter, OWASP ZAP, axe-core
- **Infra:** Docker Compose (Postgres, Redis, MailHog, MinIO, Keycloak, WireMock, Prometheus, Grafana, SonarQube)
- **Standards:** SemVer 2.0.0, Conventional Commits, synthetic/PHI-safe data only

---

## 8. Talking points for an interview

- "I reverse-engineered a running EMR into a complete, traceable spec set — then proved coverage
  with a passing headed E2E run against the real system."
- "The automation framework is itself unit-tested (98 tests), and abstracted behind an adapter
  layer so the same suite runs across five different healthcare platforms."
- "I treated non-functional and compliance concerns as first-class: ZAP/k6 gates that only ever
  touch owned environments, axe accessibility checks, and PHI-safe synthetic data throughout."

---

*See also:* `docs/INTERVIEW_GUIDE.md`, `docs/DEMO_GUIDE.md`, `docs/REPOSITORY_TOUR.md`,
`docs/OPEN_SOURCE_READINESS.md`.
