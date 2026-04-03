package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link Room}. */
class RoomTest {

  private GameWorld world;
  private Room room;

  @BeforeEach
  void setUp() {
    Map<Direction, Integer> exits = new EnumMap<>(Direction.class);
    exits.put(Direction.NORTH, 2);
    exits.put(Direction.SOUTH, 0);
    exits.put(Direction.EAST, 0);
    exits.put(Direction.WEST, 0);

    room = new Room(1, "Test Room", "A test room", exits, "room.png");
    world = new GameWorld("Test", "1.0");
    world.addRoom(room);
    world.addRoom(new Room(2, "Room 2", "Second room", exits(0, 1, 0, 0), null));
  }

  @Test
  void basicPropertiesAreExposed() {
    assertEquals(1, room.getRoomNumber());
    assertEquals("Test Room", room.getRoomName());
    assertEquals("A test room", room.getDescription());
    assertEquals("room.png", room.getPicture());
  }

  @Test
  void exitsCanBeReadAndUpdated() {
    assertEquals(2, room.getExit(Direction.NORTH));
    room.setExit(Direction.EAST, 3);
    assertEquals(3, room.getExit(Direction.EAST));
  }

  @Test
  void passableAndTransitionRespectExitValues() {
    assertTrue(room.passable(Direction.NORTH));
    assertFalse(room.passable(Direction.SOUTH));
    assertEquals(2, room.transition(Direction.NORTH, world).getRoomNumber());
    assertNull(room.transition(Direction.SOUTH, world));
  }

  @Test
  void itemsCanBeAddedAndRemoved() {
    Item item = new Item("Key", 1, 1, 1, 0, "used", "desc", null);
    room.addItem(item);
    assertEquals(1, room.getItems().size());

    room.removeItem(item);
    assertTrue(room.getItems().isEmpty());
  }

  @Test
  void fixturesCanBeAdded() {
    Fixture fixture = new Fixture("Cabinet", 500, "A tall cabinet.", null, null, null);
    room.addFixture(fixture);
    assertEquals(1, room.getFixtures().size());
  }

  @Test
  void puzzleAndMonsterCanBeAssigned() {
    Puzzle puzzle =
        new Puzzle("Door", true, false, false, "'answer'", 10, "A locked door", "Locked", null);
    Monster monster =
        new Monster(
            "Goblin",
            true,
            false,
            true,
            "Key",
            10,
            "A goblin",
            "It blocks the way",
            -5,
            null,
            true,
            "slashes you");

    room.setPuzzle(puzzle);
    room.setMonster(monster);

    assertEquals(puzzle, room.getPuzzle());
    assertEquals(monster, room.getMonster());
  }

  @Test
  void copyConstructorPreservesAssignedContent() {
    room.addItem(new Item("Map", 1, 1, 1, 0, "used", "desc", null));
    room.addFixture(new Fixture("Desk", 300, "Wooden desk", null, null, null));
    room.setPuzzle(
        new Puzzle("Door", true, false, false, "'answer'", 10, "A door", "Locked", null));

    Room copy = new Room(room);

    assertEquals(room.getRoomName(), copy.getRoomName());
    assertEquals(room.getItems().size(), copy.getItems().size());
    assertNotNull(copy.getPuzzle());
  }

  private Map<Direction, Integer> exits(int north, int south, int east, int west) {
    Map<Direction, Integer> exits = new EnumMap<>(Direction.class);
    exits.put(Direction.NORTH, north);
    exits.put(Direction.SOUTH, south);
    exits.put(Direction.EAST, east);
    exits.put(Direction.WEST, west);
    return exits;
  }
}
