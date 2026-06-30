package com.omiicare.qa.security.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence access for {@link RoleEntity}. */
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByCode(String code);
}
