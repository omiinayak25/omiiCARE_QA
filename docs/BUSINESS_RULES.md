# Business Rules

> **Authoritative healthcare business rules.** This document is the single source
> of truth for the domain logic of omiiCARE_QA. It is read at the start of every
> working session that touches behaviour. Each rule is numbered, justified, and
> bound to the RBAC roles it affects so that Milestone 2/3 implementation and the
> Milestone 5/6/7 test suites can trace directly to it. Facts (versions, roles,
> standards) defer to [PROJECT_METADATA.md](PROJECT_METADATA.md).

## Purpose

Capture the enforceable, testable healthcare business rules that govern patient
identity, scheduling, clinical encounters, prescribing, diagnostics, billing,
insurance, consent, audit, access control, tenancy isolation, and notifications.
Rules are written so that every one becomes a positive and a negative test case.

## Scope

- **In scope:** domain invariants and policy rules for the core healthcare
  workflows modelled by omiiCARE_QA, expressed independently of framework or
  storage.
- **Out of scope:** UI copy, persistence detail (see
  [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md)), API shape (see
  [API_BLUEPRINT.md](API_BLUEPRINT.md)), and any real-world certification claim.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Healthcare Architect | Keep rules clinically credible and standards-aligned |
| Backend Engineer (M3) | Implement each rule as a domain invariant or use-case guard |
| QA Architect (M5–M7) | Derive positive/negative test cases; trace via RTM |
| Auditor | Confirm audited events match §Audit rules |

---

## Rule Convention

- **ID:** `BR-<DOMAIN>-<NNN>` (stable; never reused once retired).
- **Statement:** the enforceable rule (MUST / MUST NOT / SHOULD).
- **Rationale:** clinical, legal-modelling, or data-integrity reason.
- **Affected roles:** RBAC roles that enact or are constrained by the rule.

Domains: `IDENT`, `APPT`, `ENC`, `RX`, `LAB`, `RAD`, `BILL`, `INS`, `CONS`,
`AUDIT`, `RBAC`, `TENANT`, `NOTIF`.

---

## 1. Patient Registration & Identity (`IDENT`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-IDENT-001 | A patient MUST have a unique tenant-scoped Medical Record Number (MRN); MRNs MUST NOT collide within a tenant but MAY repeat across tenants. | Identity within an organisation must be unambiguous; tenancy isolates namespaces. | Receptionist, Hospital Admin |
| BR-IDENT-002 | Registration MUST capture legal name, date of birth, and administrative sex/gender; DOB MUST NOT be in the future. | Minimum demographic set for safe care and FHIR `Patient` mapping. | Receptionist, Nurse |
| BR-IDENT-003 | The system MUST run a duplicate-detection check (name + DOB + contact) before creating a new patient and surface candidates for merge. | Duplicate charts fragment clinical history and endanger patients. | Receptionist, Hospital Admin |
| BR-IDENT-004 | Two patient records MAY be merged only by Hospital Admin; the merge MUST preserve both source identifiers and produce an audit trail. | Merges are irreversible-in-effect and high-risk. | Hospital Admin, Auditor |
| BR-IDENT-005 | A patient record MUST NOT be hard-deleted; deactivation is a soft state change retaining history. | Clinical and legal records must be retained, not destroyed. | Hospital Admin |
| BR-IDENT-006 | Contact methods (phone, email) MUST be validated for format; at least one contact channel SHOULD exist for notifications. | Enables BR-NOTIF rules and reduces no-shows. | Receptionist |
| BR-IDENT-007 | A self-registering Patient MUST be linked to exactly one patient record and MUST NOT view other patients' data. | Portal self-service must not breach isolation. | Patient |

