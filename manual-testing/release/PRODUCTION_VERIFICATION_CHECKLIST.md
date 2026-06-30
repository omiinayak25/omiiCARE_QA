# Production Verification Checklist

> **Business-flow verification after an omiiCARE_QA deployment is technically
> validated.** Where [DEPLOYMENT_VALIDATION_CHECKLIST.md](DEPLOYMENT_VALIDATION_CHECKLIST.md)
> proves the platform is up, this proves the *system does the right thing*:
> critical user journeys, business rules, FHIR conformance, audit, and data
> safety all behave correctly on the deployed build. Run as the final step of
> [GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md) §6.

## Purpose

Confirm the highest-value, highest-risk journeys work end-to-end on the deployed
environment before declaring the release stable.

## Scope

- **In scope:** critical journeys per portal, key business rules, FHIR/error
  contracts, audit, and data-safety spot checks.
- **Out of scope:** infrastructure health (see [DEPLOYMENT_VALIDATION_CHECKLIST.md](DEPLOYMENT_VALIDATION_CHECKLIST.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Lead | Executes verification; records pass/fail |
| Engineering Lead | Supports diagnosis on failure |
| Maintainer | Confirms "release stable" declaration |

> All verification uses **synthetic, PHI-safe** data only (BR-CONS-005). Do not
> enter real patient data in any environment.

---

## 1. Authentication & access (RR-03, RR-04)

- [ ] `demo.admin / Admin@12345` logs in; access token issued; refresh succeeds.
- [ ] Reused/old refresh token is rejected `401 UNAUTHENTICATED` (rotation, guards RR-09).
- [ ] Logout/expiry invalidates the session.
- [ ] Least-privileged role denied an admin action → `403 ACCESS_DENIED` (BR-RBAC-001), not 500.
- [ ] Cross-tenant id read → `403 CROSS_TENANT_DENIED` + audit event (BR-TENANT-002).

## 2. Patient management (RR-01)

- [ ] Register a synthetic patient; MRN is unique within tenant (BR-IDENT-001).
- [ ] DOB in the future is rejected `400 VALIDATION_FAILED` (BR-IDENT-002).
- [ ] Duplicate-detection surfaces candidates on a name+DOB+contact match (BR-IDENT-003).
- [ ] Patient A's chart never shows Patient B's data (wrong-patient check, RR-01).

## 3. Appointments (RR-02)

- [ ] Book a valid in-availability slot for a provider (BR-APPT-001/002).
- [ ] Overlapping/identical provider slot rejected `409 APPT_DOUBLE_BOOKING` (BR-APPT-003), incl. exact-boundary.
- [ ] Cancellation records reason + actor and releases the slot (BR-APPT-006); patient notified (BR-NOTIF-001).
- [ ] Illegal lifecycle transition rejected (BR-APPT-009).

## 4. FHIR & error contracts (RR-10, RR-13)

- [ ] `GET /fhir/Patient/{id}` returns a valid R4 resource; `gender` is lower-cased (BR-IDENT-002).
- [ ] Error bodies are RFC 7807 `application/problem+json` with correct `code` and `requestId`.
- [ ] No error body leaks SQL/stack/internal detail (guards RR-13).
- [ ] Pagination `size` over 100 is capped or rejected (guards RR-14).

## 5. Audit & compliance (RR-05, RR-12)

- [ ] A PHI read emits an access-audit event (actor, patient, result) (BR-AUDIT-002).
- [ ] A create/update emits a change-audit event with before/after + correlation ID (BR-AUDIT-001).
- [ ] Audit records are append-only (no edit/delete path exposed) (BR-AUDIT-005).
- [ ] Spot-check confirms all visible data is synthetic / obviously fake (BR-CONS-005).

## 6. Notifications (RR — operational)

- [ ] Appointment booking/cancel triggers a notification recorded with outcome (BR-NOTIF-001/004).
- [ ] Notification content carries no unmasked PHI in low-trust channels (BR-NOTIF-003).

## 7. Stability window

- [ ] No critical alerts firing in Grafana during the initial monitoring window.
- [ ] Error rate / latency within budget; no error spikes tied to a release change.
- [ ] No new S1/S2 defects observed during the window.

## 8. Declaration

- [ ] All sections pass (or failures triaged with no open S1).
- [ ] QA Lead records verification result; Maintainer declares the release **stable**.
- [ ] On any S1 failure → trigger [ROLLBACK_CHECKLIST.md](ROLLBACK_CHECKLIST.md).

## Examples

If §3 accepts an identical-boundary double-booking (OMII-BUG-0001 class), the
release is **not** stable; the defect is raised S2/P1 and rollback is considered.

## Future Enhancements

- Promote this to an automated post-deploy E2E journey suite (M7/M8).

## Dependencies

- [GO_LIVE_CHECKLIST.md](GO_LIVE_CHECKLIST.md),
  [DEPLOYMENT_VALIDATION_CHECKLIST.md](DEPLOYMENT_VALIDATION_CHECKLIST.md),
  [ROLLBACK_CHECKLIST.md](ROLLBACK_CHECKLIST.md).
- [../../docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md),
  [../../docs/API_BLUEPRINT.md](../../docs/API_BLUEPRINT.md),
  [../risk-analysis/RISK_REGISTER.md](../risk-analysis/RISK_REGISTER.md).

## References

- RFC 7807; FHIR R4; [../signoff/SIGNOFF_TEMPLATE.md](../signoff/SIGNOFF_TEMPLATE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
