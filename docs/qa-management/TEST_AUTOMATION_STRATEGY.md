# Test Automation Strategy — OpenMRS-Primary Healthcare QA Portfolio

> **The *how* of automation.** This document refines §10 of the
> [Master Test Strategy](MASTER_TEST_STRATEGY.md) into concrete framework choices,
> selection criteria, layer split, environment/data wiring, CI gates, reporting,
> flake control, and ROI. It is governed by the
> [Test Pyramid](../TEST_PYRAMID.md), traces to the
> [RTM](../../manual-testing/rtm/RTM.csv) and
> [requirements catalog](../requirements/requirements-catalog.md), and inherits
> all PHI / patient-safety rules from the Master Strategy. System facts defer to
> [reverse-engineering/*](../reverse-engineering/).

- **Document ID:** QAM-TAS-001 · **Owner:** SDET Lead / QA Architect
- **Status:** Baseline · **Date:** 2026-07-01 · **Review cadence:** per release train
- **SUT (primary):** OpenMRS reference app (`https://o2.openmrs.org`, reference-only)
- **Portability targets:** OpenEMR, HAPI FHIR, SMART Health IT, in-house omiiCARE — via the **Resource Adapter Layer (RAL)**
- **Realization window:** authored M1–M4, automation built M5, specialized bands (perf/security/visual) M7

---

## 1. Objectives & Guiding Principles

| # | Principle | Consequence for automation |
|---|-----------|----------------------------|
| P-1 | **Patient-safety first** | Dose/allergy/order/vitals paths (CLIN, PHARM, ORDLAB, VITAL) automate negative + boundary first |
| P-2 | **PHI zero-tolerance** | Synthetic data only; PHI redacted in logs, traces, screenshots, video |
| P-3 | **Pyramid-weighted** | Push assertions to the lowest layer that can prove them; UI E2E is scarce |
| P-4 | **RAL-centric** | Test intent targets the adapter interface, not a backend's quirks — one suite, many systems |
| P-5 | **Standards as code** | FHIR R4 / HL7 v2 / WCAG 2.1 AA conformance is machine-checked, not asserted by eye |
| P-6 | **Traceable** | Every automated case carries its `REQ-<PREFIX>-NNN` and `TC-<MOD>-NNNN` id |
| P-7 | **Deterministic** | No sleeps, no shared mutable state, no order dependence; flake is a defect |
| P-8 | **Owned-env only for perf/security** | Public `o2.openmrs.org` is never load-tested, fuzzed, or attacked |

**Goal (QO-6):** ≥70% of regression-eligible cases automated and CI-gated by M5 exit.

## 2. Automation Pyramid

Volume target by layer (of the *automated* estate, not the full manual catalogue):

```
                     ▲  slower · costlier · fewer · brittle
                    /U \   UI / E2E (Playwright)         ~10%
                   /----\  API + FHIR contract (RestAssured) ~25%
                  /  INT  \ RAL↔backend integration (TestNG) ~20%
                 /--------\ Adapter component (mocked backend) ~15%
                /  UNIT     \ mapper/validator/transformer    ~30%
               +-------------+  ▼  faster · cheaper · many · stable

   Cross-cutting (run across layers, not stacked):
   [ A11Y: axe-core ] [ Perf: owned-env only ] [ Security: owned-env only ] [ Visual ]
```

| Layer | Proves | Framework | Backend dependency | Speed | Share |
|-------|--------|-----------|--------------------|----|----|
| **Unit** | Mapper / validator / transformer logic in RAL | JUnit 5 | None (pure) | ms | ~30% |
| **Adapter component** | Adapter behavior vs mocked OpenMRS REST/FHIR | JUnit 5 + WireMock | Mocked | ms–s | ~15% |
| **Integration** | RAL ↔ real backend, DB, HL7 channel | TestNG | Dockerized OpenMRS/HAPI/HL7 | s | ~20% |
| **API + FHIR contract** | REST/FHIR R4 resource, codes, operations, HL7 ACK | RestAssured + HAPI validator | Local backend | s | ~25% |
| **UI / E2E** | High-value clinical journeys end to end | Playwright (TS) | Local OpenMRS env | min | ~10% |
| **BDD layer** | Acceptance criteria readable by SMEs | Cucumber (Gherkin) over the above | — | — | overlay |

**Anti-pattern forbidden:** the inverted "ice-cream cone" (UI-heavy). A UI E2E case
is justified only when a journey cannot be proven below the line (e.g., focus
restoration in a modal, a full registration→visit→order→result loop).

## 3. What to Automate — Selection Criteria

Each manual case carries an `Automation_Feasibility` value in
[ALL_TEST_CASES.csv](../../manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv).
Current catalogue (1,349 cases) splits:

| Feasibility | Count | % | Disposition |
|-------------|------:|--:|-------------|
| **Automatable** | 584 | 43% | Automate fully; primary M5 backlog |
| **Partially-Automatable** | 561 | 42% | Automate the deterministic core; keep a manual verification step (e.g., SR announcement, visual rendering, clinical judgment) |
| **Manual-Only** | 204 | 15% | Stay manual / exploratory (charter-based) |

**Automate when** a case is: deterministic, repeatable, high-frequency (regression),
high-risk (patient-safety / PHI), data-driven (boundary/pairwise/decision-table),
or a standards-conformance check (FHIR/HL7/WCAG-machine-verifiable).

**Keep manual when** a case is: exploratory/novel, one-off, requires human clinical
judgment, depends on subjective UX, or costs more to stabilize than to run by hand
(low-frequency × high-flake). Screen-reader *announcement quality* and contrast
*perception* stay manual even though axe-core covers their structural preconditions.

**ROI gate to admit a case into the automated suite:**
`(manual_run_cost × annual_runs) − (build_cost + annual_maintenance) > 0`
AND the case is not Manual-Only AND it sits at the lowest layer that can prove it.

## 4. Framework Choices

| Concern | Tool | Why this, not the alternative |
|---------|------|-------------------------------|
| UI / E2E | **Playwright** (TypeScript) | Auto-wait kills sleep-flake; trace viewer; multi-browser; faster + less brittle than Selenium for the SPA O3 frontend |
| Legacy / cross-browser fallback | **Selenium 4** (Java) | Grid for browsers Playwright cannot drive; legacy O2 pages; kept minimal |
| API / FHIR / HL7 contract | **RestAssured** (Java) | Fluent REST assertions; pairs with HAPI FHIR validator and an HL7 v2 ACK harness |
| Unit / component | **JUnit 5** | RAL is JVM-side; modern params, extensions, tags |
| Integration / E2E orchestration | **TestNG** | Groups, dependsOn, data providers, parallel suites for backend-bound tests |
| BDD / acceptance | **Cucumber** (Gherkin) | SME-readable scenarios bind acceptance criteria to step defs; living documentation |
| Mocking | **WireMock** | Stub OpenMRS REST/FHIR for adapter-component isolation |
| FHIR validation | **HAPI FHIR validator** + profile/IG checks | R4 structure, cardinality, code-system URIs |
| Accessibility | **axe-core** (via Playwright) | Automated WCAG structural checks; gated by manual SR/keyboard |
| Performance (owned env) | k6 / JMeter | Execute only on owned/staging — never public |
| Security (owned env) | OWASP ZAP (baseline), authZ-matrix harness | Owned env only; design-only against public |

**Two language tracks by design:** JVM (JUnit/TestNG/RestAssured/Cucumber-JVM) for
the RAL and all API/FHIR/HL7/integration work — co-located with the adapter under
test; TypeScript (Playwright) for browser E2E. Both publish to a single Allure report.

## 5. Resource Adapter Layer (RAL) — The Automation Hub

The RAL is the seam that makes one suite portable. Tests assert against the
**adapter contract** (normalized request/response), not a backend's native API.

```
  Test (intent) ──▶ RAL contract ──▶ ┌─ OpenMRS adapter   (PRIMARY)
                                      ├─ OpenEMR adapter
   asserts on  ◀── normalized  ◀──   ├─ HAPI FHIR adapter
   normalized model                   ├─ SMART Health IT adapter
                                      └─ omiiCARE adapter
```

- **Contract test suite:** the same `TC-FHIR-*`, `TC-HL7-*`, and cross-module API
  cases run as a **backend-conformance suite**. Pass = the adapter normalizes that
  backend correctly. OpenMRS is gate-mandatory; ≥1 secondary green per release (QO-7).
- **Capability matrix:** an adapter declares supported operations; unsupported ops
  are skipped (reported as `n/a`, not failed) so a thinner backend does not red the suite.
- **Fixture translation:** synthetic FHIR R4 bundles / HL7 v2 messages seed every
  backend through the RAL, keeping data identical across systems (FHIR_MAPPING.md, HL7_MAPPING.md).
- **One change, all backends:** a new requirement is automated once at the contract
  layer and immediately exercises every adapter — the core ROI multiplier of this design.

## 6. Test Data Management

| Need | Mechanism | PHI rule |
|------|-----------|----------|
| Synthetic patients | Synthea-style generator, deterministic seeds | Zero real PHI (P-2) |
| Boundary fixtures | Extreme vitals, dose limits, edge demographics, off-by-one dates | Versioned with suite |
| Standards fixtures | Conformant **and** intentionally malformed FHIR R4 bundles / HL7 v2 messages | Synthetic |
| Reproducibility | Seeded builder; same dataset rebuilds identically per env | — |
| Isolation | Per-suite setup/teardown; unique key namespacing; no cross-test bleed | Audit rows asserted |
| Secrets | Vault / CI secret store; never in repo or logs | Masked in output |
| Log/trace/screenshot hygiene | PHI-field redaction filter on Allure attachments, Playwright traces, video | Mandatory gate |

Data is built **via the RAL**, so OpenMRS and every secondary backend receive an
identical synthetic population. See [TEST_DATA_STRATEGY.md](../TEST_DATA_STRATEGY.md)
and [DATA_DICTIONARY.md](../reverse-engineering/DATA_DICTIONARY.md).

## 7. Environment Management

| Env | Use | Backend | Automation runs |
|-----|-----|---------|-----------------|
| **Local dev** | Author/run unit + adapter-component | Mocked (WireMock) | every commit |
| **Ephemeral CI** | Integration + API/FHIR/HL7 contract | **Dockerized** OpenMRS + HAPI + HL7 listener | every PR |
| **Staging (owned)** | System/E2E + UAT + **perf/security execution** | Production-like, owned | nightly / release |
| **Reference (read-only)** | Functional exploration only | Public `o2.openmrs.org` | **no** automated load/intrusion/writes |

- **Containers:** `docker compose` spins a clean OpenMRS + DB + HL7 channel per CI
  run; torn down after. No shared mutable state between PR runs.
- **Hard rule (inherited):** performance and security automation execute **only** on
  owned/local environments. Public OpenMRS is never a target for k6, JMeter, ZAP, or auth attacks.

## 8. CI/CD Integration

Pipeline (governed by [QUALITY_GATES.md](../QUALITY_GATES.md), [CI_CD_GUIDE.md](../CI_CD_GUIDE.md)):

| Stage | Trigger | Suites | Gate (blocks merge/release on…) | Budget |
|-------|---------|--------|----------------|--------|
| **Pre-commit** | local hook | lint, changed unit | compile + unit fail | < 30 s |
| **PR / commit** | push | unit + adapter-component + smoke (`@smoke`) | any fail; coverage drop | < 5 min |
| **Merge to main** | merge | integration + API/FHIR/HL7 contract | any P1 contract fail | < 20 min |
| **Nightly** | schedule | full regression (`@regression`) + A11Y + RAL conformance (OpenMRS + ≥1 secondary) | new fail vs baseline | < 60 min |
| **Pre-release** | tag | E2E journeys + perf + security (owned env) | open Critical/High; FHIR/HL7/WCAG below target | per release |

- **Tagging:** `@smoke @sanity @regression @e2e @fhir @hl7 @a11y @patient-safety`
  drive stage selection and risk-based execution order (high-risk first).
- **Parallelism:** TestNG suites + Playwright shards across CI workers; target ≥4× wall-clock reduction.
- **Fail-fast on safety:** any `@patient-safety` failure red-flags the build immediately.
- **Artifacts:** every run publishes Allure results, traces, and the RTM coverage delta.

## 9. Reporting

| Tool | Role |
|------|------|
| **Allure** | Primary aggregate report — JVM + Playwright unified; trend, history, flake rate, severity, REQ/TC labels |
| **Extent** | Secondary rich HTML for stakeholder/UAT readouts where Allure infra is absent |
| **JUnit XML** | Machine-readable for CI gate parsing and dashboards |
| **RTM coverage delta** | Each run emits automated-coverage % per module back into the RTM |

Every test is annotated with `@Epic(module)`, `@Story(REQ id)`, `@TmsLink(TC id)`,
and severity, so a failure reads as "REQ-PHARM-014 / TC-PHARM-0021 (Critical) failed"
— traceable to requirement and risk without spelunking logs. PHI redaction runs
before any attachment is published.

## 10. Flake Management

Flake is treated as a **defect**, not noise. Targets: suite flake rate < 1%; zero
quarantined `@patient-safety` tests permitted past one sprint.

| Source | Control |
|--------|---------|
| Timing | Playwright auto-wait + explicit web-first assertions; **no `sleep`** (lint-banned) |
| Order/state | Independent tests; fresh seed per suite; unique-key namespacing |
| Environment | Ephemeral containers; readiness/health gate before suite starts |
| Test data | Deterministic seeds; per-suite teardown |
| External flake | WireMock at component layer; real backend only at integration+ |

**Process:** auto-retry once **only** to *detect* flake (a pass-on-retry is logged as
flaky, not green); flaky test is tagged `@quarantine`, ticketed, and excluded from the
gate for ≤1 sprint; root-caused and returned or deleted. Flake rate is a tracked
[QA metric](QA_METRICS.md); rising flake blocks new automation work until burned down.

## 11. ROI Model

| Driver | Effect |
|--------|--------|
| **RAL multiplier** | One contract test validates N backends → cost amortized across OpenMRS + secondaries |
| **Layer placement** | A unit test costs ~1/20th of an equivalent UI test to build and run; pyramid maximizes cheap coverage |
| **Regression frequency** | 807 regression-level cases run every release — automation payback is fastest here |
| **Patient-safety risk** | An escaped Critical (mis-mapped FHIR code, wrong dose) dwarfs any automation cost — risk-weighted ROI |

**Break-even rule of thumb:** a deterministic regression case run ≥4×/year pays back
its build inside one year; contract-layer cases pay back faster via the RAL multiplier.
Manual-Only (204) and low-frequency cases are deliberately *not* automated — forcing
them would be ROI-negative and flake-prone.

**Prioritization order for the M5 build backlog:** (1) `@patient-safety` Automatable,
(2) FHIR/HL7 contract Automatable, (3) high-frequency regression Automatable,
(4) Partially-Automatable cores, (5) the long tail.

## 12. Automatable Case Map (by module)

From `Automation_Feasibility` over 1,349 cases. **A** = Automatable, **P** =
Partially, **M** = Manual-Only. Automation backlog priority = A first, then P-cores.

| Prefix | Module | Total | A | P | M | Auto target (A + P-core) | Primary framework |
|--------|--------|------:|--:|--:|--:|-----|-------------------|
| REG | Patient Registration | 95 | 55 | 32 | 8 | high | Playwright + RestAssured |
| FHIR | FHIR R4 API | 90 | 64 | 18 | 8 | **highest** | RestAssured + HAPI validator |
| RBAC | Roles/Privileges/Admin | 78 | 39 | 28 | 11 | high | RestAssured + Playwright |
| BILL | Billing & Insurance | 76 | 42 | 31 | 3 | high | RestAssured + Playwright |
| CLIN | Allergies/Conditions/Dx | 68 | 34 | 24 | 10 | high (safety) | Playwright + RestAssured |
| VISIT | Visits & Encounters | 68 | 30 | 21 | 17 | medium | Playwright |
| APPT | Appointment Scheduling | 68 | 32 | 29 | 7 | high | RestAssured + Playwright |
| VITAL | Vitals & Observations | 67 | 31 | 18 | 18 | high (safety/boundary) | RestAssured |
| AUTH | Authentication & Session | 64 | 32 | 28 | 4 | high | Playwright + RestAssured |
| SRCH | Find Patient / Search | 64 | 30 | 27 | 7 | high (pairwise) | RestAssured |
| ORDLAB | Orders/Lab/Radiology | 64 | 22 | 38 | 4 | medium (safety) | RestAssured |
| PDASH | Dashboard & Demographics | 64 | 17 | 30 | 17 | medium | Playwright |
| RPT | Reporting & Audit Log | 63 | 18 | 29 | 16 | medium | RestAssured + DB asserts |
| PHARM | Pharmacy & Medication | 58 | 17 | 37 | 4 | medium (safety/boundary) | RestAssured |
| A11Y | Accessibility WCAG 2.1 AA | 58 | 20 | 12 | 26 | medium | Playwright + axe-core |
| HL7 | HL7 v2 Messaging | 57 | 28 | 23 | 6 | high | HL7 ACK harness |
| SEC | Security (owned env) | 68 | 20 | 37 | 11 | owned-env only | ZAP + authZ harness |
| DATA | Data Mgmt & Integrity | 47 | 16 | 20 | 11 | medium | DB + RestAssured |
| TELE | Telemedicine | 45 | 12 | 24 | 9 | low | Playwright |
| NOTIF | Notifications & Alerts | 44 | 14 | 25 | 5 | medium | RestAssured |
| PERF | Performance Readiness | 43 | 11 | 30 | 2 | owned-env only | k6 / JMeter |
| **Total** | **21 modules** | **1,349** | **584** | **561** | **204** | **≥70% eligible by M5** | — |

**Reading the map:** **FHIR** (64 A) is the highest-leverage automation target — pure
contract work, RAL-amplified across backends. Safety modules (**CLIN/PHARM/ORDLAB/VITAL**)
skew Partially-Automatable because dose/allergy judgment needs a human step, so we
automate their deterministic boundary/negative cores and keep clinical-judgment cases
manual. **A11Y** is 45% Manual-Only by design — axe-core covers structure; SR
announcement and contrast perception stay human.

## 13. Metrics & Targets

| Metric | Formula | Target | Source |
|--------|---------|--------|--------|
| Automation coverage | automated / regression-eligible | ≥70% by M5 (QO-6) | CI + RTM |
| Suite flake rate | flaky runs / total runs | < 1% | Allure trend |
| CI feedback time | PR-stage wall clock | < 5 min | pipeline |
| RAL conformance | backends green / targeted | OpenMRS + ≥1 (QO-7) | conformance suite |
| Standards pass | FHIR+HL7+WCAG passing / total | ≥99% / 100% P1 (QO-4) | contract suites |
| Patient-safety automation | `@patient-safety` automated / eligible | 100% | CI |
| Escaped defects | Critical/High escaped per release | 0 Critical / ≤1 High (QO-5) | release retro |
| RTM automation delta | automated REQs trend | ↑ each release | RTM audit |

## 14. Roles & Responsibilities

| Role | Automation responsibility |
|------|---------------------------|
| QA Architect / SDET Lead | Owns this strategy, framework choices, pyramid shape, RAL contract design |
| SDET III | RAL/adapter seams, integration + contract automation, conformance suite |
| SDET II | Unit/component/API/UI automation, CI maintenance, flake burndown |
| Senior QA Engineer | Manual + exploratory band, A11Y human verification, automation candidacy review |
| Clinical SME | Validates `@patient-safety` scenario correctness; UAT acceptance |
| All contributors | No `sleep`; no real PHI; never bypass gates; flake is a defect |

---

### Document Control
- Refines: [Master Test Strategy](MASTER_TEST_STRATEGY.md) §10; governed by [Test Pyramid](../TEST_PYRAMID.md).
- Traces to: [RTM](../../manual-testing/rtm/RTM.csv), [requirements catalog](../requirements/requirements-catalog.md), [ALL_TEST_CASES.csv](../../manual-testing/test-cases/openmrs/ALL_TEST_CASES.csv).
- Realized in: Milestone 5 (automation), Milestone 7 (perf/security/visual bands).
- Change trigger: any escaped Critical/High, new backend adapter, or framework migration forces re-review.
