# Workflow Diagrams
## OpenMRS Reference Application — Multi-System Healthcare QA Reference

| Field | Value |
|---|---|
| Document Type | Workflow Diagrams (process / sequence reference) |
| Primary Reference System | OpenMRS Reference Application (legacy O2 — `https://o2.openmrs.org`; modern demo O3 — `o3.openmrs.org`) |
| Secondary Targets (via Resource Adapter Layer) | OpenEMR, HAPI FHIR, SMART Health IT, in-house omiiCARE |
| Status | Baseline (reverse-engineered) |
| Date | 2026-07-01 |
| Traceability | Cross-referenced to requirement catalog (472 requirements `REQ-<PREFIX>-NNN`; 1,349 manual test cases via RTM) |
| Standards Footprint | FHIR R4 (4.0.1), HL7 v2 (ADT/ORM/ORU), ICD-10, SNOMED CT, LOINC |

> **Assumption marking:** Statements beyond the VERIFIED OpenMRS facts are tagged **(Assumption)**. Verified facts are stated plainly.
> **Diagram conventions:** Rounded nodes = UI screens/states; rectangles = system actions; diamonds = decisions/RBAC gates; `{{...}}` = persistence events; dashed edges = async/integration; subgraphs labelled `RAL` = Resource Adapter Layer indirection points where alternate backends bind.

---

## 1. Purpose & How to Read These Diagrams

This document renders the **primary workflows** of the OpenMRS Reference Application as Mermaid flowcharts and sequence diagrams, each annotated with the requirement IDs (`REQ-<PREFIX>-NNN`) and the UI selectors / API endpoints that automated and manual tests assert against. Every workflow is drawn so that the OpenMRS-specific REST/FHIR call sits behind a **Resource Adapter Layer (RAL)** node, making the same workflow re-bindable to OpenEMR, HAPI FHIR, SMART Health IT, or omiiCARE without changing the business process.

### 1.1 Workflow Index

| # | Workflow | Module / Prefix | Primary Endpoint(s) | Key Requirements |
|---|---|---|---|---|
| 1 | Login + RBAC gate | AUTH / RBAC / SEC | `POST /ws/rest/v1/session` | REQ-AUTH-001..040, REQ-RBAC-001..030, REQ-SEC-001..060 |
| 2 | Patient registration | REG | `POST /ws/rest/v1/patient` | REQ-REG-001..080 |
| 3 | Find & open patient | SRCH / PDASH | `GET /ws/rest/v1/patient?q=` | REQ-SRCH-001..045, REQ-PDASH-001..060 |
| 4 | Start visit & capture vitals | VISIT / VITAL | `POST /ws/rest/v1/visit`, `/encounter`, `/obs` | REQ-VISIT-001..050, REQ-VITAL-001..040 |
| 5 | Place lab order & result | ORDLAB / HL7 | `POST /ws/rest/v1/order`, ORM/ORU | REQ-ORDLAB-001..060, REQ-HL7-010..030 |
| 6 | Appointment booking | APPT | `POST /ws/rest/v1/appointmentscheduling/appointment` | REQ-APPT-001..050 |
| 7 | Prescription (medication) | PHARM / FHIR | `MedicationRequest` (FHIR R4) | REQ-PHARM-001..055, REQ-FHIR-020..040 |

---

## 2. Workflow 1 — Login & RBAC Gate

**Verified:** Login requires selecting a session **location** (`<li id="...">` — Outpatient Clinic, Inpatient Ward, Pharmacy, Laboratory, Registration Desk, Isolation Ward) then `#username` / `#password` / `#loginButton` (demo `admin` / `Admin123`). REST/FHIR require auth (Basic/OAuth); unauthorized → **401**. RBAC roles (System Administrator, Doctor/Clinician, Nurse, Registration Clerk, Pharmacist, Lab Tech) gate apps and actions via privileges.

