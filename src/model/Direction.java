package model;

public enum Direction {
	NORTH, SOUTH, EAST, WEST;

	/**
	 * Parses a string into a Direction enum value.
	 *
	 * @param input the string to parse
	 * @return the corresponding Direction
	 * @throws IllegalArgumentException if the input is not a valid direction
	 */
	public static Direction fromString(String input) {
		if (input == null || input.isBlank()) {
			throw new IllegalArgumentException("Direction cannot be null or blank.");
		}
		switch (input.trim().toUpperCase()) {
			case "NORTH":
				return NORTH;
			case "SOUTH":
				return SOUTH;
			case "EAST":
				return EAST;
			case "WEST":
				return WEST;
			case "N":
				return NORTH;
			case "S":
				return SOUTH;
			case "E":
				return EAST;
			case "W":
				return WEST;
			default:
				throw new IllegalArgumentException(
						"Invalid direction: " + input);
		}
	}
}