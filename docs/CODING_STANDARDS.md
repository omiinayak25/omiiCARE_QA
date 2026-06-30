# Coding Standards

> **Purpose.** Define the concrete, enforceable coding standards for every
> language in omiiCARE_QA so that code is uniform, readable, testable, and
> reviewable regardless of author (human or AI). These standards turn the
> [Project Principles](PROJECT_PRINCIPLES.md) into day-to-day rules and are
> enforced by the [Definition of Done](DEFINITION_OF_DONE.md).

## Scope

Language- and tool-specific standards for: Java 21 / Spring Boot 3, TypeScript /
React 18, SQL / Flyway, test code, comments, and the formatting/static-analysis
toolchain. Versions and tools are taken from
[PROJECT_METADATA.md](PROJECT_METADATA.md) §3; this document never re-decides a
version. No application code exists in Milestone 1 — these standards govern code
authored from Milestone 2 onward.

## Responsibilities

| Role | Responsibility |
|------|----------------|
| Contributors | Write code that passes every standard before requesting review |
| Reviewers | Block changes that violate a standard without a recorded ADR exception |
| QA Architect | Keep the toolchain configuration aligned with these rules |

---

## 1. Java / Spring Boot 3 (Backend, M3)

### 1.1 Naming
| Element | Convention | Example |
|---------|-----------|---------|
| Package | lowercase, no underscores | `com.omiicare.patient.domain` |
| Class / record / enum | PascalCase | `PatientRegistration` |
| Interface (port) | PascalCase, no `I` prefix | `PatientRepository` |
| Method | camelCase verb phrase | `scheduleAppointment()` |
| Constant | UPPER_SNAKE_CASE | `MAX_RETRY_ATTEMPTS` |
| Type parameter | single capital or descriptive | `T`, `TResult` |

### 1.2 Package layout (Clean Architecture)
Packages follow the inward-pointing layering of [ARCHITECTURE.md](../ARCHITECTURE.md) §4:

```
com.omiicare.<context>.domain          // entities, value objects, events, specifications
com.omiicare.<context>.application     // use cases, ports (interfaces)
com.omiicare.<context>.infrastructure  // JPA adapters, integration clients
com.omiicare.<context>.api             // controllers, DTOs
```
The `domain` package imports no Spring, JPA, or HTTP type. Dependencies point
inward only.

### 1.3 Core rules
- **Immutability first.** Prefer `record` and `final` fields. Mutable state only
  where genuinely required and confined.
- **Lombok only where justified.** Permitted: `@Getter`, `@Builder`,
  `@RequiredArgsConstructor`, `@Slf4j`. Forbidden: `@Data`, `@EqualsAndHashCode`
  on entities, `@SneakyThrows`. Records replace Lombok for value objects.
- **Mapping via MapStruct.** DTO↔domain mapping uses MapStruct mappers; never
  hand-write repetitive mapping or leak entities across the API boundary.
- **Exceptions.** Throw specific domain exceptions; never swallow. Controllers
  contain **no** try/catch — a centralized exception layer (ARCHITECTURE §4)
  produces Problem Details responses.
- **Logging via SLF4J.** Use `@Slf4j`; parameterized logging (`log.info("id={}",
  id)`), never string concatenation. No PHI or secrets in logs.
- **Null handling.** Return `Optional<T>` for absence; never return `null` from
  public methods. Validate inputs at the boundary; use `Objects.requireNonNull`
  for invariants.
- **Dependency injection.** Constructor injection only (final fields,
  `@RequiredArgsConstructor`). No field or setter injection.
