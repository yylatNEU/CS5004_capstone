package enginedriver;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.SwingUtilities;

import controller.GameController;
import view.MainFrame;
import model.GameWorld;
import model.JsonGameLoader;
import model.IGameModel;
import model.GameModel;
import view.GraphicsController;

/**
 * Entry point for the Adventure Game Engine. Wires together the JSON loader, game model, and
 * controller, then hands control to the controller to run the game loop.
 *
 * <p>Usage example:
 *
 * <pre>
 *   GameEngineApp app = new GameEngineApp("game.json", System.in, System.out);
 *   app.start();
 * </pre>
 */
public class GameEngineApp {

  private final String gameFileName;
  private final Readable source;
  private final Appendable output;

  /**
   * Constructs a GameEngineApp with the specified game file and I/O streams.
   *
   * @param gameFileName the path to the JSON game data file
   * @param source the input source for player commands (e.g. System.in)
   * @param output the output target for game text (e.g. System.out)
   */
  public GameEngineApp(String gameFileName, Readable source, Appendable output) {
    this.gameFileName = gameFileName;
    this.source = source;
    this.output = output;
  }

  /**
   * Loads the game world from the JSON file, initializes the model and controller, and starts the
   * game loop.
   *
   * @throws IOException if the game file cannot be read or output fails
   */
  public void start() throws IOException {

    JsonGameLoader loader = new JsonGameLoader();
    GameWorld world = loader.load(gameFileName);

    IGameModel model = new GameModel(world);

    GameController controller = new GameController(model, source, output);

    controller.play();
  }

  /**
   * Application entry point for smoke testing and manual play.
   *
   * <p>Two modes are available (comment/uncomment as needed):
   *
   * <ul>
   *   <li>Synthetic input: feeds pre-written commands automatically
   *   <li>Manual input: reads commands from the keyboard (System.in)
   * </ul>
   *
   * @param args command-line arguments (not used)
   * @throws IOException if the game file cannot be read or output fails
   */
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("Usage: java -jar game_engine.jar <filename> -text|-graphics|-batch [source] [target]");
      return;
    }

    String filename = args[0];
    String mode = args[1];

    switch (mode) {
      case "-text":
        new GameEngineApp(
                filename,
                new InputStreamReader(System.in),
                System.out
        ).start();
        break;

      case "-graphics":
        SwingUtilities.invokeLater(() -> {
          try {
            JsonGameLoader loader = new JsonGameLoader();
            GameWorld world = loader.load(filename);
            IGameModel model = new GameModel(world);
            MainFrame frame = new MainFrame(world.getGameName());
            GraphicsController gc = new GraphicsController(model, frame);

            String playerName = frame.promptInput("Welcome", "Enter your name:");
            if (playerName == null || playerName.isBlank()) {
              playerName = "Player";
            }
            model.setPlayerName(playerName);

            frame.display();
          } catch (IOException e) {
            System.err.println("Failed to load game: " + e.getMessage());
          }
        });
        break;

      case "-batch":
        if (args.length == 3) {
          new GameEngineApp(
                  filename,
                  new FileReader(args[2]),
                  System.out
          ).start();
        } else if (args.length == 4) {
          new GameEngineApp(
                  filename,
                  new FileReader(args[2]),
                  new FileWriter(args[3])
          ).start();
        } else {
          System.out.println("Usage: -batch <source> [target]");
        }
        break;

      default:
        System.out.println("Unknown mode: " + mode);
    }
  }
}
