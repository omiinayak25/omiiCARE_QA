# SonarQube & Quality Gates — omiiCARE_QA

Static analysis, coverage enforcement, and a blocking **Quality Gate** for the
omiiCARE_QA enterprise healthcare QA platform. This document is the operational
reference for running SonarQube locally and in CI, the gate definition, and the
healthcare-specific rule set.

- **Scanner config:** [`sonar-project.properties`](../../sonar-project.properties) (repo root)
- **SonarQube server:** `infrastructure/docker/docker-compose.yml` service `sonarqube`
  (image `sonarqube:10-community`, container `omiicare-sonarqube`)
- **Scope:** Maven reactor modules `apps/backend`, `automation`, `ai` (Java 21)
  plus `apps/frontend` (React + TypeScript + Vite)

---

## 1. Architecture

```
+---------------------------+        scan        +---------------------------+
|  Developer / CI runner    | -----------------> |  SonarQube 10 Community    |
|  - mvn verify (JaCoCo)    |   sonar-scanner    |  omiicare-sonarqube        |
|  - frontend coverage      |                    |  host :9002 -> container   |
|  - sonar-project.props    |                    |  :9000  (web + Compute     |
+---------------------------+                    |  Engine + embedded ES)     |
                                                 +---------------------------+
```

- One SonarQube **project** (`omiicare-qa`) with four Sonar **modules**
  (`backend`, `automation`, `ai`, `frontend`) so the gate is evaluated across
  the whole platform in a single analysis.
- `automation` is a **test-only** module (package `com.omiicare.qa.automation`,
  `src/test/java` only); its sources are analysed for framework code quality.

---

## 2. Port mapping (verified)

From `infrastructure/docker/docker-compose.yml`:

| Where | Port | Notes |
|-------|------|-------|
| **Host** | `9002` | `${SONARQUBE_PORT:-9002}` — avoids MinIO on host `9000` |
| **Container** | `9000` | SonarQube's native web port |

> **Browse / API / scanner URL from the host:** `http://localhost:9002`.
> The container listens on `9000` internally; only the host mapping is `9002`.
> When a scanner runs **inside** the Docker network (e.g. a CI service container
> on the same compose network) it targets `http://sonarqube:9000`.

Health check: `http://localhost:9002/api/system/status` returns `"status":"UP"`.

---

## 3. Start the SonarQube server

```bash
# One-time host prerequisite for the embedded Elasticsearch (see compose comment).
sudo sysctl -w vm.max_map_count=262144

# Start only SonarQube (and its volumes) from the infra compose file.
docker compose -f infrastructure/docker/docker-compose.yml up -d sonarqube

# Wait until UP (first boot migrates the schema and can take ~60-90s).
until curl -fs http://localhost:9002/api/system/status | grep -q '"status":"UP"'; do
  echo "waiting for SonarQube..."; sleep 5;
done
echo "SonarQube is UP at http://localhost:9002"
```

Persistent volumes (`sonarqube-data`, `sonarqube-extensions`, `sonarqube-logs`)
retain projects, history, and the quality gate across restarts.

### First-login & token

1. Open `http://localhost:9002`, log in (`admin` / `admin`), set a new password.
2. **My Account -> Security -> Generate Token** (type: *Project Analysis Token*
   for `omiicare-qa`, or a *Global Analysis Token* for CI).
3. Export it for scanner runs:

```bash
export SONAR_HOST_URL="http://localhost:9002"
export SONAR_TOKEN="squ_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

Never commit the token. In CI, store it as the secret `SONAR_TOKEN`.

---

## 4. Generate coverage before scanning

The scanner does **not** run tests — it ingests reports. Produce them first.

### Backend / automation / ai (JaCoCo XML)

JaCoCo is configured in the root `pom.xml` (`jacoco-maven-plugin` 0.8.12) with
`prepare-agent` and a `report` execution bound to the **`verify`** phase, so the
XML lands at `<module>/target/site/jacoco/jacoco.xml` (the path referenced in
`sonar-project.properties`).

```bash
# Whole reactor: compile, run unit tests, emit JaCoCo XML for every module.
mvn -q clean verify

