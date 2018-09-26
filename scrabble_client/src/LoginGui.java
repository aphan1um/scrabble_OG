import javax.swing.*;
import java.util.ArrayList;

public class LoginGui {
    private JPanel Login;
    private JButton startButton;
    private JButton cancelButton;
    private JList Userlist;
    private JTextArea Welcome;
    private JLabel Username;
    private ArrayList Receiver;
    private final static int Maxplayer = 100;
    TestUser user1 = new TestUser("Xun Sun","1","Signin");
    TestUser user2 = new TestUser("Jing Bi","2","Signout");
    TestUser user3 = new TestUser("Chaodi Tang","2","Signin");
    JFrame frame = new JFrame("LoginGui");
    ClientGUI clientGUI = new ClientGUI();
    public LoginGui() {
        Receiver = new ArrayList(Maxplayer);
    }
    public void initial(){
        LoginGui loginGui = new LoginGui();
        frame.setContentPane(loginGui.Login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CancelAction Cancel = new CancelAction(loginGui.cancelButton);
        StartAction Start = new StartAction(loginGui,frame,loginGui.startButton);
        MouseAction Click = new MouseAction(loginGui);
        loginGui.cancelButton.addActionListener(Cancel);
        loginGui.startButton.addActionListener(Start);
        loginGui.Userlist.addMouseListener(Click);
        frame.pack();
        frame.setSize(900, 500);
        //这部分后面增添协议后再增加 There will be additional part in this section
        DefaultListModel model = new DefaultListModel();
        model.addElement(user1.getUsername());
        model.addElement(user2.getUsername());
        model.addElement(user3.getUsername());
        loginGui.Userlist.setModel(model);
        frame.setVisible(true);
    }
    public ArrayList getReceiver(){
        return Receiver;
    }
    public JList getUserlist(){
        return Userlist;
    }
    public JTextArea getWelcome(){
        return Welcome;
    }
}
