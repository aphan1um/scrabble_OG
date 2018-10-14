package client.controller;

import client.Connections;
import core.game.Agent;
import core.message.MessageEvent;
import core.message.MessageWrapper;
import core.messageType.MSGQuery;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateLobbyDialog implements Initializable {
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField txtDescript;
    @FXML
    private TextField txtName;

    private boolean gameCreated;

    private static MessageEvent<MSGQuery> joinQuery = new MessageEvent<MSGQuery>() {
        @Override
        public MessageWrapper[] onMsgReceive(MSGQuery recMessage, Agent sender) {
            return null;
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Connections.getListener().getEventList().addEvents(joinQuery);
        btnCancel.setOnAction(e -> btnCancel.getScene().getWindow().hide());

        btnCreate.setOnAction(e -> {
            Connections.getListener().createLobby(txtName.getText(), txtDescript.getText());
        });

        btnCreate.getScene().getWindow().setOnHiding(e -> {
            Connections.getListener().getEventList().removeEvents(joinQuery);
        });
    }

    public boolean hasGameCreated() {
        return gameCreated;
    }
}