```mermaid
flowchart TD
    Start([User opens /openmrs]) --> SessChk{Active session?}
    SessChk -- Yes --> Home
    SessChk -- No --> LoginPg([Login page])

    LoginPg --> LocSel{Location selected?}
    LocSel -- No --> LocErr[/Show 'select a location' hint/]
    LocErr --> LoginPg
    LocSel -- Yes --> Creds[Enter #username + #password]
    Creds --> Submit[Click #loginButton]

    subgraph RAL [Resource Adapter Layer]
      AuthCall[POST /ws/rest/v1/session<br/>Basic/OAuth] 
    end
    Submit --> AuthCall
    AuthCall --> AuthRes{Credentials valid?}
    AuthRes -- 401 Unauthorized --> Lockout{Failed attempts >= N?}
    Lockout -- Yes --> Locked[/Account lockout - REQ-SEC-014 Assumption/]
    Lockout -- No --> InvMsg[/Invalid credentials message/]
    InvMsg --> LoginPg

    AuthRes -- 200 + authenticated=true --> SetCtx[Bind sessionLocation + user privileges]
    SetCtx --> Audit{{Audit: LOGIN_SUCCESS - REQ-SEC-041}}
    Audit --> Home([Home dashboard])

    Home --> Tiles[Render app tiles by privilege]
    Tiles --> Gate{Has privilege for app X?}
    Gate -- Yes --> ShowTile[Show tile / allow action]
    Gate -- No --> HideTile[Hide tile / 403 on direct nav - REQ-RBAC-012]

    Home --> LogoutAct([Logout in collapsible navbar])
    LogoutAct --> EndSess[DELETE /ws/rest/v1/session]
    EndSess --> AuditOut{{Audit: LOGOUT - REQ-SEC-042}}
    AuditOut --> LoginPg
```

**RBAC privilege → app gate matrix (verified roles, representative privileges):**

| Role | Register a patient | Capture Vitals | Order Labs | Dispense Meds | Manage Roles | Requirement |
|---|---|---|---|---|---|---|
| System Administrator | ✅ | ✅ | ✅ | ✅ | ✅ | REQ-RBAC-001 |
| Doctor / Clinician | ✅ | ✅ | ✅ | ⚠️ order only | ❌ | REQ-RBAC-005 |
| Nurse | ⚠️ (Assumption) | ✅ | ❌ | ❌ | ❌ | REQ-RBAC-008 |
| Registration Clerk | ✅ | ❌ | ❌ | ❌ | ❌ | REQ-RBAC-010 |
| Pharmacist | ❌ | ❌ | ❌ | ✅ | ❌ | REQ-RBAC-014 |
| Lab Tech | ❌ | ❌ | ⚠️ result entry | ❌ | ❌ | REQ-RBAC-017 |

> Account lockout threshold and password policy specifics are **(Assumption)** — OpenMRS supports configurable lockout via `security.*` global properties but the demo value is not part of the verified fact set.

---

## 3. Workflow 2 — Patient Registration

**Verified:** `registrationapp` wizard — **Demographics** (given / middle / family Name, Gender, Birthdate exact or estimated), **Contact Info** (Address requires ≥1 field, Phone Number), **Relationships**, **Confirm** (`#submit`). On save: unique **Patient ID** generated, redirect to patient dashboard, **"Created Patient Record"** toast.

