package client.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class VoteScreenForm implements Initializable {
    @FXML
    private Label horWord;
    @FXML
    private Label verWord;
    @FXML
    private Button horAccept;
    @FXML
    private Button horReject;
    @FXML
    private Button verAccept;
    @FXML
    private Button verReject;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        horAccept.visibleProperty().bind(Bindings.isEmpty(horWord.textProperty()).not());
        horReject.visibleProperty().bind(Bindings.isEmpty(horWord.textProperty()).not());

        verAccept.visibleProperty().bind(Bindings.isEmpty(verWord.textProperty()).not());
        verReject.visibleProperty().bind(Bindings.isEmpty(verWord.textProperty()).not());
    }

    public void displayStrings(String str_hor, String str_ver) {
        horWord.setText(str_hor);
        verWord.setText(str_ver);
    }
}
