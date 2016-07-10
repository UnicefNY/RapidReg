Feature: Login test

  Scenario: No login details
    When I press "login" button
    Then I should see "Enter a valid username!"

  Scenario Outline: Login with incorrect credential and network connected
    When I login RapidReg for the first time with "<Username>" and "<Password>" and "<URL>"
    Then I should see "Sorry, incorrect username or password."
    And I should see "Sorry, your must be connected to the internet for the first-time login."
    Examples:
      | Username | Password | URL                       |
      | primeroo | qu01n23! | https://52.24.42.32:8443  |
      | primero  | qu01n234 | https://52.24.42.32:8443  |

  Scenario: Login with correct credential and network connected
    When I login RapidReg for the first time with "primero" and "qu01n23!" and "https://52.24.42.32:8443"
    Then I should see "Login success!"
    And I should see current user is "primero"
    And the organization is "agency-unicef"

  Scenario: Re-login with incorrect credential and network connected
    Given I login RapidReg for the first time with "primero" and "qu01n23!" and "https://52.24.42.32:8443"
    And I logout
    When I re-login RapidReg with "primeroo" and "qu01n23!"
    Then I should see "Sorry, incorrect username or password."
    And I should see "Sorry, your must be connected to the internet for the first-time login."

  Scenario: Re-login with correct credential and network connected
    Given I login RapidReg for the first time with "primero" and "qu01n23!" and "https://52.24.42.32:8443"
    And I logout
    When I re-login RapidReg with "primero" and "qu01n23!"
    Then I should see "Login success!"
    And I should see current user is "primero"
    And the organization is "agency-unicef"


  Scenario: Logout
    Given I login RapidReg for the first time with "primero" and "qu01n23!" and "https://52.24.42.32:8443"
    When I logout
    Then I should see "Logout Successfully"
