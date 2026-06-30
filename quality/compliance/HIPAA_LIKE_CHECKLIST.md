# HIPAA-like Privacy & Security Checklist — omiiCARE_QA (Milestone 7)

> **EDUCATIONAL — "HIPAA-LIKE", NOT CERTIFIED.** This checklist maps omiiCARE_QA
> features to the *spirit* of the HIPAA Security Rule (45 CFR §164.308/310/312)
> and Privacy Rule safeguards, for learning only. No certification, attestation,
> or compliance claim is made. Use synthetic data only — never real PHI.

Status legend: **Modeled** = implemented to model the safeguard ·
**Partial** = partially modeled · **Gap** = not implemented (tracked) ·
**N/A (educational)** = out of scope for this teaching artifact.

## 1. Administrative Safeguards (§164.308)

| # | Safeguard | omiiCARE_QA mapping | Status | Verify |
|---|-----------|---------------------|--------|--------|
| A1 | Access management — least privilege | Granular `@PreAuthorize` permissions (`patient:read/write`, `appointment:read/write`, `audit:read`, `admin:manage`) | Modeled | SEC-TC-004 (privilege escalation) |
| A2 | Workforce authorization levels | Permission-per-operation; no role hardcoding in business code | Modeled | Review `@PreAuthorize` coverage |
| A3 | Information access controls (tenant) | `tenant_id` on every table; `TenantContext` scoping | Modeled | SEC-TC-003 (cross-tenant) |
| A4 | Audit controls (policy) | `audit_log` table; access gated by `audit:read` | Modeled | Inspect `audit_log` writes |
| A5 | Security incident procedures | Documented in `../security/REPORTING_GUIDE.md` | Partial | Report template present |
| A6 | Risk analysis / OWASP testing | OWASP Top 10 mapping + ZAP/Dependency-Check | Modeled | `../security/` module |
| A7 | Business Associate Agreements | Not applicable to an educational artifact | N/A (educational) | — |

## 2. Physical Safeguards (§164.310)

| # | Safeguard | omiiCARE_QA mapping | Status | Verify |
|---|-----------|---------------------|--------|--------|
| P1 | Facility / device controls | Local-only dev infrastructure | N/A (educational) | — |
| P2 | Workstation security | Out of scope for the app codebase | N/A (educational) | — |

## 3. Technical Safeguards (§164.312)

| # | Safeguard | omiiCARE_QA mapping | Status | Verify |
|---|-----------|---------------------|--------|--------|
| T1 | Unique user identification | JWT `subject` (username) per user | Modeled | Inspect token claims |
| T2 | Authentication | JWT HS256, access + refresh w/ rotation; BCrypt passwords | Modeled | SEC-TC-001/002/009 |
| T3 | Access control (technical) | Stateless Spring Security + `@PreAuthorize` | Modeled | SEC-TC-004 |
| T4 | Audit logs | `audit_log` records security-relevant events | Modeled | Confirm auth + sensitive-action logging |
| T5 | Integrity (data not improperly altered) | JWT `parseSignedClaims` rejects tampered tokens; tenant scoping | Modeled | SEC-TC-001 |
| T6 | Transmission security (encryption in transit) | TLS at the edge; bearer token over HTTPS in deployment | Partial | Verify HSTS / TLS termination |
| T7 | Encryption at rest | Depends on deployment DB config | Gap | Document DB encryption requirement |
| T8 | Automatic logoff / session expiry | Short-lived access tokens; stateless | Modeled | SEC-TC-002 |

## 4. Privacy Rule alignment (modeled)

| # | Principle | omiiCARE_QA mapping | Status | Verify |
|---|-----------|---------------------|--------|--------|
| PR1 | Minimum necessary access | Permission-scoped reads; tenant isolation | Modeled | SEC-TC-003/004 |
| PR2 | No improper disclosure | Cross-tenant read returns `404 OMII-404` (no existence leak) | Modeled | SEC-TC-003 |
| PR3 | Accounting of disclosures | `audit_log` provides an access trail | Partial | Confirm read-access logging scope |
| PR4 | Safeguard PHI in errors/logs | `ProblemDetail` carries no PHI/internals | Modeled | SEC-TC-010 |

## 5. Tracked gaps

- **T7 encryption at rest** — document and configure DB-level encryption for any
  realistic deployment.
- **A5/PR3** — formalize incident runbook and read-access disclosure logging
  scope.

> Restated: every "Modeled" row demonstrates a HIPAA-*like* control for
> education. This document is **not** a HIPAA compliance assessment.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Compliance Lead | Initial (Milestone 7) |
