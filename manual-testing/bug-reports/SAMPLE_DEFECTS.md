# Sample Defects

> **Eight realistic, fully-filled defects against the omiiCARE_QA SUT.** Each uses
> the standard [BUG_REPORT_TEMPLATE.md](../bug-templates/BUG_REPORT_TEMPLATE.md),
> is graded per [SEVERITY_PRIORITY_MATRIX.md](../bug-templates/SEVERITY_PRIORITY_MATRIX.md),
> and is traced to a business rule, error code, and RCA category. These are
> representative examples for training and triage calibration — not a live defect
> log (that lives in the tracker).

## Purpose

Provide a calibrated reference set so reporters grade and document consistently,
and so triage practice has concrete, system-true material.

## Index

| ID | Summary | Sev | Pri | BR | RCA |
|----|---------|-----|-----|----|-----|
| OMII-BUG-0001 | Overlapping appointment accepted at exact slot boundary | S2 | P1 | BR-APPT-003 | requirements / test-gap |
| OMII-BUG-0002 | Duplicate MRN created under concurrent registration (race) | S1 | P1 | BR-IDENT-001 | design |
| OMII-BUG-0003 | Refresh token not rotated on refresh (reuse accepted) | S2 | P2 | BR-AUDIT-003 | coding |
| OMII-BUG-0004 | RBAC denial returns 500 instead of 403 | S3 | P2 | BR-RBAC-001 | integration |
| OMII-BUG-0005 | FHIR `Patient.gender` not lower-cased (R4 violation) | S3 | P2 | BR-IDENT-002 | integration |
| OMII-BUG-0006 | Pagination `size` over max not capped | S3 | P3 | — | config |
| OMII-BUG-0007 | Validation error leaks raw SQL fragment | S2 | P2 | BR-CONS-004 | coding |
| OMII-BUG-0008 | Cross-tenant patient read succeeds (isolation breach) | S1 | P1 | BR-TENANT-002 | coding |

---

## OMII-BUG-0001 — appointment: overlapping booking accepted at exact slot boundary

| Field | Value |
|-------|-------|
| Defect ID | OMII-BUG-0001 |
| Module / Component | appointment |
| Environment | qa |
| Build / Version | 0.5.0 / develop@a1b2c3d |
| Severity | S2 | Priority | P1 |
| Status | Triaged |
| Business Rule | BR-APPT-003 | Error code | (none returned — should be `APPT_DOUBLE_BOOKING` 409) |
| Reproducibility | Always | Regression? | No |
| Test Case Ref | TC-APPT-OVERLAP-BOUNDARY | Found in cycle | Cycle 1 |
| Reporter | QA Engineer | Assignee | Backend Eng |

### Preconditions
- Logged in as Receptionist in tenant `T-001`.
- Provider `P-100` has a `BOOKED` appointment 10:00–10:30 at branch `B-1`.

### Steps to Reproduce
1. `POST /api/v1/appointments` for provider `P-100`, branch `B-1`, slot **10:30–11:00**.
2. Observe it is accepted.
3. Now `POST` provider `P-100`, branch `B-1`, slot **10:15–10:45** (clearly overlapping) — correctly rejected `409 APPT_DOUBLE_BOOKING`.
4. Re-test the touching case: existing 10:00–10:30, new **10:30–11:00** is allowed (correct), but existing 10:00–10:30, new **09:45–10:00** abutting on the left is also fine; the failing case: existing 10:00–10:30, new slot **10:00–10:30** identical is accepted.

### Expected Result
Identical or overlapping ranges for the same provider are rejected with `409 APPT_DOUBLE_BOOKING`; only half-open touching intervals `[start, end)` are allowed.

### Actual Result
An identical 10:00–10:30 slot is accepted (`201 Created`); the overlap predicate uses strict `>` and mis-handles the equality boundary.

### Evidence
- Request/response bodies for steps 1 and 4 attached (HAR).

### Logs / Correlation ID
- `X-Request-Id`: 7f3a1c20-0b11-4e0a-9a01-2c4d6e8f0a11
- No double-booking exception logged for the identical slot.

### Suspected Root Cause
Overlap check uses `existing.end > new.start && existing.start < new.end` with strict comparisons that drop the equal-boundary case. RCA category: **requirements** (boundary inclusivity unspecified), secondary **test-gap**.

