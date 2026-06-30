# AI Capabilities

> **Purpose.** Catalogue every AI-Native QE capability in omiiCARE_QA with the
> same contract: what it does, its inputs and outputs, how it is reviewed, and
> its confidence and limitations. AI assists the engineer and never replaces
> judgement — each capability is optional, transparent, explainable, and
> reviewable.

## Scope

- One section per capability, each mapped to its prompt(s) in `ai/prompts/`.
- The review path and confidence/limitations for every capability.
- Cross-cutting healthcare awareness and execution reporting.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| AI QA Engineer | Keep capabilities aligned with the prompt library |
| SDET / QA Engineer | Invoke capabilities as assistance; review every output |
| Human reviewer | Approve AI-assisted artifacts before they land |

---

## 0. Common Contract

Every capability below shares this contract:

- **Output is a draft.** A human reviews, edits, and owns it before it is used.
- **Provenance recorded.** Prompt ID/version, provider, model, and timestamp are
  audited (`AI_CONFIGURATION.md`).
- **Synthetic-only inputs/outputs.** PHI/secrets are redacted before the model
  sees them (`AI_SECURITY_GUARDRAILS.md`).
- **Confidence + assumptions.** Every output states a confidence rating and flags
  assumptions and open questions.

---

## 1. Requirement Analysis

- **What it does:** Converts a requirement/story into testable acceptance criteria,
  edge/negative/boundary conditions, risks, and traceability to `BR-*`/RTM.
- **Inputs:** requirement text, actors/roles, business rules, domain context, NFRs.
- **Outputs:** AC table (Given/When/Then), condition enumeration, risk table,
  traceability gaps.
- **Review:** Analyst confirms AC reflect business intent; PROPOSED criteria are
  validated; gaps are reconciled with the RTM.
- **Confidence/limitations:** Strong at surfacing missing cases; cannot know
  unstated business intent — flags it instead of inventing it.
- **Prompt:** `requirement-analysis`.

## 2. Test Generation

- **What it does:** Drafts test cases, BDD scenarios, and API tests matching
  omiiCARE_QA conventions and rules.
- **Inputs:** feature/requirement, `BR-*`, roles, API contract, existing tests.
- **Outputs:** test-case tables, Gherkin `.feature` files, RestAssured skeletons —
  all traced to rules.
- **Review:** Engineer verifies executability, binds new BDD steps, edits before
  committing to `manual-testing/` or `automation/`.
- **Confidence/limitations:** Excellent for breadth and structure; generated code
  is a skeleton — it must be run and made green by a human. Never auto-claims coverage.
- **Prompts:** `test-case-generation`, `bdd-scenario-generation`, `api-test-generation`.

## 3. Failure Analysis & Root-Cause Analysis

- **What it does:** Triages raw artifacts (logs, stack traces, Allure, screenshots)
  to a probable cause, then reasons through ranked root causes with a fix.
- **Inputs:** failure artifacts (redacted), test identity, environment, run history,
  recent changes.
- **Outputs:** failure signature + classification + next steps (triage); ranked
  candidate causes + recommended fix + regression-prevention (RCA).
- **Review:** Engineer validates the cause against the system before acting; flaky
  vs real is confirmed, not assumed.
- **Confidence/limitations:** Fast first-responder; quality depends on artifact
  completeness; distinguishes product/automation/env/data/flaky but cannot run the
  experiment that confirms — it names it.
- **Prompts:** `failure-analysis`, `root-cause-analysis`.

## 4. Bug-Report Assistant

- **What it does:** Turns confirmed failure evidence into a complete, traceable
  bug report.
- **Inputs:** summary, observed/expected, repro steps, environment, evidence, `BR-*`.
- **Outputs:** titled report with steps, expected vs actual, redacted evidence,
  suggested severity/priority, FACT-vs-INFERENCE split.
- **Review:** Reporter verifies every fact and repro step; never files fabricated steps.
- **Confidence/limitations:** Produces consistent, searchable tickets; will refuse
  to invent repro steps when none are provided.
- **Prompt:** `bug-report-drafting`.

## 5. Coverage Analysis

- **What it does:** Maps existing tests against rules/RTM and proposes prioritized
  gaps to close.
