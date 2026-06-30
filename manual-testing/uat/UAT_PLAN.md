# User Acceptance Test (UAT) Plan

> **The plan for business-stakeholder acceptance of omiiCARE_QA.** UAT validates
> that the system meets real workflow needs across role-based portals, using
> synthetic data, before release. It defines objectives, participants, entry/exit
> criteria, scenarios mapped to portals and business rules, acceptance criteria,
> and defect handling.

## Purpose

Give business representatives a structured way to confirm the system supports
their workflows correctly, and to gate the release on documented acceptance.

## Scope

- **In scope:** functional acceptance of the core healthcare workflows (auth,
  patient, appointment, encounter, billing, FHIR-facing behaviour) across portals.
- **Out of scope:** performance/security/a11y deep testing (Milestone 7), and
  out-of-v1.0 items per [../../MASTER_PROJECT_SPECIFICATION.md](../../MASTER_PROJECT_SPECIFICATION.md) §3.
  All data is synthetic and PHI-safe (BR-CONS-005).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Owns the plan; coordinates UAT; tracks results |
| Business Participants | Execute scenarios; accept/reject against criteria |
| Engineering Lead | Fixes UAT defects; supports the cycle |
| Maintainer / Product Owner | Approves UAT exit and acceptance |

---

## 1. Objectives

- Confirm critical journeys work for each role in its portal.
- Confirm key business rules behave as the business expects (BR-APPT, BR-IDENT, BR-BILL, BR-RBAC, BR-TENANT).
- Confirm error handling and notifications are clear and correct.
- Surface usability/acceptance gaps before release.

## 2. Participants by role

| Portal / Role | Representative | Primary journeys |
|---------------|----------------|------------------|
| Admin (Hospital Admin / `demo.admin`) | Operations rep | User/role management, patient merge, overrides, audit review |
| Reception (Receptionist) | Front-desk rep | Register patient, book/reschedule/cancel appointments |
| Clinical (Doctor / Nurse) | Clinician rep | Open encounter, document, diagnose, order |
| Pharmacy (Pharmacist) | Pharmacy rep | Review/ dispense prescriptions, allergy/interaction stops |
| Billing (Billing Staff) | Billing rep | Invoice from activity, payment, balance handling |
| Insurance (Insurance Staff) | Insurance rep | Coverage verification, claim from issued invoice |
| Patient (Patient) | Patient rep | Self-service: view own data, book, consent |
| Auditor (Auditor) | Compliance rep | Read-only audit/access-log review |

## 3. Entry criteria

- [ ] [../release/RELEASE_CHECKLIST.md](../release/RELEASE_CHECKLIST.md) §1–2 substantially green; smoke passes.
- [ ] UAT environment deployed and validated ([../release/DEPLOYMENT_VALIDATION_CHECKLIST.md](../release/DEPLOYMENT_VALIDATION_CHECKLIST.md)).
- [ ] Synthetic test data and role accounts provisioned.
- [ ] Zero open **S1** defects; known S2 defects communicated to participants.
- [ ] UAT scenarios and acceptance criteria reviewed with participants.

## 4. Exit criteria

- [ ] All **Must-pass** scenarios accepted.
- [ ] No open **S1**; no open **S2** without QA Lead + PO approved deferral.
- [ ] UAT defects triaged; fixes Verified or accepted with rationale.
- [ ] Residual risks recorded ([../risk-analysis/RISK_MATRIX.md](../risk-analysis/RISK_MATRIX.md)).
- [ ] Product Owner records UAT acceptance; feeds [../signoff/SIGNOFF_TEMPLATE.md](../signoff/SIGNOFF_TEMPLATE.md).

## 5. Scenarios (mapped to portals & business rules)

