# Security Reporting Guide — omiiCARE_QA (Milestone 7)

> EDUCATIONAL / local-infra-only. Defines the structure and conventions for
> every security report produced from ZAP, Dependency-Check, and manual test
> cases. No formal certification is claimed.

## 1. Report structure

Every security report (per scan run or per assessment) follows this order:

1. **Executive Summary** — 3–6 sentences: what was tested, when, against which
   build/commit, headline result (pass / pass-with-warnings / fail), and the
   count of findings by severity.
2. **Scope & Environment** — target URL (`http://localhost:8080`), build/commit
   SHA, test data set, tools and versions (ZAP image tag, Dependency-Check
   version), authenticated vs unauthenticated, in/out-of-scope endpoints.
3. **Methodology** — DAST (ZAP baseline/active), SCA (Dependency-Check), manual
   cases (SEC-TC-*), and the OWASP Top 10 lenses applied.
4. **Risk Classification Summary** — table of finding counts by severity and the
   overall residual-risk rating.
5. **Findings** — one entry per finding (template in §4).
6. **OWASP Mapping** — each finding tagged to its OWASP Top 10 2021 category
   (cross-reference `OWASP_TOP10_MAPPING.md`).
7. **Mitigation & Remediation Plan** — recommended fix, owner, target date,
   verification step per finding.
8. **Appendix** — raw tool output links (ZAP HTML, Dependency-Check report),
   suppressions applied, false positives with justification.

## 2. Severity scale

| Severity | Definition | Action |
|----------|------------|--------|
| **Critical** | Direct compromise of PHI, auth, or tenant isolation; trivially exploitable | Block release; fix immediately |
| **High** | Significant control weakness exploitable with moderate effort | Fix before release |
| **Medium** | Defense-in-depth gap; limited impact or harder to exploit | Fix within the sprint |
| **Low** | Hardening / informational | Backlog |

Use **CVSS v3.1** to justify severity where applicable (e.g. Dependency-Check
gates on CVSS >= 7.0). DAST/manual findings may be rated by impact x likelihood
when no CVSS exists.

## 3. Risk classification

For each finding combine **impact** (PHI exposure, integrity, availability) and
**likelihood** (exploit complexity, auth required, exposure surface) into the
severity above. Healthcare context raises impact for any PHI-touching path.

| Likelihood / Impact | Low | Medium | High |
|---------------------|-----|--------|------|
| **High** | Medium | High | Critical |
| **Medium** | Low | Medium | High |
| **Low** | Low | Low | Medium |

## 4. Finding entry template

```
### FIND-<n>: <short title>
- Severity:        Critical | High | Medium | Low
- OWASP 2021:      A0x <category>
- CVSS (if any):   <score> (<vector>)
- Source:          ZAP <rule id> | Dependency-Check <CVE> | SEC-TC-<id> (manual)
- Affected:        <endpoint / dependency / file>
- Tenant impact:   <cross-tenant? PHI exposed? which permission?>
- Description:     <what the issue is and why it matters>
- Evidence:        <request/response excerpt, screenshot ref, log line>
- Reproduction:    <numbered steps>
- Recommendation:  <concrete fix>
- Status:          Open | In progress | Fixed | Suppressed (with justification)
```

## 5. Conventions

- Reference RFC7807 codes (OMII-4xx) in evidence, not free-text messages.
- Redact PHI from evidence; use synthetic test data only.
- Link tool output stored under `quality/security/zap/reports/` and
  `apps/backend/target/dependency-check-report.html` (gitignore generated HTML;
  commit the summary).
- A finding that is a **regression** vs the committed baseline is flagged and
  blocks release until triaged.
- Suppressed/false-positive items are listed in the appendix with the same
  justification recorded in the tool config.

## 6. Pass/fail gate (release)

A build **fails** the security gate if any of:
- ZAP produces a `FAIL`-classified alert (see `zap/zap-baseline.conf`).
- Dependency-Check finds an unsuppressed CVE with CVSS >= 7.0.
- Any Critical/High manual finding (SEC-TC-*) is Open.
- A new High/Critical finding appears versus the baseline (regression).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Security Engineer | Initial (Milestone 7) |
