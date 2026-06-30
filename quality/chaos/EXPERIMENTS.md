# Chaos Experiments Catalogue — omiiCARE_QA

> **Milestone 7 — Reliability & Data QA.** Each `CHAOS-*` experiment is tied to a
> container in the Docker stack
> ([`infrastructure/docker/docker-compose.yml`](../../infrastructure/docker/docker-compose.yml)).
> Run only against the local/test environment. See [`README.md`](README.md) for
> method, safety, and abort rules.

**Steady state (global baseline):** all health checks green, API error rate
< 1%, p95 latency < 300 ms over a 5-minute window.

`COMPOSE` below = `infrastructure/docker/docker-compose.yml`.

---

## CHAOS-001 — Database outage (Postgres down)

- **Hypothesis:** With Postgres down, write/read endpoints fail *fast and cleanly*
  (5xx with ProblemDetail, no hangs), no data corruption, and full recovery once
  the DB returns.
- **Blast radius:** Single container (`omiicare-postgres`); local stack only.
- **Method:**
  ```bash
  docker stop omiicare-postgres
  # drive traffic to a patient endpoint for ~2 min
  ```
- **Expected resilience:** Requests fail within the configured DB timeout (no
  thread-pool exhaustion); errors return RFC 7807; health check reports DB DOWN.
- **Abort if:** Backend enters a crash loop, or any integrity assertion would
  fail on recovery.
- **Rollback:**
  ```bash
  docker start omiicare-postgres
  ```
  Confirm health green and `integrity-checks.sql` returns 0 rows.

---

## CHAOS-002 — Database connection-pool starvation

- **Hypothesis:** Under more concurrent DB-bound requests than the pool size, the
  app queues/times out gracefully rather than deadlocking.
- **Method:** Drive concurrency above HikariCP `maximumPoolSize` while holding
  slow queries (e.g. `pg_sleep` via a test endpoint or `tc` delay on Postgres).
- **Expected resilience:** Excess requests time out per the connection-timeout
  setting and surface a clean 503; pool recovers when load drops.
- **Abort if:** Latency for unrelated endpoints never recovers post-load.
- **Rollback:** Stop load generator; remove any `tc` rule.

---

## CHAOS-003 — Slow database network (latency injection)

- **Hypothesis:** 200 ms added DB latency degrades but does not break the API;
  timeouts protect callers.
- **Method:**
  ```bash
  docker exec omiicare-postgres sh -c "tc qdisc add dev eth0 root netem delay 200ms"
  ```
- **Expected resilience:** Elevated p95 but error rate stays bounded; statement
  timeouts trip before requests hang indefinitely.
- **Rollback:**
  ```bash
  docker exec omiicare-postgres sh -c "tc qdisc del dev eth0 root netem"
  ```

---

## CHAOS-004 — Cache outage (Redis down)

- **Hypothesis:** With Redis down the system *degrades gracefully* — falls back to
  the source of truth (DB) with higher latency, not an outage.
- **Method:**
  ```bash
  docker stop omiicare-redis
  ```
- **Expected resilience:** Cached reads fall through to DB; no 5xx storm; sessions
  / rate-limit features degrade rather than fail hard.
- **Rollback:**
  ```bash
  docker start omiicare-redis
  ```

---

## CHAOS-005 — Cache high latency

- **Hypothesis:** Slow Redis does not block request threads beyond the cache
  timeout.
- **Method:**
  ```bash
  docker exec omiicare-redis sh -c "tc qdisc add dev eth0 root netem delay 500ms"
  ```
- **Expected resilience:** Cache calls time out and fall back to DB; p95 rises but
  requests complete.
- **Rollback:**
  ```bash
  docker exec omiicare-redis sh -c "tc qdisc del dev eth0 root netem"
  ```

---

## CHAOS-006 — Identity provider outage (Keycloak down)

- **Hypothesis:** With Keycloak down, *already-authenticated* requests with valid
  unexpired tokens keep working (offline JWT validation); new logins fail cleanly.
