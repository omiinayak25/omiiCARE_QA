# AI Knowledge Base

> **Purpose.** Define the structure of omiiCARE_QA's searchable QA knowledge
> base: the project-specific best practices, common failures, lessons learned,
> patterns, troubleshooting, ADRs, and prompt-library index that ground AI
> outputs and give human reviewers a single reference. The KB makes AI assistance
> grounded and explainable rather than free-floating.

## Scope

- The KB's section structure and what each section holds.
- How entries are formatted, tagged, and searched.
- How the KB grounds AI capabilities and how it is maintained.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| AI QA Engineer | Curate KB structure; keep entries grounded and current |
| SDET / QA Engineer | Contribute lessons learned and troubleshooting entries |
| Human reviewer | Validate entries before they ground AI outputs |

---

## 1. KB Structure

| Section | Holds | Primary use |
|---------|-------|-------------|
| Best Practices | Project-specific QA conventions (test design, BDD style, API assertions, FHIR coding) | Grounds test/BDD/API generation |
| Common Failures | Recurring failure signatures and their typical causes | Grounds failure/RCA triage |
| Lessons Learned | Post-incident and retrospective takeaways | Risk and regression analysis |
| Patterns | Reusable test/automation patterns (page objects, data builders, auth helpers) | Code generation & review |
| Troubleshooting | Step-by-step runbooks for known problems | Engineer self-service + AI next-steps |
| ADRs (index) | Decisions affecting QA/AI (links to `docs/architecture/adr/`) | Rationale for reviewers |
| Prompt Library Index | Pointer to `ai/prompts/README.md` | Capability discovery |

---

## 2. Entry Format

Each KB entry is a small, searchable record:

```
ID: KB-<SECTION>-<NNN>
TITLE: <short, searchable>
TAGS: <feature, level, BR-*, provider-agnostic>
CONTEXT: <when this applies>
GUIDANCE / SIGNATURE / STEPS: <the actionable content>
EVIDENCE / SOURCE: <doc, BR-*, incident, or test reference>
LAST REVIEWED: <date> by <human>
```

- IDs are stable and never reused.
- Tags enable retrieval by feature, test level, business rule, or symptom.

---

## 3. Best Practices (seed topics)

- Test cases are independent, self-seeding, and traced to a `BR-*`/RTM row.
- BDD scenarios are declarative and reuse existing step vocabulary (see
  `automation/.../features/`); new steps stay generic.
- API tests assert status code **and** RFC 7807 body **and** the business invariant;
  PHI-read endpoints assert audit logging (`BR-AUDIT-002`).
- FHIR payloads use correct code-system URIs (LOINC `http://loinc.org`, UCUM
  `http://unitsofmeasure.org`, admin gender, MRN identifier type) and honor
  required fields/cardinality.
- Synthetic data only; tenant scope on every tenant-scoped query.

## 4. Common Failures (seed signatures)

| Signature | Typical cause | First check |
|-----------|---------------|-------------|
| `expected 201 but 409` on booking | Double-booking (`BR-APPT-003`) or overlapping seed slot | Provider/window seed overlap |
| `401/403` on a valid action | Auth/RBAC: wrong role or expired token | `authAs(role)` helper + role policy |
| Stale-element / UI timeout | Implicit waits / unstable selector | Replace sleeps with explicit waits; stable selectors |
| FHIR validation error | Wrong code-system URI or missing required field | Code system + cardinality vs R4 |
| Audit-assertion miss | PHI read not logged or query wrong tenant | `audit_log` tenant scope |

## 5. Lessons Learned

- Captured from retrospectives and incidents; each links to the change/test that
  prevents recurrence. Feeds `risk-analysis` and `regression-impact-analysis`.

## 6. Patterns

- Data builders for synthetic patients/appointments; auth helpers; page objects;
  RFC 7807 assertion helpers; FHIR resource builders. Referenced by code generation
  and `code-review-checklist`.

## 7. Troubleshooting Runbooks

- One runbook per recurring problem (flaky suite, container/port issues, seed drift,
  provider/key misconfig). Steps are ordered cheapest-first and mirror the
  `failure-analysis` next-steps format.

## 8. ADR Index

- Links to QA/AI-relevant ADRs under `docs/architecture/adr/` (e.g. provider
  abstraction, prompt-versioning policy). New AI decisions are recorded as ADRs and
  indexed here.

## 9. Prompt Library Index

- The authoritative capability catalogue lives in
  [`ai/prompts/README.md`](../prompts/README.md). The KB links to it rather than
  duplicating it.

---

## 10. How the KB Grounds AI

- The engine may retrieve relevant KB entries (by tag/feature/`BR-*`) and include
  them as grounding context in a prompt's inputs.
- Grounding is **transparent**: retrieved entry IDs are recorded with the
  invocation's provenance.
- KB entries are themselves reviewed by humans; AI does not silently write
  unreviewed entries into the KB.

---

## 11. Maintenance

- Entries carry a `LAST REVIEWED` date and reviewer; stale entries are flagged.
- Adding/changing an entry is a reviewed change, like any documentation.
- The KB defers to canonical facts in `docs/PROJECT_METADATA.md` and business rules
  in `docs/BUSINESS_RULES.md`; it never contradicts them.

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
