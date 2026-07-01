package com.omiicare.qa.automation.core.generators;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pure-logic unit tests for {@link AppointmentFactory}. No SUT, browser, network, or DB required.
 */
class AppointmentFactoryTest {

    @Test
    @DisplayName("newAppointment() produces a valid window with end after start")
    void producesValidAppointment() {
        SyntheticAppointment appt =
                new AppointmentFactory(99L).newAppointment("Patient/p1", "Practitioner/dr1");

        assertThat(appt.patientRef()).isEqualTo("Patient/p1");
        assertThat(appt.practitionerRef()).isEqualTo("Practitioner/dr1");
        assertThat(appt.status())
                .isIn("PROPOSED", "PENDING", "BOOKED", "ARRIVED", "FULFILLED", "CANCELLED", "NOSHOW");
        assertThat(appt.minutesDuration()).isPositive();
        assertThat(appt.description()).isNotBlank();

        Instant start = Instant.parse(appt.start());
        Instant end = Instant.parse(appt.end());
        // End strictly after start, and the window matches the declared duration.
        assertThat(end).isAfter(start);
        assertThat(java.time.Duration.between(start, end).toMinutes())
                .isEqualTo((long) appt.minutesDuration());
    }

    @Test
    @DisplayName("same seed yields identical appointments (deterministic)")
    void deterministicForSameSeed() {
        SyntheticAppointment a = AppointmentFactory.seeded(5L).newAppointment();
        SyntheticAppointment b = AppointmentFactory.seeded(5L).newAppointment();
        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("timestamps are UTC ISO-8601 instants")
    void timestampsAreUtcInstants() {
        SyntheticAppointment appt = new AppointmentFactory(3L).newAppointment();
        assertThat(appt.start()).endsWith("Z");
        assertThat(appt.end()).endsWith("Z");
    }
}
