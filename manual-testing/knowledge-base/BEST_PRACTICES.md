# QA Best Practices

> **Purpose.** Concise, enforceable best practices for testing omiiCARE_QA across manual and automated work — data, execution, automation, defects, and healthcare-specific discipline. Pairs with [Lessons Learned](LESSONS_LEARNED.md).

## Purpose

Codifies how the team works so quality is consistent and repeatable. Each practice is actionable and tied to this project's stack and conventions.

## Scope

- **In scope:** Test design, data, execution, automation, defect handling, and healthcare discipline.
- **Out of scope:** Coding standards for product code (see `CONTRIBUTING.md`).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Upholds and evolves these practices |
| All QA | Apply them in every cycle |

---

## 1. Test Data

- Use only synthetic, PHI-safe data; never real PHI in any artifact, log, or screenshot.
- Treat seeded DEMO rows (`MRN-0001..0003`, `DR-001/002`) as read-only baseline; generate fresh data with `PatientFactory` for create/mutate tests.
- Keep seeds idempotent — every insert guarded by `WHERE NOT EXISTS`.
- Use reserved domains/numbers (`@demo.example`, `+1-555-01xx`) for any contact field.

## 2. Execution

- Run smoke before regression; never proceed past a failed smoke.
- Validate the environment with `./scripts/health-check.sh` before executing.
- Test on the same Spring profile as CI (`docker`/`qa`) before sign-off — not just `dev`/H2.
- Log every case in real time; do not reconstruct results after the fact.

## 3. Automation

- Make each test self-contained: own setup and teardown, no cross-test ordering assumptions.
- Use auto-wait (Playwright) and Awaitility for async; never hard-coded sleeps.
- Refresh JWTs proactively for long suites (15-min access-token lifetime).
- Keep E2E behind the `e2e` profile/tags so unit/framework tests stay fast and SUT-free.
- Quarantine and track flaky tests; keep flaky % < 2% (M-11).

## 4. Defects

- One defect = one root cause; include build SHA, profile, steps, expected vs actual, severity.
- Record the defect's origin stage to make leakage (M-03) measurable.
- Distinguish flakiness from defects — re-run before filing.
- Verify fixes on the same environment that exposed them before closing.

## 5. Coverage & Traceability

- Trace every Critical requirement to at least one executed test (RTM, 100% target).
- Map every High/Critical risk to a mitigating test.
- Watch code coverage (JaCoCo) and mutation score together — high coverage with low mutation kill means weak assertions.

## 6. Healthcare Discipline

- Pair functional and audit checks: a successful sensitive action with no audit entry is a defect.
- Verify RBAC denials return 403 (not 500) and never leak PHI in error bodies.
- For FHIR, assert content type, `resourceType`, identifier/name/gender/birthDate mapping, and `OperationOutcome` on not-found.
- For coded data, assert both `system` URI and `code` are present and consistent.

## 7. Reporting

- Produce a Test Execution Report per cycle with an explicit GO/NO-GO recommendation.
- Compute metrics with the catalog's exact formulas — no ad-hoc definitions.
- Surface top risks every cycle, even when the gate passes.

---

## Related Documents

- [Lessons Learned](LESSONS_LEARNED.md)
- [Common Failure Patterns](COMMON_FAILURE_PATTERNS.md)
- [Healthcare QA Guide](HEALTHCARE_QA_GUIDE.md)
- [QA Metrics Catalog](../metrics/QA_METRICS.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
