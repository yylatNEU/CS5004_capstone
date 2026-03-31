package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles saving and restoring game state to and from a JSON file.
 * The save file captures the minimal mutable state needed to resume
 * a game session: player stats, inventory contents, item usage,
 * puzzle/monster active status, and room exit states.
 *
 * <p>This class is used by GameModel internally. The controller
 * calls model.save() and model.restore(), which delegate here.</p>
 */
public class GameSaveManager {

  private static final String SAVE_FILE = "./savegame.json";

  /**
   * Saves the current game state to a JSON file.
   *
   * @param player the current player
   * @param world  the game world containing all rooms and elements
   * @throws IOException if writing the file fails
   */
  public void save(Player player, GameWorld world) throws IOException {
    JSONObject root = new JSONObject();

    root.put("playerName", player.getName());
    root.put("health", player.getHealth());
    root.put("score", player.getScore());
    root.put("currentRoom", player.getCurrentRoomNumber());

    root.put("inventory", buildInventoryArray(player));
    root.put("items", buildItemStatesArray(world));
    root.put("puzzles", buildPuzzleStates(world));
    root.put("monsters", buildMonsterStates(world));
    root.put("rooms", buildRoomStates(world));

    Files.write(Paths.get(SAVE_FILE), root.toString(2).getBytes());
  }

  /**
   * Restores game state from a previously saved JSON file.
   * Creates and returns a new Player with the saved stats,
   * and restores all mutable world state in place.
   *
   * @param world the game world (already loaded from the game JSON)
   * @return a new Player with restored stats and inventory
   * @throws IOException if reading the file fails or file is missing
   */
  public Player restore(GameWorld world) throws IOException {
    String content = new String(Files.readAllBytes(Paths.get(SAVE_FILE)));
    JSONObject root = new JSONObject(content);

    String name = root.getString("playerName");
    int health = root.getInt("health");
    int score = root.getInt("score");
    int currentRoom = root.getInt("currentRoom");

    Player player = new Player(name, currentRoom);
    applyHealth(player, health);
    applyScore(player, score);

    restoreItemStates(root.getJSONArray("items"), world);
    restoreInventory(root.getJSONArray("inventory"), player, world);
    restorePuzzleStates(root.getJSONObject("puzzles"), world);
    restoreMonsterStates(root.getJSONObject("monsters"), world);
    restoreRoomStates(root.getJSONArray("rooms"), world);

    return player;
  }

  // ── Save helpers ──────────────────────────────────────────────────────

  /**
   * Builds a JSON array of items currently in the player's inventory.
   * Each entry records the item name and its remaining uses.
   */
  private JSONArray buildInventoryArray(Player player) {
    JSONArray arr = new JSONArray();
    List<Item> items = getInventoryItems(player);
    for (Item item : items) {
      JSONObject obj = new JSONObject();
      obj.put("name", item.getName());
      obj.put("usesRemaining", item.getUsesRemaining());
      arr.put(obj);
    }
    return arr;
  }

  /**
   * Builds a JSON array recording usesRemaining for every item
   * in the game world, so we can restore consumed uses.
   */
  private JSONArray buildItemStatesArray(GameWorld world) {
    JSONArray arr = new JSONArray();
    // TODO: needs GameWorld.getAllItems() or similar method from Matt
    // For now, we save item states that we can access through rooms
    for (int roomNum = 1; roomNum <= world.getRoomCount(); roomNum++) {
      Room room = world.getRoom(roomNum);
      if (room == null) {
        continue;
      }
      // TODO: needs Room.getItems() from John — currently Room only
      // has a single Item field without a public getter
    }
    return arr;
  }

  /**
   * Builds a JSON object mapping puzzle names to their active status.
   */
  private JSONObject buildPuzzleStates(GameWorld world) {
    JSONObject obj = new JSONObject();
    // TODO: needs GameWorld.getAllPuzzles() from Matt
    // When available, iterate and record:
    // obj.put(puzzle.getName(), puzzle.isActive());
    return obj;
  }

  /**
   * Builds a JSON object mapping monster names to their active status.
   */
  private JSONObject buildMonsterStates(GameWorld world) {
    JSONObject obj = new JSONObject();
    // TODO: needs GameWorld.getAllMonsters() from Matt
    // When available, iterate and record:
    // obj.put(monster.getName(), monster.isActive());
    return obj;
  }

  /**
   * Builds a JSON array of room states, capturing the current exit
   * values for each room (these change when puzzles/monsters
   * are solved, flipping negative exits to positive).
   */
  private JSONArray buildRoomStates(GameWorld world) {
    JSONArray arr = new JSONArray();
    for (int roomNum = 1; roomNum <= world.getRoomCount(); roomNum++) {
      Room room = world.getRoom(roomNum);
      if (room == null) {
        continue;
      }
      JSONObject obj = new JSONObject();
      obj.put("roomNumber", room.getRoomNumber());
      obj.put("N", room.getExit(Direction.NORTH));
      obj.put("S", room.getExit(Direction.SOUTH));
      obj.put("E", room.getExit(Direction.EAST));
      obj.put("W", room.getExit(Direction.WEST));
      // TODO: needs Room.getItems() to record which items are in each room
      arr.put(obj);
    }
    return arr;
  }

