# Master Project Specification

> **Source of truth.** This is the in-repository canonical specification for
> omiiCARE_QA. It is read at the start of every working session. It restates the
> mission, scope, milestone plan, and rules so the repository never depends on an
> external prompt to stay coherent. Where a fact (version, technology, role) is
> needed, this document defers to [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md).
> Documentation is always the source of truth; implementation follows it.

## Scope

Defines *what* omiiCARE_QA is, the v1.0 boundary, the milestone sequence and
their gates, and the engineering rules every contributor (human or AI) follows.
It does **not** restate detailed designs — those live in [ARCHITECTURE.md](ARCHITECTURE.md)
and the `docs/` blueprints.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Maintainer | Keep this spec authoritative; reconcile conflicts in its favor |
| All contributors | Read at session start; never contradict it |
| AI agents | Treat as binding context; follow [docs/AI_DEVELOPMENT_RULES.md](docs/AI_DEVELOPMENT_RULES.md) |

---

## 1. Mission

Build a production-grade **Enterprise Healthcare Quality Engineering Platform**
inside a single enterprise monorepo — never a demo, never toy code — that
showcases the full spectrum of QA/SDET/QA-architecture skill and could become one
of the best public healthcare-QA repositories.

## 2. Long-Term Vision (one repo, modular)

Healthcare web app · REST APIs · FHIR APIs · database · Docker infrastructure ·
monitoring · manual testing assets · automation (UI/API/DB) · performance ·
security · accessibility · visual testing · mobile (responsive/PWA) testing ·
CI/CD · AI-assisted testing · complete documentation.

## 3. Out of Scope for v1.0

These are **roadmap-only** and must not be built during the milestones:

- Native Android and native iOS applications
- Kubernetes deployment and a microservices split
- Distributed database
- Production cloud deployment
- Real hospital / payment-gateway / insurance-provider integration
- Real patient data
- Formal HIPAA certification or formal medical-device compliance

The architecture *anticipates* these with clean seams, adapters, and
documentation, but the implementation stops at this boundary.

## 4. How We Work

Every milestone follows: **Design → Document → Review → Implement → Test →
Refactor → Document.** Documentation changes first; implementation follows.
Architecture is designed before implementation; a poor architecture is corrected
(docs first) rather than built upon. Work proceeds **one milestone at a time** —
the whole project is never generated at once.

### Autonomous development

Ask for elevated permission **once**; after it is granted, continue until the
assigned milestone is fully complete without repeatedly asking "continue?".

## 5. Engineering Standards (summary)

- Principles: see [docs/PROJECT_PRINCIPLES.md](docs/PROJECT_PRINCIPLES.md).
- Patterns: see [docs/DESIGN_PATTERNS.md](docs/DESIGN_PATTERNS.md).
- Definition of Done: see [docs/DEFINITION_OF_DONE.md](docs/DEFINITION_OF_DONE.md).
- Coding standards: see [docs/CODING_STANDARDS.md](docs/CODING_STANDARDS.md).
- ADRs for every architectural decision: `docs/architecture/adr/`.
- Quality gates: build · tests · coverage · lint · format · docs/architecture
  updated · security scan · accessibility · performance smoke · no critical
  SonarQube issues · review complete. Nothing is "done" otherwise.
- Dependencies are evaluated (support, maintenance, license, security,
  popularity, performance, alternatives) before introduction.

## 6. Git & Branching

Two long-lived branches: `main` and `develop`. Meaningful Conventional Commit
messages. Every commit compiles, passes tests, and is exactly one logical change.
Credentials and branch policy: [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md)
and [CONTRIBUTING.md](CONTRIBUTING.md).

## 7. Documentation Requirements

Root governance docs and the `docs/` set are enumerated in
[PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md). Every document is enterprise
quality — **no placeholders, no Lorem Ipsum, no TODO sections** — and each
contains: Purpose, Scope, Responsibilities, Examples, Future Enhancements,
Version History, Dependencies, and References.

## 8. Milestone Plan

> Detailed goals, deliverables, and "do-not-build" fences for each milestone are
> tracked in [ROADMAP.md](ROADMAP.md). Summary:

| # | Milestone | Goal | Key fence |
|---|-----------|------|-----------|
| 1 | Foundation, Architecture & Governance | Architecture + all governance docs | No app/API/automation code |
| 2 | Infrastructure & Environment Foundation | Docker, profiles, Flyway, seeds, scripts, monitoring | No healthcare modules/business logic |
| 3 | Healthcare Platform Core (Backend) | Domain, REST + FHIR, auth, audit/validation/exception | No frontend/automation |
| 4 | Frontend Platform & Portals | React SUT, role-based portals, a11y, responsive | No test tooling |
| 5 | Quality Engineering Platform (Automation) | Playwright/Selenium/API/BDD + adapters + reporting | No mass test cases; no perf/sec/a11y/visual yet |
| 6 | Manual Quality Engineering Assets | Requirements, RTM, plans, cases, defects, metrics | No automation/perf/security implementation |
| 7 | Advanced Quality Engineering | Performance, security, a11y, visual, contract, chaos, observability, compliance | Frameworks + representative suites, not thousands of runs |
| 8 | DevOps, CI/CD & Release Engineering | Reusable GitHub Actions, quality gates, Docker, versioning | No AI enhancements |
| 9 | AI-Native Quality Engineering | Provider abstraction, prompt library, AI assistants | No autonomous/self-merging changes |
| 10 | Production Hardening & Release 1.0.0 | Full audit, portfolio polish, stable release | No new major features |

## 9. Compliance Posture

omiiCARE_QA models **HIPAA-like** privacy practices and **FHIR/HL7** conformance
for educational and portfolio purposes only. It uses synthetic, PHI-safe data
exclusively and makes **no formal certification claims**. See [SECURITY.md](SECURITY.md).

## Examples

- Before starting Milestone 2, a contributor reads this spec, confirms the M2
  goal and fence, and updates docs before writing Compose files.
- An AI agent asked to "add a login API" during Milestone 1 must decline: §8
  fences application code out of Milestone 1.

## Future Enhancements

Post-1.0 roadmap items (cloud deployment, native mobile automation, Kubernetes,
distributed testing, additional healthcare standards) are tracked in
[ROADMAP.md](ROADMAP.md) §Future Versions.

## Dependencies

- Anchored by [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md).
- Elaborated by [ARCHITECTURE.md](ARCHITECTURE.md), [ROADMAP.md](ROADMAP.md),
  and every document under `docs/`.

## References

- Master Build Prompt v1.1 (2026-06-30) — the originating brief this spec
  internalizes.
- [docs/AI_DEVELOPMENT_RULES.md](docs/AI_DEVELOPMENT_RULES.md),
  [docs/DEFINITION_OF_DONE.md](docs/DEFINITION_OF_DONE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Maintainer | Internalized master build prompt v1.1 as repo source of truth (Milestone 1) |
