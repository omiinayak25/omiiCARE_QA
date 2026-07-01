@bdd @api @fhir
Feature: FHIR Patient read
  As an integration engineer
  I want to read Patient resources from the FHIR R4 endpoint
  So that downstream systems receive standards-compliant patient data

  Background:
    Given the FHIR R4 base path is "/ws/fhir2/R4"

  Scenario: Read a Patient resource by id
    Given a known FHIR Patient id "1a2b3c"
    When I read the Patient resource
    Then the response resourceType is "Patient"
    And the Patient resource has a logical id

  Scenario: Confirm the server advertises FHIR R4
    When I read the FHIR CapabilityStatement
    Then the advertised fhirVersion is "4.0.1"
