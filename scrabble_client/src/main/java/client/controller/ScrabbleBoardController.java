package client.controller;

import client.ClientMain;
import client.util.StageUtils;
import core.game.Agent;
import core.game.LiveGame;
import core.messageType.NewTurnMsg;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

import java.awt.*;
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
    public ScrabblePane scrabblePane;
    @FXML
    private Label lblTurn;
    @FXML
    private Label lblScore;

    private Stage popupStage;

    private LiveGame initGame;

    public ScrabbleBoardController(LiveGame initGame) {
        this.initGame = initGame;
    }

    private void updateScore() {
        lblScore.setText("Your Score: " + scrabblePane.getMark());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Debug (0 point constant)
        lblTurn.setText(String.format("It is player %s's turn", initGame.getCurrentTurn().getName()));
        lblScore.setText("Your Score: " + 0);

        btnSubmit.setFocusTraversable(false);
        btnPass.setFocusTraversable(false);
        btnClear.setFocusTraversable(false);
        btnVote.setFocusTraversable(false);

        btnClear.disableProperty().bind(scrabblePane.getCanvas().enabledProperty);
        btnSubmit.disableProperty().bind(scrabblePane.getCanvas().enabledProperty);
        btnPass.disableProperty().bind(scrabblePane.getCanvas().enabledProperty.not());

        btnPass.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ClientMain.listener.sendGameMove(null, null);
            }
        });

        // TODO: Dummy toggle enabled/disable test on board
        btnSubmit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(scrabblePane.getCanvas().enabledProperty.get()) {
                    return;
                } else {
                    Point p = scrabblePane.getCanvas().chosenCellProperty.get();

                    ClientMain.listener.sendGameMove(p,
                            scrabblePane.getCanvas().getLetter(p.x, p.y));
                    /**
                    System.out.println(scrabblePane.getMark());
                    updateScore();
                    scrabblePane.getCanvas().enabledProperty.set(!scrabblePane.getCanvas().enabledProperty.get());
                    scrabblePane.getCanvas().chosenCellProperty.set(null);
                    **/
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
                popupVoteScreen(null, null);
            }
        });
    }

    public void popupVoteScreen(String hor_str, String ver_str) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/VoteScreen.fxml"));
        VoteScreenForm voteForm = new VoteScreenForm();
        loader.setController(voteForm);

        popupStage = new Stage(StageStyle.UNDECORATED);
        popupStage.setOpacity(0.96);
        popupStage.initOwner(hbox.getScene().getWindow());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Voting Time");

        try {
            Scene voteScene = new Scene(loader.load());
            voteScene.setFill(Color.TRANSPARENT);
            popupStage.setScene(voteScene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        voteForm.displayStrings(hor_str, ver_str);
        popupStage.show();
    }

    public void closePopup() {
        if (popupStage != null)
            popupStage.close();
    }

    public void updateTurn(NewTurnMsg msg, ScoreBoxController scoreControl, ChatBoxController chatControl) {
        this.closePopup();

        lblTurn.setText(String.format("It is player %s's turn", msg.getNextPlayer().getName()));
        lblScore.setText("Your Score: " + scoreControl.scores.get(ClientMain.agentID));

        // inform last player's move
        String txtAppend = "";
        int scoreDiff = msg.getNewPoints() - scoreControl.scores.get(msg.getLastPlayer());
        if (msg.hasSkippedTurn()) {
            txtAppend = String.format("%s has skipped turn.", msg.getLastPlayer());
        } else if (scoreDiff == 0) {
            // TODO: EMPTY
        } else {
            txtAppend = String.format("%s has earned %d point"
                    + (scoreDiff == 1 ? "." : "s."), msg.getLastPlayer(), scoreDiff);
        }

        if (!txtAppend.isEmpty())
            chatControl.appendText(txtAppend + "\n", Color.DARKCYAN);

        scoreControl.updateScore(msg.getLastPlayer(), msg.getNewPoints());

        boolean isMyTurn = msg.getNextPlayer().equals(ClientMain.agentID);
        hbox.disableProperty().set(!isMyTurn);
        scrabblePane.getCanvas().enabledProperty.set(isMyTurn);
        scrabblePane.getCanvas().chosenCellProperty.set(null);
    }
}
