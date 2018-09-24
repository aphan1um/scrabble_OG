import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class LoginGui {
    private JPanel Login;
    private JButton start;
    private JButton cancelButton;
    private JList list1;
    private JTextArea welcomeToTheScrabbleTextArea;
    private ArrayList Receiver;
    private final static int Maxplayer = 100;
    TestUser user1 = new TestUser("Xun Sun","1","Signin");
    TestUser user2 = new TestUser("Chaodi Tang","2","Signout");
    TestUser user3 = new TestUser("Chaodi Tang","2","Signin");
    JFrame frame = new JFrame("LoginGui");

    public LoginGui() {
        Receiver = new ArrayList(Maxplayer);
    }

    public ArrayList getReceiver(){
        return Receiver;
    }

    public void initial(){
        LoginGui loginGui = new LoginGui();
        frame.setContentPane(loginGui.Login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginGui.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        loginGui.start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame accept_window = new JFrame("Accept Window");
                AcceptWindow window = new AcceptWindow();
                accept_window.setContentPane(window.getPanel());
                window.getAccept().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Room roomGUI = new Room();
                        roomGUI.initial(loginGui.Receiver);
                        accept_window.dispose();
                        frame.dispose();
                    }
                });
                accept_window.setSize(300, 200);
                accept_window.setVisible(true);
            }
        });

        loginGui.list1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loginGui.Receiver.add(loginGui.list1.getSelectedValue().toString());
                loginGui.list1.setForeground(Color.red);
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
        });

        frame.pack();
        frame.setSize(900, 500);
        DefaultListModel model = new DefaultListModel();
        model.addElement(user1.getUsername());
        model.addElement(user3.getUsername());
        loginGui.list1.setModel(model);
        frame.setVisible(true);
    }
}
