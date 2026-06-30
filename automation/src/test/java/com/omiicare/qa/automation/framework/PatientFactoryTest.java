package com.omiicare.qa.automation.framework;

import static org.assertj.core.api.Assertions.assertThat;

import com.omiicare.qa.automation.core.generators.PatientFactory;
import com.omiicare.qa.automation.core.generators.SyntheticPatient;
import org.junit.jupiter.api.Test;

/** Framework unit test: synthetic data generation produces valid, PHI-safe data. */
class PatientFactoryTest {

    @Test
    void generatesValidSyntheticPatient() {
        SyntheticPatient patient = new PatientFactory().newPatient();
        assertThat(patient.firstName()).isNotBlank();
        assertThat(patient.lastName()).isNotBlank();
        assertThat(patient.gender()).isIn("MALE", "FEMALE", "OTHER", "UNKNOWN");
        assertThat(patient.email()).endsWith("@demo.example");
        assertThat(patient.dateOfBirth()).matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
