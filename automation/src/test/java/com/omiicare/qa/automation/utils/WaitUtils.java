package com.omiicare.qa.automation.utils;

import java.time.Duration;
import java.util.function.BooleanSupplier;

/**
 * Generic, framework-agnostic polling waits (Utility layer). Contains NO business logic and NO
 * OpenMRS/UI-specific behaviour — only reusable "wait until a condition holds" helpers.
 *
 * <p>Prefer tool-native waits (Playwright auto-waiting, Awaitility) inside the UI/Service layers;
 * this exists for the cases where a plain, dependency-free condition poll is the clearest option.
 */
public final class WaitUtils {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_INTERVAL = Duration.ofMillis(250);

    private WaitUtils() {}

    /** Polls {@code condition} until it is {@code true} or the default timeout elapses. */
    public static boolean waitUntil(BooleanSupplier condition) {
        return waitUntil(condition, DEFAULT_TIMEOUT, DEFAULT_INTERVAL);
    }

    /** Polls {@code condition} until it is {@code true} or {@code timeout} elapses. */
    public static boolean waitUntil(BooleanSupplier condition, Duration timeout) {
        return waitUntil(condition, timeout, DEFAULT_INTERVAL);
    }

    /**
     * Polls {@code condition} at {@code interval} until it holds or {@code timeout} elapses.
     *
     * @return {@code true} if the condition became true within the timeout, else {@code false}
     */
    public static boolean waitUntil(BooleanSupplier condition, Duration timeout, Duration interval) {
        if (condition == null) {
            throw new IllegalArgumentException("condition must not be null");
        }
        long deadline = System.nanoTime() + timeout.toNanos();
        do {
            if (condition.getAsBoolean()) {
                return true;
            }
            sleep(interval);
        } while (System.nanoTime() < deadline);
        return condition.getAsBoolean();
    }

    /** Uninterruptible-ish sleep that restores the interrupt flag. */
    public static void sleep(Duration duration) {
        try {
            Thread.sleep(Math.max(0, duration.toMillis()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
