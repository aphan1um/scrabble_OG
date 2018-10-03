package new_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import new_client.controller.LoginFormController;
import new_client.util.StageUtils;

public class ClientMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(StageUtils.getResource("fxml/LoginForm.fxml"));
        loader.setController(new LoginFormController());

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Scrabble Login");
        primaryStage.setResizable(false);

        primaryStage.show();
    }
}
