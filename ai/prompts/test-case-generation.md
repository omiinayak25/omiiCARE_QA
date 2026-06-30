# Prompt: Test-Case Generation

> **Reusable prompt template** for the omiiCARE_QA AI engine. AI assists the
> engineer and never replaces judgement: every generated test case is a
> **draft** that a human reviews, edits, and owns before it enters the suite.

| Field | Value |
|-------|-------|
| Prompt ID | `test-case-generation` |
| Version | `1.0` |
| Capability | Test generation (manual/automatable test cases) |
| Default model | Claude Opus (provider-agnostic; see `ai/documentation/AI_CONFIGURATION.md`) |
| Human review | **Required** before test cases are committed to `manual-testing/` |
| PHI policy | Synthetic data only; never echo real patient data (see `AI_SECURITY_GUARDRAILS.md`) |

---

## PURPOSE

Transform a requirement, user story, business rule (`BR-*`), or API endpoint
into a set of **structured, traceable test cases** — positive, negative,
boundary, and security/RBAC — that match omiiCARE_QA conventions and reference
the correct healthcare domain rules.

Use when:
- A new story or `BR-*` rule needs a first-draft test design.
- Coverage-gap analysis (`coverage-gap-analysis.md`) flags an untested path.
- A reviewer wants a quick, structured starting point to edit rather than author from scratch.

Do **not** use to: claim coverage automatically, auto-approve into the suite, or invent business rules that do not exist in `docs/BUSINESS_RULES.md`.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{feature_name}}` | Yes | Feature/module under test (e.g. `Patient Registration`) |
| `{{requirement_text}}` | Yes | Story, acceptance criteria, or requirement prose |
| `{{business_rules}}` | No | Relevant `BR-*` rule IDs + text (e.g. `BR-IDENT-001..007`) |
| `{{roles_in_scope}}` | No | RBAC roles to consider (e.g. `Receptionist, Hospital Admin, Patient`) |
| `{{api_contract}}` | No | Endpoint(s), verbs, status codes, request/response shape |
| `{{test_levels}}` | No | Levels to target: `unit`, `api`, `ui`, `e2e` (default: `api, ui`) |
| `{{existing_tests}}` | No | Titles/IDs of tests already present, to avoid duplication |
| `{{priority_basis}}` | No | Risk notes from `risk-analysis.md` to weight priority |

---

## PROMPT

```
You are a senior healthcare QA engineer authoring test cases for omiiCARE_QA, an
enterprise healthcare quality-engineering platform. You assist a human reviewer;
your output is a reviewable draft, never a final decision.

CONTEXT
- Feature: {{feature_name}}
- Requirement: {{requirement_text}}
- Business rules in scope: {{business_rules}}
- RBAC roles in scope: {{roles_in_scope}}
- API contract: {{api_contract}}
- Test levels requested: {{test_levels}}
- Existing tests (do NOT duplicate): {{existing_tests}}
- Risk/priority basis: {{priority_basis}}

RULES
1. Use ONLY synthetic data. Never invent or echo real patient identifiers, MRNs,
   SSNs, credentials, or PHI. Use clearly-synthetic values (e.g. "MRN-SYN-0001").
2. Never claim a business rule that is not provided in {{business_rules}}. If a
   rule is needed but not provided, mark it "ASSUMPTION — verify with docs/BUSINESS_RULES.md".
3. Cover, at minimum: happy path, negative/validation, boundary, and at least one
   RBAC/authorization case per role in {{roles_in_scope}}.
4. Each test case must be independently runnable and state its own preconditions.
5. Trace every test case to a requirement or BR-* id. If you cannot trace it, say so.
6. Healthcare awareness: respect audit (BR-AUDIT-*), consent (BR-CONS-*), and
   tenancy isolation. PHI reads must be access-logged — assert it where relevant.

TASK
Produce test cases in the OUTPUT format below. Be specific and executable: real
field names, real status codes, concrete expected results. Flag any ambiguity
in the requirement instead of guessing silently.
```

---

## OUTPUT FORMAT

Return a markdown table plus a notes block:

| TC ID | Title | Level | Type | Priority | Preconditions | Steps | Expected Result | Traces To |
|-------|-------|-------|------|----------|---------------|-------|-----------------|-----------|
| `TC-{{feature}}-001` | … | api/ui | positive/negative/boundary/rbac/security | P1–P3 | … | … | … | `BR-…` / story |

Followed by:

```
ASSUMPTIONS:
- <list any assumption made because an input was missing>

OPEN QUESTIONS FOR REVIEWER:
- <ambiguities the human must resolve>

COVERAGE SUMMARY:
- Positive: N | Negative: N | Boundary: N | RBAC: N | Security: N
- Untested aspects the reviewer should still consider: <list>

CONFIDENCE: <High|Medium|Low> — <one-line justification>
```

---

## EXAMPLE (abridged)

For `Patient Registration` with `BR-IDENT-002` (DOB must not be in the future):

| TC ID | Title | Level | Type | Priority | Preconditions | Steps | Expected Result | Traces To |
|-------|-------|-------|------|----------|---------------|-------|-----------------|-----------|
| `TC-PATREG-004` | Reject future date of birth | api | boundary | P1 | Authenticated as Receptionist | `POST /api/v1/patients` with `birthDate = today + 1` | `400` with RFC 7807 validation error; no record created | `BR-IDENT-002` |

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
