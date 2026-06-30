# Performance Execution Guide — omiiCARE QA

> **PERFORMANCE SAFETY RULE:** Run every test below against **local / Docker /
> owned infrastructure ONLY** (default `http://localhost:8080`). **NEVER** load
> test a public website or any host you do not own.

This guide covers running the performance suite against local and Docker
environments, plus baselining and capacity planning.

---

## 1. Prerequisites

| Component | How to start | Verify |
|-----------|--------------|--------|
| Backend (Spring Boot) | `mvn spring-boot:run` (or Docker) | `curl http://localhost:8080/actuator/health` → `{"status":"UP"}` |
| Frontend (React) | `npm run dev` (port 5173) | open `http://localhost:5173` |
| Monitoring (M2) | `docker compose -f infrastructure/docker/docker-compose.yml up -d prometheus grafana` | Prometheus `:9090`, Grafana `:3000` |
| Load tools | install k6 / JMeter / Gatling | `k6 version`, `jmeter -v`, `mvn -v` |

Confirm the SUT is reachable and healthy **before** generating load:

```bash
curl -s http://localhost:8080/actuator/health
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo.admin","password":"Admin@12345"}'
```

---

## 2. Running Against Local

```bash
# k6 (primary)
BASE_URL=http://localhost:8080 USERNAME=demo.admin PASSWORD='Admin@12345' \
  k6 run quality/performance/k6/login-load.js

# JMeter (non-GUI)
jmeter -n -t quality/performance/jmeter/omiicare-load-test.jmx \
  -Jhost=localhost -Jport=8080 -Jthreads=25 -Jrampup=30 -Jloops=10 \
  -l results.jtl -e -o report-html

# Gatling
mvn gatling:test -Dgatling.simulationClass=omiicare.PatientSimulation \
  -DBASE_URL=http://localhost:8080
```

---

## 3. Running Against Docker

When the backend runs as a compose service you own:

```bash
docker compose -f infrastructure/docker/docker-compose.yml up -d
# Target the host-mapped port (still localhost from the test runner's view)
BASE_URL=http://localhost:8080 k6 run quality/performance/k6/login-load.js
```

To run k6 itself inside Docker on the same network:

```bash
docker run --rm -i --network host \
  -v "$PWD/quality/performance/k6:/scripts" \
  -e BASE_URL=http://localhost:8080 \
  grafana/k6 run /scripts/login-load.js
```

> All of the above stays within infrastructure you own. Do not substitute a
> remote URL.

---

## 4. Test Type Recipes

| Goal | How |
|------|-----|
| **Smoke** | Edit stages to `{ target: 2, duration: '30s' }` or run with `-e` overrides; expect 100% pass. |
| **Load** | Default `login-load.js` profile (ramp to 25 VUs, sustain 2m). |
| **Stress** | `appointment-stress.js` `stress_ramp` scenario (to 150 VUs). |
| **Spike** | `appointment-stress.js` `traffic_spike` scenario (instant 200 VUs). |
| **Soak / Endurance** | Run `login-load.js` with a long steady stage, e.g. `--duration 4h` style by extending the sustain stage. |
| **Volume** | Increase page size in the patients request (`size=500`) and/or seed a large data set via `database/`. |
| **Scalability** | Repeat the load profile while varying container CPU/memory limits; chart capacity vs resources. |

---

## 5. Capacity Planning

1. **Establish a baseline** at expected load (load profile). Record P95/P99, TPS,
   CPU, heap, DB pool usage.
2. **Find the knee** with the stress profile — the VU count where P95 latency or
   error rate starts climbing sharply.
3. **Compute headroom**: `safe_capacity ≈ knee_VUs × 0.7` (leave 30% margin).
4. **Map to users**: with average think time `T` seconds and per-iteration work,
   sustainable concurrent users ≈ `TPS × (T + avg_response_time)`.
5. **Scale test**: re-run at 1×, 2×, 4× CPU/memory and confirm capacity scales
   roughly linearly; if not, the bottleneck is shared (DB, connection pool).

Record findings in `REPORTING_GUIDE.md`'s baseline table each release.

---

## 6. During-Run Observability

While a test runs, watch the M2 Grafana dashboard
(`../observability/grafana/omiicare-qe-dashboard.json`) for:
- Request rate & total TPS
- Error rate (5xx)
- P95 / P99 latency
- JVM heap used vs max (GC pressure)
- Backend `up` health

Correlate any client-side threshold breach with a server-side spike to locate
the bottleneck (app, JVM, or DB).

---

## 7. CI Integration

k6 exits non-zero on threshold breach, so a load gate fits any CI:

```bash
k6 run --quiet quality/performance/k6/login-load.js || {
  echo "Performance gate failed"; exit 1;
}
```

Run gates against an **ephemeral local/Docker SUT spun up by the pipeline** —
never a shared or public environment.

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Performance Engineer | Initial (Milestone 7) |
