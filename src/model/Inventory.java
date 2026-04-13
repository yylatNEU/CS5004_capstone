package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Represents a player's inventory with a fixed carry capacity. */
public class Inventory {

  private static final double MAX_CAPACITY = 13.0;

  private final List<Item> storage;
  private double totalWeight;

  /** Creates an empty inventory. */
  public Inventory() {
    this.storage = new ArrayList<>();
    this.totalWeight = 0.0;
  }

  /**
   * Returns whether an item can be added without exceeding capacity.
   *
   * @param item the item to check
   * @return {@code true} if the item fits
   */
  public boolean canAdd(Item item) {
    if (item == null) {
      return false;
    }
    return totalWeight + item.getWeight() <= MAX_CAPACITY;
  }

  /**
   * Adds an item if capacity allows.
   *
   * @param item the item to add
   * @return {@code true} if the item was added
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
   * Returns the remaining uses for a named item.
   *
   * @param name the item name
   * @return the remaining uses, or {@code -1} if not present
   */
  public int getItemUses(String name) {
    int index = findItemIndex(name);
    if (index < 0) {
      return -1;
    }
    return storage.get(index).getUsesRemaining();
  }

  /**
   * Removes the first matching item by name.
   *
   * @param name the item name
   * @return {@code true} if an item was removed
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
   * Returns the first matching item by name.
   *
   * @param name the item name
   * @return the matching item, or {@code null}
   */
  public Item getItem(String name) {
    int index = findItemIndex(name);
    return index >= 0 ? storage.get(index) : null;
  }

  /**
   * Returns a formatted inventory summary.
   *
   * @return the inventory text
   */
  public String listItems() {
    if (storage.isEmpty()) {
      return "Your inventory is empty.";
    }

    StringBuilder result = new StringBuilder();
    result.append("Inventory:\n");
    for (Item item : storage) {
      result
          .append("  - ")
          .append(item.getName())
          .append(" (weight: ")
          .append(item.getWeight())
          .append(", uses: ")
          .append(item.getUsesRemaining())
          .append(")\n");
    }
    result.append(String.format("Total weight: %d / %d", (int) totalWeight, (int) MAX_CAPACITY));
    return result.toString();
  }

  /**
   * Returns the current total carried weight.
   *
   * @return the weight value
   */
  public double getCurrentWeight() {
    return totalWeight;
  }

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

  /**
   * Returns an unmodifiable view of all items currently in the inventory.
   * @return an unmodifiable list of items
   */
  public List<Item> getItems() {
    return Collections.unmodifiableList(storage);
  }
}
