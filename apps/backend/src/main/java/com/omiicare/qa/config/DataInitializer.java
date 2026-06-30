package com.omiicare.qa.config;

import com.omiicare.qa.security.domain.PermissionEntity;
import com.omiicare.qa.security.domain.PermissionRepository;
import com.omiicare.qa.security.domain.RoleEntity;
import com.omiicare.qa.security.domain.RoleRepository;
import com.omiicare.qa.security.domain.UserEntity;
import com.omiicare.qa.security.domain.UserRepository;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Non-production bootstrap that makes the synthetic DEMO data usable: it grants
 * every permission to {@code SUPER_ADMIN}, assigns that role to {@code demo.admin},
 * and sets a known development password if one has not been set yet. Idempotent
 * and never active in {@code stage}/{@code prod}.
 */
@Component
@Profile({"dev", "local", "docker", "test", "qa"})
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DEMO_ADMIN = "demo.admin";
    private static final String DEMO_ADMIN_DEFAULT_PASSWORD = "Admin@12345";
    private static final String UNSET_PASSWORD_MARKER = "NOT_SET_PENDING_M3_AUTH";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        roleRepository
                .findByCode("SUPER_ADMIN")
                .ifPresent(
                        superAdmin -> {
                            grantAllPermissions(superAdmin);
                            ensureDemoAdmin(superAdmin);
                        });
    }

    private void grantAllPermissions(RoleEntity superAdmin) {
        List<PermissionEntity> all = permissionRepository.findAll();
        if (superAdmin.getPermissions().size() != all.size()) {
            superAdmin.setPermissions(new HashSet<>(all));
            roleRepository.save(superAdmin);
            log.info("Granted {} permissions to SUPER_ADMIN", all.size());
        }
    }

    private void ensureDemoAdmin(RoleEntity superAdmin) {
        userRepository
                .findByUsername(DEMO_ADMIN)
                .ifPresent(
                        admin -> {
                            boolean changed = false;
                            if (admin.getRoles().stream()
                                    .noneMatch(r -> "SUPER_ADMIN".equals(r.getCode()))) {
                                admin.getRoles().add(superAdmin);
                                changed = true;
                            }
                            if (UNSET_PASSWORD_MARKER.equals(admin.getPasswordHash())) {
                                admin.setPasswordHash(
                                        passwordEncoder.encode(DEMO_ADMIN_DEFAULT_PASSWORD));
                                changed = true;
                                log.info(
                                        "Set development password for {} (synthetic demo account)",
                                        DEMO_ADMIN);
                            }
                            if (changed) {
                                userRepository.save(admin);
                            }
                        });
    }
}
