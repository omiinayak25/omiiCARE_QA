# API Blueprint — REST (`/ws/rest/v1`) + FHIR R4 (`/ws/fhir2/R4`)
## OpenMRS Reference Application — Multi-System Healthcare QA Reference

| Field | Value |
|---|---|
| Document Type | API Blueprint (REST + FHIR R4) |
| Primary Reference System | OpenMRS Reference Application (legacy O2 — `https://o2.openmrs.org`; modern demo O3 — `o3.openmrs.org`) |
| Secondary Targets (via Resource Adapter Layer) | OpenEMR, HAPI FHIR, SMART Health IT, in-house omiiCARE |
| Status | Baseline (reverse-engineered) |
| Date | 2026-07-01 |
| Traceability | Cross-referenced to requirement catalog (472 requirements `REQ-<PREFIX>-NNN`; 1,349 manual test cases via RTM) |
| Standards Footprint | FHIR R4 (4.0.1), HL7 v2 (ADT/ORM/ORU), ICD-10, SNOMED CT, LOINC |
| Primary Prefixes Covered | FHIR, HL7, SEC, RBAC, REG, SRCH, VISIT, VITAL, CLIN, APPT, ORDLAB, PHARM, DATA, RPT |

> **Assumption marking:** Statements beyond VERIFIED OpenMRS facts are tagged **(Assumption)**. Verified facts are stated plainly. Request/response bodies are representative; exact field sets vary by OpenMRS version and installed modules.

---

## 1. Purpose & Scope

This blueprint specifies the **two API surfaces** exposed by the OpenMRS Reference Application:

1. **OpenMRS REST Web Services** — `/openmrs/ws/rest/v1/*` (native dialect, deepest coverage of OpenMRS data model).
2. **FHIR R4** — `/openmrs/ws/fhir2/R4/*` (interoperability dialect, standards-conformant).

Every endpoint is expressed so a **Resource Adapter Layer (RAL)** can re-bind the same logical operation onto OpenEMR, HAPI FHIR, SMART Health IT, or the in-house **omiiCARE** backend (Section 9). The QA portfolio treats OpenMRS as the **system under test (SUT)**; the RAL mapping makes the API contract tests portable across backends.

### 1.1 Base URLs & Versioning

| Surface | Base URL (default install) | Versioning | Notes |
|---|---|---|---|
| REST | `{host}/openmrs/ws/rest/v1` | Path-segment `v1` | Resource/sub-resource model; `?v=` representation control |
| FHIR R4 | `{host}/openmrs/ws/fhir2/R4` | FHIR version in path | `CapabilityStatement.fhirVersion = 4.0.1` |
| FHIR R3 (legacy) | `{host}/openmrs/ws/fhir2/R3` | Deprecated | **(Assumption)** present only where `fhir2` module ships R3 shim |

Reference host for QA: `https://o2.openmrs.org`. Demo credentials: `admin / Admin123`.

---

## 2. Authentication & Sessions  *(REQ-AUTH-, REQ-SEC-, REQ-FHIR-)*

Both surfaces require authentication. Unauthenticated calls return **401 Unauthorized**.

### 2.1 Supported Auth Mechanisms

| Mechanism | REST | FHIR R4 | Header / Flow | Requirement |
|---|---|---|---|---|
| HTTP Basic | Yes | Yes | `Authorization: Basic base64(user:pass)` | REQ-SEC-010 |
| Session cookie | Yes | Yes | `JSESSIONID` after `GET /session` with Basic | REQ-AUTH-020 |
| OAuth2 / Bearer | (Assumption, via `oauth2login` module) | (Assumption) | `Authorization: Bearer <token>` | REQ-SEC-012 |
| SMART-on-FHIR | n/a | (Assumption, via SMART module) | OAuth2 + scopes `patient/*.read` | REQ-FHIR-040 |

### 2.2 `GET /ws/rest/v1/session`

Confirms credentials and returns the authenticated user, roles/privileges, and **session location** (selected at login: Outpatient Clinic, Inpatient Ward, Pharmacy, Laboratory, Registration Desk, Isolation Ward).

**Request**
```http
GET /openmrs/ws/rest/v1/session HTTP/1.1
Host: o2.openmrs.org
Authorization: Basic YWRtaW46QWRtaW4xMjM=
Accept: application/json
```

