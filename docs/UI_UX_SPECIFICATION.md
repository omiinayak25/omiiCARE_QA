# UI/UX Specification

> **Purpose.** Specify the user-experience and interface design of the omiiCARE_QA
> frontend — the primary **System Under Test (SUT)**. It defines the design
> principles, role-based portals, component library, theming, responsiveness,
> accessibility-by-default, internationalization, and the testing-readiness
> contract the M5 automation framework relies on. This guide is documentation
> only (Milestone 1); the frontend is built in **Milestone 4**.

## Purpose

- Give frontend engineers a single, authoritative description of *what* the UI
  must be and *how* it must behave.
- Guarantee the SUT is testable by design: stable selectors, consistent DOM, and
  predictable states.
- Ensure accessibility, i18n, and theming are foundational, not retrofitted.

## Scope

- **In scope:** design principles, the role-based portals and their screens, the
  common application shell, the reusable component library, theming, responsive
  breakpoints, accessibility defaults, i18n, testing-readiness, and UI states.
- **Out of scope (v1.0):** native mobile apps, pixel-level visual design assets,
  and per-screen wireframes (delivered in M4). Stack choices are fixed by
  [PROJECT_METADATA.md](PROJECT_METADATA.md) §3 (React 18+, TypeScript, Vite,
  MUI, React Router, TanStack Query, React Hook Form + Zod, i18next).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| UX Designer | Information architecture, flows, visual language |
| Frontend Engineer (M4) | Implement portals, shell, and component library |
| Accessibility QA (M7) | Verify a11y defaults (see [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md)) |
| Automation Engineer (M5) | Consume stable selectors; no app changes needed |

---

## 1. Design Principles

| Principle | Meaning |
|-----------|---------|
| Clarity over cleverness | Clinical context demands legible, unambiguous UI |
| Role-appropriate density | Show each role exactly what it needs, no more |
| Consistency | One shell, one component library, one interaction grammar |
| Accessibility by default | WCAG 2.1 AA is the baseline, not an option |
| Testability by design | Stable IDs/selectors and consistent DOM everywhere |
| Safety | Confirm destructive actions; prevent data loss; clear errors |
| Performance | Fast first paint, lazy-loaded routes, responsive interactions |

## 2. Role-Based Portals

Portals map to the RBAC roles in [PROJECT_METADATA.md](PROJECT_METADATA.md) §5.
Each portal is permission-guarded; users see only authorized features.

| Portal | Key screens / scope |
|--------|---------------------|
| **Patient** | Profile, appointments, visit history, lab/radiology results, prescriptions, bills/payments, messages |
| **Doctor** | Patient list, encounter/consultation, orders (lab/radiology/Rx), clinical notes, schedule |
| **Nurse** | Ward/patient board, vitals entry, medication administration, care tasks, handover notes |
| **Reception** | Patient registration, appointment scheduling, check-in/out, queue management |
| **Laboratory** | Order worklist, sample tracking, result entry/verification, report release |
| **Radiology** | Imaging order worklist, study status, report entry, result release |
| **Pharmacy** | Prescription queue, dispensing, inventory view, interaction flags |
| **Billing** | Invoices, charges, payments, adjustments, statements |
| **Insurance** | Claims, eligibility, pre-authorization, claim status tracking |
| **Hospital Admin** | User/role management, department config, schedules, operational reports |
| **Super Admin** | Tenant management, global config, feature-flag visibility, system audit |

> An **Auditor** role (read-only audit/compliance views) is also defined in the
> role taxonomy and consumes the audit screens of the common shell.

## 3. Common Application Shell

Shared across all portals for consistency and testability:

- **Dashboard** — role-specific landing with key metrics and quick actions.
- **Navigation** — landmark `<nav>`, role-filtered menu, breadcrumbs, skip link.
- **Profile** — user details, password/security, language preference.
- **Settings** — theme, notifications, accessibility preferences.
- **Notifications** — `aria-live` notification center and toasts.
- **Help** — contextual help and documentation links.
- **Audit** — activity/audit log views (full for Admin/Auditor; scoped otherwise).

## 4. Reusable Component Library

