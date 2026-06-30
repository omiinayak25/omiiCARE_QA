# ZAP Authenticated Scanning — omiiCARE_QA JWT Login Flow

> EDUCATIONAL / local-infra-only. Authenticated scans send active payloads while
> logged in. Run **only** against a locally-owned omiiCARE_QA instance with
> disposable test data. Use a dedicated test tenant/user — never real PHI.

## 1. Why authenticated scanning matters here

omiiCARE_QA is almost entirely behind authentication: every endpoint except the
auth/health/docs allowlist requires a valid JWT, and authorization is enforced
per-method via `@PreAuthorize`. A passive baseline scan only sees public
surface. To exercise IDOR, cross-tenant, privilege-escalation, injection, and
PHI-exposure paths, ZAP must hold a valid **access token** and replay it on
every request.

## 2. Auth model recap

- **Login:** `POST /api/v1/auth/login` → returns `accessToken` + `refreshToken`.
- **Refresh:** `POST /api/v1/auth/refresh` → rotates the refresh token, returns a
  new access token.
- **Usage:** send `Authorization: Bearer <accessToken>` on every protected call.
- **Stateless:** no cookie/session; the bearer header is the only credential.
- Access tokens are short-lived, so the scan must re-authenticate / refresh.

## 3. Recommended approach — JSON authentication + header injection

Because auth is header-based (not form/cookie), the cleanest setup is a
**script-based / replacer** approach rather than ZAP's form-based login.

### 3.1 Obtain a token (script or manual)

```bash
ACCESS_TOKEN="$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"zap.tester@example.test","password":"ZapTest!234"}' \
  | python3 -c 'import sys,json;print(json.load(sys.stdin)["accessToken"])')"
```

### 3.2 Inject the token on every request (ZAP Replacer)

In the ZAP desktop UI: **Tools → Options → Replacer → Add**:
- Description: `JWT Auth Header`
- Match Type: `Request Header`
- Match String: `Authorization`
- Replacement String: `Bearer <ACCESS_TOKEN>`
- Enable: checked

Or headless, via the automation framework / `-z` config (see 3.4).

## 4. Context, users, and session management (desktop UI)

1. **Create a Context** `omiiCARE_QA` and set **Include in Context** to
   `http://localhost:8080/api/v1/.*`. Exclude `/api/v1/auth/logout.*` and
   `/h2-console.*` so the scan does not log itself out or hit the dev console.
2. **Session Management:** `HTTP Authentication header based session management`
   (the token rides in the `Authorization` header).
3. **Authentication:** `JSON-based Authentication`:
   - Login URL: `http://localhost:8080/api/v1/auth/login`
   - POST body: `{"username":"{%username%}","password":"{%password%}"}`
   - Logged-in indicator (regex): `"accessToken"`
   - Logged-out indicator (regex): `OMII-401`
4. **Users:** add a test user per role to exercise authorization boundaries:
   - `patient-reader` (`patient:read`)
   - `clinician` (`patient:read`,`patient:write`,`appointment:read/write`)
   - `auditor` (`audit:read`)
   - `tenant-b-user` (different `tenant_id`, for cross-tenant IDOR tests)
5. **Token extraction:** add a *Script* (or HTTP Sender script) that reads
   `accessToken` from the login response JSON and stores it for header
   injection, refreshing via `/api/v1/auth/refresh` on `401`.

## 5. Automation Framework (headless / CI)

For repeatable runs, drive ZAP with an Automation Framework YAML that defines the
context, a `script` job to fetch the token, an `activeScan` job, and a `report`
job. Pseudocode of the key jobs:

```yaml
jobs:
  - type: script          # authenticate, capture accessToken
    parameters: { action: run, type: standalone, name: jwt-login }
  - type: spider
    parameters: { context: omiiCARE_QA, user: clinician }
  - type: activeScan
    parameters: { context: omiiCARE_QA, user: clinician, policy: omii-policy }
  - type: report
    parameters: { template: traditional-html, reportFile: zap-auth-report }
```

## 6. Test matrix to drive while authenticated

| Goal | How |
|------|-----|
| IDOR / cross-tenant | Scan as `tenant-b-user`; request `tenant-a` patient IDs; expect `404` `OMII-404`, never `200` |
| Privilege escalation | Scan as `patient-reader`; attempt `patient:write` / `audit:read` / `admin:manage` endpoints; expect `403` `OMII-403` |
| JWT tampering | Replacer with a payload-mutated token; expect `401` |
| Token expiry / refresh | Let access token expire; confirm refresh rotation works and old refresh token is rejected |
| SQLi on search | Active scan the `?q=` patient search parameter |

## 7. Safety checklist

- [ ] Target is `localhost` / owned host only.
- [ ] Dedicated test tenant + synthetic data (no real PHI).
- [ ] `/api/v1/auth/logout` and destructive endpoints excluded or scoped.
- [ ] Reports stored under `quality/security/zap/reports/` (gitignore the HTML).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Security Engineer | Initial (Milestone 7) |
