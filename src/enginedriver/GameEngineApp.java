package enginedriver;

import controller.GameController;
import model.GameModel;
import model.GameWorld;
import model.IGameModel;
import model.JsonGameLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * Entry point for the Adventure Game Engine.
 * Wires together the JSON loader, game model, and controller,
 * then hands control to the controller to run the game loop.
 *
 * <p>Usage example:
 * <pre>
 *   GameEngineApp app = new GameEngineApp("game.json", System.in, System.out);
 *   app.start();
 * </pre>
 * </p>
 */
public class GameEngineApp {

    private final String gameFileName;
    private final Readable source;
    private final Appendable output;

    /**
     * Constructs a GameEngineApp with the specified game file and I/O streams.
     *
     * @param gameFileName the path to the JSON game data file
     * @param source       the input source for player commands (e.g. System.in)
     * @param output       the output target for game text (e.g. System.out)
     */
    public GameEngineApp(String gameFileName, Readable source, Appendable output) {
        this.gameFileName = gameFileName;
        this.source = source;
        this.output = output;
    }

    /**
     * Loads the game world from the JSON file, initializes the model
     * and controller, and starts the game loop.
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
     * <ul>
     *   <li>Synthetic input: feeds pre-written commands automatically</li>
     *   <li>Manual input: reads commands from the keyboard (System.in)</li>
     * </ul>
     *
     * @param args command-line arguments (not used)
     * @throws IOException if the game file cannot be read or output fails
     */
    public static void main(String[] args) throws IOException {
        String s = "Sir Mix-A-Lot\nT NOTEBOOK\nN\nT HAIR CLIPPERS\nT KEY\nD NOTEBOOK\nQuit";
        BufferedReader stringReader = new BufferedReader(new StringReader(s));
        GameEngineApp app = new GameEngineApp(
                "./resources/align_quest_game_elements.json",
                new InputStreamReader(System.in),
                System.out
        );
        app.start();
    }
}
