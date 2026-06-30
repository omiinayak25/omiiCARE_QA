# Interview Guide

> **Purpose.** Turn omiiCARE_QA into interview leverage. This guide maps repository
> areas to the competencies interviewers probe for Senior QA, Senior Automation
> Engineer, SDET II/III, QA Lead, QA Architect, and Principal QA roles, and gives
> you ready-to-use answers, a walkthrough script, tradeoff prompts grounded in
> ADRs, and STAR stories derived from the milestones.

## Scope

- **In scope:** how to present and discuss this repository in technical and
  behavioral interviews — competency mapping, sample Q&A, an architecture
  walkthrough script, tradeoff talking points, and STAR stories.
- **Out of scope:** general interview logistics, compensation negotiation, and
  company-specific prep. Canonical facts defer to
  [PROJECT_METADATA.md](PROJECT_METADATA.md); never claim certifications
  (the platform models HIPAA-like practices for education only).

## Responsibilities

| Audience | How to use this guide |
|----------|-----------------------|
| Candidate (you) | Rehearse answers; pick stories that match the role |
| Interviewer (reviewing the repo) | See competencies evidenced by real artifacts |
| Mentor / mock interviewer | Source of prompts and expected depth |

---

## 1. Role → Competency → Repository Evidence

| Target role | Competencies probed | Where this repo proves it |
|-------------|--------------------|---------------------------|
| Senior QA | Test strategy, risk-based testing, RTM, defect lifecycle | [ROADMAP.md](../ROADMAP.md) M6; `manual-testing/`; [test_case issue form](../.github/ISSUE_TEMPLATE/test_case.yml) |
| Senior Automation Engineer | Framework design, POM, data-driven, reporting | M5 automation; [ADR 0004](architecture/adr/0004-resource-adapter-layer-automation.md); Playwright/Selenium/Rest Assured/BDD matrix |
| SDET II/III | Code + test design, CI integration, API/contract testing | Backend [ADR 0003](architecture/adr/0003-clean-architecture-ddd-backend.md)/[0005](architecture/adr/0005-java-spring-boot-backend.md); M7 contract/FHIR; M8 CI/CD |
| QA Lead | Process governance, DoD, planning, metrics | [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md); milestone gates/fences; PR template; [ADR 0008](architecture/adr/0008-documentation-first-governance.md) |
| QA Architect | Testability seams, environment independence, adapters | [ARCHITECTURE.md](../ARCHITECTURE.md) §6–9; [ADR 0002](architecture/adr/0002-hybrid-database-h2-postgresql.md)/[0004](architecture/adr/0004-resource-adapter-layer-automation.md) |
| Principal QA | Org-wide strategy, multi-platform vision, decision records | Whole monorepo; the ADR system; roadmap to v2.0; cross-cutting seams §7 |

## 2. Sample Q&A

**Q: Walk me through your test strategy for this platform.**
A: It follows the test pyramid (unit → component → integration → contract → API →
UI) with manual, performance, security, accessibility, and visual as explicit
layers ([ARCHITECTURE.md](../ARCHITECTURE.md) §9). Balance and ownership per layer
are governed by the pyramid; CI quality gates enforce the
[Definition of Done](DEFINITION_OF_DONE.md). Manual assets (RTM, suites) come in
M6 so automation in M5 is built on a deliberate strategy, not ad hoc.

**Q: How is the automation framework architected?**
A: Around a Resource Adapter Layer ([ADR 0004](architecture/adr/0004-resource-adapter-layer-automation.md)):
every target system (local omiiCARE, OpenMRS, OpenEMR, HAPI FHIR, SMART Health IT,
OpenFDA, DummyJSON, Restful Booker) implements one interface. Tests call the
interface, never URLs. Adding a target is adding an adapter; switching environments
is configuration only. Shared core/config/drivers/reporting serve Playwright,
Selenium, Rest Assured, and BDD uniformly.

**Q: How do you handle FHIR/HL7 in testing?**
A: Standards mapping (FHIR R4, HL7 v2, ICD-10, CPT, LOINC, SNOMED) is additive in
the backend (M3) so new standards don't force refactors, then validated in M7
(contract/FHIR validation). I'd verify resource structure, code-system URIs, and
required fields against the spec, using synthetic PHI-safe data only — the platform
makes no certification claims.

**Q: How does CI/CD enforce quality?**
A: M8 builds reusable GitHub Actions for build/lint/test/scan/release. Quality
gates map to the DoD: build, tests, coverage (JaCoCo), SonarQube/SpotBugs/PMD/
Checkstyle/Spotless, Dependency-Check, accessibility, and performance smoke.
Nothing merges "done" until gates pass — the PR template checklist mirrors this.

