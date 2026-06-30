import { test, expect } from '@playwright/test';

/**
 * Visual regression for the omiiCARE /login page (light + dark themes).
 *
 * Baselines are stored under tests/__screenshots__/ and committed to git.
 * Regenerate intended changes with: npm run update
 */

test.describe('Login page — visual', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
    // Wait for the form so we never screenshot a half-rendered page.
    await expect(page.getByTestId('login-username')).toBeVisible();
    await expect(page.getByTestId('login-submit')).toBeVisible();
  });

  test('login page — light theme', async ({ page }) => {
    await expect(page).toHaveScreenshot('login-light.png', {
      fullPage: true,
    });
  });

  test('login page — dark theme', async ({ page }) => {
    // Switch to the dark MUI palette before capturing.
    await page.getByTestId('toggle-theme').click();
    await expect(page.getByTestId('login-submit')).toBeVisible();
    await expect(page).toHaveScreenshot('login-dark.png', {
      fullPage: true,
    });
  });

  test('login submit button', async ({ page }) => {
    // Component-level snapshot of a single stable control.
    await expect(page.getByTestId('login-submit')).toHaveScreenshot(
      'login-submit-button.png',
    );
  });
});
