Feature: Case list and sorting
  As a user, I want to view cases through case list and sort the list by age and registration date.

  Scenario: As a logged in user, I add new cases and view them through case list.
    Given I login RapidReg with "primero" account
    And I press menu tab "Cases"
    When I press "fab_expand_menu_button" button
    And I press "add_case" button
    And I scroll to "Basic Identity" form
    And I fill in the following:
      | Sex (Required)                           | <Select> Male                                 |
      | Age (Required)                           | 10                                            |
    And I press "save_case" button
