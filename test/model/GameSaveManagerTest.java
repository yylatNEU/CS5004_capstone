package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link GameSaveManager}. */
class GameSaveManagerTest {

  private static final Path SAVE_FILE = Path.of("savegame.json");

  @AfterEach
  void tearDown() throws Exception {
    Files.deleteIfExists(SAVE_FILE);
  }

  @Test
  void saveAndRestoreRecoverPlayerWorldAndInventoryState() throws Exception {
    GameWorld world = TestWorldFactory.createPersistenceWorld();
    Player player = new Player("Tester", 2);
    player.takeDamage(35);
    player.addScore(125);
    player.getInventory().addItem(world.getItem("Key"));

    Room entry = world.getRoom(1);
    entry.removeItem(world.getItem("Key"));
    entry.getPuzzle().deactivate();
    entry.setExit(Direction.EAST, 2);

    Room lab = world.getRoom(2);
    lab.getMonster().deactivate();

    GameSaveManager saveManager = new GameSaveManager();
    saveManager.save(player, world);

    player.restoreHealth();
    player.setCurrentRoomNumber(1);
    player.getInventory().removeItem("Key");
    entry.addItem(world.getItem("Key"));
    world.getItem("Key").setUsesRemaining(3);

    Player restored = saveManager.restore(world);

    assertEquals("Tester", restored.getName());
    assertEquals(65, restored.getHealth());
    assertEquals(125, restored.getScore());
    assertEquals(2, restored.getCurrentRoomNumber());
    assertNotNull(restored.getInventory().getItem("Key"));
    assertEquals(1, restored.getInventory().getItem("Key").getUsesRemaining());
    assertFalse(world.getRoom(1).getPuzzle().isActive());
    assertFalse(world.getRoom(2).getMonster().isActive());
    assertEquals(2, world.getRoom(1).getExit(Direction.EAST));
    assertTrue(
        world.getRoom(1).getItems().stream().noneMatch(item -> item.getName().equals("Key")));
  }
}
