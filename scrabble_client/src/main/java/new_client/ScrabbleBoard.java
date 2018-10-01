package new_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScrabbleBoard extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/ScrabbleBoard.fxml"));

        loader.setController(new ScrabbleBoardController());

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
