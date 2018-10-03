package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class ServerListener extends SocketListener {
    public ServerListener(String name) {
        super(name);
    }

    public void startListener(int port) throws IOException {
        reset();
        ServerSocket server = new ServerSocket(port);

        // TODO: catch this exception via different Thread technique;
        // like an Executor
        new Thread(() -> {
            while (true) {
                try {
                    Socket client = server.accept();

                    synchronized (connections) {
                        connections.put(client, null);
                    }

                    // heartbeat
                    Thread t = new Thread(() -> run_heartbeat(client));
                    t.start();

                    // separate thread for connector
                    new Thread(() -> run_client(client, t)).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("Created a server!");
    }
}
