# omiiCARE_QA AI Prompt Library (v1.0.0)

> Curated, **provider-abstracted**, schema-strict prompt templates packaged for
> the v1.0.0 release. Each template is a versioned, reviewable Markdown contract
> the Java AI engine (`ai/src/`) loads, renders with `{{variables}}`, and
> dispatches to a configured provider (**Claude / OpenAI / local Ollama**) via the
> provider abstraction in `ai/providers/`.

This `library/` set is **additive** — it does not replace the templates directly
under `ai/prompts/`. It is the release-curated edition with strict YAML output
schemas, explicit guardrails, and worked examples for each capability. See the
parent index at `ai/prompts/README.md` for the full catalogue.

## Catalogue

| # | Prompt ID | File | Capability | Output |
|---|-----------|------|------------|--------|
| 1 | `test-generation` | [test-generation.md](test-generation.md) | Test generation | Traceable test cases (YAML) |
| 2 | `failure-analysis` | [failure-analysis.md](failure-analysis.md) | Failure triage | Probable cause + next steps |
| 3 | `root-cause-analysis` | [root-cause-analysis.md](root-cause-analysis.md) | RCA | Ranked cause + fix + verification |
| 4 | `bug-report-drafting` | [bug-report-drafting.md](bug-report-drafting.md) | Bug-report assistant | Severity-rated bug report |
| 5 | `regression-impact-analysis` | [regression-impact-analysis.md](regression-impact-analysis.md) | Regression analysis | Impact map + selected set |
| 6 | `coverage-gap-analysis` | [coverage-gap-analysis.md](coverage-gap-analysis.md) | Coverage analysis | Coverage matrix + ranked gaps |

## Pipeline

```
failure-analysis ──▶ root-cause-analysis ──▶ bug-report-drafting
coverage-gap-analysis ──▶ test-generation ──▶ (RTM update)
regression-impact-analysis ──▶ (test-lead selection)
```

## Template anatomy (every file)

1. Header table — Prompt ID, version, providers, determinism, human-review, PHI policy.
2. **PURPOSE** — what it does, when to use / not use.
3. **INPUTS** — `{{snake_case}}` variable table (required/optional).
4. **PROMPT TEMPLATE** — the rendered body with embedded RULES/METHOD.
5. **OUTPUT SCHEMA** — strict YAML the engine parses and the human reviews.
6. **GUARDRAILS** and a worked **EXAMPLE**, plus **Version History**.

## Shared guardrails (inherited)

- **Provider-abstracted:** no provider-specific syntax; runs on Claude, OpenAI, or local.
- **Deterministic-oriented:** `temperature=0`, fixed seed where supported, for stable parse.
- **PHI-safe / synthetic-only:** inputs/outputs redacted; data from `core.generators` (Datafaker).
- **Reviewable draft:** a human owns every output; nothing auto-merges or auto-files.
- **No fabricated compliance:** HIPAA-*like* practice only; never assert certification.
- **Traceable:** outputs reference `REQ-*` / `BR-*` / RTM where applicable.

## How the engine consumes these (planned integration)

The provider abstraction and prompt engine under `ai/src/` (config keys such as
`omii.ai.provider`, `omii.ai.model`, `omii.ai.prompt.<capability>.version`) load
the pinned template, validate + redact inputs, render variables, dispatch to the
provider, parse against the OUTPUT SCHEMA, and record provenance (prompt ID +
version, provider/model, input hash, timestamp, reviewer). See
`ai/documentation/` and `ai/evaluation/` for configuration and golden-example
evaluation. Wiring of this `library/` set into the pinned capability map is
**(planned)** for the v1.0.0 release.

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-07-01 | AI QA Engineer | Initial release-curated prompt library (6 templates) |
