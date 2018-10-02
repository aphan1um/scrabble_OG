package new_client;

import client.ClientMain;
import core.SocketListener;
import core.game.Player;
import core.message.MessageWrapper;
import core.messageType.ChatMsg;
import core.messageType.GameStatusMsg;
import core.messageType.RequestPDMsg;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class ClientListener extends SocketListener {
    public Socket socket;

    public ClientListener() {
        super("Player");
    }

    public void start(String ip, int port) throws IOException {
        socket = this.startListener(ip, port);
    }

    public void sendChatMessage(String txt) {
        try {
            sendMessage(new ChatMsg(txt, ClientMain.playerID), socket);
        } catch (IOException e) {
            e.printStackTrace();
            triggerDisconnect(socket);
        }
    }

    public void sendGameStart() {
        try {
            // TODO: Host starts the game lol..
            sendMessage(new GameStatusMsg(GameStatusMsg.GameStatus.STARTED,
                    ClientMain.playerID), socket);
        } catch (IOException e) {
            e.printStackTrace();
            triggerDisconnect(socket);
        }
    }

    @Override
    protected void onUserConnect(Socket s) throws IOException {
        // TODO: STATIC NIGHTMARE
        sendMessage(new RequestPDMsg(ClientMain.playerID), s);
    }

    @Override
    protected void prepareEvents() {
    }

    @Override
    protected boolean onMessageReceived(MessageWrapper msgRec, Socket s) {
        return true;
    }

    @Override
    protected void onUserDisconnect(Player p) {
        // TODO: This is a simplification.
        JOptionPane.showMessageDialog(null,
                "The server you've been connected to has closed down. The app will now exit.");
        System.exit(-1);
    }
}
