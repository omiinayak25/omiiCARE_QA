# Resilience Test Cases — omiiCARE_QA

> **Milestone 7 — Reliability & Data QA.** `RES-*` cases validate that omiiCARE
> applies its resilience patterns correctly under dependency failure, slowness,
> and recovery. They pair with the chaos experiments in [`../chaos`](../chaos)
> and the target config in [`README.md`](README.md). Resilience4j is the intended
> implementation; cases describe expected behaviour against that target.

**Legend** — Pri: P0/P1/P2. Pattern: TO (timeout), RT (retry), CB (circuit
breaker), FB (fallback/degradation), BH (bulkhead), RC (recovery), FO (failover).

| ID | Pri | Pattern | Title | Precondition / Fault | Steps | Expected |
|----|-----|---------|-------|----------------------|-------|----------|
| RES-001 | P0 | TO | DB statement timeout bounds latency | Inject 200 ms+ DB delay (CHAOS-003) | Call a DB-bound endpoint. | Request completes or fails within the configured statement timeout; no thread hangs indefinitely. |
| RES-002 | P0 | TO | External call honours TimeLimiter | WireMock delays response > timeout | Call the external-dependent endpoint. | Caller receives a timeout-mapped response within `timeoutDuration` (2s); thread released. |
| RES-003 | P1 | RT | Retry recovers transient failure | 20% packet loss to DB (CHAOS-012) | Issue idempotent reads. | Transient failures retried with backoff; net success rate stays high; retry count bounded by `maxAttempts`. |
| RES-004 | P0 | RT | No retry on non-idempotent write | Force one failure on a create | Submit a create that fails mid-flight. | Operation is NOT blindly retried; no duplicate row created (verified via `duplicate_mrn_per_tenant`). |
| RES-005 | P1 | RT | Backoff is exponential with jitter | Sustained transient errors | Observe retry timing. | Inter-attempt delay grows (200ms → ~400ms → ~800ms) with jitter; not a tight loop. |
| RES-006 | P0 | CB | Breaker opens on sustained failure | WireMock returns 5xx (CHAOS-007) | Drive calls past `minimumNumberOfCalls` with > `failureRateThreshold` failures. | Breaker transitions to OPEN; subsequent calls fail fast (no downstream call). |
| RES-007 | P0 | CB | Breaker fails fast while open | Breaker already OPEN | Call the protected path. | Returns immediately with fallback/clear error; latency near-zero; downstream not contacted. |
| RES-008 | P1 | CB | Breaker half-opens and recovers | Restore WireMock after open | Wait `waitDurationInOpenState`; send probe calls. | Breaker goes HALF_OPEN, permits limited probes, then CLOSED on success; normal traffic resumes. |
| RES-009 | P1 | CB | Slow calls trip breaker | WireMock slow (> `slowCallDurationThreshold`) | Drive slow calls past threshold. | Breaker opens on `slowCallRateThreshold`, protecting threads. |
| RES-010 | P0 | FB | Cache-down falls back to DB | Redis stopped (CHAOS-004) | Request data normally served from cache. | Response served from DB; no 5xx; latency higher but request succeeds. |
| RES-011 | P1 | FB | Identity-down serves valid tokens | Keycloak stopped (CHAOS-006) | Call protected endpoint with valid unexpired JWT. | Request succeeds via cached JWKS; new logins fail with clear 401/503. |
| RES-012 | P2 | FB | Non-critical dependency degrades | Mailhog stopped | Trigger an action that sends mail. | Core action succeeds; mail is queued/skipped; no user-facing failure. |
| RES-013 | P1 | BH | Bulkhead isolates slow dependency | One dependency slow under load | Load the slow path while calling unrelated endpoints. | Unrelated endpoints stay healthy; only the bulkheaded path sheds load (`maxWaitDuration` exceeded → rejected). |
| RES-014 | P0 | BH | Pool starvation fails cleanly | Concurrency > pool size (CHAOS-002) | Exceed Hikari `maximum-pool-size`. | Excess requests time out per `connection-timeout` with 503; no deadlock; pool recovers after load. |
| RES-015 | P0 | RC | DB outage recovery | Stop then start Postgres (CHAOS-001/009) | Outage during traffic, then restore. | After restart, pool reconnects automatically; new requests succeed within seconds; `integrity-checks.sql` returns 0 rows. |
| RES-016 | P1 | RC | Container restart self-heals | `docker restart omiicare-postgres` | Mid-traffic restart. | Brief blip then automatic recovery; no manual intervention; no data loss. |
| RES-017 | P1 | RC | Retry backlog drains on recovery | External path down then restored (CHAOS-008) | Generate async/external work during outage. | On recovery, queued/retried work completes; nothing silently lost. |
| RES-018 | P2 | FO | Read-replica failover (where provisioned) | Primary read path degraded | Issue reads. | Reads route to a healthy replica; writes still target primary; no error surfaced (environment-dependent). |
| RES-019 | P1 | TO+CB | Combined timeout + breaker | Slow + failing external | Sustained slow/failing calls. | Timeouts count toward breaker; breaker opens; system protected end-to-end. |
| RES-020 | P0 | RC | No data corruption after fault | Any CHAOS experiment + recovery | After rollback, run integrity checks. | All assertions in `integrity-checks.sql` return 0 rows; no orphaned/duplicate/partial rows. |

## How to Run

Each case is exercised by running its mapped chaos experiment (see the
Pattern → Chaos mapping in [`README.md`](README.md)) while driving representative
API traffic and asserting the Expected column. Post-experiment, always re-run
[`../database-testing/sql/integrity-checks.sql`](../database-testing/sql/integrity-checks.sql)
to confirm data integrity (RES-020).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Reliability Engineer | Initial (Milestone 7) |
