package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link Direction}. */
class DirectionTest {

  @Test
  void fromStringAcceptsFullNamesAndShortNames() {
    assertEquals(Direction.NORTH, Direction.fromString("north"));
    assertEquals(Direction.SOUTH, Direction.fromString("S"));
    assertEquals(Direction.EAST, Direction.fromString(" east "));
    assertEquals(Direction.WEST, Direction.fromString("W"));
  }

  @Test
  void fromStringRejectsInvalidInput() {
    assertThrows(IllegalArgumentException.class, () -> Direction.fromString(null));
    assertThrows(IllegalArgumentException.class, () -> Direction.fromString(" "));
    assertThrows(IllegalArgumentException.class, () -> Direction.fromString("up"));
  }
}
