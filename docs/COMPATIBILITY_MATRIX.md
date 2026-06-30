# Compatibility Matrix — omiiCARE_QA v1.0.0

> **Purpose.** The authoritative, version-pinned compatibility matrix for
> omiiCARE_QA `1.0.0`: toolchain, databases, browsers, Docker services, and
> operating systems. Versions here are sourced from the build files, Dockerfiles,
> and CI workflows in the repository and defer to
> [PROJECT_METADATA.md](PROJECT_METADATA.md) §3 as the canonical technology matrix.

## Scope

- **In scope:** supported and tested versions for building, running, and testing
  the platform.
- **Out of scope:** rationale and architecture (see [../ARCHITECTURE.md](../ARCHITECTURE.md))
  and installation steps (see [UPGRADE_GUIDE.md](UPGRADE_GUIDE.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Release Engineer | Keep pinned versions in sync with build files and CI per release |
| QA Architect | Confirm the supported browser and OS matrix against the test suites |
| Contributors | Use only the listed versions; propose changes via ADR before bumping |

---

## Legend

✅ Supported & exercised · 🟦 Supported (config-only / external) · ⬜ Not supported in v1.0

## 1. Toolchain (Build & Runtime)

| Tool | Required version | Source of truth | Status |
|------|------------------|-----------------|--------|
| Java (JDK) | **21 (LTS)** | `pom.xml` (`java.version=21`, `maven.compiler.release=21`); CI `java-version: "21"`; Dockerfile `eclipse-temurin:21-jre-jammy` | ✅ |
| Node.js | **22** | CI `_reusable-frontend` `node-version` default `"22"`; Dockerfile `node:22-alpine` | ✅ |
| Maven | **3.9+** | Backend build base `maven:3.9-eclipse-temurin-21` | ✅ |
| npm | bundled with Node 22 | ships with Node | ✅ |
| Spring Boot | **3.x** (3.3 parent) | `pom.xml` Spring Boot parent | ✅ |
| Vite | **5.x** (`^5.4.10`) | `apps/frontend/package.json` | ✅ |
| React | **18** (`^18.3.1`) | `apps/frontend/package.json` | ✅ |
| TypeScript | strict mode | `apps/frontend` `tsc --noEmit` | ✅ |

> **Java 17/19/20, Node 18/20, Maven < 3.9:** not validated for `1.0.0` — use the
> pinned versions above.

## 2. Supported Databases

| Database | Version | Profiles | Status |
|----------|---------|----------|--------|
| H2 (embedded) | bundled (Spring Boot managed) | `dev`, `test` | ✅ Default, no install |
| PostgreSQL | 16 (Docker Compose image); 14+ supported externally | `local`, `docker`, `qa`, `stage`, `prod` | ✅ Compose / 🟦 external |
| Testcontainers PostgreSQL | per test config | `test` | ✅ |
| Other RDBMS (MySQL/Oracle/SQL Server) | — | — | ⬜ Not supported in v1.0 |

The same Flyway migration set is portable across H2 and PostgreSQL (see
[MIGRATION_NOTES.md](MIGRATION_NOTES.md)).

## 3. Browser Matrix (Frontend SUT & UI Automation)

| Browser | Engine | Automation | Status |
|---------|--------|------------|--------|
| Google Chrome | Chromium | Playwright + Selenium | ✅ Primary |
| Microsoft Edge | Chromium | Playwright + Selenium | ✅ |
| Mozilla Firefox | Gecko | Playwright + Selenium | ✅ |
| Apple Safari | WebKit | Playwright (WebKit) | 🟦 via WebKit engine |
| WebKit (Playwright) | WebKit | Playwright | ✅ |
| Mobile Chrome (emulated) | Chromium | Playwright device emulation | ✅ Responsive |
| Mobile Safari (emulated) | WebKit | Playwright device emulation | 🟦 Responsive |
| Internet Explorer 11 | Trident | — | ⬜ Not supported |

> Mobile coverage is **responsive web / emulated viewports** only — there is no
> native Android/iOS app in `1.0.0` (see [KNOWN_ISSUES.md](KNOWN_ISSUES.md)).

## 4. Docker Services (Full Stack)

| Service | Role | Status |
|---------|------|--------|
| PostgreSQL | Primary relational database | ✅ |
| Redis | Cache / supporting service | ✅ |
| Mailpit | SMTP capture for email flows | ✅ |
| MinIO | S3-compatible object storage | ✅ |
| Keycloak | Identity provider (realm import) | ✅ |
| WireMock | External-integration stubbing | ✅ |
| Prometheus | Metrics collection | ✅ |
| Grafana | Dashboards (provisioned) | ✅ |
| SonarQube | Code-quality analysis | ✅ |

All services are defined in `infrastructure/docker/` with health checks, named
volumes, and a shared network. Docker is **not run in the build environment**;
configs validate but the stack is started on a Docker host (see
[KNOWN_ISSUES.md](KNOWN_ISSUES.md) and [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)).

## 5. Operating Systems

| OS | Build / run | Notes |
|----|-------------|-------|
| Linux (x86-64) | ✅ | Primary CI and container runtime (GitHub Actions `ubuntu-latest`) |
| macOS (Intel & Apple Silicon) | ✅ | Developer workstation; Docker Desktop for the stack |
| Windows 10/11 | ✅ | Cross-platform `setup/start/stop/reset/health-check` `.bat` scripts provided; Docker Desktop / WSL2 for the stack |

Containerized runtime is Linux-based (`eclipse-temurin:21-jre-jammy`,
`nginx:1.27-alpine`, `node:22-alpine`), so any Docker-capable host runs the stack
identically.

## 6. CI Runner Matrix

| Pipeline | Runner | Toolchain |
|----------|--------|-----------|
| `ci.yml` (PR/push gate) | `ubuntu-latest` | Temurin JDK 21, Node 22 |
| `nightly.yml` (full + e2e + Trivy) | `ubuntu-latest` | Temurin JDK 21, Node 22 |
| `release.yml` (`v*` tag) | `ubuntu-latest` | Temurin JDK 21, Node 22, Buildx → GHCR |
| `codeql.yml` (Java + JS/TS) | `ubuntu-latest` | Temurin JDK 21 |

See [CI_CD_GUIDE.md](CI_CD_GUIDE.md) for the full pipeline matrix.

## Examples

- *Local app run:* Java 21 + Node 22 with the `dev` profile uses embedded H2 — no
  PostgreSQL, no Docker.
- *Cross-browser UI run:* Playwright executes the UI suite on Chromium, Firefox,
  and WebKit from the same specs.

## Future Enhancements

- A documentation-lint rule failing the build when a version here disagrees with
  the build files (per [PROJECT_METADATA.md](PROJECT_METADATA.md) §Future).
- Expanded real-device mobile matrix once native mobile automation arrives (post-1.0).

## Dependencies

- Canonical versions from [PROJECT_METADATA.md](PROJECT_METADATA.md) §3.
- Database/profile model from [MIGRATION_NOTES.md](MIGRATION_NOTES.md).
- CI definitions under `.github/workflows/` (see [CI_CD_GUIDE.md](CI_CD_GUIDE.md)).

## References

- [PROJECT_METADATA.md](PROJECT_METADATA.md) · [UPGRADE_GUIDE.md](UPGRADE_GUIDE.md)
- [../ARCHITECTURE.md](../ARCHITECTURE.md) · [CI_CD_GUIDE.md](CI_CD_GUIDE.md)
- [KNOWN_ISSUES.md](KNOWN_ISSUES.md) · [MIGRATION_NOTES.md](MIGRATION_NOTES.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Release Engineer | Initial (Milestone 10) |
