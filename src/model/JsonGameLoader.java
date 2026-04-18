package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/** Reads a JSON game file and constructs/saves data in GameWorld. */
public class JsonGameLoader {
  /**
   * Loads a GameWorld from a JSON file.
   *
   * @param filePath path
   * @return GameWorld object
   * @throws IOException if the file cannot be read
   */
  public GameWorld load(String filePath) throws IOException {
    String content = new String(Files.readAllBytes(Paths.get(filePath)));
    JSONObject root = new JSONObject(content);

    String gameName = getString(root, "name", "Unknown Game");
    String version = getString(root, "version", "1.0.0");
    GameWorld world = new GameWorld(gameName, version);

    loadItems(root, world);
    loadFixtures(root, world);
    loadPuzzles(root, world);
    loadMonsters(root, world);
    loadRooms(root, world);

    return world;
  }

  /**
   * Loads item definitions from JSON and adds them to the game world.
   *
   * @param root root
   * @param world world o
   */
  private void loadItems(JSONObject root, GameWorld world) {
    if (!root.has("items") || root.isNull("items")) {
      return;
    }

    JSONArray arr = root.optJSONArray("items");
    if (arr == null) {
      return;
    }

    for (int i = 0; i < arr.length(); i++) {
      try {
        JSONObject o = arr.getJSONObject(i);
        Item item =
            new Item(
                getString(o, "name", "Unknown Item"),
                getInt(o, "weight", 0),
                getInt(o, "max_uses", 1),
                getInt(o, "uses_remaining", 1),
                getInt(o, "value", 0),
                getString(o, "when_used", "You use the item."),
                getString(o, "description", "No description available."),
                getString(o, "picture", null));
        world.addItem(item);
      } catch (Exception e) {
        System.err.println("Warning: skipping malformed item at index " + i);
      }
    }
  }

  /**
   * Loads fixture definitions from JSON and adds them to the game world.
   *
   * @param root root
   * @param world world o
   */
  private void loadFixtures(JSONObject root, GameWorld world) {
    if (!root.has("fixtures") || root.isNull("fixtures")) {
      return;
    }

    JSONArray arr = root.optJSONArray("fixtures");
    if (arr == null) {
      return;
    }

    for (int i = 0; i < arr.length(); i++) {
      try {
        JSONObject o = arr.getJSONObject(i);
        Fixture fixture =
            new Fixture(
                getString(o, "name", "Unknown Fixture"),
                getInt(o, "weight", 1000),
                getString(o, "description", "No description available."),
                getString(o, "puzzle", null),
                getString(o, "states", null),
                getString(o, "picture", null));
        world.addFixture(fixture);
      } catch (Exception e) {
        System.err.println("Warning: skipping malformed fixture at index " + i);
      }
    }
  }

  /**
   * Loads puzzle definitions from JSON and adds them to the game world.
   *
   * @param root root
   * @param world world o
   */
  private void loadPuzzles(JSONObject root, GameWorld world) {
    if (!root.has("puzzles") || root.isNull("puzzles")) {
      return;
    }

    JSONArray arr = root.optJSONArray("puzzles");
    if (arr == null) {
      return;
    }

    for (int i = 0; i < arr.length(); i++) {
      try {
        JSONObject o = arr.getJSONObject(i);
        Puzzle puzzle =
            new Puzzle(
                getString(o, "name", "Unknown Puzzle"),
                getBoolean(o, "active", true),
                getBoolean(o, "affects_target", true),
                getBoolean(o, "affects_player", false),
                getString(o, "solution", ""),
                getInt(o, "value", 0),
                getString(o, "description", "No description available."),
                getString(o, "effects", "Something is blocking you."),
                getString(o, "target", ""),
                getString(o, "picture", null));
        world.addPuzzle(puzzle);
      } catch (Exception e) {
        System.err.println("Warning: skipping malformed puzzle at index " + i);
      }
    }
  }

  /**
   * Loads monster definitions from JSON and adds them to the game world.
   *
   * @param root root
   * @param world world o
   */
  private void loadMonsters(JSONObject root, GameWorld world) {
    if (!root.has("monsters") || root.isNull("monsters")) {
      return;
    }

    JSONArray arr = root.optJSONArray("monsters");
    if (arr == null) {
      return;
    }

    for (int i = 0; i < arr.length(); i++) {
      try {
        JSONObject o = arr.getJSONObject(i);
        Monster monster =
            new Monster(
                getString(o, "name", "Unknown Monster"),
                getBoolean(o, "active", true),
                getBoolean(o, "affects_target", true),
                getBoolean(o, "affects_player", true),
                getString(o, "solution", ""),
                getInt(o, "value", 0),
                getString(o, "description", "No description available."),
                getString(o, "effects", "A monster blocks your path!"),
                getInt(o, "damage", 0),
                getString(o, "target", ""),
                getBoolean(o, "can_attack", false),
                getString(o, "attack", ""),
                getString(o, "picture", null));
        world.addMonster(monster);
      } catch (Exception e) {
        System.err.println("Warning: skipping malformed monster at index " + i);
      }
    }
  }

