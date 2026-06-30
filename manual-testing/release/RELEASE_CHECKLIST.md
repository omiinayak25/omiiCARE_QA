# Release Checklist

> **The master pre-release gate for omiiCARE_QA.** Complete every item before a
> build is promoted toward go-live. Tailored to this stack: Java 21 / Spring Boot
> 3.x backend, React frontend, PostgreSQL + Flyway, Docker Compose infrastructure
> (Postgres, Redis, Keycloak, MinIO, Mailpit, WireMock, Prometheus, Grafana,
> SonarQube). This is the umbrella; the go-live, rollback, deployment-validation,
> and production-verification checklists are run alongside it.

## Purpose

Guarantee that quality gates, traceability, defects, risks, data safety, and
operability are all satisfied before release promotion — no item is implicit.

## Scope

- **In scope:** code/quality gates, test completion, defect & risk posture, data
  safety, build & migration readiness, sign-off.
- **Out of scope:** the cutover steps (see [GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md))
  and post-deploy verification (see [PRODUCTION_VERIFICATION_CHECKLIST.md](PRODUCTION_VERIFICATION_CHECKLIST.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Owns this checklist; signs the QA gate |
| Engineering Lead | Confirms build, migrations, quality gates |
| Maintainer | Approves release promotion |

---

## 1. Code & quality gates

- [ ] `main`/`develop` build is green; artifact built from a tagged commit (SHA recorded).
- [ ] Unit + integration tests pass; coverage at/above threshold.
- [ ] Lint + format clean; no critical SonarQube issues.
- [ ] Dependency-Check / CVE scan clean or all findings triaged & accepted.
- [ ] OpenAPI/Swagger regenerated; API contract matches [../../docs/API_BLUEPRINT.md](../../docs/API_BLUEPRINT.md).
- [ ] CHANGELOG and version bumped per [../../VERSIONING.md](../../VERSIONING.md).

## 2. Test completion

- [ ] Smoke suite passes (auth, patient, appointment, FHIR, error contract).
- [ ] Regression suite passes; failures triaged.
- [ ] Risk priority-zone coverage verified (RR-01–RR-05, RR-08, RR-16–RR-18) per [../risk-analysis/RISK_MATRIX.md](../risk-analysis/RISK_MATRIX.md).
- [ ] RTM shows no critical requirement uncovered.
- [ ] UAT exit criteria met ([../uat/UAT_PLAN.md](../uat/UAT_PLAN.md)).

## 3. Defect posture

- [ ] **Zero open S1** defects ([../bug-templates/SEVERITY_PRIORITY_MATRIX.md](../bug-templates/SEVERITY_PRIORITY_MATRIX.md)).
- [ ] Open S2 defects reviewed; each fixed, deferred-with-approval, or accepted.
- [ ] All S1/S2 fixes Verified, not merely Fixed ([../bug-templates/DEFECT_LIFECYCLE.md](../bug-templates/DEFECT_LIFECYCLE.md)).

## 4. Risk posture

- [ ] No open **Critical (9)** or unmitigated **High (6)** healthcare/security risk.
- [ ] Residual-risk acceptances recorded and approved (Maintainer).

## 5. Data safety & compliance

- [ ] All datasets synthetic / PHI-safe (BR-CONS-005); no real PHI present.
- [ ] Audit logging verified on representative PHI actions (BR-AUDIT-001/002).
- [ ] Tenant isolation spot-checked (`CROSS_TENANT_DENIED`, BR-TENANT-002).

## 6. Build & migration readiness

- [ ] Flyway migrations validated; dry-run/`validate` clean; no pending checksum drift.
- [ ] Migration is forward-only or has a tested down path; backup taken before apply.
- [ ] Container images built, tagged, and pushed (backend, frontend); prior image tag recorded for rollback.
- [ ] `.env` / config reviewed for the target env (JWT issuer, Keycloak realm, DB URL, MinIO, mail).

## 7. Operability

- [ ] Health endpoints respond UP; readiness/liveness configured.
- [ ] Monitoring up (Prometheus scraping; Grafana dashboards loaded).
- [ ] Log correlation (`X-Request-Id` / `traceparent`) flowing.

## 8. Sign-off

- [ ] QA sign-off completed ([../signoff/SIGNOFF_TEMPLATE.md](../signoff/SIGNOFF_TEMPLATE.md)).
- [ ] Go/No-Go recommendation recorded; Maintainer approval captured.
- [ ] [GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md) ready to execute; [ROLLBACK_CHECKLIST.md](ROLLBACK_CHECKLIST.md) on standby.

## Examples

If an open S1 cross-tenant defect (OMII-BUG-0008) remains in §3, the release is
**No-Go** until it is Verified and the linked risk RR-04 leaves `Open`.

## Future Enhancements

- Wire this checklist to CI so gate items auto-tick from pipeline results (M8).

## Dependencies

- [GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md), [ROLLBACK_CHECKLIST.md](ROLLBACK_CHECKLIST.md),
  [DEPLOYMENT_VALIDATION_CHECKLIST.md](DEPLOYMENT_VALIDATION_CHECKLIST.md),
  [PRODUCTION_VERIFICATION_CHECKLIST.md](PRODUCTION_VERIFICATION_CHECKLIST.md).
- [../signoff/SIGNOFF_TEMPLATE.md](../signoff/SIGNOFF_TEMPLATE.md),
  [../risk-analysis/RISK_MATRIX.md](../risk-analysis/RISK_MATRIX.md).

## References

- [../../VERSIONING.md](../../VERSIONING.md), [../../docs/API_BLUEPRINT.md](../../docs/API_BLUEPRINT.md),
  [../../infrastructure/docker/README.md](../../infrastructure/docker/README.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
