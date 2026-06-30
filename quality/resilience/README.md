# Resilience Patterns — omiiCARE_QA

> **Milestone 7 — Reliability & Data QA. Documentation v1.0.** Defines the
> resilience patterns omiiCARE applies (or will apply) and how they are tested.
> **Resilience4j** is the intended implementation library for the Spring Boot
> backend; the config blocks below are the target configuration and ship with the
> corresponding service modules.

## Purpose

Keep the system available and correct when dependencies (DB, cache, identity,
external services) are slow or failing — degrade gracefully instead of cascading
into a full outage. Pairs with [`../chaos`](../chaos) (which injects the faults)
and [`RESILIENCE_TEST_CASES.md`](RESILIENCE_TEST_CASES.md) (which verifies the
behaviour).

## Patterns & Application

| Pattern | Problem it solves | omiiCARE application |
|---------|-------------------|----------------------|
| **Timeout** | Unbounded waits exhaust threads. | Per-dependency timeouts on DB (statement/connection), cache, identity, and external HTTP calls. |
| **Retry** | Transient blips (brief network loss, restart). | Bounded retries with exponential backoff + jitter on *idempotent* operations only. |
| **Circuit breaker** | A failing dependency dragging the whole app down. | Open on sustained failure → fail fast → half-open probe → close on recovery. Applied to external/WireMock-fronted calls and optionally cache. |
| **Fallback** | Need a usable answer when the primary path fails. | Serve cached/last-known data, a default, or a clear degraded response instead of an error. |
| **Graceful degradation** | Non-critical dependency down. | Cache down → read from DB; mail down → queue/skip; feature-flag off optional paths. |
| **Bulkhead** | One slow dependency starving all threads. | Isolate dependency calls into bounded pools so failure is contained. |
| **Recovery / self-healing** | Restore normal operation automatically. | Reconnecting pools, breaker half-open probes, retry backlog drain — no manual steps. |
| **Failover** | A replica/instance is available. | Route to a healthy replica (DB read replica / redundant instance) where provisioned. |

## Target Configuration (Resilience4j)

> These are the intended settings; values are tuned per environment. They live in
> the backend's `application.yml` once the dependency is added.

```yaml
resilience4j:
  timelimiter:
    instances:
      externalService:
        timeoutDuration: 2s
        cancelRunningFuture: true
  retry:
    instances:
      externalService:
        maxAttempts: 3
        waitDuration: 200ms
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
        retryExceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
        # Only idempotent calls are wrapped with retry.
  circuitbreaker:
    instances:
      externalService:
        slidingWindowSize: 20
        minimumNumberOfCalls: 10
        failureRateThreshold: 50          # % failures to open
        slowCallDurationThreshold: 1s
        slowCallRateThreshold: 50         # % slow calls to open
        waitDurationInOpenState: 10s      # before half-open
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
  bulkhead:
    instances:
      externalService:
        maxConcurrentCalls: 20
        maxWaitDuration: 50ms
```

Datasource timeouts (HikariCP / JPA):

```yaml
spring:
  datasource:
    hikari:
      connection-timeout: 3000      # ms to wait for a pooled connection
      validation-timeout: 2000
      maximum-pool-size: 20
  jpa:
    properties:
      jakarta.persistence.query.timeout: 5000   # ms statement timeout
```

## Pattern → Chaos Mapping

| Pattern | Verified by chaos experiment |
|---------|------------------------------|
| Timeout | CHAOS-003 (slow DB), CHAOS-005 (slow cache) |
| Retry | CHAOS-009 (restart), CHAOS-012 (packet loss) |
| Circuit breaker | CHAOS-007 (external 5xx/outage) |
| Fallback / degradation | CHAOS-004 (Redis down), CHAOS-006 (Keycloak down) |
| Recovery | CHAOS-001/009 (DB outage & restart) |
| Bulkhead | CHAOS-002 (pool starvation) |

## Artifacts

| File | Purpose |
|------|---------|
| [`RESILIENCE_TEST_CASES.md`](RESILIENCE_TEST_CASES.md) | `RES-*` test cases validating behaviour under failure/timeout. |

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Reliability Engineer | Initial (Milestone 7) |