**Response `200 OK`**
```json
{
  "authenticated": true,
  "user": {
    "uuid": "1c3db49d-440a-11e6-a65c-00e04c680037",
    "display": "admin",
    "roles": [ { "display": "System Developer" }, { "display": "Provider" } ],
    "privileges": [ { "display": "Add Patients" }, { "display": "Edit Patients" } ]
  },
  "sessionLocation": { "uuid": "b1a8b05e-...", "display": "Outpatient Clinic" },
  "locale": "en"
}
```

**Set session location**
```http
POST /openmrs/ws/rest/v1/session
Content-Type: application/json

{ "sessionLocation": "b1a8b05e-3252-11e6-bcd3-0800270d80ce" }
```

| Status | Meaning | Cross-ref |
|---|---|---|
| 200 | Authenticated; payload returned | REQ-AUTH-001 |
| 401 | Missing/invalid credentials | REQ-SEC-010 |
| 403 | Authenticated but lacks privilege for sub-action | REQ-RBAC-005 |

---

## 3. Common REST Conventions  *(REQ-DATA-, REQ-PERF-)*

| Concern | Convention |
|---|---|
| Identity | Every resource keyed by a **UUID** (`/patient/{uuid}`) |
| Representations | `?v=default | full | ref | custom:(field1,field2)` controls payload depth (REQ-PERF-030) |
| Search | `?q=`, resource-specific params; results in `{ "results": [...] }` envelope |
| Pagination | `?limit=` & `?startIndex=`; `links[]` with `rel: prev|next` |
| Create | `POST /{resource}` with JSON body → `201 Created` |
| Update (partial) | `POST /{resource}/{uuid}` (OpenMRS uses POST for edits, not PATCH) |
| Delete (void/retire) | `DELETE /{resource}/{uuid}?reason=...` (soft delete = void) |
| Purge (hard) | `DELETE /{resource}/{uuid}?purge=true` (privileged) |
| Errors | `{ "error": { "message": "...", "code": "...", "detail": "..." } }` |

### 3.1 Standard Status Codes (REST)

| Code | When |
|---|---|
| 200 | Read/update OK |
| 201 | Resource created |
| 204 | Void/delete OK, no body |
| 400 | Validation failure (missing required field, bad UUID) |
| 401 | Not authenticated |
| 403 | Authenticated but lacks privilege (RBAC) |
| 404 | Resource/UUID not found |
| 409 | Conflict (e.g., duplicate identifier) |
| 500 | Server / module error |

---

## 4. REST Resource Endpoints

### 4.1 Patient & Registration  *(REQ-REG-, REQ-SRCH-)*

| Method | Path | Purpose | Key Params | Req |
|---|---|---|---|---|
| GET | `/patient?q={name|id}` | Search patients | `q`, `limit`, `startIndex`, `v` | REQ-SRCH-001 |
| GET | `/patient/{uuid}` | Fetch patient | `v` | REQ-PDASH-001 |
| POST | `/patient` | Register patient | body: person + identifiers | REQ-REG-010 |
| POST | `/patient/{uuid}` | Edit registration | partial body | REQ-REG-040 |
| DELETE | `/patient/{uuid}?reason=` | Void patient | `reason`, `purge` | REQ-REG-060 |

**Register a patient — `POST /ws/rest/v1/patient`**
```json
{
  "person": {
    "names": [ { "givenName": "John", "middleName": "Q", "familyName": "Doe" } ],
    "gender": "M",
    "birthdate": "1990-04-15",
    "birthdateEstimated": false,
    "addresses": [ { "address1": "12 Main St", "cityVillage": "Boston", "country": "USA" } ],
    "attributes": [ { "attributeType": "<phone-uuid>", "value": "+1-617-555-0101" } ]
  },
  "identifiers": [
    { "identifier": "100GEJ", "identifierType": "<openmrs-id-uuid>",
      "location": "<reg-desk-uuid>", "preferred": true }
  ]
}
```
**Response `201 Created`** → `Location: /ws/rest/v1/patient/{uuid}`; body includes generated unique Patient ID. UI shows "Created Patient Record" toast and redirects to patient dashboard.

| Failure | Status | Cross-ref |
|---|---|---|
| Address has zero fields | 400 | REQ-REG-022 |
| No name parts | 400 | REQ-REG-011 |
| Duplicate preferred identifier | 409 | REQ-REG-031 |
| Caller lacks `Add Patients` | 403 | REQ-RBAC-010 |

### 4.2 Visits  *(REQ-VISIT-)*

