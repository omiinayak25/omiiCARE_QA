# omiiCARE_QA — Live Demo Guide

> **Audience:** interviewers, reviewers, hiring panels, open-source evaluators.
> **Duration:** ~20 minutes (core) / ~35 minutes (full tour).
> **Goal:** prove that omiiCARE_QA is a real, runnable, enterprise-grade healthcare QA
> platform — green builds, headed browser smoke against a real OpenMRS instance, a
> JUnit5 unit suite, polished reports, and a 10k-line documentation + traceability corpus.

This is a **scripted, copy-paste demo**. Every command below is real and runnable from the
repository root. Expected outcomes are stated so you can confirm success live without guessing.

---

## 0. Prerequisites & one-time checks

| Tool | Version | Verify |
|------|---------|--------|
| Java | 21 | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Node | 22 | `node -v` |
| npm | bundled with Node 22 | `npm -v` |
| Git | any recent | `git --version` |
| Docker + Compose plugin | recent (optional, infra only) | `docker compose version` |

**HARD RULES (state these out loud during the demo):**
- Performance and security tests run **only on owned/local environments** — we **never** load-test
  or attack `o2.openmrs.org`. The OpenMRS smoke is **read-create-only** functional UI verification.
- All test data is **synthetic / PHI-safe** (Datafaker generators in `automation` `core.generators`).

Set your working directory once (all commands below assume repo root):

```bash
cd /path/to/omiiCARE_QA
```

> **Tip:** keep three terminal tabs open — **(A) backend**, **(B) frontend**, **(C) tests/reports**.

---

## 1. Start the omiiCARE SUT (System Under Test)

The local in-house SUT is a Spring Boot 3 / Java 21 backend + a React + Vite frontend.

### 1a. (Optional) Bring up the infra stack

Only needed if you want the full stack (Postgres/Redis/MailHog/MinIO/Keycloak/WireMock/
Prometheus/Grafana/SonarQube). **For a quick demo you can skip this** — the backend `dev`
profile uses an embedded **H2** database and needs no containers.

```bash
./scripts/start.sh
```

**Expected:** the script detects the Docker Compose plugin, brings up the infra containers,
waits for Postgres to become healthy, and prints service URLs/creds plus the exact backend
run command (`mvn -pl apps/backend spring-boot:run -Dspring-boot.run.profiles=docker`).

> If Docker is not available, the script exits with a clear message — that is fine for the
> H2 `dev`-profile demo below.

### 1b. Start the backend — Terminal A (dev profile, H2)

```bash
mvn -pl apps/backend spring-boot:run
```

The default active profile is `dev` (`SPRING_PROFILES_ACTIVE:dev` → embedded H2), so no extra
flags are required for the demo. (To point at the Postgres container instead, use
`-Dspring-boot.run.profiles=docker` as printed by `start.sh`.)

**Expected:**
- Spring Boot banner, `Started ...Application in N seconds`.
- Backend listening on **http://localhost:8080**.
- Health probe is green:

```bash
curl -s http://localhost:8080/actuator/health
# {"status":"UP",...}
```

### 1c. Start the frontend — Terminal B (Vite dev server)

```bash
cd apps/frontend
npm install      # first run only
npm run dev
```

**Expected:**
- Vite dev server on **http://localhost:5173**.
- Open the URL in a browser; the omiiCARE React UI loads (MUI-based).

> The Playwright omiiCARE smoke defaults to `http://localhost:5173` (override with
> `SMOKE_BASE_URL`), so leaving the dev server running here feeds Step 2 directly.

---

## 2. Run the omiiCARE Node Playwright smoke — **headed** (Terminal C)

Headed, slow-motion (`slowMo: 750 ms`) so the panel can watch the browser drive the app.

```bash
cd automation/playwright
npm install      # first run only — installs @playwright/test
npx playwright install   # first run only — downloads browsers
npm run smoke    # → playwright test (uses playwright.config.ts, testDir ./tests)
```

**Expected:** **5/5 PASS**, a real Chromium window opens and visibly drives the core flow.
Artifacts land in `automation/playwright/artifacts/` and an HTML report under
`automation/playwright/playwright-report/`.

