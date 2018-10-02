package client;

import client.LoginWindow;
import core.game.Player;
import new_client.ClientListener;

import java.io.IOException;

public class ClientMain {
    public static ClientListener listener;
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
}