## 2. Appointments (`APPT`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-APPT-001 | An appointment MUST reference a valid patient, a provider, a branch, and a service/visit type, all within the same tenant. | Referential and tenancy integrity. | Receptionist, Doctor |
| BR-APPT-002 | An appointment slot MUST fall within the provider's published availability for that branch. | Prevents booking outside working hours. | Receptionist |
| BR-APPT-003 | The system MUST reject double-booking: a provider MUST NOT hold two `BOOKED`/`CHECKED_IN` appointments whose time ranges overlap. | A clinician cannot be in two places at once. | Receptionist, Doctor |
| BR-APPT-004 | A patient SHOULD NOT hold two overlapping appointments; an attempt MUST warn and require explicit override by Hospital Admin. | Reduces conflicting care and wasted slots. | Receptionist, Hospital Admin |
| BR-APPT-005 | Rescheduling MUST re-run availability and double-booking checks (BR-APPT-002, BR-APPT-003) against the new slot. | The new slot must satisfy the same invariants. | Receptionist |
| BR-APPT-006 | Cancellation MUST record a reason and a cancelling actor; a cancelled slot MUST be released back to availability. | Capacity recovery and auditability. | Receptionist, Patient |
| BR-APPT-007 | An appointment MUST NOT be cancelled or rescheduled after it has reached `COMPLETED`. | Completed encounters are historical fact. | Receptionist |
| BR-APPT-008 | A no-show MUST be markable only after the scheduled start time has passed and the patient is not `CHECKED_IN`. | No-show is a post-hoc determination. | Receptionist, Doctor |
| BR-APPT-009 | Appointment lifecycle MUST follow `BOOKED → CHECKED_IN → IN_PROGRESS → COMPLETED`, with `CANCELLED`/`NO_SHOW` as terminal exits; illegal transitions MUST be rejected. | A guarded state machine prevents data corruption. | Receptionist, Doctor, Nurse |
| BR-APPT-010 | Cancellation/reschedule MUST trigger a patient notification per BR-NOTIF-001. | Patients must be informed of changes. | Receptionist |

## 3. Encounters & Visits (`ENC`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-ENC-001 | An encounter MUST be opened only from a `CHECKED_IN` appointment or as a valid walk-in, linked to one patient and one responsible provider. | Encounters anchor all clinical activity. | Doctor, Nurse |
| BR-ENC-002 | Only one encounter MUST be `IN_PROGRESS` per patient per branch at a time. | Prevents concurrent, conflicting documentation. | Doctor, Nurse |
| BR-ENC-003 | An encounter MUST carry at least one diagnosis (ICD-10) before it can be `COMPLETED`. | Diagnosis drives coding, billing, and reporting. | Doctor |
| BR-ENC-004 | Clinical notes MUST be append-only after sign-off; corrections MUST be addenda, never overwrites. | Medico-legal record integrity. | Doctor, Nurse, Auditor |
| BR-ENC-005 | Vital signs MUST be range-validated (e.g. heart rate 20–300 bpm) and out-of-range values flagged, not silently dropped. | Protects against transcription error and supports alerts. | Nurse |
| BR-ENC-006 | A completed encounter MUST be immutable; further activity requires a new encounter or a formal amendment. | Stable basis for billing and audit. | Doctor |

## 4. Prescriptions & Medications (`RX`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-RX-001 | A prescription MUST be created only by a Doctor within an active or completed encounter and MUST identify drug, dose, route, frequency, and duration. | Prescribing is a licensed clinical act with a minimum safe data set. | Doctor |
| BR-RX-002 | The system MUST run a drug–allergy check against the patient's recorded allergies and block (hard stop) on a known contraindication unless overridden with a documented reason. | Allergy reactions are a leading preventable harm. | Doctor, Pharmacist |
| BR-RX-003 | The system MUST run a drug–drug interaction check against active medications and warn on significant interactions. | Interaction screening is standard of care. | Doctor, Pharmacist |
| BR-RX-004 | Controlled-substance prescriptions MUST record the substance schedule, MUST limit quantity/duration per policy, and MUST NOT allow refills beyond the regulatory cap for that schedule. | Models controlled-substance stewardship. | Doctor, Pharmacist, Auditor |
| BR-RX-005 | A refill MUST NOT exceed the authorised refill count and MUST NOT be dispensed after the prescription's expiry date. | Prevents over-supply and stale orders. | Pharmacist |
| BR-RX-006 | Dispensing MUST be performed by a Pharmacist and MUST decrement the authorised quantity; a fully dispensed prescription MUST move to `COMPLETED`. | Separation of prescribing and dispensing. | Pharmacist |
| BR-RX-007 | Any allergy/interaction override MUST capture the overriding clinician, reason, and timestamp, and MUST be audited (BR-AUDIT-004). | Safety overrides require accountability. | Doctor, Auditor |
| BR-RX-008 | A discontinued or expired prescription MUST NOT be dispensed or refilled. | Terminal states are final. | Pharmacist |

