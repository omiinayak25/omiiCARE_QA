# CI/CD Guide — omiiCARE_QA

This guide describes the continuous-integration and continuous-delivery
architecture for the omiiCARE_QA monorepo (Maven reactor backend + automation,
React/Vite frontend). It is the authoritative reference for how code moves from
a pull request to a production release.

## 1. Architecture & Reusable Workflows

CI/CD is composed from **reusable workflows** under `.github/workflows/` that are
invoked via `workflow_call`. Entry pipelines (`ci.yml`, `nightly.yml`,
`release.yml`) compose them; the reusable units carry the actual steps.

| Reusable unit | Responsibility |
|---------------|----------------|
| `_reusable-backend.yml` | `mvn -B -ntp -pl apps/backend -am verify`; uploads Surefire + JaCoCo |
| `_reusable-frontend.yml` | `npm ci` / `npm run lint` / `npm run build`; uploads `dist` |
| `_reusable-quality.yml` | `mvn -Pquality verify checkstyle:check pmd:check` (advisory) |
| `_reusable-docker.yml` | Buildx build of backend + frontend images; pushes to GHCR when `push=true` |

Benefits: one audited definition per concern, consistent across every trigger,
versioned in lockstep with the branch under test via `uses: ./.github/...`.

## 2. Triggers & Pipeline Matrix

| Pipeline | Trigger |
|----------|---------|
| PR / push gate (`ci.yml`) | `pull_request` and `push` → main, develop |
| Nightly (`nightly.yml`) | `schedule` 02:30 UTC daily + `workflow_dispatch` |
| Release (`release.yml`) | `push` tag `v*` |
| CodeQL (`codeql.yml`) | push / PR / weekly schedule |
| Dependency review (`dependency-review.yml`) | `pull_request` |
| Labeler (`labeler.yml`) | `pull_request_target` |

Hotfix branches (`hotfix/**`) flow through the same PR gate and ship via a `v*`
tag, so they reuse the standard pipelines rather than a bespoke path.

## 3. Quality Gates

The platform-wide quality gate requires that **build, test, coverage, lint,
format, security, and docs** all pass. In CI:

- **Build/test** — `mvn verify` (backend) and `npm run build` (frontend) must
  succeed; failures block the merge.
- **Lint/format** — frontend ESLint runs with `--max-warnings=0`; backend
  Spotless/Checkstyle/PMD run in `_reusable-quality.yml` (advisory, hardening).
- **Security** — CodeQL SAST, `dependency-review-action` (`fail-on-severity:
  high`) on PRs, and nightly Trivy filesystem scan.
- **Docs** — Markdown link checking is available via the docs reusable helper.

See `docs/QUALITY_GATES.md` for the enumerated thresholds.

## 4. Coverage (JaCoCo + Frontend)

- **Backend:** the `-Pquality` profile runs JaCoCo; reports are produced under
  `apps/backend/target/site/jacoco/` and uploaded as the `backend-jacoco-report`
  / `quality-jacoco-report` artifacts. Coverage is report-only today and becomes
  a blocking threshold as the gate hardens.
- **Frontend:** the build (`tsc --noEmit && vite build`) enforces type safety;
  component/coverage instrumentation is added with the frontend test suite and
  uploaded alongside the `frontend-dist` artifact.

## 5. Docker Image Build / Tag / Scan

- Images use the existing Dockerfiles: backend built from the **repo root**
  (`apps/backend/Dockerfile`), frontend built from **`apps/frontend`**.
- `_reusable-docker.yml` sets up Buildx and builds both images on every release.
- **Tagging:** each image is tagged with the commit `sha` and the semantic
  `version`, published to `ghcr.io/<owner>/omiicare-qa-{backend,frontend}`.
- **Push policy:** images are pushed only when `push=true` (release pipeline);
  CI builds validate the image without publishing.
- **Scan:** Trivy scans the filesystem nightly; image scanning hardens over time.

## 6. Artifacts & Semantic Versioning

- **Artifacts:** Surefire reports, JaCoCo reports, and the frontend `dist` bundle
  are uploaded via `actions/upload-artifact@v4` (7–14 day retention).
- **SemVer 2.0.0:** the project follows Semantic Versioning (current `0.7.0`,
  targeting `1.0.0`). Conventional Commits drive the bump: `fix:` → patch,
  `feat:` → minor, `!`/`BREAKING CHANGE` → major.
- **Release automation:** pushing a `v<version>` tag triggers `release.yml`,
  which builds + tests both stacks, pushes Docker images, extracts notes from
  `CHANGELOG.md`, and publishes a GitHub Release via
  `softprops/action-gh-release@v2` with the jar and `dist` attached.

## 7. Secrets & Environment Promotion

- **Secrets:** all credentials (registry, deploy keys, scan tokens) are stored in
  **GitHub Secrets / Environments** and referenced as `${{ secrets.* }}`. They are
  **never hardcoded** in workflow files. GHCR login uses the built-in
  `GITHUB_TOKEN`.
- **Promotion path:** `Dev → QA → Stage → Prod`.
  - Dev: auto-deploy from `develop`.
  - QA: auto-deploy after nightly green + smoke sign-off.
  - Stage: release candidate with a required reviewer (Environment rule).
  - Prod: tagged release with a required approver + wait timer.

## 8. Rollback

- **Images:** redeploy the previous image tag (`sha` or prior `version`) — every
  release image is immutable and retained in GHCR.
- **Releases:** a faulty release is superseded by a new patch tag; the GitHub
  Release can be marked as a pre-release/draft while remediation is in flight.
- **Database:** forward-only migrations with reversible patterns; restore from the
  pre-deploy snapshot if a migration must be undone.
- **Process:** rollbacks are PR-tracked and recorded in `CHANGELOG.md`.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | DevOps Engineer | Initial (Milestone 8) |
