# UI Functional Checklist

**Project:** omiiCARE_QA — Milestone 6
**Scope:** Frontend functional verification of Login, Dashboard, Patients, and Appointments pages.
**Usage:** Tick each item per build. Reference the `data-testid` hooks listed for stable element targeting.

---

## Login Page

| # | Check | testid | Pass/Fail |
|---|---|---|---|
| 1 | Username field renders and accepts input | `login-username` | |
| 2 | Password field renders and masks input | `login-password` | |
| 3 | Submit button enabled with valid input | `login-submit` | |
| 4 | Valid login redirects to Dashboard | `dashboard` | |
| 5 | Invalid login shows error message | `login-error` | |
| 6 | Error clears on a subsequent valid attempt | `login-error` | |
| 7 | Empty submit is blocked / shows validation | — | |

## Dashboard

| # | Check | testid | Pass/Fail |
|---|---|---|---|
| 8 | Dashboard renders after authentication | `dashboard` | |
| 9 | Navigation to Patients and Appointments works | — | |
| 10 | Direct nav to Dashboard without session redirects to Login | `dashboard` | |

## Patients Page

| # | Check | testid | Pass/Fail |
|---|---|---|---|
| 11 | Add-patient action opens the form | `add-patient` | |
| 12 | First name / last name fields accept input | `patient-firstName`, `patient-lastName` | |
| 13 | DOB field accepts a valid date | `patient-dob` | |
| 14 | Gender selector works | `patient-gender` | |
| 15 | Save creates the patient and shows it in the table | `patient-save`, `patients-table` | |
| 16 | Validation errors shown for missing required fields | — | |
| 17 | Search filters the table | `patient-search` | |
| 18 | No-match search shows the empty state | `patients-empty` | |
| 19 | Table reflects pagination | `patients-table` | |

## Appointments Page

| # | Check | testid | Pass/Fail |
|---|---|---|---|
| 20 | Book-appointment action opens the form | `book-appointment` | |
| 21 | Start / end datetime fields accept input | `appointment-start`, `appointment-end` | |
| 22 | Save books and shows the appointment in the table | `appointment-save`, `appointments-table` | |
| 23 | Overlapping booking surfaces the conflict indicator | `appointment-conflict` | |
| 24 | End-before-start is prevented / errors clearly | — | |

## Cross-Cutting UI

| # | Check | Pass/Fail |
|---|---|---|
| 25 | API errors surface as user-readable messages (no raw JSON / stack traces) | |
| 26 | Loading states shown during async calls | |
| 27 | Session expiry redirects to Login rather than breaking the page | |
| 28 | No console errors during the happy path | |

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
