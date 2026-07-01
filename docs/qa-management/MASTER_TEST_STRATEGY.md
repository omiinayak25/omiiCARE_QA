# Master Test Strategy — OpenMRS-Primary Healthcare QA Portfolio

> **Highest-level quality blueprint** for the enterprise healthcare QA portfolio.
> Declares *why*, *what*, *at which levels*, *with which techniques and tools*,
> and *who owns* testing. Subordinate documents — the
> [Master Test Plan](../MASTER_TEST_PLAN.md), [Test Data Strategy](../TEST_DATA_STRATEGY.md),
> [Test Pyramid](../TEST_PYRAMID.md), [RTM](../../manual-testing/rtm/RTM.csv),
> [Risk Analysis](../RISK_ANALYSIS.md), and the QA-management series — each refine
> a slice of this strategy.
> Reference system facts defer to [reverse-engineering/*](../reverse-engineering/)
> and the [requirements catalog](../requirements/requirements-catalog.md).

- **Document ID:** QAM-MTS-001 · **Owner:** Principal QA Engineer / QA Architect
- **Status:** Baseline · **Date:** 2026-07-01 · **Review cadence:** per release train
- **SUT (primary):** OpenMRS reference application (`https://o2.openmrs.org`)
- **Portability targets:** OpenEMR, HAPI FHIR, SMART Health IT, in-house omiiCARE — via the **Resource Adapter Layer (RAL)**

---

## 1. Mission

Deliver **defensible, patient-safety-first quality assurance** for an OpenMRS-primary
healthcare platform, designed so the same test intent runs across multiple EHR/FHIR
backends through a single Resource Adapter Layer. Every test traces to a requirement
(`REQ-<PREFIX>-NNN`), every PHI touchpoint is synthetic and minimized, and every
release decision is backed by metrics rather than opinion.

Quality is treated as a **safety property**, not a feature: a defect that corrupts a
medication order, mis-maps a FHIR `code`, or leaks PHI is categorically more severe
than a cosmetic defect, and the strategy weights effort accordingly.

## 2. Quality Objectives

| # | Objective | Measure | Target |
|---|-----------|---------|--------|
| QO-1 | Requirement coverage | REQs with ≥1 mapped, passing test / 472 | 100% P1, ≥98% overall |
| QO-2 | Patient-safety coverage | High-risk REQs (CLIN/PHARM/ORDLAB/VITAL) with negative+boundary tests | 100% |
| QO-3 | PHI safety | Real PHI in any test asset/log/fixture | 0 (zero-tolerance) |
| QO-4 | Standards conformance | FHIR R4 / HL7 v2 / WCAG 2.1 AA test pass rate | ≥99% / ≥99% / 100% P1 |
| QO-5 | Defect containment | Escaped Critical/High defects per release | 0 Critical, ≤1 High |
| QO-6 | Automation depth | Regression suite automated (eligible cases) | ≥70% by M5 exit |
| QO-7 | Portability | Adapter-conformance suite green per backend | OpenMRS + ≥1 secondary |
| QO-8 | Traceability integrity | RTM orphan rate (tests without REQ, REQs without test) | 0% |

## 3. Scope

**In scope (21 modules / 472 requirements / 1,346 manual cases today, ~4,000 target):**

| Prefix | Module | REQ | TCs | Prefix | Module | REQ | TCs |
|--------|--------|-----|-----|--------|--------|-----|-----|
| AUTH | Authentication & Session | — | 63 | RBAC | Roles/Privileges/Admin | — | 78 |
| REG | Patient Registration | — | 94 | DATA | Data Mgmt & Integrity | — | 47 |
| SRCH | Find Patient / Search | — | 64 | RPT | Reporting & Audit Log | — | 63 |
| PDASH | Dashboard & Demographics | — | 64 | FHIR | FHIR R4 API | — | 90 |
| VISIT | Visits & Encounters | — | 67 | HL7 | HL7 v2 Messaging | — | 57 |
| VITAL | Vitals & Observations | — | 67 | SEC | Security (design-only) | — | 68 |
| CLIN | Allergies/Conditions/Dx | — | 68 | A11Y | Accessibility WCAG 2.1 AA | — | 58 |
| APPT | Appointment Scheduling | — | 68 | PERF | Performance Readiness | — | 43 |
| ORDLAB | Orders/Lab/Radiology | — | 64 | NOTIF | Notifications & Alerts | — | 44 |
| PHARM | Pharmacy & Medication | — | 58 | BILL | Billing & Insurance | — | 76 |
| | | | | TELE | Telemedicine | — | 45 |

**Out of scope:** OpenMRS core engine internals (vendor-owned), third-party library
correctness, infrastructure/network provisioning, and load-bearing penetration of
**public** sites. Performance and security execution run **only against owned/local
environments** (see §8); the public `o2.openmrs.org` instance is used solely for
read-only functional exploration and reference, never for load or intrusive testing.

## 4. Test Levels

| Level | What it proves | Primary owner | Where | Standards lens |
|-------|----------------|---------------|-------|----------------|
| **Unit** | Adapter mappers, validators, transformers behave per spec | SDET II/III | CI (mocked) | FHIR/HL7 field maps |
| **Integration** | RAL ↔ OpenMRS REST/FHIR, DB, HL7 channels interoperate | SDET III | Dockerized local stack | API/DB/HL7 contracts |
| **System / E2E** | End-to-end clinical journeys across modules via UI+API | Senior QA + SDET | Local OpenMRS env | All functional + A11Y |
| **UAT** | Clinical workflows meet user/acceptance criteria | QA Lead + SME | Staging | Use-cases, BRD/FRD |

Each higher level **never re-proves** what a lower level already guarantees; the
[Test Pyramid](../TEST_PYRAMID.md) governs the volume split (many unit/adapter,
fewer system, fewest UAT).

## 5. Test Types

| Type | Scope | Representative modules |
|------|-------|------------------------|
| Functional (positive) | Happy-path requirement verification | All 21 |
| Negative | Invalid input, error handling, safe failure | REG, CLIN, PHARM, ORDLAB |
| Boundary | Min/max, ranges, off-by-one (dose, age, vitals) | VITAL, PHARM, REG |
| Decision-table | Rule combinations (eligibility, RBAC, pricing) | RBAC, BILL, APPT |
| State-transition | Lifecycle machines (visit, appt, order status) | VISIT, APPT, ORDLAB |
| API contract | REST + **FHIR R4** resource/operation conformance | FHIR, all via RAL |
| HL7 v2 messaging | ADT/ORM/ORU/SIU parse, ACK, mapping | HL7 |
| Database integrity | Constraints, referential integrity, audit rows | DATA, RPT |
| Accessibility | **WCAG 2.1 AA** keyboard/SR/contrast/reflow | A11Y (cross-cutting) |
| Security (functional) | AuthZ, session, input, audit — **OWASP** lens, owned env only | SEC, AUTH, RBAC |
| Performance readiness | Criteria/design + execution on owned env only | PERF |
| Exploratory | Charter-based, risk-targeted session testing | High-risk modules |

## 6. Test Design Techniques

Techniques are selected per requirement risk, not applied uniformly:

| Technique | When applied | Example REQ |
|-----------|--------------|-------------|
| Equivalence/Functional | Every requirement (baseline) | REQ-REG-001 |
| Negative | Inputs that must be rejected safely | REQ-APPT-003 (no past booking) |
| Boundary value | Numeric/temporal ranges | REQ-VITAL-* (BP/HR ranges) |
| Decision table | Multi-condition rules | REQ-RBAC-* privilege matrix |
| State transition | Status machines | REQ-APPT-009 (Scheduled→…→Missed) |
| Pairwise | Multi-parameter combinatorial fields | REG demographics, search filters |
| Exploratory | Ambiguous/novel/high-risk areas | CLIN drug-allergy interaction |
| Accessibility checks | All UI-bearing modules | REQ-A11Y-001..015 |

Cross-cutting lenses (Security, API, Database, FHIR, HL7) overlay these techniques
on every applicable module, ensuring a single requirement may be exercised by
functional + negative + boundary + standards tests simultaneously.

## 7. Risk-Based Approach

Risk = **Likelihood × Patient-Safety/PHI Impact**, sourced from
[RISK_REGISTER.md](../reverse-engineering/RISK_REGISTER.md) and the per-REQ Risk
column in the [requirements catalog](../requirements/requirements-catalog.md).

| Risk | Module exemplars | Mandatory coverage | Gate |
|------|------------------|--------------------|------|
| **High** | CLIN, PHARM, ORDLAB, VITAL, FHIR, HL7, AUTH, RBAC, SEC | Functional + Negative + Boundary + (standards/state) | Block release on any open Critical/High |
| **Medium** | REG, APPT, VISIT, BILL, RPT, DATA, A11Y | Functional + Negative + technique-appropriate | Block on open Critical |
| **Low** | PDASH, SRCH, NOTIF, TELE, PERF | Functional + targeted exploratory | Risk-accept w/ sign-off |

High-risk requirements receive the deepest technique stack and are first into the
automated regression suite. Risk re-scoring is mandatory whenever a defect escapes.

## 8. Environments

| Env | Purpose | Backend | PHI policy |
|-----|---------|---------|------------|
| **Local Dev** | Author/run unit + adapter tests | Dockerized OpenMRS + H2/PostgreSQL | Synthetic only |
| **Integration (owned)** | RAL↔backend, DB, HL7 channels | Local OpenMRS + HAPI FHIR + HL7 listener | Synthetic only |
| **Staging (owned)** | System/E2E + UAT + **perf/security execution** | Production-like, owned | Synthetic only |
| **Reference (read-only)** | Functional exploration only | Public `o2.openmrs.org` | No load/intrusive/PHI |

**Hard rule:** performance and security tests execute **exclusively on owned/local
environments**. Public OpenMRS is reference-only — no load generation, no fuzzing,
no auth attacks, no data writes that persist PHI. The RAL lets the same suites
target OpenEMR/HAPI/SMART/omiiCARE backends without rewriting test intent.

## 9. Test Data Strategy (Synthetic / PHI-Safe)

| Principle | Implementation |
|-----------|----------------|
| **Zero real PHI** | All patients/encounters synthetic (Synthea-style, deterministic seeds) |
| **Minimum-necessary** | Notifications/reminders carry only required identifiers (REQ-APPT-010) |
| **Reproducibility** | Seeded fixtures; same dataset rebuilds identically per env |
| **Coverage data** | Boundary fixtures (extreme vitals, dose limits, edge demographics) |
| **Standards fixtures** | Conformant + intentionally malformed FHIR R4 bundles & HL7 v2 messages |
| **Isolation** | Per-suite teardown; no cross-test data bleed; audit rows verified |
| **Masking in logs** | PHI fields redacted in test logs/reports/screenshots |

See [TEST_DATA_STRATEGY.md](../TEST_DATA_STRATEGY.md) and
[DATA_DICTIONARY.md](../reverse-engineering/DATA_DICTIONARY.md) for field-level rules.

## 10. Automation Strategy Overview

- **Pyramid-weighted:** majority at unit/adapter and API/FHIR contract level; UI E2E
  reserved for high-value clinical journeys to avoid brittleness.
- **RAL-centric:** automation targets the adapter interface so one suite validates
  OpenMRS first, then re-runs as a **conformance suite** against each secondary backend.
- **Standards harnesses:** FHIR R4 validator + profile checks; HL7 v2 parse/ACK
  assertions; automated WCAG (axe-core) gated by manual SR/keyboard verification.
- **Selection criteria:** automate stable, high-frequency, high-risk, data-driven
  cases; keep exploratory/novel cases manual.
- **Target:** ≥70% of regression-eligible cases automated by M5 exit (QO-6); CI-gated.
- Detailed framework internals live in the Milestone-5 automation docs; this strategy
  fixes only the *what* and *where*.

## 11. Entry / Exit Criteria

**Entry (a test level may begin when):**
- Requirements baselined in the catalog; RTM entries exist for in-scope REQs.
- Build deploys cleanly to the target owned environment.
- Synthetic test data seeded; lower test level is green (no open Critical).
- Standards fixtures (FHIR/HL7) and accessibility tooling available.

**Exit (a test level/release may close when):**
- 100% P1 requirements executed; ≥98% overall pass; **0 open Critical, ≤1 High** (QO-5).
- Patient-safety REQs (CLIN/PHARM/ORDLAB/VITAL) fully covered incl. negative+boundary.
- FHIR/HL7/WCAG conformance targets met (QO-4); RTM orphan rate 0% (QO-8).
- All PHI-safety checks pass (QO-3); risk register has no unmitigated High items.
- **Suspension:** halt a level if build is unstable, a blocker prevents >20% of suite,
  or any PHI leak is detected. **Resume** only after root-cause fix + re-seed.

## 12. Metrics

| Metric | Formula | Target | Source |
|--------|---------|--------|--------|
| Requirement coverage | passing REQs / 472 | 100% P1 | RTM |
| Test pass rate | passed / executed | ≥98% | CI + manual runs |
| Defect density | defects / module | trend ↓ | defect tracker |
| Defect leakage | escaped / total found | <2% | release retro |
| Critical/High open | count at gate | 0 / ≤1 | gate review |
| Automation coverage | automated / eligible | ≥70% | CI |
| Standards pass | FHIR+HL7+WCAG passing / total | ≥99% / 100% P1 | conformance suites |
| Mean time to detect/fix | avg detect, avg fix | trend ↓ | tracker |
| RTM integrity | orphan tests + REQs | 0 | RTM audit |

## 13. Roles & Responsibilities

| Role | Responsibility |
|------|----------------|
| Principal QA / QA Architect | Owns this strategy, levels, techniques, metrics, standards posture |
| QA Lead | Operationalizes into plans, schedules, UAT, gate decisions |
| SDET III | RAL/adapter test seams, integration & contract automation |
| SDET II | Layer-appropriate automated tests, CI maintenance |
| Senior QA Engineer | Manual, exploratory, risk-driven, accessibility coverage |
| Clinical SME | UAT validation, clinical-safety acceptance |
| All contributors | Shift-left; never bypass quality gates; uphold PHI zero-tolerance |

## 14. Tooling

| Concern | Tool class | Notes |
|---------|-----------|-------|
| API/FHIR contract | REST + FHIR R4 validator, profile checks | Against RAL & backends |
| HL7 v2 | Message build/parse + ACK assertion harness | ADT/ORM/ORU/SIU |
| UI E2E | Browser automation (Playwright-class) | High-value journeys only |
| Accessibility | axe-core automated + manual SR/keyboard | WCAG 2.1 AA |
| Security (owned env) | OWASP-aligned scanners, authZ matrix harness | Never public sites |
| Performance (owned env) | Load/perf harness | PERF criteria, owned env only |
| Test mgmt / traceability | RTM.csv + requirements catalog | `REQ-<PREFIX>-NNN` keys |
| Test data | Synthetic generator (Synthea-style), seeded fixtures | PHI-safe |
| CI/CD gates | Pipeline-enforced entry/exit | See [QUALITY_GATES.md](../QUALITY_GATES.md) |

## 15. Standards Compliance

| Standard | Application | Tests |
|----------|-------------|-------|
| **FHIR R4** | Resource shape, codes, references, operations via RAL | FHIR module (90 TCs) + fixtures, `FHIR_MAPPING.md` |
| **HL7 v2** | ADT/ORM/ORU/SIU parse, ACK, field mapping | HL7 module (57 TCs), `HL7_MAPPING.md` |
| **WCAG 2.1 AA** | Keyboard, SR, contrast, reflow, focus, labels | A11Y module (58 TCs), all UI modules |
| **OWASP** | Input, authZ, session, audit (owned env) | SEC/AUTH/RBAC, design-only on public |
| **HIPAA-like** | PHI minimization, audit logging, access control, zero real PHI | RPT audit, NOTIF min-necessary, §3/§9 |

Compliance is **traced, not asserted**: each standard maps to specific REQ ids and
test modules above, and conformance pass rates feed QO-4 and the release gate.

---

### Document Control
- Supersedes: ad-hoc per-module test notes.
- Subordinate to: requirements catalog + reverse-engineering baseline.
- Refined by: Master Test Plan, Test Data Strategy, Test Pyramid, RTM, Risk Analysis.
- Change trigger: any escaped Critical/High defect forces strategy + risk re-review.
