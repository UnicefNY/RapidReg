Feature: Search cases
  As a worker, I want to search cases by specific fields, and search result should be displayed in a list.
  Fields include: id, name, age, current caregiver, registration date.

  Background:
    Given I login RapidReg with "primero" account
    And I press menu tab "Cases"

  Scenario: Search fields
    When I press "Search" button
    Then I shoull see following search fields:
      | ID                |
      | Name              |
      | Age From          |
      | To                |
      | Current Caregiver |
      | Registration date |


  Scenario: No search result
    When I press "Search" button
    And I search for:
      | ID | XXXXXXX |
    Then I should see "Sorry, no results found for this search criteria."

  Scenario: View search fields
    When I press "Search" button
    And I search for:
      | ID                | 1234567   |
      | Name              | Lee       |
      | Age From          | 1         |
      | To                | 2         |
      | Current Caregiver | Jane      |
      | Registration date | 6/21/2015 |
    And I drop down the search fields
    Then I should see following information:
      | 1234567   |
      | Lee       |
      | 1         |
      | 2         |
      | Jane      |
      | 6/21/2015 |

  Scenario: Search
    And I create case "Tom"
    And I create case "Jim"
    And I create case "Jack"
    When I press "Search" button
    And I search for:
      | Name | Tom |
    Then I should see a case with sex "Male" and age "10"
    When I press "Search" button
    And I search for:
      | Age From | 7 |
      | To       | 9 |
    Then I should see a case with sex "Male" and age "8"
    When I press "Search" button
    And I search for:
      | Registration date | 6/25/2016 |
    Then I should see a case with sex "Male" and age "6"
    When I press "Search" button
    And I search for nothing
    Then I should see a case with sex "Male" and age "10"
    And I should see a case with sex "Male" and age "8"
    And I should see a case with sex "Male" and age "6"


