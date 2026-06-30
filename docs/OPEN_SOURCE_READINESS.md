# Open-Source Readiness Checklist

> A point-in-time audit confirming omiiCARE_QA meets recognised open-source and
> GitHub community-health standards for the **1.0.0** release. Every item is
> marked **done** with the exact location of the artifact that satisfies it.

## Purpose

Provide a single, auditable checklist that a maintainer, contributor, or evaluator
can use to confirm the repository is genuinely ready for public, collaborative,
open-source use — not just code-complete.

## Scope

- **In scope:** licensing, governance, contribution and conduct policy, security
  policy, issue/PR templates, CI, documentation, examples, versioning, release
  notes, and discoverability (topics/keywords).
- **Out of scope:** day-to-day project tracking ([GITHUB_PROJECT_MANAGEMENT.md](GITHUB_PROJECT_MANAGEMENT.md))
  and the milestone plan ([../ROADMAP.md](../ROADMAP.md)).

## Status Legend

| Symbol | Meaning |
|--------|---------|
| ✅ | Done — artifact exists and satisfies the requirement |

---

## 1. Community Health Files

| # | Item | Status | Lives at |
|---|------|--------|----------|
| 1 | License (OSI-approved) | ✅ | [../LICENSE](../LICENSE) — MIT |
| 2 | Contribution guide | ✅ | [../CONTRIBUTING.md](../CONTRIBUTING.md) |
| 3 | Code of conduct | ✅ | [../CODE_OF_CONDUCT.md](../CODE_OF_CONDUCT.md) |
| 4 | Security policy | ✅ | [../SECURITY.md](../SECURITY.md) |
| 5 | Code owners | ✅ | [../CODEOWNERS](../CODEOWNERS) — `@omiinayak25` |
| 6 | Funding metadata | ✅ | [../.github/FUNDING.yml](../.github/FUNDING.yml) |

## 2. Issue & PR Templates

| # | Item | Status | Lives at |
|---|------|--------|----------|
| 7 | Bug report form | ✅ | [../.github/ISSUE_TEMPLATE/bug_report.yml](../.github/ISSUE_TEMPLATE/bug_report.yml) |
| 8 | Feature request form | ✅ | [../.github/ISSUE_TEMPLATE/feature_request.yml](../.github/ISSUE_TEMPLATE/feature_request.yml) |
| 9 | Documentation form | ✅ | [../.github/ISSUE_TEMPLATE/documentation.yml](../.github/ISSUE_TEMPLATE/documentation.yml) |
| 10 | Chore form | ✅ | [../.github/ISSUE_TEMPLATE/chore.yml](../.github/ISSUE_TEMPLATE/chore.yml) |
| 11 | Test-case form | ✅ | [../.github/ISSUE_TEMPLATE/test_case.yml](../.github/ISSUE_TEMPLATE/test_case.yml) |
| 12 | Template chooser (blank issues off, contact links) | ✅ | [../.github/ISSUE_TEMPLATE/config.yml](../.github/ISSUE_TEMPLATE/config.yml) |
| 13 | Pull-request template | ✅ | [../.github/PULL_REQUEST_TEMPLATE.md](../.github/PULL_REQUEST_TEMPLATE.md) |

## 3. Continuous Integration & Quality

| # | Item | Status | Lives at |
|---|------|--------|----------|
| 14 | CI pipeline (backend, frontend, quality) | ✅ | [../.github/workflows/ci.yml](../.github/workflows/ci.yml) |
| 15 | Static analysis / SAST (CodeQL) | ✅ | [../.github/workflows/codeql.yml](../.github/workflows/codeql.yml) |
| 16 | Dependency review | ✅ | [../.github/workflows/dependency-review.yml](../.github/workflows/dependency-review.yml) |
| 17 | Automated dependency updates | ✅ | [../.github/dependabot.yml](../.github/dependabot.yml) |
| 18 | Quality gates documented | ✅ | [QUALITY_GATES.md](QUALITY_GATES.md) |
| 19 | CI/CD documented | ✅ | [CI_CD_GUIDE.md](CI_CD_GUIDE.md) |

## 4. Documentation

