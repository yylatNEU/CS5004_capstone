package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player's inventory with a weight constraint.
 */
public class Inventory {

  private static final double MAX_CAPACITY = 13.0;

  private final List<Item> storage;
  private double totalWeight;

  /**
   * Initializes an empty inventory.
   */
  public Inventory() {
    this.storage = new ArrayList<>();
    this.totalWeight = 0.0;
  }

  /**
   * Determines if an item can be stored without exceeding capacity.
   */
  public boolean canAdd(Item item) {
    if (item == null) {
      return false;
    }
    return totalWeight + item.getWeight() <= MAX_CAPACITY;
  }

  /**
   * Attempts to store an item.
   */
  public boolean addItem(Item item) {
    if (!canAdd(item)) {
      return false;
    }
    storage.add(item);
    totalWeight += item.getWeight();
    return true;
  }

  /**
   * Removes the first matching item by name (case-insensitive).
   */
  public boolean removeItem(String name) {
    int index = findItemIndex(name);
    if (index < 0) {
      return false;
    }

    Item removed = storage.remove(index);
    totalWeight -= removed.getWeight();
    return true;
  }

  /**
   * Finds an item by name without removing it.
   */
  public Item getItem(String name) {
    int index = findItemIndex(name);
    return (index >= 0) ? storage.get(index) : null;
  }

  /**
   * Returns a formatted inventory summary.
   */
  public String listItems() {
    if (storage.isEmpty()) {
      return "Your inventory is empty.";
    }

    StringBuilder result = new StringBuilder();
    result.append("Inventory:\n");

    for (int i = 0; i < storage.size(); i++) {
      Item it = storage.get(i);
      result.append("  - ")
          .append(it.getName())
          .append(" (weight: ")
          .append(it.getWeight())
          .append(", uses: ")
          .append(it.getUsesRemaining())
          .append(")\n");
    }

    result.append(String.format(
        "Total weight: %d / %d",
        (int) totalWeight, (int) MAX_CAPACITY));

    return result.toString();
  }

  /**
   * Returns current total weight.
   */
  public double getCurrentWeight() {
    return totalWeight;
  }

  /**
   * Helper: finds index of item by name.
   */
  private int findItemIndex(String name) {
    if (name == null) {
      return -1;
    }

    for (int i = 0; i < storage.size(); i++) {
      if (storage.get(i).getName().equalsIgnoreCase(name)) {
        return i;
      }
    }
    return -1;
  }
}
