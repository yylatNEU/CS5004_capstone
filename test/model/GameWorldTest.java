package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link GameWorld}. */
class GameWorldTest {

  @Test
  void worldStoresAndReturnsRegisteredObjectsCaseInsensitively() {
    GameWorld world = TestWorldFactory.createPersistenceWorld();

    assertEquals("Persistence Game", world.getGameName());
    assertEquals("1.0", world.getVersion());
    assertEquals(2, world.getRoomCount());
    assertEquals(1, world.getStartingRoom().getRoomNumber());
    assertEquals("Key", world.getItem("key").getName());
    assertEquals("Lock", world.getPuzzle("lock").getName());
    assertEquals("Robot", world.getMonster("robot").getName());
  }

  @Test
  void gettersReturnNullForUnknownNames() {
    GameWorld world = new GameWorld("Test", "1.0");

    assertNull(world.getItem(null));
    assertNull(world.getFixture("missing"));
    assertNull(world.getPuzzle("missing"));
    assertNull(world.getMonster("missing"));
    assertNull(world.getRoom(99));
  }

  @Test
  void toStringSummarizesWorldContents() {
    GameWorld world = TestWorldFactory.createPersistenceWorld();
    String summary = world.toString();

    assertTrue(summary.contains("rooms=2"));
    assertTrue(summary.contains("items=2"));
    assertTrue(summary.contains("puzzles=1"));
    assertTrue(summary.contains("monsters=1"));
  }
}