Open the report:

```bash
npm run report   # → playwright show-report
```

> **Talking point:** the suite is intentionally **headed + slowMo** for *visual verification*
> during demos and reviews, not just a pass/fail in CI.

---

## 3. Run the OpenMRS headed smoke — **read-only / functional** (Terminal C)

The **primary reference SUT** is OpenMRS at `https://o2.openmrs.org`. This suite runs **headed**
against the public reference server and exercises: **launch → login → navigate →
register-patient (CRUD) → logout**.

```bash
cd automation/playwright
npx playwright test --config=playwright.openmrs.config.ts
```

(Config: `testDir ./tests-openmrs`, `baseURL https://o2.openmrs.org` — override with
`OPENMRS_BASE_URL`; headed; `slowMo: 750 ms`; retries 0; sequential; widened timeouts for
network latency.)

**Expected:** **5/5 PASS** against `o2.openmrs.org`. A browser window visibly logs in,
navigates, registers a synthetic patient, and logs out.

**Artifacts to show on screen** (`automation/playwright/artifacts-openmrs/`):
- `screenshots/step-1-login.png` → `step-5-logged-out.png` (one per flow step)
- `trace.zip` (open with `npx playwright show-trace artifacts-openmrs/trace.zip`)
- `video/`, `diagnostics.json`
- HTML report under `playwright-report-openmrs/`

```bash
npx playwright show-trace artifacts-openmrs/trace.zip
```

> **State the hard rule again:** this is **functional UI verification only** (single login +
> one synthetic patient create) — **no load, no attack** is ever directed at `o2.openmrs.org`.

---

## 4. Run the Java automation unit suite — **98 green** (Terminal C)

The default automation build is **unit-only** — fast, deterministic, no SUT or browser needed.

```bash
cd /path/to/omiiCARE_QA   # repo root
mvn -pl automation test
```

**Expected:** **98 unit tests PASS**, `BUILD SUCCESS`. These cover the framework itself —
Resource Adapter Layer (`core.adapter`), PHI-safe data generators (`core.generators`),
FHIR assertions (`fhir/`), HL7 v2 validation (`hl7/`), DB testing helpers (`db/`), env
management — **without** touching any SUT.

> **SUT / browser tests are tagged** `ui-e2e` / `api-e2e` / `bdd` and run separately:
> ```bash
> mvn -pl automation -Pe2e test   # requires a running SUT (planned for full-stack demos)
> ```
> Skip this in a quick demo unless the SUT from Step 1 is up.

---

## 5. Open the reports (Allure + Extent + Playwright HTML)

The automation module is wired for **Allure** (`allure 2.29.0`) and **Extent**
(`5.1.2`) reporting.

**Playwright HTML reports** (already generated in Steps 2–3):

```bash
cd automation/playwright
npx playwright show-report playwright-report            # omiiCARE smoke
npx playwright show-report playwright-report-openmrs    # OpenMRS smoke
```

**Allure** (from automation results, after an Allure-instrumented run):

```bash
# Requires the Allure CLI on PATH (https://allurereport.org)
allure serve automation/target/allure-results
```

**Expected:** browser opens a polished report — suites, steps, attachments (screenshots,
traces), and trend/severity views. Use this to show "reporting is enterprise-grade, not a log dump."

> If the Allure CLI is not installed on the demo machine, fall back to the Playwright HTML
> reports (always present) and the screenshots in `artifacts-openmrs/screenshots/`.

---

## 6. Tour the documentation & traceability (the "depth" moment)

This is where omiiCARE_QA separates itself from a toy project. Open these in your editor or a
Markdown viewer.

