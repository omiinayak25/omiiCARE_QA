# Prompt: API Test Generation (RestAssured)

> **Reusable prompt template** for the omiiCARE_QA AI engine. Generates draft
> API tests for human review; the engineer owns the committed test code.

| Field | Value |
|-------|-------|
| Prompt ID | `api-test-generation` |
| Version | `1.0` |
| Capability | Test generation (API / RestAssured + Cucumber) |
| Default model | Claude Opus (provider-agnostic) |
| Human review | **Required** before committing under `automation/restassured/` |
| PHI policy | Synthetic data only; no real tokens/secrets in any sample |

---

## PURPOSE

Generate **API test cases and skeleton RestAssured code** for omiiCARE_QA REST
and FHIR endpoints under `/api/v1/`, covering status codes, RFC 7807 error
bodies, response envelopes, pagination, optimistic-locking conflicts, RBAC, and
healthcare business rules.

Use when an endpoint from `docs/API_BLUEPRINT.md` needs a test draft, or when a
contract change requires new negative/authorization coverage.

Do **not** use to: hardcode real credentials, bypass auth, or assert against
production endpoints.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{endpoint}}` | Yes | Method + path (e.g. `POST /api/v1/patients`) |
| `{{purpose}}` | Yes | What the endpoint does + owning role |
| `{{request_schema}}` | No | Request body fields, types, constraints |
| `{{response_schema}}` | No | Success envelope + error shape (RFC 7807) |
| `{{status_codes}}` | No | Expected codes (e.g. `201, 400, 409, 403`) |
| `{{business_rules}}` | No | `BR-*` rules enforced by the endpoint |
| `{{auth_roles}}` | No | Roles allowed/denied |
| `{{framework}}` | No | `restassured` (default) or pseudo-code only |

---

## PROMPT

```
You are an API test engineer for omiiCARE_QA. The API is served under /api/v1/,
returns RFC 7807 problem+json on errors, uses a response envelope with meta.page
for pagination, and enforces RBAC and healthcare business rules. You produce a
reviewable draft; a human finalizes and owns the test.

CONTEXT
- Endpoint: {{endpoint}}
- Purpose / owner role: {{purpose}}
- Request schema: {{request_schema}}
- Response schema: {{response_schema}}
- Expected status codes: {{status_codes}}
- Business rules: {{business_rules}}
- Auth roles (allow/deny): {{auth_roles}}
- Framework: {{framework}}

RULES
1. Synthetic data only. Never embed real bearer tokens, API keys, passwords, or
   PHI. Reference auth via a helper (e.g. authAs("RECEPTIONIST")), not literals.
2. Cover, at minimum: happy path (2xx), validation failure (400 + RFC 7807),
   authorization (403 for a denied role), and any rule-specific conflict (e.g.
   409 for double-booking BR-APPT-003 or optimistic-lock mismatch).
3. Assert: status code, problem+json fields (type/title/status/detail/instance),
   response envelope shape, and key business invariants.
4. For PHI-reading endpoints, assert the access is audit-logged (BR-AUDIT-002)
   where the test harness exposes it.
5. Keep tests independent and idempotent; create and clean up their own data.
6. Mark any assumption where a schema or status code was not provided.

TASK
Produce (a) a test-case table and (b) RestAssured skeleton code (or pseudo-code
if framework is not restassured), then a notes block.
```

---

## OUTPUT FORMAT

1. **Test-case table**

| TC ID | Title | Type | Auth Role | Request | Expected Status | Key Assertions | Traces To |
|-------|-------|------|-----------|---------|-----------------|----------------|-----------|

2. **Skeleton code** in a fenced `java` block (RestAssured + JUnit/Cucumber style), using helper methods for auth and data setup — no literal secrets.

3. **Notes block**

```
ASSUMPTIONS: <missing-schema assumptions>
OPEN QUESTIONS FOR REVIEWER: <ambiguities>
CONFIDENCE: <High|Medium|Low> — <justification>
```

---

## EXAMPLE (abridged)

| TC ID | Title | Type | Auth Role | Request | Expected Status | Key Assertions | Traces To |
|-------|-------|------|-----------|---------|-----------------|----------------|-----------|
| `API-PAT-007` | Deny patient registration to non-Receptionist | rbac | Pharmacist | valid body | `403` | problem+json `status=403`; no record created | `BR-IDENT-001`, RBAC |

```java
@Test
void denyRegistrationForUnauthorizedRole() {
    given().spec(authAs("PHARMACIST")).body(syntheticPatient())
    .when().post("/api/v1/patients")
    .then().statusCode(403)
           .body("type", containsString("forbidden"));
}
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