- **Configuration over hardcoding.** No literal URLs, ports, or credentials —
  bind from `@ConfigurationProperties` (Principle #3).

### 1.4 Java do / don't
| Do | Don't |
|----|-------|
| Use `record` for DTOs and value objects | Use `@Data` mutable beans for value objects |
| Constructor-inject dependencies | Field-inject with `@Autowired` |
| Return `Optional`, throw specific exceptions | Return `null`, throw raw `RuntimeException` |
| Keep controllers thin | Put business logic or try/catch in controllers |
| Parameterized SLF4J logging | `System.out.println` or string-built log messages |

## 2. TypeScript / React 18 (Frontend, M4)

### 2.1 Naming
| Element | Convention | Example |
|---------|-----------|---------|
| Component | PascalCase | `PatientCard` |
| Hook | camelCase, `use` prefix | `usePatient` |
| File (component) | PascalCase `.tsx` | `PatientCard.tsx` |
| File (hook/util) | camelCase `.ts` | `usePatient.ts` |
| Type / interface | PascalCase, no `I` prefix | `PatientSummary` |
| Constant | UPPER_SNAKE_CASE | `DEFAULT_PAGE_SIZE` |

### 2.2 Component structure
- Function components only; one component per file; props typed via an explicit
  `interface`/`type`. No `React.FC`.
- Order inside a component: hooks → derived values → handlers → `return` JSX.
- Co-locate component, its test, and styles in a feature folder
  (ARCHITECTURE §5).

### 2.3 Hooks rules
- Obey the Rules of Hooks: call at the top level, never conditionally.
- Custom hooks own one concern; complete dependency arrays (lint-enforced).
- Server state via TanStack Query; client state via Redux Toolkit **only where
  justified** (PROJECT_METADATA §3).

### 2.4 Validation, i18n, accessibility
- **Validation** with Zod schemas (React Hook Form + Zod); the schema is the
  single source of input truth.
- **No hardcoded user-facing strings** — every label/message comes from i18next
  (Principle #3, supports localization). No magic numbers; name constants.
- **Accessible selectors** (Principle #8): prefer semantic roles and `aria-*`;
  add stable `data-testid` on interactive elements so the M5 automation framework
  needs no app changes. Meet WCAG AA (Principle #7).

### 2.5 TypeScript do / don't
| Do | Don't |
|----|-------|
| `strict` mode; explicit prop and return types | `any`; implicit `any` |
| Drive copy through i18next | Hardcode user-facing strings |
| Validate with Zod schemas | Trust unvalidated form input |
| Semantic HTML + `aria-*` + stable `data-testid` | CSS-class or XPath-only selectors |
| Complete hook dependency arrays | Suppress the exhaustive-deps lint rule |

## 3. SQL / Flyway (M2+)

| Item | Convention | Example |
|------|-----------|---------|
| Migration file | `V<n>__<snake_case>.sql` | `V3__add_encounter_table.sql` |
| Repeatable migration | `R__<snake_case>.sql` | `R__patient_summary_view.sql` |
| Table | snake_case, singular | `patient`, `encounter` |
| Column | snake_case | `created_at`, `mrn` |
| Primary key | `id` (UUID) | `id` |
| Foreign key | `<table>_id` | `patient_id` |
| Index | `ix_<table>_<cols>` | `ix_encounter_patient_id` |

Migrations are immutable once merged; corrections are new migrations. Every
table carries audit columns and constraints (ARCHITECTURE §8).

## 4. Test Code Standards

| Concern | Standard |
|---------|----------|
| Java test class | `<ClassUnderTest>Test` (unit) / `<...>IT` (integration) |
| Java test method | `methodName_condition_expectedResult` |
| Frontend test file | `<Component>.test.tsx` |
| Structure | Arrange–Act–Assert; one behavior per test |
| Data | Builders/factories, PHI-safe synthetic data only |
| Determinism | No sleeps; use Awaitility / explicit waits; no shared mutable state |

Test code meets the same readability bar as production code.

## 5. Comments & Documentation Density

- Comment **why**, not **what**; code names carry the "what".
- Public Java APIs get Javadoc; exported TS types/functions get TSDoc where
  intent is non-obvious.
- No commented-out code, no dead code, no TODO/FIXME in merged code — open an
  issue instead.

## 6. Formatting & Static Analysis Toolchain

| Language | Format | Static analysis |
|----------|--------|-----------------|
| Java | **Spotless** | **Checkstyle**, **PMD**, **SpotBugs**, SonarQube |
| TypeScript/React | **Prettier** | **ESLint**, SonarQube |
| SQL | Spotless (SQL) / project formatter | Flyway validation |

Formatting is non-negotiable and machine-enforced: `spotless:check` /
`prettier --check` must be clean and lint must exit 0 before review
([DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md) §1, rows 4–5). Tool configs are
versioned in the repo and arrive with the toolchain in Milestone 2.

## Examples

- A backend service that returns `null` for a missing patient violates §1.3 and
  is changed to return `Optional<Patient>`.
- A React button labelled with a literal `"Save"` violates §2.4 and must read the
  label from i18next.
- A migration edited after merge violates §3 and is replaced by a new `V<n+1>`
  migration.

## Future Enhancements

- Shared Spotless/Checkstyle/PMD and ESLint/Prettier config modules published for
  reuse across modules (Milestone 2/8).
- A pre-commit hook bundle that runs format + lint locally before push.
- Architecture-fitness tests (e.g. ArchUnit) enforcing the §1.2 layering.

## Dependencies

- Versions and tools from [PROJECT_METADATA.md](PROJECT_METADATA.md) §3.
- Operationalizes [PROJECT_PRINCIPLES.md](PROJECT_PRINCIPLES.md); enforced by
  [DEFINITION_OF_DONE.md](DEFINITION_OF_DONE.md).
- Pattern usage detailed in [DESIGN_PATTERNS.md](DESIGN_PATTERNS.md).

## References

- Google Java Style; Airbnb/Standard TypeScript guidance; Spring Boot reference.
- [ARCHITECTURE.md](../ARCHITECTURE.md), [REPOSITORY_STANDARDS.md](REPOSITORY_STANDARDS.md).

## Version History

| Version | Date | Author | Notes |
|---------|------|--------|-------|
| 1.0 | 2026-06-30 | QA Architect | Initial language coding standards (Milestone 1) |
