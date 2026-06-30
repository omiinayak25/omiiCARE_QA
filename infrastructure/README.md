# Infrastructure — Docker & Monitoring

> **Status:** Planned — delivered in **Milestone 2**.
> This file is a *module charter*. It documents intent and boundaries so the
> directory has a clear contract before any code lands. It is intentionally
> code-free during Milestone 1 (Foundation, Architecture & Governance).

## Purpose

Docker Compose stack (PostgreSQL, Redis, MailHog, MinIO, Keycloak, WireMock, Grafana, Prometheus, SonarQube) with health checks, named volumes, networks, plus monitoring/observability config.

## Planned Contents

- `docker/` — Compose files, service configs, healthchecks
- `monitoring/grafana/`, `monitoring/prometheus/`

## Boundaries

- This module is **not** implemented during Milestone 1. No application, API,
  or automation code exists here yet.
- Build order and the exact scope are governed by [ROADMAP.md](../ROADMAP.md)
  and the master spec at
  [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md).

## References

- [ARCHITECTURE.md](../ARCHITECTURE.md)
- [PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md)
- [docs/](../docs/)