---

## OMII-BUG-0002 — patient: duplicate MRN created under concurrent registration (race)

| Field | Value |
|-------|-------|
| Defect ID | OMII-BUG-0002 |
| Module / Component | patient |
| Environment | qa |
| Build / Version | 0.5.0 / develop@a1b2c3d |
| Severity | S1 | Priority | P1 |
| Status | Open |
| Business Rule | BR-IDENT-001 | Error code | (none — duplicate persisted) |
| Reproducibility | Intermittent (≈4/10 under load) | Regression? | No |
| Test Case Ref | TC-IDENT-MRN-UNIQUE | Found in cycle | Cycle 1 |
| Reporter | SDET III | Assignee | Backend Eng |

### Preconditions
- Two Receptionist sessions in tenant `T-001`.
- MRN allocator configured to derive next MRN from a max-query.

### Steps to Reproduce
1. Fire two simultaneous `POST /api/v1/patients` requests (same tenant) within the same millisecond window (use the concurrency harness, 10 parallel).
2. Inspect created records.

### Expected Result
Exactly one patient per real person; MRN unique per tenant; the second colliding insert fails with a constraint/`409 VERSION_CONFLICT` and retries to a fresh MRN.

### Actual Result
Two patient rows share the same MRN within tenant `T-001` (e.g. `MRN-000412` twice), violating BR-IDENT-001.

### Evidence
- DB query showing two rows with identical `(tenant_id, mrn)`.

### Logs / Correlation ID
- `X-Request-Id`: c1d2e3f4-… (two correlated requests).

### Suspected Root Cause
MRN uniqueness enforced only in application code via read-then-write; no DB unique constraint on `(tenant_id, mrn)`, so concurrent inserts both read the same max. RCA category: **design**.

---

## OMII-BUG-0003 — auth: refresh token not rotated on refresh (reuse accepted)

| Field | Value |
|-------|-------|
| Defect ID | OMII-BUG-0003 |
| Module / Component | auth |
| Environment | qa |
| Build / Version | 0.5.0 / develop@a1b2c3d |
| Severity | S2 | Priority | P2 |
| Status | Open |
| Business Rule | BR-AUDIT-003 | Error code | (none — should reject reused token `401 UNAUTHENTICATED`) |
| Reproducibility | Always | Regression? | No |
| Test Case Ref | TC-AUTH-REFRESH-ROTATE | Found in cycle | Cycle 1 |
| Reporter | Security-minded QA | Assignee | Backend Eng |

### Preconditions
- Logged in as `demo.admin / Admin@12345`; capture refresh token `RT-1`.

### Steps to Reproduce
1. `POST /api/v1/auth/refresh` with `RT-1` → receive access token + refresh token `RT-2`.
2. Call `POST /api/v1/auth/refresh` **again with the old `RT-1`**.

### Expected Result
Refresh-token rotation: `RT-1` is invalidated on first use; reusing `RT-1` returns `401 UNAUTHENTICATED` and is audited as a possible replay (BR-AUDIT-003).

### Actual Result
Reusing `RT-1` still returns a new valid token pair; old refresh tokens are not invalidated, enabling token replay if exfiltrated.

### Evidence
- Two successful refresh responses, both using `RT-1`.

### Logs / Correlation ID
- `X-Request-Id`: 9a8b7c6d-…; no token-replay audit event emitted.

### Suspected Root Cause
Refresh handler issues a new token but never marks the presented token consumed (no rotation/denylist). RCA category: **coding**.

---

## OMII-BUG-0004 — platform: RBAC denial returns 500 instead of 403

| Field | Value |
|-------|-------|
| Defect ID | OMII-BUG-0004 |
| Module / Component | appointment (any RBAC-guarded endpoint) |
| Environment | qa |
| Build / Version | 0.5.0 / develop@a1b2c3d |
| Severity | S3 | Priority | P2 |
| Status | Triaged |
| Business Rule | BR-RBAC-001 | Error code | returns `INTERNAL_ERROR` 500; should be `ACCESS_DENIED` 403 |
| Reproducibility | Always | Regression? | Yes (worked in 0.4.x) |
| Test Case Ref | TC-RBAC-DENY-CONTRACT | Found in cycle | Cycle 1 |
| Reporter | API QA | Assignee | Backend Eng |

