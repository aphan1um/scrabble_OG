package new_client;

import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.system.AnchorageSystem;
import com.dslplatform.json.DslJson;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.*;
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
        node1.setMinSize(500, 500);
        node1.dock(station, DockNode.DockPosition.LEFT);

        AnchorageSystem.installDefaultStyle();

        Scene scene = new Scene(station);
        primaryStage.setTitle("Scrabble Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
