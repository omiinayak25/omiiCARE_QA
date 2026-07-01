import { defineConfig } from '@playwright/test';

/**
 * omiiCARE_QA — Smoke suite configuration.
 *
 * Purpose: a small, HEADED, SLOW-MOTION run against the live SUT so a human can
 * visually verify the core flow before expanding automation. This is NOT a
 * regression configuration.
 *
 * Execution profile (per request):
 *   - Browser:   Google Chrome (channel: 'chrome')
 *   - Mode:      headed (headless: false)
 *   - Workers:   1, sequential (fullyParallel: false)
 *   - Retries:   0
 *   - slowMo:    750 ms (within the 500–1000 ms band)
 *   - Window:    maximized (--start-maximized + viewport: null)
 *   - Artifacts: screenshots on failure, video + trace for the session
 *
 * The base URL points at the Vite dev server (frontend), which proxies /api to
 * the backend. Override with SMOKE_BASE_URL if the frontend runs elsewhere.
 */
export default defineConfig({
  testDir: './tests',
  outputDir: './artifacts/test-output',

  // Sequential, single worker, no retries — fully observable.
  fullyParallel: false,
  workers: 1,
  retries: 0,

  // Default project timeout (kept ~default but widened to absorb slowMo=750ms,
  // which inflates every interaction; without this a slow but PASSING flow could
  // trip the stock 30s limit). Justified adjustment for slow-motion visibility.
  timeout: 90_000,
  expect: { timeout: 10_000 },

  reporter: [
    ['list'],
    ['html', { outputFolder: 'playwright-report', open: 'never' }],
    ['json', { outputFile: 'results/results.json' }],
  ],

  use: {
    baseURL: process.env.SMOKE_BASE_URL ?? 'http://localhost:5173',

    // Google Chrome, headed, slow enough to watch.
    channel: 'chrome',
    headless: false,
    viewport: null, // use the real (maximized) window size
    launchOptions: {
      slowMo: 750,
      args: ['--start-maximized'],
    },

    actionTimeout: 20_000,
    navigationTimeout: 30_000,

    // Artifacts are managed MANUALLY in the spec on a single shared session
    // context (one continuous trace, one session video, failure screenshots), so
    // the runner's per-test artifact handling is disabled here to avoid double-
    // starting tracing/video on the same context.
    screenshot: 'off',
    video: 'off',
    trace: 'off',
  },
});
