package model;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JsonGameLoader.
 */
public class JsonGameLoaderTest {

  /**
   * Creates a temporary JSON file with the given content.
   * @param content JSON content
   * @return the absolute file path of the temporary file
   * @throws IOException if file creation or writing fails
   */
  private String createTempJson(String content) throws IOException {
    File temp = File.createTempFile("game", ".json");
    FileWriter writer = new FileWriter(temp);
    writer.write(content);
    writer.close();
    return temp.getAbsolutePath();
  }

  /**
   * Tests that a complete and valid JSON file is correctly loaded.
   * @throws IOException if file reading fails
   */
  @Test
  public void testLoadFullGame() throws IOException {
    String json = """
        {
          "name": "Game1",
          "version": "2.0",
          "items": [{ "name": "Key", "weight": 2 }],
          "fixtures": [{ "name": "Door" }],
          "puzzles": [{ "name": "Puzzle1", "active": "true" }],
          "monsters": [{ "name": "Dragon", "damage": 50 }],
          "rooms": [{
              "room_number": 1,
              "room_name": "Room1",
              "items": "Key",
              "fixtures": "Door",
              "puzzle": "Puzzle1",
              "monster": "Dragon",
              "N": 2
          }]
        }
        """;

    GameWorld world = new JsonGameLoader().load(createTempJson(json));

    assertEquals("Game1", world.getGameName());
    assertNotNull(world.getItem("Key"));
    assertNotNull(world.getFixture("Door"));
    assertNotNull(world.getPuzzle("Puzzle1"));
    assertNotNull(world.getMonster("Dragon"));
    assertNotNull(world.getRoom(1));
  }

  /**
   * Tests loading of items.
   * @throws IOException if file reading fails
   */
  @Test
  public void testLoadItems() throws IOException {
    String json = """
        {
          "items": [
            { "name": "Potion", "weight": 3 },
            { "name": "" },
            123
          ]
        }
        """;

    GameWorld world = new JsonGameLoader().load(createTempJson(json));

    assertNotNull(world.getItem("Potion"));
    assertNotNull(world.getItem("Unknown Item"));
  }

  /**
   * Tests that fixture definitions are correctly loaded.
   * @throws IOException if file reading fails
   */
  @Test
  public void testLoadFixtures() throws IOException {
    String json = """
        {
          "fixtures": [
            { "name": "Table" }
          ]
        }
        """;

    GameWorld world = new JsonGameLoader().load(createTempJson(json));
    assertNotNull(world.getFixture("Table"));
  }

  /**
   * Tests loading of puzzles and boolean field parsing.
   * @throws IOException if file reading fails
   */
  @Test
  public void testLoadPuzzles() throws IOException {
    String json = """
        {
          "puzzles": [
            { "name": "P1", "active": "false" }
          ]
        }
        """;

    GameWorld world = new JsonGameLoader().load(createTempJson(json));

    Puzzle p = world.getPuzzle("P1");
    assertNotNull(p);
    assertFalse(p.isActive());
  }

  /**
   * Tests loading of monster definitions.
   * @throws IOException if file reading fails
   */
  @Test
  public void testLoadMonsters() throws IOException {
    String json = """
        {
          "monsters": [
            { "name": "Zombie", "damage": 10 }
          ]
        }
        """;

    GameWorld world = new JsonGameLoader().load(createTempJson(json));

    assertNotNull(world.getMonster("Zombie"));
  }

  /**
   * Tests room creation and linking with items.
   * @throws IOException if file reading fails
   */
  @Test
  public void testLoadRooms() throws IOException {
    String json = """
        {
          "items": [{ "name": "Key" }],
          "rooms": [
            {
              "room_number": 1,
              "room_name": "Start",
              "items": "Key",
              "N": 2,
              "S": 0
            }
          ]
        }
        """;

    GameWorld world = new JsonGameLoader().load(createTempJson(json));

    Room room = world.getRoom(1);

    assertNotNull(room);
    assertFalse(room.getItems().isEmpty());
  }

  /**
   * Tests integer parsing when invalid values are provided.
   * @throws IOException if file reading fails
   */
  @Test
  public void testGetIntInvalid() throws IOException {
    String json = """
        {
          "items": [
            { "name": "BadItem", "weight": "abc" }
          ]
        }
        """;

    GameWorld world = new JsonGameLoader().load(createTempJson(json));

    Item item = world.getItem("BadItem");
    assertEquals(0, item.getWeight());
  }

  /**
   * Tests string parsing when empty values are provided.
   * @throws IOException if file reading fails
   */
  @Test
  public void testGetStringEmpty() throws IOException {
    String json = """
        {
          "items": [
            { "name": "" }
          ]
        }
        """;

    GameWorld world = new JsonGameLoader().load(createTempJson(json));

    assertNotNull(world.getItem("Unknown Item"));
  }

  /**
   * Tests behavior when a room references undefined entities.
   * @throws IOException if file reading fails
   */
  @Test
  public void testUndefinedReference() throws IOException {
    String json = """
        {
          "rooms": [
            {
              "room_number": 1,
              "items": "NotExist"
            }
          ]
        }
        """;

    assertDoesNotThrow(() ->
            new JsonGameLoader().load(createTempJson(json))
    );
  }
}
