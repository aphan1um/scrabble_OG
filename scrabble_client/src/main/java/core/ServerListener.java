package core;

import core.message.Message;
import core.message.MessageWrapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class ServerListener extends Listener {
    public ServerListener(String name) {
        super(name);
    }

    public void start(int port) throws IOException {
        reset();
        ServerSocket server = new ServerSocket(port);

        // TODO: catch this exception via different Thread technique;
        // like an Executor
        new Thread(() -> {
            while (true) {
                try {
                    Socket client = server.accept();

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
    }

    /***
     * Send a message via a socket.
     */
    protected void sendMessage(Message msg, Socket s) throws IOException {
        super.sendMessage(msg, s);
    }
}
