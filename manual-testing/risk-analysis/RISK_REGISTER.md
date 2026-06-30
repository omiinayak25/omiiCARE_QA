# Risk Register (Living)

> **The living risk register for omiiCARE_QA, owned from Milestone 6 onward.** It
> extends the Milestone-1 documentation register at
> [../../docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md) into an actively
> maintained, status-tracked artifact. Each risk carries probability, impact,
> exposure, mitigation, contingency, owner, and current status. Exposure drives
> test prioritization in [RISK_MATRIX.md](RISK_MATRIX.md).

## Purpose

Maintain a current, reviewable inventory of technical, business, compliance,
security, and schedule risks so testing concentrates where it protects patients,
data, and the release most, and so residual risk is an explicit, signed decision.

## Scope

- **In scope:** the register, scoring model, status, and review cadence.
- **Out of scope:** the heat matrix and prioritization mechanics (see
  [RISK_MATRIX.md](RISK_MATRIX.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Owns the register; runs the risk review each cycle |
| QA Architect | Validates test depth matches exposure |
| SDET III | Mitigates technical/integration risks via test design |
| Maintainer | Approves residual-risk acceptance |

---

## 1. Scoring model

- **Probability:** Low (1), Medium (2), High (3).
- **Impact:** Low (1), Medium (2), High (3).
- **Exposure = Probability × Impact** (1–9). Bands: 1–2 Low · 3–4 Medium · 6 High · 9 Critical.
- **Status:** Open · Mitigating · Monitored · Accepted (residual) · Closed.

## 2. Register

| Risk ID | Description | Category | Prob | Impact | Exposure | Mitigation | Contingency | Owner | Status |
|---------|-------------|----------|:----:|:------:|----------|------------|-------------|-------|--------|
| RR-01 | Wrong-patient association (data shown for the wrong patient) | Compliance/Safety | H | H | 9 (Critical) | Patient-context tests unit→E2E; tenant-scope checks; mandatory S1 grading | Block release; hotfix + targeted regression | QA Lead | Mitigating |
| RR-02 | Double-booking accepted (provider overlap) (BR-APPT-003) | Business | M | H | 6 (High) | Boundary/negative overlap tests incl. exact-boundary; half-open interval rule | Freeze booking path; re-verify | QA Architect | Mitigating |
| RR-03 | Login / RBAC bypass (auth defeat) (BR-RBAC-001) | Security | M | H | 6 (High) | Authz tests across all 12 roles; negative-token tests; default-deny checks | Suspend cycle; security review | QA Architect | Mitigating |
| RR-04 | Cross-tenant data leak (BR-TENANT-002) | Security/Compliance | M | H | 6 (High) | Cross-tenant read/write denial tests; assert `CROSS_TENANT_DENIED` + audit | Block release; patch tenant filter | QA Lead | Open |
| RR-05 | Missing/incomplete audit log on PHI action (BR-AUDIT-001/002) | Compliance | M | H | 6 (High) | Audit-side-effect assertions in integration tests | Add audit hooks; backfill tests | SDET III | Mitigating |
| RR-06 | Drug–allergy hard stop bypassed (BR-RX-002) | Compliance/Safety | L | H | 3 (Medium) | Contraindication negative datasets; override-audit assertions | Disable dispense path; escalate | QA Architect | Monitored |
| RR-07 | Consent not enforced before data access (BR-CONS-001/002) | Compliance | L | H | 3 (Medium) | Consent-gate tests in patient/clinical flows | Add gate; regression sweep | QA Lead | Monitored |
| RR-08 | Billing/claim total or adjudication error (BR-BILL-003/004) | Business | M | H | 6 (High) | Boundary + deterministic total/balance/overpayment tests | Freeze billing module; re-verify | SDET III | Mitigating |
| RR-09 | Refresh-token replay (token not rotated) | Security | M | M | 4 (Medium) | Rotation tests; reuse rejection; replay audit assertion | Force rotation; revoke sessions | QA Architect | Open |
| RR-10 | FHIR resource fails R4 schema / wrong code-system URI (BR-IDENT-002) | Technical | M | M | 4 (Medium) | Contract + schema validation tests; terminology checks | Fix mapping; re-validate bundles | SDET III | Mitigating |
| RR-11 | HL7 v2 segment corruption on parse/build | Technical | M | M | 4 (Medium) | Contract tests on ADT/ORM/ORU samples | Quarantine message path | SDET III | Monitored |
| RR-12 | Real PHI accidentally introduced into a dataset (BR-CONS-005) | Compliance | L | H | 3 (Medium) | Synthetic-only policy; PR review; obviously-fake IDs | Purge data; rotate; root-cause | QA Architect | Monitored |
| RR-13 | Error contract leaks internals (SQL/stack in Problem Details) | Security | M | M | 4 (Medium) | Negative tests asserting sanitized RFC 7807 bodies | Sanitize handler; re-test | SDET III | Open |
| RR-14 | Pagination/limit not enforced (resource exhaustion) | Technical | M | M | 4 (Medium) | Tests for `size` cap (100) and whitelisted params | Apply cap; load re-check | SDET III | Open |
| RR-15 | Environment instability blocks a test cycle (Docker services unhealthy) | Technical | M | M | 4 (Medium) | Health checks as entry criteria; re-seed scripts | Suspend cycle; restore env | QA Lead | Monitored |
| RR-16 | Flyway migration failure / drift between envs | Technical | M | H | 6 (High) | Migration dry-run in CI; versioned datasets regenerated on change | Roll back image; restore DB | SDET III | Mitigating |
| RR-17 | Flaky UI/E2E tests erode trust in the suite | Technical | H | M | 6 (High) | Pyramid discipline; quarantine + root-cause | Push checks down the pyramid | QA Lead | Mitigating |
| RR-18 | Coverage gap on a critical requirement | Business | M | H | 6 (High) | RTM reviewed at every exit gate | Add tests before gate | QA Lead | Mitigating |
| RR-19 | Dependency CVE introduced via new library | Security | M | M | 4 (Medium) | Dependency-Check in CI; review before adoption | Pin/upgrade; security scan | SDET III | Monitored |
| RR-20 | Rollback/restore does not actually recover state | Technical | L | H | 3 (Medium) | Rehearse rollback + DB restore on owned infra | Document gap; remediate before go-live | QA Architect | Open |

## 3. Status summary

| Status | Count | IDs |
|--------|:-----:|-----|
| Open | 5 | RR-04, RR-09, RR-13, RR-14, RR-20 |
| Mitigating | 9 | RR-01, RR-02, RR-03, RR-05, RR-08, RR-10, RR-16, RR-17, RR-18 |
| Monitored | 6 | RR-06, RR-07, RR-11, RR-12, RR-15, RR-19 |
| Accepted (residual) | 0 | — |
| Closed | 0 | — |

> No Critical (9) or unmitigated High (6) healthcare/security risk may be
> accepted for a v1.0.0 release ([RISK_MATRIX.md](RISK_MATRIX.md) §residual).

## 4. Review cadence

| Forum | Frequency | Output |
|-------|-----------|--------|
| Risk review | Each test cycle | Re-score, update status, re-prioritize tests |
| Pre-gate risk check | Before milestone / UAT / release gate | Confirm no open Critical/High safety-security risk; residual-risk decisions |

## Examples

- RR-01 (wrong-patient, Critical) is tested at unit, integration, API, and E2E and
  sits in smoke + regression — the deepest coverage in the suite.
- RR-04 (cross-tenant leak) is currently **Open** and traced to a live S1 defect
  ([../bug-reports/SAMPLE_DEFECTS.md](../bug-reports/SAMPLE_DEFECTS.md) OMII-BUG-0008).

## Future Enhancements

- Auto-compute exposure trends across cycles and chart risk burn-down.
- Join the register to the RTM for a live coverage-to-risk heatmap.

## Dependencies

- [RISK_MATRIX.md](RISK_MATRIX.md),
  [../../docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md),
  [../../docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md).
- Release-gate use in [../release/RELEASE_CHECKLIST.md](../release/RELEASE_CHECKLIST.md)
  and [../signoff/SIGNOFF_TEMPLATE.md](../signoff/SIGNOFF_TEMPLATE.md).

## References

- ISO 31000; ISTQB risk-based testing; [../../SECURITY.md](../../SECURITY.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
