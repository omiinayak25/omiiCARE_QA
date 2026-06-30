# Migration Naming Standards

> **Flyway migration naming and location conventions for omiiCARE_QA.**
> Authoritative rules for how schema and seed migrations are named, ordered,
> and placed. Aligns with the Flyway standard set in
> [docs/DATABASE_BLUEPRINT.md](../../docs/DATABASE_BLUEPRINT.md) §5.

## Purpose

Give every engineer who touches the database one unambiguous convention for
naming and locating Flyway migrations, so that migration ordering is
deterministic, history is immutable, and the same migration set runs
identically on H2 and PostgreSQL.

## Scope

- **In scope:** versioned migration naming, repeatable seed naming, undo
  migration naming, location/profile strategy, versioning rules, do/don't.
- **Out of scope:** the actual DDL content (see the migration files under
  `apps/backend/src/main/resources/db/migration`), backup/restore mechanics
  (see [ROLLBACK_STRATEGY.md](ROLLBACK_STRATEGY.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Database Engineer | Author migrations that follow these names and locations |
| Backend Engineer | Add forward migrations for new entities; never edit merged ones |
| QA Engineer | Verify migration history is linear and checksums are stable |
| Reviewer | Block any PR that edits a merged migration or breaks naming |

---

## 1. Naming Conventions

| Type | Pattern | Example | Re-run? |
|------|---------|---------|---------|
| Versioned | `V<major>_<minor>__<snake_desc>.sql` | `V1__baseline_platform_schema.sql` | No — once, in order |
| Repeatable (seed/view) | `R__<snake_desc>.sql` | `R__seed_reference_and_demo.sql` | Yes — on checksum change |
| Undo (documented) | `U<version>__<snake_desc>.sql` | `U1__drop_baseline_platform_schema.sql` | Manual only (v1.0) |

- `<major>_<minor>` is the version. `V1` and `V1_0` are equivalent (Flyway
  treats a missing minor as `0`). Use `V1`, `V1_1`, `V2` … as the platform
  evolves; reserve the minor component for follow-up corrections within a
  milestone (e.g. `V1_1__add_user_index.sql`).
- `<snake_desc>` is lowercase snake_case, verb-first where it reads naturally
  (`add_`, `create_`, `seed_`, `alter_`). Keep it short but specific.
- The double underscore `__` between version and description is required by
  Flyway and must not be a single underscore.

## 2. Versioned Migrations (`V`)

- Forward-only, immutable, strictly ordered schema or data changes.
- One logical change per migration. A migration is **never edited after it is
  merged** — a new higher-versioned migration corrects a prior one.
- Versions must be monotonic and unique. Two open branches must not both claim
  the same version; rebase to resolve before merge.
- `V1__baseline_platform_schema.sql` is the baseline (tenancy, identity, RBAC,
  audit). Clinical/domain tables arrive as `V2…`/`V3…` in Milestone 3.

## 3. Repeatable Migrations (`R`)

- For idempotent objects: reference-data seeds, views, functions.
- Re-applied by Flyway whenever the file checksum changes; applied **after** all
  pending versioned migrations in a run.
- Must be safe to run repeatedly — every statement is guarded
  (`WHERE NOT EXISTS`, `CREATE OR REPLACE`). The live
  `R__seed_reference_and_demo.sql` follows this exactly.
- No version number — ordering among repeatables is by description, so keep
  names independent of execution order.

## 4. Undo Migrations (`U`)

- Pattern `U<version>__<desc>.sql`, paired to the `V` of the same version
  (e.g. `U1__…` undoes `V1__…`).
- **In v1.0 undo is documentation-grade, executed manually**, not via Flyway
  Teams auto-undo. They capture the reverse DDL and a runbook for high-risk
  changes so an operator can step back deliberately under supervision.
- The default rollback path is forward-fix, not undo — see
  [ROLLBACK_STRATEGY.md](ROLLBACK_STRATEGY.md).

## 5. Location & Profile Strategy

Flyway scans `flyway.locations`, which is set per Spring profile. Two
classpath roots are used:

| Location | Holds | Loaded in profiles |
|----------|-------|--------------------|
| `classpath:db/migration` | Versioned schema (`V*`) | All profiles |
| `classpath:db/seed` | Repeatable seeds (`R*`) | dev, local, docker, test, qa |

- Schema migrations always load; seed data loads only where synthetic demo data
  is wanted. Production-like profiles exclude `db/seed` so demo rows never reach
  a real environment.
- The live tree: `apps/backend/src/main/resources/db/migration/` and
  `apps/backend/src/main/resources/db/seed/`.
- Rare vendor-specific statements live under `db/migration/h2/` or
  `db/migration/postgresql/`; the shared migration stays portable.

## 6. Versioning Rules

- Bump the **major** for a new milestone's structural wave (M3 clinical tables
  begin at `V2`/`V3`). Bump the **minor** for an incremental fix or addition
  inside a milestone.
- Never reuse, renumber, or delete a merged version.
- Never change a merged migration's bytes — Flyway validates checksums and a
  drift fails the build. Correct forward instead.
- Keep history linear: resolve version collisions before merge, not after.

## 7. Do / Don't

**Do**
- Use `V<major>_<minor>__snake_desc.sql` for schema, `R__snake_desc.sql` for
  seeds, `U<version>__desc.sql` for documented undo.
- Make repeatable seeds idempotent and guarded.
- Keep migrations portable across H2 and PostgreSQL; isolate vendor SQL.
- One logical change per versioned migration.

**Don't**
- Don't edit a migration after merge (use a new forward migration).
- Don't reuse or skip version numbers, or use a single underscore separator.
- Don't put demo/seed data in `db/migration` — it belongs in `db/seed`.
- Don't rely on auto-undo in v1.0; undo scripts are manual.

## 8. References

- [docs/DATABASE_BLUEPRINT.md](../../docs/DATABASE_BLUEPRINT.md) §5 Flyway standard.
- [ROLLBACK_STRATEGY.md](ROLLBACK_STRATEGY.md)
- [DATABASE_CHANGELOG.md](DATABASE_CHANGELOG.md)
- Live migrations: `apps/backend/src/main/resources/db/migration` and `.../db/seed`.
- Flyway documentation (migrations, repeatable, undo).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Database Engineer | Initial (Milestone 2) |
