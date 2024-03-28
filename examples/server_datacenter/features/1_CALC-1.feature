@REQ_CALC-1
Feature: points calculation
	#As a player
	#I want the system to calculate my score
	#So that I know how good I am

	
	@TEST_BOOK-408 @1_CALC-1.feature @features/1_CALC-1.feature @forced
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

	
	@TEST_BOOK-379 @1_CALC-1.feature
	Scenario: Another beginner game
		Given a new bowling game
		When I roll the following series: 2,7,3,4,1,1,5,1,1,1,1,1,1,1,1,1,1,1,1,5,1
		Then my total score should be 40	

	
	@TEST_CALC-4
	Scenario: Another beginner game
		Given a new bowling game
		When I roll the following series: 2,7,3,4,1,1,5,1,1,1,1,1,1,1,1,1,1,1,1,5,1
		Then my total score should be 40	

	
	@TEST_CALC-5
	Scenario: Strikes only
		Given a new bowling game
		When all of my rolls are strikes
		Then my total score should be 300	

	
	@TEST_BOOK-421 @1_CALC-1.feature @features/1_CALC-1.feature @forced
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

	
	@TEST_CALC-6
	Scenario: a single spare
		Given a new bowling game
		When I roll the following series: 3,7,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
		Then my total score should be 29	

	
	@TEST_CALC-7
	Scenario: only spares
		Given a new bowling game
		When I roll 10 times 1 and 9
		And I roll 1
		Then my total score should be 110	

	
	@TEST_BOOK-364 @1_CALC-1.feature @features/1_CALC-1.feature @forced
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

	
	@TEST_BOOK-383 @1_CALC-1.feature
	Scenario: No points
		Given a new bowling game
		When all of my balls are landing in the gutter
		Then my total score should be 0	

	
	@TEST_CALC-2
	Scenario: No points
		Given a new bowling game
		When all of my balls are landing in the gutter
		Then my total score should be 0	

	
	@TEST_BOOK-381 @1_CALC-1.feature
	Scenario: a single spare
		Given a new bowling game
		When I roll the following series: 3,7,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
		Then my total score should be 29	

	
	@TEST_CALC-3 @1_CALC-1.feature @features/1_CALC-1.feature @forced
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

	
	@TEST_BOOK-382 @1_CALC-1.feature
	Scenario: only spares
		Given a new bowling game
		When I roll 10 times 1 and 9
		And I roll 1
		Then my total score should be 110	

	
	@TEST_BOOK-380 @1_CALC-1.feature
	Scenario: Strikes only
		Given a new bowling game
		When all of my rolls are strikes
		Then my total score should be 300