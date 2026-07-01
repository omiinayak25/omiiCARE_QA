# Risk-Based Testing Strategy

> **Purpose.** Define how this healthcare QA portfolio allocates finite test
> effort against the risks that threaten **patient safety, PHI/security, data
> integrity, interoperability, performance, and compliance**. This strategy
> converts the scored inventory in
> [`docs/reverse-engineering/RISK_REGISTER.md`](../reverse-engineering/RISK_REGISTER.md)
> into concrete test prioritization, depth, and coverage targets.
>
> **System under test.** Primary: **OpenMRS** (`o2.openmrs.org` legacy O2 RefApp;
> O3 demo at `o3.openmrs.org`). Portable to **OpenEMR, HAPI FHIR, SMART Health IT**,
> and the in-house **omiiCARE** app via the **Resource Adapter Layer (RAL)**.
>
> **Anchored to.** 472 requirements (`REQ-<PREFIX>-NNN`,
> [`requirements-catalog.md`](../requirements/requirements-catalog.md)),
> 1,349+ manual test cases across 21 modules
> ([`manual-testing/test-cases/openmrs/`](../../manual-testing/test-cases/openmrs/)),
> the RTM ([`manual-testing/rtm/RTM.csv`](../../manual-testing/rtm/RTM.csv)),
> and the 30-risk register.
>
> **Standards.** FHIR R4, HL7 v2, WCAG 2.1 AA, OWASP (ASVS / Top 10), HIPAA-like
> Privacy & Security controls.
>
> **Provenance.** Facts grounded in verified OpenMRS RefApp behavior are stated
> plainly. Any threshold, weighting constant, or coverage target beyond observed
> behavior is tagged **(Assumption)** — a portfolio design decision, tunable per
> deployment.
>
> **Ethics / scope guard.** Performance and security tests run **ONLY against
> owned or local environments** (local OpenMRS SDK build, owned omiiCARE instance,
> self-hosted HAPI). They are **never** executed against public demo sites
> (`o2/o3.openmrs.org`) or third-party hosts.

---

## 1. Why Risk-Based Testing (RBT)

Exhaustive testing is impossible: 472 requirements × multiple test-design
techniques × 5 target systems via the RAL produces a combinatorial space far
larger than any sprint budget. RBT answers three questions per release:

1. **Where will failure hurt most?** — patient harm, PHI breach, data loss.
2. **Where is failure most likely?** — fragile areas, recent change, config-dependent behavior.
3. **Given a fixed test budget, where does each marginal test-hour buy the most risk reduction?**

RBT makes those trade-offs explicit, defensible, and auditable. It is the bridge
between the **Risk Register** (what could go wrong, scored) and the **RTM**
(what we actually test, traced).

### 1.1 Inputs and outputs

