package model;

/**
 * Represents a fixture in the adventure game world.
 * Fixtures are heavy, immovable objects found in rooms that can be examined
 * but not picked up by the player. Examples include desks, bookshelves,
 * and computers.
 */
public class Fixture {

  private static final int MOVABLE_WEIGHT_THRESHOLD = 200;

  private final String name;
  private final int weight;
  private final String description;

  /**
   * Constructs a Fixture with the given attributes.
   *
   * @param name        the name of this fixture
   * @param weight      how much this fixture weighs (typically 1000+)
   * @param description the text displayed when the fixture is examined
   * @throws IllegalArgumentException if name is null or blank,
   *     or weight is negative
   */
  public Fixture(String name, int weight, String description) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException(
          "Fixture name cannot be null or blank.");
    }
    if (weight < 0) {
      throw new IllegalArgumentException("Weight cannot be negative.");
    }
    this.name = name;
    this.weight = weight;
    this.description = description;
  }

  /**
   * Returns the name of this fixture.
   *
   * @return the fixture name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the weight of this fixture.
   *
   * @return the weight
   */
  public int getWeight() {
    return weight;
  }

  /**
   * Returns the description shown when this fixture is examined.
   *
   * @return the description text
   */
  public String getDescription() {
    return description;
  }

  /**
   * Checks whether this fixture can be moved by the player.
   * Fixtures weighing 200 or more are considered immovable.
   *
   * @return true if the fixture weighs less than 200
   */
  public boolean isMovable() {
    return weight < MOVABLE_WEIGHT_THRESHOLD;
  }

  /**
   * Returns the fixture name in uppercase for display purposes.
   *
   * @return the uppercase fixture name
   */
  @Override
  public String toString() {
    return name.toUpperCase();
  }
}
