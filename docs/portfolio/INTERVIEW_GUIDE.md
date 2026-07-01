# omiiCARE_QA — Interview Guide

> Talking points, deep-dive Q&A, and STAR stories grounded in the real artifacts of the
> **omiiCARE_QA** enterprise healthcare QA platform. Use this to prepare for QA Engineer,
> SDET, QA Architect, and AI-assisted Quality Engineering interviews.
>
> **Primary SUT:** OpenMRS (`https://o2.openmrs.org`) · **Local SUT:** omiiCARE
> (Spring Boot 3 / Java 21 backend, React + Vite frontend) · **Portable across**
> OpenMRS / OpenEMR / HAPI FHIR / SMART / omiiCARE via a **Resource Adapter Layer**.

---

## 0. How to Use This Guide

- Each answer is written so you can speak it in **60–120 seconds**.
- **Bold** phrases are the load-bearing keywords interviewers listen for.
- Every claim maps to a **real path/command/metric** in this repo — never bluff numbers.
- STAR stories (Section 10) are interview-ready: Situation, Task, Action, Result.
- Items marked **(planned)** are future-state and must be presented as roadmap, not done.

### Headline metrics you can quote verbatim

| Metric | Value | Source |
|---|---|---|
| Manual test cases | **4,187** across **66 modules** (17-col CSV) | `manual-testing/test-cases/openmrs/` + `ALL_TEST_CASES.csv` |
| Requirements catalog | **1,795 requirements** | `docs/requirements/requirements-catalog.md` |
| RTM coverage | **0 gaps, 0 untraced** | `manual-testing/rtm/RTM.csv` / `RTM.md` |
| Automation unit tests | **98 PASS** on default build | `mvn -pl automation test` |
| Node smoke (omiiCARE) | **5/5 PASS** headed | `automation/playwright/` |
| Node smoke (OpenMRS) | **5/5 PASS** headed vs `o2.openmrs.org` | `automation/playwright/tests-openmrs/` |
| Reverse-engineering docs | **22 docs, ~10k lines, 78 Mermaid diagrams** | `docs/reverse-engineering/` |
| QA management docs | **15 docs** | `docs/qa-management/` |

---

## 1. Manual QA & Test Design (Q1–Q6)

**Q1. Walk me through how you structure a manual test case suite at scale.**
We maintain **4,187 manual test cases across 66 functional modules** in a **17-column CSV
schema** under `manual-testing/test-cases/openmrs/`, rolled up into a single
`ALL_TEST_CASES.csv`. The 17 columns capture **traceability** (requirement ID, module),
**execution** (preconditions, steps, expected results), **classification** (priority,
test type, automation candidacy), and **lifecycle** (status, defect link). CSV is
deliberate — it is **diff-friendly in git**, importable into any TMS (Xray, TestRail,
Zephyr), and **machine-parseable** for coverage analytics without vendor lock-in.

**Q2. How do you guarantee coverage and avoid orphan tests?**
A **Requirements Traceability Matrix** at `manual-testing/rtm/` (`RTM.csv` + `RTM.md`)
links every one of the **1,795 cataloged requirements** to test cases. Our current state
is **0 gaps and 0 untraced** — every requirement has at least one test, and every test
maps back to a requirement. This is a hard **exit-gate**: a requirement with no linked
test cannot be marked verified.

**Q3. Where do your test cases come from — how do you avoid guessing requirements?**
We **reverse-engineered the SUT** into **22 structured documents (~10k lines, 78 Mermaid
diagrams)** under `docs/reverse-engineering/`: BRD/SRS/FRD, NFRs, use cases, user stories,
RBAC, navigation maps, data + field dictionaries, validation rules, ERD, API, FHIR, HL7,
risk register, and architecture. Test cases derive from the **FRD + validation + field
dictionary**, so behavior is grounded in documented spec rather than tester intuition.

