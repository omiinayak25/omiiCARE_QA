# omiiCARE Infrastructure — Local Development & QA Stack

> Milestone 2: infrastructure foundation. A Docker Compose stack providing every
> supporting service the omiiCARE backend and QA platform depend on.

---

## 1. Purpose

Provide a single, reproducible, one-command local environment that mirrors the
runtime dependencies of the omiiCARE Enterprise Healthcare QA platform. A
developer or QA engineer can bring up Postgres, Redis, mail, object storage,
identity, external-system stubs, and the full observability/quality stack with
`docker compose up -d`.

**Docker (Engine + Compose v2) is REQUIRED to run this stack.** No services run
without it.

## 2. Scope

- **In scope:** Containerized supporting services (datastores, identity,
  mocking, monitoring, code quality), their health checks, networking, volumes,
  and provisioning (Prometheus scrape config, Grafana datasource + dashboard,
  WireMock stubs, Keycloak realm).
- **Out of scope:** The backend application image itself (delivered in
  Milestone 3+; a commented-out `backend` service block shows how it joins the
  stack), production orchestration (K8s), and managed cloud equivalents.

## 3. Responsibilities

| Concern | Owned here |
|---|---|
| Service definitions | `docker-compose.yml` |
| Configurable defaults | `.env.example` → copy to `.env` |
| Metrics scraping | `../monitoring/prometheus/prometheus.yml` |
| Dashboards & datasource | `../monitoring/grafana/provisioning/*`, `../monitoring/grafana/dashboards/*` |
| External system stubs | `wiremock/mappings/*` |
| Identity realm | `keycloak/omiicare-realm.json` |

### Services at a glance

| Service | Image | Host Port(s) | Default Credentials | Health Check | Named Volume | Developer URL |
|---|---|---|---|---|---|---|
| postgres | `postgres:16-alpine` | 5432 | `omiicare` / `omiicare` (db `omiicare`) | `pg_isready` | `postgres-data` | `postgres://localhost:5432/omiicare` |
| redis | `redis:7-alpine` | 6379 | none | `redis-cli ping` | `redis-data` | `redis://localhost:6379` |
| mailhog (Mailpit) | `axllent/mailpit:latest` | 1025 (SMTP), 8025 (UI) | none | `/mailpit readyz` | `mailpit-data` | http://localhost:8025 |
| minio | `minio/minio:latest` | 9000 (API), 9001 (console) | `minioadmin` / `minioadmin` | `mc ready local` | `minio-data` | http://localhost:9001 |
| keycloak | `quay.io/keycloak/keycloak:25.0` | 8081 (HTTP), 9010 (mgmt) | `admin` / `admin` | TCP `/health/ready` on 9000 | (realm import, read-only mount) | http://localhost:8081 |
| wiremock | `wiremock/wiremock:3.9.1` | 8089 | none | `/__admin/health` | (mappings, read-only mount) | http://localhost:8089/__admin |
| prometheus | `prom/prometheus:latest` | 9090 | none | `/-/healthy` | `prometheus-data` | http://localhost:9090 |
| grafana | `grafana/grafana:latest` | 3000 | `admin` / `admin` | `/api/health` | `grafana-data` | http://localhost:3000 |
| sonarqube | `sonarqube:10-community` | 9002 | `admin` / `admin` (first login) | `/api/system/status` UP | `sonarqube-data`, `-extensions`, `-logs` | http://localhost:9002 |

All containers use the `omiicare-` name prefix, share the `omiicare-net` bridge
network, run with `restart: unless-stopped`, and have memory limits suited to a
developer laptop.

> Note: All credentials above are **local development defaults**. They live in
> `.env.example`; copy to `.env` and override for any shared environment. Never
> commit real secrets.

## 4. Examples

```bash
# One-time setup
cp .env.example .env

# Bring the stack up (detached)
docker compose up -d

# Watch health status converge
docker compose ps

# Tail logs for a single service
docker compose logs -f keycloak

# Reload Prometheus config without restart
curl -X POST http://localhost:9090/-/reload

# Stop everything (keep data)
docker compose down

# Stop and wipe all volumes (full reset)
docker compose down -v
```

Service-to-service resolution uses compose service names, e.g. the backend will
reach Postgres at `postgres:5432`, Redis at `redis:6379`, mail at `mailhog:1025`,
Keycloak at `keycloak:8080`, and MinIO at `minio:9000`.

## 5. Future Enhancements

- Enable the `backend` service (Milestone 3+) and switch the Prometheus target
  from `host.docker.internal:8080` to `backend:8080`.
- Back SonarQube with a dedicated Postgres instead of the embedded H2.
- Add a `minio-init` job to pre-create buckets on first boot.
- Add Loki + Promtail for log aggregation alongside metrics.
- Add Alertmanager and Prometheus alert rules.
- Pin floating `:latest` tags to specific digests for reproducible CI.

## 6. Dependencies

- **Docker Engine** and **Docker Compose v2** (`docker compose`).
- Host requirement for SonarQube/Elasticsearch:
  `sudo sysctl -w vm.max_map_count=262144`.
- Free host ports: 5432, 6379, 1025, 8025, 9000, 9001, 8081, 9010, 8089, 9090,
  3000, 9002 (all overridable via `.env`).

## 7. References

- `docker-compose.yml` — service definitions
- `.env.example` — configurable defaults
- `../monitoring/prometheus/prometheus.yml` — scrape config
- `../monitoring/grafana/provisioning/` — Grafana datasource + dashboard providers
- `../monitoring/grafana/dashboards/omiicare-backend-overview.json` — dashboard
- `wiremock/mappings/health-stub.json` — example external stub
- `keycloak/omiicare-realm.json` — imported identity realm
- Spring Boot Actuator (`/actuator/health`, `/actuator/prometheus`)

## 8. Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | DevOps Engineer | Initial (Milestone 2) |
