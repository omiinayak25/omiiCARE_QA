package com.omiicare.qa.automation.data.builder;

import com.omiicare.qa.automation.data.model.PatientData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a minimal FHIR R4 {@code Patient} resource (as a nested {@link Map}, JSON-serialisable)
 * from {@link PatientData} (Data layer — Builder pattern). Kept dependency-free (no Jackson/HAPI):
 * the Service/Assertion layers serialise via RestAssured.
 */
public final class FhirPatientBuilder {

    private String gender = "unknown";
    private String given = "Test";
    private String family = "Patient";
    private String birthDate;
    private boolean active = true;

    private FhirPatientBuilder() {}

    public static FhirPatientBuilder aFhirPatient() {
        return new FhirPatientBuilder();
    }

    /** Maps an OpenMRS-style {@link PatientData} onto a FHIR R4 Patient. */
    public static FhirPatientBuilder from(PatientData patient) {
        return aFhirPatient()
                .withGiven(patient.givenName())
                .withFamily(patient.familyName())
                .withGender("M".equalsIgnoreCase(patient.gender()) ? "male" : "female")
                .withBirthDate(patient.birthDateIso());
    }

    public FhirPatientBuilder withGiven(String v) {
        this.given = v;
        return this;
    }

    public FhirPatientBuilder withFamily(String v) {
        this.family = v;
        return this;
    }

    /** @param fhirGender FHIR AdministrativeGender: male|female|other|unknown */
    public FhirPatientBuilder withGender(String fhirGender) {
        this.gender = fhirGender;
        return this;
    }

    public FhirPatientBuilder withBirthDate(String isoDate) {
        this.birthDate = isoDate;
        return this;
    }

    public FhirPatientBuilder withActive(boolean v) {
        this.active = v;
        return this;
    }

    /** Builds the resource as an ordered map ready for JSON serialisation. */
    public Map<String, Object> build() {
        Map<String, Object> name = new LinkedHashMap<>();
        name.put("use", "official");
        name.put("family", family);
        name.put("given", List.of(given));

        Map<String, Object> resource = new LinkedHashMap<>();
        resource.put("resourceType", "Patient");
        resource.put("active", active);
        resource.put("name", List.of(name));
        resource.put("gender", gender);
        if (birthDate != null) {
            resource.put("birthDate", birthDate);
        }
        return resource;
    }
}
