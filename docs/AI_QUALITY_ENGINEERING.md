# AI Quality Engineering

> **Purpose.** The overview of omiiCARE_QA's AI-native Quality Engineering (QE)
> strategy: a provider-abstracted, prompt-driven assistant layer that helps
> engineers analyze requirements, generate tests, triage failures, perform RCA,
> draft bug reports, assess regression impact, and close coverage gaps — under
> hard PHI-safety and human-in-the-loop guardrails, wired into CI.
>
> **Operating principle.** *AI assists, humans decide.* Every AI output is a
> reviewable **draft**; a human reviews, edits, and owns it. AI is optional,
> off-by-default-safe, transparent, explainable, and PHI-safe. The platform is
> fully functional with `omii.ai.enabled=false`.

This document is the strategy and map. Implementation lives in the `ai` module:

- **Prompt library** — [`ai/prompts/`](../ai/prompts/README.md) (14 reusable,
  versioned markdown templates + `ai/prompts/library/`).
- **AI engine, providers, evaluation, knowledge** — [`ai/`](../ai/README.md),
  `ai/documentation/`, `ai/evaluation/`, `ai/knowledge/`.
- **QE workspace** — `ai/qe/` (reserved working area for QE artifacts; scaffolded, _(planned)_ content).

---

## 1. Why AI-Native QE

| Driver | AI assist | Human ownership |
|--------|-----------|-----------------|
| 1,795 requirements, 66 modules, 4,187 manual cases | Accelerate requirement→test drafting and coverage analysis | Engineer approves every test, AC, and trace |
| Cross-SUT portability (OpenMRS / OpenEMR / HAPI FHIR / SMART / omiiCARE) | Generate SUT-neutral assertions and FHIR R4 payloads | Engineer maps via the Resource Adapter Layer |
| Healthcare risk (PHI, HIPAA-like practice) | Flag PHI, audit, consent, tenancy concerns in drafts | Engineer/security reviewer verify; no certification claims |
| Flaky/failing CI runs | Triage artifacts → ranked RCA with confidence | Engineer confirms cause and fix |

AI is a **force multiplier on top of** the existing platform (manual cases, RTM,
automation, k6/ZAP, docs), never a replacement for it. It operationalizes Project
Principle #10 — *"AI assists, humans decide."*

---

## 2. Provider Abstraction

The `ai` module is **provider-agnostic**: a single abstraction sits in front of
multiple backends, selected entirely by configuration. Prompts are provider-neutral
markdown — switching provider/model is a **config change only**, no code or prompt
rewrites.

| Provider | Use | Notes |
|----------|-----|-------|
| **Claude** (Anthropic) | Default; latest Claude (e.g. Claude Opus) | Strong reasoning for analysis / RCA / review |
| **OpenAI** | Alternative hosted provider | Same prompt contract |
| **Local** | Self-hosted / offline model | Air-gapped or cost-sensitive runs; strongest PHI posture |

- Keys are read from environment / secret store — **never committed, never logged**.
- Config keys (`omii.ai.enabled`, `omii.ai.provider`, `omii.ai.model`,
  `omii.ai.prompt.<capability>.version`, caching, logging, audit) are defined in
  [`ai/documentation/AI_CONFIGURATION.md`](../ai/documentation/AI_CONFIGURATION.md).

---

## 3. The Prompt Library

Reusable, versioned markdown templates in [`ai/prompts/`](../ai/prompts/README.md).
Each is data the Java engine loads, renders, dispatches, parses, and records — so
changing QA behavior means editing a **reviewed prompt**, not recompiling code.

**Anatomy** (every template): Header (Prompt ID, version, capability, default
model, human-review requirement, PHI policy) → PURPOSE → INPUTS (`{{snake_case}}`
variables) → PROMPT body with embedded RULES → strict OUTPUT FORMAT → EXAMPLE +
Version History.

**Catalogue** (14 prompts; see the [index](../ai/prompts/README.md) for the full table):

| Prompt ID | Capability | Output |
|-----------|------------|--------|
| `requirement-analysis` | Requirement analysis | AC, edge/negative/boundary, risks, traceability |
| `test-case-generation` | Test generation | Structured test cases + traceability |
| `bdd-scenario-generation` | BDD / Cucumber | Gherkin `.feature` draft |
| `api-test-generation` | API tests | RestAssured cases + skeleton |
| `bug-report-drafting` | Bug-report assistant | Structured bug report |
| `root-cause-analysis` | Failure analysis | Ranked RCA + fix + confidence |
| `failure-analysis` | Failure triage | Probable cause from artifacts |
| `regression-impact-analysis` | Regression analysis | Impact map + regression selection |
| `coverage-gap-analysis` | Coverage analysis | Coverage matrix + prioritized gaps |
| `risk-analysis` | Risk analysis | Risk register + mitigating tests |
| `sql-generation` | DB test data / verify | Safe-by-default SQL |
| `fhir-payload-generation` | FHIR / HL7 test data | Synthetic FHIR R4 payloads |
| `code-review-checklist` | Code review | Adversarial findings table |
| `documentation-assistant` | Documentation | House-style draft doc |

