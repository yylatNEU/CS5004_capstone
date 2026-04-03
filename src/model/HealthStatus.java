package model;

/** Represents the player's coarse-grained health state. */
public enum HealthStatus {
  AWAKE("You feel strong and ready."),
  FATIGUED("You feel tired and worn down."),
  WOOZY("You are barely standing..."),
  SLEEP("You have fallen unconscious. Game over.");

  private final String displayMessage;

  HealthStatus(String displayMessage) {
    this.displayMessage = displayMessage;
  }

  /**
   * Returns the display text associated with this status.
   *
   * @return the user-facing status message
   */
  public String getDisplayMessage() {
    return displayMessage;
  }

  /**
   * Maps a numeric health value to a status.
   *
   * @param health the current health value
   * @return the matching status
   */
  public static HealthStatus fromHealth(int health) {
    if (health <= 0) {
      return SLEEP;
    }
    if (health <= 39) {
      return WOOZY;
    }
    if (health <= 69) {
      return FATIGUED;
    }
    return AWAKE;
  }
}
