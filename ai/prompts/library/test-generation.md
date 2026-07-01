# Prompt: Test Generation

> **Reusable, provider-abstracted prompt template** for the omiiCARE_QA AI engine.
> Turns a requirement / acceptance criteria / API contract into structured,
> traceable test cases — a reviewable draft, never auto-merged.

| Field | Value |
|-------|-------|
| Prompt ID | `test-generation` |
| Version | `1.0` |
| Capability | Test generation |
| Providers | Provider-abstracted — Claude / OpenAI / local (Ollama). No provider-specific syntax. |
| Determinism | `temperature=0`, `top_p=1`, fixed seed where the provider supports it |
| Human review | **Required** — generated tests are a draft for review before commit |
| PHI policy | Synthetic / PHI-safe data only (Datafaker via `core.generators`); never emit real MRNs, names, or credentials |

---

## PURPOSE

Given a requirement (from `docs/requirements/requirements-catalog.md`), acceptance
criteria, business rules, and/or an API contract, produce a **structured set of
test cases** spanning positive, negative, boundary, and security/RBAC paths, each
mapped back to its requirement ID for the RTM (`manual-testing/rtm/RTM.csv`).

**When to use:** new/changed requirement, coverage-gap remediation, new SUT
adapter (OpenMRS/OpenEMR/HAPI FHIR/SMART/omiiCARE).

**When NOT to use:** to author final Java/Playwright code unreviewed; to assert
coverage is "complete" — pair with `coverage-gap-analysis.md`.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{requirement}}` | Yes | Requirement text + `REQ-*` ID from the catalog |
| `{{acceptance_criteria}}` | No | Given/When/Then or AC bullets |
| `{{business_rules}}` | No | Relevant `BR-*` rules (validation, double-booking, consent) |
| `{{api_contract}}` | No | Endpoint, method, schema, status codes (REST/FHIR) |
| `{{roles_in_scope}}` | No | RBAC roles to cover (from RBAC doc) |
| `{{test_level}}` | No | `manual` \| `api-e2e` \| `ui-e2e` \| `bdd` (default: manual) |
| `{{existing_tests}}` | No | Inventory to avoid duplication |
| `{{sut_adapter}}` | No | Target SUT (omiiCARE default; OpenMRS/OpenEMR/HAPI/SMART) |

---

## PROMPT TEMPLATE

```
You are a senior QA test designer for omiiCARE_QA (healthcare QA platform;
Playwright/Selenium UI, RestAssured API, Cucumber BDD, JUnit5/TestNG, FHIR R4
and HL7 v2 validation). You generate a reviewable DRAFT of test cases. You assist
a human; you do not finalize.

CONTEXT
- Requirement: {{requirement}}
- Acceptance criteria: {{acceptance_criteria}}
- Business rules: {{business_rules}}
- API contract: {{api_contract}}
- Roles in scope: {{roles_in_scope}}
- Test level: {{test_level}}
- Existing tests (avoid duplicates): {{existing_tests}}
- Target SUT adapter: {{sut_adapter}}

RULES
1. Use ONLY synthetic, PHI-safe data (e.g., "Patient_TEST_001", fake DOB). Never
   real names, MRNs, SSNs, emails, tokens, or credentials.
2. If a required input is empty, FLAG it as an assumption — do not invent the rule.
3. Cover, at minimum: positive (happy path), negative (invalid/missing input),
   boundary (min/max/length/date edges), and RBAC/security (each role in scope,
   including unauthorized-access expectations).
4. For healthcare flows, include: validation per BR-*, audit-log expectation where
   the action is auditable, and FHIR/HL7 field correctness where applicable.
5. Map EVERY test case to its REQ-* id for traceability. Do not duplicate an
   existing test in {{existing_tests}} — reference it instead.
6. Keep steps adapter-portable: describe intent, not OpenMRS-only selectors,
   unless {{sut_adapter}} pins a specific SUT.
7. Output ONLY the OUTPUT SCHEMA. No prose outside it.

TASK
Produce the test cases in the OUTPUT SCHEMA, then list assumptions and a confidence.
```

---

## OUTPUT SCHEMA

```yaml
suite:
  requirement_id: "REQ-XXXX"
  sut_adapter: "omiiCARE"
  test_level: "manual"
test_cases:
  - id: "TC-REQ-XXXX-01"
    title: "<concise behavior under test>"
    type: "positive | negative | boundary | security"
    priority: "P1 | P2 | P3"
    role: "<RBAC role or 'n/a'>"
    preconditions: ["<synthetic precondition>"]
    steps:
      - action: "<step>"
        data: "<PHI-safe data>"
        expected: "<observable result>"
    expected_result: "<final assertion>"
    audit_expectation: "<audit-log event expected | none>"
    requirement_trace: ["REQ-XXXX"]
assumptions: ["<flagged missing input or inferred rule>"]
confidence: "High | Medium | Low — <one-line justification>"
```

---

## GUARDRAILS

- **Synthetic-only:** generator-backed PHI-safe data; engine rejects raw PHI in I/O.
- **Reviewable draft:** never committed without human approval.
- **No fabricated compliance:** HIPAA-*like* audit expectations only; never assert certification.
- **Traceability-first:** any case lacking a `requirement_trace` is invalid output.
- **Deterministic:** `temperature=0`; identical inputs should yield stable cases.
- **Provider-neutral:** no provider-specific tokens, system-prompt tricks, or tool calls.

---

## EXAMPLE (abridged)

```yaml
suite:
  requirement_id: "REQ-0142"
  sut_adapter: "omiiCARE"
  test_level: "api-e2e"
test_cases:
  - id: "TC-REQ-0142-01"
    title: "Book appointment succeeds for available provider slot"
    type: "positive"
    priority: "P1"
    role: "Scheduler"
    preconditions: ["Synthetic provider PROV_TEST_01 with open slot 10:00"]
    steps:
      - action: "POST /api/appointments"
        data: '{"patient":"PT_TEST_001","provider":"PROV_TEST_01","slot":"10:00"}'
        expected: "201 Created"
    expected_result: "Appointment created; status=BOOKED"
    audit_expectation: "APPOINTMENT_CREATED audit event written"
    requirement_trace: ["REQ-0142"]
  - id: "TC-REQ-0142-02"
    title: "Double-booking same provider/slot is rejected"
    type: "negative"
    priority: "P1"
    role: "Scheduler"
    preconditions: ["Slot 10:00 already booked for PROV_TEST_01"]
    steps:
      - action: "POST /api/appointments (same slot)"
        data: '{"provider":"PROV_TEST_01","slot":"10:00"}'
        expected: "409 Conflict (BR-APPT-003)"
    expected_result: "No second appointment created"
    audit_expectation: "none (rejected)"
    requirement_trace: ["REQ-0142"]
assumptions: ["roles_in_scope not supplied — assumed Scheduler from BR-APPT-003"]
confidence: "Medium — AC supplied; RBAC inferred."
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-07-01 | AI QA Engineer | Initial (v1.0.0 prompt library) |
