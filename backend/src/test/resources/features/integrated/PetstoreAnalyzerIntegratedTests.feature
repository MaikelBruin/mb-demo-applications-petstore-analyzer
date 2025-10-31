Feature: Petstore analyzer integrated tests

  Scenario: Get total number of dogs should not throw exception
    Given I have access to the petstore
    When I get the total number of dogs
    Then the total number of dogs response should not be null
    And the total number of dogs should be equal than or greater than 0

  Scenario: Get total number of cats should not throw exception
    Given I have access to the petstore
    When I get the total number of cats
    Then the total number of cats response should not be null
    And the total number of cats should be equal than or greater than 0
