# Chaos Engineering — omiiCARE_QA

> **Milestone 7 — Reliability & Data QA. Documentation v1.0.** This module
> defines the chaos-engineering approach for omiiCARE. The experiments are
> designed against the existing Docker stack
> ([`infrastructure/docker/docker-compose.yml`](../../infrastructure/docker/docker-compose.yml)).
> v1.0 is the disciplined method + experiment catalogue; automated execution
> (e.g. via a scheduled chaos runner) is a follow-on.

## Purpose

Deliberately inject failure into the running system to discover how it behaves
under adverse conditions *before* those conditions occur in production — and to
prove that recovery mechanisms actually work.

## Principles

1. **Steady-state hypothesis** — define a measurable "normal" (e.g. p95 latency
   < 300 ms, error rate < 1%, all health checks green) and predict it holds
   despite the injected fault.
2. **Real-world events** — inject failures that actually happen: a DB outage, a
   cache eviction, slow networks, container restarts, disk exhaustion.
3. **Minimise blast radius** — start in a non-production environment (the local
   Docker stack), one fault at a time, with the smallest scope that still tests
   the hypothesis.
4. **Abort conditions** — every experiment has explicit stop criteria; if data
   integrity or safety is threatened, abort and roll back immediately.
5. **Automate rollback** — each experiment specifies the exact command to restore
   steady state.

## Target Architecture (Docker stack)

| Component | Container | Failure modes exercised |
|-----------|-----------|-------------------------|
| Database | `omiicare-postgres` | stop/restart, slow network, connection exhaustion |
| Cache | `omiicare-redis` | stop, latency, eviction |
| Identity | `omiicare-keycloak` | stop, slow token endpoint |
| External stub / message path | `omiicare-wiremock` | stop, high latency, 5xx injection |
| Mail | `omiicare-mailhog` | stop (non-critical / degrade) |
| Object store | `omiicare-minio` | stop, disk full |
| Backend (future container) | `omiicare-backend` | restart, memory pressure |

> A message queue is not yet in the stack; queue-failure experiments
> (CHAOS-008) are written against the WireMock-fronted async/external path as a
> proxy and will retarget the broker when it lands.

## Failure Dimensions Covered

- **Service failure** — kill a dependency container (DB, cache, identity).
- **Database failure** — Postgres down / connection pool starvation.
- **Cache failure** — Redis down or evicting; verify graceful degradation.
- **Message-queue / external failure** — downstream unavailable or 5xx.
- **Slow network / high latency** — `tc netem` delay & packet loss.
- **Container restart** — verify reconnect and no data loss.
- **Disk full** — fill a volume; verify safe failure, not corruption.
- **Memory pressure** — constrain container memory; verify no crash loop.
- **Recovery validation** — after fault removal, confirm return to steady state.

## Method (per experiment)

```
1. Record steady state (health, latency, error rate) for a baseline window.
2. State the hypothesis ("steady state holds despite fault X").
3. Inject the fault with the documented command (limited blast radius).
4. Observe against abort conditions; capture metrics from Prometheus/Grafana.
5. Remove the fault (rollback command).
6. Confirm recovery to steady state; record findings & follow-ups.
```

## Observability During Experiments

Metrics and dashboards come from `omiicare-prometheus` and `omiicare-grafana`
(already in the stack). Watch: request error rate, p95/p99 latency, DB connection
pool saturation, circuit-breaker state (once Resilience4j lands — see
[`../resilience`](../resilience)), and health-check status.

## Safety / Abort

- Never run against production or real PHI.
- Abort immediately if: any data-integrity assertion (see
  [`../database-testing/sql/integrity-checks.sql`](../database-testing/sql/integrity-checks.sql))
  would fail, error rate exceeds the experiment's stated ceiling, or recovery
  does not begin within the stated window after rollback.

## Artifacts

| File | Purpose |
|------|---------|
| [`EXPERIMENTS.md`](EXPERIMENTS.md) | Catalogue of `CHAOS-*` experiments with hypothesis, method, expected resilience, rollback. |

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Reliability Engineer | Initial (Milestone 7) |
