package com.omiicare.qa.provider;

import com.omiicare.qa.provider.api.ProviderResponse;
import com.omiicare.qa.provider.domain.ProviderRepository;
import com.omiicare.qa.shared.api.ApiResponse;
import com.omiicare.qa.shared.api.PageResponse;
import com.omiicare.qa.shared.error.ResourceNotFoundException;
import com.omiicare.qa.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only provider directory used when scheduling. Any authenticated user may browse providers;
 * provider administration is out of scope for this milestone.
 */
@RestController
@RequestMapping("/api/v1/providers")
@Tag(name = "Providers", description = "Clinical provider directory (read-only)")
public class ProviderController {

    private final ProviderRepository providerRepository;

    public ProviderController(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List providers (paginated)")
    public ApiResponse<PageResponse<ProviderResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        return ApiResponse.ok(
                PageResponse.from(
                        providerRepository
                                .findAllByTenantId(
                                        TenantContext.getTenantId(),
                                        PageRequest.of(
                                                Math.max(page, 0), safeSize, Sort.by("lastName")))
                                .map(ProviderResponse::from)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Fetch a provider by id")
    public ApiResponse<ProviderResponse> get(@PathVariable Long id) {
        return providerRepository
                .findByIdAndTenantId(id, TenantContext.getTenantId())
                .map(ProviderResponse::from)
                .map(ApiResponse::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", id));
    }
}
