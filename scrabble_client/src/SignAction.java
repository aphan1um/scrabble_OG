import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignAction implements ActionListener {
    private JButton button;
    private JFrame frame;

    public SignAction(JFrame frame,JButton button){
        super();
        this.frame = frame;
        this.button = button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LoginGui loginGui = new LoginGui();
        loginGui.initial();
        frame.dispose();
    }
}
