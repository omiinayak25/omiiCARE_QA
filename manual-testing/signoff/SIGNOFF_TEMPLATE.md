# QA Sign-Off Template

> **The QA release sign-off for omiiCARE_QA.** Copy this template for each release
> candidate. It summarizes what was tested, the results, open defects by severity,
> accepted residual risks, and a Go / No-Go recommendation with approver
> signatures. It is the evidence the Maintainer relies on to promote a release.

## Purpose

Provide a single, auditable record that captures the QA position on a release
candidate and the explicit go/no-go decision with named approvers.

## Scope

- **In scope:** scope tested, results summary, defect & risk posture, recommendation,
  approvals.
- **Out of scope:** the detailed checklists and plans it references.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Authors the sign-off; makes the QA recommendation |
| Engineering Lead | Confirms build/quality-gate facts |
| Product Owner | Confirms UAT acceptance |
| Maintainer | Records the final Go / No-Go decision |

---

## Template (copy below this line)

```markdown
# QA Sign-Off — <release version> (<build SHA/tag>)

| Field | Value |
|-------|-------|
| Release version | <e.g. 1.0.0> |
| Build / commit | <develop@SHA / tag> |
| Environment validated | <qa | stage> |
| Test cycle(s) | <cycle ids> |
| Date | <YYYY-MM-DD> |
| Author (QA Lead) | <name> |

## 1. Scope tested
| Area | Covered | Notes |
|------|:-------:|-------|
| Auth & RBAC (BR-RBAC, BR-TENANT) | <Y/N> | <notes> |
| Patient & identity (BR-IDENT) | <Y/N> | <notes> |
| Appointments (BR-APPT) | <Y/N> | <notes> |
| Encounters / clinical (BR-ENC, BR-RX) | <Y/N> | <notes> |
| Billing / insurance (BR-BILL, BR-INS) | <Y/N> | <notes> |
| FHIR / error contract | <Y/N> | <notes> |
| Audit & consent (BR-AUDIT, BR-CONS) | <Y/N> | <notes> |
| Notifications (BR-NOTIF) | <Y/N> | <notes> |

## 2. Results summary
| Metric | Value |
|--------|-------|
| Test cases planned / executed | <n> / <n> |
| Passed | <n> |
| Failed | <n> |
| Blocked / not run | <n> |
| Pass rate | <%> |
| Smoke suite | <Pass/Fail> |
| Regression suite | <Pass/Fail> |
| UAT (Must-pass accepted) | <n>/<n> |
| RTM critical-requirement coverage | <%> |

## 3. Open defects by severity
| Severity | Open count | IDs | Plan (fix / defer / accept) |
|----------|:----------:|-----|-----------------------------|
| S1 Critical | <n> | <ids> | <must be 0 to ship> |
| S2 High | <n> | <ids> | <fix or approved deferral> |
| S3 Medium | <n> | <ids> | <scheduled> |
| S4 Low | <n> | <ids> | <backlog> |

## 4. Risks accepted (residual)
| Risk ID | Residual exposure | Rationale | Compensating control | Approved by |
|---------|-------------------|-----------|----------------------|-------------|
| <RR-NN> | <band> | <why acceptable> | <control> | <Maintainer> |

> No Critical (9) or unmitigated High (6) healthcare/security risk, and no open
> S1 defect, may be accepted for a v1.0.0 release.

## 5. Quality gate status
| Gate | Status |
|------|--------|
| Build green | <Pass/Fail> |
| Coverage threshold | <Pass/Fail> |
| Lint / format / SonarQube (no critical) | <Pass/Fail> |
| Dependency / CVE scan | <Pass/Fail> |
| Flyway migrations validated | <Pass/Fail> |
| Deployment validation | <Pass/Fail> |
| Production verification | <Pass/Fail> |

## 6. Go / No-Go recommendation
**QA recommendation:** <GO | NO-GO | GO WITH CONDITIONS>
**Conditions (if any):** <list>
**Rationale:** <summary tied to §3–§5>

## 7. Approvals
| Role | Name | Decision | Date | Signature |
|------|------|----------|------|-----------|
| QA Lead | <name> | <Go/No-Go> | <date> | <sig> |
| Engineering Lead | <name> | <Go/No-Go> | <date> | <sig> |
| Product Owner | <name> | <Go/No-Go> | <date> | <sig> |
| Maintainer (final) | <name> | <Go/No-Go> | <date> | <sig> |
```

---

## Decision rules

- **NO-GO** if any open **S1**, any failing smoke/critical gate, or any
  unmitigated Critical/High healthcare-security risk.
- **GO WITH CONDITIONS** only when remaining items are S2/S3 with approved
  deferrals and recorded residual risk.
- **GO** only when §3 shows zero open S1, §4 risks are approved, and §5 gates pass.

## Examples

A candidate with OMII-BUG-0008 (cross-tenant, S1) **Open** is **NO-GO**; once it
is Verified and RR-04 leaves `Open`, sign-off can move to GO.

## Future Enhancements

- Auto-populate §2/§3/§5 from the tracker and CI at tag time (M8).

## Dependencies

- [../release/RELEASE_CHECKLIST.md](../release/RELEASE_CHECKLIST.md),
  [../release/PRODUCTION_VERIFICATION_CHECKLIST.md](../release/PRODUCTION_VERIFICATION_CHECKLIST.md),
  [../uat/UAT_PLAN.md](../uat/UAT_PLAN.md),
  [../risk-analysis/RISK_MATRIX.md](../risk-analysis/RISK_MATRIX.md),
  [../bug-templates/SEVERITY_PRIORITY_MATRIX.md](../bug-templates/SEVERITY_PRIORITY_MATRIX.md).

## References

- [../../VERSIONING.md](../../VERSIONING.md),
  [../../MASTER_PROJECT_SPECIFICATION.md](../../MASTER_PROJECT_SPECIFICATION.md) §5 quality gates.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