  // ── Restore helpers ───────────────────────────────────────────────────

  /**
   * Sets the player's health using the available API.
   * Since Player has no setHealth(), we start from full health
   * and apply damage to reach the target value.
   */
  private void applyHealth(Player player, int targetHealth) {
    int damage = player.getHealth() - targetHealth;
    if (damage > 0) {
      player.takeDamage(damage);
    }
  }

  /**
   * Sets the player's score using the available API.
   * Since Player has no setScore(), we add the target value
   * directly (player starts at 0 after construction).
   */
  private void applyScore(Player player, int targetScore) {
    if (targetScore > 0) {
      player.addScore(targetScore);
    }
  }

  /**
   * Restores usesRemaining for all items tracked in the save file.
   */
  private void restoreItemStates(JSONArray itemsArr, GameWorld world) {
    for (int i = 0; i < itemsArr.length(); i++) {
      JSONObject obj = itemsArr.getJSONObject(i);
      String name = obj.getString("name");
      int uses = obj.getInt("usesRemaining");
      Item item = world.getItem(name);
      if (item != null) {
        item.setUsesRemaining(uses);
      }
    }
  }

  /**
   * Restores the player's inventory from the saved item list.
   * Looks up each item by name in the game world, sets its
   * remaining uses, and adds it to the player's inventory.
   */
  private void restoreInventory(JSONArray invArr, Player player,
      GameWorld world) {
    Inventory bag = player.getInventory();
    // TODO: needs Inventory.clear() from Vanessa, or we manually
    // remove each item. Since the player is newly constructed,
    // the inventory starts empty, so no clearing needed here.
    for (int i = 0; i < invArr.length(); i++) {
      JSONObject obj = invArr.getJSONObject(i);
      String name = obj.getString("name");
      int uses = obj.getInt("usesRemaining");
      Item item = world.getItem(name);
      if (item != null) {
        item.setUsesRemaining(uses);
        bag.addItem(item);
      }
    }
  }

  /**
   * Restores puzzle active states. If a puzzle was deactivated
   * in the save, we call deactivate() on it.
   */
  private void restorePuzzleStates(JSONObject puzzleObj,
      GameWorld world) {
    for (String key : puzzleObj.keySet()) {
      boolean active = puzzleObj.getBoolean(key);
      Puzzle puzzle = world.getPuzzle(key);
      if (puzzle != null && !active) {
        puzzle.deactivate();
      }
      // TODO: if a puzzle was active in save but currently
      // deactivated, we would need puzzle.activate() or
      // puzzle.setActive(true) from Jackson
    }
  }

  /**
   * Restores monster active states. If a monster was deactivated
   * in the save, we call deactivate() on it.
   */
  private void restoreMonsterStates(JSONObject monsterObj,
      GameWorld world) {
    for (String key : monsterObj.keySet()) {
      boolean active = monsterObj.getBoolean(key);
      Monster monster = world.getMonster(key);
      if (monster != null && !active) {
        monster.deactivate();
      }
      // TODO: same as puzzle — need monster.setActive(true) from
      // Jackson if we need to re-activate a monster
    }
  }

  /**
   * Restores room exit values from the saved data.
   * This handles the case where puzzle/monster resolution
   * flipped negative exits to positive.
   */
  private void restoreRoomStates(JSONArray roomsArr, GameWorld world) {
    for (int i = 0; i < roomsArr.length(); i++) {
      JSONObject obj = roomsArr.getJSONObject(i);
      int roomNum = obj.getInt("roomNumber");
      Room room = world.getRoom(roomNum);
      if (room == null) {
        continue;
      }
      room.setExit(Direction.NORTH, obj.getInt("N"));
      room.setExit(Direction.SOUTH, obj.getInt("S"));
      room.setExit(Direction.EAST, obj.getInt("E"));
      room.setExit(Direction.WEST, obj.getInt("W"));
    }
  }

  // ── Utility ───────────────────────────────────────────────────────────

  /**
   * Extracts the list of items from a player's inventory.
   * Works around the lack of a direct getItems() method by
   * checking known item names from the world.
   *
   * <p>TODO: Replace this with Inventory.getItems() once Vanessa
   * adds that method. For now this is a placeholder that returns
   * an empty list — the real implementation needs the full item
   * list from Inventory.</p>
   */
  private List<Item> getInventoryItems(Player player) {
    // TODO: needs Inventory.getItems() returning List<Item>
    // Placeholder — will return empty until Vanessa adds getter
    return new ArrayList<>();
  }
}
