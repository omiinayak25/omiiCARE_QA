# Upgrade & Installation Guide — omiiCARE_QA v1.0.0

> **Purpose.** Install, configure, and run omiiCARE_QA `1.0.0`, and understand
> how to upgrade safely on the post-1.0 Semantic Versioning line. Because
> `1.0.0` is the **first stable release**, the bulk of this guide is a clean
> installation; the upgrade section establishes the forward-compatibility
> contract for later versions.

## Scope

- **In scope:** prerequisites, Spring profiles, database selection, environment
  variables, first-run for backend / frontend / full stack, verification, and
  forward-compatibility notes.
- **Out of scope:** CI/CD internals (see [CI_CD_GUIDE.md](CI_CD_GUIDE.md)),
  deployment promotion (see [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)), and the
  migration model (see [MIGRATION_NOTES.md](MIGRATION_NOTES.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Release Engineer | Keep prerequisites and first-run steps accurate per release |
| QA Architect | Confirm verification steps reflect the Definition of Done |
| Contributors | Follow the documented profiles and env vars; do not introduce ad-hoc config |

---

## 1. Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| **Java** | **21 (LTS)** | Temurin recommended; backend + reactor build/test |
| **Node.js** | **22** | Frontend build/dev (matches CI default); npm ships with Node |
| **Maven** | **3.9+** | Reactor build; matches the backend Docker build base (`maven:3.9-eclipse-temurin-21`) |
| **Docker + Compose** | Current | Required only for the full containerized stack and PostgreSQL profiles |
| **Git** | Current | Clone and tag operations |

> The full version compatibility matrix (databases, browsers, OS, services) is in
> [COMPATIBILITY_MATRIX.md](COMPATIBILITY_MATRIX.md).

## 2. Get the Source

```bash
git clone https://github.com/omiinayak25/omiiCARE_QA.git
cd omiiCARE_QA
git checkout v1.0.0   # the stable release tag
```

## 3. Spring Profiles

Database and environment selection is **configuration-only** — no code change is
required to switch. Profiles (from [PROJECT_METADATA.md](PROJECT_METADATA.md) §4):

| Profile | Purpose | Database |
|---------|---------|----------|
| `dev` | Local coding, fast startup | H2 (embedded) |
| `test` | Automated test execution | H2 / Testcontainers |
| `local` | Enterprise local with real services | PostgreSQL (Docker) |
| `docker` | Full containerized stack | PostgreSQL (Docker) |
| `qa` / `stage` / `prod` | QA / pre-prod / production | External PostgreSQL |

Select a profile with `-Dspring-boot.run.profiles=<profile>` (run) or
`-Dspring.profiles.active=<profile>` (packaged jar / container env).

## 4. Database Switch (H2 ↔ PostgreSQL)

- **H2 (default):** active on `dev` and `test`; nothing to install — embedded.
- **PostgreSQL:** active on `local`/`docker`/`qa`/`stage`/`prod`. For local use,
  start it via the Compose stack (§7); for `qa`/`stage`/`prod`, point the env
  vars at an external PostgreSQL instance.
- **Flyway** applies the same migration set against either engine, so the schema
  is identical across profiles. See [MIGRATION_NOTES.md](MIGRATION_NOTES.md).

## 5. Environment Variables

Copy the templates and fill in non-default values. Never commit secrets; all
credentials are read from the environment, not the source tree.

```bash
cp .env.example .env
cp infrastructure/docker/.env.example infrastructure/docker/.env   # for the stack
cp apps/frontend/.env.example apps/frontend/.env                    # if present
```

| Variable (representative) | Purpose |
|---------------------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (e.g. `dev`, `docker`, `prod`) |
| `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD` | External PostgreSQL connection (non-H2 profiles) |
| JWT secret / token TTL settings | Signing key and access/refresh token lifetimes |
| `VITE_API_BASE_URL` | Frontend → backend API base (defaults to the Vite dev proxy) |
| AI flags (`ai.enabled`, `ai.provider`, `ai.apiKey`) | AI layer is **disabled by default**; a hosted provider needs a runtime key |

> See [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md) for the full variable catalog
> and feature-flag behavior per environment.

## 6. First Run — Backend & Frontend (no Docker)

```bash
# Backend on dev (embedded H2)
mvn -pl apps/backend -am spring-boot:run -Dspring-boot.run.profiles=dev
#   API     → http://localhost:8080/api/v1/
#   Swagger → http://localhost:8080/swagger-ui.html
#   Health  → http://localhost:8080/actuator/health

# Frontend (separate terminal)
cd apps/frontend && npm ci && npm run dev
#   App → http://localhost:5173  (Vite dev proxy → backend :8080)
```

**Demo login:** `demo.admin` / `Admin@12345` (synthetic SUPER_ADMIN, non-prod
bootstrap only).

## 7. First Run — Full Stack (Docker)

```bash
docker compose -f infrastructure/docker/docker-compose.yml up -d
```

This starts PostgreSQL plus the supporting services (Redis, Mailpit, MinIO,
Keycloak, WireMock, Prometheus, Grafana, SonarQube). Run the backend on the
`docker`/`local` profile so it targets the containerized PostgreSQL. See
[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) for promotion and image strategy.

## 8. Verify the Installation

| Step | Command / Check | Expected |
|------|-----------------|----------|
| Health | `curl http://localhost:8080/actuator/health` | `{"status":"UP"}` |
| Auth | `POST /api/v1/auth/login` with the demo credentials | 200 + JWT access/refresh tokens |
| Swagger | open `/swagger-ui.html` | OpenAPI UI with Bearer-JWT scheme |
| Backend tests | `mvn -pl apps/backend test` | 9 tests green |
| Frontend | `npm run build` / `npm run lint` | both pass cleanly |
| AI module | `mvn -pl ai test` | 9 unit tests green |

## 9. Upgrading (Post-1.0)

`1.0.0` is the baseline — there is **no upgrade from an earlier stable version**.
For future upgrades the contract is:

1. **Read the target release notes and [CHANGELOG.md](../CHANGELOG.md)** for the
   version you are moving to, including any `BREAKING CHANGE:` entries.
2. **Check [MIGRATION_NOTES.md](MIGRATION_NOTES.md)** for new Flyway migrations or
   required configuration changes.
3. **Bump dependencies per [COMPATIBILITY_MATRIX.md](COMPATIBILITY_MATRIX.md)** —
   only the listed Java/Node/Maven/DB/browser versions are supported.
4. **Take a database snapshot**, deploy, let Flyway apply forward-only migrations,
   then re-run the §8 verification steps.
5. **Roll back** by redeploying the previous immutable image tag and restoring the
   pre-upgrade snapshot if a migration must be undone (see
   [CI_CD_GUIDE.md](CI_CD_GUIDE.md) §8).

## 10. Forward-Compatibility Notes

- **SemVer 2.0.0 from `1.0.0`:** `MAJOR` = breaking, `MINOR` = backward-compatible
  features, `PATCH` = backward-compatible fixes (see [VERSIONING.md](../VERSIONING.md)).
- **API path version is independent of product version:** endpoints stay under
  `/api/v1/` until a deliberate API major; a product `MINOR` bump does not move the
  path (see [API_VERSIONING_POLICY.md](API_VERSIONING_POLICY.md)).
- **Configuration-first design:** profiles, database choice, and AI provider are
  config-driven, so most upgrades require no code changes on the adopter side.
- **Forward-only migrations:** schema evolves additively with reversible patterns;
  destructive changes are announced as breaking.

## Examples

- *Quick evaluation:* clone, `mvn ... spring-boot:run -Dspring-boot.run.profiles=dev`,
  `npm run dev`, log in as `demo.admin` — no Docker, no external database.
- *Realistic local run:* start the Compose stack, run the backend on `docker`,
  and the app uses PostgreSQL exactly as `qa`/`prod` would.

## Future Enhancements

- A one-command bootstrap script wrapping clone → profile → run.
- Automated upgrade smoke checks invoked from the release pipeline.

## Dependencies

- Versions from [PROJECT_METADATA.md](PROJECT_METADATA.md) §3 and
  [COMPATIBILITY_MATRIX.md](COMPATIBILITY_MATRIX.md).
- Environment detail from [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md).
- Migration model from [MIGRATION_NOTES.md](MIGRATION_NOTES.md).

## References

- [../RELEASE_NOTES.md](../RELEASE_NOTES.md) · [../VERSIONING.md](../VERSIONING.md)
- [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) · [CI_CD_GUIDE.md](CI_CD_GUIDE.md)
- [COMPATIBILITY_MATRIX.md](COMPATIBILITY_MATRIX.md) · [KNOWN_ISSUES.md](KNOWN_ISSUES.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Release Engineer | Initial (Milestone 10) |
