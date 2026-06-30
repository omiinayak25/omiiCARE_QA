# Test Data

> **Operational home for QA / test fixtures beyond the Flyway seed.**

## Purpose

Hold curated, synthetic datasets used by tests and demos that are not part of
the always-loaded Flyway seed — e.g. scenario fixtures, edge-case rows, and
load-test datasets. The canonical reference seed lives in
[../documentation/SEED_DATA_CATALOG.md](../documentation/SEED_DATA_CATALOG.md).

## Scope

- **In scope:** synthetic test fixtures, scenario datasets, fixture-loading
  helpers.
- **Out of scope:** the baseline reference/demo seed (shipped via
  `R__seed_reference_and_demo.sql`), and production data.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Engineer | Author and maintain fixtures; keep them synthetic |
| Database Engineer | Ensure fixtures stay portable across H2 and PostgreSQL |

## What Goes Here

- SQL or CSV fixtures, optionally grouped by scenario (`scenario-<name>/`).
- Small, reviewable datasets. Large generated datasets are produced by scripts,
  not committed.

## Naming

- `fixture_<domain>_<scenario>.sql` (e.g. `fixture_appointments_overlap.sql`).
- Scenario folders: `scenario-<name>/`.

## Retention

- Version-controlled and kept while the referencing tests exist; prune fixtures
  when their tests are removed.

## Safety

- All test data is **SYNTHETIC** and PHI-safe. No real patient information.
  Synthetic emails use the `.example` domain.

## References

- [../documentation/SEED_DATA_CATALOG.md](../documentation/SEED_DATA_CATALOG.md)
- [../documentation/DATABASE_CHANGELOG.md](../documentation/DATABASE_CHANGELOG.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Database Engineer | Initial (Milestone 2) |
