package com.omiicare.qa.automation.bdd;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber step definitions for the appointment booking feature.
 *
 * <p>These steps are intentionally self-contained and log-based: the booking logic is simulated
 * in-memory so the glue compiles and runs without a live SUT. In a wired environment the {@code
 * book(...)} helper would delegate to the {@code ResourceAdapter} REST layer; the surrounding
 * scenario phrasing and assertions remain identical. Tagged {@code @bdd} in the feature so excluded
 * from the default build.
 */
public class AppointmentBookingSteps {

    private static final Logger LOG = LoggerFactory.getLogger(AppointmentBookingSteps.class);
    private static final DateTimeFormatter SLOT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /** Fixed "now" so the past-slot scenario is deterministic regardless of the wall clock. */
    private static final LocalDateTime REFERENCE_NOW = LocalDateTime.parse("2026-07-01T00:00");

    private final Set<String> takenSlots = new HashSet<>();
    private String patientId;
    private BookingResult result;

    /** Outcome of an appointment booking attempt. */
    private record BookingResult(boolean confirmed, String reference, String rejectionReason) {
        static BookingResult confirmed(String reference) {
            return new BookingResult(true, reference, null);
        }

        static BookingResult rejected(String reason) {
            return new BookingResult(false, null, reason);
        }
    }

    @Given("an appointment scheduling session is initialised")
    public void schedulingSessionInitialised() {
        takenSlots.clear();
        patientId = null;
        result = null;
        LOG.info("Appointment scheduling session initialised");
    }

    @Given("an existing patient identified by {string}")
    public void existingPatient(String id) {
        patientId = id;
        LOG.info("Using existing patient {}", id);
    }

    @Given("the slot for provider {string} at {string} is already taken")
    public void slotAlreadyTaken(String provider, String slot) {
        takenSlots.add(slotKey(provider, slot));
        LOG.info("Marked slot taken: provider={} slot={}", provider, slot);
    }

    @When("I request an appointment with provider {string} at {string}")
    public void requestAppointment(String provider, String slot) {
        result = book(patientId, provider, slot);
        LOG.info("Booking attempt for {} -> {}", patientId, result);
    }

    @Then("the appointment is confirmed with a booking reference")
    public void appointmentConfirmed() {
        assertThat(result).isNotNull();
        assertThat(result.confirmed()).as("booking confirmed").isTrue();
        assertThat(result.reference()).as("booking reference").isNotBlank();
    }

    @Then("the booking is rejected because the slot is in the past")
    public void rejectedPast() {
        assertRejectedWith("SLOT_IN_PAST");
    }

    @Then("the booking is rejected because the slot is unavailable")
    public void rejectedUnavailable() {
        assertRejectedWith("SLOT_UNAVAILABLE");
    }

    private void assertRejectedWith(String reason) {
        assertThat(result).isNotNull();
        assertThat(result.confirmed()).as("booking should be rejected").isFalse();
        assertThat(result.rejectionReason()).isEqualTo(reason);
    }

    /**
     * Simulated booking logic. Validates the patient, that the slot is in the future, and that the
     * slot is free, then records the slot and returns a reference.
     */
    private BookingResult book(String patient, String provider, String slot) {
        if (patient == null || patient.isBlank()) {
            return BookingResult.rejected("UNKNOWN_PATIENT");
        }
        LocalDateTime when;
        try {
            when = LocalDateTime.parse(slot, SLOT_FORMAT);
        } catch (DateTimeParseException ex) {
            return BookingResult.rejected("INVALID_SLOT");
        }
        if (!when.isAfter(REFERENCE_NOW)) {
            return BookingResult.rejected("SLOT_IN_PAST");
        }
        String key = slotKey(provider, slot);
        if (!takenSlots.add(key)) {
            return BookingResult.rejected("SLOT_UNAVAILABLE");
        }
        return BookingResult.confirmed("APPT-" + UUID.randomUUID());
    }

    private String slotKey(String provider, String slot) {
        return provider + "@" + slot;
    }
}
