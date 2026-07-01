# OmiiCare QA Automation Framework

Enterprise, multi-system test-automation platform for healthcare applications.
It validates UI, REST, and FHIR R4 behaviour against the **OpenMRS Reference
Application** today and against any future target through a pluggable
**Resource Adapter Layer**.

> Reference target: OpenMRS RefApp (`o2.openmrs.org`) — login
> (`#username` / `#password` / `#loginButton` + a session-location `<li id="…">`),
> home tiles (`a[href*=registerPatient.page]`,
> `a[href*=app=coreapps.findPatient]`), FHIR
> `GET /ws/fhir2/R4/metadata` → `CapabilityStatement` (`fhirVersion 4.0.1`),
> and REST under `/ws/rest/v1`. Designed for multi-system via the adapter layer.

---

## 1. Goals & Principles

- **Multi-system by design** — tests target an *interface*, not a server. Adding
  a new SUT means writing one adapter, not rewriting tests.
- **Zero hardcoding** — every endpoint, credential, and environment flows
  through `FrameworkConfig` (system property → env var → `framework.properties`).
- **Safe-by-default builds** — anything that needs a live SUT, browser, network,
  or DB is **tagged** and excluded from `mvn test`. The default build runs only
  pure-logic unit checks.
- **Polyglot reporting** — Allure + Extent for Java; Playwright HTML for the
  Node UI suites.
- **Parallel-first** — JUnit 5 parallel execution and a TestNG parallel suite.

---

## 2. Technology Stack

| Layer            | Tooling |
|------------------|---------|
| Language / build | Java 21, Maven (module `automation`, artifact `omiicare-qa-automation`) |
| Unit / runner    | JUnit 5 (Jupiter + Platform Suite), TestNG (parallel suites) |
| Assertions       | AssertJ |
| REST / FHIR      | RestAssured (`io.restassured`), JSON-schema validator |
| UI (Java)        | Selenium (Selenium Manager — no driver paths), Playwright for Java |
| UI (Node)        | `@playwright/test` (TypeScript) |
| BDD              | Cucumber (`cucumber-java`, JUnit Platform engine) |
| Test data        | Datafaker |
| Async            | Awaitility |
| Logging          | SLF4J + Logback |
| Reporting        | Allure (junit5 / testng / cucumber7), Extent, Playwright HTML |

---

## 3. Module Layout

```
automation/
├── pom.xml                         # module build, surefire tag exclusions, -Pe2e profile
├── AUTOMATION_FRAMEWORK.md         # this document
├── src/test/java/com/omiicare/qa/automation/
│   ├── core/
│   │   ├── config/                 # FrameworkConfig, TargetSystem
│   │   └── adapter/                # ResourceAdapter, AdapterFactory, HttpResourceAdapter, per-system adapters
│   ├── api/                        # REST tests (@Tag("api-e2e"))
│   ├── fhir/                       # FHIR R4 tests (@Tag("api-e2e"))
│   ├── hl7/                        # HL7 v2 parsing (mostly pure-logic, untagged)
│   ├── ui/                         # Playwright-Java UI tests (@Tag("ui-e2e"))
│   ├── selenium/                   # Selenium UI tests (@Tag("ui-e2e"))
│   ├── bdd/                        # Cucumber glue + runner (@Tag("bdd"))
│   ├── db/                         # DB-backed tests (tagged)
│   ├── a11y/                       # accessibility checks
│   ├── parallel/                   # parallel-execution support
│   ├── reporting/                  # Allure/Extent wiring
│   └── framework/                  # shared base classes / utilities
├── src/test/resources/
│   ├── config/framework.properties # default config values
│   └── testng-parallel.xml         # TestNG parallel suite (NOT run by default)
└── playwright/                     # standalone Node Playwright suites
    ├── package.json                # scripts: smoke, report
    ├── playwright.config.ts        # generic config
    ├── playwright.openmrs.config.ts# OpenMRS-targeted config
    ├── tests/  tests-openmrs/      # specs
    └── playwright-report*/         # HTML reports
```

---

## 4. Resource Adapter Layer

The heart of multi-system support. Tests depend on the `ResourceAdapter`
interface and obtain a concrete adapter from `AdapterFactory`/`AdapterRegistry`.

