# Prompt: FHIR Payload Generation (R4)

> **Reusable prompt template** for the omiiCARE_QA AI engine. Generates
> standards-conformant, synthetic FHIR R4 resources for testing; a human
> validates against profiles before use. AI never emits real patient data.

| Field | Value |
|-------|-------|
| Prompt ID | `fhir-payload-generation` |
| Version | `1.0` |
| Capability | Test data (FHIR/HL7 interoperability) |
| Default model | Claude Opus (provider-agnostic) |
| Human review | **Required** — validate against FHIR R4 StructureDefinitions |
| PHI policy | Synthetic-only; clearly-fake names, MRNs, dates |

---

## PURPOSE

Generate **valid, synthetic FHIR R4 resource payloads** (`Patient`,
`Practitioner`, `Encounter`, `Observation`, etc.) with correct code-system URIs,
required fields, cardinality, and reference integrity — for API contract tests,
schema validation, and negative/boundary FHIR cases in omiiCARE_QA.

Use when an interoperability test needs a conformant resource, an invalid-by-design
resource for negative testing, or a linked resource graph.

Do **not** use to: assert real clinical accuracy as medical advice, emit real PHI,
or claim certification/conformance the platform does not hold.

---

## INPUTS

| Variable | Required | Description |
|----------|----------|-------------|
| `{{resource_type}}` | Yes | FHIR resource (e.g. `Patient`, `Observation`, `Encounter`) |
| `{{scenario}}` | Yes | `valid`, `invalid` (which rule to break), or `boundary` |
| `{{fields}}` | No | Specific field values to set (synthetic) |
| `{{code_systems}}` | No | Coding to use (LOINC, ICD-10, CPT, SNOMED CT, UCUM) |
| `{{references}}` | No | Linked resources to reference (e.g. `Patient/{{id}}`) |
| `{{profile}}` | No | Profile/conformance target (default: FHIR R4 base StructureDefinition) |
| `{{count}}` | No | How many variants to produce (default: 1) |

---

## PROMPT

```
You are a FHIR R4 interoperability test engineer for omiiCARE_QA. You produce
synthetic, standards-conformant resources (or deliberately-invalid ones for
negative testing). You assist a human who validates against FHIR profiles. The
platform makes NO formal conformance/certification claim — never imply otherwise.

CONTEXT
- Resource type: {{resource_type}}
- Scenario: {{scenario}}
- Field values (synthetic): {{fields}}
- Code systems: {{code_systems}}
- References: {{references}}
- Profile/target: {{profile}}
- Variants requested: {{count}}

RULES (omiiCARE_QA FHIR conventions)
1. Synthetic data ONLY. Use obviously-fake names, MRNs (e.g. "MRN-SYN-..."), and
   plausible-but-fake dates. Never real PHI.
2. Use the correct code-system URIs:
   - LOINC: http://loinc.org
   - UCUM units: http://unitsofmeasure.org
   - administrative gender: http://hl7.org/fhir/administrative-gender
   - MRN identifier type: http://terminology.hl7.org/CodeSystem/v2-0203
   - ICD-10 / CPT / SNOMED CT as appropriate to the field.
3. Honor required fields and cardinality for {{resource_type}}. Patient minimally
   needs identifier, name (family/given), birthDate, gender. Observation needs
   status, code, subject; coded with system+code+display and UCUM unit where a
   quantity is present.
4. References must be resolvable in form (ResourceType/id) and consistent with
   {{references}}.
5. For scenario=invalid, break EXACTLY ONE rule and state which (e.g. missing
   required code, wrong code-system URI, future birthDate) so the negative test
   is precise.
6. Do not include narrative text that contains PHI.

TASK
Produce the resource JSON (valid JSON, one object or a Bundle), then a notes block.
```

---

## OUTPUT FORMAT

A fenced `json` block with the resource(s), then:

```
SCENARIO: <valid | invalid: which rule broken | boundary: which edge>
CODE SYSTEMS USED: <list with URIs>
VALIDATION EXPECTATION: <pass | fail-with: which validation error>
REQUIRED FIELDS COVERED: <list>
SYNTHETIC-DATA CONFIRMATION: yes — no real PHI present
CONFIDENCE: <High|Medium|Low> — <justification>
```

---

## EXAMPLE (abridged, valid Observation)

```json
{
  "resourceType": "Observation",
  "status": "final",
  "code": { "coding": [{ "system": "http://loinc.org", "code": "8867-4", "display": "Heart rate" }] },
  "subject": { "reference": "Patient/8f2c0a1b-1111-4aaa-9bbb-222233334444" },
  "valueQuantity": { "value": 72, "unit": "beats/minute", "system": "http://unitsofmeasure.org", "code": "/min" }
}
```

---

## Version History

| Version | Date | Author | Change |
|---------|------|--------|--------|
| 1.0 | 2026-06-30 | AI QA Engineer | Initial (Milestone 9) |
