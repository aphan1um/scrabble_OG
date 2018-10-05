package client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import client.util.StageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WaitDialogController implements Initializable {
    @FXML
    private ProgressIndicator progress;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }

    public static Stage createDialog(Stage stage) {
        FXMLLoader loader = new FXMLLoader(
                WaitDialogController.class.getResource("/WaitDialog.fxml"));
        loader.setController(new WaitDialogController());

        // ensure the dialog is owned by the parent stage
        Stage newStage = new Stage(StageStyle.TRANSPARENT);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(stage);
        newStage.setResizable(false);
        newStage.setOpacity(0.9);

        newStage.setOnShown(e -> StageUtils.centreStage(stage, newStage));

        try {
            Scene scene = new Scene((Parent)loader.load());
            // to make the dialog have round edges
            scene.setFill(Color.TRANSPARENT);
            newStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return newStage;
    }
}
