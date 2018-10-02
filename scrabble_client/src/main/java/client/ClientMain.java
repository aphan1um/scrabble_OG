package client;

import client.LoginWindow;
import com.sun.security.ntlm.Server;
import core.game.Player;
import new_client.ClientListener;
import server.ServerListener;

import java.io.IOException;
import java.net.ServerSocket;

public class ClientMain {
    public static ClientListener listener;
    public static ServerListener server;
    public static Player playerID;

    public static void main(String[] args) {
        // start Login window first
        LoginWindow loginWind = new LoginWindow();
    }

    public static void connectToServer(String idName, String ip, int port) throws IOException {
        playerID = new Player(idName);
        listener = new ClientListener();

        listener.start(ip, port);
    }

    public static void createServer(String idName, int port) throws IOException {
        playerID = new Player(idName);
        ServerListener server = new ServerListener();

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
        listener = new ClientListener();
        listener.start("localhost", port);
    }
}
