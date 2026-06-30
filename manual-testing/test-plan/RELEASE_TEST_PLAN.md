# Release Test Plan — omiiCARE_QA v1.0.0

> **Release test plan** governing the final verification and go/no-go decision for
> the v1.0.0 release of the implemented omiiCARE_QA surface. It executes the
> regression and smoke suites defined under
> [MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md) and confirms RTM coverage before
> sign-off.

## 1. Release Identifier

`omiiCARE_QA v1.0.0` — first release of the implemented surface (Auth/RBAC,
Patients, Appointments, Providers, FHIR Patient read, Frontend).

## 2. Release Scope

| In release | Out of release |
|------------|----------------|
| Auth: login, refresh, `/auth/me` | HL7 v2 messaging (Future) |
| RBAC default-deny + per-endpoint permissions | Multi-tenant isolation (Future) |
| Patients CRUD + search + pagination | Consent / PHI read-audit (Future) |
| Appointments book/cancel + BR-APPT-002/003 + re-cancel | Full lifecycle state machines (Future) |
| Providers list | Billing/Lab/Radiology/Pharmacy/Insurance modules |
| FHIR Patient read + R4 conformance | Native mobile, k8s, prod cloud, real integrations |
| Frontend Login/Dashboard/Patients/Appointments/403/404 | Real PHI, HIPAA certification |

## 3. Regression Scope

All `Regression? = Y` rows in the [RTM](../rtm/RTM.md) must pass:

| Family | Cases | Priority |
|--------|-------|----------|
| Auth | TC-AUTH-001..008 | Critical/High |
| Security/RBAC | TC-SEC-001..007 | Critical/High |
| Patients | TC-PAT-001..010 | Critical/High |
| Appointments | TC-APPT-001..009 | Critical/High |
| FHIR | TC-FHIR-001..006 | Critical/High |

## 4. Smoke Scope (release gate entry)

The smoke suite (`Smoke? = Y` rows) must pass before regression begins:

- TC-AUTH-001 (login), TC-AUTH-006 (`/auth/me`), TC-AUTH-007/008 (token + UI route)
- TC-SEC-003/004 (default-deny + per-endpoint RBAC)
- TC-PAT-001/002/005/008/009/010 (list, register, retrieve, search, UI, authz)
- TC-APPT-001/002/004/007/008/009 (list, book, double-booking, providers, UI, authz)
- TC-FHIR-001/004 (FHIR read + R4 schema)

## 5. Environments

| Environment | Profile | Use |
|-------------|---------|-----|
| QA | `qa` | Full regression + smoke; release-candidate signal |
| Stage | `stage` | Pre-production smoke + critical-path E2E re-run |

Both seeded with PHI-safe synthetic data; health-checked green before execution.

## 6. Go / No-Go Criteria

**GO** when all hold:

- 100% of smoke cases pass.
- ≥98% of regression cases pass; 100% of Critical-priority cases pass.
- Zero open Critical or High defects on in-scope features.
- RTM shows no `Gap`/`Partial` on any Critical requirement (the single `Gap`,
  HL7-ADT-001, is Future/out-of-scope and accepted).
- Defect leakage from the cycle < 5%.
- All residual risks documented and accepted (QA Lead proposes, Maintainer approves).

**NO-GO** when any Critical/High in-scope defect is open, smoke fails, or a
critical requirement is untested.

## 7. Defect Posture at Gate

| Severity | Gate rule |
|----------|-----------|
| Critical | Must be zero open → blocks release |
| High | Must be zero open → blocks release |
| Medium | Allowed with documented remediation ticket |
| Low | Allowed; tracked to backlog |

## 8. Sign-Off Chain

| Order | Approver | Confirms |
|-------|----------|----------|
| 1 | Senior QA Engineer | All assigned cases executed with evidence |
| 2 | QA Lead | Pass/fail criteria met; RTM coverage complete; residual risk listed |
| 3 | QA Architect | Approach/risk alignment; no unmitigated Critical/High security risk |
| 4 | Maintainer | Final go/no-go; v1.0.0 release transition |

Sign-off records are filed under [../signoff/](../signoff/).

## 9. Rollback / Contingency

| Trigger | Action |
|---------|--------|
| Critical defect found post-gate | Halt release; hotfix + targeted regression of affected family |
| Environment failure during regression | Suspend; restore + re-seed; resume from last green case |
| RBAC bypass discovered (R-05) | No-go; security review before re-gating |

## Dependencies

- [MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md), [../rtm/RTM.md](../rtm/RTM.md),
  [../requirements/REQUIREMENTS.md](../requirements/REQUIREMENTS.md),
  [docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
