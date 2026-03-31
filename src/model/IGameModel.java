package model;

/**
 * IGameModel defines the contract between the Controller and the Model.
 *
 * The Controller ONLY interacts with the game through this interface.
 * No Controller code should ever import or reference GameModel, Player,
 * Room, Item, etc. directly.
 *
 * Every method returns a String — the message to display to the player.
 *
 * Monster attack rule:
 *   Attacks are triggered only during move() — either when blocked or
 *   on successful entry into a new room. All other actions are safe.
 *
 * Scoring rule:
 *   Final score = sum of value from all solved puzzles
 *              + sum of value from all defeated monsters
 *              + sum of value from all items in inventory at game end
 */
public interface IGameModel {

  // ── Setup ──────────────────────────────────────────────────────────────

  /**
   * Creates the Player with the given name, placed in room 1.
   * Must be called once before any other method.
   *
   * @param name the player's chosen name (non-null, non-blank)
   */
  void setPlayerName(String name);

  // ── Movement ───────────────────────────────────────────────────────────

  /**
   * Attempts to move the player in the given direction.
   *
   * Possible outcomes:
   *   - exit == 0 : wall — returns a blocked message, no attack
   *   - exit < 0  : puzzle or monster blocking — returns effects text;
   *                 monster attacks if present and can_attack is true
   *   - exit > 0  : move succeeds — returns new room description;
   *                 monster in new room attacks if active and can_attack
   *
   * @param direction "N", "S", "E", or "W"
   * @return result message to display to the player
   */
  String move(String direction);

  // ── Room ──────────────────────────────────────────────────────────────

  /**
   * Returns a description of the current room.
   *
   * Includes:
   *   - Player health and health status
   *   - Room name
   *   - Puzzle effects (if puzzle is active), or monster effects
   *     (if monster is active), or normal room description otherwise
   *   - List of visible items in the room
   *
   * Does NOT trigger a monster attack.
   *
   * @return room description string
   */
  String look();

  // ── Items ──────────────────────────────────────────────────────────────

  /**
   * Takes an item from the current room and adds it to the player's inventory.
   *
   * Fails if:
   *   - No items are present in the room
   *   - The named item is not found in the room
   *   - Adding the item would exceed the weight limit of 13
   *
   * @param itemName the name of the item to take (case-insensitive)
   * @return result message
   */
  String takeItem(String itemName);

  /**
   * Drops an item from the player's inventory into the current room.
   *
   * Fails if the named item is not in the player's inventory.
   *
   * @param itemName the name of the item to drop (case-insensitive)
   * @return result message
   */
  String dropItem(String itemName);

  /**
   * Uses an item from the player's inventory.
   *
   * Behaviour:
   *   - Always decrements the item's uses remaining
   *   - If the item matches the current room's active puzzle solution,
   *     the puzzle is deactivated, blocked exits are opened, and score
   *     is awarded
   *   - If the item matches the current room's active monster solution,
   *     the monster is defeated, blocked exits are opened, and score
   *     is awarded
   *   - If neither, returns the item's when_used text only
   *
   * Fails if:
   *   - The item is not in the player's inventory
   *   - The item has no uses remaining
   *
   * @param itemName the name of the item to use (case-insensitive)
   * @return result message
   */
  String useItem(String itemName);

  /**
   * Examines an item or fixture by name.
   *
   * Search order:
   *   1. Player's inventory
   *   2. Items in the current room
   *   3. Fixtures in the game world
   *
   * @param name the name of the object to examine (case-insensitive)
   * @return the object's description, or a not-found message
   */
  String examine(String name);

  /**
   * Returns a formatted listing of the player's current inventory,
   * including item names, weights, and total weight vs. capacity.
   *
   * @return inventory string
   */
  String getInventoryString();

  // ── Puzzle ─────────────────────────────────────────────────────────────

  /**
   * Submits a text answer to the current room's active puzzle.
   *
   * Only applies to puzzles whose solution is wrapped in single quotes
   * (i.e. isAnswerSolution() == true). Item-based puzzles must be
   * solved via useItem() instead.
   *
   * If correct: puzzle is deactivated, blocked exits are opened,
   * and score is awarded.
   *
   * Fails if:
   *   - There is no active puzzle in the current room
   *   - The puzzle requires an item, not a text answer
   *   - The answer does not match the solution
   *
   * @param answer the player's text answer
   * @return result message
   */
  String answerPuzzle(String answer);

  // ── Game state ─────────────────────────────────────────────────────────

  /**
   * Returns true if the game is over (player health <= 0).
   * Checked by the controller after every action.
   *
   * @return true if the player has been incapacitated
   */
  boolean isGameOver();

  /**
   * Returns the end-game summary, including player name, final score,
   * and rank.
   *
   * Rank thresholds:
   *   >= 500 → Gold Explorer
   *   >= 250 → Silver Adventurer
   *   >= 100 → Bronze Wanderer
   *   <  100 → Novice
   *
   * Called when the player quits or health reaches zero.
   *
   * @return end-game summary string
   */
  String getEndGameSummary();

  // ── Save / Restore ─────────────────────────────────────────────────────

  /**
   * Saves the current game state to a file (savegame.json).
   *
   * Captured state includes: player name, health, score, current room,
   * inventory contents, item uses remaining, puzzle/monster active status,
   * and room exit values.
   *
   * @return confirmation message, or error message if saving fails
   */
  String save();

  /**
   * Restores the game state from a previously saved file (savegame.json).
   * Replaces the current player and restores all world state in place.
   * Automatically calls look() to display the restored room.
   *
   * @return confirmation message followed by room description,
   *         or error message if restoring fails
   */
  String restore();
}
