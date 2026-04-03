package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for HealthStatus.
 *
 * <p>Verifies: - Correct mapping from health values - Boundary conditions - Display messages
 */
class HealthStatusTest {

  // ─────────────────────────────────────────────
  // ✅ Normal ranges
  // ─────────────────────────────────────────────

  @Test
  void testAwakeRange() {
    assertEquals(HealthStatus.AWAKE, HealthStatus.fromHealth(100));
    assertEquals(HealthStatus.AWAKE, HealthStatus.fromHealth(70));
  }

  @Test
  void testFatiguedRange() {
    assertEquals(HealthStatus.FATIGUED, HealthStatus.fromHealth(69));
    assertEquals(HealthStatus.FATIGUED, HealthStatus.fromHealth(50));
    assertEquals(HealthStatus.FATIGUED, HealthStatus.fromHealth(40));
  }

  @Test
  void testWoozyRange() {
    assertEquals(HealthStatus.WOOZY, HealthStatus.fromHealth(39));
    assertEquals(HealthStatus.WOOZY, HealthStatus.fromHealth(20));
    assertEquals(HealthStatus.WOOZY, HealthStatus.fromHealth(1));
  }

  @Test
  void testSleepRange() {
    assertEquals(HealthStatus.SLEEP, HealthStatus.fromHealth(0));
    assertEquals(HealthStatus.SLEEP, HealthStatus.fromHealth(-10)); // edge
  }

  // ─────────────────────────────────────────────
  // ⚠️ Boundary tests
  // ─────────────────────────────────────────────

  @Test
  void testBoundaryValues() {
    assertEquals(HealthStatus.SLEEP, HealthStatus.fromHealth(0));
    assertEquals(HealthStatus.WOOZY, HealthStatus.fromHealth(1));

    assertEquals(HealthStatus.WOOZY, HealthStatus.fromHealth(39));
    assertEquals(HealthStatus.FATIGUED, HealthStatus.fromHealth(40));

    assertEquals(HealthStatus.FATIGUED, HealthStatus.fromHealth(69));
    assertEquals(HealthStatus.AWAKE, HealthStatus.fromHealth(70));
  }

  // ─────────────────────────────────────────────
  // ✅ Display message
  // ─────────────────────────────────────────────

  @Test
  void testDisplayMessages() {
    assertEquals("You feel strong and ready.", HealthStatus.AWAKE.getDisplayMessage());

    assertEquals("You feel tired and worn down.", HealthStatus.FATIGUED.getDisplayMessage());

    assertEquals("You are barely standing...", HealthStatus.WOOZY.getDisplayMessage());

    assertEquals("You have fallen unconscious. Game over.", HealthStatus.SLEEP.getDisplayMessage());
  }
}
