package model;

import java.io.IOException;
import java.util.List;

/**
 * GameModel is the M in MVC.
 *
 * Responsibilities:
 *   - Maintain all game state (player, world, rooms, items, puzzles, monsters)
 *   - Enforce all game rules (movement, weight limit, puzzle solving, combat)
 *   - Return result strings to the Controller — never print anything directly
 *
 * Monster attack rule:
 *   A monster attacks ONLY when the player attempts to move (in any direction),
 *   whether the move succeeds or is blocked. This preserves strategic space —
 *   the player can examine, use items, and answer puzzles without taking damage.
 *
 * Scoring rule:
 *   Final score = sum of value from all solved puzzles
 *              + sum of value from all defeated monsters
 *              + sum of value from all items in inventory at game end
 *
 * What it does NOT do:
 *   - No reading from input (that is the Controller's job)
 *   - No printing to output (returns Strings only)
 */
public class GameModel implements IGameModel {

  // ── Scoring / rank constants ────────────────────────────────────────────
  private static final int RANK_GOLD   = 500;
  private static final int RANK_SILVER = 250;
  private static final int RANK_BRONZE = 100;

  // ── State ───────────────────────────────────────────────────────────────
  private final GameWorld      world;
  private Player               player;
  private final GameSaveManager saveManager;

  // ── Constructor ─────────────────────────────────────────────────────────

  /**
   * Constructs a GameModel from an already-loaded GameWorld.
   * The Player is created later via setPlayerName().
   *
   * @param world the game world loaded by JsonGameLoader
   */
  public GameModel(GameWorld world) {
    this.world       = world;
    this.saveManager = new GameSaveManager();
  }

  // ── Setup ────────────────────────────────────────────────────────────────

  @Override
  public void setPlayerName(String name) {
    this.player = new Player(name, 1);
  }

  // ── Movement ─────────────────────────────────────────────────────────────

  /**
   * Attempts to move the player in the given direction.
   *
   * Movement rules:
   *   exit == 0  → wall, cannot move
   *   exit < 0   → blocked by puzzle or monster; monster attacks if present
   *   exit > 0   → move succeeds; monster in NEW room attacks if active
   *
   * Monster attack is triggered on ANY move attempt (blocked or successful).
   */
  @Override
  public String move(String direction) {
    Room current = currentRoom();
    Direction dir;
    try {
      dir = Direction.fromString(direction);
    } catch (IllegalArgumentException e) {
      return "Invalid direction.";
    }

    int exit = current.getExit(dir);

    // ── Wall ──────────────────────────────────────────────────────────────
    if (exit == 0) {
      return "There is a wall in that direction. You cannot go that way.";
    }

    // ── Blocked by puzzle or monster ──────────────────────────────────────
    if (exit < 0) {
      StringBuilder sb = new StringBuilder();
      sb.append(blockedMessage(current));

      // Monster in current room attacks when player tries to push through
      Monster m = current.getMonster();
      if (m != null && m.isActive() && m.canAttack()) {
        sb.append("\n").append(monsterAttack(m));
      }
      return sb.toString();
    }

    // ── Successful move ───────────────────────────────────────────────────
    player.setCurrentRoomNumber(exit);
    Room newRoom = currentRoom();

    StringBuilder sb = new StringBuilder();
    sb.append(describeRoom(newRoom));

    // Monster in the NEW room attacks on entry
    Monster m = newRoom.getMonster();
    if (m != null && m.isActive() && m.canAttack()) {
      sb.append("\n").append(monsterAttack(m));
    }

    return sb.toString();
  }

  // ── Look ──────────────────────────────────────────────────────────────────

  /**
   * Describes the current room.
   * Does NOT trigger a monster attack — looking around is safe.
   */
  @Override
  public String look() {
    Room room = currentRoom();
    StringBuilder sb = new StringBuilder();

    sb.append("Health: ").append(player.getHealth())
            .append(" [").append(player.getHealthStatus()).append("]\n");
    sb.append(describeRoom(room));

    return sb.toString();
  }

  // ── Items ─────────────────────────────────────────────────────────────────