```
            ┌────────────────────────────────────────────┐
   Tests ──▶│           ResourceAdapter (interface)       │
            │   system() · baseUri() · url(path)          │
            └───────────────┬────────────────────────────┘
                            │ implementations
   ┌────────────────────────┼───────────────────────────────────┐
   │ HttpResourceAdapter (shared HTTP base)                      │
   │   OpenMrsResourceAdapter · OpenEmrResourceAdapter ·         │
   │   OmiiCareResourceAdapter · HapiFhirResourceAdapter · …     │
   └────────────────────────────────────────────────────────────┘
                            │ base URI resolved via
            ┌───────────────▼────────────────┐
            │ FrameworkConfig.baseUri(system) │  (sys-prop → env → properties)
            └────────────────────────────────┘
```

- **`ResourceAdapter`** — `system()`, `baseUri()`, and a default
  `url(path)` that safely joins base + path.
- **`TargetSystem`** — enum of supported systems; each carries its
  `baseUriProperty()` and `defaultBaseUri()`.
- **`AdapterFactory` / `AdapterRegistry`** — resolve the active adapter from the
  configured target system.
- **Adding a target** — implement `ResourceAdapter` (or extend
  `HttpResourceAdapter`), register it, and add a `TargetSystem` entry. **No test
  changes required.**

> When extending these classes, read the real source first
> (`src/test/java/com/omiicare/qa/automation/core/...`) to match current
> signatures — do not assume.

---

## 5. Configuration

`FrameworkConfig` (singleton, `FrameworkConfig.get()`) resolves every value with
this precedence (highest first):

1. JVM system property — `-Domii.env=qa`
2. Environment variable — `OMII_ENV=qa`
3. Bundled `config/framework.properties`

Common keys:

| Key (sys-prop)        | Env var                | Purpose                         |
|-----------------------|------------------------|---------------------------------|
| `omii.env`            | `OMII_ENV`             | active environment name         |
| `<system>.base.uri`   | `<SYSTEM>_BASE_URI`    | per-target base URI override    |
| arbitrary             | uppercased + `_`       | `get(key, default)` lookups     |

```java
String env  = FrameworkConfig.get().environment();
String base = FrameworkConfig.get().baseUri(TargetSystem.OPENMRS);
String user = FrameworkConfig.get().get("openmrs.username", "admin");
```

Nothing is hardcoded in a test — endpoints and credentials always come from
config.

---

## 6. Test Tagging & Safe Builds (important)

The default `mvn test` excludes anything that needs a live system. Surefire is
configured with:

```
<excluded.test.tags>api-e2e,ui-e2e,bdd</excluded.test.tags>
```

| Tag (`@org.junit.jupiter.api.Tag`) | Use for                                   |
|------------------------------------|-------------------------------------------|
| `ui-e2e`                           | browser / Selenium / Playwright-Java tests |
| `api-e2e`                          | REST / FHIR / network tests               |
| `bdd`                              | Cucumber scenarios                        |

Rules:

- A test that needs a **SUT, browser, network, or DB** → **must be tagged** so
  it is excluded by default.
- Only **untagged** tests are pure-logic, deterministic, SUT-free, and **must
  pass** (builders, parsers, config, HL7 string parsing).
- **TestNG** SUT suites live in `testng-parallel.xml` and are **not** wired into
  the default build.
- When in doubt — **tag it**. Never let a SUT-dependent test run in `mvn test`.

---

## 7. How to Run

### 7.1 Default unit build (no SUT, safe, always green)

```bash
mvn -pl automation test
```

Runs only untagged pure-logic tests.

### 7.2 End-to-end (UI + API + FHIR + BDD) — requires a reachable SUT

The `e2e` profile clears the tag exclusions and re-includes the Cucumber suite:

```bash
mvn -pl automation -Pe2e test \
  -Domii.env=local \
  -Domii.openmrs.base.uri=http://localhost:8080/openmrs
```

Run a single tag set by combining the profile with a JUnit tag filter:

```bash
mvn -pl automation -Pe2e test -Dgroups=api-e2e          # API/FHIR only
mvn -pl automation -Pe2e test -Dgroups=ui-e2e           # UI only
```

### 7.3 TestNG parallel suite (SUT required)

```bash
mvn -pl automation -Pe2e test -Dsurefire.suiteXmlFiles=src/test/resources/testng-parallel.xml
```

### 7.4 Node Playwright suites

```bash
cd automation/playwright
npm ci
npx playwright install --with-deps

# generic config
npm run smoke
# OpenMRS-targeted config
npx playwright test --config playwright.openmrs.config.ts
npm run report          # open the HTML report
```

