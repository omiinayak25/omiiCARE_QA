# AI Configuration

> **Purpose.** Define how omiiCARE_QA's AI layer is configured: how to enable or
> disable it, select a provider and model, pin prompt versions, control caching
> and logging, and record the audit trail. Configuration is the only lever needed
> to change AI behavior — no code edits. AI is optional and off-by-default-safe:
> the platform runs fully without it.

## Scope

- The `omii.ai.*` configuration keys and their meaning.
- Provider/model selection and prompt-version pinning.
- Caching, logging, and audit-trail configuration.
- Precedence, secrets handling, and validation.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Maintainer | Own provider config, cost ceilings, and guardrail compliance |
| SDET / QA Engineer | Set capability/model per task within policy |
| Security reviewer | Verify secrets are never committed or logged |

---

## 1. Master Switch

| Key | Type | Default | Meaning |
|-----|------|---------|---------|
| `omii.ai.enabled` | boolean | `false` | Master on/off. When `false`, no AI calls are made and the platform is fully functional. |

AI is **opt-in**. With `omii.ai.enabled=false`, every capability is inert and no
provider call, cache write, or audit AI-event occurs.

---

## 2. Provider & Model Selection

| Key | Type | Example | Meaning |
|-----|------|---------|---------|
| `omii.ai.provider` | enum | `claude` \| `openai` \| `local` | Which backend the abstraction dispatches to. |
| `omii.ai.model` | string | `claude-opus-*` (latest Claude) | Model id for the chosen provider. Default to the latest Claude (e.g. Claude Opus). |
| `omii.ai.endpoint` | url | provider base URL | Overridable for `local`/self-hosted. |
| `omii.ai.timeout.ms` | int | `60000` | Per-call timeout. |
| `omii.ai.max.tokens` | int | `4096` | Output cap. |
| `omii.ai.temperature` | float | `0.2` | Low by default for deterministic, reviewable output. |

- **Provider/model are config-only** — switching Claude ↔ OpenAI ↔ local needs no
  code or prompt change; prompts are provider-neutral markdown.
- **Per-capability override** (optional): `omii.ai.capability.<id>.model` lets a
  specific capability (e.g. `root-cause-analysis`) pin a stronger model.

### Secrets

| Key | Source | Rule |
|-----|--------|------|
| `omii.ai.apiKey` | env / secret store (e.g. `OMII_AI_API_KEY`) | **Never** committed, never logged, never echoed in output. |

API keys are resolved from the environment or a secret store at runtime only. See
`AI_SECURITY_GUARDRAILS.md`.

---

## 3. Prompt Versioning

| Key | Type | Example | Meaning |
|-----|------|---------|---------|
| `omii.ai.prompt.dir` | path | `ai/prompts` | Location of the prompt library. |
| `omii.ai.prompt.<capability>.version` | string | `1.0` | Pinned prompt version per capability. |
| `omii.ai.prompt.strictParse` | boolean | `true` | Reject responses that violate the OUTPUT contract. |

- Each capability runs a **pinned, reviewed** prompt version; advancing a version
  requires re-evaluation against golden examples (`ai/evaluation/PROMPT_EVALUATION.md`).
- The rendered prompt's ID + version is recorded with every invocation.

---

## 4. Caching

| Key | Type | Default | Meaning |
|-----|------|---------|---------|
| `omii.ai.cache.enabled` | boolean | `true` | Cache responses to cut cost and latency. |
| `omii.ai.cache.ttl.seconds` | int | `86400` | Cache lifetime. |
| `omii.ai.cache.key` | enum | `hash(prompt+inputs)` | Keyed on prompt ID/version + redacted input hash — **never raw PHI**. |
| `omii.ai.cache.store` | enum | `memory` \| `disk` | Where cache lives; disk cache must exclude PHI by construction. |

Cache keys hash **redacted** inputs only; PHI/secrets never enter a cache key or value.

---

## 5. Logging

| Key | Type | Default | Meaning |
|-----|------|---------|---------|
| `omii.ai.log.enabled` | boolean | `true` | Operational logging of AI calls. |
| `omii.ai.log.level` | enum | `INFO` | Verbosity. |
| `omii.ai.log.redact` | boolean | `true` (locked on) | Redact PHI/secrets in all log lines. Cannot be disabled. |
| `omii.ai.log.includePrompt` | boolean | `false` | If on, logs the rendered prompt with PHI/secrets redacted. |

Logs record metadata (capability, prompt version, provider, model, latency,
token usage, confidence) — never raw PHI, never secrets.

---

## 6. Audit Trail

| Key | Type | Default | Meaning |
|-----|------|---------|---------|
| `omii.ai.audit.enabled` | boolean | `true` | Record every AI invocation for transparency. |
| `omii.ai.audit.sink` | enum | `audit_log` | Where audit events are written (aligns with `BR-AUDIT-*`). |
| `omii.ai.audit.fields` | list | see below | What each audit event captures. |

Each AI audit event captures: timestamp, actor/role, capability, prompt ID +
version, provider, model, redacted input hash, output reference, confidence, and
the reviewing human (once review occurs). This makes every AI-assisted artifact
**traceable and reviewable** after the fact.

---

## 7. Configuration Precedence

1. Per-capability override (`omii.ai.capability.<id>.*`)
2. Environment variables / secret store (highest for secrets)
3. Environment-specific config (`config/<env>/...`)
4. Repository defaults

The effective configuration is logged (redacted) at startup when AI is enabled.

---

## 8. Validation & Safe Defaults

- If `omii.ai.enabled=true` but no provider/key resolves, the module **fails
  closed** (AI disabled, error surfaced) rather than calling an unconfigured backend.
- `temperature` defaults low and `strictParse` defaults on to keep output
  deterministic and reviewable.
- Disabling redaction (`omii.ai.log.redact=false`) is **not permitted** and is
  ignored by the engine.

---

## 9. Example (illustrative)

```yaml
omii:
  ai:
    enabled: true
    provider: claude          # claude | openai | local
    model: claude-opus        # latest Claude by default
    temperature: 0.2
    prompt:
      dir: ai/prompts
      strictParse: true
      requirement-analysis:
        version: "1.0"
    cache:
      enabled: true
      ttl:
        seconds: 86400
    log:
      enabled: true
      redact: true            # locked on
    audit:
      enabled: true
      sink: audit_log
```

Secrets (e.g. `OMII_AI_API_KEY`) are supplied via environment/secret store, not
this file.

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
