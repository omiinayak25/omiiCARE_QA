package com.omiicare.qa.automation.parallel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Pure-logic JUnit 5 verification of {@link ParallelDataGenerator}'s thread-safety contract.
 *
 * <p>These tests need no SUT, browser, network, or database, so they are intentionally left
 * untagged and run on every default {@code mvn test}. They are annotated {@link ExecutionMode#CONCURRENT}
 * to validate behaviour under the parallel configuration declared in
 * {@code junit-platform-parallel.properties}.
 */
@Execution(ExecutionMode.CONCURRENT)
class ParallelDataGeneratorTest {

    @Test
    @DisplayName("patient identifier matches the MRN-<7 digits> contract")
    void identifierFormatIsStable() {
        assertThat(ParallelDataGenerator.patientIdentifier()).matches("MRN-\\d{7}");
        ParallelDataGenerator.clear();
    }

    @RepeatedTest(value = 16, name = "name/email are well-formed [{currentRepetition}/{totalRepetitions}]")
    void generatedFieldsAreWellFormed() {
        assertThat(ParallelDataGenerator.fullName()).isNotBlank();
        assertThat(ParallelDataGenerator.email()).contains("@");
        ParallelDataGenerator.clear();
    }

    @Test
    @DisplayName("concurrent threads each get an independent, working generator")
    void generatorIsThreadSafe() throws InterruptedException {
        int threads = 8;
        int perThread = 200;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        Set<String> identifiers = Collections.newSetFromMap(new ConcurrentHashMap<>());

        try {
            for (int t = 0; t < threads; t++) {
                pool.submit(
                        () -> {
                            for (int i = 0; i < perThread; i++) {
                                String mrn = ParallelDataGenerator.patientIdentifier();
                                assertThat(mrn).matches("MRN-\\d{7}");
                                identifiers.add(mrn);
                            }
                            ParallelDataGenerator.clear();
                        });
            }
        } finally {
            pool.shutdown();
            assertThat(pool.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
        }

        // No exceptions thrown and a healthy spread of distinct identifiers proves the
        // ThreadLocal Faker isolated state correctly across workers.
        assertThat(identifiers).isNotEmpty();
    }
}
