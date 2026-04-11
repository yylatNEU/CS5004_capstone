package view;

import java.util.List;

/**
 * Interface for the graphical game view. The Controller calls these methods to update the GUI in
 * response to model changes.
 */
public interface IGameView {

  /**
   * Registers the controller as the listener for GUI events.
   *
   * @param listener the event listener (typically the GraphicsController)
   */
  void setListener(ViewListener listener);

  /**
   * Updates the room image panel.
   *
   * @param imagePath the image filename to display (loaded from resources/images/)
   */
  void updateRoomImage(String imagePath);

  /**
   * Updates the description text panel.
   *
   * @param text the description text to display
   */
  void updateDescription(String text);

  /**
   * Updates the inventory list.
   *
   * @param itemNames the list of item names currently in the player's inventory
   */
  void updateInventory(List<String> itemNames);

  /**
   * Updates passable directions for the current room. Direction buttons remain clickable even when
   * a direction is blocked, so the controller can react with {@link #showMessage}; the view may
   * dim non-passable directions as a hint.
   *
   * @param north true if north is passable
   * @param south true if south is passable
   * @param east true if east is passable
   * @param west true if west is passable
   */
  void updateNavigationButtons(boolean north, boolean south, boolean east, boolean west);

  /**
   * Updates the health status label text.
   *
   * @param statusText the health status description
   */
  void updateHealthStatus(String statusText);

  /**
   * Displays a simple message dialog.
   *
   * @param title the dialog title
   * @param message the message text
   */
  void showMessage(String title, String message);

  /**
   * Displays an inspect dialog with an image and description.
   *
   * @param title the dialog title (e.g. "Inspecting...")
   * @param description the object description
   * @param imagePath the image filename to display
   */
  void showInspectDialog(String title, String description, String imagePath);

  /**
   * Displays an input dialog and returns the user's text input.
   *
   * @param title the dialog title
   * @param prompt the prompt text
   * @return the user's input, or {@code null} if cancelled
   */
  String promptInput(String title, String prompt);

  /**
   * Displays a selection dialog with a list of options.
   *
   * @param title the dialog title
   * @param options the selectable options
   * @return the selected option, or {@code null} if cancelled
   */
  String promptSelection(String title, List<String> options);

  /**
   * Displays the Game Over dialog with the player's final score.
   *
   * @param playerName the player's name
   * @param score the final score
   * @param imagePath the image to show (e.g. nighty_night.png or congratulations.png)
   */
  void showGameOver(String playerName, int score, String imagePath);

  /**
   * Displays the About the Game dialog.
   *
   * @param imagePath filename relative to {@code resources/images/}, e.g. {@link
   *     ImageUtils#TEAM_ABOUT_LOGO_PATH}
   */
  void showAbout(String imagePath);

  /**
   * Returns the name of the currently selected item in the inventory list.
   *
   * @return the selected item name, or {@code null} if nothing is selected
   */
  String getSelectedInventoryItem();

  /** Makes the main window visible. */
  void display();

  /** Closes and disposes the main window. */
  void dispose();
}
