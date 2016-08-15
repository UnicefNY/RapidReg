Feature: Case list and sorting
  As a user, I want to view cases through case list and sort the list by age and registration date.

  Background:
    Given I login RapidReg with "primero" account
#    Given I press "login" button
    And I press menu tab "Cases"

  Scenario: Show/Hide case details
    When I press "add" button
    And I fill in the following:
      | Full Name (Required)  | Tom Justin Clinton |
      | Sex (Required)        | <Radio> Male       |
      | Age (Required)        | 10                 |
      | Is the age estimated? | <Checkbox> Yes     |
    And I press "save" button
    And I press menu button
    And I press menu tab "Cases"
    Then I should see the case's "gender_name" is "BOY"
    And I should see the case's "age" is "10"

    When I press "toggle" button
    Then I should not see "BOY"
    And I should not see "10"






