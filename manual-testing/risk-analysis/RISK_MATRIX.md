# Risk Matrix & Risk-Based Prioritization

> **The probability×impact heat matrix for omiiCARE_QA and the rules that turn
> exposure into test effort.** It visualizes the [RISK_REGISTER.md](RISK_REGISTER.md),
> maps each band to test depth/frequency/earliness, and defines how residual risk
> is accepted at a release gate.

## Purpose

Make risk visible and actionable: show where every risk sits, concentrate testing
on the upper-right cells, and govern residual-risk acceptance explicitly.

## Scope

- **In scope:** the heat matrix, exposure→prioritization mapping, and residual-risk
  acceptance.
- **Out of scope:** the per-risk register rows (see [RISK_REGISTER.md](RISK_REGISTER.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Maintains the matrix; proposes residual acceptance |
| QA Architect | Confirms test depth matches each cell |
| Maintainer | Approves residual-risk acceptance at the gate |

---

## 1. Scoring recap

Exposure = Probability × Impact. Bands: **1–2 Low · 3–4 Medium · 6 High · 9 Critical.**

## 2. Probability × Impact heat matrix

Risk IDs reference [RISK_REGISTER.md](RISK_REGISTER.md). Each cell shows its
exposure band.

| Probability ↓ \ Impact → | **Low (1)** | **Medium (2)** | **High (3)** |
|---------------------------|-------------|----------------|--------------|
| **High (3)** | — *(Low, 3)* | RR-17 — **High (6)** | RR-01 — **Critical (9)** |
| **Medium (2)** | — *(Med, 2)* | RR-09 RR-10 RR-11 RR-13 RR-14 RR-15 RR-19 — **Medium (4)** | RR-02 RR-03 RR-04 RR-05 RR-08 RR-16 RR-18 — **High (6)** |
| **Low (1)** | — *(Low, 1)* | — *(Med, 2)* | RR-06 RR-07 RR-12 RR-20 — **Medium (3)** |

### Heat key

| Band | Cells | Meaning |
|------|-------|---------|
| **Critical (9)** | High×High | Highest priority — exhaustive, shift-left, every commit |
| **High (6)** | High×Med, Med×High | Deep coverage, early, every cycle |
| **Medium (3–4)** | Med×Med, Low×High | Representative functional + targeted negative |
| **Low (1–2)** | Low/Med low cells | Smoke + sampled coverage |

**Priority zone (deepest + earliest testing):** RR-01 (Critical) and the High band
RR-02, RR-03, RR-04, RR-05, RR-08, RR-16, RR-17, RR-18.

## 3. How risk drives test prioritization

| Exposure band | Test depth | Frequency | Earliness | Suite placement |
|---------------|-----------|-----------|-----------|-----------------|
| **Critical (9)** | Unit→E2E + specialized; exhaustive boundary/negative | Every commit + every cycle | Shift-left to design | Smoke **and** regression |
| **High (6)** | Unit→E2E with focused specialized tests | Every cycle | Early in milestone | Smoke (key) + regression |
| **Medium (3–4)** | Representative functional + targeted negative | Per cycle / per change | Standard | Regression |
| **Low (1–2)** | Smoke-level + sampled coverage | Periodic | As scheduled | Sampled |

- High-exposure healthcare/security risks (RR-01–RR-05, RR-08) appear as
  **Critical-priority rows in the RTM** and seed both smoke and regression suites.
- Risk status changes (e.g. RR-04 moving Open → Mitigating) re-balance effort each
  cycle via the risk review in [RISK_REGISTER.md](RISK_REGISTER.md) §4.

## 4. Healthcare emphasis

| Concern | Dominant risk(s) | Why it gets the deepest testing |
|---------|------------------|---------------------------------|
| Wrong-patient | RR-01 | Direct patient-safety failure |
| Data isolation / privacy | RR-04, RR-12 | PHI exposure is the highest-stakes failure |
| Access control | RR-03, RR-13 | Auth defeat exposes all data |
| Audit completeness | RR-05 | Accountability and compliance posture |
| Clinical safety logic | RR-06 | Drug-allergy hard stop is life-safety |
| Financial integrity | RR-08 | Money and trust |

## 5. Residual-risk acceptance

- Any risk not fully mitigated at a release gate is recorded with its **residual
  exposure** and a written rationale.
- The **QA Lead** proposes acceptance; the **Maintainer** approves; both are
  recorded in [RISK_REGISTER.md](RISK_REGISTER.md) (status `Accepted`) and
  referenced in [../signoff/SIGNOFF_TEMPLATE.md](../signoff/SIGNOFF_TEMPLATE.md).
- **Hard rule:** no **Critical (9)** and no **unmitigated High (6)**
  healthcare/security risk may be accepted for a **v1.0.0** release. Open S1
  defects tied to such risks are release blockers
  ([../bug-templates/DEFECT_LIFECYCLE.md](../bug-templates/DEFECT_LIFECYCLE.md) §5).

### Residual-risk record (template)

```markdown
| Risk ID | Residual exposure | Rationale | Compensating control | Proposed by | Approved by | Date |
|---------|-------------------|-----------|----------------------|-------------|-------------|------|
| RR-NN   | <band>            | <why acceptable> | <control in place> | QA Lead | Maintainer | YYYY-MM-DD |
```

## Examples

- RR-14 (page-size cap, Medium) showing a minor non-clinical endpoint over budget
  may be proposed for residual acceptance with a remediation ticket; the
  Maintainer signs off and it is recorded.
- RR-01 (Critical) can **never** be accepted as residual for v1.0.0.

## Future Enhancements

- Auto-render this matrix from the register and chart exposure burn-down.
- Live coverage-to-risk heatmap joined to the RTM.

## Dependencies

- [RISK_REGISTER.md](RISK_REGISTER.md),
  [../../docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md).
- [../signoff/SIGNOFF_TEMPLATE.md](../signoff/SIGNOFF_TEMPLATE.md),
  [../bug-templates/DEFECT_LIFECYCLE.md](../bug-templates/DEFECT_LIFECYCLE.md).

## References

- ISO 31000; ISTQB risk-based testing.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
