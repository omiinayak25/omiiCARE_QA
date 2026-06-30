# Observability — omiiCARE QA (Milestone 7)

> **SCOPE NOTE:** Observability here applies to **our own** omiiCARE backend and
> infrastructure. Load-driven observability data is generated only against
> **local / Docker / owned infrastructure** — never public sites.

This module builds the QE observability layer on top of the **Milestone 2**
foundation: Spring Boot Actuator + Micrometer exposing **OpenTelemetry-style**
metrics at `/actuator/prometheus`, scraped by **Prometheus**, visualized in
**Grafana** (see `infrastructure/monitoring/`). M7 adds curated dashboards,
alerting rules, and the correlation/trace conventions QE relies on.

---

## 1. Foundation (from M2)

| Component | Location | Role |
|-----------|----------|------|
| Actuator / Micrometer | backend `/actuator/prometheus` | exposes app + JVM + HTTP metrics |
| Prometheus | `infrastructure/monitoring/prometheus/prometheus.yml` | scrapes `omiicare-backend` every 15s |
| Grafana | `infrastructure/monitoring/grafana/` | dashboards, Prometheus datasource (uid `prometheus`) |
| Docker compose | `infrastructure/docker/docker-compose.yml` | runs Prometheus `:9090`, Grafana `:3000` |

M7 deliverables in this directory **plug into** that stack — they do not
replace it.

---

## 2. Observability Pillars Covered

| Pillar | Examples | Metric / signal |
|--------|----------|-----------------|
| **Application** | request rate, latency, errors | `http_server_requests_seconds_*` |
| **Business** | logins, patients listed, appointments booked | custom Micrometer counters/timers + k6 custom metrics |
| **Infrastructure** | CPU, process uptime | `process_cpu_usage`, `process_uptime_seconds` |
| **JVM / runtime** | heap, GC, threads | `jvm_memory_used_bytes`, `jvm_gc_pause_seconds`, `jvm_threads_live_threads` |
| **Database** | connection pool, query latency | `hikaricp_connections_*` |
| **API** | per-endpoint percentiles | `http_server_requests_seconds_bucket` |

---

## 3. Metrics Reference (real names)

| Metric | Type | Use |
|--------|------|-----|
| `up{job="omiicare-backend"}` | gauge | target health (1=up, 0=down) |
| `http_server_requests_seconds_count` | counter | request count / throughput / error ratio |
| `http_server_requests_seconds_sum` | counter | total time → avg latency |
| `http_server_requests_seconds_bucket` | histogram | `histogram_quantile` for P95/P99 |
| `jvm_memory_used_bytes{area="heap"}` | gauge | heap usage |
| `jvm_memory_max_bytes{area="heap"}` | gauge | heap ceiling |
| `jvm_gc_pause_seconds_*` | summary | GC pause time |
| `jvm_threads_live_threads` | gauge | live threads |
| `hikaricp_connections_active` | gauge | DB pool in use |
| `process_cpu_usage` | gauge | process CPU (0–1) |

---

## 4. Correlation, Trace & Span IDs

For request correlation across logs, metrics, and traces:

- **Trace context**: Micrometer Tracing / OpenTelemetry propagates `traceId`
  and `spanId`. With Spring Boot, enable correlation in the log pattern, e.g.:
  ```
  logging.pattern.level = %5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]
  ```
- **Correlation ID**: accept/echo an `X-Correlation-Id` header on inbound
  requests; log it via MDC so a single user action is traceable end to end.
- **Exemplars**: Prometheus histograms can carry trace exemplars, letting you
  jump from a slow latency bucket in Grafana directly to the trace.
- **QE use**: during a load test, capture the `traceId` of any failed/slow
  request from logs and pivot to the trace to find the bottleneck span (DB call,
  external API, lock contention).

---

## 5. Dashboards

`grafana/omiicare-qe-dashboard.json` (import into the M2 Grafana) provides:

- Request rate (per URI)
- Error rate (5xx %)
- Latency p95 / p99
- JVM heap used vs max
- Backend health (`up`)
- Total throughput (req/s)

It uses the provisioned Prometheus datasource (uid `prometheus`) and a `$job`
template variable defaulting to `omiicare-backend`.

---

## 6. Alerting

`prometheus/alert-rules.yml` defines QE-relevant alerts:

| Alert | Condition | Severity |
|-------|-----------|----------|
| `BackendInstanceDown` | `up == 0` for 1m | critical |
| `HighHttpErrorRate` | 5xx ratio > 5% for 5m | critical |
| `HighP99Latency` | p99 > 1s for 5m | warning |
| `JvmHeapPressure` | heap > 85% of max for 10m | warning |

Wiring is described in `EXECUTION_GUIDE.md`. Alertmanager routing (Slack/email)
is left as a follow-up integration but the rules are evaluation-ready.

---

## 7. How QE Uses This

- **Before a release**: confirm all targets `up`, no firing alerts.
- **During load tests**: watch the QE dashboard for P95/P99, error rate, heap,
  and DB pool; correlate client-side threshold breaches with server spikes.
- **After**: snapshot dashboards into the performance report; file regressions.

---

## 8. Directory Layout

```
quality/observability/
├── README.md                       # this file
├── EXECUTION_GUIDE.md              # wiring into the M2 Prometheus/Grafana stack
├── prometheus/
│   └── alert-rules.yml             # error rate, p99, instance down, heap
└── grafana/
    └── omiicare-qe-dashboard.json  # request rate, errors, p95/p99, heap, health
```

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Observability Engineer | Initial (Milestone 7) |