### 6a. Reverse-engineering corpus — `docs/reverse-engineering/`
~10k lines, 78 Mermaid diagrams. Highlights to open live:
- `BRD.md`, `SRS.md`, `FRD.md`, `NFR.md` — requirements ladder
- `USE_CASES.md`, `USER_STORIES_AND_ACCEPTANCE_CRITERIA.md`
- `RBAC_MATRIX.md`, `NAVIGATION_MAP.md`
- `DATA_DICTIONARY.md`, `FIELD_DICTIONARY.md`, `VALIDATION_MATRIX.md`
- `API_BLUEPRINT.md`, `FHIR_MAPPING.md`, `HL7_MAPPING.md`
- `ARCHITECTURE.md`, `RISK_REGISTER.md`, `diagrams/` (ERD + Mermaid)

### 6b. QA management — `docs/qa-management/`
Master strategy/plan, release & sprint plans, risk-based testing, estimation, defect
management, metrics, entry/exit criteria, checklists, UAT, KB, bug templates, automation
strategy, test data & environment plans.

### 6c. Requirements catalog — `docs/requirements/requirements-catalog.md`
**1,795 requirements**, structured and IDed.

### 6d. Manual test cases — `manual-testing/test-cases/openmrs/`
**4,187 manual test cases across 66 modules**, in a 17-column CSV format, plus the
consolidated `ALL_TEST_CASES.csv`.

```bash
# Count manual test cases (header excluded)
tail -n +2 manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv | wc -l
```

### 6e. Requirements Traceability Matrix — `manual-testing/rtm/`
The closing flourish: **full traceability, 0 gaps, 0 untraced**.

```bash
ls manual-testing/rtm/        # RTM.csv, RTM.md
```

Open `manual-testing/rtm/RTM.md` and point out: every requirement maps to at least one test
case — **0 coverage gaps, 0 untraced requirements**.

---

## 7. Suggested demo running order (cheat sheet)

| # | Action | Command | Expected |
|---|--------|---------|----------|
| 1 | Backend up | `mvn -pl apps/backend spring-boot:run` | UP on :8080 |
| 2 | Frontend up | `cd apps/frontend && npm run dev` | Vite on :5173 |
| 3 | omiiCARE smoke (headed) | `cd automation/playwright && npm run smoke` | 5/5 PASS |
| 4 | OpenMRS smoke (headed, read-only) | `npx playwright test --config=playwright.openmrs.config.ts` | 5/5 PASS |
| 5 | Java unit suite | `mvn -pl automation test` | 98 PASS, BUILD SUCCESS |
| 6 | Reports | `npx playwright show-report` / `allure serve ...` | polished UI |
| 7 | Docs + RTM tour | open `docs/` + `manual-testing/rtm/RTM.md` | 0 gaps, 0 untraced |

---

## 8. Teardown

```bash
# Stop frontend (Terminal B) and backend (Terminal A): Ctrl-C in each.
# If infra was started:
./scripts/stop.sh
# Optional full reset of infra volumes/state:
./scripts/reset.sh
```

---

## 9. Troubleshooting

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| `mvn -pl apps/backend spring-boot:run` fails on DB | wrong profile | Use default `dev` (H2); only use `docker` profile when Postgres is up |
| Playwright "browser not found" | browsers not installed | `npx playwright install` |
| omiiCARE smoke can't reach app | dev server down / wrong URL | Start `npm run dev`; or set `SMOKE_BASE_URL` |
| OpenMRS smoke flaky | public server latency | Suite already widens timeouts; re-run; or set `OPENMRS_BASE_URL` to a mirror you own |
| `allure: command not found` | Allure CLI missing | Use Playwright HTML reports + screenshots instead |
| Port 8080 / 5173 in use | another process bound | Free the port or change the dev port |

---

## 10. Honest scope notes

- The **default automation build is unit-only** (98 tests). SUT/browser suites are tagged
  (`ui-e2e` / `api-e2e` / `bdd`) and run under `-Pe2e` against a **running** SUT.
- Full multi-SUT `-Pe2e` orchestration and a one-command "demo-all" runner are **(planned)**.
- Allure trend history requires repeated instrumented runs to populate; a single run shows
  the current suite only.
- The OpenMRS smoke targets the public reference server and is **read/create-only** — behavior
  depends on that server's availability; pin `OPENMRS_BASE_URL` to an owned mirror for offline demos.
```