package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for Inventory.
 *
 * <p>Verifies: - Item addition and removal - Weight constraints (max capacity = 13) - Item lookup
 * behavior - Inventory display formatting - Edge cases (null, empty, overflow)
 */
class InventoryTest {

  // Helper method to create test items
  private Item makeItem(String name, int weight, int uses) {
    return new Item(
        name,
        weight,
        uses, // maxUses
        uses, // usesRemaining
        0, // value (irrelevant for Inventory)
        "used", // whenUsed
        "test item", // description
        null // picture
        );
  }

  // ─────────────────────────────────────────────
  // ✅ addItem / canAdd
  // ─────────────────────────────────────────────

  @Test
  void testAddItemSuccess() {
    Inventory inv = new Inventory();
    Item item = makeItem("Sword", 5, 3);

    assertTrue(inv.addItem(item));
    assertEquals(5.0, inv.getCurrentWeight());
  }

  @Test
  void testCanAddValidItem() {
    Inventory inv = new Inventory();
    Item item = makeItem("Shield", 10, 1);

    assertTrue(inv.canAdd(item));
  }

  @Test
  void testAddItemNull() {
    Inventory inv = new Inventory();

    assertFalse(inv.canAdd(null));
    assertFalse(inv.addItem(null));
  }

  // ─────────────────────────────────────────────
  // ⚠️ Weight limit
  // ─────────────────────────────────────────────

  @Test
  void testAddItemExceedsCapacity() {
    Inventory inv = new Inventory();

    inv.addItem(makeItem("Heavy1", 10, 1));
    boolean result = inv.addItem(makeItem("Heavy2", 5, 1));

    assertFalse(result); // exceeds 13
  }

  @Test
  void testAddItemExactCapacity() {
    Inventory inv = new Inventory();

    assertTrue(inv.addItem(makeItem("Exact", 13, 1)));
    assertEquals(13, inv.getCurrentWeight());
  }

  // ─────────────────────────────────────────────
  // ✅ removeItem
  // ─────────────────────────────────────────────

  @Test
  void testRemoveItemSuccess() {
    Inventory inv = new Inventory();
    inv.addItem(makeItem("Potion", 2, 2));

    boolean removed = inv.removeItem("Potion");

    assertTrue(removed);
    assertEquals(0, inv.getCurrentWeight());
  }

  @Test
  void testRemoveItemNotFound() {
    Inventory inv = new Inventory();

    assertFalse(inv.removeItem("NotExist"));
  }

  @Test
  void testRemoveItemCaseInsensitive() {
    Inventory inv = new Inventory();
    inv.addItem(makeItem("Key", 1, 1));

    assertTrue(inv.removeItem("key")); // lowercase
  }

  // ─────────────────────────────────────────────
  // ✅ getItem
  // ─────────────────────────────────────────────

  @Test
  void testGetItemSuccess() {
    Inventory inv = new Inventory();
    Item item = makeItem("Map", 1, 1);

    inv.addItem(item);

    assertNotNull(inv.getItem("Map"));
  }

  @Test
  void testGetItemCaseInsensitive() {
    Inventory inv = new Inventory();
    inv.addItem(makeItem("Lantern", 2, 1));

    assertNotNull(inv.getItem("lantern"));
  }

  @Test
  void testGetItemNotFound() {
    Inventory inv = new Inventory();

    assertNull(inv.getItem("Nothing"));
  }

  // ─────────────────────────────────────────────
  // ✅ listItems
  // ─────────────────────────────────────────────

  @Test
  void testListItemsEmpty() {
    Inventory inv = new Inventory();

    String result = inv.listItems();

    assertEquals("Your inventory is empty.", result);
  }

  @Test
  void testListItemsContent() {
    Inventory inv = new Inventory();

    inv.addItem(makeItem("Sword", 5, 3));
    inv.addItem(makeItem("Shield", 3, 1));

    String result = inv.listItems();

    assertTrue(result.contains("Sword"));
    assertTrue(result.contains("Shield"));
    assertTrue(result.contains("weight: 5"));
    assertTrue(result.contains("uses: 3"));
  }

  @Test
  void testListItemsWeightFormatting() {
    Inventory inv = new Inventory();

    inv.addItem(makeItem("A", 5, 1));
    inv.addItem(makeItem("B", 3, 1));

    String result = inv.listItems();

    assertTrue(result.contains("Total weight: 8 / 13"));
  }

  // ─────────────────────────────────────────────
  // ⚠️ Weight tracking consistency
  // ─────────────────────────────────────────────

  @Test
  void testWeightAfterAddAndRemove() {
    Inventory inv = new Inventory();

    Item a = makeItem("A", 4, 1);
    Item b = makeItem("B", 3, 1);

    inv.addItem(a);
    inv.addItem(b);

    assertEquals(7, inv.getCurrentWeight());

    inv.removeItem("A");

    assertEquals(3, inv.getCurrentWeight());
  }

  @Test
  void testRemoveNullName() {
    Inventory inv = new Inventory();

    assertFalse(inv.removeItem(null));
  }

  @Test
  void testGetItemNullName() {
    Inventory inv = new Inventory();

    assertNull(inv.getItem(null));
  }
}
