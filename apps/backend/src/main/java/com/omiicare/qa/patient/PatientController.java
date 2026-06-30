package com.omiicare.qa.patient;

import com.omiicare.qa.patient.api.CreatePatientRequest;
import com.omiicare.qa.patient.api.PatientResponse;
import com.omiicare.qa.patient.api.UpdatePatientRequest;
import com.omiicare.qa.shared.api.ApiResponse;
import com.omiicare.qa.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Patient management API. Authorization is permission-based ({@code patient:read} / {@code
 * patient:write}) so access does not depend on hardcoded role names.
 */
@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patients", description = "Patient registration and demographics")
public class PatientController {

    private static final int MAX_PAGE_SIZE = 100;

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('patient:read')")
    @Operation(summary = "Search/list patients (paginated)")
    public ApiResponse<PageResponse<PatientResponse>> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Page<PatientResponse> result =
                patientService.search(
                        q, PageRequest.of(Math.max(page, 0), safeSize, Sort.by("lastName")));
        return ApiResponse.ok(PageResponse.from(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('patient:read')")
    @Operation(summary = "Fetch a patient by id")
    public ApiResponse<PatientResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(patientService.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('patient:write')")
    @Operation(summary = "Register a new patient")
    public ApiResponse<PatientResponse> create(@Valid @RequestBody CreatePatientRequest request) {
        return ApiResponse.ok(patientService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('patient:write')")
    @Operation(summary = "Update an existing patient")
    public ApiResponse<PatientResponse> update(
            @PathVariable Long id, @Valid @RequestBody UpdatePatientRequest request) {
        return ApiResponse.ok(patientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('patient:write')")
    @Operation(summary = "Delete a patient")
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}
