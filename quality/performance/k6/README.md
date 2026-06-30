# k6 Load Tests — omiiCARE QA

> **PERFORMANCE SAFETY RULE:** These scripts target **ONLY local / Docker /
> owned infrastructure** (default `http://localhost:8080`). **NEVER** point
> `BASE_URL` at a public website or a system you do not own.

[k6](https://k6.io) is a developer-centric load testing tool. Tests are written
in JavaScript (ES modules) and run by the Go-based `k6` engine.

---

## 1. Install

```bash
# macOS
brew install k6

# Debian/Ubuntu
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg \
  --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" \
  | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update && sudo apt-get install k6

# Docker (mount the script in)
docker run --rm -i --network host \
  -v "$PWD/quality/performance/k6:/scripts" grafana/k6 run /scripts/login-load.js
```

---

## 2. Scripts

| Script | Type | Journey |
|--------|------|---------|
| `login-load.js` | Load (ramping VUs) | login → list patients with token |
| `appointment-stress.js` | Stress + Spike | login → book appointments under surge |

---

## 3. Run

```bash
# Default targets localhost:8080 with demo creds
k6 run quality/performance/k6/login-load.js

# Override target + credentials via environment variables
BASE_URL=http://localhost:8080 \
USERNAME=demo.admin \
PASSWORD='Admin@12345' \
  k6 run quality/performance/k6/login-load.js

# Stress + spike profile
k6 run quality/performance/k6/appointment-stress.js
```

### Environment variables

| Variable | Default | Meaning |
|----------|---------|---------|
| `BASE_URL` | `http://localhost:8080` | SUT base URL (owned infra only) |
| `USERNAME` | `demo.admin` | login username |
| `PASSWORD` | `Admin@12345` | login password |

k6 reads these via `__ENV.*` inside the scripts. Pass them as inline env vars
(as above) or with `-e KEY=value`.

---

## 4. Interpreting Output

A k6 run ends with a summary like:

```
     ✓ login status is 200
     ✓ patients status is 200

     http_req_duration..............: avg=120ms  p(95)=410ms  p(99)=720ms
     http_req_failed................: 0.30%   ✓ 12   ✗ 3980
     login_duration.................: avg=140ms  p(95)=520ms
     patients_list_duration.........: avg=95ms   p(95)=380ms
     iterations.....................: 3992    66.5/s
     vus............................: 25      max=25
```

Key lines:
- **`✓` / `✗` checks** — functional correctness per request.
- **`http_req_duration`** — overall latency; watch **p(95)** and **p(99)**.
- **`http_req_failed`** — error rate; the threshold fails the run if `> 1%`.
- **Custom Trends** (`login_duration`, `patients_list_duration`,
  `appointment_booking_duration`) — per-journey latency.
- **`iterations` rate** — effective throughput (≈ TPS).
- **`vus`** — concurrency reached.

### Thresholds (pass/fail gates)

If any threshold defined in `options.thresholds` is breached, k6 exits with a
**non-zero status** — ideal for CI. Examples encoded in the scripts:
- `http_req_duration: p(95)<500, p(99)<1000`
- `http_req_failed: rate<0.01`

### Export results

```bash
# JSON stream (one line per metric point)
k6 run --out json=results.json quality/performance/k6/login-load.js

# CSV
k6 run --out csv=results.csv quality/performance/k6/login-load.js

# Stream to Prometheus remote-write (feeds the M2 Grafana stack)
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
  k6 run --out experimental-prometheus-rw quality/performance/k6/login-load.js
```

---

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Performance Engineer | Initial (Milestone 7) |
