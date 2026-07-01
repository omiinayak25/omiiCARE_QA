package com.omiicare.qa.automation.utils;

import java.util.Locale;

/**
 * Generic string helpers (Utility layer). No business logic.
 */
public final class StringUtils {

    private StringUtils() {}

    /** True if the value is {@code null}, empty, or only whitespace. */
    public static boolean isBlank(String value) {
        return value == null || value.strip().isEmpty();
    }

    /** True if the value has at least one non-whitespace character. */
    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    /** Trims and collapses internal whitespace runs into single spaces. */
    public static String normalize(String value) {
        return value == null ? null : value.strip().replaceAll("\\s+", " ");
    }

    /** Null-safe, case-insensitive containment check on normalized text. */
    public static boolean containsIgnoreCase(String haystack, String needle) {
        if (haystack == null || needle == null) {
            return false;
        }
        return haystack.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
    }

    /** Left-pads a sequence number, e.g. {@code padId(7, 4) -> "0007"}. */
    public static String padId(long value, int width) {
        return String.format("%0" + Math.max(1, width) + "d", value);
    }
}
