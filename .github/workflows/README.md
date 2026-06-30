# omiiCARE_QA — GitHub Actions Workflows

CI/CD for the omiiCARE_QA monorepo is built on **reusable workflows**
(`workflow_call`). Each concern (backend, frontend, quality, Docker) is a single
reusable unit; top-level "entry" pipelines compose them for each trigger. This
keeps logic DRY and lets every event-driven pipeline share the same audited steps.

> **Milestone 8 status:** the reusable units below are real pipelines wired into
> the full event matrix. The quality gate is advisory (`continue-on-error`) and
> hardens over time as baselines are agreed.

## 1. Pipeline Matrix

| Pipeline | Trigger | Composes / Does |
|----------|---------|------------------|
| **PR gate** (`ci.yml`) | `pull_request` → main/develop | backend + frontend + quality |
| **Push gate** (`ci.yml`) | `push` → main/develop | backend + frontend + quality |
| **Nightly** (`nightly.yml`) | `schedule` (02:30 UTC daily) + `workflow_dispatch` | backend + frontend + quality + e2e (note) + dependency scan |
| **Release** (`release.yml`) | `push` tag `v*` | backend + frontend + Docker build/push + GitHub Release |
| **Hotfix** | `push` → `hotfix/**` (covered by `ci.yml` branch rules + tag release) | fast build + test via PR gate; ship via `v*` tag |
| **Manual dispatch** | `workflow_dispatch` (nightly) | on-demand full run |
| **CodeQL** (`codeql.yml`) | push / PR / weekly schedule | Java + JavaScript/TypeScript SAST |
| **Dependency review** (`dependency-review.yml`) | `pull_request` | new-dependency vuln/license gate |
| **Labeler** (`labeler.yml`) | `pull_request_target` | path-based PR auto-labels |

## 2. Reusable Workflow Strategy

- **Naming:** reusable units are prefixed `_reusable-` and are never triggered
  directly — only via `uses:`.
- **Inputs:** common inputs (`java-version`, `node-version`, `push`, `version`)
  are passed through so callers control matrix dimensions without forking the unit.
- **Single responsibility:** one concern per file; composition happens in the
  entry pipelines, not inside the reusable units.
- **Local reference:** entry pipelines call units with
  `uses: ./.github/workflows/_reusable-*.yml` so versions stay in lockstep with
  the branch under test.

| Reusable unit | Responsibility |
|---------------|----------------|
| `_reusable-backend.yml` | `mvn -pl apps/backend -am verify`; uploads Surefire + JaCoCo |
| `_reusable-frontend.yml` | `npm ci` / `npm run lint` / `npm run build`; uploads `dist` |
| `_reusable-quality.yml` | `-Pquality verify checkstyle:check pmd:check` (advisory) |
| `_reusable-docker.yml` | Buildx build of backend + frontend images; push to GHCR when `push=true` |

The original Milestone 2 skeletons (`_reusable-build.yml`, `_reusable-test.yml`,
`_reusable-lint.yml`, `_reusable-security-scan.yml`, `_reusable-docs.yml`) remain
as advisory helpers and are superseded by the units above for the entry pipelines.

## 3. Composition

```text
ci.yml (push / PR on main, develop)
  ├─ backend   →  _reusable-backend.yml
  ├─ frontend  →  _reusable-frontend.yml
  └─ quality   →  _reusable-quality.yml

release.yml (tag v*)
  ├─ backend   →  _reusable-backend.yml
  ├─ frontend  →  _reusable-frontend.yml
  ├─ docker    →  _reusable-docker.yml (push=true)
  └─ release   →  softprops/action-gh-release@v2
```

## 4. Quality Gates — what fails a pipeline

| Gate | Where | Blocking? |
|------|-------|-----------|
| Backend build + unit tests | `_reusable-backend.yml` (`mvn verify`) | Yes |
| Frontend type-check + build | `_reusable-frontend.yml` (`npm run build`) | Yes |
| Frontend lint (`--max-warnings=0`) | `_reusable-frontend.yml` (`npm run lint`) | Yes |
| Static analysis (Checkstyle/PMD/SpotBugs/Spotless) | `_reusable-quality.yml` | Advisory (hardens over time) |
| Coverage (JaCoCo) | `_reusable-quality.yml` / backend | Reported (artifact) |
| CodeQL SAST | `codeql.yml` | Reported to Security tab |
| Dependency review | `dependency-review.yml` (`fail-on-severity: high`) | Yes on PRs |
| Dependency vuln scan (Trivy) | `nightly.yml` | Advisory (nightly) |
| Docker image build | `_reusable-docker.yml` | Yes on release |

## 5. Branch Protection Summary

- **main** — protected: require PR, ≥1 review, required status checks
  (`Backend`, `Frontend`, `Quality Gate`, `Dependency Review`, CodeQL), linear
  history, no force-push. Releases are cut from `main` via `v*` tags.
- **develop** — protected: require PR, required status checks (`Backend`,
  `Frontend`, `Quality Gate`). Integration branch for feature work.
- **feature/**, **release/**, **hotfix/** branches PR into `develop`/`main`;
  the PR gate runs on every push.

## 6. Environment Promotion

```text
Dev  →  QA  →  Stage  →  Prod
```

- **Dev** — auto-deploy from `develop` (no approval).
- **QA** — auto-deploy after nightly green; manual smoke sign-off.
- **Stage** — deploy release candidate (`v*-rc`) with a required reviewer
  (GitHub Environment protection rule).
- **Prod** — deploy tagged release (`v*`) with a required approver + wait timer;
  rollback by re-deploying the previous image tag.

Secrets are supplied per environment via **GitHub Secrets / Environments** and
are never hardcoded in workflow files.

## 7. Conventions & Maintenance

- Pin third-party actions to a tag/SHA; bump deliberately (Dependabot covers this).
- Keep reusable units small and side-effect free; do composition in entry files.
- Advisory steps (`continue-on-error: true`) are reviewed each milestone and
  flipped to blocking as gates mature.
- Set least-privilege `permissions:` per workflow.

## 8. Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | DevOps Engineer | Initial (Milestone 8) |
