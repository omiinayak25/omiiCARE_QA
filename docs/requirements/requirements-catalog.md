# Requirements Catalog — OpenMRS-Primary Healthcare QA

> Generated 2026-07-01. Reference SUT OpenMRS (https://o2.openmrs.org); portable to OpenEMR/HAPI FHIR/SMART/omiiCARE via the Resource Adapter Layer.

**1795 requirements** across **66 modules**, traced by **4,187 test cases** — 100% coverage (0 gaps, 0 untraced). Full requirement→test mapping: [RTM.csv](../../manual-testing/rtm/RTM.csv).

## Accessibility (WCAG 2.1 AA) — 15 requirements, 58 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-A11Y-001 | All functionality is operable via keyboard-only interaction | P1 | High | 3 |
| REQ-A11Y-002 | No keyboard traps; focus is contained correctly in modals and restored on close | P1 | High | 3 |
| REQ-A11Y-003 | Focus order follows a logical, meaningful sequence matching reading order | P2 | Medium | 1 |
| REQ-A11Y-004 | All interactive controls display a visible focus indicator with adequate contrast | P1 | High | 2 |
| REQ-A11Y-005 | Bypass blocks and consistent navigation (skip links, landmarks, consistent ordering) | P2 | Medium | 3 |
| REQ-A11Y-006 | Pages expose correct semantic structure: landmarks, roles/states, headings, title, language | P2 | Medium | 7 |
| REQ-A11Y-007 | Every form field has a programmatically associated, persistent label/accessible name | P1 | High | 4 |
| REQ-A11Y-008 | Validation errors are identified, programmatically associated, and prevent destructive loss | P1 | High | 4 |
| REQ-A11Y-009 | Dynamic changes and status messages are announced to assistive technology via live regions | P2 | Medium | 4 |
| REQ-A11Y-010 | Text and UI component color contrast meets WCAG 1.4.3 / 1.4.11 thresholds | P1 | High | 4 |
| REQ-A11Y-011 | Information is not conveyed by color alone (required/error states) | P1 | High | 1 |
| REQ-A11Y-012 | Icon-only controls (logout, toggles, row actions) have unique descriptive accessible names | P1 | High | 5 |
| REQ-A11Y-013 | Tables, images, and clinical/standards-sourced data render with correct accessible semantics | P2 | Medium | 10 |
| REQ-A11Y-014 | Content reflows and remains usable at 200%/400% zoom and with text-spacing overrides | P2 | Medium | 4 |
| REQ-A11Y-015 | Time limits and motion are accommodated (warnings, extension, reduced-motion, pause) | P1 | High | 3 |

## Accessibility (per WCAG 2.1 AA SC) — 13 requirements, 64 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-A11Y2-001 | Non-text content has equivalent text alternatives (SC 1.1.1) | P2 | Medium | 7 |
| REQ-A11Y2-002 | Information, structure, and relationships are programmatically determinable (SC 1.3.1) | P1 | High | 7 |
| REQ-A11Y2-003 | Text contrast meets minimum AA thresholds (SC 1.4.3) | P2 | Medium | 5 |
| REQ-A11Y2-004 | Content reflows at 320 CSS px / 400% without 2D scrolling (SC 1.4.10) | P2 | Medium | 4 |
| REQ-A11Y2-005 | Non-text contrast for UI components and graphics meets 3:1 (SC 1.4.11) | P2 | Medium | 4 |
| REQ-A11Y2-006 | All functionality is keyboard operable without traps (SC 2.1.1) | P1 | High | 6 |
| REQ-A11Y2-007 | Focus order is logical and meaningful (SC 2.4.3) | P2 | High | 5 |
| REQ-A11Y2-008 | Keyboard focus indicator is visible (SC 2.4.7) | P2 | Medium | 3 |
| REQ-A11Y2-009 | Input errors are identified and described in text (SC 3.3.1) | P1 | High | 5 |
| REQ-A11Y2-010 | Labels and instructions are provided for user input (SC 3.3.2) | P1 | High | 4 |
| REQ-A11Y2-011 | Name, role, and value are exposed for all components (SC 4.1.2) | P2 | High | 8 |
| REQ-A11Y2-012 | Status messages are programmatically announced (SC 4.1.3) | P2 | High | 5 |
| REQ-A11Y2-013 | Accessibility metadata must not leak PHI beyond visible content | P2 | High | 1 |

## Allergies & Interactions (deep) — 25 requirements, 54 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-ALLERG2-001 | Drug-class allergen coding granularity | P1 | High | 1 |
| REQ-ALLERG2-002 | Allergen concept maps to active RxNorm/SNOMED reference term | P2 | Medium | 2 |
| REQ-ALLERG2-003 | Reaction severity vs overall criticality distinction and derivation | P1 | High | 3 |
| REQ-ALLERG2-004 | Allergy onset date validity boundaries | P2 | Medium | 2 |
| REQ-ALLERG2-005 | NKA vs unassessed allergy status semantics and transitions | P1 | High | 4 |
| REQ-ALLERG2-006 | Duplicate allergen detection and normalization | P2 | Medium | 2 |
| REQ-ALLERG2-007 | Allergy void requires reason and excludes from cross-check | P1 | High | 2 |
| REQ-ALLERG2-008 | Allergy edit history auditability | P2 | Medium | 1 |
| REQ-ALLERG2-009 | Drug-allergy cross-check at order entry | P1 | High | 6 |
| REQ-ALLERG2-010 | Cross-reactivity advisory (beta-lactam) | P2 | High | 1 |
| REQ-ALLERG2-011 | Allergy alert override decision table and reason capture | P1 | High | 3 |
| REQ-ALLERG2-012 | Override audit trail completeness | P1 | High | 1 |
| REQ-ALLERG2-013 | Drug-drug interaction matrix and active-list scoping | P1 | High | 4 |
| REQ-ALLERG2-014 | Drug-condition and pregnancy contraindication advisories | P2 | High | 2 |
| REQ-ALLERG2-015 | Duplicate-therapy / therapeutic overlap detection | P2 | Medium | 1 |
| REQ-ALLERG2-016 | Interaction check latency SLA | P2 | Medium | 1 |
| REQ-ALLERG2-017 | Allergy reconciliation and conflict resolution | P2 | High | 2 |
| REQ-ALLERG2-018 | FHIR R4 AllergyIntolerance mapping fidelity | P1 | High | 5 |
| REQ-ALLERG2-019 | HL7 v2 AL1 allergy ingestion mapping | P2 | High | 3 |
| REQ-ALLERG2-020 | Allergy REST API CRUD validation | P2 | Medium | 2 |
| REQ-ALLERG2-021 | Override audit immutability/tamper-evidence | P1 | High | 1 |
| REQ-ALLERG2-022 | Allergy IDOR / authorization on direct UUID access | P1 | High | 1 |
| REQ-ALLERG2-023 | Free-text allergen XSS sanitization | P2 | Medium | 1 |
| REQ-ALLERG2-024 | Accessibility of allergy alerts and severity cues | P2 | Medium | 2 |
| REQ-ALLERG2-025 | Concurrent allergy edit conflict handling | P2 | Medium | 1 |

## Appointment Scheduling — 19 requirements, 68 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-APPT-001 | Book an appointment with a valid patient, provider, service, location and future slot | P1 | High | 4 |
| REQ-APPT-002 | Service and appointment-type selection drive provider filtering and duration defaults | P2 | Medium | 3 |
| REQ-APPT-003 | Prevent booking appointments in the past | P1 | High | 3 |
| REQ-APPT-004 | Prevent double-booking and concurrent slot conflicts for provider, patient and location | P1 | High | 4 |
| REQ-APPT-005 | Restrict bookable slots to provider availability, location and service duration | P2 | Medium | 4 |
| REQ-APPT-006 | Reschedule an appointment to a valid future slot with conflict and state checks | P1 | High | 4 |
| REQ-APPT-007 | Cancel an appointment with reason capture and slot release | P1 | High | 4 |
| REQ-APPT-008 | Mark and auto-flag no-show/missed appointments | P2 | Medium | 3 |
| REQ-APPT-009 | Enforce the appointment status state machine (Scheduled/CheckedIn/Completed/Cancelled/Missed) | P1 | High | 8 |
| REQ-APPT-010 | Schedule appointment reminders respecting consent and minimum-necessary PHI | P2 | Medium | 4 |
| REQ-APPT-011 | Create and manage recurring appointment series | P2 | Medium | 5 |
| REQ-APPT-012 | Validate combinatorial booking parameters via pairwise coverage | P2 | Medium | 2 |
| REQ-APPT-013 | Display appointments correctly across views, timezones and DST | P3 | Medium | 2 |
| REQ-APPT-014 | Booking interface meets WCAG 2.1 AA accessibility | P2 | Medium | 2 |
| REQ-APPT-015 | Enforce authorization, access scope and session controls for scheduling | P1 | High | 5 |
| REQ-APPT-016 | Maintain a complete audit trail for appointment changes | P1 | High | 2 |
| REQ-APPT-017 | Expose appointment data via REST API with integrity and filtering | P1 | High | 3 |
| REQ-APPT-018 | Support FHIR R4 Appointment/Slot resources and value sets | P1 | High | 3 |
| REQ-APPT-019 | Process inbound HL7 SIU appointment messages with proper ACK handling | P2 | Medium | 3 |

## Audit & Compliance (deep) — 22 requirements, 58 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-AUDIT2-001 | PHI CRUD operations (create/update/void/merge) are fully audited with before/after values | P1 | High | 4 |
| REQ-AUDIT2-002 | PHI read/view and disclosure (print) events generate access audit records | P1 | High | 2 |
| REQ-AUDIT2-003 | Authentication events (login success/failure, logout, timeout, lockout) are audited | P1 | High | 4 |
| REQ-AUDIT2-004 | PHI exports including FHIR Bulk $export are audited with record counts and parameters | P1 | High | 2 |
| REQ-AUDIT2-005 | Configuration, RBAC and audit-toggle changes are audited (anti-tamper) | P1 | High | 3 |
| REQ-AUDIT2-006 | Audit records are immutable: no UI edit, DB update/delete blocked or detected | P1 | High | 3 |
| REQ-AUDIT2-007 | Tamper-evidence via hash chaining and periodic integrity verification | P1 | High | 3 |
| REQ-AUDIT2-008 | Audit retention, purge and legal hold enforcement | P1 | High | 3 |
| REQ-AUDIT2-009 | Access reports answer who/what/when/where with correct paging and filters | P1 | High | 5 |
| REQ-AUDIT2-010 | Minimum-necessary / out-of-care-team access review | P2 | Medium | 1 |
| REQ-AUDIT2-011 | Break-glass emergency access requires justification and is logged/reviewed | P1 | High | 4 |
| REQ-AUDIT2-012 | Breach/anomaly detection (mass access, snooping, off-hours, rate thresholds) | P1 | High | 4 |
| REQ-AUDIT2-013 | FHIR AuditEvent conformance (agent/source/entity/outcome) | P1 | High | 3 |
| REQ-AUDIT2-014 | HL7/IHE ATNA audit messaging and message-id linkage | P2 | Medium | 2 |
| REQ-AUDIT2-015 | Regulatory mapping to HIPAA/GDPR audit controls and minimum dataset | P1 | High | 3 |
| REQ-AUDIT2-016 | Timestamp integrity in UTC across timezones and DST | P2 | Medium | 2 |
| REQ-AUDIT2-017 | Audit log access restricted to compliance/admin with PHI masking | P1 | High | 2 |
| REQ-AUDIT2-018 | Audit query API enforces auth, authorization and pagination limits | P1 | High | 2 |
| REQ-AUDIT2-019 | Exported audit reports are signed/checksummed for non-repudiation | P2 | Medium | 1 |
| REQ-AUDIT2-020 | Fail-safe audit handling: block/queue on store outage, no event loss on crash | P1 | High | 2 |
| REQ-AUDIT2-021 | Consent grant/revoke and consent-denied access are audited | P2 | Medium | 2 |
| REQ-AUDIT2-022 | Audit log viewer is accessible (keyboard + screen reader) | P3 | Low | 1 |

## Authentication & Session — 23 requirements, 64 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-AUTH-001 | User can authenticate with valid credentials at a selected session location | P1 | High | 3 |
| REQ-AUTH-002 | Invalid credentials are rejected with a generic non-enumerating error | P1 | High | 5 |
| REQ-AUTH-003 | Required login fields are validated and oversized/empty input is handled safely | P2 | Medium | 4 |
| REQ-AUTH-004 | A session location must be selected before a session is established | P2 | Medium | 1 |
| REQ-AUTH-005 | Account lockout enforced after configured consecutive failures with auto-unlock and counter reset | P1 | High | 6 |
| REQ-AUTH-006 | User can log out and terminate the session | P1 | High | 1 |
| REQ-AUTH-007 | Server-side session is invalidated on logout and cannot be replayed or back-buttoned | P1 | High | 3 |
| REQ-AUTH-008 | Idle sessions expire after the configured inactivity timeout | P1 | High | 3 |
| REQ-AUTH-009 | Concurrent sessions are managed per configured policy | P2 | Medium | 4 |
| REQ-AUTH-010 | User can switch session location only among authorized login locations | P2 | Medium | 3 |
| REQ-AUTH-011 | Remembered login location is scoped to the browser and applied as default | P3 | Low | 2 |
| REQ-AUTH-012 | Unauthenticated deep links redirect to login and return to target after auth | P1 | High | 2 |
| REQ-AUTH-013 | REST API authentication accepts valid and rejects invalid credentials | P1 | High | 2 |
| REQ-AUTH-014 | Password complexity, reuse, confirmation and forced-change rules are enforced | P2 | Medium | 7 |
| REQ-AUTH-015 | Login is resistant to injection and script payloads | P1 | High | 2 |
| REQ-AUTH-016 | Authentication and logout events are audited | P1 | High | 2 |
| REQ-AUTH-017 | Stored credentials are salted-hashed and never plaintext | P1 | High | 1 |
| REQ-AUTH-018 | Login form meets WCAG 2.1 AA accessibility | P2 | Medium | 3 |
| REQ-AUTH-019 | Credentials and sessions are protected in transit with secure cookies and id rotation | P1 | High | 3 |
| REQ-AUTH-020 | Disabled/retired accounts cannot authenticate | P1 | High | 1 |
| REQ-AUTH-021 | FHIR API enforces authentication, scope and token expiry | P1 | High | 3 |
| REQ-AUTH-022 | HL7 interface authenticates/authorizes message sources | P3 | Medium | 1 |
| REQ-AUTH-023 | Resource Adapter Layer propagates identity and denies unmapped users | P2 | Medium | 2 |

## Billing & Insurance — 22 requirements, 76 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-BILL-001 | Charge capture from finalized encounters with status tracking | P1 | High | 2 |
| REQ-BILL-002 | Multi-service capture with duplicate-charge prevention | P2 | High | 2 |
| REQ-BILL-003 | Service pricing via effective-dated chargemaster | P1 | High | 6 |
| REQ-BILL-004 | Invoice generation with accurate subtotaling | P1 | High | 3 |
| REQ-BILL-005 | Invoice lifecycle and void controls | P1 | High | 4 |
| REQ-BILL-006 | Co-pay calculation with network/plan rules | P1 | Medium | 4 |
| REQ-BILL-007 | Insurance eligibility verification (270/271, FHIR) | P1 | High | 6 |
| REQ-BILL-008 | Claim creation with code and NPI validation | P1 | High | 4 |
| REQ-BILL-009 | Claim submission via 837P/clearinghouse | P1 | High | 4 |
| REQ-BILL-010 | Claim status tracking (276/277) | P2 | High | 2 |
| REQ-BILL-011 | Denial and rejection handling with CARC/RARC and rebill | P1 | High | 3 |
| REQ-BILL-012 | Payment posting (ERA 835 and patient) with idempotency | P1 | High | 7 |
| REQ-BILL-013 | Adjustments and write-offs | P2 | Medium | 2 |
| REQ-BILL-014 | Refunds and payment reversals with approval controls | P2 | High | 3 |
| REQ-BILL-015 | Coverage rules decision engine (COB, deductible, coinsurance, OOP max) | P1 | High | 5 |
| REQ-BILL-016 | Tax calculation and exemptions | P2 | Medium | 3 |
| REQ-BILL-017 | Patient statement generation and suppression | P1 | Medium | 2 |
| REQ-BILL-018 | Billing audit logging and data integrity | P2 | High | 3 |
| REQ-BILL-019 | Billing security: RBAC, IDOR, injection, PHI masking, auth | P1 | High | 5 |
| REQ-BILL-020 | EOB display and reconciliation | P2 | High | 3 |
| REQ-BILL-021 | Billing REST API contract | P2 | Medium | 1 |
| REQ-BILL-022 | Billing UI/document accessibility (WCAG 2.1 AA) | P2 | Medium | 2 |

## Billing (deep) — 40 requirements, 75 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-BILL2-001 | Charge master item creation and validation | P1 | High | 3 |
| REQ-BILL2-002 | Charge master price effective-dating and history | P2 | Medium | 2 |
| REQ-BILL2-003 | Charge item activation/inactivation lifecycle | P2 | Medium | 1 |
| REQ-BILL2-004 | Currency and rounding precision for charges | P3 | Low | 1 |
| REQ-BILL2-010 | Itemized invoice generation and line math | P1 | High | 3 |
| REQ-BILL2-011 | Invoice subtotal/tax/total reconciliation | P1 | High | 2 |
| REQ-BILL2-012 | Invoice finalization, void and immutability | P1 | High | 2 |
| REQ-BILL2-013 | Invoice PHI handling and export controls | P1 | High | 1 |
| REQ-BILL2-020 | Service bundling and pricing | P1 | High | 2 |
| REQ-BILL2-021 | Bundle decision logic and overlap prevention | P1 | High | 2 |
| REQ-BILL2-030 | Discount application and approval limits | P1 | High | 3 |
| REQ-BILL2-031 | Fixed-amount discount constraints | P2 | Medium | 1 |
| REQ-BILL2-032 | Write-off processing with reason and approval | P1 | High | 2 |
| REQ-BILL2-040 | Payment plan creation and installment math | P1 | High | 2 |
| REQ-BILL2-041 | Payment plan state transitions | P2 | Medium | 2 |
| REQ-BILL2-042 | Payment plan term/frequency combinations | P3 | Medium | 1 |
| REQ-BILL2-050 | Payment application and credit handling | P1 | High | 3 |
| REQ-BILL2-060 | Patient statement generation | P2 | Medium | 3 |
| REQ-BILL2-070 | Aging bucket classification | P1 | High | 2 |
| REQ-BILL2-071 | Re-aging on partial payment | P2 | Medium | 1 |
| REQ-BILL2-080 | Refund processing and limits | P1 | High | 2 |
| REQ-BILL2-081 | Refund eligibility and method controls | P2 | Medium | 2 |
| REQ-BILL2-082 | Credit/debit adjustments and balance floor | P1 | High | 3 |
| REQ-BILL2-090 | Tax computation and exemptions | P1 | High | 2 |
| REQ-BILL2-091 | Tax rounding and order-of-operations | P1 | High | 2 |
| REQ-BILL2-100 | Payer contract pricing and precedence | P1 | High | 2 |
| REQ-BILL2-101 | Pricing rule combinations and fallback | P2 | Medium | 3 |
| REQ-BILL2-110 | Insurance estimate and denial handling | P1 | High | 2 |
| REQ-BILL2-120 | FHIR Invoice resource exposure | P1 | High | 2 |
| REQ-BILL2-121 | FHIR ChargeItem and validation | P2 | Medium | 2 |
| REQ-BILL2-130 | HL7 DFT P03 charge messaging | P2 | Medium | 2 |
| REQ-BILL2-131 | HL7 inbound message error handling | P3 | Low | 1 |
| REQ-BILL2-140 | Billing REST API auth and creation | P1 | High | 2 |
| REQ-BILL2-141 | Payment API idempotency | P1 | High | 1 |
| REQ-BILL2-150 | Transaction atomicity and concurrency | P1 | High | 2 |
| REQ-BILL2-151 | Monetary fixed-precision storage | P2 | Medium | 1 |
| REQ-BILL2-160 | Segregation of duties and tamper protection | P1 | High | 2 |
| REQ-BILL2-161 | Financial audit logging | P1 | High | 1 |
| REQ-BILL2-170 | Billing UI accessibility | P2 | Medium | 1 |
| REQ-BILL2-180 | Exploratory monetary edge-case coverage | P3 | Medium | 1 |

## Allergies, Conditions & Diagnoses — 24 requirements, 68 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-CLIN-001 | Record patient allergies with allergen, category, reaction and severity | P1 | High | 10 |
| REQ-CLIN-002 | Edit an existing allergy's reaction and severity | P1 | High | 1 |
| REQ-CLIN-003 | Remove/void an allergy with a captured reason | P1 | High | 1 |
| REQ-CLIN-004 | Manage No Known Allergies status and its transitions | P1 | High | 3 |
| REQ-CLIN-005 | Prevent duplicate allergy entries for the same allergen | P1 | High | 1 |
| REQ-CLIN-006 | Validate allergy input fields (required allergen, length, value codes) | P1 | High | 4 |
| REQ-CLIN-007 | Trigger severity-driven allergy decision support alerts at drug order | P2 | High | 2 |
| REQ-CLIN-008 | Clinical widgets meet WCAG 2.1 AA accessibility | P2 | Medium | 2 |
| REQ-CLIN-009 | Restrict allergy/condition/diagnosis access and edits to authorized roles | P1 | High | 2 |
| REQ-CLIN-010 | Maintain audit trail for clinical record creation and changes | P2 | High | 2 |
| REQ-CLIN-011 | Expose allergies as standards-compliant FHIR AllergyIntolerance | P1 | High | 3 |
| REQ-CLIN-012 | Record conditions (coded and non-coded) with status and onset | P1 | High | 4 |
| REQ-CLIN-013 | Edit and void conditions | P2 | Medium | 2 |
| REQ-CLIN-014 | Support condition state transitions Active/Inactive/Resolved/reactivation | P1 | High | 4 |
| REQ-CLIN-015 | Validate condition date logic (onset, end date, status-date rules) | P2 | Medium | 4 |
| REQ-CLIN-016 | Prevent duplicate active conditions for the same concept | P2 | Medium | 1 |
| REQ-CLIN-017 | Expose conditions as standards-compliant FHIR Condition | P1 | High | 2 |
| REQ-CLIN-018 | Record encounter diagnoses with order (primary/secondary) and certainty | P1 | High | 6 |
| REQ-CLIN-019 | Enforce clinical coding (ICD-10/SNOMED) and code validation for diagnoses | P1 | High | 5 |
| REQ-CLIN-020 | Enforce diagnosis integrity rules (single primary, no duplicates, limits) | P2 | Medium | 3 |
| REQ-CLIN-021 | Expose diagnoses as FHIR Condition with encounter-diagnosis context and rank | P1 | High | 2 |
| REQ-CLIN-022 | Export allergies and diagnoses via HL7 v2 (AL1/DG1) segments | P2 | Medium | 2 |
| REQ-CLIN-023 | Sanitize free-text clinical inputs against injection/XSS | P2 | Medium | 1 |
| REQ-CLIN-024 | Normalize clinical data across platforms via Resource Adapter Layer | P2 | Medium | 1 |

## Cross-Browser & Compatibility — 28 requirements, 50 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-COMPAT-001 | Patient dashboard renders correctly on latest browsers (Chrome/Firefox) | P1 | Medium | 2 |
| REQ-COMPAT-002 | Registration and clinical forms function on Edge and Safari latest | P1 | High | 2 |
| REQ-COMPAT-003 | Core flows supported on prior browser versions (N-1/ESR) | P2 | Medium | 3 |
| REQ-COMPAT-004 | Application functions on Linux desktop OS | P2 | Medium | 1 |
| REQ-COMPAT-005 | Login succeeds across pairwise Browser x OS matrix | P1 | Medium | 1 |
| REQ-COMPAT-006 | Locale-driven date/number/name formatting correct across browser/OS | P2 | Medium | 2 |
| REQ-COMPAT-007 | Layout integrity across min/high resolutions and scaling | P2 | Medium | 2 |
| REQ-COMPAT-008 | Responsive breakpoint behavior at viewport boundaries | P2 | Medium | 2 |
| REQ-COMPAT-009 | Zoom accessibility (WCAG 1.4.4/1.4.10) up to 200-400 percent | P2 | Medium | 3 |
| REQ-COMPAT-010 | Internationalization: RTL and UTF-8 multibyte support | P2 | Medium | 2 |
| REQ-COMPAT-011 | Timezone and DST handling for clinical datetimes | P1 | High | 2 |
| REQ-COMPAT-012 | FHIR API content negotiation and timezone-correct instants | P2 | Medium | 2 |
| REQ-COMPAT-013 | Secure session cookie attributes and cookie-blocking behavior | P1 | High | 4 |
| REQ-COMPAT-014 | Session persistence, isolation and timeout across browsers | P1 | High | 2 |
| REQ-COMPAT-015 | File download (PDF/CSV) integrity and naming across browsers/OS | P2 | Medium | 3 |
| REQ-COMPAT-016 | Large file download completes without timeout | P3 | Medium | 1 |
| REQ-COMPAT-017 | Attachment upload works across browsers including drag-and-drop | P2 | Medium | 2 |
| REQ-COMPAT-018 | Upload validation: size boundary and disallowed file types | P1 | High | 2 |
| REQ-COMPAT-019 | Rendering engine parity (Blink/Gecko/WebKit) for layout and inputs | P2 | Medium | 2 |
| REQ-COMPAT-020 | JavaScript compatibility: no console errors and JS-disabled degradation | P1 | Medium | 2 |
| REQ-COMPAT-021 | Consistent static asset caching headers across browsers | P3 | Low | 1 |
| REQ-COMPAT-022 | bfcache must not expose stale PHI after logout | P1 | High | 1 |
| REQ-COMPAT-023 | SMART on FHIR app launch across browsers with storage partitioning | P2 | High | 1 |
| REQ-COMPAT-024 | HL7 v2 ingestion independent of client OS and encoding-preserving | P3 | Low | 1 |
| REQ-COMPAT-025 | Database/data consistency regardless of authoring browser | P2 | Medium | 1 |
| REQ-COMPAT-026 | Print stylesheet renders patient summary correctly across browsers | P3 | Low | 1 |
| REQ-COMPAT-027 | Resource Adapter Layer portability across OpenMRS/HAPI/OpenEMR | P2 | Medium | 1 |
| REQ-COMPAT-028 | Screen reader/AT compatibility across browser pairs (WCAG 4.1.2) | P2 | Medium | 1 |

## Concept Dictionary & Metadata Admin — 25 requirements, 72 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-CONFIG-001 | Concept creation requires valid fully specified name, class and datatype | P2 | Medium | 3 |
| REQ-CONFIG-002 | Coded concepts support an ordered set of permitted answers | P2 | Medium | 3 |
| REQ-CONFIG-003 | Fully specified names are unique within class and locale | P2 | Medium | 1 |
| REQ-CONFIG-004 | Numeric/complex datatype attributes and ranges are validated and nested correctly | P2 | High | 6 |
| REQ-CONFIG-005 | Concept edits are safe, propagate to dependents and protect in-use concepts | P2 | High | 3 |
| REQ-CONFIG-006 | Concept classes can be created, retired and are protected when in use | P3 | Medium | 3 |
| REQ-CONFIG-007 | Names, synonyms and short names are validated, localized and length/unicode safe | P3 | Low | 6 |
| REQ-CONFIG-008 | Concept sets support ordered members without self or circular references | P2 | Medium | 4 |
| REQ-CONFIG-009 | Concept mappings to SNOMED/LOINC/ICD-10 are validated and de-duplicated | P2 | Medium | 7 |
| REQ-CONFIG-010 | Concepts can be retired/unretired with mandatory reason and audit metadata | P2 | Medium | 4 |
| REQ-CONFIG-011 | Forms bind concepts to fields with versioning and retirement handling | P2 | Medium | 4 |
| REQ-CONFIG-012 | Order types are uniquely named and configurable | P3 | Medium | 2 |
| REQ-CONFIG-013 | Encounter types bind view/edit privileges enforced across UI and API | P2 | High | 2 |
| REQ-CONFIG-014 | Visit types are uniquely named, required and retirable | P3 | Low | 2 |
| REQ-CONFIG-015 | Dictionary search matches name/synonym/mapping/id with retired toggle | P2 | Medium | 2 |
| REQ-CONFIG-016 | Metadata admin enforces privileges and resists XSS/SQL injection | P1 | High | 3 |
| REQ-CONFIG-017 | All metadata changes are immutably audit logged | P2 | Medium | 1 |
| REQ-CONFIG-018 | REST API supports concept CRUD with validation and soft-retire/purge protection | P2 | High | 4 |
| REQ-CONFIG-019 | Concept metadata exports to FHIR with correct code system URIs and ValueSets | P2 | Medium | 3 |
| REQ-CONFIG-020 | Coded concepts resolve correctly in HL7 v2 OBX with coding system | P3 | Medium | 1 |
| REQ-CONFIG-021 | Database-level integrity for UUIDs, source references and search indexing | P2 | High | 3 |
| REQ-CONFIG-022 | Metadata admin screens meet WCAG 2.1 AA accessibility | P2 | Medium | 2 |
| REQ-CONFIG-023 | Concept authoring remains consistent under exploratory toggling | P3 | Medium | 1 |
| REQ-CONFIG-024 | Class and datatype combinations render and validate correctly (pairwise) | P3 | Medium | 1 |
| REQ-CONFIG-025 | Resource Adapter Layer normalizes concept metadata across target systems | P2 | High | 1 |

## Consent & Privacy — 22 requirements, 58 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-CONSENT-001 | Consent capture with scope and status | P1 | High | 4 |
| REQ-CONSENT-002 | Consent effective/expiry date validation and horizon limits | P2 | Medium | 3 |
| REQ-CONSENT-003 | Minor/guardian grantor consent and age-of-capacity rules | P1 | High | 2 |
| REQ-CONSENT-004 | Scope-based authorization by purpose and role | P1 | High | 2 |
| REQ-CONSENT-005 | Except/deny provisions for sensitive categories | P1 | High | 1 |
| REQ-CONSENT-006 | Consent expiry state transition and boundary enforcement | P1 | High | 3 |
| REQ-CONSENT-007 | Consent revocation, partial revoke, immutability and cascade | P1 | High | 6 |
| REQ-CONSENT-008 | Break-glass emergency override with justification and TTL | P1 | High | 4 |
| REQ-CONSENT-009 | Break-glass post-hoc review queue | P2 | High | 1 |
| REQ-CONSENT-010 | Minimum-necessary data projection by purpose | P1 | High | 4 |
| REQ-CONSENT-011 | Accounting of disclosures log capture and immutability | P1 | High | 3 |
| REQ-CONSENT-012 | Disclosure record retention floor | P3 | Medium | 1 |
| REQ-CONSENT-013 | Restricted/VIP record masking and care-team access | P1 | High | 4 |
| REQ-CONSENT-014 | FHIR Consent resource CRUD and profile validation | P1 | High | 3 |
| REQ-CONSENT-015 | FHIR Consent provision deny/period enforcement | P1 | High | 2 |
| REQ-CONSENT-016 | Authorization checks: opt-in default-deny, conflict resolution | P1 | High | 5 |
| REQ-CONSENT-017 | Fail-closed and tamper-resistant consent enforcement | P1 | High | 2 |
| REQ-CONSENT-018 | Audit logging of consent operations and sensitive reads | P1 | High | 3 |
| REQ-CONSENT-019 | HL7 disclosure/recipient segment mapping | P3 | Medium | 1 |
| REQ-CONSENT-020 | Accessibility of consent/restriction/break-glass dialogs | P3 | Low | 2 |
| REQ-CONSENT-021 | Consent referential integrity on patient merge | P2 | Medium | 1 |
| REQ-CONSENT-022 | Patient right-of-access self-disclosure handling | P3 | Low | 1 |

## Dashboard Widgets (deep) — 55 requirements, 60 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-DASH2-001 | Diagnoses widget data accuracy, ordering and certainty | P1 | High | 2 |
| REQ-DASH2-002 | Diagnoses widget empty state | P3 | Low | 1 |
| REQ-DASH2-003 | Diagnoses widget drill-down and concurrency | P2 | Medium | 2 |
| REQ-DASH2-004 | Diagnoses widget permission-gated visibility | P1 | High | 1 |
| REQ-DASH2-005 | Diagnoses FHIR Condition coded mapping | P2 | Medium | 1 |
| REQ-DASH2-006 | Diagnoses widget loading state | P3 | Medium | 1 |
| REQ-DASH2-010 | Latest Obs latest-value and unit accuracy | P1 | High | 2 |
| REQ-DASH2-011 | Latest Obs tie-break determinism | P3 | Medium | 1 |
| REQ-DASH2-012 | Latest Obs abnormal/critical flagging | P1 | High | 1 |
| REQ-DASH2-013 | Latest Obs voided exclusion | P2 | Medium | 1 |
| REQ-DASH2-014 | Latest Obs refresh without reload | P2 | Medium | 1 |
| REQ-DASH2-015 | Latest Obs XSS-safe free-text rendering | P2 | Medium | 1 |
| REQ-DASH2-020 | Vitals widget latest set accuracy and partial sets | P1 | High | 2 |
| REQ-DASH2-021 | Vitals BP boundary classification | P1 | High | 1 |
| REQ-DASH2-022 | Vitals impossible-value rejection | P2 | Medium | 1 |
| REQ-DASH2-023 | Vitals unit-system handling | P3 | Medium | 1 |
| REQ-DASH2-024 | Vitals widget error state | P2 | Medium | 1 |
| REQ-DASH2-025 | Vitals BMI derivation consistency | P3 | Low | 1 |
| REQ-DASH2-030 | Recent Visits ordering and accuracy | P1 | Medium | 1 |
| REQ-DASH2-031 | Recent Visits active/ended state | P2 | Medium | 1 |
| REQ-DASH2-032 | Recent Visits drill-down to encounters | P2 | Medium | 1 |
| REQ-DASH2-033 | Recent Visits pagination/limit boundary | P3 | Low | 1 |
| REQ-DASH2-034 | Recent Visits FHIR location accuracy | P3 | Low | 1 |
| REQ-DASH2-040 | Family History relationships and conditions | P2 | Medium | 1 |
| REQ-DASH2-041 | Family History empty state | P3 | Low | 1 |
| REQ-DASH2-042 | Family History PHI-safe rendering | P2 | Medium | 1 |
| REQ-DASH2-050 | Conditions active/inactive separation | P1 | High | 1 |
| REQ-DASH2-051 | Conditions clinicalStatus transition | P2 | Medium | 1 |
| REQ-DASH2-052 | Conditions FHIR status mapping | P2 | Medium | 1 |
| REQ-DASH2-053 | Conditions non-coded free-text handling | P3 | Low | 1 |
| REQ-DASH2-060 | Allergies allergen/reaction/severity display | P1 | High | 2 |
| REQ-DASH2-061 | Allergies Unknown vs NKA distinction | P1 | High | 1 |
| REQ-DASH2-062 | Allergies critical-safety visibility under permissions | P1 | High | 1 |
| REQ-DASH2-063 | Allergies FHIR criticality mapping | P2 | Medium | 1 |
| REQ-DASH2-064 | Allergies HL7 AL1 ingestion | P2 | Medium | 1 |
| REQ-DASH2-070 | Attachments listing and metadata | P2 | Medium | 1 |
| REQ-DASH2-071 | Attachments file-type/size validation | P2 | Medium | 1 |
| REQ-DASH2-072 | Attachments authorization on retrieval (IDOR) | P1 | High | 1 |
| REQ-DASH2-073 | Attachments corrupt-file handling | P3 | Low | 1 |
| REQ-DASH2-074 | Attachments upload audit trail | P2 | Medium | 1 |
| REQ-DASH2-080 | Weight graph chronological accuracy | P2 | Medium | 1 |
| REQ-DASH2-081 | Weight graph single/empty data rendering | P3 | Low | 1 |
| REQ-DASH2-082 | Weight graph voided exclusion | P3 | Medium | 1 |
| REQ-DASH2-083 | Weight graph unit consistency | P3 | Medium | 1 |
| REQ-DASH2-090 | Appointments upcoming listing and status | P1 | Medium | 1 |
| REQ-DASH2-091 | Appointments status transitions | P2 | Medium | 1 |
| REQ-DASH2-092 | Appointments cancelled/missed handling | P2 | Medium | 1 |
| REQ-DASH2-093 | Appointments timezone/DST correctness | P2 | Medium | 1 |
| REQ-DASH2-094 | Appointments API cross-patient isolation | P1 | High | 1 |
| REQ-DASH2-100 | Dashboard partial-failure isolation | P2 | Medium | 1 |
| REQ-DASH2-101 | Dashboard refresh-all consistency | P3 | Low | 1 |
| REQ-DASH2-102 | Per-role widget visibility configuration | P2 | Medium | 1 |
| REQ-DASH2-103 | Dashboard widget accessibility (WCAG 2.1 AA) | P2 | Medium | 1 |
| REQ-DASH2-104 | Dashboard widget data-access audit logging | P2 | Medium | 1 |
| REQ-DASH2-105 | Dashboard loading race / patient-switch isolation | P3 | Medium | 1 |

## Data Management & Integrity — 17 requirements, 47 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-DATA-001 | Merge two duplicate patient records into a preferred surviving record | P1 | High | 6 |
| REQ-DATA-002 | Preserve and transfer all encounters, identifiers, observations and diagnoses to the surviving record on merge | P1 | High | 4 |
| REQ-DATA-003 | Resolve conflicting demographic fields during merge by explicit source selection | P1 | High | 4 |
| REQ-DATA-004 | Maintain referential integrity (visits, orders, relationships, providers) and concurrency safety on merge | P1 | High | 4 |
| REQ-DATA-005 | Capture immutable audit metadata for merge and void operations | P1 | High | 2 |
| REQ-DATA-006 | Soft-delete (void) a patient with mandatory reason and exclude from standard search/reports | P1 | High | 4 |
| REQ-DATA-007 | Restore (unvoid) a voided patient and recover associated clinical data | P1 | High | 2 |
| REQ-DATA-008 | Differentiate reversible void from irreversible purge with a valid lifecycle state model | P1 | High | 2 |
| REQ-DATA-009 | Enforce patient identifier uniqueness, format, check-digit, length and normalization | P1 | High | 5 |
| REQ-DATA-010 | Support safe bulk data operations with partial-failure handling and batch limits | P2 | Medium | 4 |
| REQ-DATA-011 | Protect against orphaning dependent clinical records on hard delete | P1 | High | 1 |
| REQ-DATA-012 | Restrict destructive data operations to privileged roles | P1 | High | 2 |
| REQ-DATA-013 | Reflect void and merge state correctly in FHIR Patient resources | P1 | High | 2 |
| REQ-DATA-014 | Handle HL7 v2 ADT merge and delete messages per documented policy | P2 | Medium | 2 |
| REQ-DATA-015 | Provide accessible (WCAG 2.1 AA) data management screens | P3 | Medium | 1 |
| REQ-DATA-016 | Provide consistent merge semantics across backends via the Resource Adapter Layer | P2 | Medium | 1 |
| REQ-DATA-017 | Enforce data-retention and legal-hold blocks on purge | P2 | Medium | 1 |

## Data Quality & Migration — 29 requirements, 56 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-DATAQ-001 | Duplicate detection via deterministic and probabilistic matching | P1 | High | 2 |
| REQ-DATAQ-002 | Merge eligibility, thresholds and false-merge prevention | P1 | High | 3 |
| REQ-DATAQ-003 | Survivorship/golden-record consolidation rules | P2 | High | 1 |
| REQ-DATAQ-004 | Completeness checks and load completeness gate | P1 | High | 2 |
| REQ-DATAQ-005 | Validity checks (range, value set, format, check-digit) | P1 | High | 5 |
| REQ-DATAQ-006 | Cross-field consistency rules | P1 | High | 1 |
| REQ-DATAQ-007 | Referential integrity and FK/orphan enforcement | P1 | High | 3 |
| REQ-DATAQ-008 | Unit-of-measure normalization consistency | P2 | High | 1 |
| REQ-DATAQ-009 | Row-count reconciliation source-to-target | P1 | High | 2 |
| REQ-DATAQ-010 | Field-level/checksum and sampled reconciliation | P1 | High | 2 |
| REQ-DATAQ-011 | Transformation rules (dates, nulls, truncation guards) | P2 | Medium | 4 |
| REQ-DATAQ-012 | Code-system and concept mapping | P1 | High | 2 |
| REQ-DATAQ-013 | Identifier remapping and key crosswalk integrity | P1 | High | 1 |
| REQ-DATAQ-014 | Character-encoding fidelity (UTF-8/utf8mb4) | P1 | High | 3 |
| REQ-DATAQ-015 | Delimited-file parsing robustness | P2 | Medium | 1 |
| REQ-DATAQ-016 | Atomic rollback and batch state model | P1 | High | 3 |
| REQ-DATAQ-017 | Idempotent re-run after failure | P1 | High | 1 |
| REQ-DATAQ-018 | Post-migration validation (smoke, aggregates, latest-obs) | P1 | High | 3 |
| REQ-DATAQ-019 | FHIR resource validity and reference resolution post-migration | P1 | High | 2 |
| REQ-DATAQ-020 | HL7 v2 migration ingestion and mapping | P2 | High | 2 |
| REQ-DATAQ-021 | Data profiling and anomaly surfacing | P3 | Medium | 1 |
| REQ-DATAQ-022 | Uniqueness/natural-key enforcement | P1 | High | 1 |
| REQ-DATAQ-023 | Migration security: PHI masking and least privilege | P1 | High | 2 |
| REQ-DATAQ-024 | ETL/migration REST API for validation results | P2 | Medium | 1 |
| REQ-DATAQ-025 | Timezone/DST normalization of timestamps | P2 | High | 2 |
| REQ-DATAQ-026 | Incremental/delta and late-arriving load handling | P2 | High | 2 |
| REQ-DATAQ-027 | Cross-platform pairwise migration coverage | P3 | Medium | 1 |
| REQ-DATAQ-028 | Data lineage and audit trail | P2 | High | 1 |
| REQ-DATAQ-029 | Cutover sign-off gate | P1 | High | 1 |

## Death & Record Lifecycle — 17 requirements, 44 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-DEATH-001 | Mark patient deceased with coded/free-text cause and Dead flag handling | P1 | High | 5 |
| REQ-DEATH-002 | Death date validation and boundary rules (future/birth/today/calendar) | P1 | High | 5 |
| REQ-DEATH-003 | Reverse deceased status with downstream reconciliation and concurrency safety | P1 | High | 4 |
| REQ-DEATH-004 | Downstream effects on appointments and visits at death | P1 | High | 3 |
| REQ-DEATH-005 | Downstream effects on active orders at death | P1 | High | 2 |
| REQ-DEATH-006 | Void/unvoid patient with cascade and audit columns | P1 | High | 5 |
| REQ-DEATH-007 | Retire metadata vs void data semantics | P2 | Medium | 2 |
| REQ-DEATH-008 | Archival and retention policy enforcement with purge boundaries | P1 | High | 3 |
| REQ-DEATH-009 | Immutable, complete audit trail of death lifecycle | P1 | High | 2 |
| REQ-DEATH-010 | RBAC and privilege-escalation protection for lifecycle actions | P1 | High | 2 |
| REQ-DEATH-011 | REST API deceased set/validation behavior | P2 | Medium | 2 |
| REQ-DEATH-012 | FHIR R4 Patient.deceased[x] projection consistency | P1 | High | 3 |
| REQ-DEATH-013 | HL7 v2 ADT death message inbound/outbound mapping and idempotency | P2 | Medium | 2 |
| REQ-DEATH-014 | Accessibility of mark-deceased dialog (WCAG 2.1 AA) | P3 | Medium | 1 |
| REQ-DEATH-015 | Consistent prominent deceased indicator across views | P2 | Medium | 1 |
| REQ-DEATH-016 | Billing downstream handling on patient death | P3 | Medium | 1 |
| REQ-DEATH-017 | Post-mortem PHI/privacy and minimum-necessary enforcement | P2 | High | 1 |

## Drug Orders & Dispensing (deep) — 33 requirements, 76 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-DRUG-001 | Coded drug selection and drug search | P1 | High | 3 |
| REQ-DRUG-002 | Free-text (non-coded) drug ordering with config gating | P2 | Medium | 2 |
| REQ-DRUG-003 | Dose value validation and boundaries | P1 | High | 5 |
| REQ-DRUG-004 | Dose units and route compatibility | P2 | Medium | 1 |
| REQ-DRUG-005 | Frequency, duration and continuous order handling | P2 | Medium | 4 |
| REQ-DRUG-006 | Route requirement enforcement | P2 | Medium | 1 |
| REQ-DRUG-007 | PRN (as-needed) ordering with reason | P2 | Medium | 2 |
| REQ-DRUG-008 | Taper/titration sequential regimen scheduling | P2 | Medium | 2 |
| REQ-DRUG-009 | Discontinue and renew/reorder lifecycle | P1 | High | 3 |
| REQ-DRUG-010 | Revise order with linkage and concurrency control | P1 | High | 3 |
| REQ-DRUG-011 | Order state machine integrity and date validation | P1 | High | 2 |
| REQ-DRUG-012 | Substitution (generic/DAW/policy) controls | P2 | Medium | 3 |
| REQ-DRUG-013 | Full dispense workflow and gating on order state | P1 | High | 2 |
| REQ-DRUG-014 | Partial dispense and cumulative quantity limits | P1 | High | 3 |
| REQ-DRUG-015 | Refill authorization and decrement logic | P2 | Medium | 3 |
| REQ-DRUG-016 | Allergy alerting and override capture | P1 | High | 3 |
| REQ-DRUG-017 | Drug-drug interaction severity decision behavior | P1 | High | 3 |
| REQ-DRUG-018 | Duplicate and therapeutic-class duplication alerts | P2 | Medium | 2 |
| REQ-DRUG-019 | Dose-range, renal, pediatric and pregnancy safety checks | P1 | High | 3 |
| REQ-DRUG-020 | Controlled substance ordering controls | P1 | High | 2 |
| REQ-DRUG-021 | RBAC for ordering and dispensing privileges | P1 | High | 2 |
| REQ-DRUG-022 | REST API order create/read and auth validation | P1 | High | 3 |
| REQ-DRUG-023 | FHIR MedicationRequest representation and round-trip | P1 | Medium | 3 |
| REQ-DRUG-024 | FHIR MedicationDispense representation | P2 | Medium | 1 |
| REQ-DRUG-025 | HL7 v2 pharmacy order/dispense messaging | P2 | Medium | 3 |
| REQ-DRUG-026 | Database immutability, voiding and uniqueness | P2 | Medium | 3 |
| REQ-DRUG-027 | Accessibility of order entry and alerts | P2 | Medium | 2 |
| REQ-DRUG-028 | Security: injection, scope and access control | P2 | Medium | 2 |
| REQ-DRUG-029 | Audit logging of order lifecycle actions | P1 | High | 1 |
| REQ-DRUG-030 | Exploratory robustness of order workflows | P3 | Medium | 1 |
| REQ-DRUG-031 | Wrong-patient safeguards during ordering | P1 | High | 1 |
| REQ-DRUG-032 | Billing charge generation on dispense | P3 | Medium | 1 |
| REQ-DRUG-033 | Order sets / regimen template application | P2 | Medium | 1 |

## End-to-End Patient Journeys — 75 requirements, 75 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-E2E2-001 | Complete outpatient register-to-checkout journey within single visit context | P1 | High | 1 |
| REQ-E2E2-002 | Prevent concurrent active visits across locations | P2 | Medium | 1 |
| REQ-E2E2-003 | Vitals-to-diagnosis handoff preserves visit and provider context | P2 | Medium | 1 |
| REQ-E2E2-004 | Lab order lifecycle state transitions ordered-to-completed with review | P1 | High | 1 |
| REQ-E2E2-005 | Allergy interaction alert before order finalization with audited override | P1 | High | 1 |
| REQ-E2E2-006 | Dispense quantity bounded by prescribed quantity | P2 | Medium | 1 |
| REQ-E2E2-007 | Billing aggregates all chargeable visit items | P2 | Medium | 1 |
| REQ-E2E2-008 | Checkout closes visit and finalizes encounter datetimes | P2 | Medium | 1 |
| REQ-E2E2-009 | Inpatient admission from outpatient visit with bed assignment | P1 | High | 1 |
| REQ-E2E2-010 | Prevent bed double-assignment under concurrency | P1 | High | 1 |
| REQ-E2E2-011 | Inpatient scheduled medication administration and MAR | P2 | Medium | 1 |
| REQ-E2E2-012 | Ward transfer preserves orders and visit | P2 | Medium | 1 |
| REQ-E2E2-013 | Discharge gating on unresolved active orders | P1 | High | 1 |
| REQ-E2E2-014 | Discharge summary generation and locking | P2 | Medium | 1 |
| REQ-E2E2-015 | Length-of-stay computation boundaries | P3 | Low | 1 |
| REQ-E2E2-016 | ER triage acuity assignment and routing | P1 | High | 1 |
| REQ-E2E2-017 | Triage queue ordering by acuity then arrival | P2 | Medium | 1 |
| REQ-E2E2-018 | Unidentified emergency patient placeholder and reconciliation | P2 | Medium | 1 |
| REQ-E2E2-019 | ER-to-inpatient conversion within single visit | P2 | Medium | 1 |
| REQ-E2E2-020 | LWBS disposition closes visit and reports metric | P3 | Medium | 1 |
| REQ-E2E2-021 | Outbound referral creation with reason and target | P2 | Medium | 1 |
| REQ-E2E2-022 | Referral lifecycle state transitions | P2 | Medium | 1 |
| REQ-E2E2-023 | Referral rejection re-routing with audit | P3 | Medium | 1 |
| REQ-E2E2-024 | FHIR ServiceRequest represents referral | P2 | Medium | 1 |
| REQ-E2E2-025 | Visit-encounter referential integrity on void | P2 | Medium | 1 |
| REQ-E2E2-026 | Critical lab value auto-flag and provider notification | P1 | High | 1 |
| REQ-E2E2-027 | Result entry blocked for cancelled order | P2 | Medium | 1 |
| REQ-E2E2-028 | Pairwise visit/location/role/order-type authorization | P2 | Medium | 1 |
| REQ-E2E2-029 | Duplicate diagnosis dedupe/warn on same visit | P3 | Low | 1 |
| REQ-E2E2-030 | Encounter datetime bounded by visit window | P2 | Medium | 1 |
| REQ-E2E2-031 | Concurrent encounter edit conflict prevention | P2 | Medium | 1 |
| REQ-E2E2-032 | FHIR Encounter status maps to visit lifecycle | P2 | Medium | 1 |
| REQ-E2E2-033 | HL7 ADT event sequence for journey | P2 | Medium | 1 |
| REQ-E2E2-034 | HL7 lab order/result round-trip matching | P2 | Medium | 1 |
| REQ-E2E2-035 | FHIR MedicationRequest-to-Dispense linkage | P2 | Medium | 1 |
| REQ-E2E2-036 | FHIR DiagnosticReport links Observations to order | P3 | Medium | 1 |
| REQ-E2E2-037 | Cross-patient IDOR prevention | P1 | High | 1 |
| REQ-E2E2-038 | Break-the-glass emergency access audited and time-boxed | P2 | High | 1 |
| REQ-E2E2-039 | End-to-end audit trail reconstruction | P2 | High | 1 |
| REQ-E2E2-040 | Age-appropriate vitals boundary validation | P2 | Medium | 1 |
| REQ-E2E2-041 | Weight-based pediatric dosing guardrail | P1 | High | 1 |
| REQ-E2E2-042 | Drug-drug interaction alert across active prescriptions | P1 | High | 1 |
| REQ-E2E2-043 | Duplicate lab order detection window | P3 | Medium | 1 |
| REQ-E2E2-044 | Nurse handover continuity across shifts | P3 | Low | 1 |
| REQ-E2E2-045 | Diet/NPO order drives meal scheduling | P3 | Medium | 1 |
| REQ-E2E2-046 | Multi-encounter vitals trend aggregation | P3 | Low | 1 |
| REQ-E2E2-047 | Mass-casualty rapid registration throughput | P3 | Medium | 1 |
| REQ-E2E2-048 | Acuity-driven reassessment intervals | P3 | Medium | 1 |
| REQ-E2E2-049 | Inbound referral seeds patient and visit | P3 | Medium | 1 |
| REQ-E2E2-050 | Referral required-field validation | P3 | Low | 1 |
| REQ-E2E2-051 | Ordering provider attribution on bill vs data entry user | P2 | Medium | 1 |
| REQ-E2E2-052 | Insurance eligibility drives billing split | P2 | Medium | 1 |
| REQ-E2E2-053 | Patient merge consolidates clinical data without loss | P2 | High | 1 |
| REQ-E2E2-054 | Timezone/DST/midnight consistency across encounters | P3 | Medium | 1 |
| REQ-E2E2-055 | FHIR transaction Bundle atomicity | P3 | Medium | 1 |
| REQ-E2E2-056 | FHIR Patient $everything scoped by authorization | P3 | Medium | 1 |
| REQ-E2E2-057 | Malformed HL7 message rejected and NACKed | P2 | Medium | 1 |
| REQ-E2E2-058 | Session timeout protects unsaved PHI | P2 | Medium | 1 |
| REQ-E2E2-059 | Server-side privilege enforcement on order signing | P1 | High | 1 |
| REQ-E2E2-060 | Keyboard-only journey completion | P2 | Medium | 1 |
| REQ-E2E2-061 | Screen reader announces critical alerts | P2 | Medium | 1 |
| REQ-E2E2-062 | REST API drives full journey | P2 | Medium | 1 |
| REQ-E2E2-063 | API rejects order on ended visit | P2 | Medium | 1 |
| REQ-E2E2-064 | DB referential integrity of obs to concept/encounter/person | P3 | Medium | 1 |
| REQ-E2E2-065 | Diagnosis-driven follow-up appointment suggestion | P3 | Low | 1 |
| REQ-E2E2-066 | Result-pending checkout warning | P2 | Medium | 1 |
| REQ-E2E2-067 | External transfer-out disposition closes visit | P3 | Medium | 1 |
| REQ-E2E2-068 | Deceased patient pathway stops active orders | P2 | High | 1 |
| REQ-E2E2-069 | Back-navigation safety in multi-step order entry | P3 | Medium | 1 |
| REQ-E2E2-070 | Pairwise insurance/service/network billing outcomes | P3 | Medium | 1 |
| REQ-E2E2-071 | Consent gates referral data sharing | P2 | High | 1 |
| REQ-E2E2-072 | Pregnancy contraindication alert blocks order | P1 | High | 1 |
| REQ-E2E2-073 | Renal-adjusted dosing prompt | P2 | High | 1 |
| REQ-E2E2-074 | Smoke check of core journey endpoints | P1 | Medium | 1 |
| REQ-E2E2-075 | Lab interface outage queuing and retry | P2 | High | 1 |

## Encounters & Clinical Forms — 39 requirements, 78 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-ENC-001 | Encounter type selection and type-specific defaults | P2 | Medium | 3 |
| REQ-ENC-002 | Encounter type list filtered by privilege | P2 | Medium | 1 |
| REQ-ENC-010 | Multi-section clinical form entry | P2 | Medium | 1 |
| REQ-ENC-011 | Form-level required observation validation | P1 | High | 2 |
| REQ-ENC-012 | Numeric field range/precision/format validation | P1 | High | 5 |
| REQ-ENC-013 | Coded answer set enforcement | P2 | Medium | 4 |
| REQ-ENC-014 | Obs group repeating/nesting integrity | P2 | Medium | 5 |
| REQ-ENC-015 | Conditional skip-logic fields | P2 | Medium | 3 |
| REQ-ENC-016 | Cross-field, date, length and datatype validation | P2 | Medium | 4 |
| REQ-ENC-017 | Concurrent encounter edit handling | P1 | High | 1 |
| REQ-ENC-018 | Unsaved-change and session-timeout protection | P2 | Medium | 2 |
| REQ-ENC-019 | Duplicate submission prevention | P2 | Medium | 1 |
| REQ-ENC-020 | Encounter/observation edit and versioning | P2 | Medium | 3 |
| REQ-ENC-021 | Edit/type-change audit trail | P1 | High | 2 |
| REQ-ENC-022 | Edit privilege enforcement | P1 | High | 1 |
| REQ-ENC-023 | Void encounter with mandatory reason | P1 | High | 2 |
| REQ-ENC-024 | Void cascade and exclusion from aggregations | P2 | High | 2 |
| REQ-ENC-025 | Unvoid/restore capability | P2 | Medium | 1 |
| REQ-ENC-030 | Provider-to-encounter-role assignment | P2 | Medium | 3 |
| REQ-ENC-031 | Provider requiredness and retired-provider exclusion | P2 | Medium | 2 |
| REQ-ENC-040 | Retrospective encounter entry and visit binding | P2 | Medium | 3 |
| REQ-ENC-041 | Encounter datetime visit-window and future-date bounds | P1 | High | 2 |
| REQ-ENC-042 | Retrospective entry privilege enforcement | P2 | Medium | 1 |
| REQ-ENC-050 | Free-text injection neutralization | P1 | High | 1 |
| REQ-ENC-051 | Coded concept tamper resistance | P1 | High | 1 |
| REQ-ENC-052 | Patient-scope authorization and unauthenticated access control | P1 | High | 2 |
| REQ-ENC-060 | REST encounter creation | P2 | Medium | 1 |
| REQ-ENC-061 | REST encounter create validation (type/range/future) | P2 | Medium | 3 |
| REQ-ENC-062 | REST encounter void | P2 | Medium | 1 |
| REQ-ENC-070 | FHIR Encounter resource read | P2 | Medium | 1 |
| REQ-ENC-071 | FHIR Encounter status/participant mapping | P2 | Medium | 2 |
| REQ-ENC-072 | FHIR Observation and hasMember mapping | P2 | Medium | 2 |
| REQ-ENC-080 | HL7 inbound encounter/observation creation | P2 | Medium | 1 |
| REQ-ENC-081 | HL7 unmappable-code error handling | P2 | Medium | 1 |
| REQ-ENC-090 | Encounter/provider/obs database persistence integrity | P2 | Medium | 3 |
| REQ-ENC-100 | Keyboard operability of clinical forms | P2 | Medium | 1 |
| REQ-ENC-101 | Accessible names, ARIA and error announcement | P2 | Medium | 2 |
| REQ-ENC-102 | Cross-browser/zoom form rendering | P3 | Low | 1 |
| REQ-ENC-110 | Encounter lifecycle audit for compliance | P1 | High | 1 |

## FHIR R4 API — 34 requirements, 90 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-FHIR-001 | Server exposes a conformant CapabilityStatement at /metadata listing supported resources and interactions | P1 | High | 2 |
| REQ-FHIR-002 | Server declares and conforms to HL7 FHIR R4 (4.0.1) | P1 | High | 1 |
| REQ-FHIR-003 | Server supports application/fhir+json content negotiation and _format override | P1 | Medium | 3 |
| REQ-FHIR-004 | Patient resource is readable by id | P1 | High | 1 |
| REQ-FHIR-005 | Reads of missing or malformed ids return 404/400 with OperationOutcome, never 500 | P1 | High | 3 |
| REQ-FHIR-006 | Patient resource supports create with FHIR cardinality rules | P1 | High | 3 |
| REQ-FHIR-007 | Create validation enforces required fields, value sets, parse and type integrity | P1 | High | 5 |
| REQ-FHIR-008 | Patient supports PUT update with URL/body id consistency | P1 | High | 2 |
| REQ-FHIR-009 | Patient supports delete with correct post-delete read semantics | P2 | Medium | 2 |
| REQ-FHIR-010 | Patient search supports _id, identifier, name, family, birthdate, gender and combined params | P1 | Medium | 10 |
| REQ-FHIR-011 | Search parameters support prefixes and boundary/empty value handling | P2 | Medium | 2 |
| REQ-FHIR-012 | Search returns RFC-conformant searchset Bundles with pagination links and _count | P2 | Medium | 5 |
| REQ-FHIR-013 | Encounter resource read, search and status validation | P1 | Medium | 4 |
| REQ-FHIR-014 | Reference integrity enforced across resources (no dangling/phantom references) | P1 | High | 2 |
| REQ-FHIR-015 | Observation resource read and search by patient/code/category | P1 | Medium | 3 |
| REQ-FHIR-016 | Observation create enforces required fields and FHIR invariants | P1 | High | 4 |
| REQ-FHIR-017 | Condition resource read and search | P2 | Medium | 2 |
| REQ-FHIR-018 | Condition create and clinicalStatus state transitions | P2 | Medium | 2 |
| REQ-FHIR-019 | AllergyIntolerance resource read and search by patient | P1 | High | 2 |
| REQ-FHIR-020 | AllergyIntolerance create with criticality validation for patient safety | P1 | High | 2 |
| REQ-FHIR-021 | MedicationRequest resource read and search | P1 | Medium | 2 |
| REQ-FHIR-022 | MedicationRequest create, validation and status state transitions | P1 | High | 3 |
| REQ-FHIR-023 | Unauthenticated/invalid-token requests are rejected with 401 and no PHI | P1 | High | 2 |
| REQ-FHIR-024 | Authorization enforces privilege-based access with 403 on insufficient rights | P1 | High | 2 |
| REQ-FHIR-025 | API is hardened against injection, path traversal and PHI leakage in errors/logs | P1 | High | 3 |
| REQ-FHIR-026 | PHI access generates audit log entries for HIPAA traceability | P1 | High | 1 |
| REQ-FHIR-027 | Resources persist durably and concurrent updates use optimistic locking | P2 | Medium | 2 |
| REQ-FHIR-028 | Transaction bundles are atomic with full rollback on failure | P2 | Medium | 1 |
| REQ-FHIR-029 | All error responses return OperationOutcome and unsupported methods handled gracefully | P2 | Medium | 4 |
| REQ-FHIR-030 | Server supports result-shaping params (_summary, _lastUpdated, _sort, _revinclude) | P3 | Low | 4 |
| REQ-FHIR-031 | Resource versioning/_history supported per CapabilityStatement | P3 | Low | 1 |
| REQ-FHIR-032 | Resource Adapter Layer exposes OpenEMR/HAPI/omiiCARE backends as conformant FHIR R4 | P2 | Medium | 3 |
| REQ-FHIR-033 | HL7 v2 ingest is correctly mapped into FHIR resources | P2 | Medium | 1 |
| REQ-FHIR-034 | US Core profile must-support elements satisfied where asserted | P2 | Medium | 1 |

## FHIR R4 (deep: search/bundle/operations) — 43 requirements, 96 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-FHIR2-001 | Patient name search (family/given, modifiers) | P2 | Medium | 2 |
| REQ-FHIR2-002 | Patient identifier token search (system\|value, value-only) | P1 | High | 2 |
| REQ-FHIR2-003 | Patient demographic search (birthdate, gender, active) incl boundary/decision-table | P2 | Medium | 2 |
| REQ-FHIR2-004 | _id common search parameter | P1 | Medium | 2 |
| REQ-FHIR2-005 | _lastUpdated date range and precision search | P2 | Medium | 2 |
| REQ-FHIR2-006 | _sort single/multi-key and unsupported-param handling | P2 | Medium | 3 |
| REQ-FHIR2-007 | _count paging size, boundaries, capping, validation | P2 | Medium | 4 |
| REQ-FHIR2-008 | Bundle paging links (self/next/previous/first) and stale links | P1 | High | 3 |
| REQ-FHIR2-009 | _include and _revinclude resolution | P2 | Medium | 4 |
| REQ-FHIR2-010 | Chained and reverse-chained (_has) search | P2 | Medium | 3 |
| REQ-FHIR2-011 | Token search (system\|code, :not, :text, code-only) | P1 | High | 4 |
| REQ-FHIR2-012 | Date search prefixes, partial precision, malformed values | P2 | Medium | 3 |
| REQ-FHIR2-013 | Quantity search with comparator and UCUM units | P3 | Medium | 1 |
| REQ-FHIR2-014 | Reference search (typed and id-only) | P2 | Medium | 2 |
| REQ-FHIR2-015 | Composite search parameters | P3 | Medium | 1 |
| REQ-FHIR2-016 | _summary and _elements result shaping | P3 | Medium | 2 |
| REQ-FHIR2-017 | Invalid/unsupported param and AND/OR handling (strict/lenient) | P2 | Medium | 4 |
| REQ-FHIR2-018 | Instance read, vread, conditional read, deleted/not-found semantics | P1 | High | 5 |
| REQ-FHIR2-019 | History (type/instance _history, _since, lifecycle chain) | P2 | Medium | 3 |
| REQ-FHIR2-020 | Conditional create (If-None-Exist) outcomes | P2 | Medium | 3 |
| REQ-FHIR2-021 | Conditional update outcomes and conflicts | P2 | Medium | 3 |
| REQ-FHIR2-022 | Optimistic concurrency with If-Match/ETag | P1 | High | 1 |
| REQ-FHIR2-023 | Searchset Bundle well-formedness | P1 | Medium | 1 |
| REQ-FHIR2-024 | Transaction Bundle (refs, atomicity, conditional, circular) | P1 | High | 4 |
| REQ-FHIR2-025 | Batch Bundle independent processing | P2 | Medium | 2 |
| REQ-FHIR2-026 | $everything compartment operation and authorization | P1 | High | 3 |
| REQ-FHIR2-027 | Operations $validate/$lastn/$expand | P2 | Medium | 3 |
| REQ-FHIR2-028 | OperationOutcome structure and diagnostics | P2 | Medium | 2 |
| REQ-FHIR2-029 | US Core profile conformance (Patient/vitals/extensions) | P2 | High | 3 |
| REQ-FHIR2-030 | CapabilityStatement and declared-vs-actual conformance | P1 | Medium | 2 |
| REQ-FHIR2-031 | Content negotiation (_format/Accept) and 406 handling | P3 | Low | 2 |
| REQ-FHIR2-032 | Authentication enforcement on FHIR endpoint | P1 | High | 1 |
| REQ-FHIR2-033 | SMART scope enforcement | P1 | High | 1 |
| REQ-FHIR2-034 | Injection (SQLi/XSS) neutralization in search params | P1 | High | 1 |
| REQ-FHIR2-035 | No PHI leakage in error responses | P2 | High | 1 |
| REQ-FHIR2-036 | Audit/history record on FHIR writes | P2 | Medium | 1 |
| REQ-FHIR2-037 | Reference integrity to backing data model | P2 | Medium | 1 |
| REQ-FHIR2-038 | API docs accessibility (WCAG) | P4 | Low | 1 |
| REQ-FHIR2-039 | Search/deep-paging performance SLA | P2 | Medium | 1 |
| REQ-FHIR2-040 | Exploratory combined complex query behavior | P3 | Medium | 1 |
| REQ-FHIR2-041 | HL7 v2 (ADT/ORU) to FHIR mapping | P3 | Medium | 2 |
| REQ-FHIR2-042 | Cross-resource API search (MedicationRequest, unsupported types) | P2 | Medium | 2 |
| REQ-FHIR2-043 | Conditional delete and JSON Patch partial update | P3 | Medium | 2 |

## HL7 v2 Messaging — 33 requirements, 57 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-HL7-001 | Process valid ADT^A01 admit and create inpatient encounter | P1 | High | 1 |
| REQ-HL7-002 | Process valid ADT^A03 discharge and close active encounter | P1 | High | 1 |
| REQ-HL7-003 | Process valid ADT^A04 register and create patient if absent | P1 | Medium | 1 |
| REQ-HL7-004 | Process valid ADT^A08 update and merge demographics without duplication | P1 | Medium | 1 |
| REQ-HL7-005 | Process valid ORM^O01 new order and create pending order | P1 | Medium | 1 |
| REQ-HL7-006 | Process valid ORU^R01 result and file OBX observations with flags | P1 | High | 2 |
| REQ-HL7-007 | Validate required MSH header fields (MSH-9 type, MSH-10 control ID) | P1 | High | 2 |
| REQ-HL7-008 | Enforce required PID-3 patient identifier | P1 | High | 1 |
| REQ-HL7-009 | Validate PV1 patient class against HL7 table 0004 | P2 | Medium | 1 |
| REQ-HL7-010 | Honor and validate field separators and encoding characters (MSH-1/MSH-2) | P2 | Medium | 3 |
| REQ-HL7-011 | Generate correct ACK/NAK (AA/AE/AR) with proper MSA echo | P1 | High | 4 |
| REQ-HL7-012 | Reject malformed, truncated, or mis-ordered messages safely | P1 | High | 2 |
| REQ-HL7-013 | Sanitize control/non-printable characters in inbound data | P2 | Medium | 1 |
| REQ-HL7-014 | Detect and suppress duplicate messages while allowing valid retransmission | P1 | High | 2 |
| REQ-HL7-015 | Map PID identifiers via assigning authority to correct internal identity | P1 | High | 3 |
| REQ-HL7-016 | Parse and validate PID demographics (name, DOB, sex, null handling) | P2 | Medium | 4 |
| REQ-HL7-017 | Parse and validate TS datetime fields with timezone offsets | P2 | Medium | 2 |
| REQ-HL7-018 | Enforce required OBR-4 universal service identifier | P1 | High | 1 |
| REQ-HL7-019 | Validate OBX value type/content and handle coded and repeating results | P2 | Medium | 3 |
| REQ-HL7-020 | Enforce valid ADT encounter state transitions and cancellations | P1 | High | 3 |
| REQ-HL7-021 | Handle out-of-order and order/result message sequencing correctly | P2 | High | 2 |
| REQ-HL7-022 | Enforce correct MLLP framing (VT/FS/CR) | P2 | Medium | 1 |
| REQ-HL7-023 | Secure the inbound channel (mutual TLS, sender allowlist) | P1 | High | 2 |
| REQ-HL7-024 | Prevent injection via HL7 escape sequences on downstream render | P2 | Medium | 1 |
| REQ-HL7-025 | Produce immutable audit entries for processed messages | P1 | High | 1 |
| REQ-HL7-026 | Guarantee transactional integrity and reliable retry on failure | P1 | High | 2 |
| REQ-HL7-027 | Handle segment/field length boundaries gracefully | P2 | Medium | 1 |
| REQ-HL7-028 | Honor declared character set (MSH-18) for extended characters | P2 | Medium | 1 |
| REQ-HL7-029 | Support multiple HL7 versions and reject unsupported ones | P2 | Medium | 2 |
| REQ-HL7-030 | Remain robust and deterministic under adversarial/exploratory inputs | P2 | Medium | 1 |
| REQ-HL7-031 | Honor MSH-11 processing ID to isolate test from production data | P1 | High | 1 |
| REQ-HL7-032 | Process ADT^A40 patient merge and reconcile duplicate identities | P1 | High | 1 |
| REQ-HL7-033 | Map HL7 messages to target systems via the Resource Adapter Layer (FHIR/platform) | P2 | Medium | 2 |

## HL7 v2 (deep) — 58 requirements, 66 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-HL7v2-001 | ADT A04 register new patient from PID/PV1 | P1 | High | 1 |
| REQ-HL7v2-002 | ADT A08 update demographics in place | P1 | High | 1 |
| REQ-HL7v2-003 | ADT A40 merge via MRG with validation | P1 | High | 2 |
| REQ-HL7v2-004 | ADT A02 transfer updates location | P2 | Medium | 1 |
| REQ-HL7v2-005 | ADT A03 discharge closes visit | P1 | High | 1 |
| REQ-HL7v2-006 | ADT A11 cancel admit reversal | P2 | Medium | 1 |
| REQ-HL7v2-007 | ADT A12 cancel transfer reversal | P2 | Medium | 1 |
| REQ-HL7v2-008 | ADT A13 cancel discharge reactivation | P2 | Medium | 1 |
| REQ-HL7v2-009 | ADT A05 pre-admit pending visit | P3 | Medium | 1 |
| REQ-HL7v2-010 | MSH-9 required validation | P1 | High | 1 |
| REQ-HL7v2-011 | MSH-1/MSH-2 delimiter and encoding parsing | P2 | Medium | 2 |
| REQ-HL7v2-012 | MSH-12 version negotiation | P2 | Medium | 1 |
| REQ-HL7v2-013 | Generate AA acknowledgment | P1 | High | 1 |
| REQ-HL7v2-014 | Generate AE on business validation failure | P1 | High | 1 |
| REQ-HL7v2-015 | Generate AR on malformed message | P1 | High | 1 |
| REQ-HL7v2-016 | Enhanced ack mode MSH-15/16 decision table | P2 | Medium | 1 |
| REQ-HL7v2-017 | PID-7 DOB TS datatype boundaries | P2 | Medium | 1 |
| REQ-HL7v2-018 | PID-8 sex table 0001 validation | P3 | Medium | 1 |
| REQ-HL7v2-019 | PID-3 repeating identifiers and required MRN | P1 | High | 2 |
| REQ-HL7v2-020 | PID-5 XPN name component mapping | P3 | Low | 1 |
| REQ-HL7v2-021 | PID-11 repeating address with type codes | P3 | Low | 1 |
| REQ-HL7v2-022 | EVN datetime and event type consistency | P3 | Medium | 2 |
| REQ-HL7v2-023 | PV1-2 patient class table 0004 | P2 | Medium | 1 |
| REQ-HL7v2-024 | PV1-7 attending provider mapping | P2 | Medium | 1 |
| REQ-HL7v2-025 | PV1-19 visit number correlation | P1 | High | 1 |
| REQ-HL7v2-026 | ORM O01 new order creation | P1 | High | 1 |
| REQ-HL7v2-027 | ORC cancel order transition | P1 | High | 1 |
| REQ-HL7v2-028 | ORC-5 status and ORC-9 datetime | P2 | Medium | 1 |
| REQ-HL7v2-029 | ORU R01 result filing and correction | P1 | High | 2 |
| REQ-HL7v2-030 | OBX-2 value type drives OBX-5 datatype | P2 | Medium | 2 |
| REQ-HL7v2-031 | OBX-8/OBX-11 flags and status mapping | P2 | Medium | 1 |
| REQ-HL7v2-032 | OBR-4 universal service identifier mapping | P2 | Medium | 1 |
| REQ-HL7v2-033 | OBR-7/OBR-22 datetime validation | P3 | Low | 1 |
| REQ-HL7v2-034 | SIU S12 new appointment booking | P2 | Medium | 1 |
| REQ-HL7v2-035 | SIU S14 appointment modification | P3 | Medium | 1 |
| REQ-HL7v2-036 | SIU S15 appointment cancellation | P3 | Medium | 1 |
| REQ-HL7v2-037 | MDM T02 original document notification | P2 | Medium | 1 |
| REQ-HL7v2-038 | MDM T06 document addendum | P3 | Medium | 1 |
| REQ-HL7v2-039 | TXA completion/availability status mapping | P3 | Low | 1 |
| REQ-HL7v2-040 | DFT P03 post financial charge | P2 | Medium | 1 |
| REQ-HL7v2-041 | FT1 transaction type and MO amount datatype | P3 | Medium | 1 |
| REQ-HL7v2-042 | Batch BHS/BTS processing and count validation | P2 | Medium | 2 |
| REQ-HL7v2-043 | File FHS/FTS multi-batch handling | P3 | Low | 1 |
| REQ-HL7v2-044 | Duplicate detection by MSH-10 | P1 | High | 1 |
| REQ-HL7v2-045 | MSH-13 sequence number gap handling | P2 | Medium | 1 |
| REQ-HL7v2-046 | Z-segment tolerance and mapping | P3 | Low | 2 |
| REQ-HL7v2-047 | HL7 escape sequence resolution | P2 | Medium | 1 |
| REQ-HL7v2-048 | MSH-18 character set handling | P3 | Medium | 1 |
| REQ-HL7v2-049 | MLLP framing enforcement | P2 | Medium | 1 |
| REQ-HL7v2-050 | MLLP TLS-required enforcement | P1 | High | 1 |
| REQ-HL7v2-051 | Injection sanitization in free-text fields | P2 | Medium | 1 |
| REQ-HL7v2-052 | Inbound message and ack audit logging | P1 | High | 1 |
| REQ-HL7v2-053 | HL7 ADT to FHIR Patient parity | P2 | Medium | 1 |
| REQ-HL7v2-054 | HL7 OBX to FHIR Observation code system | P2 | Medium | 1 |
| REQ-HL7v2-055 | Error-queue routing without payload loss | P1 | High | 1 |
| REQ-HL7v2-056 | Large multi-OBX ORU handling | P3 | Medium | 1 |
| REQ-HL7v2-057 | Field length boundary handling | P3 | Low | 1 |
| REQ-HL7v2-058 | Pairwise routing across type/ack/transport | P3 | Medium | 1 |

## Patient Identifiers & MPI — 14 requirements, 64 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-IDENT-001 | Identifier types and IDGEN auto-generation (OpenMRS ID, sequential pool, no reuse) | P1 | Medium | 4 |
| REQ-IDENT-002 | Check-digit and format validation (Luhn mod-30, Verhoeff, regex, length boundaries) | P1 | High | 6 |
| REQ-IDENT-003 | Identifier uniqueness and duplicate prevention (per-type scope, normalization, concurrency, DB constraint) | P1 | High | 7 |
| REQ-IDENT-004 | Multiple identifiers per patient (add/remove, cardinality) | P2 | Medium | 4 |
| REQ-IDENT-005 | Preferred identifier selection and single-preferred invariant | P1 | Medium | 5 |
| REQ-IDENT-006 | National ID format validation (locale masks, checksum, leading zeros) | P2 | Medium | 5 |
| REQ-IDENT-007 | Identifier search (exact/partial, prefix tolerance, sanitization, accessibility) | P1 | Medium | 7 |
| REQ-IDENT-008 | Potential-duplicate detection during registration with audit and scoring | P1 | High | 3 |
| REQ-IDENT-009 | MPI/EMPI match (deterministic, probabilistic, FHIR $match, adapter resolution) | P1 | High | 6 |
| REQ-IDENT-010 | MPI patient merge/unmerge with conflict resolution and audit | P1 | High | 6 |
| REQ-IDENT-011 | RBAC and PHI security for identifier management and merge | P1 | High | 3 |
| REQ-IDENT-012 | FHIR R4 Patient.identifier representation and search | P1 | Medium | 3 |
| REQ-IDENT-013 | HL7 v2 PID-3/MRG identifier ingestion and merge (A04/A40) | P1 | High | 3 |
| REQ-IDENT-014 | REST API identifier create/validation | P2 | Medium | 2 |

## Immunizations — 15 requirements, 65 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-IMM-001 | Age-appropriate vaccine schedule recommendation and series advancement | P1 | High | 5 |
| REQ-IMM-002 | Dose number sequencing, duplicate prevention and series completion | P2 | Medium | 6 |
| REQ-IMM-003 | Vaccine lot number, manufacturer and expiry validation | P1 | High | 5 |
| REQ-IMM-004 | Route and anatomical site capture and clinical coherence | P2 | Medium | 4 |
| REQ-IMM-005 | Contraindication and precaution alerting with audited override | P1 | High | 5 |
| REQ-IMM-006 | Catch-up scheduling with minimum age and interval enforcement | P1 | High | 5 |
| REQ-IMM-007 | Vaccine refusal capture, reason and re-offer lifecycle | P2 | Medium | 4 |
| REQ-IMM-008 | Adverse event following immunization (AEFI) capture, linkage and reporting | P1 | High | 6 |
| REQ-IMM-009 | Immunization registry submission, coverage reporting and de-duplication | P2 | Medium | 3 |
| REQ-IMM-010 | FHIR Immunization resource CRUD, validation and terminology | P1 | High | 7 |
| REQ-IMM-011 | HL7 v2 VXU/QBP immunization messaging and CVX mapping | P2 | Medium | 4 |
| REQ-IMM-012 | Immunization authorization, audit logging and PHI protection | P1 | High | 4 |
| REQ-IMM-013 | Immunization data persistence, soft-delete and referential integrity | P2 | Medium | 3 |
| REQ-IMM-014 | Immunization UI accessibility and alert announcement | P2 | Medium | 2 |
| REQ-IMM-015 | Immunization data consistency under concurrency and timezone boundaries | P3 | Medium | 2 |

## Inpatient / Ward Management — 44 requirements, 62 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-INPT-001 | Inpatient admission via Admission encounter with provider and ward | P1 | High | 3 |
| REQ-INPT-002 | Admission datetime validation against future/visit-start bounds | P2 | Medium | 2 |
| REQ-INPT-003 | Prevent duplicate concurrent admissions | P1 | High | 1 |
| REQ-INPT-004 | ADT state-transition integrity (admit/transfer/discharge) | P1 | High | 4 |
| REQ-INPT-005 | Void/cancel erroneous admission with audit and bed release | P2 | High | 2 |
| REQ-INPT-010 | Bed assignment from available beds | P1 | High | 1 |
| REQ-INPT-011 | Prevent bed double-occupancy under concurrency | P1 | High | 2 |
| REQ-INPT-012 | Bed release on discharge and transfer | P1 | High | 2 |
| REQ-INPT-013 | Gender-restricted ward/bed assignment rules | P2 | Medium | 1 |
| REQ-INPT-014 | Ward capacity enforcement and occupancy counters | P1 | High | 2 |
| REQ-INPT-015 | Ward occupancy dashboard accuracy and non-negative flooring | P3 | Low | 2 |
| REQ-INPT-020 | Ward transfer records source/destination/reason | P1 | High | 2 |
| REQ-INPT-021 | Transfer datetime ordering validation | P2 | Medium | 1 |
| REQ-INPT-030 | Ward round / progress note encounters | P2 | Medium | 2 |
| REQ-INPT-031 | Ward round restricted to active admission | P3 | Low | 1 |
| REQ-INPT-040 | Inpatient orders linked to admission | P1 | High | 1 |
| REQ-INPT-041 | Order discontinue/revise with history chain | P2 | Medium | 2 |
| REQ-INPT-042 | Allergy/interaction alerting on inpatient orders | P1 | High | 1 |
| REQ-INPT-043 | Auto-stop active orders on discharge | P2 | Medium | 1 |
| REQ-INPT-050 | Length-of-stay computation rules | P2 | Medium | 2 |
| REQ-INPT-051 | Current LOS for in-progress admissions | P3 | Low | 1 |
| REQ-INPT-060 | Discharge with disposition and diagnosis | P1 | High | 1 |
| REQ-INPT-061 | Discharge datetime ordering validation | P1 | High | 1 |
| REQ-INPT-062 | Warn/block discharge with active critical orders | P2 | Medium | 1 |
| REQ-INPT-063 | AMA discharge with mandatory reason | P2 | Medium | 1 |
| REQ-INPT-064 | Deceased discharge disposition handling | P2 | Medium | 1 |
| REQ-INPT-070 | Discharge summary required clinical sections | P2 | Medium | 1 |
| REQ-INPT-071 | Mandatory-section enforcement on summary finalization | P2 | Medium | 1 |
| REQ-INPT-072 | Discharge summary amendment via addendum | P3 | Medium | 1 |
| REQ-INPT-073 | Discharge medication reconciliation | P2 | Medium | 1 |
| REQ-INPT-080 | 30-day readmission detection windowing | P2 | Medium | 2 |
| REQ-INPT-090 | RBAC enforcement for admit action | P1 | High | 1 |
| REQ-INPT-091 | Server-side RBAC for discharge via API | P1 | High | 1 |
| REQ-INPT-092 | ADT and PHI-access audit logging | P2 | Medium | 2 |
| REQ-INPT-100 | FHIR Encounter class IMP and status lifecycle | P2 | Medium | 2 |
| REQ-INPT-101 | FHIR Encounter.location across transfers | P3 | Medium | 1 |
| REQ-INPT-102 | FHIR Encounter search for active inpatients | P3 | Low | 1 |
| REQ-INPT-110 | HL7 ADT^A01 inbound admission | P2 | Medium | 1 |
| REQ-INPT-111 | HL7 ADT^A02 inbound transfer | P2 | Medium | 1 |
| REQ-INPT-112 | HL7 ADT^A03 inbound discharge | P2 | Medium | 1 |
| REQ-INPT-113 | HL7 ADT unmatched-identifier rejection | P3 | Medium | 1 |
| REQ-INPT-120 | Pairwise admission configuration coverage | P3 | Medium | 1 |
| REQ-INPT-130 | Bed board / census accessibility | P3 | Medium | 1 |
| REQ-INPT-140 | Bed-day charge accrual per night of stay | P2 | Medium | 1 |

## Insurance & Claims (deep) — 47 requirements, 76 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-INS-001 | Real-time eligibility inquiry (270/271) submission and handling | P1 | High | 4 |
| REQ-INS-002 | Benefit accumulators (deductible/OOP/copay) parsing and display | P2 | Medium | 2 |
| REQ-INS-003 | FHIR CoverageEligibilityRequest/Response support | P2 | Medium | 1 |
| REQ-INS-004 | Eligibility transaction auditing and PHI masking | P2 | High | 1 |
| REQ-INS-010 | Prior authorization request creation and linkage | P1 | High | 1 |
| REQ-INS-011 | Prior-auth enforcement before service rendering/scheduling | P1 | High | 2 |
| REQ-INS-012 | Prior-auth lifecycle, units and validity-window management | P2 | High | 4 |
| REQ-INS-013 | Prior authorization via FHIR Claim preauthorization | P3 | Medium | 1 |
| REQ-INS-020 | Professional claim build from charges with financial integrity | P1 | High | 4 |
| REQ-INS-021 | Institutional (837I) claim build with rev/DRG codes | P2 | High | 1 |
| REQ-INS-022 | Claim build via FHIR Claim resource | P2 | Medium | 1 |
| REQ-INS-023 | Claim scrubber - provider identifier validation | P1 | High | 2 |
| REQ-INS-024 | Claim scrubber - code validity, NCCI and demographic edits | P2 | High | 3 |
| REQ-INS-025 | Clean claim submission and X12 acknowledgements (999/277CA) | P1 | High | 1 |
| REQ-INS-026 | Claim rejection handling and correction | P1 | High | 1 |
| REQ-INS-027 | Claim status lifecycle/state machine | P1 | High | 1 |
| REQ-INS-028 | Duplicate prevention, corrected/replacement and void claims | P1 | High | 3 |
| REQ-INS-029 | Claim submission and status via secured API | P2 | Medium | 1 |
| REQ-INS-030 | 835 ERA auto-posting and matching | P1 | High | 1 |
| REQ-INS-031 | ERA balancing, reconciliation and payment integrity | P1 | High | 4 |
| REQ-INS-032 | CARC/RARC adjustment reason mapping | P2 | High | 1 |
| REQ-INS-033 | Patient responsibility posting to ledger | P2 | Medium | 1 |
| REQ-INS-034 | Unmatched/malformed ERA handling (suspense, quarantine) | P2 | Medium | 2 |
| REQ-INS-035 | ERA reversal/recoupment handling | P2 | High | 1 |
| REQ-INS-040 | Denial work item creation and assignment | P1 | High | 2 |
| REQ-INS-041 | Denial reason routing and rejection/denial classification | P2 | High | 2 |
| REQ-INS-042 | Denial aging and timely-filing SLA tracking | P3 | Medium | 1 |
| REQ-INS-050 | Appeal filing with documentation | P1 | High | 1 |
| REQ-INS-051 | Appeal deadline enforcement | P2 | High | 1 |
| REQ-INS-052 | Appeal lifecycle and overturn-driven reprocessing | P2 | High | 2 |
| REQ-INS-053 | Appeal PHI access control and auditing | P2 | High | 1 |
| REQ-INS-060 | COB primary/secondary ordering and conflict prevention | P1 | High | 2 |
| REQ-INS-061 | Secondary claim submission with COB amounts and no overpayment | P1 | High | 2 |
| REQ-INS-062 | Medicare Secondary Payer determination logic | P2 | High | 1 |
| REQ-INS-063 | Coverage modeling via FHIR Coverage | P3 | Medium | 1 |
| REQ-INS-070 | Payer-specific timely filing limit enforcement | P1 | High | 1 |
| REQ-INS-071 | Payer-specific modifier/POS rule application | P2 | High | 1 |
| REQ-INS-072 | Payer claim format/channel routing | P2 | Medium | 1 |
| REQ-INS-073 | Contract fee schedule and underpayment variance detection | P2 | High | 2 |
| REQ-INS-080 | X12 837 envelope/control segment integrity | P2 | High | 1 |
| REQ-INS-081 | 999 implementation acknowledgement error mapping | P2 | Medium | 1 |
| REQ-INS-082 | Encrypted EDI transmission and storage of PHI | P1 | High | 1 |
| REQ-INS-090 | Payer/insurance master maintenance | P2 | Medium | 1 |
| REQ-INS-091 | Patient coverage assignment and validation | P2 | Medium | 3 |
| REQ-INS-100 | Claims module accessibility (WCAG 2.1 AA) | P3 | Medium | 2 |
| REQ-INS-101 | RBAC for claim build/submit/adjust | P1 | High | 1 |
| REQ-INS-102 | PHI masking in claim lists and exports | P2 | High | 1 |

## Integration (cross-module & external) — 36 requirements, 68 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-INTEG-001 | Lab order placement propagates to result worklist and back to ordering encounter/dashboard | P1 | High | 2 |
| REQ-INTEG-002 | Order completion drives billable charge generation by status | P1 | High | 3 |
| REQ-INTEG-003 | Drug order propagation to pharmacy with allergy/interaction safety checks | P1 | High | 2 |
| REQ-INTEG-004 | Radiology order propagation to imaging worklist and report back-flow | P2 | Medium | 1 |
| REQ-INTEG-005 | Encounter-to-visit linkage consistency across modules | P2 | Medium | 2 |
| REQ-INTEG-006 | Appointment check-in to visit linkage and lifecycle state transitions | P2 | Medium | 2 |
| REQ-INTEG-007 | FHIR R4 outbound exposure of patient, vitals, medication and diagnostic resources | P1 | High | 4 |
| REQ-INTEG-008 | FHIR inbound create/validation including conditional create | P1 | High | 3 |
| REQ-INTEG-009 | HL7 inbound ADT/ORU processing with ACK semantics | P1 | High | 3 |
| REQ-INTEG-010 | Patient matching exception handling on unknown MRN | P2 | High | 1 |
| REQ-INTEG-011 | HL7 outbound event emission on registration | P2 | Medium | 1 |
| REQ-INTEG-012 | Insurance eligibility/claims via WireMock payer stub | P1 | High | 3 |
| REQ-INTEG-013 | External connector resilience to errors, timeouts and unknown fields | P2 | High | 3 |
| REQ-INTEG-014 | Outbound lab order and async result callback via WireMock LIS stub | P1 | High | 2 |
| REQ-INTEG-015 | ePrescribe message delivery via WireMock pharmacy stub | P2 | Medium | 1 |
| REQ-INTEG-016 | Idempotent processing of duplicate/replayed inbound and outbound messages and payments | P1 | High | 3 |
| REQ-INTEG-017 | Business-key duplicate detection and concurrent-write protection | P2 | High | 2 |
| REQ-INTEG-018 | Retry with backoff, dead-letter queue, replay and compensating actions on failure | P1 | High | 4 |
| REQ-INTEG-019 | Domain event emission and consumption across modules | P2 | Medium | 2 |
| REQ-INTEG-020 | Event ordering/versioning and void/retraction propagation convergence | P2 | High | 2 |
| REQ-INTEG-021 | Registration-to-billing coverage/guarantor handoff | P2 | Medium | 1 |
| REQ-INTEG-022 | Diagnosis flow from note to encounter diagnosis to reporting | P2 | Medium | 1 |
| REQ-INTEG-023 | Pairwise order routing by type, destination and priority | P2 | Medium | 1 |
| REQ-INTEG-024 | FHIR transaction atomicity and batch per-entry independence | P2 | High | 2 |
| REQ-INTEG-025 | HL7-to-FHIR cross-interface data consistency | P2 | Medium | 1 |
| REQ-INTEG-026 | Minimum-necessary PHI in outbound interface payloads | P1 | High | 1 |
| REQ-INTEG-027 | Inbound auth enforcement and TLS-only transport | P1 | High | 2 |
| REQ-INTEG-028 | Audit logging of all inbound/outbound interface transactions | P1 | High | 1 |
| REQ-INTEG-029 | Referential integrity and timezone-correct timestamps across handoffs | P2 | High | 2 |
| REQ-INTEG-030 | Message and bundle size boundary handling | P2 | Medium | 2 |
| REQ-INTEG-031 | Late-arriving result linkage after visit closure | P2 | High | 1 |
| REQ-INTEG-032 | Critical-value and preference-aware notification propagation | P1 | High | 2 |
| REQ-INTEG-033 | FHIR Subscription notification on matching resource | P3 | Medium | 1 |
| REQ-INTEG-034 | Resource Adapter Layer portability and code-system mapping across SUTs | P3 | Medium | 2 |
| REQ-INTEG-035 | SMART on FHIR scoped-token enforcement | P2 | High | 1 |
| REQ-INTEG-036 | Accessibility of integration error/reconciliation screens | P3 | Low | 1 |

## Laboratory (deep) — 24 requirements, 78 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-LAB2-001 | Test panel ordering generates member analyte child orders | P2 | Medium | 4 |
| REQ-LAB2-002 | Panel cancellation cascades to member orders | P2 | Medium | 1 |
| REQ-LAB2-003 | Specimen collection metadata capture and datetime validation | P1 | High | 3 |
| REQ-LAB2-004 | Specimen rejection with mandatory reason and compatibility/QNS rules | P1 | High | 4 |
| REQ-LAB2-005 | Unique accession generation, labeling and re-accession | P1 | High | 4 |
| REQ-LAB2-006 | Result entry validation for numeric/coded/staged analytes | P1 | High | 5 |
| REQ-LAB2-007 | Result verification, privileges and two-person rule | P1 | High | 3 |
| REQ-LAB2-008 | Result amendment preserves history and re-notifies | P1 | High | 3 |
| REQ-LAB2-009 | Reference range interpretation by age/sex/boundary | P1 | High | 4 |
| REQ-LAB2-010 | Delta check threshold, time-window and first-result handling | P1 | High | 4 |
| REQ-LAB2-011 | Critical result mandatory callback, read-back and escalation | P1 | High | 4 |
| REQ-LAB2-012 | LOINC/UCUM coding correctness and mapping coverage | P2 | Medium | 5 |
| REQ-LAB2-013 | Turnaround time computation against priority SLAs | P2 | Medium | 3 |
| REQ-LAB2-014 | Reflex test triggering, loop-guard and pairwise rules | P1 | High | 4 |
| REQ-LAB2-015 | Lab worklist filtering, sorting and routing | P2 | Medium | 3 |
| REQ-LAB2-016 | FHIR ServiceRequest/DiagnosticReport/Observation correctness | P2 | Medium | 4 |
| REQ-LAB2-017 | HL7 ORU result ingestion, status, correction and error handling | P2 | High | 5 |
| REQ-LAB2-018 | Lab result access control, patient-scope and injection defense | P1 | High | 3 |
| REQ-LAB2-019 | Result lifecycle audit immutability and completeness | P1 | High | 2 |
| REQ-LAB2-020 | Database referential integrity and concurrency control | P2 | Medium | 2 |
| REQ-LAB2-021 | Lab UI accessibility (keyboard, non-color cues, screen reader) | P3 | Medium | 3 |
| REQ-LAB2-022 | Exploratory robustness of result/specimen workflows | P3 | Medium | 2 |
| REQ-LAB2-023 | Order/result state-transition validity enforcement | P2 | High | 2 |
| REQ-LAB2-024 | Result release decision matrix by priority/criticality/verification | P2 | Medium | 1 |

## Locations & Facilities — 27 requirements, 56 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-LOCN-001 | Location hierarchy creation and multi-level ancestry | P2 | Medium | 3 |
| REQ-LOCN-002 | Prevent self/circular parent references | P2 | High | 2 |
| REQ-LOCN-003 | Location name validation (unique, required, length, Unicode) | P2 | Medium | 5 |
| REQ-LOCN-004 | Login Location tag drives session location picker | P1 | High | 4 |
| REQ-LOCN-005 | Visit Location tag and ancestor resolution | P2 | Medium | 2 |
| REQ-LOCN-006 | Independent multi-tag behavior combinations | P3 | Medium | 2 |
| REQ-LOCN-007 | Custom location tag management | P3 | Low | 2 |
| REQ-LOCN-008 | Custom location attribute types and values | P2 | Medium | 1 |
| REQ-LOCN-009 | Attribute datatype and occurrence enforcement | P2 | Medium | 2 |
| REQ-LOCN-010 | Retire attribute type preserves history | P3 | Low | 1 |
| REQ-LOCN-011 | Retire location with required reason | P2 | Medium | 2 |
| REQ-LOCN-012 | Retired locations excluded from pickers; unretire restores | P2 | Medium | 2 |
| REQ-LOCN-013 | Retire referential integrity (active visits, parent/child) | P2 | High | 2 |
| REQ-LOCN-014 | Structured location address capture and validation | P2 | Medium | 2 |
| REQ-LOCN-015 | Locale-driven address template layout | P3 | Low | 1 |
| REQ-LOCN-016 | System default location global property and form pre-select | P2 | Medium | 2 |
| REQ-LOCN-017 | Default location precedence and invalid-reference handling | P2 | Medium | 3 |
| REQ-LOCN-018 | RBAC for location management and view-only access | P1 | High | 2 |
| REQ-LOCN-019 | Audit trail for location lifecycle actions | P2 | Medium | 1 |
| REQ-LOCN-020 | Database integrity (uuid immutability, FK constraints) | P2 | Medium | 2 |
| REQ-LOCN-021 | Input sanitization against XSS/injection in location fields | P1 | High | 2 |
| REQ-LOCN-022 | FHIR Location read and search | P2 | Medium | 2 |
| REQ-LOCN-023 | FHIR Location status and partOf mapping from OpenMRS | P2 | Medium | 2 |
| REQ-LOCN-024 | FHIR Location address mapping | P3 | Low | 1 |
| REQ-LOCN-025 | FHIR Location write validation and authentication | P1 | High | 2 |
| REQ-LOCN-026 | HL7 facility/location mapping and unmapped handling | P3 | Medium | 2 |
| REQ-LOCN-027 | Accessibility of location management forms | P2 | Medium | 2 |

## Maternal & Antenatal Care — 31 requirements, 65 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-MAT-001 | Register a new pregnancy episode and enroll patient into the Antenatal Care program | P3 | Medium | 2 |
| REQ-MAT-002 | Reject ANC enrollment for a male patient | P3 | Medium | 1 |
| REQ-MAT-003 | Capture LMP at registration and validate it is not a future date | P3 | Medium | 1 |
| REQ-MAT-004 | Capture gravida and parity obstetric history | P3 | Medium | 2 |
| REQ-MAT-005 | Calculate EDD from LMP using Naegele rule (LMP + 280 days) | P3 | Medium | 3 |
| REQ-MAT-006 | Gestational age computed from LMP at current visit | P3 | Medium | 5 |
| REQ-MAT-007 | Reconcile EDD from ultrasound vs LMP when discrepancy exceeds threshold | P3 | Medium | 1 |
| REQ-MAT-008 | High-risk pregnancy flag triggered by advanced maternal age | P3 | Medium | 4 |
| REQ-MAT-009 | Rh-negative mother flagged for anti-D prophylaxis | P3 | Medium | 1 |
| REQ-MAT-010 | Pre-eclampsia early-warning flag from elevated BP and proteinuria | P3 | Medium | 1 |
| REQ-MAT-011 | Generate WHO-aligned ANC visit schedule from EDD | P3 | Medium | 2 |
| REQ-MAT-012 | Flag a missed ANC visit and compute overdue status | P3 | Medium | 3 |
| REQ-MAT-013 | Record routine ANC visit observations (fundal height, FHR, presentation) | P3 | Medium | 3 |
| REQ-MAT-014 | Record tetanus toxoid immunization during ANC | P3 | Medium | 1 |
| REQ-MAT-015 | Order ANC profile labs and link results to pregnancy episode | P3 | Medium | 3 |
| REQ-MAT-016 | Record a normal vaginal delivery outcome and transition ANC state | P3 | Medium | 4 |
| REQ-MAT-017 | Capture estimated blood loss and trigger PPH alert at threshold | P3 | Medium | 1 |
| REQ-MAT-018 | Record multiple birth (twins) with per-baby outcomes | P3 | Medium | 2 |
| REQ-MAT-019 | Register newborn and link to mother as relationship | P3 | Medium | 5 |
| REQ-MAT-020 | Enroll mother into postnatal care after delivery | P3 | Medium | 2 |
| REQ-MAT-021 | Record postnatal danger signs and escalate | P3 | Medium | 1 |
| REQ-MAT-022 | Postpartum family planning method selection | P3 | Medium | 1 |
| REQ-MAT-023 | Plot cervical dilation on partograph and detect alert-line crossing | P3 | Medium | 3 |
| REQ-MAT-024 | Expose pregnancy status and EDD via FHIR observation/condition | P3 | Medium | 3 |
| REQ-MAT-025 | ADT message links newborn to mother via next-of-kin/parent segment | P3 | Medium | 2 |
| REQ-MAT-026 | REST create ANC program enrollment via API | P3 | Medium | 2 |
| REQ-MAT-027 | PHI access to maternity record is audit-logged | P3 | Medium | 1 |
| REQ-MAT-028 | Unauthorized role cannot view or edit maternity clinical data | P3 | Medium | 1 |
| REQ-MAT-029 | Prevent stored XSS in ANC free-text complaint field | P3 | Medium | 1 |
| REQ-MAT-030 | ANC visit form is keyboard navigable and screen-reader labeled | P3 | Medium | 2 |
| REQ-MAT-031 | Exploratory session on concurrent edits to the same ANC visit | P3 | Medium | 1 |

## Responsive / Mobile Web — 15 requirements, 55 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-MOBILE-001 | Responsive breakpoints reflow content correctly across width bands | P2 | Medium | 5 |
| REQ-MOBILE-002 | No horizontal scrolling on any primary mobile view | P1 | High | 3 |
| REQ-MOBILE-003 | Collapsible navbar toggles, closes, and is keyboard/SR accessible | P1 | High | 5 |
| REQ-MOBILE-004 | Touch targets meet minimum size/spacing and avoid accidental destructive actions | P1 | High | 4 |
| REQ-MOBILE-005 | Orientation changes preserve layout and in-progress data | P2 | Medium | 4 |
| REQ-MOBILE-006 | Viewport meta configured for device-width and user scaling allowed | P1 | High | 2 |
| REQ-MOBILE-007 | Forms are usable, validatable, and keyboard-appropriate on small screens | P1 | High | 8 |
| REQ-MOBILE-008 | Mobile navigation (back, sticky, context banner, tiles) works across devices/roles | P2 | Medium | 5 |
| REQ-MOBILE-009 | Mobile performance budgets (FCP, transfer size, CLS, offline) are met | P2 | Medium | 5 |
| REQ-MOBILE-010 | Mobile accessibility: reflow, text spacing, focus order, contrast, RTL (WCAG) | P2 | Medium | 5 |
| REQ-MOBILE-011 | Mobile security: timeout, masking, deep-link auth, PHI handling | P1 | High | 4 |
| REQ-MOBILE-012 | REST API returns device-agnostic patient data | P2 | Low | 1 |
| REQ-MOBILE-013 | FHIR resources render and paginate correctly in mobile views | P2 | Low | 2 |
| REQ-MOBILE-014 | HL7-driven patient data surfaces correctly in mobile search | P3 | Low | 1 |
| REQ-MOBILE-015 | Mobile-originated PHI access is audited | P2 | Medium | 1 |

## Notifications & Alerts — 25 requirements, 44 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-NOTIF-001 | Appointment reminders are generated and suppressed correctly based on appointment state and reminder window | P1 | High | 2 |
| REQ-NOTIF-002 | Reminder lead-time and time-zone/DST scheduling are accurate | P2 | Medium | 2 |
| REQ-NOTIF-003 | Reminder generation is idempotent (no duplicate notifications) | P2 | Medium | 1 |
| REQ-NOTIF-004 | Critical lab results trigger real-time high-priority clinician alerts | P1 | High | 1 |
| REQ-NOTIF-005 | Unacknowledged critical alerts escalate per SLA and severity-based routing | P1 | High | 2 |
| REQ-NOTIF-006 | Alert acknowledgement state machine enforces valid transitions, auth, idempotency, and audit | P1 | High | 5 |
| REQ-NOTIF-007 | Drug-allergy alerts fire before contraindicated medication orders are confirmed | P1 | High | 1 |
| REQ-NOTIF-008 | Drug-drug interaction alerts require captured override reason | P1 | High | 1 |
| REQ-NOTIF-009 | Alert deduplication suppresses identical alerts but preserves clinically distinct ones | P2 | Medium | 2 |
| REQ-NOTIF-010 | System/broadcast messages are displayed to targeted audiences without blocking clinical workflow | P3 | Low | 1 |
| REQ-NOTIF-011 | In-app OpenMRS alerts are targeted, viewable, and mark-as-read persists | P2 | Medium | 2 |
| REQ-NOTIF-012 | Email/SMS delivery succeeds for valid recipients within channel constraints | P2 | Medium | 2 |
| REQ-NOTIF-013 | Delivery failures are handled with status capture and bounded retry/backoff | P2 | Medium | 2 |
| REQ-NOTIF-014 | Channel fallback and dead-letter review/resend for persistent delivery failures | P2 | Medium | 2 |
| REQ-NOTIF-015 | Opt-in/opt-out consent is honored, persisted, double-opt-in confirmed, and does not suppress mandatory safety alerts | P1 | High | 4 |
| REQ-NOTIF-016 | FHIR Communication/CommunicationRequest resources represent notifications correctly per R4 | P2 | Medium | 2 |
| REQ-NOTIF-017 | Invalid FHIR notification resources are rejected with OperationOutcome | P2 | Medium | 1 |
| REQ-NOTIF-018 | HL7 v2 ORU result messages trigger alerts and malformed messages are rejected with error ACK | P2 | Medium | 2 |
| REQ-NOTIF-019 | Notification API exposes provider-scoped alerts with correct pagination and no cross-tenant leakage | P2 | Medium | 1 |
| REQ-NOTIF-020 | Notification API enforces authentication and per-owner authorization | P1 | High | 2 |
| REQ-NOTIF-021 | Critical alert acknowledgement actions are immutably audited | P1 | High | 1 |
| REQ-NOTIF-022 | Outbound notification content discloses only minimum-necessary PHI | P2 | Medium | 1 |
| REQ-NOTIF-023 | Alert UI meets WCAG 2.1 AA accessibility (keyboard, screen reader, non-color severity) | P2 | Medium | 2 |
| REQ-NOTIF-024 | Delivery routing behaves correctly across channel/opt-in/contact-validity combinations | P2 | Medium | 1 |
| REQ-NOTIF-025 | Notification system remains performant and lossless under high alert volume | P3 | Medium | 1 |

## Notifications & Alerts (deep) — 21 requirements, 50 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-NOTIF2-001 | Critical-result alert generation against reference/panic ranges | P1 | High | 3 |
| REQ-NOTIF2-002 | Critical alert recipient resolution and coverage/on-call routing | P1 | High | 1 |
| REQ-NOTIF2-003 | Escalation chains and unacknowledged/undeliverable safety escalation | P1 | High | 4 |
| REQ-NOTIF2-004 | Drug-drug interaction alerting, severity tiers and override governance | P1 | High | 3 |
| REQ-NOTIF2-005 | Allergy/cross-reactivity alerting and NKA handling | P1 | High | 3 |
| REQ-NOTIF2-006 | Appointment reminder scheduling, lead-time and channel preference | P2 | Medium | 2 |
| REQ-NOTIF2-007 | Deduplication of alerts within dedup window | P1 | High | 3 |
| REQ-NOTIF2-008 | In-app notification inbox, badges and read-state | P2 | Medium | 1 |
| REQ-NOTIF2-009 | Email/SMS channel rendering, PHI minimization and link integrity | P2 | High | 2 |
| REQ-NOTIF2-010 | Channel x alert-type x role routing matrix | P2 | Medium | 1 |
| REQ-NOTIF2-011 | Throttling, quiet hours and critical bypass | P2 | Medium | 3 |
| REQ-NOTIF2-012 | Acknowledgement state, idempotency and concurrency | P1 | High | 4 |
| REQ-NOTIF2-013 | Authorization of alert view/ack and API access control | P2 | High | 2 |
| REQ-NOTIF2-014 | Delivery-failure retry, backoff, failover and durable queueing | P1 | High | 3 |
| REQ-NOTIF2-015 | Opt-in/out preferences and regulatory safety exceptions | P1 | High | 3 |
| REQ-NOTIF2-016 | Alert fatigue reduction - aggregation, snooze, override analytics | P2 | Medium | 3 |
| REQ-NOTIF2-017 | FHIR Communication/CommunicationRequest representation | P2 | Medium | 2 |
| REQ-NOTIF2-018 | FHIR Subscription-driven critical Observation notification | P2 | Medium | 1 |
| REQ-NOTIF2-019 | HL7 v2 ORU abnormal-flag result alerting and error handling | P2 | High | 2 |
| REQ-NOTIF2-020 | Notification audit immutability and timestamp/time-zone correctness | P2 | High | 2 |
| REQ-NOTIF2-021 | Accessibility of alert dialogs and status indicators (WCAG 2.1) | P2 | Medium | 2 |

## Orders, Laboratory & Radiology — 31 requirements, 64 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-ORDLAB-001 | Clinician can create lab orders within an active visit with urgency | P1 | High | 2 |
| REQ-ORDLAB-002 | Lab order requires a selected test before saving | P2 | Medium | 1 |
| REQ-ORDLAB-003 | CPOE blocks orders without active visit and warns on duplicates | P1 | High | 2 |
| REQ-ORDLAB-004 | Order urgency and scheduling field validation rules | P2 | Medium | 2 |
| REQ-ORDLAB-005 | Panel/order-set and reflex order generation | P2 | Medium | 2 |
| REQ-ORDLAB-006 | Revise an existing order preserving prior version | P3 | Low | 1 |
| REQ-ORDLAB-007 | Order cancellation/discontinuation with reason and history preservation | P1 | High | 4 |
| REQ-ORDLAB-008 | Specimen collection recording and datetime validation | P1 | High | 2 |
| REQ-ORDLAB-009 | Specimen rejection routes order to recollection | P2 | Medium | 1 |
| REQ-ORDLAB-010 | Numeric and coded lab result entry with validation | P1 | High | 3 |
| REQ-ORDLAB-011 | Reference-range based abnormal High/Low flagging | P1 | High | 4 |
| REQ-ORDLAB-012 | Demographic-specific and missing reference range handling | P2 | Medium | 2 |
| REQ-ORDLAB-013 | Critical (panic) value flagging, alerting, and acknowledgement | P1 | High | 2 |
| REQ-ORDLAB-014 | Absolute/plausibility limits on result values | P2 | Medium | 3 |
| REQ-ORDLAB-015 | Result verification and amendment with audit | P1 | High | 2 |
| REQ-ORDLAB-016 | Segregation of duties for result entry vs verification | P2 | Medium | 1 |
| REQ-ORDLAB-017 | Order status state machine integrity (Ordered to Verified) | P1 | High | 4 |
| REQ-ORDLAB-018 | Radiology order entry with modality and reason for exam | P1 | High | 3 |
| REQ-ORDLAB-019 | Radiology attribute combination coverage (modality/urgency/contrast/portable) | P2 | Medium | 1 |
| REQ-ORDLAB-020 | Imaging/contrast safety and contraindication screening | P1 | High | 2 |
| REQ-ORDLAB-021 | Radiology report and result/report linkage with critical-finding notification | P1 | High | 3 |
| REQ-ORDLAB-022 | Order creation and validation via REST API | P1 | High | 2 |
| REQ-ORDLAB-023 | API authentication, authorization, and PHI access control | P1 | High | 3 |
| REQ-ORDLAB-024 | FHIR ServiceRequest/Observation representation of orders and results | P2 | Medium | 3 |
| REQ-ORDLAB-025 | FHIR DiagnosticReport linkage to ServiceRequest | P2 | Medium | 1 |
| REQ-ORDLAB-026 | HL7 v2 inbound order (ORM) processing | P2 | Medium | 1 |
| REQ-ORDLAB-027 | HL7 v2 inbound result (ORU) processing with abnormal flags and error handling | P2 | Medium | 2 |
| REQ-ORDLAB-028 | Order persistence integrity in the database | P2 | Medium | 1 |
| REQ-ORDLAB-029 | Audit logging of order lifecycle | P2 | Medium | 1 |
| REQ-ORDLAB-030 | Accessibility of order forms and result indicators | P3 | Low | 2 |
| REQ-ORDLAB-031 | Robustness under concurrent/rapid order operations | P3 | Medium | 1 |

## Order Sets & Protocols — 22 requirements, 50 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-ORDSET-001 | Order set selection and catalog filtering | P1 | High | 2 |
| REQ-ORDSET-002 | Order set lifecycle, retirement and versioning | P2 | Medium | 3 |
| REQ-ORDSET-003 | Visit/care-setting context required for application | P1 | High | 1 |
| REQ-ORDSET-004 | Protocol-driven order generation with scheduling and provenance | P1 | High | 2 |
| REQ-ORDSET-005 | Weight/parameter-based protocol dose computation | P1 | High | 2 |
| REQ-ORDSET-006 | Conditional orders and rule-driven dose adjustment | P1 | High | 4 |
| REQ-ORDSET-007 | Modify draft members before sign (dose/freq/route/optional/ad-hoc) | P1 | Medium | 3 |
| REQ-ORDSET-008 | Dose/duration/quantity validation and limits | P1 | High | 2 |
| REQ-ORDSET-009 | Draft and order state transitions (discard/lifecycle) | P2 | Medium | 2 |
| REQ-ORDSET-010 | Bulk sign atomicity and batch handling | P1 | High | 2 |
| REQ-ORDSET-011 | Signing authorization, e-signature and re-authentication | P1 | High | 3 |
| REQ-ORDSET-012 | Concurrency/optimistic locking on signing | P2 | Medium | 1 |
| REQ-ORDSET-013 | Clinical decision support alerts (DDI/allergy/duplicate) | P1 | High | 3 |
| REQ-ORDSET-014 | Alert override capture, hard-stops and audit | P1 | High | 3 |
| REQ-ORDSET-015 | Formulary status checks and therapeutic substitution | P1 | High | 3 |
| REQ-ORDSET-016 | Restricted/stewardship drug approval and role enforcement | P1 | High | 2 |
| REQ-ORDSET-017 | Order set creation via REST API with validation | P1 | Medium | 2 |
| REQ-ORDSET-018 | FHIR PlanDefinition/$apply/RequestGroup and code systems | P1 | Medium | 3 |
| REQ-ORDSET-019 | HL7 v2 outbound order and cancellation messaging | P2 | Medium | 2 |
| REQ-ORDSET-020 | Transactional persistence and order set provenance linkage | P1 | High | 2 |
| REQ-ORDSET-021 | Accessibility of order set flow and alert announcements | P2 | Medium | 2 |
| REQ-ORDSET-022 | Draft state-machine robustness under exploratory use | P3 | Medium | 1 |

## Patient Dashboard & Demographics — 15 requirements, 64 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-PDASH-001 | Patient dashboard displays demographic name, gender, and birthdate as registered | P1 | High | 4 |
| REQ-PDASH-002 | Preferred patient identifier is displayed prominently in the banner | P1 | High | 4 |
| REQ-PDASH-003 | Age is calculated and displayed correctly across adult, infant, neonate, leap-day, estimated, and deceased cases | P1 | High | 8 |
| REQ-PDASH-004 | Show Contact Info reveals address and phone, omitting absent or voided fields | P2 | Medium | 6 |
| REQ-PDASH-005 | Edit Registration Information allows privileged users to update demographics with validation and conflict safety | P1 | High | 6 |
| REQ-PDASH-006 | Mark Patient Deceased records death date and cause with state transitions and validation | P1 | High | 10 |
| REQ-PDASH-007 | General Actions visibility is governed by user privileges | P1 | High | 4 |
| REQ-PDASH-008 | Attachments widget lists, uploads, and validates patient attachments | P2 | Medium | 4 |
| REQ-PDASH-009 | REST API exposes patient demographics with proper authentication and error handling | P1 | High | 3 |
| REQ-PDASH-010 | FHIR R4 Patient resource correctly represents demographics, identifiers, telecom, and address | P1 | High | 3 |
| REQ-PDASH-011 | HL7 v2 ADT messages update demographics and deceased status on the dashboard | P2 | Medium | 2 |
| REQ-PDASH-012 | Demographic edits, deceased changes, and PHI access are audit-logged | P1 | High | 3 |
| REQ-PDASH-013 | Dashboard meets accessibility standards (screen reader, keyboard, non-color status cues) | P2 | Medium | 3 |
| REQ-PDASH-014 | Dashboard enforces security: output encoding against XSS and cross-patient isolation | P1 | High | 2 |
| REQ-PDASH-015 | Dashboard renders robustly and within performance budget across data and viewport variations | P2 | Medium | 2 |

## Pediatrics & Growth — 28 requirements, 54 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-PEDS-001 | WHO/CDC growth chart plotting and standard selection by age band | P1 | High | 3 |
| REQ-PEDS-002 | Weight/length percentile and z-score computation via LMS reference | P1 | High | 3 |
| REQ-PEDS-003 | Nutritional status banding and malnutrition flagging by z-score | P1 | High | 2 |
| REQ-PEDS-004 | Implausible growth value and growth-faltering detection | P2 | High | 1 |
| REQ-PEDS-005 | Length vs height measurement method handling for reference selection | P2 | Medium | 1 |
| REQ-PEDS-006 | Head circumference-for-age plotting and macrocephaly flag | P2 | High | 2 |
| REQ-PEDS-007 | Weight-based dose calculation with current-weight requirement | P1 | High | 3 |
| REQ-PEDS-008 | Dose cap, stale-weight and invalid-weight safeguards | P1 | High | 3 |
| REQ-PEDS-009 | Dose-form/volume conversion and BSA-based dosing | P2 | High | 2 |
| REQ-PEDS-010 | Age-specific vitals reference ranges and abnormal flagging | P1 | High | 4 |
| REQ-PEDS-011 | Pediatric BP interpretation by age-sex-height percentile | P2 | High | 1 |
| REQ-PEDS-012 | Temperature unit conversion integrity and fever flag | P2 | Medium | 1 |
| REQ-PEDS-013 | Newborn and multiple-birth registration | P1 | High | 3 |
| REQ-PEDS-014 | DOB validation on registration | P1 | Medium | 1 |
| REQ-PEDS-015 | Gestational age, prematurity flag and corrected-age plotting | P2 | High | 2 |
| REQ-PEDS-016 | Guardian/parent-child relationships and delivery linkage | P1 | Medium | 3 |
| REQ-PEDS-017 | Relationship integrity (no self/circular links) | P2 | Medium | 1 |
| REQ-PEDS-018 | Minor consent and adolescent confidentiality controls | P1 | High | 2 |
| REQ-PEDS-019 | Immunization schedule linkage and overdue flagging | P1 | High | 2 |
| REQ-PEDS-020 | Vaccine minimum-interval enforcement and schedule state transitions | P2 | High | 2 |
| REQ-PEDS-021 | FHIR Immunization resource representation | P2 | Medium | 1 |
| REQ-PEDS-022 | FHIR pediatric growth/vitals Observation conformance (LOINC/UCUM) | P2 | High | 3 |
| REQ-PEDS-023 | HL7v2 ORU/ADT pediatric data fidelity | P3 | Medium | 2 |
| REQ-PEDS-024 | Age-band display and pediatric/adult workflow routing | P2 | Medium | 2 |
| REQ-PEDS-025 | Growth chart accessibility | P2 | Medium | 1 |
| REQ-PEDS-026 | Audit logging of dose calculation and overrides | P2 | High | 1 |
| REQ-PEDS-027 | Persistence precision/units for growth observations | P3 | Medium | 1 |
| REQ-PEDS-028 | Vitals encounter draft/signed state and amendment behavior | P2 | Medium | 1 |

## Performance Readiness (criteria/design) — 17 requirements, 43 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-PERF-001 | Page response-time acceptance criteria (P95/P99) per clinical page under nominal load | P1 | High | 3 |
| REQ-PERF-002 | API response-time and payload-handling latency budgets for REST endpoints | P1 | High | 3 |
| REQ-PERF-003 | Concurrent-user readiness at rated capacity without functional break | P1 | High | 3 |
| REQ-PERF-004 | Search/list pagination latency stays within budget and sub-linear under data volume | P2 | Medium | 4 |
| REQ-PERF-005 | Report generation completes within defined time budget without starving interactive traffic | P2 | Medium | 3 |
| REQ-PERF-006 | Database query-count budget, slow-query threshold and connection-pool limits | P1 | High | 3 |
| REQ-PERF-007 | Soak/endurance stability with no memory leak or latency/error drift | P1 | High | 2 |
| REQ-PERF-008 | Resource-utilization thresholds (CPU, memory, GC) at rated load | P2 | Medium | 3 |
| REQ-PERF-009 | Graceful degradation and load-shedding under overload and dependency faults | P1 | High | 3 |
| REQ-PERF-010 | No functional break / data-integrity preservation under sustained load | P1 | High | 2 |
| REQ-PERF-011 | FHIR API performance and pagination/transaction latency under load | P2 | Medium | 3 |
| REQ-PERF-012 | HL7 v2 interface ingestion throughput and burst/backpressure handling | P2 | Medium | 2 |
| REQ-PERF-013 | Caching effectiveness and stampede protection | P3 | Low | 2 |
| REQ-PERF-014 | Authentication and security-control performance under load (login storm, throttling) | P2 | Medium | 2 |
| REQ-PERF-015 | Audit-logging performance overhead and no record loss under load | P1 | High | 1 |
| REQ-PERF-016 | Scalability, failover and capacity headroom criteria | P2 | Medium | 3 |
| REQ-PERF-017 | Cold-start / readiness boot-time SLA | P3 | Low | 1 |

## Performance & Load (criteria, deep) — 27 requirements, 54 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-PERF2-001 | Page/login latency SLO (p50/p95/p99) under nominal and storm load | P1 | High | 3 |
| REQ-PERF2-002 | REST API latency SLO and breach detection/alerting | P1 | High | 2 |
| REQ-PERF2-003 | Sustained throughput ceiling and saturation-knee stability | P1 | High | 2 |
| REQ-PERF2-004 | Concurrent-user mixed-workload scalability and session caps | P1 | High | 3 |
| REQ-PERF2-005 | Search latency at high data volume incl. boundary queries | P1 | High | 2 |
| REQ-PERF2-006 | List/pagination performance and parameter guardrails | P2 | Medium | 3 |
| REQ-PERF2-007 | Report generation time budget and interactive isolation | P1 | High | 3 |
| REQ-PERF2-008 | Soak stability / memory-leak and steady-state criteria | P1 | High | 2 |
| REQ-PERF2-009 | Spike absorption, recovery RTO, and no duplicate/lost writes | P1 | High | 3 |
| REQ-PERF2-010 | Stress-to-failure breaking point and self-recovery | P2 | High | 2 |
| REQ-PERF2-011 | DB query budget (no N+1), slow-query and pool sizing | P1 | High | 4 |
| REQ-PERF2-012 | Caching hit ratio and stale-data-free invalidation | P1 | High | 3 |
| REQ-PERF2-013 | Capacity headroom and growth forecasting | P1 | High | 2 |
| REQ-PERF2-014 | Graceful degradation and circuit-breaker behavior | P1 | High | 3 |
| REQ-PERF2-015 | FHIR read/search/bulk-export performance and conformance | P1 | High | 3 |
| REQ-PERF2-016 | HL7v2 ingest throughput and backpressure | P1 | High | 2 |
| REQ-PERF2-017 | CPOE order submission latency and concurrency integrity | P1 | High | 2 |
| REQ-PERF2-018 | High-frequency vitals write load and persistence | P2 | Medium | 1 |
| REQ-PERF2-019 | Appointment slot booking contention | P1 | High | 1 |
| REQ-PERF2-020 | Audit logging overhead under PHI-access load | P2 | High | 1 |
| REQ-PERF2-021 | Accessibility responsiveness under server load | P3 | Low | 1 |
| REQ-PERF2-022 | Security/authz/rate-limit integrity under load | P1 | High | 1 |
| REQ-PERF2-023 | Exploratory hotspot/profiling discovery | P2 | Medium | 1 |
| REQ-PERF2-024 | Cold-start and warm-up latency criteria | P2 | Medium | 1 |
| REQ-PERF2-025 | Zero-downtime rolling deploy under load | P1 | High | 1 |
| REQ-PERF2-026 | Per-tenant/per-user throttling policy | P2 | Medium | 1 |
| REQ-PERF2-027 | Bulk import throughput without interactive impact | P2 | High | 1 |

## Pharmacy & Medication Orders — 22 requirements, 58 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-PHARM-001 | Drug order entry requires drug, dose, dose unit, route, frequency, and duration | P1 | High | 3 |
| REQ-PHARM-002 | Support free-text (non-coded) drug orders flagged distinctly | P2 | Medium | 1 |
| REQ-PHARM-003 | Enforce dosing boundaries: min/max single dose, max cumulative daily dose, valid numeric input | P1 | High | 7 |
| REQ-PHARM-004 | Weight-based pediatric dose calculation requires recorded patient weight | P1 | High | 2 |
| REQ-PHARM-005 | Drug-drug interaction and duplicate-therapy checking with audited override | P1 | High | 4 |
| REQ-PHARM-006 | Interaction severity decision table maps tiers to system actions (block/override/acknowledge/info) | P1 | High | 2 |
| REQ-PHARM-007 | Allergy and cross-sensitivity contraindication alerts with audited override | P1 | High | 4 |
| REQ-PHARM-008 | Administration frequency, scheduling, and PRN order capture | P2 | Medium | 2 |
| REQ-PHARM-009 | Duration validation and future scheduled start dates | P2 | Medium | 2 |
| REQ-PHARM-010 | Order lifecycle: revise, discontinue with reason, and valid state transitions | P1 | High | 5 |
| REQ-PHARM-011 | Refill authorization tracking and decrement with exhaustion blocking | P2 | Medium | 2 |
| REQ-PHARM-012 | Dispensing against active orders with quantity controls and partial fills | P1 | High | 3 |
| REQ-PHARM-013 | Controlled-substance handling: prescriber authorization, no CII refills, immutable dispense audit | P1 | High | 4 |
| REQ-PHARM-014 | FHIR R4 MedicationRequest conformance with valid coded units and validation errors | P1 | Medium | 3 |
| REQ-PHARM-015 | HL7 pharmacy messaging (RDE^O11 / RDS^O13) with malformed-message handling | P2 | Medium | 3 |
| REQ-PHARM-016 | REST order-entry API create/validate with encounter requirement | P1 | Medium | 2 |
| REQ-PHARM-017 | Access control: authentication, role-based prescribing privilege, and PHI access audit | P1 | High | 3 |
| REQ-PHARM-018 | Database integrity: referential integrity and soft-delete (voided) semantics | P2 | Medium | 2 |
| REQ-PHARM-019 | Pairwise validity across route, frequency, and formulation combinations | P2 | Medium | 1 |
| REQ-PHARM-020 | Order entry UI accessibility (WCAG 2.1 AA keyboard and screen-reader support) | P2 | Medium | 1 |
| REQ-PHARM-021 | Robust behavior under complex polypharmacy ordering | P3 | Medium | 1 |
| REQ-PHARM-022 | Renal-function-adjusted dosing guidance | P1 | High | 1 |

## Patient Portal & Self-Service — 23 requirements, 50 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-PORTAL-001 | Patient self-registration with identity verification and abuse resistance | P1 | High | 3 |
| REQ-PORTAL-002 | Portal password policy enforcement | P2 | Medium | 1 |
| REQ-PORTAL-003 | Self-registrant to existing-record identity matching/linkage | P1 | High | 1 |
| REQ-PORTAL-004 | Minimum self-registration age enforcement | P1 | High | 1 |
| REQ-PORTAL-005 | Account lockout and secure password reset | P1 | High | 2 |
| REQ-PORTAL-006 | Portal authentication: MFA, session timeout, re-auth for PHI | P1 | High | 3 |
| REQ-PORTAL-007 | Patient view of own visit/encounter history | P2 | Medium | 1 |
| REQ-PORTAL-008 | Lab/result release rules, embargo, ranges and abnormal flags | P1 | High | 2 |
| REQ-PORTAL-009 | Appointment booking with concurrency and scheduling boundaries | P1 | High | 3 |
| REQ-PORTAL-010 | Appointment cancellation and lifecycle state transitions | P2 | Medium | 3 |
| REQ-PORTAL-011 | Secure messaging with attachment policy, sanitization, annotations | P1 | High | 4 |
| REQ-PORTAL-012 | Consent capture, versioning, withdrawal and FHIR mapping | P1 | High | 3 |
| REQ-PORTAL-013 | Patient data export via FHIR $everything with access control | P1 | High | 3 |
| REQ-PORTAL-014 | Bulk Data export and human-readable record download | P2 | Medium | 4 |
| REQ-PORTAL-015 | Guardian/proxy access provisioning and revocation | P1 | High | 2 |
| REQ-PORTAL-016 | Adolescent confidentiality and majority-age proxy auto-revoke | P1 | High | 2 |
| REQ-PORTAL-017 | Authorization: RBAC/consent/category decisions and IDOR protection | P2 | High | 2 |
| REQ-PORTAL-018 | Patient profile/demographics self-service with validation | P3 | Medium | 2 |
| REQ-PORTAL-019 | Medication refill self-service with controlled-substance guardrails | P2 | High | 2 |
| REQ-PORTAL-020 | Portal accessibility (WCAG 2.1 AA) | P2 | Medium | 3 |
| REQ-PORTAL-021 | Audit logging of portal PHI access and disclosures | P1 | High | 1 |
| REQ-PORTAL-022 | Patient notifications and channel opt-out | P3 | Medium | 1 |
| REQ-PORTAL-023 | HL7 ADT-driven demographic updates to portal | P3 | Medium | 1 |

## Care Programs & Enrollment — 23 requirements, 70 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-PROG-001 | Enroll patient into a care program with valid program and date | P1 | High | 3 |
| REQ-PROG-002 | Enrollment date validation (future, birthdate, death date boundaries) | P2 | Medium | 5 |
| REQ-PROG-003 | Prevent duplicate concurrent active enrollment in same program | P1 | High | 1 |
| REQ-PROG-004 | Support multiple and re-enrollments without overlap | P2 | Medium | 3 |
| REQ-PROG-005 | Program workflow states and valid state transitions | P1 | High | 9 |
| REQ-PROG-006 | Complete enrollment with completion date validation | P1 | High | 5 |
| REQ-PROG-007 | Program outcomes constrained to configured concept set | P2 | High | 5 |
| REQ-PROG-008 | Transfer enrollment location and transfer-out handoff | P2 | High | 2 |
| REQ-PROG-009 | Eligibility rules enforcement for enrollment | P2 | High | 5 |
| REQ-PROG-010 | Role-based authorization for program enrollment management | P1 | High | 3 |
| REQ-PROG-011 | Edit enrollment with date/state consistency | P2 | Medium | 2 |
| REQ-PROG-012 | Void enrollment with reason and cascade | P2 | High | 2 |
| REQ-PROG-013 | Audit trail for enrollment lifecycle changes | P2 | High | 2 |
| REQ-PROG-014 | REST API for enrollment and state management | P1 | High | 5 |
| REQ-PROG-015 | FHIR EpisodeOfCare/CarePlan mapping for programs | P2 | Medium | 5 |
| REQ-PROG-016 | HL7 inbound/outbound enrollment messaging | P3 | Medium | 2 |
| REQ-PROG-017 | Database referential integrity and date/timezone persistence | P2 | High | 2 |
| REQ-PROG-018 | Accessibility of programs widget and enrollment form | P3 | Low | 2 |
| REQ-PROG-019 | PHI privacy and access control for sensitive program data | P2 | High | 2 |
| REQ-PROG-020 | State integrity across rapid enroll/complete cycles | P3 | Medium | 1 |
| REQ-PROG-021 | Concurrent edit conflict handling | P3 | High | 1 |
| REQ-PROG-022 | Enrollment history ordering, status labeling and filtering | P3 | Low | 2 |
| REQ-PROG-023 | Input sanitization for enrollment free-text fields | P2 | High | 1 |

## Provider & Practitioner Management — 17 requirements, 60 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-PROV-001 | Create provider linked to person or by name | P1 | High | 2 |
| REQ-PROV-002 | Provider identifier validation and uniqueness | P1 | High | 5 |
| REQ-PROV-003 | Edit provider and re-link person with audit | P2 | Medium | 4 |
| REQ-PROV-004 | Link provider to user/person account | P2 | Medium | 2 |
| REQ-PROV-005 | Provider roles and supervision hierarchy | P2 | Medium | 3 |
| REQ-PROV-006 | Provider attributes and specialty management | P2 | Medium | 5 |
| REQ-PROV-007 | Provider availability schedules | P2 | Medium | 4 |
| REQ-PROV-008 | Retire/unretire provider state lifecycle | P1 | High | 7 |
| REQ-PROV-009 | Provider search and listing | P2 | Medium | 6 |
| REQ-PROV-010 | Authorization and audit of provider management | P1 | High | 2 |
| REQ-PROV-011 | FHIR Practitioner resource support | P1 | Medium | 7 |
| REQ-PROV-012 | FHIR PractitionerRole and versioning | P3 | Medium | 2 |
| REQ-PROV-013 | HL7 provider resolution on inbound messages | P2 | Medium | 2 |
| REQ-PROV-014 | REST API provider CRUD | P2 | Medium | 4 |
| REQ-PROV-015 | Input sanitization (XSS/SQLi) | P1 | High | 2 |
| REQ-PROV-016 | Accessibility of provider UI (WCAG 2.1 AA) | P3 | Low | 2 |
| REQ-PROV-017 | Provider lifecycle robustness (exploratory) | P3 | Medium | 1 |

## Radiology & Imaging (deep) — 34 requirements, 68 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-RAD-001 | Radiology order entry with clinical indication | P1 | High | 2 |
| REQ-RAD-002 | Order data validation (laterality, indication, length) | P2 | Medium | 2 |
| REQ-RAD-003 | Order priority handling and STAT surfacing | P1 | High | 2 |
| REQ-RAD-004 | Contrast safety checks and allergy gating/override | P1 | High | 4 |
| REQ-RAD-005 | Renal function (eGFR) gating for contrast | P1 | High | 1 |
| REQ-RAD-006 | Pregnancy/radiation safety warning | P1 | High | 1 |
| REQ-RAD-007 | Metformin/contrast medication advisory | P2 | Medium | 1 |
| REQ-RAD-008 | Modality scheduling and slot conflict prevention | P1 | High | 2 |
| REQ-RAD-009 | Schedule boundary and routing rules | P2 | Medium | 3 |
| REQ-RAD-010 | DICOM Modality Worklist and accession integrity | P1 | High | 2 |
| REQ-RAD-011 | Worklist filtering and order querying/pagination | P2 | Medium | 2 |
| REQ-RAD-012 | Order status lifecycle state transitions | P1 | High | 3 |
| REQ-RAD-013 | Order cancellation/exception with reason and history retention | P1 | High | 5 |
| REQ-RAD-014 | Report preliminary/final sign and co-sign workflow | P1 | High | 3 |
| REQ-RAD-015 | Report dictation draft autosave and content integrity | P3 | Medium | 2 |
| REQ-RAD-016 | Finalized report immutability and addendum | P1 | High | 3 |
| REQ-RAD-017 | Critical/panic finding communication and notification | P1 | High | 2 |
| REQ-RAD-018 | Report turnaround time metrics | P3 | Low | 1 |
| REQ-RAD-019 | PACS viewer linkage by accession | P1 | High | 2 |
| REQ-RAD-020 | Image-order patient match safety | P1 | High | 1 |
| REQ-RAD-021 | Radiology RBAC and privilege enforcement | P1 | High | 2 |
| REQ-RAD-022 | Inbound HL7 ORM order processing and validation | P1 | High | 2 |
| REQ-RAD-023 | HL7 ORU results and outbound status messaging | P1 | High | 2 |
| REQ-RAD-024 | FHIR ServiceRequest create and validation | P1 | High | 3 |
| REQ-RAD-025 | FHIR DiagnosticReport retrieval and amendment status | P1 | High | 2 |
| REQ-RAD-026 | FHIR ImagingStudy modality/series exposure | P2 | Medium | 1 |
| REQ-RAD-027 | PHI access control / IDOR / scope enforcement | P1 | High | 2 |
| REQ-RAD-028 | Report free-text input sanitization | P2 | Medium | 1 |
| REQ-RAD-029 | Audit logging of report and image access events | P1 | High | 2 |
| REQ-RAD-030 | Report-order referential integrity | P2 | Medium | 1 |
| REQ-RAD-031 | MRI safety screening gating | P1 | High | 1 |
| REQ-RAD-032 | Radiology accessibility (keyboard/screen reader/alerts) | P2 | Medium | 2 |
| REQ-RAD-033 | Imaging charge capture and reversal | P3 | Medium | 2 |
| REQ-RAD-034 | Duplicate-order detection | P2 | Medium | 1 |

## Roles, Privileges & User Admin — 26 requirements, 78 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-RBAC-001 | System administrators can create, edit, rename, and configure roles including inheritance from parent roles | P1 | High | 5 |
| REQ-RBAC-002 | Role name validation enforces uniqueness, required value, and maximum length | P2 | Medium | 3 |
| REQ-RBAC-003 | Roles in use cannot be silently deleted; unused roles can be removed safely | P2 | High | 2 |
| REQ-RBAC-004 | Administrators can create privileges and assign them to roles with uniqueness validation | P2 | Medium | 2 |
| REQ-RBAC-005 | Administrators can provision user accounts and assign one or more roles | P1 | High | 2 |
| REQ-RBAC-006 | Username validation enforces uniqueness, length, and allowed character format | P2 | Medium | 3 |
| REQ-RBAC-007 | Password policy enforces complexity, confirmation match, admin reset, and forced first-login change | P2 | High | 4 |
| REQ-RBAC-008 | Users default to least privilege when no role is assigned | P3 | Medium | 1 |
| REQ-RBAC-009 | User accounts can be disabled and re-enabled, with active sessions revoked on disable | P1 | High | 3 |
| REQ-RBAC-010 | Home dashboard apps and menus are filtered by the user's role-based privileges | P1 | High | 4 |
| REQ-RBAC-011 | Privileges are enforced on clinical and patient actions (register, edit, prescribe, void, view) | P1 | High | 7 |
| REQ-RBAC-012 | Role-action access matrix is enforced consistently across clerk, nurse, doctor, pharmacist, and admin | P1 | High | 6 |
| REQ-RBAC-013 | Unauthorized and unauthenticated access via direct URL or forced browsing is denied server-side | P1 | High | 3 |
| REQ-RBAC-014 | Vertical privilege escalation (self-grant, tampered role/privilege parameters) is prevented | P1 | High | 3 |
| REQ-RBAC-015 | Horizontal access across patient/provider scope is prevented | P1 | High | 2 |
| REQ-RBAC-016 | Administrative and security-relevant actions are recorded in an audit log | P1 | High | 5 |
| REQ-RBAC-017 | Account lockout after configured failed login attempts with counter reset on success | P2 | High | 2 |
| REQ-RBAC-018 | Session management enforces timeout, token invalidation, and concurrent-session policy | P2 | Medium | 3 |
| REQ-RBAC-019 | REST API enforces authentication and the same privilege rules as the UI | P1 | High | 4 |
| REQ-RBAC-020 | FHIR endpoints enforce SMART scopes for read and write access | P1 | High | 2 |
| REQ-RBAC-021 | HL7 interfaces accept messages only from authorized sending facilities/applications | P2 | High | 2 |
| REQ-RBAC-022 | User admin and access-denied UI meet accessibility standards (keyboard, screen reader, non-color cues) | P3 | Low | 3 |
| REQ-RBAC-023 | Admin input fields are protected against injection and stored XSS | P2 | Medium | 2 |
| REQ-RBAC-024 | RBAC data persistence maintains referential integrity and stores credentials hashed/salted | P1 | High | 2 |
| REQ-RBAC-025 | The last administrator account cannot be disabled, preventing system lockout | P2 | High | 1 |
| REQ-RBAC-026 | Provider records link to users and retiring a provider removes them from active selection | P2 | Medium | 2 |

## Referrals & Transfers — 15 requirements, 55 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-REFER-001 | Create outbound referral with required fields and validations | P1 | High | 6 |
| REQ-REFER-002 | Referral guardrails for ineligible patients (deceased) | P2 | High | 1 |
| REQ-REFER-003 | Specialty and facility selection and network rules | P2 | Medium | 3 |
| REQ-REFER-004 | Referral urgency, SLA mapping and priority routing | P1 | High | 4 |
| REQ-REFER-005 | Attach and access-control supporting documents | P2 | High | 4 |
| REQ-REFER-006 | Accept, reject and complete referral actions | P1 | High | 4 |
| REQ-REFER-007 | Referral state-transition lifecycle integrity | P1 | High | 5 |
| REQ-REFER-008 | Inter-facility transfer of care workflow | P1 | High | 5 |
| REQ-REFER-009 | Feedback loop, escalation and loop closure | P1 | High | 5 |
| REQ-REFER-010 | FHIR ServiceRequest representation of referrals | P1 | High | 7 |
| REQ-REFER-011 | HL7 REF^I12 referral messaging interoperability | P2 | Medium | 3 |
| REQ-REFER-012 | RBAC for referral creation and queue access | P1 | High | 2 |
| REQ-REFER-013 | Referral audit trail, persistence and concurrency | P1 | High | 3 |
| REQ-REFER-014 | Accessibility of referral forms and indicators | P2 | Medium | 2 |
| REQ-REFER-015 | Exploratory robustness of referral workflows | P3 | Medium | 1 |

## Patient Registration — 25 requirements, 95 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-REG-001 | Register a patient with valid required demographics and generate a record | P1 | High | 8 |
| REQ-REG-002 | Enforce mandatory-field validation (name, gender, birthdate/age, address) | P1 | High | 9 |
| REQ-REG-003 | Name field handling: unicode, special chars, length, whitespace, sanitization | P2 | Medium | 5 |
| REQ-REG-004 | Capture and persist patient gender | P1 | Medium | 2 |
| REQ-REG-005 | Support exact birthdate and estimated age with correct derivation | P1 | Medium | 6 |
| REQ-REG-006 | Birthdate boundary and validity rules (future date, age 0, age 120+) | P1 | High | 5 |
| REQ-REG-007 | Require at least one address field and persist address components | P1 | High | 6 |
| REQ-REG-008 | Capture and validate patient contact phone number | P2 | Medium | 3 |
| REQ-REG-009 | Support unknown/anonymous patient registration and later identification | P2 | High | 2 |
| REQ-REG-010 | Detect similar/duplicate patients before record creation | P1 | High | 6 |
| REQ-REG-011 | Auto-generate unique, format-valid Patient ID/MRN without collision or duplicate submit | P1 | High | 4 |
| REQ-REG-012 | Registration wizard state transitions (incomplete -> confirm -> saved, cancel, back) | P1 | High | 5 |
| REQ-REG-013 | Edit existing patient registration with recalculation and conflict handling | P2 | High | 3 |
| REQ-REG-014 | Authentication and authorization for registration access and edits | P1 | High | 4 |
| REQ-REG-015 | Protect against injection (SQL/XSS) in registration inputs | P1 | High | 2 |
| REQ-REG-016 | Audit logging of patient create and edit events | P1 | High | 2 |
| REQ-REG-017 | Accessibility of the registration form (WCAG 2.1 AA) | P2 | Medium | 4 |
| REQ-REG-018 | Create and retrieve patients via REST API with validation | P1 | High | 3 |
| REQ-REG-019 | FHIR R4 Patient resource conformance (name, gender, identifier, birthDate) | P1 | High | 4 |
| REQ-REG-020 | HL7 v2 ADT inbound registration/update and error acknowledgement | P2 | High | 3 |
| REQ-REG-021 | Correct database persistence and soft-delete/void semantics | P2 | Medium | 2 |
| REQ-REG-022 | Capture and validate insurance/payer coverage at registration | P2 | Medium | 2 |
| REQ-REG-023 | Capture mandatory patient consent/privacy acknowledgement | P2 | High | 2 |
| REQ-REG-024 | National identifier validation and uniqueness enforcement | P1 | High | 2 |
| REQ-REG-025 | Registration performance within agreed response-time SLA | P3 | Medium | 1 |

## Regression Pack (high-value) — 28 requirements, 84 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-REGR-001 | Appointment scheduling conflict and slot lifecycle rules | P1 | High | 4 |
| REQ-REGR-002 | Patient registration demographic validation (DOB, gender) | P1 | High | 5 |
| REQ-REGR-003 | Required address field enforcement | P2 | Medium | 1 |
| REQ-REGR-004 | Duplicate patient detection | P1 | High | 1 |
| REQ-REGR-005 | RBAC and authorization enforcement | P1 | High | 4 |
| REQ-REGR-006 | Visit and encounter integrity and state transitions | P2 | Medium | 4 |
| REQ-REGR-007 | Vitals range validation and computed values | P2 | Medium | 3 |
| REQ-REGR-008 | Allergy management integrity | P2 | Medium | 3 |
| REQ-REGR-009 | Conditions and diagnoses coding and status | P2 | Medium | 2 |
| REQ-REGR-010 | Drug order lifecycle and validation | P1 | High | 5 |
| REQ-REGR-011 | Lab/radiology order and result validation | P2 | Medium | 3 |
| REQ-REGR-012 | Pharmacy dispensing controls | P2 | High | 3 |
| REQ-REGR-013 | Drug-allergy interaction checking | P1 | High | 1 |
| REQ-REGR-014 | FHIR R4 resource correctness and error handling | P2 | High | 6 |
| REQ-REGR-015 | HL7 interface inbound/ack handling | P2 | Medium | 4 |
| REQ-REGR-016 | Audit logging of PHI access and key actions | P1 | High | 3 |
| REQ-REGR-017 | Security: auth, session, injection, lockout | P1 | High | 6 |
| REQ-REGR-018 | Patient search and voided-record exclusion | P2 | Medium | 3 |
| REQ-REGR-019 | Billing charge, copay, refund and invoice lifecycle | P2 | High | 4 |
| REQ-REGR-020 | Reporting accuracy and date-range boundaries | P2 | Medium | 2 |
| REQ-REGR-021 | Data management: concept retire and patient merge | P1 | High | 3 |
| REQ-REGR-022 | Notification reminder rules and preferences | P2 | Medium | 2 |
| REQ-REGR-023 | Telemedicine session security and documentation | P2 | Medium | 2 |
| REQ-REGR-024 | Accessibility WCAG conformance | P2 | Medium | 3 |
| REQ-REGR-025 | Performance under load and large datasets | P2 | Medium | 2 |
| REQ-REGR-026 | REST API pagination boundaries | P3 | Low | 1 |
| REQ-REGR-027 | Database referential integrity and concurrency | P2 | High | 2 |
| REQ-REGR-028 | Exploratory regression charters | P3 | Medium | 2 |

## Reporting & Analytics (deep) — 35 requirements, 64 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-REPT2-001 | Create and validate report definitions with datasets | P2 | Medium | 3 |
| REQ-REPT2-002 | Edit, version and safely delete report definitions | P3 | Medium | 2 |
| REQ-REPT2-010 | Build cohorts from patient attribute searches | P2 | Medium | 1 |
| REQ-REPT2-011 | Compose cohorts with boolean logic and valid expressions | P2 | High | 3 |
| REQ-REPT2-012 | Save, reload and bound cohort definitions | P3 | Medium | 3 |
| REQ-REPT2-020 | Define count and proportion indicators over cohorts | P2 | High | 2 |
| REQ-REPT2-021 | Indicator dimensions, disaggregation and divide-by-zero safety | P2 | High | 3 |
| REQ-REPT2-030 | Required date-range parameter evaluation | P1 | High | 3 |
| REQ-REPT2-031 | Date parameter boundary and format validation | P2 | Medium | 2 |
| REQ-REPT2-032 | Location parameter filtering | P2 | Medium | 1 |
| REQ-REPT2-033 | Multi-parameter pairwise combination correctness | P2 | High | 1 |
| REQ-REPT2-040 | Schedule reports via cron | P2 | Medium | 1 |
| REQ-REPT2-041 | Scheduled report lifecycle states and pause/resume | P2 | Medium | 2 |
| REQ-REPT2-042 | Concurrent/overlapping runs and delivery integrity | P2 | High | 2 |
| REQ-REPT2-050 | Export results to CSV and Excel accurately | P1 | Medium | 2 |
| REQ-REPT2-051 | Export to PDF preserving layout | P2 | Medium | 1 |
| REQ-REPT2-052 | Export sanitization and special-character integrity | P2 | High | 2 |
| REQ-REPT2-053 | Empty-result export behavior | P3 | Low | 1 |
| REQ-REPT2-060 | Large-dataset export and evaluation performance | P2 | High | 2 |
| REQ-REPT2-061 | Pagination correctness, boundaries and sort stability | P2 | Medium | 3 |
| REQ-REPT2-070 | Privilege and endpoint authorization for reports | P1 | High | 2 |
| REQ-REPT2-071 | Location-scoped data access enforcement | P1 | High | 1 |
| REQ-REPT2-072 | Audit logging of report execution | P2 | High | 1 |
| REQ-REPT2-080 | Report counts reconcile with source database | P1 | High | 2 |
| REQ-REPT2-081 | Aggregate, precision and timezone accuracy | P2 | High | 2 |
| REQ-REPT2-082 | Export snapshot immutability vs re-run | P3 | Medium | 1 |
| REQ-REPT2-090 | Empty-data handling for indicators and UI | P3 | Medium | 2 |
| REQ-REPT2-100 | Exploratory robustness of reporting module | P3 | Medium | 1 |
| REQ-REPT2-110 | Accessibility of report grid and export controls | P3 | Medium | 2 |
| REQ-REPT2-120 | Injection prevention and export authorization scope | P1 | High | 2 |
| REQ-REPT2-130 | Report REST API execution and authentication | P1 | High | 2 |
| REQ-REPT2-131 | Report API parameter validation | P3 | Medium | 1 |
| REQ-REPT2-140 | FHIR Measure/MeasureReport evaluation and reconciliation | P2 | High | 2 |
| REQ-REPT2-141 | FHIR Bulk Data export for analytics | P3 | Medium | 1 |
| REQ-REPT2-150 | HL7 result feed analytics consistency and dedup | P3 | Medium | 2 |

## REST API (deep, per resource) — 22 requirements, 84 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-RESTAPI-001 | Patient resource creation and required-field validation | P1 | High | 2 |
| REQ-RESTAPI-002 | Patient identifier management and uniqueness enforcement | P2 | High | 2 |
| REQ-RESTAPI-003 | Void/purge lifecycle and authorized voided-record access | P2 | High | 4 |
| REQ-RESTAPI-004 | Representations (ref/default/full/custom) selection and parsing | P2 | Low | 4 |
| REQ-RESTAPI-005 | Person/name/address/attribute CRUD and demographic validation | P2 | Medium | 5 |
| REQ-RESTAPI-006 | Encounter CRUD, datetime/visit-window validation and void cascade | P1 | High | 5 |
| REQ-RESTAPI-007 | Encounter query/filtering and paging | P2 | Low | 1 |
| REQ-RESTAPI-008 | Observation CRUD, datatype/range validation and immutability | P2 | High | 5 |
| REQ-RESTAPI-009 | Visit lifecycle, overlap prevention and active/inactive query | P1 | High | 4 |
| REQ-RESTAPI-010 | Order CRUD, dosing validation and NEW/REVISE/DISCONTINUE state model | P1 | High | 6 |
| REQ-RESTAPI-011 | Concept CRUD, search, retire and terminology mapping | P3 | Medium | 4 |
| REQ-RESTAPI-012 | Provider CRUD, search and retire semantics | P2 | Medium | 3 |
| REQ-RESTAPI-013 | Location CRUD, tag filtering and retire constraints | P3 | Medium | 3 |
| REQ-RESTAPI-014 | Query params: paging/limit/startIndex/totalCount boundaries and stable ordering | P2 | Medium | 6 |
| REQ-RESTAPI-015 | HTTP error semantics (400/401/403/404/405) without PHI/stack leakage | P1 | High | 6 |
| REQ-RESTAPI-016 | Content negotiation (Accept/Content-Type, 406/415) | P3 | Low | 2 |
| REQ-RESTAPI-017 | Concurrency control via ETag/If-Match/If-None-Match and lost-update prevention | P2 | High | 3 |
| REQ-RESTAPI-018 | API security: injection, XSS, IDOR, session invalidation and brute-force | P1 | High | 5 |
| REQ-RESTAPI-019 | Database integrity: soft-delete columns, typed obs storage and referential integrity | P3 | Medium | 3 |
| REQ-RESTAPI-020 | FHIR R4 resource read/search/create, Bundles, OperationOutcome and coding | P1 | Medium | 7 |
| REQ-RESTAPI-021 | HL7 v2 inbound ADT/ORU processing and error/NAK handling | P3 | Medium | 3 |
| REQ-RESTAPI-022 | Audit metadata across create/update/void lifecycle | P2 | High | 1 |

## Reporting & Audit Logging — 19 requirements, 63 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-RPT-001 | Users can run report definitions and report run status transitions correctly through its lifecycle | P1 | High | 4 |
| REQ-RPT-002 | Reports accept and validate date-range parameters including boundaries and malformed input | P1 | Medium | 8 |
| REQ-RPT-003 | Reports filter by single or multiple locations and enforce mandatory location selection | P2 | Medium | 3 |
| REQ-RPT-004 | Completed reports export to CSV, PDF, and Excel with accurate, well-formed content | P1 | Medium | 4 |
| REQ-RPT-005 | Exports are protected against CSV injection and mask restricted PHI per authorization | P1 | High | 2 |
| REQ-RPT-006 | Reports handle empty result sets gracefully on screen and in exports | P2 | Medium | 2 |
| REQ-RPT-007 | Reporting is ready for large datasets and concurrent runs within performance SLAs | P2 | High | 3 |
| REQ-RPT-008 | Reports can be scheduled, disabled, retried on failure, and respect timezone/DST | P2 | Medium | 5 |
| REQ-RPT-009 | Audit log captures CRUD and bulk-access actions on PHI with mandatory fields | P1 | High | 6 |
| REQ-RPT-010 | Audit log entries are immutable via UI and database, including during rollback | P1 | High | 3 |
| REQ-RPT-011 | Audit trail is tamper-evident via hash chaining, sequencing, and consistent timestamps | P1 | High | 3 |
| REQ-RPT-012 | Who-accessed-what reporting supports HIPAA accounting of disclosures and snooping detection | P1 | High | 2 |
| REQ-RPT-013 | Authentication events (login, logout, failures, lockout) are audited | P1 | High | 4 |
| REQ-RPT-014 | Reports and audit viewers are restricted by role and unauthorized attempts are logged | P1 | High | 2 |
| REQ-RPT-015 | Reporting REST API enforces auth and validates parameters | P2 | Medium | 3 |
| REQ-RPT-016 | Audit and report data map to valid FHIR R4 AuditEvent and Bundle resources | P2 | Medium | 3 |
| REQ-RPT-017 | HL7 v2 exports conform to message structure and reject malformed messages | P3 | Medium | 2 |
| REQ-RPT-018 | Report UI and export controls meet WCAG 2.1 AA accessibility | P2 | Medium | 2 |
| REQ-RPT-019 | Audit storage enforces retention minimums and performant indexed queries | P2 | High | 2 |

## Sanity Pack — 26 requirements, 45 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-SANITY2-001 | Authentication lockout and session timeout sanity | P1 | High | 2 |
| REQ-SANITY2-002 | Registration duplicate detection and identifier check-digit | P1 | High | 2 |
| REQ-SANITY2-003 | Patient search injection-safety and phonetic match | P1 | High | 2 |
| REQ-SANITY2-004 | Patient dashboard deceased-state safeguards | P1 | High | 1 |
| REQ-SANITY2-005 | Visit/encounter state and datetime integrity | P1 | High | 2 |
| REQ-SANITY2-006 | Vitals age-aware ranges and physiologic bounds | P1 | High | 2 |
| REQ-SANITY2-007 | Allergy-driven interaction alerting | P1 | High | 1 |
| REQ-SANITY2-008 | Diagnosis coding mapping and condition status transitions | P2 | Medium | 2 |
| REQ-SANITY2-009 | Appointment scheduling integrity and collision prevention | P1 | High | 2 |
| REQ-SANITY2-010 | Lab order lifecycle and critical-value notification | P1 | High | 2 |
| REQ-SANITY2-011 | Radiology accession uniqueness | P2 | Medium | 1 |
| REQ-SANITY2-012 | Pharmacy dosing safety and dispense controls | P1 | High | 2 |
| REQ-SANITY2-013 | RBAC revocation and horizontal access control | P1 | High | 2 |
| REQ-SANITY2-014 | Audit logging and report boundary integrity | P1 | High | 2 |
| REQ-SANITY2-015 | FHIR Patient read and authorization | P1 | High | 2 |
| REQ-SANITY2-016 | FHIR search, pagination and resource mapping | P2 | Medium | 3 |
| REQ-SANITY2-017 | HL7 ADT processing and error handling | P1 | High | 2 |
| REQ-SANITY2-018 | HL7 ORU result mapping | P2 | Medium | 1 |
| REQ-SANITY2-019 | Application security hardening (XSS/CSRF) | P1 | High | 2 |
| REQ-SANITY2-020 | Accessibility keyboard and alert conformance | P2 | Medium | 2 |
| REQ-SANITY2-021 | Chart load performance SLA | P2 | Medium | 1 |
| REQ-SANITY2-022 | Notification acknowledgement lifecycle | P2 | Medium | 1 |
| REQ-SANITY2-023 | Billing charge capture and void controls | P2 | Medium | 2 |
| REQ-SANITY2-024 | Telemedicine consent gating | P2 | Medium | 1 |
| REQ-SANITY2-025 | Data management merge and bulk import integrity | P1 | High | 2 |
| REQ-SANITY2-026 | Concurrent edit conflict handling | P2 | High | 1 |

## Appointment Scheduling (deep) — 53 requirements, 77 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-SCHED2-001 | Service and service-type definition with type-specific default durations | P2 | Medium | 3 |
| REQ-SCHED2-002 | Service retirement with safe handling of future appointments | P2 | Medium | 1 |
| REQ-SCHED2-003 | Service display attributes (specialty/color/duration) integrity | P3 | Low | 1 |
| REQ-SCHED2-010 | Provider weekly availability blocks constrain bookable slots | P1 | High | 1 |
| REQ-SCHED2-011 | Provider time-off and overlapping-block handling | P1 | High | 2 |
| REQ-SCHED2-012 | Timezone/DST-correct slot and display handling | P2 | High | 3 |
| REQ-SCHED2-020 | Slot generation length equals service-type duration within window | P1 | Medium | 2 |
| REQ-SCHED2-021 | Configurable inter-appointment buffer/gap in slot generation | P2 | Medium | 2 |
| REQ-SCHED2-022 | Slot availability excludes booked/held; concurrency-safe | P1 | High | 2 |
| REQ-SCHED2-030 | Overbooking capacity and role-based rules | P1 | High | 2 |
| REQ-SCHED2-031 | Overbooking override requires reason, audit, and threshold logic | P2 | Medium | 2 |
| REQ-SCHED2-040 | Waitlist add/remove and ordering | P2 | Medium | 2 |
| REQ-SCHED2-041 | Waitlist auto-offer, expiry, and preference matching | P2 | Medium | 3 |
| REQ-SCHED2-050 | Recurring series occurrence generation | P2 | Medium | 1 |
| REQ-SCHED2-051 | Recurring series conflict handling and max-occurrence bounds | P2 | Medium | 2 |
| REQ-SCHED2-052 | Recurring series edit scopes (single / this-and-following) | P2 | Medium | 2 |
| REQ-SCHED2-053 | Recurring series cancellation cascade with audit | P2 | Medium | 1 |
| REQ-SCHED2-060 | Reschedule frees original slot and retains history | P1 | High | 1 |
| REQ-SCHED2-061 | Reschedule conflict and past-time validation | P1 | High | 2 |
| REQ-SCHED2-062 | Reschedule cascade to reminders and group participants | P2 | Medium | 2 |
| REQ-SCHED2-070 | Reminder scheduled at configured lead time | P2 | Medium | 1 |
| REQ-SCHED2-071 | Reminder voided on cancellation | P2 | Medium | 1 |
| REQ-SCHED2-072 | Reminder channel/preference/consent enforcement | P2 | Medium | 1 |
| REQ-SCHED2-073 | Reminder content minimum-necessary PHI | P1 | High | 1 |
| REQ-SCHED2-080 | Check-in/out state machine and timestamps | P1 | Medium | 2 |
| REQ-SCHED2-081 | Invalid check-in/out state transitions rejected | P2 | Medium | 1 |
| REQ-SCHED2-082 | Check-in links visit/encounter; kiosk identity verification | P2 | Medium | 2 |
| REQ-SCHED2-090 | No-show marking after grace and series isolation | P2 | Medium | 2 |
| REQ-SCHED2-091 | No-show not allowed before start time | P2 | Medium | 1 |
| REQ-SCHED2-092 | Repeated no-show threshold flagging | P3 | Low | 1 |
| REQ-SCHED2-100 | Multi-resource booking conflict decision table | P1 | High | 1 |
| REQ-SCHED2-101 | Patient double-booking and adjacency boundary rules | P1 | High | 2 |
| REQ-SCHED2-110 | FHIR Appointment create/persist round-trip | P2 | Medium | 1 |
| REQ-SCHED2-111 | FHIR Appointment validation (status/participant rules) | P2 | Medium | 2 |
| REQ-SCHED2-112 | FHIR Slot/Schedule free-slot search | P2 | Medium | 1 |
| REQ-SCHED2-113 | FHIR Appointment cancel consistency with UI | P2 | Medium | 1 |
| REQ-SCHED2-120 | HL7 SIU^S12 inbound appointment creation | P2 | Medium | 1 |
| REQ-SCHED2-121 | HL7 SIU cancel/reschedule idempotent processing | P2 | Medium | 2 |
| REQ-SCHED2-122 | HL7 SIU unresolved-patient rejection | P2 | Medium | 1 |
| REQ-SCHED2-130 | API optimistic concurrency (ETag/version) | P1 | High | 1 |
| REQ-SCHED2-131 | API datetime/payload validation | P2 | Medium | 1 |
| REQ-SCHED2-132 | API filtering and pagination integrity | P3 | Low | 1 |
| REQ-SCHED2-140 | Location-scoped authorization for scheduling | P1 | High | 1 |
| REQ-SCHED2-141 | IDOR protection on appointment access | P1 | High | 1 |
| REQ-SCHED2-142 | Audit logging of all scheduling mutations | P1 | High | 1 |
| REQ-SCHED2-143 | Webhook signature validation for delivery callbacks | P2 | Medium | 1 |
| REQ-SCHED2-150 | DB-level exclusive-slot uniqueness constraint | P1 | High | 1 |
| REQ-SCHED2-151 | Soft-delete/void of cancelled appointments | P2 | Medium | 1 |
| REQ-SCHED2-152 | Referential integrity of visit/encounter links on reschedule | P2 | Medium | 1 |
| REQ-SCHED2-160 | Keyboard-navigable scheduling calendar | P2 | Medium | 1 |
| REQ-SCHED2-161 | Screen-reader announcements for slots and errors | P2 | Medium | 1 |
| REQ-SCHED2-170 | Exploratory slot-state consistency and edge probing | P2 | Medium | 2 |
| REQ-SCHED2-180 | Cancellation policy fee/penalty decision logic | P3 | Low | 1 |

## Security (functional/readiness, design-only) — 15 requirements, 68 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-SEC-001 | Authentication strength and identity verification (credentials, MFA, password recovery, anti-enumeration) | P1 | High | 10 |
| REQ-SEC-002 | Password complexity and reuse policy enforcement | P1 | High | 3 |
| REQ-SEC-003 | Account lockout and rate limiting against brute force | P1 | High | 4 |
| REQ-SEC-004 | Secure session management (cookie flags, fixation, timeout, logout, token expiry) | P1 | High | 7 |
| REQ-SEC-005 | Role/privilege-based authorization and least privilege (UI, REST, FHIR scopes, HL7 senders) | P1 | High | 6 |
| REQ-SEC-006 | Object-level authorization / IDOR protection on patient records and resources | P1 | High | 6 |
| REQ-SEC-007 | Input validation against injection and malformed/oversized input | P1 | High | 6 |
| REQ-SEC-008 | XSS resilience via output encoding and sanitization | P1 | High | 3 |
| REQ-SEC-009 | Transport security and security response headers (HTTPS/TLS/HSTS/CSP) | P1 | High | 5 |
| REQ-SEC-010 | PHI exposure prevention in URLs, logs, caches, and browser history | P1 | High | 4 |
| REQ-SEC-011 | Secure password storage (salted one-way hashing) | P1 | High | 2 |
| REQ-SEC-012 | CSRF protection on state-changing endpoints | P1 | High | 2 |
| REQ-SEC-013 | Safe error handling without information leakage | P2 | Medium | 2 |
| REQ-SEC-014 | Audit logging of security and PHI-access events (tamper-evident) | P1 | High | 6 |
| REQ-SEC-015 | Data-at-rest protection (encrypted backups, externalized secrets) | P1 | High | 2 |

## Security (OWASP deep, design-only) — 27 requirements, 82 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-SECDEEP-001 | IDOR / object-level authorization on patient & clinical resources (REST/FHIR/batch) | P1 | High | 3 |
| REQ-SECDEEP-002 | Horizontal & contextual (location/restricted/break-glass) access control enforcement | P1 | High | 5 |
| REQ-SECDEEP-003 | Vertical privilege escalation & forced-browse prevention | P1 | High | 2 |
| REQ-SECDEEP-004 | FHIR/SMART patient-compartment & account-patient binding enforcement | P1 | High | 2 |
| REQ-SECDEEP-005 | Mass-assignment / over-posting protection on writes | P2 | Medium | 1 |
| REQ-SECDEEP-006 | PHI in transit: TLS, HSTS, no-PHI-in-URL, FHIR cleartext refusal | P1 | High | 4 |
| REQ-SECDEEP-007 | Cryptography at rest: DB/backup encryption, password hashing, key rotation | P1 | High | 3 |
| REQ-SECDEEP-008 | Injection defense: SQL/HQL/NoSQL/parameterized queries | P1 | High | 5 |
| REQ-SECDEEP-009 | LDAP injection / auth-backend filter escaping | P2 | Medium | 1 |
| REQ-SECDEEP-010 | Command injection / XXE / path traversal protection | P2 | Medium | 2 |
| REQ-SECDEEP-011 | XSS (stored/reflected) & header/CRLF injection output handling | P1 | High | 3 |
| REQ-SECDEEP-012 | Secure-by-design: fail-closed authZ, clinical safety gates, consent gating | P2 | High | 3 |
| REQ-SECDEEP-013 | Abuse-case design: reset-flow hardening & anti-enumeration | P1 | High | 2 |
| REQ-SECDEEP-014 | Security headers, CORS, methods, clickjacking, cookie hardening | P2 | Medium | 5 |
| REQ-SECDEEP-015 | Misconfiguration: defaults, error leakage, exposed consoles, excessive data exposure | P1 | High | 5 |
| REQ-SECDEEP-016 | Session management: lockout, fixation, cookie flags, logout, timeout, concurrency | P1 | High | 6 |
| REQ-SECDEEP-017 | MFA & password policy enforcement | P1 | High | 2 |
| REQ-SECDEEP-018 | JWT integrity: alg/none, tampering, expiry, token storage | P1 | High | 4 |
| REQ-SECDEEP-019 | OAuth/SMART: scope, redirect_uri, PKCE enforcement | P1 | High | 3 |
| REQ-SECDEEP-020 | Audit immutability & FHIR Provenance/AuditEvent integrity | P1 | High | 2 |
| REQ-SECDEEP-021 | Integrity: signed updates, safe deserialization, optimistic locking | P2 | Medium | 3 |
| REQ-SECDEEP-022 | Logging of auth/PHI events without sensitive data; accurate timestamps | P1 | High | 4 |
| REQ-SECDEEP-023 | Monitoring/alerting & log tamper detection | P3 | Medium | 2 |
| REQ-SECDEEP-024 | SSRF protection across references, import/webhook, redirects | P1 | High | 3 |
| REQ-SECDEEP-025 | CSRF token enforcement and unpredictability | P1 | High | 2 |
| REQ-SECDEEP-026 | Rate limiting on auth, search/bulk-read and across endpoint classes | P2 | High | 3 |
| REQ-SECDEEP-027 | HL7v2 interface authZ and segment/escape injection safety | P2 | High | 2 |

## Smoke Pack — 31 requirements, 50 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-SMOKE2-001 | Login critical-path and session-location enforcement | P1 | High | 2 |
| REQ-SMOKE2-002 | Session lifecycle: lockout, idle timeout, logout/back-nav protection | P1 | High | 3 |
| REQ-SMOKE2-003 | Patient registration critical path | P1 | High | 1 |
| REQ-SMOKE2-004 | Registration field validation (DOB, estimated age boundaries) | P2 | Medium | 2 |
| REQ-SMOKE2-005 | Potential duplicate detection on registration | P2 | High | 1 |
| REQ-SMOKE2-006 | Find Patient search (identifier, name, diacritics, partial) | P1 | Medium | 2 |
| REQ-SMOKE2-007 | Search input injection resistance | P2 | High | 1 |
| REQ-SMOKE2-008 | Patient dashboard render and state indicators | P1 | Medium | 2 |
| REQ-SMOKE2-009 | Visit lifecycle and overlap prevention | P1 | High | 3 |
| REQ-SMOKE2-010 | Vitals capture critical path | P1 | Medium | 1 |
| REQ-SMOKE2-011 | Vitals range validation and derived values (BMI) | P2 | High | 2 |
| REQ-SMOKE2-012 | Allergy capture and NKA/unknown state handling | P2 | High | 2 |
| REQ-SMOKE2-013 | Coded diagnosis rank and certainty | P2 | Medium | 1 |
| REQ-SMOKE2-014 | Appointment booking, conflict and status state machine | P1 | High | 3 |
| REQ-SMOKE2-015 | Drug order critical path and order state transitions | P1 | High | 2 |
| REQ-SMOKE2-016 | Allergy-interaction safety alerting on ordering | P1 | High | 1 |
| REQ-SMOKE2-017 | Lab and radiology order/result lifecycle | P2 | Medium | 2 |
| REQ-SMOKE2-018 | Pharmacy dispensing and quantity validation | P2 | High | 2 |
| REQ-SMOKE2-019 | RBAC enforcement across roles and API | P1 | High | 2 |
| REQ-SMOKE2-020 | Data management: merge and persistence integrity | P2 | High | 2 |
| REQ-SMOKE2-021 | Audit logging of PHI access and changes | P1 | High | 1 |
| REQ-SMOKE2-022 | Report execution build verification | P2 | Medium | 1 |
| REQ-SMOKE2-023 | FHIR CapabilityStatement metadata | P1 | Medium | 1 |
| REQ-SMOKE2-024 | FHIR resource read/search mapping correctness | P2 | Medium | 2 |
| REQ-SMOKE2-025 | FHIR endpoint authentication enforcement | P1 | High | 1 |
| REQ-SMOKE2-026 | HL7 v2 ADT/ORU ingestion and ACK/NACK handling | P2 | High | 2 |
| REQ-SMOKE2-027 | Accessibility conformance on login/registration | P2 | Medium | 1 |
| REQ-SMOKE2-028 | Critical-result clinician notification | P2 | High | 1 |
| REQ-SMOKE2-029 | Charge generation from billable service | P3 | Medium | 1 |
| REQ-SMOKE2-030 | Telemedicine session link and access control | P3 | Medium | 1 |
| REQ-SMOKE2-031 | End-to-end cross-module patient journey integrity | P2 | High | 1 |

## Find Patient Record / Search — 24 requirements, 64 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-SRCH-001 | Search patients by name (given, family, full, configured name parts) | P1 | High | 4 |
| REQ-SRCH-002 | Partial / prefix name matching | P1 | Medium | 2 |
| REQ-SRCH-003 | Case-insensitive name search | P1 | Medium | 2 |
| REQ-SRCH-004 | Diacritic/accent-insensitive name matching | P2 | Medium | 2 |
| REQ-SRCH-005 | Search by Patient ID / MRN (full, partial, check-digit) | P1 | High | 3 |
| REQ-SRCH-006 | No-results empty-state handling | P1 | Medium | 3 |
| REQ-SRCH-007 | Minimum-character threshold before search executes | P2 | Medium | 3 |
| REQ-SRCH-008 | Special character, whitespace and oversized input handling | P2 | Medium | 5 |
| REQ-SRCH-009 | Injection resistance (SQLi/XSS) in search input | P1 | High | 2 |
| REQ-SRCH-010 | Authentication and authorization for patient search | P1 | High | 2 |
| REQ-SRCH-011 | Audit logging of patient search and record access | P1 | High | 1 |
| REQ-SRCH-012 | Pagination / scroll of large result sets | P2 | Medium | 4 |
| REQ-SRCH-013 | Recent patients list | P2 | Medium | 2 |
| REQ-SRCH-014 | Open patient record from search results | P1 | High | 2 |
| REQ-SRCH-015 | Search input state transitions (clear, debounce, switch) | P2 | Medium | 3 |
| REQ-SRCH-016 | Search performance and indexing readiness | P1 | High | 5 |
| REQ-SRCH-017 | Input-class routing logic (name vs id vs no-match) | P2 | Medium | 2 |
| REQ-SRCH-018 | Accessibility of search UI (WCAG 2.1 AA) | P2 | Medium | 3 |
| REQ-SRCH-019 | REST patient search API contract and pagination | P1 | High | 3 |
| REQ-SRCH-020 | FHIR R4 Patient search (name, identifier, given/family, _count) | P1 | High | 4 |
| REQ-SRCH-021 | HL7v2 QBP^Q22/RSP patient query support | P2 | Medium | 2 |
| REQ-SRCH-022 | Search inclusion rules for voided and deceased patients | P1 | High | 2 |
| REQ-SRCH-023 | Resource Adapter normalization across backend systems | P2 | High | 1 |
| REQ-SRCH-024 | omiiCARE multi-tenant isolation and PHI masking in search | P1 | High | 2 |

## Advanced Search & Filters — 19 requirements, 54 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-SRCH2-001 | Multi-criteria search with AND semantics and filter state management | P2 | Medium | 3 |
| REQ-SRCH2-002 | Identifier search with check-digit, whitespace and retired-identifier handling | P1 | High | 4 |
| REQ-SRCH2-003 | Name search: partial, multi-token, minimum-length and punctuation handling | P2 | Medium | 4 |
| REQ-SRCH2-004 | Fuzzy/phonetic matching with tolerance and false-positive guardrails | P2 | Medium | 4 |
| REQ-SRCH2-005 | Diacritics and Unicode normalization in name search | P2 | Medium | 3 |
| REQ-SRCH2-006 | DOB/age search: exact, estimated, range, validation and boundary correctness | P2 | Medium | 4 |
| REQ-SRCH2-007 | Attribute and alternate-identifier-type search | P2 | Medium | 3 |
| REQ-SRCH2-008 | Result sorting with locale collation and deterministic tie-breaking | P3 | Low | 2 |
| REQ-SRCH2-009 | Result limits, pagination continuity and concurrency consistency | P2 | Medium | 3 |
| REQ-SRCH2-010 | No-result and too-many-results handling with refine guidance | P2 | Medium | 2 |
| REQ-SRCH2-011 | Saved searches: persistence and access scoping | P2 | Medium | 2 |
| REQ-SRCH2-012 | Search performance and index usage at volume | P2 | High | 2 |
| REQ-SRCH2-013 | Search input security: SQLi and XSS prevention | P1 | High | 2 |
| REQ-SRCH2-014 | Access-controlled search results and autocomplete leak prevention | P1 | High | 2 |
| REQ-SRCH2-015 | Audit logging of search and record access | P2 | Medium | 1 |
| REQ-SRCH2-016 | FHIR Patient search parameters, modifiers, paging and error handling | P2 | Medium | 7 |
| REQ-SRCH2-017 | HL7 PDQ/PIX demographic and identifier query support | P3 | Medium | 3 |
| REQ-SRCH2-018 | Search accessibility: keyboard operation and screen-reader announcements | P2 | Medium | 2 |
| REQ-SRCH2-019 | Exploratory robustness of search criteria handling | P3 | Medium | 1 |

## Telemedicine — 14 requirements, 45 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-TELE-001 | Schedule virtual telemedicine visit with conflict-free booking and join link provisioning | P1 | High | 3 |
| REQ-TELE-002 | Availability windows, time-zone handling, and no-show lifecycle for virtual slots | P2 | Medium | 3 |
| REQ-TELE-003 | Secure single-use, signed, expiring join links with identity-bound access control | P1 | High | 6 |
| REQ-TELE-004 | Waiting room queue, provider admit, and concurrency-safe session creation | P1 | High | 4 |
| REQ-TELE-005 | Video/audio session establishment with graceful media-permission degradation | P1 | High | 3 |
| REQ-TELE-006 | Connectivity resilience: reconnection grace, prolonged-drop handling, adaptive quality | P1 | High | 4 |
| REQ-TELE-007 | Telehealth e-consent capture, enforcement, versioning, and FHIR persistence | P1 | High | 4 |
| REQ-TELE-008 | Dual-party recording consent, visible indicator, and encrypted restricted storage | P1 | High | 3 |
| REQ-TELE-009 | In-session clinical notes and orders with safety alerts on the telemedicine encounter | P1 | High | 3 |
| REQ-TELE-010 | Session end with mandatory documentation and accurate session/encounter records | P1 | Medium | 3 |
| REQ-TELE-011 | Fallback to telephone visit with modality change captured and audited | P1 | High | 2 |
| REQ-TELE-012 | Cross-device join, resume, and pairwise reliability across OS/browser/network | P2 | Medium | 3 |
| REQ-TELE-013 | Accessibility of waiting room and session controls (WCAG 2.1 AA, captions) | P2 | Medium | 2 |
| REQ-TELE-014 | HL7 interoperability for telemedicine registration and in-session orders | P3 | Medium | 2 |

## Telemedicine (deep) — 24 requirements, 48 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-TELE2-001 | Pre-visit device self-test and permission handling | P1 | High | 2 |
| REQ-TELE2-002 | Pre-visit connectivity check and mode recommendation | P2 | Medium | 2 |
| REQ-TELE2-003 | Virtual waiting room and provider queue | P1 | High | 4 |
| REQ-TELE2-004 | Video/audio session establishment with ICE/TURN fallback | P1 | High | 3 |
| REQ-TELE2-005 | In-call media quality and device controls | P2 | Medium | 3 |
| REQ-TELE2-006 | Screen sharing with PHI-safety and concurrency control | P2 | High | 3 |
| REQ-TELE2-007 | In-session documentation linked to telehealth visit | P1 | High | 1 |
| REQ-TELE2-008 | Telehealth encounter virtual class/type via FHIR R4 | P2 | Medium | 1 |
| REQ-TELE2-009 | In-session ordering and ordering RBAC | P2 | Medium | 2 |
| REQ-TELE2-010 | E-prescribe safety checks and EPCS step-up auth | P1 | High | 2 |
| REQ-TELE2-011 | E-prescription as valid FHIR MedicationRequest | P2 | Medium | 1 |
| REQ-TELE2-012 | Recording consent, jurisdiction rules and indicator | P1 | High | 4 |
| REQ-TELE2-013 | Recording storage encryption and access control | P2 | High | 1 |
| REQ-TELE2-014 | Session end, documentation completion and visit status | P1 | High | 2 |
| REQ-TELE2-015 | Accurate session timestamps for billing | P2 | Medium | 1 |
| REQ-TELE2-016 | Phone/audio-only fallback with masking | P1 | High | 3 |
| REQ-TELE2-017 | Cross-device/browser compatibility and network continuity | P2 | Medium | 3 |
| REQ-TELE2-018 | Accessibility of controls and captions | P2 | Medium | 2 |
| REQ-TELE2-019 | Join-link security: single-use, time-bound, session binding | P1 | High | 2 |
| REQ-TELE2-020 | Media/transport encryption and idle timeout | P2 | Medium | 2 |
| REQ-TELE2-021 | Telehealth lifecycle audit logging | P2 | Medium | 1 |
| REQ-TELE2-022 | HL7 v2 ADT generation for telehealth | P3 | Low | 1 |
| REQ-TELE2-023 | Telehealth session API authorization | P2 | Medium | 1 |
| REQ-TELE2-024 | Session stability under chaotic interaction | P3 | Low | 1 |

## Triage & Emergency — 19 requirements, 56 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-TRIAGE-001 | ESI acuity assignment (levels 1-5) and validation | P1 | High | 7 |
| REQ-TRIAGE-002 | Age-specific danger-zone vitals influence on acuity | P1 | High | 4 |
| REQ-TRIAGE-003 | Re-triage and acuity history preservation | P2 | Medium | 1 |
| REQ-TRIAGE-004 | Fast-track routing eligibility and capacity handling | P2 | Medium | 4 |
| REQ-TRIAGE-005 | Triage vitals capture, validation and derived values | P1 | High | 4 |
| REQ-TRIAGE-006 | FHIR exposure of triage observations and encounter priority | P2 | Medium | 2 |
| REQ-TRIAGE-007 | ED queue priority ordering and tie-break rules | P1 | High | 4 |
| REQ-TRIAGE-008 | Privileged, audited manual queue override | P2 | High | 2 |
| REQ-TRIAGE-009 | Wait-time escalation flagging | P3 | Medium | 1 |
| REQ-TRIAGE-010 | Auto-escalation and notification reliability for critical acuity | P1 | High | 2 |
| REQ-TRIAGE-011 | Clinical screening pathways (sepsis/stroke/STEMI) and time-critical clocks | P1 | High | 4 |
| REQ-TRIAGE-012 | Unknown/unidentified patient registration with temporary identifiers | P1 | High | 2 |
| REQ-TRIAGE-013 | Unknown-patient merge to confirmed identity with audited PHI access | P1 | High | 2 |
| REQ-TRIAGE-014 | Mass-casualty incident mode and START tagging (assumption) | P1 | High | 4 |
| REQ-TRIAGE-015 | Time-critical SLA timers, breach alerts and acknowledgement | P1 | High | 4 |
| REQ-TRIAGE-016 | HL7 ADT interop for ED registration and acuity updates | P2 | Medium | 3 |
| REQ-TRIAGE-017 | Triage REST API contract, authz and context protection | P1 | High | 3 |
| REQ-TRIAGE-018 | Immutable audit of acuity assignments and changes | P2 | High | 1 |
| REQ-TRIAGE-019 | Triage UI accessibility (WCAG 2.1 AA, non-color cues) | P3 | Medium | 2 |

## UAT Scenarios — 37 requirements, 64 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-UAT2-001 | Walk-in patient registration and mandatory demographics | P1 | High | 2 |
| REQ-UAT2-002 | Duplicate prevention via demographic matching | P1 | High | 1 |
| REQ-UAT2-003 | Newborn registration with estimated DOB and relationships | P2 | Medium | 1 |
| REQ-UAT2-004 | Triage vitals, acuity and queue routing | P1 | High | 1 |
| REQ-UAT2-005 | Vital range validation and acuity decision logic | P1 | High | 2 |
| REQ-UAT2-006 | Clinician consultation, drafts and problem list | P1 | High | 3 |
| REQ-UAT2-007 | Allergy interaction alert and override audit | P1 | High | 2 |
| REQ-UAT2-008 | Signed encounter amendment versioning | P1 | High | 1 |
| REQ-UAT2-009 | Prescription verification and dispense lifecycle | P1 | High | 2 |
| REQ-UAT2-010 | Weight-based dose safety checks | P1 | High | 1 |
| REQ-UAT2-011 | Controlled substance second-witness control | P1 | High | 1 |
| REQ-UAT2-012 | Lab specimen acceptance and result posting | P1 | High | 2 |
| REQ-UAT2-013 | Critical value call-back and result amendment | P1 | High | 2 |
| REQ-UAT2-014 | Claim generation, validation and denial handling | P1 | High | 3 |
| REQ-UAT2-015 | Coverage split and refund accounting | P1 | High | 2 |
| REQ-UAT2-016 | User onboarding, deactivation and access | P1 | High | 2 |
| REQ-UAT2-017 | Role privilege propagation | P2 | Medium | 1 |
| REQ-UAT2-018 | Care handoff and shift change continuity | P1 | High | 2 |
| REQ-UAT2-019 | Appointment lifecycle and conflict prevention | P1 | High | 3 |
| REQ-UAT2-020 | Referral creation and closed-loop status | P2 | Medium | 2 |
| REQ-UAT2-021 | Program enrollment and state workflow | P2 | Medium | 2 |
| REQ-UAT2-022 | Patient record merge integrity | P1 | High | 2 |
| REQ-UAT2-023 | Consent restriction and break-the-glass | P1 | High | 2 |
| REQ-UAT2-024 | Inbound HL7 ADT/ORU reconciliation | P1 | High | 2 |
| REQ-UAT2-025 | Outbound FHIR export and bundle atomicity | P2 | Medium | 2 |
| REQ-UAT2-026 | Telemedicine consult and identity guard | P2 | Medium | 2 |
| REQ-UAT2-027 | Maternal and pediatric clinical capture | P2 | Medium | 2 |
| REQ-UAT2-028 | Unidentified emergency patient reconciliation | P1 | High | 1 |
| REQ-UAT2-029 | ED order set and alert generation | P2 | Medium | 1 |
| REQ-UAT2-030 | Discharge reconciliation and open-order guard | P1 | High | 2 |
| REQ-UAT2-031 | Concurrency control and void retention | P2 | High | 2 |
| REQ-UAT2-032 | Scoped reporting and privacy suppression | P2 | Medium | 2 |
| REQ-UAT2-033 | Accessible workflow operation | P2 | Medium | 2 |
| REQ-UAT2-034 | Idle session timeout protection | P1 | High | 1 |
| REQ-UAT2-035 | Wrong-patient context guard | P1 | High | 1 |
| REQ-UAT2-036 | Cross-backend portability via adapter | P3 | Medium | 1 |
| REQ-UAT2-037 | Exploratory journey coverage | P3 | Medium | 1 |

## Usability & UX — 15 requirements, 48 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-USAB-001 | UI consistency: labels, dates, terminology, iconography, locale uniform across modules | P2 | Medium | 4 |
| REQ-USAB-002 | Confirmation required on destructive actions (void/retire/discard) with reason and audit | P1 | High | 5 |
| REQ-USAB-003 | Unsaved-changes guard prevents accidental data loss on navigation | P2 | High | 2 |
| REQ-USAB-004 | Error prevention: duplicate-submission, out-of-range and wrong-patient safeguards | P1 | High | 3 |
| REQ-USAB-005 | Error recovery: data preserved and retryable after validation or transient failure | P2 | High | 2 |
| REQ-USAB-006 | Helpful messages: specific, safe, accessible, no stack traces, permission/session clarity | P2 | High | 5 |
| REQ-USAB-007 | Navigation efficiency: minimal click paths, persistent patient context, deep-link return | P2 | High | 5 |
| REQ-USAB-008 | Learnability: field help, recognizable iconography, first-time-user completability | P3 | Low | 3 |
| REQ-USAB-009 | Undo/reversibility: unvoid restores data; soft-delete preserves audit chain | P2 | Medium | 3 |
| REQ-USAB-010 | Keyboard operability: full keyboard nav, visible focus, modal focus management | P1 | High | 3 |
| REQ-USAB-011 | Empty states are informative with clear next actions | P3 | Low | 3 |
| REQ-USAB-012 | Loading states: indicators, no layout shift, timeout handling | P2 | Medium | 3 |
| REQ-USAB-013 | Error/status states: accessible, color-independent, reflow-safe, partial-failure clarity | P2 | Medium | 4 |
| REQ-USAB-014 | Standards error surfacing: FHIR OperationOutcome and HL7 v2 failures human-readable | P2 | Medium | 2 |
| REQ-USAB-015 | Alert fatigue management: interruptive prompt density tuned for safety vs reflexive dismissal | P2 | Medium | 1 |

## Visits & Encounters — 23 requirements, 68 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-VISIT-001 | Start a new visit with a valid visit type, location, and start mode | P1 | High | 4 |
| REQ-VISIT-002 | Prevent concurrent active visits for the same patient | P1 | High | 2 |
| REQ-VISIT-003 | Visit start datetime must not be in the future | P2 | Medium | 3 |
| REQ-VISIT-004 | Add retrospective past visits within allowed range | P2 | Medium | 1 |
| REQ-VISIT-005 | Visit/encounter datetime chronology and birthdate validation | P1 | High | 4 |
| REQ-VISIT-006 | End/stop, reopen, and lifecycle transitions of a visit | P1 | High | 5 |
| REQ-VISIT-007 | Active visits list display, search, and real-time updates | P2 | Medium | 4 |
| REQ-VISIT-008 | Manage visit types (create, uniqueness, retire) | P2 | Medium | 3 |
| REQ-VISIT-009 | Create clinical encounters linked to a visit (UI and API) | P1 | High | 3 |
| REQ-VISIT-010 | Encounter datetime must fall within visit window and not be future | P1 | High | 4 |
| REQ-VISIT-011 | Manage encounter providers and roles | P2 | Medium | 6 |
| REQ-VISIT-012 | Prevent overlapping visits for the same patient | P1 | High | 4 |
| REQ-VISIT-013 | Merge visits safely preserving encounters and patient boundaries | P2 | High | 3 |
| REQ-VISIT-014 | Visit note encounter with note, diagnoses, and provider | P1 | High | 4 |
| REQ-VISIT-015 | Authorization and authentication for visit/encounter actions | P1 | High | 2 |
| REQ-VISIT-016 | Audit logging of visit and encounter actions | P2 | High | 1 |
| REQ-VISIT-017 | FHIR R4 Encounter mapping (status, period, participant) | P2 | Medium | 4 |
| REQ-VISIT-018 | HL7 v2 ADT inbound admit/discharge to visit handling | P2 | Medium | 3 |
| REQ-VISIT-019 | Database persistence and soft-delete integrity of visits | P2 | Medium | 3 |
| REQ-VISIT-020 | Accessibility of visit forms (WCAG 2.1 AA) | P2 | Medium | 2 |
| REQ-VISIT-021 | Telehealth visit creation with session link (platform) | P3 | Medium | 1 |
| REQ-VISIT-022 | Billable encounter generation on visit completion (platform) | P3 | Medium | 1 |
| REQ-VISIT-023 | Consent enforcement before recording clinical visit data | P2 | High | 1 |

## Vitals & Observations — 29 requirements, 67 test-case links

| Requirement ID | Title | Priority | Risk | #Tests |
|---|---|---|---|---|
| REQ-VITAL-001 | Capture and persist patient height with unit | P1 | High | 1 |
| REQ-VITAL-002 | Capture and persist patient weight with unit | P1 | High | 1 |
| REQ-VITAL-003 | Capture systolic/diastolic blood pressure | P1 | High | 1 |
| REQ-VITAL-004 | Capture pulse rate | P1 | Medium | 1 |
| REQ-VITAL-005 | Capture body temperature | P1 | Medium | 1 |
| REQ-VITAL-006 | Capture oxygen saturation (SpO2) | P1 | High | 1 |
| REQ-VITAL-007 | Capture respiratory rate | P2 | Medium | 1 |
| REQ-VITAL-008 | Capture a full vitals set in one encounter | P1 | High | 1 |
| REQ-VITAL-009 | Auto-calculate and recalculate BMI from height/weight | P1 | Medium | 4 |
| REQ-VITAL-010 | Enforce required vs optional vital fields | P2 | Medium | 2 |
| REQ-VITAL-011 | Validate numeric input (type, sign, locale) | P2 | Medium | 4 |
| REQ-VITAL-012 | Enforce height absolute range boundaries | P2 | Medium | 4 |
| REQ-VITAL-013 | Enforce temperature range and decimal precision | P2 | High | 4 |
| REQ-VITAL-014 | Enforce SpO2 0-100 percent bounds | P2 | Medium | 2 |
| REQ-VITAL-015 | Flag abnormal/critical blood pressure | P1 | High | 3 |
| REQ-VITAL-016 | Decision-table classification of abnormal vitals (temp/pulse/SpO2) | P1 | High | 3 |
| REQ-VITAL-017 | Handle unit selection, conversion and display | P2 | Medium | 4 |
| REQ-VITAL-018 | Persist vitals under a single encounter with provider/datetime | P1 | High | 3 |
| REQ-VITAL-019 | Display latest and historical vitals on dashboard | P1 | Medium | 3 |
| REQ-VITAL-020 | Edit existing observations with state tracking | P2 | Medium | 2 |
| REQ-VITAL-021 | Void observations with mandatory reason and audit retention | P2 | High | 3 |
| REQ-VITAL-022 | Role-based access control for vitals capture | P1 | High | 2 |
| REQ-VITAL-023 | Audit logging of edit/void actions | P2 | High | 1 |
| REQ-VITAL-024 | Protect vitals PHI against injection and unauthenticated access | P1 | High | 3 |
| REQ-VITAL-025 | REST API for creating/validating vitals encounters | P1 | High | 2 |
| REQ-VITAL-026 | FHIR R4 vital-signs Observation correctness (LOINC/UCUM/components) | P1 | High | 4 |
| REQ-VITAL-027 | HL7 v2 ORU ingestion and abnormal-flag mapping | P2 | High | 2 |
| REQ-VITAL-028 | Accessibility of the vitals capture and display UI | P3 | Medium | 3 |
| REQ-VITAL-029 | Cross-backend vitals parity via Resource Adapter Layer | P2 | Medium | 1 |

