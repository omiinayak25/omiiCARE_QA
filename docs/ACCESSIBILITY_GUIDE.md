# Accessibility Guide

> **Purpose.** Define how omiiCARE_QA achieves and verifies **WCAG 2.1 Level AA**
> conformance across the React frontend SUT. Accessibility is built in during the
> frontend milestone (**Milestone 4**) and verified by automated tooling in the
> advanced quality milestone (**Milestone 7**). This guide is documentation only
> (Milestone 1); it specifies the target the implementation must meet.

## Purpose

- Make the platform usable by people with visual, motor, auditory, and cognitive
  differences.
- Give engineers a concrete, component-level checklist rather than an abstract
  goal.
- Define how accessibility is measured, scored, and reported so conformance is
  evidence-based, not assumed.

## Scope

- **In scope:** WCAG 2.1 AA success criteria applied to the frontend portals,
  reusable component library, keyboard interaction, ARIA, color contrast,
  semantic HTML, screen-reader behavior, automated tooling, and manual checks.
- **Out of scope (v1.0):** WCAG AAA, native mobile accessibility, and formal
  accessibility certification. The platform targets AA conformance for
  educational and portfolio purposes; it makes **no certification claims**.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Frontend Engineer (M4) | Build accessible components; meet the checklist |
| Accessibility QA Engineer (M7) | Run axe-core/Lighthouse; perform manual audits |
| QA Architect | Own this guide and the success-criteria table |
| UX Designer | Provide accessible palettes, focus states, and layouts |

---

## 1. Principles (POUR)

| Principle | Meaning | What we do |
|-----------|---------|------------|
| **Perceivable** | Information presentable to all senses | Text alternatives, captions, contrast, scalable text |
| **Operable** | UI usable by any input method | Full keyboard access, visible focus, no time traps |
| **Understandable** | Predictable and clear | Consistent navigation, labeled inputs, helpful errors |
| **Robust** | Works with assistive tech | Valid semantic HTML, correct ARIA, name/role/value |

## 2. Accessibility Checklist by Component

| Component | Requirements |
|-----------|--------------|
| **Forms** | `<label>` bound to every control; required state announced; inline errors linked via `aria-describedby`; error summary focusable; no placeholder-as-label |
| **Tables** | `<th>` with `scope`; `<caption>`; sortable headers expose `aria-sort`; row actions reachable by keyboard; no layout tables |
| **Dialogs / Modals** | `role="dialog"` + `aria-modal`; focus trapped within; focus returns to trigger on close; `Esc` closes; labelled by title |
| **Navigation** | `<nav>` landmark; current item via `aria-current`; skip-to-content link; consistent order across portals |
| **Charts** | Text/table alternative for chart data; not color-only encoding; accessible name + summary; tooltips keyboard-reachable |
| **Buttons/Links** | Semantic `<button>`/`<a>`; descriptive accessible name; icon-only controls have `aria-label` |
| **Notifications/Toasts** | `aria-live` region (`polite`/`assertive`); dismissible by keyboard; sufficient dwell time |

## 3. Keyboard Navigation & Focus Order

- Every interactive element is reachable and operable with keyboard alone
  (`Tab`, `Shift+Tab`, `Enter`, `Space`, arrow keys for composites).
- **Focus order follows reading order**; DOM order matches visual order.
- **Visible focus indicator** on every focusable element (no `outline: none`
  without a replacement).
- **No keyboard traps**; modals trap focus intentionally and release on close.
- Custom widgets implement the WAI-ARIA Authoring Practices keyboard pattern for
  their role (menu, tabs, combobox, etc.).

## 4. ARIA Usage

- **First rule of ARIA:** prefer native HTML; use ARIA only when no semantic
  element exists.
- Provide accessible **name, role, and value** for every custom control.
- Use `aria-live` for dynamic updates, `aria-expanded`/`aria-controls` for
  disclosures, `aria-current` for active nav items.
- Never use ARIA to override correct native semantics; never leave stale ARIA
  state.

## 5. Color Contrast Ratios

| Content | Minimum ratio (AA) |
|---------|--------------------|
| Normal text (< 18pt / < 14pt bold) | **4.5:1** |
| Large text (≥ 18pt / ≥ 14pt bold) | **3:1** |
| UI components & graphical objects | **3:1** |
| Focus indicator vs background | **3:1** |

- Information is **never conveyed by color alone** (pair with icon/text/pattern).
- Both light and dark themes are validated against these ratios.

## 6. Semantic HTML & Screen-Reader Support

