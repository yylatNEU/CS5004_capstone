package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the entire game world.
 */
public class GameWorld {
  private String gameName;
  private String version;
  private static final Map<Integer, Room>   rooms    = new HashMap<>();
  private final Map<String, Item>    items    = new HashMap<>();
  private final Map<String, Fixture> fixtures = new HashMap<>();
  private final Map<String, Puzzle>  puzzles  = new HashMap<>();
  private final Map<String, Monster> monsters = new HashMap<>();

  /**
   * Constructs a new GameWorld with the given name and version.
   * @param gameName name
   * @param version version
   */
  public GameWorld(String gameName, String version) {
    this.gameName = gameName;
    this.version  = version;
  }

  /**
   * Trimming whitespace and converting it to uppercase
   * @param name name
   * @return String
   */
  private String normalize(String name) {
    return name.trim().toUpperCase();
  }

  // All these add methods are call by JsonGameLoader.

  /**
   * Add room to game world.
   * @param room room o
   */
  public void addRoom(Room room) {
    if (room != null) {
      rooms.put(room.getRoomNumber(), room);
    }
  }

  /**
   * Add item.
   * @param item item o
   */
  public void addItem(Item item) {
    if (item != null) {
      items.put(normalize(item.getName()), item);
    }
  }

  /**
   * Add fixture.
   * @param fixture fixture o
   */
  public void addFixture(Fixture fixture) {
    if (fixture != null) {
      fixtures.put(normalize(fixture.getName()), fixture);
    }
  }

  /**
   * Add puzzle.
   * @param puzzle puzzle o
   */
  public void addPuzzle(Puzzle puzzle) {
    if (puzzle != null) {
      puzzles.put(normalize(puzzle.getName()), puzzle);
    }
  }

  /**
   * Add monster.
   * @param monster monster o
   */
  public void addMonster(Monster monster) {
    if (monster != null) {
      monsters.put(normalize(monster.getName()), monster);
    }
  }

  // All these getters are call by GameModel

  /**
   * Get room.
   * @param roomNumber room num
   * @return room
   */
  public static Room getRoom(int roomNumber) {
    return rooms.get(roomNumber);
  }

  /**
   * Get item.
   * @param name item name
   * @return item
   */
  public Item getItem(String name) {
    if (name == null) return null;
    return items.get(normalize(name));
  }

  /**
   * Get fixture.
   * @param name fixture name
   * @return fixture
   */
  public Fixture getFixture(String name) {
    if (name == null) return null;
    return fixtures.get(normalize(name));
  }

  /**
   * Get puzzle.
   * @param name puzzle name
   * @return puzzle
   */
  public Puzzle getPuzzle(String name) {
    if (name == null) return null;
    return puzzles.get(normalize(name));
  }

  /**
   * Get monster.
   * @param name monster name
   * @return monster
   */
  public Monster getMonster(String name) {
    if (name == null) return null;
    return monsters.get(normalize(name));
  }

  /**
   * Get starting room.
   * @return starting room
   */
  public Room getStartingRoom() {
    return rooms.get(1);
  }

  /**
   * Get game name.
   * @return game name
   */
  public String getGameName() {
    return gameName;
  }

  /**
   * Get version.
   * @return version
   */
  public String getVersion()  {
    return version;
  }

  /**
   * Get room count.
   * @return count
   */
  public int getRoomCount() {
    return rooms.size();
  }

  /**
   * Returns a string representation of the game world summary.
   * @return string representation
   */
  @Override
  public String toString() {
    return String.format("GameWorld[name=%s, rooms=%d, items=%d, fixtures=%d, puzzles=%d, monsters=%d]",
            gameName, rooms.size(), items.size(), fixtures.size(), puzzles.size(), monsters.size());
  }
}
