package com.omiicare.qa.automation.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Generic date helpers (Utility layer). No business logic; reusable across suites.
 */
public final class DateUtils {

    /** ISO date format (yyyy-MM-dd), the canonical form used for FHIR/REST payloads. */
    public static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private DateUtils() {}

    /** Formats a date as {@code yyyy-MM-dd}. */
    public static String iso(LocalDate date) {
        return date == null ? null : date.format(ISO);
    }

    /** Parses a {@code yyyy-MM-dd} string into a {@link LocalDate}. */
    public static LocalDate parseIso(String value) {
        return LocalDate.parse(value, ISO);
    }

    /** Whole-year age from a birth date to today. */
    public static int ageInYears(LocalDate birthDate) {
        return ageInYears(birthDate, LocalDate.now());
    }

    /** Whole-year age from a birth date to a reference date. */
    public static int ageInYears(LocalDate birthDate, LocalDate reference) {
        if (birthDate == null || reference == null) {
            throw new IllegalArgumentException("birthDate and reference must not be null");
        }
        return Period.between(birthDate, reference).getYears();
    }

    /** A birth date approximately {@code years} old (today minus years). */
    public static LocalDate birthDateForAge(int years) {
        return LocalDate.now().minusYears(years);
    }
}
