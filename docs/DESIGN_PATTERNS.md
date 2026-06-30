# Design Patterns

> **Purpose.** Catalogue the design patterns omiiCARE_QA deliberately uses and
> the ones it reserves for documented future use, so that contributors apply a
> shared, intentional vocabulary. Patterns are tools, not goals: each is applied
> **only where it reduces complexity** (KISS/YAGNI). A pattern used for its own
> sake is a defect.

## Scope

The patterns that recur across the backend ([ARCHITECTURE.md](../ARCHITECTURE.md) §4)
and the automation platform (§6): Repository, Specification, Mediator, Observer,
Command, Adapter, Strategy, Factory, and Builder. It also documents CQRS and
Event Sourcing as **future** options that the architecture seams for but does not
implement in v1.0. Per-module mechanics live in module READMEs and ADRs.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Software Architect | Decide where a pattern earns its place; record the rationale in an ADR |
| Contributors | Use the catalogued vocabulary; do not introduce un-catalogued patterns without an ADR |
| Reviewers | Reject pattern misuse (over-engineering or wrong fit) |

---

## 1. Guiding Rule

> **Apply a pattern only when it removes more complexity than it adds.** Prefer
> the simplest construct that satisfies the requirement. A plain function or a
> single class beats a pattern whenever it is clearer. Patterns 4–9 below
> directly serve [PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md) #4 (reusability)
> and #5 (composition over inheritance).

## 2. Pattern → Module → Milestone Map

| Pattern | Primary module | Introduced | Status in v1.0 |
|---------|----------------|-----------|----------------|
| Repository | `apps/backend` (infrastructure) | M3 | Used |
| Specification | `apps/backend` (domain) | M3 | Used |
| Mediator | `apps/backend` (application) | M3 | Used (use-case dispatch) |
| Observer | `apps/backend` (domain events) | M3 | Used (in-process) |
| Command | `apps/backend` (application) + `automation` | M3 / M5 | Used |
| Adapter | `automation` (resource adapters), `apps/backend` (integration) | M3 / M5 | Used (defining pattern) |
| Strategy | `apps/backend`, `automation` | M3 / M5 | Used |
| Factory | `apps/backend`, `automation` (drivers/data) | M3 / M5 | Used |
| Builder | `apps/backend` (test data, value objects), `automation` | M3 / M5 | Used |
| CQRS | `apps/backend` | — | **Future / seamed** |
| Event Sourcing | `apps/backend` | — | **Future / seamed** |

## 3. Patterns In Use

### 3.1 Repository
- **Intent.** Mediate between the domain and data mapping, exposing a
  collection-like interface so the domain never knows about JPA or SQL.
- **Where applied.** `apps/backend` infrastructure layer implements application
  *ports* (e.g. `PatientRepository`) defined in the application layer. Keeps the
  domain framework-free (ARCHITECTURE §4).
- **Sketch.** The application layer declares `interface PatientRepository {
  Optional<Patient> findById(PatientId id); Patient save(Patient p); }`; the
  infrastructure layer provides a JPA-backed implementation. Tests swap an
  in-memory implementation with no domain change.

### 3.2 Specification
- **Intent.** Encapsulate a business rule as a composable, reusable predicate
  object that can be combined with `and`/`or`/`not`.