| Category | Components |
|----------|------------|
| Inputs | TextField, Select, Combobox, DatePicker, Checkbox, Radio, Switch, FileUpload |
| Forms | Form, FormSection, FieldError, FormActions (React Hook Form + Zod) |
| Data display | Table (sortable/paginated), DataGrid, Card, Badge, Tag, KeyValue |
| Feedback | Toast, Alert, Banner, Skeleton, Spinner, EmptyState, ErrorState |
| Overlays | Modal/Dialog, Drawer, Popover, Tooltip, ConfirmDialog |
| Navigation | AppBar, SideNav, Breadcrumbs, Tabs, Pagination, Menu |
| Charts | LineChart, BarChart, PieChart (with accessible data alternatives) |
| Layout | PageLayout, Grid, Stack, Divider, Section |

All components are typed, themeable, accessible, and expose stable test
selectors (§8).

## 5. Theming

- **Light and dark themes** with an enterprise, clinical-neutral palette.
- Centralized design tokens (color, spacing, typography, elevation, radius);
  no hardcoded values in components.
- Both themes meet the contrast ratios in
  [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md) §5.
- Theme switch persists per user and respects the OS `prefers-color-scheme`.

## 6. Responsive Breakpoints

| Breakpoint | Target | Layout intent |
|------------|--------|---------------|
| Mobile | < 600px | Single column, collapsible nav, stacked actions |
| Tablet | 600–904px | Two-column where useful, drawer nav |
| Laptop | 905–1239px | Full nav rail, multi-column content |
| Desktop | 1240–1599px | Dense data views, side panels |
| Large | ≥ 1600px | Max content width, expanded dashboards |

Layouts reflow without horizontal scroll and remain usable at 200% zoom
(WCAG 1.4.10).

## 7. Accessibility by Default

- Every component ships keyboard support, focus management, ARIA, and labels per
  [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md).
- Semantic HTML and landmarks structure every page; one `<h1>` per view.
- Color never carries meaning alone; focus is always visible.

## 8. Testing-Readiness (Contract with Automation)

- **Stable selectors:** every interactive/asserted element exposes a
  deterministic `data-testid` (and an accessible name) that does not change with
  styling, locale, or content.
- **Consistent DOM:** the same component renders the same structure everywhere;
  no order/structure changes that would break selectors.
- **No random IDs** in test-relevant attributes; generated IDs are stable across
  renders for a given element.
- **Predictable states:** loading/empty/error/success states are explicit and
  individually addressable.
- This contract lets the M5 automation framework target the SUT **without any app
  changes**, per [ARCHITECTURE.md](../ARCHITECTURE.md) §5.

## 9. Internationalization (i18n)

- **No hardcoded user-facing strings**; all text flows through i18next keys.
- Dates, numbers, and currency are locale-formatted (Day.js).
- Layouts tolerate text expansion and are RTL-ready in structure.
- Language preference lives in the user profile and the app shell.

## 10. Error, Empty, and Loading States

| State | Requirement |
|-------|-------------|
| Loading | Skeletons/spinners with accessible status; no layout shift |
| Empty | Friendly EmptyState with cause and next action; not a blank screen |
| Error | Clear message, recovery action, no raw stack traces; `aria-live` announce |
| Success | Confirmation toast/inline message; focus management preserved |

## Examples

- **Doctor orders a lab test:** uses the shared Form + ConfirmDialog components;
  the submit button carries `data-testid="order-lab-submit"`, enabling the M5
  suite to assert success without touching the app.
- **Reception on a tablet:** the SideNav collapses to a drawer at the tablet
  breakpoint; the registration form reflows to a single column with no horizontal
  scroll.
- **Empty worklist:** the Laboratory worklist renders an EmptyState ("No pending
  orders") rather than an empty table, keeping the loading/empty/error states
  distinct and testable.

## Future Enhancements

- Progressive Web App polish (offline-aware shell) within v1.0 frontend scope.
- Component-level visual regression baselines (M7 visual testing).
- Design-token sync with Figma and Storybook documentation (post-1.0).

## Dependencies

- Realized by [ROADMAP.md](../ROADMAP.md) Milestone 4; consumed by Milestone 5
  automation.
- Roles from [PROJECT_METADATA.md](PROJECT_METADATA.md) §5; a11y from
  [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md); architecture from
  [ARCHITECTURE.md](../ARCHITECTURE.md) §5.

## References

- Material UI, React Router, TanStack Query, React Hook Form, Zod, i18next docs.
- [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md),
  [PERFORMANCE_GUIDE.md](PERFORMANCE_GUIDE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect / UX | Initial UI/UX specification for the frontend SUT (Milestone 1) |
