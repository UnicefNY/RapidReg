Feature: Login test

  Scenario: No login details
    When I press "button_login"
    Then I should see "Enter a valid username!"

  Scenario Outline: Login with incorrect credential and network connected
    When I login RapidReg for the first time with "<Username>" and "<Password>" and "<URL>"
    Then I should see "Sorry, incorrect username or password."
    And I should see "Login failed, first time users please login online to enable your account for offline mode"
    Examples:
      | Username | Password | URL                       |
      | primeroo | qu01n23! | https://52.24.42.32:8443  |
      | primero  | qu01n234 | https://52.24.42.32:8443  |

  Scenario Outline: Login with correct credential and network connected
    When I login RapidReg for the first time with "<Username>" and "<Password>" and "<URL>"
    Then I should see "Login success!"
    And I should see current user is "primero"
    Examples:
      | Username | Password | URL                      |
      | primero  | qu01n23! | https://52.24.42.32:8443 |

  Scenario Outline: Re-login with incorrect credential and network connected
    Given I login RapidReg for the first time with "<Username>" and "<Password>" and "<URL>"
    And I logout
    When I re-login RapidReg with "<Username>" and "<Password>"
    Then I should see "Sorry, incorrect username or password."
    And I should see "Login failed, first time users please login online to enable your account for offline mode"
    Examples:
      | Username | Password | URL                      |
      | primero  | qu01n23! | https://52.24.42.32:8443 |
      | primeroo | qu01n234 |                          |

  Scenario Outline: Re-login with correct credential and network connected
    Given I login RapidReg for the first time with "<Username>" and "<Password>" and "<URL>"
    And I logout
    When I re-login RapidReg with "<Username>" and "<Password>"
    Then I should see "Login success!"
    And I should see current user is "<Username>"
    Examples:
      | Username | Password | URL                      |
      | primero  | qu01n23! | https://52.24.42.32:8443 |
      | primero  | qu01n23! |                          |

  Scenario Outline: Logout
    Given I login RapidReg for the first time with "<Username>" and "<Password>" and "<URL>"
    When I logout
    Then I should see "Logout Successfully"
    Examples:
      | Username | Password | URL                      |
      | primero  | qu01n23  | https://52.24.42.32:8443 |