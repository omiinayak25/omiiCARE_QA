# Security Test Cases — omiiCARE_QA (Milestone 7)

> EDUCATIONAL / local-infra-only. All cases run against a locally-owned
> omiiCARE_QA instance (`http://localhost:8080`) with synthetic data. Assertions
> reference RFC7807 `ProblemDetail` codes (OMII-4xx). Use a dedicated test tenant
> and disposable users — never real PHI.

## Preconditions (shared)
- Backend running at `http://localhost:8080`.
- Test users: `clinician` (`patient:read/write`, `appointment:read/write`),
  `patient-reader` (`patient:read`), `auditor` (`audit:read`),
  `tenant-b-user` (different `tenant_id`).
- Obtain a token: `POST /api/v1/auth/login` → `{accessToken, refreshToken}`.

---

## SEC-TC-001 — JWT signature tampering
**OWASP:** A07 / A08 · **Severity:** Critical
**Objective:** A token with a mutated payload must be rejected.
**Steps:**
1. Log in as `patient-reader`; capture `accessToken`.
2. Base64URL-decode the payload, change `authorities` to include
   `admin:manage` (or change `tenantId`), re-encode, keep the original signature.
3. Call `GET /api/v1/fhir/Patient/{id}` with the tampered token.

**Expected:** `401 Unauthorized`, body `OMII-401`. `JwtService.parse` uses
`verifyWith(key).parseSignedClaims`, so the signature mismatch is rejected; no
elevated access granted.

---

## SEC-TC-002 — Expired access token
**OWASP:** A07 · **Severity:** High
**Objective:** Expired access tokens are not accepted.
**Steps:**
1. Obtain an access token; wait past `accessTokenMinutes` (or mint one with a
   past `exp` in a controlled test).
2. Call any protected endpoint with it.
3. Then exercise refresh: `POST /api/v1/auth/refresh` with the valid refresh
   token.

**Expected:** Step 2 → `401 OMII-401`. Step 3 → new access token issued and the
refresh token is **rotated** (old refresh token subsequently rejected).

---

## SEC-TC-003 — IDOR / cross-tenant patient access
**OWASP:** A01 · **Severity:** Critical
**Objective:** A tenant cannot read another tenant's patient.
**Steps:**
1. As `clinician` (tenant A), create/identify patient with id `N`.
2. Log in as `tenant-b-user` (tenant B, also holding `patient:read`).
3. Call `GET /api/v1/fhir/Patient/N` with tenant B's token.

**Expected:** `404 Not Found`, body `OMII-404` (resource invisible across
tenants). `findByIdAndTenantId(N, tenantB)` returns empty →
`ResourceNotFoundException`. Must **not** return `200` or leak data, and must
**not** return `403` (which would confirm existence).

---

## SEC-TC-004 — Privilege escalation (insufficient authority)
**OWASP:** A01 · **Severity:** Critical
**Objective:** A read-only permission cannot perform write/admin actions.
**Steps:**
1. Log in as `patient-reader` (`patient:read` only).
2. Attempt a `patient:write` action (create/update patient), an `audit:read`
   action (read `audit_log`), and an `admin:manage` action.

**Expected:** Each → `403 Forbidden`, body `OMII-403`. `@PreAuthorize` denies
the call before business logic; no state change; the denied attempt is recorded
in `audit_log`.

---

## SEC-TC-005 — SQL injection attempt on `?q=` search
**OWASP:** A03 · **Severity:** Critical
**Objective:** Search input cannot alter the SQL query.
**Steps:**
1. As `clinician`, call the patient search with injection payloads in `?q=`:
   - `?q=' OR '1'='1`
   - `?q='); DROP TABLE patient;--`
   - `?q=%27%20UNION%20SELECT%20null--`
2. Observe results and server behavior; review backend logs.

**Expected:** Payloads are treated as literal search text → normal `200` with no
extra rows, or `400 OMII-400` on validation. No SQL error, no data dump, no
schema change. Queries are parameterized (JPA `findBy...AndTenantId`); ZAP active
rule 40018 reports no SQLi.

---

