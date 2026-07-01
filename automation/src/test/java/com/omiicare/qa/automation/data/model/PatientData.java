package com.omiicare.qa.automation.data.model;

import com.omiicare.qa.automation.utils.DateUtils;
import java.time.LocalDate;

/**
 * Immutable patient test-data value object (Data layer). PHI-safe, synthetic. Produced by
 * {@code PatientBuilder} and consumed by workflows/services — never hardcoded inside tests.
 *
 * @param givenName given (first) name
 * @param middleName optional middle name (may be {@code null})
 * @param familyName family (last) name
 * @param gender OpenMRS gender code: {@code "M"} or {@code "F"}
 * @param birthDate date of birth
 * @param addressLine primary address line (may be {@code null})
 * @param city city / village (may be {@code null})
 * @param phone contact phone (may be {@code null})
 */
public record PatientData(
        String givenName,
        String middleName,
        String familyName,
        String gender,
        LocalDate birthDate,
        String addressLine,
        String city,
        String phone) {

    /** "Given Family" display name. */
    public String fullName() {
        return (givenName + " " + familyName).strip();
    }

    /** Birth date as an ISO {@code yyyy-MM-dd} string. */
    public String birthDateIso() {
        return DateUtils.iso(birthDate);
    }
}
