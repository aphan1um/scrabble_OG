package client.controller;

import client.Connections;
import core.game.Agent;
import core.message.MessageEvent;
import core.message.MessageWrapper;
import core.messageType.MSGLobbyList;
import core.messageType.MSGPlayerList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayerInviteDialog implements Initializable {
    @FXML
    private Button btnInvite;
    @FXML
    private TableView tblPlayers;

    private ObservableList<String> lobbyNames;
    private GUIEvents events;

    private class GUIEvents {
        MessageEvent<MSGPlayerList> listReceived = new MessageEvent<MSGPlayerList>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGPlayerList recMessage, Agent sender) {
                lobbyNames.addAll(recMessage.getPlayerNames());
                return null;
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        events = new GUIEvents();
        Connections.getListener().getEventList().addEvents(events.listReceived);

        TableColumn<String, String> colNames = new TableColumn<>("Player name");
        colNames.setCellValueFactory((param) -> { return new SimpleStringProperty(param.getValue()); });

        lobbyNames = FXCollections.observableArrayList();

        tblPlayers.setItems(lobbyNames);
        tblPlayers.getColumns().setAll(colNames);

        Connections.getListener().requestOnlinePlayers();
    }

    public void shutdown() {
        Connections.getListener().getEventList().removeEvents(events.listReceived);
    }
}
