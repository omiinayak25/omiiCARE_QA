# Migration Notes — omiiCARE_QA v1.0.0

> **Purpose.** Explain how database and configuration migration works for adopters
> of omiiCARE_QA, and what to expect when moving between versions. Because
> `1.0.0` is the **first stable release**, this is a **baseline**: there are **no
> migrations from a prior stable version**. The forward sections define how future
> migrations will be documented.

## Scope

- **In scope:** the Flyway migration model, profile-driven database selection, the
  baseline schema state at `1.0.0`, the breaking-change policy, and how future
  migrations will be communicated.
- **Out of scope:** installation/first-run (see [UPGRADE_GUIDE.md](UPGRADE_GUIDE.md)),
  schema design detail (see [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md)), and the
  versioning contract itself (see [../VERSIONING.md](../VERSIONING.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Release Engineer | Document new migrations and required config changes per release |
| QA Architect | Validate migrations against referential integrity and seed data |
| Contributors | Add forward-only Flyway scripts; never edit a released migration |

---

## 1. Baseline — No Migrations From Prior Versions

`1.0.0` is the **starting point**. There is **nothing to migrate from**; a fresh
install applies the full migration set from empty. The earlier `0.x` tags were
milestone checkpoints, not stable releases with a compatibility guarantee, so no
upgrade path is offered from them — install `1.0.0` clean.

The baseline schema applied at `1.0.0`:

| Migration | Content |
|-----------|---------|
| `V1` | Platform baseline — tenancy, identity, RBAC, audit; repeatable seed (`R__`) loading 12 roles, permissions, and a synthetic PHI-safe DEMO tenant/hospital/departments/admin |
| `V2` | Clinical schema — patient / provider / appointment with foreign keys, unique keys, and indexes; synthetic PHI-safe clinical seed data |

## 2. Flyway Migration Model

- **Tooling:** Flyway, invoked automatically by the backend on startup.
- **Versioned migrations (`V<n>__*.sql`):** applied once, in order, and recorded in
  the Flyway schema-history table. They are **immutable once released** — a shipped
  migration is never edited; corrections ship as a new `V<n+1>`.
- **Repeatable migrations (`R__*.sql`):** re-applied whenever their checksum changes
  (used for portable, idempotent seed data).
- **Forward-only:** the schema evolves additively; destructive changes use
  reversible patterns and are flagged as breaking. Rollback is by snapshot restore
  plus a compensating forward migration, not by editing history.
- **Naming & rollback conventions:** see the `database/` module standards and
  [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md).

## 3. Profile-Driven Database

The same migration set runs against **either** engine, selected by Spring profile —
no code change to switch (see [PROJECT_METADATA.md](PROJECT_METADATA.md) §4):

| Profile group | Engine | Migration behavior |
|---------------|--------|--------------------|
| `dev`, `test` | H2 (embedded / Testcontainers) | Flyway applies `V1`/`V2` + seeds on startup |
| `local`, `docker` | PostgreSQL (Docker Compose) | Same migrations against containerized PostgreSQL |
| `qa`, `stage`, `prod` | External PostgreSQL | Same migrations against the managed instance |

Migrations are written to be **portable** across H2 and PostgreSQL, so the schema
is identical regardless of profile.

## 4. Breaking-Change Policy

From `1.0.0`, the project follows Semantic Versioning 2.0.0
([../VERSIONING.md](../VERSIONING.md)):

| Change type | Version effect | How it is announced |
|-------------|----------------|---------------------|
| Backward-compatible schema/data fix | `PATCH` | [CHANGELOG.md](../CHANGELOG.md) entry |
| Additive, backward-compatible migration (new table/column/index) | `MINOR` | CHANGELOG + a row in the next version's migration notes |
| Breaking schema/contract change (drop/rename/retype, destructive data move) | `MAJOR` | `BREAKING CHANGE:` commit + CHANGELOG + explicit migration steps here |

REST/FHIR API contract changes follow their own path versioning
([API_VERSIONING_POLICY.md](API_VERSIONING_POLICY.md)) — independent of the
product SemVer line.

## 5. How Future Migrations Will Be Documented

For every future release that touches the schema or required configuration, a new
section will be added here containing:

1. **From → To** versions covered.
2. **New migrations** — each `V<n>` script with a one-line description.
3. **Required actions** — config/env changes, manual data steps, downtime notes.
4. **Rollback** — snapshot/restore guidance and any compensating migration.
5. **Verification** — checks to confirm the migration succeeded.

Until such a section exists, adopters can assume a clean `1.0.0` install requires
no migration steps beyond letting Flyway run on first startup.

## Examples

- *Fresh install:* deploy `1.0.0`, start the backend on any profile, and Flyway
  applies `V1`/`V2` + seeds automatically — done.
- *Future minor:* a `1.1.0` adding an `allergy` table ships a new `V3__allergy.sql`
  and a "1.0.0 → 1.1.0" section here; Flyway applies it forward on startup.

## Future Enhancements

- A migration-validation job in CI that runs Flyway against H2 and PostgreSQL and
  asserts referential integrity on the seeded data.
- Generated per-release migration changelogs linked from [CHANGELOG.md](../CHANGELOG.md).

## Dependencies

- Schema detail from [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md) and the
  `database/` module.
- Versioning contract from [../VERSIONING.md](../VERSIONING.md).
- Profiles/databases from [PROJECT_METADATA.md](PROJECT_METADATA.md) §4 and
  [COMPATIBILITY_MATRIX.md](COMPATIBILITY_MATRIX.md).

## References

- [UPGRADE_GUIDE.md](UPGRADE_GUIDE.md) · [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md)
- [../VERSIONING.md](../VERSIONING.md) · [API_VERSIONING_POLICY.md](API_VERSIONING_POLICY.md)
- [../CHANGELOG.md](../CHANGELOG.md) · [KNOWN_ISSUES.md](KNOWN_ISSUES.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Release Engineer | Initial (Milestone 10) |
