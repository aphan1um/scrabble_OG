package client.controller;

import client.Connections;
import client.util.StageUtils;
import core.game.Player;
import core.message.MessageEvent;
import core.message.MessageWrapper;
import core.messageType.MSGQuery;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private Stage waitDialog;

    private MessageEvent<MSGQuery> createResp = new MessageEvent<MSGQuery>() {
        @Override
        public MessageWrapper[] onMsgReceive(MSGQuery recMessage, Player sender) {
            switch (recMessage.getQueryType()) {
                case GAME_ALREADY_STARTED:
                    if (recMessage.getValue()) {
                        Platform.runLater(() -> waitDialog.close());
                        showDialogWarn("The lobby has already been made (game ongoing).");
                    } else {
                        // close window after connecting to lobby
                        Platform.runLater(() -> {
                            gameCreated = true;
                            waitDialog.close();
                            ((Stage)btnCreate.getScene().getWindow()).close();
                        });
                    }
                    break;
                case LOBBY_ALREADY_MADE:
                    Platform.runLater(() -> waitDialog.close());
                    showDialogWarn("The lobby has already been created by another player.");
                    break;
                default:
                    break;
            }
            return null;
        }
    };

    private void showDialogWarn(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, msg);
            StageUtils.dialogCenter((Stage) btnCancel.getScene().getWindow(), alert);
            alert.showAndWait();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Connections.getListener().getEventList().addEvents(createResp);

        btnCancel.setOnAction(e -> btnCancel.getScene().getWindow().hide());

        btnCreate.setOnAction(e -> {
            if (txtName.getText().isEmpty()) {
                showDialogWarn("Cannot create game: The lobby name is empty.");
                return;
            }

            waitDialog = WaitDialogController.createDialog(
                    (Stage)btnCancel.getScene().getWindow(),
                    "Attempting to create game...");

            waitDialog.show();
            Connections.getListener().createLobby(txtName.getText(), txtDescript.getText());
        });
    }

    public boolean hasGameCreated() {
        return gameCreated;
    }

    public void shutdown() {
        Connections.getListener().getEventList().removeEvents(createResp);
    }
}
