package model;

import java.io.IOException;
import java.util.List;

/**
 * Core game model (MVC - Model).
 * <p>
 * Handles:
 *   - Game state (player, world, rooms, items, puzzles, monsters)
 *   - Game logic (movement, combat, puzzles, inventory rules)
 * <p>
 * Notes:
 *   - Monsters attack only when the player attempts to move
 *   - Methods return messages; no direct printing
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
   * Attempts to move the player in a direction.
   * <p>
   * Rules:
   *   - 0  → wall (cannot move)
   *   - <0 → blocked (puzzle/monster)
   *   - >0 → move succeeds
   * <p>
   * A monster may attack on any move attempt.
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
   * Returns a description of the current room.
   * Safe action — does not trigger attacks.
   */
  @Override
  public String look() {
    Room room = currentRoom();
    StringBuilder sb = new StringBuilder();

    sb.append("Health: ").append(player.getHealth())
            .append(" [").append(player.getHealthStatus()).append("]\n");
    sb.append("Weight: ").append(player.getInventory().getCurrentWeight());
    sb.append(describeRoom(room));

    return sb.toString();
  }

  // ── Items ─────────────────────────────────────────────────────────────────

  /**
   * Picks up an item from the current room.
   */
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

  /**
   * Drops an item from inventory into the current room.
   */
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
   * <p>
   * - Solves puzzle if applicable
   * - Defeats monster if it matches
   * - Always consumes a use
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

  /**
   * Examines an object by name.
   * <p>
   * Searches in:
   *   - Inventory
   *   - Current room items
   *   - World fixtures
   * <p>
   * Returns its description if found.
   */
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

  /**
   * Returns a formatted list of items in the player's inventory.
   */
  @Override
  public String getInventoryString() {
    return player.getInventory().listItems();
  }

  // ── Puzzle ────────────────────────────────────────────────────────────────

  /**
   * Submits a text answer to the current puzzle.
   * Only valid for text-based puzzles.
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

  /**
   * Returns true if the player has no health remaining.
   */
  @Override
  public boolean isGameOver() {
    return player.getHealth() <= 0;
  }

  /**
   * Builds the end-game summary with final score and rank.
   * <p>
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

  /**
   * Saves the current game state.
   */
  @Override
  public String save() {
    try {
      saveManager.save(player, world);
      return "Game saved successfully.";
    } catch (IOException e) {
      return "Failed to save game: " + e.getMessage();
    }
  }

  /**
   * Restores a previously saved game state.
   */
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
   * Unlocks exits after puzzle solved or monster defeated.
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
