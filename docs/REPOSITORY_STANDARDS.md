# Repository Standards

> **Purpose.** Define how the omiiCARE_QA monorepo is organized, named,
> documented, branched, and owned, so the repository stays coherent as ten
> milestones add modules. These standards govern the *structure* of the
> repository; [CODING_STANDARDS.md](CODING_STANDARDS.md) governs the code inside
> it.

## Scope

Directory and file-naming rules, README/charter requirements, the `.gitkeep`
policy, the eight-section documentation standard, markdown style and link
integrity, branch protection, the label taxonomy preview, commit/PR standards,
and ownership via `CODEOWNERS`. The authoritative tree lives in
[PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md); this document states the *rules*
that tree obeys.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Maintainer (`omiinayak25`) | Approve top-level structure changes; own branch protection |
| Contributors | Follow naming, documentation, and commit standards |
| Reviewers | Enforce link integrity, charter presence, and label hygiene |
| CODEOWNERS | Review changes within their owned paths |

---

## 1. Directory Standards

- The monorepo's top-level modules are fixed by [ARCHITECTURE.md](../ARCHITECTURE.md)
  §3: `apps/`, `database/`, `infrastructure/`, `automation/`, `manual-testing/`,
  `quality/`, `ai/`, `.github/`, `docs/`.
- **No new top-level directory without an ADR.** Adding or renaming a top-level
  directory is an architectural decision and requires an ADR in
  `architecture/adr/` plus maintainer approval.
- Within a module, follow the layout that module's README charter declares.
- One concern per directory; no "misc"/"utils dumping ground" folders.

## 2. File Naming Standards

| Artifact | Convention | Example |
|----------|-----------|---------|
| Documentation | UPPER_SNAKE_CASE `.md` for governance; Title-style for guides | `REPOSITORY_STANDARDS.md` |
| ADR | `NNNN-kebab-title.md` | `0007-adopt-flyway.md` |
| Java source | PascalCase `.java` | `PatientRepository.java` |
| TS component | PascalCase `.tsx` | `PatientCard.tsx` |
| SQL migration | `V<n>__snake_case.sql` | `V3__add_encounter.sql` |
| Config | tool-native lowercase | `docker-compose.yml`, `.editorconfig` |

(Language-specific naming is detailed in [CODING_STANDARDS.md](CODING_STANDARDS.md).)

## 3. README-per-Module Charters

Every top-level module has a `README.md` **charter** stating:

- **Mission** — what the module is responsible for.
- **Scope & boundary** — what it does *not* do (its fence).
- **Layout** — its internal directory rules.
- **Milestone** — when it is built (per [ROADMAP.md](../ROADMAP.md)).
- **Links** — to the governing blueprint(s) and ADRs.

A module without a charter is incomplete. Charters follow the same enterprise
tone as governance docs.

## 4. `.gitkeep` Policy

- Directories that must exist before their content lands (e.g. `architecture/adr/`,
  future module folders) carry a `.gitkeep`.
- `.gitkeep` is removed in the same change that adds the first real file.
- `.gitkeep` is never used to fake completeness of a deliverable.

## 5. Documentation Standard (Eight Sections)

Every document in the repository contains, in order: **Purpose, Scope,
Responsibilities, Examples, Future Enhancements, Dependencies, References, and a
Version History table**. The Version History table uses columns
`| Version | Date | Author | Notes |` and ends with the initial row dated
`2026-06-30`. No placeholders, Lorem Ipsum, or TODO sections are permitted
([MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §7).

## 6. Markdown Style

- One H1 (`#`) per file; sentence-case headings; logical heading hierarchy.
- Prefer **tables and bullets over prose**; keep paragraphs short.
- Fenced code blocks for code and diagrams; language hint where useful.
- Wrap intentionally; no trailing whitespace; files end with a newline.
- Target ~150–280 lines per governance document — useful and specific, not padded.

## 7. Link Integrity

- Cross-link related documents with **relative** markdown links.
- Links resolve at all times; broken links fail the per-change DoD
  ([DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) §1, row 17).
- Link to a sibling document even if it is authored in a later step of the same
  milestone — the link must resolve by milestone close.

## 8. Branch Protection Summary

| Branch | Protection |
|--------|-----------|
| `main` | Protected; no direct push; PR + required reviews; green required checks; linear history |
| `develop` | Protected; PR + review; green required checks |
| feature/* | Short-lived; branched from `develop`; deleted after merge |

`main` and `develop` are the only long-lived branches
([PROJECT_METADATA.md](PROJECT_METADATA.md) §2). Full enforcement configuration
lands with CI in Milestone 8; the policy applies from Milestone 1.

## 9. Label Taxonomy (Preview)

| Group | Example labels |
|-------|----------------|
| Type | `type:feature`, `type:bug`, `type:docs`, `type:chore` |
| Milestone | `milestone:M1` … `milestone:M10` |
| Module | `module:backend`, `module:frontend`, `module:automation`, `module:docs` |
| Priority | `priority:critical`, `priority:high`, `priority:medium`, `priority:low` |
| Status | `status:blocked`, `status:needs-review`, `status:ai-assisted` |

The taxonomy is finalized and applied with GitHub project management in
Milestone 8; this is the reserved preview.

## 10. Commit & PR Standards

- **Conventional Commits** (`type(scope): subject`), imperative mood, scope =
  module (e.g. `docs(governance): add repository standards`).
- **One logical change per commit**; every commit compiles and passes tests
  (from Milestone 2 onward).
- A pull request: targets `develop` (or `main` per release flow), links its
  issue/milestone, fills the PR template, and satisfies the
  [Definition of Done](DEFINITION_OF_DONE.md).

## 11. Ownership via CODEOWNERS

- A root `CODEOWNERS` maps each path to its responsible owner(s).
- Owner review is required for changes under owned paths.
- `docs/` governance is owned by the maintainer + QA Architect; module code is
  owned by its module lead. Ownership is reviewed at each milestone transition.

## Examples

- A pull request introducing a `tools/` top-level directory is rejected until an
  ADR justifies it (§1).
- A new module merged without a `README.md` charter fails review (§3).
- A documentation file missing the Version History table fails the eight-section
  standard (§5) and the DoD.

## Future Enhancements

- A markdown linter and link checker wired into CI to enforce §5–§7 automatically
  (Milestone 8).
- A repository-structure fitness check that fails on un-ADR'd top-level changes.
- Auto-labeling from changed paths using the §9 taxonomy.

## Dependencies

- Realizes the tree in [PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md) and
  [ARCHITECTURE.md](../ARCHITECTURE.md) §3.
- Enforced through [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) and
  [DEVELOPMENT_WORKFLOW.md](DEVELOPMENT_WORKFLOW.md).
- Facts from [PROJECT_METADATA.md](PROJECT_METADATA.md).

## References

- [CONTRIBUTING.md](../CONTRIBUTING.md), [CODING_STANDARDS.md](CODING_STANDARDS.md)
- Conventional Commits 1.0.0; GitHub branch protection and CODEOWNERS docs.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Maintainer | Initial monorepo repository standards (Milestone 1) |
