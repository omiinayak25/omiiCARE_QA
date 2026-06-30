package com.omiicare.qa.appointment.domain;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Tenant-scoped persistence access for {@link AppointmentEntity}. */
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    Optional<AppointmentEntity> findByIdAndTenantId(Long id, Long tenantId);

    Page<AppointmentEntity> findAllByTenantId(Long tenantId, Pageable pageable);

    /**
     * Counts active (non-cancelled) appointments for a provider that overlap the given half-open
     * interval [start, end). Used to enforce the no-double-booking business rule.
     */
    @Query(
            """
            SELECT COUNT(a) FROM AppointmentEntity a
            WHERE a.tenantId = :tenantId
              AND a.providerId = :providerId
              AND a.status <> 'CANCELLED'
              AND a.scheduledStart < :end
              AND a.scheduledEnd   > :start
            """)
    long countOverlapping(
            @Param("tenantId") Long tenantId,
            @Param("providerId") Long providerId,
            @Param("start") Instant start,
            @Param("end") Instant end);
}
