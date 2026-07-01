@bdd @ui @registration
Feature: Patient registration and search
  As reception staff
  I want to register a patient and find them again
  So that returning patients are not duplicated

  Scenario: Register then locate a patient by name
    Given a fresh patient registration form
    When I enter the demographics for a synthetic patient
    And I submit the registration form
    Then a registration draft is captured with a non-blank full name
    And the captured patient can be located by their last name

  Scenario: Block submission when mandatory demographics are missing
    Given a fresh patient registration form
    When I submit the registration form without entering demographics
    Then the form reports that mandatory demographics are missing
