# Observability Execution Guide — omiiCARE QA

> **SCOPE NOTE:** Wires QE observability into **our own** M2 Prometheus/Grafana
> stack. Generate load only against **local / Docker / owned infrastructure**.

This guide explains how to wire the M7 alert rules and dashboard into the
existing **Milestone 2** monitoring stack
(`infrastructure/monitoring/`, `infrastructure/docker/docker-compose.yml`).

---

## 1. Start the M2 Stack

```bash
docker compose -f infrastructure/docker/docker-compose.yml up -d prometheus grafana
# Prometheus → http://localhost:9090
# Grafana    → http://localhost:3000  (admin / admin by default)
```

Confirm the backend is being scraped: open
`http://localhost:9090/targets` and check `omiicare-backend` is **UP**. If the
backend runs on the host, the scrape target is `host.docker.internal:8080`
(already configured in `prometheus.yml`).

---

## 2. Load the Alert Rules

The M2 `prometheus.yml` does not yet reference rule files. Add this block and
mount the rules file:

**`infrastructure/monitoring/prometheus/prometheus.yml`** — add at top level:
```yaml
rule_files:
  - /etc/prometheus/alert-rules.yml
```

**`infrastructure/docker/docker-compose.yml`** — add a read-only mount to the
`prometheus` service `volumes:` list:
```yaml
      - ../../quality/observability/prometheus/alert-rules.yml:/etc/prometheus/alert-rules.yml:ro
```

Reload Prometheus (the compose command already enables `--web.enable-lifecycle`):
```bash
curl -s -X POST http://localhost:9090/-/reload
```

Verify rules loaded:
```bash
# Open the Rules page, or query the API
curl -s http://localhost:9090/api/v1/rules | head
```

Visit `http://localhost:9090/alerts` to see alert states (Inactive / Pending /
Firing).

---

## 3. Import the Grafana Dashboard

**Option A — manual import (quickest):**
1. Grafana → Dashboards → New → **Import**.
2. Upload `quality/observability/grafana/omiicare-qe-dashboard.json`.
3. Select the **Prometheus** datasource (uid `prometheus`) when prompted.

**Option B — auto-provision (persistent):** copy the JSON into the M2
provisioning path so it loads on startup:
```bash
cp quality/observability/grafana/omiicare-qe-dashboard.json \
   infrastructure/monitoring/grafana/provisioning/dashboards/
docker compose -f infrastructure/docker/docker-compose.yml restart grafana
```
(The M2 `dashboards.yml` provider already watches that directory.)

The dashboard's `$job` variable defaults to `omiicare-backend` and resolves
automatically from `label_values(up, job)`.

---

## 4. Generate Signal (Owned Infra Only)

Drive traffic so the panels and alerts have data:
```bash
BASE_URL=http://localhost:8080 k6 run quality/performance/k6/login-load.js
```
Watch the QE dashboard populate (request rate, p95/p99, heap). To exercise the
error-rate alert deliberately, run the stress profile which pushes the SUT past
its comfort zone:
```bash
k6 run quality/performance/k6/appointment-stress.js
```

---

## 5. Verify the Alerts Fire

| Alert | How to trigger (safely, on owned infra) |
|-------|------------------------------------------|
| `BackendInstanceDown` | stop the backend container/process for > 1 min |
| `HighHttpErrorRate` | run the stress profile until 5xx ratio > 5% |
| `HighP99Latency` | run heavy load until p99 > 1s |
| `JvmHeapPressure` | run a long soak / large-volume test |

Check `http://localhost:9090/alerts` transitions Pending → Firing.

---

## 6. Next Step: Alertmanager (Optional)

To route firing alerts to Slack/email, add an `alertmanager` service to the
compose stack and an `alerting:` block in `prometheus.yml` pointing at it. The
rules in `alert-rules.yml` already carry `severity` labels and descriptive
annotations suitable for routing and templating.

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Observability Engineer | Initial (Milestone 7) |
