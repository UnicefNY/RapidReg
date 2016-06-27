Feature: Search cases
  As a worker, I want to search cases by specific fields, and search result should be displayed in a list.
  Fields include: id, name, age, current caregiver, registration date.

  Background:
#    Given I login RapidReg with "primero" account
    Given I press "button_login" button
    And I press menu tab "Cases"


  Scenario: Search fields
    When I press "search" button
    And I search for:
      | ID                | 1234567 |
      | Name              | Lee     |
      | Age From          | 1       |
      | To                | 2       |
      | Current Caregiver | Jane    |
#      | Registration date | <Date> 6/21/2015 |
    And I press "done" button
    And I press "search_bar" button
    Then I should see "1234567"
    And I should see "Lee"
    And I should see "1"
    And I should see "2"
    And I should see "Jane"
#    And I should see "6/21/2016"


  Scenario: No search result
    When I press "search" button
    And I search for:
      | ID | XXXXXXX |
    Then I should see "Sorry, no results found for this search criteria."


  Scenario: Search
    And I create case "Tom"
    And I create case "Jim"
    And I create case "Jack"
    When I press "search" button
    And I search for:
      | Name | Tom |
    And I press "done" button
    Then I should see a case with sex "Male" and age "10"

    And I press "search_bar" button
    And I clear up existing search info
    And I search for:
      | Age From | 7 |
      | To       | 9 |
    And I press "done" button
    Then I should see a case with sex "Male" and age "8"

    And I press "search_bar" button
    And I clear up existing search info
    And I search for:
      | Registration date | <Date> 6/25/2016 |
    And I press "done" button
    Then I should see a case with sex "Male" and age "6"

    And I press "search_bar" button
    And I clear up existing search info
    And I press "done" button
    Then I should see a case with sex "Male" and age "10"
    And I should see a case with sex "Male" and age "8"
    And I should see a case with sex "Male" and age "6"


