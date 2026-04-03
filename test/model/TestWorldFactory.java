package model;

import java.util.EnumMap;
import java.util.Map;

/** Factory helpers for model tests. */
public final class TestWorldFactory {

  private TestWorldFactory() {
    // Utility class.
  }

  /**
   * Creates a minimal world with two rooms and one item in the starting room.
   *
   * @return the test world
   */
  public static GameWorld createSimpleWorld() {
    GameWorld world = new GameWorld("Test Game", "1.0");

    Item key = new Item("Key", 2, 3, 3, 0, "You used the key", "A small key", null);
    Fixture desk = new Fixture("Desk", 400, "A sturdy desk.", null, null, null);
    world.addItem(key);
    world.addFixture(desk);

    Room room1 = new Room(1, "Start Room", "This is the starting room.", exits(0, 0, 2, 0), null);
    room1.addItem(key);

    Room room2 = new Room(2, "Second Room", "This is another room.", exits(0, 0, 0, 1), null);

    world.addRoom(room1);
    world.addRoom(room2);
    return world;
  }

  /**
   * Creates a world with an answer-based puzzle in the starting room.
   *
   * @return the test world
   */
  public static GameWorld createAnswerPuzzleWorld() {
    GameWorld world = new GameWorld("Puzzle Game", "1.0");
    Puzzle puzzle =
        new Puzzle(
            "Riddle Door",
            true,
            true,
            false,
            "'align'",
            120,
            "A sealed door with a riddle.",
            "Symbols glow across the locked door.",
            "1:Start Room");
    world.addPuzzle(puzzle);

    Room room1 =
        new Room(1, "Start Room", "A room with a sealed northern door.", exits(-2, 0, 0, 0), null);
    room1.setPuzzle(puzzle);

    Room room2 = new Room(2, "Archive", "Rows of books line the walls.", exits(0, 1, 0, 0), null);

    world.addRoom(room1);
    world.addRoom(room2);
    return world;
  }

  /**
   * Creates a world with an item-based puzzle in the starting room.
   *
   * @return the test world
   */
  public static GameWorld createItemPuzzleWorld() {
    GameWorld world = new GameWorld("Item Puzzle Game", "1.0");
    Item key =
        new Item("Silver Key", 1, 2, 2, 0, "You turn the silver key", "A polished key.", null);
    Puzzle puzzle =
        new Puzzle(
            "Lock",
            true,
            true,
            false,
            "Silver Key",
            150,
            "A lock bars the way.",
            "A heavy lock blocks the exit.",
            "1:Start Room");

    world.addItem(key);
    world.addPuzzle(puzzle);

    Room room1 =
        new Room(1, "Start Room", "A room with a locked eastern gate.", exits(0, 0, -2, 0), null);
    room1.setPuzzle(puzzle);
    room1.addItem(key);

    Room room2 = new Room(2, "Treasure Room", "A room beyond the gate.", exits(0, 0, 0, 1), null);

    world.addRoom(room1);
    world.addRoom(room2);
    return world;
  }

  /**
   * Creates a world with a monster in the second room.
   *
   * @return the test world
   */
  public static GameWorld createMonsterWorld() {
    GameWorld world = new GameWorld("Monster Game", "1.0");
    Item clippers =
        new Item("Hair Clippers", 2, 2, 2, 0, "The clippers buzz loudly", "Sharp clippers.", null);
    Monster monster =
        new Monster(
            "Teddy Bear",
            true,
            true,
            true,
            "Hair Clippers",
            200,
            "A harmless teddy bear lies on the floor.",
            "A furious teddy bear blocks the room.",
            -20,
            "2:Den",
            true,
            "It swipes at you.");

    world.addItem(clippers);
    world.addMonster(monster);

    Room room1 = new Room(1, "Start Room", "A quiet starting room.", exits(0, 0, 2, 0), null);
    room1.addItem(clippers);

    Room room2 = new Room(2, "Den", "A cramped den.", exits(0, 0, 0, 1), null);
    room2.setMonster(monster);

    world.addRoom(room1);
    world.addRoom(room2);
    return world;
  }

  /**
   * Creates a world suitable for save/restore tests.
   *
   * @return the test world
   */
  public static GameWorld createPersistenceWorld() {
    GameWorld world = new GameWorld("Persistence Game", "1.0");
    Item key = new Item("Key", 1, 3, 1, 5, "You turn the key", "A brass key.", null);
    Item note = new Item("Note", 1, 1, 1, 2, "You read the note", "A handwritten note.", null);
    Puzzle puzzle =
        new Puzzle(
            "Lock",
            true,
            true,
            false,
            "Key",
            50,
            "A locked mechanism.",
            "A lock seals the passage.",
            "1:Entry");
    Monster monster =
        new Monster(
            "Robot",
            true,
            true,
            true,
            "Key",
            75,
            "A disabled robot slumps in the corner.",
            "A robot patrols the chamber.",
            -10,
            "2:Lab",
            true,
            "It shocks you.");

    world.addItem(key);
    world.addItem(note);
    world.addPuzzle(puzzle);
    world.addMonster(monster);

    Room room1 = new Room(1, "Entry", "The first room.", exits(0, 0, 2, 0), null);
    room1.addItem(key);
    room1.addItem(note);
    room1.setPuzzle(puzzle);

    Room room2 = new Room(2, "Lab", "A humming laboratory.", exits(0, 0, 0, 1), null);
    room2.setMonster(monster);

    world.addRoom(room1);
    world.addRoom(room2);
    return world;
  }

  private static Map<Direction, Integer> exits(int north, int south, int east, int west) {
    Map<Direction, Integer> exits = new EnumMap<>(Direction.class);
    exits.put(Direction.NORTH, north);
    exits.put(Direction.SOUTH, south);
    exits.put(Direction.EAST, east);
    exits.put(Direction.WEST, west);
    return exits;
  }
}
