# Refined Textual Scenarios

## Scenario 1: Start a New Game

**Goal:** begin a new session from the JSON-defined world.

**Preconditions:**
- A valid game JSON file exists.
- The application is started with input and output streams.

**Main success scenario:**
1. The player launches the application.
2. `GameEngineApp` loads the world from JSON through `JsonGameLoader`.
3. The app creates `GameModel` with the loaded `GameWorld`.
4. The app creates `GameController` with the model and console I/O.
5. The controller prompts for the player name.
6. The player enters a name.
7. The controller calls `setPlayerName(name)`.
8. The model creates a `Player` in room 1 with full health and an empty inventory.
9. The controller requests `look()`.
10. The model returns the current room description.
11. The controller displays the starting game state.

**Postconditions:**
- The game loop is active.
- The player is initialized and placed in the starting room.

## Scenario 2: Move into a Blocked or Dangerous Room

**Goal:** attempt to move north, south, east, or west.

**Preconditions:**
- The game is running.
- The player has already been created.

**Main success scenario:**
1. The player enters a movement command.
2. The controller maps the command to `move(direction)`.
3. The model looks up the current room and the exit value for that direction.
4. If the exit value is `0`, the model returns a wall message.
5. If the exit value is negative, the model returns the blocking puzzle or monster message.
6. If an active monster in the room can attack, the monster attack is applied during the movement attempt.
7. If the exit value is positive, the model updates the player's room number.
8. The model describes the new room.
9. If the new room contains an active attacking monster, the attack is applied on entry.
10. The controller prints the result.

**Alternative flows:**
- If the direction text is invalid, the model returns `"Invalid direction."`
- If health drops to zero, the controller exits the main loop and reports game over.

**Postconditions:**
- The player either remains in the same room or moves to the destination room.
- Health may be reduced by a monster attack.

## Scenario 3: Solve a Puzzle with an Item

**Goal:** use an inventory item to solve the current room's active puzzle.

**Preconditions:**
- The player possesses the required item.
- The current room has an active item-based puzzle.

**Main success scenario:**
1. The player enters `use <item name>`.
2. The controller calls `useItem(itemName)`.
3. The model verifies that the item is in inventory and still usable.
4. The model decrements the item's remaining uses.
5. The model compares the item against the active puzzle solution.
6. The puzzle is deactivated.
7. The puzzle value is added to the player's score.
8. Blocked exits in the room are opened by converting negative exits to positive exits.
9. If the item has no uses remaining, it is removed from inventory.
10. The controller displays the success message.

**Alternative flows:**
- If the player does not have the item, the model returns an inventory error.
- If the item has no uses left, the model returns an unusable-item message.
- If the item does not solve the puzzle, only the item's normal use text is returned.

**Postconditions:**
- The puzzle is either still active or permanently deactivated.
- Score and inventory state may change.

## Scenario 4: Save and Restore a Game

**Goal:** persist the current session and later resume it.

**Preconditions:**
- The player has already started a game.

**Main success scenario for save:**
1. The player enters `save <file name>`.
2. The controller calls `save()`.
3. The model delegates to `GameSaveManager.save(player, world)`.
4. The save manager serializes player stats, inventory, item uses, puzzle states, monster states, and room exits to JSON.
5. In the current implementation, the state is written to the fixed path `./savegame.json`.
6. The controller displays a confirmation message.

**Main success scenario for restore:**
1. The player enters `restore <file name>`.
2. The controller calls `restore()`.
3. The model delegates to `GameSaveManager.restore(world)`.
4. The save manager reads the fixed path `./savegame.json`.
5. The save manager recreates player state and restores mutable world state in place.
6. The model replaces its current `Player` reference.
7. The model calls `look()` and returns the restored room description.
8. The controller displays the restored state.

**Alternative flows:**
- If the save file cannot be written or read, the model returns an error message.

**Postconditions:**
- Save: the current mutable state is written to disk.
- Restore: the player and world return to the saved state.
