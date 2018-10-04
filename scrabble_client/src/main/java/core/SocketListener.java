package core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import core.game.Agent;
import core.message.EventMessageList;
import core.message.Message;
import core.message.MessageWrapper;
import core.messageType.PingMsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class SocketListener {
    private static final int HEARTBEAT_PERIOD = 10000; // in ms

    public String listenerName;

    // TODO: This solves the issue of iterating through the list, but we must make
    // sure only ONE thread can modify it.
    public volatile EventMessageList eventList;
    protected final Gson gson;
    protected BiMap<Socket, Agent> connections;

    protected abstract void onUserConnect(Socket s) throws IOException;
    protected abstract void prepareEvents();
    protected abstract boolean onMessageReceived(MessageWrapper msgRec, Socket s) throws IOException;
    protected abstract void onUserDisconnect(Agent p);

    public SocketListener(String name) {
        this.listenerName = name;
        eventList = new EventMessageList();
        gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        reset();
        prepareEvents();
    }

    // reset variables
    protected void reset() {
        connections = Maps.synchronizedBiMap(HashBiMap.create());
    }


    /***
     * For listening to messages from a client/player.
     * Should be handled by separate threads.
     * @param client
     */
    void run_client(Socket client, Thread heartbeat_t) {
        try {
            /**
            // TODO: Adding this line causes issues with ScrabbleServerListener [FIX]
            synchronized (connections) {
                connections.put(client, null);
            }
            **/

            DataInputStream in = new DataInputStream(client.getInputStream());

            while (true) {
                String read = in.readUTF();

                System.out.println("(Preparse from " + listenerName + ":)\t" + read);
                MessageWrapper msgRec = Message.fromJSON(read, gson);

                // TODO: debug
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(read);
                JsonArray obj = element.getAsJsonObject().getAsJsonArray("timeStamps");
                msgRec.timeStamps = gson.fromJson(obj, long[].class);

                // TODO: debug
                if (msgRec.getMessageType() != Message.MessageType.PING)
                    System.out.println(String.format("[%s gets from %s]:\t" + read,
                            listenerName, connections.get(client)));


                if (!onMessageReceived(msgRec, client))
                    continue;

                processMessages(eventList.fireEvent(
                        msgRec.getMessage(), msgRec.getMessageType(),
                        connections.get(client)), msgRec.getTimeStamps());
            }
        } catch (IOException e) {
            // client disconnect (most likely)\
            System.out.println("Error coming from: " + listenerName);
            e.printStackTrace();

            // stop heartbeating the same connection
            if (heartbeat_t != null)
                heartbeat_t.interrupt();

            triggerDisconnect(client);
        }
    }

    void run_heartbeat(Socket client) {
        // TODO: Document something about write error while using TCP.
        try {
            while (true) {
                sendMessage(new PingMsg(), client, null);
                Thread.sleep(HEARTBEAT_PERIOD);
            }
        }  catch (IOException | InterruptedException e) {
            System.out.println("Error coming from: " + listenerName + "\t[HEARTBEAT] ");
        }
    }

    // TODO: A VERY BAD PROCESSOR
    // TODO: A VERY BAD BROADCASTER
    private void processMessages(Collection<MessageWrapper> msgList, long[] timeStamps) {
        if (msgList == null || msgList.contains(null))
            return;

        for (MessageWrapper smsg : msgList) {
            sendMessage(smsg, timeStamps);
        }
    }

    /***
     * Removes player from connection list and signals to all other
     * player of the disconnect.
     * @param s Socket of player.
     */
    protected void triggerDisconnect(Socket s) {
        System.out.println("Disconnected called");
        Agent disconnectedAgent = connections.get(s);

        synchronized (connections) {
            connections.remove(s);
            System.out.println("REMOVED SOMETHING");
        }

        onUserDisconnect(disconnectedAgent);
    }

    public void sendMessage(Message msg, Socket s, long[] timeStamps) throws IOException {
        MessageWrapper smsg = new MessageWrapper(msg);
        addTimestamp(smsg, timeStamps);

        String json = gson.toJson(smsg);
        System.out.println("[" + listenerName + " sends:]\t" + json);
        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        out.writeUTF(json);
    }

    private void addTimestamp(MessageWrapper smsg, long[] timeStamps) {
        // append timestamp to message
        if (timeStamps == null) {
            smsg.addTimeStamps(System.nanoTime());
        } else {
            // TODO: debug
            smsg.addTimeStamps(timeStamps[0], System.nanoTime());
        }
    }

    protected void sendMessage(MessageWrapper smsg, long[] timeStamps) {
        if (smsg == null)
            return;

        for (Agent p : smsg.getSendTo()) {
            try {
                Socket socket_send = connections.inverse().get(p);

                // append timestamp to message
                addTimestamp(smsg, timeStamps);

                // send message to client's socket
                String json = gson.toJson(smsg);
                System.out.println("[" + listenerName + " sends to " + p + "]:\t" + json);
                DataOutputStream out = new DataOutputStream(socket_send.getOutputStream());
                out.writeUTF(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