| Method | Path | Purpose | Req |
|---|---|---|---|
| GET | `/visit?patient={uuid}&includeInactive=false` | Active visits | REQ-VISIT-001 |
| POST | `/visit` | Start visit | REQ-VISIT-010 |
| POST | `/visit/{uuid}` | Stop / edit visit (set `stopDatetime`) | REQ-VISIT-020 |

```json
POST /ws/rest/v1/visit
{ "patient": "{uuid}", "visitType": "<outpatient-uuid>",
  "location": "<outpatient-clinic-uuid>", "startDatetime": "2026-07-01T09:00:00.000+0000" }
```

### 4.3 Encounters & Observations  *(REQ-VITAL-, REQ-CLIN-)*

| Method | Path | Purpose | Req |
|---|---|---|---|
| POST | `/encounter` | Create encounter (Vitals/Visit Note) with nested obs | REQ-VITAL-010 |
| GET | `/encounter?patient={uuid}&v=full` | Encounter history | REQ-PDASH-020 |
| POST | `/obs` | Single observation | REQ-CLIN-030 |
| GET | `/obs?patient={uuid}&concept={loinc-uuid}` | Obs by concept | REQ-VITAL-030 |

**Capture Vitals — encounter with grouped obs (LOINC-coded concepts)**
```json
POST /ws/rest/v1/encounter
{
  "patient": "{uuid}", "encounterType": "<vitals-uuid>", "visit": "{visit-uuid}",
  "encounterDatetime": "2026-07-01T09:05:00.000+0000",
  "location": "<outpatient-clinic-uuid>",
  "obs": [
    { "concept": "<systolic-bp-uuid>", "value": 128 },
    { "concept": "<diastolic-bp-uuid>", "value": 82 },
    { "concept": "<pulse-uuid>", "value": 76 },
    { "concept": "<temp-c-uuid>", "value": 36.9 }
  ]
}
```

### 4.4 Conditions, Diagnoses, Allergies  *(REQ-CLIN-)*

| Method | Path | Purpose | Coding | Req |
|---|---|---|---|---|
| GET/POST | `/condition` | Patient conditions | ICD-10 / SNOMED | REQ-CLIN-040 |
| POST | `/encounterdiagnosis` *(via encounter)* | Diagnosis on encounter | ICD-10 | REQ-CLIN-050 |
| GET/POST | `/patient/{uuid}/allergy` | Allergies sub-resource | SNOMED substance | REQ-CLIN-060 |

### 4.5 Appointments  *(REQ-APPT-)*

| Method | Path | Purpose | Req |
|---|---|---|---|
| GET | `/appointmentscheduling/appointment?patient={uuid}` | List appointments | REQ-APPT-001 |
| POST | `/appointmentscheduling/appointment` | Schedule | REQ-APPT-010 |
| POST | `/appointmentscheduling/appointment/{uuid}` | Reschedule / cancel (`status`) | REQ-APPT-020 |

> Endpoint path is module-dependent: legacy `appointmentscheduling` vs newer `appointment` module. **(Assumption)** for newer module shape.

### 4.6 Orders, Lab & Pharmacy  *(REQ-ORDLAB-, REQ-PHARM-)*

| Method | Path | Purpose | Req |
|---|---|---|---|
| POST | `/order` (`type: testorder`) | Lab order entry | REQ-ORDLAB-010 |
| GET | `/order?patient={uuid}&t=testorder` | Order list | REQ-ORDLAB-020 |
| POST | `/order` (`type: drugorder`) | Medication order | REQ-PHARM-010 |
| GET | `/drug?q=` | Drug formulary lookup | REQ-PHARM-005 |

### 4.7 Metadata & Concepts  *(REQ-DATA-)*

| Method | Path | Purpose | Req |
|---|---|---|---|
| GET | `/concept?q=&source=LOINC` | Concept dictionary search | REQ-DATA-010 |
| GET | `/location` | Locations (login locations) | REQ-AUTH-005 |
| GET | `/encountertype`, `/visittype`, `/patientidentifiertype` | Metadata | REQ-DATA-020 |
| GET | `/relationship?person={uuid}` | Family/relationships | REQ-REG-050 |

### 4.8 RBAC Administration  *(REQ-RBAC-)*

| Method | Path | Purpose | Req |
|---|---|---|---|
| GET | `/role` / `/privilege` | List roles & privileges | REQ-RBAC-001 |
| POST | `/role` | Create/edit role (needs `Manage Roles`) | REQ-RBAC-030 |
| POST | `/user/{uuid}` | Assign roles to user | REQ-RBAC-040 |

---

