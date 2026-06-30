# Performance Testing — omiiCARE QA (Milestone 7)

> **PERFORMANCE SAFETY RULE (READ FIRST):**
> All load tests in this module target **ONLY local, Docker, or owned
> infrastructure** — by default `http://localhost:8080` (Spring Boot backend)
> and `http://localhost:5173` (React frontend). **NEVER point any load tool at
> a public website or any system you do not own and have explicit written
> permission to test.** Load testing third-party hosts is abusive and may be
> illegal. Every script here defaults to localhost for this reason.

System Under Test (SUT): Spring Boot backend with Actuator (`/actuator/health`,
`/actuator/prometheus`) + React frontend. Demo credentials:
`demo.admin` / `Admin@12345`.

---

## 1. Purpose & Scope

This module provides repeatable, version-controlled performance tests for the
omiiCARE platform. It validates that critical user journeys — authentication,
patient retrieval, and appointment booking — meet response-time, throughput, and
stability targets under realistic and adverse load, against infrastructure we own.

Goals:
- Establish performance **baselines** for key endpoints.
- Detect regressions before release via repeatable load profiles.
- Inform **capacity planning** (how many concurrent users a node sustains).
- Feed results into the M2 observability stack (Prometheus/Grafana).

---

## 2. Tooling

| Tool | Language / DSL | Best for | Location |
|------|----------------|----------|----------|
| **k6** | JavaScript (ES modules) | Developer-centric scripted load, CI gates, custom metrics | `k6/` |
| **JMeter** | XML test plan (GUI/CLI) | Classic enterprise load testing, rich listeners, HTML reports | `jmeter/` |
| **Gatling** | Java DSL | High-throughput simulations, code-as-test, assertions | `gatling/` |

All three exercise the same journeys so results can be cross-validated. k6 is the
primary CI gate; JMeter and Gatling provide alternative engines and reporting.

---

## 3. Test Types

| Type | Question it answers | Profile shape |
|------|---------------------|---------------|
| **Smoke** | Does the system work under minimal load? | 1–2 VUs, short |
| **Load** | Does it meet SLAs at expected peak? | Ramp to expected peak, sustain |
| **Stress** | Where is the breaking point? | Ramp beyond peak until degradation |
| **Spike** | Does it survive a sudden surge? | Instant jump to high VUs, hold, drop |
| **Soak / Endurance** | Are there leaks/degradation over time? | Moderate load for hours |
| **Volume** | How does it behave with large data sets? | Large payloads / big result pages |
| **Scalability** | How does capacity scale with resources? | Repeat load at different node sizes |

Mapping to scripts:
- `k6/login-load.js` — **Load** (login + list patients, ramping VUs).
- `k6/appointment-stress.js` — **Stress + Spike** (appointment booking).
- `jmeter/omiicare-load-test.jmx` — **Load** (login + patients, parameterized).
- `gatling/PatientSimulation.java` — **Load** (login + patients, closed model).

For soak/volume, reuse the load scripts with longer durations / larger page sizes
(documented in `EXECUTION_GUIDE.md`).

---

## 4. Metrics Collected

| Category | Metrics |
|----------|---------|
| **Response time** | min / avg / median / **P95** / **P99** / max per endpoint |
| **Throughput** | requests/sec, **TPS** (transactions/sec), iterations/sec |
| **Errors** | HTTP error rate (`http_req_failed`), business error count |
| **Concurrency** | active VUs / threads, queued requests |
| **Resource (server)** | CPU %, memory / JVM heap, GC pauses |
| **Connection pool** | active/idle/pending DB connections (HikariCP) |
| **Database** | query latency, slow queries, connection wait time |

Server-side metrics come from the M2 Prometheus/Grafana stack
(`/actuator/prometheus`); client-side metrics come from the load tool output.

### SLA Targets (baseline)

| Endpoint | P95 | P99 | Error rate |
|----------|-----|-----|------------|
| `POST /api/v1/auth/login` | < 600 ms | < 1000 ms | < 1% |
| `GET /api/v1/patients` | < 500 ms | < 1000 ms | < 1% |
| `POST /api/v1/appointments` (load) | < 800 ms | < 1500 ms | < 1% |
| `POST /api/v1/appointments` (stress) | < 2000 ms | — | < 10% |

These targets are encoded as thresholds in the k6 scripts and assertions in the
Gatling simulation, so a breach fails the run.

---

## 5. The Owned-Infrastructure-Only Rule

This rule is non-negotiable and restated because it matters:

- Default targets are **`localhost`** in every script.
- When testing Docker, target the compose service / mapped port you own.
- Before any run, confirm the `BASE_URL` / host points at your environment.
- Do **not** override `BASE_URL` to a staging/prod host without explicit sign-off
  and a maintenance window.
- **Never** target a public, third-party, or shared SaaS endpoint.

---

## 6. How to Run

Quick start (k6, the primary engine):

```bash
# Smoke / sanity (override stages by editing or using a smaller profile)
BASE_URL=http://localhost:8080 USERNAME=demo.admin PASSWORD='Admin@12345' \
  k6 run quality/performance/k6/login-load.js

# Stress + spike on appointment booking
BASE_URL=http://localhost:8080 k6 run quality/performance/k6/appointment-stress.js
```

JMeter (non-GUI):

```bash
jmeter -n -t quality/performance/jmeter/omiicare-load-test.jmx \
  -Jhost=localhost -Jport=8080 -Jthreads=25 -Jrampup=30 -Jloops=10 \
  -l results.jtl -e -o report-html
```

Gatling (Maven plugin):

```bash
mvn gatling:test -Dgatling.simulationClass=omiicare.PatientSimulation \
  -DBASE_URL=http://localhost:8080
```

Detailed instructions: see [`EXECUTION_GUIDE.md`](./EXECUTION_GUIDE.md) and
[`k6/README.md`](./k6/README.md).

---

## 7. Reporting

- **k6**: end-of-run summary (thresholds pass/fail), optional JSON/CSV export,
  or stream to Prometheus/Grafana.
- **JMeter**: `-e -o report-html` produces a full HTML dashboard.
- **Gatling**: generates an HTML report under `target/gatling/`.
- **Server-side**: import `../observability/grafana/omiicare-qe-dashboard.json`
  to watch P95/P99, throughput, CPU, heap, and DB pool during the run.

Full guidance — dashboards, baselines, trend reporting — is in
[`REPORTING_GUIDE.md`](./REPORTING_GUIDE.md).

---

## 8. Directory Layout

```
quality/performance/
├── README.md                  # this file
├── EXECUTION_GUIDE.md         # how to run against local/Docker, capacity planning
├── REPORTING_GUIDE.md         # dashboards, baselines, trend reporting
├── k6/
│   ├── login-load.js          # load: login + list patients
│   ├── appointment-stress.js  # stress + spike: appointment booking
│   └── README.md              # k6-specific run guide
├── jmeter/
│   └── omiicare-load-test.jmx # parameterized login + patients plan
└── gatling/
    └── PatientSimulation.java # login + list patients (Java DSL)
```

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Performance Engineer | Initial (Milestone 7) |
