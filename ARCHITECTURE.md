# Architecture

> **Purpose.** Describe the complete target architecture of omiiCARE_QA — the
> healthcare platform *and* the quality-engineering platform that tests it — at
> a level sufficient to guide every milestone. Architecture is designed **before**
> implementation; if a better design emerges, this document changes first, then
> the code follows.

## Scope

This document covers the macro architecture: the monorepo composition, the
backend's internal layering, the frontend's modular structure, the automation
platform, infrastructure, and the cross-cutting seams (multi-tenancy, events,
observability) that v1.0 *anticipates* even where it does not fully implement
them. Per-component detail lives in the `docs/` blueprints and in ADRs.

## Responsibilities

| Role | Concern this document serves |
|------|------------------------------|
| Software Architect | Layering, dependencies, coupling boundaries |
| QA Architect | Testability seams, adapter layer, environment independence |
| Healthcare Architect | Domain fidelity, standards mapping, privacy posture |

---

## 1. Architectural Goals & Constraints

**Goals**
- One monorepo, many cleanly separated modules.
- The healthcare web app is the primary **System Under Test (SUT)**; the QA
  platform is a first-class peer, not an afterthought.
- Configuration over code: environment and database switch by **configuration only**.
- Testability by design: stable selectors, deterministic seams, adapter
  interfaces instead of direct coupling to external systems.
- Anticipate scale (multi-tenant SaaS, event-driven, distributed tracing)
  without over-building it in v1.0.

**Constraints (v1.0 boundary)** — see [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md)
- No native mobile, no Kubernetes/microservices split, no distributed DB, no
  production cloud deployment, no real external integrations, no real PHI, no
  formal certification. Architecture leaves *seams* for these; implementation
  stops at the documented boundary.

## 2. System Context (C4 Level 1)

```
            +-------------------+        +------------------------+
            |  Healthcare User  |        |  QA / SDET Engineer    |
            | (12 RBAC roles)   |        |  (authoring & running) |
            +---------+---------+        +-----------+------------+
                      |                              |
                      v                              v
            +-------------------------------------------------------+
            |                 omiiCARE_QA Monorepo                  |
            |                                                       |
            |  [Frontend SUT] --HTTP--> [Backend API + FHIR] --> [DB]|
            |        ^                        ^                      |
            |        |                        |                     |
            |  [Automation Platform] --adapters--> (SUT + external) |
            +----------------------+--------------------------------+
                                   |
                                   v
            +-------------------------------------------------------+
            | Stubbed/mocked externals (WireMock): insurance,       |
            | payment, SMS, email, IdP, registry, lab, FHIR, HL7    |
            +-------------------------------------------------------+
```

External systems are **stubbed** in v1.0 and reached only through adapter
interfaces, so a real vendor can replace a stub without touching business or
test logic.

## 3. Monorepo Composition (C4 Level 2 — Containers)

| Module | Tech | Milestone | Role |
|--------|------|-----------|------|
| `apps/backend` | Spring Boot | M3 | Domain, REST + FHIR APIs, auth, audit |
| `apps/frontend` | React + Vite | M4 | Primary SUT, role-based portals |
| `database` | Flyway + SQL | M2 | Schema, migrations, PHI-safe seeds |
| `infrastructure` | Docker Compose | M2 | Postgres, Redis, MinIO, Keycloak, WireMock, Prometheus, Grafana, SonarQube |
| `automation` | Playwright/Selenium/Rest Assured/Cucumber | M5 | UI/API/DB test platform + adapters |
| `manual-testing` | Markdown assets | M6 | Requirements, RTM, cases, suites, defects |
| `quality` | JMeter/k6/ZAP/axe/… | M7 | Perf, security, a11y, visual, contract, chaos |
| `ai` | Provider-abstracted | M9 | Optional AI assistants |
| `.github` | GitHub Actions | M8 | Reusable CI/CD, templates, quality gates |
| `docs` | Markdown | M1+ | Governance, blueprints, ADRs |

See [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) for the full tree.

## 4. Backend Architecture (Clean Architecture + DDD) — M3

Dependencies point **inward**. The domain knows nothing about Spring, JPA, or HTTP.

```
        +------------------------------------------------------+
        |                     API / Web                        |  controllers, DTOs, OpenAPI
        |   +----------------------------------------------+   |
        |   |               Application                    |   |  use cases, ports, orchestration
        |   |   +--------------------------------------+   |   |
        |   |   |              Domain                  |   |   |  entities, value objects, domain events,
        |   |   |   (no framework dependencies)        |   |   |  invariants, specifications
        |   |   +--------------------------------------+   |   |
        |   +----------------------------------------------+   |
        |              Infrastructure (adapters)               |  JPA repos, integration clients, messaging
        +------------------------------------------------------+
```

