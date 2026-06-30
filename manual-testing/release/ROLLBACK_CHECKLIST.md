# Rollback Checklist

> **The abort-and-recover runbook for an omiiCARE_QA release.** Triggered when the
> go-live smoke gate fails, a Critical defect surfaces post-deploy, or monitoring
> shows a regression. Rollback restores the **prior container image** plus a
> **verified database backup**, then re-verifies the system is healthy on the old
> version.

## Purpose

Return the environment to the last known-good state quickly and safely, with data
integrity preserved and the failure captured for root-cause analysis.

## Scope

- **In scope:** decision criteria, image rollback, DB restore, re-verification,
  and post-mortem capture.
- **Out of scope:** the forward cutover (see [GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Release Manager / Maintainer | Declares rollback; owns the decision |
| Engineering Lead | Executes image + DB restore |
| QA Lead | Re-runs smoke on the restored version; confirms recovery |

---

## 0. Trigger criteria (declare rollback if ANY)

- [ ] Go-live smoke gate failed ([GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md) §4).
- [ ] A new **S1** defect is observed post-deploy (patient-safety, PHI leak, auth/tenant breach, data loss).
- [ ] Flyway migration failed or left the schema inconsistent.
- [ ] Monitoring shows critical alerts / error-rate spike beyond threshold.
- [ ] Health endpoints fail to reach UP within the window.

## 1. Declare & freeze

- [ ] Release Manager declares rollback; timestamp and reason recorded.
- [ ] Stop accepting new traffic / extend the maintenance window; notify stakeholders.
- [ ] Capture diagnostics first: logs, correlation IDs, failing responses, Grafana snapshots (for RCA — do not discard).

## 2. Restore application

- [ ] Stop the new containers gracefully (drain in-flight requests).
- [ ] Re-deploy the **prior image tags** (backend + frontend) recorded in go-live §1.
- [ ] Restore the prior `.env`/config snapshot for the old version.

## 3. Restore database (only if migration ran / data changed)

- [ ] Assess: did the new release run a forward-only Flyway migration? If yes and it is not backward-compatible, restore is required.
- [ ] Restore the **pre-migration backup** taken in go-live §1; verify row counts / integrity checks.
- [ ] Confirm Flyway schema history matches the restored DB (no orphaned/failed migration row).
- [ ] Re-seed only synthetic reference data if needed (BR-CONS-005).

## 4. Re-verify (known-good)

- [ ] All Docker services healthy (Postgres, Redis, Keycloak, MinIO, Mailpit, WireMock).
- [ ] App health endpoints UP on the restored version.
- [ ] Re-run the go-live smoke gate ([GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md) §4): auth, patient, appointment double-booking reject, FHIR, error contract.
- [ ] Monitoring back to baseline; no firing critical alerts.

## 5. Communicate & close

- [ ] Confirm to stakeholders the environment is restored and stable.
- [ ] Lift the maintenance window once smoke passes on the old version.
- [ ] Raise/keep open the blocking defect(s) at S1 with the captured evidence.
- [ ] Link the failure to its risk in [../risk-analysis/RISK_REGISTER.md](../risk-analysis/RISK_REGISTER.md) (e.g. RR-16 migration, RR-20 restore).

## 6. Post-mortem

- [ ] Conduct a 5-Whys RCA ([../bug-templates/ROOT_CAUSE_CATEGORIES.md](../bug-templates/ROOT_CAUSE_CATEGORIES.md)).
- [ ] Record corrective + preventive actions; add a regression/smoke check to catch the class next time.
- [ ] Update the release & go-live checklists if a gate gap allowed the escape.

## Examples

If a forward Flyway migration drops a column the old image still reads (RR-16),
rollback restores the pre-migration backup and the prior image, then re-runs smoke
to confirm the old version is healthy.

## Future Enhancements

- One-command scripted rollback (image + DB restore) with automated smoke (M8).
- Blue/green or canary to make rollback near-instant (roadmap).

## Dependencies

- [GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md), [RELEASE_CHECKLIST.md](RELEASE_CHECKLIST.md),
  [../risk-analysis/RISK_REGISTER.md](../risk-analysis/RISK_REGISTER.md),
  [../bug-templates/ROOT_CAUSE_CATEGORIES.md](../bug-templates/ROOT_CAUSE_CATEGORIES.md).
- [../../infrastructure/docker/README.md](../../infrastructure/docker/README.md),
  [../../database/restore/README.md](../../database/restore/README.md).

## References

- [../../docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md); Flyway migration semantics.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
