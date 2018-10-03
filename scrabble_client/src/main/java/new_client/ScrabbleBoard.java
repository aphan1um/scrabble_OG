package new_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import new_client.controller.ScrabbleBoardController;

public class ScrabbleBoard extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/ScrabbleBoard.fxml"));

        loader.setController(new ScrabbleBoardController());

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
