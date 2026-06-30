# Performance Reporting Guide — omiiCARE QA

> **PERFORMANCE SAFETY RULE:** All data reported here comes from tests run
> against **local / Docker / owned infrastructure ONLY**. Never report or
> compare results gathered against systems you do not own.

This guide standardizes how performance results are captured, baselined, and
reported so trends are comparable across releases.

---

## 1. What to Report

Every performance run should capture, per critical endpoint:

| Metric | Source |
|--------|--------|
| Response time: avg / median / **P95** / **P99** / max | load tool summary |
| Throughput: req/s and **TPS** | load tool summary |
| Error rate (%) | load tool (`http_req_failed`) |
| Peak concurrency (VUs / threads) | load tool |
| CPU % (server) | Prometheus `process_cpu_usage` |
| JVM heap used / max | Prometheus `jvm_memory_used_bytes` |
| GC pause time | Prometheus `jvm_gc_pause_seconds` |
| DB connection pool active/idle/pending | Prometheus `hikaricp_connections_*` |
| Threads (live/peak) | Prometheus `jvm_threads_live_threads` |

---

## 2. Baselines

A baseline is the accepted performance of a build at the standard load profile.
Store one row per release:

| Release | Date | Endpoint | P95 (ms) | P99 (ms) | TPS | Err % | CPU % | Heap % | Verdict |
|---------|------|----------|----------|----------|-----|-------|-------|--------|---------|
| _example_ | 2026-06-30 | `GET /api/v1/patients` | 380 | 720 | 66 | 0.3 | 45 | 60 | PASS |

Rules:
- A new build **passes** if P95/P99/error are within the SLA targets in the
  performance `README.md` and within **+10%** of the prior baseline.
- A **regression** is any metric worse than baseline by **> 10%** — investigate
  before release.
- Update the baseline only after a green run is reviewed and approved.

---

## 3. Generating Reports

### k6
```bash
# Human summary (stdout) + machine-readable export
k6 run --out json=results.json quality/performance/k6/login-load.js
# Optional: pipe JSON into your own report generator or a dashboard.
```

### JMeter
```bash
jmeter -n -t quality/performance/jmeter/omiicare-load-test.jmx \
  -l results.jtl -e -o report-html
# Open report-html/index.html for the full dashboard (APDEX, percentiles, graphs).
```

### Gatling
```bash
mvn gatling:test -Dgatling.simulationClass=omiicare.PatientSimulation
# HTML report written under target/gatling/<run>/index.html
```

---

## 4. Dashboards (Server-Side)

Import `../observability/grafana/omiicare-qe-dashboard.json` into the M2 Grafana
instance. Panels to screenshot for each report:

| Panel | What it shows |
|-------|---------------|
| Request Rate | throughput per URI |
| Error Rate (5xx %) | server failures under load |
| Latency p95 / p99 | server-side latency vs client-side |
| JVM Heap Used vs Max | memory pressure / GC risk |
| Throughput (total req/s) | overall TPS |
| Backend Health (up) | availability during the run |

For deeper capacity views, add panels for:
- **CPU**: `rate(process_cpu_usage[1m])`
- **DB pool**: `hikaricp_connections_active`, `_idle`, `_pending`
- **Threads**: `jvm_threads_live_threads`, `jvm_threads_peak_threads`

---

## 5. Trend Reporting

Maintain a trend table across releases so regressions surface early:

| Sprint / Release | P95 patients (ms) | TPS | Err % | Notes |
|------------------|-------------------|-----|-------|-------|
| _R1_ | 380 | 66 | 0.3 | baseline established |
| _R2_ | 360 | 70 | 0.2 | index added, faster |
| _R3_ | 540 | 55 | 0.8 | REGRESSION — N+1 query |

Visualize trends in Grafana by streaming k6 results via Prometheus remote-write
(`--out experimental-prometheus-rw`) so historical runs live alongside server
metrics on one timeline.

---

## 6. Report Template

```
omiiCARE Performance Report — <release> — <date>
Environment: local | docker (owned infra only)
Profile: <load|stress|spike|soak>  Tool: <k6|jmeter|gatling>

Results (per endpoint):
  POST /api/v1/auth/login : P95 __ms  P99 __ms  TPS __  err __%
  GET  /api/v1/patients   : P95 __ms  P99 __ms  TPS __  err __%

Server: peak CPU __%  peak heap __%  DB pool peak active __  GC pause __ms
Verdict: PASS | FAIL vs SLA & baseline
Actions: <regressions / follow-ups>
```

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Performance Engineer | Initial (Milestone 7) |