**Q: How would you test performance and security?**
A: M7 adds JMeter/k6/Gatling on owned infrastructure only, OWASP ZAP for security,
and axe-core/Lighthouse for accessibility. Frameworks plus representative suites —
not thousands of runs — and never load-testing third-party systems.

**Q: What design patterns appear here?**
A: Hexagonal ports/adapters and DDD in the backend ([ADR 0003](architecture/adr/0003-clean-architecture-ddd-backend.md));
Adapter/Strategy/Factory/Builder/Page Object and DI in automation (M5). The same
ports/adapters mental model spans app and test platform — a deliberate consistency.

## 3. Architecture Walkthrough Script (3–5 minutes)

1. **Frame it:** "One monorepo containing a healthcare app and the QA platform that
   tests it, built across ten fenced milestones." (cite [ADR 0001](architecture/adr/0001-monorepo-structure.md))
2. **Context (C4 L1):** users + SDETs → frontend SUT → backend + FHIR → DB, with
   externals stubbed via WireMock behind adapters ([ARCHITECTURE.md](../ARCHITECTURE.md) §2).
3. **Backend:** Clean Architecture + DDD + Hexagonal; dependencies point inward;
   domain is framework-free and unit-testable ([ADR 0003](architecture/adr/0003-clean-architecture-ddd-backend.md)).
4. **Data:** profile-driven H2 ↔ PostgreSQL, config-only switching, Flyway
   migrations ([ADR 0002](architecture/adr/0002-hybrid-database-h2-postgresql.md), [0007](architecture/adr/0007-flyway-database-migrations.md)).
5. **Automation:** Resource Adapter Layer for environment independence
   ([ADR 0004](architecture/adr/0004-resource-adapter-layer-automation.md)).
6. **Governance:** docs-first, fences, gates, ADRs ([ADR 0008](architecture/adr/0008-documentation-first-governance.md)).
7. **Close:** "Seams for multi-tenancy, eventing, and observability are anticipated
   now and implemented later — the v2.0 roadmap shows the path."

## 4. "Explain a Tradeoff" Prompts (cite ADRs)

- **Hybrid DB:** speed/zero-install (H2) vs production fidelity (PostgreSQL) — solved
  by profile-driven switching plus a PostgreSQL integration suite ([ADR 0002](architecture/adr/0002-hybrid-database-h2-postgresql.md)).
- **Monorepo:** atomic cross-module changes vs repo size/CI scoping ([ADR 0001](architecture/adr/0001-monorepo-structure.md)).
- **Clean Architecture:** testability/isolation vs more interfaces and mapping ([ADR 0003](architecture/adr/0003-clean-architecture-ddd-backend.md)).
- **Adapter layer:** test stability/extensibility vs upfront abstraction design ([ADR 0004](architecture/adr/0004-resource-adapter-layer-automation.md)).
- **Docs-first:** coherence/resumability vs a full milestone of no app code ([ADR 0008](architecture/adr/0008-documentation-first-governance.md)).

## 5. STAR Stories (derivable from milestones)

- **Governance (M1):** *Situation* a sprawling ten-part platform risked incoherence;
  *Task* establish governance; *Action* built ADR system, fences/gates, canonical
  metadata, templates; *Result* a fully resumable, internally consistent baseline.
- **Testability by design (M4→M5):** *Action* required stable selectors in the SUT so
  the automation framework needed zero app changes; *Result* decoupled SUT and tests.
- **Environment independence (M5):** *Action* designed the Resource Adapter Layer;
  *Result* tests run unchanged across seven environments and eight target systems.
- **Quality gates (M8):** *Action* mapped the DoD to CI gates and the PR checklist;
  *Result* "done" became objective and enforceable.

## Examples

- Asked "design a test automation framework," open the
  [ADR 0004](architecture/adr/0004-resource-adapter-layer-automation.md) adapter
  pattern and explain environment switching by configuration.
- Asked a behavioral "tell me about enforcing quality," use the M8 quality-gates
  STAR story and point to the [PR template](../.github/PULL_REQUEST_TEMPLATE.md).

## Future Enhancements

- Add a recorded architecture walkthrough video link once M4 UI exists.
- Add role-specific one-page cheat sheets generated from this guide.

## Dependencies

- Anchored by [PROJECT_METADATA.md](PROJECT_METADATA.md) and
  [ARCHITECTURE.md](../ARCHITECTURE.md).
- Cross-references the ADRs in [architecture/adr/](architecture/adr/README.md).

## References

- [PORTFOLIO_GUIDE.md](PORTFOLIO_GUIDE.md)
- [ROADMAP.md](../ROADMAP.md), [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md)
- [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial interview guide (Milestone 1) |
