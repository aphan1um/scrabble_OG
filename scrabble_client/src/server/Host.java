package server;

/*  TODO: For the time being, the server is the host.
 *  Can figure out some further details later.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Host extends Player {
    // TODO: Make port dynamic
    public static final int PORT_NUMBER = 13337;

    public Host(String id) {
        super(id);
    }

    // TODO: Dummy constructor.
    public Host(Player p) {
        super(p.getID());
    }

    public void createGame() {
        // create TCP connection
        try {
            ServerSocket server = new ServerSocket(PORT_NUMBER);

            while (true) {
                Socket player = server.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
