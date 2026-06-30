# Boundary & Edge Data Sets

> **Purpose.** Explicit boundary and edge-case datasets for omiiCARE_QA — date-of-birth boundaries, name length limits, MRN format, appointment duration/overlap edges, and pagination sizes — as ready-to-use tables. All values are synthetic and PHI-safe.

## Purpose

Boundary testing finds defects at the edges of valid ranges. This document gives concrete, copy-ready values so testers and automation use the same edges, tied to the actual data model (VARCHAR(100) names, `MRN-####`, DATE DOB, TIMESTAMP appointments).

## Scope

- **In scope:** Boundary/edge values for patient and appointment data and list pagination.
- **Out of scope:** Functional happy-path data (see [Test Data Catalog](TEST_DATA_CATALOG.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| QA Engineer | Uses these sets when executing boundary cases |
| Automation Engineer | Encodes them as parameterized cases |

---

## 1. Date-of-Birth Boundaries (`date_of_birth` DATE, ISO 8601)

| ID | Value | Class | Expected |
|----|-------|-------|----------|
| DOB-01 | `1900-01-01` | Very old (max plausible age) | Accept |
| DOB-02 | `2026-06-30` | Today (newborn) | Accept |
| DOB-03 | `2026-06-29` | Yesterday | Accept |
| DOB-04 | `2027-01-01` | Future date | Reject (validation error) |
| DOB-05 | `2024-02-29` | Valid leap day | Accept |
| DOB-06 | `2025-02-29` | Invalid leap day (non-leap year) | Reject |
| DOB-07 | `1985-13-01` | Invalid month | Reject |
| DOB-08 | `1985-04-31` | Invalid day for month | Reject |
| DOB-09 | `12-04-1985` | Wrong format (non-ISO) | Reject |
| DOB-10 | empty / null | Required field | Reject (NOT NULL) |

## 2. Name Length & Character Limits (`first_name` / `last_name` VARCHAR(100))

| ID | Value | Class | Expected |
|----|-------|-------|----------|
| NM-01 | `A` | 1 char (min) | Accept |
| NM-02 | 100-char string | At max length | Accept |
| NM-03 | 101-char string | Over max | Reject |
| NM-04 | `   ` (spaces only) | Blank | Reject (NOT NULL/blank) |
| NM-05 | `O'Brien` | Apostrophe | Accept |
| NM-06 | `Smith-Jones` | Hyphen | Accept |
| NM-07 | `José` | Accented (i18n) | Accept |
| NM-08 | `李伟` | CJK | Accept |
| NM-09 | `محمد` | RTL script | Accept |
| NM-10 | `Robert😀` | Emoji | Accept or Reject per policy — assert consistently |
| NM-11 | `<script>alert(1)</script>` | XSS payload | Stored/escaped safely, not executed |

## 3. MRN Format (`mrn` VARCHAR(40), UNIQUE per tenant, `MRN-####`)

| ID | Value | Class | Expected |
|----|-------|-------|----------|
| MRN-B1 | `MRN-0001` | Valid existing | Lookup succeeds |
| MRN-B2 | `MRN-9999` | Valid format, new | Create succeeds |
| MRN-B3 | `MRN-0001` (re-create, same tenant) | Duplicate in tenant | Reject (unique violation) |
| MRN-B4 | `MRN-1` | Wrong digit count | Reject if format-enforced |
| MRN-B5 | `mrn-0001` | Lowercase | Reject / normalize per policy |
| MRN-B6 | 40-char MRN | At column max | Accept |
| MRN-B7 | 41-char MRN | Over column max | Reject |
| MRN-B8 | empty | Required | Reject (NOT NULL) |

## 4. Appointment Duration / Overlap (TIMESTAMP start/end)

Anchor: DR-001 has BOOKED `2027-01-15 09:00–09:30`.

| ID | New slot | Class | Expected |
|----|----------|-------|----------|
| AP-01 | 09:30–10:00 | Back-to-back (no overlap) | Accept |
| AP-02 | 09:15–09:45 | Partial overlap | Reject (double-booking) |
| AP-03 | 09:00–09:30 | Exact same slot | Reject |
| AP-04 | 08:45–09:15 | Overlaps start | Reject |
| AP-05 | 09:00–09:00 | Zero duration | Reject |
| AP-06 | 09:30–09:00 | End before start | Reject |
| AP-07 | 09:15–09:45 (different provider DR-002) | Same time, other provider | Accept |
| AP-08 | 2026-01-01 09:00–09:30 | Past date | Reject / flag per policy |

## 5. Pagination Sizes (list/search endpoints)

| ID | `size` | Class | Expected |
|----|--------|-------|----------|
| PG-01 | `0` | Lower edge | Empty page or reject per contract — assert consistently |
| PG-02 | `1` | Minimum non-empty | One record returned |
| PG-03 | `100` | Max allowed | Up to 100 records |
| PG-04 | `101` | Over max | Clamp to 100 or reject per contract |
| PG-05 | `-1` | Negative | Reject (validation error) |
| PG-06 | `abc` | Non-numeric | Reject (400) |
| PG-07 | `page=9999` | Beyond last page | Empty content, correct total |

## 6. Invalid Email (`email` VARCHAR(200))

| ID | Value | Expected |
|----|-------|----------|
| EM-01 | `john.public@demo.example` | Accept |
| EM-02 | `john.publicdemo.example` (no `@`) | Reject |
| EM-03 | `john@@demo.example` (double `@`) | Reject |
| EM-04 | `john@demo.` (trailing dot) | Reject |
| EM-05 | `john@demo .example` (space) | Reject |
| EM-06 | 200-char local part | At/over max length | Reject if over |
| EM-07 | empty | Accept (nullable) unless required by context |

---

## Related Documents

- [Test Data Catalog](TEST_DATA_CATALOG.md)
- [Execution Guide](../execution/EXECUTION_GUIDE.md)
- [docs/BUSINESS_RULES.md](../../docs/BUSINESS_RULES.md) (BR-APPT double-booking rules)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Lead | Initial (Milestone 6) |