| ID | Portal | Scenario | Business rule | Priority |
|----|--------|----------|---------------|----------|
| UAT-01 | Admin | Log in as `demo.admin`; manage a user and role grant; change is audited | BR-RBAC-007, BR-AUDIT-003 | Must-pass |
| UAT-02 | Reception | Register a synthetic patient; unique MRN; future DOB rejected | BR-IDENT-001/002 | Must-pass |
| UAT-03 | Reception | Duplicate-detection surfaces a candidate on name+DOB match | BR-IDENT-003 | Should-pass |
| UAT-04 | Reception | Book a valid in-availability appointment; patient notified | BR-APPT-001/002, BR-NOTIF-001 | Must-pass |
| UAT-05 | Reception | Attempt overlapping provider booking → rejected (incl. exact boundary) | BR-APPT-003 | Must-pass |
| UAT-06 | Reception | Reschedule re-runs availability + overlap checks | BR-APPT-005 | Should-pass |
| UAT-07 | Reception | Cancel appointment with reason; slot released; notify sent | BR-APPT-006, BR-NOTIF-001 | Must-pass |
| UAT-08 | Clinical | Open encounter from a checked-in appointment; add diagnosis before complete | BR-ENC-001/003 | Must-pass |
| UAT-09 | Pharmacy | Prescribe drug with a known allergy → hard stop; override audited | BR-RX-002, BR-AUDIT-004 | Must-pass |
| UAT-10 | Billing | Generate invoice from completed activity; payment cannot exceed balance | BR-BILL-001/004 | Must-pass |
| UAT-11 | Insurance | Claim only from an issued invoice with valid coverage | BR-BILL-007, BR-INS-003 | Should-pass |
| UAT-12 | Patient | Patient views only own records; cannot see others | BR-IDENT-007, BR-RBAC-005 | Must-pass |
| UAT-13 | Patient | Record and withdraw consent; withdrawal affects later access | BR-CONS-002 | Should-pass |
| UAT-14 | Auditor | Read-only review of access/change audit; cannot mutate | BR-RBAC-006, BR-AUDIT-005 | Must-pass |
| UAT-15 | Cross-cutting | Cross-tenant access denied with `CROSS_TENANT_DENIED` | BR-TENANT-002 | Must-pass |

## 6. Acceptance criteria (per scenario)

A scenario is **Accepted** when:
- The expected outcome occurs and matches the business rule.
- Errors are RFC 7807 with the correct `code` and a clear message (no internal leakage).
- Any required audit event and notification are produced.
- No new S1/S2 defect is found in the flow.

Otherwise it is **Rejected** and a defect is raised
([../bug-templates/BUG_REPORT_TEMPLATE.md](../bug-templates/BUG_REPORT_TEMPLATE.md)).

## 7. Defect handling during UAT

- Participants log defects on the standard template; QA Lead triages daily
  ([../bug-reports/TRIAGE_GUIDE.md](../bug-reports/TRIAGE_GUIDE.md)).
- **S1** halts the affected scenario stream until fixed and Verified.
- Fixes are re-verified on a fresh UAT build before the scenario is re-accepted.
- Daily UAT status reports open defects by severity and Must-pass progress.

## 8. Schedule (indicative)

| Phase | Activity |
|-------|----------|
| Prep | Provision env, data, accounts; walkthrough with participants |
| Cycle 1 | Execute Must-pass scenarios; log defects |
| Fix & re-test | Engineering fixes; QA re-verifies |
| Cycle 2 | Re-run failed + Should-pass scenarios |
| Exit | Confirm exit criteria; record acceptance |

## Examples

UAT-05 fails if an identical-boundary slot is accepted (OMII-BUG-0001 class);
the scenario is Rejected, an S2/P1 defect is raised, and UAT exit is blocked until
it is Verified.

## Future Enhancements

- Capture UAT runs in a tracker with per-scenario evidence attachments.
- Convert accepted Must-pass scenarios into automated E2E journeys (M7).

## Dependencies

- [../bug-templates/BUG_REPORT_TEMPLATE.md](../bug-templates/BUG_REPORT_TEMPLATE.md),
  [../bug-reports/TRIAGE_GUIDE.md](../bug-reports/TRIAGE_GUIDE.md),
  [../risk-analysis/RISK_MATRIX.md](../risk-analysis/RISK_MATRIX.md),
  [../signoff/SIGNOFF_TEMPLATE.md](../signoff/SIGNOFF_TEMPLATE.md).
- [../../docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md).

## References

- ISTQB acceptance testing; [../../MASTER_PROJECT_SPECIFICATION.md](../../MASTER_PROJECT_SPECIFICATION.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
