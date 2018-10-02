package server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        int PORT = 12345;
        ServerListener server = new ServerListener();
        server.startListener(new ServerSocket(PORT));
    }
}
