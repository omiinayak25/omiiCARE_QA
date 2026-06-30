package com.omiicare.qa.provider.api;

import com.omiicare.qa.provider.domain.ProviderEntity;

/** API representation of a clinical provider. */
public record ProviderResponse(
        Long id, String code, String firstName, String lastName, String specialty, String status) {

    public static ProviderResponse from(ProviderEntity e) {
        return new ProviderResponse(
                e.getId(),
                e.getCode(),
                e.getFirstName(),
                e.getLastName(),
                e.getSpecialty(),
                e.getStatus());
    }
}
