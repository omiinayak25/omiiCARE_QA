# Branching Strategy — omiiCARE_QA

This document defines the Git branching model, protection rules, commit
conventions, and merge strategy for the omiiCARE_QA monorepo.

## 1. Branch Model

A trunk-with-release model based on two long-lived branches plus short-lived
working branches.

| Branch | Role | Lifetime |
|--------|------|----------|
| `main` | Production-ready. Every commit is releasable; releases are tagged here. | Permanent |
| `develop` | Integration branch for completed work heading to the next release. | Permanent |
| `feature/<scope>` | New features / changes; branched from `develop`. | Short-lived |
| `release/<version>` | Stabilisation of a release candidate; branched from `develop`. | Short-lived |
| `hotfix/<scope>` | Urgent production fixes; branched from `main`. | Short-lived |

```text
feature/* ─┐
           ├─▶ develop ─▶ release/x.y.0 ─▶ main ──(tag vX.Y.0)──▶ Release
hotfix/*  ─┴────────────────────────────▶ main ──(tag vX.Y.Z)──▶ Release
```

- **feature → develop** via PR once the PR gate is green.
- **release/* → main** (and back-merge to `develop`) when the candidate is signed off.
- **hotfix/* → main** then back-merge to `develop` so the fix is not lost.

## 2. Protection Rules

### `main`
- Require a pull request before merging; **≥1 approving review**.
- Required status checks: `Backend`, `Frontend`, `Quality Gate`,
  `Dependency Review`, CodeQL.
- Require branches up to date before merge; require linear history.
- No force-push, no deletion; conversation resolution required.

### `develop`
- Require a pull request before merging.
- Required status checks: `Backend`, `Frontend`, `Quality Gate`.
- No force-push, no deletion.

## 3. Conventional Commits

All commits follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>
```

| Type | Use | SemVer impact |
|------|-----|---------------|
| `feat` | New feature | minor |
| `fix` | Bug fix | patch |
| `docs` | Documentation only | none |
| `test` | Tests | none |
| `build` / `ci` | Build system / pipeline | none |
| `refactor` / `perf` / `style` / `chore` | Internal changes | none |

A `!` after the type or a `BREAKING CHANGE:` footer triggers a **major** bump.
Scopes mirror the labeler: `backend`, `frontend`, `automation`, `docs`,
`quality`, `infra`, `ci`.

## 4. Merge Strategy

- **Squash merge** for `feature/*` PRs — one clean Conventional Commit per change,
  keeping `develop` history linear and bisectable.
- **Merge commit** for `release/*` and `hotfix/*` into `main` to preserve the
  release boundary; always back-merge into `develop`.
- Rebase feature branches on `develop` to stay current; never force-push shared
  branches.

## 5. GitHub Projects, Labels & Milestones

- **Labels** are applied automatically by path via `.github/labeler.yml`
  (`backend`, `frontend`, `automation`, `docs`, `quality`, `infra`, `ci`) plus
  `dependencies` from Dependabot.
- **Milestones** track delivery (e.g. *Milestone 8 — CI/CD & Release Engineering*)
  and map to SemVer releases.
- **GitHub Projects** boards organise issues/PRs by status; PRs link issues via
  `Closes #` so merges auto-close tracked work.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | DevOps Engineer | Initial (Milestone 8) |
