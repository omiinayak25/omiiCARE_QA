package com.omiicare.qa.automation.bdd;

import org.junit.jupiter.api.Tag;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit Platform suite for the expanded BDD coverage (appointment booking, FHIR Patient read, and
 * patient registration + search).
 *
 * <p>Distinct from {@code RunCucumberTest}: this runner narrows execution to the newer feature
 * tags via a Cucumber tag filter so the two suites can be run independently. Glue resolution is
 * shared and configured in {@code junit-platform.properties} ({@code cucumber.glue =
 * com.omiicare.qa.automation.bdd}).
 *
 * <p>Tagged {@code @bdd} so it is excluded from the default build and only executed with the e2e
 * profile against a running SUT (the step definitions themselves are log-/in-memory based and need
 * no live SUT to compile).
 */
@Suite
@Tag("bdd")
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = "cucumber.filter.tags",
        value = "@appointments or @fhir or @registration")
public class RunBddExpansionTest {}
