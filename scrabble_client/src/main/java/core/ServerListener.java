package core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import core.game.Agent;
import core.game.Lobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ServerListener extends SocketListener {
    protected BiMap<String, Lobby> lobbyMap;
    protected Map<Agent, Lobby> playerLobbyMap; // note this is not a bijection, so a BiMap can't be used

    public ServerListener(String name) {
        super(name);
    }

    @Override
    public void reset() {
        super.reset();
        lobbyMap = Maps.synchronizedBiMap(HashBiMap.create());
        playerLobbyMap = new ConcurrentHashMap<>(); // TODO: concurrent hash map
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
