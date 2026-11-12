Feature: Petstore analyzer system integration tests

  Background:
    Given I have access to the actual petstore
    And I have access to the actual consumer

    ################### same tests as system tests ##################################################

  Scenario: Get total number of dogs should not throw exception
    When I get the total number of dogs
    Then the total number of dogs response should not be null
    And the total number of dogs should be equal than or greater than 0

  Scenario: Get total number of cats should not throw exception
    When I get the total number of cats
    Then the total number of cats response should not be null
    And the total number of cats should be equal than or greater than 0

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

    @using-spy
  Scenario: Consumer should be notified for each analysis done
    When I get if there are any rats available
    And I get the ratio of available pets vs sold pets
    And I get the total number of available pets
    And I get the total number of pets with tag "rat"
    Then the consumer should be notified 4 times

    ################### END same tests as system tests ##################################################
