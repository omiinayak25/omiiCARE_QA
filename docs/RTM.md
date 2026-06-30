# Requirements Traceability Matrix (RTM)

> **Traceability framework + seeded example.** This document defines how
> omiiCARE_QA links every requirement to the tests that verify it, and seeds a
> representative populated matrix. The *living* RTM is maintained under
> `manual-testing/rtm/` from Milestone 6; this is the framework and worked
> example that govern it. It realizes the traceability promised in the
> [Test Strategy](TEST_STRATEGY.md) and the [Master Test Plan](MASTER_TEST_PLAN.md).

## Purpose

- Define the requirement-ID scheme and the matrix columns.
- Show, by example, how requirements map forward to manual, automated, API, DB,
  security, performance, accessibility, and visual tests.
- Make coverage gaps and untested critical requirements visible at a glance.

This is **documentation only** in Milestone 1; the populated, continuously
maintained RTM arrives in Milestone 6.

## Scope

- **In scope:** the ID scheme; column definitions; status semantics; a
  representative seeded matrix (~18 rows across modules).
- **Out of scope:** the full requirement catalogue (Milestone 6) and the test
  cases themselves (Milestones 5–6). Example BR-*/TC-* IDs are illustrative and
  will be reconciled against `BUSINESS_RULES.md` when it is authored.

## Responsibilities

| Role | RTM responsibility |
|------|--------------------|
| QA Lead | Owns the living RTM; reviews coverage each cycle |
| QA Architect | Owns the ID scheme and column model |
| SDET II/III | Keep automated-test references current |
| Senior QA Engineer | Keep manual-test references current |

---

## 1. Requirement ID Scheme

| Prefix | Requirement type | Example |
|--------|------------------|---------|
| `BR-*` | Business rule | `BR-PAT-001` |
| `FR-*` | Functional requirement | `FR-APPT-004` |
| `NFR-*` | Non-functional requirement | `NFR-PERF-002` |
| `SEC-*` | Security requirement | `SEC-AUTH-001` |
| `A11Y-*` | Accessibility requirement | `A11Y-UI-003` |
| `PERF-*` | Performance requirement | `PERF-API-001` |
| `FHIR-*` | FHIR conformance requirement | `FHIR-PAT-001` |
| `HL7-*` | HL7 v2 conformance requirement | `HL7-ADT-001` |

Module segments (e.g. `PAT`, `APPT`, `BILL`, `LAB`, `RAD`, `PHARM`, `INS`,
`AUTH`, `ADMIN`) keep IDs human-readable and groupable.

## 2. Column Definitions

| Column | Meaning |
|--------|---------|
| Req ID | Unique requirement identifier (§1) |
| Description | What the requirement asserts |
| Source | Originating document/standard |
| Manual TC | Manual test-case ID(s) |
| Automated TC | Automated test-case ID(s) |
| API/DB/Sec/Perf/A11y/Visual | ✓ where that specialized test type covers the req |
| Regression? | In the regression suite (Y/N) |
| Smoke? | In the smoke suite (Y/N) |
| Status | Traced / Covered / Partial / Gap |

**Status semantics:** *Traced* = requirement has at least one mapped test;
*Covered* = all planned tests exist and pass; *Partial* = some test types missing;
*Gap* = no test mapped (must be resolved before the relevant exit criteria).

## 3. Seeded Traceability Matrix (Representative)

