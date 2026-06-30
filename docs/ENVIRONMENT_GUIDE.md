# Environment Guide

> **Purpose.** Define the complete environment matrix for omiiCARE_QA, the Spring
> profile files behind each environment, how configuration is inherited and
> overridden, how secrets are handled, and the feature-flag strategy that keeps
> experimental features out of stage/prod. This guide is documentation only
> (Milestone 1); the environments and profiles it describes are implemented in
> **Milestone 2**.

## Purpose

- Provide one authoritative description of every environment and its
  configuration.
- Make configuration **inheritable, validated, and isolated** so environments
  never bleed into one another.
- Govern feature flags so unfinished work cannot reach pre-production or
  production.

## Scope

- **In scope:** the environment matrix, Spring profile files, configuration
  inheritance, `.env`/`.env.example` and secret management, feature flags,
  configuration validation, environment isolation, and the certificate/credential
  strategy summary.
- **Out of scope (v1.0):** production cloud provisioning and Kubernetes config
  (post-1.0). Environment **names and profiles** are fixed by
  [PROJECT_METADATA.md](PROJECT_METADATA.md) §4 and restated here.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| DevOps Engineer (M2) | Implement profiles, Compose, scripts, validation |
| QA Architect | Own the matrix and feature-flag gating policy |
| Security Engineer | Secrets and certificate/credential strategy |
| All contributors | Never commit secrets; never invent ad-hoc env names |

---

## 1. Environment Matrix

| Environment | Profile | Purpose | Database | Data set | Primary users |
|-------------|---------|---------|----------|----------|---------------|
| Development | `dev` | Fast local coding, quick startup | H2 (embedded) | Minimal seed | Developers |
| Local | `local` | Enterprise local with real services | PostgreSQL (Docker) | Full PHI-safe seed | Developers |
| Docker | `docker` | Full containerized stack | PostgreSQL (Docker) | Full PHI-safe seed | Dev / QA |
| Test | `test` | Automated test execution | H2 / Testcontainers | Deterministic fixtures | CI / SDETs |
| QA | `qa` | QA validation | External PostgreSQL | Curated QA data | QA team |
| Stage | `stage` | Pre-production rehearsal | External PostgreSQL | Prod-like data | Release/QA |
| Production | `prod` | Production | External PostgreSQL | Production data | End users |
| Demo | `demo` | Curated demonstrations | Configurable | Curated demo set | Sales/portfolio |
| Training | `training` | Onboarding/teaching | Configurable | Training set | Trainees |
| Performance | `perf` | Load/stress (owned infra only) | Configurable | Volume data set | Performance QA |
| Disaster Recovery | `dr` | Failover/restore drills | Configurable | Restored snapshot | DevOps |