**Conventions.** File name = kebab-case `<capability>.md` = stable Prompt ID;
`{{snake_case}}` variables; per-prompt SemVer-lite (MAJOR = output-contract change,
MINOR = added inputs, PATCH = wording). The engine **pins** a version per capability
via `omii.ai.prompt.<capability>.version`; advancing a pin requires re-evaluation
against golden examples in
[`ai/evaluation/PROMPT_EVALUATION.md`](../ai/evaluation/PROMPT_EVALUATION.md).

Reserved shared variables include `{{business_rules}}` (BR-* from
`docs/BUSINESS_RULES.md`), `{{roles_in_scope}}`/`{{actors}}` (RBAC from
`docs/PROJECT_METADATA.md`), `{{environment}}`, `{{evidence}}`/`{{failure_artifacts}}`
(PHI-redacted), and `{{existing_tests}}` (to avoid duplication).

---

## 4. Reference Tools & Knowledge

| Asset | Path | Role |
|-------|------|------|
| Prompt library index | [`ai/prompts/README.md`](../ai/prompts/README.md) | Catalogue, conventions, engine contract |
| Capabilities reference | `ai/documentation/AI_CAPABILITIES.md` | Per-capability inputs/outputs, review path, limits |
| Configuration | `ai/documentation/AI_CONFIGURATION.md` | `omii.ai.*` keys, provider/model, audit |
| Security guardrails | `ai/documentation/AI_SECURITY_GUARDRAILS.md` | Hard prohibitions G1–G7, PHI redaction |
| Prompt evaluation | `ai/evaluation/PROMPT_EVALUATION.md` | Golden examples; gate before pin advances |
| Knowledge base | `ai/knowledge/KNOWLEDGE_BASE.md` | Best practices, common failures, ADRs, patterns |
| QE workspace | `ai/qe/` | Reserved QE working area _(planned content)_ |

The KB **grounds** AI outputs in project-specific context (business rules, RBAC,
FHIR code systems, known failures) and gives reviewers a reference, reducing
hallucination and drift.

---

## 5. Where AI Assists

Each capability maps to one or more prompts and always returns a **draft** for review:

| Capability | What AI assists with | Prompt(s) | Human reviews |
|------------|----------------------|-----------|---------------|
| **Test generation** | Test cases, BDD scenarios, API cases from requirements | `test-case-generation`, `bdd-scenario-generation`, `api-test-generation` | Correctness, dedup vs `manual-testing/`, RTM trace |
| **Failure analysis** | Triage logs/traces/screenshots from a failed run | `failure-analysis` | Confirm probable cause vs evidence |
| **Root-cause analysis (RCA)** | Ranked causes + fix + confidence | `root-cause-analysis` | Validate cause, own the fix |
| **Bug drafting** | Structured, traceable bug report | `bug-report-drafting` | Severity, repro, files before filing |
| **Regression impact** | Impact radius + which suites to re-run | `regression-impact-analysis` | Approve regression selection |
| **Coverage analysis** | Gaps vs business rules / RTM, prioritized | `coverage-gap-analysis` | Decide which gaps to close |
| **Requirement analysis** | AC, edge/negative/boundary, risks, traceability | `requirement-analysis` | AC sign-off |
| **Risk analysis** | Risk register + mitigating tests | `risk-analysis` | Risk acceptance |
| **Test data** | Synthetic SQL fixtures; FHIR R4 payloads | `sql-generation`, `fhir-payload-generation` | Verify safe-by-default + PHI-safe |
| **Docs & code review** | House-style docs; adversarial findings | `documentation-assistant`, `code-review-checklist` | Edit and approve |

AI outputs feed the existing pipeline: drafted cases land alongside
`manual-testing/test-cases/`, traces reconcile against `manual-testing/rtm/RTM.csv`,
generated automation skeletons target the `automation` framework (RestAssured /
Cucumber / Playwright) — but **a human authors the committed Java**, never the AI
engine writing it unattended.

---

## 6. Human-in-the-Loop Guardrails

Enforced by the engine **and** by mandatory review — not by trust. See
`ai/documentation/AI_SECURITY_GUARDRAILS.md` for full detail.

