package client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainLobbyController implements Initializable {
    @FXML
    private AnchorPane paneChat;
    @FXML
    private Button btnJoin;
    @FXML
    private Button btnCreate;

    private ChatBoxController chatBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatBox.fxml"));
        chatBox = new ChatBoxController();
        loader.setController(chatBox);


        // load & add chat box
        Node node = null;
        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        paneChat.getChildren().add(node);

        btnJoin.setOnAction(e -> {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/JoinLobby.fxml"));
            JoinLobbyController joinLobbyController = new JoinLobbyController();

            loader1.setController(joinLobbyController);

            Stage joinStage = new Stage();
            try {
                joinStage.setScene(new Scene(loader1.load()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }
}
