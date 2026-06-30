# Frontend — Healthcare Web Application (SUT)

> **Status:** Planned — delivered in **Milestone 4**.
> This file is a *module charter*. It documents intent and boundaries so the
> directory has a clear contract before any code lands. It is intentionally
> code-free during Milestone 1 (Foundation, Architecture & Governance).

## Purpose

React + TypeScript + Vite web application that becomes the primary System Under Test. Role-based portals (Patient, Doctor, Reception, Lab, Radiology, Pharmacy, Billing, Insurance, Admin, Super Admin), accessible (WCAG AA) and responsive.

## Planned Contents

- `src/app/`, `src/features/`, `src/components/`, `src/layouts/`
- `src/services/`, `src/hooks/`, `src/contexts/`, `src/routing/`
- i18n, theming, error boundaries, stable test selectors

## Boundaries

- This module is **not** implemented during Milestone 1. No application, API,
  or automation code exists here yet.
- Build order and the exact scope are governed by [ROADMAP.md](../../ROADMAP.md)
  and the master spec at
  [MASTER_PROJECT_SPECIFICATION.md](../../MASTER_PROJECT_SPECIFICATION.md).

## References

- [ARCHITECTURE.md](../../ARCHITECTURE.md)
- [PROJECT_STRUCTURE.md](../../PROJECT_STRUCTURE.md)
- [docs/](../../docs/)
