package com.omiicare.qa.fhir;

import com.omiicare.qa.patient.domain.PatientRepository;
import com.omiicare.qa.shared.error.ResourceNotFoundException;
import com.omiicare.qa.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FHIR R4 read facade for Patient. Returns {@code application/fhir+json}. The
 * resource is produced from the internal model by {@link FhirPatientMapper}, so
 * the FHIR surface stays decoupled from persistence. Tenant-scoped and gated by
 * the {@code patient:read} permission.
 */
@RestController
@RequestMapping("/api/v1/fhir/Patient")
@Tag(name = "FHIR", description = "FHIR R4 read facade")
public class FhirPatientController {

    private final PatientRepository patientRepository;
    private final FhirPatientMapper fhirPatientMapper;

    public FhirPatientController(
            PatientRepository patientRepository, FhirPatientMapper fhirPatientMapper) {
        this.patientRepository = patientRepository;
        this.fhirPatientMapper = fhirPatientMapper;
    }

    @GetMapping(value = "/{id}", produces = "application/fhir+json")
    @PreAuthorize("hasAuthority('patient:read')")
    @Operation(summary = "Read a Patient as a FHIR R4 resource")
    public Map<String, Object> read(@PathVariable Long id) {
        return patientRepository
                .findByIdAndTenantId(id, TenantContext.getTenantId())
                .map(fhirPatientMapper::toFhir)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    /** Declared to document the FHIR media type; harmless if unused directly. */
    public static MediaType fhirMediaType() {
        return MediaType.valueOf("application/fhir+json");
    }
}
