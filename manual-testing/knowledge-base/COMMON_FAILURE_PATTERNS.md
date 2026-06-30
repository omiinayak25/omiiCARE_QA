# Common Failure & Flaky Patterns

> **Purpose.** Catalog of recurring test failure and flakiness patterns in omiiCARE_QA — timing, auth/token expiry, test-data collisions, environment drift, and ordering dependencies — with concrete causes and fixes. Drives the flaky-% metric (M-11) down.

## Purpose

Most "random" failures are recognizable patterns. Naming them lets engineers diagnose fast and apply a known fix instead of re-investigating each time. Tied to this project's stack (Rest Assured, Playwright, Cucumber, JWT auth, Flyway seeds, Docker Compose).

## Scope

- **In scope:** Non-deterministic and environment-induced failures in manual and automated execution.
- **Out of scope:** Genuine product defects (those go to the defect tracker, not here).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Automation Engineer | Applies fixes; tracks flaky tests |
| QA Engineer | Recognizes the pattern before raising a defect |

---

## 1. Timing / Race Conditions

| Symptom | Root cause | Fix |
|---------|-----------|-----|
| UI test fails on slow load, passes on retry | Hard-coded `sleep` or asserting before render | Use Playwright auto-wait / `expect`; use Awaitility (4.2.2) for async API polling |
| API assert fails right after create | Read-after-write before commit visible | Poll until resource is queryable; avoid fixed delays |
| Appointment booking intermittently wrong | Clock/timezone skew | Pin timezone to UTC (backend default); use explicit timestamps |

## 2. Auth / Token Expiry

| Symptom | Root cause | Fix |
|---------|-----------|-----|
| 401 midway through a long suite | Access token expired (15-min lifetime) | Refresh token before expiry; re-authenticate per suite, not once globally |
| All tests 401 from start | Wrong credentials / profile | Confirm `demo.admin` / `Admin@12345` and correct base URI in `framework.properties` |
| 403 on a valid action | Role lacks permission | Verify the role-to-endpoint mapping; this may be a real RBAC defect, not flakiness |

## 3. Test-Data Collisions

| Symptom | Root cause | Fix |
|---------|-----------|-----|
| Unique-constraint error creating patient | Reusing a fixed MRN already seeded (`MRN-0001`) | Generate fresh MRNs via `PatientFactory`; never collide with seeded keys |
| Cross-test interference | Tests mutate shared seeded rows | Each test owns its data; treat seeds as read-only baseline |
| Duplicate-row failures after re-seed | Non-idempotent insert | Guard seeds with `WHERE NOT EXISTS` (project convention) |
| Cross-tenant data bleed | Missing tenant scope | Always operate within tenant `DEMO`; assert isolation |

## 4. Environment Drift

| Symptom | Root cause | Fix |
|---------|-----------|-----|
| Works locally, fails in CI | Different profile/DB (H2 dev vs PostgreSQL docker) | Run the same profile as CI; validate on `docker`/`qa` before sign-off |
| Service connection refused | Docker stack not fully up | Run `./scripts/health-check.sh`; wait for PASS before executing |
| Stale schema/data | Old volumes | `./scripts/reset.sh --yes` then `./scripts/start.sh` |
| External-system errors | WireMock stub missing/changed | Confirm WireMock (`:8089`) is healthy and stubs loaded |

## 5. Ordering Dependencies

| Symptom | Root cause | Fix |
|---------|-----------|-----|
| Suite passes in one order, fails in another | Tests depend on execution order | Make each test self-contained (own setup/teardown) |
| Seed-dependent failure | Repeatable seed ordering assumed | Seeds run alphabetically by description — use numeric prefixes (`R__seed_010_`, `R__seed_020_`) and keep cross-file dependencies explicit |
| Pagination test flaky | Relies on default sort | Assert with an explicit sort/order, not insertion order |

## 6. Reporting & Triage of Flakiness

| Step | Action |
|------|--------|
| Detect | Re-run a suspect test 5–10×; if mixed results, mark flaky |
| Quarantine | Tag and isolate so it does not block the build; log it |
| Track | Count toward M-11 (target < 2%); list in the execution report's top risks |
| Fix | Apply the matching pattern above; re-validate stability before un-quarantining |

---

## Related Documents

- [QA Metrics Catalog](../metrics/QA_METRICS.md) (M-11 Flaky-Test %)
- [Best Practices](BEST_PRACTICES.md)
- [Lessons Learned](LESSONS_LEARNED.md)
- [Execution Guide](../execution/EXECUTION_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