```mermaid
flowchart TD
    Entry([Home: Register a patient tile]) --> Priv{Has 'Add Patients' privilege?}
    Priv -- No --> Deny[/403 - REQ-RBAC-010/]
    Priv -- Yes --> Demo([Step 1: Demographics])

    Demo --> DemoVal{Required name + gender present?}
    DemoVal -- No --> DemoErr[/Inline field errors - REQ-REG-009/]
    DemoErr --> Demo
    DemoVal -- Yes --> BD{Birthdate mode}
    BD -- Exact --> BDexact[Capture exact DOB]
    BD -- Estimated --> BDest[Capture age -> derive DOB - REQ-REG-014]
    BDexact --> Contact([Step 2: Contact Info])
    BDest --> Contact

    Contact --> AddrVal{Address has >= 1 field?}
    AddrVal -- No --> AddrErr[/Address required - REQ-REG-021/]
    AddrErr --> Contact
    AddrVal -- Yes --> Phone[Phone Number optional/validated]
    Phone --> Rel([Step 3: Relationships])

    Rel --> RelAdd{Add relationship?}
    RelAdd -- Yes --> RelPick[Select relType + related person]
    RelPick --> Rel
    RelAdd -- No --> Confirm([Step 4: Confirm - review summary])

    Confirm --> Dup{Possible duplicate match?}
    Dup -- Yes --> DupWarn[/Show potential duplicates - REQ-REG-040 Assumption/]
    DupWarn --> ConfirmDec{Proceed anyway?}
    ConfirmDec -- No --> Demo
    ConfirmDec -- Yes --> SaveAct
    Dup -- No --> SaveAct[Click #submit]

    subgraph RAL [Resource Adapter Layer]
      CreatePt[POST /ws/rest/v1/patient<br/>person + identifiers + addresses]
    end
    SaveAct --> CreatePt
    CreatePt --> SaveRes{2xx created?}
    SaveRes -- 4xx/5xx --> SaveErr[/Surface error, retain form - REQ-REG-052/]
    SaveErr --> Confirm
    SaveRes -- Yes --> GenID[Generate unique Patient ID]
    GenID --> AuditR{{Audit: PATIENT_CREATED - REQ-SEC-044}}
    AuditR --> Toast[/'Created Patient Record' toast/]
    Toast --> Dash([Redirect: Patient dashboard])
```

**Field validation matrix:**

| Step | Field | Rule | Requirement |
|---|---|---|---|
| Demographics | Given / Family Name | Non-empty | REQ-REG-006 |
| Demographics | Gender | Required, coded M/F/O | REQ-REG-008 |
| Demographics | Birthdate | Exact date **or** estimated age (mutually exclusive) | REQ-REG-014 |
| Contact Info | Address | At least one address field populated | REQ-REG-021 |
| Contact Info | Phone | Format-validated (Assumption) | REQ-REG-024 |
| Confirm | Patient ID | System-generated, unique, immutable | REQ-REG-035 |

---

## 4. Workflow 3 — Find & Open Patient

**Verified:** Home tile **Find Patient Record** (`coreapps` findPatient). Patient dashboard shows name / gender / age / DOB / Patient ID and widgets (Diagnoses, Latest Observations, Vitals, Recent Visits, Family, Conditions, Allergies, Attachments, Weight graph, Appointments) plus **General Actions**.

```mermaid
flowchart TD
    A([Home: Find Patient Record tile]) --> Q[Enter query: name or ID]
    Q --> Search

    subgraph RAL [Resource Adapter Layer]
      Search[GET /ws/rest/v1/patient?q=&v=full]
    end
    Search --> Res{Result count}
    Res -- 0 --> NoRes[/No matches - offer Register - REQ-SRCH-018/]
    NoRes --> Q
    Res -- 1 --> Open
    Res -- Many --> List([Results table: name/ID/age/gender])
    List --> Pick[User selects a row]
    Pick --> Open[GET /ws/rest/v1/patient/uuid?v=full]

    Open --> AccChk{Has 'View Patients' privilege?}
    AccChk -- No --> Deny[/403 - REQ-RBAC-020/]
    AccChk -- Yes --> AuditV{{Audit: PATIENT_VIEWED - REQ-SEC-045}}
    AuditV --> Dash([Patient dashboard])

    Dash --> W1[Header: name/gender/age/DOB/Patient ID]
    Dash --> W2[Widgets: Vitals, Diagnoses, Allergies, Visits...]
    Dash --> Act{General Action chosen?}
    Act -- Start Visit --> WF4>Go to Workflow 4]
    Act -- Schedule Appointment --> WF6>Go to Workflow 6]
    Act -- Edit Registration --> EditReg[Open registration in edit mode]
    Act -- Mark Deceased --> Deceased[Set deceased + cause - REQ-PDASH-048]
    Act -- Delete Patient --> DelGate{Has 'Delete Patients'?}
    DelGate -- No --> Deny
    DelGate -- Yes --> SoftDel[Void patient - REQ-PDASH-052]
```

> **(Assumption)** Search ranking (exact-ID-first, then name fuzzy match) and minimum-character threshold are inferred; OpenMRS exposes configurable name search but the demo behaviour is not in the verified set.

