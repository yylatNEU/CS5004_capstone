package model;

/**
 * Represents an item in the adventure game world.
 * Items can be picked up, dropped, examined, and used by the player.
 * Each item has a limited number of uses and a weight that counts
 * toward the player's carry limit.
 */
public class Item {

  private final String name;
  private final int weight;
  private final int maxUses;
  private int usesRemaining;
  private final int value;
  private final String whenUsed;
  private final String description;
  private final String picture;

  /**
   * Constructs an Item with the given attributes.
   *
   * @param name          the name of this item
   * @param weight        how much this item weighs (must be non-negative)
   * @param maxUses       the maximum number of times this item can be used
   * @param usesRemaining the current remaining uses (0 to maxUses)
   * @param value         the point value of this item for scoring
   * @param whenUsed      the text displayed when the item is successfully used
   * @param description   the text displayed when the item is examined
   * @param picture       the filename of an associated image, or null
   * @throws IllegalArgumentException if name is null or blank, weight is negative,
   *     maxUses is negative, or usesRemaining is out of range
   */
  public Item(String name, int weight, int maxUses, int usesRemaining,
      int value, String whenUsed, String description, String picture) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Item name cannot be null or blank.");
    }
    if (weight < 0) {
      throw new IllegalArgumentException("Weight cannot be negative.");
    }
    if (maxUses < 0) {
      throw new IllegalArgumentException("Max uses cannot be negative.");
    }
    if (usesRemaining < 0 || usesRemaining > maxUses) {
      throw new IllegalArgumentException(
          "Uses remaining must be between 0 and maxUses.");
    }
    this.name = name;
    this.weight = weight;
    this.maxUses = maxUses;
    this.usesRemaining = usesRemaining;
    this.value = value;
    this.whenUsed = whenUsed;
    this.description = description;
    this.picture = picture;
  }

  /**
   * Returns the name of this item.
   *
   * @return the item name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the weight of this item.
   *
   * @return the weight
   */
  public int getWeight() {
    return weight;
  }

  /**
   * Returns the maximum number of times this item can be used.
   *
   * @return the max uses
   */
  public int getMaxUses() {
    return maxUses;
  }

  /**
   * Returns how many uses this item has remaining.
   *
   * @return the remaining uses
   */
  public int getUsesRemaining() {
    return usesRemaining;
  }

  /**
   * Returns the point value of this item for scoring purposes.
   *
   * @return the value
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the text displayed when this item is successfully used.
   *
   * @return the when-used text
   */
  public String getWhenUsed() {
    return whenUsed;
  }

  /**
   * Returns the description shown when this item is examined.
   *
   * @return the description text
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the picture filename associated with this item, or null.
   *
   * @return the picture filename, or null if none
   */
  public String getPicture() {
    return picture;
  }

  /**
   * Checks whether this item can still be used (has remaining uses).
   *
   * @return true if usesRemaining is greater than zero
   */
  public boolean isUsable() {
    return usesRemaining > 0;
  }

  /**
   * Uses this item once, decrementing the remaining uses.
   * If the item still has uses, returns the when-used text.
   * If the item is exhausted, returns a message indicating it cannot be used.
   *
   * @return the result text of attempting to use this item
   */
  public String use() {
    if (usesRemaining <= 0) {
      return name + " has no uses remaining.";
    }
    usesRemaining--;
    return whenUsed;
  }

  /**
   * Returns the item name in uppercase for display purposes.
   *
   * @return the uppercase item name
   */
  @Override
  public String toString() {
    return name.toUpperCase();
  }
}
