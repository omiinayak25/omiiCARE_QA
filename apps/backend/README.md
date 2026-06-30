# Backend — Healthcare Platform Core

> **Status:** Planned — delivered in **Milestone 3**.
> This file is a *module charter*. It documents intent and boundaries so the
> directory has a clear contract before any code lands. It is intentionally
> code-free during Milestone 1 (Foundation, Architecture & Governance).

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
