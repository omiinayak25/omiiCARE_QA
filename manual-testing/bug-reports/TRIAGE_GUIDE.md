# Defect Triage Guide

> **How omiiCARE_QA decides what to fix, when, and by whom.** Triage converts raw
> `New` defects into graded, assigned, scheduled work. This guide defines the
> process, the triage board, decision criteria, and cadence so triage is fast and
> consistent.

## Purpose

Run a disciplined, time-boxed triage that grades each defect
([../bug-templates/SEVERITY_PRIORITY_MATRIX.md](../bug-templates/SEVERITY_PRIORITY_MATRIX.md)),
moves it to the right lifecycle state
([../bug-templates/DEFECT_LIFECYCLE.md](../bug-templates/DEFECT_LIFECYCLE.md)),
and protects the release from S1/S2 escapes.

## Scope

- **In scope:** triage attendees, board columns, decision criteria, cadence,
  and escalation.
- **Out of scope:** the fix workflow and verification (lifecycle doc).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Triage Lead (QA Lead) | Chairs triage; final grade/priority call |
| Engineering Lead | Confirms feasibility; nominates assignee |
| Product Owner | Resolves business-priority and deferral questions |
| Reporter | On call to clarify reproduction if needed |

---

## 1. Triage process

1. **Intake filter** — reject/dup obvious non-defects first (`New → Rejected/Duplicate`).
2. **Validate** — confirm reproduction, environment, evidence, correlation ID. Missing → `Rejected — Insufficient Info`, back to reporter.
3. **Grade severity** — by impact, using the matrix; apply the healthcare escalation rule (safety/PHI/audit/consent/tenant ⇒ S1).
4. **Set priority** — by schedule/business need against the current milestone & UAT gate.
5. **Map root cause hypothesis & business rule** — record candidate `BR-*` and RCA category.
6. **Assign & schedule** — owner + target cycle; `Triaged → Open`.
7. **Flag blockers** — S1, and S2 with patient/financial impact, raised to QA Lead + Maintainer immediately.

## 2. Triage board columns

| Column | Lifecycle state(s) | Meaning |
|--------|--------------------|---------|
| **Inbox** | New | Awaiting triage |
| **Needs Info** | New (info requested) | Blocked on reporter clarification |
| **Triaged** | Triaged | Graded, valid, awaiting assignment |
| **Ready** | Open | Assigned, scheduled |
| **In Progress** | In Progress | Being fixed |
| **In Test** | Fixed / In Test | Awaiting verification |
| **Verified** | Verified | Confirmed fixed |
| **Closed** | Closed | Done |
| **Parked** | Deferred | Postponed (rationale recorded) |
| **Not a Bug** | Rejected / Duplicate | Closed without fix |

## 3. Decision criteria

| Question | Decision |
|----------|----------|
| Reproducible with the evidence given? | No → **Needs Info / Rejected** |
| Touches patient safety, PHI, audit, consent, or tenant isolation? | Yes → **S1, P1, release blocker** |
| Violates a documented business rule (`BR-*`)? | Yes → at least **S2**; priority by reach |
| Wrong error contract (e.g. 500 for a 403)? | **S3**, P2 if it leaks info |
| Cosmetic, no data/function impact? | **S4**, P4 |
| Valid but out of v1.0 scope or low reach? | **Deferred** with QA Lead + PO sign-off |
| Same root cause as an existing defect? | **Duplicate**, link to original |

## 4. Cadence

| Forum | Frequency | Focus |
|-------|-----------|-------|
| Async intake triage | Daily (Triage Lead) | Clear Inbox; grade & assign new defects |
| Triage standup | Daily, 15 min | S1/S2 status, blockers, SLA risk |
| Triage review | Weekly | Backlog grooming, deferrals, RCA trends |
| Pre-gate triage | Before each milestone / UAT / release gate | Confirm zero open S1; review open S2; residual-risk decisions |

## 5. Escalation

- **S1** opened → notify QA Lead + Maintainer within the response SLA (2h); release hold considered immediately.
- **SLA breach** on any S1/S2 → escalated at the daily standup and recorded against [../metrics](../metrics).
- **Deferral of an S1/S2** → requires QA Lead **and** Maintainer approval plus a residual-risk note in [../risk-analysis/RISK_MATRIX.md](../risk-analysis/RISK_MATRIX.md).

## Examples

- OMII-BUG-0008 (cross-tenant read) hits the safety/PHI criterion → graded S1/P1,
  release blocker, escalated at intake.
- OMII-BUG-0006 (page-size cap) is reproducible with a workaround → S3/P3, parked
  to the next sprint. See [SAMPLE_DEFECTS.md](SAMPLE_DEFECTS.md).

## Future Enhancements

- Jira automation to auto-route by component and pre-fill default priority.
- Triage SLA dashboard fed from the tracker.

## Dependencies

- [../bug-templates/SEVERITY_PRIORITY_MATRIX.md](../bug-templates/SEVERITY_PRIORITY_MATRIX.md),
  [../bug-templates/DEFECT_LIFECYCLE.md](../bug-templates/DEFECT_LIFECYCLE.md),
  [SAMPLE_DEFECTS.md](SAMPLE_DEFECTS.md).

## References

- ISTQB defect management; Agile triage practice.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
