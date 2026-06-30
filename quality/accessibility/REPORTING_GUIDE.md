# Accessibility Reporting Guide — omiiCARE_QA

How to read axe-core and Lighthouse output, interpret scores, and map findings
to WCAG 2.1 success criteria.

---

## 1. axe-core Result Structure

Each scan returns four arrays:

| Array | Meaning | Action |
|-------|---------|--------|
| `violations` | Confirmed accessibility failures | **Must be empty** — fails the test |
| `passes` | Rules that passed | Evidence of conformance |
| `incomplete` | Needs manual review (axe couldn't decide) | Triage by hand |
| `inapplicable` | Rule not relevant to this page | Informational |

A single violation looks like:

```json
{
  "id": "color-contrast",
  "impact": "serious",
  "tags": ["wcag2aa", "wcag143"],
  "help": "Elements must meet minimum color contrast ratio thresholds",
  "helpUrl": "https://dequeuniversity.com/rules/axe/4.10/color-contrast",
  "nodes": [
    { "target": ["button.MuiButton-root"], "failureSummary": "..." }
  ]
}
```

**Read it as:** rule `id` → severity `impact` → `help` (what to fix) →
`helpUrl` (how) → `nodes[].target` (which element).

### Impact levels

| Impact | Meaning | Priority |
|--------|---------|----------|
| `critical` | Blocks a user group entirely | Fix immediately |
| `serious` | Major barrier | Fix this release |
| `moderate` | Noticeable difficulty | Schedule |
| `minor` | Small inconvenience | Backlog |

## 2. Lighthouse Accessibility Score

- Score is a **weighted average** of audits (0.0–1.0), shown as 0–100.
- Our gate: **>= 0.90** (`categories:accessibility` assertion in `lighthouserc.json`).
- A score of 1.0 does **not** mean fully accessible — Lighthouse runs a subset
  of axe rules. Always pair with the axe suite and the manual checklist.

| Score | Reading |
|-------|---------|
| 0.90–1.00 | Passing gate; review individual audits for `warn` items |
| 0.50–0.89 | Below gate; build fails; address failed audits |
| < 0.50 | Significant gaps; structural review needed |

Open the uploaded report (temporary public storage URL) -> **Accessibility**
section -> expand each failed audit for the failing elements and remediation.

## 3. WCAG Success-Criteria Mapping Table

Maps the most common axe rule / Lighthouse audit ids to WCAG 2.1 success
criteria. Use this to translate a tool failure into a compliance statement.

| Tool rule id | WCAG SC | Level | Principle | What it verifies |
|--------------|---------|-------|-----------|------------------|
| `color-contrast` | 1.4.3 Contrast (Minimum) | AA | Perceivable | Text/background ratio >= 4.5:1 (3:1 large) |
| `image-alt` | 1.1.1 Non-text Content | A | Perceivable | Images have text alternatives |
| `link-name` | 2.4.4 Link Purpose | A | Operable | Links have discernible text |
| `button-name` | 4.1.2 Name, Role, Value | A | Robust | Buttons have accessible names |
| `label` | 3.3.2 Labels or Instructions | A | Understandable | Form inputs are labelled |
| `form-field-multiple-labels` | 3.3.2 | A | Understandable | One unambiguous label per field |
| `aria-required-attr` | 4.1.2 | A | Robust | ARIA roles have required attributes |
| `aria-valid-attr` / `aria-valid-attr-value` | 4.1.2 | A | Robust | Valid ARIA attributes & values |
| `aria-allowed-attr` | 4.1.2 | A | Robust | No disallowed ARIA on a role |
| `aria-hidden-focus` | 4.1.2 | A | Robust | Hidden elements aren't focusable |
| `document-title` | 2.4.2 Page Titled | A | Operable | Page has a `<title>` |
| `html-has-lang` | 3.1.1 Language of Page | A | Understandable | `<html lang>` is set |
| `html-lang-valid` | 3.1.1 | A | Understandable | `lang` value is valid |
| `heading-order` | 1.3.1 Info & Relationships | A | Perceivable | Headings increase by one level |
| `list` / `listitem` | 1.3.1 | A | Perceivable | Lists use correct markup |
| `landmark-one-main` | 1.3.1 / 2.4.1 | A | Perceivable/Operable | Exactly one `<main>` landmark |
| `region` | 1.3.1 | A | Perceivable | Content sits in landmarks |
| `bypass` | 2.4.1 Bypass Blocks | A | Operable | Skip link / landmarks present |
| `tabindex` | 2.4.3 Focus Order | A | Operable | No positive tabindex breaking order |
| `focus-order-semantics` | 2.4.3 | A | Operable | Focusable elements are interactive |
| `duplicate-id-aria` | 4.1.1 Parsing | A | Robust | IDs referenced by ARIA are unique |
| `aria-roles` | 4.1.2 | A | Robust | Valid role values |
| `meta-viewport` | 1.4.4 Resize Text | AA | Perceivable | Zoom not disabled by viewport meta |

> Full WCAG to axe mapping: <https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md>

## 4. Sample Report Summary (template)

```
Accessibility Run — 2026-06-30
SUT: http://localhost:5173 | axe-core 4.10 | Lighthouse 11

Page         | axe violations | Lighthouse a11y | Gate
-------------|----------------|-----------------|------
/login       | 0              | 0.97            | PASS
/ (light)    | 0              | 0.94            | PASS
/ (dark)     | 0              | n/a             | PASS

Manual checklist: 7/7 passed
Result: PASS — WCAG 2.1 AA gate met
```

## 5. Triage Workflow

1. Run `npm run test:a11y` -> collect `violations` + `incomplete`.
2. For each violation: read `id` -> map to WCAG SC above -> open `helpUrl`.
3. Fix in the frontend (`apps/frontend/src/...`); re-run the single spec.
4. Manually verify `incomplete` items; record outcome in the run summary.
5. Run `npm run lighthouse`; confirm each URL >= 0.90.
6. Complete the manual checklist (`EXECUTION_GUIDE.md` section 6).
7. Attach the run summary to the release / Jira ticket.

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Accessibility Engineer | Initial (Milestone 7) |
