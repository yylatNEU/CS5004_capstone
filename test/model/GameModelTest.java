package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link GameModel}. */
class GameModelTest {

  private static final Path SAVE_FILE = Path.of("savegame.json");

  private GameModel model;

  @BeforeEach
  void setUp() throws Exception {
    Files.deleteIfExists(SAVE_FILE);
    model = new GameModel(TestWorldFactory.createSimpleWorld());
    model.setPlayerName("Tester");
  }

  @AfterEach
  void tearDown() throws Exception {
    Files.deleteIfExists(SAVE_FILE);
  }

  @Test
  void moveRejectsInvalidDirection() {
    assertEquals("Invalid direction.", model.move("invalid"));
  }

  @Test
  void moveRejectsWallExit() {
    assertTrue(model.move("north").contains("wall"));
  }

  @Test
  void moveChangesRoomOnValidExit() {
    String result = model.move("east");
    assertTrue(result.contains("You are in"));
    assertTrue(result.contains("SECOND ROOM"));
  }

  @Test
  void lookIncludesPlayerStatusAndRoomDescription() {
    String result = model.look();
    assertTrue(result.contains("Health"));
    assertTrue(result.contains("Weight"));
    assertTrue(result.contains("You are in"));
  }

  @Test
  void takeItemMovesItemIntoInventory() {
    assertTrue(model.takeItem("Key").contains("pick up Key"));
    assertTrue(model.getInventoryString().contains("Key"));
  }

  @Test
  void dropItemMovesItemBackIntoRoom() {
    model.takeItem("Key");
    assertTrue(model.dropItem("Key").contains("drop Key"));
    assertTrue(model.examine("Key").contains("A small key"));
  }

  @Test
  void useItemConsumesUsesAndEventuallyBreaksItem() {
    model.takeItem("Key");

    assertTrue(model.useItem("Key").contains("2 uses remaining"));
    model.useItem("Key");
    String finalUse = model.useItem("Key");

    assertTrue(finalUse.contains("broken"));
    assertTrue(model.getInventoryString().contains("empty"));
  }

  @Test
  void examineFindsInventoryItemsRoomItemsAndFixtures() {
    assertTrue(model.examine("Key").contains("A small key"));
    assertTrue(model.examine("Desk").contains("sturdy desk"));

    model.takeItem("Key");
    assertTrue(model.examine("Key").contains("A small key"));
  }

  @Test
  void answerPuzzleSolvesAnswerBasedPuzzle() {
    GameModel puzzleModel = new GameModel(TestWorldFactory.createAnswerPuzzleWorld());
    puzzleModel.setPlayerName("Tester");

    String result = puzzleModel.answerPuzzle("ALIGN");

    assertTrue(result.contains("Correct!"));
    assertTrue(puzzleModel.move("north").contains("ARCHIVE"));
  }

  @Test
  void answerPuzzleRejectsWrongAnswer() {
    GameModel puzzleModel = new GameModel(TestWorldFactory.createAnswerPuzzleWorld());
    puzzleModel.setPlayerName("Tester");

    assertTrue(puzzleModel.answerPuzzle("wrong").contains("not the correct answer"));
  }

  @Test
  void useItemSolvesItemBasedPuzzle() {
    GameModel puzzleModel = new GameModel(TestWorldFactory.createItemPuzzleWorld());
    puzzleModel.setPlayerName("Tester");
    puzzleModel.takeItem("Silver Key");

    String result = puzzleModel.useItem("Silver Key");

    assertTrue(result.contains("Puzzle solved"));
    assertTrue(puzzleModel.move("east").contains("TREASURE ROOM"));
  }

  @Test
  void enteringMonsterRoomTriggersAttackAndUsingSolutionDefeatsMonster() {
    GameModel monsterModel = new GameModel(TestWorldFactory.createMonsterWorld());
    monsterModel.setPlayerName("Tester");
    monsterModel.takeItem("Hair Clippers");

    String moveResult = monsterModel.move("east");
    String useResult = monsterModel.useItem("Hair Clippers");

    assertTrue(moveResult.contains("attacks"));
    assertTrue(moveResult.contains("Health: 80"));
    assertTrue(useResult.contains("defeated Teddy Bear"));
  }

  @Test
  void endGameSummaryIncludesRankAndScore() {
    String result = model.getEndGameSummary();
    assertTrue(result.contains("GAME OVER"));
    assertTrue(result.contains("Score"));
    assertTrue(result.contains("Rank"));
  }

  @Test
  void gameStartsActive() {
    assertFalse(model.isGameOver());
  }

  @Test
  void saveAndRestoreReturnPlayerToSavedState() {
    model.takeItem("Key");
    model.useItem("Key");

    assertEquals("Game saved successfully.", model.save());

    model.dropItem("Key");
    assertTrue(model.move("east").contains("SECOND ROOM"));

    String restoreResult = model.restore();

    assertTrue(restoreResult.contains("Game restored."));
    assertTrue(restoreResult.contains("START ROOM"));
    assertTrue(model.getInventoryString().contains("Key"));
  }
}
