package new_client.util;

import javafx.stage.Stage;

import java.net.URL;

public class StageUtils {
    // annoyingly JavaFX doesn't provide a method to center a window on another window
    // TODO: Thanks to https://stackoverflow.com/a/13702636 for the code
    public static void centreStage(Stage parent, Stage child) {
        child.setX(parent.getX() + parent.getWidth() / 2 - child.getWidth() / 2);
        child.setY(parent.getY() + parent.getHeight() / 2 - child.getHeight() / 2);
    }

    public static URL getResource(String name) {
        return StageUtils.class.getResource("../../../resources/" + name);
    }
}
