# OWASP Top 10 (2021) → omiiCARE_QA Mapping

> EDUCATIONAL / local-infra-only. Maps each OWASP Top 10 2021 category to the
> concrete risk in omiiCARE_QA, the test approach, and the implementing
> endpoint/control. No certification is claimed.

| OWASP 2021 Category | omiiCARE_QA Risk | Test Approach | Endpoint / Control |
|---------------------|------------------|---------------|--------------------|
| **A01 Broken Access Control** | Cross-tenant IDOR (read another tenant's patient); `@PreAuthorize` bypass; privilege escalation between permissions | Authenticated ZAP + manual (SEC-TC-003/004); request foreign-tenant IDs and over-privileged actions per role | `FhirPatientController.read` → `findByIdAndTenantId(id, TenantContext.getTenantId())`; method-level `@PreAuthorize("hasAuthority('patient:read')")`; `TenantContext` |
| **A02 Cryptographic Failures** | Weak HS256 secret → forgeable tokens; PHI exposed in transit/at rest; low BCrypt cost | Inspect JWT secret length/entropy; verify TLS at edge; verify BCrypt encoder; confirm no PHI in logs | `JwtService` (`Keys.hmacShaKeyFor`, HS256); `SecurityConfig.passwordEncoder()` BCrypt |
| **A03 Injection** | SQLi via `?q=` patient search; reflected/stored XSS in React; CRLF/log injection | ZAP active scan on `?q=`; manual SQLi payloads (SEC-TC-005); review JPA queries for concatenation; XSS sink review | Patient search endpoint; JPA `findBy...AndTenantId` repositories; React auto-escaping |
| **A04 Insecure Design** | Missing rate limiting on login/refresh; refresh-rotation gaps; weak lockout | Brute-force login (SEC-TC-008); verify refresh rotation invalidates old token | `/api/v1/auth/login`, `/api/v1/auth/refresh`; `JwtService.generateRefreshToken` (rotation) |
| **A05 Security Misconfiguration** | Missing security headers; permissive CORS; dev `h2-console` exposed; verbose errors; actuator over-exposure | ZAP baseline header/CORS rules (SEC-TC-006/007); confirm h2-console disabled in prod; check actuator allowlist | `SecurityConfig` `permitAll` list (`/h2-console/**` dev-only), `frameOptions.sameOrigin`; `/actuator/*` allowlist |
| **A06 Vulnerable & Outdated Components** | Maven dependencies with known CVEs | OWASP Dependency-Check on the Maven build; fail on CVSS ≥ 7.0 | `apps/backend/pom.xml`; `dependency-check/` config |
| **A07 Identification & Auth Failures** | Brute force; weak credentials; JWT tampering; expired / `alg=none` token acceptance; user enumeration | JWT tamper + expiry tests (SEC-TC-001/002); login error-message review; `alg=none` test | `JwtService.parse` (`verifyWith(key)`); `ErrorCode.INVALID_CREDENTIALS` (OMII-401-1) generic message |
| **A08 Software & Data Integrity Failures** | Forged/unsigned JWT accepted; tampered dependencies | Signature-verification tests; Dependency-Check integrity; verify `parseSignedClaims` rejects unsigned tokens | `JwtService.parse` uses `parseSignedClaims`; SCA pipeline |
| **A09 Security Logging & Monitoring Failures** | Incomplete `audit_log`; auth failures not logged; PHI leaked into logs | Verify auth success/failure + sensitive actions write `audit_log`; grep logs for PHI/tokens | `audit_log` table; `audit:read` permission; logging config |
| **A10 SSRF** | Outbound requests from FHIR facade / integrations could be coerced | Review outbound HTTP clients for user-controlled URLs; restrict egress | FHIR facade (`FhirPatientController` is read-only/internal mapping today — low surface); integration clients |

## Notes
- **Error contract:** all failures surface as RFC7807 `ProblemDetail` with stable
  codes — OMII-400/400-1 (validation/malformed), OMII-401/401-1 (auth/credentials),
  OMII-403 (access denied), OMII-404 (not found), OMII-422 (business rule).
  Tests assert on these codes, not on free-text messages.
- **Cross-tenant expectation:** a foreign-tenant resource read must return
  `404 OMII-404` (resource is invisible to the tenant), not `403`, to avoid
  confirming existence.
- **CSRF:** disabled by design — auth is a bearer token in the `Authorization`
  header, not a cookie; verify no auth path is cookie-borne (ZAP rule 10202).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Security Engineer | Initial (Milestone 7) |