  /**
   * Loads room definitions, including items, fixtures, puzzles, and monsters.
   *
   * @param root root
   * @param world world o
   */
  private void loadRooms(JSONObject root, GameWorld world) {
    if (!root.has("rooms") || root.isNull("rooms")) {
      return;
    }

    JSONArray arr = root.optJSONArray("rooms");
    if (arr == null) {
      return;
    }

    for (int i = 0; i < arr.length(); i++) {
      try {
        JSONObject o = arr.getJSONObject(i);

        int roomNumber = getInt(o, "room_number", 0);
        String roomName = getString(o, "room_name", "Unknown Room");
        String description = getString(o, "description", "An empty room.");
        int north = getInt(o, "N", 0);
        int south = getInt(o, "S", 0);
        int east = getInt(o, "E", 0);
        int west = getInt(o, "W", 0);
        String picture = getString(o, "picture", null);
        Map<Direction, Integer> exits = new HashMap<>();
        exits.put(Direction.NORTH, north);
        exits.put(Direction.SOUTH, south);
        exits.put(Direction.EAST, east);
        exits.put(Direction.WEST, west);
        Room room = new Room(roomNumber, roomName, description, exits, picture);

        String itemNames = getString(o, "items", null);
        if (itemNames != null) {
          for (String name : splitNames(itemNames)) {
            Item item = world.getItem(name);
            if (item != null) {
              room.addItem(item);
            } else {
              // graceful: room references an item not defined in JSON
              System.err.println(
                  "Warning: room '"
                      + roomName
                      + "' references undefined item '"
                      + name
                      + "' — skipping.");
            }
          }
        }

        String fixtureNames = getString(o, "fixtures", null);
        if (fixtureNames != null) {
          for (String name : splitNames(fixtureNames)) {
            Fixture fixture = world.getFixture(name);
            if (fixture != null) {
              room.addFixture(fixture);
            } else {
              System.err.println(
                  "Warning: room '"
                      + roomName
                      + "' references undefined fixture '"
                      + name
                      + "' — skipping.");
            }
          }
        }

        String puzzleName = getString(o, "puzzle", null);
        if (puzzleName != null) {
          Puzzle puzzle = world.getPuzzle(puzzleName);
          if (puzzle != null) {
            room.setPuzzle(puzzle);
          } else {
            System.err.println(
                "Warning: room '"
                    + roomName
                    + "' references undefined puzzle '"
                    + puzzleName
                    + "' — skipping.");
          }
        }

        String monsterName = getString(o, "monster", null);
        if (monsterName != null) {
          Monster monster = world.getMonster(monsterName);
          if (monster != null) {
            room.setMonster(monster);
          } else {
            System.err.println(
                "Warning: room '"
                    + roomName
                    + "' references undefined monster '"
                    + monsterName
                    + "' — skipping.");
          }
        }

        world.addRoom(room);

      } catch (Exception e) {
        System.err.println(
            "Warning: skipping malformed room at index " + i + " (" + e.getMessage() + ")");
      }
    }
  }

  // Helper methods — safe field reading

  /**
   * Safely reads a String field. Returns defaultValue if field is missing or null.
   *
   * @param o the JSON object
   * @param key key
   * @param defaultValue default value
   * @return the string value or defaultValue
   */
  private String getString(JSONObject o, String key, String defaultValue) {
    if (!o.has(key) || o.isNull(key)) {
      return defaultValue;
    }
    String val = o.optString(key, defaultValue);
    return (val == null || val.isEmpty()) ? defaultValue : val;
  }

  /**
   * Safely retrieves an integer value from a JSON object.
   *
   * @param o the JSON object
   * @param key key
   * @param defaultValue default value
   * @return the value
   */
  private int getInt(JSONObject o, String key, int defaultValue) {
    if (!o.has(key) || o.isNull(key)) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(o.optString(key, String.valueOf(defaultValue)).trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Safely reads a boolean field stored as string "true"/"false".
   *
   * @param o the JSON object
   * @param key key
   * @param defaultValue default value
   * @return true/false
   */
  private boolean getBoolean(JSONObject o, String key, boolean defaultValue) {
    if (!o.has(key) || o.isNull(key)) {
      return defaultValue;
    }
    String val = o.optString(key, String.valueOf(defaultValue));
    return "true".equalsIgnoreCase(val.trim());
  }

  /**
   * Splits a comma-separated string into a list of trimmed names.
   *
   * @param raw string
   * @return a list of trimmed names
   */
  private List<String> splitNames(String raw) {
    List<String> result = new ArrayList<>();
    if (raw == null || raw.trim().isEmpty()) {
      return result;
    }
    for (String part : raw.split(",")) {
      String trimmed = part.trim();
      if (!trimmed.isEmpty()) {
        result.add(trimmed);
      }
    }
    return result;
  }
}
