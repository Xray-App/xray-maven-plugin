@REQ_CALC-1
Feature: As a user, I can sum two numbers

	Background: Calculator
		Given I have a calculator turned on

	@core
	Scenario Outline: Cucumber Test As a user, I can sum two numbers
		Given I have entered <input_1> into the calculator
		And I have entered <input_2> into the calculator
		When I press <button>
		Then the result should be <output> on the screen
		
			Examples:
				| input_1 | input_2 | button | output |
				| 20      | 30      | add    | 50     |
				| 2       | 5       | add    | 7      |
				| 0       | 40      | add    | 40     |
				| 4       | 50      | add    | 54     |
				| 5       | 50      | add    | 55     |
