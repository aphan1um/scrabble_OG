package client.controller;

import client.GameWindow;
import core.game.Agent;
import core.message.MessageEvent;
import core.message.MessageWrapper;
import core.messageType.AgentChangedMsg;
import core.messageType.ChatMsg;
import core.messageType.GameStatusMsg;
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
import client.ClientMain;
import client.util.StageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {
    @FXML
    private ListView lstPlayers;
    @FXML
    private Button btnStartGame;
    @FXML
    private Button btnKick;
    @FXML
    private AnchorPane chatPane;

    private ChatBoxController chatBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // add chat box
        FXMLLoader loader = new FXMLLoader(StageUtils.getResource("fxml/ChatBox.fxml"));
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

        // message received
        MessageEvent<ChatMsg> chatEvent = new MessageEvent<ChatMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(ChatMsg recMessage, Agent sender) {
                chatBox.appendText(String.format("%s said:\t%s\n",
                        recMessage.getSender().getName(), recMessage.getChatMsg()), Color.BLACK);
                return null;
            }
        };

        // when listeners sends the initial list of players in lobby
        MessageEvent<AgentChangedMsg> getPlayersEvent = new MessageEvent<AgentChangedMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(AgentChangedMsg recMessage, Agent sender) {
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
        MessageEvent<AgentChangedMsg> getPlayerStatus = new MessageEvent<AgentChangedMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(AgentChangedMsg recMessage, Agent sender) {
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
        MessageEvent<GameStatusMsg> gameStartEvent = new MessageEvent<GameStatusMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(GameStatusMsg recMessage, Agent sender) {
                // clear events
                // TODO: There's got to be a better way to do this..
                ClientMain.listener.eventList.removeEvents(chatEvent,
                        getPlayersEvent, getPlayerStatus, this);

                Platform.runLater(() -> {
                    ((Stage)btnKick.getScene().getWindow()).close();
                    GameWindow.startApp(recMessage.getGameData());
                });

                return null;
            }
        };

        // add events to clientlistener
        ClientMain.listener.eventList.addEvents(chatEvent,
                getPlayersEvent, getPlayerStatus, gameStartEvent);

        btnStartGame.setOnAction(e -> {
            ClientMain.listener.sendGameStart();
            btnStartGame.disableProperty().set(true); // TODO: debug
        });
    }


    public static Stage createStage() {
        Stage newStage = new Stage();

        FXMLLoader loader = new FXMLLoader(
                StageUtils.getResource("fxml/LobbyForm.fxml"));
        loader.setController(new LobbyController());

        try {
            newStage.setScene(new Scene((Parent)loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        newStage.setTitle("Lobby Room");

        return newStage;
    }
}
