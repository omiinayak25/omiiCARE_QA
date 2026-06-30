# omiiCARE_QA — GitHub Actions Workflows

CI/CD for the omiiCARE_QA monorepo is built on **reusable workflows**
(`workflow_call`). Each concern (build, test, lint, security, docs) is a single
reusable unit; top-level "entry" pipelines compose them. This keeps logic
DRY and lets every event-driven pipeline share the same, audited steps.

> **Milestone 2 status:** the reusable units below are *skeletons* — correct
> structure, minimal steps, advisory (`continue-on-error`) where appropriate.
> The full event matrix and blocking gates land in **Milestone 8**.

## 1. Overview

| Workflow | Type | Trigger | Status |
|----------|------|---------|--------|
| `_reusable-build.yml` | reusable | `workflow_call` | skeleton |
| `_reusable-test.yml` | reusable | `workflow_call` | skeleton |
| `_reusable-lint.yml` | reusable | `workflow_call` | skeleton |
| `_reusable-security-scan.yml` | reusable | `workflow_call` | skeleton |
| `_reusable-docs.yml` | reusable | `workflow_call` | skeleton |
| `ci.yml` | entry | `push`/`pull_request` (main, develop) | active (composes build+test+lint) |

## 2. Reusable Workflow Strategy

- **Naming:** reusable units are prefixed `_reusable-` and are never triggered
  directly — only via `uses:`.
- **Inputs:** common inputs (e.g. `java-version`) are passed through so callers
  control matrix dimensions without forking the unit.
- **Single responsibility:** one concern per file; composition happens in the
  entry pipelines, not inside the reusable units.
- **Local reference:** entry pipelines call units with
  `uses: ./.github/workflows/_reusable-*.yml` so versions stay in lockstep with
  the branch under test.

## 3. How `ci.yml` Composes the Skeletons

```text
ci.yml  (push / PR on main, develop)
  └─ build  →  _reusable-build.yml
       ├─ test  (needs: build)  →  _reusable-test.yml
       └─ lint  (needs: build)  →  _reusable-lint.yml
```

`build` runs first; `test` and `lint` fan out from it in parallel. Security and
docs units exist as skeletons and are wired into the entry pipelines in M8.

## 4. Planned Pipeline Matrix (Milestone 8)

| Pipeline | Trigger | Composes |
|----------|---------|----------|
| **pull-request** | `pull_request` → main/develop | build + test + lint + security-scan + docs |
| **push** | `push` → develop | build + test + lint |
| **nightly** | `schedule` (cron) | build + full test + security-scan (deep) + docs |
| **release** | `push` tag `v*` / `release` | build + test + security-scan + publish artifacts |
| **hotfix** | `push` → `hotfix/**` | build + test (fast) + lint |

## 5. Configuration Dependencies

The lint unit consumes the shared static-analysis configs under `config/`
(`checkstyle`, `pmd`, `spotbugs`, `spotless`). See `config/README.md`.

## 6. Local Reproduction

Most steps map 1:1 to Maven goals runnable locally:

```bash
mvn -B -ntp -DskipTests install        # build
mvn -B -ntp test                       # test
mvn -B -ntp -Pquality spotless:check checkstyle:check pmd:check   # lint
```

## 7. Conventions & Maintenance

- Pin third-party actions to a tag/SHA; bump deliberately.
- Keep reusable units small and side-effect free; do composition in entry files.
- Advisory steps (`continue-on-error: true`) are temporary for M2 and must be
  reviewed/removed as gates become blocking in M8.
- Set least-privilege `permissions:` per workflow.

## 8. Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Code Quality Engineer | Initial reusable skeletons (Milestone 2) |
