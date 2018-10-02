package client;

import com.sun.security.ntlm.Client;
import core.game.Player;
import core.message.Message;
import core.message.MessageEvent;
import core.message.MessageWrapper;
import core.messageType.ChatMsg;
import core.messageType.PlayerStatusMsg;
import core.messageType.RequestPDMsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

public class LobbyWindow {
    private JList lstPlayers;
    private JPanel room;
    private JButton btnReady;
    private JTextArea txtChat;
    private JTextPane txtChatInput;
    private JButton btnSendMsg;
    private ArrayList player = new ArrayList<String>();
    // TODO: For debug reasons
    public JFrame frame = new JFrame("Lobby Window");

    private DefaultListModel model = new DefaultListModel();

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

        lstPlayers.setModel(model);
        registerClientEvents();

        // check if player is host
        btnReady.setEnabled(ClientMain.server != null);

        //roomGUI.setPlayer(Player);
        frame.setContentPane(room);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 500);
        //showRoomMember(roomGUI.player, roomGUI.lstPlayers);
        frame.setLocationRelativeTo(comp);
    }

    public void show() {
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

        ClientMain.listener.eventList.addEvent(new MessageEvent<PlayerStatusMsg>() {
            @Override
            public MessageWrapper onMsgReceive(PlayerStatusMsg recMessage, Set<Player> players, Player sender) {
                switch (recMessage.getStatus()) {
                    case JOINED:
                        txtChat.append("\n" + recMessage.getPlayer().getName() + " has joined.");
                        model.addElement(recMessage.getPlayer().getName());
                        break;
                    case DISCONNECTED:
                        txtChat.append("\n" + recMessage.getPlayer().getName() + " has left the room.");
                        model.removeElement(recMessage.getPlayer().getName());
                        break;
                }

                return null;
            }
        });

        // TODO: This should only get called once, or better code the protocol.
        ClientMain.listener.eventList.addEvent(new MessageEvent<RequestPDMsg>() {
            @Override
            public MessageWrapper onMsgReceive(RequestPDMsg recMessage, Set<Player> players, Player sender) {
                for (Player p : recMessage.getPlayerList()) {
                    model.addElement(p.getName());
                }

                return null;
            }
        });

        System.out.println("Added event");
    }

}
