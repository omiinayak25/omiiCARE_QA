# 0001. Single enterprise monorepo over polyrepo

## Status

Accepted — 2026-06-30

## Context / Problem

omiiCARE_QA delivers two intertwined platforms: a healthcare web application
(the System Under Test) and an enterprise quality-engineering platform that
tests it, plus database, infrastructure, manual-testing assets, advanced QE
frameworks, AI tooling, and governance. These parts evolve together across ten
milestones and constantly cross-reference one another (frontend ↔ backend
contracts, automation ↔ SUT selectors, docs ↔ everything). We must decide
whether all of this lives in one repository or is split across many. The choice
shapes how changes are reviewed, how contracts stay in sync, and how a single
contributor or AI agent reasons about the whole system.

## Decision Drivers

- Atomic, cross-cutting changes (a contract change touching backend, frontend,
  and tests) must be reviewable in one pull request.
- Documentation is the source of truth and must sit beside the code it governs.
- A single maintainer (`omiinayak25`) and AI agents need one coherent context.
- Portfolio value: reviewers should grasp the whole engineering story in one place.
- Milestone fences must be enforceable across modules from one set of rules.

## Alternatives Considered

### Alternative A — Single enterprise monorepo (chosen)
- **Pros:** atomic cross-module commits; one source of truth for docs and config;
  unified CI/quality gates; trivial cross-referencing; one clone tells the whole
  story; consistent standards and tooling everywhere.
- **Cons:** repository grows large; CI must scope work to changed modules to stay
  fast; coarse-grained access control; risk of accidental coupling if layering is
  not enforced.

### Alternative B — Polyrepo (one repo per module)
- **Pros:** independent release cadence and access control per module; smaller
  individual checkouts; clear physical ownership boundaries.
- **Cons:** contract changes span multiple PRs across repos and drift easily;
  documentation fragments; cross-repo versioning and dependency management add
  heavy overhead; the portfolio narrative scatters; far higher coordination cost
  for one maintainer.

### Alternative C — Hybrid (app monorepo + separate QA repo)
- **Pros:** some separation of the SUT from the test platform.
- **Cons:** the defining value proposition — app and its quality platform as
  first-class peers — is precisely what breaks across a repo boundary; selector
  and contract drift between SUT and tests becomes likely.

## Decision

We will host the entire platform in a single enterprise monorepo at
`https://github.com/omiinayak25/omiiCARE_QA.git`, with clearly separated
top-level modules (`apps/`, `database/`, `infrastructure/`, `automation/`,
`manual-testing/`, `quality/`, `ai/`, `docs/`, `.github/`, `scripts/`) as
described in [PROJECT_STRUCTURE.md](../../../PROJECT_STRUCTURE.md).

## Consequences / Tradeoffs

**Positive**
- Cross-module changes are atomic and reviewable in one PR.
- Governance documents live with the code they govern; one source of truth holds.
- CI, quality gates, and the Definition of Done apply uniformly.
- A single clone conveys the full architecture — strong portfolio signal.

**Negative / Accepted tradeoffs**
- The repository is large; CI must use path filters and per-module jobs (M8) to
  avoid running everything on every change.
- Module isolation is a discipline, not a physical boundary; layering rules and
  CODEOWNERS must prevent accidental coupling.
- Access control is repo-wide rather than per-module.

## Future Impact

The monorepo is the substrate for every later milestone. Clean module boundaries
preserve the option to extract a module into its own repository or service later
(see [ROADMAP.md](../../../ROADMAP.md) v2.0 microservices split) without a rewrite.
Adding a new top-level directory requires a superseding or companion ADR, per
[PROJECT_STRUCTURE.md](../../../PROJECT_STRUCTURE.md) conventions.

## References

- [ARCHITECTURE.md](../../../ARCHITECTURE.md) §3 (Monorepo composition)
- [PROJECT_STRUCTURE.md](../../../PROJECT_STRUCTURE.md)
- [MASTER_PROJECT_SPECIFICATION.md](../../../MASTER_PROJECT_SPECIFICATION.md) §1–2
- Google/Microsoft monorepo engineering practice (path-scoped CI).
