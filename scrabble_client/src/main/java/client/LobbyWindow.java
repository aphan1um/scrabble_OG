package client;

import core.game.Player;
import core.message.Message;
import core.message.MessageEvent;
import core.message.MessageWrapper;
import core.messageType.ChatMsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

public class LobbyWindow {
    private JList RoomMember;
    private JPanel room;
    private JButton btnReady;
    private JTextArea txtChat;
    private JTextPane txtChatInput;
    private JButton btnSendMsg;
    private ArrayList player = new ArrayList<String>();
    JFrame frame = new JFrame("Lobby Window");

    public void setPlayer(ArrayList player) {
        this.player = (ArrayList) player.clone();
    }

    public ArrayList getPlayer() {
        return player;
    }

    public LobbyWindow(Component comp) {
        // =========== EVENT LISTENERS

        btnSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientMain.listener.sendChatMessage(txtChatInput.getText());
                txtChatInput.setText("");
            }
        });

        // =========== END EVENT LISTENERS

        registerClientEvents();

        // check if player is host
        btnReady.setEnabled(ClientMain.server != null);

        //roomGUI.setPlayer(Player);
        frame.setContentPane(room);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 500);
        //showRoomMember(roomGUI.player, roomGUI.RoomMember);
        frame.setLocationRelativeTo(comp);
        frame.setVisible(true);

    }

    public void registerClientEvents() {
        // TODO: Static method nightmare
        ClientMain.listener.eventList.addEvent(new MessageEvent<ChatMsg>() {
            @Override
            public MessageWrapper onMsgReceive(ChatMsg recMessage, Set<Player> players, Player sender) {
                txtChat.append("\n[" + recMessage.getSender().getName() + "]:\t" + recMessage.getChatMsg());
                return null;
            }
        });

        System.out.println("Added event");
    }

    public void showRoomMember(ArrayList player, JList list1) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < player.size(); i++) {
            model.addElement(player.get(i));
        }
        list1.setModel(model);
    }

}
