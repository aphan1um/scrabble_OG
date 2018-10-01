package new_client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatBoxController implements Initializable {
    @FXML
    private Button btnSendMsg;
    @FXML
    private TextArea txtInput;

    public ChatBoxController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSendMsg.setFocusTraversable(false);
        txtInput.setFocusTraversable(false);
    }
}
