Feature: Case list and sorting
  As a user, I want to view cases through case list and sort the list by age and registration date.

  Scenario: Show/Hide case details
#    Given I login RapidReg with "primero" account
    Given I press "login" button
    And I press menu tab "Cases"
    When I create case "Tom"
    Then I should see the case's "gender_name" is "BOY"
    And I should see the case's "age" is "10"
    And I should see the case's "registration_date" is "Today's date"
    When I press "toggle" button
    Then I should not see "BOY"
    And I should not see "10"
    And I should not see "Today's date"
    When I press "toggle" button
    Then I should see the case's "gender_name" is "BOY"
    And I should see the case's "age" is "10"
    And I should see the case's "registration_date" is "Today's date"


  Scenario: Order by Age/Registration date
#    Given I login RapidReg with "primero" account
    Given I press "login" button
    And I press menu tab "Cases"
    When I create case "Tom"
    And I create case "Lily"
    Then I should see the first case is a "GIRL"
    When I order by "Age descending age"
    Then I should see the first case is a "BOY"
    When I order by "Registration date descending order"
    Then I should see the first case is a "GIRL"





