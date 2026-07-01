package com.omiicare.qa.automation.api.rest;

/**
 * Immutable view of an OpenMRS REST encounter resource as returned by
 * {@code GET /ws/rest/v1/encounter/{uuid}} or entries of
 * {@code GET /ws/rest/v1/encounter?patient={uuid}}.
 *
 * @param uuid the encounter UUID
 * @param display the human-readable display string
 * @param encounterDatetime the ISO-8601 encounter datetime string, or {@code null} when absent
 * @param patientUuid the UUID of the patient the encounter belongs to, or {@code null} when absent
 * @param encounterTypeUuid the UUID of the encounter type, or {@code null} when absent
 * @param voided whether the encounter is voided (soft-deleted)
 */
public record EncounterSummary(
        String uuid,
        String display,
        String encounterDatetime,
        String patientUuid,
        String encounterTypeUuid,
        boolean voided) {

    /**
     * @return {@code true} when a non-blank UUID is present
     */
    public boolean hasUuid() {
        return uuid != null && !uuid.isBlank();
    }
}
