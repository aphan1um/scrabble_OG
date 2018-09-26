import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class LoginGui {
    private JPanel Login;
    private JButton startButton;
    private JButton cancelButton;
    private JList Userlist;
    private JTextArea welcomeToTheScrabbleTextArea;
    private ArrayList Receiver;
    private final static int Maxplayer = 100;
    TestUser user1 = new TestUser("Xun Sun","1","Signin");
    TestUser user2 = new TestUser("Jing Bi","2","Signout");
    TestUser user3 = new TestUser("Chaodi Tang","2","Signin");
    JFrame frame = new JFrame("LoginGui");
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
}
class StartAction implements ActionListener{
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
                roomGUI.initial(loginGui.getReceiver());
                accept_window.dispose();
                frame.dispose();
            }
        });
        accept_window.setSize(300, 200);
        accept_window.setVisible(true);
    }
}
class MouseAction implements MouseListener{
    private LoginGui loginGui;
    public MouseAction(LoginGui loginGui){
        this.loginGui = loginGui;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        loginGui.getReceiver().add(loginGui.getUserlist().getSelectedValue().toString());
        loginGui.getUserlist().setForeground(Color.red);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        //Todo Auto-generated method
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        //Todo Auto-generated method
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        //Todo Auto-generated method
    }
    @Override
    public void mouseExited(MouseEvent e) {
        //Todo Auto-generated method
    }
}