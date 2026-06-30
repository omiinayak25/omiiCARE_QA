# Quality Gates — omiiCARE_QA

This document enumerates the CI/CD quality gates, their thresholds, and how each
is enforced. A pipeline is **green** only when every blocking gate passes; a
**red** blocking gate prevents merge or release.

## 1. Gate Summary

| # | Gate | Tool / Command | Threshold | Enforced in | Blocking |
|---|------|----------------|-----------|-------------|----------|
| 1 | Backend build | `mvn -B -ntp -pl apps/backend -am verify` | Compiles, packages | `_reusable-backend.yml` | Yes |
| 2 | Backend unit tests | Surefire (`mvn verify`) | 100% pass, 0 failures/errors | `_reusable-backend.yml` | Yes |
| 3 | Backend coverage | JaCoCo (`-Pquality`) | Report-only now; target ≥70% line as gate hardens | `_reusable-quality.yml` | Advisory |
| 4 | Frontend type-check + build | `npm run build` (`tsc --noEmit && vite build`) | No type errors, bundle builds | `_reusable-frontend.yml` | Yes |
| 5 | Frontend lint | `npm run lint` (`eslint --max-warnings=0`) | 0 warnings, 0 errors | `_reusable-frontend.yml` | Yes |
| 6 | Backend format | Spotless (`-Pquality`) | Code formatted (advisory) | `_reusable-quality.yml` | Advisory |
| 7 | Static analysis | Checkstyle + PMD + SpotBugs (`-Pquality`) | No new violations above baseline | `_reusable-quality.yml` | Advisory |
| 8 | SAST | CodeQL (java + javascript-typescript) | No new high-severity alerts | `codeql.yml` | Reported to Security tab |
| 9 | Dependency review | `dependency-review-action@v4` | `fail-on-severity: high` | `dependency-review.yml` | Yes (PR) |
| 10 | Dependency vuln scan | Trivy filesystem | HIGH/CRITICAL surfaced | `nightly.yml` | Advisory |
| 11 | Docs link-check | lychee (`_reusable-docs.yml`) | No broken links (advisory) | docs helper | Advisory |
| 12 | Docker build | Buildx (`_reusable-docker.yml`) | Both images build | release | Yes (release) |

## 2. Build & Test Gates (1, 2, 4)

The backend reactor builds with `-am` so module dependencies are resolved, and
runs the full Surefire suite. Any compile failure, test failure, or test error
fails the job. The frontend runs `tsc --noEmit` (type safety) before `vite build`;
a single type error fails the gate.

## 3. Coverage Gate (3)

JaCoCo runs under the `-Pquality` profile and publishes
`apps/backend/target/site/jacoco/`. It is **report-only** today and uploaded as a
CI artifact. As the codebase matures, the JaCoCo `check` rule is enabled with a
minimum line/branch ratio (initial target ≥70% line) and becomes blocking.

## 4. Lint, Format & Static Analysis Gates (5, 6, 7)

- **Frontend lint** is already blocking via `--max-warnings=0`.
- **Backend** Spotless (format), Checkstyle, PMD, and SpotBugs run via
  `_reusable-quality.yml` with `continue-on-error: true`. This is deliberate: the
  gate **hardens over time** — baselines are agreed, severities raised to error,
  and `continue-on-error` is removed so violations block.

## 5. Security & Dependency Gates (8, 9, 10)

- **CodeQL** scans both languages on push, PR, and weekly; alerts appear in the
  Security tab and are triaged as part of review.
- **Dependency review** blocks PRs that introduce dependencies with
  high-severity vulnerabilities or disallowed licenses.
- **Trivy** runs nightly across the filesystem for HIGH/CRITICAL findings.

## 6. Docs & Docker Gates (11, 12)

- **Docs link-check** validates Markdown links across `docs/` and top-level
  `*.md` (advisory).
- **Docker build** must succeed for both images during a release; tags are the
  commit `sha` and the semantic `version`, pushed to GHCR only on release.

## 7. Hardening Roadmap

| Gate | Today | Next |
|------|-------|------|
| Coverage (3) | Report-only | JaCoCo `check` ≥70% line, blocking |
| Static analysis (7) | Advisory | Severities → error, baseline frozen, blocking |
| Trivy (10) | Advisory nightly | Per-PR with severity gating |
| Docs (11) | Advisory | Blocking link-check on docs PRs |

## 8. Enforcement Model

Gates are enforced two ways: (a) the workflow **job fails**, marking the check
red; and (b) **branch protection** lists the blocking checks (`Backend`,
`Frontend`, `Quality Gate`, `Dependency Review`, CodeQL) as required, so GitHub
prevents merge until they pass. Advisory gates report status without blocking
until they are promoted per the hardening roadmap.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | DevOps Engineer | Initial (Milestone 8) |
