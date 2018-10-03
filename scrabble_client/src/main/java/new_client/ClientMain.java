package new_client;

import core.game.Agent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import new_client.controller.LoginFormController;
import new_client.util.StageUtils;
import server.ScrabbleServerListener;

public class ClientMain extends Application {
    public static ScrabbleClientListener listener;
    public static ScrabbleServerListener server;
    public static Agent agentID;

    public static void main(String[] args) {
        listener = new ScrabbleClientListener();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(StageUtils.getResource("fxml/LoginForm.fxml"));
        loader.setController(new LoginFormController(primaryStage));

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Scrabble Login");
        primaryStage.setResizable(false);

        primaryStage.show();
    }
}
