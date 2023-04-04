# Casino Project

This is the final project for the course CS-C2105 Programming Studio A 2022 at Aalto University.

## Aim
The project aims to recreate real-life Casino card game following Finnish rules. Specifically, major requirements include:
* Gameplay between 2-N players
* Functionality to validate cards taken from the table (capture moves)
* Possibility to play against 0-N simple computer controlled opponents
* Graphical Interface
* Functionality to save and load the game state into/from a file

## Implementation
The project includes some complex algorithms to meet the requirements. The project document will include and explain in details the implementation and algorithms used. Major algorithms are:

### Validating moves
Each turn, the player always plays one card to the table. If the player captures a card or cards, the player put the card played and the card (s) captured in a separate pile. If no card is captured, the player plays his or her card on the table face up to be captured later. A played card can capture:
* A card on the table of the same capture value
* A set of cards on the table whose capture values add up to the capture value of the played
card
* Several cards or sets of cards that satisfy conditions 1 and/or 2 above

To validate user's moves, the program find all possible moves with the played card and the chosen cards to be taken, and checks if the player's move is among them. To find all possible moves with the played card and the the chosen cards, the algorithm proceeds as following:
* Include the simple Play move (no taken cards, the player simply leaves card on table)
* Use Subset-Sum algorithm to find all the smallest subsets of cards in the chosen cards which sums up to value of the played card
* Stack non-overlapping subsets

This algorithm is superior to following algorithms in terms of accuracy: checking total sum modulo of value of the played card, subarray sum algorithm. Nevertheless, the algorithm can be improved by switching from Power Set to Dynamic Programming to implement Subset-Sum Algorithm.

### Computer-controlled players
The computer-controlled players use the aforementioned algorithm to find all possible moves with the player's cards and the cards on table. However, the goal is to create computer-controlled players that use strategies to decide which move to choose and create different types of players that use different strategies or have different preferences.

This goal is achieved with a "grading" system that is similar to a linear equation. In the grading system, there are several strategies which assess the move separately and give from it a fixed amount of points (positive & negative). The points awarded by each strategy is multiplied with a corresponding coefficient for the strategy, and then summed up to give a final point for the move. The set of coefficients for each strategy differs between different types of players. Thus, we can create infinitely many types of players by simply changing the coefficients.

## Details
Further details of gameplay, the programme implementation, data structures and algorithms, as well as testing are available in the [Project Document](https://github.com/nghivo94/casino/blob/main/documents/Project%20Document.pdf).