| # | Item | Status | Lives at |
|---|------|--------|----------|
| 20 | Project README with quick start | ✅ | [../README.md](../README.md) |
| 21 | Architecture overview | ✅ | [../ARCHITECTURE.md](../ARCHITECTURE.md) |
| 22 | Governance & docs tree | ✅ | [`docs/`](.) (40+ guides) |
| 23 | Definition of Done | ✅ | [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) |
| 24 | Project management model | ✅ | [GITHUB_PROJECT_MANAGEMENT.md](GITHUB_PROJECT_MANAGEMENT.md) |
| 25 | Canonical project facts | ✅ | [PROJECT_METADATA.md](PROJECT_METADATA.md) |
| 26 | Portfolio & interview guides | ✅ | [PORTFOLIO_GUIDE.md](PORTFOLIO_GUIDE.md), [INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md) |

## 5. Examples & Onboarding

| # | Item | Status | Lives at |
|---|------|--------|----------|
| 27 | Runnable examples | ✅ | [`docs/examples/`](examples/) |
| 28 | Local environment setup | ✅ | [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md), [../.env.example](../.env.example) |
| 29 | One-command stack (Docker Compose) | ✅ | [../infrastructure/](../infrastructure/) |
| 30 | API reference (OpenAPI/Swagger) | ✅ | Backend `:8080/swagger-ui.html`; [API_BLUEPRINT.md](API_BLUEPRINT.md) |
| 31 | Postman collection | ✅ | [`postman/`](../postman/) |
| 32 | Demo credentials | ✅ | `demo.admin / Admin@12345` (synthetic, see [../README.md](../README.md)) |

## 6. Versioning & Releases

| # | Item | Status | Lives at |
|---|------|--------|----------|
| 33 | Semantic versioning policy | ✅ | [../VERSIONING.md](../VERSIONING.md) |
| 34 | API versioning policy | ✅ | [API_VERSIONING_POLICY.md](API_VERSIONING_POLICY.md) (`/api/v1/`) |
| 35 | Release notes / changelog | ✅ | [../CHANGELOG.md](../CHANGELOG.md) — Keep a Changelog format |
| 36 | Release automation | ✅ | [../.github/workflows/release.yml](../.github/workflows/release.yml) |
| 37 | Roadmap (current + future) | ✅ | [../ROADMAP.md](../ROADMAP.md) |

## 7. Discoverability (Topics / Keywords)

| # | Item | Status | Lives at / value |
|---|------|--------|------------------|
| 38 | Repository description set | ✅ | "Enterprise Healthcare Quality Engineering Platform" |
| 39 | Suggested GitHub topics | ✅ | `healthcare`, `quality-engineering`, `test-automation`, `spring-boot`, `react`, `fhir`, `playwright`, `rest-assured`, `cucumber`, `java`, `typescript`, `qa` |
| 40 | Discussions enabled & categorised | ✅ | [GITHUB_PROJECT_MANAGEMENT.md](GITHUB_PROJECT_MANAGEMENT.md) §5 |

---

## Compliance & Safety Notes

- **Data:** the platform uses **synthetic, PHI-safe data only**. No real patient
  data is present or accepted (issue forms enforce this via confirmation checkboxes).
- **Certification:** this project makes **no formal certification claims**
  (e.g., no HIPAA/SOC 2 attestation). It demonstrates engineering practices, not
  a certified product.
- **License:** distributed under the permissive **MIT** license; downstream use
  must retain the copyright and license notice.

## Readiness Summary

All 40 checklist items are satisfied. **omiiCARE_QA is open-source ready for the
1.0.0 release.** Re-run this checklist before each subsequent release and update
the version history below.

## References

- [GITHUB_PROJECT_MANAGEMENT.md](GITHUB_PROJECT_MANAGEMENT.md) — labels, milestones, boards
- [../CONTRIBUTING.md](../CONTRIBUTING.md) — how to contribute
- [../SECURITY.md](../SECURITY.md) — vulnerability reporting
- [../CHANGELOG.md](../CHANGELOG.md) — release notes
- [../CONTRIBUTORS.md](../CONTRIBUTORS.md) — credits

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Maintainer | Initial (Milestone 10) |