### Preconditions
- Logged in as Patient (least-privileged) in tenant `T-001`.

### Steps to Reproduce
1. As Patient, call an admin-only endpoint, e.g. `POST /api/v1/patients/{id}/merge`.

### Expected Result
`403` with RFC 7807 body `code: ACCESS_DENIED` (BR-RBAC-001 default-deny), no stack detail.

### Actual Result
`500 INTERNAL_ERROR`; an `AccessDeniedException` escapes the security filter and is mapped as an unhandled fault.

### Evidence
- Response body showing `status: 500, code: INTERNAL_ERROR`.

### Logs / Correlation ID
- `X-Request-Id`: 4d3c2b1a-…; stack trace for `AccessDeniedException` in app log.

### Suspected Root Cause
`AccessDeniedException` thrown after the security filter chain (at method-security layer) is not mapped by the central exception handler to a 403. RCA category: **integration** (security ↔ exception-handler contract).

---

## OMII-BUG-0005 — FHIR: `Patient.gender` not lower-cased (R4 code-system violation)

| Field | Value |
|-------|-------|
| Defect ID | OMII-BUG-0005 |
| Module / Component | FHIR |
| Environment | qa |
| Build / Version | 0.5.0 / develop@a1b2c3d |
| Severity | S3 | Priority | P2 |
| Status | Open |
| Business Rule | BR-IDENT-002 | Error code | (schema validation failure downstream) |
| Reproducibility | Always | Regression? | No |
| Test Case Ref | TC-FHIR-PATIENT-GENDER | Found in cycle | Cycle 1 |
| Reporter | Interop QA | Assignee | Backend Eng |

### Preconditions
- A patient registered with administrative sex `Male`.

### Steps to Reproduce
1. `GET /fhir/Patient/{id}` (FHIR R4 endpoint).
2. Inspect the `gender` element.

### Expected Result
`"gender": "male"` — FHIR R4 `AdministrativeGender` codes are lower-case (`male | female | other | unknown`); a conformant bundle validates.

### Actual Result
`"gender": "MALE"`; the internal enum name is emitted verbatim, failing R4 schema/terminology validation and breaking downstream consumers.

### Evidence
- FHIR resource JSON; validator output flagging invalid code.

### Logs / Correlation ID
- `X-Request-Id`: 2b3c4d5e-…

### Suspected Root Cause
Mapper serializes the Java enum `name()` instead of mapping to the FHIR code system value. RCA category: **integration** (standards mapping).

---

## OMII-BUG-0006 — platform: pagination `size` over max not capped

| Field | Value |
|-------|-------|
| Defect ID | OMII-BUG-0006 |
| Module / Component | appointment (list endpoint) |
| Environment | qa |
| Build / Version | 0.5.0 / develop@a1b2c3d |
| Severity | S3 | Priority | P3 |
| Status | Open |
| Business Rule | — | Error code | should `400 VALIDATION_FAILED` or cap at 100 |
| Reproducibility | Always | Regression? | No |
| Test Case Ref | TC-API-PAGE-SIZE-MAX | Found in cycle | Cycle 1 |
| Reporter | API QA | Assignee | Backend Eng |

### Preconditions
- ≥ 500 appointments seeded in tenant `T-001`.

### Steps to Reproduce
1. `GET /api/v1/appointments?page=0&size=5000`.

### Expected Result
`size` is capped at the documented max of 100 (per [../../docs/API_BLUEPRINT.md](../../docs/API_BLUEPRINT.md) §4), or rejected `400 VALIDATION_FAILED`.

### Actual Result
The endpoint returns up to 5000 rows in one page; the cap is not enforced, risking memory/latency under load.

### Evidence
- Response with `meta.page.size = 5000`.

### Logs / Correlation ID
- `X-Request-Id`: 6e7f8a9b-…; elevated query/serialization time.

### Suspected Root Cause
Max-page-size property unset/not applied to the `Pageable` resolver. RCA category: **configuration**.

---

## OMII-BUG-0007 — platform: validation error leaks raw SQL fragment

