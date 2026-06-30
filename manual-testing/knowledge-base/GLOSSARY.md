# QA & Healthcare Glossary

> **Purpose.** Shared vocabulary for omiiCARE_QA — QA/testing terms, metrics, and healthcare/interoperability terms — so every document and conversation uses words the same way. Project-specific where relevant.

## Purpose

A single lookup for acronyms and terms used across the manual-testing and automation assets, reducing onboarding friction and ambiguity.

## Scope

- **In scope:** QA process, metrics, automation, and healthcare-domain terms referenced in this repository.
- **Out of scope:** General software-engineering glossary unrelated to QA.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Keeps definitions accurate and current |
| All QA | Use these terms consistently in docs and defects |

---

## A. QA & Testing Terms

| Term | Definition |
|------|------------|
| SUT | System Under Test — here, the backend + frontend + FHIR facade running locally |
| RTM | Requirements Traceability Matrix — maps requirements to tests |
| Smoke test | Minimal critical-path check run before deeper testing |
| Regression test | Re-run of existing cases to catch newly introduced defects |
| Boundary test | Test at the edges of valid input ranges |
| Blocked | A case that cannot run due to an environment/dependency failure |
| Defect leakage | Defects that escape a stage and are found later |
| Escaped defect | A defect found in a stage later than where it should have been caught |
| Quality gate | Pass/fail threshold that must be met to proceed/release |
| Test pyramid | Proportional mix of unit > integration > E2E tests |
| BDD | Behavior-Driven Development — Given/When/Then scenarios (Cucumber) |
| Gherkin | The plain-language syntax for BDD feature files |
| Flaky test | A test that passes and fails non-deterministically without code change |
| Adapter (framework) | Pluggable client resolving a `TargetSystem` to a concrete API client |
| Synthetic data | Fabricated, PHI-safe data generated for testing |
| Idempotent seed | A seed that can re-run without duplicating data |

## B. Metrics Acronyms

| Term | Definition |
|------|------------|
| MTTD | Mean Time To Detect — avg time from defect introduction to detection |
| MTTR | Mean Time To Resolve — avg time from defect open to verified fix |
| Defect density | Defects per unit of scope (module or KLOC) |
| Code coverage | % of code lines/branches exercised by tests (JaCoCo) |
| Mutation score | % of injected faults killed by the test suite |
| Flaky % | % of automated tests that are non-deterministic |
| Automation ROI | Net time saved by automation relative to its cost |
| Pass rate | Passed ÷ executed test cases |

## C. Healthcare & Interoperability Terms

| Term | Definition |
|------|------------|
| PHI | Protected Health Information — patient-identifying data; never real in this project |
| HIPAA | US privacy/security regulation for health information |
| FHIR | Fast Healthcare Interoperability Resources — REST/JSON health-data standard; omiiCARE exposes FHIR R4 read |
| FHIR R4 | Release 4 of the FHIR specification |
| HL7 | Health Level Seven — messaging standards body; HL7 v2 = segment/pipe message format |
| OperationOutcome | FHIR resource representing an error/warning result |
| MRN | Medical Record Number — patient business key; format `MRN-####` here |
| Provider | Clinician (e.g., doctor) delivering care; codes `DR-001`, `DR-002` |
| Encounter | A clinical interaction (visit/appointment) |
| Consent | A patient's authorization governing data use/access |
| Audit log | Append-only record of who did what, when |
| ICD-10 | Diagnosis coding system |
| CPT | Procedure coding system |
| LOINC | Lab/observation coding system |
| SNOMED CT | Clinical terminology for findings/concepts |

## D. Project / Platform Terms

| Term | Definition |
|------|------------|
| RBAC | Role-Based Access Control — 12 roles enforced via `@PreAuthorize` |
| Tenant | Top-level isolation boundary; demo tenant is `DEMO` |
| Flyway | DB migration tool; `V__` versioned, `R__` repeatable seeds |
| JWT | JSON Web Token — access (15 min) / refresh (7 day) auth tokens |
| Spring profile | Environment config selector: dev, test, local, docker, qa, stage, prod |
| Datafaker | Library generating synthetic data (`PatientFactory`) |
| Surefire | Maven plugin running unit/integration tests; reports under `target/surefire-reports/` |
| JaCoCo | Code-coverage Maven plugin |
| Allure / Extent | Test reporting tools (roadmap) |

---

## Related Documents

- [Healthcare QA Guide](HEALTHCARE_QA_GUIDE.md)
- [QA Metrics Catalog](../metrics/QA_METRICS.md)
- [Best Practices](BEST_PRACTICES.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