## 5. Laboratory Orders & Results (`LAB`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-LAB-001 | A lab order MUST be placed by a Doctor, reference an encounter, and identify each test by a LOINC code. | Orders must be coded for interoperability and result matching. | Doctor |
| BR-LAB-002 | A result MUST be entered/verified by a Lab Technician and MUST link to its originating order and test. | Result provenance and traceability. | Lab Technician |
| BR-LAB-003 | Each numeric result MUST carry units and a reference range; values outside range MUST be flagged abnormal. | Clinical interpretability and alerting. | Lab Technician, Doctor |
| BR-LAB-004 | Critical (panic) values MUST raise a high-priority notification to the ordering provider (BR-NOTIF-002). | Critical results require immediate action. | Lab Technician, Doctor |
| BR-LAB-005 | A verified result MUST NOT be edited; corrections MUST be issued as an amended result that supersedes and references the original. | Result integrity, mirroring HL7/FHIR amend semantics. | Lab Technician, Auditor |
| BR-LAB-006 | Order lifecycle MUST follow `ORDERED → COLLECTED → IN_PROGRESS → RESULTED → VERIFIED`; cancellation is permitted only before `RESULTED`. | Guarded workflow prevents premature reporting. | Doctor, Lab Technician |

## 6. Radiology Orders & Results (`RAD`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-RAD-001 | A radiology order MUST be placed by a Doctor, reference an encounter, identify the modality, and carry a clinical indication. | Indication is required for appropriateness and safety. | Doctor |
| BR-RAD-002 | A radiology report MUST be authored/finalised by a Radiologist and linked to its order and study. | Reporting is a specialist act. | Radiologist |
| BR-RAD-003 | Procedures SHOULD be coded with CPT and findings with SNOMED CT where applicable. | Standards alignment for billing and analytics. | Radiologist, Billing Staff |
| BR-RAD-004 | A finalised report MUST be immutable; changes MUST be issued as an addendum referencing the original. | Diagnostic record integrity. | Radiologist, Auditor |

## 7. Billing, Invoices, Payments & Claims (`BILL`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-BILL-001 | An invoice MUST derive its line items from completed, coded clinical activity (encounters, procedures, labs, dispenses). | Billing must reflect delivered care, not free entry. | Billing Staff |
| BR-BILL-002 | Each billable line item MUST carry a procedure (CPT) and/or diagnosis (ICD-10) code as required for the charge type. | Coded charges enable claims and audit. | Billing Staff |
| BR-BILL-003 | Invoice total MUST equal the sum of line items minus adjustments/discounts; the system MUST reject a manually overridden total that breaks this equality. | Financial arithmetic integrity. | Billing Staff |
| BR-BILL-004 | A payment MUST NOT exceed the invoice outstanding balance; overpayment attempts MUST be rejected or routed to credit handling. | Prevents negative balances and reconciliation errors. | Billing Staff |
| BR-BILL-005 | Invoice lifecycle MUST follow `DRAFT → ISSUED → PARTIALLY_PAID → PAID`, with `VOID` as an admin-only terminal exit; a `PAID` invoice MUST NOT accept further charges. | Stable financial state machine. | Billing Staff, Hospital Admin |
| BR-BILL-006 | A refund MUST reference an existing settled payment and MUST NOT exceed the amount paid. | Refund integrity. | Billing Staff |
| BR-BILL-007 | A claim MUST be generated only from an `ISSUED` (or later) invoice and MUST attach payer, member, and the supporting diagnosis/procedure codes. | Claims require a billable, coded basis. | Insurance Staff, Billing Staff |
| BR-BILL-008 | Patient responsibility MUST be computed only after insurance adjudication results (or denial) are recorded. | Patients are billed the correct residual. | Billing Staff, Insurance Staff |