**Q4. What test design techniques do you apply?**
**Equivalence partitioning** and **boundary value analysis** off the field dictionary
(field lengths, numeric ranges, date validity), **decision tables** for RBAC permission
matrices, **state transition** testing for patient/visit/encounter lifecycles, and
**error-guessing** for clinical edge cases (e.g., date-of-birth in the future, allergy
on a discontinued drug). The validation-rules doc is the single source for negative cases.

**Q5. How do you prioritize when you can't run all 4,187 cases?**
**Risk-based testing** (Section 5) — each module carries a risk score from
`docs/reverse-engineering/` risk register and `docs/qa-management/` risk-based strategy.
**Patient-safety and PHI-handling paths run first** (patient registration, orders,
allergies, identifiers), then high-traffic workflows, then low-risk config screens.

**Q6. How is your test data managed safely?**
**Synthetic, PHI-safe data only** — generated via **Datafaker** in the automation module
(`core.generators`). No real patient data ever enters the repo or CI. The test-data
strategy lives in `docs/qa-management/` and `docs/portfolio/TEST_DATA_STRATEGY.md`. This
is non-negotiable for **HIPAA-like** handling even though the data is fabricated.

---

## 2. Automation Architecture (Q7–Q14)

**Q7. Give me the 30-second tour of the automation framework.**
A Maven module `automation/` (package `com.omiicare.qa.automation`) on **Java 21 / JUnit5
/ TestNG**. It layers **Playwright (Java)** and **Selenium** for UI, **RestAssured** for
API, **Cucumber** for BDD, plus dedicated packages for **FHIR assertions** (`fhir/`),
**HL7 v2 validation** (`hl7/`), **DB testing** (`db/`), **accessibility** (axe via
Playwright), **reporting** (Allure + Extent), and **environment management**. The default
build `mvn -pl automation test` runs **98 unit tests, all green**.

**Q8. How do you separate fast feedback from heavy SUT/browser tests?**
**Tag-based execution.** The default build runs only the **98 unit tests** (no browser,
no live SUT) for fast PR feedback. SUT- and browser-bound tests are tagged
**`ui-e2e`, `api-e2e`, `bdd`** and run on demand via the e2e profile:
`mvn -pl automation -Pe2e test`. This keeps the **inner loop fast** while gating heavy
suites to nightly/manual runs.

**Q9. What's the design rationale for Playwright *and* Selenium?**
Playwright is the **default modern driver** — auto-waiting, tracing, network interception,
built-in axe a11y. Selenium is retained for **legacy-grid compatibility** and breadth of
the Resource Adapter Layer across older SUTs. The adapter abstracts the driver so test
logic doesn't care which engine runs underneath.

**Q10. Why both Java Playwright and a Node `@playwright/test` suite?**
The **Java Playwright** path lives inside the unified Maven reactor for API/UI/BDD
integration and shared adapters. The **Node `@playwright/test`** smoke suites under
`automation/playwright/` give a **lightweight, fast browser smoke** independent of the JVM
— **omiiCARE smoke 5/5 PASS** and **OpenMRS smoke 5/5 PASS** against `o2.openmrs.org`
(launch → login → nav → register-patient CRUD → logout), emitting `trace.zip`,
step screenshots (`artifacts-openmrs/screenshots/step-1..5`), and an HTML report.

**Q11. How do you keep selectors and flows from rotting?**
**Page-object / component abstraction** behind the adapter, **role- and label-based
locators** (Playwright `getByRole`/`getByLabel`) over brittle CSS/XPath, **auto-waiting**
instead of sleeps, and **trace + screenshot artifacts** on every run so a failure is
debuggable post-mortem from `trace.zip` without re-running.

**Q12. What does your test pyramid look like?**
Wide base of **98 unit tests** (framework/adapter/generator logic, no I/O), a middle layer
of **API/integration** (RestAssured against local SUT), and a **thin, tagged UI-e2e tip**
(Playwright/Selenium `ui-e2e`). The Node smokes are the **top-of-funnel sanity** gate.
Detailed in `docs/portfolio/TEST_PYRAMID.md`.

