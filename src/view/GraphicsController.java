package view;

import model.IGameModel;
import model.Room;
import model.Direction;
import java.util.List;

public class GraphicsController implements ViewListener{

  private final IGameModel model;
  private final IGameView view;

  public GraphicsController(IGameModel model, IGameView view) {
    this.model = model;
    this.view = view;
    this.view.setListener(this);
  }

  /**
   * Prompts the player for a name, renders the initial view state, and makes the window visible.
   * Call this once after constructing the controller. Splitting this from the constructor keeps
   * unit tests free of modal dialogs.
   */
  public void start() {
    String playerName = view.promptInput("Welcome", "Enter your name:");
    if (playerName == null || playerName.isBlank()) {
      playerName = "Player";
    }
    model.setPlayerName(playerName);
    refreshView();
    view.display();
  }

  /**
   * Refreshes all view components to reflect the current game state.
   * Updates the room image, description, navigation buttons,
   * health status, and inventory display.
   */
  private void refreshView() {
    Room room = model.getCurrentRoom();
    view.updateDescription(model.look());
    view.updateRoomImage(room.getPicture());
    view.updateNavigationButtons(
        room.getExit(Direction.NORTH) != 0,
        room.getExit(Direction.SOUTH) != 0,
        room.getExit(Direction.EAST) != 0,
        room.getExit(Direction.WEST) != 0
    );
    view.updateHealthStatus(
        model.getPlayer().getHealthStatus().getDisplayMessage()
    );
    view.updateInventory(model.getInventoryItemNames());
  }

  /**
   * Called when the player clicks a direction button. Successful moves refresh the view silently;
   * blocked moves (walls, locked puzzle / monster rooms) surface the blocking message in a dialog.
   *
   * @param direction one of "N", "S", "E", "W"
   */
  @Override
  public void onMove(String direction) {
    Room before = model.getCurrentRoom();
    String result = model.move(direction);
    Room after = model.getCurrentRoom();
    if (before == after) {
      view.showMessage("Move", result);
    }
    refreshView();
    checkGameOver();
  }

  /**
   * Called when the player clicks the Take button.
   */
  @Override
  public void onTake() {
    List<String> roomItems = model.getRoomItemNames();

    if (roomItems.isEmpty()) {
      view.showMessage("Take", "There are no items here.");
      return;
    }

    String selected = view.promptSelection("Select an item", roomItems);
    if (selected == null) return;  // Player cancels

    String result = model.takeItem(selected);
    view.showMessage("Take", result);
    refreshView();
  }

  /**
   * Called when the player clicks the Examine button.
   */
  @Override
  public void onExamine() {
    List<String> options = model.getExaminableNames();

    if (options.isEmpty()) {
      view.showMessage("Examine", "Nothing to examine here.");
      return;
    }

    String selected = view.promptSelection("Examine", options);
    if (selected == null) return;  // Player cancels

    String info = model.examine(selected);
    view.showMessage(selected, info);
  }

  /**
   * Called when the player clicks the Answer button.
   */
  @Override
  public void onAnswer() {
    String answer = view.promptInput("Answer Puzzle", "Enter your answer:");
    if (answer == null) return;  // Player cancels

    String result = model.answerPuzzle(answer);
    view.showMessage("Answer", result);
    refreshView();
    checkGameOver();
  }

  /**
   * Called when the player clicks the Inspect button (inventory).
   */
  @Override
  public void onInspect() {
    List<String> items = model.getInventoryItemNames();

    if (items.isEmpty()) {
      view.showMessage("Inspect", "Your inventory is empty.");
      return;
    }

    String selected = view.promptSelection("Inspect", items);
    if (selected == null) return;

    String description = model.examine(selected);
    String imagePath = model.getItemImage(selected);
    view.showInspectDialog(selected, description, imagePath);
  }

  /**
   * Called when the player clicks the Use button (inventory).
   */
  @Override
  public void onUse() {
    List<String> items = model.getInventoryItemNames();

    if (items.isEmpty()) {
      view.showMessage("Use", "Your inventory is empty.");
      return;
    }

    String selected = view.promptSelection("Use Item", items);
    if (selected == null) return;

    String message = model.useItem(selected);
    view.showMessage(selected, message);
    refreshView();
    checkGameOver();
  }

  /**
   * If the player has run out of health, shows the Game Over dialog. Called after any action that
   * may take damage ({@code onMove}, {@code onAnswer}, {@code onUse}).
   */
  private void checkGameOver() {
    if (model.isGameOver()) {
      view.showGameOver(
          model.getPlayer().getName(),
          model.getPlayer().getScore(),
          "nighty_night.png"
      );
    }
  }

  /**
   * Called when the player clicks the Drop button (inventory).
   */
  @Override
  public void onDrop() {
    List<String> items = model.getInventoryItemNames();

    if (items.isEmpty()) {
      view.showMessage("Drop", "Your inventory is empty.");
      return;
    }

    String selected = view.promptSelection("Drop Item", items);
    if (selected == null) return;

    String message = model.dropItem(selected);
    view.showMessage(selected, message);
    refreshView();
  }

  /**
   * Called when the player selects File > Save Game.
   */
  @Override
  public void onSave() {
    String result = model.save();
    view.showMessage("Save", result);
    refreshView();
  }

  /**
   * Called when the player selects File > Restore Game.
   */
  @Override
  public void onRestore() {
    String result = model.restore();
    view.showMessage("Restore", result);
    refreshView();
  }

  /**
   * Called when the player selects {@code About the Game...} from the File menu.
   */
  @Override
  public void onAbout() {
    view.showAbout(ImageUtils.TEAM_ABOUT_LOGO_PATH);
  }

  /**
   * Called when the player selects File > Exit or closes the window.
   */
  @Override
  public void onExit() {
    view.dispose();
  }
}
