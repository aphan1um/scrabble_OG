package client;

import core.game.Agent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import client.controller.LoginFormController;
import client.listeners.ScrabbleClientListener;

public class ClientMain extends Application {
    public static Agent agentID;

    public static boolean appEnded = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/LoginForm.fxml"));
        loader.setController(new LoginFormController(primaryStage));

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Scrabble Login");
        primaryStage.setResizable(false);

        primaryStage.show();
    }
}
