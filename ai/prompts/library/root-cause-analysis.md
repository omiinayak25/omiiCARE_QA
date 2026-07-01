# Prompt: Root-Cause Analysis (RCA)

> **Reusable, provider-abstracted prompt template** for the omiiCARE_QA AI engine.
> Takes a triaged failure + context and produces a ranked, evidence-grounded RCA
> with a proposed fix and confidence. Companion to `ai/prompts/root-cause-analysis.md`;
> this `library/` copy is the v1.0.0-release, schema-strict edition.

| Field | Value |
|-------|-------|
| Prompt ID | `root-cause-analysis` (library) |
| Version | `1.0` |
| Capability | Root-cause analysis |
| Providers | Provider-abstracted — Claude / OpenAI / local. No provider-specific syntax. |
| Determinism | `temperature=0`, fixed seed where supported |
| Human review | **Required** — RCA is advisory; engineer owns the fix |
| PHI policy | Inputs PHI/secret-redacted before substitution |

---

## PURPOSE

Reason from a triaged failure (often the output of `failure-analysis.md`) plus
code/diff, logs, and history to the **most likely true root cause**, ranked
against alternates using the 5-Whys / fault-tree discipline, and propose a
concrete fix with verification steps.

**When to use:** confirmed non-flaky failure needing a durable fix; recurring
defect; post-incident analysis.
**When NOT to use:** initial triage (use `failure-analysis.md`); to ship a fix unreviewed.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{triage_summary}}` | No | Output of `failure-analysis` (signature, classification) |
| `{{failure_artifacts}}` | Yes | Logs/traces/HTTP dumps (redacted) |
| `{{code_context}}` | No | Relevant code/diff (no secrets) |
| `{{recent_changes}}` | No | Commits/config changes in the blast radius |
| `{{run_history}}` | No | When it started failing, frequency |
| `{{business_rules}}` | No | `BR-*` rules implicated (e.g., consent, double-booking) |
| `{{environment}}` | No | Env/build/version where it reproduces |

---

## PROMPT TEMPLATE

```
You are a root-cause analyst for omiiCARE_QA (Spring Boot 3 / Java 21 SUT;
Playwright/Selenium/RestAssured/Cucumber automation; FHIR R4 + HL7 v2; Postgres).
You assist a human engineer; produce a ranked, evidence-grounded RCA. Never assert
a cause you cannot tie to evidence.

CONTEXT
- Triage summary: {{triage_summary}}
- Failure artifacts (redacted): {{failure_artifacts}}
- Code context/diff: {{code_context}}
- Recent changes: {{recent_changes}}
- Run history: {{run_history}}
- Business rules: {{business_rules}}
- Environment: {{environment}}

METHOD
1. Redact PHI/secrets.
2. State the observable failure precisely (quote evidence).
3. Apply 5-Whys: chain each cause to the next with evidence; stop at the first
   cause that, if fixed, prevents recurrence.
4. Build a short fault tree of candidate causes; for each, rate likelihood and
   cite supporting/contradicting evidence.
5. Select the PRIMARY root cause. Distinguish defect category: product code,
   test/automation, data/seed, environment/infra, requirement/spec gap.
6. Propose a fix AND a verification step (the test/assert that proves it fixed).
7. Note prevention (guardrail, test to add, monitor).
8. Output ONLY the OUTPUT SCHEMA with confidence.
```

---

## OUTPUT SCHEMA

```yaml
observable_failure: "<precise, evidence-quoted statement>"
five_whys:
  - why: "<observed>"
    because: "<cause, with evidence>"
candidate_causes:
  - cause: "<candidate>"
    likelihood: "High | Medium | Low"
    evidence_for: "<...>"
    evidence_against: "<...>"
primary_root_cause: "<one-liner>"
defect_category: "product | automation | data | environment | requirement"
proposed_fix: "<concrete change>"
verification_step: "<test/assert that proves the fix>"
prevention: "<guardrail / test / monitor to add>"
confidence: "High | Medium | Low — <justification>"
```

---

## GUARDRAILS

- **Evidence-bound:** no cause without cited evidence; contradicting evidence shown.
- **Advisory:** RCA is a draft; the engineer owns and verifies the fix.
- **Redacted-only:** no PHI/secrets in reasoning or output.
- **No fabricated certainty:** insufficient evidence ⇒ explicit `Low` confidence.
- **Deterministic & provider-neutral.**

---

## EXAMPLE (abridged)

```yaml
observable_failure: "401 on GET /api/patients for role NURSE after token-scope change"
five_whys:
  - why: "Request returns 401"
    because: "Token lacks 'patient:read' scope (evidence: JWT claims dump)"
  - why: "Scope missing"
    because: "RBAC mapping for NURSE not updated after BR-RBAC-007 change"
candidate_causes:
  - cause: "RBAC mapping regression"
    likelihood: "High"
    evidence_for: "Diff removed nurse->patient:read mapping"
    evidence_against: "none"
primary_root_cause: "NURSE role lost patient:read scope in RBAC config change."
defect_category: "product"
proposed_fix: "Restore patient:read scope for NURSE per BR-RBAC-007."
verification_step: "api-e2e: NURSE GET /api/patients expects 200."
prevention: "Add RBAC matrix test covering all roles x core endpoints."
confidence: "High — diff + JWT claims align."
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-07-01 | AI QA Engineer | Initial (v1.0.0 prompt library) |
