Feature: Add tracing request
  As a logged in user, I want to be able to add/view/edit/search tracing requests.

  Background:
    #    Given I login RapidReg with "primero" account
    Given I press "login" button
    And I press menu tab "Tracing request"

  Scenario: Create tracing request through mini form / full form.
    When I press "add" button
    And I fill in the following:
      | Inquiry Status                | <Radio> Open                           |
      | Name of inquirer              | Raymond Messiaen                       |
      | Age                           | 42                                     |
      | Nationality                   | <Multiple> Nationality1,Nationality2   |
      | Current Address               | <Text> 111 Main St, Davidson NC, 28036 |
      | Is this a permanent location? | <Checkbox> Yes                         |
    And I press "save" button
    Then I should see following:
      | Inquiry Status                | <Radio> Open                    |
      | Name of inquirer              | Raymond Messiaen                |
      | Age                           | 42                              |
      | Nationality                   | Nationality1,Nationality2       |
      | Current Address               | 111 Main St, Davidson NC, 28036 |
      | Is this a permanent location? | <Checkbox> Yes                  |

    When I press menu button
    And I press menu tab "Tracing request"
    Then I should see the tracing request "age" is "42"
    And I should see the tracing request "registration_date" is "Today's date"

    When I press "add" button
    And I switch to full form
    And I scroll to "Inquirer" form
    And I fill in the following:
      | Inquiry Status                                       | <Radio> Open                             |
      | Name of inquirer                                     | Susie Thatcher                           |
      | Nickname of inquirer                                 | Susie                                    |
      | Age                                                  | 36                                       |
      | Language                                             | <Multiple> Language1,Language2           |
      | Religion                                             | <Multiple> Religion1,Religion2           |
      | Ethnicity                                            | <Select> Ethnicity1                      |
      | Sub Ethnicity 1                                      | <Select> Ethnicity1                      |
      | Sub Ethnicity 2                                      | <Select> Ethnicity2                      |
      | Nationality                                          | <Multiple> Nationality1,Nationality2     |
      | Additional details / comments                        | <Text> Miss family very much.            |
      | Current Address                                      | <Text> 111 Main St, Davidson NC, 28036   |
      | Is this a permanent location?                        | <Checkbox> Yes                           |
      | Telephone                                            | 0535-7768597                             |
      | What was the main cause of separation?               | <Select> Conflict                        |
      | Did the separation occur in relation to evacuation?  | <Checkbox> No                            |
      | Circumstances of Separation (please provide details) | <Text> It is ok now.                     |
      | Separation Address (Place)                           | <Text> In the middle part of the contry. |
      | Last Address                                         | <Text> Capital                           |
      | Last Landmark                                        | A huge mountain                          |
#      | Last Telephone                                       | 010-88672342                             |
      | Additional info that could help in tracing?          | <Text> The child is very tall.           |
    And I press "save" button
    And I scroll to "Inquirer" form
    Then I should see following:
      | Inquiry Status                                       | <Radio> Open                      |
      | Name of inquirer                                     | Susie Thatcher                    |
      | Nickname of inquirer                                 | Susie                             |
      | Age                                                  | 36                                |
      | Language                                             | Language1,Language2               |
      | Religion                                             | Religion1,Religion2               |
      | Ethnicity                                            | Ethnicity1                        |
      | Sub Ethnicity 1                                      | Ethnicity1                        |
      | Sub Ethnicity 2                                      | Ethnicity2                        |
      | Nationality                                          | Nationality1,Nationality2         |
      | Additional details / comments                        | Miss family very much.            |
      | Current Address                                      | 111 Main St, Davidson NC, 28036   |
      | Is this a permanent location?                        | <Checkbox> Yes                    |
      | Telephone                                            | 0535-7768597                      |
      | What was the main cause of separation?              | Conflict                          |
      | Circumstances of Separation (please provide details) | It is ok now.                     |
      | Separation Address (Place)                           | In the middle part of the contry. |
      | Last Address                                         | Capital                           |
#      | Last Landmark                                        | A huge mountain                   |
#      | Last Telephone                                       | 010-88672342                      |
      | Additional info that could help in tracing?          | The child is very tall.           |

    When I press menu button
    And I press menu tab "Tracing request"
    And I press "search" button
    And I search for:
      | Name | Susie |
    And I press "done" button
    Then I should see a tracing request with age "36"

    When I press "search_bar" button
    And I clear up above "1" search conditions
    And I search for:
      | Age From | 40 |
      | To       | 50 |
    And I press "done" button
    Then I should see a tracing request with age "42"