| Req ID | Description | Source | Manual TC | Automated TC | Spec. tests | Regr? | Smoke? | Status |
|--------|-------------|--------|-----------|--------------|-------------|-------|--------|--------|
| SEC-AUTH-001 | Valid credentials authenticate; JWT issued | SECURITY.md | TC-AUTH-001 | AT-AUTH-001 | API, Sec | Y | Y | Covered |
| SEC-AUTH-002 | Account locks after N failed attempts | SECURITY.md | TC-AUTH-005 | AT-AUTH-007 | API, Sec | Y | N | Covered |
| SEC-AUTH-003 | RBAC denies cross-role access | ARCHITECTURE.md | TC-AUTH-011 | AT-AUTH-012 | API, Sec | Y | N | Covered |
| BR-PAT-001 | Patient MRN is unique per tenant | BUSINESS_RULES.md | TC-PAT-002 | AT-PAT-002 | API, DB | Y | Y | Covered |
| BR-PAT-007 | Patient context never crosses patients | BUSINESS_RULES.md | TC-PAT-014 | AT-PAT-019 | API, DB | Y | Y | Covered |
| FR-APPT-004 | Double-booking a slot is rejected | MASTER_TEST_PLAN.md | TC-APPT-009 | AT-APPT-010 | API | Y | N | Covered |
| FR-APPT-006 | Cancelled appointment frees the slot | MASTER_TEST_PLAN.md | TC-APPT-013 | AT-APPT-015 | API | Y | N | Partial |
| BR-BILL-002 | Invoice total equals sum of line items | BUSINESS_RULES.md | TC-BILL-003 | AT-BILL-004 | API, DB | Y | Y | Covered |
| BR-BILL-005 | Adjustment cannot exceed invoice balance | BUSINESS_RULES.md | TC-BILL-011 | AT-BILL-012 | API | Y | N | Partial |
| FR-LAB-002 | Lab result carries valid LOINC code | PROJECT_METADATA.md | TC-LAB-004 | AT-LAB-005 | API, Contract | Y | N | Covered |
| FR-LAB-008 | Abnormal flag set at reference boundary | BUSINESS_RULES.md | TC-LAB-017 | AT-LAB-021 | API, DB | Y | N | Covered |
| FR-RAD-003 | Report links to correct imaging study | MASTER_TEST_PLAN.md | TC-RAD-006 | AT-RAD-008 | API | Y | N | Traced |
| BR-PHARM-001 | Drug-interaction check blocks unsafe dispense | BUSINESS_RULES.md | TC-PHARM-002 | AT-PHARM-003 | API, Sec | Y | Y | Covered |
| BR-INS-004 | Claim adjudication outcome is deterministic | BUSINESS_RULES.md | TC-INS-007 | AT-INS-009 | API, DB | Y | N | Partial |
| FHIR-PAT-001 | Patient resource validates against R4 schema | PROJECT_METADATA.md | TC-FHIR-001 | AT-FHIR-002 | Contract, API | Y | Y | Covered |
| HL7-ADT-001 | ADT^A01 message parses with intact segments | PROJECT_METADATA.md | TC-HL7-001 | AT-HL7-002 | Contract | Y | N | Traced |
| A11Y-UI-003 | Patient portal meets WCAG 2.1 AA | UI_UX_SPECIFICATION.md | TC-A11Y-003 | AT-A11Y-004 | A11y, Visual | N | N | Partial |
| PERF-API-001 | Patient search responds within budget (owned infra) | NFR / perf budget | TC-PERF-001 | AT-PERF-002 | Perf | N | N | Traced |

The matrix is read forward (requirement → tests) and backward (a failing test →
the requirement at risk). Any row reaching the relevant exit criteria as *Gap* or
*Partial* on a critical requirement blocks release.

## 4. Maintaining the Living RTM

- The authoritative RTM lives in `manual-testing/rtm/` from Milestone 6 and is
  updated whenever a requirement, test case, or status changes.
- Every new requirement is added with at least *Traced* status before build
  (shift-left, per [TEST_STRATEGY.md](TEST_STRATEGY.md) §13).
- Coverage is reviewed each cycle as part of exit criteria
  ([MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md) §6).

## Examples

- *Forward trace:* `BR-PHARM-001` (drug-interaction block) maps to a manual case,
  an automated case, and API + security tests — and is in both smoke and
  regression, reflecting its high risk exposure.
- *Gap detection:* If `FR-RAD-003` stayed *Traced* with no automated TC at a
  release gate, the RTM surfaces it as missing automated coverage to resolve.

## Future Enhancements

- Generate the RTM automatically from test annotations and requirement tags (M9).
- Bidirectional links to a defect tracker so each requirement shows open defects.
- Coverage-to-risk overlay joining this matrix with the [Risk Analysis](RISK_ANALYSIS.md).

## Dependencies

- Realizes traceability for [TEST_STRATEGY.md](TEST_STRATEGY.md) and
  [MASTER_TEST_PLAN.md](MASTER_TEST_PLAN.md).
- Consumes requirement IDs from `BUSINESS_RULES.md` and standards in
  [PROJECT_METADATA.md](PROJECT_METADATA.md) §6 (both authored in later milestones).
- Living RTM delivered in Milestone 6 (`manual-testing/rtm/`).

## References

- IEEE 829; ISTQB traceability guidance.
- [ARCHITECTURE.md](../ARCHITECTURE.md), [SECURITY.md](../SECURITY.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial RTM framework and seeded example matrix (Milestone 1) |
