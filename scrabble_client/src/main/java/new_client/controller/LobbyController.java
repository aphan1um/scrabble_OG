package new_client.controller;

import core.game.Agent;
import core.message.MessageEvent;
import core.message.MessageWrapper;
import core.messageType.AgentChangedMsg;
import core.messageType.ChatMsg;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import new_client.ClientMain;
import new_client.util.StageUtils;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class LobbyController implements Initializable {
    @FXML
    private Button btnSend;
    @FXML
    private InlineCssTextArea rtChat;
    @FXML
    private ListView lstPlayers;
    @FXML
    private TextArea txtInput;
    @FXML
    private Button btnStartGame;
    @FXML
    private Button btnKick;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSend.setOnAction(e -> {
            ClientMain.listener.sendChatMessage(txtInput.getText());
            txtInput.setText("");
        });

        btnSend.disableProperty().bind(Bindings.isEmpty(txtInput.textProperty()));

        MessageEvent<ChatMsg> chatEvent = new MessageEvent<ChatMsg>() {
            @Override
            public MessageWrapper onMsgReceive(ChatMsg recMessage, Set<Agent> agents, Agent sender) {
                appendText(String.format("%s said:\t%s\n",
                        recMessage.getSender().getName(), recMessage.getChatMsg()), Color.BLACK);
                return null;
            }
        };

        MessageEvent<AgentChangedMsg> getPlayersEvent = new MessageEvent<AgentChangedMsg>() {
            @Override
            public MessageWrapper onMsgReceive(AgentChangedMsg recMessage, Set<Agent> agents, Agent sender) {
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

        MessageEvent<AgentChangedMsg> getPlayerStatus = new MessageEvent<AgentChangedMsg>() {
            @Override
            public MessageWrapper onMsgReceive(AgentChangedMsg recMessage, Set<Agent> agents, Agent sender) {
                for (Agent agent : recMessage.getAgents()) {
                    switch (recMessage.getStatus()) {
                        case JOINED:
                            appendText(String.format("%s has joined the lobby.\n", agent),
                                    Color.GREEN);
                            break;
                        case DISCONNECTED:
                            appendText(String.format("%s has left the lobby.\n", agent),
                                    Color.RED);
                            break;
                    }
                }

                return null;
            }
        };

        // add events to clientlistener
        ClientMain.listener.eventList.addEvent(chatEvent);
        ClientMain.listener.eventList.addEvent(getPlayersEvent);
        ClientMain.listener.eventList.addEvent(getPlayerStatus);


        // pressing ENTER key sends the chat msg, SHIFT+ENTER creates a new line
        txtInput.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    if (event.isShiftDown())
                        txtInput.appendText("\n");
                    else {
                        event.consume(); // prevents new line after pressing ENTER
                        btnSend.fireEvent(new ActionEvent());
                    }
                }
            }
        });
    }

    public void appendText(String txt, Color c) {
        Platform.runLater(() -> {
            int prevPos = rtChat.getLength();
            rtChat.appendText(txt);

            String hex =  String.format("#%02x%02x%02x",
                    (int)(c.getRed() * 255),
                    (int)(c.getGreen() * 255),
                    (int)(c.getBlue() * 255));

            System.out.println(hex);

            rtChat.setStyle(prevPos, rtChat.getLength(),
                    String.format("-fx-fill: %s;", hex));
        });
    }

    public static Stage createStage() {
        FXMLLoader loader = new FXMLLoader(
                StageUtils.getResource("fxml/LobbyForm.fxml"));
        loader.setController(new LobbyController());

        try {
            Stage newStage = new Stage();
            newStage.setScene(new Scene((Parent)loader.load()));
            newStage.setTitle("Lobby Room");

            return newStage;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
