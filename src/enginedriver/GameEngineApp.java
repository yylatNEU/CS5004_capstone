package enginedriver;

import controller.GameController;
import model.GameModel;
import model.GameWorld;
import model.IGameModel;
import model.JsonGameLoader;

import java.io.IOException;

public class GameEngineApp {

    private final String gameFileName;
    private final Readable source;
    private final Appendable output;

    public GameEngineApp(String gameFileName, Readable source, Appendable output) {
        this.gameFileName = gameFileName;
        this.source = source;
        this.output = output;
    }

    public void start() throws IOException {

        JsonGameLoader loader = new JsonGameLoader();
        GameWorld world = loader.load(gameFileName);

        IGameModel model = new GameModel(world);

        GameController controller = new GameController(model, source, output);

        
        controller.play();
    }
}