---

## 8. Parallel Execution

- **JUnit 5** — parallelism via `junit-platform.properties`
  (`junit.jupiter.execution.parallel.enabled=true`); see the `parallel` package
  for coordination helpers.
- **TestNG** — `src/test/resources/testng-parallel.xml` declares thread-count /
  parallel mode for SUT suites (opt-in, not default).
- **Playwright (Node)** — workers configured in `playwright.config.ts`.
- **Isolation** — tests build their own data (Datafaker) and resolve their own
  adapter, so concurrent runs don't share mutable state.

---

## 9. Reporting

| Suite              | Reporter        | Output                                   |
|--------------------|-----------------|------------------------------------------|
| JUnit 5 (Java)     | Allure junit5   | `automation/allure-results/` → `allure serve` |
| TestNG (Java)      | Allure testng   | `automation/allure-results/`             |
| Cucumber           | Allure cucumber7| `automation/allure-results/`             |
| Java (rich HTML)   | Extent          | configured under `reporting/`            |
| Node UI            | Playwright HTML | `automation/playwright/playwright-report*/` |

```bash
# Allure (after an e2e run that produced results)
allure serve automation/allure-results
```

---

## 10. Continuous Integration

Recommended pipeline stages:

1. **Build & unit gate** (every push / PR) — `mvn -pl automation test`. Fast,
   no SUT, must stay green.
2. **E2E** (against an ephemeral / owned environment) —
   `mvn -pl automation -Pe2e test` with `-Domii.*` overrides pointing at the
   spun-up SUT; publish Allure results as an artifact.
3. **Node UI** — `npm ci && npx playwright test`; upload `playwright-report*`.
4. **Quality gates (owned env only)** — perf (k6 / JMeter) and security
   (OWASP ZAP baseline) jobs; see §11.

CI tips: cache `~/.m2` and `node_modules`; let Selenium Manager fetch drivers;
fail the unit stage on any tagged test accidentally running unscoped.

---

## 11. Performance & Security Assets — Owned Environments ONLY

Load and security tooling lives under `quality/`:

| Asset | Path | Run against |
|-------|------|-------------|
| k6 smoke / light load | `quality/performance/k6/openmrs-smoke.js` | **owned/local only** |
| JMeter smoke plan     | `quality/performance/jmeter/openmrs-plan.jmx` | **owned/local only** |
| ZAP baseline config   | `quality/security/zap/zap-baseline-openmrs.conf` | **owned/local only** |
| ZAP baseline guide    | `quality/security/zap/README-openmrs-baseline.md` | — |

> **Critical:** Performance and security runs generate aggressive traffic and
> can be treated as attacks. Execute them **ONLY** against an OpenMRS instance
> you **own** or are explicitly authorized to test (local Docker / dedicated
> QA host). **Never** target `o2.openmrs.org`, any shared community server, a
> third-party site, or production. Each asset's header repeats this rule and
> defaults to `localhost`.

---

## 12. Extending the Framework

| To add…                | Do this |
|------------------------|---------|
| A new target system    | Implement `ResourceAdapter` (or extend `HttpResourceAdapter`), register it, add a `TargetSystem` enum entry. No test changes. |
| A new API/FHIR test    | Place under `api/` or `fhir/`, obtain the adapter from `AdapterFactory`, **tag `@Tag("api-e2e")`**. |
| A new UI test          | `ui/` (Playwright-Java) or `selenium/`, **tag `@Tag("ui-e2e")`**. |
| A new BDD scenario     | Add a feature + glue under `bdd/`, **tag `@Tag("bdd")`**. |
| A pure-logic unit test | Anywhere; leave untagged **only** if it needs no SUT and always passes. |

---

## 13. Quick Reference

```bash
# Safe unit build (default, no SUT)
mvn -pl automation test

# Full e2e against a local OpenMRS
mvn -pl automation -Pe2e test -Domii.env=local

# Node Playwright (OpenMRS)
cd automation/playwright && npm ci && npx playwright test --config playwright.openmrs.config.ts

# Reports
allure serve automation/allure-results

# Perf / security — OWNED ENV ONLY
k6 run quality/performance/k6/openmrs-smoke.js
jmeter -n -t quality/performance/jmeter/openmrs-plan.jmx -l results.jtl -e -o html-report
```