**Q13. How is reporting wired?**
**Allure + Extent** in the Java module for rich step/attachment reports, and the Node
suites emit a **Playwright HTML report** plus traces and screenshots. Allure gives
historical trend and categorization; Extent gives a self-contained shareable HTML.

**Q14. How do you run against different environments?**
An **env-management** layer (`.properties` config + the adapter's env resolution) selects
base URLs, credentials (synthetic), and SUT flavor. **Performance and security suites are
hard-gated to owned/local environments only** — we never load- or attack-test
`o2.openmrs.org`.

---

## 3. The Resource Adapter Layer (Q15–Q18)

**Q15. What is the Resource Adapter Layer and why does it exist?**
It's the **portability seam** (`core.adapter` —
`automation/src/test/java/.../core/adapter`) that lets the **same test logic run against
OpenMRS, OpenEMR, HAPI FHIR, SMART, or omiiCARE**. Each SUT differs in REST endpoints,
auth, FHIR conformance, and HL7 quirks; the adapter normalizes those behind a stable
interface so a "register patient" test is written **once** and executed everywhere.

**Q16. How does it actually decouple tests from a SUT?**
The adapter exposes **resource-oriented operations** (patient, encounter, observation,
order) and **maps them per-SUT** to the correct endpoint, payload shape, and auth flow.
Tests speak the **domain language**; the adapter speaks the **dialect** of each system.
This is the **Adapter / Strategy pattern** applied to heterogeneous health systems.

**Q17. What's the payoff in numbers?**
**Test reuse.** The OpenMRS smoke (`tests-openmrs/`) and omiiCARE smoke share the same
flow shape; both are **5/5 PASS**. Adding a new SUT means writing **one adapter
implementation**, not re-authoring suites. This is the core differentiator I'd present in
an interview — most QA frameworks are **single-SUT-coupled**; ours is **SUT-agnostic by
construction**.

**Q18. What are the limits / honest tradeoffs?**
The adapter is only as good as the **conformance assumptions** baked in; non-standard SUT
extensions still need per-SUT code. There's an **abstraction tax** — debugging crosses an
indirection layer. We mitigate with **strong typing**, contract tests per adapter, and the
trace artifacts so failures localize quickly.

---

## 4. FHIR & HL7 Interoperability (Q19–Q23)

**Q19. How do you validate FHIR resources?**
A dedicated **`fhir/` assertion package** validates **structure and semantics** of FHIR
resources — required fields, cardinality, value-set/code-system URIs (e.g., LOINC,
SNOMED CT, RxNorm), reference integrity, and resource-type correctness. We assert on the
**parsed resource**, not raw JSON string matching, so reordered-but-valid payloads pass.

**Q20. What FHIR mistakes do you specifically catch?**
**Wrong code-system URIs** (a classic — using a display string instead of the canonical
system URI), **missing required identifiers**, **dangling references**, and **profile
nonconformance**. The reverse-engineering FHIR doc in `docs/reverse-engineering/` defines
the expected profiles so assertions have a spec to check against.

**Q21. How do you handle HL7 v2?**
The **`hl7/` package** validates **HL7 v2 messages** — segment presence/order (MSH, PID,
PV1, OBR, OBX), field/component structure, and required data types. This matters for lab
and ADT interfaces that predate FHIR and are still production-critical in hospitals.

**Q22. FHIR vs HL7 v2 — when each?**
**HL7 v2** is the legacy pipe-delimited workhorse for ADT/lab/orders within hospital
integration engines. **FHIR (R4)** is the modern RESTful/resource model for apps and APIs
(SMART-on-FHIR). A real platform supports **both**, which is why we test both — see
`docs/portfolio/FHIR_GUIDE.md` and `HL7_GUIDE.md`.

**Q23. How do you test a SMART-on-FHIR launch? (planned where noted)**
The adapter targets **SMART** as a SUT flavor; OAuth2 launch context and scoped tokens are
modeled in env config. End-to-end SMART app-launch automation is **(planned)** — the
adapter seam is in place, deeper launch-sequence coverage is roadmap.

