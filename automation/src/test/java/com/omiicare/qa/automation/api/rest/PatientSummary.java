package com.omiicare.qa.automation.api.rest;

/**
 * Immutable view of an OpenMRS REST patient resource, projecting the fields most tests assert on.
 * Mirrors the shape returned by {@code GET /ws/rest/v1/patient/{uuid}} and the entries inside
 * {@code GET /ws/rest/v1/patient?q=...}.
 *
 * @param uuid the patient's universally unique identifier
 * @param display the human-readable display string (typically identifier + preferred name)
 * @param identifier the preferred patient identifier value, or {@code null} when absent
 * @param givenName the preferred person's given name, or {@code null} when absent
 * @param familyName the preferred person's family name, or {@code null} when absent
 * @param gender the person's gender code (e.g. {@code M}, {@code F}), or {@code null} when absent
 * @param voided whether the patient record is voided (soft-deleted)
 */
public record PatientSummary(
        String uuid,
        String display,
        String identifier,
        String givenName,
        String familyName,
        String gender,
        boolean voided) {

    /**
     * @return {@code true} when a non-blank UUID is present
     */
    public boolean hasUuid() {
        return uuid != null && !uuid.isBlank();
    }
}
