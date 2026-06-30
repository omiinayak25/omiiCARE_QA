package com.omiicare.qa.provider.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Tenant-scoped persistence access for {@link ProviderEntity}. */
public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    Optional<ProviderEntity> findByIdAndTenantId(Long id, Long tenantId);

    Page<ProviderEntity> findAllByTenantId(Long tenantId, Pageable pageable);
}
