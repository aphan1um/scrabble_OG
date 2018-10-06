package client.controller;

import client.ClientMain;
import client.Connections;
import core.game.Board;
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
import client.boardUI.BoardPane;
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
    public BoardPane boardPane;
    @FXML
    private Label lblTurn;
    @FXML
    private Label lblScore;

    private Board board;

    public ScrabbleBoardController(LiveGame initGame) {
        this.board = new Board(
                initGame.getBoard().getNumRows(),
                initGame.getBoard().getNumColumns());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boardPane.setBoard(board);

        btnSubmit.setFocusTraversable(false);
        btnPass.setFocusTraversable(false);
        btnClear.setFocusTraversable(false);
        btnVote.setFocusTraversable(false);

        btnClear.disableProperty().bind(boardPane.enabledProperty());
        btnSubmit.disableProperty().bind(boardPane.enabledProperty());
        btnPass.disableProperty().bind(boardPane.enabledProperty().not());

        btnPass.setOnMouseClicked(e ->
                Connections.getListener().sendGamePass());

        btnSubmit.setOnMouseClicked(e -> {
            if (!boardPane.enabledProperty().get()) {
                Point p = boardPane.chosenCellProperty().get();
                Connections.getListener().sendGameMove(p, board.get(p));
            }
        });

        btnClear.setOnMouseClicked(e -> {
            if (boardPane.chosenCellProperty().get() != null)
                board.empty(boardPane.chosenCellProperty().get());
        });
    }

    public void updateUI(NewTurnMsg msg, int myScore) {
        lblTurn.setText(String.format("It is player %s's turn", msg.getNextPlayer().getName()));
        lblScore.setText("Your Score: " + myScore);

        boolean isMyTurn = msg.getNextPlayer().equals(ClientMain.agentID);
        hbox.disableProperty().set(!isMyTurn);
        boardPane.enabledProperty().set(isMyTurn);
        boardPane.chosenCellProperty().set(null);
    }
}
