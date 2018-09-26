package server;

import java.net.InetAddress;

public class Player {
    private String id;
    private String ip_address = "NONE";

    public Player(String id) {
        this.id = id;
    }

    public Player(String id, String ip_address) {
        this.id = id;
        this.ip_address = ip_address;
    }

    public String getID() {
        return id;
    }

    public String getIPAddress() {
        return ip_address;
    }

    public String toString() {
        return getID();
    }
}
