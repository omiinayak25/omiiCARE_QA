# 0006. React + TypeScript + Vite + Material UI frontend

## Status

Accepted — 2026-06-30

## Context / Problem

The frontend (Milestone 4) is the platform's primary System Under Test: a
production-quality web app with role-based portals for twelve RBAC roles,
accessibility to WCAG AA, responsive/PWA behavior, and — critically — testability
by design so the M5 automation framework needs no app changes. We must choose the
frontend library, language, build tool, and UI kit now, because they determine
component structure, type safety, selector stability, and the developer-experience
of every later frontend feature and test.

## Decision Drivers

- Testability: stable, accessible selectors and consistent DOM for automation.
- Type safety across a large multi-portal application.
- Fast developer feedback (dev server, HMR, build speed).
- A mature, accessible component library to reach WCAG AA efficiently.
- Portfolio relevance and a large hiring/talent ecosystem.

## Alternatives Considered

### Alternative A — React 18 + TypeScript + Vite + Material UI (chosen)
- **Pros:** React's ecosystem is the largest and most testing-friendly (Testing
  Library, Playwright, Selenium); TypeScript gives strong typing across portals;
  Vite delivers fast dev startup and HMR and modern builds; Material UI provides
  accessible, themeable components that accelerate WCAG AA; pairs cleanly with
  React Router, TanStack Query, React Hook Form + Zod, and i18next from the
  technology matrix; high portfolio signal.
- **Cons:** React requires deliberate state-management choices; Material UI adds
  bundle weight and a theming learning curve; flexibility demands disciplined
  conventions.

### Alternative B — Angular
- **Pros:** batteries-included, opinionated, strong typing, enterprise pedigree.
- **Cons:** heavier framework and steeper ramp; the React + TS + Vite combination
  better showcases modern, composable frontend engineering for this portfolio.

### Alternative C — Vue 3 + Vite
- **Pros:** approachable, excellent Vite integration, good performance.
- **Cons:** smaller enterprise-healthcare and automation-tooling footprint than
  React; weaker portfolio signal for the target roles.

### Alternative D — React with Create React App / Webpack
- **Pros:** familiar, long-established React tooling.
- **Cons:** CRA is effectively unmaintained and slower; Vite is the modern
  standard for dev speed and build ergonomics.

## Decision

We will build the frontend with **React 18+**, **TypeScript**, **Vite**, and
**Material UI** (preferred UI kit), composed with React Router, TanStack Query,
Axios, React Hook Form + Zod, Redux Toolkit (only where appropriate), i18next,
Day.js, and Chart.js/ECharts, as registered in
[docs/PROJECT_METADATA.md](../../PROJECT_METADATA.md) §3. Components will expose
stable IDs and accessible selectors so the automation platform needs no app changes.

## Consequences / Tradeoffs

**Positive**
- Fast developer feedback (Vite) and strong typing (TS) across all portals.
- Accessible, themeable components (Material UI) speed WCAG AA conformance.
- The richest automation/testing ecosystem (Playwright/Selenium/Testing Library).
- Testability-by-design selectors keep the SUT and the test suite decoupled.

**Negative / Accepted tradeoffs**
- State-management and conventions must be chosen and documented deliberately.
- Material UI adds bundle size and a theming learning curve.
- React's flexibility requires enforced project conventions to stay consistent.

## Future Impact

The modular, feature-first structure and stable selectors directly enable the M5
automation platform and M7 visual/accessibility testing without app rework. The
PWA/responsive strategy leaves a seam toward native-mobile responsive automation
on the post-1.0 roadmap. The component library and theming support future portal
expansion (v1.1).

## References

- [docs/PROJECT_METADATA.md](../../PROJECT_METADATA.md) §3 (Frontend)
- [ARCHITECTURE.md](../../../ARCHITECTURE.md) §5 (Frontend architecture)
- [ROADMAP.md](../../../ROADMAP.md) Milestones 4 and 7
- React, Vite, Material UI, TanStack Query documentation.
