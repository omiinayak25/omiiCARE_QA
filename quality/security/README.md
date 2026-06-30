# omiiCARE_QA — Security Testing (Milestone 7)

> EDUCATIONAL PROJECT. omiiCARE_QA models HIPAA-like privacy, FHIR R4/HL7, OWASP,
> and WCAG practices for learning and QA demonstration. It makes **NO formal
> certification claims**. All security testing targets **owned/local
> infrastructure only** (`http://localhost:8080`). Never point these tools at
> systems you do not own or have written authorization to test.

## 1. Purpose & Scope

This module defines the security testing approach for the omiiCARE_QA backend
(Spring Boot, JWT auth) and React frontend. It covers dynamic scanning (DAST),
software composition analysis (SCA), static analysis preparation (SAST), a
repeatable baseline/regression process, and reporting.

### System Under Test (SUT) — security-relevant facts
- **Auth:** JWT, HS256, access + refresh tokens, refresh-token rotation.
- **Session:** Stateless Spring Security (`SessionCreationPolicy.STATELESS`); no
  server-side session, no `JSESSIONID` reliance.
- **Authorization:** Method-level `@PreAuthorize` permissions —
  `patient:read/write`, `appointment:read/write`, `audit:read`, `admin:manage`.
- **Multi-tenancy:** `tenant_id` on every table; `TenantContext` scopes all
  repository queries (e.g. `findByIdAndTenantId`).
- **Errors:** RFC7807 `ProblemDetail`; stable codes OMII-400/401/403/404/422.
- **Passwords:** BCrypt.
- **Audit:** `audit_log` table.
- **Public endpoints:** `/api/v1/auth/login`, `/api/v1/auth/refresh`,
  `/actuator/health/**`, `/actuator/info`, `/actuator/prometheus`,
  `/v3/api-docs/**`, `/swagger-ui/**`, `/h2-console/**` (dev only).

## 2. OWASP Top 10 (2021) → omiiCARE_QA

| ID | Category | Relevance to omiiCARE_QA |
|----|----------|--------------------------|
| A01 | Broken Access Control | IDOR / cross-tenant patient access, `@PreAuthorize` bypass, privilege escalation |
| A02 | Cryptographic Failures | JWT HS256 secret strength, PHI in transit/at rest, BCrypt cost |
| A03 | Injection | SQLi on `?q=` search, XSS reflected/stored in React, header/log injection |
| A04 | Insecure Design | Token rotation gaps, missing rate limiting, weak lockout |
| A05 | Security Misconfiguration | Security headers, CORS, H2 console exposure, verbose errors, actuator exposure |
| A06 | Vulnerable & Outdated Components | Maven dependency CVEs (Dependency-Check SCA) |
| A07 | Identification & Auth Failures | Brute force, weak credentials, JWT tampering, expired/`alg=none` tokens |
| A08 | Software & Data Integrity Failures | Unsigned/forged JWT acceptance, dependency integrity |
| A09 | Security Logging & Monitoring Failures | `audit_log` completeness, auth-failure logging, PHI in logs |
| A10 | SSRF | Outbound calls (FHIR facade, integrations) — currently low surface |

Full mapping with controls and endpoints: [`OWASP_TOP10_MAPPING.md`](./OWASP_TOP10_MAPPING.md).

## 3. What to Validate

### Authentication
- Login issues access + refresh tokens; refresh rotates the refresh token.
- Invalid credentials → `401` `OMII-401-1`, no user enumeration in messages.
- Expired access token rejected; expired/revoked refresh token rejected.

### Authorization (AuthZ)
- `@PreAuthorize` enforced on every protected method; missing-authority → `403`
  `OMII-403`.
- **IDOR / cross-tenant:** a token for tenant A cannot read tenant B resources
  (repositories use `...AndTenantId`); verify `404` not `403`-leak / `200`.
- **Privilege escalation:** `patient:read` cannot perform `patient:write`,
  `audit:read`, or `admin:manage` actions.

### JWT
- Signature tampering (modified payload) rejected.
- `alg=none` / algorithm-confusion (RS→HS) rejected.
- `tenantId` / `authorities` claims are server-trusted, not client-mutable.

### Session & Cookies
- Stateless: no session fixation surface. If any cookie is set, verify
  `HttpOnly`, `Secure`, `SameSite`.

### Security Headers
- `Strict-Transport-Security`, `X-Content-Type-Options: nosniff`,
  `Content-Security-Policy`, `X-Frame-Options`/`frame-ancestors`,
  `Referrer-Policy`, `Cache-Control` on PHI responses.

### CORS
- Allowed origins are an explicit allowlist; no `*` with credentials; correct
  preflight handling.

### Input Validation
- Bean Validation on request bodies; oversized/malformed → `400` `OMII-400` /
  `OMII-400-1`; business rule → `422` `OMII-422`.

### Rate Limiting
- Login and refresh endpoints throttle brute-force attempts.

### Injection
- **SQLi:** `?q=` patient search uses parameterized queries / JPA — no string
  concatenation.
- **XSS:** React escapes by default; check `dangerouslySetInnerHTML`, URL sinks,
  and reflected error content.

### CSRF
- CSRF disabled is acceptable **only** because auth is a bearer JWT in the
  `Authorization` header (not a cookie). Confirm no auth is cookie-borne.

### Sensitive-Data Exposure
- No PHI/secrets in logs, error bodies, or stack traces; `ProblemDetail` does not
  leak internals.

### File Uploads
- If/when present: content-type allowlist, size limits, AV/extension checks,
  out-of-webroot storage.

## 4. Tooling

| Tool | Type | Use | Location |
|------|------|-----|----------|
| OWASP ZAP (`zaproxy` docker) | DAST | Baseline + authenticated active scan | [`zap/`](./zap/) |
| OWASP Dependency-Check | SCA | Maven dependency CVE scan | [`dependency-check/`](./dependency-check/) |
| SAST (prep) | SAST | Spotless/PMD/SpotBugs + future CodeQL — see below | (CI) |

### SAST preparation
SAST is staged for CI integration. Recommended: **CodeQL** (GitHub Actions
`java` query pack) and **SpotBugs + find-sec-bugs** on the Maven build. This
module ships the DAST/SCA configs now; SAST gate is added when CI is wired.

## 5. Baselines & Regression

1. Establish a clean **baseline** ZAP report and Dependency-Check report on a
   known-good build; commit the report summary (not the HTML) as the reference.
2. Each release re-runs both scans. New WARN/FAIL alerts or CVEs above threshold
   are **regressions** and block release until triaged.
3. False positives are suppressed deliberately:
   - ZAP: tune rule actions in [`zap/zap-baseline.conf`](./zap/zap-baseline.conf).
   - Dependency-Check: add justified entries to
     [`dependency-check/dependency-check-suppressions.xml`](./dependency-check/dependency-check-suppressions.xml).

## 6. Reporting

All security findings follow [`REPORTING_GUIDE.md`](./REPORTING_GUIDE.md):
executive summary → risk classification → OWASP mapping → severity → mitigation.
Manual test cases live in [`SECURITY_TEST_CASES.md`](./SECURITY_TEST_CASES.md).

## 7. Layout

```
quality/security/
├── README.md                     # this file
├── OWASP_TOP10_MAPPING.md
├── SECURITY_TEST_CASES.md
├── REPORTING_GUIDE.md
├── zap/
│   ├── zap-baseline.conf
│   ├── run-zap-baseline.sh
│   └── auth-context.md
└── dependency-check/
    ├── README.md
    └── dependency-check-suppressions.xml
```

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Security Engineer | Initial (Milestone 7) |
