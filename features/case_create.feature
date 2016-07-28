Feature: Create new case
  As a logged in user, I want to be able to create new case through full form / mini form.

  Background:
#    Given I login RapidReg with "primero" account
    Given I press "login" button
    And I press menu tab "Cases"


  Scenario: As a logged in user, I create a new case through mini form.
    When I press "add" button
    And I fill in the following:
      | Full Name             | Tom Justin Clinton |
      | Sex                   | <Radio> Male       |
      | Age                   | 10                 |
      | Is the age estimated? | <Checkbox> Yes     |
    And I press "save" button
    Then I should see following:
      | Full Name             | Tom Justin Clinton |
      | Sex                   | <Radio> Male       |
      | Age                   | 10                 |
      | Is the age estimated? | <Checkbox> Yes     |

  Scenario: As a logged in user, I create a new case through full form.
    When I press "add" button
    And I switch to full form
    And I scroll to "Basic Identity" form
    And I fill in the following:
      | Sex | <Radio> Male |
      | Age | 10           |
    And I press "save" button
    Then I should see following:
      | Sex | <Radio> Male |
      | Age | 10           |

  Scenario: Leave the creating page without saving.
    When I press "add" button
    And I click to open navigation drawer
    And I press menu tab "Cases"
    Then I should see "Are you sure to quit without saving?"











