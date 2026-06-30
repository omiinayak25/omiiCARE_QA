# Project Principles

> **Purpose.** The concise philosophy every contributor — human or AI — reads
> before making an implementation decision. When a choice is ambiguous, these
> principles, in order, break the tie.

## Scope

The ten guiding principles, what each means in practice, and how to apply them.
Detailed mechanics live in [CODING_STANDARDS.md](CODING_STANDARDS.md),
[DESIGN_PATTERNS.md](DESIGN_PATTERNS.md), and [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Every contributor | Apply these before coding; cite them in reviews and ADRs |
| Reviewers | Reject changes that violate a principle without a recorded ADR |

---

## The Ten Principles

| # | Principle | In practice |
|---|-----------|-------------|
| 1 | **Documentation first** | Update docs before code. Docs are the source of truth; code that disagrees with docs is the bug. |
| 2 | **Architecture first** | Design before building. If a better design appears, change docs, then refactor — never build on a poor architecture. |
| 3 | **Configuration over hardcoding** | URLs, credentials, environments, feature toggles live in config/secrets, never in code. Switching DB or environment is config-only. |
| 4 | **Reusability over duplication** | Extract shared abstractions (DRY). Duplicated logic is a defect waiting to diverge. |
| 5 | **Composition over inheritance** | Prefer assembling behavior from small parts over deep class hierarchies. |
| 6 | **Security by default** | Secure defaults, least privilege, no secrets in the repo, authorization on every endpoint, encryption where data is sensitive. |
| 7 | **Accessibility by default** | WCAG AA is a baseline, not an afterthought: semantic HTML, keyboard support, ARIA, contrast. |
| 8 | **Testability by design** | Stable selectors, deterministic seams, dependency injection, adapter interfaces — code is built to be tested. |
| 9 | **Automation as a first-class citizen** | The QA platform is a peer to the product, not an add-on. Manual steps that can be automated, are. |
| 10 | **AI assists, humans decide** | AI is optional, transparent, explainable, and reviewable. It never makes unreviewed changes. |

## Applying the Principles

- **Tie-breaking order:** when two principles conflict, the lower number wins.
  Documentation-and-architecture-first outrank convenience; security and
  accessibility outrank cleverness.
- **In ADRs:** decisions cite the principles they uphold and any they trade off
  (see [architecture/adr/](architecture/adr/)).
- **In reviews:** a reviewer may block on a principle violation; the author
  either fixes it or records an ADR justifying the exception.
- **Avoid over-engineering:** principles 4–5 and the patterns guide are applied
  *where they reduce complexity* (KISS/YAGNI), not dogmatically.

## Examples

- *Config over hardcoding:* a test that needs the FHIR base URL reads it from
  environment configuration and the adapter layer — it never embeds a literal
  URL (principle 3, supports [ARCHITECTURE.md](../ARCHITECTURE.md) §6).
- *Documentation first:* adding a new entity begins by updating
  [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md), then the migration, then code
  (principle 1).

## Future Enhancements

- A lightweight "principles checklist" embedded in the PR template's review
  section.
- Automated lint rules that catch the most common violations (hardcoded URLs,
  missing ARIA labels) in CI (Milestone 8).

## Dependencies

- Operationalized by [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) and
  [CODING_STANDARDS.md](CODING_STANDARDS.md).
- Referenced by [AI_DEVELOPMENT_RULES.md](AI_DEVELOPMENT_RULES.md).

## References

- SOLID, DRY, KISS, YAGNI; Clean Architecture; Hexagonal Architecture.
- [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial principles (Milestone 1) |
