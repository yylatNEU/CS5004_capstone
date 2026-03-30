package model;

/**
 * Models a player in the adventure game, including stats and inventory.
 */
public class Player {

  private static final int MAX_HEALTH = 100;

  private final String playerName;
  private int health;
  private int totalScore;
  private int roomNumber;
  private final Inventory bag;

  /**
   * Creates a player with a name and starting room.
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
   */
  public void takeDamage(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Damage cannot be negative.");
    }

    health = Math.max(0, health - amount);
  }

  /**
   * Adds points to the player's score.
   */
  public void addScore(int value) {
    totalScore += value;
  }

  /**
   * Fully heals the player.
   */
  public void restoreHealth() {
    health = MAX_HEALTH;
  }

  /**
   * Gets health status enum.
   */
  public HealthStatus getHealthStatus() {
    return HealthStatus.fromHealth(health);
  }

  // ===== Getters / Setters =====
  public String getName() {
    return playerName;
  }

  public int getHealth() {
    return health;
  }

  public int getScore() {
    return totalScore;
  }

  public int getCurrentRoomNumber() {
    return roomNumber;
  }

  public void setCurrentRoomNumber(int newRoom) {
    this.roomNumber = newRoom;
  }

  public Inventory getInventory() {
    return bag;
  }

  /**
   * Returns a summary of player state.
   */
  @Override
  public String toString() {
    return String.format(
        "Player[name=%s, health=%d, score=%d, status=%s, room=%d]",
        playerName,
        health,
        totalScore,
        getHealthStatus(),
        roomNumber
    );
  }

  /**
   * Helper method to validate player name.
   */
  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Player name cannot be null or blank.");
    }
  }
}
