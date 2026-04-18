package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link Puzzle}. */
class PuzzleTest {

  @Test
  void answerPuzzleRecognizesQuotedSolution() {
    Puzzle puzzle =
        new Puzzle(
            "Riddle",
            true,
            true,
            false,
            "'Align'",
            30,
            "A riddle plaque.",
            "The plaque glows.",
            "1:Hall");

    assertTrue(puzzle.isAnswerSolution());
    assertEquals("Align", puzzle.getCleanSolution());
    assertTrue(puzzle.solve("align"));
    assertFalse(puzzle.solveWithItem("Align"));
  }

  @Test
  void itemPuzzleMatchesItemNamesCaseInsensitively() {
    Puzzle puzzle =
        new Puzzle(
            "Lock", true, true, false, "Key", 50, "A lock.", "The lock blocks the door.", "1:Hall");

    assertFalse(puzzle.isAnswerSolution());
    assertEquals("Key", puzzle.getCleanSolution());
    assertFalse(puzzle.solve("key"));
    assertTrue(puzzle.solveWithItem("key"));
    assertFalse(puzzle.solveWithItem(null));
  }

  @Test
  void deactivateMarksPuzzleInactiveAndToStringUppercasesName() {
    Puzzle puzzle =
        new Puzzle("Lock", true, true, false, "Key", 50, "A lock.", "Blocked.", "1:Hall");

    puzzle.deactivate();

    assertFalse(puzzle.isActive());
    assertEquals("LOCK", puzzle.toString());
  }

  @Test
  void pictureDefaultsToNullWhenLegacyConstructorUsed() {
    Puzzle puzzle =
        new Puzzle("Lock", true, true, false, "Key", 50, "A lock.", "Blocked.", "1:Hall");
    assertNull(puzzle.getPicture());
  }

  @Test
  void pictureIsExposedWhenProvidedToFullConstructor() {
    Puzzle puzzle =
        new Puzzle(
            "DARKNESS",
            true,
            true,
            true,
            "Lamp",
            150,
            "Darkness! You cannot see!",
            "It's dark!",
            "6:Kitchen",
            "darkness.png");
    assertEquals("darkness.png", puzzle.getPicture());
  }
}
