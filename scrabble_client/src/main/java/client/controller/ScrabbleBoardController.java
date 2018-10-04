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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        lblScore.setText("Your Score: " + state.getScores().get(ClientMain.agentID));

        hbox.disableProperty().set(!state.getCurrentTurn().equals(ClientMain.agentID));
    }

    private void updateScore() {
        lblScore.setText("Your Score: " + scrabblePane.getMark());
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
                if(scrabblePane.getCanvas().enabledProperty.get()) {
                    return;
                }
                else {
                    System.out.println(scrabblePane.getMark());
                    updateScore();
                    scrabblePane.getCanvas().enabledProperty.set(!scrabblePane.getCanvas().enabledProperty.get());
                    scrabblePane.getCanvas().chosenCellProperty.set(null);
                }
            }
        });


//        btnClear.setOnMouseClicked(e -> scrabblePane.getCanvas().chosenCellProperty.set(null));
        btnClear.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(scrabblePane.getCanvas().enabledProperty.get()) {
                    return;
                }
                else {
                    scrabblePane.getCanvas().removeLetter(scrabblePane.getLetterType_cell().x, scrabblePane.getLetterType_cell().y);
                    scrabblePane.getCanvas().chosenCellProperty.set(null);
                    scrabblePane.getCanvas().enabledProperty.set(!scrabblePane.getCanvas().enabledProperty.get());
                }
            }
        });

        btnVote.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /**
                scrabblePane.getCanvas().chosenCellProperty.set(scrabblePane.getCanvas().getSelectedCell());
                 **/
                FXMLLoader loader = new FXMLLoader(StageUtils.getResource("fxml/VoteScreen.fxml"));
                loader.setController(new VoteScreenForm());

                Stage popupStage = new Stage(StageStyle.TRANSPARENT);
                popupStage.setOpacity(0.95);
                popupStage.initOwner((Stage)hbox.getScene().getWindow());
                popupStage.initModality(Modality.APPLICATION_MODAL);

                try {
                    Scene voteScene = new Scene(loader.load());
                    popupStage.setScene(voteScene);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                popupStage.show();

            }
        });
    }
}
