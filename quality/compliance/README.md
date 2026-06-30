# omiiCARE_QA — Compliance (Milestone 7)

> **EDUCATIONAL PROJECT — NO CERTIFICATION.** omiiCARE_QA *models* HIPAA-like
> privacy/security, FHIR R4, HL7, OWASP, WCAG 2.1 AA, and security-baseline
> practices for **learning and QA demonstration only**. Nothing here is a formal
> assessment, attestation, audit, or certification. No claim of HIPAA, FHIR,
> HL7, OWASP, or WCAG conformance/compliance is made or implied. Do not store or
> process real PHI in this project.

## 1. Scope

This module documents how omiiCARE_QA's implemented features *map to* recognized
standards, so QA can validate behavior against an educational baseline.

| Area | Standard modeled | Document |
|------|------------------|----------|
| Privacy & security (health data) | HIPAA-like Privacy/Security Rule safeguards | [`HIPAA_LIKE_CHECKLIST.md`](./HIPAA_LIKE_CHECKLIST.md) |
| Interoperability | FHIR R4 / HL7 | [`FHIR_HL7_COMPLIANCE.md`](./FHIR_HL7_COMPLIANCE.md) |
| Accessibility + app security | WCAG 2.1 AA + OWASP ASVS-style | [`WCAG_OWASP_BASELINE.md`](./WCAG_OWASP_BASELINE.md) |
| Application security testing | OWASP Top 10 / DAST / SCA | [`../security/`](../security/) |

## 2. Implemented controls referenced by these checklists

- **Access control (RBAC):** method-level `@PreAuthorize` permissions —
  `patient:read/write`, `appointment:read/write`, `audit:read`, `admin:manage`.
- **Authentication:** JWT (HS256, access + refresh, rotation); stateless Spring
  Security; BCrypt password hashing.
- **Tenant isolation:** `tenant_id` on every table; `TenantContext` scopes all
  repository access (`findBy...AndTenantId`).
- **Audit:** `audit_log` table; `audit:read` permission gates access.
- **Error contract:** RFC7807 `ProblemDetail` (OMII-400/401/403/404/422).
- **FHIR surface:** read-only FHIR R4 Patient facade
  (`application/fhir+json`).

## 3. How to use

Each checklist lists a control/criterion, the omiiCARE_QA implementation that
*models* it, and a verification note. Items not implemented are marked
**N/A (educational)** or **Gap** — gaps are tracked, not hidden. Treat every
"compliant" row as "modeled for education," never as certified.

## 4. Disclaimer (restated)

This project is a teaching artifact. It does **not** establish a covered entity,
business associate relationship, or any regulatory obligation, and must not be
relied upon for real-world compliance.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Compliance Lead | Initial (Milestone 7) |
