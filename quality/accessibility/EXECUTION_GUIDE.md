# Accessibility Execution Guide — omiiCARE_QA

How to run the axe-core and Lighthouse accessibility suites against the local
omiiCARE frontend.

---

## 1. Prerequisites

| Requirement | Version / Note |
|-------------|----------------|
| Node.js | 18 LTS or 20 LTS |
| npm | 9+ |
| omiiCARE frontend running | `http://localhost:5173` |
| Chromium for Playwright | installed via `npx playwright install chromium` |
| Chrome / Chromium for Lighthouse | system Chrome or the Playwright build |

## 2. Start the System Under Test (SUT)

In a **separate terminal**, from the repo root:

```bash
cd apps/frontend
npm install        # first time only
npm run dev        # serves http://localhost:5173
```

Confirm the app is reachable:

```bash
curl -sSf http://localhost:5173 > /dev/null && echo "SUT is up"
```

> Login credentials default to `admin` / `admin123`. Override with the
> `A11Y_USERNAME` / `A11Y_PASSWORD` environment variables if your seed data differs.

## 3. Install the Accessibility Suite

```bash
cd quality/accessibility
npm install
npx playwright install chromium
```

## 4. Run axe-core Scans (Playwright)

```bash
# All scans (login + dashboard, light + dark)
npm run test:a11y

# Single page
npx playwright test tests/login.a11y.spec.ts
npx playwright test tests/dashboard.a11y.spec.ts

# Open the HTML report after a run
npm run test:a11y:report
```

Point the suite at a different host:

```bash
A11Y_BASE_URL=http://127.0.0.1:5173 npm run test:a11y
```

### Expected output

```
Running 4 tests using 4 workers
  ✓  login.a11y.spec.ts:21:3 › has no detectable WCAG A/AA violations
  ✓  login.a11y.spec.ts:45:3 › login form controls expose accessible names
  ✓  dashboard.a11y.spec.ts:27:3 › ... (light theme)
  ✓  dashboard.a11y.spec.ts:50:3 › ... (dark theme)
  4 passed
```

On failure, the violating rule ids, impact, and affected node count print in
the assertion message; the full JSON is attached to the HTML report
(`playwright-report/`) and written to `a11y-results.json`.

## 5. Run Lighthouse CI

```bash
npm run lighthouse
```

This runs `lhci autorun`: it collects 3 runs per URL (`/login`, `/`), asserts
the accessibility category score ≥ 0.90, and uploads the report to temporary
public storage (URL printed at the end).

Collect and assert separately for debugging:

```bash
npm run lighthouse:collect
npm run lighthouse:assert
```

> Lighthouse audits the dashboard URL `/` unauthenticated; if your app
> redirects unauthenticated users to `/login`, Lighthouse scores the login
> page for that URL. For an authenticated Lighthouse run, supply a session
> cookie via `settings.extraHeaders` in `lighthouserc.json` or audit through a
> Playwright-authenticated context.

## 6. Manual / Assisted Checklist

Automation catches a subset of WCAG. Run these by hand each release:

| # | Check | WCAG SC | How |
|---|-------|---------|-----|
| 1 | Tab order is logical on `/login` | 2.4.3 | Tab from username → password → submit |
| 2 | Visible focus ring everywhere | 2.4.7 | Tab through dashboard nav, `toggle-theme`, `logout` |
| 3 | No keyboard trap in dialogs | 2.1.2 | Open a dialog, press `Esc` and `Tab` |
| 4 | Error announced to SR | 4.1.3 | Submit empty login, confirm `login-error` is announced |
| 5 | Tables navigable by SR | 1.3.1 | Arrow through `patients-table` / `appointments-table` |
| 6 | 200% zoom reflow | 1.4.10 | Browser zoom to 200%, no horizontal scroll/loss |
| 7 | SR smoke (NVDA/VoiceOver) | 4.1.2 | Names + roles announced for all controls |

## 7. CI Integration

```yaml
# Example GitHub Actions step (frontend already started as a service/step)
- name: Accessibility (axe-core)
  working-directory: quality/accessibility
  run: |
    npm ci
    npx playwright install --with-deps chromium
    npm run test:a11y

- name: Accessibility (Lighthouse CI)
  working-directory: quality/accessibility
  run: npm run lighthouse
```

A non-zero exit on either step fails the pipeline. See `REPORTING_GUIDE.md` for
reading scores and the WCAG success-criteria mapping.

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Accessibility Engineer | Initial (Milestone 7) |
