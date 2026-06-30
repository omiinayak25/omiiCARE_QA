# Security Testing Guide

> **Purpose.** Define the security-testing approach for omiiCARE_QA: how the
> System Under Test (SUT) is probed for vulnerabilities, which risks are
> validated, which tools execute the work, and how findings are reported. This
> guide is documentation only (Milestone 1); the frameworks it describes are
> implemented in **Milestone 7** and wired into CI in **Milestone 8**. It is the
> companion to the governance posture in [SECURITY.md](../SECURITY.md).

## Purpose

- Establish a repeatable, evidence-driven security-testing discipline mapped to
  the **OWASP Top 10**.
- Make every security finding traceable to a risk, a severity, and a mitigation.
- Constrain all active testing to **owned infrastructure** so the program is safe
  and lawful by construction.

## Scope

- **In scope:** Dynamic Application Security Testing (DAST) of the backend and
  frontend SUT, Software Composition Analysis (SCA) of dependencies, Static
  Application Security Testing (SAST) preparation, security regression, and
  baseline management — all against local / Docker / project-owned environments.
- **Out of scope (v1.0):** penetration testing of third-party systems, real PHI,
  formal certification, production cloud targets, and offensive testing of any
  asset omiiCARE_QA does not own. See
  [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §3.

> **Responsible scope (binding rule).** Active security scans run **only** against
> `dev`, `local`, `docker`, `test`, `qa` (owned), and `perf` environments that
> the project controls. Never scan public websites, third-party SaaS, or any host
> not provisioned by this repository. Stubbed externals (WireMock) are the only
> "external" surfaces, and they are owned.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Security QA Engineer | Author and run DAST/SCA suites; triage findings |
| QA Architect | Own OWASP-to-test mapping; approve baselines |
| Backend Engineer (M3) | Remediate auth/authz/input-validation findings |
| Frontend Engineer (M4) | Remediate XSS/CSRF/header/cookie findings |
| DevOps Engineer (M8) | Gate the pipeline on security thresholds |
| Maintainer | Accept residual risk; approve any exception |

---

## 1. OWASP Top 10 → Test-Case Mapping

| OWASP (2021) | What it means here | Representative test cases | Primary tool |
|--------------|--------------------|---------------------------|--------------|
| A01 Broken Access Control | RBAC bypass, IDOR, privilege escalation | Access another tenant's record; call admin route as `Patient`; mutate `id` in URL | ZAP + manual |
| A02 Cryptographic Failures | Weak/absent transport & at-rest crypto | TLS config, JWT signing alg, password hashing, MinIO encryption | ZAP + review |
| A03 Injection | SQLi, command, header, XSS | Parameterized-query proof, payload fuzzing on every input | ZAP active scan |
| A04 Insecure Design | Missing security controls by design | Rate-limit absence, no lockout, no audit on sensitive ops | Manual + review |
| A05 Security Misconfiguration | Headers, CORS, error verbosity, defaults | Missing security headers, permissive CORS, stack traces leaked | ZAP passive |
| A06 Vulnerable Components | Known-CVE dependencies | Dependency-Check SCA against Maven/npm trees | Dependency-Check |
| A07 Identification & Auth Failures | Weak login, session, MFA gaps | Brute force, weak passwords, session fixation | ZAP + manual |
| A08 Software & Data Integrity | Unsigned artifacts, unsafe deserialization | Build-artifact integrity, deserialization probes | Review + SCA |
| A09 Logging & Monitoring Failures | Missing/insufficient audit trail | Verify HIPAA-like audit on PHI access; alerting present | Review + OTel |
| A10 SSRF | Server fetches attacker-controlled URLs | Probe outbound-fetch endpoints (adapter/integration layer) | ZAP + manual |

## 2. What to Validate (Control Checklist)

| Area | Validation |
|------|------------|
| Authentication | Login throttling, account lockout, password policy, no user enumeration, credential storage (hashing) |
| Authorization | RBAC + permission checks on **every** `/api/v1/` endpoint; deny-by-default; tenant isolation |
| JWT | Signature verification, `alg=none` rejected, expiry enforced, refresh rotation, audience/issuer claims |
| Session | Server-side invalidation on logout, idle/absolute timeouts, no session fixation |
| Cookies | `HttpOnly`, `Secure`, `SameSite`, scoped path/domain, no sensitive data in cookies |
| Security headers | `Content-Security-Policy`, `Strict-Transport-Security`, `X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy` |
| CORS | Explicit allow-list of owned origins; no `*` with credentials |
| Input validation | Server-side validation on all inputs; length/type/format; reject-by-default |
| Rate limiting | Throttle on auth, search, export, and write-heavy endpoints |
| SQL Injection | Parameterized queries / JPA criteria only; no string-concatenated SQL |
| XSS | Output encoding, CSP, React auto-escaping respected (no `dangerouslySetInnerHTML` misuse) |
| CSRF | Anti-CSRF tokens or stateless JWT design; state-changing verbs protected |
| IDOR | Object ownership checks; opaque/authorized identifiers |
| Broken auth | No default credentials, no auth bypass via parameter tampering |
| Sensitive-data exposure | No PHI/secrets in logs, URLs, error responses, or client storage |
| File uploads | Type/size allow-list, AV-scan seam, stored outside web root (MinIO), no executable rendering |

## 3. Tooling

| Tool | Type | Role | Milestone |
|------|------|------|-----------|
| OWASP ZAP | DAST | Passive + active scanning of frontend/backend on owned infra | M7 |
| OWASP Dependency-Check | SCA | CVE detection in Maven (backend) and npm (frontend) trees | M7 / M8 |
| SonarQube (SAST prep) | SAST | Static rules, hotspots; full SAST integration prepared | M2 wired → M8 gate |
| WireMock | Stub | Owned stand-ins for external systems under test | M2 |

- **DAST (ZAP):** spider + AJAX spider the SUT, run passive rules continuously,
  schedule active scans against `qa`/`docker`. Authenticated scans use a seeded
  RBAC test user per role.
- **SCA (Dependency-Check):** runs on every dependency change; suppression file
  documents accepted, justified exceptions with expiry.
- **SAST prep:** SonarQube security hotspots are reviewed in M7; M8 promotes them
  to blocking quality gates.

## 4. Security Regression & Baselines

- A **ZAP baseline file** records the known, accepted alert set per environment;
  scans diff against it so only **new** alerts fail the run.
- Each fixed vulnerability gains a **regression test** so it cannot silently
  return.
- Baselines are versioned with the docs and re-approved by the QA Architect when
  intentionally changed.
- A scan with zero new findings is acceptable **only** when the baseline is
  current; an empty result from a misconfigured scan is treated as a failure.

## 5. Report Contents

Every security report includes:

| Section | Contents |
|---------|----------|
| Summary | Scope, environment, scan date, totals by severity |
| Risk classification | Likelihood × impact rating per finding |
| OWASP mapping | Each finding tied to an A01–A10 category |
| Severity | Critical / High / Medium / Low / Informational |
| Evidence | Request/response excerpt, affected endpoint, reproduction steps |
| Mitigation | Concrete remediation + owning role + target milestone |
| Residual risk | Accepted items with justification and expiry |

Severity drives action: **Critical/High must be fixed before promotion**;
Medium is scheduled; Low is tracked.

## 6. Examples

- **IDOR check:** authenticate as `Patient` A, request `GET /api/v1/patients/{B}`;
  expect `403`. A `200` is an **A01 Critical**, mapped, evidenced, and assigned to
  the backend owner.
- **Dependency CVE:** Dependency-Check flags a transitive library with a known
  RCE. It is an **A06 High**, fixed by a version bump, and a suppression is added
  only if no fix exists, with documented justification and expiry.
- **Header gap:** ZAP passive scan reports a missing `Content-Security-Policy`.
  It is an **A05 Medium**, assigned to the frontend owner, verified by a
  regression assertion.

## Future Enhancements

- Add a dedicated SAST engine alongside SonarQube and DAST-API (OpenAPI-driven
  scanning) in a future version.
- Introduce secret-scanning and Infrastructure-as-Code scanning as the infra
  surface grows (post-1.0).
- Automate ZAP authenticated scans for all 12 RBAC roles in the M8 pipeline.

## Dependencies

- Governed by [SECURITY.md](../SECURITY.md) and
  [MASTER_PROJECT_SPECIFICATION.md](../MASTER_PROJECT_SPECIFICATION.md) §9.
- Targets the environments defined in [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md).
- Tooling versions anchored by [PROJECT_METADATA.md](PROJECT_METADATA.md) §3.
- Gated in CI per [ROADMAP.md](../ROADMAP.md) Milestone 8.

## References

- OWASP Top 10 (2021); OWASP ASVS; OWASP ZAP & Dependency-Check documentation.
- [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md),
  [PERFORMANCE_GUIDE.md](PERFORMANCE_GUIDE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Security/QA Specialist | Initial security-testing guide (Milestone 1) |
