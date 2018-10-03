package new_client.controller;

import core.ClientListener;
import core.game.Agent;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;
import new_client.ClientMain;
import server.ScrabbleServerListener;

import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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

        InputStream is = getClass().getResourceAsStream("../../../resources/scrabble.jpg");
        Image scrabble_img = new Image(is);

        // adjust image ratio
        grid.setPrefWidth(scrabble_img.getWidth() * IMG_RATIO);
        grid.getRowConstraints().get(0).setMinHeight(scrabble_img.getHeight() * IMG_RATIO);
        grid.getRowConstraints().get(0).setPrefHeight(scrabble_img.getHeight() * IMG_RATIO);
        imgScrabble.setImage(scrabble_img);

        btnConnect.setDefaultButton(true);

        // add events
        btnConnect.setOnAction(e -> {
            if (validateConnect(false))
                connect(false );
        });
        btnCreateGame.setOnAction(e -> {
            //WaitDialogController.showDialog((Stage)btnCreateGame.getScene().getWindow());
            if (validateConnect(true))
                connect(true);
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

    private void handleConnectError(Throwable th) {
        Exception ex = new Exception(th);
        String descript;

        if (th == null) {
            descript = "Cause of error unknown.";
        } else if (th instanceof UnknownHostException) {
            descript = "Unable to connect at specified IP address:\n" + th.getMessage();
        } else {
            descript = ex.toString();
        }

        new Alert(Alert.AlertType.ERROR,
                "There was an error while connecting. Description:\n\n" +
                descript)
                .showAndWait();
    }

    private void connect(boolean isHosting) {
        Stage stage = (Stage) btnConnect.getScene().getWindow();
        Stage dialog = WaitDialogController.createDialog(stage);

        // set player details
        ClientMain.agentID = new Agent(txtName.getText(), Agent.AgentType.PLAYER);

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (isHosting) {
                    ClientMain.server = new ScrabbleServerListener();
                    ClientMain.server.startListener(Integer.parseInt(txtPort.getText()));

                    return ClientMain.listener.startListener(
                            "localhost",
                            Integer.parseInt(txtPort.getText()));
                } else {
                    return ClientMain.listener.startListener(
                            txtIP.getText(),
                            Integer.parseInt(txtPort.getText()));
                }
            }
        };// load lobby window

        task.setOnRunning(e -> dialog.show());
        task.setOnSucceeded((e) -> {
            dialog.close();

            if ((Boolean)e.getSource().getValue()) {
                stage.close();
                LobbyController.createStage().show();
            } else {
                handleConnectError(null);
            }

        });
        // happens if exception is thrown (e.g. server doesn't exist)
        task.setOnFailed((e) -> {
            dialog.close();
            handleConnectError(task.getException());
        });

        Thread t = new Thread(task);
        t.start();
    }
}