| Field | Value |
|-------|-------|
| Defect ID | OMII-BUG-0007 |
| Module / Component | patient (search) |
| Environment | qa |
| Build / Version | 0.5.0 / develop@a1b2c3d |
| Severity | S2 | Priority | P2 |
| Status | Triaged |
| Business Rule | BR-CONS-004 (info disclosure) | Error code | `INTERNAL_ERROR` 500 leaking detail |
| Reproducibility | Always | Regression? | No |
| Test Case Ref | TC-SEC-ERROR-LEAK | Found in cycle | Cycle 1 |
| Reporter | Security-minded QA | Assignee | Backend Eng |

### Preconditions
- Logged in as Receptionist in tenant `T-001`.

### Steps to Reproduce
1. `GET /api/v1/patients?sort=lastName);DROP--` (malformed/unwhitelisted sort field).

### Expected Result
`400 VALIDATION_FAILED` with a safe, generic field error; no internals exposed (information-minimisation, BR-CONS-004 spirit).

### Actual Result
`500 INTERNAL_ERROR` whose `detail` echoes a raw SQL/ORM fragment and column names, leaking schema internals to the client.

### Evidence
- Response body containing the SQL fragment in `detail`.

### Logs / Correlation ID
- `X-Request-Id`: 8a9b0c1d-…

### Suspected Root Cause
Unwhitelisted sort param reaches the query layer; the resulting exception message is passed through to the Problem Details `detail` instead of being sanitized. RCA category: **coding** (unsafe error mapping + missing input whitelist).

---

## OMII-BUG-0008 — platform: cross-tenant patient read succeeds (isolation breach)

| Field | Value |
|-------|-------|
| Defect ID | OMII-BUG-0008 |
| Module / Component | patient |
| Environment | qa |
| Build / Version | 0.5.0 / develop@a1b2c3d |
| Severity | S1 | Priority | P1 |
| Status | Open |
| Business Rule | BR-TENANT-002 | Error code | should `403 CROSS_TENANT_DENIED`; returns `200` |
| Reproducibility | Always | Regression? | Unknown |
| Test Case Ref | TC-TENANT-CROSS-READ | Found in cycle | Cycle 1 |
| Reporter | SDET III | Assignee | Backend Eng |

### Preconditions
- Patient `PT-B-9` belongs to tenant `T-002`.
- Logged in as Receptionist of tenant `T-001`; capture `PT-B-9`'s id.

### Steps to Reproduce
1. As Tenant `T-001` user, `GET /api/v1/patients/PT-B-9` (an id owned by Tenant `T-002`).

### Expected Result
`403 CROSS_TENANT_DENIED` plus a tenant-violation audit event (BR-TENANT-002, BR-AUDIT-002). The record must not be returned.

### Actual Result
`200 OK` returns Tenant `T-002`'s patient PHI to a Tenant `T-001` user — a hard data-isolation breach.

### Evidence
- Response body showing the foreign patient's demographics; redacted screenshot.

### Logs / Correlation ID
- `X-Request-Id`: 0c1d2e3f-…; no cross-tenant denial audit event present.

### Suspected Root Cause
The fetch-by-id query is not filtered by the caller's tenant context (missing `tenant_id` predicate / row scope), so any known id resolves regardless of tenant. RCA category: **coding** (missing tenant filter on read path).

---

## Notes

- All data above is synthetic and PHI-safe per BR-CONS-005 / [../../MASTER_PROJECT_SPECIFICATION.md](../../MASTER_PROJECT_SPECIFICATION.md) §9.
- Defects S1 (0002, 0008) and S2/P1 (0001) are release blockers per
  [../bug-templates/DEFECT_LIFECYCLE.md](../bug-templates/DEFECT_LIFECYCLE.md) §5.

## Future Enhancements

- Link each sample to its automated reproduction test once built (M7).

## Dependencies

- [../bug-templates/BUG_REPORT_TEMPLATE.md](../bug-templates/BUG_REPORT_TEMPLATE.md),
  [../bug-templates/SEVERITY_PRIORITY_MATRIX.md](../bug-templates/SEVERITY_PRIORITY_MATRIX.md),
  [../bug-templates/ROOT_CAUSE_CATEGORIES.md](../bug-templates/ROOT_CAUSE_CATEGORIES.md).
- [../../docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md),
  [../../docs/API_BLUEPRINT.md](../../docs/API_BLUEPRINT.md).

## References

- RFC 7807 Problem Details; FHIR R4 `AdministrativeGender`; OWASP A01/A05.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
