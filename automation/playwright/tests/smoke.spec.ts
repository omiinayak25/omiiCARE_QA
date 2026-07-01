import {
  test,
  expect,
  type BrowserContext,
  type Page,
  type ConsoleMessage,
  type Request,
  type Response,
} from '@playwright/test';
import fs from 'node:fs';
import path from 'node:path';

/**
 * omiiCARE_QA — core-flow smoke suite (headed, slow-motion).
 *
 * One continuous browser session walks the most important user journey so a
 * human can watch it: application launch → login → dashboard → navigation →
 * a representative CRUD workflow (register a patient) → logout.
 *
 * Selectors are the stable `data-testid` hooks the frontend exposes — no brittle
 * text/CSS locators. The whole session is traced and (on failure) recorded to
 * video; per-test failures also capture a full-page screenshot.
 */

const ART = path.join(__dirname, '..', 'artifacts');
const SHOTS = path.join(ART, 'screenshots');
const VIDEO_DIR = path.join(ART, 'video');
fs.mkdirSync(SHOTS, { recursive: true });
fs.mkdirSync(VIDEO_DIR, { recursive: true });

const CRED = { username: 'demo.admin', password: 'Admin@12345' };
const UNIQUE_LAST = `Qa${Date.now().toString().slice(-8)}`;
const NEW_PATIENT = { firstName: 'Smoke', lastName: UNIQUE_LAST, dob: '1990-01-01' };

// Session-wide diagnostics (asked for: console errors, JS exceptions, network failures, failed API calls).
const consoleErrors: string[] = [];
const pageErrors: string[] = [];
const failedRequests: string[] = [];
const httpErrors: string[] = [];

let context: BrowserContext;
let page: Page;
let hadFailure = false;

test.describe.configure({ mode: 'serial' });

test.describe('omiiCARE_QA core-flow smoke (Google Chrome, headed, slowMo)', () => {
  test.beforeAll(async ({ browser }) => {
    context = await browser.newContext({
      viewport: null, // honor the maximized window
      recordVideo: { dir: VIDEO_DIR },
    });
    await context.tracing.start({ screenshots: true, snapshots: true, sources: true });
    page = await context.newPage();

    page.on('console', (msg: ConsoleMessage) => {
      if (msg.type() === 'error') consoleErrors.push(`console.error :: ${msg.text()}`);
    });
    page.on('pageerror', (err: Error) => pageErrors.push(`pageerror :: ${err.message}`));
    page.on('requestfailed', (req: Request) => {
      failedRequests.push(`requestfailed :: ${req.method()} ${req.url()} :: ${req.failure()?.errorText ?? 'unknown'}`);
    });
    page.on('response', (res: Response) => {
      if (res.status() >= 400) {
        httpErrors.push(`http ${res.status()} :: ${res.request().method()} ${res.url()}`);
      }
    });
  });

  test.afterEach(async ({}, testInfo) => {
    if (testInfo.status !== testInfo.expectedStatus) {
      hadFailure = true;
      try {
        await page.screenshot({
          path: path.join(SHOTS, `FAIL-${testInfo.title.replace(/[^\w]+/g, '_').slice(0, 60)}.png`),
          fullPage: true,
        });
      } catch {
        /* ignore screenshot errors */
      }
    }
  });

  test.afterAll(async () => {
    try {
      await context?.tracing.stop({ path: path.join(ART, 'trace.zip') });
    } catch {
      /* ignore */
    }
    fs.writeFileSync(
      path.join(ART, 'diagnostics.json'),
      JSON.stringify({ consoleErrors, pageErrors, failedRequests, httpErrors }, null, 2),
    );

    const video = page?.video();
    await page?.close();
    await context?.close();
    // retain-on-failure: keep the session video only if something failed
    if (!hadFailure) {
      const p = await video?.path().catch(() => undefined);
      if (p) {
        try {
          fs.rmSync(p);
        } catch {
          /* ignore */
        }
      }
    }
    // eslint-disable-next-line no-console
    console.log(
      `\n[diagnostics] console.error=${consoleErrors.length} pageerror=${pageErrors.length} ` +
        `requestfailed=${failedRequests.length} http>=400=${httpErrors.length}`,
    );
  });

  test('1) application launch — unauthenticated visit lands on the login screen', async () => {
    await page.goto('/');
    await expect(page).toHaveURL(/\/login$/);
    await expect(page.getByTestId('login-username')).toBeVisible();
    await expect(page.getByTestId('login-password')).toBeVisible();
    await expect(page.getByTestId('login-submit')).toBeVisible();
  });

  test('2) login — demo account signs in and the dashboard loads', async () => {
    await page.getByTestId('login-username').fill(CRED.username);
    await page.getByTestId('login-password').fill(CRED.password);
    await page.getByTestId('login-submit').click();

    await expect(page.getByTestId('dashboard')).toBeVisible();
    await expect(page.getByRole('heading', { name: /Welcome,/ })).toBeVisible();
    await expect(page).toHaveURL(/\/$/); // root path, port-agnostic
    await page.screenshot({ path: path.join(SHOTS, 'step-2-dashboard.png') });
  });

  test('3) navigation — sidebar routes to Patients and Appointments', async () => {
    await page.getByTestId('nav-patients').click();
    await expect(page).toHaveURL(/\/patients$/);
    await expect(page.getByTestId('patients-page')).toBeVisible();
    await expect(page.getByTestId('patients-table')).toBeVisible();
    await page.screenshot({ path: path.join(SHOTS, 'step-3-patients.png') });

    await page.getByTestId('nav-appointments').click();
    await expect(page).toHaveURL(/\/appointments$/);
    await expect(page.getByTestId('appointments-page')).toBeVisible();

    await page.getByTestId('nav-dashboard').click();
    await expect(page).toHaveURL(/\/$/); // root path, port-agnostic
    await expect(page.getByTestId('dashboard')).toBeVisible();
  });

  test('4) CRUD — register a new patient and confirm it is listed', async () => {
    await page.getByTestId('nav-patients').click();
    await expect(page.getByTestId('patients-page')).toBeVisible();

    await page.getByTestId('add-patient').click();
    await expect(page.getByTestId('patient-firstName')).toBeVisible();
    await page.getByTestId('patient-firstName').fill(NEW_PATIENT.firstName);
    await page.getByTestId('patient-lastName').fill(NEW_PATIENT.lastName);
    await page.getByTestId('patient-dob').fill(NEW_PATIENT.dob);
    await page.getByTestId('patient-save').click();

    // Success signals: the dialog closes (it stays open with an error on failure)…
    await expect(page.getByTestId('patient-firstName')).toBeHidden();
    // …and the record is retrievable via the q-param search.
    await page.getByTestId('patient-search').fill(NEW_PATIENT.lastName);
    await expect(page.getByTestId('patients-table')).toContainText(NEW_PATIENT.lastName, {
      timeout: 15_000,
    });
    await page.screenshot({ path: path.join(SHOTS, 'step-4-patient-created.png') });
  });

  test('5) logout — returns to the login screen', async () => {
    await page.getByTestId('logout').click();
    await expect(page).toHaveURL(/\/login$/);
    await expect(page.getByTestId('login-submit')).toBeVisible();
    await page.screenshot({ path: path.join(SHOTS, 'step-5-logged-out.png') });
  });
});
