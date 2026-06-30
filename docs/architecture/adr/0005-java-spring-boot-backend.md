# 0005. Java 21 + Spring Boot 3 backend stack

## Status

Accepted — 2026-06-30

## Context / Problem

The healthcare platform core (Milestone 3) needs a backend language and framework
that supports a rich domain model, first-class testing, a mature security and
persistence ecosystem, healthcare-standards libraries (FHIR/HL7), and strong
long-term support — while signaling enterprise competence to the QA/SDET roles
this portfolio targets. We must choose the backend stack now because it shapes the
build system, the test foundation, the API tooling, and every downstream module
that integrates with the backend.

## Decision Drivers

- Long-term support and stability (LTS language, current major framework).
- Mature ecosystem for security, persistence, migrations, API docs, and testing.
- First-class healthcare-standards tooling (e.g. HAPI FHIR on the JVM).
- Strong static typing and tooling for a large, evolving domain.
- Portfolio relevance: a stack widely expected in enterprise healthcare/QA roles.

## Alternatives Considered

### Alternative A — Java 21 (LTS) + Spring Boot 3.x (chosen)
- **Pros:** LTS language; Spring Boot 3 is the current enterprise standard with
  Spring Security, Spring Data JPA/Hibernate, Flyway, springdoc/OpenAPI, JUnit 5,
  Mockito, and Testcontainers all first-class; HAPI FHIR and HL7 libraries are
  mature on the JVM; excellent observability (Micrometer/OpenTelemetry); the most
  common enterprise healthcare backend stack — high portfolio signal.
- **Cons:** heavier runtime and startup than lightweight stacks; more boilerplate
  (mitigated by Lombok/MapStruct where justified); JVM memory footprint.

### Alternative B — Node.js + NestJS (TypeScript)
- **Pros:** shares TypeScript with the frontend; fast startup; large ecosystem.
- **Cons:** weaker enterprise FHIR/HL7 tooling than the JVM; less mature for
  heavy transactional domains; the portfolio already showcases TS on the
  frontend, so Java better demonstrates polyglot enterprise breadth.

### Alternative C — C# + .NET / ASP.NET Core
- **Pros:** strong typing, excellent tooling, good performance, Firely FHIR SDK.
- **Cons:** ecosystem and hosting conventions diverge from the JVM-centric
  automation tooling (Rest Assured, JUnit, TestNG, Allure) this platform builds
  on; less cohesive with the chosen test stack.

### Alternative D — Python + FastAPI
- **Pros:** rapid development; readable; good for AI integration (M9).
- **Cons:** dynamic typing is weaker for a large invariant-rich domain; FHIR/HL7
  and enterprise transactional tooling are less mature than the JVM's.

## Decision

We will build the backend on **Java 21 (LTS)** with **Spring Boot 3.x**, using
Spring Security + JWT, Spring Data JPA + Hibernate, Flyway for migrations,
MapStruct for mapping, springdoc for OpenAPI, Maven as the build tool, and JUnit 5
+ Mockito (with Testcontainers) for tests, exactly as registered in
[docs/PROJECT_METADATA.md](../../PROJECT_METADATA.md) §3.

## Consequences / Tradeoffs

**Positive**
- A complete, mature enterprise toolchain for security, persistence, migrations,
  API docs, and testing out of the box.
- Best-in-class healthcare-standards libraries on the JVM (HAPI FHIR, HL7).
- Cohesion with the JVM-based automation stack (Rest Assured, JUnit, Allure).
- Strong, recognizable enterprise signal for the target roles.

**Negative / Accepted tradeoffs**
- Higher memory/startup cost than lightweight runtimes — acceptable for the
  target deployment profile.
- Some boilerplate; constrained, justified use of Lombok and MapStruct mitigates it.
- Two languages in the repo (Java + TypeScript) — accepted as deliberate breadth.

## Future Impact

The Spring Boot foundation cleanly supports the M3 cross-cutting frameworks
(audit/validation/exception), multi-tenancy, OpenTelemetry observability, and the
event-driven seams the architecture anticipates. It leaves room for a future
microservices split (post-1.0) since each bounded context can become its own
Spring Boot service.

## References

- [docs/PROJECT_METADATA.md](../../PROJECT_METADATA.md) §3 (Backend)
- [ARCHITECTURE.md](../../../ARCHITECTURE.md) §4
- [0003](0003-clean-architecture-ddd-backend.md)
- Spring Boot 3, Java 21 LTS, HAPI FHIR documentation.