  @Override
  public String takeItem(String itemName) {
    Room room = currentRoom();
    List<Item> roomItems = room.getItems();

    if (roomItems == null || roomItems.isEmpty()) {
      return "There are no items here to take.";
    }

    Item target = findItemInList(roomItems, itemName);
    if (target == null) {
      return "You don't see '" + itemName + "' here.";
    }

    if (!player.getInventory().canAdd(target)) {
      return "You cannot carry '" + target.getName()
              + "'. It would exceed your weight limit of 13.";
    }

    room.removeItem(target);
    player.getInventory().addItem(target);
    return "You pick up " + target.getName() + ".";
  }

  @Override
  public String dropItem(String itemName) {
    Inventory inv = player.getInventory();
    Item target = inv.getItem(itemName);

    if (target == null) {
      return "You don't have '" + itemName + "' in your inventory.";
    }

    inv.removeItem(itemName);
    currentRoom().addItem(target);
    return "You drop " + target.getName() + ".";
  }

  /**
   * Uses an item from inventory.
   *
   * If the current room has an active puzzle and this item is its solution,
   * the puzzle is deactivated, the blocked exit is opened, and score is awarded.
   * If the item matches the current monster's solution, the monster is defeated.
   * The item's use() is always called (decrementing uses remaining).
   */
  @Override
  public String useItem(String itemName) {
    Inventory inv = player.getInventory();
    Item item = inv.getItem(itemName);

    if (item == null) {
      return "You don't have '" + itemName + "' in your inventory.";
    }
    if (!item.isUsable()) {
      return item.getName() + " has no uses remaining.";
    }

    String useResult = item.use();
    Room room = currentRoom();

    // Check puzzle solution
    Puzzle puzzle = room.getPuzzle();
    if (puzzle != null && puzzle.isActive() && puzzle.solveWithItem(itemName)) {
      puzzle.deactivate();
      player.addScore(puzzle.getValue());
      openBlockedExits(room);
      return useResult + "\nPuzzle solved! " + puzzle.getName() + " is deactivated.";
    }

    // Check monster solution
    Monster monster = room.getMonster();
    if (monster != null && monster.isActive()
            && itemName.equalsIgnoreCase(monster.getSolution())) {
      monster.deactivate();
      player.addScore(monster.getValue());
      openBlockedExits(room);
      return useResult + "\nYou defeated " + monster.getName() + "!";
    }

    return useResult;
  }

  @Override
  public String examine(String name) {
    // Check inventory first
    Item invItem = player.getInventory().getItem(name);
    if (invItem != null) {
      return invItem.getName() + ": " + invItem.getDescription();
    }

    // Check room items
    Room room = currentRoom();
    if (room.getItems() != null) {
      Item roomItem = findItemInList(room.getItems(), name);
      if (roomItem != null) {
        return roomItem.getName() + ": " + roomItem.getDescription();
      }
    }

    // Check fixtures in world
    Fixture fixture = world.getFixture(name);
    if (fixture != null) {
      return fixture.getName() + ": " + fixture.getDescription();
    }

    return "You don't see '" + name + "' here.";
  }

  @Override
  public String getInventoryString() {
    return player.getInventory().listItems();
  }

  // ── Puzzle ────────────────────────────────────────────────────────────────

  /**
   * Submits a text answer to the current room's active puzzle.
   * Only works for puzzles whose solution is wrapped in single quotes.
   */
  @Override
  public String answerPuzzle(String answer) {
    Room room = currentRoom();
    Puzzle puzzle = room.getPuzzle();

    if (puzzle == null || !puzzle.isActive()) {
      return "There is no active puzzle here to answer.";
    }

    if (!puzzle.isAnswerSolution()) {
      return "This puzzle requires an item, not a text answer.";
    }

    if (puzzle.solve(answer)) {
      puzzle.deactivate();
      player.addScore(puzzle.getValue());
      openBlockedExits(room);
      return "Correct! " + puzzle.getName() + " is solved.";
    }

    return "That is not the correct answer. Try again.";
  }

  // ── Game state ────────────────────────────────────────────────────────────

  @Override
  public boolean isGameOver() {
    return player.getHealth() <= 0;
  }

