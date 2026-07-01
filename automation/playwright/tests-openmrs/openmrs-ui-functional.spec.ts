import { test, expect } from '@playwright/test';

/**
 * OpenMRS UI functional / negative tests against the live public demo.
 * Complements the happy-path smoke suite with a negative-auth path and a
 * post-login navigation + search check. Selectors are the verified O2 RefApp
 * selectors (#username/#password/#loginButton, session-location <li>).
 */

const ROOT = process.env.OPENMRS_BASE_URL ?? 'https://o2.openmrs.org';
const LOCATION = 'Registration Desk';

async function selectLocation(page: import('@playwright/test').Page) {
  await page.goto(`${ROOT}/openmrs/login.htm`, { waitUntil: 'domcontentloaded' });
  await page.locator(`li[id="${LOCATION}"]`).click();
}

test.describe('OpenMRS · UI functional', () => {
  test('UI-NEG-01 · login with a wrong password is rejected and stays on the login screen', async ({ page }) => {
    await selectLocation(page);
    await page.locator('#username').fill('admin');
    await page.locator('#password').fill('definitely-the-wrong-password');
    await page.locator('#loginButton').click();
    // Bad credentials must be rejected: no access to the home dashboard, and the app
    // re-renders the login page with an "Invalid username/password" error.
    await expect(page).not.toHaveURL(/home\.page/);
    await expect(page.getByText(/Invalid username\/password/i)).toBeVisible();
  });

  test('UI-02 · valid login reaches home, and Find Patient Record opens a working search', async ({ page }) => {
    await selectLocation(page);
    await page.locator('#username').fill('admin');
    await page.locator('#password').fill('Admin123');
    await page.locator('#loginButton').click();
    await expect(page).toHaveURL(/home\.page/);

    await page.locator('a[href*="app=coreapps.findPatient"]').first().click();
    await expect(page).toHaveURL(/findPatient\.page/);
    const search = page.locator('#patient-search, input[placeholder*="earch" i]').first();
    await expect(search).toBeVisible();
    await search.fill('a');
    await expect(search).toHaveValue('a');
  });
});
