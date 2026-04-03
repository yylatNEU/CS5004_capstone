package model;

import java.io.IOException;
import java.util.List;

/** Core game model that owns the active player session and applies gameplay rules. */
public class GameModel implements IGameModel {

  private static final int RANK_GOLD = 500;
  private static final int RANK_SILVER = 250;
  private static final int RANK_BRONZE = 100;

  private final GameWorld world;
  private final GameSaveManager saveManager;
  private Player player;

  /**
   * Creates a model backed by an already-loaded world.
   *
   * @param world the game world
   */
  public GameModel(GameWorld world) {
    this.world = world;
    this.saveManager = new GameSaveManager();
  }

  @Override
  public void setPlayerName(String name) {
    this.player = new Player(name, 1);
  }

  @Override
  public String move(String direction) {
    Room current = currentRoom();
    Direction parsedDirection;
    try {
      parsedDirection = Direction.fromString(direction);
    } catch (IllegalArgumentException exception) {
      return "Invalid direction.";
    }

    int exit = current.getExit(parsedDirection);
    if (exit == 0) {
      return "There is a wall in that direction. You cannot go that way.";
    }

    if (exit < 0) {
      StringBuilder blocked = new StringBuilder(blockedMessage(current));
      Monster monster = current.getMonster();
      if (monster != null && monster.isActive() && monster.canAttack()) {
        blocked.append("\n").append(monsterAttack(monster));
      }
      return blocked.toString();
    }

    player.setCurrentRoomNumber(exit);
    Room nextRoom = currentRoom();
    StringBuilder description = new StringBuilder(describeRoom(nextRoom));
    Monster monster = nextRoom.getMonster();
    if (monster != null && monster.isActive() && monster.canAttack()) {
      description.append("\n").append(monsterAttack(monster));
    }
    return description.toString();
  }

  @Override
  public String look() {
    StringBuilder description = new StringBuilder();
    description
        .append("Health: ")
        .append(player.getHealth())
        .append(" [")
        .append(player.getHealthStatus())
        .append("]\n");
    description
        .append("Weight: ")
        .append((int) player.getInventory().getCurrentWeight())
        .append("\n");
    description.append(describeRoom(currentRoom()));
    return description.toString();
  }

  @Override
  public String takeItem(String itemName) {
    Room room = currentRoom();
    List<Item> roomItems = room.getItems();
    if (roomItems.isEmpty()) {
      return "There are no items here to take.";
    }

    Item target = findItemInList(roomItems, itemName);
    if (target == null) {
      return "You don't see '" + itemName + "' here.";
    }

    if (!player.getInventory().canAdd(target)) {
      return "You cannot carry '"
          + target.getName()
          + "'. It would exceed your weight limit of 13.";
    }

    room.removeItem(target);
    player.getInventory().addItem(target);
    return "You pick up " + target.getName() + ".";
  }

  @Override
  public String dropItem(String itemName) {
    Inventory inventory = player.getInventory();
    Item target = inventory.getItem(itemName);
    if (target == null) {
      return "You don't have '" + itemName + "' in your inventory.";
    }

    inventory.removeItem(itemName);
    currentRoom().addItem(target);
    return "You drop " + target.getName() + ".";
  }

  @Override
  public String useItem(String itemName) {
    Inventory inventory = player.getInventory();
    Item item = inventory.getItem(itemName);
    if (item == null) {
      return "You don't have '" + itemName + "' in your inventory.";
    }
    if (!item.isUsable()) {
      return item.getName() + " has no uses remaining.";
    }

    StringBuilder result = new StringBuilder(item.use());
    if (!item.isUsable()) {
      inventory.removeItem(itemName);
      result.append("\n").append(item.getName()).append(" is broken and dropped from inventory.");
    } else {
      result.append(" (").append(item.getUsesRemaining()).append(" uses remaining)");
    }

    Room room = currentRoom();
    Puzzle puzzle = room.getPuzzle();
    if (puzzle != null && puzzle.isActive() && puzzle.solveWithItem(itemName)) {
      puzzle.deactivate();
      player.addScore(puzzle.getValue());
      openBlockedExits(room);
      return result
          .append("\nPuzzle solved! ")
          .append(puzzle.getName())
          .append(" is deactivated.")
          .toString();
    }

    Monster monster = room.getMonster();
    if (monster != null && monster.isActive() && itemName.equalsIgnoreCase(monster.getSolution())) {
      monster.deactivate();
      player.addScore(monster.getValue());
      openBlockedExits(room);
      return result.append("\nYou defeated ").append(monster.getName()).append("!").toString();
    }

    return result.toString();
  }

