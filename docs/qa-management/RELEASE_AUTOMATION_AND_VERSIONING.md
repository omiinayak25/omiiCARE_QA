# Release Automation & Versioning

> How **omiiCARE_QA** turns a clean `main` history into a tagged, reproducible,
> documented release — automatically. This is the operational companion to the
> policy in [VERSIONING.md](../../VERSIONING.md): that document defines *what a
> version means*; this one defines *how a release is produced, automated, and
> shipped*, including the test-suite and documentation artifacts unique to a QA
> platform.

## Purpose

Give maintainers and contributors a single, unambiguous runbook for cutting a
release: which commits drive which bump, how branches and tags are named, how the
changelog and release notes are produced, how the `v*` tag triggers CI, what
artifacts are versioned (code **and** the QA suite/docs), and how hotfix,
deprecation, and release-train cadence work.

## Scope

- **In scope:** Conventional-Commit → bump mapping, branch/tag strategy,
  changelog automation, the tag → CI → GitHub Release flow, versioning of the
  **test-suite and documentation** as release artifacts, release-train cadence,
  the hotfix flow, and the deprecation policy.
- **Out of scope:** the canonical SemVer policy and milestone map (owned by
  [VERSIONING.md](../../VERSIONING.md)), the REST/FHIR API path versioning
  mechanics ([docs/API_VERSIONING_POLICY.md](../API_VERSIONING_POLICY.md)), and
  the canonical version value ([docs/PROJECT_METADATA.md](../PROJECT_METADATA.md) §7).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Maintainer (`omiinayak25`) | Approves the bump; cuts the `release/*` branch and the `v*` tag; publishes the GitHub Release |
| Contributors | Use Conventional Commits so the bump can be derived from history; keep the `Unreleased` changelog section current |
| QA Architect | Confirms release readiness against [ENTRY_EXIT_CRITERIA.md](ENTRY_EXIT_CRITERIA.md) and [RELEASE_CHECKLISTS.md](RELEASE_CHECKLISTS.md) before the tag is cut |
| Release Engineer (DevOps) | Owns the `.github/workflows/` automation; keeps the release pipeline green |

---

## 1. Versioning Scheme (SemVer 2.0.0)

omiiCARE_QA uses **Semantic Versioning 2.0.0** — `MAJOR.MINOR.PATCH`, with
optional `-PRERELEASE` and `+BUILD` metadata. The current version is **`1.0.0`**
(stable; per [docs/PROJECT_METADATA.md](../PROJECT_METADATA.md) §7), so the full
contract is in force:

| Segment | Bump when… | Conventional-Commit trigger |
|---------|------------|-----------------------------|
| **MAJOR** | Backward-incompatible (breaking) change | `BREAKING CHANGE:` footer or `type!` |
| **MINOR** | Backward-compatible new capability | `feat:` |
| **PATCH** | Backward-compatible bug fix | `fix:`, `perf:` |
| *(no release)* | Docs-only / internal refactor / test-only | `docs:`, `refactor:`, `style:`, `test:`, `chore:` |

> Pre-1.0 rules (the historical `0.x` line) remain documented in
> [VERSIONING.md](../../VERSIONING.md) §2 for reference; they no longer apply now
> that `1.0.0` has shipped.

**Pre-release suffixes** (planned use for release candidates):
`-rc.N` for stabilization builds (e.g. `v1.1.0-rc.1`), `-alpha.N` / `-beta.N` for
early previews. Build metadata (`+sha.<short>`) never affects precedence and is
not tagged.

## 2. Conventional Commits → Bump Derivation

