# Definition of Done

> **Purpose.** Define the objective, non-negotiable bar that separates "in
> progress" from "done" in omiiCARE_QA. Nothing merges, closes, or ships until
> it satisfies the applicable Definition of Done (DoD). The DoD operationalizes
> the [Project Principles](PROJECT_PRINCIPLES.md) and the quality gates named in
> the [Master Project Specification](../MASTER_PROJECT_SPECIFICATION.md) §5.

## Scope

This document declares two distinct, layered checklists:

- **DoD (every change)** — applied to every individual change (commit, pull
  request, or documentation edit), at any milestone.
- **DoD (every milestone)** — the additional gate a milestone must clear before
  the [Roadmap](../ROADMAP.md) allows the next milestone to begin.

It does **not** restate how to run each tool (see
[CODING_STANDARDS.md](CODING_STANDARDS.md) and
[DEVELOPMENT_WORKFLOW.md](DEVELOPMENT_WORKFLOW.md)); it states *what must be true*.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Author (human or AI) | Self-verify the per-change DoD before requesting review |
| Reviewer | Refuse to approve any item that is unchecked or unverifiable |
| Maintainer (`omiinayak25`) | Verify the per-milestone DoD before a milestone transition |
| QA Architect | Keep coverage thresholds and quality gates current as tooling lands |

---

## 1. Definition of Done — Every Change

Every change satisfies **all** rows below before it is considered done. Items
whose tooling has not yet landed (infrastructure arrives in Milestone 2, CI in
Milestone 8) are marked **Phased** and verified manually until automated.

