package new_server;

import com.google.common.collect.*;
import com.google.gson.Gson;
import core.Message;
import core.MessageEvent;
import core.MessageType;
import core.Player;
import core.message.PingMessage;
import core.message.ReqMessage;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ServerListener {
    public static final int PORT = 12345;
    private static final int HEARTBEAT_PERIOD = 9000; // in ms

    private static final Gson gson = new Gson();

    // TODO: Dummy player
    private static Player dummy_player = new Player();

    // TODO: Explain why I used this. A bijective mapping.
    private static BiMap<Socket, Player> connections = HashBiMap.create();

    private static Multimap<MessageType, MessageEvent> events = HashMultimap.create();

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
        addEvent(MessageType.PING, new MessageEvent<PingMessage>() {
            @Override
            public Message onServerReceive(PingMessage recMessage) {
                System.out.println(recMessage.getMessageType() + " IM HERE");
                return null;
            }

            @Override
            public Message onClientReceive(PingMessage recMessage) {
                return null;
            }
        });

        fireEvent(new ReqMessage(dummy_player));
        fireEvent(new PingMessage());

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

    private static void fireEvent(Message msg) {
        for (MessageEvent e: events.get(msg.getMessageType())) {
            e.onServerReceive(msg);
        }
    }

    public static <T extends Message> void addEvent(MessageType mtype, MessageEvent<T> event) {
        // verify the messagetype and event are 'related'
        Class<? extends Message> cl = mtype.getCorrespondingClass();
        Type generic_type = ((ParameterizedType)
                event.getClass().getGenericInterfaces()[0])
                .getActualTypeArguments()[0];

        // TODO: Explain this code. And this assert here for debug
        if (!generic_type.getTypeName().equals(cl.getName())) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(generic_type.getTypeName().equals(cl.getName()));

        events.put(mtype, event);
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
