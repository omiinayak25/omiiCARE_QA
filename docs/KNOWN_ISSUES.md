# Known Issues & Limitations — omiiCARE_QA v1.0.0

> **Purpose.** An honest, severity-rated register of the known issues and
> deliberate limitations of the `1.0.0` release. Most entries are **intentional
> v1.0 scope boundaries** (see
> [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §3), not
> defects. Each row gives a workaround and the planned direction.

## Scope

- **In scope:** environment/tooling prerequisites, deliberate functional
  boundaries, and operational constraints that an adopter should know before
  running or evaluating the platform.
- **Out of scope:** the per-area highlights (see
  [../RELEASE_NOTES.md](../RELEASE_NOTES.md)) and the post-1.0 roadmap (see
  [../ROADMAP.md](../ROADMAP.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Release Engineer | Keep this register accurate at each release |
| Maintainer ([@omiinayak25](https://github.com/omiinayak25)) | Approve severity ratings and scope decisions |
| Contributors | Add new known issues here rather than leaving them undocumented |

---

## 1. Severity Legend

| Severity | Meaning |
|----------|---------|
| **High** | Blocks a common workflow without the stated workaround |
| **Medium** | Limits functionality or requires extra setup; workaround is straightforward |
| **Low** | Minor or cosmetic; deliberate scope boundary with negligible impact |

## 2. Known Issues & Limitations

| # | Area | Severity | Issue / Limitation | Workaround | Planned |
|---|------|----------|--------------------|------------|---------|
| 1 | Infrastructure | Medium | The **full containerized stack requires Docker**, which is **not run in the build environment** — Compose configs validate but the stack is not started during the build. | Run `docker compose -f infrastructure/docker/docker-compose.yml up -d` on a host with Docker; for app-only work use the `dev` profile (embedded H2, no Docker). | Containerized smoke verification in CI as the gate hardens (post-1.0). |
| 2 | Automation | Medium | **End-to-end / UI / BDD suites need a running SUT** and are tagged-excluded from the default build, so they do not execute on a plain `mvn test`. | Start backend (:8080) and frontend (:5173), then run with `-Pe2e` and point `framework.properties` / env at the target. | Ephemeral SUT in the nightly pipeline for full e2e on a schedule. |
| 3 | Advanced QE | Medium | **Quality scans require their tools installed** — k6, JMeter, Gatling, OWASP ZAP, Dependency-Check, Lighthouse, axe-core, and Trivy are not bundled. | Install each tool per its `quality/<dimension>/` guide before running that suite. | Containerized tool runners and documented versions per dimension. |
| 4 | Performance | Medium | Performance scripts are **owned-infrastructure-only** and ship as representative suites, **not thousands of executions**. | Run only against infrastructure you own; treat scripts as templates to scale up deliberately. | Expanded load profiles and trend reporting (v1.1+). |
| 5 | FHIR | Medium | FHIR is a **read-only Patient facade** (`GET /api/v1/fhir/Patient/{id}` only) — no write operations and no other resources. | Use the internal REST API (`/api/v1/patients`, etc.) for writes; use the facade for FHIR read interoperability. | Additional read resources, then write/search, in v1.1 (see [ROADMAP.md](../ROADMAP.md)). |
| 6 | Deployment | Medium | **Single-node only** — no microservices split, Kubernetes, distributed database, or cloud provisioning. | Run the single-node Docker Compose stack or local profiles. | Scale-out (microservices, K8s, distributed DB, cloud) is **v2.0** roadmap scope. |
| 7 | Mobile | Low | **No native Android/iOS applications**; the frontend is responsive web/PWA-oriented only. | Use the responsive web app; test mobile viewports via browser device emulation. | Native mobile automation is post-1.0 roadmap scope. |
| 8 | Integrations | Low | **No real hospital / payment-gateway / insurance-provider integrations** — external systems are stubbed/WireMock or public sandboxes. | Use the resource-adapter layer against the documented public targets and WireMock stubs. | Real integrations remain out of scope; clean adapter seams are in place. |
| 9 | Compliance | Low | Models **HIPAA-like** practices and FHIR/HL7 conformance for education only — **no formal certification claims**. | Treat compliance artifacts as educational mappings, not audit evidence; see [../SECURITY.md](../SECURITY.md). | Formal certification is explicitly out of scope (v1.0 and beyond). |
| 10 | Data | Low | **Synthetic, PHI-safe data only** — no real patient data, intentionally. | Use the seeded synthetic datasets and `PatientFactory`; never load real PHI. | Expanded synthetic datasets (v1.2). |
| 11 | Coverage | Low | Backend JaCoCo and security/lint gates are **report-only / advisory** today, not yet blocking thresholds. | Read the reports under `apps/backend/target/site/jacoco/`; enforce locally as needed. | Thresholds become blocking as the quality gate hardens (see [CI_CD_GUIDE.md](CI_CD_GUIDE.md)). |
| 12 | Frontend tests | Low | No frontend component/unit test suite ships in `1.0.0`; the build enforces type safety and lint instead. | Rely on `npm run build` (tsc strict) + ESLint `--max-warnings=0` and the automation UI suite. | Component/coverage instrumentation added with a frontend test suite (v1.1). |

## 3. Reporting a New Issue

Report issues via GitHub Issues on
<https://github.com/omiinayak25/omiiCARE_QA.git>. Security-sensitive reports
follow the disclosure process in [../SECURITY.md](../SECURITY.md). Triage and
severity conventions are described in `manual-testing/` defect-management assets.

## Examples

- *Evaluator wants the full stack:* row 1 — install Docker, then
  `docker compose ... up -d`; otherwise use the `dev` profile for app-only work.
- *Adopter expects FHIR writes:* row 5 — `1.0.0` exposes only a read facade; use
  the REST API for writes and watch v1.1 for additional resources.

## Future Enhancements

- Link each row to a GitHub Issue / Milestone once issues are opened.
- Auto-generate a "resolved since last release" delta in future release cycles.

## Dependencies

- Scope boundaries from [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §3.
- Roadmap direction from [../ROADMAP.md](../ROADMAP.md).
- Release summary in [../RELEASE_NOTES.md](../RELEASE_NOTES.md).

## References

- [../RELEASE_NOTES.md](../RELEASE_NOTES.md) · [../ROADMAP.md](../ROADMAP.md)
- [UPGRADE_GUIDE.md](UPGRADE_GUIDE.md) · [COMPATIBILITY_MATRIX.md](COMPATIBILITY_MATRIX.md)
- [../SECURITY.md](../SECURITY.md) · [CI_CD_GUIDE.md](CI_CD_GUIDE.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Release Engineer | Initial (Milestone 10) |
