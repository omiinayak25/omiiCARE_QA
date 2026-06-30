# AI Security Guardrails

> **Purpose.** The non-negotiable security and safety rules that bound every AI
> capability in omiiCARE_QA. AI must **never** store secrets, expose or transmit
> credentials, leak sensitive information, or generate unsafe code without a
> warning — and it must never fabricate compliance claims. These guardrails are
> enforced by the engine and by mandatory human-in-the-loop review, not by trust.

## Scope

- The hard prohibitions every AI invocation obeys.
- PHI-safe prompting and redaction.
- Human-in-the-loop review obligations.
- Unsafe-code handling and compliance honesty.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| AI engine (`ai/src/`) | Enforce redaction, secret-blocking, and provenance |
| SDET / QA Engineer | Never feed real PHI/secrets; review every output |
| Security reviewer | Audit guardrail compliance; own incident response |

---

## 1. Hard Prohibitions (never, under any configuration)

| # | The AI MUST NEVER… | Enforcement |
|---|--------------------|-------------|
| G1 | **Store secrets** — persist API keys, tokens, passwords, or private keys in prompts, outputs, caches, logs, or audit records | Redaction + cache/log key on redacted hashes only |
| G2 | **Expose credentials** — emit, echo, or reconstruct a secret it was given or inferred | Output scanning; secret patterns blocked |
| G3 | **Transmit production secrets** — send prod credentials/connection strings to any provider | Inputs are synthetic/redacted; prod secrets never enter inputs |
| G4 | **Leak sensitive info / PHI** — output real patient data, MRNs, or identifiable health info | PHI redaction before dispatch; synthetic-only policy |
| G5 | **Generate unsafe code without a warning** — produce destructive SQL, auth bypasses, injection-prone code, or PHI-exposing queries silently | Mandatory inline WARNING + safe-by-default templates |
| G6 | **Fabricate compliance** — assert HIPAA/medical-device certification or conformance the platform does not hold | Prompts mandate "HIPAA-like practice, no certification" |
| G7 | **Act autonomously on safety-critical artifacts** — merge, file, or gate without human review | Output is always a draft; provenance recorded |

A violation of G1–G7 is a stop-and-escalate event: the engine refuses the output
and surfaces the reason.

---

## 2. PHI-Safe Prompting

- **Synthetic-only inputs.** Only synthetic, clearly-fake data may reach a prompt:
  fake names, `MRN-SYN-*` identifiers, fabricated dates. Real patient data is never
  an input.
- **Redaction before dispatch.** The engine redacts PHI and secrets from every
  input value *before* rendering the prompt — replacing them with
  `[REDACTED-PHI]` / `[REDACTED-SECRET]`. Redaction is locked on
  (`omii.ai.log.redact` cannot be disabled).
- **PHI categories redacted:** names, MRNs, SSNs/national ids, addresses, phone/
  email, dates of birth, full-face images, and any free-text that may contain them.
- **Minimum necessary.** Prompts receive only the fields a capability needs — not
  whole records.
- **Local provider for sensitive contexts.** For the strongest posture, route
  sensitive analysis to `omii.ai.provider=local` so no data leaves the boundary.

---

## 3. Secrets Handling

- API keys are read from environment/secret store at runtime; **never** committed,
  hardcoded, or written to prompts, outputs, caches, logs, or audit records.
- Generated test code references credentials only through helpers
  (e.g. `authAs("RECEPTIONIST")`), never literal tokens.
- Cache and log keys are hashes of **redacted** inputs; raw PHI/secrets never form
  a key or value.
- If a secret is detected in an input, the engine strips it and records that a
  redaction occurred (not the secret).

---

## 4. Human-in-the-Loop Review (mandatory)

- Every AI output is a **draft**. A qualified human reviews, edits, and **owns** it
  before it is committed, filed, or used to gate a release.
- Safety-critical artifacts — test selection for a release gate, risk sign-off,
  bug severity, FHIR conformance, anything touching patient identity/scheduling/
  Rx/billing/consent/audit — require explicit human confirmation; AI may
  recommend, never decide.
- Reviewers verify: facts are grounded, no PHI/secrets leaked, assumptions are
  flagged, and confidence is justified.
- The audit trail records who reviewed each AI-assisted artifact.

---

## 5. Unsafe-Code Handling

- Destructive or risky output (DELETE/UPDATE without WHERE, auth bypass, injection-
  prone string-built SQL, PHI-exposing SELECT, hard-deletes of patient records)
  must carry an inline **WARNING** and a safer alternative — never be emitted
  silently.
- Templates are **safe-by-default**: `sql-generation` is read-only unless explicitly
  set to mutation and then requires WHERE + tenant scope + test-DB warning; patient
  records are soft-deactivated, never hard-deleted (`BR-IDENT-005`).
- The engine flags generated code that lacks assertions, lacks teardown, or hardcodes
  data for human attention.

---

## 6. Compliance Honesty

- omiiCARE_QA models **HIPAA-like** privacy practice for education/portfolio
  purposes and makes **no formal certification claim**. AI must restate this and
  must never assert certification, conformance levels, or regulatory approval.
- AI must not invent business rules, controls, or audit guarantees; it references
  only documented `BR-*` rules and flags anything it cannot ground.

---

## 7. Transparency & Provenance

- Every invocation records prompt ID/version, provider, model, redacted input hash,
  confidence, and (after review) the reviewer — to the audit trail (`AI_CONFIGURATION.md`).
- This makes every AI-assisted artifact explainable and reviewable after the fact,
  satisfying the "transparent and reviewable" principle.

---

## 8. Incident Response

- On suspected leakage (PHI/secret in an output, cache, or log): disable AI
  (`omii.ai.enabled=false`), purge affected cache/log entries, rotate any exposed
  secret, and file a security ticket per `SECURITY.md`.
- Treat any G1–G7 violation as a security incident, not a bug.

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
