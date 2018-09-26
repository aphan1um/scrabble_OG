package server;

import java.net.InetAddress;

public class Player {
    private String id;
    private InetAddress ip_address;
    private int port;

    public Player(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }
}
