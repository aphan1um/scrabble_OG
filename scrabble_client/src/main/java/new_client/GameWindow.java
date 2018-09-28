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
        final SwingNode sNode = new SwingNode();


        prepareGameTable(sNode);
        DockStation station = AnchorageSystem.createStation();

        // PREPARE TABLE SECTION

        // prepare columns
        TableView<String> table = new TableView() {
            /*
            @Override
            public void resize(double width, double height) {
                super.resize(width, height);
                Pane header = (Pane) lookup("TableHeaderRow");
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setMaxHeight(0);
                header.setVisible(false);
            }
            */
        };
        //TableColumn[] cols = new TableColumn[20];

        for (int i = 0; i < 20; i++) {
            TableColumn<String, String> tc = new TableColumn<>("Row");
            tc.setCellFactory(TextFieldTableCell.forTableColumn());
            table.getColumns().add(tc);
        }
        table.setEditable(true);

        //table.getItems().add("abc");
        table.getItems().add("Hello");
        //table.getItems().add("abc");



        // END PREPARE TABLE SECTION

        DockNode node1 = AnchorageSystem.createDock("Game Board",
                table);
        node1.dock(station, DockNode.DockPosition.LEFT);

        AnchorageSystem.installDefaultStyle();

        Scene scene = new Scene(station);
        primaryStage.setTitle("Scrabble Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void prepareGameTable(final SwingNode sNode) {
        SwingUtilities.invokeLater(() ->
                sNode.setContent(new ScrabbleTable(20,20)));
    }
}
