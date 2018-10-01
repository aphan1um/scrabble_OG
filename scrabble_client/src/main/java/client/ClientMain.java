package client;

import client.LoginWindow;
import new_client.ClientListener;

import java.io.IOException;

public class ClientMain {
    private static ClientListener listener;

    public static void main(String[] args) {
        // start Login window first
        LoginWindow loginWind = new LoginWindow();
    }

    public static void connectToServer(String idName, String ip, int port) throws IOException {
        listener = new ClientListener(idName, ip, port);
    }
}
