package model;

/**
 * Represents a monster in the adventure game world. A monster blocks the player's progress and may
 * attack the player when they attempt to move. The monster is defeated by using the correct item
 * against it.
 */
public class Monster {
  private final String name;
  private boolean active;
  private final boolean affectsTarget;
  private final boolean affectsPlayer;
  private final String solution;
  private final int value;
  private final String description;
  private final String effects;
  private final int damage;
  private final String target;
  private final boolean canAttack;
  private final String attack;
  private final String picture;

  /**
   * Constructs a Monster without an associated picture. Delegates to the full constructor with
   * {@code picture = null}; kept for backwards compatibility with older tests / fixtures that don't
   * supply an image.
   *
   * @param name the name of this monster
   * @param active whether the monster is currently active
   * @param affectsTarget whether the monster blocks a room exit
   * @param affectsPlayer whether the monster can damage the player
   * @param solution the name of the item required to defeat this monster
   * @param value the score awarded when this monster is defeated
   * @param description the text displayed when the monster is at rest
   * @param effects the text displayed when the monster is active in a room
   * @param damage the amount of health the monster removes per attack (negative value)
   * @param target the room this monster affects, in "number:name" format
   * @param canAttack whether this monster actively attacks the player on movement
   * @param attack the text displayed when the monster attacks the player
   */
  public Monster(
      String name,
      boolean active,
      boolean affectsTarget,
      boolean affectsPlayer,
      String solution,
      int value,
      String description,
      String effects,
      int damage,
      String target,
      boolean canAttack,
      String attack) {
    this(name, active, affectsTarget, affectsPlayer, solution, value, description, effects,
        damage, target, canAttack, attack, null);
  }

  /**
   * Constructs a Monster with the given attributes.
   *
   * @param name the name of this monster
   * @param active whether the monster is currently active
   * @param affectsTarget whether the monster blocks a room exit
   * @param affectsPlayer whether the monster can damage the player
   * @param solution the name of the item required to defeat this monster
   * @param value the score awarded when this monster is defeated
   * @param description the text displayed when the monster is at rest
   * @param effects the text displayed when the monster is active in a room
   * @param damage the amount of health the monster removes per attack (negative value)
   * @param target the room this monster affects, in "number:name" format
   * @param canAttack whether this monster actively attacks the player on movement
   * @param attack the text displayed when the monster attacks the player
   * @param picture the filename of the monster's image (shown while active), or {@code null}
   */
  public Monster(
      String name,
      boolean active,
      boolean affectsTarget,
      boolean affectsPlayer,
      String solution,
      int value,
      String description,
      String effects,
      int damage,
      String target,
      boolean canAttack,
      String attack,
      String picture) {
    this.name = name;
    this.active = active;
    this.affectsTarget = affectsTarget;
    this.affectsPlayer = affectsPlayer;
    this.solution = solution;
    this.value = value;
    this.description = description;
    this.effects = effects;
    this.damage = damage;
    this.target = target;
    this.canAttack = canAttack;
    this.attack = attack;
    this.picture = picture;
  }

  /**
   * Returns the name of this monster.
   *
   * @return the monster name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns whether this monster is currently active. An active monster blocks the room and
   * overrides its description.
   *
   * @return true if the monster is active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Returns whether this monster blocks a room exit.
   *
   * @return true if the monster affects a target room
   */
  public boolean affectsTarget() {
    return affectsTarget;
  }

  /**
   * Returns whether this monster can damage the player.
   *
   * @return true if the monster affects the player's health
   */
  public boolean affectsPlayer() {
    return affectsPlayer;
  }

  /**
   * Returns the name of the item required to defeat this monster.
   *
   * @return the solution item name
   */
  public String getSolution() {
    return solution;
  }

  /**
   * Returns the score awarded to the player upon defeating this monster.
   *
   * @return the point value
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the description shown when this monster is at rest (defeated).
   *
   * @return the description text
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the effects text shown when this monster is active in a room. This text replaces the
   * room's normal description while the monster is active.
   *
   * @return the effects text
   */
  public String getEffects() {
    return effects;
  }

  /**
   * Returns the damage this monster inflicts per attack. The value is negative (e.g. -15), and
   * should be applied using {@code Math.abs(getDamage())} when calling {@code Player.takeDamage()}.
   *
   * @return the damage value (negative integer)
   */
  public int getDamage() {
    return damage;
  }

  /**
   * Returns the target of this monster in "roomNumber:roomName" format.
   *
   * @return the target string
   */
  public String getTarget() {
    return target;
  }

  /**
   * Returns whether this monster actively attacks the player on movement. If true, the monster
   * attacks whenever the player attempts to move while in the same room.
   *
   * @return true if the monster can attack
   */
  public boolean canAttack() {
    return canAttack;
  }

  /**
   * Returns the attack description shown when this monster attacks the player.
   *
   * @return the attack text
   */
  public String getAttack() {
    return attack;
  }

  /**
   * Returns the monster's picture filename. This image is shown in the view while the monster is
   * active, replacing the room's default picture.
   *
   * @return the picture filename, or {@code null} if none is set
   */
  public String getPicture() {
    return picture;
  }

  /**
   * Deactivates this monster, marking it as defeated. Once deactivated, the monster no longer
   * blocks the room or attacks the player.
   */
  public void deactivate() {
    this.active = false;
  }

  /**
   * Returns the monster name in uppercase for display purposes.
   *
   * @return the uppercase monster name
   */
  @Override
  public String toString() {
    return name.toUpperCase();
  }
}
