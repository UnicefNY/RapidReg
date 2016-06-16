Feature: Basic Identify Form
  As an social, I want to be able to create a case through mobile app.

  Background:
    Given I login RapidReg with "primero" account
    And I press menu tab "Cases"
    When I press "New Case" button
    And I select "Basic Identify" form

  Scenario: As a logged in user, I create a case by entering something in every field in the basic identity form
    And the "Is the age estimated?" tick box should have a "Yes" label
    And the "Is this address permanent?" tick box should not have a label
    And I fill in the following:
      | Case Status                              | <Select> Open                      |
      | Full Name                                | Tom Justin Clinton                 |
      | First Name                               | Tom                                |
      | Middle Name                              | Justin                             |
      | Surname                                  | Clinton                            |
      | Nickname                                 | Tommy                              |
      | Other Name                               | Tommy                              |
      | Name(s) given to child after separation? | <Radio> No                         |
      | Date of Registration or Interview        | today's date                       |
      | Sex                                      | <Select> Male                      |
      | Age                                      | 10                                 |
      | Date of Birth                            | 17-Jan-2006                        |
      | Is the age estimated?                    | <Tickbox>                          |
      | Distinguishing Physical Characteristics  | Really tall, dark hair, brown eyes |
      | Ration Card Number                       | 121121                             |
      | ICRC Ref No.                             | 131313                             |
      | RC ID No.                                | 141414                             |
      | UNHCR ID                                 | AAA000                             |
      | UN Number                                | EEE444                             |
      | Other Agency ID                          | ABC12345                           |
      | Other Agency Name                        | Test Agency                        |
      | List of documents carried by the child | Driver's License, Passport, Birth Certificate |
#      | Current Civil/Marital Status             | <Select> Married/Cohabitating      |
      | Occupation                               | Farmer                             |
      | Current Address                          | 111 Main St, Davidson NC, 28036    |
      | Landmark                                 | Old Oak Tree                       |
      | Current Location                         | <Choose>A Location Country         |
      | Is this address permanent?               | <Tickbox>                          |
      | Current Telephone                        | 336-555-1313                       |
    And I press "Save" button
    Then I should see a success message for new Case
    And I should see a value for "Case ID" on the show page
    And I should see values on the page for the following:
      | Case Status                              | <Select> Open                      |
      | Full Name                                | Tom Justin Clinton                 |
      | First Name                               | Tom                                |
      | Middle Name                              | Justin                             |
      | Surname                                  | Clinton                            |
      | Nickname                                 | Tommy                              |
      | Other Name                               | Tommy                              |
      | Name(s) given to child after separation? | <Radio> No                         |
      | Date of Registration or Interview        | today's date                       |
      | Sex                                      | <Select> Male                      |
      | Age                                      | 10                                 |
      | Date of Birth                            | 17-Jan-2006                        |
      | Current Telephone                        | 333-444-666                        |