- Use landmarks (`header`, `nav`, `main`, `aside`, `footer`) and a single `<h1>`
  per view with a logical heading hierarchy.
- Page `<title>` and `lang` attribute set; route changes update both and move
  focus to the new view's heading.
- Images: meaningful `alt`; decorative images `alt=""`.
- Verified with NVDA, VoiceOver, and JAWS where available.

## 7. Tooling

| Tool | Type | Role |
|------|------|------|
| **axe-core** | Automated rules engine | Component- and page-level scans in the M7 suite; integrates with Playwright |
| **Lighthouse** | Automated audit | Accessibility score per route + actionable items |

- Automated tools catch roughly half of issues; **manual testing is mandatory**
  for the rest.

## 8. Scoring & Reporting

| Section | Contents |
|---------|----------|
| Summary | Routes audited, date, environment, pass/fail counts |
| Automated results | axe violations by impact (critical/serious/moderate/minor) |
| Lighthouse score | Per-route accessibility score + opportunities |
| Manual results | Keyboard, screen-reader, zoom/reflow findings |
| SC mapping | Each finding tied to a WCAG 2.1 success criterion |
| Remediation | Fix, owner, target milestone |

A clean automated scan is **not** a pass on its own; the manual checks below
must also pass.

## 9. Manual Accessibility Checks

- **Keyboard-only** walkthrough of every critical flow.
- **Screen-reader** pass on forms, tables, dialogs, and navigation.
- **200% zoom / reflow** at 320px width with no loss of content or function.
- **Reduced motion** honored (`prefers-reduced-motion`).
- **Contrast spot-check** of dynamic and state-dependent UI.

## 10. Success-Criteria Table (key WCAG 2.1 AA)

| SC | Name | Level | What it requires |
|----|------|-------|------------------|
| 1.1.1 | Non-text Content | A | Text alternatives for images/icons/charts |
| 1.3.1 | Info & Relationships | A | Structure conveyed programmatically (labels, headings, table semantics) |
| **1.4.3** | Contrast (Minimum) | AA | 4.5:1 normal / 3:1 large text |
| 1.4.4 | Resize Text | AA | Usable at 200% zoom without loss |
| 1.4.10 | Reflow | AA | No 2-D scroll at 320px width |
| 1.4.11 | Non-text Contrast | AA | 3:1 for UI components & focus |
| **2.1.1** | Keyboard | A | All functionality via keyboard |
| 2.1.2 | No Keyboard Trap | A | Focus can always move away |
| 2.4.3 | Focus Order | A | Logical, meaningful focus order |
| **2.4.7** | Focus Visible | AA | Visible keyboard focus indicator |
| 3.3.1 | Error Identification | A | Errors described in text |
| 3.3.2 | Labels or Instructions | A | Inputs have labels/instructions |
| **4.1.2** | Name, Role, Value | A | Custom controls expose correct semantics to AT |
| 4.1.3 | Status Messages | AA | Dynamic status announced without focus change |

## Examples

- **Modal:** a "Schedule Appointment" dialog uses `role="dialog"`, traps focus,
  closes on `Esc`, and returns focus to the trigger — satisfying 2.1.1, 2.1.2,
  and 4.1.2.
- **Contrast:** a disabled-button gray of 2.9:1 fails 1.4.3 in axe; the palette is
  corrected to ≥ 4.5:1 and re-verified.
- **Chart:** a vitals chart adds a visually-hidden data table and a text summary,
  satisfying 1.1.1 for non-text content.

## Future Enhancements

- Add automated keyboard-traversal and screen-reader snapshot testing.
- Track an accessibility score trend in CI dashboards (M8).
- Evaluate selective WCAG AAA criteria for high-impact clinical flows (post-1.0).

## Dependencies

- Implemented by the frontend in [UI_UX_SPECIFICATION.md](UI_UX_SPECIFICATION.md)
  and [ROADMAP.md](../ROADMAP.md) Milestone 4.
- Verified by the M7 quality framework; tooling anchored by
  [PROJECT_METADATA.md](PROJECT_METADATA.md) §3.

## References

- W3C WCAG 2.1; WAI-ARIA Authoring Practices Guide.
- Deque axe-core docs; Google Lighthouse docs.
- [SECURITY_TESTING_GUIDE.md](SECURITY_TESTING_GUIDE.md),
  [PERFORMANCE_GUIDE.md](PERFORMANCE_GUIDE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Accessibility/QA Specialist | Initial WCAG 2.1 AA accessibility guide (Milestone 1) |
