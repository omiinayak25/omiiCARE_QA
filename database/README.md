# Database — Schema, Migrations & Seed Data

> **Status:** Planned — delivered in **Milestone 2**.
> This file is a *module charter*. It documents intent and boundaries so the
> directory has a clear contract before any code lands. It is intentionally
> code-free during Milestone 1 (Foundation, Architecture & Governance).

## Purpose

Profile-driven hybrid database module (H2 / PostgreSQL) with Flyway migrations, repeatable migrations, PHI-safe healthcare seed data, and rollback documentation.

## Planned Contents

- `migrations/`, `repeatable/`, `seeds/`, `scripts/`
- `backups/`, `restore/`, `test-data/`, `documentation/`

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
