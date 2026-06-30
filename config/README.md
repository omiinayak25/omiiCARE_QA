# omiiCARE_QA — Code Quality Configuration

Centralized, reusable configuration for the static-analysis and formatting tools
that gate the omiiCARE_QA backend (Maven, Java 21). These files live under
`config/` so a single source of truth is shared across modules and CI.

> Milestone 2 deliverable: configuration only. The parent `pom.xml` already
> declares `pluginManagement` for each tool; the maintainer wires the
> `<configLocation>` / `<rulesets>` / `<excludeFilterFile>` paths to the files
> documented here.

## 1. Overview

| Tool | Config file | Plugin (consumer) | Purpose |
|------|-------------|-------------------|---------|
| Checkstyle | `config/checkstyle/checkstyle.xml` | `maven-checkstyle-plugin` | Style + Javadoc + naming + imports |
| Checkstyle suppressions | `config/checkstyle/suppressions.xml` | referenced by `checkstyle.xml` | Exempt generated/test code |
| PMD | `config/pmd/ruleset.xml` | `maven-pmd-plugin` | Best-practice / design / error-prone rules |
| SpotBugs | `config/spotbugs/exclude.xml` | `spotbugs-maven-plugin` | Bytecode bug filter (exclusions) |
| Spotless | (uses Google Java Format via pom) | `spotless-maven-plugin` | Auto-formatting / import order |
| JaCoCo | (coverage thresholds in pom) | `jacoco-maven-plugin` | Test coverage gate |

## 2. Checkstyle

- **`checkstyle.xml`** — Google Java Style base, adapted for the repo:
  Javadoc required on public types/methods, naming conventions, no star imports,
  `LineLength` = 120, whitespace/brace rules, indentation (4-space).
- **`suppressions.xml`** — wired via `SuppressionFilter`. Exempts `target/`,
  `generated/`, `*MapperImpl.java`, OpenAPI sources, and relaxes Javadoc/magic
  numbers in `src/test/`.
- Severity is `warning` for Milestone 2 so it does not break the build before
  the full quality gate lands (M8). Maintainer flips to `error` when ready.

## 3. PMD

- **`ruleset.xml`** — PMD 7 ruleset referencing the standard categories:
  `bestpractices`, `codestyle`, `design`, `errorprone`, `performance`.
- Pragmatic exclusions to avoid noise (e.g. `LawOfDemeter`, `OnlyOneReturn`,
  `GuardLogStatement`) and tuned thresholds for `CyclomaticComplexity` and
  `TooManyMethods`.
- Generated paths excluded via `<exclude-pattern>`.

## 4. SpotBugs

- **`exclude.xml`** — `FindBugsFilter` excluding generated code and common,
  intentional false positives:
  - `EI_EXPOSE_REP` / `EI_EXPOSE_REP2` on DTO / model / entity value carriers.
  - Serialization noise (`SE_BAD_FIELD`, `SE_NO_SERIALVERSIONID`).
  - Lombok-generated `equals`/`hashCode` patterns.
  - Test-only false positives.

## 5. How to Run Locally

All commands run from the repo root and target the backend module.

```bash
# Format check (Spotless)
mvn -q -pl apps/backend spotless:check
# Auto-fix formatting
mvn -q -pl apps/backend spotless:apply

# Checkstyle
mvn -pl apps/backend checkstyle:check

# PMD
mvn -pl apps/backend pmd:check

# SpotBugs
mvn -pl apps/backend spotbugs:check

# Everything via the quality profile (planned)
mvn -B -ntp -Pquality verify
```

## 6. CI Integration

These configs are consumed by the reusable lint workflow
`.github/workflows/_reusable-lint.yml`, composed by `.github/workflows/ci.yml`.
Full enforcement (severity = error, gates blocking merge) is scheduled for
Milestone 8. See `.github/workflows/README.md`.

## 7. Conventions & Maintenance

- One source of truth — do not fork these configs per module.
- Prefer narrow, documented exclusions over disabling whole rules.
- In-code suppressions: Checkstyle `// CHECKSTYLE:OFF`, PMD
  `@SuppressWarnings("PMD.RuleName")`, SpotBugs `@SuppressFBWarnings`.
- Keep tool versions pinned in the parent `pom.xml`; bump configs alongside.

## 8. Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Code Quality Engineer | Initial (Milestone 2) |
