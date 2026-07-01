package com.omiicare.qa.automation.core.generators;

/**
 * An immutable, PHI-safe synthetic appointment used as test data.
 *
 * <p>Timestamps are ISO-8601 instants (UTC, {@code ...Z}) so they round-trip cleanly through
 * FHIR/REST payloads. {@code end} is always strictly after {@code start}.
 *
 * @param patientRef logical reference to the subject patient (e.g. "Patient/example")
 * @param practitionerRef logical reference to the attending provider (e.g. "Practitioner/example")
 * @param status appointment lifecycle status (e.g. PROPOSED | BOOKED | ARRIVED | FULFILLED |
 *     CANCELLED)
 * @param start ISO-8601 UTC start instant
 * @param end ISO-8601 UTC end instant (after {@code start})
 * @param minutesDuration scheduled duration in minutes (matches start/end window)
 * @param description short human-readable reason for the visit
 */
public record SyntheticAppointment(
        String patientRef,
        String practitionerRef,
        String status,
        String start,
        String end,
        int minutesDuration,
        String description) {}
