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
The project includes some complex algorithms to meet the requirements.

### Validating moves
Each turn, the player always plays one card to the table. If the player captures a card or cards, the player put the card played and the card (s) captured in a separate pile. If no card is captured, the player plays his or her card on the table face up to be captured later. A played card can capture:
* A card on the table of the same capture value
* A set of cards on the table whose capture values add up to the capture value of the played
card
* Several cards or sets of cards that satisfy conditions 1 and/or 2 above

To validate user's moves, the program find all possible moves with the played card and the table, and checks if the player's move is among them. To find all possible moves with the played card and the table, the algorithm proceeds as following:
* Include the simple Play move (no taken cards, the player simply leaves card on table)
* 
