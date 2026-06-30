# Developer Experience Scripts

Cross-platform setup / start / stop / reset / health-check scripts so a new
developer can go **clone → setup → start → develop** with minimal manual steps.
Delivered in **Milestone 2 (Infrastructure Foundation)**.

## Purpose

Provide a single, consistent entry point for the local developer workflow across
macOS, Linux and Windows. Each task has a POSIX `.sh` (bash) and a Windows
`.bat` version with identical behavior, so the onboarding experience is the same
regardless of operating system.

## Scope

- Toolchain validation (Java 21+, Maven, Node, Docker).
- Bootstrapping the infrastructure environment file (`infrastructure/docker/.env`).
- Building the backend and installing frontend dependencies (when present).
- Lifecycle of the local Docker Compose infra stack (up / down / wipe).
- Health probing of the backend and all infra services.

Out of scope: CI pipelines, production deployment, and cloud provisioning. The
scripts target a developer's local machine only.

## Responsibilities

| Audience | Responsibility |
|----------|----------------|
| New developers | Run `setup` once, then `start` / `stop` daily. |
| Maintainers | Keep the service catalog and compose path in sync with infra. |
| CI (optional) | May call `health-check` for smoke verification. |

All `.sh` scripts source shared helpers from `lib/common.sh` and use a single
`COMPOSE_FILE` variable pointing at `infrastructure/docker/docker-compose.yml`,
so the compose path is defined in exactly one place.

## 1. Scripts Overview

| Script | Purpose | Destructive | Needs Docker |
|--------|---------|-------------|--------------|
| `setup` | Validate tooling, create `.env`, build backend, install frontend deps | No | No (warns) |
| `start` | Bring up infra stack, wait for Postgres, print URLs | No | Yes |
| `stop` | Stop infra stack, **keep** volumes | No | Yes |
| `reset` | Stop infra **and remove volumes** + clean Maven `target/` | **Yes** | Optional |
| `health-check` | Probe backend + infra, print PASS/FAIL table | No | Recommended |
| `lib/common.sh` | Shared bash helpers (sourced, not run) | — | — |

## 2. Prerequisites

| Tool | Version | Needed for | Notes |
|------|---------|-----------|-------|
| Java (JDK) | 21+ | Backend build/run | Scripts auto-detect Java via `JAVA_HOME` or `PATH`. Maven needs a valid `JAVA_HOME`. |
| Maven | 3.9+ | Backend build | `mvn -DskipTests install`. |
| Node.js | 20+ | Frontend (later) | Only used once `apps/frontend/package.json` exists. |
| Docker + Compose | Engine 24+ | Infra stack | Required for `start` / `stop` / `reset` / full `health-check`. |
| curl | any | `health-check` | HTTP probes are skipped if absent. |

> **JAVA_HOME requirement:** Maven resolves the JDK from `JAVA_HOME`. The
> scripts honor an existing valid `JAVA_HOME`, otherwise derive it from the
> `java` on your `PATH`. If neither resolves to a Java 21+ JDK, the backend
> build is skipped with a warning rather than crashing setup.

## 3. Usage

POSIX (macOS / Linux):

```bash
./scripts/setup.sh            # one-time setup
./scripts/start.sh            # start infra
mvn -pl apps/backend spring-boot:run -Dspring-boot.run.profiles=docker
./scripts/health-check.sh     # verify
./scripts/stop.sh             # stop infra (keep data)
./scripts/reset.sh --yes      # wipe everything (no prompt)
```

Windows (cmd / PowerShell):

```bat
scripts\setup.bat
scripts\start.bat
mvn -pl apps/backend spring-boot:run -Dspring-boot.run.profiles=docker
scripts\health-check.bat
scripts\stop.bat
scripts\reset.bat --yes
```

## 4. What Each Script Does (step by step)

