# Authentication & Authorization — Manual Test Cases

**Module:** Authentication / RBAC
**Project:** omiiCARE_QA — Milestone 6
**Endpoints under test:** `POST /api/v1/auth/login`, `POST /api/v1/auth/refresh`, `GET /api/v1/auth/me`
**Frontend under test:** Login page, Dashboard

---

## TC-AUTH-001 — Valid login with seeded admin credentials

| Field | Value |
|---|---|
| **ID** | TC-AUTH-001 |
| **Title** | Valid login returns access + refresh JWT and lands on Dashboard |
| **Module** | Authentication |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | Backend and frontend running; seed user `demo.admin` provisioned |
| **Test Data** | username=`demo.admin`, password=`Admin@12345` |
| **Steps** | 1. Open the Login page. 2. Enter username into `login-username`. 3. Enter password into `login-password`. 4. Click `login-submit`. |
| **Expected Result** | `POST /api/v1/auth/login` returns `200` with `accessToken` and `refreshToken` (both JWT). UI redirects to the page with `data-testid=dashboard`. No `login-error` shown. |
| **Postconditions** | Active authenticated session; tokens stored client-side |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUTH-001 |
| **Risk Level** | High |

---

## TC-AUTH-002 — Login with wrong password is rejected

| Field | Value |
|---|---|
| **ID** | TC-AUTH-002 |
| **Title** | Wrong password returns 401 OMII-401-1 and login-error |
| **Module** | Authentication |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | Login page reachable; `demo.admin` exists |
| **Test Data** | username=`demo.admin`, password=`WrongPass1!` |
| **Steps** | 1. Open Login page. 2. Enter valid username, wrong password. 3. Click `login-submit`. |
| **Expected Result** | `POST /auth/login` returns `401` with `errorCode = OMII-401-1` (Invalid credentials). UI shows `login-error`. No redirect to Dashboard. No tokens issued. |
| **Postconditions** | No session created |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUTH-002 |
| **Risk Level** | High |

---

## TC-AUTH-003 — Login with unknown username is rejected

| Field | Value |
|---|---|
| **ID** | TC-AUTH-003 |
| **Title** | Unknown username returns 401 OMII-401-1 (no user enumeration) |
| **Module** | Authentication |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Login page reachable |
| **Test Data** | username=`no.such.user`, password=`Admin@12345` |
| **Steps** | 1. Open Login page. 2. Enter a non-existent username and any password. 3. Submit. |
| **Expected Result** | `401` with `errorCode = OMII-401-1`. Error message is generic (does not reveal whether the username exists). `login-error` shown. |
| **Postconditions** | No session |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUTH-002 |
| **Risk Level** | Medium |

---

## TC-AUTH-004 — Empty credential fields blocked

| Field | Value |
|---|---|
| **ID** | TC-AUTH-004 |
| **Title** | Submitting empty username/password is rejected |
| **Module** | Authentication |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Login page reachable |
| **Test Data** | username=``, password=`` |
| **Steps** | 1. Open Login page. 2. Leave `login-username` and `login-password` empty. 3. Click `login-submit`. |
| **Expected Result** | Submission is blocked client-side, or backend returns `400 OMII-400`. No `200`/token issued. |
| **Postconditions** | No session |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUTH-002 |
| **Risk Level** | Low |

---

## TC-AUTH-005 — Refresh token rotation issues a new pair

| Field | Value |
|---|---|
| **ID** | TC-AUTH-005 |
| **Title** | POST /auth/refresh rotates tokens and returns a new access + refresh pair |
| **Module** | Authentication |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | Valid session from TC-AUTH-001 with a fresh `refreshToken` |
| **Test Data** | body=`{ "refreshToken": "<valid refresh JWT>" }` |
| **Steps** | 1. Authenticate (TC-AUTH-001). 2. Call `POST /api/v1/auth/refresh` with the current refresh token. |
| **Expected Result** | `200` with a NEW `accessToken` and a NEW `refreshToken` (rotation). New access token differs from the prior one. |
| **Postconditions** | New token pair active |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUTH-003 |
| **Risk Level** | High |

---

## TC-AUTH-006 — Reusing a rotated (old) refresh token is rejected

| Field | Value |
|---|---|
| **ID** | TC-AUTH-006 |
| **Title** | Old refresh token cannot be reused after rotation |
| **Module** | Authentication |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | TC-AUTH-005 completed (token rotated once) |
| **Test Data** | body=`{ "refreshToken": "<previous/old refresh JWT>" }` |
| **Steps** | 1. Rotate once (TC-AUTH-005). 2. Call `POST /auth/refresh` again with the OLD refresh token. |
| **Expected Result** | `401` (token invalid/revoked). No new tokens issued. |
| **Postconditions** | Old token remains invalid |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUTH-003 |
| **Risk Level** | High |

---