# Or just the default automation unit suite (98 unit tests PASS):
mvn -q -pl automation verify
```

> SUT/browser tests (tags `ui-e2e`, `api-e2e`, `bdd`) run via
> `mvn -pl automation -Pe2e test` and are **excluded** from the gate's coverage
> math — the gate measures the unit suite only.

### Frontend (LCOV) — *(planned)*

The frontend `build`/`lint`/`typecheck` scripts are green today. A coverage
script emitting `apps/frontend/coverage/lcov.info` is **(planned)**; until it
exists, `frontend.sonar.javascript.lcov.reportPaths` resolves to nothing and
the frontend contributes static-analysis findings but `0%` measured coverage.
Planned wiring:

```jsonc
// apps/frontend/package.json (planned)
"scripts": { "coverage": "vitest run --coverage" }
```

```bash
npm --prefix apps/frontend run coverage   # (planned) -> coverage/lcov.info
```

---

## 5. Run the analysis

### Option A — sonar-scanner CLI (recommended; picks up `sonar-project.properties`)

```bash
# From repo ROOT (the properties file lives at the root).
docker run --rm \
  -e SONAR_HOST_URL="http://localhost:9002" \
  -e SONAR_TOKEN="$SONAR_TOKEN" \
  --network host \
  -v "$(pwd):/usr/src" \
  sonarsource/sonar-scanner-cli:latest
```

Override version at release time:

```bash
... sonarsource/sonar-scanner-cli:latest -Dsonar.projectVersion="$RELEASE_VERSION"
```

### Option B — native scanner binary

```bash
sonar-scanner \
  -Dsonar.host.url="$SONAR_HOST_URL" \
  -Dsonar.token="$SONAR_TOKEN"
```

> The scanner reads module layout, sources/tests, JaCoCo/LCOV report paths, and
> exclusions from `sonar-project.properties`. Do not duplicate them on the CLI.

After analysis, the gate result is at
`http://localhost:9002/dashboard?id=omiicare-qa`.

---

## 6. Quality Gate definition — `omiiCARE QA Gate`

Create a gate named **`omiiCARE QA Gate`** and set it as the project gate
(Project Settings -> Quality Gate). Conditions are evaluated on **New Code**
(the change set) so the gate is enforceable from day one without blocking on the
existing backlog, with one absolute overall coverage floor.

| # | Metric | Operator | Threshold | Scope | Blocks release? |
|---|--------|----------|-----------|-------|-----------------|
| 1 | Coverage | is less than | **80.0%** | New code | **Yes** |
| 2 | Coverage (overall) | is less than | **60.0%** | Overall | **Yes** |
| 3 | Duplicated lines (%) | is greater than | **3.0%** | New code | **Yes** |
| 4 | Maintainability rating | is worse than | **A** | New code | **Yes** |
| 5 | Reliability rating | is worse than | **A** | New code | **Yes** |
| 6 | Security rating | is worse than | **A** | New code | **Yes** |
| 7 | Security hotspots reviewed | is less than | **100%** | New code | **Yes** |
| 8 | Blocker issues | is greater than | **0** | Overall | **Yes** |
| 9 | Critical issues | is greater than | **0** | New code | **Yes** |

**Ratings recap**
- *Maintainability A* = technical-debt ratio <= 5%.
- *Reliability A* = zero bugs (worse than A means >= 1 Minor bug introduced).
- *Security A* = zero vulnerabilities; **Security hotspots reviewed = 100%** is
  mandatory for a healthcare codebase (PHI exposure, auth, crypto).

