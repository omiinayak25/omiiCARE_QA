package com.omiicare.qa.security.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence access for {@link UserEntity}, including login lookup by username. */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
