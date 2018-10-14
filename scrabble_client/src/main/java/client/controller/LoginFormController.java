package client.controller;

import client.Connections;
import core.ConnectType;
import core.game.Agent;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import client.ClientMain;
import client.util.StageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
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

    private Stage stage;

    private static final double IMG_RATIO = 0.75;

    public LoginFormController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imgScrabble.fitHeightProperty().bind(imgPane.heightProperty());
        imgScrabble.fitWidthProperty().bind(imgPane.widthProperty());

        InputStream is = getClass().getResourceAsStream("/scrabble.jpg");
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

    /**
     * Checks if entered details aren't empty and port number is integer
     * @param isHosting If the player pressed the Create Host game button
     * @return
     */
    private boolean validateConnect(boolean isHosting) {
        if (txtName.getText().isEmpty() ||
                (!isHosting && txtIP.getText().isEmpty()) ||
        !txtPort.getText().matches("^[0-9]+$")) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "One of the required fields is empty and/or the port number is not a number.\n\n" +
                    "Please ensure the details are correct, then try again.");

            StageUtils.dialogCenter(stage, alert);
            alert.showAndWait();

            return false;
        }
        return true;
    }

    private void handleConnectError(Throwable th) {
        Exception ex = new Exception(th);
        String descript;

        ex.printStackTrace();

        if (th == null) {
            descript = "Cause of error unknown.";
        } else if (th instanceof UnknownHostException) {
            descript = "Unable to connect at specified IP address:\n" + th.getMessage();
        } else if (th instanceof ConnectException) {
            descript = "Sorry, there is no host at " + this.txtIP.getText() + ":" + this.txtPort.getText() + ".";
        } else if (th instanceof BindException) {
            descript = "Can't create server at " + this.txtIP.getText() + ":" + this.txtPort.getText() +"; a server is likely being hosted there.";
        } else {
            descript = ex.toString();
        }

        Alert alert = new Alert(Alert.AlertType.ERROR,
                "There was an error while connecting. Description:\n\n" +
                descript);
        StageUtils.dialogCenter(stage, alert);
        alert.showAndWait();
    }

    private void connect(boolean isHosting) {
        Stage dialog = WaitDialogController.createDialog(stage, "Connecting to server...");

        // set player details
        Connections.playerProperty().set(new Agent(txtName.getText(), Agent.AgentType.PLAYER));

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                if (isHosting) {
                    Connections.getServer().start(
                            Integer.parseInt(txtPort.getText()));
                }

                Connections.getListener().start(
                        isHosting ? "localhost" : txtIP.getText(),
                        Integer.parseInt(txtPort.getText()));

                return null;
            }
        };

        task.setOnRunning(e -> dialog.show());
        task.setOnSucceeded((e) -> {
            dialog.close();
            stage.close();

            if (Connections.getListener().getServerType() == ConnectType.LOCAL) {
                LobbyController.createStage(
                        isHosting ? "localhost" : txtIP.getText(),
                        txtPort.getText(), isHosting).show();
            } else {
                FXMLLoader loader = new FXMLLoader(
                        LobbyController.class.getResource("/MainLobby.fxml"));
                loader.setController(new MainLobbyController());

                Stage mainLobby = new Stage();
                try {
                    mainLobby.setScene(new Scene(loader.load()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                mainLobby.setOnCloseRequest(t -> {
                    Platform.exit();
                    System.exit(0);
                });

                mainLobby.show();
            }
        });

        // happens if exception is thrown (e.g. client.listeners doesn't exist)
        task.setOnFailed((e) -> {
            dialog.close();
            handleConnectError(task.getException());
        });

        Thread t = new Thread(task);
        t.start();
    }
}
