# Root Cause Categories & 5-Whys

> **The RCA taxonomy for omiiCARE_QA defects.** Every closed defect records one
> primary root-cause category. Aggregating categories over cycles shows where the
> process leaks (e.g. too many *test-gap* defects ⇒ strengthen coverage; too many
> *requirements* defects ⇒ tighten refinement). A 5-Whys template drives each
> defect from symptom to systemic cause.

## Purpose

Standardize *why* defects happen so trends are measurable and preventable, and so
the [metrics](../metrics) program can chart RCA distribution per release.

## Scope

- **In scope:** the eight RCA categories, identifying signals, prevention levers,
  and a reusable 5-Whys template.
- **Out of scope:** severity/priority (see [SEVERITY_PRIORITY_MATRIX.md](SEVERITY_PRIORITY_MATRIX.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Assignee (Engineer) | Assign the primary RCA category at fix time |
| QA Lead | Review category trends each cycle; drive prevention |
| Triage Lead | Validate the category during verification |

---

## 1. Category Taxonomy

| Category | Definition | Typical signal in this system | Prevention lever |
|----------|------------|-------------------------------|------------------|
| **Requirements** | The spec was wrong, missing, or ambiguous. | Business rule under-specified (e.g. boundary inclusivity of an appointment slot). | Sharper acceptance criteria; RTM review. |
| **Design** | Requirement correct, but the architecture/approach is flawed. | Race condition because MRN uniqueness relied on read-then-write, not a DB constraint. | Design review against [../../docs/DESIGN_PATTERNS.md](../../docs/DESIGN_PATTERNS.md). |
| **Coding** | Implementation bug — logic, off-by-one, null handling, mapping. | `>=` vs `>` in overlap check; FHIR gender not lower-cased; wrong exception mapped. | Unit tests; code review; static analysis. |
| **Data** | Bad seed/test/migration data or data-state assumptions. | Synthetic dataset drifted from schema; missing reference range row. | Versioned datasets regenerated on Flyway change. |
| **Configuration** | Misconfigured property, flag, secret, or limit. | Page-size cap unset; token TTL mis-set; CORS/JWT issuer mismatch. | Config-as-code review; env parity checks. |
| **Environment** | Infra/tooling, not the app — container, network, dependency service. | Postgres/Keycloak container unhealthy; WireMock down; port clash. | Health checks as entry criteria ([../release](../release)). |
| **Integration** | Boundary between components/standards is wrong. | FHIR R4 schema/code-system URI mismatch; HL7 segment build; auth↔gateway contract. | Contract tests; schema validation. |
| **Test Gap** | The escape existed because no test covered it. | A boundary/negative case never asserted; missing audit-side-effect assertion. | Add the missing case + RTM trace. |

## 2. Choosing the primary category

- Pick the **earliest** point in the lifecycle where the defect could have been
  prevented (requirements precedes design precedes coding).
- Record exactly **one primary**; note a secondary in the defect if material.
- A *coding* bug that **also** had no test is primary **Coding**, secondary
  **Test Gap** — both are tracked, because the prevention actions differ.

## 3. 5-Whys Template

```markdown
### RCA — OMII-BUG-NNNN

**Problem statement:** <observable symptom, one sentence>

1. Why did <symptom> happen?
   -> <immediate cause>
2. Why did <immediate cause> happen?
   -> <deeper cause>
3. Why did <that> happen?
   -> <deeper cause>
4. Why did <that> happen?
   -> <deeper cause>
5. Why did <that> happen?
   -> <systemic / root cause>

**Primary RCA category:** <requirements | design | coding | data | config | environment | integration | test-gap>
**Secondary (optional):** <category>

**Corrective action (this defect):** <the fix>
**Preventive action (the class):** <test added, guardrail, review change, config check>
**Trace:** business rule <BR-*>, test case <TC-*>, requirement <REQ-*>
```

## 4. Worked 5-Whys example

```markdown
### RCA — OMII-BUG-0001 (overlapping appointment accepted at exact boundary)

Problem statement: A provider was double-booked when a new appointment started
at the exact end time of an existing one.

1. Why? The overlap check treated touching intervals (end == start) as non-overlapping.
2. Why? The predicate used `existing.end > new.start` with strict `>`, excluding equality.
3. Why? The boundary inclusivity rule for BR-APPT-003 was never stated in the AC.
4. Why? Refinement assumed "overlap" was self-evident and skipped the boundary case.
5. Why? No boundary/negative test existed to expose the assumption.

Primary RCA category: requirements
Secondary: test-gap
Corrective action: change predicate to half-open interval semantics [start, end).
Preventive action: add boundary AC to BR-APPT-003; add exact-boundary negative test.
Trace: BR-APPT-003, TC-APPT-OVERLAP-BOUNDARY, REQ-APPT-DOUBLEBOOK
```

## Examples

Each defect in [../bug-reports/SAMPLE_DEFECTS.md](../bug-reports/SAMPLE_DEFECTS.md)
carries a primary RCA category drawn from this taxonomy.

## Future Enhancements

- Chart RCA-category distribution per release in the metrics dashboard.
- Pareto analysis to target the top two leaking categories each quarter.

## Dependencies

- [BUG_REPORT_TEMPLATE.md](BUG_REPORT_TEMPLATE.md),
  [../bug-reports/SAMPLE_DEFECTS.md](../bug-reports/SAMPLE_DEFECTS.md).

## References

- Ishikawa cause-and-effect; Toyota 5-Whys; ISTQB defect-cause analysis.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
