package com.omiicare.qa.automation.parallel;

import static org.assertj.core.api.Assertions.assertThat;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Self-validating TestNG example used to exercise parallel execution wiring (see
 * {@code testng-parallel.xml}, {@code parallel="methods"}, {@code thread-count="4"}).
 *
 * <p>Every method here is pure logic: it validates {@link FrameworkConfig}, {@link TargetSystem},
 * and {@link ParallelDataGenerator} without contacting any SUT, browser, network, or database.
 * Because there is no external dependency, these tests are deterministic and safe to run in
 * parallel and on every default build. They demonstrate the thread-safety contract that real
 * SUT-backed suites must honour.
 */
public class ParallelExampleTest {

    private static final Logger LOG = LoggerFactory.getLogger(ParallelExampleTest.class);

    /** Frees the thread-local generator after each method so pooled threads stay clean. */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        ParallelDataGenerator.clear();
    }

    @DataProvider(name = "iterations", parallel = true)
    public Object[][] iterations() {
        return new Object[][] {{1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}};
    }

    /** Config defaults must resolve without any environment overrides present. */
    @Test
    public void frameworkConfigResolvesDefaults() {
        String value = FrameworkConfig.get().get("omii.parallel.example.missing", "fallback");
        assertThat(value).isEqualTo("fallback");
        assertThat(FrameworkConfig.get().environment()).isNotBlank();
    }

    /** The adapter base URI must come from the enum default when unconfigured. */
    @Test
    public void targetSystemBaseUriResolves() {
        String uri = FrameworkConfig.get().baseUri(TargetSystem.OPENMRS);
        assertThat(uri).isNotBlank().startsWith("http");
        assertThat(TargetSystem.OPENMRS.baseUriProperty())
                .isEqualTo("omii.adapter.openmrs.baseUri");
    }

    /**
     * Runs across the parallel data provider so the generator is hammered from multiple threads at
     * once; each invocation must produce well-formed, non-colliding-format data.
     */
    @Test(dataProvider = "iterations")
    public void generatorProducesValidDataUnderParallelism(int iteration) {
        String name = ParallelDataGenerator.fullName();
        String email = ParallelDataGenerator.email();
        String mrn = ParallelDataGenerator.patientIdentifier();

        LOG.debug(
                "iteration={} thread={} name={} mrn={}",
                iteration,
                Thread.currentThread().getName(),
                name,
                mrn);

        assertThat(name).isNotBlank();
        assertThat(email).contains("@");
        assertThat(mrn).matches("MRN-\\d{7}");
    }
}
