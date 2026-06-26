Feature: Bank account management

  Scenario: Create a new bank account
    Given the bank contains no accounts
    When a customer creates account "ACC001" for holder "Carla"
    Then account "ACC001" is created
    And account "ACC001" has a balance of 0.00

  Scenario: Deposit money into an account
    Given account "ACC001" exists for holder "Carla" with balance 0.00
    When 100.00 is deposited into account "ACC001"
    Then account "ACC001" has a balance of 100.00

  Scenario: Withdraw money with sufficient funds
    Given account "ACC001" exists for holder "Carla" with balance 100.00
    When 40.00 is withdrawn from account "ACC001"
    Then account "ACC001" has a balance of 60.00

  Scenario: Reject withdrawal with insufficient funds
    Given account "ACC001" exists for holder "Carla" with balance 20.00
    When 50.00 is withdrawn from account "ACC001"
    Then a conflict response is returned

  Scenario: Transfer money between two accounts
    Given account "ACC001" exists for holder "Carla" with balance 100.00
    And account "ACC002" exists for holder "Alice" with balance 0.00
    When 30.00 is transferred from "ACC001" to "ACC002"
    Then account "ACC001" has a balance of 70.00
    And account "ACC002" has a balance of 30.00

  Scenario: Reject transfer with insufficient funds
    Given account "ACC001" exists for holder "Carla" with balance 10.00
    And account "ACC002" exists for holder "Alice" with balance 0.00
    When 50.00 is transferred from "ACC001" to "ACC002"
    Then a conflict response is returned