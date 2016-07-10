Feature: Basic Identify Form
  As a logged in user, I want to be able to create new cases. So that I should fill in Basic Identify Form.

  Background:
#    Given I login RapidReg with "primero" account
    When I press "login" button
    And I press menu tab "Cases"
    When I press "fab_expand_menu_button" button
    And I press "add_case" button
    And I switch to full form
    And I scroll to "Basic Identity" form

  Scenario: As a logged in user, I create a case by entering something in every field in the basic identity form
    And I fill in the following:
      | Case Status                              | <Select> Open                                        |
      | Full Name                                | Tom Justin Clinton                                   |
      | First Name                               | Tom                                                  |
      | Middle Name                              | Justin                                               |
      | Surname                                  | Clinton                                              |
      | Nickname                                 | Tommy                                                |
      | Other Name                               | Tommy                                                |
      | Name(s) given to child after separation? | <Radio> No                                           |
      | Date of Registration or Interview        | <Date> Today's date                                  |
      | Sex                                      | <Radio> Male                                         |
      | Age                                      | 10                                                   |
      | Date of Birth                            | <Date> Today's date                                  |
      | Is the age estimated?                    | <Checkbox> Yes                                       |
      | Distinguishing Physical Characteristics  | <Text> Really tall, dark hair, brown eyes            |
      | Ration Card Number                       | 121121                                               |
      | ICRC Ref No.                             | 131313                                               |
      | RC ID No.                                | 141414                                               |
      | UNHCR ID                                 | AAA000                                               |
      | UN Number                                | EEE444                                               |
      | Other Agency ID                          | ABC12345                                             |
      | Other Agency Name                        | Test Agency                                          |
      | List of documents carried by the child   | <Text> Driver's License, Passport, Birth Certificate |
      | Current Civil/Marital Status             | <Select> Single                                      |
      | Occupation                               | Farmer                                               |
      | Current Address                          | <Text> 111 Main St, Davidson NC, 28036               |
      | Is this address permanent?               | <Checkbox> No                                        |
      | Current Telephone                        | 336-555-1313                                         |
    And I press "save_case" button
    Then I should see a case with sex "BOY" and age "10"
    When I click the case
    Then I should see following:
      | Full Name | Tom Justin Clinton |
      | Age       | 10                 |
    When I switch to full form
    And I scroll to "Basic Identity" form
    Then I should see following:
      | Case Status                             | Open                                          |
      | Full Name                               | Tom Justin Clinton                            |
      | First Name                              | Tom                                           |
      | Middle Name                             | Justin                                        |
      | Surname                                 | Clinton                                       |
      | Nickname                                | Tommy                                         |
      | Other Name                              | Tommy                                         |
      | Age                                     | 10                                            |
      | Distinguishing Physical Characteristics | Really tall, dark hair, brown eyes            |
      | Ration Card Number                      | 121121                                        |
      | ICRC Ref No.                            | 131313                                        |
      | RC ID No.                               | 141414                                        |
      | UNHCR ID                                | AAA000                                        |
      | UN Number                               | EEE444                                        |
      | Other Agency ID                         | ABC12345                                      |
      | Other Agency Name                       | Test Agency                                   |
      | List of documents carried by the child  | Driver's License, Passport, Birth Certificate |
      | Current Civil/Marital Status            | Single                                        |
      | Occupation                              | Farmer                                        |
      | Current Address                         | 111 Main St, Davidson NC, 28036               |
      | Current Telephone                       | 336-555-1313                                  |