All commits follow [Conventional Commits 1.0.0](https://www.conventionalcommits.org/)
(enforced by [CONTRIBUTING.md](../../CONTRIBUTING.md) §2). The next version is
**derived from the commit range since the last `v*` tag**:

```
<type>(<scope>): <imperative summary>

<body — what & why>

<footer — BREAKING CHANGE: …, Refs: #123>
```

Derivation rule (highest-precedence trigger in the range wins):

1. Any `BREAKING CHANGE:` footer **or** any `type!` (e.g. `feat!:`) → **MAJOR**.
2. Else any `feat:` → **MINOR**.
3. Else any `fix:` / `perf:` → **PATCH**.
4. Else → no release (docs/refactor/test/chore only).

Scopes map to repo modules so the changelog groups cleanly, e.g.
`feat(automation):`, `fix(backend):`, `feat(fhir):`, `docs(qa-management):`,
`test(playwright):`. This module-scoped convention also feeds the per-artifact
versioning in §6.

> **Tooling note (planned):** bump derivation is currently a maintainer decision
> validated against history. A `git-cliff`/`semantic-release`-style derivation
> step can be added to CI (see §10) without changing this policy — the commit
> grammar above is already the contract it would consume.

## 3. Branch & Tag Strategy

Two long-lived branches plus three short-lived prefixes (mirrors
[CONTRIBUTING.md](../../CONTRIBUTING.md) §1):

| Branch / prefix | Purpose | Branches from | Merges into |
|-----------------|---------|---------------|-------------|
| `main` | Always-releasable, tagged history | — | — |
| `develop` | Integration of completed work | `main` | `main` (via release) |
| `feature/*` | New work (feature, doc, chore) | `develop` | `develop` |
| `release/*` | Stabilize a version for release | `develop` | `main` **and** `develop` |
| `hotfix/*` | Urgent fix to a released version | `main` | `main` **and** `develop` |

**Naming:** `feature/m11-perf-baseline`, `release/1.1.0`, `hotfix/1.0.1-rtm-link`.

**Tags:** every release is an annotated Git tag `vMAJOR.MINOR.PATCH` (e.g.
`v1.0.0`), cut from `main` **after** the `release/*` branch merges. Tags are
**immutable** once published — corrections ship as a new patch version, never a
re-tag.

```
develop ──●──●──●───────────────●─────────────▶
           \                   /
release/1.1.0 ●──●(stabilize)──●  ── merge ──▶ main ──▶ tag v1.1.0 ──▶ Release CI
                                                  \
hotfix/1.0.1                          main ●──●─────●  ── merge ──▶ tag v1.0.1
                                              \_____ back-merge to develop
```

> **Signed/annotated tags (planned):** future tags will be GPG/Sigstore-signed
> with build provenance attestations (carried over from
> [VERSIONING.md](../../VERSIONING.md) §"Future Enhancements").

## 4. Automated Changelog

The [CHANGELOG.md](../../CHANGELOG.md) follows
[Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/) and links to
[SemVer 2.0.0](https://semver.org/spec/v2.0.0.html).

- The **`[Unreleased]`** section accumulates pending changes; contributors add an
  entry under the right group (`Added`, `Changed`, `Deprecated`, `Removed`,
  `Fixed`, `Security`) in the same PR.
- At release time the `[Unreleased]` block is **renamed to the new version with a
  date** (`## [1.1.0] - YYYY-MM-DD`), and a fresh empty `[Unreleased]` is opened.
- Compare links at the foot of the file connect each tag to the next, giving a
  navigable history.

**Generation modes:**
- **Manual baseline (current):** maintainer curates the section during the
  `release/*` freeze, grouping Conventional-Commit subjects by type/scope.
- **Derived draft (planned):** a CI step generates a draft changelog from the
  commit range (`git log <prev-tag>..HEAD`) using the type→group mapping in §2,
  which the maintainer edits before the tag — keeping human-readable notes while
  removing manual transcription.

## 5. Tag → CI → Release Flow

Pushing a `v*` tag triggers the existing release pipeline,
[`.github/workflows/release.yml`](../../.github/workflows/release.yml):

```
git tag v1.1.0   # annotated, from main after release/* merges
git push origin v1.1.0
        │
        ▼  (on: push: tags: ["v*"])
┌───────────────────────────────────────────────────────────────┐
│ backend   → _reusable-backend.yml   (build + tests, JDK 21)     │
│ frontend  → _reusable-frontend.yml  (build/lint/typecheck)      │
│ docker    → _reusable-docker.yml    (build & push to GHCR,      │
│             needs: [backend, frontend]   version = github.ref)  │
│ release   → package backend jar (-DskipTests, already verified) │
│             extract notes from CHANGELOG for ${ref#v} via awk    │
│             softprops/action-gh-release@v2 → publishes Release   │
│             attaches apps/backend/target/*.jar + frontend dist   │
└───────────────────────────────────────────────────────────────┘
```

Key facts (verified against the committed workflow):
- Permissions are scoped `contents: write` + `packages: write`.
- Release notes are sliced from `CHANGELOG.md` for the tag's version with an
  `awk` matcher; if the section is empty it falls back to a generic line.
- Docker images are tagged with the Git ref name (the `v*` tag).
- The job ordering guarantees code is built and tested **before** the Release is
  published.

**Pre-flight gates (run before tagging, not in `release.yml`):** the PR/push
gate [`ci.yml`](../../.github/workflows/ci.yml) (backend + frontend + quality)
must be green on `main`, and the [`nightly.yml`](../../.github/workflows/nightly.yml)
full verify + dependency scan should be green for the candidate commit. Release
readiness is signed off against [ENTRY_EXIT_CRITERIA.md](ENTRY_EXIT_CRITERIA.md)
and [RELEASE_CHECKLISTS.md](RELEASE_CHECKLISTS.md).

## 6. Versioning of Test-Suite & Documentation Artifacts

A QA platform ships **more than code** — the test assets and docs are
first-class, versioned release artifacts. All of the following move **in lockstep
with the product SemVer line** (one repo, one version, per
[docs/PROJECT_METADATA.md](../PROJECT_METADATA.md) §7); a tag freezes them
together:

| Artifact class | Source of truth | What a tag freezes |
|----------------|-----------------|--------------------|
| Automation suite | `automation/` (`mvn -pl automation test` = 98 unit tests; e2e via `-Pe2e`) | The exact test code + tags (`ui-e2e`/`api-e2e`/`bdd`) at the tag commit |
| Node smoke suites | `automation/playwright/` (omiiCARE 5/5; `tests-openmrs/` 5/5 vs o2.openmrs.org) | Spec files + expected step set; artifacts are run outputs, regenerated per run |
| Manual test cases | `manual-testing/test-cases/openmrs/` (4,187 cases / 66 modules, 17-col CSV + `ALL_TEST_CASES.csv`) | The CSV corpus snapshot |
| Traceability | `manual-testing/rtm/` (`RTM.csv`/`.md`, 0 gaps, 0 untraced) | RTM coverage state at release |
| Requirements | `docs/requirements/requirements-catalog.md` (1,795 requirements) | Requirement baseline the release was tested against |
| Reverse-engineering docs | `docs/reverse-engineering/` (22 docs, ~10k lines, 78 Mermaid) | Spec snapshot |
| QA management docs | `docs/qa-management/` (15 docs, incl. this one) | Process/strategy snapshot |
| Quality assets | `quality/performance` (k6/JMeter), `quality/security` (ZAP baseline), `quality/accessibility`, `quality/visual` | Script/config snapshot (run only on owned/local SUTs) |

Versioning rules for these artifacts:
- **No independent version numbers.** The Git tag *is* their version; reference a
  suite state as "the test-suite at `v1.1.0`".
- **Coverage deltas are changelog-worthy.** Adding modules/cases, RTM changes, or
  new automation tags are `feat`/`test`-scoped entries and appear in the release
  notes (e.g. `test(manual): add 120 lab-order cases`).
- **Run outputs are not versioned artifacts.** Allure/Extent reports,
  `trace.zip`, `artifacts-openmrs/screenshots/step-1..5`, and HTML reports are
  *evidence of a run*, attached to a release or CI run as needed — never
  committed as the source-of-truth version.
- **Reproducibility:** because suite + docs + code share one tag, anyone can
  check out `v1.1.0` and reproduce the exact test corpus and expected metrics
  documented for that release.

> **Hard rule reminder:** performance and security suites execute **only on
> owned/local environments** (omiiCARE / local Docker stack), never against
> `o2.openmrs.org`. The OpenMRS smoke suite is functional CRUD only.

## 7. Release Train Cadence

| Cadence | Trigger | Version effect | Branch |
|---------|---------|----------------|--------|
| **Minor train** | A milestone / feature epic closes its exit criteria | `MINOR` bump (`feat` accumulation) | `release/<x.y.0>` |
| **Patch train** | On-demand, when fixes warrant a release | `PATCH` bump | `release/<x.y.z>` or `hotfix/*` |
| **Pre-release** *(planned)* | Stabilization before a `MINOR`/`MAJOR` | `-rc.N` tag from `release/*` | `release/*` |
| **No release** | Definition of Done unmet | none — never date-forced | — |

- Primary cadence is **milestone/feature-driven**, not calendar-locked: a release
  ships when [ENTRY_EXIT_CRITERIA.md](ENTRY_EXIT_CRITERIA.md) is met, not on a
  fixed date.
- A `release/*` branch opens the **stabilization freeze**: only fixes, the
  changelog finalization, and the version anchor update land on it; no new
  features.
- Patches ship as needed and do not wait for the next minor train.

## 8. Hotfix Flow

For an urgent defect in a **released** version:

1. Branch `hotfix/<x.y.z>-<slug>` from `main` (e.g. `hotfix/1.0.1-rtm-link`).
2. Commit the minimal `fix:` (or `fix(scope):`) change with a Conventional-Commit
   message; add a `Fixed` (or `Security`) changelog entry.
3. Open a PR into `main`; pass the [`ci.yml`](../../.github/workflows/ci.yml) gate
   and adversarial review.
4. Merge to `main`, then **tag the patch** `v<x.y.z+1>` → `release.yml` runs and
   publishes the GitHub Release.
5. **Back-merge** `main` into `develop` so the fix is not lost on the next train.

```
v1.0.0 (released) ──▶ defect found
  main ●──── hotfix/1.0.1-rtm-link ●──● ── merge ──▶ main ──▶ tag v1.0.1 ──▶ Release
                                                       └──── back-merge ──▶ develop
```

Security-sensitive hotfixes additionally follow [SECURITY.md](../../SECURITY.md)
(private disclosure first; `Security` changelog group; CVE/advisory if warranted).

## 9. Deprecation Policy

Deprecations are announced **before** removal so consumers can migrate safely:

| Stage | What happens | Version effect |
|-------|--------------|----------------|
| **Announce** | Mark the capability deprecated in docs + a `Deprecated` changelog entry; state the intended removal version | `MINOR` (announcement is backward-compatible) |
| **Maintain** | Keep it working for at least **one minor cycle** after the announcement | patches as needed |
| **Remove** | Delete it; `Removed` changelog entry referencing the original announcement | `MAJOR` (breaking) |

Rules:
- Nothing is removed in the **same** release it is deprecated in.
- REST/FHIR endpoint deprecations additionally follow
  [docs/API_VERSIONING_POLICY.md](../API_VERSIONING_POLICY.md) (path versions
  under `/api/v1/` evolve independently of the product SemVer line).
- Deprecated test assets (e.g. retired manual modules or automation tags) are
  announced the same way so coverage expectations stay traceable in the RTM.

## 10. Automation Roadmap (planned)

These extend the current pipeline without changing the policy above:

- **Bump + changelog derivation in CI:** a `git-cliff`/`semantic-release`-style
  step that proposes the next version and a draft changelog from the commit range,
  for maintainer approval (§2, §4).
- **Pre-release (`-rc.N`) channel** wired into `release.yml` (`prerelease: true`
  when the tag carries a pre-release suffix).
- **Signed tags + provenance:** GPG/Sigstore signing and SLSA build attestations
  on `v*` tags.
- **Machine-readable release metadata** published alongside each GitHub Release
  (version, commit, suite metrics, artifact digests).

## References

- [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html)
- [Conventional Commits 1.0.0](https://www.conventionalcommits.org/)
- [Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/)
- [VERSIONING.md](../../VERSIONING.md) — canonical SemVer policy & milestone map
- [CHANGELOG.md](../../CHANGELOG.md) — release history
- [CONTRIBUTING.md](../../CONTRIBUTING.md) — branching model & commit grammar
- [.github/workflows/release.yml](../../.github/workflows/release.yml) — release pipeline
- [docs/PROJECT_METADATA.md](../PROJECT_METADATA.md) — canonical version value (§7)
- [docs/API_VERSIONING_POLICY.md](../API_VERSIONING_POLICY.md) — API path versioning
- [ENTRY_EXIT_CRITERIA.md](ENTRY_EXIT_CRITERIA.md), [RELEASE_CHECKLISTS.md](RELEASE_CHECKLISTS.md) — readiness gates

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-07-01 | QA Architect / DevOps | Initial release-automation & versioning runbook |
