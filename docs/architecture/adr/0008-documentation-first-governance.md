# 0008. Documentation-first, milestone-fenced governance

## Status

Accepted — 2026-06-30

## Context / Problem

omiiCARE_QA is a ten-milestone enterprise build executed by a single maintainer
with AI assistance. Without a strong governance model, such a project tends toward
scope creep, inconsistent decisions, generated-all-at-once incoherence, and
documentation that drifts from reality. We must decide the operating model that
keeps the repository coherent end to end: how decisions are made, how scope is
bounded, and what counts as the source of truth, so that any contributor (human or
AI) can resume work from the documents alone.

## Decision Drivers

- Coherence: the repository must never depend on an external prompt to stay consistent.
- Scope control: each milestone must have an enforceable "do-not-build" fence.
- Resumability: documents must carry enough context for anyone to continue.
- Decision durability: significant choices must be recorded, not re-litigated.
- Portfolio quality: governance itself is part of what the repository showcases.

## Alternatives Considered

### Alternative A — Documentation-first, milestone-fenced, ADR-backed governance (chosen)
- **Pros:** docs are the source of truth and change before code; each milestone has
  a goal, deliverables, a fence, and a gate ([ROADMAP.md](../../../ROADMAP.md));
  significant decisions are captured as ADRs; a canonical metadata file prevents
  fact drift; the project is built one milestone at a time, never all at once;
  fully resumable from documents; strong enterprise/portfolio signal.
- **Cons:** heavy upfront documentation investment (an entire milestone — M1 — is
  documentation only); discipline required to keep docs and code in lockstep.

### Alternative B — Code-first, document-later
- **Pros:** fastest path to running software; visible progress sooner.
- **Cons:** documentation drifts or never arrives; decisions are lost; scope creeps;
  AI agents and new contributors lack reliable context; poor governance signal.

### Alternative C — Lightweight README-only governance
- **Pros:** low overhead; easy to start.
- **Cons:** insufficient for a ten-milestone, multi-module enterprise platform;
  no decision history, no enforceable fences, no canonical fact source;
  inconsistency accumulates.

## Decision

We will adopt documentation-first, milestone-fenced governance: documentation is
the source of truth and is changed **before** implementation; work proceeds one
milestone at a time, each with an explicit goal, deliverables, a do-not-build
fence, and a completion gate; every significant decision is recorded as an ADR in
this directory; and canonical facts live in
[docs/PROJECT_METADATA.md](../../PROJECT_METADATA.md), to which all other documents
defer. Every governance document carries the standard eight sections and contains
no placeholders, Lorem Ipsum, or TODOs.

## Consequences / Tradeoffs

**Positive**
- The repository is internally consistent and resumable from documents alone.
- Scope is bounded per milestone; fences are enforceable in review and CI.
- Decisions have durable rationale; reviewers and AI agents inherit context.
- Governance quality is itself a portfolio differentiator.

**Negative / Accepted tradeoffs**
- A full milestone (M1) produces no application code — accepted, deliberate cost.
- Ongoing discipline is required to keep docs and code synchronized; the
  Definition of Done makes "docs/ADR updated" a release condition.
- More process overhead per change than an ad-hoc workflow.

## Future Impact

This model governs all ten milestones and the post-1.0 roadmap: every future
feature changes documentation first, records significant decisions as ADRs, and
respects the active milestone's fence (changing a fence requires a roadmap-amending
ADR). It establishes the practice — ADRs, canonical metadata, fences, gates — that
later milestones (including the M10 documentation audit) rely on.

## References

- [MASTER_PROJECT_SPECIFICATION.md](../../../MASTER_PROJECT_SPECIFICATION.md) §4, §7
- [ROADMAP.md](../../../ROADMAP.md) (milestones, fences, gates)
- [docs/PROJECT_METADATA.md](../../PROJECT_METADATA.md) (canonical facts)
- [README.md](README.md) (ADR process); Michael Nygard, *Documenting Architecture Decisions*.
