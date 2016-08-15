Feature: Create new case
  As a logged in user, I want to be able to create new case through full form / mini form.

  Background:
    Given I login RapidReg with "primero" account
#    Given I press "login" button
    And I press menu tab "Cases"


  Scenario: As a logged in user, I create a new case through mini form.
    When I press "add" button
    And I fill in the following:
      | Full Name (Required)  | Tom Justin Clinton |
      | Sex (Required)        | <Radio> Male       |
      | Age (Required)        | 10                 |
      | Is the age estimated? | <Checkbox> Yes     |
    And I press "save" button
    Then I should see following:
      | Full Name (Required)  | Tom Justin Clinton |
      | Sex (Required)        | <Radio> Male       |
      | Age (Required)        | 10                 |
      | Is the age estimated? | <Checkbox> Yes     |

    When I press "edit" button
    And I edit the value of "Full Name (Required)" from "Tom Justin Clinton" to "Mary Justin Clinton"
    And I edit the value of "Sex (Required)" from "<Radio> Male" to "<Radio> Female"
    And I edit the value of "Age (Required)" from "10" to "8"
    And I press "save" button
    Then I should see following:
      | Full Name (Required)  | Mary Justin Clinton |
      | Sex (Required)        | <Radio> Female      |
      | Age (Required)        | 8                   |
      | Is the age estimated? | <Checkbox> Yes      |


  Scenario: As a logged in user, I create a new case through full form.
    When I press "add" button
    And I switch to full form
    And I scroll to "Basic Identity" form
    And I fill in the following:
      | Full Name (Required) | Tom Justin Clinton |
      | Sex (Required)       | <Radio> Male       |
      | Age (Required)       | 10                 |
    And I press "save" button
    And I scroll to "Basic Identity" form
    Then I should see following:
      | Full Name (Required) | Tom Justin Clinton |
      | Sex (Required)       | <Radio> Male       |
      | Age (Required)       | 10                 |

    When I press "edit" button
    And I scroll to "Basic Identity" form
    And I edit the value of "Full Name (Required)" from "Tom Justin Clinton" to "Mary Justin Clinton"
    And I edit the value of "Sex (Required)" from "<Radio> Male" to "<Radio> Female"
    And I edit the value of "Age (Required)" from "10" to "8"
    And I press "save" button
    And I scroll to "Basic Identity" form
    Then I should see following:
      | Full Name (Required) | Mary Justin Clinton |
      | Sex (Required)       | <Radio> Female      |
      | Age (Required)       | 8                   |


  Scenario: Leave the creating page without saving.
    When I press "add" button
    And I click to open navigation drawer
    And I press menu tab "Cases"
    Then I should see "Are you sure to quit without saving?"











