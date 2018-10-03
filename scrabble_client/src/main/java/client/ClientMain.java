package client;

import client.LoginWindow;
import com.sun.security.ntlm.Server;
import core.game.Player;
import core.message.Message;
import core.message.MessageEvent;
import core.message.MessageWrapper;
import core.messageType.ErrorMsg;
import new_client.ClientListener;
import server.ServerListener;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Set;

public class ClientMain {
    public static ClientListener listener;
    public static ServerListener server;
    public static Player playerID;

    public static void main(String[] args) {
        // start Login window first
        listener = new ClientListener();

        // TODO: better arrange this
        listener.eventList.addEvent(new MessageEvent<ErrorMsg>() {
            @Override
            public MessageWrapper onMsgReceive(ErrorMsg recMessage, Set<Player> players, Player sender) {
                if (recMessage.errorType == ErrorMsg.ErrorType.DUPLICATE_ID) {
                    JOptionPane.showMessageDialog(null,
                            "The host already has a player with the same name; " +
                            "try a different name instead.");
                }
                return null;
            }
        });

        LoginWindow loginWind = new LoginWindow();
    }

    public static void connectToServer(String idName, String ip, int port) throws IOException {
        playerID = new Player(idName);
        listener.listenerName = "P:" + playerID.getName();
        listener.startListener(ip, port);
    }

    public static void prepareServer() {
        server = new ServerListener();
    }

    public static void createServer(String idName, int port) throws IOException {
        playerID = new Player(idName);

        Thread t = new Thread() {
            public void run() {
                try {
                    server.startListener(new ServerSocket(port));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();

        // now connect the host to its own server
        listener.startListener("localhost", port);
    }
}
