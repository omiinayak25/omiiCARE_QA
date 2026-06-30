# Project Structure

> **Purpose.** Document the monorepo layout, what each directory is for, and the
> milestone that populates it. This is the map; [ARCHITECTURE.md](ARCHITECTURE.md)
> is the reasoning. The tree below reflects the Milestone 1 baseline, which is
> deliberately documentation- and scaffold-only.

## Scope

The complete top-level and second-level directory layout, ownership, and fill
order. Leaf directories that hold no Milestone 1 content carry a `.gitkeep` so
the structure is version-controlled from day one.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Architect | Keep this map in sync with the real tree |
| All contributors | Place new files in the correct module; do not invent top-level dirs without an ADR |

---

## Top-Level Layout

```
omiiCARE_QA/
├── README.md                     Project front door
├── MASTER_PROJECT_SPECIFICATION.md  Source-of-truth spec (mirrors build prompt)
├── ARCHITECTURE.md               Target architecture
├── PROJECT_STRUCTURE.md          This document
├── ROADMAP.md                    Milestone & version roadmap
├── CHANGELOG.md                  Keep a Changelog format
├── VERSIONING.md                 SemVer policy
├── CONTRIBUTING.md               Workflow, branching, commits, DoD
├── SECURITY.md                   Security policy & compliance scope
├── CODE_OF_CONDUCT.md            Contributor Covenant
├── LICENSE                       MIT + healthcare-data notice
├── CODEOWNERS                    Review ownership
├── .editorconfig .gitattributes .gitignore
│
├── .github/                      Templates & (future) workflows
│   ├── ISSUE_TEMPLATE/           Bug, feature, test-case, docs, chore
│   ├── workflows/                Reusable CI/CD (M8) — scaffold only in M1
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── CONTRIBUTING / config
│
├── docs/                         All governance, blueprints, guides
│   └── architecture/
│       ├── adr/                  Architecture Decision Records
│       └── diagrams/             C4 / sequence / ER diagrams
│
├── apps/
│   ├── backend/                  Spring Boot healthcare core      (M3)
│   └── frontend/                 React SUT, role-based portals    (M4)
│
├── database/                     Flyway migrations, PHI-safe seeds (M2)
│   ├── migrations/ repeatable/ seeds/ scripts/
│   └── backups/ restore/ test-data/ documentation/
│
├── infrastructure/               Docker Compose + monitoring      (M2)
│   ├── docker/
│   └── monitoring/ (grafana/ prometheus/)
│
├── automation/                   Quality engineering platform     (M5)
│   ├── playwright/ selenium/ restassured/ bdd/
│   ├── shared/ (core, config, drivers, listeners, reporting,
│   │            utilities, generators, assertions, database,
│   │            security, accessibility, visual)
│   └── resources/ (fixtures, test-data)
│
├── manual-testing/               Manual QA assets                 (M6)
│   ├── requirements/ test-strategy/ test-plan/ test-cases/
│   ├── test-suites/ checklists/ bug-reports/ bug-templates/
│   ├── execution/ test-data/ rtm/ risk-analysis/ estimation/
│   └── release/ uat/ signoff/ metrics/ training/ knowledge-base/
│
├── quality/                      Advanced QE frameworks           (M7)
│   ├── performance/ security/ accessibility/ visual/
│   ├── database-testing/ contract-testing/ chaos/ resilience/
│   └── observability/ compliance/
│
├── ai/                           AI-native QE                     (M9)
│   ├── providers/ prompts/ templates/ agents/ analysis/
│   └── reporting/ documentation/ quality/ knowledge/ evaluation/
│
└── scripts/                      setup/start/stop/reset/health    (M2)
```

## Directory Ownership & Fill Order

| Directory | Milestone | Charter |
|-----------|-----------|---------|
| `docs/`, root governance | M1 | This milestone |
| `database/`, `infrastructure/`, `scripts/` | M2 | [database/README.md](database/README.md), [infrastructure/README.md](infrastructure/README.md) |
| `apps/backend/` | M3 | [apps/backend/README.md](apps/backend/README.md) |
| `apps/frontend/` | M4 | [apps/frontend/README.md](apps/frontend/README.md) |
| `automation/` | M5 | [automation/README.md](automation/README.md) |
| `manual-testing/` | M6 | [manual-testing/README.md](manual-testing/README.md) |
| `quality/` | M7 | [quality/README.md](quality/README.md) |
| `.github/workflows/` | M8 | populated in DevOps milestone |
| `ai/` | M9 | [ai/README.md](ai/README.md) |

## Conventions

- **No new top-level directory** without an ADR justifying it.
- Each non-trivial module owns a `README.md` charter describing purpose,
  planned contents, and boundaries.
- Empty-by-design directories keep a `.gitkeep`; remove it when real files land.
- Module-internal layout follows the patterns in
  [ARCHITECTURE.md](ARCHITECTURE.md) and [docs/CODING_STANDARDS.md](docs/CODING_STANDARDS.md).

## Examples

- A new Flyway migration goes in `database/migrations/` using the naming
  standard from [docs/DATABASE_BLUEPRINT.md](docs/DATABASE_BLUEPRINT.md).
- A new Playwright page object goes under `automation/playwright/` reusing
  `automation/shared/core`.

## Future Enhancements

- A `tools/` directory for repository tooling once CI matures (M8).
- Per-module `CHANGELOG` fragments aggregated into the root changelog.

## Dependencies

- Mirrors [ARCHITECTURE.md](ARCHITECTURE.md) and is scoped by [ROADMAP.md](ROADMAP.md).

## References

- [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md)
- [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial structure baseline (Milestone 1) |
