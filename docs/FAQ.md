# FAQ — omiiCARE_QA

> **Purpose.** Fast, honest answers to the questions newcomers, reviewers, and
> contributors actually ask about omiiCARE_QA **1.0.0** — what it is, whether it
> is production/certified, how to run it, why a monorepo, how to switch databases,
> how to extend the automation, whether AI is required, and where the data comes
> from.

## Scope

- **In scope:** common conceptual, setup, and policy questions with direct answers.
- **Out of scope:** the live walkthrough ([DEMO_GUIDE.md](DEMO_GUIDE.md)),
  canonical facts ([PROJECT_METADATA.md](PROJECT_METADATA.md)), and deep design
  ([ARCHITECTURE.md](../ARCHITECTURE.md)). Each answer links to the authoritative doc.

---

## General

### What is omiiCARE_QA?

An **Enterprise Healthcare Quality Engineering Platform**: a single monorepo that
pairs a production-grade healthcare web application (the System Under Test) with a
first-class QA platform that tests it across UI, API, database, performance,
security, accessibility, visual, contract, chaos, and observability layers. See
[README.md](../README.md) and [PROJECT_METADATA.md](PROJECT_METADATA.md).

### Is it production-ready or certified?

**No certification claims.** omiiCARE_QA models **HIPAA-like** privacy practices and
**FHIR/HL7** standards conformance for **educational and portfolio purposes only**.
It makes **no formal HIPAA, medical-device, or other regulatory certification
claims**. Do not deploy it as a real clinical system. See [SECURITY.md](../SECURITY.md)
and the compliance disclaimer in [README.md](../README.md).

### Who maintains it and under what license?

Maintainer: **@omiinayak25**. License: **MIT** (with a healthcare-data notice) —
see [LICENSE](../LICENSE) and [CODEOWNERS](../CODEOWNERS).

### What is the current version?

**1.0.0** — the first stable release (Milestone 10). All ten milestones are
complete; the backend, automation, and AI reactors build green and the frontend
build + lint are green. See [CHANGELOG.md](../CHANGELOG.md) and [ROADMAP.md](../ROADMAP.md).

## Running It

### How do I run it locally?

Follow [DEMO_GUIDE.md](DEMO_GUIDE.md). In short:

1. (Optional) `./scripts/start.sh` — Docker infra stack (needed only for dashboards).
2. Backend on `:8080` — `mvn -pl apps/backend -am spring-boot:run -Dspring-boot.run.profiles=dev` (H2, no external DB).
3. Frontend on `:5173` — `cd apps/frontend && npm ci && npm run dev`.
4. Log in with **`demo.admin` / `Admin@12345`**.

Health: `http://localhost:8080/actuator/health`. API docs:
`http://localhost:8080/swagger-ui.html`.

### What can I do in the demo?

Register a patient, book an appointment, trigger the **double-booking `422`**
(BR-APPT-001 → `OMII-422`), read a **FHIR R4 Patient**, and browse the **OpenAPI**
spec. Step-by-step in [DEMO_GUIDE.md](DEMO_GUIDE.md).

### Where is the API documented?

In Swagger UI at `/swagger-ui.html` (generated from the code) and narratively in
[API_BLUEPRINT.md](API_BLUEPRINT.md). Errors follow RFC 7807 with stable
`OMII-4xx`/`5xx` codes (`shared/error/ErrorCode.java`).

## Architecture

### Why a monorepo?

So the application and the QA platform are **first-class peers**, versioned and
built together, with shared standards and one source of truth. Rationale and
alternatives are in [ADR 0001](architecture/adr/0001-monorepo-structure.md); the
layout is in [REPOSITORY_TOUR.md](REPOSITORY_TOUR.md) and
[PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md).

### Where do the business rules live?

In the domain/service layer, not controllers — e.g. BR-APPT-001 (no double-booking)
is enforced in `appointment/AppointmentService.java` and surfaced as `422`. See
[BUSINESS_RULES.md](BUSINESS_RULES.md) and
[ADR 0003](architecture/adr/0003-clean-architecture-ddd-backend.md).

## Database

### How do I switch the database (H2 ↔ PostgreSQL)?

