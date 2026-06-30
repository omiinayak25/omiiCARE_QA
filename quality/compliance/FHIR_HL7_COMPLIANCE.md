# FHIR R4 / HL7 Conformance Checklist — omiiCARE_QA (Milestone 7)

> **EDUCATIONAL — NOT CERTIFIED.** Maps the implemented FHIR R4 Patient facade to
> FHIR R4 / HL7 conformance expectations for learning only. No HL7/FHIR
> certification or conformance claim is made. Use synthetic data only.

## 1. Implemented FHIR surface

- **Resource:** `Patient` (FHIR R4), **read-only facade**.
- **Endpoint:** `GET /api/v1/fhir/Patient/{id}`.
- **Media type:** `application/fhir+json` (produced).
- **Mapping:** internal model → FHIR via `FhirPatientMapper.toFhir` (persistence
  stays decoupled from the FHIR surface).
- **Security:** tenant-scoped (`findByIdAndTenantId`), gated by
  `@PreAuthorize("hasAuthority('patient:read')")`.
- **Not found:** `ResourceNotFoundException` → `404 OMII-404`.

## 2. FHIR R4 conformance checklist

Status: **Modeled** · **Partial** · **Gap** · **N/A (educational)**.

| # | FHIR R4 expectation | omiiCARE_QA mapping | Status | Verify |
|---|---------------------|---------------------|--------|--------|
| F1 | RESTful `read` interaction `GET [base]/Patient/[id]` | `GET /api/v1/fhir/Patient/{id}` | Modeled | Call endpoint, assert `200` |
| F2 | `application/fhir+json` media type | `produces = "application/fhir+json"` | Modeled | Inspect `Content-Type` |
| F3 | Resource `resourceType: "Patient"` in body | Produced by `FhirPatientMapper` | Modeled | Assert JSON `resourceType` |
| F4 | Logical `id` element populated | Mapper sets `id` from internal id | Modeled | Assert `Patient.id` |
| F5 | `name` (HumanName) structure | Mapper emits `name[].family` / `given[]` | Partial | Assert HumanName shape |
| F6 | `gender` from FHIR value set (`administrative-gender`) | Mapped from internal gender | Partial | Verify code system value set |
| F7 | `birthDate` as `date` (YYYY-MM-DD) | Mapped from DOB | Partial | Verify date format |
| F8 | `identifier` with `system` URI + `value` | Modeled if internal MRN present | Partial | Verify identifier system URI |
| F9 | OperationOutcome on errors | Currently RFC7807 `ProblemDetail` (OMII-4xx), not FHIR `OperationOutcome` | Gap | Documented divergence |
| F10 | `search` / `create` / `update` / `vread` / history | Only `read` implemented | N/A (educational) | Scope = read facade |
| F11 | `_format` parameter negotiation | Fixed `application/fhir+json` | Partial | — |
| F12 | CapabilityStatement (`/metadata`) | Not implemented | Gap | Tracked |

## 3. HL7 conformance notes

| # | HL7 expectation | omiiCARE_QA mapping | Status |
|---|-----------------|---------------------|--------|
| H1 | Use HL7 FHIR R4 resource model (not custom) | FHIR Patient facade derived from R4 | Modeled |
| H2 | Standard code systems (administrative-gender, identifier systems) | Referenced via mapper; verify URIs | Partial |
| H3 | HL7 v2 messaging | Not in scope | N/A (educational) |
| H4 | Terminology binding strength compliance | Educational subset | Partial |

## 4. Code-system / value-set checks (validate during QA)

- `gender` → `http://hl7.org/fhir/administrative-gender`
  (`male` | `female` | `other` | `unknown`).
- `identifier.system` → a stable, documented URI (e.g. an MRN namespace).
- `birthDate` → FHIR `date` primitive format `YYYY-MM-DD`.

## 5. Tracked gaps / divergences

- **F9** — error bodies use RFC7807 `ProblemDetail`, not FHIR `OperationOutcome`.
  This is an intentional, documented divergence for the unified API error
  contract (OMII-4xx). A FHIR-pure deployment would emit `OperationOutcome` on
  the `fhir+json` routes.
- **F12** — no `CapabilityStatement` (`/metadata`); add for fuller R4 modeling.
- **F10** — only `read` is implemented (educational scope).

> Restated: this is an educational FHIR R4 modeling exercise, **not** an HL7/FHIR
> certification or conformance attestation.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Compliance Lead | Initial (Milestone 7) |