---

## 5. Workflow 4 — Start Visit & Capture Vitals

**Verified:** General Actions include **Start Visit / Add Past Visit / Merge Visits**; Home tile **Capture Vitals**. Visits, encounters, and observations persist via REST (`/visit`, `/encounter`, `/obs`). Vitals appear in the dashboard Vitals widget and Weight graph.

```mermaid
flowchart TD
    Start([Patient dashboard: Start Visit]) --> VPriv{Has 'Add Visits' privilege?}
    VPriv -- No --> Deny[/403 - REQ-RBAC-022/]
    VPriv -- Yes --> VType[Select visit type + location = session location]

    subgraph RAL1 [Resource Adapter Layer]
      MkVisit[POST /ws/rest/v1/visit]
    end
    VType --> MkVisit
    MkVisit --> ActiveV{{Visit active - shows in Active Visits - REQ-VISIT-012}}
    ActiveV --> Vit([Capture Vitals form])

    Vit --> Enter[Enter Height, Weight, Temp, Pulse, RR, BP, SpO2]
    Enter --> RangeChk{Values within plausible range?}
    RangeChk -- No --> RangeWarn[/Out-of-range warning - REQ-VITAL-018/]
    RangeWarn --> RangeDec{Override?}
    RangeDec -- No --> Enter
    RangeDec -- Yes --> SaveV
    RangeChk -- Yes --> SaveV[Save vitals]

    subgraph RAL2 [Resource Adapter Layer]
      MkEnc[POST /ws/rest/v1/encounter<br/>type=Vitals]
      MkObs[POST /ws/rest/v1/obs<br/>LOINC-coded - REQ-VITAL-030]
    end
    SaveV --> MkEnc
    MkEnc --> MkObs
    MkObs --> AuditVit{{Audit: VITALS_RECORDED - REQ-SEC-046}}
    AuditVit --> Refresh[Refresh Vitals widget + Weight graph]
    Refresh --> Done([Back to dashboard])
```

**Vitals concept coding (verified standards footprint):**

| Vital | LOINC (representative) | Unit | Requirement |
|---|---|---|---|
| Body Temperature | 8310-5 | °C | REQ-VITAL-031 |
| Pulse | 8867-4 | /min | REQ-VITAL-032 |
| Respiratory Rate | 9279-1 | /min | REQ-VITAL-033 |
| Systolic BP | 8480-6 | mmHg | REQ-VITAL-034 |
| Diastolic BP | 8462-4 | mmHg | REQ-VITAL-035 |
| SpO2 | 59408-5 | % | REQ-VITAL-036 |
| Weight | 29463-7 | kg | REQ-VITAL-037 |

---

## 6. Workflow 5 — Place Lab Order & Result

**Verified:** Order entry / lab results module (ORDLAB) with REST `/order`; HL7 v2 **ORM** (order) and **ORU** (result) interfaces; LOINC coding. As a sequence diagram to show the order→fulfilment→result loop and HL7 integration boundary.

```mermaid
sequenceDiagram
    autonumber
    actor Clin as Clinician
    participant UI as OpenMRS UI
    participant RAL as Resource Adapter Layer
    participant API as OpenMRS REST/Order API
    participant LIS as Lab System (HL7)
    participant Tech as Lab Tech

    Clin->>UI: Open active visit -> Order Labs
    UI->>UI: RBAC gate 'Add Orders' (REQ-RBAC-024)
    Clin->>UI: Select test (LOINC) + priority + specimen
    UI->>RAL: createOrder(testConcept, careSetting)
    RAL->>API: POST /ws/rest/v1/order (type=testorder)
    API-->>RAL: 201 order uuid (status=ordered)
    RAL-->>UI: Order accepted (REQ-ORDLAB-014)
    Note over API,LIS: Async integration boundary (dashed)
    API--)LIS: HL7 ORM^O01 (new order) (REQ-HL7-012)
    Tech->>LIS: Perform test, enter results
    LIS--)API: HL7 ORU^R01 (results, LOINC + value + units) (REQ-HL7-022)
    API->>API: Match to order, create result Obs (REQ-ORDLAB-038)
    API->>API: Flag abnormal vs reference range (REQ-ORDLAB-042)
    API-->>UI: Result available in Latest Observations widget
    UI-->>Clin: Notify result ready (REQ-NOTIF-016 Assumption)
```

