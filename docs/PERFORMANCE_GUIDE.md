# Performance Guide

> **Purpose.** Define the performance-engineering discipline for omiiCARE_QA:
> the test types, tooling, metrics, baselines, dashboards, and service-level
> objectives used to characterize and protect the platform's performance. This
> guide is documentation only (Milestone 1); the frameworks it describes are
> implemented in **Milestone 7** on the OpenTelemetry/observability foundation
> laid in **Milestone 2**.

## Purpose

- Establish how performance is measured, against what targets, and with what
  tools.
- Make capacity and scalability decisions data-driven via baselines and SLOs.
- Enforce safe, lawful load generation through the Performance Rule below.

## Scope

- **In scope:** smoke/load/stress/spike/soak/endurance/volume/scalability tests
  of the backend APIs, database, and frontend, plus workload modeling, baseline
  management, dashboards, and reporting.
- **Out of scope (v1.0):** production cloud load tests, geographically
  distributed load generation, and certification benchmarks. Performance work
  targets owned environments only.

> ## THE PERFORMANCE RULE (binding)
>
> **Load, stress, spike, and soak tests run ONLY against `local`, `docker`, the
> dedicated `perf` environment, or other project-owned infrastructure — NEVER
> against public websites or any host omiiCARE_QA does not own.** Generating load
> against third-party systems is prohibited. Stubbed externals (WireMock) are the
> only "external" targets, and they are owned. This rule is non-negotiable and
> overrides any test convenience.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Performance QA Engineer (M7) | Author/run test types; manage baselines |
| QA Architect | Own SLOs, thresholds, and workload models |
| Backend Engineer (M3) | Tune queries, pools, and endpoints to meet SLOs |
| DevOps Engineer (M8) | Provision `perf` env; run perf smoke in CI |

---

## 1. Test Types

| Type | Question answered | Pattern |
|------|-------------------|---------|
| **Smoke** | Does the system work under minimal load? | A few users, short duration — gate before deeper tests |
| **Load** | Does it meet SLOs at expected volume? | Ramp to expected concurrency, hold steady |
| **Stress** | Where does it break? | Ramp beyond expected load until failure |
| **Spike** | Can it absorb sudden surges? | Instant jump to high load, then drop |
| **Soak / Endurance** | Does it degrade over time (leaks)? | Sustained moderate load for hours |
| **Volume** | How does it handle large data sets? | Large DB/payload sizes |
| **Scalability** | How does it scale with resources? | Vary resources/instances, measure response |

## 2. Tooling

| Tool | Strength | Role |
|------|----------|------|
| **Apache JMeter** | Mature, GUI + CLI, protocol breadth | API/DB load suites, parameterized plans |
| **k6** | Scriptable (JS), CI-friendly, low overhead | Developer-centric load tests in pipelines |
| **Gatling** | High-throughput, expressive Scala/DSL, rich reports | Large-scale simulations, detailed reporting |

All three target owned infra only and feed a common reporting/metrics view.

## 3. Metrics

| Metric | Definition |
|--------|------------|
| Response time | Server time to handle a request |
| **P95 / P99 latency** | 95th/99th percentile response times (tail latency) |
| Throughput / TPS | Requests (transactions) processed per second |
| Concurrency | Simultaneous active virtual users |
| Error rate | Percentage of failed responses |
| Resource utilization | CPU, memory, disk, network on the SUT |
| Connection pools | DB/HTTP pool usage, saturation, wait time |
| Database performance | Query latency, slow queries, lock contention |
| API performance | Per-endpoint latency and error budget |
| UI performance | Page load, Time to Interactive, Core Web Vitals |

Percentiles (P95/P99) — not averages — drive SLO decisions, because tail latency
is what users actually feel.

## 4. Baseline & Capacity Planning

- A **baseline** captures current performance per scenario; new runs are compared
  to it, and regressions beyond a tolerance fail the run.
- Baselines are versioned with the docs and re-approved when the system or
  workload intentionally changes.
- **Capacity planning** uses load/scalability results to project the headroom and
  resource needs for target concurrency and data volume.

## 5. Dashboards (Grafana)

- Performance metrics flow through the **OpenTelemetry** instrumentation
  (correlation/request/trace/span IDs) into **Prometheus**, visualized in
  **Grafana**.
- Dashboards show latency percentiles, throughput, error rate, resource use, and
  pool saturation in real time during a run.
- Trace IDs link a slow request in a dashboard back to its span in the SUT.

## 6. Thresholds / SLOs

| Indicator (SLI) | Example objective (SLO) |
|-----------------|-------------------------|
| API P95 latency | ≤ 500 ms under expected load |
| API P99 latency | ≤ 1000 ms under expected load |
| Error rate | < 1% under expected load |
| Throughput | Meets the modeled target TPS |
| Soak stability | < 5% latency drift over the soak window; no memory growth trend |

> Numbers above are **illustrative defaults**; concrete SLOs are set per endpoint
> and ratified by the QA Architect before M7 execution. A test fails when an SLO
> threshold is breached, not merely when an average looks high.

## 7. Workload Modeling

- Derive realistic user journeys per RBAC role (e.g., Doctor reviewing charts,
  Reception scheduling, Lab posting results).
- Weight scenarios by expected frequency; parameterize with synthetic, PHI-safe
  data.
- Model think-time, ramp-up, steady-state, and ramp-down so load resembles real
  traffic rather than an unrealistic flood.

## 8. Reporting

| Section | Contents |
|---------|----------|
| Summary | Test type, environment, date, pass/fail vs SLOs |
| Workload | Scenarios, concurrency, ramp profile, data volume |
| Results | P50/P95/P99, throughput, error rate, resource use |
| Comparison | Delta vs baseline; regression callouts |
| Bottlenecks | Slow endpoints/queries, pool saturation, GC pauses |
| Recommendations | Tuning actions, owner, target milestone |

## Examples

- **Load test (owned infra):** ramp to modeled concurrency against the `docker`
  stack; P95 of `420 ms` passes the `≤ 500 ms` SLO; the run is baselined.
- **Soak test:** an 8-hour run on `perf` shows steady memory growth — a leak —
  flagged with the trace ID of the offending endpoint for backend remediation.
- **Prohibited request declined:** a request to load-test a public FHIR sandbox is
  refused under the Performance Rule; the HAPI FHIR target is instead run as an
  owned WireMock-backed adapter.

## Future Enhancements

- Continuous performance regression in CI with automated baseline comparison (M8).
- Distributed load generation and cloud-scale runs (post-1.0, v2.0).
- Frontend Real-User-Monitoring correlation with backend traces.

## Dependencies

- Builds on OpenTelemetry/Prometheus/Grafana from [ROADMAP.md](../ROADMAP.md)
  Milestone 2; frameworks delivered in Milestone 7.
- Targets environments in [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md)
  (notably `perf`).
- Tooling anchored by [PROJECT_METADATA.md](PROJECT_METADATA.md) §3.

## References

- Apache JMeter, k6, and Gatling documentation.
- Google Core Web Vitals; SRE workbook (SLI/SLO/error budgets).
- [SECURITY_TESTING_GUIDE.md](SECURITY_TESTING_GUIDE.md),
  [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Performance/QA Specialist | Initial performance-engineering guide (Milestone 1) |