## SEC-TC-006 — Missing security headers
**OWASP:** A05 · **Severity:** High
**Objective:** Responses carry required security headers.
**Steps:**
1. `curl -sD - http://localhost:8080/api/v1/fhir/Patient/1 -H "Authorization: Bearer <token>" -o /dev/null`
2. Inspect response headers.

**Expected:** Present and correct: `X-Content-Type-Options: nosniff`,
`X-Frame-Options`/CSP `frame-ancestors`, `Content-Security-Policy`,
`Strict-Transport-Security` (TLS), `Cache-Control: no-store` on PHI responses,
`Referrer-Policy`. ZAP FAIL rules 10038/10020/10021/10035 produce no alert.

---

## SEC-TC-007 — CORS misconfiguration
**OWASP:** A05 · **Severity:** High
**Objective:** CORS uses an explicit allowlist, not `*` with credentials.
**Steps:**
1. Send a preflight: `curl -s -X OPTIONS http://localhost:8080/api/v1/fhir/Patient/1`
   `-H "Origin: https://evil.example" -H "Access-Control-Request-Method: GET" -D -`
2. Inspect `Access-Control-Allow-Origin` / `-Allow-Credentials`.

**Expected:** Unknown origin is **not** reflected; no
`Access-Control-Allow-Origin: *` combined with
`Access-Control-Allow-Credentials: true`. Only configured frontend origin(s) are
allowed. ZAP rule 10098 reports no permissive-CORS alert.

---

## SEC-TC-008 — Rate limiting / brute force on login
**OWASP:** A04 / A07 · **Severity:** High
**Objective:** Repeated failed logins are throttled.
**Steps:**
1. Send 20+ rapid `POST /api/v1/auth/login` with wrong passwords for one user.
2. Observe responses and timing.

**Expected:** After the threshold, requests are throttled (e.g. `429 Too Many
Requests`) or the account/IP is temporarily locked; valid credentials are not
silently accepted faster. Failed attempts are written to `audit_log`. Error
bodies stay generic (`OMII-401-1`) with no user enumeration. *(If no rate limit
exists yet, this case fails and is logged as a finding — see REPORTING_GUIDE.)*

---

## SEC-TC-009 — `alg=none` / algorithm confusion
**OWASP:** A02 / A08 · **Severity:** Critical
**Objective:** Unsigned and algorithm-swapped tokens are rejected.
**Steps:**
1. Craft a token with header `{"alg":"none"}` and a stripped signature.
2. Craft a token signed with a different algorithm/key.
3. Call a protected endpoint with each.

**Expected:** Both → `401 OMII-401`. `parseSignedClaims` requires a valid HS256
signature with the configured key; `none`/foreign-key tokens are rejected.

---

## SEC-TC-010 — Sensitive-data exposure in errors/logs
**OWASP:** A02 / A09 · **Severity:** High
**Objective:** Errors and logs do not leak PHI, secrets, or internals.
**Steps:**
1. Trigger validation (`OMII-400`), not-found (`OMII-404`), and a forced server
   error; inspect `ProblemDetail` bodies.
2. Inspect application logs for tokens, passwords, BCrypt hashes, or PHI.

**Expected:** `ProblemDetail` bodies contain only code/title/detail — no stack
traces, SQL, or PHI. Logs contain no raw tokens/credentials/PHI.

---

## Coverage summary

| Case | OWASP | Severity | Control under test |
|------|-------|----------|--------------------|
| SEC-TC-001 | A07/A08 | Critical | JWT signature verification |
| SEC-TC-002 | A07 | High | Token expiry + refresh rotation |
| SEC-TC-003 | A01 | Critical | Tenant isolation (IDOR) |
| SEC-TC-004 | A01 | Critical | `@PreAuthorize` permissions |
| SEC-TC-005 | A03 | Critical | SQLi on `?q=` search |
| SEC-TC-006 | A05 | High | Security headers |
| SEC-TC-007 | A05 | High | CORS allowlist |
| SEC-TC-008 | A04/A07 | High | Rate limiting / brute force |
| SEC-TC-009 | A02/A08 | Critical | `alg=none` / algorithm confusion |
| SEC-TC-010 | A02/A09 | High | Sensitive-data exposure |

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Security Engineer | Initial (Milestone 7) |
