# Sprint Test Plan — Sprint 6.2: Patient + Appointment Modules

> **Sample sprint test plan** refining the
> [MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md) for one two-week sprint. It scopes
> manual verification of the **Patient** and **Appointment** modules of the
> implemented omiiCARE_QA surface, traced through the [RTM](../rtm/RTM.md).

## 1. Sprint Identifier & Goal

- **Sprint:** S6.2 (2026-06-16 → 2026-06-27, 10 working days).
- **Goal:** Achieve `Covered` status for all Critical/High Patient and Appointment
  requirements, with smoke + regression suites green on the `qa` environment.

## 2. In-Scope Test Cases

| Module | Requirement | Manual TC IDs | Type |
|--------|-------------|---------------|------|
| Patient | FR-PAT-001 list/pagination | TC-PAT-001 | Functional, Smoke |
| Patient | FR-PAT-002 register valid | TC-PAT-002 | Functional, Smoke |
| Patient | FR-PAT-003 future-DOB reject (BR-IDENT-002) | TC-PAT-003 | Negative, Boundary |
| Patient | FR-PAT-004 missing-field reject | TC-PAT-004 | Negative |
| Patient | FR-PAT-005 retrieve by ID / 404 | TC-PAT-005 | Functional |
| Patient | FR-PAT-006 update | TC-PAT-006 | Functional |
| Patient | FR-PAT-007 soft-delete (BR-IDENT-005) | TC-PAT-007 | Functional |
| Patient | FR-PAT-008 search `?q=` | TC-PAT-008 | Functional, Boundary |
| Patient | FR-PAT-009 UI list/search/register | TC-PAT-009 | E2E, A11y |
| Patient | FR-PAT-010 `patient:write` authz | TC-PAT-010 | Security |
| Appointment | FR-APPT-001 list | TC-APPT-001 | Functional, Smoke |
| Appointment | FR-APPT-002 book valid | TC-APPT-002 | Functional, Smoke |
| Appointment | FR-APPT-003 end-after-start (BR-APPT-002) | TC-APPT-003 | Negative, Boundary |
| Appointment | FR-APPT-004 double-booking (BR-APPT-003) | TC-APPT-004 | Negative |
| Appointment | FR-APPT-005 cancel (BR-APPT-006) | TC-APPT-005 | Functional |
| Appointment | FR-APPT-006 re-cancel reject | TC-APPT-006 | Negative |
| Appointment | FR-APPT-007 providers list | TC-APPT-007 | Functional |
| Appointment | FR-APPT-008 UI book/list | TC-APPT-008 | E2E, A11y |
| Appointment | FR-APPT-009 `appointment:write` authz | TC-APPT-009 | Security |

Total: **19 cases** in scope this sprint.

## 3. Out of Scope (this sprint)

- Auth-only cases (TC-AUTH-*) and FHIR cases (TC-FHIR-*) — covered in adjacent
  sprints; depended upon only as preconditions (login to obtain a token).
- All **(Future)** requirements from
  [REQUIREMENTS.md](../requirements/REQUIREMENTS.md).

## 4. Capacity

| Tester | Days available | Cases owned | Notes |
|--------|----------------|-------------|-------|
| Senior QA Engineer | 8 | TC-PAT-001..010 | Functional + negative + boundary |
| Senior QA Engineer (2) | 8 | TC-APPT-001..009 | Functional + negative + E2E |
| QA Lead | 2 | Triage + review | Daily triage, exit-gate review |

Assumed throughput ~6 manual cases/tester/day including evidence capture →
adequate headroom for re-runs and exploratory time.

## 5. Entry Criteria

- `qa` environment healthy and seeded with PHI-safe data.
- Patient + Appointment builds deployed; smoke green.
- TC-PAT-* and TC-APPT-* authored and peer-reviewed.
- RTM rows for in-scope requirements at ≥ `Traced`.

## 6. Exit Criteria

- 100% of the 19 cases executed.
- ≥98% pass; 100% of Critical cases (FR-PAT-002/003, FR-APPT-002/003/004) pass.
- No open Critical/High defect on Patient or Appointment.
- RTM rows updated to `Covered`/`Partial` with no critical `Gap`.

## 7. Daily Cadence

| Ceremony | When | Output |
|----------|------|--------|
| Daily stand-up | 09:30 | Blockers, case progress, defect status |
| Defect triage | 14:00 | Severity/priority set with QA Lead |
| Burn-down update | EOD | Cases executed vs planned; pass/fail |
| Mid-sprint review | Day 5 | Re-scope if >40% failure or blocker |
| Exit-gate review | Day 10 | RTM coverage check, sign-off recommendation |

## 8. Risks (sprint-local)

| Risk | Mitigation |
|------|------------|
| Double-booking edge cases under-specified (R-03) | Full boundary set: adjacent, overlapping, identical, back-to-back slots |
| Soft-delete leaves record discoverable (R-01) | Verify deactivated patient excluded from active list but retrievable by ID |
| Seed drift mid-sprint (R-13) | Re-seed and re-run affected cases |

## Dependencies

- [MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md), [../rtm/RTM.md](../rtm/RTM.md),
  [../requirements/REQUIREMENTS.md](../requirements/REQUIREMENTS.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
