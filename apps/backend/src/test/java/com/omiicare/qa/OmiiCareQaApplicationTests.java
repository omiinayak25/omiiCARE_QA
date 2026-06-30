package com.omiicare.qa;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Milestone 2 smoke tests: verify the application context loads under the test profile, Flyway
 * applies the baseline schema, and the seed data is present. These guard the bootable
 * infrastructure shell, not business behavior.
 */
@SpringBootTest
@ActiveProfiles("test")
class OmiiCareQaApplicationTests {

    @Autowired private DataSource dataSource;

    @Test
    void contextLoads() {
        assertThat(dataSource).isNotNull();
    }

    @Test
    void flywayBaselineCreatesCoreTables() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Integer roleCount = jdbc.queryForObject("SELECT COUNT(*) FROM role", Integer.class);
        assertThat(roleCount).isEqualTo(12);
    }

    @Test
    void seedCreatesDemoTenant() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Integer tenantCount =
                jdbc.queryForObject(
                        "SELECT COUNT(*) FROM tenant WHERE code = 'DEMO'", Integer.class);
        assertThat(tenantCount).isEqualTo(1);
    }
}
