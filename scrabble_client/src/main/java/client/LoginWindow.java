package client;

import javax.swing.*;

public class LoginWindow {
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
        JFrame frame = new JFrame("LoginWindow");
        LoginWindow loginWindow = new LoginWindow();
        CancelAction Cancel = new CancelAction(loginWindow.cancelButton);
        SignAction Sign = new SignAction(frame, loginWindow.Signbutton);
        loginWindow.cancelButton.addActionListener(Cancel);
        loginWindow.Signbutton.addActionListener(Sign);
        loginWindow.iWantToBeButton.addActionListener(Sign);
        frame.setContentPane(loginWindow.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 500);
        frame.setVisible(true);
    }

}