package com.omiicare.qa.automation.ui.openmrs;

import static org.assertj.core.api.Assertions.assertThat;

import com.microsoft.playwright.Page;
import com.omiicare.qa.automation.core.config.FrameworkConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Playwright UI end-to-end test driving the OpenMRS Reference Application login and home navigation
 * via the page-object layer ({@link OpenmrsLoginPage}, {@link OpenmrsHomePage}, {@link
 * OpenmrsRegistrationPage}, {@link OpenmrsFindPatientPage}).
 *
 * <p>Tagged {@code ui-e2e}: it requires a reachable OpenMRS instance and a real browser, so it is
 * excluded from the default {@code mvn test} build. Distinct from the existing {@code
 * LoginUiE2ETest} (which targets the React SUT), this exercises the OpenMRS RefApp specifically.
 *
 * <p>All endpoints and credentials are resolved through {@link FrameworkConfig}; nothing is
 * hardcoded.
 */
@Tag("ui-e2e")
class OpenmrsLoginNavigateUiE2ETest {

    private static final Logger LOG =
            LoggerFactory.getLogger(OpenmrsLoginNavigateUiE2ETest.class);

    private PlaywrightFactory factory;
    private Page page;
    private FrameworkConfig config;

    @BeforeEach
    void setUp() {
        config = FrameworkConfig.get();
        factory = new PlaywrightFactory(config);
        page = factory.createPage();
    }

    @AfterEach
    void tearDown() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    @DisplayName("Logs into OpenMRS RefApp and reaches the home dashboard tiles")
    void logsInAndReachesHomeDashboard() {
        String baseUrl = factory.baseUrl();
        String username = config.get("omii.openmrs.ui.username", "admin");
        String password = config.get("omii.openmrs.ui.password", "Admin123");
        String locationId = config.get("omii.openmrs.ui.loginLocationId", "");

        OpenmrsHomePage home =
                new OpenmrsLoginPage(page)
                        .loginAs(baseUrl, username, password, locationId);

        assertThat(home.isRegisterPatientTileVisible() || home.isFindPatientTileVisible())
                .as("at least one recognizable home tile should be visible after login")
                .isTrue();
    }

    @Test
    @DisplayName("Navigates from home into the Find Patient search screen")
    void navigatesToFindPatient() {
        String baseUrl = factory.baseUrl();
        String username = config.get("omii.openmrs.ui.username", "admin");
        String password = config.get("omii.openmrs.ui.password", "Admin123");
        String locationId = config.get("omii.openmrs.ui.loginLocationId", "");

        OpenmrsHomePage home =
                new OpenmrsLoginPage(page)
                        .loginAs(baseUrl, username, password, locationId);

        OpenmrsFindPatientPage find = home.openFindPatient();
        assertThat(find.isLoaded())
                .as("the patient search input should be visible")
                .isTrue();
    }
}
