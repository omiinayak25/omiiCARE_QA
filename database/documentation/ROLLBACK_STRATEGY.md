# Rollback Strategy

> **How omiiCARE_QA recovers from a bad migration or schema change.**
> Defines the forward-fix-first philosophy, the role of documented undo
> migrations, and the backup / point-in-time-restore safety net per
> environment. Aligns with [docs/DATABASE_BLUEPRINT.md](../../docs/DATABASE_BLUEPRINT.md) §8.

## Purpose

Make recovery from schema problems predictable. State clearly when we roll
forward, when we restore from backup, and exactly how each is done across
dev/local, qa/stage, and prod — so no operator improvises during an incident.

## Scope

- **In scope:** forward-fix philosophy, undo migrations, backup-before-migrate,
  PostgreSQL point-in-time restore, per-environment procedures, transactional
  DDL behaviour (PostgreSQL vs H2), the `restore/` and `backups/` directories.
- **Out of scope:** migration naming (see
  [MIGRATION_NAMING_STANDARDS.md](MIGRATION_NAMING_STANDARDS.md)), application
  rollback / blue-green deploy (release pipeline, M8).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Database Engineer | Author the corrective forward migration; keep undo scripts current |
| Release Engineer | Take pre-migrate backups in qa/stage/prod; trigger restore if needed |
| On-call / Operator | Execute documented restore runbook under change control |
| QA Engineer | Confirm schema state and data integrity after any rollback |

---

## 1. Philosophy: Forward-Fix First

- Flyway migrations are **forward-only by default.** A mistake is corrected by a
  new, higher-versioned migration — not by editing or deleting the bad one.
- Forward-fix preserves a linear, auditable history and keeps checksums stable.
- True rollback (undo or restore) is reserved for changes that cannot be safely
  corrected forward (destructive DDL, data loss, corruption).

## 2. Undo Migrations

- Documented undo scripts follow `U<version>__<desc>.sql` (see naming standards).
- **v1.0 undo is manual and documentation-grade** — an operator reviews and runs
  the reverse DDL deliberately; there is no automatic Flyway undo.
- Each high-risk migration ships with either a paired `U` script or an inline
  runbook describing how to reverse it and what data is at risk.
- Undo is a controlled, supervised action — never the casual first response.

## 3. Backup Before Migrate

- In qa/stage/prod, the release pipeline takes a database backup/snapshot
  **immediately before** running `flyway migrate`. This is the primary safety
  net for any structural change.
- Backups are produced with `pg_dump` (logical) and/or a storage-level snapshot
  and land in the `backups/` directory or the managed snapshot store; see
  [../backups/README.md](../backups/README.md) for naming and retention.
- No migrate runs against a protected environment without a confirmed fresh
  backup.

## 4. Point-in-Time Restore (PostgreSQL)

- PostgreSQL supports point-in-time recovery (PITR) via base backups plus WAL
  archiving — restore the cluster to any moment before the faulty migration.
- Logical restores use `pg_restore` (from a `pg_dump` custom-format archive) for
  database- or table-level recovery.
- Restore inputs and runbooks live in the `restore/` directory; see
  [../restore/README.md](../restore/README.md).
- H2 (dev/test) has no PITR — recovery there is "drop and re-migrate" (see §6).

## 5. Rolling Back in dev / local

- Cheapest path: discard and rebuild. For H2 in-memory, restart the app — the
  schema is recreated from migrations on startup.
- For a persistent local PostgreSQL (docker), drop the schema/volume and let
  Flyway + repeatable seeds rebuild from scratch.
- No backup ceremony required; data is synthetic and disposable.

## 6. Rolling Back in qa / stage / prod

1. Stop further deploys; freeze writes if data integrity is at risk.
2. Prefer **forward-fix**: author and deploy a corrective `V*` migration.
3. If forward-fix is impossible, execute the documented `U*` undo under change
   control, or restore from the pre-migrate backup (logical `pg_restore`) or via
   PITR to just before the migration.
4. Verify schema (`flyway info`/`validate`) and data integrity (QA checks).
5. Record the incident and the corrective migration in
   [DATABASE_CHANGELOG.md](DATABASE_CHANGELOG.md).

## 7. Transactional DDL Notes (PostgreSQL vs H2)

- **PostgreSQL** runs DDL inside transactions: if a migration fails mid-way, the
  whole migration rolls back atomically, leaving no partial schema. This makes
  forward-fix the safe default — a failed migrate does not corrupt the schema.
- **H2** does **not** treat DDL transactionally the same way; statements may
  auto-commit, so a partially failed migration can leave a half-applied schema.
  In dev/test this is acceptable because the fix is to drop and re-migrate.
- Implication: author migrations so each is internally consistent and, where
  possible, idempotent, so a re-run after a partial H2 failure is safe.

## 8. The `restore/` and `backups/` Directories

| Directory | Role |
|-----------|------|
| `backups/` | Destination for pre-migrate `pg_dump` archives and snapshot metadata; retention defined in its README |
| `restore/` | Restore runbooks, `pg_restore`/PITR helper scripts, and recovery checklists |

Both are operational homes only — large binary dumps are not committed to git;
the directories hold the procedures, naming rules, and (optionally) small
fixtures. See [../backups/README.md](../backups/README.md) and
[../restore/README.md](../restore/README.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Database Engineer | Initial (Milestone 2) |
