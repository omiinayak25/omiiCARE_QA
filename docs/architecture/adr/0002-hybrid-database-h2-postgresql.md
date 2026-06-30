# 0002. Profile-driven hybrid database (H2 ↔ PostgreSQL)

## Status

Accepted — 2026-06-30

## Context / Problem

The platform must run in many environments: fast local development, full
containerized stacks, automated test execution, and PostgreSQL-backed
QA/stage/prod (per [docs/PROJECT_METADATA.md](../../PROJECT_METADATA.md) §4).
Developers want zero-install, instant-start databases; CI wants deterministic,
disposable databases; integration and production must exercise the real
PostgreSQL engine. We must decide how a single codebase serves all of these
without per-environment code branches that would themselves need testing and
would undermine the "configuration over code" goal.

## Decision Drivers

- Configuration over code: switching databases must change config only, never
  source.
- Fast developer startup with no external service for the `dev` profile.
- Deterministic, disposable databases for automated tests.
- Production-fidelity: QA/stage/prod must run the real engine (PostgreSQL).
- One schema definition governs all engines (no divergent DDL to maintain).

## Alternatives Considered

### Alternative A — Profile-driven hybrid H2 ↔ PostgreSQL (chosen)
- **Pros:** `dev`/`test` use embedded H2 for instant, dependency-free startup;
  `local`/`docker`/`qa`/`stage`/`prod` use PostgreSQL; selection is a Spring
  profile only; Flyway applies one migration set to both; supports the full
  environment matrix.
- **Cons:** two SQL dialects must be kept compatible; H2 cannot reproduce every
  PostgreSQL feature, so integration tests must also run against PostgreSQL.

### Alternative B — PostgreSQL everywhere (including dev/test)
- **Pros:** maximum fidelity; one dialect; no H2 caveats.
- **Cons:** every developer and every CI job must run PostgreSQL (Docker/
  Testcontainers); slower startup; heavier local footprint; friction against the
  fast-feedback goal.

### Alternative C — H2 everywhere
- **Pros:** simplest, fastest, fully embedded.
- **Cons:** no production fidelity; PostgreSQL-specific behavior (types, indexes,
  concurrency, JSONB) goes untested until too late; unacceptable for a healthcare
  platform that targets PostgreSQL.

## Decision

We will use a profile-driven hybrid database: embedded H2 for the `dev` and
`test` profiles and PostgreSQL for `local`, `docker`, `qa`, `stage`, and `prod`.
Engine selection is by Spring profile and externalized configuration only — never
by code change — and a single Flyway-managed, dialect-compatible schema serves
both engines. H2 is configured in PostgreSQL-compatibility mode to minimize
dialect drift.

## Consequences / Tradeoffs

**Positive**
- Developers start in seconds with no external database.
- CI gets fast, disposable databases for the bulk of tests.
- Production-grade PostgreSQL is exercised where fidelity matters.
- The same migrations validate against both engines, catching dialect issues early.

**Negative / Accepted tradeoffs**
- Migrations and queries must avoid engine-specific syntax or guard it explicitly.
- A PostgreSQL-targeted integration suite (Testcontainers) is required so H2 is
  never the only thing tested — an explicit M2/M3 responsibility.
- Two engines mean two sets of edge-case behaviors to keep in mind.

## Future Impact

Profile-driven selection leaves clean seams for read replicas, connection
pooling tuning, and eventually a distributed database (post-1.0, see
[ROADMAP.md](../../../ROADMAP.md) v2.0) without touching business code. New
environments slot into the matrix by adding a profile, not by branching logic.
Flyway migration policy is recorded separately in
[0007](0007-flyway-database-migrations.md).

## References

- [ARCHITECTURE.md](../../../ARCHITECTURE.md) §8 (Data architecture)
- [docs/PROJECT_METADATA.md](../../PROJECT_METADATA.md) §3–4
- [ROADMAP.md](../../../ROADMAP.md) Milestone 2
- Spring Boot profiles; Flyway; H2 PostgreSQL-compatibility mode documentation.
