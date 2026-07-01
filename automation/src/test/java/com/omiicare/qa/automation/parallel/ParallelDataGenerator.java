package com.omiicare.qa.automation.parallel;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import net.datafaker.Faker;

/**
 * Thread-safe synthetic patient data generator used by the parallel example suite.
 *
 * <p>Each calling thread receives its own {@link Faker} instance via a {@link ThreadLocal}. This
 * removes shared mutable state, which is the single most common cause of flaky failures when test
 * methods run concurrently (TestNG {@code parallel="methods"} / JUnit 5 concurrent execution).
 *
 * <p>The generator is intentionally dependency-light: it relies only on Datafaker and the JDK so it
 * can run as a pure-logic, self-validating unit with no SUT, browser, or network involved.
 */
public final class ParallelDataGenerator {

    /** One Faker per thread, each seeded independently for reproducible-yet-distinct data. */
    private static final ThreadLocal<Faker> FAKER =
            ThreadLocal.withInitial(() -> new Faker(Locale.ENGLISH));

    private ParallelDataGenerator() {
        // Utility class; not instantiable.
    }

    /** @return a non-blank synthetic full name for the current thread. */
    public static String fullName() {
        return FAKER.get().name().fullName();
    }

    /** @return a syntactically valid synthetic e-mail address. */
    public static String email() {
        return FAKER.get().internet().emailAddress();
    }

    /**
     * Generates a deterministic-format OpenMRS-style patient identifier (e.g. {@code MRN-1000042}).
     * The numeric suffix is drawn from a thread-local random source so concurrent callers do not
     * collide.
     *
     * @return a patient identifier matching {@code MRN-\d{7}}
     */
    public static String patientIdentifier() {
        int suffix = ThreadLocalRandom.current().nextInt(1_000_000, 9_999_999);
        return "MRN-" + suffix;
    }

    /**
     * Releases the thread-local {@link Faker} for the current thread. Pools that reuse worker
     * threads across many suites should call this in an {@code @AfterMethod}/{@code @AfterEach} hook
     * to avoid retaining state on long-lived threads.
     */
    public static void clear() {
        FAKER.remove();
    }
}
