package com.omiicare.qa.fhir;

import com.omiicare.qa.patient.domain.PatientEntity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Maps an internal {@link PatientEntity} to a FHIR R4 {@code Patient} resource representation (as
 * an ordered map serialized to JSON). This is the v1.0 read-side mapping; full FHIR validation and
 * a server facade arrive in Milestone 7. Code-system URIs follow the FHIR guide
 * (docs/FHIR_GUIDE.md).
 */
@Component
public class FhirPatientMapper {

    private static final String MRN_SYSTEM = "urn:omiicare:mrn";

    public Map<String, Object> toFhir(PatientEntity p) {
        Map<String, Object> resource = new LinkedHashMap<>();
        resource.put("resourceType", "Patient");
        resource.put("id", String.valueOf(p.getId()));

        Map<String, Object> identifier = new LinkedHashMap<>();
        identifier.put("system", MRN_SYSTEM);
        identifier.put("value", p.getMrn());
        resource.put("identifier", List.of(identifier));

        resource.put("active", "ACTIVE".equalsIgnoreCase(p.getStatus()));

        Map<String, Object> name = new LinkedHashMap<>();
        name.put("use", "official");
        name.put("family", p.getLastName());
        name.put("given", List.of(p.getFirstName()));
        resource.put("name", List.of(name));

        resource.put("gender", mapGender(p.getGender()));
        if (p.getDateOfBirth() != null) {
            resource.put("birthDate", p.getDateOfBirth().toString());
        }

        List<Map<String, Object>> telecom = new ArrayList<>();
        if (StringUtils.hasText(p.getEmail())) {
            telecom.add(Map.of("system", "email", "value", p.getEmail()));
        }
        if (StringUtils.hasText(p.getPhone())) {
            telecom.add(Map.of("system", "phone", "value", p.getPhone()));
        }
        if (!telecom.isEmpty()) {
            resource.put("telecom", telecom);
        }
        return resource;
    }

    /** Maps internal gender to the FHIR {@code AdministrativeGender} value set. */
    private String mapGender(String gender) {
        if (gender == null) {
            return "unknown";
        }
        return switch (gender.toLowerCase(Locale.ROOT)) {
            case "male", "m" -> "male";
            case "female", "f" -> "female";
            case "other" -> "other";
            default -> "unknown";
        };
    }
}
