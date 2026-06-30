package com.omiicare.qa.security.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence access for {@link PermissionEntity}. */
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {}
