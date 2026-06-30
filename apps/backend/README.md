# Backend — Healthcare Platform Core

> **Status:** Delivered (initial vertical) — **Milestone 3**.
> The bootable shell (profiles, Flyway, observability) landed in Milestone 2; the
> domain, security, audit/exception frameworks, healthcare modules (Patient,
> Provider, Appointment), and the FHIR read facade landed in Milestone 3.
> Test: `mvn -pl apps/backend test`. Run: `mvn -pl apps/backend spring-boot:run`
> (dev profile, H2). Swagger UI at `/swagger-ui.html`.

## Purpose

Java + Spring Boot backend implementing the healthcare domain (Clean Architecture / DDD), REST + FHIR APIs, authentication/authorization, audit/validation/exception frameworks, and persistence.

## Planned Contents

- `domain/` — entities, value objects, domain events (framework-free)
- `application/` — use cases, services, ports
- `infrastructure/` — persistence (JPA), adapters, integrations
- `api/` — REST controllers, DTOs, OpenAPI
- `config/` — Spring profiles, security, beans

## Boundaries

- This module is **not** implemented during Milestone 1. No application, API,
  or automation code exists here yet.
- Build order and the exact scope are governed by [ROADMAP.md](../../ROADMAP.md)
  and the master spec at
  [MASTER_PROJECT_SPECIFICATION.md](../../MASTER_PROJECT_SPECIFICATION.md).

## References

- [ARCHITECTURE.md](../../ARCHITECTURE.md)
- [PROJECT_STRUCTURE.md](../../PROJECT_STRUCTURE.md)
- [docs/](../../docs/)
