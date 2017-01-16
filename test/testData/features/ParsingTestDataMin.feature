Tags: @non-visual

Feature: A feature to self test the webdriver substeps implementations
  Description over multiple lines

# a comment

 Background:
   Given stuff that happens first

  Scenario: a scenario
    Given I go to the self test page   # an end of line comment
    Then I can see "Hello Self Test page"
    Then I can see "#not a comment"

 Tags: another
  Scenario Outline: a scenario
    Given I go to the self test page
    Then I can see "Hello Self Test page"

    Examples:
      |col1 | col2 |
      | v1  |  v2  |



  Scenario Outline: another outline scenario
    Given I go to the self test page
    Then I can see "Hello Self Test page"

    Examples:
      |colA | colB |
      | v3  |  v4  |
