# Go-Live Checklist

> **The cutover runbook for promoting an omiiCARE_QA build to the target
> environment.** Executed only after [RELEASE_CHECKLIST.md](RELEASE_CHECKLIST.md)
> is fully green and QA sign-off is recorded. Steps are ordered; do not skip
> ahead. Keep [ROLLBACK_CHECKLIST.md](ROLLBACK_CHECKLIST.md) open in parallel.

## Purpose

Provide a deterministic, ordered cutover so deployment, migration, smoke, and
hand-off happen safely with a defined abort point.

## Scope

- **In scope:** pre-cutover gate, deployment & migration sequence, smoke gate,
  monitoring confirmation, hand-off.
- **Out of scope:** detailed post-deploy validation (see
  [DEPLOYMENT_VALIDATION_CHECKLIST.md](DEPLOYMENT_VALIDATION_CHECKLIST.md)) and
  business verification (see [PRODUCTION_VERIFICATION_CHECKLIST.md](PRODUCTION_VERIFICATION_CHECKLIST.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Release Manager / Maintainer | Owns the cutover; calls go/abort |
| Engineering Lead | Executes deploy + migration |
| QA Lead | Runs the smoke gate; confirms pass/fail |

---

## 0. Pre-cutover gate

- [ ] [RELEASE_CHECKLIST.md](RELEASE_CHECKLIST.md) fully complete; QA sign-off recorded.
- [ ] Maintainer Go decision captured (with timestamp & build SHA/tag).
- [ ] Maintenance/change window announced; stakeholders notified.
- [ ] Rollback path confirmed: prior image tag noted; DB backup taken & verified restorable.

## 1. Freeze & backup

- [ ] Code freeze on the release branch; no new merges during cutover.
- [ ] Take a fresh database backup (pre-migration) and confirm it is restorable.
- [ ] Snapshot current running image tags (backend, frontend) for rollback.

## 2. Deploy

- [ ] Pull the tagged images; stop old containers gracefully (drain in-flight requests).
- [ ] Bring up Docker services in dependency order; wait for Postgres, Redis, Keycloak, MinIO, Mailpit, WireMock to report healthy.
- [ ] Confirm config/secrets for the target env loaded (JWT issuer, Keycloak realm, DB URL, MinIO, mail host).

## 3. Migrate

- [ ] Run Flyway migrations; confirm `flyway info`/`validate` shows all applied, no failures, no checksum drift.
- [ ] Seed/refresh reference data if required (synthetic only, BR-CONS-005).

## 4. Smoke gate (abort point)

- [ ] App health endpoints report UP (backend + frontend reachable).
- [ ] Auth: `demo.admin / Admin@12345` logs in; token issued; refresh works.
- [ ] Patient: create + read a synthetic patient (MRN unique, BR-IDENT-001).
- [ ] Appointment: book a valid slot; double-booking correctly rejected `409 APPT_DOUBLE_BOOKING` (BR-APPT-003).
- [ ] FHIR: `GET /fhir/Patient/{id}` returns a valid R4 resource (`gender` lower-cased).
- [ ] Error contract: an RBAC-denied call returns `403 ACCESS_DENIED` (not 500).
- [ ] **If any smoke item fails → ABORT and run [ROLLBACK_CHECKLIST.md](ROLLBACK_CHECKLIST.md).**

## 5. Monitoring & observability

- [ ] Prometheus scraping the backend; targets UP.
- [ ] Grafana dashboards rendering; no firing critical alerts.
- [ ] Correlation IDs (`X-Request-Id`/`traceparent`) visible end-to-end in logs.

## 6. Hand-off

- [ ] Run [DEPLOYMENT_VALIDATION_CHECKLIST.md](DEPLOYMENT_VALIDATION_CHECKLIST.md).
- [ ] Run [PRODUCTION_VERIFICATION_CHECKLIST.md](PRODUCTION_VERIFICATION_CHECKLIST.md).
- [ ] Lift code freeze; record release in CHANGELOG; notify stakeholders Go-Live complete.
- [ ] Heightened monitoring window scheduled (first N hours) with rollback on standby.

## Examples

If the FHIR smoke check returns `"gender": "MALE"` (OMII-BUG-0005 class), the
smoke gate fails at §4 and the cutover aborts to rollback.

## Future Enhancements

- Automate the smoke gate as a one-command post-deploy job (M8).

## Dependencies

- [RELEASE_CHECKLIST.md](RELEASE_CHECKLIST.md), [ROLLBACK_CHECKLIST.md](ROLLBACK_CHECKLIST.md),
  [DEPLOYMENT_VALIDATION_CHECKLIST.md](DEPLOYMENT_VALIDATION_CHECKLIST.md),
  [PRODUCTION_VERIFICATION_CHECKLIST.md](PRODUCTION_VERIFICATION_CHECKLIST.md).
- [../../infrastructure/docker/README.md](../../infrastructure/docker/README.md).

## References

- [../../docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md),
  [../../docs/API_BLUEPRINT.md](../../docs/API_BLUEPRINT.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
