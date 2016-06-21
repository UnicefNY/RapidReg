Feature: Create new case through full form.
  As a logged in user, I want to be able to create new case through full form / mini form.

  Background:
    Given I re-login RapidReg with "primero" and "qu01n23!"
    And I press menu tab "Cases"
    When I press "New Case" button

  Scenario: As a logged in user, I create a new case by entering nothing.
    And I press "SAVE" button
    Then I should see "Sorry, please make sure all required information are entered"















