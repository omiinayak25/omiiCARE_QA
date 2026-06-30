package com.omiicare.qa.automation.bdd;

import org.junit.jupiter.api.Tag;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit Platform suite that runs the Cucumber BDD feature files. Tagged {@code bdd} (via the
 * feature {@code @bdd} tags and the surefire excludedGroups) so it is skipped in the default build
 * and executed with {@code -Pe2e} against a running SUT. Glue is configured in {@code
 * junit-platform.properties}.
 */
@Suite
@Tag("bdd")
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class RunCucumberTest {}