- **Domain:** entities (Patient, Encounter, …), value objects, domain events,
  business invariants, specifications.
- **Application:** use cases and *ports* (interfaces). Transactions begin here.
- **Infrastructure:** JPA persistence adapters, external-system clients, file
  storage (MinIO), messaging — all implementing application ports.
- **API:** thin controllers, request/response wrappers, Problem Details errors;
  no business logic, no exception handling in controllers (a central framework
  owns it). All endpoints versioned under `/api/v1/`.

Cross-cutting frameworks (M3): centralized **validation**, **exception**, and
**audit** layers; RBAC + permission-based authorization on every endpoint.

## 5. Frontend Architecture (Modular React) — M4

Feature-first modular structure: `app/`, `features/`, `components/` (reusable
library), `layouts/` (per-role portals), `services/` (centralized API clients),
`hooks/`, `contexts/`, `routing/` (route + permission guards), plus i18n,
theming, and error boundaries. Testability is a requirement: stable IDs,
accessible selectors, and consistent DOM so the M5 automation framework needs no
app changes.

## 6. Automation Architecture (Adapter-Centric) — M5

The defining pattern is the **Resource Adapter Layer**: every external target
(Local omiiCARE, OpenMRS, OpenEMR, HAPI FHIR, SMART Health IT, OpenFDA,
DummyJSON, Restful Booker) implements a common interface. Tests call the
interface, never the system. Adding a target = adding an adapter; switching
environments = configuration only. Shared `core/config/drivers/reporting/…`
layers serve Playwright, Selenium, Rest Assured, and BDD uniformly.

## 7. Cross-Cutting Seams (anticipated in v1.0)

| Concern | v1.0 treatment | Future |
|---------|----------------|--------|
| Multi-tenancy | Tenant-scoped entities/queries/audit from M3; isolation verified | Per-tenant DB, network split |
| Event-driven | Domain events + Outbox documented/seamed; synchronous code assumes other consumers | Kafka/RabbitMQ, Saga, replay |
| Observability | OpenTelemetry wired in M2; correlation/request/trace/span IDs propagated | Full tracing backends (M7) |
| Background jobs | Idempotent, retry-safe scheduler layer (M3) | Distributed scheduling |
| File/document mgmt | MinIO, versioning, encryption-at-rest, audit (M3) | DICOM, AV pipeline |
| Notifications | Channel-agnostic framework, pluggable channels (M3) | Real gateways |

## 8. Data Architecture

Profile-driven hybrid database (H2 ↔ PostgreSQL) with **no code change** to
switch. Normalized schema, Flyway-versioned, with audit tables, constraints,
and indexes. Standards mapping (FHIR/HL7/ICD-10/CPT/LOINC/SNOMED) is additive so
new standards do not force refactors. See
[docs/DATABASE_BLUEPRINT.md](docs/DATABASE_BLUEPRINT.md).

## 9. Quality Architecture

The test pyramid (unit → component → integration → contract → API → UI, with
manual, performance, security, accessibility as explicit layers) governs balance
and ownership; see [docs/TEST_PYRAMID.md](docs/TEST_PYRAMID.md). Quality gates in
CI enforce the [Definition of Done](docs/DEFINITION_OF_DONE.md).

## 10. Architecture Decision Records

Every significant decision is recorded in `docs/architecture/adr/` with Problem,
Alternatives, Decision, Tradeoffs, and Future Impact. The index lives at
[docs/architecture/adr/README.md](docs/architecture/adr/README.md).

## Examples

- *Switching dev → docker:* change the active Spring profile; H2 gives way to
  PostgreSQL with zero code change (§8).
- *Pointing API tests at HAPI FHIR instead of local omiiCARE:* select a
  different adapter via configuration; test code is untouched (§6).

## Future Enhancements

Microservices extraction along bounded contexts, Kubernetes deployment,
event-bus implementation, GraphQL/gRPC surfaces, and distributed test execution —
all on the post-1.0 roadmap (see [ROADMAP.md](ROADMAP.md)).

## Dependencies

- Governed by [MASTER_PROJECT_SPECIFICATION.md](MASTER_PROJECT_SPECIFICATION.md)
  and [docs/PROJECT_METADATA.md](docs/PROJECT_METADATA.md).
- Realized incrementally per [ROADMAP.md](ROADMAP.md).

## References

- C4 model (context/container/component/code).
- Clean Architecture (Martin); Domain-Driven Design (Evans); Hexagonal
  Architecture (Cockburn).
- [docs/DESIGN_PATTERNS.md](docs/DESIGN_PATTERNS.md), [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Software/QA/Healthcare Architects | Initial target architecture (Milestone 1) |
