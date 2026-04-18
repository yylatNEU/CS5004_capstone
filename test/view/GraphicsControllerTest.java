package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import model.GameModel;
import model.IGameModel;
import model.TestWorldFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link GraphicsController} using a stub {@link IGameView}. */
class GraphicsControllerTest {

  private IGameModel model;
  private StubView view;
  private GraphicsController controller;

  @BeforeEach
  void setUp() {
    model = new GameModel(TestWorldFactory.createSimpleWorld());
    model.setPlayerName("Tester");
    view = new StubView();
    controller = new GraphicsController(model, view);
  }

  // ---------- onMove ----------

  @Test
  void onMoveShowsResult() {
    controller.onMove("E");
    assertTrue(view.lastMessage.contains("SECOND ROOM"));
  }

  @Test
  void onMoveWallShowsBlockedMessage() {
    controller.onMove("N");
    assertTrue(view.lastMessage.contains("wall"));
  }

  @Test
  void onMoveRefreshesView() {
    controller.onMove("E");
    assertTrue(view.lastDescription.contains("SECOND ROOM"));
    assertEquals(0, view.lastInventory.size());
    assertNotNull(view.lastHealthStatus);
  }

  // ---------- onTake ----------

  @Test
  void onTakeShowsNoItemsWhenRoomEmpty() {
    model.move("E");
    controller.onTake();
    assertEquals("Take", view.lastTitle);
    assertTrue(view.lastMessage.contains("no items"));
  }

  @Test
  void onTakePicksUpSelectedItem() {
    view.promptSelectionResult = "Key";
    controller.onTake();
    assertEquals("Take", view.lastTitle);
    assertTrue(view.lastMessage.contains("pick up"));
    assertTrue(view.lastInventory.contains("Key"));
  }

  @Test
  void onTakeCancelledStaysSilent() {
    view.promptSelectionResult = null;
    view.lastTitle = "UNTOUCHED";
    controller.onTake();
    assertEquals("UNTOUCHED", view.lastTitle);
  }

  // ---------- onExamine ----------

  @Test
  void onExamineShowsItemDescription() {
    view.promptSelectionResult = "Key";
    controller.onExamine();
    assertEquals("Key", view.lastTitle);
    assertTrue(view.lastMessage.contains("small key"));
  }

  @Test
  void onExamineEmptyRoomShowsNothing() {
    model.move("E");
    controller.onExamine();
    assertEquals("Examine", view.lastTitle);
    assertTrue(view.lastMessage.toLowerCase().contains("nothing"));
  }

  @Test
  void onExamineCancelledStaysSilent() {
    view.promptSelectionResult = null;
    view.lastTitle = "UNTOUCHED";
    controller.onExamine();
    assertEquals("UNTOUCHED", view.lastTitle);
  }

  // ---------- onAnswer ----------

  @Test
  void onAnswerCancelledDoesNothing() {
    view.promptInputResult = null;
    controller.onAnswer();
    assertEquals("", view.lastMessage);
  }

  @Test
  void onAnswerNoPuzzleHere() {
    view.promptInputResult = "whatever";
    controller.onAnswer();
    assertEquals("Answer", view.lastTitle);
    assertTrue(view.lastMessage.contains("no active puzzle"));
  }

  @Test
  void onAnswerSolvesPuzzleInPuzzleWorld() {
    IGameModel pm = new GameModel(TestWorldFactory.createAnswerPuzzleWorld());
    pm.setPlayerName("Tester");
    StubView pv = new StubView();
    GraphicsController pc = new GraphicsController(pm, pv);

    pv.promptInputResult = "align";
    pc.onAnswer();

    assertEquals("Answer", pv.lastTitle);
    assertTrue(pv.lastMessage.toLowerCase().contains("correct"));
  }

  // ---------- onInspect ----------

  @Test
  void onInspectEmptyInventory() {
    controller.onInspect();
    assertEquals("Inspect", view.lastTitle);
    assertTrue(view.lastMessage.toLowerCase().contains("empty"));
  }

  @Test
  void onInspectShowsDialogForSelectedItem() {
    model.takeItem("Key");
    view.promptSelectionResult = "Key";
    controller.onInspect();
    assertEquals(1, view.inspectShownCount);
    assertEquals("Key", view.lastInspectTitle);
    assertTrue(view.lastInspectDescription.contains("small key"));
  }

  @Test
  void onInspectCancelledStaysSilent() {
    model.takeItem("Key");
    view.promptSelectionResult = null;
    view.lastTitle = "UNTOUCHED";
    controller.onInspect();
    assertEquals("UNTOUCHED", view.lastTitle);
    assertEquals(0, view.inspectShownCount);
  }

