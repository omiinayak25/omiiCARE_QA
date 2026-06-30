import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright configuration for omiiCARE accessibility (axe-core) tests.
 *
 * The SUT is the omiiCARE React frontend served at http://localhost:5173.
 * Start it before running: see EXECUTION_GUIDE.md.
 */
export default defineConfig({
  testDir: './tests',
  testMatch: '**/*.a11y.spec.ts',
  timeout: 60_000,
  expect: { timeout: 10_000 },
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: [
    ['list'],
    ['html', { outputFolder: 'playwright-report', open: 'never' }],
    ['json', { outputFile: 'a11y-results.json' }],
  ],
  use: {
    baseURL: process.env.A11Y_BASE_URL ?? 'http://localhost:5173',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'off',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
