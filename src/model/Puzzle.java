package model;

/**
 * Represents a puzzle in the adventure game world. A puzzle blocks the player's progress until it
 * is solved, either by providing a text answer or using the correct item.
 *
 * <p>Solution format:
 *
 * <ul>
 *   <li>If the solution is wrapped in single quotes (e.g. {@code 'Align'}), the player must type a
 *       text answer using the Answer command.
 *   <li>Otherwise, the solution is an item name and the player must use that item with the Use
 *       command.
 * </ul>
 */
public class Puzzle {
  private final String name;
  private boolean active;
  private final boolean affectsTarget;
  private final boolean affectsPlayer;
  private final String solution;
  private final int value;
  private final String description;
  private final String effects;
  private final String target;
  private final String picture;

  /**
   * Constructs a Puzzle without an associated picture. Delegates to the full constructor with
   * {@code picture = null}; kept for backwards compatibility with older tests / fixtures that
   * don't supply an image.
   *
   * @param name the name of this puzzle
   * @param active whether the puzzle is currently active
   * @param affectsTarget whether the puzzle blocks a room exit
   * @param affectsPlayer whether the puzzle affects the player's health
   * @param solution the solution string (item name or single-quoted text answer)
   * @param value the score awarded when this puzzle is solved
   * @param description the text displayed when the puzzle is examined
   * @param effects the text displayed when the puzzle is active in a room
   * @param target the room this puzzle affects, in "number:name" format
   */
  public Puzzle(
      String name,
      boolean active,
      boolean affectsTarget,
      boolean affectsPlayer,
      String solution,
      int value,
      String description,
      String effects,
      String target) {
    this(name, active, affectsTarget, affectsPlayer, solution, value, description, effects,
        target, null);
  }

  /**
   * Constructs a Puzzle with the given attributes.
   *
   * @param name the name of this puzzle
   * @param active whether the puzzle is currently active
   * @param affectsTarget whether the puzzle blocks a room exit
   * @param affectsPlayer whether the puzzle affects the player's health
   * @param solution the solution string (item name or single-quoted text answer)
   * @param value the score awarded when this puzzle is solved
   * @param description the text displayed when the puzzle is examined
   * @param effects the text displayed when the puzzle is active in a room
   * @param target the room this puzzle affects, in "number:name" format
   * @param picture the filename of the puzzle's image (shown while active), or {@code null}
   */
  public Puzzle(
      String name,
      boolean active,
      boolean affectsTarget,
      boolean affectsPlayer,
      String solution,
      int value,
      String description,
      String effects,
      String target,
      String picture) {
    this.name = name;
    this.active = active;
    this.affectsTarget = affectsTarget;
    this.affectsPlayer = affectsPlayer;
    this.solution = solution;
    this.value = value;
    this.description = description;
    this.effects = effects;
    this.target = target;
    this.picture = picture;
  }

  /**
   * Returns the name of this puzzle.
   *
   * @return the puzzle name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns whether this puzzle is currently active. An active puzzle blocks the room and overrides
   * its description.
   *
   * @return true if the puzzle is active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Returns whether this puzzle affects a target room by blocking its exits.
   *
   * @return true if the puzzle affects a target
   */
  public boolean affectsTarget() {
    return affectsTarget;
  }

  /**
   * Returns whether this puzzle affects the player directly (e.g. health).
   *
   * @return true if the puzzle affects the player
   */
  public boolean affectsPlayer() {
    return affectsPlayer;
  }

  /**
   * Returns the raw solution string as stored in the game data. Use {@link #getCleanSolution()} to
   * get the solution without single quotes.
   *
   * @return the raw solution string
   */
  public String getSolution() {
    return solution;
  }

  /**
   * Returns the score awarded to the player upon solving this puzzle.
   *
   * @return the point value
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the description shown when this puzzle is examined.
   *
   * @return the description text
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the effects text shown when this puzzle is active in a room. This text replaces the
   * room's normal description while the puzzle is active.
   *
   * @return the effects text
   */
  public String getEffects() {
    return effects;
  }

  /**
   * Returns the target of this puzzle in "roomNumber:roomName" format.
   *
   * @return the target string
   */
  public String getTarget() {
    return target;
  }

  /**
   * Returns the puzzle's picture filename. This image is shown in the view while the puzzle is
   * active, replacing the room's default picture.
   *
   * @return the picture filename, or {@code null} if none is set
   */
  public String getPicture() {
    return picture;
  }

  /**
   * Returns whether this puzzle requires a text answer rather than an item. A puzzle requires a
   * text answer if its solution is wrapped in single quotes. For example, {@code 'Align'} is a text
   * answer, while {@code Ticket} is an item.
   *
   * @return true if the solution is a text answer
   */
  public boolean isAnswerSolution() {
    return solution != null && solution.startsWith("'") && solution.endsWith("'");
  }

  /**
   * Returns the solution string with surrounding single quotes removed. If the solution is not a
   * text answer, returns the raw solution unchanged.
   *
   * @return the clean solution string
   */
  public String getCleanSolution() {
    if (isAnswerSolution()) {
      return solution.substring(1, solution.length() - 1);
    }
    return solution;
  }

  /**
   * Deactivates this puzzle, marking it as solved. Once deactivated, the puzzle no longer blocks
   * the room or affects the player.
   */
  public void deactivate() {
    this.active = false;
  }

  /**
   * Checks whether the given text answer solves this puzzle. Comparison is case-insensitive. For
   * item-based puzzles, use {@link #solveWithItem(String)} instead.
   *
   * @param input the player's text answer
   * @return true if the answer matches the solution
   */
  public boolean solve(String input) {
    if (solution.startsWith("'")) {
      return input.equalsIgnoreCase(getCleanSolution());
    } else {
      return input.equals(solution);
    }
  }

  /**
   * Checks whether the given item name solves this puzzle. Only applies to item-based puzzles
   * (solution without single quotes). Comparison is case-insensitive.
   *
   * @param itemName the name of the item being used
   * @return true if the item matches the solution, false if null or wrong type
   */
  public boolean solveWithItem(String itemName) {
    if (itemName == null) {
      return false;
    }
    if (isAnswerSolution()) {
      return false;
    }
    return itemName.equalsIgnoreCase(solution);
  }

  /**
   * Returns the puzzle name in uppercase for display purposes.
   *
   * @return the uppercase puzzle name
   */
  @Override
  public String toString() {
    return name.toUpperCase();
  }
}