## 5. FHIR R4 Surface  *(REQ-FHIR-)*

### 5.1 Capability & Metadata

```http
GET /openmrs/ws/fhir2/R4/metadata
Accept: application/fhir+json
```
Returns a **`CapabilityStatement`** with `fhirVersion: "4.0.1"`, listing supported resources. Verified supported resources: **Patient, Encounter, Observation, Condition, AllergyIntolerance, MedicationRequest**. Common additional: Practitioner, Location, Organization, ServiceRequest, DiagnosticReport (availability is version/module dependent — **(Assumption)** beyond the six verified).

| Status | Meaning |
|---|---|
| 200 | CapabilityStatement returned |
| 401 | Auth required (FHIR endpoints are protected) |
| 406 | Unsupported `Accept` (use `application/fhir+json`) |

### 5.2 FHIR Resource Endpoints

| Resource | Read | Search | Create | Update | Key search params | Req |
|---|---|---|---|---|---|---|
| Patient | `GET /Patient/{id}` | `GET /Patient?name=&identifier=&birthdate=&gender=` | POST | PUT | `identifier`, `name`, `_id` | REQ-FHIR-010 |
| Encounter | `GET /Encounter/{id}` | `GET /Encounter?patient=&date=` | POST | PUT | `patient`, `date`, `type` | REQ-FHIR-020 |
| Observation | `GET /Observation/{id}` | `GET /Observation?patient=&code=&category=vital-signs` | POST | PUT | `patient`, `code` (LOINC), `category` | REQ-FHIR-030 |
| Condition | `GET /Condition/{id}` | `GET /Condition?patient=&clinical-status=` | POST | PUT | `patient`, `code` (ICD-10/SNOMED) | REQ-FHIR-050 |
| AllergyIntolerance | `GET /AllergyIntolerance/{id}` | `GET /AllergyIntolerance?patient=` | POST | PUT | `patient` | REQ-FHIR-060 |
| MedicationRequest | `GET /MedicationRequest/{id}` | `GET /MedicationRequest?patient=&status=` | POST | PUT | `patient`, `status` | REQ-FHIR-070 |

### 5.3 FHIR Search — Patient (example)

**Request**
```http
GET /openmrs/ws/fhir2/R4/Patient?identifier=100GEJ&_count=10 HTTP/1.1
Authorization: Basic YWRtaW46QWRtaW4xMjM=
Accept: application/fhir+json
```

**Response `200 OK` — `Bundle` (type `searchset`)**
```json
{
  "resourceType": "Bundle", "type": "searchset", "total": 1,
  "link": [ { "relation": "self", "url": ".../Patient?identifier=100GEJ&_count=10" } ],
  "entry": [
    { "fullUrl": ".../Patient/8d70...", "resource": {
        "resourceType": "Patient", "id": "8d70...",
        "identifier": [ { "system": "OpenMRS ID", "value": "100GEJ" } ],
        "name": [ { "family": "Doe", "given": ["John", "Q"] } ],
        "gender": "male", "birthDate": "1990-04-15",
        "address": [ { "line": ["12 Main St"], "city": "Boston", "country": "USA" } ]
    } }
  ]
}
```

### 5.4 FHIR Observation — vital sign create

```json
POST /ws/fhir2/R4/Observation
Content-Type: application/fhir+json
{
  "resourceType": "Observation",
  "status": "final",
  "category": [ { "coding": [ { "system": "http://terminology.hl7.org/CodeSystem/observation-category",
    "code": "vital-signs" } ] } ],
  "code": { "coding": [ { "system": "http://loinc.org", "code": "8867-4",
    "display": "Heart rate" } ] },
  "subject": { "reference": "Patient/8d70..." },
  "encounter": { "reference": "Encounter/abcd..." },
  "effectiveDateTime": "2026-07-01T09:05:00+00:00",
  "valueQuantity": { "value": 76, "unit": "/min",
    "system": "http://unitsofmeasure.org", "code": "/min" }
}
```
**`201 Created`** → `Location: /ws/fhir2/R4/Observation/{id}`.

### 5.5 FHIR Coding Systems (canonical URIs)  *(REQ-FHIR-, REQ-CLIN-)*

| System | Canonical URI | Used by |
|---|---|---|
| LOINC | `http://loinc.org` | Observation.code (vitals, labs) |
| SNOMED CT | `http://snomed.info/sct` | Condition, AllergyIntolerance |
| ICD-10 | `http://hl7.org/fhir/sid/icd-10` | Condition, diagnosis |
| RxNorm | `http://www.nlm.nih.gov/research/umls/rxnorm` | MedicationRequest **(Assumption)** |
| Obs category | `http://terminology.hl7.org/CodeSystem/observation-category` | Observation.category |

