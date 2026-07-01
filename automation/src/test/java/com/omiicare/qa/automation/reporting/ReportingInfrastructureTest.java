package com.omiicare.qa.automation.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import java.io.File;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pure-logic verification of the reporting infrastructure. No SUT, browser, network, or DB is
 * involved: the test exercises the Extent singleton and the Allure environment writer against the
 * local filesystem only, so it is safe to run in the default {@code mvn test} build (untagged).
 */
class ReportingInfrastructureTest {

    @Test
    @DisplayName("ExtentReportManager initializes as a singleton and flushes an HTML report file")
    void extentManagerInitializesAndWritesReport() {
        ExtentReportManager manager = ExtentReportManager.getInstance();
        assertThat(manager).isNotNull();
        assertThat(ExtentReportManager.getInstance()).isSameAs(manager);
        assertThat(manager.getExtent()).isNotNull();

        ExtentTest test =
                manager.createTest("reporting-selftest", "verifies report file creation");
        test.log(Status.PASS, "infrastructure self-test");
        assertThat(manager.getCurrentTest()).isSameAs(test);

        manager.flush();

        File report = manager.getReportFile();
        assertThat(report).isNotNull();
        assertThat(report)
                .as("Extent HTML report should exist after flush")
                .exists()
                .isFile();
        assertThat(report.length()).isGreaterThan(0L);
        assertThat(report.getName()).endsWith(".html");

        manager.clearCurrentTest();
        assertThat(manager.getCurrentTest()).isNull();
    }

    @Test
    @DisplayName("AllureEnvironmentWriter writes a non-empty environment.properties file")
    void allureEnvironmentWriterProducesFile() {
        Map<String, String> env = AllureEnvironmentWriter.defaultEnvironment();
        assertThat(env).containsKey("Environment").containsKey("Java.Version");

        File written = AllureEnvironmentWriter.write(env);
        assertThat(written)
                .as("environment.properties should be written")
                .exists()
                .isFile();
        assertThat(written.getName()).isEqualTo("environment.properties");
        assertThat(written.length()).isGreaterThan(0L);
    }
}
