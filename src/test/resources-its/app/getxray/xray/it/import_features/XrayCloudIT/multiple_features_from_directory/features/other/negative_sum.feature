Feature:  As a user, I can add negative numbers

Scenario: addition of one negative integer
        Given I have entered -1 into the calculator
        And I have entered 2 into the calculator
        When I press add
        Then the result should be 1 on the screen

Scenario: addition of two negative integers
        Given I have entered -1 into the calculator
        And I have entered -2 into the calculator
        When I press add
        Then the result should be -3 on the screen