> **Coverage ramp:** the absolute floor (#2) starts at 60% to match the current
> maturity (`jacoco.line.coverage` in `pom.xml` is `0.00` today). Raise it as
> coverage grows; the New-Code 80% (#1) prevents regressions immediately.

A **red** gate must **fail the CI job and block the PR/release**. Never override
a red gate to ship — fix or formally waive via an issue with reviewer sign-off.

---

## 7. Healthcare-specific rules

Beyond the default *Sonar way* profile, enable a derived profile
**`omiiCARE Healthcare Java`** (and a JS/TS counterpart) with these focus areas.
PHI handling and auditability are non-negotiable in a HIPAA context.

| Area | Rule intent | Why it matters here |
|------|-------------|---------------------|
| **No PHI in logs** | Flag logging of patient identifiers, MRN, DOB, SSN, names, FHIR `Patient`/`Encounter` fields | Avoid PHI leakage to log sinks (HIPAA §164.312) |
| **No hard-coded secrets/PHI** | `secrets`/credential detection; ban literal patient data | Synthetic/PHI-safe data only — repo rule |
| **Crypto strength** | Ban weak hashing/ciphers (MD5/SHA-1/DES); require TLS | Protect data in transit/at rest |
| **Injection** | SQL/LDAP/command injection, XXE, SSRF (OWASP Top 10) | DB-testing & adapter layer touch many backends |
| **AuthN/AuthZ** | No auth bypass, no broken access control, RBAC checks present | Matches RBAC docs in `docs/reverse-engineering/` |
| **Security hotspots** | 100% reviewed (gate #7) | Auth, crypto, deserialization paths get human review |
| **Deserialization / SSRF** | Unsafe deserialization, unvalidated outbound URLs | Resource Adapter Layer calls external SUTs |
| **Test integrity** | No disabled/ignored assertions, no `@Disabled` without reason | QA platform's own tests must be trustworthy |

Implementation:
- Inherit *Sonar way*, then activate the rules above and raise PHI/secret/crypto
  rules to **Blocker/Critical** so they trip gate conditions #8/#9.
- Use `sonar.issue.ignore` *only* for verified false positives, justified inline.
- Synthetic data (Datafaker, PHI-safe generators in `core.generators`) must
  never be replaced with real patient data — the no-PHI rules guard this.

> **Hard rule reminder:** performance & security testing run on **owned/local**
> environments only. Never point load/attack tooling at `o2.openmrs.org`.

---

## 8. CI integration *(planned — use a DISTINCT workflow name)*

Add a new workflow (e.g. `.github/workflows/sonar-quality-gate.yml`) that does
**not** overwrite the existing omiiCARE CI workflow. Outline:

1. Checkout (full history: `fetch-depth: 0`, required for accurate New-Code).
2. Set up JDK 21 (Temurin) and Node 22.
3. `mvn -B clean verify` (runs unit tests + emits JaCoCo XML per module).
4. `npm --prefix apps/frontend ci` then `npm --prefix apps/frontend run coverage` *(planned)*.
5. Run `sonarsource/sonar-scanner-cli` with `SONAR_HOST_URL` + `SONAR_TOKEN`
   secrets (reads `sonar-project.properties`).
6. Pass `-Dsonar.qualitygate.wait=true` so the job **blocks** on the gate result
   and fails CI when the gate is red.
7. For PRs, pass `-Dsonar.pullrequest.key`, `.branch`, `.base` to scope New-Code
   to the PR diff and post the gate status as a check.

```bash
# Gate-blocking flag (step 6):
sonar-scanner -Dsonar.qualitygate.wait=true
```

Required CI secrets: `SONAR_TOKEN` (global/project analysis token),
`SONAR_HOST_URL` (the reachable server URL for the runner).

---

## 9. Troubleshooting

| Symptom | Cause | Fix |
|---------|-------|-----|
| SonarQube container won't start / exits | `vm.max_map_count` too low for ES | `sudo sysctl -w vm.max_map_count=262144` |
| `status` never `UP` | First-boot schema migration still running | Wait 60-90s; check `docker logs omiicare-sonarqube` |
| Coverage shows `0%` for Java | `mvn verify` not run, or wrong report path | Ensure `target/site/jacoco/jacoco.xml` exists per module |
| Frontend coverage `0%` | LCOV report missing | Wire the `coverage` script *(planned)* -> `coverage/lcov.info` |
| New-Code metrics empty | Shallow clone in CI | `fetch-depth: 0` on checkout |
| Gate passes but should fail | Gate not assigned to project | Project Settings -> Quality Gate -> `omiiCARE QA Gate` |
| Scanner can't reach server | Wrong URL/port | Host: `http://localhost:9002`; in-network: `http://sonarqube:9000` |

---

## 10. Quick reference

```bash
# 1. Start server
docker compose -f infrastructure/docker/docker-compose.yml up -d sonarqube
# 2. Coverage
mvn -q clean verify
# 3. Scan (from repo root)
docker run --rm -e SONAR_HOST_URL=http://localhost:9002 -e SONAR_TOKEN="$SONAR_TOKEN" \
  --network host -v "$(pwd):/usr/src" sonarsource/sonar-scanner-cli:latest
# 4. Result
open http://localhost:9002/dashboard?id=omiicare-qa
```

| Item | Value |
|------|-------|
| Project key | `omiicare-qa` |
| Server (host) | `http://localhost:9002` |
| Server (in-network) | `http://sonarqube:9000` |
| Image | `sonarqube:10-community` |
| Gate | `omiiCARE QA Gate` (blocking) |
| Java coverage report | `<module>/target/site/jacoco/jacoco.xml` |
| Frontend coverage report | `apps/frontend/coverage/lcov.info` *(planned)* |
