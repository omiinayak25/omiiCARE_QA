# Manual Quality Engineering Assets

> **Status:** Delivered — **Milestone 6**.
> A complete, production-quality manual testing repository. Every asset traces to
> a documented requirement or business rule and to the **actually implemented**
> features (auth, patient, appointment, FHIR) — never random test cases.

## Purpose

Enable another QA team to execute an entire release using only this repository:
requirements, traceability, plans, executable test cases and suites, defect and
release management, risk analysis, metrics, and a knowledge base.

## Layout

```
manual-testing/
  requirements/   REQUIREMENTS.md (BR/FR/NFR/SEC/A11Y/PERF/FHIR/HL7 with IDs)
  rtm/            RTM.md (requirement -> manual/automated/specialized tests)
  test-strategy/  executable manual test strategy
  test-plan/      MASTER / SPRINT / RELEASE test plans
  test-cases/     AUTHENTICATION / PATIENT / APPOINTMENT / FHIR / ADMIN_AUDIT
  test-suites/    SMOKE / REGRESSION / NEGATIVE_BOUNDARY / EXPLORATORY
  checklists/     UI and API functional checklists
  bug-templates/  template, severity/priority matrix, lifecycle, RCA
  bug-reports/    realistic sample defects, triage guide
  risk-analysis/  risk register, risk matrix
  release/        release, go-live, rollback, deployment, prod-verification
  uat/            UAT plan        signoff/  QA sign-off template
  estimation/     test estimation (Test-Case-Point + PERT)
  execution/      execution guide + log templates
  metrics/        QA metrics catalog + execution report template
  test-data/      synthetic PHI-safe data catalog + boundary datasets
  knowledge-base/ healthcare QA guide, glossary, failure patterns, best practices
  training/       QA onboarding guide
```

## Traceability

Test case IDs (`TC-AUTH-*`, `TC-PAT-*`, `TC-APPT-*`, `TC-FHIR-*`, `TC-ADMIN-*`)
are stable and mapped in [rtm/RTM.md](rtm/RTM.md) to requirements and to the
automated suites in [automation/](../automation/) (PatientApiE2ETest,
LoginUiE2ETest, RunCucumberTest). Business rules (e.g. BR-APPT-001 no
double-booking) come from [docs/BUSINESS_RULES.md](../docs/BUSINESS_RULES.md).

## Boundaries

- No performance/security/accessibility/visual **automation** here — those are
  the Milestone 7 `quality/` module. This milestone is the manual asset library.
- All data is synthetic and PHI-safe.

## References

- [docs/TEST_STRATEGY.md](../docs/TEST_STRATEGY.md) - [docs/RTM.md](../docs/RTM.md)
- [docs/RISK_ANALYSIS.md](../docs/RISK_ANALYSIS.md) - [docs/TEST_PYRAMID.md](../docs/TEST_PYRAMID.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial manual QA repository (Milestone 6) |
