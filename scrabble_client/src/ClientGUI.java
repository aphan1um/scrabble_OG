import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI {
    private JPanel panel1;
    private JButton Signbutton;
    private JTextField Text1;
    private JButton cancelButton;
    private JLabel Lable1;
    private JLabel picture;

    public static void main(String[] args) {
        JFrame frame = new JFrame("ClientGUI");
        ClientGUI clientGUI = new ClientGUI();
        CancelAction Cancel = new CancelAction(clientGUI.cancelButton);
        SignAction Sign = new SignAction(frame,clientGUI.Signbutton);
        clientGUI.cancelButton.addActionListener(Cancel);
        clientGUI.Signbutton.addActionListener(Sign);
        frame.setContentPane(clientGUI.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 500);
        frame.setVisible(true);
    }
}

class SignAction implements ActionListener{
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