package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Represents a room in the game world. */
public class Room {

  private final int roomNumber;
  private final String roomName;
  private final String description;
  private final Map<Direction, Integer> exits;
  private final String picture;
  private final List<Item> items;
  private final List<Fixture> fixtures;
  private Puzzle puzzle;
  private Monster monster;

  /**
   * Creates a room.
   *
   * @param roomNumber the room identifier
   * @param roomName the room name
   * @param description the base room description
   * @param exits the directional exits for the room
   * @param picture the optional picture filename
   */
  public Room(
      int roomNumber,
      String roomName,
      String description,
      Map<Direction, Integer> exits,
      String picture) {
    this.roomNumber = roomNumber;
    this.roomName = roomName;
    this.description = description;
    this.exits = new EnumMap<>(Direction.class);
    if (exits != null) {
      this.exits.putAll(exits);
    }
    for (Direction direction : Direction.values()) {
      this.exits.putIfAbsent(direction, 0);
    }
    this.picture = picture;
    this.items = new ArrayList<>();
    this.fixtures = new ArrayList<>();
  }

  /**
   * Creates a shallow copy of another room.
   *
   * @param room the room to copy
   */
  public Room(Room room) {
    this(room.roomNumber, room.roomName, room.description, room.exits, room.picture);
    this.items.addAll(room.items);
    this.fixtures.addAll(room.fixtures);
    this.puzzle = room.puzzle;
    this.monster = room.monster;
  }

  /**
   * Creates a room wrapper holding only a puzzle.
   *
   * @param puzzle the puzzle to assign
   */
  public Room(Puzzle puzzle) {
    this(0, "", "", null, null);
    this.puzzle = puzzle;
  }

  /**
   * Creates a room wrapper holding only a monster.
   *
   * @param monster the monster to assign
   */
  public Room(Monster monster) {
    this(0, "", "", null, null);
    this.monster = monster;
  }

  /**
   * Returns the room number.
   *
   * @return the room number
   */
  public int getRoomNumber() {
    return roomNumber;
  }

  /**
   * Returns the room name.
   *
   * @return the room name
   */
  public String getRoomName() {
    return roomName;
  }

  /**
   * Returns the room description.
   *
   * @return the room description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the optional room picture.
   *
   * @return the picture filename, or {@code null}
   */
  public String getPicture() {
    return picture;
  }

  /**
   * Sets the destination value for a direction.
   *
   * @param direction the direction to update
   * @param destination the destination room number or wall/block marker
   */
  public void setExit(Direction direction, int destination) {
    exits.put(direction, destination);
  }

  /**
   * Returns the exit value for a direction.
   *
   * @param direction the direction to inspect
   * @return the exit value, or {@code 0} if absent
   */
  public int getExit(Direction direction) {
    return exits.getOrDefault(direction, 0);
  }

  /**
   * Returns whether the given direction leads to a traversable room.
   *
   * @param direction the direction to inspect
   * @return {@code true} if the exit value is positive
   */
  public boolean passable(Direction direction) {
    return getExit(direction) > 0;
  }

  /**
   * Transitions to the destination room if the exit is passable.
   *
   * @param direction the direction to travel
   * @param world the world used to resolve the destination room
   * @return the destination room, or {@code null} if blocked
   */
  public Room transition(Direction direction, GameWorld world) {
    if (!passable(direction) || world == null) {
      return null;
    }
    return world.getRoom(getExit(direction));
  }

  /**
   * Returns an immutable view of the room exits.
   *
   * @return the exit map
   */
  public Map<Direction, Integer> getExits() {
    return Collections.unmodifiableMap(exits);
  }

  /**
   * Adds an item to the room.
   *
   * @param item the item to add
   */
  public void addItem(Item item) {
    if (item != null) {
      items.add(item);
    }
  }

  /**
   * Removes an item from the room.
   *
   * @param item the item to remove
   */
  public void removeItem(Item item) {
    items.remove(item);
  }

  /**
   * Returns the room items.
   *
   * @return the mutable item list
   */
  public List<Item> getItems() {
    return items;
  }

  /**
   * Adds a fixture to the room.
   *
   * @param fixture the fixture to add
   */
  public void addFixture(Fixture fixture) {
    if (fixture != null) {
      fixtures.add(fixture);
    }
  }

  /**
   * Returns the room fixtures.
   *
   * @return an immutable view of fixtures in the room
   */
  public List<Fixture> getFixtures() {
    return Collections.unmodifiableList(fixtures);
  }

  /**
   * Sets the room monster.
   *
   * @param monster the monster to place in the room
   */
  public void setMonster(Monster monster) {
    this.monster = monster;
  }

  /**
   * Returns the room monster.
   *
   * @return the monster, or {@code null}
   */
  public Monster getMonster() {
    return monster;
  }

  /**
   * Sets the room puzzle.
   *
   * @param puzzle the puzzle to place in the room
   */
  public void setPuzzle(Puzzle puzzle) {
    this.puzzle = puzzle;
  }

  /**
   * Returns the room puzzle.
   *
   * @return the puzzle, or {@code null}
   */
  public Puzzle getPuzzle() {
    return puzzle;
  }
}
