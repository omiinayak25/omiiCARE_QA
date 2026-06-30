# Architecture Decision Records (ADRs)

> **Purpose.** This directory is the durable, version-controlled memory of *why*
> omiiCARE_QA is built the way it is. An ADR captures a single significant
> decision — the problem, the options weighed, the choice made, and the
> consequences accepted — so that future contributors (human or AI) inherit the
> reasoning, not just the result. Documentation is the source of truth;
> architecture is decided here before code is written.

## What an ADR Is

An Architecture Decision Record is a short, immutable document describing one
architecturally significant decision. "Architecturally significant" means the
decision is costly to reverse, shapes structure or dependencies, affects
cross-cutting quality attributes (security, testability, performance,
compliance), or constrains future milestones. ADRs are referenced by
[ARCHITECTURE.md](../../../ARCHITECTURE.md) §10 and by the
[MASTER_PROJECT_SPECIFICATION.md](../../../MASTER_PROJECT_SPECIFICATION.md) §5.

## When to Write One

Write an ADR when a decision:

- introduces, removes, or replaces a top-level module or a core technology;
- changes a dependency boundary or layering rule;
- affects a cross-cutting concern (multi-tenancy, audit, eventing, observability);
- alters a governance rule, a milestone fence, or the Definition of Done;
- trades one quality attribute for another in a way reviewers should see;
- creates a new top-level directory (required by
  [PROJECT_STRUCTURE.md](../../../PROJECT_STRUCTURE.md) conventions).

You do **not** need an ADR for routine code, naming, or reversible local choices.
When in doubt, write the ADR — it is cheaper than re-deriving the rationale later.

## ADR Sections

Every ADR uses the same eight sections, defined in
[`0000-adr-template.md`](0000-adr-template.md):

1. **Status** — Proposed / Accepted / Superseded.
2. **Context / Problem** — the forces and the question being answered.
3. **Decision Drivers** — the criteria the decision is judged against.
4. **Alternatives Considered** — real options, each with pros and cons.
5. **Decision** — the choice, stated plainly.
6. **Consequences / Tradeoffs** — what we gain and what we accept.
7. **Future Impact** — how this constrains or enables later milestones.
8. **References** — supporting documents and external sources.

## Numbering

- ADRs are numbered with a zero-padded four-digit sequence: `NNNN`.
- `0000` is reserved for the template.
- File name: `NNNN-short-kebab-case-title.md` (e.g. `0002-hybrid-database-h2-postgresql.md`).
- Numbers are assigned in order and never reused, even if an ADR is superseded.

## Lifecycle

```
Proposed  ──review──▶  Accepted  ──new decision──▶  Superseded
   │                                                     ▲
   └──────────────── rejected (closed, kept for record) ─┘
```

- **Proposed** — drafted and under review; not yet binding.
- **Accepted** — approved by the maintainer; binding on all later work.
- **Superseded** — replaced by a newer ADR. The old record stays in place
  (history is never deleted) and links forward to its replacement; the new ADR
  links back. ADRs are immutable once Accepted: to change a decision, write a
  new ADR that supersedes the old one rather than editing it in place.

## How to Add an ADR

1. Copy `0000-adr-template.md` to the next `NNNN-title.md`.
2. Fill in every section — no placeholders, per
   [MASTER_PROJECT_SPECIFICATION.md](../../../MASTER_PROJECT_SPECIFICATION.md) §7.
3. Set Status to `Proposed` and open a PR using the
   [pull request template](../../../.github/PULL_REQUEST_TEMPLATE.md).
4. On approval, set Status to `Accepted`, add the date, and add a row to the
   index table below.
5. If it supersedes an existing ADR, update both records' Status and links.

## ADR Index

| ID | Title | Status | Date |
|----|-------|--------|------|
| [0000](0000-adr-template.md) | ADR Template | Template | 2026-06-30 |
| [0001](0001-monorepo-structure.md) | Single enterprise monorepo over polyrepo | Accepted | 2026-06-30 |
| [0002](0002-hybrid-database-h2-postgresql.md) | Profile-driven hybrid database (H2 ↔ PostgreSQL) | Accepted | 2026-06-30 |
| [0003](0003-clean-architecture-ddd-backend.md) | Clean Architecture + DDD + Hexagonal for the backend | Accepted | 2026-06-30 |
| [0004](0004-resource-adapter-layer-automation.md) | Resource adapter layer for the automation platform | Accepted | 2026-06-30 |
| [0005](0005-java-spring-boot-backend.md) | Java 21 + Spring Boot 3 backend stack | Accepted | 2026-06-30 |
| [0006](0006-react-typescript-vite-frontend.md) | React + TypeScript + Vite + Material UI frontend | Accepted | 2026-06-30 |
| [0007](0007-flyway-database-migrations.md) | Flyway for versioned database migrations | Accepted | 2026-06-30 |
| [0008](0008-documentation-first-governance.md) | Documentation-first, milestone-fenced governance | Accepted | 2026-06-30 |

## Dependencies

- Governed by [ARCHITECTURE.md](../../../ARCHITECTURE.md) and
  [MASTER_PROJECT_SPECIFICATION.md](../../../MASTER_PROJECT_SPECIFICATION.md).
- Facts (versions, technology) defer to
  [docs/PROJECT_METADATA.md](../../PROJECT_METADATA.md).

## References

- Michael Nygard, *Documenting Architecture Decisions* (the original ADR pattern).
- [adr.github.io](https://adr.github.io/) — ADR community resources.
- [ROADMAP.md](../../../ROADMAP.md) for milestone scoping.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial ADR process, lifecycle, and index (Milestone 1) |