> All data is **synthetic and PHI-safe**; no real patient data exists in any
> environment ([MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §9).

## 2. Spring Profile Files

A common base plus one override file per environment:

| File | Role |
|------|------|
| `application.yml` | Shared defaults inherited by all environments |
| `application-dev.yml` | Development overrides (H2, fast startup) |
| `application-local.yml` | Local overrides (Dockerized PostgreSQL + services) |
| `application-docker.yml` | Full containerized stack overrides |
| `application-test.yml` | Test overrides (H2 / Testcontainers) |
| `application-qa.yml` | QA overrides (external PostgreSQL) |
| `application-stage.yml` | Stage overrides (pre-production) |
| `application-prod.yml` | Production overrides |

> `demo`, `training`, `perf`, and `dr` activate via their named profiles layered
> on the closest base (typically the `docker`/external-PostgreSQL configuration)
> with environment-specific overrides.

## 3. Configuration Inheritance (Common + Per-Env Override)

```
application.yml  (common defaults)
        │  inherited by every environment
        ▼
application-{env}.yml  (only the values that differ)
        │
        ▼
environment variables / secrets  (highest precedence; never committed)
```

- The **common** file holds everything shared (logging format, OpenTelemetry,
  API versioning, validation defaults).
- Each **per-env** file overrides **only** what differs (datasource, URLs, flags).
- **No duplicated configuration** — a value lives in exactly one place; this is a
  Milestone 2 gate.
- Environment variables override file values, so secrets and host-specific
  settings stay out of source.

## 4. `.env`, `.env.example` & Secret Management

| Item | Rule |
|------|------|
| `.env.example` | Committed; documents every required variable with placeholder values |
| `.env` | **Never committed**; provides real values locally (git-ignored) |
| CI secrets | Supplied via **GitHub Secrets**; injected as env vars at run time |
| In code/logs | Secrets never appear in source, logs, URLs, or error responses |

Only `.env.example` is the contract; real credentials are always external to the
repository. See [SECURITY.md](../SECURITY.md).

## 5. Feature Flags

- **Definition:** a feature flag is a named, config-driven toggle that enables or
  disables a feature without a code change.
- **Per-environment gating:** experimental flags are **enabled in `dev`/`local`/
  `qa`** and **disabled by default in `stage`/`prod`**, so unfinished work cannot
  reach pre-production or production.
- **Lifecycle:** `proposed → enabled (lower envs) → validated → promoted (stage)
  → enabled (prod) → retired (flag removed once permanent)`. Stale flags are
  cleaned up, not left to rot.
- **Naming:** descriptive, namespaced, lowercase keys (e.g.,
  `feature.scheduling.online-booking`); no ambiguous abbreviations.

| Flag state | dev/local/qa | stage | prod |
|------------|-------------|-------|------|
| Experimental | Enabled | **Disabled** | **Disabled** |
| Beta | Enabled | Enabled (gated) | Disabled |
| Released | Enabled | Enabled | Enabled |

## 6. Configuration Validation

- On startup the application **validates required configuration** (datasource,
  required URLs, secret presence) and **fails fast** with a clear message if a
  value is missing or malformed.
- Profile-specific invariants are checked (e.g., `prod` must not use H2; `perf`
  load targets must be owned infrastructure).
- A documentation/config-lint step (future, per
  [PROJECT_METADATA.md](PROJECT_METADATA.md) Future Enhancements) catches drift.

## 7. Environment Isolation

- Each environment has its **own database, credentials, and service endpoints**;
  no shared mutable state across environments.
- Docker Compose uses **dedicated networks and volumes** per stack so services do
  not cross-talk between environments.
- Lower environments never connect to higher-environment data stores.

## 8. Certificate & Credential Strategy (Summary)

- TLS certificates and credentials are **provisioned per environment** and stored
  outside the repository (env vars / GitHub Secrets / local secret stores).
- Keycloak provides identity in `local`/`docker`; external IdP configuration is
  injected per environment.
- Credentials are **rotated** and never reused across `qa`/`stage`/`prod`.
- Detailed handling lives in [SECURITY.md](../SECURITY.md); this is the summary
  contract every environment honors.

## Examples

- **Switch dev → docker:** change the active Spring profile; H2 gives way to
  Dockerized PostgreSQL with **no code change**
  ([ARCHITECTURE.md](../ARCHITECTURE.md) §8).
- **Gate an experiment:** `feature.billing.new-claims-engine` is enabled in `qa`
  for validation but disabled in `stage`/`prod`, so it cannot ship before it is
  ready.
- **Missing secret:** starting `qa` without `DB_PASSWORD` fails fast at boot with
  an explicit validation error rather than a confusing runtime failure.

## Future Enhancements

- Generate a machine-readable environment registry from this matrix in CI.
- Centralized secret manager integration (e.g., Vault) post-1.0.
- Automated feature-flag audit that flags stale toggles in pull requests.

## Dependencies

- Implemented in [ROADMAP.md](../ROADMAP.md) Milestone 2; consumed by every later
  milestone.
- Environment names/profiles fixed by [PROJECT_METADATA.md](PROJECT_METADATA.md)
  §4; deployment uses these in [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md).
- Secrets policy from [SECURITY.md](../SECURITY.md).

## References

- Spring Boot externalized configuration & profiles documentation.
- Twelve-Factor App (config); Docker Compose networking/volumes.
- [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md),
  [PERFORMANCE_GUIDE.md](PERFORMANCE_GUIDE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | DevOps/QA Specialist | Initial environment & feature-flag guide (Milestone 1) |