| # | The AI MUST NEVER… |
|---|--------------------|
| G1 | Store secrets (keys/tokens/passwords) in prompts, outputs, caches, logs, or audit |
| G2 | Expose / echo / reconstruct a credential it was given or inferred |
| G3 | Transmit production secrets or connection strings to any provider |
| G4 | Leak PHI — real patient data, MRNs, identifiable health info |
| G5 | Generate unsafe code (destructive SQL, auth bypass, injection) without an inline WARNING |
| G6 | Fabricate compliance — assert HIPAA / medical-device certification |
| G7 | Act autonomously on safety-critical artifacts (merge, file, gate) without human review |

A G1–G7 violation is a **stop-and-escalate** event: the engine refuses the output
and surfaces the reason.

**Shared inherited guardrails:** Reviewable (draft → human owns), Transparent
(prompt ID/version + provider/model recorded), Synthetic-only, No fabricated
compliance, Explainable (evidence + confidence + flagged assumptions).

---

## 7. PHI Safety

The platform uses **synthetic / PHI-safe data only** (Datafaker generators in
`automation` core.generators). The AI layer adds defense-in-depth:

- **Redact before dispatch** — PHI and secrets are stripped from every input value
  by the engine (not the prompt) before it reaches any provider.
- **Synthetic-only inputs/outputs** — no real MRNs, patient data, credentials, or
  connection strings enter prompts; FHIR/SQL generators emit synthetic data.
- **Hash, don't store** — caches and audit records key on **redacted hashes**, never
  raw PHI or secrets.
- **Local provider option** — the `Local` self-hosted provider gives the strongest
  PHI posture for air-gapped runs (no data leaves the environment).
- **Provenance** — prompt ID/version, provider/model, input **hash**, timestamp, and
  reviewer are recorded to the audit trail for every invocation.

This complements the platform rule that performance and security tests run **only on
owned/local environments** — AI never sends environment secrets or real data outward.

---

## 8. CI Integration

The AI layer is **opt-in** and does not block the green build. Default
`omii.ai.enabled=false` means CI runs are deterministic and provider-free; AI assists
are invoked deliberately by engineers or in dedicated, gated jobs.

| Stage | AI assist (when enabled) | Gating |
|-------|--------------------------|--------|
| PR / pre-merge | `code-review-checklist`, `coverage-gap-analysis`, `regression-impact-analysis` to advise reviewers and scope regression | **Advisory** — human review gates merge (G7) |
| Nightly E2E (`qa-nightly-e2e.yml`) | `failure-analysis` / `root-cause-analysis` over `automation` + Playwright artifacts (trace.zip, screenshots, Allure/Extent) | Advisory triage; engineer confirms |
| Quality gate (`qa-quality-gate.yml`) | Summaries via `documentation-assistant` + `failure-analysis` | Existing gates unchanged |
| Release (`qa-release.yml`) | Draft release/test-summary notes | Human approves |

Notes:
- AI jobs are **separate, optional** workflow steps that never gate on a non-deterministic
  model response; the existing `.github/workflows/` pipelines remain the source of truth.
- Provider keys are supplied via CI **secrets**, never committed; with AI disabled no
  secret or provider call is required _(dedicated AI CI jobs are **planned**; the engine,
  prompts, config, and guardrails are in place today)_.

---

## 9. Maturity & Roadmap

| Item | Status |
|------|--------|
| Provider abstraction (Claude / OpenAI / Local), config-driven | Delivered (Milestone 9) |
| 14-prompt versioned library + evaluation harness | Delivered |
| Guardrails G1–G7, PHI redaction, provenance/audit | Delivered |
| Knowledge base grounding | Delivered |
| `ai/qe/` workspace contents | _(planned)_ |
| Dedicated AI CI jobs (advisory PR / nightly triage) | _(planned)_ |
| Auto-filed bug drafts to Jira via human-approved gate | _(planned)_ |

---

## See Also

- [`ai/README.md`](../ai/README.md) — AI module overview
- [`ai/prompts/README.md`](../ai/prompts/README.md) — Prompt library index & conventions
- `ai/documentation/AI_CAPABILITIES.md` · `AI_CONFIGURATION.md` · `AI_SECURITY_GUARDRAILS.md`
- [`docs/TEST_STRATEGY.md`](TEST_STRATEGY.md) · [`docs/QUALITY_GATES.md`](QUALITY_GATES.md) · [`docs/RTM.md`](RTM.md)
- [`docs/AI_DEVELOPMENT_RULES.md`](AI_DEVELOPMENT_RULES.md) · [`docs/AI_DEVELOPMENT_WORKFLOW.md`](AI_DEVELOPMENT_WORKFLOW.md)

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-07-01 | AI Quality Engineer | Initial AI-native QE strategy overview |