## 8. Insurance Verification & Coverage (`INS`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-INS-001 | A coverage record MUST identify payer, plan, member ID, subscriber relationship, and an effective date range. | Minimum data to adjudicate (FHIR `Coverage`). | Insurance Staff, Receptionist |
| BR-INS-002 | Eligibility MUST be verifiable for a service date; a service date outside the coverage effective range MUST be treated as not covered. | Coverage is time-bounded. | Insurance Staff |
| BR-INS-003 | A claim MUST NOT be submitted against expired, terminated, or unverified coverage. | Prevents predictable denials. | Insurance Staff |
| BR-INS-004 | Each member MUST have at most one active primary coverage at a service date; additional coverages MUST be ranked (secondary, tertiary). | Coordination-of-benefits correctness. | Insurance Staff |
| BR-INS-005 | Adjudication outcomes (approved/denied/partial with reason codes) MUST be recorded and drive BR-BILL-008. | Traceable adjudication. | Insurance Staff, Billing Staff |

## 9. Consent & Privacy (`CONS`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-CONS-001 | Access to a patient's clinical data MUST require an active treatment, payment, or operations relationship plus the role permission; absence MUST deny access. | Models minimum-necessary access. | All clinical/admin roles |
| BR-CONS-002 | A patient MUST be able to record and withdraw consent for data sharing; withdrawal MUST take effect for subsequent access decisions. | Patient autonomy over data. | Patient, Hospital Admin |
| BR-CONS-003 | Sensitive categories (e.g. behavioural health) MAY carry stricter consent and MUST NOT be exposed without the matching consent scope. | Heightened protection for sensitive data. | Doctor, Nurse, Auditor |
| BR-CONS-004 | Every read of patient PHI MUST be access-logged (BR-AUDIT-002) regardless of outcome. | "Who saw what" is a privacy requirement. | All roles, Auditor |
| BR-CONS-005 | All patient data in every environment MUST be synthetic and PHI-safe; real PHI MUST NOT be ingested. | Compliance posture of the platform. | All roles |

## 10. Audit (`AUDIT`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-AUDIT-001 | Every create/update/delete of a clinical, financial, or identity entity MUST emit an immutable audit event (who, what, when, before/after, tenant, correlation ID). | Tamper-evident change history. | Auditor |
| BR-AUDIT-002 | Every read of PHI MUST emit an access-audit event including actor, patient, purpose-of-use, and result (granted/denied). | Privacy access accounting. | Auditor |
| BR-AUDIT-003 | Authentication and authorization events (login success/failure, permission denial, token refresh) MUST be audited. | Security forensics. | Super Admin, Auditor |
| BR-AUDIT-004 | Safety overrides (allergy/interaction), merges, voids, refunds, and consent changes MUST be specially audited with the override reason. | High-risk actions need stronger accounting. | Auditor |
| BR-AUDIT-005 | Audit records MUST be append-only and MUST NOT be editable or deletable by any application role. | Integrity of the audit trail itself. | Super Admin, Auditor |
| BR-AUDIT-006 | Audit events MUST be tenant-scoped and queryable by tenant, actor, entity, and time range. | Investigations are scoped and efficient. | Auditor |

