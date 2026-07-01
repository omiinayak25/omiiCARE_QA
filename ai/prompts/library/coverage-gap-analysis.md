# Prompt: Coverage-Gap Analysis

> **Reusable, provider-abstracted prompt template** for the omiiCARE_QA AI engine.
> Compares requirements against existing test coverage and surfaces prioritized
> gaps. Companion to `ai/prompts/coverage-gap-analysis.md`; this `library/` copy
> is the v1.0.0-release, schema-strict edition.

| Field | Value |
|-------|-------|
| Prompt ID | `coverage-gap-analysis` (library) |
| Version | `1.0` |
| Capability | Coverage analysis |
| Providers | Provider-abstracted — Claude / OpenAI / local. No provider-specific syntax. |
| Determinism | `temperature=0`, fixed seed where supported |
| Human review | **Required** — gaps are a backlog proposal |
| PHI policy | No PHI; requirements/inventory metadata only |

---

## PURPOSE

Compare the requirements catalog (`docs/requirements/requirements-catalog.md`,
1,795 requirements) and RTM (`manual-testing/rtm/`, currently 0 gaps / 0 untraced)
against the test inventory (4,187 manual cases + tagged automation) to surface
**coverage gaps** — untested requirements, missing negative/boundary/RBAC/security
paths, and under-covered high-risk areas — ranked by risk.

**When to use:** release readiness, post-requirement-change audit, new module
onboarding, periodic coverage health check.
**When NOT to use:** to assert "100% covered" — output is a prioritized proposal.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{requirements}}` | Yes | `REQ-*` set (text + IDs) in scope |
| `{{rtm}}` | No | Requirement↔test mapping (RTM.csv/md) |
| `{{test_inventory}}` | No | Existing cases/tags by module |
| `{{risk_register}}` | No | Risk-based priorities (from qa-management) |
| `{{coverage_dimensions}}` | No | Dimensions to check (positive/negative/boundary/RBAC/security/a11y/perf) |
| `{{scope}}` | No | Module(s)/area(s) to bound the analysis |

---

## PROMPT TEMPLATE

```
You are a coverage analyst for omiiCARE_QA (healthcare QA platform). Compare
requirements to existing tests and surface prioritized coverage gaps. You assist a
human; output is a backlog proposal, not a verdict of completeness.

CONTEXT
- Requirements in scope: {{requirements}}
- RTM: {{rtm}}
- Test inventory: {{test_inventory}}
- Risk register: {{risk_register}}
- Coverage dimensions to check: {{coverage_dimensions}}
- Scope: {{scope}}

METHOD
1. Build a coverage matrix: each REQ-* x dimension (positive, negative, boundary,
   RBAC/security, accessibility, performance) -> covered / partial / missing,
   citing the test ref when covered.
2. Flag any REQ-* with NO mapped test (untraced) as a Critical gap.
3. For covered requirements, flag MISSING dimensions (e.g., no negative/boundary,
   no RBAC for a privileged action, no audit-log assertion, no FHIR/HL7 validation).
4. Weight gaps by risk (patient-safety, data-integrity, compliance highest), using
   the risk register when supplied.
5. Propose concrete test cases to close top gaps (hand off to test-generation).
6. Do NOT claim coverage is complete; report residual unknowns.
7. Output ONLY the OUTPUT SCHEMA.
```

---

## OUTPUT SCHEMA

```yaml
coverage_matrix:
  - requirement_id: "REQ-XXXX"
    positive: "covered:TC-id | partial | missing"
    negative: "covered | partial | missing"
    boundary: "covered | partial | missing"
    rbac_security: "covered | partial | missing"
    accessibility: "covered | n/a | missing"
    performance: "covered | n/a | missing"
prioritized_gaps:
  - gap: "<requirement + missing dimension>"
    risk: "Critical | High | Medium | Low"
    rationale: "<safety/integrity/compliance>"
    proposed_test: "<one-line case to generate>"
untraced_requirements: ["REQ-XXXX"]
summary:
  requirements_in_scope: 0
  fully_covered: 0
  partial: 0
  uncovered: 0
residual_unknowns: ["<what could not be assessed>"]
confidence: "High | Medium | Low — <justification>"
```

---

## GUARDRAILS

- **No completeness claims:** report gaps + residual unknowns, never "100%".
- **Risk-ranked:** safety/integrity/compliance gaps surface first.
- **Traceable:** covered cells cite a test ref; untraced REQs flagged Critical.
- **Backlog proposal:** human prioritizes; pairs with `test-generation` to close gaps.
- **Deterministic & provider-neutral; no PHI.**

---

## EXAMPLE (abridged)

```yaml
coverage_matrix:
  - requirement_id: "REQ-0142"
    positive: "covered:TC-REQ-0142-01"
    negative: "covered:TC-REQ-0142-02"
    boundary: "missing"
    rbac_security: "partial"
    accessibility: "n/a"
    performance: "missing"
prioritized_gaps:
  - gap: "REQ-0142 boundary: slot at day-boundary / timezone edge untested"
    risk: "High"
    rationale: "Scheduling data-integrity across DST/timezones"
    proposed_test: "Boundary TC: book slot at 23:59 across DST transition"
  - gap: "REQ-0142 RBAC: Receptionist booking permission unverified"
    risk: "Medium"
    rationale: "Privilege correctness"
    proposed_test: "RBAC TC: Receptionist POST /appointments expects 403/201 per BR"
untraced_requirements: []
summary:
  requirements_in_scope: 1
  fully_covered: 0
  partial: 1
  uncovered: 0
residual_unknowns: ["Perf coverage not assessable without k6 scope"]
confidence: "Medium — inventory mapped; perf/a11y dims thin."
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-07-01 | AI QA Engineer | Initial (v1.0.0 prompt library) |
