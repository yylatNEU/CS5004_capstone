/**
 * Represents the health status of a player.
 * Health ranges from 0 (incapacitated) to 100 (full health).
 */
package model;

/**
 * Represents the health status of a player based on current health value.
 * Each status carries a display message for the game controller to show.
 */
public enum HealthStatus {
  AWAKE("You feel strong and ready."),
  FATIGUED("You feel tired and worn down."),
  WOOZY("You are barely standing..."),
  SLEEP("You have fallen unconscious. Game over.");

  private final String displayMessage;

  HealthStatus(String displayMessage) {
    this.displayMessage = displayMessage;
  }

  public String getDisplayMessage() {
    return displayMessage;
  }

  /**
   * Returns the HealthStatus corresponding to the given health value.
   * @param health current health (0–100)
   * @return the matching HealthStatus
   */
  public static HealthStatus fromHealth(int health) {
    if (health <= 0)  return SLEEP;
    if (health <= 39) return WOOZY;
    if (health <= 69) return FATIGUED;
    return AWAKE;
  }
}