| Input | Source | Output of this strategy |
|-------|--------|-------------------------|
| Scored risks (`RISK-<CAT>-NN`) | RISK_REGISTER.md | Risk → test mapping & depth |
| 472 requirements (`REQ-<PREFIX>-NNN`) | requirements-catalog.md | Per-requirement priority (P1–P4) |
| 1,349+ test cases (`TC-<PREFIX>-NNNN`) | manual-testing/test-cases | Coverage allocation by risk band |
| Module risk ratings | RTM.csv (`Risk` column) | Regression scope & execution order |
| Standards (FHIR/HL7/WCAG/OWASP/HIPAA) | reverse-engineering/* | Mandatory technique coverage |

---

## 2. Risk Identification

Risks are identified continuously from seven complementary sources, then logged
into the register with a `RISK-<CAT>-NN` ID. Categories: `SAFE` (clinical/patient
safety), `SEC` (security/PHI), `DATA` (data integrity), `INTEROP`
(interoperability), `PERF` (performance/availability), `COMP`
(compliance/regulatory), `PROJ` (project/delivery).

| # | Identification source | What it surfaces | Example risks |
|---|----------------------|------------------|---------------|
| 1 | Reverse-engineering analysis | Behavior gaps in BRD/SRS/FRD/use-cases | RISK-SAFE-01, RISK-SEC-02 |
| 2 | Clinical workflow review (FMEA-style) | Failure modes in care pathways | RISK-SAFE-02 wrong-patient |
| 3 | STRIDE / OWASP threat modeling | Auth, injection, PHI-leak vectors | RISK-SEC-01, RISK-SEC-05 |
| 4 | Data-model & ERD review | Duplicates, orphans, merge loss | RISK-DATA-01, RISK-DATA-02 |
| 5 | Interop conformance review | FHIR/HL7 mapping & code-system drift | RISK-INTEROP-01..03 |
| 6 | NFR & architecture review | Latency, availability, RAL divergence | RISK-PERF-01, RISK-INTEROP-04 |
| 7 | Compliance gap analysis | Audit trail, consent, retention | RISK-COMP-01..03 |

**FMEA lens for clinical risks.** Each patient-safety risk is described as a
*failure mode → effect → cause → detection gap*, which makes the test target
explicit (e.g., RISK-SAFE-03 vitals mis-capture → wrong dosing → missing
server-side range guard → tested by boundary + unit-conversion cases).

---

## 3. Likelihood / Impact Scoring

The portfolio reuses the register's two-axis model so that scores stay identical
across documents — **single source of truth = RISK_REGISTER.md**.

### 3.1 Base scoring axes (1–5)

| Axis | 1 | 2 | 3 | 4 | 5 |
|------|---|---|---|---|---|
| **Likelihood** | Rare | Unlikely | Possible | Likely | Almost certain |
| **Impact** | Negligible | Minor | Moderate | Major | Catastrophic (patient harm / PHI breach) |

**Likelihood anchors** (evidence used to choose the value): change frequency in
the area, config-dependence in the RefApp, historical defect density, complexity
of the data flow, and number of RAL targets the path crosses.

**Impact anchors:** worst-case clinical consequence, number of patients affected,
PHI records exposed, reversibility, and regulatory exposure.

### 3.2 Base exposure and bands

`Exposure = Likelihood × Impact` (range 1–25).

| Exposure | Band | Response | Test depth |
|----------|------|----------|------------|
| 20–25 | **Critical** | Mitigate now; release blocker | Exhaustive + adversarial + negative |
| 12–19 | **High** | Mitigate before GA; track weekly | Deep functional + boundary + security |
| 6–11 | **Medium** | Mitigate or accept with sign-off | Standard functional + key negatives |
| 1–5 | **Low** | Monitor; accept | Smoke / regression only |

### 3.3 Patient-safety weighting (this strategy's extension)

Base exposure under-ranks low-likelihood / catastrophic-harm risks (e.g., a rare
but lethal wrong-patient order). To make **patient safety and PHI first-class**,
the portfolio computes a **Weighted Risk Priority (WRP)** by applying a category
multiplier to base exposure. **(Assumption)** — multipliers are tunable.

| Category | Multiplier `w` | Rationale |
|----------|---------------|-----------|
| `SAFE` — patient safety | **1.5** | Irreversible harm; life-critical |
| `SEC` — security / PHI | **1.4** | Breach is irreversible; legal + trust |
| `COMP` — compliance | **1.2** | Regulatory exposure; audit defensibility |
| `DATA` — data integrity | **1.2** | Corrupts clinical record of truth |
| `INTEROP` — interoperability | **1.1** | Propagates errors to external systems |
| `PERF` — performance | **1.0** | Degradation, usually recoverable |
| `PROJ` — project | **0.9** | Delivery risk, not runtime harm |

`WRP = (Likelihood × Impact) × w`, rounded to one decimal.

**Catastrophe override.** Any risk with **Impact = 5** (patient harm or PHI
breach) is treated as **at least High** regardless of computed exposure — it can
never fall to Low/Medium on likelihood alone. This guarantees RISK-SAFE-04
(deceased-orderable, base 10) and RISK-DATA-03 (ID collision, base 10) receive
deep testing despite a "Medium" base band.

### 3.4 Worked example — re-ranking by WRP

| Risk | L | I | Base | Base band | `w` | **WRP** | Effective band |
|------|---|---|------|-----------|-----|---------|----------------|
| RISK-SEC-01 broken RBAC | 4 | 5 | 20 | Critical | 1.4 | **28.0** | Critical |
| RISK-SAFE-01 allergy miss | 3 | 5 | 15 | High | 1.5 | **22.5** | **Critical** ↑ |
| RISK-SAFE-02 wrong-patient | 3 | 5 | 15 | High | 1.5 | **22.5** | **Critical** ↑ |
| RISK-SEC-04 PHI in logs | 4 | 4 | 16 | High | 1.4 | **22.4** | **Critical** ↑ |
| RISK-COMP-01 audit gap | 3 | 5 | 15 | High | 1.2 | **18.0** | High |
| RISK-DATA-01 dup patient | 4 | 4 | 16 | High | 1.2 | **19.2** | High |
| RISK-SAFE-04 deceased-orderable | 2 | 5 | 10 | Medium | 1.5 | **15.0** | **High** ↑ (override) |
| RISK-PERF-01 search latency | 4 | 3 | 12 | High | 1.0 | **12.0** | High |
| RISK-PROJ-01 RAL drift | 3 | 3 | 9 | Medium | 0.9 | **8.1** | Medium |

Patient-safety weighting promotes three High risks (SAFE-01, SAFE-02, SEC-04)
into the Critical test tier and rescues SAFE-04 from Medium — exactly the
intent of safety-first QA.

---

## 4. Risk → Test Prioritization (P1–P4 Mapping)

Each requirement already carries a `Priority` (P1–P4) and a `Risk` rating in the
requirements catalog and RTM. This strategy defines the **canonical mapping**
between WRP bands, requirement priority, and test treatment.

### 4.1 Band ↔ priority ↔ treatment

| WRP band | Priority | Meaning | Gating | Execution cadence | Automation target |
|----------|----------|---------|--------|-------------------|-------------------|
| Critical (≥20) | **P1** | Release blocker | Hard gate — zero open defects | Every build + nightly | 100% automatable cases automated |
| High (12–19.9) | **P1/P2** | Must fix before GA | Gate — no open Critical/High defects | Daily regression | ≥80% **(Assumption)** |
| Medium (6–11.9) | **P3** | Fix or accept w/ sign-off | Soft gate — risk-accepted with QA sign-off | Per-release regression | ≥50% **(Assumption)** |
| Low (1–5.9) | **P4** | Monitor | No gate | Smoke / sampled | Best-effort |

### 4.2 Priority definitions used across the portfolio

| Priority | Definition | Defect SLA (fix-by) **(Assumption)** |
|----------|-----------|--------------------------------------|
| **P1** | Patient-safety, PHI/security, or core-clinical path; failure blocks release | Critical sev: same day; High sev: ≤2 days |
| **P2** | Important clinical/operational function; degraded but workable | ≤1 sprint |
| **P3** | Secondary function, edge handling, non-AA-blocking a11y | ≤2 sprints |
| **P4** | Cosmetic, rare-config, or informational | Backlog |

> **Consistency rule.** Where the catalog marks a requirement `P1 / High` (e.g.
> REQ-AUTH-005 lockout, REQ-APPT-004 double-booking, REQ-RBAC-*, all REQ-FHIR
> auth requirements), its linked test cases inherit the **highest** band of any
> risk they trace to. A test case never executes at a lower depth than the
> riskiest requirement it covers.

### 4.3 Mapping risks to modules and test techniques

| Risk (representative) | WRP band | Modules (prefix) | Mandatory techniques |
|-----------------------|----------|------------------|----------------------|
| RISK-SEC-01 RBAC | Critical/P1 | RBAC, SEC, AUTH, APPT | Decision-table (role×action), forced-browse, UI-vs-API parity |
| RISK-SAFE-01 allergy | Critical/P1 | CLIN, PHARM, ORDLAB, FHIR | Negative (no allergy), severity decision-table, FHIR AllergyIntolerance |
| RISK-SAFE-02 wrong-patient | Critical/P1 | SRCH, PDASH, VITAL, CLIN | Exploratory, state (context switch), near-duplicate boundary |
| RISK-SEC-04 PHI leak | Critical/P1 | SEC, COMP, all write paths | Log-redaction scan, URL/referer, error-page review (local only) |
| RISK-DATA-01 duplicates | High/P1 | REG, SRCH, DATA | Boundary (near-match), pairwise demographics, DB assertions |
| RISK-SAFE-03 vitals range | High/P1 | VITAL, CLIN | Boundary/equivalence, unit-conversion, impossible-value negative |
| RISK-INTEROP-01/02/03 | High/P2 | FHIR, HL7, INTEROP | FHIR R4 conformance, HL7 v2 segment, code-system URI checks |
| RISK-COMP-01 audit | High/P2 | DATA, RPT, SEC, COMP | State-transition (event→log), DB audit-row assertions |
| RISK-PERF-01 latency | High/P2 | SRCH, PDASH, PERF | Load/soak (local only), N+1 profiling |
| RISK-A11Y (WCAG) | High/P1–P2 | A11Y, all UI | Keyboard-only, screen-reader, contrast, reflow (WCAG 2.1 AA) |
| RISK-PROJ-01 RAL drift | Medium/P3 | DATA, all via RAL | Cross-adapter contract tests, golden-output diff |

---

## 5. Test Depth and Technique by Risk Band

Test depth = how many techniques and how many negative/boundary cases a
requirement receives.

| Band / Priority | Functional | Negative | Boundary | Decision-table | State-transition | Pairwise | Security | A11Y | API/FHIR/HL7 | DB | Exploratory |
|-----------------|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **Critical / P1** | ● | ● | ● | ● | ● | ● | ● | ● | ● | ● | ● |
| **High / P1–P2** | ● | ● | ● | ● | ● | ◐ | ● | ● | ● | ◐ | ◐ |
| **Medium / P3** | ● | ● | ◐ | ◐ | ◐ | ○ | ◐ | ◐ | ◐ | ○ | ○ |
| **Low / P4** | ◐ | ○ | ○ | ○ | ○ | ○ | ○ | ○ | ○ | ○ | ○ |

● mandatory ◐ where applicable ○ optional / sampled

**Mandatory regardless of band** (safety floor): every write-path requirement
gets at least one negative case and one authorization (RBAC) case; every PHI-bearing
endpoint gets an auth-required (401) test; every UI screen gets keyboard +
contrast checks (WCAG 2.1 AA).

---

## 6. Coverage Allocation by Risk

The portfolio deliberately **over-invests** in high-WRP areas. The target below
allocates the **growing test-case budget** (1,349 today → ~4,000 planned) by
effective band, not evenly across modules.

### 6.1 Target distribution of test-case effort **(Assumption — tunable)**

| Effective band | % of test-case budget | % of execution time | Coverage objective |
|----------------|:---:|:---:|--------------------|
| Critical / P1 | **40%** | 50% | 100% requirement + ≥90% branch/path of safety logic |
| High / P1–P2 | **35%** | 30% | 100% requirement coverage; key negatives + boundaries |
| Medium / P3 | **20%** | 15% | 100% requirement coverage; representative negatives |
| Low / P4 | **5%** | 5% | Smoke + sampled regression |

### 6.2 Coverage objectives by dimension

| Dimension | Critical/P1 | High | Medium | Low |
|-----------|:---:|:---:|:---:|:---:|
| Requirement coverage (RTM) | 100% | 100% | 100% | 100% |
| Negative-path coverage | 100% | ≥80% | ≥50% | sampled |
| Boundary coverage of numeric/clinical fields | 100% | ≥80% | key only | — |
| Security (OWASP) technique coverage | full | targeted | spot | — |
| FHIR R4 / HL7 v2 conformance | full | full | mapped fields | — |
| WCAG 2.1 AA criteria | AA full | AA full | AA core | AA core |

> **100% requirement coverage at every band** is non-negotiable — every
> `REQ-<PREFIX>-NNN` is traced to ≥1 test case in the RTM. Bands govern *depth*
> (how many techniques and negatives), not *whether* a requirement is tested.

### 6.3 Module heat → execution order

Modules inherit the highest WRP of their requirements. Regression executes in
descending WRP order so the riskiest areas fail fast:

`SEC/RBAC/AUTH → CLIN/PHARM/ORDLAB/VITAL (safety) → FHIR/HL7 → REG/SRCH/DATA →
APPT/VISIT/PDASH → RPT/BILL/NOTIF/TELE → A11Y/PERF (cross-cutting, gated separately)`.

---

## 7. Patient-Safety as a First-Class Gate

| Control | Rule |
|---------|------|
| **Safety hard-stop** | No release ships with an open Critical or High defect on any `RISK-SAFE-*` or `RISK-SEC-*` test. |
| **Catastrophe floor** | Any Impact = 5 risk is tested at ≥ High depth even if base band is Medium. |
| **PHI confidentiality** | Security/perf tests run on owned/local environments only; PHI in test data is synthetic; logs scanned for identifier leakage (RISK-SEC-04). |
| **Wrong-patient defense** | Patient-banner persistence + context-isolation cases (RISK-SAFE-02) are P1, run every build. |
| **Interop safety** | FHIR/HL7 mapping defects that could alter clinical meaning (units, code systems, allergy codes) are escalated one band (RISK-INTEROP-01/03). |

---

## 8. Re-Scoring Cadence and Governance

| Trigger | Action |
|---------|--------|
| New sprint / release | Re-review WRP for changed modules; re-sort regression order |
| Production / staging incident | Re-score affected risk; add regression cases; update register |
| New requirement added to catalog | Assign priority, map to risk(s), add RTM trace before "done" |
| New RAL target onboarded (OpenEMR/HAPI/SMART/omiiCARE) | Re-run cross-adapter contract suite; re-score RISK-INTEROP-04, RISK-PROJ-01 |
| Defect-density spike in an area | Raise Likelihood; re-band; deepen coverage |
| Quarterly | Full register review; recalibrate multipliers and budget split |

**Ownership.** QA Architect owns test-coverage for every risk; category owners
(CMO/SEC/DBA/INT/SRE/DPO/PM, per the register) own mitigation. A risk is **not
closeable** until its linked test cases pass at the required depth and the RTM
shows the trace.

---

## 9. Traceability Chain

```
RISK-<CAT>-NN  (RISK_REGISTER.md: L×I → band)
      │  × patient-safety weighting (this doc §3.3) → WRP → P1–P4 (§4)
      ▼
REQ-<PREFIX>-NNN  (requirements-catalog.md: Priority + Risk)
      ▼
TC-<PREFIX>-NNNN  (manual-testing/test-cases/: technique + depth per §5)
      ▼
RTM.csv  (coverage trace; gate evidence per §6)
```

Every link is auditable end-to-end: a regulator or clinical-safety officer can
start from any patient-safety risk and reach the exact passing test cases, or
start from a failing test and reach the risk it protects.

---

## 10. Cross-References

| Document | Role in RBT |
|----------|-------------|
| [`RISK_REGISTER.md`](../reverse-engineering/RISK_REGISTER.md) | Scored risk source of truth (30 risks, L×I, bands) |
| [`requirements-catalog.md`](../requirements/requirements-catalog.md) | 472 requirements with Priority + Risk |
| [`RTM.csv`](../../manual-testing/rtm/RTM.csv) | Requirement→test coverage evidence |
| [`NFR.md`](../reverse-engineering/NFR.md) | Performance/availability risk inputs |
| [`RBAC_MATRIX.md`](../reverse-engineering/RBAC_MATRIX.md) | Role×action source for RISK-SEC-01 tests |
| [`FHIR_MAPPING.md`](../reverse-engineering/FHIR_MAPPING.md) / [`HL7_MAPPING.md`](../reverse-engineering/HL7_MAPPING.md) | Interop conformance targets |
| [`ARCHITECTURE.md`](../reverse-engineering/ARCHITECTURE.md) | RAL design for cross-system risk |
