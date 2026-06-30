# Automation — Quality Engineering Platform

> **Status:** Planned — delivered in **Milestone 5**.
> This file is a *module charter*. It documents intent and boundaries so the
> directory has a clear contract before any code lands. It is intentionally
> code-free during Milestone 1 (Foundation, Architecture & Governance).

## Purpose

Enterprise test automation ecosystem: Playwright, Selenium, Rest Assured, BDD (Cucumber), shared core, reporting, and a pluggable resource-adapter layer so tests target systems through interfaces, not URLs.

## Planned Contents

- `playwright/`, `selenium/`, `restassured/`, `bdd/`
- `shared/` — core, config, drivers, listeners, reporting, utilities,
  generators, assertions, database, security, accessibility, visual
- `resources/` — fixtures, PHI-safe test data

## Boundaries

- This module is **not** implemented during Milestone 1. No application, API,
  or automation code exists here yet.
- Build order and the exact scope are governed by [ROADMAP.md](../ROADMAP.md)
  and the master spec at
  [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md).

## References

- [ARCHITECTURE.md](../ARCHITECTURE.md)
- [PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md)
- [docs/](../docs/)
