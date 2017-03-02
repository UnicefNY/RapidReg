Feature: Login test

  @login_with_no_account
  Scenario: No login details
    When I press "login" button
    Then I should see "Enter a valid URL!"


  @first_time_with_corrct_URL
  Scenario Outline: Login with incorrect Uaername / Passowrd and corrct URL with network connected
    When I login RapidReg for the first time with incorrect "<Username>" or "<Password>" and correct "<URL>"
    Then I should see "Sorry, please check your username, password and URL."
    Examples:
      | Username     | Password   | URL                         |
      | primero_cp   | qu01n23!!  | https://35.161.56.113:8443  |
      | primero_cpp  | qu01n23!   | https://35.161.56.113:8443  |
      | primero_GBV  | qu01n23!!  | http://35.164.57.91:12306   |
      | primero_GBVV | qu01n23!   | http://35.164.57.91:12306   |


  @first_time_with_incorrct_URL
  Scenario Outline: Login with incorrct URL with network connected
    When I login RapidReg for the first time with "<Username>" and "<Password>" and incorrect "<URL>"
    Then I should see "Failed to connect to ‘your URL’"
    And I should see "Sorry, please check your network and URL."
    Examples:
      | Username     | Password   | URL                        |
      | primero_cp   | qu01n23!   | https://35.161.4.113:8443  |
      | primero_cp   | qu01n23!!  | https://35.161.4.113:8443  |
      | primero_cpp  | qu01n23!   | https://35.161.4.113:8443  |
      | primero_GBV  | qu01n23!   | http://35.164.5.91:12306   |
      | primero_GBV  | qu01n23!!  | http://35.164.5.91:12306   |
      | primero_GBVV | qu01n23!   | http://35.164.5.91:12306   |


  @first_time_with_corrct_info
  Scenario Outline: Login with correct credential and network connected
    When I login RapidReg for the first time with "<Username>" and "<Password>" and "<URL>"
    Then I should see "Login succeed"
    And I should see current user is "<Username>"
    And the organization is "agency-unicef"
    Examples:
      | Username     | Password   | URL                        |
      | primero_cp   | qu01n23!   | https://35.161.56.113:8443  |
      | primero_gbv  | qu01n23!   | http://35.164.57.91:12306   |

  @second_time_with_corrct_URL
  Scenario Outline: Re-login with incorrect Uaername or Passowrd and corrct URL with network connected
    When I re-login RapidReg with incorrect "<Username>" or "<Password>" and correct "<URL>"
    Then I should see "Sorry, please check your username, password and URL."
    Examples:
      | Username     | Password   | URL                         |
      | primero_cp   | qu01n23!!  | https://35.161.56.113:8443  |
      | primero_cpp  | qu01n23!   | https://35.161.56.113:8443  |
      | primero_GBV  | qu01n23!!  | http://35.164.57.91:12306   |
      | primero_GBVV | qu01n23!   | http://35.164.57.91:12306   |

  @second_time_with_incorrct_URL
  Scenario Outline: Login with incorrct URL with network connected
    When I re-login RapidReg with "<Username>" and "<Password>" and incorrect "<URL>"
    Then I should see "Failed to connect to ‘your URL’"
    And I should see "Sorry, please check your network and URL."
    Examples:
      | Username     | Password   | URL                        |
      | primero_cp   | qu01n23!   | https://35.161.4.113:8443  |
      | primero_cp   | qu01n23!!  | https://35.161.4.113:8443  |
      | primero_cpp  | qu01n23!   | https://35.161.4.113:8443  |
      | primero_GBV  | qu01n23!   | http://35.164.5.91:12306   |
      | primero_GBV  | qu01n23!!  | http://35.164.5.91:12306   |
      | primero_GBVV | qu01n23!   | http://35.164.5.91:12306   |


  @second_time_with_corrct_info
  Scenario Outline: Re-Login with correct credential and network connected
    When I re-login RapidReg with "<Username>" and "<Password>" and "<URL>"
    Then I should see "Login succeed"
    And I should see current user is "<Username>"
    And the organization is "agency-unicef"
    Examples:
      | Username     | Password   | URL                        |
      | primero_cp   | qu01n23!   | https://35.161.56.113:8443  |
      | primero_gbv  | qu01n23!   | http://35.164.57.91:12306   |

  @logout
  Scenario: Logout
    Given I login RapidReg with "primero_cp" account
    When I logout
    Then I should see "Logout Succeed"

