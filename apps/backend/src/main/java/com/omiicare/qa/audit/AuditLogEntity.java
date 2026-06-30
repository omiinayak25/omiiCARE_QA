package com.omiicare.qa.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;

/** Immutable audit record. Maps to the {@code audit_log} table from V1. */
@Entity
@Table(name = "audit_log")
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column private String actor;

    @Column(nullable = false)
    private String action;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "request_id")
    private String requestId;

    @Lob
    @Column(name = "old_value")
    private String oldValue;

    @Lob
    @Column(name = "new_value")
    private String newValue;

    protected AuditLogEntity() {}

    AuditLogEntity(
            Long tenantId,
            String actor,
            String action,
            String entityType,
            String entityId,
            String correlationId,
            String requestId) {
        this.tenantId = tenantId;
        this.occurredAt = Instant.now();
        this.actor = actor;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.correlationId = correlationId;
        this.requestId = requestId;
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }
}
