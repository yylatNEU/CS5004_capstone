package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link Monster}. */
class MonsterTest {

  private Monster monster;

  @BeforeEach
  void setUp() {
    monster =
        new Monster(
            "Robot",
            true,
            true,
            true,
            "Keycard",
            75,
            "A dormant robot.",
            "A robot blocks the hall.",
            -15,
            "2:Lab",
            true,
            "It shocks you.");
  }

  @Test
  void gettersExposeConfiguredState() {
    assertEquals("Robot", monster.getName());
    assertTrue(monster.isActive());
    assertTrue(monster.affectsTarget());
    assertTrue(monster.affectsPlayer());
    assertEquals("Keycard", monster.getSolution());
    assertEquals(75, monster.getValue());
    assertEquals(-15, monster.getDamage());
    assertEquals("2:Lab", monster.getTarget());
    assertTrue(monster.canAttack());
    assertEquals("It shocks you.", monster.getAttack());
  }

  @Test
  void deactivateMarksMonsterInactive() {
    monster.deactivate();
    assertFalse(monster.isActive());
  }

  @Test
  void toStringUppercasesName() {
    assertEquals("ROBOT", monster.toString());
  }
}
