# Versioning Policy

> omiiCARE_QA versions with **Semantic Versioning 2.0.0**. This document defines how
> versions are decided, how milestones map to versions on the path to `1.0.0`, how
> tags and releases are cut, and how the change log and API versioning relate.

## Purpose

Give contributors and consumers a predictable, unambiguous contract for what a
version number means, when it changes, and how releases are produced — so upgrades
are safe and expectations are clear.

## Scope

- **In scope:** the SemVer policy, pre-1.0 rules, bump criteria, the milestone →
  version map, tagging, GitHub Releases, change-log linkage, and release cadence.
- **Out of scope:** detailed REST/FHIR API versioning mechanics (see
  [docs/API_VERSIONING_POLICY.md](docs/API_VERSIONING_POLICY.md)) and the canonical
  version value (owned by [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Maintainer (`omiinayak25`) | Decides version bumps; cuts tags and releases |
| Contributors | Use Conventional Commits so bumps can be derived from history |
| QA Architect | Confirms release readiness against the Definition of Done |

---

## 1. Semantic Versioning 2.0.0

Versions take the form **`MAJOR.MINOR.PATCH`** (optionally `-PRERELEASE` and
`+BUILD`). Per [SemVer 2.0.0](https://semver.org/):

| Segment | Increment when… |
|---------|-----------------|
| **MAJOR** | You make incompatible (breaking) changes |
| **MINOR** | You add functionality in a backward-compatible manner |
| **PATCH** | You make backward-compatible bug fixes |

A pre-release suffix (`-alpha.1`, `-rc.1`) marks an unstable build; build metadata
(`+sha.abc123`) never affects precedence.

## 2. Pre-1.0 (0.x) Rules

The project is currently on the **`0.x`** line (current: **`0.1.0`**). Under SemVer,
the `0.x` series is explicitly for rapid development and **anything may change at any
time** — the public API is not yet stable. Our conventions while on `0.x`:

- **`0.MINOR.x`** is bumped for new milestone capability or any breaking change.
- **`0.x.PATCH`** is bumped for backward-compatible fixes within a milestone line.
- Breaking changes are still announced (CHANGELOG + commit `BREAKING CHANGE:`), even
  though they are permitted pre-1.0.
- The first **stable** release is **`1.0.0`** (Milestone 10), at which point the
  full MAJOR/MINOR/PATCH contract takes effect.

## 3. What Bumps What

| Change | Pre-1.0 effect | Post-1.0 effect |
|--------|----------------|-----------------|
| Breaking API/contract change | `0.MINOR` bump | `MAJOR` bump |
| New backward-compatible capability | `0.MINOR` bump | `MINOR` bump |
| Backward-compatible bug fix | `0.PATCH` bump | `PATCH` bump |
| Docs-only / internal refactor | usually no release | usually no release |

Version bumps are derived from Conventional Commit history (`feat` → minor, `fix` →
patch, `BREAKING CHANGE:` → major) per [CONTRIBUTING.md](CONTRIBUTING.md).

## 4. Milestone → Version Mapping

Each milestone advances the `0.x` line; the tenth delivers the stable release.

| Milestone | Theme | Target version |
|-----------|-------|----------------|
| M1 | Foundation, Architecture & Governance | `0.1.0` |
| M2 | Infrastructure & Environment Foundation | `0.2.0` |
| M3 | Healthcare Platform Core (Backend) | `0.3.0` |
| M4 | Frontend Platform & Portals | `0.4.0` |
| M5 | Quality Engineering Platform (Automation) | `0.5.0` |
| M6 | Manual Quality Engineering Assets | `0.6.0` |
| M7 | Advanced Quality Engineering | `0.7.0` |
| M8 | DevOps, CI/CD & Release Engineering | `0.8.0` |
| M9 | AI-Native Quality Engineering | `0.9.0` |
| M10 | Production Hardening & Release | **`1.0.0`** |

Patch releases (e.g. `0.1.1`) are cut as needed for fixes within a milestone line.
The mapping is the planned baseline; see [ROADMAP.md](ROADMAP.md) for fences/gates.

## 5. Tags & GitHub Releases

- Every release is a **Git tag** of the form `vMAJOR.MINOR.PATCH` (e.g. `v0.1.0`),
  cut from `main` after a `release/*` branch merges.
- Each tag has a corresponding **GitHub Release** whose notes are drawn from the
  matching [CHANGELOG.md](CHANGELOG.md) section.
- Tags are **immutable** once published; corrections ship as a new patch version.
- Release tagging and notes generation are automated in **Milestone 8**.

## 6. Changelog Linkage

The [CHANGELOG.md](CHANGELOG.md) follows *Keep a Changelog 1.1.0*. Every released
version has a dated section, and the `Unreleased` section accumulates pending
changes. Compare links at the bottom of the changelog connect each tag to the next,
giving a navigable history.

## 7. API Versioning

REST and FHIR endpoints carry their **own** versioning, independent of the product
SemVer line: endpoints are served under `/api/v1/` and evolve per
[docs/API_VERSIONING_POLICY.md](docs/API_VERSIONING_POLICY.md). A product MINOR bump
does not necessarily change the API path version, and vice versa.

## 8. Release Cadence

- **Milestone-driven** primary cadence: a minor release closes each milestone once
  its gate is met.
- **Patch releases** ship on demand for fixes — no fixed schedule.
- **No release** is cut while the Definition of Done is unmet, regardless of date.

## Examples

- *Milestone close:* completing M2's gate produces tag `v0.2.0` and a GitHub Release
  generated from the `[0.2.0]` changelog section.
- *Hotfix:* a broken link discovered after `v0.1.0` ships as `v0.1.1` from a
  `hotfix/*` branch, with a `PATCH` bump.

## Future Enhancements

- Automate version derivation and changelog generation from commits in CI (M8).
- Add signed tags and release provenance/attestations.
- Publish machine-readable release metadata alongside each GitHub Release.

## Dependencies

- Version value owned by [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md) §7.
- Drives [CHANGELOG.md](CHANGELOG.md); informed by [ROADMAP.md](ROADMAP.md).
- Commit conventions from [CONTRIBUTING.md](CONTRIBUTING.md).

## References

- [Semantic Versioning 2.0.0](https://semver.org/)
- [Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/)
- [docs/API_VERSIONING_POLICY.md](docs/API_VERSIONING_POLICY.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial versioning policy (Milestone 1) |
