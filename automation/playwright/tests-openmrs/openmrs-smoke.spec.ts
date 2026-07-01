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
 * omiiCARE_QA smoke SCENARIOS, re-targeted at a LIVE PUBLIC healthcare demo:
 * OpenMRS Reference Application (o2.openmrs.org) — the project's `OPENMRS` target.
 *
 * Same journey as the local SUT smoke — launch → login → home (dashboard) →
 * navigation → a representative CRUD (register a patient) → logout — but bound to
 * OpenMRS's real DOM (verified via recon). One continuous headed, slow-motion
 * Chrome session; whole session traced, video on failure, screenshots on failure
 * plus milestone captures.
 */

const ART = path.join(__dirname, '..', 'artifacts-openmrs');
const SHOTS = path.join(ART, 'screenshots');
const VIDEO_DIR = path.join(ART, 'video');
fs.mkdirSync(SHOTS, { recursive: true });
fs.mkdirSync(VIDEO_DIR, { recursive: true });

const CRED = { username: 'admin', password: 'Admin123' };
const LOCATION = 'Registration Desk';
const PATIENT = {
  given: 'Smoke',
  family: `Qa${Date.now().toString().slice(-8)}`, // unique → avoids "similar patient" matches
  day: '15',
  year: '1990',
};

const consoleErrors: string[] = [];
const pageErrors: string[] = [];
const failedRequests: string[] = [];
const httpErrors: string[] = [];

let context: BrowserContext;
let page: Page;
let hadFailure = false;

test.describe.configure({ mode: 'serial' });

