package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import model.GameModel;
import model.IGameModel;
import model.TestWorldFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.GraphicsController;

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
  void onSaveShowsSaveMessage() {
    controller.onSave();
    assertEquals("Save", view.lastTitle);
    assertTrue(view.lastMessage.contains("saved"));
  }

  @Test
  void onExitDisposesView() {
    controller.onExit();
    assertTrue(view.disposed);
  }

  @Test
  void onTakeShowsNoItemsWhenRoomEmpty() {
    model.move("E");
    controller.onTake();
    assertEquals("Take", view.lastTitle);
  }

  @Test
  void onAnswerCancelledDoesNothing() {
    view.promptInputResult = null;
    controller.onAnswer();
    assertEquals("", view.lastMessage);
  }

  static class StubView implements IGameView {
    String lastTitle = "";
    String lastMessage = "";
    boolean disposed = false;
    String promptInputResult = "";
    String promptSelectionResult = null;

    public void setListener(ViewListener l) {}
    public void updateRoomImage(String p) {}
    public void updateDescription(String t) {}
    public void updateInventory(List<String> i) {}
    public void updateNavigationButtons(boolean n, boolean s, boolean e, boolean w) {}
    public void updateHealthStatus(String t) {}
    public void showMessage(String title, String message) {
      lastTitle = title;
      lastMessage = message;
    }
    public void showInspectDialog(String t, String d, String p) {}
    public String promptInput(String t, String p) { return promptInputResult; }
    public String promptSelection(String t, List<String> o) { return promptSelectionResult; }
    public void showGameOver(String n, int s, String p) {}
    public void showAbout(String p) {}
    public String getSelectedInventoryItem() { return null; }
    public void display() {}
    public void dispose() { disposed = true; }
  }
}