---

## 5. Risk-Based Testing (Q24–Q26)

**Q24. Define risk-based testing in your context.**
Risk = **likelihood × patient-safety/business impact**. We score every module using the
**risk register** in `docs/reverse-engineering/` and the **risk-based strategy** in
`docs/qa-management/`, then allocate test depth and execution order by score. High-risk,
high-impact clinical paths get the most cases and run first.

**Q25. Give a concrete example of a high-risk area.**
**Patient identity and orders.** A merged/duplicated patient record or a mis-routed
medication order is a **patient-safety event**, not a cosmetic bug. Those modules get
exhaustive boundary/negative coverage, are first in the run order, and are blocking exit
criteria. Config/cosmetic screens are deprioritized.

**Q26. How does risk feed your entry/exit criteria?**
`docs/qa-management/` defines **entry/exit gates**: e.g., **no open Critical/High defects
on patient-safety modules**, **RTM at 0 gaps**, and **smoke green** before a release is
signed off. Risk determines *which* failures are release-blocking.

---

## 6. CI/CD & DevOps (Q27–Q30)

**Q27. How is the pipeline structured? (existing + planned)**
The repo has existing **GitHub Actions workflows** under `.github/workflows/` (omiiCARE
CI). New QA workflows use **distinct names** so they never overwrite existing ones. The
pattern: **PR stage** runs fast — `mvn -pl automation test` (98 unit) + frontend
`build/lint/typecheck`; **nightly/manual stage** runs tagged `-Pe2e` suites and Node
smokes. Broader QA-gate orchestration is **(planned)**.

**Q28. What's in the local infra stack?**
`infrastructure/docker/docker-compose.yml` brings up **Postgres, Redis, MailHog, MinIO,
Keycloak, WireMock, Prometheus, Grafana, and SonarQube** — a full local SUT-support stack.
`scripts/` provides **setup/start/stop/health** helpers. WireMock enables **contract/stub
testing**, Keycloak provides **realistic auth**, and Prometheus/Grafana give observability.

**Q29. How do quality gates work?**
**SonarQube** for static analysis/coverage thresholds, **defined quality gates** in
`docs/portfolio/QUALITY_GATES.md`, and **adversarial code review** (min 10 issues across
security/quality/architecture/performance/tests — never "looks good"). A build that fails
a gate doesn't merge.

**Q30. How do you keep the build green while extending QA assets?**
A strict rule on this repo: **non-Java assets only** (Markdown, YAML, properties, Python,
shell, k6/JMeter/ZAP). We **never touch Java sources, `pom.xml`, or build files**, so the
**98-test green build is protected**. New files use distinct names. This is exactly how
you add QA tooling to a live repo without destabilizing it.

---

## 7. Performance & Security Testing (Q31–Q34)

**Q31. What's your performance toolkit and where can it run?**
**k6 scripts** and **JMeter plans** under `quality/performance/`. The **hard rule**:
performance tests run **only on owned/local environments** — **never against
`o2.openmrs.org`**. We model load against the local omiiCARE SUT + the docker stack, with
Prometheus/Grafana capturing latency/throughput/error-rate.

**Q32. What do you measure?**
**p95/p99 latency**, throughput (req/s), error rate under load, and resource saturation
(CPU/mem from Prometheus). Thresholds live in `docs/portfolio/PERFORMANCE_GUIDE.md`. We
test the **patient-search and registration** hot paths first since they're highest-traffic.

**Q33. How do you approach security testing?**
**OWASP ZAP** baseline scan (`quality/security/` — ZAP baseline config + run script)
against the **local SUT only**. Plus the manual review lens covers **OWASP Top 10**:
authn/authz (RBAC matrix from reverse-eng docs), injection, PHI exposure, and audit
logging. Security guide: `docs/portfolio/SECURITY_TESTING_GUIDE.md`.

