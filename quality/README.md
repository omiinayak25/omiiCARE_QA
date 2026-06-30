# Advanced Quality Engineering

> **Status:** Delivered — **Milestone 7**.
> Reusable frameworks and representative suites across performance, security,
> accessibility, visual, database, contract, chaos, resilience, observability, and
> compliance — integrated with the Milestone 5 automation platform and the
> Milestone 2 monitoring stack.

## Purpose

Extend omiiCARE_QA into a complete quality-engineering ecosystem. Each module
ships real, runnable artifacts plus the documentation needed to operate it.
Per the roadmap, this milestone delivers frameworks and representative suites —
not thousands of executions.

## Modules

| Module | Tooling | Key artifacts |
|--------|---------|---------------|
| [performance/](performance/) | k6, JMeter, Gatling | load/stress scripts, JMeter plan, Gatling sim, execution/reporting guides |
| [security/](security/) | OWASP ZAP, Dependency-Check | ZAP baseline + auth context, suppressions, OWASP Top 10 mapping, SEC-TC-* cases |
| [accessibility/](accessibility/) | axe-core, Lighthouse | Playwright a11y specs, Lighthouse CI config, WCAG mapping |
| [visual/](visual/) | Playwright | visual specs (light/dark), baseline/approval workflow |
| [database-testing/](database-testing/) | SQL | integrity + migration validation queries, DB-TC-* cases |
| [contract-testing/](contract-testing/) | JSON Schema, OpenAPI, FHIR | response/envelope/FHIR schemas, CT-* cases |
| [chaos/](chaos/) | Docker, tc/netem | CHAOS-* experiment catalog (hypothesis/method/recovery) |
| [resilience/](resilience/) | Resilience4j (future) | retry/circuit-breaker/timeout patterns, RES-* cases |
| [observability/](observability/) | Prometheus, Grafana | alert rules, QE dashboard, wiring guide |
| [compliance/](compliance/) | — | HIPAA-like, FHIR/HL7, WCAG/OWASP baseline checklists |

## Rules

- **Performance:** load tests run **only** against local/Docker/owned
  infrastructure — never public websites (stated in every script and guide).
- **Security:** scans target owned infrastructure only.
- **Compliance:** practices are modeled for education; **no formal certification**
  is claimed (see [SECURITY.md](../SECURITY.md)).

## References

- [docs/PERFORMANCE_GUIDE.md](../docs/PERFORMANCE_GUIDE.md) · [docs/SECURITY_TESTING_GUIDE.md](../docs/SECURITY_TESTING_GUIDE.md)
- [docs/ACCESSIBILITY_GUIDE.md](../docs/ACCESSIBILITY_GUIDE.md) · [automation/](../automation/) · [infrastructure/](../infrastructure/)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial advanced-QE modules (Milestone 7) |
