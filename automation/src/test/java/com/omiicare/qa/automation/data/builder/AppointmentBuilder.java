package com.omiicare.qa.automation.data.builder;

import com.omiicare.qa.automation.data.model.AppointmentData;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Fluent {@link AppointmentData} builder (Data layer — Builder pattern).
 */
public final class AppointmentBuilder {

    private String patientName;
    private String service = "General Consultation";
    private String provider;
    private String location = "Outpatient Clinic";
    private LocalDateTime startsAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS);
    private int durationMinutes = 30;

    private AppointmentBuilder() {}

    public static AppointmentBuilder anAppointment() {
        return new AppointmentBuilder();
    }

    /** A near-future appointment for the given patient with sensible defaults. */
    public static AppointmentBuilder defaultFor(String patientName) {
        return anAppointment().withPatientName(patientName);
    }

    public AppointmentBuilder withPatientName(String v) {
        this.patientName = v;
        return this;
    }

    public AppointmentBuilder withService(String v) {
        this.service = v;
        return this;
    }

    public AppointmentBuilder withProvider(String v) {
        this.provider = v;
        return this;
    }

    public AppointmentBuilder withLocation(String v) {
        this.location = v;
        return this;
    }

    public AppointmentBuilder startingAt(LocalDateTime v) {
        this.startsAt = v;
        return this;
    }

    public AppointmentBuilder withDurationMinutes(int minutes) {
        this.durationMinutes = minutes;
        return this;
    }

    public AppointmentData build() {
        if (patientName == null || patientName.isBlank()) {
            throw new IllegalStateException("patientName is required");
        }
        return new AppointmentData(
                patientName, service, provider, location, startsAt, durationMinutes);
    }
}
