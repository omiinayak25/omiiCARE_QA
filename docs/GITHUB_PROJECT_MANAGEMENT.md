# GitHub Project Management

> How omiiCARE_QA is planned, tracked, and triaged on GitHub: the issue-label
> taxonomy, the milestone map, sprint and release boards, the automation labels
> that drive workflows, and the Discussions categories. This document is the
> single source of truth for **how work flows through GitHub** in this repository.

## Purpose

Give every contributor (human or AI) one consistent model for labelling issues
and pull requests, mapping work to milestones, and moving cards across boards,
so the project history stays traceable from idea to merged change to release.

## Scope

- **In scope:** label taxonomy, GitHub Milestones, sprint/release project boards,
  label-driven automation, and Discussions categories.
- **Out of scope:** the branching and commit workflow ([../CONTRIBUTING.md](../CONTRIBUTING.md)),
  the milestone plan itself ([../ROADMAP.md](../ROADMAP.md)), CI/CD mechanics
  ([CI_CD_GUIDE.md](CI_CD_GUIDE.md)), and canonical project facts
  ([PROJECT_METADATA.md](PROJECT_METADATA.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Contributor | Apply the correct type label and milestone when opening an issue/PR |
| Reviewer | Confirm labels, milestone, and board column are correct before merge |
| Maintainer (`omiinayak25`) | Own the taxonomy, triage `triage`-labelled issues, curate boards and Discussions |

---

## 1. Issue-Label Taxonomy

Labels fall into five groups. **Type** is required on every issue; **Module**
mirrors the path-based PR labels in [`../.github/labeler.yml`](../.github/labeler.yml);
**Workflow**, **Priority**, and **Meta** are applied during triage.

### 1.1 Type labels (required — one per issue)

Set automatically by the issue forms in [`../.github/ISSUE_TEMPLATE/`](../.github/ISSUE_TEMPLATE/).

| Label | Color | Meaning | Issue form |
|-------|-------|---------|------------|
| `bug` | `#d73a4a` | Defect: behaviour differs from documented/expected | `bug_report.yml` |
| `enhancement` | `#a2eeef` | New capability or improvement | `feature_request.yml` |
| `documentation` | `#0075ca` | Docs add/change/fix | `documentation.yml` |
| `chore` | `#fef2c0` | Build, config, dependency, or housekeeping work | `chore.yml` |
| `test-case` | `#5319e7` | New or updated manual/automated test case | `test_case.yml` |

### 1.2 Module labels (path-based — auto-applied to PRs)

These match the categories in [`../.github/labeler.yml`](../.github/labeler.yml) and
are applied to PRs automatically by `actions/labeler` via
[`../.github/workflows/labeler.yml`](../.github/workflows/labeler.yml). Apply the
same labels to issues by hand to keep filtering consistent.

| Label | Color | Path triggers (PR) | Maps to module |
|-------|-------|--------------------|----------------|
| `backend` | `#1d76db` | `apps/backend/**` | apps/backend (Spring Boot) |
| `frontend` | `#0e8a16` | `apps/frontend/**` | apps/frontend (React) |
| `automation` | `#bfd4f2` | `automation/**`, `quality/**` | automation + quality suites |
| `docs` | `#0075ca` | `docs/**`, `**/*.md` | documentation tree |
| `quality` | `#c5def5` | `config/checkstyle|pmd|spotbugs|spotless/**` | static-analysis config |
| `infra` | `#5319e7` | Dockerfiles, `docker-compose*.yml`, `infra/**`, `deploy/**` | infrastructure |
| `ci` | `#ededed` | `.github/workflows/**`, `labeler.yml`, `dependabot.yml` | CI/CD |

### 1.3 Workflow labels (lifecycle / automation drivers)

| Label | Color | Meaning |
|-------|-------|---------|
| `triage` | `#e99695` | Newly filed; awaiting maintainer assessment (set by `bug`/`enhancement` forms) |
| `needs-info` | `#fbca04` | Blocked pending reporter clarification |
| `accepted` | `#0e8a16` | Triaged and scheduled into a milestone |
| `in-progress` | `#fbca04` | Work has started; branch open |
| `blocked` | `#b60205` | Cannot proceed; dependency noted in thread |
| `ready-for-review` | `#0052cc` | PR open and passing checks |
| `wontfix` | `#ffffff` | Closed by decision; rationale recorded |
| `duplicate` | `#cfd3d7` | Superseded by an existing issue (link required) |
| `good-first-issue` | `#7057ff` | Scoped, low-context entry point for new contributors |
| `help-wanted` | `#008672` | Maintainer is actively seeking contributors |

### 1.4 Priority labels

| Label | Color | Meaning |
|-------|-------|---------|
| `priority:critical` | `#b60205` | Release-blocking; security or data integrity |
| `priority:high` | `#d93f0b` | Important for the active milestone |
| `priority:medium` | `#fbca04` | Normal scheduling |
| `priority:low` | `#0e8a16` | Nice to have; backlog |

### 1.5 Meta labels

| Label | Color | Meaning |
|-------|-------|---------|
| `dependencies` | `#0366d6` | Applied by Dependabot ([`../.github/dependabot.yml`](../.github/dependabot.yml)) |
| `breaking-change` | `#b60205` | Requires a major-version bump per [VERSIONING.md](../VERSIONING.md) |
| `adr-needed` | `#5319e7` | Decision requires an Architecture Decision Record |
| `security` | `#b60205` | Security-relevant; follow [SECURITY.md](../SECURITY.md) before public discussion |

---

## 2. Milestones

GitHub Milestones map **one-to-one** to the ten project milestones in
[../ROADMAP.md](../ROADMAP.md). All ten are complete at the 1.0.0 release; future
milestones track post-1.0 roadmap items.

| GitHub Milestone | Roadmap milestone | Status |
|------------------|-------------------|--------|
| `M1 — Foundation, Architecture & Governance` | Milestone 1 | ✅ Complete |
| `M2 — Enterprise Infrastructure & Environment Foundation` | Milestone 2 | ✅ Complete |
| `M3 — Enterprise Healthcare Platform Core (Backend)` | Milestone 3 | ✅ Complete |
| `M4 — Enterprise Frontend Platform & Healthcare Portals` | Milestone 4 | ✅ Complete |
| `M5 — Enterprise Quality Engineering Platform (Automation)` | Milestone 5 | ✅ Complete |
| `M6 — Enterprise Manual Quality Engineering Assets` | Milestone 6 | ✅ Complete |
| `M7 — Advanced Quality Engineering Platform` | Milestone 7 | ✅ Complete |
| `M8 — Enterprise DevOps, CI/CD & Release Engineering` | Milestone 8 | ✅ Complete |
| `M9 — AI-Native Quality Engineering Platform` | Milestone 9 | ✅ Complete |
| `M10 — Production Hardening, Portfolio Excellence & Release 1.0.0` | Milestone 10 | ✅ Complete |

**Rules**

- Every issue and PR is assigned to exactly one milestone.
- The active milestone enforces a **fence**: changes outside its scope are
  rejected per [../ROADMAP.md](../ROADMAP.md). Issue forms include a fence-respect
  confirmation checkbox.
- Post-1.0 milestones (`v1.1`, `v1.2`, …) are created on demand from the
  *Future Versions* section of the roadmap.

---

## 3. Project Boards

Two GitHub Projects (v2) drive day-to-day flow.

### 3.1 Sprint board (`omiiCARE_QA — Sprint`)

Tracks in-flight work for the active milestone. Columns map to workflow labels.

| Column | Entry condition | Driving label |
|--------|-----------------|---------------|
| Backlog | Triaged, milestone-assigned | `accepted` |
| Ready | Scoped with acceptance criteria | `accepted` + priority |
| In Progress | Branch open, work started | `in-progress` |
| In Review | PR open and green | `ready-for-review` |
| Blocked | Waiting on dependency/info | `blocked` / `needs-info` |
| Done | Merged and verified | issue closed |

### 3.2 Release board (`omiiCARE_QA — Release`)

Tracks milestone-to-release readiness and groups closed issues by GitHub
Milestone. A milestone is **release-ready** when all its issues are `Done`, the
[Definition of Done](DEFINITION_OF_DONE.md) is met, and `CHANGELOG.md` has an
entry. Release mechanics live in [CI_CD_GUIDE.md](CI_CD_GUIDE.md) (`release.yml`).

---

## 4. Label-Driven Automation

Labels are not cosmetic — they trigger workflow behaviour.

| Trigger | Mechanism | Effect |
|---------|-----------|--------|
| PR opened/updated | `actions/labeler` (`labeler.yml`) | Auto-applies module labels from changed paths |
| `bug` / `enhancement` form submitted | Issue template `labels:` | Adds type + `triage` for maintainer review |
| Dependabot PR | `dependabot.yml` `labels:` | Adds `dependencies` + module label (`backend`/`frontend`/`ci`) |
| `security` applied | SECURITY.md process | Routes to private advisory; blocks public detail |
| `breaking-change` applied | `VERSIONING.md` | Flags the next release as a major bump |
| `good-first-issue` / `help-wanted` | GitHub surfacing | Promoted on the community contribution surfaces |

> Automation is **additive**: maintainers may always override an auto-applied
> label. The path rules are the contract — keep `labeler.yml` and section 1.2 in sync.

---

## 5. Discussions Categories

GitHub Discussions is the pre-issue forum (linked from
[`../.github/ISSUE_TEMPLATE/config.yml`](../.github/ISSUE_TEMPLATE/config.yml),
which disables blank issues and points contributors here first).

| Category | Format | Purpose |
|----------|--------|---------|
| 📣 Announcements | Announcement | Releases, milestone completions, roadmap updates (maintainer-posted) |
| 💬 General | Open | Project-wide conversation that is not yet an issue |
| 💡 Ideas | Open | Propose enhancements before filing a `feature_request` |
| 🙏 Q&A | Question/Answer | Setup, usage, and architecture questions; best answer marked |
| 🏛️ Architecture & ADRs | Open | Discuss decisions before raising an `adr-needed` issue |
| 🧪 Quality & Testing | Open | Test strategy, coverage, flake, and tooling discussion |
| 🎉 Show & Tell | Open | Share portfolio usage, demos, and derivative work |

Security reports never go to Discussions — use the private path in
[SECURITY.md](../SECURITY.md).

---

## References

- [../CONTRIBUTING.md](../CONTRIBUTING.md) — branching, commits, PR process
- [../ROADMAP.md](../ROADMAP.md) — the 10-milestone plan and fences
- [../.github/labeler.yml](../.github/labeler.yml) — path-based PR labels
- [../.github/dependabot.yml](../.github/dependabot.yml) — dependency-update labels
- [CI_CD_GUIDE.md](CI_CD_GUIDE.md) — pipelines and release workflow
- [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) — completion criteria
- [PROJECT_METADATA.md](PROJECT_METADATA.md) — canonical project facts
- [OPEN_SOURCE_READINESS.md](OPEN_SOURCE_READINESS.md) — community-health checklist

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Maintainer | Initial (Milestone 10) |
