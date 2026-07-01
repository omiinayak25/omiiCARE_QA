package com.omiicare.qa.automation.core.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pure-logic unit test for {@link AdapterRegistry} and the specialized adapters. Exercises only
 * in-memory registry resolution and URL composition — no SUT, browser, network, or DB — so it runs
 * in the default build and must always pass.
 */
class AdapterRegistryTest {

    @Test
    @DisplayName("registry resolves each framework-known system to its specialized adapter type")
    void resolvesSpecializedAdapters() {
        FrameworkConfig config = FrameworkConfig.get();

        assertThat(AdapterRegistry.resolve(TargetSystem.OPENMRS, config))
                .isInstanceOf(OpenMrsResourceAdapter.class);
        assertThat(AdapterRegistry.resolve(TargetSystem.OPENEMR, config))
                .isInstanceOf(OpenEmrResourceAdapter.class);
        assertThat(AdapterRegistry.resolve(TargetSystem.HAPI_FHIR, config))
                .isInstanceOf(HapiFhirResourceAdapter.class);
        assertThat(AdapterRegistry.resolve(TargetSystem.LOCAL_OMIICARE, config))
                .isInstanceOf(OmiiCareResourceAdapter.class);
    }

    @Test
    @DisplayName("resolved adapter reports the requested target system")
    void adapterReportsRequestedSystem() {
        FrameworkConfig config = FrameworkConfig.get();
        for (TargetSystem system : TargetSystem.values()) {
            ResourceAdapter adapter = AdapterRegistry.resolve(system, config);
            assertThat(adapter.system()).isEqualTo(system);
            assertThat(adapter.baseUri()).isNotBlank();
        }
    }

    @Test
    @DisplayName("systems without a specialized adapter fall back to the generic HTTP adapter")
    void fallsBackToGenericAdapter() {
        FrameworkConfig config = FrameworkConfig.get();

        assertThat(AdapterRegistry.hasSpecializedAdapter(TargetSystem.DUMMYJSON)).isFalse();
        assertThat(AdapterRegistry.supplierFor(TargetSystem.DUMMYJSON)).isEmpty();

        ResourceAdapter adapter = AdapterRegistry.resolve(TargetSystem.DUMMYJSON, config);
        assertThat(adapter).isExactlyInstanceOf(HttpResourceAdapter.class);
    }

    @Test
    @DisplayName("hasSpecializedAdapter and supplierFor agree for known systems")
    void registryFlagsAreConsistent() {
        assertThat(AdapterRegistry.hasSpecializedAdapter(TargetSystem.OPENMRS)).isTrue();
        assertThat(AdapterRegistry.supplierFor(TargetSystem.OPENMRS)).isPresent();
        assertThat(AdapterRegistry.hasSpecializedAdapter(TargetSystem.HAPI_FHIR)).isTrue();
        assertThat(AdapterRegistry.supplierFor(TargetSystem.HAPI_FHIR)).isPresent();
    }

    @Test
    @DisplayName("OpenMRS adapter derives the FHIR root and metadata URL from the REST root")
    void openMrsDerivesFhirPaths() {
        OpenMrsResourceAdapter adapter =
                new OpenMrsResourceAdapter(TargetSystem.OPENMRS, FrameworkConfig.get());

        // Default base is https://demo.openmrs.org/openmrs/ws/rest/v1
        assertThat(adapter.restRoot()).endsWith("/ws/rest/v1");
        assertThat(adapter.fhirRoot()).endsWith("/ws/fhir2/R4");
        assertThat(adapter.fhirRoot()).doesNotContain("/ws/rest/v1");
        assertThat(adapter.fhirMetadataUrl()).endsWith("/ws/fhir2/R4/metadata");
        assertThat(adapter.rest("/patient")).endsWith("/ws/rest/v1/patient");
        assertThat(adapter.fhir("/Patient")).endsWith("/ws/fhir2/R4/Patient");
    }

    @Test
    @DisplayName("OpenEMR adapter derives FHIR root and OAuth token endpoint")
    void openEmrDerivesPaths() {
        OpenEmrResourceAdapter adapter =
                new OpenEmrResourceAdapter(TargetSystem.OPENEMR, FrameworkConfig.get());

        // Default base is http://localhost:8300/apis/default/api
        assertThat(adapter.restRoot()).endsWith("/apis/default/api");
        assertThat(adapter.fhirRoot()).endsWith("/apis/default/fhir");
        assertThat(adapter.tokenUrl()).endsWith("/oauth2/default/token");
        assertThat(adapter.tokenUrl()).doesNotContain("/apis/default/api");
    }

    @Test
    @DisplayName("HAPI adapter treats the configured base as the FHIR root")
    void hapiUsesBaseAsFhirRoot() {
        HapiFhirResourceAdapter adapter =
                new HapiFhirResourceAdapter(TargetSystem.HAPI_FHIR, FrameworkConfig.get());

        assertThat(adapter.fhirRoot()).isEqualTo(adapter.baseUri());
        assertThat(adapter.fhirMetadataUrl()).endsWith("/metadata");
        assertThat(adapter.resource("Patient", "123")).endsWith("/Patient/123");
    }

    @Test
    @DisplayName("omiiCARE adapter composes versioned API and auth paths")
    void omiiCareComposesPaths() {
        OmiiCareResourceAdapter adapter =
                new OmiiCareResourceAdapter(TargetSystem.LOCAL_OMIICARE, FrameworkConfig.get());

        assertThat(adapter.apiRoot()).isEqualTo(adapter.baseUri());
        assertThat(adapter.api("/patients")).endsWith("/patients");
        assertThat(adapter.authUrl()).endsWith("/auth/login");
    }
}