## TC-AUTH-007 — GET /auth/me with valid token returns identity

| Field | Value |
|---|---|
| **ID** | TC-AUTH-007 |
| **Title** | /auth/me returns the authenticated principal and its permissions |
| **Module** | Authentication |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Valid access token (TC-AUTH-001) |
| **Test Data** | header `Authorization: Bearer <accessToken>` |
| **Steps** | 1. Authenticate. 2. Call `GET /api/v1/auth/me` with the bearer token. |
| **Expected Result** | `200` with the current user's identity (username `demo.admin`) and role/permission claims (e.g. SUPER_ADMIN). |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUTH-004 |
| **Risk Level** | Medium |

---

## TC-AUTH-008 — GET /auth/me without token is unauthenticated

| Field | Value |
|---|---|
| **ID** | TC-AUTH-008 |
| **Title** | Protected endpoint without Authorization header returns 401 |
| **Module** | Authentication |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | Backend running |
| **Test Data** | No `Authorization` header |
| **Steps** | 1. Call `GET /api/v1/auth/me` with no bearer token. |
| **Expected Result** | `401` with `errorCode = OMII-401` (Authentication required). No identity returned. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUTH-004 |
| **Risk Level** | High |

---

## TC-AUTH-009 — Invalid/malformed bearer token rejected

| Field | Value |
|---|---|
| **ID** | TC-AUTH-009 |
| **Title** | Tampered or malformed JWT is rejected on a protected endpoint |
| **Module** | Authentication |
| **Priority** | P1 |
| **Severity** | High |
| **Preconditions** | Backend running |
| **Test Data** | `Authorization: Bearer abc.def.ghi` (invalid signature/structure) |
| **Steps** | 1. Call `GET /api/v1/patients` with a garbage bearer token. |
| **Expected Result** | `401` (UNAUTHENTICATED). Request not processed. No data returned. |
| **Postconditions** | None |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-AUTH-004 |
| **Risk Level** | High |

---

## TC-AUTH-010 — Expired access token rejected; refresh recovers session

| Field | Value |
|---|---|
| **ID** | TC-AUTH-010 |
| **Title** | Expired access token returns 401 and can be recovered via refresh |
| **Module** | Authentication |
| **Priority** | P2 |
| **Severity** | High |
| **Preconditions** | An access token whose TTL has elapsed (wait out TTL or use short-TTL config) |
| **Test Data** | Expired `accessToken`; valid `refreshToken` |
| **Steps** | 1. Wait until access token expires. 2. Call a protected endpoint with the expired token → observe rejection. 3. Call `POST /auth/refresh` with the valid refresh token. 4. Retry the protected endpoint with the new access token. |
| **Expected Result** | Step 2: `401`. Step 3: `200` new pair. Step 4: `200` success. |
| **Postconditions** | Active session restored |
| **Automation Status** | Manual |
| **Requirement Ref** | FR-AUTH-003 |
| **Risk Level** | Medium |

---

## TC-AUTH-011 — Logout / session termination behavior

| Field | Value |
|---|---|
| **ID** | TC-AUTH-011 |
| **Title** | After logout, client tokens are cleared and protected UI is inaccessible |
| **Module** | Authentication |
| **Priority** | P2 |
| **Severity** | Medium |
| **Preconditions** | Logged-in session on Dashboard |
| **Test Data** | None |
| **Steps** | 1. Log in. 2. Trigger logout from the app. 3. Attempt to navigate back to the `dashboard` view. |
| **Expected Result** | Client-side tokens cleared; user is redirected to Login. Protected views are not rendered without re-authentication. |
| **Postconditions** | No active session client-side |
| **Automation Status** | Manual |
| **Requirement Ref** | FR-AUTH-005 |
| **Risk Level** | Medium |

---

## TC-AUTH-012 — Authenticated-but-unauthorized returns 403 (RBAC)

| Field | Value |
|---|---|
| **ID** | TC-AUTH-012 |
| **Title** | Valid token lacking required permission returns 403 OMII-403 |
| **Module** | Authentication / RBAC |
| **Priority** | P1 |
| **Severity** | Critical |
| **Preconditions** | A user/token WITHOUT `patient:write` permission |
| **Test Data** | Valid bearer token for a read-only role; `POST /api/v1/patients` body |
| **Steps** | 1. Authenticate as a role lacking `patient:write`. 2. Call `POST /api/v1/patients`. |
| **Expected Result** | `403` with `errorCode = OMII-403` (Access denied). 401 is NOT returned (the user is authenticated). No patient created. |
| **Postconditions** | No data change |
| **Automation Status** | Candidate |
| **Requirement Ref** | FR-RBAC-001 |
| **Risk Level** | High |

---

## Version History

| Version | Date | Author | Notes |
|---|---|---|---|
| 1.0 | 2026-06-30 | Senior QA Engineer | Initial (Milestone 6) |
