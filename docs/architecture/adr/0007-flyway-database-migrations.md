# 0007. Flyway for versioned database migrations

## Status

Accepted — 2026-06-30

## Context / Problem

The platform's schema must evolve predictably across a hybrid database
(H2 ↔ PostgreSQL, see [0002](0002-hybrid-database-h2-postgresql.md)) and across
the full environment matrix (dev/test/local/docker/qa/stage/prod). We need a
single, auditable, repeatable mechanism that applies the same versioned schema to
every engine and environment, supports PHI-safe seed data, and never relies on an
ORM silently mutating production schemas. We must decide the migration tool and
policy now, because Milestone 2 builds the database module and every later
milestone depends on a trustworthy schema-evolution process.

## Decision Drivers

- Deterministic, version-controlled, reviewable schema changes (one source of truth).
- Same migrations apply to both H2 and PostgreSQL.
- Repeatable migrations and seed loading for tests and demos.
- No implicit ORM schema generation against real databases.
- Strong, mature ecosystem and Spring Boot integration.

## Alternatives Considered

### Alternative A — Flyway (chosen)
- **Pros:** simple SQL-first migrations are transparent and reviewable; first-class
  Spring Boot integration; versioned (`V`) plus repeatable (`R`) migrations fit
  seeds and views; baseline/validate/clean support the environment matrix; works
  across H2 and PostgreSQL; the team can read exactly what runs.
- **Cons:** SQL-first means dialect differences must be managed by hand or guarded;
  advanced branching/undo is limited in the free edition.

### Alternative B — Liquibase
- **Pros:** powerful, database-agnostic changelogs (XML/YAML/JSON/SQL); built-in
  rollback; good abstraction over dialects.
- **Cons:** changelog abstraction is more verbose and less transparent than plain
  SQL; heavier mental model; for this platform's straightforward schema, Flyway's
  SQL-first clarity is preferred and better as a portfolio teaching artifact.

### Alternative C — Hibernate `hbm2ddl` (ORM auto-schema generation)
- **Pros:** zero migration files in development; fastest to start.
- **Cons:** non-deterministic, unreviewable, and unsafe for shared/production
  databases; no audit trail; explicitly inappropriate for a healthcare platform.

## Decision

We will use **Flyway** for all database migrations: versioned `V__` migrations for
schema changes and repeatable `R__` migrations for views and idempotent seed
logic, with PHI-safe seed data managed alongside. Migrations are SQL-first,
written to be compatible across H2 and PostgreSQL, validated against both, and
applied automatically per Spring profile. Hibernate `ddl-auto` is restricted to
`validate` (never `update`/`create`) so the ORM never mutates a schema.

## Consequences / Tradeoffs

**Positive**
- Every schema change is a reviewed, versioned, auditable artifact.
- The same migrations validate against both database engines.
- Repeatable migrations make test/demo data reproducible.
- Production schema is never altered implicitly by the ORM.

**Negative / Accepted tradeoffs**
- Dialect-specific SQL must be avoided or guarded; CI must run migrations on both
  engines to catch drift early.
- Rollback is forward-fix-oriented (write a new migration) rather than automatic
  undo — an accepted, safer discipline for healthcare data.
- Contributors must follow the migration naming and ordering standard strictly.

## Future Impact

Versioned migrations underpin every later milestone's data needs and keep schema
evolution safe as the domain grows (M3+). The same mechanism scales toward
external managed PostgreSQL in QA/stage/prod and remains valid if the database
later moves toward replication or partitioning (post-1.0). Migration policy detail
is elaborated in the M2 `database/` module documentation.

## References

- [0002](0002-hybrid-database-h2-postgresql.md) (hybrid database)
- [ARCHITECTURE.md](../../../ARCHITECTURE.md) §8 (Data architecture)
- [docs/PROJECT_METADATA.md](../../PROJECT_METADATA.md) §3 (Migrations: Flyway)
- [ROADMAP.md](../../../ROADMAP.md) Milestone 2; Flyway documentation.
