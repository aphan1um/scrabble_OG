package client.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import client.ScrabblePane;

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

    public ScrabbleBoardController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Hello World!");

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
