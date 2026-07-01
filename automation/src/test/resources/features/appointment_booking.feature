@bdd @api @appointments
Feature: Appointment booking
  As reception staff
  I want to book appointments for registered patients
  So that clinicians can see them at a scheduled time

  Background:
    Given an appointment scheduling session is initialised

  Scenario: Book an appointment for an existing patient
    Given an existing patient identified by "PAT-1001"
    When I request an appointment with provider "DR-GREEN" at "2026-08-12T09:30"
    Then the appointment is confirmed with a booking reference

  Scenario: Reject an appointment in the past
    Given an existing patient identified by "PAT-1001"
    When I request an appointment with provider "DR-GREEN" at "2000-01-01T09:30"
    Then the booking is rejected because the slot is in the past

  Scenario: Reject a double-booked slot
    Given an existing patient identified by "PAT-2002"
    And the slot for provider "DR-GREEN" at "2026-08-12T09:30" is already taken
    When I request an appointment with provider "DR-GREEN" at "2026-08-12T09:30"
    Then the booking is rejected because the slot is unavailable
