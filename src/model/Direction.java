package model;

/** Enumerates the supported movement directions in the game. */
public enum Direction {
  NORTH,
  SOUTH,
  EAST,
  WEST;

  /**
   * Parses a user-facing direction string.
   *
   * @param input the direction text to parse
   * @return the matching direction
   * @throws IllegalArgumentException if the input is null, blank, or invalid
   */
  public static Direction fromString(String input) {
    if (input == null || input.isBlank()) {
      throw new IllegalArgumentException("Direction cannot be null or blank.");
    }

    switch (input.trim().toUpperCase()) {
      case "NORTH":
      case "N":
        return NORTH;
      case "SOUTH":
      case "S":
        return SOUTH;
      case "EAST":
      case "E":
        return EAST;
      case "WEST":
      case "W":
        return WEST;
      default:
        throw new IllegalArgumentException("Invalid direction: " + input);
    }
  }
}
