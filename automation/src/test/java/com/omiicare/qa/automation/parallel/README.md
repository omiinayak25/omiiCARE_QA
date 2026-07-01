# Parallel Execution & CI

This package wires up parallel test execution for both supported runners and provides
self-validating examples that are safe to run on every build.

## Files

| File | Purpose |
|------|---------|
| `ParallelDataGenerator.java` | Thread-safe (`ThreadLocal<Faker>`) synthetic patient-data generator. |
| `ParallelExampleTest.java` | TestNG example, all methods self-validating (no SUT). Referenced by `testng-parallel.xml`. |
| `ParallelDataGeneratorTest.java` | JUnit 5 example proving the generator's thread-safety; runs on default build. |
| `../../../../../resources/testng-parallel.xml` | TestNG suite, `parallel="methods"`, `thread-count="4"`. |
| `../../../../../resources/junit-platform-parallel.properties` | JUnit 5 parallel profile (4 fixed threads). Does **not** replace `junit-platform.properties`. |

## Design principles

- **No shared mutable state.** `ParallelDataGenerator` hands each thread its own `Faker`
  via a `ThreadLocal`, the most common fix for flaky parallel suites. Call `clear()` in
  teardown so pooled worker threads do not retain state.
- **Default build stays green & offline.** Every test in this package is pure logic
  (validates `FrameworkConfig`, `TargetSystem`, and the generator). Nothing here needs a
  SUT, browser, network, or database, so nothing is tagged out.
- **SUT-backed tests are isolated.** Real end-to-end suites must use
  `@Tag("ui-e2e")` / `@Tag("api-e2e")` / `@Tag("bdd")` (JUnit) or a separate, non-default
  `testng.xml` (TestNG) so they are excluded from `mvn test`.

## Running

### JUnit 5 (parallel profile)

```bash
mvn test \
  -Djunit.platform.config.file.name=junit-platform-parallel.properties
```

To run only the parallel package:

```bash
mvn test -Dtest='com.omiicare.qa.automation.parallel.*' \
  -Djunit.platform.config.file.name=junit-platform-parallel.properties
```

### TestNG (parallel suite)

```bash
mvn -DsuiteXmlFile=src/test/resources/testng-parallel.xml test
```

`parallel="methods"` + `thread-count="4"` runs up to four `@Test` methods concurrently;
`data-provider-thread-count="4"` fans out parallel `@DataProvider` rows the same way.

## CI guidance

- Pin `thread-count` / `fixed.parallelism` to the runner's available cores (here: 4) to
  avoid oversubscription, which itself causes timeout-driven flakiness.
- Run the fast, offline default suite (`mvn test`) on every push; gate
  `ui-e2e` / `api-e2e` / `bdd` tagged suites behind a separate, on-demand or nightly job
  that has the SUT, browser, and network available.
- Export environment overrides (e.g. `OMII_ENV`, `OMII_ADAPTER_OPENMRS_BASEURI`) as CI
  secrets/variables; `FrameworkConfig` resolves system properties → env vars →
  `framework.properties`, so nothing is hardcoded.
- Publish Allure/Extent results as build artifacts after the parallel run completes.
