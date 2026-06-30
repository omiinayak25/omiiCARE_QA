# Risk Analysis & Risk-Based Testing

> **Risk register and prioritization model.** This document identifies what could
> go wrong in omiiCARE_QA, scores each risk by exposure, and uses that exposure to
> drive where testing goes deepest. It directs the prioritization promised in the
> [Test Strategy](TEST_STRATEGY.md) §10 and the [Master Test Plan](MASTER_TEST_PLAN.md)
> §12, and overlays the coverage in the [RTM](RTM.md). Facts defer to
> [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

- Maintain a register of technical, business, compliance, security, and schedule
  risks, each with a mitigation, contingency, and owner.
- Score risks by exposure (probability × impact) and visualize them on a matrix.
- Translate exposure into test prioritization so effort concentrates where it
  protects patients, data, and the release most.
- Record residual-risk acceptance explicitly.

This is **documentation only** in Milestone 1; the living risk register is
maintained under `manual-testing/` from Milestone 6.

## Scope

- **In scope:** risk register; probability×impact matrix; healthcare-specific
  risks; how risk drives prioritization; residual-risk acceptance.
- **Out of scope:** per-defect risk tracking (Milestone 6) and incident response
  (operational, post-1.0).

## Responsibilities

| Role | Risk responsibility |
|------|---------------------|
| QA Lead | Owns the register; runs risk reviews each cycle |
| QA Architect | Validates that test depth matches exposure |
| SDET III | Mitigates technical/integration risks via test design |
| Senior QA Engineer | Probes high-exposure areas with exploratory testing |
| Maintainer | Approves residual-risk acceptance |

---

## 1. Scoring Model

- **Probability:** Low (1), Medium (2), High (3).
- **Impact:** Low (1), Medium (2), High (3).
- **Exposure score = Probability × Impact** (range 1–9).
- **Bands:** 1–2 Low · 3–4 Medium · 6 High · 9 Critical.

Exposure determines test depth, frequency, and earliness (§4).

## 2. Risk Register

| Risk ID | Description | Category | Prob | Impact | Exposure | Mitigation | Contingency | Owner |
|---------|-------------|----------|------|--------|----------|------------|-------------|-------|
| R-01 | Wrong-patient association (data shown for the wrong patient) | Compliance | M | H | 6 (High) | Patient-context tests at unit→E2E; tenant scoping checks | Block release; hotfix + targeted regression | QA Lead |
| R-02 | Undetected drug-interaction allows unsafe dispense | Compliance | M | H | 6 (High) | Interaction-rule tests; negative/boundary datasets | Disable dispense path; escalate | QA Architect |
| R-03 | Billing/claim total or adjudication error | Business | M | H | 6 (High) | Boundary + deterministic data tests on totals/adjustments | Freeze billing module; re-verify | SDET III |
| R-04 | Missing/incomplete audit log on PHI-relevant action | Compliance | M | H | 6 (High) | Integration tests assert audit side-effects | Add audit hooks; backfill tests | SDET III |
| R-05 | Broken access control / RBAC bypass | Security | M | H | 6 (High) | Security + API authz tests across all roles | Suspend cycle; security review | QA Architect |
| R-06 | Consent not enforced before data access | Compliance | L | H | 3 (Medium) | Consent-gate tests in patient/clinical flows | Add gate; regression sweep | QA Lead |
| R-07 | FHIR resource fails R4 schema / wrong code-system URI | Technical | M | M | 4 (Medium) | Contract + schema validation tests | Fix mapping; re-validate bundles | SDET III |
| R-08 | HL7 v2 segment corruption on parse/build | Technical | M | M | 4 (Medium) | Contract tests on ADT/ORM/ORU samples | Quarantine message path | SDET II |
| R-09 | Real PHI accidentally introduced into a dataset | Compliance | L | H | 3 (Medium) | Synthetic-only policy; PR review; obviously-fake IDs | Purge data; rotate; root-cause | QA Architect |
| R-10 | Flaky UI/E2E tests erode trust in the suite | Technical | H | M | 6 (High) | Pyramid discipline; quarantine + root-cause | Push checks down the pyramid | QA Lead |
| R-11 | Public adapter target (OpenMRS/HAPI FHIR/etc.) unavailable | Technical | H | L | 3 (Medium) | Adapter abstraction; WireMock/Local fallback | Switch adapter via config | SDET III |
| R-12 | Environment instability blocks a test cycle | Technical | M | M | 4 (Medium) | Health checks as entry criterion; re-seed | Suspend per plan §7; restore | QA Lead |
| R-13 | Synthetic data drifts from evolving schema | Technical | M | M | 4 (Medium) | Versioned datasets regenerated on Flyway change | Regenerate + re-baseline | SDET II |
| R-14 | Accessibility regressions (WCAG AA) ship unnoticed | Business | M | M | 4 (Medium) | axe/Lighthouse in CI; a11y band | Block release on serious violations | SDET II |
| R-15 | Performance regression beyond budget | Technical | M | M | 4 (Medium) | Perf smoke on owned infra; budgets per endpoint | Investigate; gate release | SDET III |
| R-16 | Localization/RTL defects in portals | Business | L | M | 2 (Low) | l10n datasets; locale test matrix | Patch + targeted regression | SDET II |
| R-17 | Coverage gap on a critical requirement | Business | M | H | 6 (High) | RTM reviewed at every exit gate | Add tests before gate | QA Lead |
| R-18 | Upstream milestone slip compresses test time | Schedule | M | M | 4 (Medium) | Risk-weighted scheduling; protect critical path | Re-sequence; defer low-risk areas | QA Lead |
| R-19 | Dependency CVE introduced via new library | Security | M | M | 4 (Medium) | Dependency-Check in CI; review before adoption | Pin/upgrade; security scan | SDET III |
| R-20 | Recovery/failover not actually restoring state | Technical | L | H | 3 (Medium) | Recovery tests on `dr`/owned infra | Document gap; remediate | QA Architect |

## 3. Probability × Impact Matrix

```
            IMPACT →      Low (1)         Medium (2)              High (3)
 PROBABILITY
   High (3)            R-11              R-10                    (none) 
                       [3 Med]           [6 High]
   ----------------------------------------------------------------------
   Medium (2)          (none)            R-07 R-08 R-12 R-13     R-01 R-02 R-03
                                         R-14 R-15 R-18 R-19     R-04 R-05 R-17
                                         [4 Med]                 [6 High]
   ----------------------------------------------------------------------
   Low (1)             R-16              (none)                  R-06 R-09 R-20
                       [2 Low]                                   [3 Med]
```

Cells in the upper-right and the High-probability/Medium-impact cell are the
priority zone: R-01–R-05, R-10, and R-17 receive the deepest and earliest testing.

## 4. How Risk Drives Test Prioritization

| Exposure band | Test depth | Frequency | Earliness |
|---------------|------------|-----------|-----------|
| Critical (9) | Unit→E2E + specialized; exhaustive boundary/negative | Every commit + every cycle | Shift-left to design |
| High (6) | Unit→E2E with focused specialized tests | Every cycle | Early in milestone |
| Medium (3–4) | Representative functional + targeted negative | Per cycle / per change | Standard |
| Low (1–2) | Smoke-level + sampled coverage | Periodic | As scheduled |

High-exposure healthcare risks (R-01–R-05) are placed in both the **smoke** and
**regression** suites and appear as Critical-priority rows in the [RTM](RTM.md).

## 5. Healthcare-Specific Risks (Emphasis)

| Concern | Why it dominates | Primary controls |
|---------|------------------|------------------|
| Data privacy | PHI exposure is the highest-stakes failure | Synthetic-only data, audit logging, access control |
| Wrong-patient | Directly endangers care | Patient-context integrity tests at every level |
| Drug interactions | Safety-critical clinical logic | Interaction-rule tests + negative datasets |
| Billing errors | Financial and trust damage | Boundary + deterministic total/adjudication tests |
| Consent | Legal/ethical gate on data use | Consent-gate enforcement tests |
| Audit gaps | Undermines accountability and compliance posture | Audit-side-effect assertions in integration tests |

## 6. Residual Risk Acceptance

- Any risk not fully mitigated at a release gate is documented with its residual
  exposure and a rationale.
- The **QA Lead** proposes acceptance; the **Maintainer** approves; both are
  recorded in the living register.
- No Critical (9) or unmitigated High (6) healthcare/security risk may be
  accepted for a v1.0.0 release.

## Examples

- *Depth from exposure:* R-01 (wrong-patient, High) is tested at unit,
  integration, API, and E2E levels and sits in smoke + regression — far more than
  R-16 (localization, Low), which gets sampled coverage.
- *Residual acceptance:* If R-15 (perf regression) shows a minor, non-clinical
  endpoint slightly over budget, the QA Lead may propose acceptance with a
  remediation ticket; the Maintainer signs off and it is recorded.

## Future Enhancements

- Auto-compute exposure trends across cycles and chart risk burn-down.
- Join the register to the [RTM](RTM.md) for a live coverage-to-risk heatmap.
- AI-assisted risk identification from requirement and defect history (M9).

## Dependencies

- Directs prioritization in [TEST_STRATEGY.md](TEST_STRATEGY.md) §10 and
  [MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md) §12.
- Overlays coverage in [RTM.md](RTM.md).
- Living register delivered in Milestone 6 (`manual-testing/`).

## References

- ISO 31000 risk-management principles; ISTQB risk-based testing.
- [SECURITY.md](../SECURITY.md), [ARCHITECTURE.md](../ARCHITECTURE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial risk register, matrix, and risk-based testing model (Milestone 1) |