**Q34. Healthcare-specific security concerns?**
**PHI never in logs or test data**, **HIPAA-like audit logging** of access to patient
resources, **least-privilege RBAC** validated via decision tables, and **consent**
handling. Even with synthetic data we treat it as if real to keep habits correct.

---

## 8. Healthcare Compliance & Patient Safety (Q35–Q38)

**Q35. What does HIPAA-like testing mean if the data is synthetic?**
It means we validate the **controls**, not the data: **audit logging on PHI access**,
**RBAC least-privilege**, **encryption-in-transit assumptions**, and **no-PHI-in-artifacts**
discipline. Datafaker keeps data **PHI-safe**; the controls are tested as if it were real.

**Q36. What is "patient-safety testing" concretely?**
Tests whose failure could cause **clinical harm**: medication order routing, allergy
checks, patient-identity matching, dosage/unit validation, and result association. These
are **risk-ranked highest**, run first, and are **release-blocking**. A cosmetic defect
can ship with a note; a patient-safety defect cannot.

**Q37. How do you trace compliance back to requirements?**
The **compliance map** in the knowledge base plus the **RTM** tie regulatory/clinical
requirements to specific test cases — so we can demonstrate, per requirement, **what
verifies it**. Auditors want traceability; **0 untraced** is the answer.

**Q38. Give an example field-level validation that's safety-relevant.**
**Date-of-birth cannot be in the future**, **patient identifier format/uniqueness**, and
**allergy cannot reference a discontinued drug**. These come straight from the
**validation-rules** and **field-dictionary** reverse-engineering docs and drive negative
test cases.

---

## 9. AI-Assisted Quality Engineering (Q39–Q42)

**Q39. Where does AI fit in your QA workflow?**
There's a dedicated **`ai/` module** in the Maven reactor. The role of AI here is
**assistive, not authoritative**: accelerating **test-case generation from requirements**,
**reverse-engineering docs from a SUT**, **selector/locator suggestions**, and **triage of
failures** — always behind a **human review gate**. AI proposes; the QE verifies.

**Q40. How do you stop AI from producing plausible-but-wrong tests?**
**Ground it in artifacts** — requirements catalog, field dictionary, validation rules —
and run an **adversarial review** (min 10 issues, never "looks good"). Generated tests
must **pass the RTM trace check** and **execute green** before they count. AI output that
can't be traced or run is discarded.

**Q41. What about determinism and flakiness from AI-generated automation?**
We enforce the same hygiene as human-written tests: **role/label locators, auto-wait, no
sleeps, idempotent synthetic data**. Flaky AI tests are quarantined, not retried-into-green.

**Q42. If asked about the LLM tooling itself (provider-agnostic answer):**
The QE workflow is **model-agnostic** — it's a pipeline of prompt → generate → trace-check
→ execute → adversarial-review. The value is in the **guardrails and grounding**, not a
specific model. (Deeper agentic QE orchestration is **(planned)**.)

---

## 10. STAR Stories (grounded in this repo)

**STAR-1 — Building SUT-agnostic automation.**
- **S:** Healthcare QA frameworks are usually hard-coupled to one EHR, so coverage can't
  port across OpenMRS/OpenEMR/HAPI FHIR/SMART.
- **T:** Make one test suite run against multiple heterogeneous SUTs.
- **A:** Designed the **Resource Adapter Layer** (`core.adapter`) using Adapter/Strategy,
  exposing domain operations and mapping each SUT's REST/FHIR/HL7 dialect behind it.
- **R:** Same flow shape runs against **omiiCARE and OpenMRS — both 5/5 smoke PASS**; a new
  SUT now needs **one adapter**, not a rewrite.

**STAR-2 — Achieving full traceability at scale.**
- **S:** **1,795 requirements** and thousands of tests risked coverage gaps and orphans.
- **T:** Guarantee every requirement is tested and every test is justified.
- **A:** Reverse-engineered **22 spec docs**, authored **4,187 cases across 66 modules** in
  a 17-col CSV schema, and built an **RTM** as a hard exit-gate.
