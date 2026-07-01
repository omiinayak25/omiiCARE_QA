# Project Policy — OpenMRS Enterprise Quality Engineering Platform

> **Status:** Permanent, standing policy. This document governs the purpose, scope, and
> working rules of this repository. It supersedes any earlier assumption that the in-house
> `omiiCARE` application is the primary target.

## 1. Mission

This repository is an **Enterprise Healthcare Quality Engineering Platform**. Its objective is
to demonstrate world-class **Software Testing, Test Automation, Quality Engineering, and
Healthcare QA** expertise — at the level expected of a Senior QA Engineer / Senior Automation
Engineer / SDET II–III / QA Lead / QA Architect / Principal QA Engineer.

It is **NOT** intended to build a custom healthcare application. The focus is enterprise-quality
**testing**, not product development.

## 2. Primary System Under Test (SUT)

The **permanent primary application under test is the OpenMRS Reference Application**:

- **URL:** https://o2.openmrs.org (legacy O2 Reference Application UI — the automation-friendly
  demo; `demo.openmrs.org` redirects to the newer O3 microfrontend UI at `o3.openmrs.org`)
- **REST API:** `/openmrs/ws/rest/v1/*` · **FHIR R4:** `/openmrs/ws/fhir2/R4` (CapabilityStatement `fhirVersion 4.0.1`)
- **Demo credentials:** `admin` / `Admin123`, after selecting a session **location** (e.g. Registration Desk)

Treat OpenMRS exactly as the **Senior QA Engineer responsible for certifying enterprise releases
of OpenMRS**. Unless another application is explicitly named in a request, **every activity
targets OpenMRS** — analysis, reverse-engineering, documentation, test planning/strategy, manual
testing, automation, API/DB testing, FHIR/HL7 validation, accessibility, security analysis,
visual, performance planning, risk analysis, traceability, defect analysis, reporting, and
AI-assisted QE.

The in-house `omiiCARE` app (Spring Boot backend + React frontend) remains only as a **secondary /
example SUT**, reachable — like OpenEMR, HAPI FHIR and SMART Health IT — through the
**Resource Adapter Layer**. It is never the default.

### Default-target rule
Unless a request explicitly specifies another application, assume it refers to OpenMRS
(*"Generate test cases" → OpenMRS test cases*, *"Create automation" → OpenMRS automation*,
*"Generate RTM/SRS" → for OpenMRS*, etc.). **Never ask which application to use** unless another
is explicitly mentioned.

## 3. Reverse-Engineering Policy

Before creating any test asset, **continue learning OpenMRS**: repeatedly explore every module,
page, workflow, form, button, menu, report, search, API, validation, permission, role,
navigation path, business rule, and healthcare workflow — until no additional functionality can
be discovered. Documentation improves continuously as new knowledge is gained.

## 4. Documentation Policy

Maintain and continuously update the enterprise documentation set (already established under
[`docs/reverse-engineering/`](reverse-engineering/) and [`docs/qa-management/`](qa-management/)):
BRD, SRS, FRD, NFR, Business Rules, RTM, Test Strategy, Master Test Plan, Risk Register, RBAC
Matrix, Field Dictionary, Data Dictionary, API/FHIR/HL7 documentation, Validation Matrix,
Navigation Map, Workflow & Architecture documentation, and Healthcare Domain documentation.
**When new functionality is discovered, update documentation immediately — it must never become
outdated.**

## 5. Test-Case Strategy

Enterprise-quality, **traceable** test cases only — no random or duplicate tests; every case maps
to a documented requirement ([`docs/requirements/requirements-catalog.md`](requirements/requirements-catalog.md)
· [`manual-testing/rtm/`](../manual-testing/rtm/)). Cover all disciplines: Functional, Smoke,
Sanity, Regression, System, Integration, E2E, API, Database, FHIR, HL7, Negative, Boundary,
Equivalence Partitioning, Decision Table, State Transition, Pairwise, Exploratory, Session-Based,
Monkey, Gorilla, Recovery, Reliability, Accessibility, Security, Performance Planning, Visual,
Compatibility, Localization, Usability, Audit, Logging, Monitoring, Healthcare Workflow, Patient
Safety, Clinical Validation, Insurance, Billing, Laboratory, Radiology, Prescription, Medical
Records, Notifications, and Role-Based Access. **Quality over quantity.**

## 6. Automation Policy

Reusable, modular, enterprise-grade architecture using Playwright, Selenium, Rest Assured, JUnit,
TestNG, Cucumber/BDD, database validation, accessibility automation, visual regression, API
automation, and FHIR/HL7 validation (see [`automation/AUTOMATION_FRAMEWORK.md`](../automation/AUTOMATION_FRAMEWORK.md)).

## 7. Public-Demo Rules

The OpenMRS public demo may **reset periodically**, contain **shared test data**, return **expected
`401`/`404`** responses, and have **variable latency**. Do **not** classify these as application
defects. Always separate findings into: **Product defects**, **Test defects**, **Environment
limitations**, and **Public-demo characteristics**.

## 8. Ethical Testing (hard rule)

**Never** execute **load, stress, spike, soak, active security attacks, or destructive testing**
against the public OpenMRS demo. Such activities target **only owned or explicitly authorized**
environments (e.g. a locally-run OpenMRS/omiiCARE stack). Against the public demo: functional
smoke, design-only performance/security *cases*, and passive checks only.

## 9. Repository Workflow

Two permanent branches (`main` = production-ready, `develop` = active development); feature
branches are short-lived and deleted after merge (see [BRANCHING_STRATEGY.md](BRANCHING_STRATEGY.md)).
After each milestone: review → ensure the project builds → commit to `develop` → push `develop`.
No pull requests unless explicitly requested. Dependabot opens dependency PRs against `develop`.

## 10. Long-Term Goal

Become one of the world's most comprehensive **public Healthcare Quality Engineering** repositories
centered on OpenMRS. Every enhancement should strengthen testing depth, quality-engineering
practices, documentation quality, and healthcare-domain expertise.
