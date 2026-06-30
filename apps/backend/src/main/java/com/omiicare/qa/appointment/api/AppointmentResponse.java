package com.omiicare.qa.appointment.api;

import java.time.Instant;

/** API representation of an appointment. */
public record AppointmentResponse(
        Long id,
        Long patientId,
        Long providerId,
        Instant scheduledStart,
        Instant scheduledEnd,
        String status,
        String reason) {}