## 11. RBAC Access Rules (`RBAC`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-RBAC-001 | Every API operation MUST require an authenticated principal and an explicit permission; default is deny. | No implicit access. | All |
| BR-RBAC-002 | Authorization MUST be permission-based; roles are bundles of permissions, checked per endpoint. | Fine-grained, least-privilege control. | Super Admin, Hospital Admin |
| BR-RBAC-003 | Super Admin MAY administer across tenants; all other roles are confined to their tenant (BR-TENANT-001). | Platform vs organisation scope separation. | Super Admin |
| BR-RBAC-004 | Clinical write actions (diagnose, prescribe, order, report) MUST be restricted to the licensed role for that action (Doctor/Radiologist) and MUST NOT be performed by Reception/Billing/Insurance roles. | Enforces scope of practice. | Doctor, Radiologist, Nurse |
| BR-RBAC-005 | A Patient MUST access only their own (or authorised dependant's) records and MUST NOT perform clinical or administrative writes. | Portal least privilege. | Patient |
| BR-RBAC-006 | The Auditor role MUST be read-only across audit/clinical/financial data and MUST NOT mutate any record. | Independence of audit. | Auditor |
| BR-RBAC-007 | Privilege changes (role/permission grants) MUST be performed only by Super Admin or Hospital Admin within scope and MUST be audited (BR-AUDIT-003). | Controlled privilege escalation. | Super Admin, Hospital Admin, Auditor |

## 12. Multi-Tenancy Isolation Rules (`TENANT`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-TENANT-001 | Every tenant-scoped entity MUST carry a `tenant_id`; every query MUST be filtered by the caller's tenant context. | Hard data isolation between organisations. | All |
| BR-TENANT-002 | A request MUST NOT read or write data belonging to another tenant; cross-tenant access MUST be denied and audited. | Prevents data leakage across customers. | All, Auditor |
| BR-TENANT-003 | The tenant hierarchy MUST be Tenant → Hospital Network → Branch; an entity MUST resolve to exactly one tenant at the top. | Consistent scoping model. | Hospital Admin |
| BR-TENANT-004 | Uniqueness constraints (e.g. MRN, user email) MUST be enforced per tenant unless explicitly global (e.g. Super Admin accounts). | Tenancy-aware uniqueness. | Hospital Admin |
| BR-TENANT-005 | Branch-level data MUST NOT leak to sibling branches unless the user holds network-level scope. | Intra-tenant least privilege. | Hospital Admin, Doctor |

## 13. Notification Rules (`NOTIF`)

| ID | Statement | Rationale | Affected roles |
|----|-----------|-----------|----------------|
| BR-NOTIF-001 | Appointment booking, reschedule, and cancellation MUST trigger a patient notification on an available channel. | Keeps patients informed; reduces no-shows. | Receptionist, Patient |
| BR-NOTIF-002 | Critical lab values and other high-severity clinical events MUST trigger a high-priority notification to the responsible provider. | Time-critical safety signalling. | Lab Technician, Doctor |
| BR-NOTIF-003 | Notifications MUST NOT include unmasked PHI in low-trust channels (e.g. SMS); content MUST be minimised. | Limits exposure of protected data. | All |
| BR-NOTIF-004 | Notification delivery attempts MUST be recorded with outcome (sent/failed/retried) for traceability. | Operational accountability. | Hospital Admin, Auditor |
| BR-NOTIF-005 | Channel selection MUST honour patient communication preferences and consent (BR-CONS-002). | Respects patient choice. | Receptionist, Patient |

## Examples

- **Double-booking (BR-APPT-003):** Booking a Doctor for 10:00–10:30 when an
  existing `BOOKED` appointment already covers 10:15–10:45 → rejected with a
  validation Problem Details response; a non-overlapping 10:30–11:00 slot →
  accepted.
- **Allergy hard stop (BR-RX-002):** Prescribing amoxicillin to a patient with a
  recorded penicillin allergy → blocked; prescriber may override with a
  documented reason, which is audited under BR-AUDIT-004.
- **Cross-tenant denial (BR-TENANT-002):** Tenant A's Receptionist requesting a
  patient that belongs to Tenant B → `403`-class denial plus an audit event.

## Future Enhancements

- Externalise selected rules to a rules engine (e.g. Drools) so policy changes do
  not require redeploys.
- Add clinical-decision-support hooks (CDS Hooks) for BR-RX and BR-LAB checks.
- Expand controlled-substance rules to model jurisdiction-specific schedules.

## Dependencies

- Facts and roles: [PROJECT_METADATA.md](PROJECT_METADATA.md).
- Persistence of rule state: [DATABASE_BLUEPRINT.md](DATABASE_BLUEPRINT.md).
- Rule enforcement surface: [API_BLUEPRINT.md](API_BLUEPRINT.md).
- Standards used by rules: [FHIR_GUIDE.md](FHIR_GUIDE.md), [HL7_GUIDE.md](HL7_GUIDE.md).

## References

- [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §9 Compliance posture.
- [ARCHITECTURE.md](../ARCHITECTURE.md) §7 Cross-cutting seams.
- ICD-10, CPT, LOINC, SNOMED CT, FHIR R4, HL7 v2 (standards anchors).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Healthcare Architect | Initial authoritative business-rules catalogue (Milestone 1) |
