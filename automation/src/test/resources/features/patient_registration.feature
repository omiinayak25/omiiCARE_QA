@bdd @api
Feature: Patient registration
  As reception staff
  I want to register patients
  So that they can be scheduled and treated

  Background:
    Given the omiiCARE API is available
    And I am authenticated as the demo administrator

  Scenario: Register a new patient
    When I register a synthetic patient
    Then the patient is created with a medical record number

  Scenario: Reject a patient with a missing last name
    When I register a patient without a last name
    Then the API responds with a validation error
