Tags: @non-visual

Feature: A feature to self test the webdriver substeps implementations
  Description over multiple lines

# a comment

  Background:
    Given stuff that happens first

  Scenario: a scenario
    Given I go to the self test page
    Then I can see "Hello Self Test page"

  Tags: other

  Scenario Outline: an outline scenario
    Given something
    And something else

    Examples:
      |col1	|col2	|
      |val1	|val2	|
      |val3	|val4	|
