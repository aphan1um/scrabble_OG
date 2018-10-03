package core;

import core.game.Agent;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

public abstract class ClientListener extends SocketListener {

    public ClientListener(String name) {
        super(name);
    }

    public abstract void onAuthenticate() throws Exception;
    public Socket socket;

    public void startListener(String ip, int port) throws Exception {
        reset();

        if (socket != null && !socket.isClosed())
            socket.close();

        socket = new Socket(ip, port);
        connections.put(socket, new Agent("Server", Agent.AgentType.SERVER));

        // perform a simple authentication check
        // TODO: Maybe use executor on other threads here?
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                onAuthenticate();
                return null;
            }
        });

        executor.shutdown();
        future.get();

        // heartbeat
        Thread t = new Thread(() -> run_heartbeat(socket));
        t.start();

        // separate thread for connector
        new Thread(() -> run_client(socket, t)).start();

        onUserConnect(socket);
    }
}
