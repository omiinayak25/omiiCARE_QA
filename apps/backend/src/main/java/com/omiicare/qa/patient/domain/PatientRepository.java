package com.omiicare.qa.patient.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Persistence access for {@link PatientEntity}. Every query is tenant-scoped so a caller can only
 * ever read or write patients belonging to their own tenant.
 */
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    Optional<PatientEntity> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByTenantIdAndMrn(Long tenantId, String mrn);

    @Query(
            """
            SELECT p FROM PatientEntity p
            WHERE p.tenantId = :tenantId
              AND ( :term IS NULL
                    OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :term, '%'))
                    OR LOWER(p.lastName)  LIKE LOWER(CONCAT('%', :term, '%'))
                    OR LOWER(p.mrn)       LIKE LOWER(CONCAT('%', :term, '%')) )
            """)
    Page<PatientEntity> search(
            @Param("tenantId") Long tenantId, @Param("term") String term, Pageable pageable);
}
