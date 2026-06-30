import { test, expect } from '@playwright/test';

/**
 * Visual regression for the omiiCARE dashboard (authenticated route `/`).
 *
 * Logs in via the real form, waits for the dashboard, then captures
 * light and dark theme baselines. Regenerate with: npm run update
 */

const USERNAME = process.env.VISUAL_USERNAME ?? 'admin';
const PASSWORD = process.env.VISUAL_PASSWORD ?? 'admin123';

test.describe('Dashboard — visual', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
    await page.getByTestId('login-username').fill(USERNAME);
    await page.getByTestId('login-password').fill(PASSWORD);
    await page.getByTestId('login-submit').click();
    await expect(page.getByTestId('dashboard')).toBeVisible({ timeout: 15_000 });
  });

  test('dashboard — light theme', async ({ page }) => {
    await expect(page).toHaveScreenshot('dashboard-light.png', {
      fullPage: true,
    });
  });

  test('dashboard — dark theme', async ({ page }) => {
    await page.getByTestId('toggle-theme').click();
    await expect(page.getByTestId('dashboard')).toBeVisible();
    await expect(page).toHaveScreenshot('dashboard-dark.png', {
      fullPage: true,
    });
  });

  test('primary navigation', async ({ page }) => {
    // Component-level snapshot of the nav landmark.
    await expect(page.getByTestId('primary-nav')).toHaveScreenshot(
      'primary-nav.png',
    );
  });
});
