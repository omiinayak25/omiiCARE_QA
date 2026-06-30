# Prompt: BDD Scenario Generation (Gherkin)

> **Reusable prompt template** for the omiiCARE_QA AI engine. Output is a
> reviewable Gherkin draft for the engineer to refine; the human owns the
> committed `.feature` file and its step bindings.

| Field | Value |
|-------|-------|
| Prompt ID | `bdd-scenario-generation` |
| Version | `1.0` |
| Capability | Test generation (BDD / Cucumber) |
| Default model | Claude Opus (provider-agnostic) |
| Human review | **Required** before scenarios reach `automation/src/test/resources/features/` |
| PHI policy | Synthetic data only |

---

## PURPOSE

Generate **Cucumber/Gherkin** `Feature` files (`.feature`) that follow the
omiiCARE_QA house style — declarative, business-readable, reusing existing step
vocabulary — for healthcare flows such as patient registration, appointment
booking, encounters, and prescriptions.

Use when a story or `BR-*` rule should be expressed as executable specification,
or when extending an existing feature with negative/boundary scenarios.

Do **not** use to: invent steps that have no plausible binding, or to write
imperative UI-click scripts (those belong in Playwright/Selenium page logic).

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{feature_title}}` | Yes | Feature name (e.g. `Appointment booking`) |
| `{{user_role}}` | Yes | Actor for the `As a …` clause (e.g. `Receptionist`) |
| `{{capability}}` | Yes | The `I want … So that …` business value |
| `{{acceptance_criteria}}` | Yes | Criteria to convert into scenarios |
| `{{business_rules}}` | No | Relevant `BR-*` IDs + text |
| `{{existing_steps}}` | No | Existing Given/When/Then phrases to reuse |
| `{{tags}}` | No | Tags to apply (default `@bdd @api`; add `@ui`, `@regression`, `@rbac`) |
| `{{scenario_types}}` | No | Which to include: positive, negative, boundary, rbac (default: all) |

---

## PROMPT

```
You are a BDD specialist writing Gherkin for omiiCARE_QA, a healthcare QA
platform using Cucumber over RestAssured (API) and Playwright/Selenium (UI).
You assist a human; your scenarios are a draft to be reviewed and bound to steps.

CONTEXT
- Feature: {{feature_title}}
- Role: {{user_role}}
- Capability: As a {{user_role}}, I want {{capability}}
- Acceptance criteria: {{acceptance_criteria}}
- Business rules: {{business_rules}}
- Existing step phrases to REUSE where possible: {{existing_steps}}
- Tags: {{tags}}
- Scenario types: {{scenario_types}}

HOUSE STYLE (match it exactly)
- One Feature per file; start with tags, then `Feature:`, then the
  `As a / I want / So that` narrative.
- Use a `Background:` for shared preconditions (e.g. API availability, auth).
- Scenarios are DECLARATIVE and business-readable — describe intent, not clicks.
- Prefer reusing {{existing_steps}}; introduce a new step only when necessary and
  keep it generic and reusable.
- Use Scenario Outline + Examples for boundary/data-driven cases.
- Synthetic data only. Never use real PHI, MRNs, or credentials.

RULES
1. Every scenario must trace to an acceptance criterion or BR-* id (as a comment).
2. Include at least one negative and one RBAC/authorization scenario when relevant.
3. Healthcare awareness: where a step reads PHI, add a Then that asserts the access
   is audit-logged (BR-AUDIT-002) if applicable.
4. Do not assert implementation details (DB columns, internal IDs) at UI level.
5. Flag any acceptance criterion you could not express as a scenario.

TASK
Produce a complete, valid `.feature` file plus a short notes block.
```

---

## OUTPUT FORMAT

A fenced `gherkin` block containing the full feature file, followed by:

```
NEW STEPS INTRODUCED (need binding):
- <Given/When/Then phrase> → suggested binding intent

TRACEABILITY:
- <Scenario name> → <AC or BR-* id>

OPEN QUESTIONS FOR REVIEWER:
- <ambiguities>

CONFIDENCE: <High|Medium|Low> — <justification>
```

---

## EXAMPLE (abridged)

```gherkin
@bdd @api @regression
Feature: Appointment booking
  As a Receptionist
  I want to book appointments within provider availability
  So that patients are scheduled without conflicts

  Background:
    Given the omiiCARE API is available
    And I am authenticated as the demo receptionist

  # Traces: BR-APPT-003
  Scenario: Reject a double-booked provider slot
    Given a provider has a BOOKED appointment from "09:00" to "09:30"
    When I book the same provider an overlapping slot from "09:15" to "09:45"
    Then the API responds with status 409
    And the conflict reason names double-booking
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
