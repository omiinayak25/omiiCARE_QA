# Development Workflow

> **Purpose.** Describe the end-to-end human development workflow for
> omiiCARE_QA: the disciplined cycle every change follows, how branches and pull
> requests flow, the local development loop, the quality gates enforced before
> push, and how milestones gate the work. It makes the
> [Master Project Specification](../MASTER_PROJECT_SPECIFICATION.md) §4 process
> concrete and actionable.

## Scope

The workflow for human contributors. AI contribution is governed by the parallel
[AI_DEVELOPMENT_WORKFLOW.md](AI_DEVELOPMENT_WORKFLOW.md) and
[AI_DEVELOPMENT_RULES.md](AI_DEVELOPMENT_RULES.md), which share this same cycle.
Infrastructure-dependent steps (containers, CI) note when they become available;
in Milestone 1 the workflow operates on documentation only.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Contributor | Follow the cycle; meet the DoD before requesting review |
| Reviewer | Apply adversarial review; verify the DoD; protect the milestone fence |
| Maintainer | Approve milestone transitions; own branch protection |
| QA Architect | Keep quality gates and this workflow current |

---

## 1. The Core Cycle

Every change — code or documentation — follows the same seven-step cycle from
[MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §4:

```
Design ─▶ Document ─▶ Review ─▶ Implement ─▶ Test ─▶ Refactor ─▶ Document
   ▲                                                                │
   └────────────────── feedback / iteration ───────────────────────┘
```

| Step | What happens | Principle anchor |
|------|--------------|------------------|
| **Design** | Decide the approach; consider patterns and seams | Architecture first (#2) |
| **Document** | Update docs/ADRs *before* code; docs are source of truth | Documentation first (#1) |
| **Review** | Peer/architect reviews the design + doc change | AI assists, humans decide (#10) |
| **Implement** | Write the smallest correct change matching the design | KISS / YAGNI |
| **Test** | Add/extend tests; verify behavior and edge cases | Testability by design (#8) |
| **Refactor** | Remove duplication and dead code; simplify | Reusability (#4) |
| **Document** | Reconcile docs with the final implementation | Documentation first (#1) |

Documentation changes first and last; a poor design is corrected in docs, not
built upon.

## 2. Branching & Pull Request Flow

```
        feature/<scope>-<short-desc>
                 │  (branched from develop)
   develop ◀─────┤  PR → review → green checks → squash/merge
      │          ▼
      │     delete feature branch
      ▼
    main  ◀── release PR from develop (tagged, semantic version)
```

- Branch from `develop`; name `feature/<module>-<short-desc>`,
  `fix/<...>`, or `docs/<...>`.
- Keep branches short-lived and rebased on `develop`.
- **Delete the feature branch immediately after it merges.** Only `main` (production-ready) and `develop` (active development) are permanent; the repo keeps no other long-lived branches.
- Open a pull request into `develop`; releases promote `develop → main`.
- `main` and `develop` are protected ([REPOSITORY_STANDARDS.md](REPOSITORY_STANDARDS.md) §8).

## 3. Local Development Loop

> **Milestone note.** Container infrastructure, profiles, and scripts arrive in
> **Milestone 2**. In Milestone 1 the loop is documentation-only: edit → preview
> markdown → check links → review.

From Milestone 2 onward the loop is:

1. Pull `develop`; create a feature branch.
2. Start the local stack (`setup`/`start` scripts; profile `local` or `docker`).
3. Make a small change following the core cycle.
4. Run format + lint + tests locally.
5. Self-verify the per-change [Definition of Done](DEFINITION_OF_DONE.md) §1.
6. Commit (one logical change, Conventional Commit), push, open a PR.

## 4. Quality Gates Before Push

The following must be green locally before pushing (mirrored by CI from
Milestone 8):

| Gate | Tool | Active from |
|------|------|-------------|
| Format | Spotless / Prettier | M2 |
| Lint / static analysis | Checkstyle, PMD, SpotBugs / ESLint | M2 |
| Unit + integration tests | JUnit 5, Mockito / Vitest | M3 / M4 |
| Coverage threshold | JaCoCo / coverage provider | M3 / M4 |
| Security scan | Dependency-Check / ZAP | M2 / M7 |
| Accessibility | axe-core / Lighthouse | M4 |
| Docs/links updated | doc lint + review | **M1** |

In Milestone 1 only the documentation and link gates apply; all others are
recorded N/A (see [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) §1).

## 5. Review Process

- Reviews are **adversarial**: the reviewer expects to find problems and looks
  for what is *missing*, not only what is wrong.
- Lenses: correctness, security (OWASP), architecture conformance
  ([DESIGN_PATTERNS.md](DESIGN_PATTERNS.md)), performance, tests, and — for
  healthcare-touching changes — audit logging and standards correctness.
- A reviewer may block on a principle violation; the author fixes it or records a
  justifying ADR.
- Approval requires every active DoD row satisfied. "Looks good" with no
  scrutiny is not a review.

## 6. ADR Creation Trigger

Open an ADR (`architecture/adr/NNNN-kebab-title.md`) when a change:

- adds/renames a top-level directory or module,
- introduces or removes a dependency or technology,
- changes a layering boundary, public contract, or data model shape,
- lowers a coverage threshold or amends a roadmap fence, or
- trades off a project principle.

The ADR records Problem, Alternatives, Decision, Tradeoffs, Future Impact, and is
reviewed with the change.

## 7. How Milestones Gate Work

- Work proceeds **one milestone at a time**; a contributor works only within the
  active milestone's scope ([ROADMAP.md](../ROADMAP.md)).
- Each milestone's **fence** is non-negotiable without a roadmap-amending ADR; a
  PR that crosses the fence is rejected (e.g. frontend code during M3).
- A milestone closes only when its per-milestone DoD is met
  ([DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) §2); that gate is the entry
  criterion for the next milestone.

## 8. Walkthrough — Adding a Change

1. **Read context.** Open [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md),
   the active milestone in [ROADMAP.md](../ROADMAP.md), and the relevant blueprint.
2. **Confirm scope.** Verify the change is inside the active milestone's fence.
3. **Design.** Choose the approach; identify any pattern or seam; decide if an
   ADR is required (§6).
4. **Document first.** Update the affected doc(s)/ADR before writing code.
5. **Branch.** `git switch -c feature/<module>-<desc>` from `develop`.
6. **Implement.** Smallest correct change matching the design and
   [CODING_STANDARDS.md](CODING_STANDARDS.md).
7. **Test.** Add/extend tests; run them green locally.
8. **Refactor.** Remove duplication/dead code; simplify.
9. **Reconcile docs.** Ensure docs match the final behavior; update
   CHANGELOG/ROADMAP if affected.
10. **Self-verify DoD.** Walk the §1 checklist of the
    [Definition of Done](DEFINITION_OF_DONE.md).
11. **Commit & push.** One logical Conventional Commit; push the branch.
12. **Open PR.** Fill the template, link the issue/milestone, request review.
13. **Address review.** Resolve findings; re-verify the DoD.
14. **Merge.** On approval and green checks, squash-merge and delete the branch.

## Examples

- A contributor improving the database blueprint updates
  [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md) and an ADR *before* writing the
  Flyway migration (steps 3–6), honoring documentation-first.
- A reviewer rejects a Milestone 3 PR that adds a React component because it
  crosses the M3 fence (§7).

## Future Enhancements

- A pull-request template embedding the DoD checklist and principle reminders.
- CI status checks that block merge until all active gates pass (Milestone 8).
- Pre-commit hooks running format + lint + link-check locally.

## Dependencies

- Implements [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §4.
- Enforces [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md),
  [CODING_STANDARDS.md](CODING_STANDARDS.md), and
  [REPOSITORY_STANDARDS.md](REPOSITORY_STANDARDS.md).
- Mirrored for agents by [AI_DEVELOPMENT_WORKFLOW.md](AI_DEVELOPMENT_WORKFLOW.md).

## References

- [PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md), [ROADMAP.md](../ROADMAP.md),
  [CONTRIBUTING.md](../CONTRIBUTING.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial human development workflow (Milestone 1) |
