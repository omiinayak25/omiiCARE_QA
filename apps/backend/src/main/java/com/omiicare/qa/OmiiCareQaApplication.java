package com.omiicare.qa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point for the omiiCARE_QA healthcare platform backend.
 *
 * <p>Milestone 2 scope: this is the bootable infrastructure shell. It wires the
 * profile-driven datasource, Flyway migrations, Actuator health endpoints,
 * structured logging, and Micrometer/OpenTelemetry tracing. Healthcare domain
 * modules, REST APIs, and security are intentionally absent until Milestone 3,
 * per the roadmap fence.
 */
@SpringBootApplication
public class OmiiCareQaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OmiiCareQaApplication.class, args);
    }
}
