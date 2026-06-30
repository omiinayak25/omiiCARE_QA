# Security Policy

> Security is a first-class concern in omiiCARE_QA. This policy explains which
> versions receive fixes, how to report a vulnerability privately, our response
> commitments, the secrets policy, and — critically — the **healthcare compliance
> scope** of this educational platform.

## Purpose

Provide a single, authoritative process for reporting and handling security issues
in omiiCARE_QA, and to state plainly the boundaries of its healthcare-compliance
modeling so no reader mistakes it for a certified clinical system.

## Scope

- **In scope:** vulnerability reporting, supported versions, response SLAs, the
  secrets policy, the healthcare compliance scope, and responsible-disclosure terms.
- **Out of scope:** detailed security test design (see
  [docs/SECURITY_TESTING_GUIDE.md](docs/SECURITY_TESTING_GUIDE.md)) and conduct
  matters (see [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)).

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Maintainer (`omiinayak25`) | Receives reports, triages, coordinates fixes and disclosure |
| Reporters | Disclose privately and in good faith; allow remediation time |
| Contributors | Never commit secrets or real PHI; follow the secrets policy |

---

## Supported Versions

Because the project is pre-1.0, only the latest release line receives security
fixes. Support widens after v1.0.0.

| Version | Supported |
|---------|-----------|
| `0.1.x` (current) | ✅ Security fixes |
| `< 0.1.0` | ❌ Unsupported |
| `1.0.x` and later | Planned once released (Milestone 10) |

## Reporting a Vulnerability

**Do not open a public issue for security vulnerabilities.**

Report privately by email to the maintainer:

- **Email:** **nayakawadiomkar258@gmail.com**
- **Subject:** `SECURITY: omiiCARE_QA — <short summary>`

Please include, where possible:

- A description of the issue and its impact.
- Steps to reproduce or a proof of concept.
- Affected version, module, and environment.
- Any suggested remediation.

If GitHub private vulnerability reporting is enabled on the repository, you may use
that channel instead. Encrypt sensitive details on request.

## Response SLAs

| Stage | Target |
|-------|--------|
| Acknowledge receipt | Within **3 business days** |
| Initial assessment & severity | Within **7 business days** |
| Remediation plan communicated | Within **14 business days** of confirmation |
| Fix released (critical/high) | As soon as practicable; coordinated with the reporter |

These targets reflect a single-maintainer, pre-1.0 project and will tighten as the
team and release cadence mature.

## Scope of This Policy

This policy covers the omiiCARE_QA repository contents: documentation today, and the
application, infrastructure, automation, and CI/CD artifacts as they land in later
milestones. It does **not** cover third-party services, dependencies' own
infrastructure, or stubbed external systems (WireMock-backed) used only for testing.

## Secrets Policy

- **Never commit secrets** — credentials, tokens, private keys, connection strings,
  or any real PHI — to the repository or its history.
- Configuration uses **`.env.example`** files with placeholder values only; real
  values live in local, untracked `.env` files or a secrets manager.
- Secret scanning and dependency/security scanning (OWASP Dependency-Check, OWASP
  ZAP) are integrated into CI as those milestones land (M7–M8).
- If a secret is committed by mistake, **rotate it immediately** and report it via
  the channel above; history rewriting alone is not sufficient.

## Healthcare Compliance Scope

> **Read this carefully.** omiiCARE_QA is an **educational and portfolio** platform.

- It **models** HIPAA-like privacy and security practices (access control, audit
  logging, least privilege, encryption-at-rest seams) to teach and demonstrate good
  patterns — it is **not** a HIPAA-certified system.
- It **models** healthcare interoperability standards — **FHIR R4**, **HL7 v2**, and
  code systems (ICD-10, CPT, LOINC, SNOMED CT) — for conformance demonstration, not
  for production clinical exchange.
- It uses **synthetic, PHI-safe data exclusively**. No real patient data is present,
  accepted, or processed at any time.
- It makes **NO formal certification claims** — no HIPAA certification, no
  medical-device clearance, and no other regulatory accreditation.
- Nothing in this repository should be deployed for real clinical use without
  independent compliance review, validation, and certification.

## Security Testing

The design and execution of security testing — threat modeling, OWASP ZAP scans,
dependency checks, and the security test suite — are documented in
[docs/SECURITY_TESTING_GUIDE.md](docs/SECURITY_TESTING_GUIDE.md) and realized in
Milestone 7.

## Responsible Disclosure

We support coordinated, responsible disclosure. We ask that you:

- Give us a reasonable opportunity to remediate before any public disclosure.
- Avoid privacy violations, data destruction, and service degradation while testing.
- Use only synthetic data and your own test environments.

In return, we will acknowledge your report, keep you informed of remediation
progress, and credit you (with your consent) in the relevant release notes.

## Examples

- *Reporting:* a contributor finds a missing authorization check in a future API and
  emails the maintainer privately rather than filing a public issue.
- *Secrets:* a `.env.example` ships with `JWT_SECRET=changeme`; the real secret is
  injected locally and never committed.

## Future Enhancements

- Enable GitHub private vulnerability reporting and a published `security.txt`.
- Add a PGP key for encrypted reports.
- Introduce a vulnerability-disclosure acknowledgements page post-1.0.

## Dependencies

- Compliance posture anchored by [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md) §9
  and [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md) §6.
- Realized by [docs/SECURITY_TESTING_GUIDE.md](docs/SECURITY_TESTING_GUIDE.md).

## References

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [docs/SECURITY_TESTING_GUIDE.md](docs/SECURITY_TESTING_GUIDE.md) · [VERSIONING.md](VERSIONING.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial security policy & compliance scope (Milestone 1) |
