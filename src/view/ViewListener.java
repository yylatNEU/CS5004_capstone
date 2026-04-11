package view;

/**
 * Callback interface from the View to the Controller. The Controller implements this interface to
 * receive user interaction events from the GUI.
 */
public interface ViewListener {

  /**
   * Called when the player clicks a direction button.
   *
   * @param direction one of "N", "S", "E", "W"
   */
  void onMove(String direction);

  /** Called when the player clicks the Take button. */
  void onTake();

  /** Called when the player clicks the Examine button. */
  void onExamine();

  /** Called when the player clicks the Answer button. */
  void onAnswer();

  /** Called when the player clicks the Inspect button (inventory). */
  void onInspect();

  /** Called when the player clicks the Use button (inventory). */
  void onUse();

  /** Called when the player clicks the Drop button (inventory). */
  void onDrop();

  /** Called when the player selects File > Save Game. */
  void onSave();

  /** Called when the player selects File > Restore Game. */
  void onRestore();

  /** Called when the player selects {@code About the Game...} from the File menu. */
  void onAbout();

  /** Called when the player selects File > Exit or closes the window. */
  void onExit();
}
