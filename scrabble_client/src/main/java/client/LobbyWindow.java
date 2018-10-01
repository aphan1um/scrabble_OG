package client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LobbyWindow {
    private JList RoomMember;
    private JPanel room;
    private JButton readyButton;
    private JTextArea textArea1;
    private ArrayList player = new ArrayList<String>();
    JFrame frame = new JFrame("LobbyWindow");

    public void setPlayer(ArrayList player) {
        this.player = (ArrayList) player.clone();
    }

    public ArrayList getPlayer() {
        return player;
    }

    public LobbyWindow(Component comp) {
        //roomGUI.setPlayer(Player);
        frame.setContentPane(room);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 500);
        //showRoomMember(roomGUI.player, roomGUI.RoomMember);
        frame.setLocationRelativeTo(comp);
        frame.setVisible(true);
    }

    public void showRoomMember(ArrayList player, JList list1) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < player.size(); i++) {
            model.addElement(player.get(i));
        }
        list1.setModel(model);
    }

}
