Feature: Nested Family Details Form
  As a logged in user, I want to be able to add nested form.

  Background:
#    Given I login RapidReg with "primero" account
    Given I press "login" button
    And I press menu tab "Cases"
    And I press "fab_expand_menu_button" button
    And I press "add_case" button
    And I switch to full form

  Scenario: As a logged in user, I add a nested family detail form by entering something in fields.
    When I scroll to "Family Details" form
    Then I should see a button named "Add Family Details"
    When I press the button named "Add Family Details"
    And I fill in the following:
      | Name                                                   | Jack             |
      | How are they related to the child?                     | <Select> Father  |
      | Is this person the caregiver?                          | <Checkbox> Yes   |
      | Did the child live with this person before separation? | <Radio> Yes      |
      | Is the child in contact with this person?              | <Radio> No       |
      | Is the child separated from this person?               | <Radio> Yes      |
      | List any agency identifiers as a comma separated list  | Red Cross, Chain |
      | Nickname                                               | Jpa              |
      | Is this family member alive?                           | <Select> Unknown |
      | If dead, please provide details                        | <Text> Not sure  |
      | Age                                                    | 39               |
    And I press "save" button
    And I scroll to "Family Details" form
    Then I should see following:
      | Name                                                   | Jack             |
      | How are they related to the child?                     | Father           |
      | Is this person the caregiver?                          | <Checkbox> Yes   |
      | Did the child live with this person before separation? | <Radio> Yes      |
      | Is the child in contact with this person?              | <Radio> No       |
      | Is the child separated from this person?               | <Radio> Yes      |
      | List any agency identifiers as a comma separated list  | Red Cross, Chain |
      | Nickname                                               | Jpa              |
      | Is this family member alive?                           | Unknown          |
      | If dead, please provide details                        | Not sure         |
      | Age                                                    | 39               |