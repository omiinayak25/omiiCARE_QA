# omiiCARE_QA — Sample Reports & Executions

> A catalog of **real example executions and artifacts already committed in this
> repository**, with the exact paths and the commands to regenerate each. Every
> path below is verified present in the repo unless explicitly marked *(planned)*.

**Conventions:** all paths are relative to the repository root. Toolchain: Java 21,
Maven 3.9, Node 22. Performance and security suites run **only** on owned/local
environments — the OpenMRS demo (`o2.openmrs.org`) is used for functional smoke only.

---

## Execution summary

| Suite | Result | Type | Evidence committed |
|-------|--------|------|--------------------|
| OpenMRS headed smoke | **5 / 5 PASS** | Node Playwright (headed) vs `o2.openmrs.org` | screenshots step-1..5, `trace.zip`, HTML report, `results.json`, `.last-run.json` |
| omiiCARE smoke | **5 / 5 PASS** | Node Playwright (headed) vs local omiiCARE | HTML report |
| Automation unit suite | **98 / 98 PASS** | JUnit5/TestNG (default Maven build) | Surefire output, Allure results |
| Manual test suite | **4,187 cases / 66 modules** | Manual (17-col CSV) | per-module CSVs + `ALL_TEST_CASES.csv` |

---

## 1. OpenMRS headed smoke — 5/5 PASS (reference SUT)

The flagship reference run: a real browser drives the public OpenMRS demo through a
full patient lifecycle. Spec: `automation/playwright/tests-openmrs/openmrs-smoke.spec.ts`.
Config: `automation/playwright/playwright.openmrs.config.ts`
(`baseURL = process.env.OPENMRS_BASE_URL ?? 'https://o2.openmrs.org'`).

**Five steps (each with a committed screenshot):**

| Step | Flow | Screenshot artifact |
|------|------|---------------------|
| 1 | Launch + login | `automation/playwright/artifacts-openmrs/screenshots/step-1-login.png` |
| 2 | Navigate home | `automation/playwright/artifacts-openmrs/screenshots/step-2-home.png` |
| 3 | Find patient | `automation/playwright/artifacts-openmrs/screenshots/step-3-find-patient.png` |
| 4 | Register patient (CRUD) | `automation/playwright/artifacts-openmrs/screenshots/step-4-patient-created.png` |
| 5 | Logout | `automation/playwright/artifacts-openmrs/screenshots/step-5-logged-out.png` |

**Other committed artifacts (verified):**

- Trace (open in Playwright trace viewer): `automation/playwright/artifacts-openmrs/trace.zip`
- HTML report: `automation/playwright/playwright-report-openmrs/index.html`
- JSON result: `automation/playwright/results-openmrs/results.json`
- Run status: `automation/playwright/artifacts-openmrs/test-output/.last-run.json`
  → `{ "status": "passed", "failedTests": [] }`
- Console diagnostics: `automation/playwright/artifacts-openmrs/diagnostics.json`

**Regenerate:**

```bash
cd automation/playwright
npm ci                      # first time only
npx playwright install      # browsers, first time only
# headed run against the OpenMRS reference demo (functional smoke only):
npx playwright test --config playwright.openmrs.config.ts --headed
# view artifacts:
npx playwright show-report playwright-report-openmrs
npx playwright show-trace artifacts-openmrs/trace.zip
```

> Override the target with `OPENMRS_BASE_URL` to point at a self-hosted OpenMRS.
> Keep this suite functional and low-volume — never use it for load or attack traffic.

---

## 2. omiiCARE smoke — 5/5 PASS (owned SUT)

Local headed smoke against the in-house omiiCARE app.
Spec: `automation/playwright/tests/smoke.spec.ts`.
Config: `automation/playwright/playwright.config.ts`.

**Committed artifact:** HTML report
`automation/playwright/playwright-report/index.html`
(plus working artifacts under `automation/playwright/artifacts/` and `results/`).

**Regenerate:**

```bash
cd automation/playwright
npm run smoke               # = playwright test (default config)
npm run report              # = playwright show-report
```

> Requires the local omiiCARE backend/frontend running (see
> `infrastructure/docker/docker-compose.yml` and `scripts/`).

---

## 3. Automation unit suite — 98/98 PASS (default build)

The fast, hermetic gate. No SUT or browser required — this is what runs on every PR
via `qa-ci.yml`.

```bash
mvn -pl automation test     # 98 unit tests PASS
```

