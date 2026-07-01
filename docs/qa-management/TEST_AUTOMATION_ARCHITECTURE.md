# Layered Enterprise Test Automation Architecture (Permanent Standard)

> **Status:** Permanent project standard. Every new Page, Component, Workflow, Service, Assertion,
> Builder, Factory, Utility and Test **must** conform to the layers and dependency rules below.
> Primary SUT: **OpenMRS Reference Application** (https://o2.openmrs.org). Additional healthcare
> systems are supported through the **Resource Adapter Layer** without changing the core.

Module: `automation/` (Java 21, Maven). Default build `mvn -pl automation test` runs the framework
unit tests (currently **109 green**); SUT/browser/BDD tests are tagged (`ui-e2e`, `api-e2e`, `bdd`)
and run via `mvn -pl automation -Pe2e test`.

## 1. Layer stack & dependency flow

Dependencies point **downward only**. A layer may use the layers below it, never above.

```
Tests  (Smoke·Sanity·Regression·Integration·API·Database·Accessibility·Security·Performance·BDD)
  │  only: workflow calls + assertion calls + data-factory calls
  ▼
Business Workflow Layer   openmrs/workflow/*      (business processes; the ONLY place for business logic)
  ▼
Page Object Layer         ui/openmrs/*, selenium/pages/*   (one page = locators + interactions)
  ▼
Page Component Layer      openmrs/component/*     (reusable UI regions: Header, Nav, Table, Dialog, Form, Search, Toast, Pagination)
  ▼
Service Layer             openmrs/service/*, api/rest/*, fhir/*   (REST/FHIR/Auth/DB; no UI dependency)
  ▼
Assertion Layer           openmrs/assertion/*, fhir/*, db/*, a11y/*   (all verification; never inside pages)
  ▼
Data Layer                data/model/*, data/builder/*, data/*   (Factory + Builder; no hardcoded data)
  ▼
Core Layer                core/config, core/adapter, core/env, core/generators, reporting/*   (config, drivers, browser factory, env, adapters, reporting)
  ▼
Utilities                 utils/*                 (generic reusable helpers; no business/OpenMRS specifics)
```

## 2. Layer Responsibility Matrix

| Layer | Package(s) | Contains | MUST NOT contain |
|---|---|---|---|
| **Test** | `openmrs/tests`, `api`, `bdd`, framework tests | scenarios, workflow calls, assertion calls, data-factory calls | locators, business logic, API impl, test-data construction, complex navigation |
| **Workflow** | `openmrs/workflow` | complete business processes (login, register patient, search, navigate) | locators, raw assertions, tool boilerplate |
| **Page Object** | `ui/openmrs`, `selenium/pages` | one page's locators + UI methods | assertions, workflows, cross-page logic, data generation |
| **Component** | `openmrs/component` | reusable UI regions (Header, Navigation, DataTable, Dialog, Form, Search, Toast, Pagination) | assertions, business workflow |
| **Service** | `openmrs/service`, `api/rest`, `fhir` | REST/FHIR/Auth/DB clients & facades | UI dependencies |
| **Assertion** | `openmrs/assertion`, `fhir`, `db`, `a11y` | UI/API/DB/FHIR/A11y assertions | UI interactions, navigation |
| **Data** | `data`, `data/model`, `data/builder` | Factory + Builder, models (Patient/Appointment/FHIR) | hardcoded test data, UI/service calls |
| **Core** | `core/config`, `core/adapter`, `core/env`, `core/generators`, `reporting` | config, driver/browser lifecycle, environment, adapters, reporting, base classes | test scenarios, page locators |
| **Utilities** | `utils` | generic helpers (Date, File, Screenshot, Wait, Random, String) | business logic, OpenMRS specifics |

## 3. Folder Structure (`automation/src/test/java/com/omiicare/qa/automation/`)

```
utils/                 WaitUtils, DateUtils, StringUtils, RandomDataUtils, FileUtils, ScreenshotUtils
data/                  TestDataFactory
data/model/            PatientData, AppointmentData
data/builder/          PatientBuilder, AppointmentBuilder, FhirPatientBuilder
core/config/           FrameworkConfig, TargetSystem
core/adapter/          ResourceAdapter, AdapterFactory, AdapterRegistry, *Adapter        (Adapter pattern)
core/env/              EnvironmentManager, SutTarget, MultiEnvConfig
core/generators/       PatientFactory, ProviderFactory, AppointmentFactory, OrderFactory  (Factory pattern)
reporting/             ExtentReportManager, ExtentJUnitListener, AllureEnvironmentWriter
openmrs/service/       AuthenticationService, OpenMrsPatientService, FhirService           (Facade)
api/rest/              OpenMrsRestClient, BaseApiClient, ApiConfig, *Summary
fhir/                  FhirClient, FhirAssertions
db/                    JdbcRunner, DbAssertions, QueryBuilder                              (Builder)
a11y/                  AxeRunner, A11yAssertions
openmrs/component/     BaseComponent, HeaderComponent, NavigationComponent, DataTableComponent,
                       DialogComponent, SearchComponent, ToastComponent, FormComponent, PaginationComponent
ui/openmrs/            PlaywrightFactory, PwBasePage, Openmrs{Login,Home,Registration,FindPatient}Page
selenium/…             WebDriverFactory, BasePage, Login/Home/Registration/FindPatient pages
openmrs/assertion/     UiAssertions, LoginAssertions, PatientAssertions, ApiAssertions, OpenMrsAssertions (Facade)
openmrs/workflow/      BaseWorkflow, AuthenticationWorkflow, PatientRegistrationWorkflow,
                       PatientSearchWorkflow, NavigationWorkflow
openmrs/tests/         LayeredSmokeE2ETest (reference example)
bdd/                   Cucumber features + steps + runners
```

## 4. Design Pattern Usage Report

| Pattern | Where | Purpose |
|---|---|---|
| **Page Object Model** | `ui/openmrs/*`, `selenium/pages/*` | one page = locators + interactions |
| **Page Component Model** | `openmrs/component/*` | reusable UI regions shared across pages |
| **Workflow (Facade over pages)** | `openmrs/workflow/*` | business processes; tests stay declarative |
| **Factory** | `core/generators/*`, `data/TestDataFactory` | create synthetic domain objects |
| **Builder** | `data/builder/*` (Patient/Appointment/FhirPatient), `db/QueryBuilder`, `hl7/Hl7MessageBuilder` | fluent, validated object construction |
| **Adapter** | `core/adapter/*` | swap SUTs (OpenMRS/OpenEMR/HAPI/omiiCARE) without touching core |
| **Strategy** | `core/env` env resolution, browser channel/headless selection, `TargetSystem` | pluggable runtime behaviour |
| **Facade** | `openmrs/service/*`, `openmrs/assertion/OpenMrsAssertions` | single simple entry point over subsystems |

## 5. SOLID Compliance Report

- **S — Single Responsibility:** each class owns one concern (a page = one page; a component = one UI region; a workflow = one process; an assertion class = one verification family).
- **O — Open/Closed:** new SUTs via new `ResourceAdapter` implementations; new data via new `Builder`s — no edits to core.
- **L — Liskov:** page objects substitute for `PwBasePage`; components for `BaseComponent`; workflows for `BaseWorkflow`.
- **I — Interface Segregation:** thin, intention-revealing service facades expose only what callers need (`AuthenticationService`, `FhirService`).
- **D — Dependency Inversion:** tests depend on workflow/assertion abstractions, not on Playwright/RestAssured internals; config flows through `FrameworkConfig`.
- **DRY / KISS / YAGNI / Composition-over-Inheritance:** shared UI in components (no duplicate locators); workflows compose pages+components+services rather than deep inheritance.

## 6. Development Guidelines ("search before you create")

Before adding any Page / Component / Workflow / Service / Utility / Assertion / Builder / Factory / Test,
**search the repo and reuse** the existing implementation. Then:

1. **Tests** call **workflows** + **assertions** + **data factories** only. No locators, no `if/else` business logic.
2. **Business logic** lives **only** in **workflows**.
3. **Locators** live **only** in **page objects / components**.
4. **Assertions** live **only** in the **assertion layer** — never in pages or components.
5. **Test data** comes from **builders/factories** — never hardcoded in tests.
6. Dependencies flow **downward only** (see §1).
7. SUT/browser/DB/network tests are **tagged** (`ui-e2e` / `api-e2e` / `bdd`) so the default build stays fast & green.

## 7. Correct-usage example (reference)

```java
// Test layer — declarative: workflow + assertion + data factory only.
@Tag("ui-e2e")
class LayeredSmokeE2ETest {
  @Test void canRegisterAPatientThroughTheWorkflow() {
    new AuthenticationWorkflow(page).loginAsAdmin();              // Workflow
    PatientData patient = TestDataFactory.randomPatient();        // Data (Factory + Builder)
    new PatientRegistrationWorkflow(page).registerPatient(patient); // Workflow -> Pages/Components
    OpenMrsAssertions.patient(page).wasRegistered(patient.familyName()); // Assertion
  }
}
```

See `automation/src/test/java/com/omiicare/qa/automation/openmrs/tests/LayeredSmokeE2ETest.java`.

## 8. Refactoring Summary (this milestone)

- **Added** the missing enterprise layers: Business Workflow (5), Page Component (9), unified
  Assertion facade + UI/Login/Patient/Api assertions (5), Data models + fluent Builders + factory (6),
  Service facades (3), generic Utilities (6), and a reference layered E2E test.
- **Refactored** `OpenmrsRegistrationPage` to the verified OpenMRS wizard selectors and full
  Demographics→Contact→Confirm step methods (kept all existing method signatures — no breaking change).
- **Preserved** the existing core (config/adapter/env/generators/reporting) and page/service classes;
  new layers **reuse** them (composition, not replacement).
- **Verified:** `mvn -pl automation test` → BUILD SUCCESS, **109 tests pass** (was 98; +11 Data/Utils unit tests).

## 9. Technical-Debt Report (automation)

- Page objects and components are **assertion-free** (verified). Business logic is confined to workflows.
- No duplicate component implementations (shared UI centralised in `openmrs/component/*`).
- **Follow-ups (tracked, non-blocking):** consolidate the Selenium and Playwright page-object families
  behind a common UI-driver strategy if Selenium coverage grows; expand workflows/components as new
  OpenMRS modules (Visit, Encounter, Laboratory, Pharmacy, Administration) are automated.

## 10. Enterprise Readiness Assessment

**Ready for large-scale OpenMRS automation.** The layered separation (tests ↔ workflows ↔ pages ↔
components ↔ services ↔ assertions ↔ data ↔ core ↔ utils), the eight design patterns, and the
adapter-based multi-SUT core mean the suite can grow to thousands of OpenMRS tests while remaining
clean, modular, reusable and maintainable. New scenarios are authored as thin, business-readable
tests over reusable workflows/components/builders — the maintenance surface stays flat.
