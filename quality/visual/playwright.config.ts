import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright configuration for omiiCARE visual regression tests.
 *
 * Baselines are stored next to the specs under tests/__screenshots__/ and are
 * committed to git. The SUT is the omiiCARE frontend at http://localhost:5173.
 */
export default defineConfig({
  testDir: './tests',
  testMatch: '**/*.visual.spec.ts',
  timeout: 60_000,
  expect: {
    timeout: 10_000,
    toHaveScreenshot: {
      // Allow up to 0.2% of pixels to differ (anti-aliasing / sub-pixel noise).
      maxDiffPixelRatio: 0.002,
      // Per-pixel color sensitivity (0 strict .. 1 lax).
      threshold: 0.2,
      animations: 'disabled',
      caret: 'hide',
      scale: 'css',
    },
  },
  // Deterministic baselines: stable file path per project + spec + arg.
  snapshotPathTemplate:
    'tests/__screenshots__/{projectName}/{testFilePath}/{arg}{ext}',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: [
    ['list'],
    ['html', { outputFolder: 'playwright-report', open: 'never' }],
    ['json', { outputFile: 'visual-results.json' }],
  ],
  use: {
    baseURL: process.env.VISUAL_BASE_URL ?? 'http://localhost:5173',
    trace: 'on-first-retry',
    // Pin a viewport-relative device scale so screenshots are reproducible.
    deviceScaleFactor: 1,
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'], viewport: { width: 1280, height: 800 } },
    },
    {
      name: 'chromium-mobile',
      use: { ...devices['Pixel 5'] },
    },
  ],
});
