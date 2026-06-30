# OWASP Dependency-Check (SCA) — omiiCARE_QA (Milestone 7)

> EDUCATIONAL / local-infra-only. Software Composition Analysis (SCA) scans the
> project's declared dependencies against the NVD CVE feed and other advisories.

## 1. Purpose

Detect known-vulnerable, outdated third-party components in the Maven build
(OWASP Top 10 **A06: Vulnerable and Outdated Components**). Output feeds the
release security gate and the regression baseline described in
[`../README.md`](../README.md).

## 2. Running on the Maven build

### 2.1 Maven plugin (recommended)

Add the plugin to `apps/backend/pom.xml` (build/plugins) and run from the backend
module:

```xml
<plugin>
  <groupId>org.owasp</groupId>
  <artifactId>dependency-check-maven</artifactId>
  <version>10.0.4</version>
  <configuration>
    <formats>
      <format>HTML</format>
      <format>JSON</format>
    </formats>
    <suppressionFiles>
      <suppressionFile>
        ${maven.multiModuleProjectDirectory}/quality/security/dependency-check/dependency-check-suppressions.xml
      </suppressionFile>
    </suppressionFiles>
    <!-- Fail the build when any dependency has a CVSS >= threshold. -->
    <failBuildOnCVSS>7.0</failBuildOnCVSS>
  </configuration>
</plugin>
```

Run:

```bash
# From the backend module directory:
cd apps/backend
mvn org.owasp:dependency-check-maven:check
# Reports: apps/backend/target/dependency-check-report.html (+ .json)
```

### 2.2 One-off without editing the POM

```bash
cd apps/backend
mvn org.owasp:dependency-check-maven:check \
  -Dformats=HTML,JSON \
  -DfailBuildOnCVSS=7.0 \
  -DsuppressionFiles=../../quality/security/dependency-check/dependency-check-suppressions.xml
```

### 2.3 NVD API key (strongly recommended)

Without an NVD API key the database update is rate-limited and slow. Obtain a
free key at <https://nvd.nist.gov/developers/request-an-api-key> and pass it:

```bash
mvn org.owasp:dependency-check-maven:check -DnvdApiKey="${NVD_API_KEY}"
```

CI should cache the dependency-check data directory between runs to avoid
re-downloading the full NVD feed.

## 3. Suppression usage

False positives and accepted risks are recorded in
[`dependency-check-suppressions.xml`](./dependency-check-suppressions.xml). Each
suppression **must** include:

- A `<notes>` justification (why it is a false positive or accepted, who
  approved, and a review/expiry date).
- A precise scope — match by `<sha1>`, `<packageUrl>`, or a tightly anchored
  `<filePath>` regex — never a broad wildcard.
- A specific `<cve>`, `<vulnerabilityName>`, or anchored `<cpe>`; never suppress
  "all CVEs" for a dependency.

The report's "suppress" button generates a ready-to-paste `<suppress>` block;
copy it in, then add notes and tighten the scope.

## 4. Thresholds

| Gate | Policy |
|------|--------|
| `failBuildOnCVSS` | **7.0** — any unsuppressed CVE with CVSS ≥ 7.0 (High/Critical) fails the build |
| Medium (4.0–6.9) | WARN — triage within the sprint; suppress with justification or upgrade |
| Low (< 4.0) | Informational — track, no gate |
| New High/Critical vs baseline | **Regression** — blocks release |

## 5. Workflow

1. Run the scan on the current build.
2. Triage each finding: upgrade the dependency, or suppress with a justified,
   scoped, time-boxed entry.
3. Re-run; confirm the build passes the threshold.
4. Commit the suppression file changes (not the generated HTML report).
5. Compare against the baseline; investigate any new High/Critical CVEs.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Security Engineer | Initial (Milestone 7) |
