package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Room.
 */
class RoomTest {

  private Room room;

  @BeforeEach
  void setup() {
    Map<Direction, Integer> exits = new HashMap<>();
    exits.put(Direction.NORTH, 2);
    exits.put(Direction.SOUTH, 0); // wall

    room = new Room(1, "Test Room", "A test room", exits, null);

    GameWorld world = new GameWorld("Test", "1.0");
    world.addRoom(room);

    Room room2 = new Room(2, "Room 2", "Second room", new HashMap<>(), null);
    world.addRoom(room2);
  }

  // ─────────────────────────────────────────────
  // exits
  // ─────────────────────────────────────────────

  @Test
  void testGetExit() {
    assertEquals(2, room.getExit(Direction.NORTH));
    assertEquals(0, room.getExit(Direction.SOUTH));
  }

  @Test
  void testSetExit() {
    room.setExit(Direction.EAST, 3);
    assertEquals(3, room.getExit(Direction.EAST));
  }

  // ─────────────────────────────────────────────
  // passable / transition
  // ─────────────────────────────────────────────

  @Test
  void testPassableTrue() {
    assertTrue(room.passable(Direction.NORTH));
  }

  @Test
  void testPassableFalse() {
    assertFalse(room.passable(Direction.SOUTH));
  }

  @Test
  void testTransitionSuccess() {
    Room next = room.transition(Direction.NORTH);
    assertNotNull(next);
    assertEquals(2, next.getRoomNumber());
  }

  @Test
  void testTransitionBlocked() {
    assertNull(room.transition(Direction.SOUTH));
  }

  // ─────────────────────────────────────────────
  // items
  // ─────────────────────────────────────────────

  private Item makeItem(String name) {
    return new Item(name, 1, 1, 1, 0, "used", "desc", null);
  }

  @Test
  void testAddItem() {
    Item item = makeItem("Key");
    room.addItem(item);

    assertNotNull(room.getItems());
    assertEquals(1, room.getItems().size());
  }

  @Test
  void testRemoveItem() {
    Item item = makeItem("Key");

    room.addItem(item);
    room.removeItem(item);

    assertTrue(room.getItems().isEmpty());
  }

  @Test
  void testRemoveItemWhenEmpty() {
    room.removeItem(makeItem("Ghost"));
    assertNull(room.getItems());
  }

  // ─────────────────────────────────────────────
  // monster / puzzle（修正重點）
  // ─────────────────────────────────────────────

  @Test
  void testSetAndGetMonster() {
    Monster monster = new Monster(
        "Goblin",
        true,   // active
        false,  // affectsTarget
        true,   // affectsPlayer
        "key",  // solution
        10,     // value
        "A goblin",
        "It blocks the way",
        5,      // damage
        null,   // target
        true,   // canAttack
        "slashes you"
    );

    room.setMonster(monster);

    assertEquals(monster, room.getMonster());
    assertTrue(room.getMonster().isActive());
  }

  @Test
  void testSetAndGetPuzzle() {
    Puzzle puzzle = new Puzzle(
        "Door",
        true,   // active
        false,  // affectsTarget
        false,  // affectsPlayer
        "'answer'", // solution (text answer)
        10,     // value
        "A locked door",
        "The door is locked",
        null    // target
    );

    room.setPuzzle(puzzle);

    assertEquals(puzzle, room.getPuzzle());
    assertTrue(room.getPuzzle().isActive());
  }

  // ─────────────────────────────────────────────
  // basic getters
  // ─────────────────────────────────────────────

  @Test
  void testBasicProperties() {
    assertEquals(1, room.getRoomNumber());
    assertEquals("Test Room", room.getRoomName());
    assertEquals("A test room", room.getDescription());
  }
}