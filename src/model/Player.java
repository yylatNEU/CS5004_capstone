package model;

/** Models a player in the adventure game, including health, score, and inventory. */
public class Player {

  private static final int MAX_HEALTH = 100;

  private final String playerName;
  private int health;
  private int totalScore;
  private int roomNumber;
  private final Inventory bag;

  /**
   * Creates a player with a name and starting room.
   *
   * @param name the player name
   * @param startingRoom the initial room number
   */
  public Player(String name, int startingRoom) {
    validateName(name);
    this.playerName = name;
    this.health = MAX_HEALTH;
    this.totalScore = 0;
    this.roomNumber = startingRoom;
    this.bag = new Inventory();
  }

  /**
   * Applies damage to the player.
   *
   * @param amount the non-negative damage amount
   */
  public void takeDamage(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Damage cannot be negative.");
    }
    health = Math.max(0, health - amount);
  }

  /**
   * Adds points to the player's score.
   *
   * @param value the score delta
   */
  public void addScore(int value) {
    totalScore += value;
  }

  /** Restores the player to full health. */
  public void restoreHealth() {
    health = MAX_HEALTH;
  }

  /**
   * Returns the player's current health status.
   *
   * @return the health status
   */
  public HealthStatus getHealthStatus() {
    return HealthStatus.fromHealth(health);
  }

  /**
   * Returns the player name.
   *
   * @return the player name
   */
  public String getName() {
    return playerName;
  }

  /**
   * Returns the current health.
   *
   * @return the health value
   */
  public int getHealth() {
    return health;
  }

  /**
   * Returns the current score.
   *
   * @return the score
   */
  public int getScore() {
    return totalScore;
  }

  /**
   * Returns the current room number.
   *
   * @return the room number
   */
  public int getCurrentRoomNumber() {
    return roomNumber;
  }

  /**
   * Updates the current room number.
   *
   * @param newRoom the new room number
   */
  public void setCurrentRoomNumber(int newRoom) {
    this.roomNumber = newRoom;
  }

  /**
   * Returns the player's inventory.
   *
   * @return the inventory
   */
  public Inventory getInventory() {
    return bag;
  }

  @Override
  public String toString() {
    return String.format(
        "Player[name=%s, health=%d, score=%d, status=%s, room=%d]",
        playerName, health, totalScore, getHealthStatus(), roomNumber);
  }

  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Player name cannot be null or blank.");
    }
  }
}
