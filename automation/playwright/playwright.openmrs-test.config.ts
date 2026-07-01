import { defineConfig } from '@playwright/test';

/**
 * OpenMRS functional test pass — API (REST) + FHIR R4 + UI functional/negative,
 * against the live public demo (https://o2.openmrs.org). Read-only / gentle
 * (no load/stress/security-attack per project policy).
 *
 * Sequential, single worker, no retries; Google Chrome; headless (this is a
 * functional/diagnostic pass, not a headed observation run). Full artifacts.
 */
export default defineConfig({
  testDir: './tests-openmrs',
  testMatch: ['**/openmrs-api-fhir.spec.ts', '**/openmrs-ui-functional.spec.ts'],
  outputDir: './artifacts-openmrs-test/test-output',

  fullyParallel: false,
  workers: 1,
  retries: 0,

  timeout: 120_000,
  expect: { timeout: 20_000 },

  reporter: [
    ['list'],
    ['html', { outputFolder: 'playwright-report-openmrs-test', open: 'never' }],
    ['json', { outputFile: 'results-openmrs-test/results.json' }],
  ],

  use: {
    baseURL: process.env.OPENMRS_BASE_URL ?? 'https://o2.openmrs.org',
    channel: 'chrome',
    headless: true,
    actionTimeout: 30_000,
    navigationTimeout: 60_000,
    ignoreHTTPSErrors: true,
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    trace: 'on',
  },
});
