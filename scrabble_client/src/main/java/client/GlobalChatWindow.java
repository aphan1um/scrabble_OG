package client;

import javax.swing.*;
import java.util.ArrayList;

public class GlobalChatWindow {
    private JPanel Login;
    private JButton startButton;
    private JButton cancelButton;
    private JList lstUsers;
    private JTextArea Welcome;
    private JLabel Username;
    private ArrayList Receiver;
    private final static int Maxplayer = 100;

    TestUser user1 = new TestUser("Xun Sun", "1", "Signin");
    TestUser user2 = new TestUser("Jing Bi", "2", "Signout");
    TestUser user3 = new TestUser("Chaodi Tang", "2", "Signin");

    JFrame frame = new JFrame("GlobalChatWindow");
    LoginWindow loginWindow = new LoginWindow();

    public GlobalChatWindow() {
        Receiver = new ArrayList(Maxplayer);
    }

    public void initial() {
        GlobalChatWindow lobbyWindow = new GlobalChatWindow();
        frame.setContentPane(lobbyWindow.Login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 500);
        //这部分后面增添协议后再增加 There will be additional part in this section
        DefaultListModel model = new DefaultListModel();
        model.addElement(user1.getUsername());
        model.addElement(user2.getUsername());
        model.addElement(user3.getUsername());
        lobbyWindow.lstUsers.setModel(model);
        frame.setVisible(true);
    }

    public ArrayList getReceiver() {
        return Receiver;
    }

    public JList getLstUsers() {
        return lstUsers;
    }

    public JTextArea getWelcome() {
        return Welcome;
    }

}
