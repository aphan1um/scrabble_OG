package client;

import javax.swing.*;
import java.util.ArrayList;

public class LobbyWindow {
    private JPanel Login;
    private JButton startButton;
    private JButton cancelButton;
    private JList Userlist;
    private JTextArea Welcome;
    private JLabel Username;
    private JLabel label1;
    private ArrayList Receiver;
    private final static int Maxplayer = 100;
    TestUser user1 = new TestUser("Xun Sun", "1", "Signin");
    TestUser user2 = new TestUser("Jing Bi", "2", "Signout");
    TestUser user3 = new TestUser("Chaodi Tang", "2", "Signin");
    JFrame frame = new JFrame("LobbyWindow");
    LoginWindow loginWindow = new LoginWindow();

    public LobbyWindow() {
        Receiver = new ArrayList(Maxplayer);
    }

    public void initial() {
        LobbyWindow lobbyWindow = new LobbyWindow();
        frame.setContentPane(lobbyWindow.Login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CancelAction Cancel = new CancelAction(lobbyWindow.cancelButton);
        StartAction Start = new StartAction(lobbyWindow, frame, lobbyWindow.startButton);
        MouseAction Click = new MouseAction(lobbyWindow);
        lobbyWindow.cancelButton.addActionListener(Cancel);
        lobbyWindow.startButton.addActionListener(Start);
        lobbyWindow.Userlist.addMouseListener(Click);
        frame.pack();
        frame.setSize(900, 500);
        //这部分后面增添协议后再增加 There will be additional part in this section
        DefaultListModel model = new DefaultListModel();
        model.addElement(user1.getUsername());
        model.addElement(user2.getUsername());
        model.addElement(user3.getUsername());
        lobbyWindow.Userlist.setModel(model);
        frame.setVisible(true);
    }

    public ArrayList getReceiver() {
        return Receiver;
    }

    public JList getUserlist() {
        return Userlist;
    }

    public JTextArea getWelcome() {
        return Welcome;
    }

}