**By Spring profile only — no code change.** `dev`/`test` use embedded **H2**;
`local`/`docker`/`qa`/`stage`/`prod` use **PostgreSQL**. The same **Flyway**
migrations run on both engines, so behavior is consistent. See
[ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md),
[ADR 0002](architecture/adr/0002-hybrid-database-h2-postgresql.md), and
[ADR 0007](architecture/adr/0007-flyway-database-migrations.md).

```bash
# H2 (fast, zero setup)
-Dspring-boot.run.profiles=dev
# PostgreSQL via the Docker stack
./scripts/start.sh ; -Dspring-boot.run.profiles=docker
```

## Extending the Platform

### How do I add a new automation test target or adapter?

The automation layer targets capabilities through a **resource-adapter layer**, not
hard-coded URLs, so the suite stays environment-independent. To add a target:

1. Add/extend an adapter under `automation/shared/core/` (and config under
   `automation/shared/config/`).
2. Register fixtures/data under `automation/resources/`.
3. Write the test in the matching runner folder — `automation/restassured/`
   (API), `automation/playwright/` or `automation/selenium/` (UI), or
   `automation/bdd/` (Gherkin).

See [ADR 0004](architecture/adr/0004-resource-adapter-layer-automation.md) and
`automation/README.md`. The competency path is Stage 3 of
[LEARNING_ROADMAP.md](LEARNING_ROADMAP.md).

### How do I add a new quality discipline?

Create a folder under `quality/` with a `README.md` + `EXECUTION_GUIDE.md`,
following an existing sibling (e.g. `quality/performance/`). Wire it into CI via a
reusable workflow — see [CI_CD_GUIDE.md](CI_CD_GUIDE.md).

## AI

### Is AI required to use the platform?

**No.** AI is **opt-in and disabled by default** (`omii.ai.enabled=false`). With AI
off, the platform is **fully functional** — no AI calls are made. See
`ai/documentation/AI_CONFIGURATION.md` and [AI_DEVELOPMENT_RULES.md](AI_DEVELOPMENT_RULES.md).

### Which AI providers are supported?

A **provider abstraction** supports Claude, OpenAI, and local LLMs — selected by
configuration, never vendor-coupled. See `ai/README.md` and
[TECHNOLOGY_MATRIX.md](TECHNOLOGY_MATRIX.md) §6.

## Data & Compliance

### Where does the data come from?

**Synthetic, PHI-safe data only.** There is no real patient information anywhere in
the repository. Seeds live in `database/seeds/` and
`apps/backend/src/main/resources/db/seed/`; the policy is in
[TEST_DATA_STRATEGY.md](TEST_DATA_STRATEGY.md) and [SECURITY.md](../SECURITY.md).

### How are healthcare standards handled?

The backend exposes a **FHIR R4 Patient** read facade and models HL7/ICD-10/CPT/
LOINC/SNOMED concepts for education. See [FHIR_GUIDE.md](FHIR_GUIDE.md),
[HL7_GUIDE.md](HL7_GUIDE.md), and `quality/compliance/`. Again: **no certification
claims**.

## Contributing

### How do I contribute?

Read [CONTRIBUTING.md](../CONTRIBUTING.md), follow Conventional Commits and the
[BRANCHING_STRATEGY.md](BRANCHING_STRATEGY.md), and ensure the
[DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) and [QUALITY_GATES.md](QUALITY_GATES.md)
pass. Code ownership is in [CODEOWNERS](../CODEOWNERS).

## Examples

- *Recruiter asking "is this a real hospital system?"* → "No — it is a portfolio
  platform with synthetic data and no certification claims" (see the compliance
  answer above).
- *Contributor asking "do I need an API key for AI?"* → "No — AI is off by default
  and the platform works fully without it."

## Future Enhancements

- Auto-publish this FAQ to the documentation site.
- Add a troubleshooting section as common setup issues are reported.

## Dependencies

- Summarizes answers owned by [PROJECT_METADATA.md](PROJECT_METADATA.md),
  [SECURITY.md](../SECURITY.md), and the linked guides.

## References

- [README.md](../README.md) · [DEMO_GUIDE.md](DEMO_GUIDE.md) ·
  [REPOSITORY_TOUR.md](REPOSITORY_TOUR.md)
- [ENVIRONMENT_GUIDE.md](ENVIRONMENT_GUIDE.md) · [CONTRIBUTING.md](../CONTRIBUTING.md)

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial (Milestone 10) |
