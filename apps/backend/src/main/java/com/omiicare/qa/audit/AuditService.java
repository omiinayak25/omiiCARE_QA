package com.omiicare.qa.audit;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Records significant actions (login, logout, create/update/delete of clinical and billing data,
 * role changes) to the audit log. Each record captures the actor, action, target, tenant, and the
 * correlation/request IDs from the MDC. Audit writes run in their own transaction so an audit
 * failure never rolls back the business operation and vice versa.
 */
@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String action, String entityType, String entityId, Long tenantId) {
        repository.save(
                new AuditLogEntity(
                        tenantId,
                        currentActor(),
                        action,
                        entityType,
                        entityId,
                        MDC.get("correlationId"),
                        MDC.get("requestId")));
    }

    private String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "anonymous" : authentication.getName();
    }
}