- **Method:**
  ```bash
  docker stop omiicare-keycloak
  ```
- **Expected resilience:** Token signature validation uses cached JWKS; protected
  endpoints serve valid tokens; login attempts return a clear 503/401.
- **Rollback:**
  ```bash
  docker start omiicare-keycloak
  ```

---

## CHAOS-007 — External dependency 5xx / outage (WireMock)

- **Hypothesis:** A failing downstream does not cascade — circuit breaker opens,
  fallback engages, core flows stay up.
- **Method:**
  ```bash
  docker stop omiicare-wiremock        # or configure WireMock to return 500s
  ```
- **Expected resilience:** Calls to the external path fail fast once the breaker
  opens; unrelated endpoints unaffected; breaker half-opens and recovers when the
  dependency returns. (Breaker behaviour is the target once Resilience4j ships —
  see [`../resilience`](../resilience).)
- **Rollback:**
  ```bash
  docker start omiicare-wiremock
  ```

---

## CHAOS-008 — Message-queue / async path failure (proxy via WireMock)

- **Hypothesis:** When the async/external delivery path is unavailable, messages
  are retried/queued and not silently lost; the producing request still succeeds.
- **Method:** Block the downstream (`docker stop omiicare-wiremock`) while
  triggering an action that emits an async/external call.
- **Expected resilience:** Producer returns success; the message is retried with
  backoff (or dead-lettered) when the path recovers — no data loss.
- **Rollback:** `docker start omiicare-wiremock`; confirm backlog drains.
- **Note:** Retarget to the real broker once a queue is added to the stack.

---

## CHAOS-009 — Container restart (recovery validation)

- **Hypothesis:** Restarting a dependency mid-traffic causes a brief blip, then
  automatic reconnection with no manual intervention and no data loss.
- **Method:**
  ```bash
  docker restart omiicare-postgres
  ```
- **Expected resilience:** Connection pool reconnects; in-flight requests fail
  cleanly but new requests succeed within seconds; `integrity-checks.sql` clean.
- **Rollback:** None needed (restart is self-healing); verify steady state.

---

## CHAOS-010 — Disk full (volume exhaustion)

- **Hypothesis:** A full data volume causes *safe* failure (refused writes,
  clear errors) — never silent corruption.
- **Method:** Fill the Postgres or MinIO volume to capacity in the test stack
  (e.g. write a large file into the mounted volume) to force `ENOSPC`.
- **Expected resilience:** Writes are rejected with explicit errors; reads
  continue; no partial/corrupt rows; recovery is clean after space is freed.
- **Abort if:** Any integrity assertion would fail.
- **Rollback:** Remove the filler file; restart the affected container.

---

## CHAOS-011 — Memory pressure

- **Hypothesis:** A memory-constrained dependency degrades predictably and does
  not enter an unrecoverable crash loop.
- **Method:** Recreate a service with a tight memory limit:
  ```bash
  docker update --memory=256m --memory-swap=256m omiicare-redis
  ```
- **Expected resilience:** Service stays within limit or restarts cleanly; the app
  degrades to DB-backed behaviour; no cascading failure.
- **Rollback:**
  ```bash
  docker update --memory=0 omiicare-redis   # remove limit
  docker restart omiicare-redis
  ```

---

## CHAOS-012 — Network partition between backend and DB (packet loss)

- **Hypothesis:** Intermittent packet loss to the DB is absorbed by retries and
  timeouts without unbounded latency.
- **Method:**
  ```bash
  docker exec omiicare-postgres sh -c "tc qdisc add dev eth0 root netem loss 20%"
  ```
- **Expected resilience:** Transient errors retried within policy; persistent loss
  trips timeouts/breaker; recovery on rule removal.
- **Rollback:**
  ```bash
  docker exec omiicare-postgres sh -c "tc qdisc del dev eth0 root netem"
  ```

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Reliability Engineer | Initial (Milestone 7) |
