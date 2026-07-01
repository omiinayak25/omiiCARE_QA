# Contributing to omiiCARE_QA

> Thank you for contributing. omiiCARE_QA is **documentation-first** and
> enterprise-grade: every change — prose or code — is designed, documented,
> reviewed, and verified. This guide defines exactly how to work in the repository
> so contributions stay consistent, traceable, and high quality.

## Purpose

Give every contributor (human or AI) a single, unambiguous workflow for branching,
committing, reviewing, and merging changes, so the repository's history stays clean
and every change meets the [Definition of Done](docs/DEFINITION_OF_DONE.md).

## Scope

- **In scope:** branching model, commit conventions, commit hygiene, the pull-request
  and review process, the ADR-when-needed rule, and template pointers.
- **Out of scope:** project facts and versions ([docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md)),
  the milestone plan ([ROADMAP.md](ROADMAP.md)), and conduct expectations
  ([CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Contributor | Follow this guide; open one focused PR per logical change |
| Reviewer | Apply adversarial review; block merges that miss the Definition of Done |
| Maintainer (`omiinayak25`) | Protect branch policy; approve merges to `main`/`develop` |

---

## 1. Branching Model

Two **long-lived** branches plus three short-lived prefixes:

| Branch / prefix | Purpose | Branches from | Merges into |
|-----------------|---------|---------------|-------------|
| `main` | Always-releasable, tagged history | — | — |
| `develop` | Integration of completed work | `main` | `main` (via release) |
| `feature/*` | New work (feature, doc, chore) | `develop` | `develop` |
| `release/*` | Stabilize a version for release | `develop` | `main` **and** `develop` |
| `hotfix/*` | Urgent fix to a released version | `main` | `main` **and** `develop` |

**Naming:** `feature/m1-security-policy`, `release/0.1.0`, `hotfix/0.1.1-readme-link`.
Keep branches short-lived; rebase on the base branch before opening a PR.

> **Only `main` and `develop` are permanent.** `feature/*` (and any `release/*` / `hotfix/*`) branches are short-lived and are **deleted immediately after merge** — no other long-lived branches remain in the repository. Stale/automated (Dependabot) branches are pruned regularly.

## 2. Conventional Commits

All commit messages follow [Conventional Commits 1.0.0](https://www.conventionalcommits.org/):

```
<type>(<scope>): <short imperative summary>

<optional body — what & why, wrapped at ~72 chars>

<optional footer — BREAKING CHANGE:, Refs: #123>
```

### Allowed types

| Type | Use for |
|------|---------|
| `feat` | A new capability |
| `fix` | A bug fix |
| `docs` | Documentation only |
| `style` | Formatting, no behavior change |
| `refactor` | Code change that neither fixes a bug nor adds a feature |
| `perf` | Performance improvement |
| `test` | Adding or correcting tests |
| `build` | Build system or dependencies |
| `ci` | CI/CD configuration |
| `chore` | Maintenance not touching src/test |
| `revert` | Reverts a previous commit |

### Allowed scopes (aligned to the monorepo)

`backend`, `frontend`, `database`, `infra`, `automation`, `manual`, `quality`,
`ai`, `ci`, `docs`, `repo`. Use the scope that names the affected module; omit the
scope only for repository-wide changes.

### Examples

```
docs(repo): add SECURITY, VERSIONING, and CODEOWNERS governance docs
feat(backend): add patient encounter aggregate with invariants
fix(frontend): guard role-based route when permission claim is absent
ci(repo): add reusable build-and-test workflow scaffold
refactor(automation): extract HAPI FHIR adapter behind ResourceAdapter port

feat(backend): switch audit IDs to ULID

BREAKING CHANGE: audit event identifiers change from UUID to ULID.
```

## 3. Commit Hygiene

- **One logical change per commit.** No "misc fixes" bundles.
- **Every commit compiles and passes tests** once code exists (M2+). During the
  current documentation phase, every commit must leave the docs internally
  consistent and free of broken cross-links.
- **No secrets, no PHI, no real credentials** — ever. See [SECURITY.md](SECURITY.md).
- Reference issues in the footer (`Refs: #123`, `Closes: #123`).
- Write messages in the imperative mood ("add", not "added").

## 4. Pull-Request Process

1. Branch from the correct base (§1) and make your change.
2. Keep the PR focused; large PRs are split.
3. Fill in the PR template (see §7). State the milestone, scope, and risk.
4. Self-review first: confirm the [Definition of Done](docs/DEFINITION_OF_DONE.md).
5. Request review. At least **one maintainer approval** is required to merge.
6. Resolve every review thread; do not merge over unresolved blockers.
7. Use a **squash merge** for `feature/*`; preserve history for `release/*` and
   `hotfix/*` merges.

### Review expectations

Reviews are **adversarial by design** — reviewers look for what is *missing*, not
only what is wrong (security, correctness, architecture fit, tests, docs). A change
is not approved until it satisfies the Definition of Done.

## 5. Definition of Done

Every change must meet the criteria in
[docs/DEFINITION_OF_DONE.md](docs/DEFINITION_OF_DONE.md) — build, tests, coverage,
lint, format, documentation/architecture updated, security and accessibility checks,
and completed review. Nothing is "done" otherwise.

## 6. Local Setup

> **NOTE.** A runnable local environment (Docker Compose, profiles, scripts) arrives
> in **Milestone 2**. Until then, contributing means editing Markdown with any editor
> that honours [.editorconfig](.editorconfig). Setup instructions will be added to
> this section and to `docs/ENVIRONMENT_GUIDE.md` when M2 lands.

Code style is enforced by tooling once code exists: Spotless/Checkstyle/PMD/SpotBugs
for Java and ESLint/Prettier for TypeScript. See
[docs/CODING_STANDARDS.md](docs/CODING_STANDARDS.md).

## 7. ADRs and Templates

- **ADR-when-needed:** any *architecturally significant* decision (a new dependency,
  a structural change, a deviation from a documented pattern, a roadmap-fence change)
  requires an Architecture Decision Record under `docs/architecture/adr/`, capturing
  Problem, Alternatives, Decision, Tradeoffs, and Future Impact. Trivial changes do not.
- **Issue & PR templates:** use the templates in `.github/ISSUE_TEMPLATE/` and
  `.github/PULL_REQUEST_TEMPLATE.md`. Code ownership and required reviewers are
  defined in [CODEOWNERS](CODEOWNERS).

## Examples

- *Documentation change:* branch `feature/m1-versioning-doc`, commit
  `docs(repo): add VERSIONING policy`, open a PR, pass review, squash-merge to `develop`.
- *Architectural change:* introducing Redis caching requires an ADR before the
  implementing PR; the PR links the ADR.

## Future Enhancements

- Add `commitlint` + a pre-commit hook to enforce Conventional Commits automatically (M2).
- Automate Definition-of-Done checks as required CI status checks (M8).
- Add a `CODEOWNERS`-driven auto-reviewer assignment once a team forms.

## Dependencies

- Governed by [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md).
- Relies on [docs/DEFINITION_OF_DONE.md](docs/DEFINITION_OF_DONE.md),
  [docs/CODING_STANDARDS.md](docs/CODING_STANDARDS.md), and [CODEOWNERS](CODEOWNERS).

## References

- [Conventional Commits 1.0.0](https://www.conventionalcommits.org/)
- [VERSIONING.md](VERSIONING.md) · [CHANGELOG.md](CHANGELOG.md) · [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial contribution guide (Milestone 1) |
