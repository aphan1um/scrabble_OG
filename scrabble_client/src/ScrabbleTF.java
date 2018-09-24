import javax.swing.*;
import java.util.ArrayList;

public class ScrabbleTF extends JTextField {

    public int getStf_x() {
        return stf_x;
    }

    public int getStf_y() {
        return stf_y;
    }

    private int stf_x;
    private int stf_y;

    public ScrabbleTF(int x, int y) {
        this.stf_x = x;
        this.stf_y = y;
    }
}