**Order lifecycle states:**

```mermaid
stateDiagram-v2
    [*] --> Ordered
    Ordered --> InProgress: specimen collected
    Ordered --> Cancelled: clinician voids (REQ-ORDLAB-050)
    InProgress --> Resulted: ORU received
    Resulted --> Reviewed: clinician acknowledges (REQ-ORDLAB-046)
    Reviewed --> [*]
    Cancelled --> [*]
```

---

## 7. Workflow 6 — Appointment Booking

**Verified:** Home tile **Appointment Scheduling**; dashboard General Actions **Schedule Appointment** / **Request Appointment**; Appointments widget. REST under `appointmentscheduling`.

```mermaid
flowchart TD
    A([Schedule Appointment - dashboard or APPT app]) --> Priv{Has 'Schedule Appointments'?}
    Priv -- No --> Deny[/403 - REQ-RBAC-026/]
    Priv -- Yes --> Svc[Select appointment type / service]
    Svc --> Prov[Select provider + location]
    Prov --> Slot

    subgraph RAL [Resource Adapter Layer]
      Slot[GET available slots / blocks]
    end
    Slot --> SlotRes{Slot available?}
    SlotRes -- No --> NoSlot[/No slots - suggest next - REQ-APPT-022/]
    NoSlot --> Slot
    SlotRes -- Yes --> Pick[Select date/time slot]
    Pick --> Conflict{Double-booking / overlap?}
    Conflict -- Yes --> ConfWarn[/Conflict warning - REQ-APPT-028/]
    ConfWarn --> Slot
    Conflict -- No --> Book[POST /ws/rest/v1/appointmentscheduling/appointment]
    Book --> BookRes{2xx?}
    BookRes -- No --> BookErr[/Error, retain selection/]
    BookErr --> Pick
    BookRes -- Yes --> Status[Status = Scheduled - REQ-APPT-030]
    Status --> AuditA{{Audit: APPT_CREATED - REQ-SEC-047}}
    AuditA --> Notify[/Send confirmation - REQ-NOTIF-008 Assumption/]
    Notify --> Widget([Appears in Appointments widget])
```

**Appointment status transitions (Assumption — modelled on OpenMRS appointment module):**

| From | Event | To | Requirement |
|---|---|---|---|
| Scheduled | Patient arrives | CheckedIn | REQ-APPT-034 |
| CheckedIn | Seen by provider | Completed | REQ-APPT-036 |
| Scheduled | No-show | Missed | REQ-APPT-038 |
| Scheduled | Cancelled by patient/staff | Cancelled | REQ-APPT-040 |

---

## 8. Workflow 7 — Prescription (Medication Request)

**Verified:** Pharmacy app (PHARM), `MedicationRequest` resource via FHIR R4 (`/ws/fhir2/R4`). Drug orders flow from clinician prescribing to pharmacist dispensing.

```mermaid
sequenceDiagram
    autonumber
    actor Doc as Clinician
    participant UI as OpenMRS UI
    participant RAL as Resource Adapter Layer
    participant FHIR as FHIR R4 API (/ws/fhir2/R4)
    actor Pharm as Pharmacist

    Doc->>UI: Active visit -> Prescribe medication
    UI->>UI: RBAC gate 'Add Drug Orders' (REQ-RBAC-028)
    Doc->>UI: Select drug + dose + route + frequency + duration
    UI->>UI: Allergy / interaction check vs Allergies widget (REQ-PHARM-020)
    alt Allergy or interaction found
        UI-->>Doc: Warning - require override reason (REQ-PHARM-022)
    end
    UI->>RAL: createMedicationRequest(...)
    RAL->>FHIR: POST MedicationRequest (status=active, intent=order) (REQ-FHIR-024)
    FHIR-->>RAL: 201 MedicationRequest id
    RAL-->>UI: Prescription recorded
    UI-->>Doc: Shown in active medications
    Note over FHIR,Pharm: Pharmacy queue (dashed = async)
    FHIR--)Pharm: MedicationRequest appears in dispense queue
    Pharm->>FHIR: GET MedicationRequest?status=active
    Pharm->>FHIR: Dispense -> MedicationDispense (REQ-PHARM-040)
    FHIR->>FHIR: Audit DISPENSED (REQ-SEC-048)
    FHIR-->>UI: Status update -> completed
```

