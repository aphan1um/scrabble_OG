package new_server;

import com.google.common.collect.*;
import com.google.gson.Gson;
import core.*;
import core.message.EventMessageList;
import core.message.Message;
import core.message.MessageEvent;
import core.message.MessageType;
import core.messageType.PingMessage;
import core.messageType.ReqMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ServerListener {
    public static final int PORT = 12345;
    private static final int HEARTBEAT_PERIOD = 9000; // in ms

    private static final Gson gson = new Gson();

    // TODO: Dummy player
    private static Player dummy_player = new Player();

    // TODO: Explain why I used this. A bijective mapping.
    private static BiMap<Socket, Player> connections = HashBiMap.create();

    private static EventMessageList eventList = new EventMessageList();

    /**
     * TODO:
     * =========> CURRENT DESIGN:
     * TCP connection required.
     *
     * Stage 1: Local
     * Stage 2: Become a lobby (over internet)
     *
     */

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Started server...");

        // TODO: Dummy test
        MessageEvent test = new MessageEvent<PingMessage>() {
            @Override
            public Message onServerReceive(PingMessage recMessage) {
                System.out.println(recMessage.getMessageType() + " IM HERE");
                return null;
            }

            @Override
            public Message onClientReceive(PingMessage recMessage) {
                return null;
            }
        };

        eventList.addEvent(test);

        eventList.fireEvent(new ReqMessage(dummy_player));
        eventList.fireEvent(new PingMessage());

        eventList.removeEvent(test);
        eventList.fireEvent(new PingMessage());

        System.exit(0);


        while (true) {
            Socket client = server.accept();

            // TODO: Fix this null
            synchronized (connections) {
                connections.put(client, null);
            }

            Thread heartbeat_thread = new Thread(() -> run_heartbeat(client));
            heartbeat_thread.setDaemon(true);
            heartbeat_thread.start();
        }
    }

    /***
     * For listening to messages from a client/player.
     * Should be handled by separate threads.
     * @param client
     */
    public static void run_client(Socket client) {

        try {
            DataInputStream in = new DataInputStream(client.getInputStream());

            while (true) {
                String read = in.readUTF();
                System.out.println(read);

                Message msgrec = MessageType.fromJSON(read, gson);
            }
        } catch (IOException e) {
            // client disconnect (most likely)
            e.printStackTrace();
        }
    }

    /***
     * To be used in making sure client's are connected.
     */
    public static void run_heartbeat(Socket client) {
        // TODO: Document something about write error while using TCP.
        try {
            while (true) {
                sendMessage(new PingMessage(dummy_player), client);
                Thread.sleep(HEARTBEAT_PERIOD);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleDisconnect(Socket s) {
        synchronized (connections) {
            connections.remove(s);
        }
    }

    // TODO: A VERY BAD BROADCASTER
    private void broadcastMessage(Message msg) {
        new Thread(() -> {
            for (Socket socket : new HashSet<Socket>(connections.keySet())) {
                try {
                    sendMessage(msg, socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void sendMessage(Message msg, Socket dest) throws IOException {
        DataOutputStream out = new DataOutputStream(dest.getOutputStream());
        out.writeUTF(gson.toJson(msg));
    }
}
