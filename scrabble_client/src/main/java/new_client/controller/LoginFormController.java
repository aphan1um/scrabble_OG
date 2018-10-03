package new_client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginFormController implements Initializable {
    @FXML
    private ImageView imgScrabble;
    @FXML
    private Pane imgPane;
    @FXML
    private GridPane grid;
    @FXML
    private TextField txtIP;
    @FXML
    private TextField txtPort;
    @FXML
    private TextField txtName;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnCreateGame;

    private static final double IMG_RATIO = 0.75;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imgScrabble.fitHeightProperty().bind(imgPane.heightProperty());
        imgScrabble.fitWidthProperty().bind(imgPane.widthProperty());

        InputStream is = getClass().getResourceAsStream("../../resources/scrabble.jpg");
        Image scrabble_img = new Image(is);

        // adjust image ratio
        grid.setPrefWidth(scrabble_img.getWidth() * IMG_RATIO);
        grid.getRowConstraints().get(0).setMinHeight(scrabble_img.getHeight() * IMG_RATIO);
        grid.getRowConstraints().get(0).setPrefHeight(scrabble_img.getHeight() * IMG_RATIO);
        imgScrabble.setImage(scrabble_img);

        btnConnect.setDefaultButton(true);

        // add events
        btnConnect.setOnAction(e -> {
            if (validateConnect(false)) {
                Stage stage = (Stage)btnConnect.getScene().getWindow();
                stage.close();

                // load lobby window
                LobbyController.createStage().show();
            }
        });
        btnCreateGame.setOnAction(e -> {
            WaitDialogController.showDialog((Stage)btnCreateGame.getScene().getWindow());
            validateConnect(true);
        });
    }

    private boolean validateConnect(boolean isHosting) {
        // ensure details aren't empty and port number is integer
        if (txtIP.getText().isEmpty() ||
                (!isHosting && txtIP.getText().isEmpty()) ||
        !txtPort.getText().matches("^[0-9]+$")) {
            new Alert(Alert.AlertType.WARNING,
                    "One of the details is empty and/or the port number is empty.\n\n" +
                    "Please ensure the details are correct, then try again.")
                    .showAndWait();

            return false;
        }

        return true;
    }

    /**
    private void waitForConnectResponse() {
        Task<Boolean> task = new Task<Boolean>() {
            @Override public Boolean call() {
                // do your operation in here
                return myService.operate();
            }
        };

        task.setOnRunning((e) -> loadingDialog.show());
        task.setOnSucceeded((e) -> {
            loadingDialog.hide();
            Boolean returnValue = task.get();
            // process return value again in JavaFX thread
        });
        task.setOnFailed((e) -> {
            // eventual error handling by catching exceptions from task.get()
        });
        new Thread(task).start();
    } */


}
