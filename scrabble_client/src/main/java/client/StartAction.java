package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartAction implements ActionListener {
    private LobbyWindow lobbyWindow;
    private JButton button;
    private JFrame frame;
    public StartAction(LobbyWindow lobbyWindow, JFrame frame, JButton button){
        this.lobbyWindow = lobbyWindow;
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
                roomGUI.initial(lobbyWindow.getReceiver());
                accept_window.dispose();
                frame.dispose();
            }
        });
        window.getRefused().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accept_window.dispose();
            }
        });
        accept_window.setSize(300, 200);
        accept_window.setVisible(true);
    }
}

