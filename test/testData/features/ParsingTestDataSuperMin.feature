Feature: A barebones features

  Scenario: a barebones scenario
    Given line 1 of the first scenario
    Then line 2 of the first scenario

  Scenario Outline: another barebones scenario
    Given line 1 of the second scenario
    Then I should see on the page for each selection
          |fieldDescription                |
          |Selection name                  |
          |Market name                     |
          |Event name                      |
          |Correct Potential returns       |
          |Correct Total Stake             |
          |Correct Total potential returns |
          |Selection name                  |
    When I click Place Bet
    And I will have the correct amount of funds remaining in my sportsbook account
    And my bet placement receipt will include: "<selection_name>", "<betprice>", "<market_name>", "<event_name>", "<stake>", "<potential_return>", "<sogei_id>"
    Then I can view the SOGEI ID through a deep link

Examples:
| one | two |
| three | four |
