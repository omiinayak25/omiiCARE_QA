# Visual Regression Testing — omiiCARE_QA

> Pixel-level visual regression testing for the omiiCARE React frontend
> (`http://localhost:5173`) using **Playwright's
> [`toHaveScreenshot()`](https://playwright.dev/docs/test-snapshots)**
> assertion. Catches unintended UI changes — layout shifts, color/theme
> regressions, broken responsive breakpoints, and component drift.

---

## 1. Approach

Visual regression works by comparing a **fresh screenshot** of a page/component
against a previously approved **baseline image**:

```
  render page  ──►  capture screenshot  ──►  compare to baseline
                                               │
                          ┌────────────────────┴───────────────────┐
                          ▼                                         ▼
                 within maxDiffPixelRatio                     exceeds threshold
                       PASS                                       FAIL
                                                          (writes -diff & -actual)
```

- **Baseline** (`*-expected.png`) — the approved reference, committed to git.
- **Actual** (`*-actual.png`) — what rendered this run.
- **Diff** (`*-diff.png`) — highlighted pixels that changed (generated only on failure).

Playwright computes the difference with `pixelmatch`; a test fails when the
changed-pixel ratio exceeds `maxDiffPixelRatio` (configured in
`playwright.config.ts`).

## 2. What We Capture

| Spec | Page | Variants |
|------|------|----------|
| `login.visual.spec.ts` | `/login` | light theme, dark theme |
| `dashboard.visual.spec.ts` | `/` (authenticated) | light theme, dark theme |

Each variant is captured on **two projects**: desktop Chromium and a mobile
viewport (Pixel 5) — so responsive breakpoints and cross-device layout are
covered by the same specs.

## 3. Baseline & Diff Workflow

| Step | Command | Result |
|------|---------|--------|
| Create / update baselines | `npm run update` | Writes `*-expected.png` snapshots |
| Run comparison | `npm run test:visual` | Pass/fail vs. baselines |
| Inspect a failure | `npx playwright show-report` | Side-by-side expected / actual / diff |

### Approval workflow

1. A spec fails -> open the HTML report and inspect the **diff** image.
2. **Regression?** Fix the frontend, re-run. Do **not** update the baseline.
3. **Intended change?** Re-run with `npm run update` to regenerate that
   baseline, then review the new `*-expected.png` in the pull request diff.
4. A reviewer **approves the new baseline images in code review** — the PNG
   diff is visible on GitHub. Baselines are never auto-accepted in CI.

## 4. Responsive, Dark Mode & Cross-Browser

- **Responsive** — the `chromium-mobile` project (Pixel 5 viewport) re-runs
  every spec at a mobile breakpoint. Add more viewports as projects in
  `playwright.config.ts`.
- **Dark mode** — specs toggle theme via the `toggle-theme` control and capture
  a separate dark baseline (`*-dark.png`).
- **Cross-browser** — add `webkit` / `firefox` projects to broaden coverage;
  each browser gets its own baseline folder via `snapshotPathTemplate`.

## 5. Screenshot History & Storage

- Baselines live under `tests/__screenshots__/{projectName}/{testFilePath}/`
  (set by `snapshotPathTemplate`) and are **committed to git**.
- Git history is the screenshot history: every baseline change is a reviewable
  commit with a visible PNG diff.
- Failure artifacts (`*-actual.png`, `*-diff.png`) are written to
  `test-results/` and attached to the HTML report; they are **git-ignored**.

## 6. Reporting

- `npm run test:visual` prints a pass/fail list and emits an HTML report
  (`playwright-report/`) with embedded expected/actual/diff triplets.
- A JSON reporter (`visual-results.json`) feeds CI dashboards.
- See [`EXECUTION_GUIDE.md`](./EXECUTION_GUIDE.md) for baseline creation, the
  review/approval workflow, and CI considerations (font/AA determinism).

## 7. Directory Layout

```
quality/visual/
├── README.md                    # this file
├── package.json                 # omiicare-visual-tests
├── playwright.config.ts         # snapshotPathTemplate + toHaveScreenshot opts
├── tests/
│   ├── login.visual.spec.ts     # /login light + dark
│   ├── dashboard.visual.spec.ts # dashboard light + dark
│   └── __screenshots__/         # committed baselines (generated)
├── EXECUTION_GUIDE.md
```

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Visual QA Engineer | Initial (Milestone 7) |
