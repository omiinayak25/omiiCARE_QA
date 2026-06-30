# Lessons Learned Log

> **Purpose.** Running log of concrete lessons from building and testing omiiCARE_QA — what happened, the impact, the resolution, and the durable practice it created. Seeded with real engineering and QA lessons from Milestones 1–6.

## Purpose

A lesson recorded once should never cost the team twice. This log captures root causes and the practices they produced so the same problem does not recur. New lessons are appended each milestone.

## Scope

- **In scope:** Engineering and QA lessons that change how we build or test.
- **Out of scope:** Individual defect detail (lives in the defect tracker).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Curates and reviews the log each milestone |
| All contributors | Submit lessons as they occur |

---

## Lessons Log

| # | Area | Lesson / What happened | Impact | Resolution & durable practice |
|---|------|------------------------|--------|-------------------------------|
| LL-01 | Backend mapping | **Nested-DTO CGLIB proxy issue.** A nested DTO surfaced through a Spring/CGLIB proxy serialized as a proxy class instead of the plain object, corrupting JSON output and confusing assertions. | API responses and tests intermittently saw proxy artifacts instead of clean data | Map to flat, concrete DTO types via MapStruct (no proxied nested entities crossing the controller boundary); QA asserts on the serialized JSON shape, not the Java type. Practice: never return entities/proxies from controllers — always a mapped DTO. |
| LL-02 | Database | **Flyway repeatable (`R__`) ordering.** Repeatable migrations run alphabetically by description (not numerically), so clinical seed depending on reference seed could load in the wrong order. | Seed failures when clinical data referenced a tenant not yet created | Use numeric prefixes in descriptions (`R__seed_010_reference_and_demo`, `R__seed_020_clinical_demo`) to force semantic order; keep every seed idempotent with `WHERE NOT EXISTS`. Practice: encode cross-seed dependencies in the name, never assume order. |
| LL-03 | Architecture | **DTO promotion.** Inline/anonymous response shapes were promoted to named, versioned DTOs once shared across endpoints. | Earlier duplication and drift between similar responses | Promote shared shapes to explicit DTOs with MapStruct mappers; tests bind to the named DTO contract. Practice: when a response shape is reused, name it. |
| LL-04 | Data | **Future-dated demo appointment.** The seeded appointment uses `2027-01-15` to decouple it from the current date. | Avoids tests breaking as real time advances past a fixed "today" | Anchor time-sensitive fixtures to fixed future dates; parameterize "now" where relative timing matters. |
| LL-05 | Auth | **15-minute access-token lifetime in long suites.** Long-running E2E suites hit 401 mid-run as the access token expired. | Intermittent auth failures mistaken for product defects | Refresh tokens proactively / re-authenticate per suite; recognized as a flaky pattern, not a defect. |
| LL-06 | Environment | **H2 (dev) vs PostgreSQL (docker/qa) parity.** Some behavior differed between the in-memory dev DB and PostgreSQL. | "Passes locally, fails in CI" confusion | Validate on the CI-equivalent profile (`docker`/`qa`) before sign-off; rely on Flyway's single dialect-compatible schema across both. |
| LL-07 | Automation design | **E2E excluded by default behind a profile.** Framework/unit tests must run without a live SUT; E2E (`@api-e2e`, `@ui-e2e`, `@bdd`) only under `-Pe2e`. | Keeps default `mvn -pl automation test` fast and CI-safe | Tag E2E suites and gate them behind the `e2e` profile; document both commands in the execution guide. |

---

## Related Documents

- [Best Practices](BEST_PRACTICES.md)
- [Common Failure Patterns](COMMON_FAILURE_PATTERNS.md)
- [Test Data Catalog](../test-data/TEST_DATA_CATALOG.md)
- [CHANGELOG.md](../../CHANGELOG.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
