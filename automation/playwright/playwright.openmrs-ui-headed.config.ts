import { defineConfig } from '@playwright/test';

/**
 * OpenMRS UI functional/negative tests — HEADED Google Chrome, single worker,
 * slow-motion, against the live public demo (read-only). Companion to the
 * headed core-flow smoke config (playwright.openmrs.config.ts).
 */
export default defineConfig({
  testDir: './tests-openmrs',
  testMatch: ['**/openmrs-ui-functional.spec.ts'],
  outputDir: './artifacts-openmrs-ui-headed/test-output',

  fullyParallel: false,
  workers: 1,
  retries: 0,

  timeout: 120_000,
  expect: { timeout: 20_000 },

  reporter: [
    ['list'],
    ['html', { outputFolder: 'playwright-report-openmrs-ui-headed', open: 'never' }],
    ['json', { outputFile: 'results-openmrs-ui-headed/results.json' }],
  ],

  use: {
    baseURL: process.env.OPENMRS_BASE_URL ?? 'https://o2.openmrs.org',
    channel: 'chrome',
    headless: false,
    viewport: null,
    launchOptions: { slowMo: 700, args: ['--start-maximized', '--window-size=1680,1050'] },
    actionTimeout: 30_000,
    navigationTimeout: 60_000,
    ignoreHTTPSErrors: true,
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    trace: 'on',
  },
});
