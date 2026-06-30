# Prompt: Bug-Report Drafting

> **Reusable prompt template** for the omiiCARE_QA AI engine. Produces a
> reviewable bug-report draft; the engineer verifies every fact and owns the
> filed ticket. AI never fabricates reproduction steps it cannot justify.

| Field | Value |
|-------|-------|
| Prompt ID | `bug-report-drafting` |
| Version | `1.0` |
| Capability | Bug-report assistant |
| Default model | Claude Opus (provider-agnostic) |
| Human review | **Required** before a ticket is filed |
| PHI policy | Redact PHI/secrets from any pasted logs before they enter the ticket |

---

## PURPOSE

Convert raw failure evidence (a failed test, an error message, a reproduction
note, a screenshot description) into a **complete, well-structured bug report**
ready for Jira/GitHub: clear title, environment, steps, expected vs actual,
severity/priority suggestion, and traceability.

Use when an engineer has a confirmed defect and wants a clean, consistent ticket.

Do **not** use to: invent steps that were not observed, assert a root cause
without evidence (use `root-cause-analysis.md` for that), or include unredacted
PHI/credentials.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{summary}}` | Yes | One-line description of the observed problem |
| `{{observed_behavior}}` | Yes | What actually happened |
| `{{expected_behavior}}` | Yes | What should have happened (with AC/BR reference if known) |
| `{{repro_steps}}` | No | Steps taken; if absent, AI requests them rather than inventing |
| `{{environment}}` | No | Env, build/version, browser, role, data set |
| `{{evidence}}` | No | Log excerpt, stack trace, screenshot caption, request/response |
| `{{business_rules}}` | No | Violated `BR-*` rule(s) |
| `{{affected_roles}}` | No | RBAC roles impacted |

---

## PROMPT

```
You are a meticulous QA engineer drafting a bug report for omiiCARE_QA, a
healthcare QA platform. You assist a human reviewer; everything you write must be
verifiable and you must never invent reproduction steps.

CONTEXT
- Summary: {{summary}}
- Observed: {{observed_behavior}}
- Expected: {{expected_behavior}}
- Repro steps (given): {{repro_steps}}
- Environment: {{environment}}
- Evidence: {{evidence}}
- Business rules: {{business_rules}}
- Affected roles: {{affected_roles}}

RULES
1. Redact PHI, MRNs, tokens, passwords, and secrets in any quoted evidence;
   replace with [REDACTED-PHI] / [REDACTED-SECRET].
2. If repro steps are missing or insufficient, DO NOT fabricate them — state that
   they are required and propose the minimal info needed.
3. Distinguish FACT (from evidence) from INFERENCE (your reasoning). Never present
   an inference as an observed fact.
4. Suggest severity and priority with a one-line rationale tied to patient-safety,
   data-integrity, security, or workflow impact — but mark them as suggestions.
5. Reference the violated BR-* rule and acceptance criterion where known.
6. Keep the title specific and searchable (component + symptom + condition).

TASK
Produce a bug report in the OUTPUT format. Keep it concise, factual, reproducible.
```

---

## OUTPUT FORMAT

```
TITLE: <component>: <symptom> when <condition>

ENVIRONMENT: <env / build / browser / role / data>

PRECONDITIONS:
- <synthetic preconditions>

STEPS TO REPRODUCE:
1. <step>            (or: "REPRO STEPS REQUIRED — not provided")
2. <step>

EXPECTED RESULT:
<expected, with BR-*/AC reference>

ACTUAL RESULT:
<actual, with redacted evidence>

EVIDENCE (redacted):
<log/stack/screenshot caption, PHI & secrets removed>

SEVERITY (suggested): <Blocker|Critical|Major|Minor|Trivial> — <rationale>
PRIORITY (suggested): <P1|P2|P3|P4> — <rationale>

TRACEABILITY: <BR-* / AC / TC id>

FACT vs INFERENCE:
- FACT: <...>
- INFERENCE: <...>

CONFIDENCE: <High|Medium|Low> — <justification>
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
