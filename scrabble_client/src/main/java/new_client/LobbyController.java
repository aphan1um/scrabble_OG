package new_client;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public static Stage createStage() {
        FXMLLoader loader = new FXMLLoader(
                LoginFormController.class.getResource("../../resources/LobbyForm.fxml"));
        loader.setController(new LobbyController());

        try {
            Stage newStage = new Stage();
            newStage.setScene(new Scene((Parent)loader.load()));
            newStage.setTitle("Lobby Room");

            return newStage;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
