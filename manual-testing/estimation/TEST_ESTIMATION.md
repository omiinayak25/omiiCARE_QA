# Test Estimation — omiiCARE_QA v1.0 Manual Testing (Milestone 6)

> **Sizing-based test estimation** for the manual verification of the implemented
> omiiCARE_QA surface. It uses a **Test-Case Point (TCP)** sizing model combined
> with a **3-point (PERT)** estimate to produce a defensible effort total for the
> modules in [REQUIREMENTS.md](../requirements/REQUIREMENTS.md) and the cases in the
> [RTM](../rtm/RTM.md).

## 1. Method

### 1.1 Test-Case Point (TCP) sizing

Each manual case is sized by complexity, which sets a TCP weight:

| Complexity | Drivers | TCP weight | Base minutes/case |
|------------|---------|------------|-------------------|
| Simple | Single endpoint/screen, 1 assertion, no precondition chain | 2 | 15 |
| Medium | Multi-step, negative/boundary, ProblemDetail assertions, auth precondition | 4 | 30 |
| Complex | E2E journey, RBAC matrix, FHIR conformance, multi-entity setup | 8 | 60 |

Effort per case = base minutes × productivity factor. Minutes cover design review,
execution, evidence capture, and result logging (not first-time authoring of the
framework).

### 1.2 3-point (PERT) estimate

For each module: **Expected = (Optimistic + 4×Most-Likely + Pessimistic) / 6**,
in person-hours, derived from the TCP roll-up.

## 2. Assumptions

| # | Assumption |
|---|------------|
| A1 | Scope is the **implemented** surface only; **(Future)** requirements excluded. |
| A2 | Environments (`test`, `qa`) are healthy and PHI-safe-seeded before execution. |
| A3 | One full execution cycle plus one regression re-run is included. |
| A4 | Demo credentials and seed data are available; no environment build effort here. |
| A5 | Productivity factor 1.2 applied for evidence capture + RTM update overhead. |
| A6 | Defect-fix verification budgeted at 15% of execution effort (re-test allowance). |
| A7 | A11y and Perf manual cases are scoped as smoke-level (sampled), not exhaustive. |

## 3. Case Inventory & Sizing

| Module | Manual cases | Simple | Medium | Complex | TCP total |
|--------|--------------|--------|--------|---------|-----------|
| Auth (TC-AUTH-001..008) | 8 | 2 | 5 | 1 | (2×2)+(5×4)+(1×8)=32 |
| Security/RBAC (TC-SEC-001..007) | 7 | 0 | 5 | 2 | (5×4)+(2×8)=36 |
| Patients (TC-PAT-001..010) | 10 | 2 | 6 | 2 | (2×2)+(6×4)+(2×8)=44 |
| Appointments (TC-APPT-001..009) | 9 | 1 | 6 | 2 | (1×2)+(6×4)+(2×8)=42 |
| FHIR (TC-FHIR-001..006) | 6 | 1 | 3 | 2 | (1×2)+(3×4)+(2×8)=30 |
| A11y (TC-A11Y-001..002) | 2 | 0 | 1 | 1 | (1×4)+(1×8)=12 |
| Perf smoke (TC-PERF-001..002) | 2 | 0 | 2 | 0 | (2×4)=8 |
| **Total** | **44** | 6 | 28 | 10 | **204 TCP** |

## 4. Effort From TCP (base hours)

Base minutes by weight: Simple 15, Medium 30, Complex 60.

| Module | Simple min | Medium min | Complex min | Total min | Base hrs |
|--------|-----------|-----------|------------|-----------|----------|
| Auth | 30 | 150 | 60 | 240 | 4.0 |
| Security/RBAC | 0 | 150 | 120 | 270 | 4.5 |
| Patients | 30 | 180 | 120 | 330 | 5.5 |
| Appointments | 15 | 180 | 120 | 315 | 5.25 |
| FHIR | 15 | 90 | 120 | 225 | 3.75 |
| A11y | 0 | 30 | 60 | 90 | 1.5 |
| Perf smoke | 0 | 60 | 0 | 60 | 1.0 |
| **Subtotal** | | | | | **25.5 hrs** |

Apply productivity factor (A5 = 1.2): **25.5 × 1.2 = 30.6 base execution hours**.

## 5. 3-Point (PERT) Estimate per Module

Optimistic/Pessimistic derived as 0.8× and 1.5× the productivity-adjusted base.

| Module | Optimistic (h) | Most-Likely (h) | Pessimistic (h) | Expected (h) |
|--------|----------------|-----------------|-----------------|--------------|
| Auth | 3.8 | 4.8 | 7.2 | 5.0 |
| Security/RBAC | 4.3 | 5.4 | 8.1 | 5.7 |
| Patients | 5.3 | 6.6 | 9.9 | 6.9 |
| Appointments | 5.0 | 6.3 | 9.5 | 6.6 |
| FHIR | 3.6 | 4.5 | 6.8 | 4.7 |
| A11y | 1.4 | 1.8 | 2.7 | 1.9 |
| Perf smoke | 1.0 | 1.2 | 1.8 | 1.3 |
| **Total** | **24.4** | **30.6** | **46.0** | **32.1** |

*Expected = (O + 4×ML + P) / 6 per row.*

## 6. Total Estimate (with allowances)

| Component | Hours |
|-----------|-------|
| PERT expected execution | 32.1 |
| Defect re-test allowance (A6, 15%) | 4.8 |
| Exploratory / session-based (risk areas R-01/R-03/R-05) | 6.0 |
| Triage, RTM updates, sign-off prep | 4.0 |
| **Grand total** | **46.9 person-hours (~6 person-days)** |

| Roll-up | Value |
|---------|-------|
| Total manual cases | 44 |
| Total TCP | 204 |
| Expected execution (PERT) | 32.1 h |
| Grand total with allowances | 46.9 h (~6 person-days) |

With two Senior QA Engineers at ~6 effective hours/day, the cycle fits comfortably
inside a two-week sprint with headroom for re-runs (cf.
[SPRINT_TEST_PLAN.md](../test-plan/SPRINT_TEST_PLAN.md) §4).

## 7. Confidence & Re-Estimation Triggers

| Trigger | Action |
|---------|--------|
| New implemented endpoint added | Add cases; re-size that module's TCP |
| Defect leakage > 5% | Increase re-test allowance from 15% |
| A **(Future)** requirement promoted to implemented | Move its rows in-scope and re-estimate |
| >40% case failure (suspension) | Re-baseline and re-run; treat as a fresh cycle |

## Dependencies

- [../requirements/REQUIREMENTS.md](../requirements/REQUIREMENTS.md),
  [../rtm/RTM.md](../rtm/RTM.md),
  [../test-plan/MASTER_TEST_PLAN.md](../test-plan/MASTER_TEST_PLAN.md),
  [../test-plan/SPRINT_TEST_PLAN.md](../test-plan/SPRINT_TEST_PLAN.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
