# Accessibility Testing — omiiCARE_QA

> Automated and assisted **WCAG 2.1 Level AA** conformance testing for the
> omiiCARE React frontend (`http://localhost:5173`). Built with
> [axe-core](https://github.com/dequelabs/axe-core) (via
> `@axe-core/playwright`) and [Lighthouse CI](https://github.com/GoogleChrome/lighthouse-ci).

---

## 1. Goal & Standard

The system under test (SUT) is a healthcare application. Accessibility is a
**non-functional requirement and a compliance concern** (Section 508 / ADA /
EN 301 549, all of which reference WCAG). The target conformance level is:

| Item | Value |
|------|-------|
| Standard | WCAG 2.1 |
| Conformance level | **AA** (includes all A criteria) |
| Tooling | axe-core 4.x, Lighthouse 11.x |
| Pages in scope | `/login`, `/` (dashboard), `/patients`, `/appointments` |
| Browser engine | Chromium (Playwright) |
| Themes | Light + Dark (MUI palette) |

## 2. POUR — The Four Principles

WCAG is organized under four principles. Our checks map to each:

| Principle | Meaning | What we verify |
|-----------|---------|----------------|
| **Perceivable** | Information must be presentable in ways users can perceive | Color contrast (1.4.3), text alternatives (1.1.1), info & relationships (1.3.1), reflow |
| **Operable** | UI components must be operable | Keyboard access (2.1.1), no keyboard trap (2.1.2), focus order (2.4.3), visible focus (2.4.7), bypass blocks (2.4.1) |
| **Understandable** | Information & operation must be understandable | Labels/instructions (3.3.2), error identification (3.3.1), consistent navigation (3.2.3), page language (3.1.1) |
| **Robust** | Content must be robust enough for assistive tech | Valid name/role/value (4.1.2), parsing (4.1.1), status messages (4.1.3) |

## 3. What Is Checked

### Automated (axe-core + Lighthouse)
- **Color contrast** — text vs. background ratio ≥ 4.5:1 (≥ 3:1 large text), both themes.
- **ARIA** — valid roles, required attributes, allowed attribute values, no broken `aria-*` references.
- **Semantic HTML** — landmarks (`<main>`, `<nav>`, `<header>`), heading order, lists.
- **Forms** — every input has a programmatic label; `login-username` / `login-password` resolve to accessible names.
- **Tables** — `patients-table` / `appointments-table` have headers and a caption/`aria-label`.
- **Dialogs** — MUI `Dialog` exposes `role="dialog"`, `aria-modal`, and focus trapping.
- **Images / icons** — `alt` text or `aria-hidden` on decorative icons.
- **Page structure** — single `<h1>`, document `lang`, unique landmarks.

### Assisted / manual (documented, run alongside automation)
- **Keyboard navigation** — tab through `/login` and dashboard; all interactive elements reachable in a logical order.
- **Focus visible** — focus ring present on buttons, links, `toggle-theme`, `logout`.
- **No keyboard trap** — focus can leave dialogs via `Esc` / `Tab`.
- **Screen reader** — NVDA / VoiceOver spot checks for announced names, roles, and live-region status (`login-error`).

> Automation reliably catches ~30–40% of WCAG issues. The manual checklist
> (see `EXECUTION_GUIDE.md`) covers the remainder.

## 4. Scoring & Reporting

| Source | Pass criteria |
|--------|---------------|
| axe-core | **Zero** violations of `wcag2a`, `wcag2aa`, `wcag21a`, `wcag21aa` tags |
| Lighthouse | Accessibility category score **≥ 0.90** per URL |

- axe results are asserted directly in Playwright specs (`tests/*.a11y.spec.ts`).
- A serialized violation list is attached to the Playwright HTML report for triage.
- Lighthouse CI uploads scores to temporary public storage and fails the build on assertion breach.
- See `REPORTING_GUIDE.md` for the WCAG success-criteria → tool mapping table.

## 5. How to Run

```bash
cd quality/accessibility
npm install
npx playwright install chromium

# Start the SUT in another terminal first: frontend on http://localhost:5173

npm run test:a11y       # axe-core scans via Playwright
npm run lighthouse      # Lighthouse CI accessibility audit
```

Full instructions, including starting the SUT and interpreting output, are in
[`EXECUTION_GUIDE.md`](./EXECUTION_GUIDE.md).

## 6. Directory Layout

```
quality/accessibility/
├── README.md                    # this file
├── package.json                 # omiicare-a11y-tests
├── playwright.config.ts         # axe runner config
├── lighthouserc.json            # Lighthouse CI config
├── tests/
│   ├── login.a11y.spec.ts       # axe scan of /login
│   └── dashboard.a11y.spec.ts   # axe scan of dashboard (post-login)
├── EXECUTION_GUIDE.md
└── REPORTING_GUIDE.md
```

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Accessibility Engineer | Initial (Milestone 7) |
