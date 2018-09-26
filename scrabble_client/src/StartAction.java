import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartAction implements ActionListener {
    private LoginGui loginGui;
    private JButton button;
    private JFrame frame;
    public StartAction(LoginGui loginGui,JFrame frame,JButton button){
        this.loginGui = loginGui;
        this.button = button;
        this.frame = frame;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame accept_window = new JFrame("Accept Window");
        AcceptWindow window = new AcceptWindow();
        accept_window.setContentPane(window.getPanel());
        window.getAccept().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Room roomGUI = new Room();
                //roomGUI.initial(loginGui.getReceiver());
                accept_window.dispose();
                frame.dispose();
            }
        });
        accept_window.setSize(300, 200);
        accept_window.setVisible(true);
    }
}

