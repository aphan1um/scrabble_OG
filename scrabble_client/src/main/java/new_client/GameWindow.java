package new_client;

import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.system.AnchorageSystem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GameWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        DockStation station = AnchorageSystem.createStation();

        // END PREPARE TABLE SECTION
        Parent root = FXMLLoader.load(getClass().getResource("../../resources/ScrabbleBoard.fxml"));
        DockNode node1 = AnchorageSystem.createDock("Game Board",
                root);
        //node1.setMinSize(500, 500);
        node1.dock(station, DockNode.DockPosition.LEFT);

        Parent chat_root = FXMLLoader.load(getClass().getResource("../../resources/ChatBox.fxml"));
        DockNode node2 = AnchorageSystem.createDock("Chat", chat_root);
        //node2.setMinSize(500, 200);
        node2.dock(station, DockNode.DockPosition.BOTTOM, 0.75);

        AnchorageSystem.installDefaultStyle();

        Scene scene = new Scene(station, 600, 700);
        primaryStage.setTitle("Scrabble Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
