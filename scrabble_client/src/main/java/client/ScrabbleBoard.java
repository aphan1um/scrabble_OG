package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import client.controller.ScrabbleBoardController;
import client.util.StageUtils;

public class ScrabbleBoard extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(StageUtils.getResource("fxml/ScrabbleBoard.fxml"));

        loader.setController(new ScrabbleBoardController());

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
