package client;

import client.controller.ChatBoxController;
import client.controller.ScoreBoxController;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.system.AnchorageSystem;
import core.game.LiveGame;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import client.controller.ScrabbleBoardController;
import client.util.StageUtils;

import java.io.IOException;

public class GameWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        DockStation station = AnchorageSystem.createStation();

        // END PREPARE TABLE SECTION
        FXMLLoader loader = new FXMLLoader(StageUtils.getResource("/fxml/ScrabbleBoard.fxml"));
        loader.setController(new ScrabbleBoardController(null));

        DockNode node1 = AnchorageSystem.createDock("Game Board",
                loader.load());
        //node1.setMinSize(500, 500);
        node1.dock(station, DockNode.DockPosition.LEFT);

        Parent chat_root = FXMLLoader.load(StageUtils.getResource("fxml/ChatBox.fxml"));
        DockNode node2 = AnchorageSystem.createDock("Chat", chat_root);
        //node2.setMinSize(500, 200);
        node2.dock(station, DockNode.DockPosition.BOTTOM, 0.75);

        AnchorageSystem.installDefaultStyle();

        Scene scene = new Scene(station, 600, 700);
        primaryStage.setTitle("Scrabble Game");
        primaryStage.setScene(scene);

        // TODO: This is silly with Swing and JavaFX
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        primaryStage.show();
    }

    public static void startApp(LiveGame initGame) {
        DockStation station = AnchorageSystem.createStation();

        FXMLLoader loader = new FXMLLoader(StageUtils.getResource("fxml/ScrabbleBoard.fxml"));
        loader.setController(new ScrabbleBoardController(initGame));

        DockNode node1 = null;
        try {
            node1 = AnchorageSystem.createDock("Game Board",
                    loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //node1.setMinSize(500, 500);
        node1.dock(station, DockNode.DockPosition.LEFT);

        Parent chat_root = null;
        loader = new FXMLLoader(StageUtils.getResource("fxml/ChatBox.fxml"));
        try {
            loader.setController(new ChatBoxController());
            chat_root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DockNode node2 = AnchorageSystem.createDock("Chat", chat_root);
        //node2.setMinSize(500, 200);
        node2.dock(station, DockNode.DockPosition.BOTTOM, 0.75);

        // add scoretable;
        loader = new FXMLLoader(StageUtils.getResource("fxml/ScoreBox.fxml"));
        loader.setController(new ScoreBoxController(initGame.getScores()));
        DockNode node3 = null;
        try {
            node3 = AnchorageSystem.createDock("Scores", loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        node3.dock(station, DockNode.DockPosition.RIGHT, 0.8);
        System.out.println("Got created");

        AnchorageSystem.installDefaultStyle();

        Stage stage = new Stage();
        Scene scene = new Scene(station, 800, 700);
        stage.setTitle("Scrabble Game");
        stage.setScene(scene);

        // TODO: Temporary fix
        stage.setOnCloseRequest(e -> System.exit(0));

        stage.show();
    }
}