| # | Done criterion | How verified | Status in M1 |
|---|----------------|--------------|--------------|
| 1 | Build passes (`mvn verify` / `vite build`) | Local build + CI when present | Phased — no code in M1 |
| 2 | All tests pass (unit, component, integration as applicable) | Test run green | Phased |
| 3 | Coverage threshold met (see §3) | JaCoCo / Vitest coverage report | Phased |
| 4 | Lint passes (Checkstyle, PMD, SpotBugs / ESLint) | Tool exit code 0 | Phased |
| 5 | Format applied (Spotless / Prettier) | `spotless:check` / `prettier --check` clean | Phased |
| 6 | No critical or blocker SonarQube issues | SonarQube quality gate | Phased |
| 7 | Security scan passes (Dependency-Check / OWASP ZAP as applicable) | Scan report, no new high/critical | Phased |
| 8 | Accessibility checks pass (axe-core / Lighthouse) for UI changes | a11y report, WCAG AA | Phased (M4+) |
| 9 | Performance smoke passes for hot paths | k6 / JMeter smoke threshold | Phased (M7+) |
| 10 | Documentation updated (the change's docs are current) | Reviewer confirms docs match behavior | **Active** |
| 11 | ADR added/updated if an architectural decision was made | ADR present in `architecture/adr/` | **Active** |
| 12 | No dead code and no duplicated logic introduced | Review + SonarQube duplication | **Active** |
| 13 | Code review complete and approved | PR has required approvals | **Active** |
| 14 | Conventional Commit message; one logical change per commit | Commit history inspection | **Active** |
| 15 | CHANGELOG / ROADMAP / PROJECT_METADATA updated when affected | Reviewer confirms | **Active** |
| 16 | No placeholders, Lorem Ipsum, or TODO sections remain | Grep + review | **Active** |
| 17 | Cross-document links resolve (no broken relative links) | Link check | **Active** |

> **Rule of closure.** A change with any unchecked active row is *not done*. A
> change that only fails a Phased row because its tooling does not yet exist is
> done **iff** the corresponding manual verification was performed and recorded.

## 2. Definition of Done — Every Milestone

A milestone is done only when every change inside it met §1 **and** the
milestone-level criteria below are all satisfied. These are the entry criteria
for the next milestone (see [ROADMAP.md](../ROADMAP.md)).

| # | Milestone-done criterion | Evidence |
|---|--------------------------|----------|
| 1 | All milestone deliverables in the Roadmap exist | Roadmap deliverable list reconciled |
| 2 | The milestone fence was respected (nothing out-of-scope built) | Diff vs fence; no fenced artifacts |
| 3 | Every governance/blueprint doc touched is internally consistent | Cross-document consistency review |
| 4 | Full build + full test suite green across all modules | CI summary / local full run |
| 5 | Aggregate coverage meets the milestone threshold (§3) | Coverage trend report |
| 6 | Zero critical/blocker SonarQube issues; debt within budget | SonarQube project dashboard |
| 7 | Security scans clean (no unresolved high/critical) | Dependency-Check + ZAP reports |
| 8 | Accessibility baseline (WCAG AA) holds for shipped UI | axe/Lighthouse milestone report |
| 9 | Performance smoke baselines recorded for new hot paths | Perf baseline artifacts |
| 10 | ADRs exist for every significant decision in the milestone | ADR index updated |
| 11 | ROADMAP status flipped; CHANGELOG entry written | Roadmap + CHANGELOG diff |
| 12 | No placeholders, dead code, or duplicated modules anywhere added | Repository sweep |
| 13 | Maintainer sign-off recorded | Milestone transition approval |

### Milestone 1 application

Milestone 1 builds **no application, API, or automation code** ([ROADMAP.md](../ROADMAP.md)
§M1 fence). Its DoD reduces to the documentation and governance subset: every
governance document exists, follows the eight-section structure, is free of
placeholders, cross-links correctly, and is internally consistent with
[PROJECT_METADATA.md](PROJECT_METADATA.md). Build/test/coverage/security/a11y
rows are **Not Applicable** in M1 and become active from Milestone 2 onward.

## 3. Coverage Thresholds

| Layer | Minimum line coverage | Minimum branch coverage | Effective from |
|-------|-----------------------|-------------------------|----------------|
| Backend domain + application | 90% | 85% | M3 |
| Backend infrastructure + API | 80% | 70% | M3 |
| Frontend components + hooks | 80% | 70% | M4 |
| Automation framework core | 75% | — | M5 |

Thresholds are enforced by JaCoCo (Java) and the Vitest/coverage provider
(frontend) and gate the build once CI lands in Milestone 8. Lowering a threshold
requires an ADR.

## Examples

- A backend pull request in Milestone 3 that compiles and passes tests but drops
  domain coverage to 84% is **not done** — row §1.3 fails the 90% threshold.
- A Milestone 1 documentation pull request is **done** when rows 10–17 pass and
  build/test/security rows are recorded N/A; no code gate applies.
- Milestone 2 cannot begin until Milestone 1's §2 criteria — all governance docs
  present, consistent, placeholder-free, maintainer-approved — are met.

## Future Enhancements

- A CI "DoD bot" that posts the §1 checklist on every pull request and blocks
  merge until each active row is green (Milestone 8).
- Coverage-trend and quality-gate badges surfaced on module READMEs (Milestone 8).
- Automated link-integrity and placeholder-scan jobs wired into the docs lint.

## Dependencies

- Derives its authority from [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md)
  §5 and [PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md).
- Enforced through [DEVELOPMENT_WORKFLOW.md](DEVELOPMENT_WORKFLOW.md) and
  [AI_DEVELOPMENT_WORKFLOW.md](AI_DEVELOPMENT_WORKFLOW.md).
- Gates milestone transitions in [ROADMAP.md](../ROADMAP.md).

## References

- [CODING_STANDARDS.md](CODING_STANDARDS.md), [REPOSITORY_STANDARDS.md](REPOSITORY_STANDARDS.md)
- [DESIGN_PATTERNS.md](DESIGN_PATTERNS.md)
- [CONTRIBUTING.md](../CONTRIBUTING.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial repository-wide and per-milestone Definition of Done (Milestone 1) |
