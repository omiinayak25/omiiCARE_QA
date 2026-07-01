package com.omiicare.qa.automation.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.omiicare.qa.automation.data.builder.AppointmentBuilder;
import com.omiicare.qa.automation.data.builder.FhirPatientBuilder;
import com.omiicare.qa.automation.data.builder.PatientBuilder;
import com.omiicare.qa.automation.data.model.AppointmentData;
import com.omiicare.qa.automation.data.model.PatientData;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for the Data layer (builders + factory). No SUT — runs in the default build. */
class DataLayerTest {

    @Test
    @DisplayName("PatientBuilder.randomValid produces a complete, valid, unique patient")
    void randomValidPatient() {
        PatientData p = PatientBuilder.randomValid().build();
        assertThat(p.givenName()).isNotBlank();
        assertThat(p.familyName()).startsWith("Qa");
        assertThat(p.gender()).isIn("M", "F");
        assertThat(p.birthDate()).isNotNull();
        assertThat(p.birthDateIso()).matches("\\d{4}-\\d{2}-\\d{2}");
    }

    @Test
    void builderOverridesAndFullName() {
        PatientData p =
                PatientBuilder.aPatient()
                        .withGivenName("John")
                        .withFamilyName("Doe")
                        .withGender("F")
                        .withAge(40)
                        .build();
        assertThat(p.fullName()).isEqualTo("John Doe");
        assertThat(p.gender()).isEqualTo("F");
    }

    @Test
    void builderRejectsMissingRequiredFields() {
        assertThatThrownBy(() -> PatientBuilder.aPatient().withGivenName("A").build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void factoryProducesUniqueFamilyNames() {
        assertThat(TestDataFactory.randomPatient().familyName())
                .isNotEqualTo(TestDataFactory.randomPatient().familyName());
    }

    @Test
    void fhirPatientBuilderMapsFromPatientData() {
        PatientData p =
                PatientBuilder.aPatient()
                        .withGivenName("Jane")
                        .withFamilyName("Roe")
                        .withGender("F")
                        .withAge(30)
                        .build();
        Map<String, Object> fhir = FhirPatientBuilder.from(p).build();
        assertThat(fhir.get("resourceType")).isEqualTo("Patient");
        assertThat(fhir.get("gender")).isEqualTo("female");
        assertThat(fhir).containsKey("birthDate");
    }

    @Test
    void appointmentBuilderDefaults() {
        AppointmentData a = AppointmentBuilder.defaultFor("John Doe").build();
        assertThat(a.patientName()).isEqualTo("John Doe");
        assertThat(a.durationMinutes()).isEqualTo(30);
        assertThat(a.service()).isNotBlank();
    }
}
