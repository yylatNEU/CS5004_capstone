package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Fixture class.
 */
public class FixtureTest {

  private Fixture computer;
  private Fixture desk;
  private Fixture lightChair;

  @BeforeEach
  void setUp() {
    computer = new Fixture("Computer", 1000,
        "A computer with a password screen.", "Password",
        null, "computer.png");
    desk = new Fixture("Desk", 500,
        "A large wooden desk.", null, null, null);
    lightChair = new Fixture("Chair", 50,
        "A small wooden chair.", null, null, null);
  }

  // ── Constructor validation ──────────────────────────────────────────

  @Test
  void testConstructorNullName() {
    assertThrows(IllegalArgumentException.class, () ->
        new Fixture(null, 100, "desc", null, null, null));
  }

  @Test
  void testConstructorBlankName() {
    assertThrows(IllegalArgumentException.class, () ->
        new Fixture("  ", 100, "desc", null, null, null));
  }

  @Test
  void testConstructorNegativeWeight() {
    assertThrows(IllegalArgumentException.class, () ->
        new Fixture("Table", -1, "desc", null, null, null));
  }

  @Test
  void testConstructorValidZeroWeight() {
    Fixture f = new Fixture("Ghost", 0, "desc", null, null, null);
    assertEquals(0, f.getWeight());
  }

  // ── Getters ─────────────────────────────────────────────────────────

  @Test
  void testGetName() {
    assertEquals("Computer", computer.getName());
  }

  @Test
  void testGetWeight() {
    assertEquals(1000, computer.getWeight());
  }

  @Test
  void testGetDescription() {
    assertEquals("A computer with a password screen.",
        computer.getDescription());
  }

  @Test
  void testGetPuzzleWithValue() {
    assertEquals("Password", computer.getPuzzle());
  }

  @Test
  void testGetPuzzleNull() {
    assertNull(desk.getPuzzle());
  }

  @Test
  void testGetStatesNull() {
    assertNull(computer.getStates());
  }

  @Test
  void testGetStatesWithValue() {
    Fixture f = new Fixture("Lever", 300, "A lever.",
        null, "on,off", null);
    assertEquals("on,off", f.getStates());
  }

  @Test
  void testGetPictureWithValue() {
    assertEquals("computer.png", computer.getPicture());
  }

  @Test
  void testGetPictureNull() {
    assertNull(desk.getPicture());
  }

  // ── isMovable ───────────────────────────────────────────────────────

  @Test
  void testIsMovableFalseHeavy() {
    assertFalse(computer.isMovable());
  }

  @Test
  void testIsMovableFalseAtThreshold() {
    Fixture f = new Fixture("Cabinet", 200, "desc",
        null, null, null);
    assertFalse(f.isMovable());
  }

  @Test
  void testIsMovableTrueLight() {
    assertTrue(lightChair.isMovable());
  }

  @Test
  void testIsMovableTrueJustBelow() {
    Fixture f = new Fixture("Stool", 199, "desc",
        null, null, null);
    assertTrue(f.isMovable());
  }

  // ── toString ────────────────────────────────────────────────────────

  @Test
  void testToString() {
    assertEquals("COMPUTER", computer.toString());
  }

  @Test
  void testToStringLowerCase() {
    Fixture f = new Fixture("big bookshelf", 800, "desc",
        null, null, null);
    assertEquals("BIG BOOKSHELF", f.toString());
  }
}
