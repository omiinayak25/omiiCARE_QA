package com.omiicare.qa.automation.data;

import com.omiicare.qa.automation.data.builder.AppointmentBuilder;
import com.omiicare.qa.automation.data.builder.PatientBuilder;
import com.omiicare.qa.automation.data.model.AppointmentData;
import com.omiicare.qa.automation.data.model.PatientData;

/**
 * Central entry point for test data (Data layer — Factory/Facade). Tests and workflows obtain data
 * through this facade rather than constructing builders directly, so data creation stays in one
 * place and free of hardcoded values.
 */
public final class TestDataFactory {

    private TestDataFactory() {}

    /** A complete, valid, unique, PHI-safe synthetic patient. */
    public static PatientData randomPatient() {
        return PatientBuilder.randomValid().build();
    }

    /** A builder seeded with a random valid patient, for targeted overrides. */
    public static PatientBuilder patient() {
        return PatientBuilder.randomValid();
    }

    /** A default near-future appointment for the given patient. */
    public static AppointmentData appointmentFor(String patientName) {
        return AppointmentBuilder.defaultFor(patientName).build();
    }

    /** A builder for an appointment, for targeted overrides. */
    public static AppointmentBuilder appointment(String patientName) {
        return AppointmentBuilder.defaultFor(patientName);
    }
}