test.describe('OpenMRS (public demo) core-flow smoke — Chrome, headed, slowMo', () => {
  test.beforeAll(async ({ browser }) => {
    context = await browser.newContext({ viewport: null, recordVideo: { dir: VIDEO_DIR } });
    await context.tracing.start({ screenshots: true, snapshots: true, sources: true });
    page = await context.newPage();

    page.on('console', (m: ConsoleMessage) => {
      if (m.type() === 'error') consoleErrors.push(`console.error :: ${m.text()}`.slice(0, 300));
    });
    page.on('pageerror', (e: Error) => pageErrors.push(`pageerror :: ${e.message}`.slice(0, 300)));
    page.on('requestfailed', (r: Request) =>
      failedRequests.push(`requestfailed :: ${r.method()} ${r.url()} :: ${r.failure()?.errorText ?? '?'}`.slice(0, 300)),
    );
    page.on('response', (r: Response) => {
      if (r.status() >= 400) httpErrors.push(`http ${r.status()} :: ${r.request().method()} ${r.url()}`.slice(0, 300));
    });
  });

  test.afterEach(async ({}, testInfo) => {
    if (testInfo.status !== testInfo.expectedStatus) {
      hadFailure = true;
      try {
        await page.screenshot({
          path: path.join(SHOTS, `FAIL-${testInfo.title.replace(/[^\w]+/g, '_').slice(0, 50)}.png`),
          fullPage: true,
        });
      } catch {
        /* ignore */
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

  test('1) application launch — OpenMRS login screen renders (location + credentials)', async () => {
    await page.goto('/openmrs/login.htm', { waitUntil: 'domcontentloaded' });
    await expect(page.locator('#username')).toBeVisible();
    await expect(page.locator('#password')).toBeVisible();
    await expect(page.locator(`li[id="${LOCATION}"]`)).toBeVisible();
    await page.screenshot({ path: path.join(SHOTS, 'step-1-login.png') });
  });

  test('2) login — admin signs in at a session location and the home dashboard loads', async () => {
    await page.locator(`li[id="${LOCATION}"]`).click();
    await page.locator('#username').fill(CRED.username);
    await page.locator('#password').fill(CRED.password);
    await page.locator('#loginButton').click();

    await expect(page).toHaveURL(/home\.page/);
    // Home dashboard loaded: the app tiles are visible (the Logout link lives in a
    // collapsible navbar, so it is NOT a reliable "logged-in" signal).
    await expect(page.locator('a[href*="registerPatient.page"]')).toBeVisible();
    await expect(page.locator('a[href*="app=coreapps.findPatient"]').first()).toBeVisible();
    await page.screenshot({ path: path.join(SHOTS, 'step-2-home.png') });
  });

  test('3) navigation — open "Find Patient Record" and return home', async () => {
    await page.locator('a[href*="app=coreapps.findPatient"]').first().click();
    await expect(page).toHaveURL(/findPatient\.page/);
    await expect(page.locator('#patient-search, input[placeholder*="earch" i]').first()).toBeVisible();
    await page.screenshot({ path: path.join(SHOTS, 'step-3-find-patient.png') });

    await page.goBack();
    await expect(page).toHaveURL(/home\.page/);
    await expect(page.locator('a[href*="registerPatient.page"]')).toBeVisible();
  });

  test('4) CRUD — register a new patient through the wizard and confirm it is created', async () => {
    await page.locator('a[href*="registerPatient.page"]').click();
    await expect(page).toHaveURL(/registerPatient\.page/);

    // Step: name
    await expect(page.locator('input[name="givenName"]')).toBeVisible();
    await page.locator('input[name="givenName"]').fill(PATIENT.given);
    await page.locator('input[name="familyName"]').fill(PATIENT.family);
    await page.locator('#next-button').click();

    // Step: gender
    await expect(page.locator('select[name="gender"]')).toBeVisible();
    await page.locator('select[name="gender"]').selectOption('M');
    if (!(await page.locator('#birthdateDay-field').isVisible().catch(() => false))) {
      await page.locator('#next-button').click();
    }

    // Step: birthdate (day / month / year)
    await expect(page.locator('#birthdateDay-field')).toBeVisible();
    await page.locator('#birthdateDay-field').fill(PATIENT.day);
    await page.locator('#birthdateMonth-field').selectOption({ index: 1 }); // first real month
    await page.locator('#birthdateYear-field').fill(PATIENT.year);
    await page.locator('#next-button').click();

    // Step: Contact Info → Address. OpenMRS requires at least one address field,
    // so fill it (otherwise the wizard refuses to advance to the Confirm screen).
    await expect(page.locator('#cityVillage')).toBeVisible();
    await page.locator('#address1').fill('1 Test Street');
    await page.locator('#cityVillage').fill('Boston');

    // Walk the remaining optional steps (Phone, Relationships) to the Confirm screen.
    for (let i = 0; i < 10; i++) {
      if (await page.locator('#submit').isVisible().catch(() => false)) break;
      const next = page.locator('#next-button');
      if (await next.isVisible().catch(() => false)) {
        await next.click();
      } else {
        break;
      }
    }

    // Confirm / save
    await expect(page.locator('#submit')).toBeVisible();
    await page.locator('#submit').click();

    // Success: leaves the registration form and the new patient's name is shown.
    await expect(page).not.toHaveURL(/registerPatient\.page/, { timeout: 60_000 });
    await expect(page.getByText(PATIENT.family, { exact: false }).first()).toBeVisible({ timeout: 30_000 });
    await page.screenshot({ path: path.join(SHOTS, 'step-4-patient-created.png') });
  });

  test('5) logout — returns to the OpenMRS login screen', async () => {
    const logout = page.locator('a[href*="logout.action"]').first();
    // Reveal the collapsible navbar if the Logout link is hidden behind the toggle.
    if (!(await logout.isVisible().catch(() => false))) {
      const toggle = page.getByRole('button', { name: /toggle navigation/i });
      if (await toggle.isVisible().catch(() => false)) await toggle.click();
    }
    await logout.click();
    await expect(page.locator('#username')).toBeVisible();
    await expect(page.locator('#loginButton')).toBeVisible();
    await page.screenshot({ path: path.join(SHOTS, 'step-5-logged-out.png') });
  });
});
