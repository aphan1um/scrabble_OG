package server;

import com.google.common.collect.*;
import com.google.gson.Gson;
import core.game.Player;
import core.message.*;
import core.messageType.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ServerListener {
    public static final int PORT = 12345;
    private static final int HEARTBEAT_PERIOD = 9000; // in ms

    private static final Gson gson = new Gson();

    // TODO: Dummy player
    private static Player dummy_player = new Player("Dummy player");

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

        /**
        // TODO: Dummy test
        MessageEvent test = new MessageEvent<PingMsg>() {
            @Override
            public Message onServerReceive(PingMsg recMessage) {
                System.out.println(recMessage.getMessageType() + " IM HERE");
                return null;
            }

            @Override
            public Message onClientReceive(PingMsg recMessage) {
                return null;
            }
        };

        eventList.addEvent(test);

        eventList.fireEvent(new RequestPDMsg(dummy_player));
        eventList.fireEvent(new PingMsg());

        eventList.removeEvent(test);
        eventList.fireEvent(new PingMsg());

        System.exit(0);
        **/

        // ================== EVENT LISTENERS HERE =================
        // TODO: Weird thing if we use lambda expressions. Is there a better way to do this?

        // return list of players back to player who sent details
        eventList.addEvent(new MessageEvent<RequestPDMsg>() {
            @Override
            public MessageWrapper onMsgReceive(RequestPDMsg recv, Set<Player> p, Player sender) {
                //new PlayerStatusMsg()
                // return list of players back to player who sent details
                Message msg = new RequestPDMsg(connections.values());
                System.out.println("Sending player list...");
                return new MessageWrapper(msg, sender);
            }
        });

        // tell other players a player has joined
        eventList.addEvent(new MessageEvent<RequestPDMsg>() {
            @Override
            public MessageWrapper onMsgReceive(RequestPDMsg recv, Set<Player> p, Player sender) {
                //new PlayerStatusMsg()
                // return list of players back to player who sent details
                Message msg = new PlayerStatusMsg(sender, PlayerStatusMsg.NewStatus.JOINED);

                // TODO: Is there a cleaner way to do this?
                Set<Player> retSend = new HashSet<Player>(p);
                retSend.remove(sender);

                return new MessageWrapper(msg, retSend);
            }
        });

        eventList.addEvent(new MessageEvent<ChatMsg>() {
            @Override
            public MessageWrapper onMsgReceive(ChatMsg recMessage, Set<Player> players, Player sender) {
                System.out.println("Chat: " + recMessage.getChatMsg());
                return null;
            }
        });

        // ================= END EVENT LISTENERS HERE ==============

        while (true) {
            Socket client = server.accept();

            // initially we have the connection but do not know user details
            synchronized (connections) {
                connections.put(client, null);
            }

            Thread client_thread = new Thread(() -> run_client(client));
            client_thread.setDaemon(true);
            client_thread.start();

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

                MessageWrapper msgRec = Message.fromJSON(read, gson);

                // TODO: Fix this PING from client (do we need it)
                if (msgRec.getMessageType() == Message.MessageType.PING)
                    continue;

                // ensure we get credientials
                if (connections.get(client) == null) {
                    if (msgRec.getMessageType() != Message.MessageType.REQUEST) {
                        sendMessage(
                                new ErrorMsg(ErrorMsg.ErrorType.DEMAND_PLAYER_DETAILS),
                                client);
                        continue;
                    }

                    Player joinedPlayer = (Player)(
                            (RequestPDMsg)msgRec.getMessage())
                            .getPlayerList().toArray()[0];

                    // if connecting player shares ID to another player in server
                    // TODO: The player should only send their details once. This is
                    // a limitation if the player wants to change their name, for example
                    // TODO: We close connection in this case.
                    if (connections.inverse().get(joinedPlayer) != null) {
                        sendMessage(new ErrorMsg(ErrorMsg.ErrorType.DUPLICATE_ID), client);
                        client.close();
                    } else
                        connections.put(client, joinedPlayer);
                }

                processMessages(eventList.fireEvent(
                        msgRec.getMessage(), msgRec.getMessageType(),
                        connections.inverse().keySet(),
                        connections.get(client)));
            }
        } catch (IOException e) {
            // client disconnect (most likely)
            e.printStackTrace();
            handleDisconnect(client);
        }
    }

    // TODO: A VERY BAD PROCESSOR
    // TODO: A VERY BAD BROADCASTER
    public static void processMessages(Collection<MessageWrapper> msgList) {
        if (msgList == null || msgList.contains(null))
            return;

        for (MessageWrapper smsg : msgList) {
            processMessage(smsg);
        }
    }

    // TODO: Redundant, doing it for ease
    public static void processMessage(MessageWrapper smsg) {
        if (smsg == null)
            return;

        for (Player p : smsg.getSendTo()) {
            try {
                Socket socket_send = connections.inverse().get(p);

                // send message to client's socket
                DataOutputStream out = new DataOutputStream(socket_send.getOutputStream());
                out.writeUTF(gson.toJson(smsg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * To be used in making sure client's are connected.
     */
    public static void run_heartbeat(Socket client) {
        // TODO: Document something about write error while using TCP.
        try {
            while (true) {
                sendMessage(new PingMsg(), client);
                Thread.sleep(HEARTBEAT_PERIOD);
            }
        }  catch (IOException | InterruptedException e) {
            e.printStackTrace();
            handleDisconnect(client);
        }
    }

    private static void handleDisconnect(Socket s) {
        Player disconnectedPlayer = connections.get(s);

        synchronized (connections) {
            // TODO: This may get called twice due to two threads
            System.out.println("A player disconnected");
            connections.remove(s);
        }

        // broadcast to other players
        Message msg = new PlayerStatusMsg(disconnectedPlayer,
                PlayerStatusMsg.NewStatus.DISCONNECTED);

        processMessage(new MessageWrapper(msg, connections.values()));
    }

    private static void sendMessage(Message msg, Socket s) throws IOException {
        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        out.writeUTF(gson.toJson(new MessageWrapper(msg)));
    }
}
