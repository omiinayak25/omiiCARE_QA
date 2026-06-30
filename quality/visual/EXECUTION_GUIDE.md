# Visual Regression Execution Guide — omiiCARE_QA

How to create baselines, run comparisons, review/approve diffs, and run the
visual suite in CI.

---

## 1. Prerequisites

| Requirement | Version / Note |
|-------------|----------------|
| Node.js | 18 LTS or 20 LTS |
| npm | 9+ |
| omiiCARE frontend running | `http://localhost:5173` |
| Playwright browsers | `npx playwright install chromium` |

> Credentials default to `admin` / `admin123`. Override with
> `VISUAL_USERNAME` / `VISUAL_PASSWORD`.

## 2. Start the SUT

```bash
cd apps/frontend
npm install        # first time only
npm run dev        # http://localhost:5173
```

## 3. Install the Visual Suite

```bash
cd quality/visual
npm install
npx playwright install chromium
```

## 4. Create Baselines (first run)

There are no baselines on a fresh checkout. Generate them once, review, commit:

```bash
npm run update          # writes tests/__screenshots__/**/*-expected.png
git add tests/__screenshots__
git commit -m "test(visual): add baseline screenshots"
```

> **Important:** generate baselines on the **same OS/CI image** that will run
> comparisons. Font rendering and anti-aliasing differ across platforms and
> will cause false diffs. The recommended approach is to generate baselines in
> CI (or a pinned container) — see section 7.

## 5. Run the Comparison

```bash
npm run test:visual                         # all projects (chromium + chromium-mobile)
npx playwright test login.visual.spec.ts    # one spec
npx playwright test --project=chromium      # one project
```

Point at a different host:

```bash
VISUAL_BASE_URL=http://127.0.0.1:5173 npm run test:visual
```

### Expected output (passing)

```
Running 12 tests using 4 workers
  ✓  [chromium] login.visual.spec.ts:18:3 › login page — light theme
  ✓  [chromium] login.visual.spec.ts:24:3 › login page — dark theme
  ✓  [chromium-mobile] dashboard.visual.spec.ts:23:3 › dashboard — light theme
  ...
  12 passed
```

## 6. Review & Approval Workflow

When a spec fails:

```bash
npm run report      # opens the HTML report
```

The report shows **Expected | Actual | Diff** for each failure.

1. **Inspect the diff image.**
2. **Unintended change (regression)** -> fix the frontend code and re-run.
   Do **not** touch the baseline.
3. **Intended change** -> regenerate that baseline:
   ```bash
   npx playwright test login.visual.spec.ts --update-snapshots
   ```
   Commit the updated `*-expected.png`. The PNG diff is visible in the pull
   request, where a reviewer **approves the new baseline** as part of code
   review. Baselines are never auto-accepted in CI.

Failure artifacts (`*-actual.png`, `*-diff.png`) land in `test-results/` and
are git-ignored — only `*-expected.png` baselines are committed.

## 7. CI Considerations

- **Determinism is everything.** Run baselines and comparisons on the **same
  container image**. Differences in OS, fonts, GPU, or device scale factor
  produce false positives.
- Pin the Playwright version (already pinned in `package.json`) so the bundled
  browser build matches the baseline.
- Disable animations and hide the caret (already set globally in
  `playwright.config.ts` via `toHaveScreenshot`).
- Tune tolerance with `maxDiffPixelRatio` / `threshold` if minor font noise
  remains after pinning the image — start strict, loosen only as needed.
- Do **not** run `--update-snapshots` in CI; CI compares, humans approve.

```yaml
# Example GitHub Actions
- name: Visual regression
  working-directory: quality/visual
  run: |
    npm ci
    npx playwright install --with-deps chromium
    npm run test:visual
  # Upload the HTML report + diffs on failure
- uses: actions/upload-artifact@v4
  if: failure()
  with:
    name: visual-report
    path: |
      quality/visual/playwright-report
      quality/visual/test-results
```

A failed comparison exits non-zero and fails the pipeline. Approved baseline
updates flow through a reviewed pull request.

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Visual QA Engineer | Initial (Milestone 7) |
