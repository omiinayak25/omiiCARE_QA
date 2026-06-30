# Severity & Priority Matrix

> **The grading scheme for every omiiCARE_QA defect.** Severity measures *impact
> on the system / patient / data*; priority measures *urgency of the fix in the
> schedule*. They are set independently and may diverge. This document defines
> both scales and the severity×priority decision matrix with healthcare worked
> examples.

## Purpose

Make severity and priority objective and repeatable so two reporters grade the
same defect the same way, and so triage ([../bug-reports/TRIAGE_GUIDE.md](../bug-reports/TRIAGE_GUIDE.md))
and SLAs ([DEFECT_LIFECYCLE.md](DEFECT_LIFECYCLE.md)) flow deterministically.

## Scope

- **In scope:** severity definitions (S1–S4), priority definitions (P1–P4), the
  combination matrix, and healthcare examples.
- **Out of scope:** lifecycle states and SLAs (see [DEFECT_LIFECYCLE.md](DEFECT_LIFECYCLE.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Reporter | Proposes severity from observed impact |
| Triage Lead | Confirms severity; sets priority against the release plan |
| Product / QA Lead | Arbitrates severity↔priority disputes |

---

## 1. Severity (impact)

| Severity | Name | Definition | omiiCARE_QA signals |
|----------|------|------------|---------------------|
| **S1** | Critical | Patient-safety, data-integrity, security breach, or total outage. No workaround. | Wrong-patient data shown; login/RBAC bypass; cross-tenant leak (`BR-TENANT-002`); allergy hard-stop bypassed (`BR-RX-002`); audit event missing (`BR-AUDIT-001`); data loss/corruption. |
| **S2** | High | Major function broken or business rule violated; workaround painful/absent. | Double-booking accepted (`BR-APPT-003`); payment exceeds balance (`BR-BILL-004`); refresh token not rotated; FHIR resource fails R4 schema. |
| **S3** | Medium | Function partially impaired; reasonable workaround exists. | Misleading validation message; pagination over max not capped; wrong sort order; non-blocking FHIR coding warning. |
| **S4** | Low | Cosmetic or trivial; no functional/data impact. | Label typo, minor alignment, inconsistent date format, tooltip wording. |

## 2. Priority (urgency)

| Priority | Name | Definition | Action window (target) |
|----------|------|------------|------------------------|
| **P1** | Immediate | Fix now; blocks release/blocks others; active risk to patients or data. | Same business day; hotfix path. |
| **P2** | High | Fix in current cycle; must clear before the milestone/UAT gate. | Within the current sprint. |
| **P3** | Medium | Schedule into a near-term cycle. | Next 1–2 sprints. |
| **P4** | Low | Backlog; fix opportunistically. | When capacity allows / deferred. |

## 3. Severity × Priority Matrix

Default priority for a given severity, before schedule/business adjustment.
*S* drives the **starting** priority; the Triage Lead may raise priority for a
visible/contractual defect or lower it for a rarely hit edge case.

| | **S1 Critical** | **S2 High** | **S3 Medium** | **S4 Low** |
|--------------------|-----------------|-------------|---------------|------------|
| **Default priority** | **P1** | **P1–P2** | **P3** | **P4** |
| **Release blocker?** | Always | Usually | No | No |
| **Hotfix eligible?** | Yes | If patient/financial impact | No | No |

### Worked healthcare examples

| Example defect | Severity | Priority | Why |
|----------------|----------|----------|-----|
| Patient A's chart renders Patient B's allergies (wrong-patient) | **S1** | **P1** | Direct patient-safety + privacy; release blocker, hotfix. |
| Login accepts an expired/invalid JWT (auth bypass) | **S1** | **P1** | Security breach; full access to PHI; blocks release. |
| Tenant A reads Tenant B's appointment (`CROSS_TENANT_DENIED` missing) | **S1** | **P1** | Data isolation breach (`BR-TENANT-002`). |
| Provider booked for two overlapping appts; system accepts it | **S2** | **P1** | Business-rule violation (`BR-APPT-003`); operational harm; high urgency. |
| Payment of $200 accepted on a $150 balance | **S2** | **P1** | Financial integrity (`BR-BILL-004`). |
| Refresh token reused after refresh (not rotated) | **S2** | **P2** | Security weakness; fix before UAT gate. |
| `403` denial surfaces as `500 INTERNAL_ERROR` | **S3** | **P2** | Wrong error contract; leaks nothing but misleads clients. |
| `?size=5000` returns 5000 rows instead of capping at 100 | **S3** | **P3** | Performance/contract issue; workaround exists. |
| Validation message echoes a raw SQL fragment | **S3** | **P2** | Information leak; low data risk but security-adjacent. |
| FHIR `Patient.gender` emitted as `MALE` not `male` | **S3** | **P2** | R4 code-system case violation; interop break. |
| "Apointment" misspelled on the booking button | **S4** | **P4** | Cosmetic only. |

> **Independence:** an S4 typo on a marketing-critical screen can be raised to
> **P2**; an S1 data bug behind an unreleased feature flag may sit at **P3**
> until the flag is enabled. Severity is fixed by impact; priority by schedule.

## 4. Healthcare escalation rule

Any defect touching **patient safety, PHI exposure, audit completeness, consent,
or tenant isolation** is graded **S1** and **must not be downgraded below P2**,
regardless of reproduction rarity, per
[../../docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md) §5–6.

## Examples

See applied gradings in
[../bug-reports/SAMPLE_DEFECTS.md](../bug-reports/SAMPLE_DEFECTS.md).

## Future Enhancements

- Encode the matrix as Jira automation rules for default priority.
- Add a "safety flag" field that auto-pins severity to S1.

## Dependencies

- [BUG_REPORT_TEMPLATE.md](BUG_REPORT_TEMPLATE.md), [DEFECT_LIFECYCLE.md](DEFECT_LIFECYCLE.md).
- [../../docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md),
  [../../docs/RISK_ANALYSIS.md](../../docs/RISK_ANALYSIS.md).

## References

- ISTQB Foundation — defect severity vs priority.
- RFC 7807 error codes ([../../docs/API_BLUEPRINT.md](../../docs/API_BLUEPRINT.md)).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
