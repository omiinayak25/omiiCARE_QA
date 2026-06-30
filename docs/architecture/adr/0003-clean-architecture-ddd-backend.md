# 0003. Clean Architecture + DDD + Hexagonal for the backend

## Status

Accepted — 2026-06-30

## Context / Problem

The backend (Milestone 3) models a non-trivial healthcare domain — patients,
encounters, orders, results, billing, multi-tenancy, audit, standards mapping
(FHIR/HL7/ICD-10/CPT/LOINC/SNOMED) — and must remain testable, evolvable, and
free of accidental coupling to frameworks. A typical layered Spring service that
lets JPA entities and HTTP concerns leak into business logic becomes hard to
unit-test and brittle as the domain grows. We must decide the internal layering
and dependency rules of the backend so that domain logic is isolated, testable in
isolation, and protected from infrastructure churn.

## Decision Drivers

- Testability by design: domain logic must be unit-testable without Spring, a
  database, or HTTP.
- Dependency inversion: business rules must not depend on frameworks or I/O.
- Replaceable infrastructure: persistence and external clients are details that
  can change behind ports (supports the M5 adapter philosophy and WireMock stubs).
- Domain fidelity: a rich healthcare model with explicit invariants and events.
- Longevity: a structure that survives the full ten-milestone build and beyond.

## Alternatives Considered

### Alternative A — Clean Architecture + DDD + Hexagonal (chosen)
- **Pros:** dependencies point inward; the domain knows nothing about Spring/JPA/
  HTTP; ports/adapters make infrastructure swappable; use cases own transactions;
  domain is unit-testable with no framework; aligns with the resource-adapter
  philosophy in [0004](0004-resource-adapter-layer-automation.md).
- **Cons:** more layers, interfaces, and mapping (DTO ↔ domain ↔ persistence);
  higher upfront ceremony; team must understand the discipline.

### Alternative B — Traditional layered (Controller → Service → Repository)
- **Pros:** familiar, minimal ceremony, fast to start.
- **Cons:** JPA entities and HTTP concerns leak into business logic; hard to unit
  test without the database; the domain becomes anemic; refactoring infrastructure
  ripples into business code.

### Alternative C — Transaction Script / active-record style
- **Pros:** quickest for simple CRUD.
- **Cons:** does not scale to a rich healthcare domain with invariants and events;
  logic scatters; testability and maintainability degrade sharply.

## Decision

We will structure the backend with Clean Architecture and Domain-Driven Design,
realized through a Hexagonal (ports-and-adapters) arrangement: a framework-free
**Domain** core (entities, value objects, domain events, invariants,
specifications); an **Application** layer of use cases and ports that owns
transactions; an **Infrastructure** layer of adapters (JPA repositories, external
clients, file storage, messaging) implementing those ports; and a thin **API**
layer (controllers, DTOs, OpenAPI, Problem Details) with no business logic.
Dependencies point strictly inward.

## Consequences / Tradeoffs

**Positive**
- The domain is pure and unit-testable in milliseconds, no Spring required.
- Infrastructure (database, external systems) is swappable behind ports.
- Clear seams for multi-tenancy, audit, validation, and exception frameworks.
- Architecture mirrors the adapter philosophy used by the automation platform.

**Negative / Accepted tradeoffs**
- More interfaces and explicit mapping (MapStruct) between DTO, domain, and
  persistence models — accepted as the cost of isolation.
- Higher onboarding cost; the layering rules must be documented and enforced in
  review (CODEOWNERS + adversarial review).
- Risk of over-engineering trivial CRUD; pragmatism is required for simple cases.

## Future Impact

Bounded contexts and ports prepare the system for a future microservices split
along context lines (post-1.0, see [ROADMAP.md](../../../ROADMAP.md) v2.0) without
rewriting domain logic. Event-driven seams (domain events, Outbox) live in the
domain/application layers, ready for a real event bus later. The pure domain keeps
FHIR/HL7 mapping additive rather than invasive.

## References

- [ARCHITECTURE.md](../../../ARCHITECTURE.md) §4 (Backend architecture)
- Robert C. Martin, *Clean Architecture*; Eric Evans, *Domain-Driven Design*;
  Alistair Cockburn, *Hexagonal Architecture*.
- [0004](0004-resource-adapter-layer-automation.md),
  [0005](0005-java-spring-boot-backend.md)
- [docs/DESIGN_PATTERNS.md](../../DESIGN_PATTERNS.md)
