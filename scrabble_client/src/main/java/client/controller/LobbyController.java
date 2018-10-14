package client.controller;

import client.ClientMain;
import client.Connections;
import client.GameWindow;
import core.ConnectType;
import core.game.Agent;
import core.message.MessageEvent;
import core.message.MessageWrapper;
import core.messageType.MSGAgentChanged;
import core.messageType.MSGChat;
import core.messageType.MSGGameStatus;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {
    @FXML
    private ListView lstPlayers;
    @FXML
    private  ListView lstReceivers;
    @FXML
    private Button btnStartGame;
    @FXML
    private Button btnInvite;
    @FXML
    private AnchorPane chatPane;

    private ChatBoxController chatBox;

    private boolean isHosting;

    private class GUIEvents {
        // message received
        MessageEvent<MSGChat> chatEvent = new MessageEvent<MSGChat>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGChat recMessage, Agent sender) {
                chatBox.appendText(String.format("%s said:\t%s\n",
                        recMessage.getSender().getName(), recMessage.getChatMsg()), Color.BLACK);
                return null;
            }
        };

        // when client.listeners sends the initial list of players in lobby
        MessageEvent<MSGAgentChanged> getPlayersEvent = new MessageEvent<MSGAgentChanged>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGAgentChanged recMessage, Agent sender) {
                switch (recMessage.getStatus()) {
                    case JOINED:
                        Platform.runLater(() -> lstPlayers.getItems().addAll(recMessage.getAgents()));
                        break;
                    case DISCONNECTED:
                        Platform.runLater(() -> lstPlayers.getItems().removeAll(recMessage.getAgents()));
                        break;
                }

                return null;
            }
        };

        // when player joins or leaves the lobby
        MessageEvent<MSGAgentChanged> getPlayerStatus = new MessageEvent<MSGAgentChanged>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGAgentChanged recMessage, Agent sender) {
                // if host has left the lobby
                if (recMessage.hasHostLeft()) {
                    Platform.runLater(() ->
                            ClientMain.endApp("The host has left the lobby. The app will now close."));
                }

                for (Agent agent : recMessage.getAgents()) {
                    switch (recMessage.getStatus()) {
                        case JOINED:
                            chatBox.appendText(String.format("%s has joined the lobby.\n", agent),
                                    Color.GREEN);
                            break;
                        case DISCONNECTED:
                            chatBox.appendText(String.format("%s has left the lobby.\n", agent),
                                    Color.RED);
                            break;
                    }
                }

                return null;
            }
        };

        // host has announced the game to start
        MessageEvent<MSGGameStatus> gameStartEvent = new MessageEvent<MSGGameStatus>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGGameStatus recMessage, Agent sender) {
                Platform.runLater(() -> {
                    shutdown(); // clear events
                    ((Stage)btnStartGame.getScene().getWindow()).close();

                    try {
                        GameWindow gameWindow = new GameWindow(recMessage.getGameData());
                        gameWindow.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                return null;
            }
        };
    }

    private GUIEvents events;

    public LobbyController(boolean isHosting) {
        this.isHosting = isHosting;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // add chat box
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatBox.fxml"));
        chatBox = new ChatBoxController();
        loader.setController(chatBox);

        try {
            Node node = loader.load();

            chatPane.getChildren().add(node);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add events to clientlistener
        events = new GUIEvents();
        Connections.getListener().getEventList().addEvents(
                events.chatEvent,
                events.getPlayersEvent,
                events.getPlayerStatus,
                events.gameStartEvent);

        btnStartGame.setOnAction(e -> {
            Connections.getListener().sendGameStart();
            btnStartGame.disableProperty().set(true); // TODO: debug
        });

        // invite not allowed in local
        if (Connections.getListener().getServerType() == ConnectType.LOCAL) {
            btnInvite.setDisable(false);
        }

        btnStartGame.setDisable(!isHosting);
    }

    public void shutdown() {
        if (events != null) {
            Connections.getListener().getEventList().removeEvents(
                    events.chatEvent,
                    events.getPlayersEvent,
                    events.getPlayerStatus,
                    events.gameStartEvent);
        }
    }

    public static Stage createStage(String ip, String port, boolean isHosting) {
        FXMLLoader loader = new FXMLLoader(
                LobbyController.class.getResource("/LobbyForm.fxml"));
        LobbyController lobbyController = new LobbyController(isHosting);
        loader.setController(lobbyController);

        Stage lobbyStage = new Stage();
        try {
            lobbyStage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        lobbyStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        lobbyStage.setTitle(String.format("[User %s] @ %s:%s (Lobby - %s)",
                Connections.playerProperty().get().getName(), ip, port,
                Connections.getListener().getLobbyName()));

        Connections.getListener().requestLobbyDetails();
        return lobbyStage;
    }
}
