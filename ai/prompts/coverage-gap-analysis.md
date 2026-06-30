# Prompt: Coverage-Gap Analysis

> **Reusable prompt template** for the omiiCARE_QA AI engine. Compares what
> exists against what should be tested and proposes the highest-value gaps to
> close — a reviewable recommendation, not an automatic coverage claim.

| Field | Value |
|-------|-------|
| Prompt ID | `coverage-gap-analysis` |
| Version | `1.0` |
| Capability | Coverage analysis |
| Default model | Claude Opus (provider-agnostic) |
| Human review | **Required** — coverage judgements are advisory |
| PHI policy | Synthetic examples only |

---

## PURPOSE

Given the set of requirements/business rules and the set of existing tests,
identify **coverage gaps** — untested rules, missing negative/boundary/RBAC
cases, untested healthcare invariants — and propose prioritized tests to close
them, traced to `BR-*`/RTM.

Use after a feature lands, before a release gate, or when the RTM shows thin
coverage.

Do **not** use to: assert a numeric coverage percentage as fact (it is an
estimate), or to claim a rule is covered without a matching test.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{requirements_or_rules}}` | Yes | `BR-*` rules / acceptance criteria in scope |
| `{{existing_tests}}` | Yes | Test inventory: ids, titles, levels, tags |
| `{{feature_scope}}` | No | Module/feature boundary for the analysis |
| `{{test_levels}}` | No | Levels to assess (unit/api/ui/e2e) |
| `{{risk_weighting}}` | No | Risk notes from `risk-analysis.md` to prioritize gaps |
| `{{rtm}}` | No | RTM rows to reconcile against |

---

## PROMPT

```
You are a coverage analyst for omiiCARE_QA, a healthcare QA platform. You assist
a human; your output is a prioritized gap list, not a guarantee of coverage.

CONTEXT
- Requirements/rules: {{requirements_or_rules}}
- Existing tests: {{existing_tests}}
- Feature scope: {{feature_scope}}
- Test levels: {{test_levels}}
- Risk weighting: {{risk_weighting}}
- RTM: {{rtm}}

METHOD
1. Build a coverage matrix: each rule/criterion vs the test(s) that exercise it,
   across the requested levels. Mark COVERED / PARTIAL / UNCOVERED with the
   evidence (which test).
2. For PARTIAL/UNCOVERED, identify the missing TYPE: positive, negative, boundary,
   RBAC/authorization, security, healthcare-invariant (audit/consent/tenancy/
   state-machine), interoperability (FHIR/HL7).
3. Detect over-coverage/duplication too (redundant tests of the same path).
4. Prioritize gaps by risk x likelihood-of-defect; weight patient-safety and
   data-integrity rules highest.
5. Propose a specific test for each top gap (title, level, type, trace).
6. Be honest about estimation: any percentage is an ESTIMATE, label it so.

TASK
Produce the matrix, the gap list, and proposals in the OUTPUT format.
```

---

## OUTPUT FORMAT

```
COVERAGE MATRIX:
| Rule/AC | Level(s) | Status (Covered/Partial/Uncovered) | Covered By | Missing Type |
|---------|----------|-------------------------------------|------------|--------------|

PRIORITIZED GAPS (highest value first):
| # | Gap | Risk | Missing Type | Proposed Test (title/level) | Traces To |
|---|-----|------|--------------|-----------------------------|-----------|

REDUNDANCY / OVER-COVERAGE:
- <duplicate tests that could be consolidated>

ESTIMATED COVERAGE (estimate, not measured):
- By rule: ~N%   | By level: api ~N%, ui ~N%

CONFIDENCE: <High|Medium|Low> — <justification>
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
