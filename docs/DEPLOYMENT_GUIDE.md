# Deployment Guide

> **Purpose.** Describe how omiiCARE_QA is built, packaged, promoted, and
> deployed. The primary mechanism is **Docker Compose**; the repository is
> **Kubernetes-ready** in structure, with K8s itself documented as future scope.
> This guide is documentation only (Milestone 1). The container/infrastructure
> foundation arrives in **Milestone 2**; the actual CI/CD pipelines that automate
> this flow arrive in **Milestone 8**.

## Purpose

- Define a consistent build-and-run flow from a developer laptop to production.
- Specify environment promotion, deployment strategies, and rollback.
- Document the image and secrets strategy so deployments are reproducible and
  safe.

## Scope

- **In scope:** build/run flow, environment promotion (Dev→QA→Stage→Prod) with
  approvals, blue/green and rolling strategies, rollback, Docker image strategy,
  versioning/tags, secrets handling, health checks, and smoke verification.
- **Out of scope (v1.0):** Kubernetes manifests, production cloud provisioning,
  and a microservices split — all post-1.0 per
  [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §3. The
  repo leaves clean seams for them.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| DevOps Engineer (M2/M8) | Compose stacks, image builds, pipelines, promotion |
| QA Architect | Smoke/verification gates and promotion criteria |
| Maintainer | Approve Stage→Prod promotions; own release tags |
| Security Engineer | Secrets policy; image scanning gate (M8) |

---

## 1. Build & Run Flow

1. **Build** each module image (backend Maven build → JAR → image; frontend Vite
   build → static assets → image).
2. **Compose** the target environment with `docker compose` using the
   environment's `.env` (never committed; see [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md)).
3. **Start** supporting services (PostgreSQL, Redis, MailHog, MinIO, Keycloak,
   WireMock, Prometheus, Grafana, SonarQube).
4. **Migrate** the database with Flyway on startup; load PHI-safe seeds where
   appropriate.
5. **Health-check** every service; **smoke-verify** the running stack (§7).

> Helper scripts (`setup`/`start`/`stop`/`reset`/`health-check`) are delivered in
> Milestone 2 and are the supported entry points for local and Docker runs.

## 2. Environment Promotion (Dev → QA → Stage → Prod)

```
Dev ──▶ QA ──▶ Stage ──▶ Prod
 │       │       │         │
 build   QA      pre-prod  release
 + unit  gate    gate      approval
```

| Promotion | Entry criteria | Approver |
|-----------|----------------|----------|
| Dev → QA | Build + unit tests green; smoke passes | Automated (M8) |
| QA → Stage | QA suite + security/a11y/perf smoke pass | QA Architect |
| Stage → Prod | Full regression green; release notes ready | Maintainer (manual approval) |

The **same image** is promoted across environments; only configuration changes.
This guarantees what was tested is what ships.

## 3. Deployment Strategies

| Strategy | How it works | When to use |
|----------|--------------|-------------|
| **Rolling** | Replace instances incrementally, draining old ones | Default for routine, backward-compatible releases |
| **Blue/Green** | Stand up a parallel ("green") stack, cut traffic over, keep "blue" as instant fallback | High-risk releases; near-zero-downtime cutover |

- Both are **documented** in v1.0; full automation lands with the M8 pipelines.
- Database changes are **backward-compatible** (expand/contract) so old and new
  app versions can run during a rollout.

## 4. Rollback Strategy

- **Image rollback:** redeploy the previous immutable tag (the prior known-good
  image).
- **Blue/Green fallback:** route traffic back to the retained "blue" stack
  instantly.
- **Database:** forward-fix preferred; Flyway provides versioned migrations and
  documented rollback procedures (M2). Destructive changes are gated and staged.
- A rollback is verified by the same smoke checks as a deployment (§7).

## 5. Docker Image Strategy

| Image | Built from | Contents |
|-------|-----------|----------|
| Backend | `apps/backend` | Spring Boot JAR on a slim JRE base |
| Frontend | `apps/frontend` | Vite static build served by a lightweight web server |
| Automation | `automation` | Test runtime (Playwright/Selenium/Rest Assured) |
| Monitoring | `infrastructure` | Prometheus/Grafana configuration images |
| Database | `database` | Postgres + Flyway migrations/seeds |

- Images are **immutable** and **minimal**; multi-stage builds keep them small.
- Images are scanned for vulnerabilities in the M8 pipeline before promotion.

## 6. Versioning & Tags

- Images are tagged with the **semantic version** (per [VERSIONING.md](../VERSIONING.md))
  and the **git short SHA** (e.g., `1.0.0`, `1.0.0-rc.1`, `sha-ab12cd3`).
- `latest` is never deployed to QA/Stage/Prod; only pinned tags are promoted.
- The tag deployed to each environment is recorded for traceability.

## 7. Secrets, Health Checks & Smoke Verification

- **Secrets:** supplied via environment variables and, in CI, **GitHub Secrets**.
  Only `.env.example` is committed; real `.env` files and credentials are never
  committed. See [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md) and
  [SECURITY.md](../SECURITY.md).
- **Health checks:** every Compose service defines a healthcheck; dependents wait
  for healthy upstreams before starting.
- **Smoke verification:** after deploy, run a minimal smoke suite (app up, auth
  endpoint reachable, DB migrated, key page renders) before declaring success.
- **Production-verification checklist:** a release is confirmed against the
  production-verification checklist (delivered with the M8 release engineering
  work) before Stage→Prod is approved.

## Examples

- **Promote a release:** the `1.0.0` backend image that passed QA is the exact
  image deployed to Stage and Prod; only its `.env` and DB endpoint differ.
- **Blue/green cutover:** a risky schema-touching release is deployed to a green
  stack; smoke passes; traffic cuts over; blue is retained for one cycle as
  instant rollback.
- **Rollback:** a Prod smoke check fails; traffic is routed back to the blue stack
  and the previous tag remains live with zero data loss.

## Future Enhancements

- Kubernetes manifests/Helm charts and cloud deployment (post-1.0, v2.0).
- Progressive delivery (canary) and automated rollback on SLO breach.
- GitOps-driven environment promotion.

## Dependencies

- Infrastructure foundation from [ROADMAP.md](../ROADMAP.md) Milestone 2;
  pipelines from Milestone 8.
- Environments and secrets from [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md);
  versions from [VERSIONING.md](../VERSIONING.md) and
  [PROJECT_METADATA.md](PROJECT_METADATA.md).

## References

- Docker & Docker Compose documentation; Twelve-Factor App (config/build/run).
- [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md),
  [PERFORMANCE_GUIDE.md](PERFORMANCE_GUIDE.md),
  [SECURITY_TESTING_GUIDE.md](SECURITY_TESTING_GUIDE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | DevOps/QA Specialist | Initial deployment guide (Milestone 1) |