  /**
   * Builds the end-game summary with final score and rank.
   *
   * Rank thresholds:
   *   >= 500 → Gold Explorer
   *   >= 250 → Silver Adventurer
   *   >= 100 → Bronze Wanderer
   *   <  100 → Novice
   */
  @Override
  public String getEndGameSummary() {
    int score = player.getScore();
    String rank;
    if      (score >= RANK_GOLD)   rank = "Gold Explorer";
    else if (score >= RANK_SILVER) rank = "Silver Adventurer";
    else if (score >= RANK_BRONZE) rank = "Bronze Wanderer";
    else                           rank = "Novice";

    return String.format(
            "\n=== GAME OVER ===\nPlayer : %s\nScore  : %d\nRank   : %s\n",
            player.getName(), score, rank);
  }

  // ── Save / Restore ────────────────────────────────────────────────────────

  @Override
  public String save() {
    try {
      saveManager.save(player, world);
      return "Game saved successfully.";
    } catch (IOException e) {
      return "Failed to save game: " + e.getMessage();
    }
  }

  @Override
  public String restore() {
    try {
      this.player = saveManager.restore(world);
      return "Game restored.\n" + look();
    } catch (IOException e) {
      return "Failed to restore game: " + e.getMessage();
    }
  }

  // ── Private helpers ───────────────────────────────────────────────────────

  /** Returns the room the player is currently in. */
  private Room currentRoom() {
    return GameWorld.getRoom(player.getCurrentRoomNumber());
  }

  /**
   * Builds a full room description string.
   * Shows puzzle/monster effects text if active;
   * otherwise shows the room's normal description.
   */
  private String describeRoom(Room room) {
    StringBuilder sb = new StringBuilder();
    sb.append("You are in: ").append(room.getRoomName().toUpperCase()).append("\n");

    Puzzle  puzzle  = room.getPuzzle();
    Monster monster = room.getMonster();

    if (puzzle != null && puzzle.isActive()) {
      sb.append(puzzle.getEffects()).append("\n");
    } else if (monster != null && monster.isActive()) {
      sb.append(monster.getEffects()).append("\n");
      sb.append("A monster ").append(monster.getName()).append(" blocks your way!\n");
    } else {
      sb.append(room.getDescription()).append("\n");
    }

    // List visible items
    List<Item> items = room.getItems();
    if (items != null && !items.isEmpty()) {
      sb.append("Items here: ");
      for (int i = 0; i < items.size(); i++) {
        if (i > 0) sb.append(", ");
        sb.append(items.get(i).getName().toUpperCase());
      }
      sb.append("\n");
    }

    return sb.toString();
  }

  /**
   * Returns the blocked-path message from the active puzzle or monster.
   */
  private String blockedMessage(Room room) {
    Puzzle puzzle = room.getPuzzle();
    if (puzzle != null && puzzle.isActive()) {
      return puzzle.getEffects();
    }
    Monster monster = room.getMonster();
    if (monster != null && monster.isActive()) {
      return monster.getEffects();
    }
    return "Something is blocking your path.";
  }

  /**
   * Applies monster attack damage to the player and returns the attack message.
   */
  private String monsterAttack(Monster monster) {
    player.takeDamage(Math.abs(monster.getDamage()));
    return monster.getName().toUpperCase() + " attacks! " + monster.getAttack()
            + "\nYou take " + Math.abs(monster.getDamage()) + " damage!"
            + "\nHealth: " + player.getHealth()
            + " [" + player.getHealthStatus() + "]";
  }

  /**
   * Opens all exits in a room that are currently blocked (negative values)
   * by flipping them to their positive equivalent.
   * Called after a puzzle is solved or a monster is defeated.
   */
  private void openBlockedExits(Room room) {
    for (Direction dir : Direction.values()) {
      int exit = room.getExit(dir);
      if (exit < 0) {
        room.setExit(dir, Math.abs(exit));
      }
    }
  }

  /**
   * Finds an item in a list by name (case-insensitive).
   */
  private Item findItemInList(List<Item> items, String name) {
    for (Item item : items) {
      if (item.getName().equalsIgnoreCase(name)) {
        return item;
      }
    }
    return null;
  }
}
