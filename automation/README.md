# Automation — Quality Engineering Platform

> **Status:** Delivered (framework + example suites) — **Milestone 5**.
> A modular, configuration-driven, environment-independent test framework. The
> framework's own unit tests run on every build; suites that drive a live System
> Under Test are tagged and excluded by default.

## Purpose

The enterprise testing ecosystem for omiiCARE_QA: shared core, a pluggable
resource-adapter layer, PHI-safe test-data generators, and UI/API/BDD suites —
all reusable and switchable by configuration, never by code change.

## Layout

```
automation/src/test/
  java/com/omiicare/qa/automation/
    core/config/      FrameworkConfig (layered), TargetSystem (adapter registry)
    core/adapter/     ResourceAdapter, HttpResourceAdapter, AdapterFactory
    core/generators/  PatientFactory + SyntheticPatient (Datafaker, PHI-safe)
    framework/        framework unit tests (run by default)
    api/              Rest Assured API suite        (@Tag api-e2e)
    ui/               Playwright UI suite            (@Tag ui-e2e)
    bdd/              Cucumber glue + JUnit suite    (@Tag bdd)
  resources/
    config/framework.properties   features/*.feature   schemas/*.json
```

## Resource Adapter Layer

Tests reference a `TargetSystem` (LOCAL_OMIICARE, OPENMRS, OPENEMR, HAPI_FHIR,
SMART_HEALTH_IT, OPENFDA, DUMMYJSON, RESTFUL_BOOKER); `AdapterFactory` resolves
the concrete endpoint from configuration. Adding a target = registering an
adapter; switching environments = configuration only. No hardcoded URLs.

## Running

```bash
# Framework unit tests only (no SUT required) — runs in CI:
mvn -pl automation test

# Full suite against a running SUT (backend :8080, frontend :5173):
mvn -pl automation -Pe2e test
```

Configuration precedence: JVM system properties → environment variables →
`framework.properties`. Browser matrix and SUT credentials are configured there.

## Boundaries

- No performance/security/accessibility/visual testing here — those are the
  Milestone 7 `quality/` module.
- This milestone delivers the platform and representative suites, not thousands
  of test cases.

## References

- [ARCHITECTURE.md](../ARCHITECTURE.md) §6 · [docs/TEST_STRATEGY.md](../docs/TEST_STRATEGY.md)
- [docs/TEST_DATA_STRATEGY.md](../docs/TEST_DATA_STRATEGY.md) · [docs/INTEGRATION_GUIDE.md](../docs/INTEGRATION_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | SDET | Initial framework + example suites (Milestone 5) |
