package client.controller;

import client.ClientMain;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.InlineCssTextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatBoxController implements Initializable {
    @FXML
    protected Button btnSend;
    @FXML
    protected InlineCssTextArea rtChat;
    @FXML
    protected TextArea txtInput;

    public void appendText(String txt, Color c) {
        Platform.runLater(() -> {
            int prevPos = rtChat.getLength();
            rtChat.appendText(txt);

            // TODO: Thanks to https://stackoverflow.com/a/3607942 for the hint
            String hex =  String.format("#%02x%02x%02x",
                    (int)(c.getRed() * 255),
                    (int)(c.getGreen() * 255),
                    (int)(c.getBlue() * 255));

            rtChat.setStyle(prevPos, rtChat.getLength(),
                    String.format("-fx-fill: %s;", hex));
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        btnSend.setOnAction(e -> {
            ClientMain.listener.sendChatMessage(txtInput.getText());
            txtInput.setText("");
        });

        btnSend.disableProperty().bind(Bindings.isEmpty(txtInput.textProperty()));
    }
}
