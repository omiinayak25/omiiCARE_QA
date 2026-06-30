# Bug Report Template

> **Standard defect record for omiiCARE_QA.** Copy this template for every defect
> raised against the system under test (Java 21 / Spring Boot 3.x backend, React
> frontend, FHIR R4 / HL7 interfaces). Every field is mandatory unless marked
> *(optional)*. A defect missing reproduction steps, environment, or
> expected/actual is returned to the reporter as **Rejected — Insufficient Info**.

## Purpose

Capture a defect with enough fidelity that any engineer can reproduce, triage,
fix, and verify it without contacting the reporter. The template binds every
defect to a build, an environment, a correlation ID, and (where relevant) the
violated business rule (`BR-*`) and RFC 7807 error `code`.

## Scope

- **In scope:** functional, security, data-integrity, FHIR/HL7-conformance,
  accessibility, and performance defects in the omiiCARE_QA SUT.
- **Out of scope:** enhancement requests (raise as a story), and known
  out-of-v1.0 items per [MASTER_PROJECT_SPECIFICATION.md](../../MASTER_PROJECT_SPECIFICATION.md) §3.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Reporter (QA / SDET) | Fill every mandatory field; attach evidence + correlation ID |
| Triage Lead | Confirm severity/priority; route per [DEFECT_LIFECYCLE.md](DEFECT_LIFECYCLE.md) |
| Assignee (Engineer) | Reproduce, root-cause, fix; record fix + RCA category |
| Verifier (QA) | Re-test on the fixed build; close or reopen |

---

## Field Reference

| Field | Required | Description |
|-------|----------|-------------|
| Defect ID | Yes | `OMII-BUG-NNNN` (stable, never reused) |
| Summary | Yes | One line: *component — observed problem — condition* |
| Module / Component | Yes | auth · patient · appointment · encounter · billing · FHIR · frontend portal |
| Environment | Yes | `dev` · `local`/`docker` · `qa` · `stage` (never `prod` data) |
| Build / Version | Yes | App version + image tag/commit SHA (e.g. `0.5.0` / `develop@a1b2c3d`) |
| Severity | Yes | S1–S4 per [SEVERITY_PRIORITY_MATRIX.md](SEVERITY_PRIORITY_MATRIX.md) |
| Priority | Yes | P1–P4 per [SEVERITY_PRIORITY_MATRIX.md](SEVERITY_PRIORITY_MATRIX.md) |
| Status | Yes | Per [DEFECT_LIFECYCLE.md](DEFECT_LIFECYCLE.md) (starts `New`) |
| Business Rule | If applicable | Violated `BR-*` (e.g. `BR-APPT-003`) |
| Error code | If applicable | RFC 7807 `code` returned (e.g. `APPT_DOUBLE_BOOKING`) |
| Preconditions | Yes | Roles, seed data, tenant, feature flags required first |
| Steps to Reproduce | Yes | Numbered, deterministic, copy-pasteable requests/clicks |
| Expected Result | Yes | The correct behaviour per spec/business rule |
| Actual Result | Yes | What actually happened (status code, payload, UI state) |
| Reproducibility | Yes | Always · Intermittent (frequency) · Once |
| Evidence | Yes | Screenshots, HAR, request/response bodies, video |
| Logs / Correlation ID | Yes | `X-Request-Id` / `traceparent`, relevant log excerpt |
| Suspected Root Cause | Yes | Best hypothesis + RCA category ([ROOT_CAUSE_CATEGORIES.md](ROOT_CAUSE_CATEGORIES.md)) |
| Regression? | Yes | Yes (last good build) / No / Unknown |
| Test Case Ref | If applicable | Failing test case ID / RTM requirement |
| Reporter | Yes | Name / role |
| Assignee | On triage | Engineer responsible |
| Found in cycle | Yes | Test cycle / sprint |

---

## Template (copy below this line)

```markdown
## OMII-BUG-NNNN — <one-line summary>

| Field | Value |
|-------|-------|
| Defect ID | OMII-BUG-NNNN |
| Summary | <component — observed problem — condition> |
| Module / Component | <auth | patient | appointment | encounter | billing | FHIR | frontend> |
| Environment | <dev | local/docker | qa | stage> |
| Build / Version | <0.5.0 / develop@SHA> |
| Severity | <S1 | S2 | S3 | S4> |
| Priority | <P1 | P2 | P3 | P4> |
| Status | New |
| Business Rule | <BR-XXX-NNN or n/a> |
| Error code | <RFC7807 code or n/a> |
| Reproducibility | <Always | Intermittent (x/y) | Once> |
| Regression? | <Yes (last good: build) | No | Unknown> |
| Test Case Ref | <TC-id / requirement or n/a> |
| Reporter | <name / role> |
| Assignee | <unassigned> |
| Found in cycle | <cycle / sprint> |

### Preconditions
- Logged in as `<role>` (e.g. `demo.admin / Admin@12345`) in tenant `<tenant>`.
- Seed data: <fixtures required>.

### Steps to Reproduce
1. <step>
2. <step>
3. <step>

### Expected Result
<correct behaviour per spec / business rule>

### Actual Result
<observed behaviour: HTTP status, body, UI state>

### Evidence
- <screenshot / HAR / request+response body / video link>

### Logs / Correlation ID
- `X-Request-Id`: <uuid>
- Log excerpt:
  ```
  <relevant log lines>
  ```

### Suspected Root Cause
<hypothesis> — RCA category: <requirements | design | coding | data | config | environment | integration | test-gap>
```

---

## Examples

A fully worked set of defects using this template lives in
[../bug-reports/SAMPLE_DEFECTS.md](../bug-reports/SAMPLE_DEFECTS.md).

## Future Enhancements

- Generate this template from a Jira issue type so fields stay in lockstep.
- Auto-attach the correlation-ID log slice from the observability stack (M7).

## Dependencies

- [SEVERITY_PRIORITY_MATRIX.md](SEVERITY_PRIORITY_MATRIX.md),
  [DEFECT_LIFECYCLE.md](DEFECT_LIFECYCLE.md),
  [ROOT_CAUSE_CATEGORIES.md](ROOT_CAUSE_CATEGORIES.md).
- [../../docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md),
  [../../docs/API_BLUEPRINT.md](../../docs/API_BLUEPRINT.md) (error codes).

## References

- RFC 7807 Problem Details; ISTQB defect-management terminology.
- [../../MASTER_PROJECT_SPECIFICATION.md](../../MASTER_PROJECT_SPECIFICATION.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
