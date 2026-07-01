package com.omiicare.qa.automation.bdd;

import static org.assertj.core.api.Assertions.assertThat;

import com.omiicare.qa.automation.core.generators.PatientFactory;
import com.omiicare.qa.automation.core.generators.SyntheticPatient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber step definitions for registering a patient and searching for them afterwards.
 *
 * <p>The form interaction is modelled in-memory so the glue compiles and runs without a browser or
 * SUT: {@link #form} captures entered demographics and {@link #registry} acts as a stand-in patient
 * index. In a wired environment these steps would drive the Selenium/Playwright registration page
 * and the search results grid while keeping the same Gherkin phrasing. Tagged {@code @bdd} in the
 * feature so excluded from the default build.
 */
public class PatientRegistrationSearchSteps {

    private static final Logger LOG = LoggerFactory.getLogger(PatientRegistrationSearchSteps.class);

    private final PatientFactory patientFactory = new PatientFactory();
    private final List<SyntheticPatient> registry = new ArrayList<>();

    private SyntheticPatient form;
    private SyntheticPatient submitted;
    private boolean submissionBlocked;
    private String missingFieldsMessage;

    @Given("a fresh patient registration form")
    public void freshForm() {
        form = null;
        submitted = null;
        submissionBlocked = false;
        missingFieldsMessage = null;
        LOG.info("Opened a fresh patient registration form");
    }

    @When("I enter the demographics for a synthetic patient")
    public void enterDemographics() {
        form = patientFactory.newPatient();
        LOG.info("Entered demographics for {} {}", form.firstName(), form.lastName());
    }

    @When("I submit the registration form")
    public void submitForm() {
        if (form == null
                || isBlank(form.firstName())
                || isBlank(form.lastName())
                || isBlank(form.dateOfBirth())) {
            submissionBlocked = true;
            missingFieldsMessage = "Mandatory demographics are missing";
            LOG.info("Registration blocked: {}", missingFieldsMessage);
            return;
        }
        submitted = form;
        registry.add(form);
        LOG.info("Registered patient {} {}", form.firstName(), form.lastName());
    }

    @When("I submit the registration form without entering demographics")
    public void submitWithoutDemographics() {
        submitForm();
    }

    @Then("a registration draft is captured with a non-blank full name")
    public void draftCaptured() {
        assertThat(submissionBlocked).as("submission accepted").isFalse();
        assertThat(submitted).as("submitted patient").isNotNull();
        String fullName = (submitted.firstName() + " " + submitted.lastName()).trim();
        assertThat(fullName).isNotBlank();
    }

    @Then("the captured patient can be located by their last name")
    public void locatableByLastName() {
        assertThat(submitted).as("submitted patient").isNotNull();
        List<SyntheticPatient> matches = searchByLastName(submitted.lastName());
        assertThat(matches).as("search results for last name").isNotEmpty();
        assertThat(matches).contains(submitted);
    }

    @Then("the form reports that mandatory demographics are missing")
    public void formReportsMissing() {
        assertThat(submissionBlocked).as("submission blocked").isTrue();
        assertThat(missingFieldsMessage).contains("Mandatory demographics");
    }

    private List<SyntheticPatient> searchByLastName(String lastName) {
        String needle = lastName.toLowerCase(Locale.ROOT);
        List<SyntheticPatient> matches = new ArrayList<>();
        for (SyntheticPatient candidate : registry) {
            if (candidate.lastName() != null
                    && candidate.lastName().toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(candidate);
            }
        }
        return matches;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
