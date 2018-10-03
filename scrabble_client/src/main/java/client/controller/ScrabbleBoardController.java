package client.controller;

import client.ClientMain;
import client.util.StageUtils;
import core.game.Agent;
import core.game.LiveGame;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import client.ScrabblePane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ScrabbleBoardController implements Initializable {
    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnPass;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnVote;
    @FXML
    private HBox hbox;
    @FXML
    private ScrabblePane scrabblePane;
    @FXML
    private Label lblTurn;
    @FXML
    private Label lblScore;

    private LiveGame state;

    public ScrabbleBoardController(LiveGame initGame) {
        state = initGame;
    }

    public void updateUIState() {
        lblTurn.setText(String.format("It is player %s's turn", state.getCurrentTurn().getName()));
        lblScore.setText("Your Score: " + state.getScore(ClientMain.agentID));

        hbox.disableProperty().set(state.getCurrentTurn().equals(ClientMain.agentID));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateUIState();

        btnSubmit.setFocusTraversable(false);
        btnPass.setFocusTraversable(false);
        btnClear.setFocusTraversable(false);
        btnVote.setFocusTraversable(false);

        // TODO: Dummy toggle enabled/disable test on board
        btnSubmit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                scrabblePane.getCanvas().enabledProperty.set(!scrabblePane.getCanvas().enabledProperty.get());
            }
        });


        btnClear.setOnMouseClicked(e -> scrabblePane.getCanvas().chosenCellProperty.set(null));

        btnVote.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                scrabblePane.getCanvas().chosenCellProperty.set(scrabblePane.getCanvas().getSelectedCell());
            }
        });
    }
}
