package client;

import javax.swing.*;
import java.awt.*;

public class ClientGUI {
    private JPanel panel1;
    private JButton Signbutton;
    private JTextField Text1;
    private JButton cancelButton;
    private JLabel Lable1;
    private JLabel picture;
    private JTextField IP;
    private JTextField Port;
    private JLabel Lable2;
    private JLabel Label3;
    private JButton iWantToBeButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("ClientGUI");
        ClientGUI clientGUI = new ClientGUI();
        CancelAction Cancel = new CancelAction(clientGUI.cancelButton);
        SignAction Sign = new SignAction(frame, clientGUI.Signbutton);
        clientGUI.cancelButton.addActionListener(Cancel);
        clientGUI.Signbutton.addActionListener(Sign);
        clientGUI.iWantToBeButton.addActionListener(Sign);
        frame.setContentPane(clientGUI.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 500);
        frame.setVisible(true);
    }

}