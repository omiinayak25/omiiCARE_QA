import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

/**
 * Accessibility scan of the omiiCARE /login page.
 *
 * Asserts zero WCAG 2.0/2.1 Level A and AA violations using axe-core.
 */

const WCAG_TAGS = ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'];

test.describe('Login page — WCAG 2.1 AA', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
    // Ensure the login form is rendered before scanning.
    await expect(page.getByTestId('login-username')).toBeVisible();
    await expect(page.getByTestId('login-password')).toBeVisible();
    await expect(page.getByTestId('login-submit')).toBeVisible();
  });

  test('has no detectable WCAG A/AA violations', async ({ page }, testInfo) => {
    const results = await new AxeBuilder({ page })
      .withTags(WCAG_TAGS)
      .analyze();

    // Attach the full violation list to the report for triage.
    await testInfo.attach('axe-login-violations.json', {
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

  test('login form controls expose accessible names', async ({ page }) => {
    // Forms (WCAG 3.3.2): inputs must have programmatic labels.
    const results = await new AxeBuilder({ page })
      .withTags(WCAG_TAGS)
      .include('form')
      .analyze();

    const labelIssues = results.violations.filter((v) =>
      ['label', 'label-title-only', 'form-field-multiple-labels'].includes(v.id),
    );
    expect(labelIssues, JSON.stringify(labelIssues, null, 2)).toEqual([]);
  });
});
