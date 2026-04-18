package enginedriver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link GameEngineApp#main(String[])}, focused on the HW9 command-line argument
 * validation contract: every invocation that doesn't match one of the four supported formats must
 * print the exact "Incorrect command-line format" block and exit without launching the engine.
 */
class GameEngineAppTest {

  private static final String EXPECTED_ERROR_HEADER = "Incorrect command-line format for game engine.";
  private static final String EXPECTED_FORMATS_LINE = "Formats allowed:";
  private static final String EXPECTED_TEXT = "game_engine <filename> -text";
  private static final String EXPECTED_GRAPHICS = "game_engine <filename> -graphics";
  private static final String EXPECTED_BATCH_SRC = "game_engine <filename> -batch <source file>";
  private static final String EXPECTED_BATCH_SRC_TGT =
      "game_engine <filename> -batch <source file> <target file>";

  private PrintStream originalOut;
  private ByteArrayOutputStream captured;

  @BeforeEach
  void redirectStdout() {
    originalOut = System.out;
    captured = new ByteArrayOutputStream();
    System.setOut(new PrintStream(captured));
  }

  @AfterEach
  void restoreStdout() {
    System.setOut(originalOut);
  }

  /** Asserts the captured stdout contains the full spec-required usage-error block. */
  private void assertUsageErrorPrinted() {
    String out = captured.toString();
    assertTrue(out.contains(EXPECTED_ERROR_HEADER), () -> "missing header in: " + out);
    assertTrue(out.contains(EXPECTED_FORMATS_LINE), () -> "missing formats line in: " + out);
    assertTrue(out.contains(EXPECTED_TEXT), () -> "missing -text line in: " + out);
    assertTrue(out.contains(EXPECTED_GRAPHICS), () -> "missing -graphics line in: " + out);
    assertTrue(out.contains(EXPECTED_BATCH_SRC), () -> "missing -batch <source> line in: " + out);
    assertTrue(
        out.contains(EXPECTED_BATCH_SRC_TGT),
        () -> "missing -batch <source> <target> line in: " + out);
  }

  @Test
  void noArgsPrintsUsageError() throws IOException {
    GameEngineApp.main(new String[] {});
    assertUsageErrorPrinted();
  }

  @Test
  void onlyFilenamePrintsUsageError() throws IOException {
    GameEngineApp.main(new String[] {"alignquest.json"});
    assertUsageErrorPrinted();
  }

  @Test
  void unknownModeFlagPrintsUsageError() throws IOException {
    GameEngineApp.main(new String[] {"alignquest.json", "-turbo"});
    assertUsageErrorPrinted();
  }

  @Test
  void textModeWithExtraArgPrintsUsageError() throws IOException {
    GameEngineApp.main(new String[] {"alignquest.json", "-text", "oops"});
    assertUsageErrorPrinted();
  }

  @Test
  void graphicsModeWithExtraArgPrintsUsageError() throws IOException {
    GameEngineApp.main(new String[] {"alignquest.json", "-graphics", "oops"});
    assertUsageErrorPrinted();
  }

  @Test
  void batchModeWithoutSourcePrintsUsageError() throws IOException {
    GameEngineApp.main(new String[] {"alignquest.json", "-batch"});
    assertUsageErrorPrinted();
  }

  @Test
  void batchModeWithTooManyArgsPrintsUsageError() throws IOException {
    GameEngineApp.main(
        new String[] {"alignquest.json", "-batch", "src.txt", "tgt.txt", "extra"});
    assertUsageErrorPrinted();
  }

  @Test
  void usageErrorDoesNotAppearForValidGraphicsInvocation() throws IOException {
    // -graphics schedules work on the EDT and returns; since the file doesn't exist, the EDT task
    // will log a load error to System.err, but nothing should be written to stdout.
    GameEngineApp.main(new String[] {"definitely-not-a-real-file.json", "-graphics"});
    assertFalse(captured.toString().contains(EXPECTED_ERROR_HEADER));
  }
}
