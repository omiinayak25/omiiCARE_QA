# Prompt: Regression-Impact Analysis

> **Reusable prompt template** for the omiiCARE_QA AI engine. Given a change,
> proposes the blast radius and the regression tests to run — a reviewable
> recommendation the engineer validates before trusting it as a test-selection.

| Field | Value |
|-------|-------|
| Prompt ID | `regression-impact-analysis` |
| Version | `1.0` |
| Capability | Regression analysis |
| Default model | Claude Opus (provider-agnostic) |
| Human review | **Required** — never the sole basis for skipping tests in a release gate |
| PHI policy | Synthetic examples only |

---

## PURPOSE

Given a code/config/schema change (diff, PR, or description), estimate the
**impact radius** — affected modules, endpoints, business rules, and user flows —
and recommend a **prioritized regression set** plus newly-needed tests. Helps
focus regression effort without losing safety-critical coverage.

Use before merging a change, when scoping a regression cycle, or when triaging a
risky refactor.

Do **not** use to: justify skipping safety-critical regression, or to assert
impact without naming the evidence (which files/rules).

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{change_description}}` | Yes | Diff, PR summary, or description of the change |
| `{{changed_artifacts}}` | No | Files/modules/endpoints touched |
| `{{dependency_map}}` | No | Known callers/consumers of changed code |
| `{{business_rules}}` | No | `BR-*` rules near the change |
| `{{test_inventory}}` | No | Existing tests with tags/levels to select from |
| `{{release_context}}` | No | Target env, gate, time budget |

---

## PROMPT

```
You are a regression strategist for omiiCARE_QA, a healthcare QA platform. You
assist a human; your impact assessment is a recommendation, and safety-critical
coverage is never dropped on AI's say-so alone.

CONTEXT
- Change: {{change_description}}
- Changed artifacts: {{changed_artifacts}}
- Dependency map: {{dependency_map}}
- Business rules near change: {{business_rules}}
- Test inventory: {{test_inventory}}
- Release context: {{release_context}}

METHOD
1. Map the BLAST RADIUS: directly-changed components, then transitive consumers
   (callers, shared utilities, contracts, DB schema, FHIR/HL7 mappings, UI flows).
   Cite the specific artifact/rule for each impacted area.
2. Classify impact per area: DIRECT, ADJACENT, or REMOTE-but-possible.
3. Flag SAFETY-CRITICAL paths (patient identity, scheduling conflicts, Rx safety,
   billing/coding correctness, consent, audit). These are ALWAYS in the regression
   set when adjacent — mark them MANDATORY.
4. Select the regression set from {{test_inventory}}: must-run, should-run, optional
   — ordered by risk; justify each selection with the impacted rule/component.
5. Identify NEW tests needed for behavior the change introduces or that has no
   covering test today.
6. Note what you CANNOT see (missing dependency info) as a coverage caveat.

TASK
Produce the impact map and the regression recommendation in the OUTPUT format.
```

---

## OUTPUT FORMAT

```
IMPACT MAP:
| Area | Impact (Direct/Adjacent/Remote) | Why (evidence: file/rule) | Safety-Critical? |
|------|---------------------------------|---------------------------|------------------|

REGRESSION SELECTION:
| Tier | Test / Suite | Reason | Traces To |
|------|--------------|--------|-----------|
(Tiers: MUST-RUN, SHOULD-RUN, OPTIONAL)

NEW TESTS NEEDED:
- <title / level / what it guards>

CAVEATS (blind spots):
- <missing info that limits confidence>

RECOMMENDED SCOPE: <smoke | targeted | full-regression> — <one-line rationale>

CONFIDENCE: <High|Medium|Low> — <justification>
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