### 5.6 FHIR Error Handling — `OperationOutcome`

Errors return an `OperationOutcome` (not the REST `{error}` shape):
```json
{
  "resourceType": "OperationOutcome",
  "issue": [ { "severity": "error", "code": "not-found",
    "diagnostics": "Patient/zzz is not known" } ]
}
```

| Status | FHIR `issue.code` | Trigger |
|---|---|---|
| 400 | `invalid` / `structure` | Malformed resource, missing required element |
| 401 | `login` | No/invalid auth |
| 403 | `forbidden` | RBAC privilege missing |
| 404 | `not-found` | Unknown id |
| 409 / 412 | `conflict` | Version conflict (`If-Match` / `ETag`) |
| 422 | `processing` / `business-rule` | Constraint violation |

---

## 6. Cross-Surface Operation Map (REST ↔ FHIR)

| Logical operation | REST | FHIR R4 | Requirement |
|---|---|---|---|
| Find patient | `GET /patient?q=` | `GET /Patient?name=` | REQ-SRCH-001 |
| Register patient | `POST /patient` | `POST /Patient` | REQ-REG-010 |
| Start visit | `POST /visit` | `POST /Encounter` (class `IMP`/`AMB`) | REQ-VISIT-010 |
| Capture vitals | `POST /encounter` + obs | `POST /Observation` (`vital-signs`) | REQ-VITAL-010 |
| Record condition | `POST /condition` | `POST /Condition` | REQ-CLIN-040 |
| Record allergy | `POST /patient/{uuid}/allergy` | `POST /AllergyIntolerance` | REQ-CLIN-060 |
| Prescribe | `POST /order (drugorder)` | `POST /MedicationRequest` | REQ-PHARM-010 |

> OpenMRS has **no native `Visit` FHIR resource**; a Visit maps to a parent `Encounter` (often class `IMP`/`AMB`) and member encounters. **(Assumption)** on exact grouping per fhir2 module version.

---

## 7. HL7 v2 Interface Boundary  *(REQ-HL7-)*

REST/FHIR are the synchronous APIs; HL7 v2 is the asynchronous integration boundary (channels via OpenMRS HL7 queue / Mirth-style engine — **(Assumption)** on engine).

| Message | Direction | Maps to API write | Req |
|---|---|---|---|
| ADT^A01/A04/A08 | Inbound | Patient register/update (`POST /Patient`) | REQ-HL7-010 |
| ADT^A03 | Inbound | Discharge → stop Encounter/Visit | REQ-HL7-015 |
| ORM^O01 | Inbound | Order → `POST /ServiceRequest` / `/order` | REQ-HL7-020 |
| ORU^R01 | Inbound | Result → `POST /Observation` | REQ-HL7-030 |

---

## 8. Security & Audit Contract  *(REQ-SEC-, REQ-RBAC-, REQ-RPT-)*

| Control | Behavior | Req |
|---|---|---|
| Transport | TLS required for all API traffic (**(Assumption)** on prod enforcement) | REQ-SEC-001 |
| AuthN | 401 on missing/invalid creds, both surfaces | REQ-SEC-010 |
| AuthZ | Privilege-gated; 403 when role lacks privilege (e.g. `Delete Patients`) | REQ-RBAC-005 |
| Audit | Create/update/void write audit/log entries (HIPAA-style) | REQ-RPT-040 |
| PHI in errors | Error bodies must not leak PHI beyond resource ids — **verify in test** | REQ-SEC-030 |
| Rate limiting | **(Assumption)** not native; enforced at gateway/RAL | REQ-SEC-040 |

### 8.1 RBAC → Endpoint Gate (excerpt)

| Role | Can POST `/patient` | Can `DELETE /patient` (purge) | Can POST `/order drugorder` |
|---|---|---|---|
| Registration Clerk | Yes (`Add Patients`) | No | No |
| Doctor / Clinician | Yes | No | Yes |
| Pharmacist | No | No | Dispense only |
| System Administrator | Yes | Yes | No |

---

## 9. Resource Adapter Layer (RAL) — Backend Mapping

The RAL exposes a **canonical contract** (the logical operations in Section 6) and binds each to a backend dialect. OpenMRS is the primary; OpenEMR, HAPI FHIR, SMART Health IT, and omiiCARE are alternates.

