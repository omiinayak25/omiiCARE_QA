# Prompt: Bug-Report Drafting

> **Reusable, provider-abstracted prompt template** for the omiiCARE_QA AI engine.
> Turns a failure + evidence into a clean, reproducible, severity-rated bug report
> matching `docs/qa-management/` bug templates. Companion to
> `ai/prompts/bug-report-drafting.md`; this `library/` copy is the v1.0.0-release,
> schema-strict edition.

| Field | Value |
|-------|-------|
| Prompt ID | `bug-report-drafting` (library) |
| Version | `1.0` |
| Capability | Bug-report assistant |
| Providers | Provider-abstracted — Claude / OpenAI / local. No provider-specific syntax. |
| Determinism | `temperature=0`, fixed seed where supported |
| Human review | **Required** — QA owns the filed report |
| PHI policy | Evidence PHI/secret-redacted; synthetic repro data only |

---

## PURPOSE

Draft a high-quality, reproducible bug report from failure evidence and (optional)
RCA, aligned to the project's defect-management standards and bug template, ready
for human review and Jira filing (`/project:sync-jira`).

**When to use:** a confirmed defect ready to be filed.
**When NOT to use:** unconfirmed/flaky failures (triage first); to auto-file without review.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{failure_evidence}}` | Yes | Logs/HTTP/assertion + (redacted) screenshot caption |
| `{{rca_summary}}` | No | Output of `root-cause-analysis` |
| `{{requirement_trace}}` | No | `REQ-*` / `BR-*` the defect violates |
| `{{environment}}` | No | Env, build/version, browser, SUT adapter |
| `{{repro_steps_raw}}` | No | Raw steps the engineer took |
| `{{module}}` | No | Affected module (from 66-module map) |

---

## PROMPT TEMPLATE

```
You are a QA engineer drafting a defect report for omiiCARE_QA, a healthcare QA
platform. Produce a clear, reproducible, severity-rated bug report a triage lead
can action without follow-up questions. You assist a human; QA owns the filing.

CONTEXT
- Failure evidence (redacted): {{failure_evidence}}
- RCA summary: {{rca_summary}}
- Requirement/rule trace: {{requirement_trace}}
- Environment: {{environment}}
- Raw repro steps: {{repro_steps_raw}}
- Module: {{module}}

RULES
1. Redact PHI/secrets; repro data must be synthetic and PHI-safe.
2. Title = concise, specific, searchable (component + observed defect).
3. Steps to reproduce must be numbered, deterministic, and minimal.
4. Separate EXPECTED vs ACTUAL crisply; quote the evidence for ACTUAL.
5. Assign severity (Critical/High/Medium/Low) AND priority (P1-P4) with a one-line
   justification anchored to patient-safety/data-integrity/compliance impact.
6. If a required input is missing, list it under "Open questions", do not invent.
7. Map to REQ-*/BR-* when provided.
8. Output ONLY the OUTPUT SCHEMA.
```

---

## OUTPUT SCHEMA

```yaml
title: "<component>: <observed defect>"
module: "<affected module>"
environment: "<env / build / browser / SUT adapter>"
severity: "Critical | High | Medium | Low"
priority: "P1 | P2 | P3 | P4"
severity_justification: "<patient-safety/data-integrity/compliance rationale>"
preconditions: ["<synthetic precondition>"]
steps_to_reproduce:
  - "1. <step>"
  - "2. <step>"
expected_result: "<expected>"
actual_result: "<actual, evidence-quoted>"
evidence_redacted: ["<log/HTTP/screenshot ref>"]
requirement_trace: ["REQ-XXXX", "BR-XXXX"]
suspected_root_cause: "<from RCA or 'unknown'>"
open_questions: ["<missing input needed to confirm>"]
```

---

## GUARDRAILS

- **Synthetic & redacted:** no PHI, MRNs, real credentials in repro or evidence.
- **Reviewable:** draft only; QA edits and files via `/project:sync-jira`.
- **Severity discipline:** justification tied to safety/integrity/compliance, not vibes.
- **No fabrication:** unknowns go to `open_questions`, never invented.
- **Deterministic & provider-neutral.**

---

## EXAMPLE (abridged)

```yaml
title: "Appointments: provider double-booking allowed via concurrent POST"
module: "Scheduling"
environment: "omiiCARE dev (H2), build 1.0.0-rc2, API"
severity: "High"
priority: "P1"
severity_justification: "Violates BR-APPT-003; risks clinical scheduling integrity."
preconditions: ["Synthetic provider PROV_TEST_01 with single open 10:00 slot"]
steps_to_reproduce:
  - "1. Send two concurrent POST /api/appointments for PROV_TEST_01 @ 10:00"
  - "2. Observe both responses"
expected_result: "One 201 Created; the other 409 Conflict"
actual_result: "Both return 201 (evidence: two appointment IDs created)"
evidence_redacted: ["surefire: two 201 responses; DB shows 2 rows same slot"]
requirement_trace: ["REQ-0142", "BR-APPT-003"]
suspected_root_cause: "Missing optimistic lock / unique constraint on slot."
open_questions: []
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-07-01 | AI QA Engineer | Initial (v1.0.0 prompt library) |
