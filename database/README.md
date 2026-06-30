# Database — Documentation & Operations

> **Operational and documentation home for the omiiCARE_QA database.**
> Delivered in **Milestone 2** (infrastructure foundation). The live Flyway
> migrations run from `apps/backend`; this module holds the standards,
> rollback strategy, changelog, seed catalog, and helper scripts.

## Purpose

Be the single place an engineer goes to understand how the database is
versioned, seeded, backed up, and recovered — and to find the helper scripts to
operate it. The executable schema lives with the backend; the *knowledge and
operations* of the database live here.

## Scope

- **In scope:** migration naming standards, rollback strategy, human-readable
  changelog, seed-data catalog, and operational scripts/directories
  (`backups/`, `restore/`, `test-data/`).
- **Out of scope:** the migration DDL itself (it lives in `apps/backend`), and
  JPA entities (backend, M3).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Database Engineer | Maintain standards, changelog, seed catalog, scripts |
| Backend Engineer | Author migrations in `apps/backend` following these standards |
| QA Engineer | Use seed/test-data fixtures; verify migration state |
| Release Engineer | Run backup/restore procedures in qa/stage/prod |

---

## 1. Where Live Migrations Actually Live

The versioned schema and repeatable seed are **not** in this directory — they
ship with the backend application so Spring Boot can apply them:

| Path | Contents |
|------|----------|
| `apps/backend/src/main/resources/db/migration/` | Versioned schema (`V1__baseline_platform_schema.sql`, …) |
| `apps/backend/src/main/resources/db/seed/` | Repeatable seeds (`R__seed_reference_and_demo.sql`) |

This `database/` module is the documentation and operations home; the
`migrations/`, `repeatable/`, and `seeds/` sub-directories here are reserved
placeholders kept in sync with the blueprint and may hold staged or vendor-split
artifacts as the schema grows.

## 2. How Migrations Run

- **Spring Boot Flyway runs on application startup.** Flyway applies all pending
  versioned migrations in order, then any changed repeatable migrations.
- Hibernate runs in `validate` mode — it never alters the schema; Flyway owns it.
- No standalone Flyway install is required for normal runs. For status, use
  `scripts/flyway-info.sh` or query `flyway_schema_history`.

## 3. Profile → Location Mapping

`flyway.locations` is set per Spring profile:

| Profile(s) | Engine | Locations loaded | Seeds? |
|------------|--------|------------------|--------|
| dev, test | H2 (PostgreSQL compat) | `db/migration` + `db/seed` | Yes |
| local, docker, qa | PostgreSQL | `db/migration` + `db/seed` | Yes |
| stage, prod | PostgreSQL | `db/migration` only | No |

Schema always loads; synthetic demo seeds load only in non-production profiles.

## 4. Module Structure

```
database/
├── documentation/   Standards & operational docs (see §5)
├── scripts/         Helper scripts: psql-connect.sh, flyway-info.sh
├── backups/         Pre-migrate backup runbooks & artifacts
├── restore/         Restore runbooks & PITR helpers
├── test-data/       QA fixtures beyond the Flyway seed
├── migrations/      (reserved) staged/vendor-split versioned artifacts
├── repeatable/      (reserved) staged repeatable artifacts
└── seeds/           (reserved) staged seed artifacts
```

## 5. Documentation Index

| Document | Covers |
|----------|--------|
| [MIGRATION_NAMING_STANDARDS.md](documentation/MIGRATION_NAMING_STANDARDS.md) | V/R/U naming, locations, versioning rules, do/don't |
| [ROLLBACK_STRATEGY.md](documentation/ROLLBACK_STRATEGY.md) | Forward-fix, undo, backup, PITR, transactional DDL |
| [DATABASE_CHANGELOG.md](documentation/DATABASE_CHANGELOG.md) | Human-readable schema version history |
| [SEED_DATA_CATALOG.md](documentation/SEED_DATA_CATALOG.md) | Roles, permissions, demo tenant/org/user |

## 6. Helper Scripts

| Script | Purpose |
|--------|---------|
| [scripts/psql-connect.sh](scripts/psql-connect.sh) | Open a psql shell to the docker PostgreSQL (env-driven defaults) |
| [scripts/flyway-info.sh](scripts/flyway-info.sh) | Show migration status (Maven plugin or documented alternatives) |

## 7. Conventions

- Migrations are forward-only; correct forward, never edit a merged migration.
- All seed/test data is SYNTHETIC and PHI-safe.
- Migrations stay portable across H2 and PostgreSQL.

## 8. References

- [docs/DATABASE_BLUEPRINT.md](../docs/DATABASE_BLUEPRINT.md) — target schema vision.
- [ARCHITECTURE.md](../ARCHITECTURE.md), [PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md)
- Live migrations: `apps/backend/src/main/resources/db/migration` and `.../db/seed`.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Database Engineer | Initial module README (Milestone 2) |