```mermaid
flowchart LR
  QA[QA Contract Tests / Clients] --> RAL[Resource Adapter Layer\ncanonical operations]
  RAL -->|native REST + fhir2| OMRS[(OpenMRS RefApp\nPRIMARY)]
  RAL -->|REST + FHIR module| OEMR[(OpenEMR)]
  RAL -->|pure FHIR R4| HAPI[(HAPI FHIR)]
  RAL -->|SMART OAuth2 + FHIR| SMART[(SMART Health IT)]
  RAL -->|in-house API| OMII[(omiiCARE)]
```

### 9.1 Endpoint Adapter Matrix

| Canonical op | OpenMRS (primary) | OpenEMR | HAPI FHIR | SMART Health IT | omiiCARE |
|---|---|---|---|---|---|
| Find patient | `GET /ws/rest/v1/patient?q=` or `/fhir2/R4/Patient?name=` | `/apis/.../fhir/Patient` | `GET /fhir/Patient?name=` | `GET /v/r4/fhir/Patient?name=` | `GET /api/v1/patients?search=` (Assumption) |
| Register patient | `POST /ws/rest/v1/patient` | `POST /apis/.../api/patient` | `POST /fhir/Patient` | n/a (read-mostly sandbox) | `POST /api/v1/patients` (Assumption) |
| Capture vitals | `POST /ws/rest/v1/encounter` | `POST .../fhir/Observation` | `POST /fhir/Observation` | sandbox read | `POST /api/v1/observations` (Assumption) |
| Prescribe | `POST /ws/rest/v1/order` | OpenEMR Rx API | `POST /fhir/MedicationRequest` | read | `POST /api/v1/medication-requests` (Assumption) |
| Auth | Basic / session / OAuth2 | API key + OAuth2 | none/Basic/OAuth2 | SMART OAuth2 scopes | API key / JWT (Assumption) |

### 9.2 Adapter Design Notes

- **Identity normalization:** all backends keyed differently (OpenMRS UUID, OpenEMR `pid`, FHIR `id`). RAL maintains a cross-walk so canonical ids are stable across backends. (REQ-DATA-030)
- **Error normalization:** RAL maps OpenMRS `{error}`, FHIR `OperationOutcome`, and OpenEMR error shapes to one canonical error envelope so contract tests assert once. (REQ-SEC-031)
- **Capability gating:** RAL queries each backend's capability (FHIR `metadata`, OpenMRS `/session` privileges) and skips/marks-not-supported unsupported ops (e.g. write ops on SMART sandbox). (REQ-FHIR-041)
- **Coding alignment:** RAL guarantees LOINC/SNOMED/ICD-10 URIs are emitted consistently regardless of backend native coding. (REQ-CLIN-070)

---

## 10. Contract Test Hooks (for RTM traceability)

| Test family | Target | Asserts | Sample Req IDs |
|---|---|---|---|
| AuthN negative | both | 401 without creds | REQ-SEC-010 |
| AuthZ negative | both | 403 for under-privileged role | REQ-RBAC-005 |
| Schema conformance | FHIR | resources validate against R4 4.0.1 profiles | REQ-FHIR-010..070 |
| Required-field validation | REST | 400 on missing name/address/identifier | REQ-REG-011, REQ-REG-022 |
| Idempotency / conflict | both | 409 on duplicate identifier | REQ-REG-031 |
| Coding correctness | FHIR | LOINC/SNOMED/ICD-10 URIs exact | REQ-CLIN-070 |
| Cross-backend parity | RAL | same canonical op behaves equivalently | REQ-DATA-030 |

---

## 11. Open Questions / Assumptions Register

| # | Item | Status |
|---|---|---|
| 1 | OAuth2/SMART availability depends on installed modules | (Assumption) |
| 2 | Newer `appointment` module path vs legacy `appointmentscheduling` | (Assumption) |
| 3 | FHIR resources beyond the six verified (DiagnosticReport, ServiceRequest, Practitioner) | (Assumption) |
| 4 | HL7 v2 engine (internal queue vs Mirth) | (Assumption) |
| 5 | omiiCARE endpoint shapes (in-house, not public) | (Assumption) |
| 6 | Rate limiting / TLS enforcement at production gateway | (Assumption) |

---

*End of API Blueprint. Cross-referenced to requirement catalog (REQ-<PREFIX>-NNN) and RTM. OpenMRS RefApp is the primary SUT; alternate backends are reachable via the Resource Adapter Layer.*
