# Defect Lifecycle

> **The state machine every omiiCARE_QA defect follows from report to closure.**
> States are guarded: only the listed transitions are legal. The lifecycle, its
> RACI, and severity-based SLAs make defect flow predictable across cycles and
> auditable for the release gate.

## Purpose

Define defect states, legal transitions, ownership (RACI), and resolution SLAs by
severity so that no defect stalls silently and every closure is traceable.

## Scope

- **In scope:** state definitions, the transition diagram, RACI, and SLAs.
- **Out of scope:** grading scales (see [SEVERITY_PRIORITY_MATRIX.md](SEVERITY_PRIORITY_MATRIX.md))
  and triage cadence (see [../bug-reports/TRIAGE_GUIDE.md](../bug-reports/TRIAGE_GUIDE.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Reporter (QA) | Raise, supply evidence, verify, reopen/close |
| Triage Lead | Triage, assign, set severity/priority, defer/reject/dup |
| Assignee (Engineer) | Reproduce, fix, record RCA, move to In Test |
| QA Lead | Approve deferrals, own SLA adherence, gate the release |

---

## 1. States

| State | Meaning | Entered by |
|-------|---------|-----------|
| **New** | Just reported; not yet reviewed. | Reporter |
| **Triaged** | Reviewed; severity/priority confirmed; valid. | Triage Lead |
| **Open** | Accepted, assigned, awaiting work. | Triage Lead |
| **In Progress** | Engineer actively fixing. | Assignee |
| **Fixed** | Code change complete and merged to `develop`. | Assignee |
| **In Test** | Fix deployed to a test env; awaiting verification. | Assignee / CI |
| **Verified** | Re-tested and confirmed fixed. | QA Verifier |
| **Closed** | Verified and accepted; defect ends. | QA Lead / Verifier |
| **Reopened** | Verification failed or regression returned. | QA Verifier |
| **Rejected** | Not a defect / insufficient info / works as designed. | Triage Lead |
| **Deferred** | Valid but intentionally postponed (with rationale). | QA Lead |
| **Duplicate** | Same root cause as an existing defect (linked). | Triage Lead |

## 2. Legal Transitions

| From | To | Condition |
|------|----|-----------|
| New | Triaged | Reviewed and valid |
| New | Rejected | Invalid / insufficient info / not-a-bug |
| New | Duplicate | Matches an existing defect |
| Triaged | Open | Accepted and assigned |
| Triaged | Deferred | Postponed with approval |
| Open | In Progress | Work started |
| In Progress | Fixed | Fix merged |
| In Progress | Open | Blocked / reassigned |
| Fixed | In Test | Deployed to test env |
| In Test | Verified | Re-test passes |
| In Test | Reopened | Re-test fails |
| Verified | Closed | Accepted |
| Reopened | Triaged | Re-graded and re-routed |
| Deferred | Triaged | Re-prioritized later |
| Closed | Reopened | Regression recurs |

## 3. State Diagram (ASCII)

```
                 +-----------+   reject/dup/insufficient
   report  ----> |    New    | -------------------+----> [ Rejected ]
                 +-----+-----+                     |
                       | valid                     +----> [ Duplicate ]
                       v
                 +-----------+    postpone (approved)
                 |  Triaged  | -----------------------> [ Deferred ]
                 +-----+-----+  <----------------------------+  re-prioritize
                       | accept + assign
                       v
                 +-----------+
                 |   Open    | <----------+ blocked/reassign
                 +-----+-----+            |
                       | start           |
                       v                 |
                 +-------------+ ---------+
                 | In Progress |
                 +-----+-------+
                       | fix merged
                       v
                 +-----------+   deploy to test env
                 |   Fixed   | -----------------------+
                 +-----------+                        v
                                               +-------------+
                          re-test FAILS  +----- |   In Test   |
                                         |      +------+------+
                                         v             | re-test PASSES
                                  +------------+        v
                                  |  Reopened  |  +-----------+
                                  +-----+------+  | Verified  |
                                        | re-grade+-----+-----+
                                        v              | accept
                                   (-> Triaged)        v
                                                 +-----------+
                                                 |  Closed   |
                                                 +-----+-----+
                                                       | regression recurs
                                                       v
                                                 (-> Reopened)
```

## 4. RACI

| Activity | Reporter (QA) | Triage Lead | Assignee (Eng) | QA Lead |
|----------|:-------------:|:-----------:|:--------------:|:-------:|
| Raise defect | **R/A** | I | I | I |
| Triage & grade | C | **R/A** | C | I |
| Assign | I | **R/A** | I | C |
| Reproduce & fix | I | I | **R/A** | I |
| Record RCA | I | C | **R/A** | I |
| Verify fix | **R/A** | I | C | I |
| Approve deferral | I | C | I | **R/A** |
| Close | C | I | I | **R/A** |
| Reject / mark duplicate | C | **R/A** | I | I |

*R = Responsible, A = Accountable, C = Consulted, I = Informed.*

## 5. SLAs by Severity

Targets measured from **Triaged** to **Fixed**; verification adds up to one cycle.

| Severity | Response (to Triaged) | Resolution target | Escalation if breached |
|----------|-----------------------|-------------------|------------------------|
| **S1 Critical** | ≤ 2 business hours | ≤ 1 business day (hotfix) | Immediate to QA Lead + Maintainer; release held |
| **S2 High** | ≤ 1 business day | ≤ 3 business days / current sprint | QA Lead at standup; gate risk flagged |
| **S3 Medium** | ≤ 2 business days | ≤ 1–2 sprints | Reviewed at sprint planning |
| **S4 Low** | ≤ 1 sprint | Backlog / opportunistic | None |

> An S1/S2 still **Open** at a milestone or UAT exit gate is a documented
> blocker in [../signoff/SIGNOFF_TEMPLATE.md](../signoff/SIGNOFF_TEMPLATE.md);
> deferral requires QA Lead + Maintainer approval and a residual-risk note
> ([../risk-analysis/RISK_MATRIX.md](../risk-analysis/RISK_MATRIX.md) §residual).

## Examples

A double-booking defect (`BR-APPT-003`, S2/P1) flows
New → Triaged → Open → In Progress → Fixed → In Test → Verified → Closed; if the
overlap returns at the slot boundary it goes Closed → Reopened → Triaged.

## Future Enhancements

- Enforce transitions via a Jira workflow so illegal moves are blocked.
- Auto-escalate SLA breaches via the notification stack.

## Dependencies

- [SEVERITY_PRIORITY_MATRIX.md](SEVERITY_PRIORITY_MATRIX.md),
  [BUG_REPORT_TEMPLATE.md](BUG_REPORT_TEMPLATE.md),
  [../bug-reports/TRIAGE_GUIDE.md](../bug-reports/TRIAGE_GUIDE.md).

## References

- ISTQB defect-management lifecycle; ITIL incident states (adapted).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