  @Override
  public String examine(String name) {
    Item inventoryItem = player.getInventory().getItem(name);
    if (inventoryItem != null) {
      return inventoryItem.getName() + ": " + inventoryItem.getDescription();
    }

    Item roomItem = findItemInList(currentRoom().getItems(), name);
    if (roomItem != null) {
      return roomItem.getName() + ": " + roomItem.getDescription();
    }

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

  @Override
  public String answerPuzzle(String answer) {
    Puzzle puzzle = currentRoom().getPuzzle();
    if (puzzle == null || !puzzle.isActive()) {
      return "There is no active puzzle here to answer.";
    }
    if (!puzzle.isAnswerSolution()) {
      return "This puzzle requires an item, not a text answer.";
    }
    if (!puzzle.solve(answer)) {
      return "That is not the correct answer. Try again.";
    }

    puzzle.deactivate();
    player.addScore(puzzle.getValue());
    openBlockedExits(currentRoom());
    return "Correct! " + puzzle.getName() + " is solved.";
  }

  @Override
  public boolean isGameOver() {
    return player.getHealth() <= 0;
  }

  @Override
  public String getEndGameSummary() {
    int score = player.getScore();
    String rank;
    if (score >= RANK_GOLD) {
      rank = "Gold Explorer";
    } else if (score >= RANK_SILVER) {
      rank = "Silver Adventurer";
    } else if (score >= RANK_BRONZE) {
      rank = "Bronze Wanderer";
    } else {
      rank = "Novice";
    }

    return String.format(
        "\n=== GAME OVER ===\nPlayer : %s\nScore  : %d\nRank   : %s\n",
        player.getName(), score, rank);
  }

  @Override
  public String save() {
    try {
      saveManager.save(player, world);
      return "Game saved successfully.";
    } catch (IOException exception) {
      return "Failed to save game: " + exception.getMessage();
    }
  }

  @Override
  public String restore() {
    try {
      player = saveManager.restore(world);
      return "Game restored.\n" + look();
    } catch (IOException exception) {
      return "Failed to restore game: " + exception.getMessage();
    }
  }

  private Room currentRoom() {
    return world.getRoom(player.getCurrentRoomNumber());
  }

  private String describeRoom(Room room) {
    StringBuilder description = new StringBuilder();
    description.append("You are in: ").append(room.getRoomName().toUpperCase()).append("\n");

    Puzzle puzzle = room.getPuzzle();
    Monster monster = room.getMonster();
    if (puzzle != null && puzzle.isActive()) {
      description.append(puzzle.getEffects()).append("\n");
    } else if (monster != null && monster.isActive()) {
      description
          .append(monster.getEffects())
          .append("\nA monster ")
          .append(monster.getName())
          .append(" blocks your way!\n");
    } else {
      description.append(room.getDescription()).append("\n");
    }

    List<Item> items = room.getItems();
    if (!items.isEmpty()) {
      description.append("Items here: ");
      for (int index = 0; index < items.size(); index++) {
        if (index > 0) {
          description.append(", ");
        }
        Item item = items.get(index);
        description
            .append(item.getName().toUpperCase())
            .append(" (weight: ")
            .append(item.getWeight())
            .append(")");
      }
      description.append("\n");
    }

    return description.toString();
  }

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

  private String monsterAttack(Monster monster) {
    int damage = Math.abs(monster.getDamage());
    player.takeDamage(damage);
    return monster.getName().toUpperCase()
        + " attacks! "
        + monster.getAttack()
        + "\nYou take "
        + damage
        + " damage!"
        + "\nHealth: "
        + player.getHealth()
        + " ["
        + player.getHealthStatus()
        + "]";
  }

  private void openBlockedExits(Room room) {
    for (Direction direction : Direction.values()) {
      int exit = room.getExit(direction);
      if (exit < 0) {
        room.setExit(direction, Math.abs(exit));
      }
    }
  }

  private Item findItemInList(List<Item> items, String name) {
    if (items == null || name == null) {
      return null;
    }
    for (Item item : items) {
      if (item.getName().equalsIgnoreCase(name)) {
        return item;
      }
    }
    return null;
  }
}
