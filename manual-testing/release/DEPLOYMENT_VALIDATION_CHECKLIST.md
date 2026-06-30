# Deployment Validation Checklist

> **Technical validation immediately after an omiiCARE_QA deployment.** Confirms
> the *platform* came up correctly — services, migrations, connectivity, security
> config, and observability — before business verification begins. Run as part of
> [GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md) §6, after the smoke gate passes.

## Purpose

Verify the deployed system is technically sound and correctly wired so that any
later business-flow failure can be attributed to logic, not infrastructure.

## Scope

- **In scope:** service health, schema/migration state, connectivity, security
  config, observability.
- **Out of scope:** business-flow correctness (see
  [PRODUCTION_VERIFICATION_CHECKLIST.md](PRODUCTION_VERIFICATION_CHECKLIST.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Engineering Lead | Confirms services, migrations, connectivity |
| QA Lead | Confirms security config + observability evidence |

---

## 1. Service health (Docker Compose)

- [ ] `postgres` healthy (`pg_isready`); reachable on its configured port.
- [ ] `redis` healthy (`redis-cli ping` → PONG).
- [ ] `keycloak` ready; realm imported; issuer URL matches backend config.
- [ ] `minio` ready; bucket(s) present; credentials valid.
- [ ] `mailpit` ready (SMTP + UI reachable).
- [ ] `wiremock` ready (`/__admin/health`) if external adapters are stubbed.
- [ ] Backend container UP; `/actuator/health` (or equivalent) reports UP.
- [ ] Frontend served and reachable; loads without console errors.

## 2. Schema & migrations

- [ ] Flyway: all migrations `Success`; none `Pending`/`Failed`; no checksum drift.
- [ ] Schema version matches the released build's expected version.
- [ ] Reference/seed data present and synthetic only (BR-CONS-005).

## 3. Connectivity & integration

- [ ] Backend ↔ Postgres connection pool healthy (no connection errors at startup).
- [ ] Backend ↔ Redis (cache/session) operational.
- [ ] Backend ↔ Keycloak: token issuance + JWKS fetch succeed (valid JWT accepted).
- [ ] Backend ↔ MinIO: object put/get round-trip works.
- [ ] Outbound mail reaches Mailpit (test notification, BR-NOTIF-004).

## 4. Security configuration

- [ ] HTTPS/TLS terminated as expected for the env; secure headers present.
- [ ] JWT issuer/audience/expiry configured; expired token rejected `401 UNAUTHENTICATED`.
- [ ] RBAC default-deny active: unauthorized call returns `403 ACCESS_DENIED` (BR-RBAC-001), not 500.
- [ ] Tenant context derived from token; cross-tenant id returns `403 CROSS_TENANT_DENIED` (BR-TENANT-002).
- [ ] No secrets/credentials present in logs or error bodies.

## 5. Observability

- [ ] Prometheus targets for the backend are UP and scraping.
- [ ] Grafana dashboards load and show live metrics.
- [ ] Correlation/trace IDs (`X-Request-Id`/`traceparent`) propagate request→log.
- [ ] Application logs free of startup ERRORs / unresolved exceptions.

## 6. Configuration parity

- [ ] Active Spring profile correct for the env (`docker`/`qa`/`stage`).
- [ ] `.env` values reviewed (DB URL, ports, issuer, mail host) — no `dev`/H2 leakage into a server env.
- [ ] Page-size cap (max 100) and other limits applied (guards RR-14).

## Examples

If §2 shows a Flyway migration `Failed`, deployment validation fails and the
release proceeds to [ROLLBACK_CHECKLIST.md](ROLLBACK_CHECKLIST.md) before any
business verification.

## Future Enhancements

- Convert this into an automated post-deploy health job emitting a pass/fail report (M8).

## Dependencies

- [GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md),
  [PRODUCTION_VERIFICATION_CHECKLIST.md](PRODUCTION_VERIFICATION_CHECKLIST.md),
  [ROLLBACK_CHECKLIST.md](ROLLBACK_CHECKLIST.md).
- [../../infrastructure/docker/README.md](../../infrastructure/docker/README.md),
  [../../docs/API_BLUEPRINT.md](../../docs/API_BLUEPRINT.md).

## References

- [../../docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md),
  [../../infrastructure/README.md](../../infrastructure/README.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
