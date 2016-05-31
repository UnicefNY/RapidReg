Feature: Login test

  Scenario: No login details
    When I press "button_login"
    Then I should see "Login success!"

  Scenario Outline: Login with incorrect credential and network connected
    When I login RapidReg for the first time with "<Username>" and "<Password>" and "<URL>"
    Then I should see "Sorry, incorrect username or password."
    And I should see "Login failed, first time users please login online to enable your account for offline mode"
    Examples:
      | Username | Password | URL                     |
      | primeroo | qu01n23! | http://10.29.3.184:3000 |
      | primero  | qu01n234 | http://10.29.3.184:3000 |

  Scenario Outline: Login with correct credential and network connected
    When I login RapidReg for the first time with "<Username>" and "<Password>" and "<URL>"
    Then I should see "Login success!"
    Examples:
      | Username | Password | URL                     |
      | primero  | qu01n23  | http://10.29.3.184:3000 |

  Scenario Outline: Re-login with incorrect credential and network connected
    When I re-login RapidReg with "<Username>" and "<Password>"
    Then I should see "Sorry, incorrect username or password."
    And I should see "Login failed, first time users please login online to enable your account for offline mode"
    Examples:
      | Username | Password |
      | primeroo | qu01n23! |
      | primero  | qu01n234 |

  Scenario: Re-login with correct credential and network connected
    When I re-login RapidReg with "<Username>" and "<Password>"
    Then I should see "Login success!"

    Scenario: Logout
      Given I re-login RapidReg with "<Username>" and "Password"
      When I logout
      Then I should see "Logout Successfully"
