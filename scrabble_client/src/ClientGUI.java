import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.sun.deploy.uitoolkit.ToolkitStore.dispose;

public class ClientGUI {
    private JPanel panel1;
    private JButton Sign;
    private JTextField Text1;
    private JButton cancelButton;
    private JLabel Lable1;
    private JLabel picture;

    public static void main(String[] args) {
        JFrame frame = new JFrame("ClientGUI");
        ClientGUI clientGUI = new ClientGUI();
        frame.setContentPane(clientGUI.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 500);
        frame.setVisible(true);

        clientGUI.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        clientGUI.Sign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginGui loginGui = new LoginGui();
                loginGui.initial();
                frame.dispose();
            }
        });
    }
}