Covers, among others: Resource Adapter registry resolution
(`core/adapter/AdapterRegistryTest.java`) and PHI-safe data factories
(`core/generators/ProviderFactoryTest.java`, `AppointmentFactoryTest.java`).

**Artifacts:**

- Surefire reports: `automation/target/surefire-reports/` *(generated on run)*
- Allure results: `automation/allure-results/`

**Generate the Allure HTML report:**

```bash
# requires the Allure CLI (https://allurereport.org)
allure generate automation/allure-results --clean -o automation/target/allure-report
allure open automation/target/allure-report
```

**Live SUT / browser tiers (opt-in, owned envs):**

```bash
mvn -pl automation -Pe2e test   # runs ui-e2e / api-e2e / bdd tagged suites
```

---

## 4. Manual test suite — 4,187 cases / 66 modules

The largest evidence asset: structured manual coverage in 17-column CSVs.

| Asset | Path |
|-------|------|
| Per-module test cases | `manual-testing/test-cases/openmrs/<MODULE>.csv` (e.g. `AUTH.csv`, `APPT.csv`, `CLIN.csv`, `BILL.csv`, `A11Y.csv`) |
| Consolidated suite | `manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv` |
| Requirements traceability | `manual-testing/rtm/RTM.csv`, `manual-testing/rtm/RTM.md` (0 gaps, 0 untraced) |
| Requirements catalog | `docs/requirements/requirements-catalog.md` (1,795 requirements) |

**Inspect / verify counts (std tools):**

```bash
# total manual cases (minus header):
tail -n +2 manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv | wc -l
# module CSVs present:
ls manual-testing/test-cases/openmrs/*.csv | wc -l
# confirm RTM has no gaps:
grep -ic 'gap' manual-testing/rtm/RTM.csv
```

---

## 5. Quality, performance & security executions

> **Hard rule:** these run on owned/local environments only — never against
> `o2.openmrs.org`.

| Suite | Location | Run |
|-------|----------|-----|
| Performance (k6) | `quality/performance/` (k6 scripts) | `k6 run quality/performance/<script>.js` against a **local** target |
| Performance (JMeter) | `quality/performance/` (JMeter plans) | `jmeter -n -t <plan>.jmx -l results.jtl` against a **local** target |
| Security (ZAP) | `quality/security/` (ZAP baseline config + run script) | run the committed ZAP baseline script against a **local** target |
| Accessibility | `quality/accessibility/` + `a11y/` (axe via Playwright) | Node Playwright a11y run |
| Visual | `quality/visual/` | Node Playwright visual run |

*Captured baseline reports for perf/security are generated per local run and are not
committed as fixed artifacts — regenerate against your owned environment.* *(planned:
committed sample perf/security baseline reports once a shared owned env is published.)*

---

## 6. Reports index (where each report lands)

| Report | Path | Regenerate with |
|--------|------|-----------------|
| OpenMRS Playwright HTML | `automation/playwright/playwright-report-openmrs/index.html` | `npx playwright test --config playwright.openmrs.config.ts` |
| OpenMRS trace | `automation/playwright/artifacts-openmrs/trace.zip` | (same run) → `npx playwright show-trace …` |
| OpenMRS screenshots | `automation/playwright/artifacts-openmrs/screenshots/step-1..5*.png` | (same run) |
| omiiCARE Playwright HTML | `automation/playwright/playwright-report/index.html` | `npm run smoke` |
| Allure (Java) | `automation/allure-results/` → HTML | `allure generate automation/allure-results -o …` |
| Extent (Java) | Extent HTML output | produced by `reporting/` during `-Pe2e` runs |
| Surefire | `automation/target/surefire-reports/` | `mvn -pl automation test` |

---

## 7. One-shot reproduction

```bash
# 1) Fast unit gate (no SUT):
mvn -pl automation test                                   # 98/98 PASS

# 2) OpenMRS reference smoke (functional only):
cd automation/playwright && npx playwright test \
  --config playwright.openmrs.config.ts --headed          # 5/5 PASS

# 3) omiiCARE local smoke:
npm run smoke                                             # 5/5 PASS (needs local omiiCARE)

# 4) Open the latest reports:
npx playwright show-report playwright-report-openmrs
```

---

*Cross-references:* `docs/portfolio/ARCHITECTURE_WALKTHROUGH.md`,
`docs/portfolio/PORTFOLIO_GUIDE.md`, `automation/AUTOMATION_FRAMEWORK.md`,
`docs/MASTER_TEST_PLAN.md`.
