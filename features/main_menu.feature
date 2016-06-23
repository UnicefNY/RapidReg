Feature: Main menu
  As a user, I want to show/hide main menu, so that I can switch among tabs and proceed with necessary operations.

  Scenario: Show/Hide main menu
    Given I login RapidReg with "primero" account
    Then I should see current user is "primero"
    When I swipe to hide the main menu
    Then I should see page title is "Cases"
    When I swipe to show the main menu
    Then I should see current user is "primero"
    When I press menu tab "Cases"
    Then I should see page title is "Cases"
    When I press menu button
    Then I should see current user is "primero"


