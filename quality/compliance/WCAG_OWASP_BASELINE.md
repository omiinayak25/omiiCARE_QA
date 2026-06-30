# WCAG 2.1 AA + OWASP ASVS-style Baseline — omiiCARE_QA (Milestone 7)

> **EDUCATIONAL — NOT CERTIFIED.** Models WCAG 2.1 Level AA accessibility and an
> OWASP ASVS-style application-security baseline for learning only. No WCAG or
> ASVS conformance/certification claim is made.

Status: **Modeled** · **Partial** · **Gap** · **N/A (educational)**.

---

## Part A — WCAG 2.1 Level AA (React frontend)

| # | WCAG SC | Principle | Check | Status | Verify |
|---|---------|-----------|-------|--------|--------|
| W1 | 1.1.1 Non-text Content (A) | Perceivable | All images have `alt`; icons have accessible names | Partial | Axe / manual |
| W2 | 1.3.1 Info & Relationships (A) | Perceivable | Semantic HTML / ARIA; form labels associated | Partial | Axe |
| W3 | 1.4.3 Contrast (Minimum) (AA) | Perceivable | Text contrast >= 4.5:1 (3:1 large) | Partial | Contrast tool |
| W4 | 1.4.11 Non-text Contrast (AA) | Perceivable | UI/control contrast >= 3:1 | Partial | Manual |
| W5 | 2.1.1 Keyboard (A) | Operable | All functionality keyboard-operable | Partial | Keyboard pass |
| W6 | 2.4.3 Focus Order (A) | Operable | Logical focus order | Partial | Keyboard pass |
| W7 | 2.4.7 Focus Visible (AA) | Operable | Visible focus indicator | Partial | Manual |
| W8 | 3.1.1 Language of Page (A) | Understandable | `<html lang>` set | Modeled | Inspect markup |
| W9 | 3.3.1 Error Identification (A) | Understandable | Form errors identified in text | Partial | Map to OMII-400/422 |
| W10 | 3.3.2 Labels or Instructions (A) | Understandable | Inputs labelled | Partial | Axe |
| W11 | 4.1.2 Name, Role, Value (A) | Robust | Custom controls expose name/role/value | Partial | Axe / screen reader |
| W12 | 4.1.3 Status Messages (AA) | Robust | Live regions for async status | Gap | Manual |

**Tooling:** axe-core / Lighthouse for automated checks, plus manual keyboard and
screen-reader passes. Server validation errors (`OMII-400`/`OMII-422`) must be
surfaced as accessible, text-based form errors (W9).

---

## Part B — OWASP ASVS-style security baseline (backend)

| # | ASVS area | Requirement | omiiCARE_QA mapping | Status |
|---|-----------|-------------|---------------------|--------|
| V1 | V2 Authentication | Strong credential storage | BCrypt password hashing | Modeled |
| V2 | V2 Authentication | Secure session/token handling | JWT HS256, access+refresh, rotation, stateless | Modeled |
| V3 | V3 Session Mgmt | No server session fixation | Stateless (`SessionCreationPolicy.STATELESS`) | Modeled |
| V4 | V4 Access Control | Enforced server-side authorization | `@PreAuthorize` per operation | Modeled |
| V5 | V4 Access Control | Multi-tenant data isolation | `tenant_id` + `TenantContext` scoping | Modeled |
| V6 | V5 Validation | Input validation & output encoding | Bean Validation → OMII-400/422; React escaping | Partial |
| V7 | V5 Validation | Injection-safe data access | Parameterized JPA queries | Modeled |
| V8 | V7 Error/Logging | No sensitive data in errors/logs | RFC7807 `ProblemDetail`; PHI-free logs | Modeled |
| V9 | V7 Error/Logging | Security event logging | `audit_log` table | Modeled |
| V10 | V8 Data Protection | Encryption in transit | TLS at edge / HSTS | Partial |
| V11 | V8 Data Protection | Encryption at rest | Deployment DB config | Gap |
| V12 | V13 API/Web | Security headers + CORS allowlist | Verify headers/CORS (SEC-TC-006/007) | Partial |
| V13 | V14 Config | No debug/dev surfaces in prod | `h2-console` dev-only; actuator allowlist | Partial |
| V14 | V1 Architecture | Dependency hygiene | OWASP Dependency-Check gate | Modeled |

**Cross-reference:** ASVS items map to the security test cases in
[`../security/SECURITY_TEST_CASES.md`](../security/SECURITY_TEST_CASES.md) and the
[`../security/OWASP_TOP10_MAPPING.md`](../security/OWASP_TOP10_MAPPING.md).

---

## Tracked gaps

- **W12** — add ARIA live regions for async status messages.
- **V11** — document/configure encryption at rest.
- **V10/V12/V13** — verify TLS/HSTS, security headers, CORS allowlist, and that
  dev surfaces are disabled in non-dev profiles.

> Restated: educational baseline only — **not** a WCAG or ASVS certification.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Compliance Lead | Initial (Milestone 7) |
