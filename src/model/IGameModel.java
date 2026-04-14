package model;

import java.util.List;

/** Defines the controller-facing contract for the game model. */
public interface IGameModel {

  /**
   * Creates the player with the given name in room 1.
   *
   * @param name the player name
   */
  void setPlayerName(String name);

  /**
   * Attempts to move the player in the given direction.
   *
   * @param direction the requested direction
   * @return the result message
   */
  String move(String direction);

  /**
   * Returns the current room description.
   *
   * @return the look result
   */
  String look();

  /**
   * Takes an item from the current room.
   *
   * @param itemName the item name
   * @return the result message
   */
  String takeItem(String itemName);

  /**
   * Drops an item into the current room.
   *
   * @param itemName the item name
   * @return the result message
   */
  String dropItem(String itemName);

  /**
   * Uses an item from the player's inventory.
   *
   * @param itemName the item name
   * @return the result message
   */
  String useItem(String itemName);

  /**
   * Examines a visible object by name.
   *
   * @param name the object name
   * @return the result message
   */
  String examine(String name);

  /**
   * Returns the inventory summary string.
   *
   * @return the inventory summary
   */
  String getInventoryString();

  /**
   * Attempts to solve the current puzzle with a text answer.
   *
   * @param answer the answer text
   * @return the result message
   */
  String answerPuzzle(String answer);

  /**
   * Returns whether the game is over.
   *
   * @return {@code true} if the player has no health remaining
   */
  boolean isGameOver();

  /**
   * Returns the final game summary.
   *
   * @return the summary text
   */
  String getEndGameSummary();

  /**
   * Saves the current game state.
   *
   * @return the save result message
   */
  String save();

  /**
   * Restores a previously saved game state.
   *
   * @return the restore result message
   */
  String restore();

  /**
   * Returns the room the player is currently in.
   *
   * @return the current room
   */
  Room getCurrentRoom();

  /**
   * Returns the current player object.
   *
   * @return the player
   */
  Player getPlayer();

  /**
   * Returns the name of this game as declared in the game JSON file.
   *
   * @return the game name string
   */
  String getGameName();

  /**
   * Returns the names of all items currently in the player's inventory.
   *
   * @return a list of item name strings
   */
  List<String> getInventoryItemNames();

  /**
   * Returns the names of all items currently sitting in the player's room.
   *
   * @return a list of item name strings
   */
  List<String> getRoomItemNames();

  /**
   * Returns the names of all objects the player can examine in the current room.
   *
   * @return a list of examinable object names, never {@code null}
   */
  List<String> getExaminableNames();
  
  /**
   * Get item image.
   * @param itemName name of the item
   * @return the picture file name.
   */
  String getItemImage(String itemName);
}