**Prescription decision gate (flowchart view):**

```mermaid
flowchart TD
    P([Prescribe]) --> Allergy{Drug-allergy match?}
    Allergy -- Yes --> Override{Override reason given?}
    Override -- No --> Block[/Block save - REQ-PHARM-022/]
    Block --> P
    Override -- Yes --> Dose
    Allergy -- No --> Dose{Dose within safe range?}
    Dose -- No --> DoseWarn[/Dosing alert - REQ-PHARM-026/]
    DoseWarn --> P
    Dose -- Yes --> Save[POST MedicationRequest - FHIR R4]
    Save --> Active[Status=active -> dispense queue]
```

---

## 9. Cross-Workflow Concerns

### 9.1 Resource Adapter Layer (RAL) binding

Every `RAL` subgraph above is a single seam where the same logical operation maps to a backend-specific call. This keeps all 7 workflows portable across the secondary targets.

| Logical Operation | OpenMRS (primary) | OpenEMR | HAPI FHIR | SMART Health IT | omiiCARE (Assumption) |
|---|---|---|---|---|---|
| Authenticate | `POST /ws/rest/v1/session` | OAuth2 / API token | n/a (server) | SMART OAuth2 launch | JWT login |
| Create patient | `POST /ws/rest/v1/patient` | `POST /api/patient` | `POST /Patient` | `POST /Patient` | adapter `createPatient` |
| Record vitals | `/obs` (LOINC) | form save | `POST /Observation` | `POST /Observation` | `recordObservation` |
| Place order | `/order` + HL7 ORM | order module | `POST /ServiceRequest` | `POST /ServiceRequest` | `createOrder` |
| Prescribe | FHIR `MedicationRequest` | prescription | `MedicationRequest` | `MedicationRequest` | `createRx` |

### 9.2 Cross-cutting checks present in every workflow

```mermaid
flowchart LR
    Req[Any user action] --> Auth{Authenticated?}
    Auth -- No --> R401[/401 -> login/]
    Auth -- Yes --> RBAC{Privileged?}
    RBAC -- No --> R403[/403/]
    RBAC -- Yes --> Valid{Input valid?}
    Valid -- No --> R400[/Validation error/]
    Valid -- Yes --> Exec[Execute via RAL]
    Exec --> Aud{{Audit log - REQ-SEC-040..048}}
    Aud --> Resp([Response + UI update])
```

| Concern | Applies To | Requirement |
|---|---|---|
| AuthN (401 on missing/invalid creds) | All API workflows | REQ-AUTH-030, REQ-SEC-001 |
| AuthZ / privilege gate (403) | All app entry points | REQ-RBAC-001..030 |
| Input validation | REG, VITAL, ORDLAB, PHARM, APPT | REQ-REG, REQ-VITAL, REQ-A11Y-010 |
| Audit logging | All write operations | REQ-SEC-040..048 |
| Accessibility (keyboard, labels, ARIA) | All UI screens | REQ-A11Y-001..030 |
| Performance budget (Assumption) | Search, dashboard load | REQ-PERF-001..020 |

---

## 10. Traceability Note

Each diagram node carrying a `REQ-<PREFIX>-NNN` tag is a direct hook into the requirement catalog (472 requirements) and, via the RTM, to the 1,349 manual test cases. QA engineers should treat each **decision diamond** and each `{{audit}}` node as a distinct test condition (positive + negative path), and each `RAL` subgraph as the boundary where backend-specific contract tests (OpenMRS / OpenEMR / HAPI FHIR / SMART / omiiCARE) are parameterised.

> **End of Workflow Diagrams.** Assumptions are explicitly tagged; all untagged statements derive from the verified OpenMRS Reference Application fact set.
