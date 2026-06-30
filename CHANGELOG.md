# Changelog

All notable changes to **omiiCARE_QA** are documented in this file.

The format is based on [Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
See [VERSIONING.md](VERSIONING.md) for the versioning policy.

## [Unreleased]

### Added

- Placeholder for changes accumulating toward the next release.

## [0.1.0] - 2026-06-30

Milestone 1 — Foundation, Architecture & Governance. Documentation-only baseline;
no application, API, or automation code by design.

### Added

- **Canonical specification & facts:** `MASTER_PROJECT_SPECIFICATION.md` as the
  in-repo source of truth and `docs/PROJECT_METADATA.md` as the canonical fact sheet
  (identity, technology matrix, environments, roles, healthcare standards, versioning anchor).
- **Enterprise architecture:** `ARCHITECTURE.md` describing the target system —
  monorepo composition, Clean Architecture + DDD backend, modular React frontend,
  adapter-centric automation, cross-cutting seams, and data/quality architecture.
- **Roadmap:** `ROADMAP.md` defining the 10-milestone path to `1.0.0` with explicit
  deliverables, fences, gates, and post-1.0 future versions.
- **Governance documents:** `README.md`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`
  (Contributor Covenant v2.1), `SECURITY.md` (policy + healthcare compliance scope),
  `VERSIONING.md` (SemVer policy + milestone mapping), and this `CHANGELOG.md`.
- **ADR structure:** Architecture Decision Record framework under
  `docs/architecture/adr/` with an index and recording conventions.
- **GitHub configuration:** issue templates, pull-request template, and reusable
  GitHub Actions workflow structure under `.github/`.
- **Code ownership:** `CODEOWNERS` assigning default and path-specific ownership to
  the single maintainer with notes on future team handles.
- **Repository standards:** `.editorconfig`, `.gitattributes`, and `.gitignore`
  establishing consistent formatting, line-ending, and ignore rules.
- **Monorepo skeleton:** the documented directory layout (`apps/`, `database/`,
  `infrastructure/`, `automation/`, `manual-testing/`, `quality/`, `ai/`, `docs/`,
  `.github/`) per `PROJECT_STRUCTURE.md`.
- **Development & AI workflows:** governance for how contributors and AI agents work,
  plus the cross-document consistency baseline.
- **License:** MIT license with a healthcare-data notice.

[Unreleased]: https://github.com/omiinayak25/omiiCARE_QA/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/omiinayak25/omiiCARE_QA/releases/tag/v0.1.0
