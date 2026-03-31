package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Item class.
 */
public class ItemTest {

  private Item sword;
  private Item potion;
  private Item ticket;

  @BeforeEach
  void setUp() {
    sword = new Item("Sword", 5, 10, 10, 15,
        "You swing the sword!", "A sharp blade.", "sword.png");
    potion = new Item("Potion", 2, 1, 1, 10,
        "You drink the potion.", "A healing potion.", null);
    ticket = new Item("Ticket", 1, 1, 0, 5,
        "You use the ticket.", "A museum ticket.", null);
  }

  // ── Constructor validation ──────────────────────────────────────────

  @Test
  void testConstructorNullName() {
    assertThrows(IllegalArgumentException.class, () ->
        new Item(null, 1, 1, 1, 0, "msg", "desc", null));
  }

  @Test
  void testConstructorBlankName() {
    assertThrows(IllegalArgumentException.class, () ->
        new Item("  ", 1, 1, 1, 0, "msg", "desc", null));
  }

  @Test
  void testConstructorNegativeWeight() {
    assertThrows(IllegalArgumentException.class, () ->
        new Item("Key", -1, 1, 1, 0, "msg", "desc", null));
  }

  @Test
  void testConstructorNegativeMaxUses() {
    assertThrows(IllegalArgumentException.class, () ->
        new Item("Key", 1, -1, 0, 0, "msg", "desc", null));
  }

  @Test
  void testConstructorUsesRemainingTooHigh() {
    assertThrows(IllegalArgumentException.class, () ->
        new Item("Key", 1, 3, 5, 0, "msg", "desc", null));
  }

  @Test
  void testConstructorUsesRemainingNegative() {
    assertThrows(IllegalArgumentException.class, () ->
        new Item("Key", 1, 3, -1, 0, "msg", "desc", null));
  }

  @Test
  void testConstructorValidZeroWeight() {
    Item item = new Item("Feather", 0, 1, 1, 1, "msg", "desc", null);
    assertEquals(0, item.getWeight());
  }

  // ── Getters ─────────────────────────────────────────────────────────

  @Test
  void testGetName() {
    assertEquals("Sword", sword.getName());
  }

  @Test
  void testGetWeight() {
    assertEquals(5, sword.getWeight());
  }

  @Test
  void testGetMaxUses() {
    assertEquals(10, sword.getMaxUses());
  }

  @Test
  void testGetUsesRemaining() {
    assertEquals(10, sword.getUsesRemaining());
  }

  @Test
  void testGetValue() {
    assertEquals(15, sword.getValue());
  }

  @Test
  void testGetWhenUsed() {
    assertEquals("You swing the sword!", sword.getWhenUsed());
  }

  @Test
  void testGetDescription() {
    assertEquals("A sharp blade.", sword.getDescription());
  }

  @Test
  void testGetPictureWithValue() {
    assertEquals("sword.png", sword.getPicture());
  }

  @Test
  void testGetPictureNull() {
    assertNull(potion.getPicture());
  }

  // ── isUsable ────────────────────────────────────────────────────────

  @Test
  void testIsUsableTrue() {
    assertTrue(sword.isUsable());
  }

  @Test
  void testIsUsableFalseWhenZeroUses() {
    assertFalse(ticket.isUsable());
  }

  // ── use ─────────────────────────────────────────────────────────────

  @Test
  void testUseReturnsWhenUsedText() {
    assertEquals("You swing the sword!", sword.use());
  }

  @Test
  void testUseDecrementsRemaining() {
    sword.use();
    assertEquals(9, sword.getUsesRemaining());
  }

  @Test
  void testUseMultipleTimes() {
    for (int i = 0; i < 10; i++) {
      sword.use();
    }
    assertEquals(0, sword.getUsesRemaining());
    assertFalse(sword.isUsable());
  }

  @Test
  void testUseSingleUseItem() {
    assertEquals("You drink the potion.", potion.use());
    assertEquals(0, potion.getUsesRemaining());
    assertFalse(potion.isUsable());
  }

  @Test
  void testUseWhenExhausted() {
    String result = ticket.use();
    assertEquals("Ticket has no uses remaining.", result);
    assertEquals(0, ticket.getUsesRemaining());
  }

  @Test
  void testUseExhaustedAfterDepleting() {
    potion.use();
    String result = potion.use();
    assertEquals("Potion has no uses remaining.", result);
  }

  // ── setUsesRemaining ────────────────────────────────────────────────

  @Test
  void testSetUsesRemainingValid() {
    sword.setUsesRemaining(5);
    assertEquals(5, sword.getUsesRemaining());
  }

  @Test
  void testSetUsesRemainingToZero() {
    sword.setUsesRemaining(0);
    assertEquals(0, sword.getUsesRemaining());
    assertFalse(sword.isUsable());
  }

  @Test
  void testSetUsesRemainingToMax() {
    sword.use();
    sword.setUsesRemaining(10);
    assertEquals(10, sword.getUsesRemaining());
  }

  @Test
  void testSetUsesRemainingNegative() {
    assertThrows(IllegalArgumentException.class, () ->
        sword.setUsesRemaining(-1));
  }

  @Test
  void testSetUsesRemainingAboveMax() {
    assertThrows(IllegalArgumentException.class, () ->
        sword.setUsesRemaining(11));
  }

  // ── toString ────────────────────────────────────────────────────────

  @Test
  void testToString() {
    assertEquals("SWORD", sword.toString());
  }

  @Test
  void testToStringLowerCase() {
    Item item = new Item("golden key", 1, 1, 1, 5,
        "msg", "desc", null);
    assertEquals("GOLDEN KEY", item.toString());
  }
}
