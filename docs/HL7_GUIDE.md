# HL7 v2 Guide

> **How omiiCARE_QA models legacy HL7 v2 messaging.** This guide defines the
> message types and segments the platform recognises, worked example messages,
> the mapping from HL7 to internal entities and to FHIR, where HL7 interfaces are
> stubbed in v1.0, and the validation approach. Mapping is introduced in
> Milestone 3 and validated in Milestone 7. omiiCARE_QA makes **no formal
> certification claims**. Facts defer to [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

Document HL7 v2 concepts so the platform can model legacy hospital messaging
(admissions, orders, results, scheduling, financials) as a peer to its FHIR R4
surface, and so QA can validate message handling.

## Scope

- **In scope:** HL7 v2 message types used, core segment overview, example
  `ADT^A01` and `ORU^R01` messages with field explanations, HL7↔entity↔FHIR
  mapping, the v1.0 stubbing approach, and validation.
- **Out of scope:** real interface engine integration, all HL7 v2.x trigger events
  (only the modelled subset), and certification.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Healthcare Architect | Keep message/segment mappings correct |
| Integration Engineer (M3) | Implement message parsing/building behind adapters |
| QA Architect (M7) | Validate message structure and round-trip mapping |

---

## 1. Standard & Encoding

- **HL7 v2.x** pipe-and-hat encoded messages (`|` field, `^` component, `~`
  repetition, `&` sub-component, `\` escape).
- Every message begins with an `MSH` (Message Header) segment; segments are
  newline-separated; fields are 1-based after the segment name.
- HL7 v2 is modelled alongside FHIR R4 (see [FHIR_GUIDE.md](FHIR_GUIDE.md)); the
  two are cross-mapped (§5).

## 2. Message Types Used

| Type | Trigger | Purpose | Internal effect |
|------|---------|---------|-----------------|
| `ADT` | `A01` admit, `A03` discharge, `A04` register, `A08` update | Patient administration | Patient/encounter create/update |
| `ORM` | `O01` | General order message | Lab/radiology order placed |
| `ORU` | `R01` | Observation result | Lab/radiology results delivered |
| `SIU` | `S12` new, `S13` reschedule, `S15` cancel | Scheduling | Appointment lifecycle |
| `DFT` | `P03` | Detail financial transaction | Billing charge posted |

## 3. Segment Overview

| Segment | Name | Carries |
|---------|------|---------|
| `MSH` | Message Header | Sending/receiving app & facility, type, control id, version |
| `EVN` | Event Type | Trigger event, recorded date/time |
| `PID` | Patient Identification | MRN, name, DOB, gender, address |
| `PV1` | Patient Visit | Class, assigned location, attending provider, visit number |
| `NK1` | Next of Kin | Contacts/guarantor relations |
| `IN1` | Insurance | Payer, plan, member/group |
| `ORC` | Common Order | Order control, placer/filler order numbers |
| `OBR` | Observation Request | Ordered test/study, universal service id (LOINC/CPT) |
| `OBX` | Observation/Result | Individual result value, units, reference range, abnormal flag |
| `SCH` | Schedule Activity | Appointment id, timing, duration (SIU) |
| `FT1` | Financial Transaction | Charge code, amount (DFT) |

## 4. Example Messages

### `ADT^A01` (Admit) with field explanation

```
MSH|^~\&|OMIICARE|DEMO_BRANCH|RECEIVER|RECEIVER_FAC|20260630091500||ADT^A01|MSG00001|P|2.5
EVN|A01|20260630091500
PID|1||MRN-000123^^^OMIICARE^MR||Synthetic^Avery||19900412|F|||123 Test St^^Townsville^ST^00000
PV1|1|I|WARD^101^A|||||1a4b^Clinician^Dana^^^Dr|||MED||||A|||V0001
```

| Field | Value | Meaning |
|-------|-------|---------|
| `MSH-9` | `ADT^A01` | Message type + trigger (admit) |
| `MSH-10` | `MSG00001` | Message control id (idempotency/correlation) |
| `MSH-12` | `2.5` | HL7 version |
| `PID-3` | `MRN-000123…^MR` | Patient MRN (identifier type MR) |
| `PID-5` | `Synthetic^Avery` | Family^Given name |
| `PID-7` | `19900412` | Date of birth |
| `PID-8` | `F` | Administrative sex |
| `PV1-2` | `I` | Patient class (Inpatient) |
| `PV1-7` | `1a4b^Clinician^Dana…` | Attending provider |
| `PV1-19` | `V0001` | Visit/encounter number |

### `ORU^R01` (Observation Result) with field explanation

```
MSH|^~\&|LAB|DEMO_BRANCH|OMIICARE|DEMO_BRANCH|20260630101500||ORU^R01|MSG00045|P|2.5
PID|1||MRN-000123^^^OMIICARE^MR||Synthetic^Avery||19900412|F
OBR|1|ORD-789|FIL-789|4548-4^Hemoglobin A1c^LN|||20260629083000
OBX|1|NM|4548-4^Hemoglobin A1c^LN||5.6|%|4.0-5.6|N|||F
```

| Field | Value | Meaning |
|-------|-------|---------|
| `MSH-9` | `ORU^R01` | Unsolicited observation result |
| `OBR-2/3` | `ORD-789` / `FIL-789` | Placer / filler order numbers (link to `lab_order`) |
| `OBR-4` | `4548-4^…^LN` | Universal service id (LOINC) |
| `OBX-2` | `NM` | Value type (numeric) |
| `OBX-3` | `4548-4^…^LN` | Observation identifier (LOINC) |
| `OBX-5` | `5.6` | Result value |
| `OBX-6` | `%` | Units |
| `OBX-7` | `4.0-5.6` | Reference range |
| `OBX-8` | `N` | Abnormal flag (Normal) |
| `OBX-11` | `F` | Result status (Final) |

## 5. HL7 ↔ Internal Entity ↔ FHIR Mapping

| HL7 segment/field | Internal entity | FHIR resource |
|-------------------|-----------------|---------------|
| `PID` | `patient` | `Patient` |
| `PV1` | `encounter` | `Encounter` |
| `PV1-7/-8/-9` (providers) | `provider` | `Practitioner` / `PractitionerRole` |
| `IN1` | `coverage` | `Coverage` |
| `ORC`/`OBR` (ORM) | `lab_order` / `rad_order` | `ServiceRequest` |
| `OBX` (ORU) | `lab_result` / `observation` | `Observation` |
| `OBR`+`OBX` group (ORU) | `lab_result` set / `rad_report` | `DiagnosticReport` |
| `SCH` (SIU) | `appointment` | `Appointment` |
| `FT1` (DFT) | `invoice_line` | `ChargeItem` |

LOINC, ICD-10, CPT, and SNOMED codings carried in HL7 fields map to the same
code-system URIs used in FHIR (see [FHIR_GUIDE.md](FHIR_GUIDE.md) §3) and the
standards tables (see [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md) §7).

## 6. HL7 Interfaces in v1.0 (Stubbed)

- Real HL7 interface engines and external senders/receivers are **out of scope**
  for v1.0 (see [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §3).
- HL7 endpoints are **stubbed/mocked with WireMock** behind the common adapter
  interface (see [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)); business and test
  logic call the adapter, never a live interface.
- Sample HL7 messages used for stubbing and tests live with the automation/
  contract assets and are synthetic, PHI-safe (BR-CONS-005).

## 7. Validation Approach (Milestone 7)

| Layer | Validation |
|-------|------------|
| Structural | Parse with an HL7 v2 parser (e.g. HAPI HL7v2); confirm required segments/fields and message-type conformance |
| Terminology | Confirm coded fields (LOINC/ICD-10/CPT) resolve against the standards tables |
| Round-trip | Map HL7 → internal → FHIR and back; assert semantic equivalence in contract tests |
| Negative | Malformed/short messages must be rejected with a clear error, not silently accepted |

## 8. Scope & Disclaimer

omiiCARE_QA models HL7 v2 concepts for **educational and portfolio purposes
only**, with synthetic PHI-safe data, no live interfaces in v1.0, and **no formal
certification claims**. Only the message subset in §2 is modelled. See
[MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §9 and
[SECURITY.md](../SECURITY.md).

## Examples

- An inbound `ADT^A04` (register) stub creates/updates a synthetic `patient` and
  is mappable to a FHIR `Patient`.
- An `ORU^R01` stub delivers a LOINC-coded result that lands as a `lab_result`
  and is exposable as a FHIR `Observation`.

## Future Enhancements

- Additional triggers (`ADT^A02` transfer, `A11` cancel admit) and message types
  (`MDM` documents, `VXU` immunizations).
- MLLP transport simulation for end-to-end interface testing (post-1.0).

## Dependencies

- Entity/FHIR cross-mapping: [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md),
  [FHIR_GUIDE.md](FHIR_GUIDE.md).
- Stubbing/adapter approach: [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md).
- Privacy posture: [BUSINESS_RULES.md](BUSINESS_RULES.md) §Consent & Privacy.

## References

- HL7 v2.x messaging standard (v2.5).
- HAPI HL7v2 parser.
- LOINC, ICD-10, CPT, SNOMED CT.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Healthcare Architect | Initial HL7 v2 concepts & mapping guide (Milestone 1) |
