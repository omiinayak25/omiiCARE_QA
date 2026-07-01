package com.omiicare.qa.automation.data.model;

import java.time.LocalDateTime;

/**
 * Immutable appointment test-data value object (Data layer). Synthetic, PHI-safe.
 *
 * @param patientName patient the appointment is for
 * @param service appointment service / type (e.g. "General Consultation")
 * @param provider provider name (may be {@code null})
 * @param location clinic location (may be {@code null})
 * @param startsAt appointment start date-time
 * @param durationMinutes appointment duration in minutes
 */
public record AppointmentData(
        String patientName,
        String service,
        String provider,
        String location,
        LocalDateTime startsAt,
        int durationMinutes) {}
