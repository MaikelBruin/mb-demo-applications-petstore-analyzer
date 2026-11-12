Feature: Petstore analyzer system tests

  Background:
    Given I have access to the mocked petstore
    And I configure default responses for the petstore
    And I have access to the mocked consumer
    And I configure default responses for the consumer

    ################### same tests as system integration tests ##################################################
  Scenario: Get total number of dogs should not throw exception
    When I get the total number of dogs
    Then the total number of dogs response should not be null
    And the total number of dogs should be equal than or greater than 0

  Scenario: Get total number of pets with tag should not throw exception
    When I get the total number of pets with tag "rat"
    Then the total number of pets with tag response should not be null
    And the total number of pets with tag should be equal than or greater than 0

  Scenario: Get total number of available pets should not throw exception
    When I get the total number of available pets
    Then the total number of available pets response should not be null
    And the total number of available pets should be equal than or greater than 0

  Scenario: Get availability ratio should not throw exception
    When I get the ratio of available pets vs sold pets
    Then the availability ratio response should not be null
    And the availability ratio should be equal to the total available divided by the total sold

  Scenario: Get has available rats should not throw exception
    When I get if there are any rats available
    Then the has available rats response should not be null

    ################### END same tests as system integration tests ##################################################

  Scenario: Availability ratio - Division by zero should result not result in exception
    Given I configured the petstore find pets by status "sold" response to return an empty array
    When I get the ratio of available pets vs sold pets
    Then the availability ratio response should not be null

  Scenario: Has available rats - no rats available
    Given I configured the petstore find pets by tags "rat,rats" response to return an empty array
    When I get if there are any rats available
    Then the has available rats response should not be null
    And the has available rats response should say that there are "no" rats available

  Scenario: Has available rats - has rats available
    Given I configured the petstore find pets by tags "rat,rats" response to return a non-empty array
    When I get if there are any rats available
    Then the has available rats response should not be null
    And the has available rats response should say that there are "" rats available

    ############## new application requirements ###########################

  Scenario: 1. Consumer should be notified for each analysis done
    When I get if there are any rats available
    And I get the ratio of available pets vs sold pets
    And I get the total number of available pets
    And I get the total number of pets with tag "rat"
    Then the consumer should be notified 4 times

  Scenario: 2. Petstore 503 should result in SupplierDownException
    Given I configured the petstore to respond with a service unavailable exception
    When I get the total number of dogs and capture the HTTP status
    Then the analyzer should have responded with HTTP status 503
    And the analyzer should have thrown a SupplierDownException
    And the error message of the response should be "Petstore supplier is down"
    And the consumer should not have received any event

  Scenario: 3. Consumer 503 should result in ConsumerDownException
    Given I configured the consumer to respond with a service unavailable exception
    When I get the total number of dogs and capture the HTTP status
    Then the analyzer should have responded with HTTP status 503
    And the analyzer should have thrown a ConsumerDownException
    And the error message of the response should be "Petstore analyzer consumer is down"

  Scenario: 4. Consumer 418 should be ignored
    Given I configured the consumer to respond with a teapot exception
    When I get the total number of dogs
    Then the consumer should have received an event of type "total-dogs"
    And the total number of dogs response should not be null
    And the total number of dogs should be equal than or greater than 0

    ############## END new application requirements ###########################