- **Inputs:** requirements/rules, test inventory, risk weighting, RTM.
- **Outputs:** coverage matrix, prioritized gap list, redundancy notes, estimated
  coverage (labeled an estimate).
- **Review:** QA lead confirms gap priorities; percentages treated as estimates,
  not measured metrics.
- **Confidence/limitations:** Strong at structured gap-finding; coverage percentages
  are heuristic, not instrumented.
- **Prompt:** `coverage-gap-analysis`.

## 6. Regression Analysis

- **What it does:** Estimates a change's impact radius and recommends a regression set.
- **Inputs:** change/diff, changed artifacts, dependency map, `BR-*`, test inventory.
- **Outputs:** impact map, tiered regression selection (must/should/optional), new
  tests needed, blind-spot caveats.
- **Review:** Release owner confirms scope; safety-critical regression is never
  dropped on AI's recommendation alone.
- **Confidence/limitations:** Good at reasoning over visible dependencies; limited by
  the dependency info it is given — it flags what it cannot see.
- **Prompt:** `regression-impact-analysis`.

## 7. Risk Analysis

- **What it does:** Produces a risk-based-testing register with likelihood, impact,
  and mitigating tests.
- **Inputs:** subject, scope, `BR-*`, known issues, constraints, risk appetite.
- **Outputs:** ordered risk register, release-gating risks, residual-risk statement.
- **Review:** QA architect confirms ratings; safety/PHI risks need human sign-off
  to downgrade.
- **Confidence/limitations:** Surfaces categories engineers miss; ratings are
  judgement inputs, not verdicts; never concludes "safe" or asserts certification.
- **Prompt:** `risk-analysis`.

## 8. Documentation & Code Review

- **What it does:** Drafts house-style documentation; performs adversarial review of
  test/automation code.
- **Inputs:** doc purpose + source material; or code diff + standards.
- **Outputs:** markdown doc draft with Version History; prioritized findings table
  (≥10 issues, never "looks good").
- **Review:** Author/reviewer edits docs and confirms findings; AI flags, the human
  merges.
- **Confidence/limitations:** Documentation is grounded only in provided facts
  (marks `[NEEDS INPUT]`); review findings still need human confirmation of real defects.
- **Prompts:** `documentation-assistant`, `code-review-checklist`.

## 9. Test Data (SQL & FHIR)

- **What it does:** Generates safe-by-default verification/setup SQL and synthetic,
  conformant FHIR R4 payloads.
- **Inputs:** intent/tables/conditions (SQL); resource type/scenario/code systems (FHIR).
- **Outputs:** read-biased SQL with safety review; synthetic FHIR resources with
  correct code-system URIs and validation expectation.
- **Review:** Engineer reviews SQL before running on shared DBs; validates FHIR
  against R4 StructureDefinitions.
- **Confidence/limitations:** SQL defaults to read-only and refuses unguarded
  mutations; FHIR is synthetic and structurally checked, not a clinical-accuracy claim.
- **Prompts:** `sql-generation`, `fhir-payload-generation`.

## 10. Healthcare Awareness (cross-cutting)

- **What it does:** Ensures every capability respects audit (`BR-AUDIT-*`), consent
  (`BR-CONS-*`), tenancy isolation, state machines, and correct FHIR/HL7 coding.
- **Review:** Domain reviewer confirms clinical/standards fidelity.
- **Confidence/limitations:** Encodes documented rules; HIPAA-like practice only —
  never a certification claim.

## 11. Execution Reports

- **What it does:** Summarizes test runs and triage into stakeholder-readable reports
  (pass/fail trends, top failures, probable causes, recommended actions).
- **Inputs:** run results/Allure summaries (redacted) + triage outputs.
- **Outputs:** concise execution summary with links to evidence and confidence.
- **Review:** QA lead confirms the narrative matches the raw results.
- **Confidence/limitations:** A summarization aid; the authoritative result remains
  the raw report.
- **Prompts:** `documentation-assistant` + `failure-analysis`.

---

## Confidence Legend

| Rating | Meaning |
|--------|---------|
| High | Evidence is strong and complete; reviewer expected to confirm quickly |
| Medium | Reasonable but gaps exist; reviewer should validate key claims |
| Low | Speculative or input-starved; treat as a prompt for human investigation |

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
