Feature: Create new case through full form.
  As a logged in user, I want to be able to create new case through full form / mini form.

  Background:
    Given I login RapidReg with "primero" account
    And I press menu tab "Cases"
    When I press "New Case" button

  Scenario: As a logged in user, I create a new case by entering nothing.
    And I press "SAVE" button
    Then I should see "Sorry, please make sure all required information are entered"


  Scenario: As a logged in user, I create a new case by entering something in required fields
    And I scroll to "Basic Identity" form
    And I fill in the following:
      | Sex (Required)                           | <Select> Male                                 |
      | Age (Required)                           | 10                                            |
    And I press "SAVE" button
    Then I should see a new case with sex "Male" and age "10"












