package com.omiicare.qa.appointment.api;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/** Payload to book a new appointment. */
public record BookAppointmentRequest(
        @NotNull Long patientId,
        @NotNull Long providerId,
        @NotNull @Future(message = "scheduledStart must be in the future") Instant scheduledStart,
        @NotNull Instant scheduledEnd,
        @Size(max = 500) String reason) {}
