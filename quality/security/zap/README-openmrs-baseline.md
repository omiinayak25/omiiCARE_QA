# OWASP ZAP Baseline Scan — OpenMRS Reference Application

A safe, passive web-security baseline for an OpenMRS Reference Application
instance, driven by [`zap-baseline-openmrs.conf`](./zap-baseline-openmrs.conf).

> This README documents the **OpenMRS-specific** baseline. A generic
> `zap-baseline.conf` may also exist in this directory; the OpenMRS variant adds
> context-path scoping and tuned rule actions for the RefApp.

---

## ⚠️ Owned-Environment Only — Read First

Security scanning sends a high volume of crafted requests and spiders the whole
application. It can degrade a server and **may be treated as an attack**.

- **Only** scan an OpenMRS instance you **own** or are **explicitly authorized**
  to test — e.g. a local Docker stack (`http://localhost:8080/openmrs`) or a
  dedicated QA host.
- **Never** scan `o2.openmrs.org`, the shared community demo, any third-party
  server, or production. Unauthorized scanning may be **illegal**.
- The example commands deliberately target `localhost`. Change the target only
  to another environment you control.

---

## What the Baseline Does

The ZAP **baseline** scan (`zap-baseline.py`) is **passive**:

1. Spiders the target for a bounded time (default ~1 minute).
2. Passively inspects every request/response (headers, cookies, content).
3. Reports findings; it does **not** fire active SQLi/XSS payloads.

This makes it the safest scan to run in CI on an owned ephemeral environment.
For deeper coverage, run a **full** active scan (`zap-full-scan.py`) **only** on
a throwaway owned environment with explicit sign-off — never from this baseline
config.

---

## Prerequisites

- Docker (recommended), or a local ZAP install (`zap.sh`).
- A reachable, **owned** OpenMRS instance.

---

## How to Run (Docker)

From the repository root:

```bash
docker run --rm -t \
  --network host \
  -v "$(pwd)/quality/security/zap:/zap/wrk:rw" \
  zaproxy/zap-stable zap-baseline.py \
    -t http://localhost:8080/openmrs \
    -c zap-baseline-openmrs.conf \
    -r zap-baseline-openmrs-report.html \
    -w zap-baseline-openmrs-report.md \
    -J zap-baseline-openmrs-report.json \
    -z "spider.maxDepth=5 spider.maxChildren=20"
```

- `--network host` lets the container reach `localhost` on the host. On macOS /
  Windows use `-t http://host.docker.internal:8080/openmrs` instead.
- `-v .../zap:/zap/wrk` maps this directory in so ZAP can read the `.conf` and
  write reports here.
- Reports (`*-report.html` / `.md` / `.json`) land in
  `quality/security/zap/`. They are scan artifacts — keep them out of version
  control (add to `.gitignore`).

### Authenticated scanning (optional)

The baseline runs unauthenticated by default. To exercise authenticated pages,
supply a ZAP context/session or a replacer rule with a valid session cookie for
your **owned** instance. See `auth-context.md` in this directory if present, or
the [ZAP authentication docs](https://www.zaproxy.org/docs/authentication/).

---

## Reading the Results

The `.conf` file maps each passive rule to an action:

| Action   | Meaning                                                |
|----------|--------------------------------------------------------|
| `FAIL`   | Breaks the build (exit code 1). Reserved for high-value issues (CSP, cookie flags, sensitive-data leaks). |
| `WARN`   | Reported but non-blocking. Triage and ticket as needed. |
| `IGNORE` | Suppressed (known/accepted OpenMRS noise). Re-review before production sign-off. |

### Exit codes

| Code | Meaning                                  |
|------|------------------------------------------|
| `0`  | No FAIL/WARN above threshold.            |
| `1`  | At least one `FAIL` rule triggered.      |
| `2`  | At least one `WARN` (when `-I` not set). |

Gate CI on exit code `1` against an **owned ephemeral environment only**.

---

## Tuning

- Promote a rule to `FAIL` once the corresponding header/cookie issue is fixed,
  to prevent regressions.
- Remove `IGNORE` suppressions before any production-readiness review so latent
  issues surface.
- Keep spider scope tight (`spider.maxDepth`, `spider.maxChildren`) so the scan
  stays on-host and finishes quickly.

---

## Mapping to OWASP

Findings map to the OWASP Top 10 (e.g. missing CSP / security headers →
A05 Security Misconfiguration; cookie flags → A05/A07; sensitive-data-in-URL →
A04 Insecure Design). See `../OWASP_TOP10_MAPPING.md` for the full project
mapping.
