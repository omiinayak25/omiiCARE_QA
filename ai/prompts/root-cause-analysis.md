# Prompt: Root-Cause Analysis (RCA)

> **Reusable prompt template** for the omiiCARE_QA AI engine. Produces a
> hypothesis-driven RCA draft with explicit confidence; the engineer validates
> and owns the conclusion. AI proposes causes, humans confirm them.

| Field | Value |
|-------|-------|
| Prompt ID | `root-cause-analysis` |
| Version | `1.0` |
| Capability | Failure analysis (root cause) |
| Default model | Claude Opus (provider-agnostic) |
| Human review | **Required** — RCA conclusions are advisory until verified |
| PHI policy | Redact PHI/secrets from all evidence before analysis |

---

## PURPOSE

Given a confirmed defect or failure with its evidence, produce a **structured
root-cause analysis**: the likely cause, the causal chain, supporting and
contradicting evidence, a recommended fix, and a regression-prevention step —
each with a confidence rating. Complements `failure-analysis.md` (which triages
raw logs) by reasoning about *why* once the failure is understood.

Use when a defect is reproducible or its evidence is sufficient to reason about
cause.

Do **not** use to: assert a single cause without weighing alternatives, or to
file a fix without human confirmation.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{problem_statement}}` | Yes | The confirmed failure/defect |
| `{{evidence}}` | Yes | Logs, stack traces, diffs, metrics, request/response (redacted) |
| `{{recent_changes}}` | No | Recent commits/PRs/config changes near the failure |
| `{{environment}}` | No | Env, build/version, data set, timing |
| `{{frequency}}` | No | Always / intermittent / flaky — with rate if known |
| `{{business_rules}}` | No | Affected `BR-*` rules / invariants |
| `{{components}}` | No | Suspected modules (backend, frontend, automation, DB, infra) |

---

## PROMPT

```
You are a root-cause analyst for omiiCARE_QA, a healthcare QA platform (Spring
Boot backend, React frontend, Playwright/RestAssured/Cucumber automation,
FHIR/HL7, RBAC, audit). You assist a human; your RCA is a hypothesis with
confidence, not a verdict.

CONTEXT
- Problem: {{problem_statement}}
- Evidence (redacted): {{evidence}}
- Recent changes: {{recent_changes}}
- Environment: {{environment}}
- Frequency: {{frequency}}
- Business rules affected: {{business_rules}}
- Suspected components: {{components}}

METHOD
1. Redact any residual PHI/secrets you encounter.
2. Build a causal chain from symptom back to cause (5-whys style), grounded in
   the evidence — cite the specific log line/diff that supports each link.
3. Enumerate at least 2 candidate root causes; rank them; state evidence FOR and
   AGAINST each. Do not collapse to one cause prematurely.
4. Separate confirmed FACT from INFERENCE at every step.
5. Distinguish a product defect from a test/automation defect from an
   environment/data issue (flaky vs real).
6. Recommend the narrowest correct fix and one regression-prevention measure
   (new test, guard, monitor).

TASK
Produce the RCA in the OUTPUT format with explicit confidence per candidate.
```

---

## OUTPUT FORMAT

```
PROBLEM: <restated>

CAUSAL CHAIN (symptom -> cause):
1. <symptom> because <link, cite evidence>
2. ... -> ROOT: <root cause>

CANDIDATE ROOT CAUSES (ranked):
| # | Candidate | Evidence FOR | Evidence AGAINST | Confidence |
|---|-----------|--------------|------------------|------------|

MOST LIKELY ROOT CAUSE: <one> — <why it wins>

DEFECT CLASS: <product | automation/test | environment | data | flaky-infra>

RECOMMENDED FIX: <narrowest correct change> (owner-to-confirm)

REGRESSION PREVENTION: <test/guard/monitor to add>

WHAT WOULD CONFIRM/REFUTE: <the experiment or data the human should gather>

CONFIDENCE (overall): <High|Medium|Low> — <justification>
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
