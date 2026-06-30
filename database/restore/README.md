# Database Restore

> **Operational home for restore runbooks and recovery helpers.**

## Purpose

Hold the procedures and helper scripts to restore the omiiCARE_QA PostgreSQL
database from a backup or via point-in-time recovery when forward-fix is not
viable. See [../documentation/ROLLBACK_STRATEGY.md](../documentation/ROLLBACK_STRATEGY.md).

## Scope

- **In scope:** `pg_restore`/PITR runbooks, recovery checklists, helper scripts.
- **Out of scope:** taking backups (see [../backups/](../backups/)), the
  forward-fix migration path (see the rollback strategy doc).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Operator / On-call | Execute the restore runbook under change control |
| QA Engineer | Verify schema and data integrity after restore |

## What Goes Here

- Restore runbooks and checklists.
- `pg_restore` / PITR helper scripts that consume archives from `../backups/`.
- Large binary archives are **not** committed; they come from managed storage.

## Naming

- `restore-<scenario>.md` for runbooks (e.g. `restore-pitr.md`,
  `restore-logical.md`).
- Helper scripts: `restore-<scenario>.sh`.

## Retention

- Runbooks and scripts are version-controlled and kept indefinitely.
- Any temporary restored artifacts are transient and removed after recovery.

## Safety

- Restores operate on synthetic / PHI-safe data in current milestones; once real
  PHI exists, restores follow its access-control and audit requirements.

## References

- [../documentation/ROLLBACK_STRATEGY.md](../documentation/ROLLBACK_STRATEGY.md)
- [../backups/README.md](../backups/README.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Database Engineer | Initial (Milestone 2) |