  // ---------- onUse ----------

  @Test
  void onUseEmptyInventory() {
    controller.onUse();
    assertEquals("Use", view.lastTitle);
    assertTrue(view.lastMessage.toLowerCase().contains("empty"));
  }

  @Test
  void onUseItemShowsMessage() {
    model.takeItem("Key");
    view.promptSelectionResult = "Key";
    controller.onUse();
    assertEquals("Key", view.lastTitle);
    assertTrue(view.lastMessage.contains("used the key"));
  }

  @Test
  void onUseNoGameOverWhenPlayerAlive() {
    model.takeItem("Key");
    view.promptSelectionResult = "Key";
    controller.onUse();
    assertEquals(0, view.gameOverShownCount);
  }

  // ---------- onDrop ----------

  @Test
  void onDropEmptyInventory() {
    controller.onDrop();
    assertEquals("Drop", view.lastTitle);
    assertTrue(view.lastMessage.toLowerCase().contains("empty"));
  }

  @Test
  void onDropDropsSelectedItem() {
    model.takeItem("Key");
    view.promptSelectionResult = "Key";
    controller.onDrop();
    assertEquals("Key", view.lastTitle);
    assertTrue(view.lastMessage.contains("drop"));
    assertFalse(view.lastInventory.contains("Key"));
  }

  @Test
  void onDropCancelledStaysSilent() {
    model.takeItem("Key");
    view.promptSelectionResult = null;
    view.lastTitle = "UNTOUCHED";
    controller.onDrop();
    assertEquals("UNTOUCHED", view.lastTitle);
  }

  // ---------- onSave / onRestore ----------

  @Test
  void onSaveShowsSaveMessage() {
    controller.onSave();
    assertEquals("Save", view.lastTitle);
    assertTrue(view.lastMessage.contains("saved"));
  }

  @Test
  void onRestoreShowsRestoreMessage() {
    controller.onRestore();
    assertEquals("Restore", view.lastTitle);
    assertNotNull(view.lastMessage);
  }

  // ---------- onAbout / onExit ----------

  @Test
  void onAboutShowsAboutDialog() {
    controller.onAbout();
    assertEquals(1, view.aboutShownCount);
    assertEquals(ImageUtils.TEAM_ABOUT_LOGO_PATH, view.lastAboutImage);
  }

  @Test
  void onExitDisposesView() {
    controller.onExit();
    assertTrue(view.disposed);
  }

  // ---------- Stub view ----------

  static class StubView implements IGameView {
    String lastTitle = "";
    String lastMessage = "";
    String lastDescription = "";
    String lastRoomImage = "";
    String lastHealthStatus;
    List<String> lastInventory = new ArrayList<>();
    boolean disposed = false;

    String promptInputResult = "";
    String promptSelectionResult = null;

    int inspectShownCount = 0;
    String lastInspectTitle;
    String lastInspectDescription;
    String lastInspectImage;

    int aboutShownCount = 0;
    String lastAboutImage;

    int gameOverShownCount = 0;
    String lastGameOverName;
    int lastGameOverScore;
    String lastGameOverImage;

    @Override public void setListener(ViewListener l) {}
    @Override public void updateRoomImage(String p) { lastRoomImage = p; }
    @Override public void updateDescription(String t) { lastDescription = t; }
    @Override public void updateInventory(List<String> i) {
      lastInventory = new ArrayList<>(i);
    }
    @Override public void updateNavigationButtons(boolean n, boolean s, boolean e, boolean w) {}
    @Override public void updateHealthStatus(String t) { lastHealthStatus = t; }
    @Override public void showMessage(String title, String message) {
      lastTitle = title;
      lastMessage = message;
    }
    @Override public void showInspectDialog(String title, String description, String imagePath) {
      inspectShownCount++;
      lastInspectTitle = title;
      lastInspectDescription = description;
      lastInspectImage = imagePath;
    }
    @Override public String promptInput(String t, String p) { return promptInputResult; }
    @Override public String promptSelection(String t, List<String> o) {
      return promptSelectionResult;
    }
    @Override public void showGameOver(String name, int score, String imagePath) {
      gameOverShownCount++;
      lastGameOverName = name;
      lastGameOverScore = score;
      lastGameOverImage = imagePath;
    }
    @Override public void showAbout(String imagePath) {
      aboutShownCount++;
      lastAboutImage = imagePath;
    }
    @Override public String getSelectedInventoryItem() { return null; }
    @Override public void display() {}
    @Override public void dispose() { disposed = true; }
  }
}
