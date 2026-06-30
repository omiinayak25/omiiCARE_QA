# AI-Native Quality Engineering (omiiCARE_QA)

> **Purpose.** The home of omiiCARE_QA's AI-Native QE capabilities — a
> provider-abstracted, prompt-driven assistant layer that helps engineers analyze
> requirements, generate tests, triage failures, draft bug reports, and assess
> coverage/regression/risk. **AI assists, humans decide.** Every capability is
> optional, transparent, explainable, and reviewable; AI never stores secrets,
> exposes credentials, or fabricates compliance claims.

> **Status:** Delivered in **Milestone 9**. Milestones 1–8 (foundation through
> CI/CD) are complete; this module adds the AI layer on top of the existing
> platform without changing its source of truth: documentation leads,
> implementation follows.

## Scope

- The AI module's intent, boundaries, and contents.
- The provider abstraction and how providers/models are selected by configuration.
- The prompt library and the AI capabilities it powers.
- The knowledge base, configuration, and security guardrails.
- The Java implementation under `ai/src/`.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| AI QA Engineer | Author prompts, knowledge, and capability docs |
| SDET / QA Engineer | Use AI as an assistant; review and own every AI output |
| Human reviewer | Approve AI-assisted artifacts before they land |
| Maintainer | Govern provider config, cost, and guardrail compliance |

---

## 1. Principles

AI in omiiCARE_QA operationalizes Project Principle #10 — *"AI assists, humans
decide"*:

- **Optional** — every capability can be disabled (`omii.ai.enabled=false`); the
  platform is fully functional without AI.
- **Transparent** — each invocation records its prompt ID/version, provider, and
  model to an audit trail.
- **Explainable** — outputs carry evidence, confidence, and flagged assumptions.
- **Reviewable** — outputs are drafts; a human reviews, edits, and owns them.
- **Safe** — no real PHI, no stored secrets, no exposed credentials, no
  fabricated compliance.

---

## 2. Provider Abstraction (config-driven)

The AI module is **provider-agnostic**. A single abstraction sits in front of
multiple backends, chosen entirely by configuration:

| Provider | Use | Notes |
|----------|-----|-------|
| **Claude** (Anthropic) | Default; latest Claude (e.g. Claude Opus) | Strong reasoning for analysis/RCA/review |
| **OpenAI** | Alternative hosted provider | Same prompt contract |
| **Local** | Self-hosted/offline model | For air-gapped or cost-sensitive runs; strongest PHI posture |

- Switching provider or model is a **config change only** — no code edits, no
  prompt rewrites (prompts are provider-neutral markdown).
- Keys are read from environment/secret store, never committed, never logged.
- See `ai/documentation/AI_CONFIGURATION.md` for keys (`omii.ai.provider`,
  `omii.ai.model`, …).

---

## 3. Prompt Library

Reusable, versioned markdown templates live in [`ai/prompts/`](prompts/README.md).
Each has a clear PURPOSE, `{{variable}}` INPUTS, a PROMPT body with embedded
rules, and a strict OUTPUT format. The Java engine loads, renders, dispatches,
parses, and records each prompt. See the [Prompt Library index](prompts/README.md)
for the full catalogue and conventions.

---

## 4. AI Capabilities

| Capability | What it assists with | Prompt(s) |
|------------|----------------------|-----------|
| Requirement analysis | AC, edge/negative/boundary, risks, traceability | `requirement-analysis` |
| Test generation | Test cases, BDD scenarios, API tests | `test-case-generation`, `bdd-scenario-generation`, `api-test-generation` |
| Failure analysis | Triage artifacts, root-cause reasoning | `failure-analysis`, `root-cause-analysis` |
| Bug-report assistant | Structured, traceable bug reports | `bug-report-drafting` |
| Coverage analysis | Gaps vs rules/RTM, prioritized | `coverage-gap-analysis` |
| Regression analysis | Impact radius + regression selection | `regression-impact-analysis` |
| Risk analysis | Risk register + mitigating tests | `risk-analysis` |
| Documentation & code review | House-style docs; adversarial review | `documentation-assistant`, `code-review-checklist` |
| Test data | SQL fixtures/verification; FHIR R4 payloads | `sql-generation`, `fhir-payload-generation` |
| Healthcare awareness | FHIR code systems, audit/consent/tenancy, BR-* | cross-cutting in all prompts |
| Execution reports | Summaries of runs/triage for stakeholders | `documentation-assistant` + `failure-analysis` |

Full detail — inputs, outputs, review path, confidence/limitations — is in
`ai/documentation/AI_CAPABILITIES.md`.

---

## 5. Knowledge Base

`ai/knowledge/KNOWLEDGE_BASE.md` defines a searchable QA knowledge base: best
practices, common failures, lessons learned, patterns, troubleshooting, ADRs, and
the prompt-library index. The KB grounds AI outputs in project-specific context
and gives reviewers a reference.

---

## 6. Configuration

AI is governed by `omii.ai.*` configuration: enable/disable, provider/model
selection, prompt-version pinning, caching, logging, and audit. See
`ai/documentation/AI_CONFIGURATION.md`.

---

## 7. AI Security Guardrails

Non-negotiable rules: AI must never store secrets, expose or transmit
credentials/production secrets, leak sensitive info, or generate unsafe code
without a warning. PHI-safe prompting, redaction, and human-in-the-loop review
are mandatory. See `ai/documentation/AI_SECURITY_GUARDRAILS.md`.

---

## 8. The Java Module (`ai/src/`)

The provider abstraction and prompt engine are implemented as a Java module under
`ai/src/`, built separately from these markdown assets:

- **Provider abstraction** — uniform interface over Claude/OpenAI/local.
- **Prompt engine** — loads `ai/prompts/*.md`, validates inputs, redacts PHI/secrets,
  renders variables, dispatches, parses the OUTPUT contract, and records provenance.
- This module depends on the markdown prompts and docs in this directory; the
  markdown is the behavioral source of truth, the Java is the runtime.

---

## 9. Directory Map

| Path | Contents |
|------|----------|
| `ai/prompts/` | Prompt library (templates + index) |
| `ai/documentation/` | Capabilities, configuration, security guardrails |
| `ai/knowledge/` | Searchable knowledge base |
| `ai/evaluation/` | Prompt evaluation rubric and golden examples |
| `ai/providers/` | Provider-abstraction assets |
| `ai/templates/`, `ai/agents/`, `ai/analysis/`, `ai/reporting/`, `ai/quality/` | Supporting assets |
| `ai/src/` | Java AI module (provider abstraction + prompt engine) |

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
