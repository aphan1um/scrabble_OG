package client;

import client.controller.ChatBoxController;
import client.controller.ScoreBoxController;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.system.AnchorageSystem;
import core.game.Agent;
import core.game.LiveGame;
import core.message.MessageEvent;
import core.message.MessageWrapper;
import core.messageType.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import client.controller.ScrabbleBoardController;
import client.util.StageUtils;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

public class GameWindow extends Application {

    private ChatBoxController chatBox;
    private ScrabbleBoardController scrabbleBoard;
    private ScoreBoxController scoreBoard;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        DockStation station = AnchorageSystem.createStation();

        // END PREPARE TABLE SECTION
        FXMLLoader loader = new FXMLLoader(StageUtils.getResource("/fxml/ScrabbleBoard.fxml"));
        loader.setController(new ScrabbleBoardController(null));

        DockNode node1 = AnchorageSystem.createDock("Game Board",
                loader.load());
        //node1.setMinSize(500, 500);
        node1.dock(station, DockNode.DockPosition.LEFT);

        Parent chat_root = FXMLLoader.load(StageUtils.getResource("fxml/ChatBox.fxml"));
        DockNode node2 = AnchorageSystem.createDock("Chat", chat_root);
        //node2.setMinSize(500, 200);
        node2.dock(station, DockNode.DockPosition.BOTTOM, 0.75);

        AnchorageSystem.installDefaultStyle();

        Scene scene = new Scene(station, 600, 700);
        primaryStage.setTitle("Scrabble Game");
        primaryStage.setScene(scene);

        // TODO: This is silly with Swing and JavaFX
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        primaryStage.show();
    }

    public GameWindow(LiveGame initGame) {
        DockStation station = AnchorageSystem.createStation();

        FXMLLoader loader = new FXMLLoader(StageUtils.getResource("fxml/ScrabbleBoard.fxml"));
        scrabbleBoard = new ScrabbleBoardController(initGame);
        loader.setController(scrabbleBoard);

        DockNode node1 = null;
        try {
            node1 = AnchorageSystem.createDock("Game Board",
                    loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //node1.setMinSize(500, 500);
        node1.dock(station, DockNode.DockPosition.LEFT);

        Parent chat_root = null;
        loader = new FXMLLoader(StageUtils.getResource("fxml/ChatBox.fxml"));
        try {
            chatBox = new ChatBoxController();
            loader.setController(chatBox);
            chat_root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DockNode node2 = AnchorageSystem.createDock("Chat", chat_root);
        //node2.setMinSize(500, 200);
        node2.dock(station, DockNode.DockPosition.BOTTOM, 0.75);

        // add scoretable;
        loader = new FXMLLoader(StageUtils.getResource("fxml/ScoreBox.fxml"));
        scoreBoard = new ScoreBoxController(initGame.getScores());
        loader.setController(scoreBoard);
        DockNode node3 = null;
        try {
            node3 = AnchorageSystem.createDock("Scores", loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        node3.dock(station, DockNode.DockPosition.RIGHT, 0.8);
        System.out.println("Got created");

        AnchorageSystem.installDefaultStyle();

        Stage stage = new Stage();
        Scene scene = new Scene(station, 800, 700);
        stage.setTitle(String.format("[%s] Scrabble Game", ClientMain.agentID.getName()));
        stage.setScene(scene);

        // TODO: Temporary fix
        stage.setOnCloseRequest(e -> System.exit(0));

        addEvents();

        stage.show();
    }

    public void addEvents() {
        // message received
        MessageEvent<ChatMsg> chatEvent = new MessageEvent<ChatMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(ChatMsg recMessage, Agent sender) {
                chatBox.appendText(String.format("%s said:\t%s\n",
                        recMessage.getSender().getName(), recMessage.getChatMsg()), Color.BLACK);
                return null;
            }
        };

        // received move from player
        MessageEvent<GameActionMsg> actionEvent = new MessageEvent<GameActionMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(GameActionMsg recMessage, Agent sender) {
                Platform.runLater(() -> {
                    Point p = recMessage.getMoveLocation();

                    scrabbleBoard.scrabblePane.getCanvas().setLetter(p.x, p.y, recMessage.getLetter());

                    Map<GameVoteMsg.Orientation, String> strMap =
                            GameVoteMsg.getValidOrientations(scrabbleBoard.scrabblePane.getCanvas().letters,
                                    p);

                    scrabbleBoard.popupVoteScreen(strMap.get(GameVoteMsg.Orientation.HORIZONTAL),
                            strMap.get(GameVoteMsg.Orientation.VERTICAL));
                });

                return null;
            }
        };

        MessageEvent<NewTurnMsg> newTurnEvent = new MessageEvent<NewTurnMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(NewTurnMsg recMessage, Agent sender) {
                Platform.runLater(() -> {
                    scrabbleBoard.closePopup();
                    scoreBoard.updateScore(recMessage.getLastPlayer(), recMessage.getNewPoints());
                    scrabbleBoard.updateTurn(recMessage.getNextPlayer(),
                            scoreBoard.scores.get(ClientMain.agentID));
                });
                return null;
            }
        };

        MessageEvent<GameStatusMsg> gameEndEvent = new MessageEvent<GameStatusMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(GameStatusMsg recMessage, Agent sender) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.INFORMATION,
                            "Game has ended. App will now close.").showAndWait();
                    System.exit(0);

                });

                return null;
            }
        };

        MessageEvent<AgentChangedMsg> playerLeftEvent = new MessageEvent<AgentChangedMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(AgentChangedMsg recMessage, Agent sender) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.WARNING,
                            "A player has disconnected from the game. App will now close.")
                            .showAndWait();
                    System.exit(0);
                });

                return null;
            }
        };

        ClientMain.listener.eventList.addEvents(chatEvent,
                actionEvent, newTurnEvent, gameEndEvent, playerLeftEvent);
    }
}
