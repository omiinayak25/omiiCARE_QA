import { defineConfig } from '@playwright/test';

/**
 * Smoke configuration for a LIVE PUBLIC healthcare demo — OpenMRS Reference
 * Application (https://o2.openmrs.org, the `OPENMRS` TargetSystem adapter).
 *
 * Same execution profile as the local SUT smoke (headed Google Chrome, 1 worker,
 * sequential, retries 0, slowMo, maximized), but with widened timeouts because
 * the target is a shared public server over the internet.
 *
 * Scope: gentle FUNCTIONAL smoke only (login, navigation, one patient
 * registration, logout). No load/stress/security against a public site.
 */
export default defineConfig({
  testDir: './tests-openmrs',
  outputDir: './artifacts-openmrs/test-output',

  fullyParallel: false,
  workers: 1,
  retries: 0,

  // Remote public server → generous timeouts (slowMo + network latency).
  timeout: 180_000,
  expect: { timeout: 25_000 },

  reporter: [
    ['list'],
    ['html', { outputFolder: 'playwright-report-openmrs', open: 'never' }],
    ['json', { outputFile: 'results-openmrs/results.json' }],
  ],

  use: {
    baseURL: process.env.OPENMRS_BASE_URL ?? 'https://o2.openmrs.org',

    channel: 'chrome',
    headless: false,
    viewport: null,
    launchOptions: {
      slowMo: 750,
      // --start-maximized for visibility; --window-size guarantees a wide enough
      // window so OpenMRS's responsive navbar (incl. the Logout link) stays expanded.
      args: ['--start-maximized', '--window-size=1680,1050'],
    },

    actionTimeout: 35_000,
    navigationTimeout: 60_000,

    // Artifacts managed manually on a shared session context in the spec.
    screenshot: 'off',
    video: 'off',
    trace: 'off',
  },
});
