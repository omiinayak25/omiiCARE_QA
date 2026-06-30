import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

/**
 * Accessibility scan of the omiiCARE dashboard (authenticated route `/`).
 *
 * Logs in via the real form, waits for the dashboard, then asserts zero
 * WCAG 2.0/2.1 Level A and AA violations using axe-core.
 */

const WCAG_TAGS = ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'];

const USERNAME = process.env.A11Y_USERNAME ?? 'admin';
const PASSWORD = process.env.A11Y_PASSWORD ?? 'admin123';

test.describe('Dashboard — WCAG 2.1 AA', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
    await page.getByTestId('login-username').fill(USERNAME);
    await page.getByTestId('login-password').fill(PASSWORD);
    await page.getByTestId('login-submit').click();
    // Wait for the authenticated dashboard to mount.
    await expect(page.getByTestId('dashboard')).toBeVisible({ timeout: 15_000 });
  });

  test('has no detectable WCAG A/AA violations (light theme)', async ({ page }, testInfo) => {
    const results = await new AxeBuilder({ page })
      .withTags(WCAG_TAGS)
      .analyze();

    await testInfo.attach('axe-dashboard-light-violations.json', {
      body: JSON.stringify(results.violations, null, 2),
      contentType: 'application/json',
    });

    const summary = results.violations.map((v) => ({
      id: v.id,
      impact: v.impact,
      help: v.help,
      nodes: v.nodes.length,
    }));

    expect(summary, JSON.stringify(summary, null, 2)).toEqual([]);
  });

  test('has no detectable WCAG A/AA violations (dark theme)', async ({ page }, testInfo) => {
    // Toggle to dark theme and re-scan — contrast (1.4.3) must hold in both palettes.
    await page.getByTestId('toggle-theme').click();
    await expect(page.getByTestId('dashboard')).toBeVisible();

    const results = await new AxeBuilder({ page })
      .withTags(WCAG_TAGS)
      .analyze();

    await testInfo.attach('axe-dashboard-dark-violations.json', {
      body: JSON.stringify(results.violations, null, 2),
      contentType: 'application/json',
    });

    const summary = results.violations.map((v) => ({
      id: v.id,
      impact: v.impact,
      help: v.help,
      nodes: v.nodes.length,
    }));

    expect(summary, JSON.stringify(summary, null, 2)).toEqual([]);
  });
});
