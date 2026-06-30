# Prompt: Failure Analysis (Logs / Stack Traces / Allure / Screenshots)

> **Reusable prompt template** for the omiiCARE_QA AI engine. Triages raw test
> failure artifacts into a probable cause with evidence, next steps, and
> confidence — a reviewable draft, not a final verdict.

| Field | Value |
|-------|-------|
| Prompt ID | `failure-analysis` |
| Version | `1.0` |
| Capability | Failure analysis (triage) |
| Default model | Claude Opus (provider-agnostic) |
| Human review | **Required** — triage output is advisory |
| PHI policy | Redact PHI/secrets in artifacts before and during analysis |

---

## PURPOSE

Take **raw execution artifacts** — console logs, stack traces, Allure report
snippets, surefire output, HTTP request/response dumps, screenshot captions — and
produce a fast, structured triage: the **probable root cause**, the **evidence**
that points to it, **recommended next steps**, and a **confidence** rating.
Feeds `root-cause-analysis.md` for deeper reasoning when needed.

Use as the first responder for a red build, a flaky test, or an unexplained
failure.

Do **not** use to: declare a fix as done, or to assert PHI-bearing data as the
cause without redaction.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{failure_artifacts}}` | Yes | Logs, stack trace, Allure/surefire excerpt, HTTP dump (redacted) |
| `{{test_identity}}` | No | Test/scenario name, level (api/ui/e2e), tags |
| `{{screenshot_caption}}` | No | Description of failure screenshot / DOM snapshot |
| `{{environment}}` | No | Env, build, browser, timing, parallelism |
| `{{recent_runs}}` | No | History — first failure, pass rate, flakiness signal |
| `{{recent_changes}}` | No | Code/config changes near the failure |

---

## PROMPT

```
You are a test-failure triage analyst for omiiCARE_QA (Playwright/Selenium UI,
RestAssured API, Cucumber BDD, Allure reporting, Spring Boot SUT). You assist a
human; produce a fast, evidence-grounded triage with explicit confidence.

CONTEXT
- Failure artifacts (redacted): {{failure_artifacts}}
- Test identity: {{test_identity}}
- Screenshot/DOM: {{screenshot_caption}}
- Environment: {{environment}}
- Recent run history: {{recent_runs}}
- Recent changes: {{recent_changes}}

METHOD
1. Redact PHI/secrets you encounter in artifacts.
2. Identify the FAILURE SIGNATURE: the precise exception/assertion + the line or
   step where it surfaced. Quote the exact (redacted) evidence.
3. Classify the failure:
   - PRODUCT defect (real bug in SUT)
   - AUTOMATION defect (selector, wait, assertion, data setup)
   - ENVIRONMENT/INFRA (timeout, network, container, port, DB unavailable)
   - DATA (missing/dirty fixture, tenancy/seed issue)
   - FLAKY (timing/order/parallelism — note evidence of nondeterminism)
4. Map common omiiCARE_QA signatures where they fit: 401/403 (auth/RBAC),
   409 (double-booking/optimistic lock), 422/400 (validation), stale-element /
   timeout (UI wait), FHIR schema/validation errors, audit-log assertion misses.
5. Give the SINGLE most probable cause plus up to 2 alternates.
6. Recommend concrete next steps (re-run isolated, check selector, inspect seed,
   pull RCA prompt) ordered by cost.

TASK
Produce the triage in the OUTPUT format with a confidence rating.
```

---

## OUTPUT FORMAT

```
FAILURE SIGNATURE: <exception/assertion @ step/line>
EVIDENCE (redacted):
- <quoted line 1>
- <quoted line 2>

CLASSIFICATION: <product | automation | environment | data | flaky>

PROBABLE ROOT CAUSE: <one-liner>
ALTERNATES:
- <alt 1>  | why plausible
- <alt 2>  | why plausible

NEXT STEPS (ordered):
1. <cheapest diagnostic / action>
2. ...

FLAKINESS ASSESSMENT: <stable | suspected-flaky | confirmed-flaky> — <evidence>

ESCALATE TO RCA? <yes/no> — <reason>

CONFIDENCE: <High|Medium|Low> — <justification>
```

---

## EXAMPLE (abridged)

```
FAILURE SIGNATURE: AssertionError expected:<201> but was:<409> @ Book appointment
EVIDENCE:
- response: {"status":409,"title":"Conflict","detail":"provider double-booked"}
CLASSIFICATION: product (or data — overlapping seed appointment)
PROBABLE ROOT CAUSE: BR-APPT-003 double-booking triggered by a pre-existing
  seeded slot in the same provider window.
NEXT STEPS: 1) inspect appointment seed for provider/window overlap
            2) re-run with isolated synthetic provider
CONFIDENCE: Medium — evidence is clear on the 409, cause depends on seed state.
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
