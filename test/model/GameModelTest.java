package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameModel.
 *
 * Covers:
 *   - Movement (valid / invalid / wall)
 *   - Room description (look)
 *   - Item interactions (take, drop, use)
 *   - Examine behavior
 *   - Game state (game over, summary)
 */
class GameModelTest {

  private GameModel model;

  @BeforeEach
  void setup() {
    GameWorld world = TestWorldFactory.createSimpleWorld();
    model = new GameModel(world);
    model.setPlayerName("Tester");
  }

  // ─────────────────────────────────────────────
  // move()
  // ─────────────────────────────────────────────

  @Test
  void testMoveInvalidDirection() {
    String result = model.move("invalid");
    assertEquals("Invalid direction.", result);
  }

  @Test
  void testMoveWall() {
    String result = model.move("north"); // defined as wall (0)
    assertTrue(result.contains("wall"));
  }

  @Test
  void testMoveSuccessChangesRoom() {
    String result = model.move("east"); // goes to room 2

    assertTrue(result.contains("You are in"));
    assertTrue(result.contains("SECOND ROOM"));
  }

  // ─────────────────────────────────────────────
  // look()
  // ─────────────────────────────────────────────

  @Test
  void testLookContainsPlayerInfo() {
    String result = model.look();

    assertTrue(result.contains("Health"));
    assertTrue(result.contains("Weight"));
    assertTrue(result.contains("You are in"));
  }

  @Test
  void testLookDoesNotChangeHealth() {
    String before = model.look();
    String after = model.look();

    assertEquals(before, after);
  }

  // ─────────────────────────────────────────────
  // takeItem()
  // ─────────────────────────────────────────────

  @Test
  void testTakeItemSuccess() {
    String result = model.takeItem("Key");

    assertTrue(result.contains("pick up Key"));
  }

  @Test
  void testTakeItemNotFound() {
    String result = model.takeItem("Ghost");

    assertTrue(result.contains("don't see"));
  }

  @Test
  void testTakeItemRemovesFromRoom() {
    model.takeItem("Key");

    String result = model.takeItem("Key");

    assertTrue(
        result.contains("no items") || result.contains("don't see")
    );
  }

  // ─────────────────────────────────────────────
  // dropItem()
  // ─────────────────────────────────────────────

  @Test
  void testDropItemSuccess() {
    model.takeItem("Key");

    String result = model.dropItem("Key");

    assertTrue(result.contains("drop Key"));
  }

  @Test
  void testDropItemNotInInventory() {
    String result = model.dropItem("Nothing");

    assertTrue(result.contains("don't have"));
  }

  // ─────────────────────────────────────────────
  // useItem()
  // ─────────────────────────────────────────────

  @Test
  void testUseItemSuccess() {
    model.takeItem("Key");

    String result = model.useItem("Key");

    assertTrue(result.contains("used"));
  }

  @Test
  void testUseItemNotFound() {
    String result = model.useItem("Fake");

    assertTrue(result.contains("don't have"));
  }

  @Test
  void testUseItemDecreasesUses() {
    model.takeItem("Key");

    String result = model.useItem("Key");

    assertTrue(result.contains("uses remaining"));
  }

  @Test
  void testUseItemEventuallyRemoved() {
    model.takeItem("Key");

    // uses = 3 → use 3 times
    model.useItem("Key");
    model.useItem("Key");
    String result = model.useItem("Key");

    assertTrue(result.contains("broken"));
  }

  // ─────────────────────────────────────────────
  // examine()
  // ─────────────────────────────────────────────

  @Test
  void testExamineInventoryItem() {
    model.takeItem("Key");

    String result = model.examine("Key");

    assertTrue(result.contains("Key"));
    assertTrue(result.contains("A small key"));
  }

  @Test
  void testExamineRoomItem() {
    String result = model.examine("Key");

    assertTrue(result.contains("Key"));
  }

  @Test
  void testExamineNotFound() {
    String result = model.examine("Ghost");

    assertTrue(result.contains("don't see"));
  }

  // ─────────────────────────────────────────────
  // inventory string
  // ─────────────────────────────────────────────

  @Test
  void testGetInventoryStringEmpty() {
    String result = model.getInventoryString();

    assertTrue(result.contains("empty"));
  }

  @Test
  void testGetInventoryStringAfterPickup() {
    model.takeItem("Key");

    String result = model.getInventoryString();

    assertTrue(result.contains("Key"));
  }

  // ─────────────────────────────────────────────
  // game state
  // ─────────────────────────────────────────────

  @Test
  void testGameNotOverInitially() {
    assertFalse(model.isGameOver());
  }

  @Test
  void testEndGameSummaryFormat() {
    String result = model.getEndGameSummary();

    assertTrue(result.contains("GAME OVER"));
    assertTrue(result.contains("Score"));
    assertTrue(result.contains("Rank"));
  }
}