package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Represents the complete loaded game world. */
public class GameWorld {

  private final String gameName;
  private final String version;
  private final Map<Integer, Room> rooms;
  private final Map<String, Item> items;
  private final Map<String, Fixture> fixtures;
  private final Map<String, Puzzle> puzzles;
  private final Map<String, Monster> monsters;

  /**
   * Creates a new world.
   *
   * @param gameName the game name
   * @param version the game version
   */
  public GameWorld(String gameName, String version) {
    this.gameName = gameName;
    this.version = version;
    this.rooms = new HashMap<>();
    this.items = new HashMap<>();
    this.fixtures = new HashMap<>();
    this.puzzles = new HashMap<>();
    this.monsters = new HashMap<>();
  }

  /**
   * Adds a room to the world.
   *
   * @param room the room to add
   */
  public void addRoom(Room room) {
    if (room != null) {
      rooms.put(room.getRoomNumber(), room);
    }
  }

  /**
   * Adds an item definition to the world.
   *
   * @param item the item to add
   */
  public void addItem(Item item) {
    if (item != null) {
      items.put(normalize(item.getName()), item);
    }
  }

  /**
   * Adds a fixture definition to the world.
   *
   * @param fixture the fixture to add
   */
  public void addFixture(Fixture fixture) {
    if (fixture != null) {
      fixtures.put(normalize(fixture.getName()), fixture);
    }
  }

  /**
   * Adds a puzzle definition to the world.
   *
   * @param puzzle the puzzle to add
   */
  public void addPuzzle(Puzzle puzzle) {
    if (puzzle != null) {
      puzzles.put(normalize(puzzle.getName()), puzzle);
    }
  }

  /**
   * Adds a monster definition to the world.
   *
   * @param monster the monster to add
   */
  public void addMonster(Monster monster) {
    if (monster != null) {
      monsters.put(normalize(monster.getName()), monster);
    }
  }

  /**
   * Returns a room by room number.
   *
   * @param roomNumber the room number
   * @return the room, or {@code null}
   */
  public Room getRoom(int roomNumber) {
    return rooms.get(roomNumber);
  }

  /**
   * Returns an item definition by name.
   *
   * @param name the item name
   * @return the item, or {@code null}
   */
  public Item getItem(String name) {
    return name == null ? null : items.get(normalize(name));
  }

  /**
   * Returns a fixture definition by name.
   *
   * @param name the fixture name
   * @return the fixture, or {@code null}
   */
  public Fixture getFixture(String name) {
    return name == null ? null : fixtures.get(normalize(name));
  }

  /**
   * Returns a puzzle definition by name.
   *
   * @param name the puzzle name
   * @return the puzzle, or {@code null}
   */
  public Puzzle getPuzzle(String name) {
    return name == null ? null : puzzles.get(normalize(name));
  }

  /**
   * Returns a monster definition by name.
   *
   * @param name the monster name
   * @return the monster, or {@code null}
   */
  public Monster getMonster(String name) {
    return name == null ? null : monsters.get(normalize(name));
  }

  /**
   * Returns all known items.
   *
   * @return an immutable item map
   */
  public Map<String, Item> getAllItems() {
    return Collections.unmodifiableMap(items);
  }

  /**
   * Returns all known puzzles.
   *
   * @return an immutable puzzle map
   */
  public Map<String, Puzzle> getAllPuzzles() {
    return Collections.unmodifiableMap(puzzles);
  }

  /**
   * Returns all known monsters.
   *
   * @return an immutable monster map
   */
  public Map<String, Monster> getAllMonsters() {
    return Collections.unmodifiableMap(monsters);
  }

  /**
   * Returns the starting room.
   *
   * @return room 1, or {@code null}
   */
  public Room getStartingRoom() {
    return rooms.get(1);
  }

  /**
   * Returns the game name.
   *
   * @return the game name
   */
  public String getGameName() {
    return gameName;
  }

  /**
   * Returns the game version.
   *
   * @return the version string
   */
  public String getVersion() {
    return version;
  }

  /**
   * Returns the number of rooms.
   *
   * @return the room count
   */
  public int getRoomCount() {
    return rooms.size();
  }

  @Override
  public String toString() {
    return String.format(
        "GameWorld[name=%s, rooms=%d, items=%d, fixtures=%d, puzzles=%d, monsters=%d]",
        gameName, rooms.size(), items.size(), fixtures.size(), puzzles.size(), monsters.size());
  }

  private String normalize(String name) {
    return name.trim().toUpperCase();
  }
}
