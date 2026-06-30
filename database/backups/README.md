# Database Backups

> **Operational home for pre-migrate and scheduled database backups.**

## Purpose

Hold the procedures and (small) artifacts for backing up the omiiCARE_QA
PostgreSQL database before migrations and on a routine schedule, so a clean
restore point always exists. See [../documentation/ROLLBACK_STRATEGY.md](../documentation/ROLLBACK_STRATEGY.md).

## Scope

- **In scope:** backup runbooks, naming/retention conventions, small helper
  scripts.
- **Out of scope:** the restore procedure (see [../restore/](../restore/)),
  large binary dumps (kept in managed storage, not git).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Release Engineer | Take a pre-migrate backup in qa/stage/prod before `flyway migrate` |
| Operator | Verify backup completion and integrity |

## What Goes Here

- `pg_dump` custom-format archives and storage-snapshot metadata.
- Helper scripts for taking backups. Large dumps are **not** committed to git.

## Naming

- `omiicare_<env>_<UTC-timestamp>.dump` — e.g. `omiicare_prod_20260630T1200Z.dump`.
- Pre-migrate backups append the target version, e.g. `..._preV2.dump`.

## Retention

- dev/local: ad hoc, not retained.
- qa/stage: keep latest 7.
- prod: per data-retention policy (release pipeline, M8); never delete the
  pre-migrate backup until the deploy is confirmed healthy.

## Safety

- All backups derive from synthetic / PHI-safe data in current milestones.
  When real PHI exists, backups inherit its handling and encryption requirements.

## References

- [../documentation/ROLLBACK_STRATEGY.md](../documentation/ROLLBACK_STRATEGY.md)
- [../restore/README.md](../restore/README.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Database Engineer | Initial (Milestone 2) |