- **Where applied.** Domain invariants and query criteria (e.g. "active patient
  with an open encounter") expressed as specifications, reusable by both
  validation and repository queries.
- **Sketch.** `Specification<Patient> active = p -> p.status() == ACTIVE;` then
  `active.and(hasOpenEncounter)` — one rule, reused in validation and filtering,
  avoiding duplicated `if` logic (Principle #4).

### 3.3 Mediator
- **Intent.** Decouple callers from handlers by routing a request to its single
  handler through a mediator, so controllers depend on use cases abstractly.
- **Where applied.** Thin API controllers dispatch a request object to the
  application layer via a mediator rather than wiring each use case directly,
  keeping controllers logic-free (ARCHITECTURE §4).
- **Sketch.** A controller builds `RegisterPatientCommand` and calls
  `mediator.send(command)`; the mediator resolves the matching handler. Adding a
  use case adds a handler, not controller wiring.

### 3.4 Observer
- **Intent.** Let interested parties react to domain events without the emitter
  knowing the listeners.
- **Where applied.** In-process domain events (e.g. `PatientRegistered`) raised
  by aggregates; audit and notification subscribers react. The event-driven seam
  (ARCHITECTURE §7) later swaps the in-process bus for a broker without changing
  emitters.
- **Sketch.** An aggregate records `PatientRegistered`; an audit subscriber and a
  notification subscriber handle it independently. Emitter stays oblivious.

### 3.5 Command
- **Intent.** Encapsulate a request as an object, enabling validation, queuing,
  logging, and undo around a single intent.
- **Where applied.** Application-layer write operations modelled as command
  objects; in `automation`, reusable test actions are commands for composition
  and retry.
- **Sketch.** `ScheduleAppointmentCommand` carries its data and is handled in one
  transactional boundary; a test framework composes commands into deterministic,
  retryable steps.

### 3.6 Adapter — *the defining pattern of the QA platform*
- **Intent.** Convert a foreign interface into the interface the system expects,
  so callers depend on a stable abstraction, not a concrete external system.
- **Where applied.** The **Resource Adapter Layer** (ARCHITECTURE §6): Local
  omiiCARE, OpenMRS, OpenEMR, HAPI FHIR, SMART Health IT, OpenFDA, DummyJSON, and
  Restful Booker each implement one common interface. Backend integration clients
  also adapt stubbed externals (WireMock) behind ports.
- **Sketch.** Tests call `fhirAdapter.readPatient(id)`; configuration chooses
  whether that resolves to local omiiCARE or HAPI FHIR. Adding a target = adding
  an adapter; switching environments = configuration only (Principle #3).

### 3.7 Strategy
- **Intent.** Define a family of interchangeable algorithms selected at runtime.
- **Where applied.** Pluggable behaviors such as notification channels, retry
  policies, and (in `automation`) browser/driver and data-generation strategies.
- **Sketch.** `NotificationStrategy` has email/SMS/in-app implementations chosen
  by configuration; the caller is agnostic to which is active.

### 3.8 Factory
- **Intent.** Centralize and abstract object creation, hiding construction
  complexity from callers.
- **Where applied.** Driver creation in `automation` (Playwright/Selenium),
  test-data object creation, and domain value-object construction with
  validation.
- **Sketch.** `DriverFactory.create(config)` returns a configured driver; callers
  never touch browser-specific construction details.

### 3.9 Builder
- **Intent.** Construct complex objects step by step with readable, validated,
  immutable results.
- **Where applied.** Immutable domain value objects and aggregates, and
  fluent test-data builders in `automation` (e.g. `PatientTestDataBuilder`).
- **Sketch.** `Patient.builder().name(name).mrn(mrn).build()` validates
  invariants once and yields an immutable instance — no half-built objects.

## 4. Future Patterns (seamed, not implemented in v1.0)

| Pattern | Why reserved | Seam already present |
|---------|--------------|----------------------|
| **CQRS** | Read/write models may diverge as reporting grows; premature in v1.0 | Application-layer command/query separation keeps a clean split point |
| **Event Sourcing** | Full event-store rebuild is unjustified at v1.0 scale | Domain events + Outbox documented (ARCHITECTURE §7) give a future event log |

Both are activated only by a roadmap-amending ADR; until then the synchronous
model with documented seams is the deliberate, simpler choice (YAGNI).

## Examples

- *Adapter over coupling:* pointing API tests at HAPI FHIR instead of local
  omiiCARE selects a different adapter via config — test code is untouched.
- *Specification over duplication:* the "eligible for discharge" rule is written
  once as a specification and reused by validation and a repository query.
- *Anti-example:* wrapping a one-line calculation in a Strategy with a single
  implementation is rejected in review as over-engineering.

## Future Enhancements

- A pattern-conformance check in review templates linking each new abstraction to
  its catalogue entry or a justifying ADR.
- CQRS read-model and Event-Sourcing reference ADRs drafted ahead of any v2.0
  scale-out (see [ROADMAP.md](../ROADMAP.md) Future Versions).

## Dependencies

- Realizes [ARCHITECTURE.md](../ARCHITECTURE.md) §4 and §6.
- Constrained by [PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md) (#4, #5) and
  [CODING_STANDARDS.md](CODING_STANDARDS.md).

## References

- Gamma et al., *Design Patterns*; Fowler, *Patterns of Enterprise Application
  Architecture*; Evans, *Domain-Driven Design*.
- [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md), `architecture/adr/`.

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | Software Architect | Initial pattern catalogue with future CQRS/Event Sourcing seams (Milestone 1) |
