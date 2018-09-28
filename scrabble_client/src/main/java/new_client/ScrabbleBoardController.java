package new_client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ScrabbleBoardController {
    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnPass;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnVote;

    public ScrabbleBoardController() {}

    @FXML
    public void initialize() {
        System.out.println("Hello World!");

        btnSubmit.setFocusTraversable(false);
        btnPass.setFocusTraversable(false);
        btnClear.setFocusTraversable(false);
        btnVote.setFocusTraversable(false);
    }
}
