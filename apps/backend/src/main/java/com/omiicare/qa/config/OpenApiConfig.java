package com.omiicare.qa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Central OpenAPI metadata and the Bearer-JWT security scheme for Swagger UI. */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearer-jwt";

    @Bean
    public OpenAPI omiiCareOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("omiiCARE_QA Healthcare Platform API")
                                .description(
                                        "REST and FHIR APIs for the omiiCARE_QA healthcare platform. "
                                                + "All data is synthetic and PHI-safe; no formal certification is claimed.")
                                .version("0.3.0")
                                .contact(new Contact().name("omiiCARE_QA").url(
                                        "https://github.com/omiinayak25/omiiCARE_QA"))
                                .license(new License().name("MIT").url(
                                        "https://github.com/omiinayak25/omiiCARE_QA/blob/main/LICENSE")))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        BEARER_SCHEME,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}