- **R:** **0 gaps, 0 untraced** — provable coverage for audit and release sign-off.

**STAR-3 — Fast feedback without sacrificing depth.**
- **S:** Browser/SUT tests are slow and would bog down every PR.
- **T:** Keep PR feedback fast while retaining heavy e2e depth.
- **A:** **Tag-partitioned** the suite — default `mvn -pl automation test` runs **98 unit
  tests**; `ui-e2e/api-e2e/bdd` run via `-Pe2e`. Added independent **Node smoke** suites.
- **R:** Fast green inner loop; deep suites on demand; **5/5 + 5/5** smokes as top-of-funnel.

**STAR-4 — Protecting a live green build while hardening QA.**
- **S:** Needed to add enterprise QA assets to a repo whose **98-test build was green**.
- **T:** Extend QA without risking the build.
- **A:** Constrained all additions to **non-Java assets** (MD/YAML/properties/Python/shell/
  k6/JMeter/ZAP), distinct filenames, **zero edits to `pom.xml` or Java**.
- **R:** Build stayed **green**; CI/perf/security/docs all expanded with **no regression**.

**STAR-5 — Safe performance/security testing of a third-party SUT.**
- **S:** The primary SUT is the **public** `o2.openmrs.org` demo — load/attack testing it
  is unethical and against the rules.
- **T:** Get perf/security signal without harming a shared resource.
- **A:** Hard-gated **k6/JMeter/ZAP to owned/local environments only**, stood up the docker
  stack (Postgres/Keycloak/WireMock/Prometheus/Grafana) for realistic local load/scan.
- **R:** Full perf + ZAP baseline coverage with **zero impact** on the public demo.

**STAR-6 — Risk-based prioritization for patient safety.**
- **S:** Couldn't run all 4,187 cases every cycle.
- **T:** Maximize safety signal under a time budget.
- **A:** Scored modules via the **risk register**, ran **patient-identity/orders/allergy**
  paths first, made them **release-blocking** in entry/exit gates.
- **R:** Highest-impact defects surface earliest; release decisions tie to **risk**, not
  raw pass-rate.

---

## 11. Rapid-Fire & Curveballs

| Question | One-line answer |
|---|---|
| Biggest tradeoff in the framework? | Adapter indirection tax vs. multi-SUT reuse — reuse wins. |
| Most brittle part? | UI selectors — mitigated by role/label locators + traces. |
| What would you do with 1 more sprint? | Wire the `-Pe2e` suites + Node smokes into a named nightly GH Actions workflow **(planned)**. |
| How do you measure QA success? | RTM coverage, escaped-defect rate on safety modules, smoke stability, build green-rate. |
| Flaky test policy? | Quarantine + root-cause, never blind retry. |
| Why CSV over a TMS? | Git-diffable, vendor-neutral, machine-parseable; import to any TMS later. |
| How big is the doc base? | 22 reverse-eng + 15 QA-mgmt docs, ~10k lines, 78 Mermaid diagrams. |
| Coverage number you're proud of? | 0 gaps / 0 untraced across 1,795 requirements. |

---

## 12. Questions to Ask the Interviewer

- How do you balance **patient-safety testing** against release velocity?
- Is your automation **SUT-coupled**, or is there a portability seam?
- What's your **PHI handling** in test/CI — synthetic-only, masking, or other?
- Where does **AI-assisted QE** sit today, and what guardrails exist?
- How mature is **FHIR/HL7 conformance** testing in your pipeline?

---

*Cross-references: `docs/portfolio/PORTFOLIO_GUIDE.md`, `INTERVIEW_GUIDE.md` (root),
`TEST_PYRAMID.md`, `TEST_STRATEGY.md`, `FHIR_GUIDE.md`, `HL7_GUIDE.md`,
`SECURITY_TESTING_GUIDE.md`, `PERFORMANCE_GUIDE.md`, `QUALITY_GATES.md`;
`docs/reverse-engineering/`, `docs/qa-management/`, `manual-testing/rtm/`.*
