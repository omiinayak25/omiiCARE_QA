package com.omiicare.qa.provider.domain;

import com.omiicare.qa.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** A clinical provider (e.g. a doctor). Tenant-scoped; identified by its code. */
@Entity
@Table(name = "provider")
public class ProviderEntity extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "hospital_id")
    private Long hospitalId;

    @Column(nullable = false)
    private String code;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column private String specialty;

    @Column(nullable = false)
    private String status = "ACTIVE";

    protected ProviderEntity() {}

    public Long getTenantId() {
        return tenantId;
    }

    public String getCode() {
        return code;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getStatus() {
        return status;
    }
}
