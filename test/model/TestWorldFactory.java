package model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds a minimal GameWorld for unit testing.
 * <p>
 * World layout:
 * <p>
 *   Room 1 (start)
 *     - EAST → Room 2
 *     - NORTH → wall (0)
 *     - contains: Key
 * <p>
 *   Room 2
 *     - WEST → Room 1
 */
public class TestWorldFactory {

  public static GameWorld createSimpleWorld() {
    resetRooms(); // very important (static map)

    GameWorld world = new GameWorld("Test Game", "1.0");

    // ─────────────────────────────────────────
    // Create exits map
    // ─────────────────────────────────────────
    Map<Direction, Integer> exits1 = new HashMap<>();
    Map<Direction, Integer> exits2 = new HashMap<>();

    // ─────────────────────────────────────────
    // Create rooms
    // ─────────────────────────────────────────
    Room room1 = new Room(
        1,
        "Start Room",
        "This is the starting room.",
        exits1,
        null
    );

    Room room2 = new Room(
        2,
        "Second Room",
        "This is another room.",
        exits2,
        null
    );

    // ─────────────────────────────────────────
    // Setup exits
    // ─────────────────────────────────────────
    room1.setExit(Direction.EAST, 2);
    room1.setExit(Direction.NORTH, 0); // wall

    room2.setExit(Direction.WEST, 1);

    // ─────────────────────────────────────────
    // Add item to room1
    // ─────────────────────────────────────────
    Item key = new Item(
        "Key",
        2,      // weight
        3,      // maxUses
        3,      // usesRemaining
        0,      // value
        "You used the key",
        "A small key",
        null
    );

    room1.addItem(key);

    // ─────────────────────────────────────────
    // Register rooms into world (IMPORTANT)
    // ─────────────────────────────────────────
    world.addRoom(room1);
    world.addRoom(room2);

    return world;
  }

  /**
   * Clears static GameWorld.rooms map using reflection.
   * Required to avoid cross-test contamination.
   */
  private static void resetRooms() {
    try {
      Field roomsField = GameWorld.class.getDeclaredField("rooms");
      roomsField.setAccessible(true);

      Map<?, ?> rooms = (Map<?, ?>) roomsField.get(null);
      rooms.clear();

    } catch (Exception e) {
      throw new RuntimeException("Failed to reset GameWorld rooms", e);
    }
  }
}