**setup** — (1) validate Java 21+ / Maven / Node / Docker and print versions
(warn, don't fail, on missing optional tools); (2) create
`infrastructure/docker/.env` from `.env.example` if absent; (3) run
`mvn -q -DskipTests install` for the backend; (4) run `npm ci` if
`apps/frontend/package.json` exists; (5) print next steps. Exits non-zero only
if a *required* tool (Java/Maven) blocks the backend build.

**start** — (1) verify Docker + Compose and the compose file; (2)
`docker compose -f <compose> up -d`; (3) poll Postgres health (container health
status, falling back to `pg_isready`) for up to 120s; (4) print the backend run
command; (5) print all service URLs and default credentials.

**stop** — `docker compose -f <compose> down` **without** `-v`, preserving all
named volumes so data survives to the next `start`.

**reset** — *destructive.* Warns, then requires a `y/N` confirmation (or
`--yes` / `-y` to skip). Runs `docker compose down -v` (removes volumes) and
cleans Maven output via `mvn clean` (falling back to deleting `target/`
directories). Re-run `setup` afterward.

**health-check** — (1) curl backend `/actuator/health` and look for
`"status":"UP"` (critical); (2) read container states from `docker compose ps`
(Postgres critical, others informational); (3) curl Grafana, Prometheus,
MailHog, MinIO, Keycloak, SonarQube and WireMock (treating 2xx/3xx/401/403 as
"up"); (4) print a PASS/FAIL/WARN table; (5) exit non-zero if any **critical**
service is down.

## 5. Services & Default Credentials

| Service | URL | Default credentials |
|---------|-----|---------------------|
| Backend API | http://localhost:8080 | — |
| Swagger UI | http://localhost:8080/swagger-ui.html | — |
| Backend health | http://localhost:8080/actuator/health | — |
| Grafana | http://localhost:3000 | admin / admin |
| Prometheus | http://localhost:9090 | — |
| MailHog / Mailpit | http://localhost:8025 | — |
| MinIO Console | http://localhost:9001 | minioadmin / minioadmin |
| Keycloak | http://localhost:8081 | admin / admin |
| SonarQube | http://localhost:9000 | admin / admin |
| WireMock | http://localhost:8089/__admin | — |

> Keycloak runs on **8081** to avoid clashing with the backend on 8080. Default
> credentials are for local development only and must never be used elsewhere.

## 6. Cross-Platform Notes

- **Parity:** `.sh` and `.bat` are behaviorally equivalent; the `.sh` set shares
  logic via `lib/common.sh`, while each `.bat` is self-contained (no shared
  include) per Windows batch conventions.
- **Permissions:** `.sh` files are committed executable (`chmod +x`). On Windows
  run the `.bat` files; under WSL/Git Bash the `.sh` files work directly.
- **Strictness:** `.sh` scripts use `set -euo pipefail`; `.bat` scripts use
  `setlocal enabledelayedexpansion` and explicit `errorlevel` checks.
- **Graceful degradation:** Missing Docker, a missing compose file, an absent
  `.env.example`, or an absent `apps/frontend` are handled with actionable
  messages instead of crashing.
- **Compose command:** `.sh` scripts auto-select `docker compose` (plugin) or
  legacy `docker-compose`.
- **Colors:** `.sh` output auto-disables ANSI colors when not a TTY or when
  `NO_COLOR` is set.

## Examples

```bash
# Fresh machine onboarding
git clone <repo> && cd omiiCARE_QA
./scripts/setup.sh
./scripts/start.sh
./scripts/health-check.sh

# End of day — stop but keep data
./scripts/stop.sh

# Something is wedged — nuke and rebuild
./scripts/reset.sh --yes && ./scripts/setup.sh
```

## Future Enhancements

- Seed/demo-data loader script once backend domain endpoints land.
- Frontend dev-server launcher (`dev.sh`) when `apps/frontend` is added.
- Automation/test-runner entry points for the `automation/` module.
- A unified `doctor` command aggregating tool and service diagnostics.

## Dependencies

- `infrastructure/docker/docker-compose.yml` — infra stack (added in Milestone 2).
- `infrastructure/docker/.env.example` — env template (consumed by `setup`).
- Root `pom.xml` and `apps/backend` — backend build target.
- `apps/frontend/package.json` — optional, detected at runtime.

## References

- [ARCHITECTURE.md](../ARCHITECTURE.md)
- [PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md)
- [ROADMAP.md](../ROADMAP.md)
- [infrastructure/README.md](../infrastructure/README.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | DevEx Engineer | Initial (Milestone 2) |
