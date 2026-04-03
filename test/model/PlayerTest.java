package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerTest {

  // ─────────────────────────────────────────────
  // Constructor tests
  // ─────────────────────────────────────────────

  @Test
  void testValidPlayerCreation() {
    Player p = new Player("Alice", 1);

    assertEquals("Alice", p.getName());
    assertEquals(100, p.getHealth());
    assertEquals(0, p.getScore());
    assertEquals(1, p.getCurrentRoomNumber());
    assertNotNull(p.getInventory());
  }

  @Test
  void testConstructorInvalidName_null() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new Player(null, 1);
        });
  }

  @Test
  void testConstructorInvalidName_blank() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new Player("   ", 1);
        });
  }

  // ─────────────────────────────────────────────
  // takeDamage()
  // ─────────────────────────────────────────────

  @Test
  void testTakeDamageNormal() {
    Player p = new Player("Bob", 1);
    p.takeDamage(30);

    assertEquals(70, p.getHealth());
  }

  @Test
  void testTakeDamageToZero() {
    Player p = new Player("Bob", 1);
    p.takeDamage(150);

    assertEquals(0, p.getHealth()); // floor at 0
  }

  @Test
  void testTakeDamageNegative() {
    Player p = new Player("Bob", 1);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          p.takeDamage(-10);
        });
  }

  @Test
  void testTakeDamageZero() {
    Player p = new Player("Test", 1);

    p.takeDamage(0);

    assertEquals(100, p.getHealth()); // should remain unchanged
  }

  // ─────────────────────────────────────────────
  // addScore()
  // ─────────────────────────────────────────────

  @Test
  void testAddScoreNormal() {
    Player p = new Player("Carol", 1);
    p.addScore(50);

    assertEquals(50, p.getScore());
  }

  @Test
  void testAddScoreMultipleTimes() {
    Player p = new Player("Carol", 1);
    p.addScore(30);
    p.addScore(20);

    assertEquals(50, p.getScore());
  }

  // ─────────────────────────────────────────────
  // restoreHealth()
  // ─────────────────────────────────────────────

  @Test
  void testRestoreHealth() {
    Player p = new Player("Dave", 1);
    p.takeDamage(80);

    p.restoreHealth();

    assertEquals(100, p.getHealth());
  }

  // ─────────────────────────────────────────────
  // getHealthStatus()
  // ─────────────────────────────────────────────

  @Test
  void testHealthStatus() {
    Player p = new Player("Eve", 1);

    assertNotNull(p.getHealthStatus());

    p.takeDamage(90);
    assertNotNull(p.getHealthStatus());
  }

  // ─────────────────────────────────────────────
  // Room movement
  // ─────────────────────────────────────────────

  @Test
  void testSetAndGetRoom() {
    Player p = new Player("Frank", 1);

    p.setCurrentRoomNumber(5);

    assertEquals(5, p.getCurrentRoomNumber());
  }

  // ─────────────────────────────────────────────
  // toString()
  // ─────────────────────────────────────────────

  @Test
  void testToStringContainsInfo() {
    Player p = new Player("Grace", 2);
    String result = p.toString();

    assertTrue(result.contains("Grace"));
    assertTrue(result.contains("health="));
    assertTrue(result.contains("score="));
    assertTrue(result.contains("room=2"));
  }
}
