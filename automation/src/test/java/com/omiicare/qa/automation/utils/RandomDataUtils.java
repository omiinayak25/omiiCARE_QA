package com.omiicare.qa.automation.utils;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Deterministic-friendly random helpers (Utility layer). Generates synthetic, PHI-safe tokens for
 * unique test data. No business logic; no OpenMRS specifics.
 */
public final class RandomDataUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final AtomicLong SEQUENCE = new AtomicLong(0);
    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private RandomDataUtils() {}

    /** A short, unique, monotonically-increasing suffix (safe for unique names/identifiers). */
    public static String uniqueSuffix() {
        return StringUtils.padId(SEQUENCE.incrementAndGet(), 4) + randomAlphanumeric(4);
    }

    /** A random alphanumeric token of the requested length. */
    public static String randomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(Math.max(0, length));
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }

    /** A random integer in {@code [minInclusive, maxInclusive]}. */
    public static int randomInt(int minInclusive, int maxInclusive) {
        if (maxInclusive < minInclusive) {
            throw new IllegalArgumentException("max < min");
        }
        return minInclusive + RANDOM.nextInt(maxInclusive - minInclusive + 1);
    }
}
