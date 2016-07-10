Feature: Edit case
  As a logged in user, I want to be able to edit case through full form / mini form.

  Background:
#    Given I login RapidReg with "primero" account
    Given I press "login" button
    And I press menu tab "Cases"
    And I create case "Tom"

  Scenario: As a logged in user, I can edit case through mini form.
    When I click the case
    And I press "edit_case" button
    And I edit the value of "Full Name" from "Tom Justin Clinton" to "Mary Justin Clinton"
    And I edit the value of "Sex" from "<Radio> Male" to "<Radio> Female"
    And I edit the value of "Age" from "10" to "8"
    And I press "save_case" button
    Then I should see the case's "gender_name" is "GIRL"
    And I should see the case's "age" is "8"

  Scenario: As a logged in user, I can edit case through full form.
    When I click the case
    And I press "edit_case" button
    And I switch to full form
    And I scroll to "Basic Identity" form
    And I edit the value of "Full Name" from "Tom Justin Clinton" to "Mary Justin Clinton"
    And I edit the value of "Sex" from "<Radio> Male" to "<Radio> Female"
    And I edit the value of "Age" from "10" to "8"
    And I press "save_case" button
    Then I should see the case's "gender_name" is "GIRL"
    And I should see the case's "age" is "8"

