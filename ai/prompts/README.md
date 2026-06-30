# omiiCARE_QA Prompt Library

> **Purpose.** Index and contract for the reusable prompt templates that power
> omiiCARE_QA's AI-Native Quality Engineering capabilities. Every prompt is a
> versioned, reviewable markdown template the Java AI engine (`ai/src/`) loads,
> fills with variables, and dispatches to a configured provider. AI assists the
> engineer and never replaces judgement: every prompt's output is a reviewable
> draft.

## Scope

- The catalogue of prompt templates and what each does.
- The naming, versioning, and variable conventions every prompt follows.
- How the Java prompt engine consumes these templates.
- The shared guardrails every template inherits.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| AI QA Engineer | Author and version prompts; keep this index in sync |
| Human reviewer | Review prompt changes; own the output a prompt produces |
| Java engine (`ai/src/`) | Load, render, and dispatch prompts; record provenance |

---

## 1. Prompt Catalogue

| # | Prompt ID | File | Capability | Output |
|---|-----------|------|------------|--------|
| 1 | `test-case-generation` | [test-case-generation.md](test-case-generation.md) | Test generation | Structured test cases + traceability |
| 2 | `bdd-scenario-generation` | [bdd-scenario-generation.md](bdd-scenario-generation.md) | BDD/Cucumber | Gherkin `.feature` draft |
| 3 | `api-test-generation` | [api-test-generation.md](api-test-generation.md) | API tests | RestAssured cases + skeleton |
| 4 | `bug-report-drafting` | [bug-report-drafting.md](bug-report-drafting.md) | Bug-report assistant | Structured bug report |
| 5 | `root-cause-analysis` | [root-cause-analysis.md](root-cause-analysis.md) | Failure analysis | Ranked RCA + fix + confidence |
| 6 | `failure-analysis` | [failure-analysis.md](failure-analysis.md) | Failure triage | Probable cause from artifacts |
| 7 | `sql-generation` | [sql-generation.md](sql-generation.md) | DB test data/verify | Safe-by-default SQL |
| 8 | `fhir-payload-generation` | [fhir-payload-generation.md](fhir-payload-generation.md) | FHIR/HL7 test data | Synthetic FHIR R4 payloads |
| 9 | `requirement-analysis` | [requirement-analysis.md](requirement-analysis.md) | Requirement analysis | AC, edge/negative/boundary, risks, traceability |
| 10 | `coverage-gap-analysis` | [coverage-gap-analysis.md](coverage-gap-analysis.md) | Coverage analysis | Coverage matrix + prioritized gaps |
| 11 | `regression-impact-analysis` | [regression-impact-analysis.md](regression-impact-analysis.md) | Regression analysis | Impact map + regression selection |
| 12 | `risk-analysis` | [risk-analysis.md](risk-analysis.md) | Risk analysis | Risk register + mitigations |
| 13 | `code-review-checklist` | [code-review-checklist.md](code-review-checklist.md) | Code review | Adversarial findings table |
| 14 | `documentation-assistant` | [documentation-assistant.md](documentation-assistant.md) | Documentation | House-style draft doc |

---

## 2. Naming & Versioning Convention

- **File name** = kebab-case `<capability>.md` matching the **Prompt ID**.
- **Prompt ID** is stable; never reuse a retired ID for a different prompt.
- **Versioning** is per-prompt SemVer-lite recorded in the prompt header and
  Version History table:
  - **MAJOR** (`2.0`) — output format/contract changes; callers must adapt.
  - **MINOR** (`1.1`) — added inputs/instructions, backward compatible.
  - **PATCH** (`1.0.1`) — wording/clarity fixes, no behavioral contract change.
- The engine pins a prompt version per capability via configuration
  (`omii.ai.prompt.<capability>.version`); see `ai/documentation/AI_CONFIGURATION.md`.
- Every prompt change is reviewed and re-evaluated against golden examples
  (`ai/evaluation/PROMPT_EVALUATION.md`) before the pinned version advances.

---

## 3. Anatomy of a Prompt Template

Every template contains, in order:

1. **Header table** — Prompt ID, version, capability, default model, human-review
   requirement, PHI policy.
2. **PURPOSE** — what it does, when to use, when not to.
3. **INPUTS** — a table of `{{double_brace}}` variables (required/optional + meaning).
4. **PROMPT** — the body the engine renders and sends, with embedded RULES.
5. **OUTPUT FORMAT** — the exact structure the model must return (so the engine
   can parse and the human can review).
6. **EXAMPLE** (where useful) and a **Version History** table.

---

## 4. Variable Conventions

- Variables use `{{snake_case}}` double-braces.
- The engine substitutes values; an unset optional variable renders as an empty
  string and the prompt's RULES tell the model to flag the missing input rather
  than invent it.
- Reserved/common variables across prompts:

| Variable | Meaning |
|----------|---------|
| `{{business_rules}}` | Relevant `BR-*` rule IDs + text from `docs/BUSINESS_RULES.md` |
| `{{roles_in_scope}}` / `{{actors}}` | RBAC roles from `docs/PROJECT_METADATA.md` |
| `{{environment}}` | Target env / build / data set |
| `{{evidence}}` / `{{failure_artifacts}}` | Logs/traces/responses (PHI-redacted) |
| `{{existing_tests}}` | Inventory used to avoid duplication |

- **Inputs MUST be PHI- and secret-redacted before substitution** — redaction is
  enforced by the engine, not the prompt (see `AI_SECURITY_GUARDRAILS.md`).

---

## 5. How the Java Engine Uses These Prompts

The AI module under `ai/src/` (provider abstraction + prompt engine) consumes
these templates as follows:

1. **Load** the template for the requested capability and pinned version.
2. **Validate** that required variables are supplied; redact PHI/secrets from
   every input value.
3. **Render** `{{variables}}` into the PROMPT body.
4. **Dispatch** to the configured provider/model (`omii.ai.provider`,
   `omii.ai.model`) via the provider abstraction (Claude/OpenAI/local), with
   caching where enabled.
5. **Parse** the response against the OUTPUT FORMAT contract.
6. **Record provenance** — prompt ID + version, provider/model, input hash
   (not raw PHI), timestamp, and reviewer — to the audit trail.
7. **Return a draft** to the human, who reviews, edits, and owns the result.

The engine treats every template as data: changing QA behavior means editing a
reviewed markdown prompt, not recompiling code.

---

## 6. Shared Guardrails (inherited by every prompt)

- **Reviewable:** output is a draft; a human approves before it lands.
- **Transparent:** prompt ID/version and provider/model are recorded.
- **Synthetic-only:** no real PHI, MRNs, credentials, or secrets in inputs or outputs.
- **No fabricated compliance:** HIPAA-like practice only; never assert certification.
- **Explainable:** prompts require evidence, confidence, and flagged assumptions.

See `ai/documentation/AI_SECURITY_GUARDRAILS.md` for the full guardrail set.

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
