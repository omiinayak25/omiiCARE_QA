package com.omiicare.qa.audit;

import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence access for {@link AuditLogEntity}. Append-only by convention. */
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {}
