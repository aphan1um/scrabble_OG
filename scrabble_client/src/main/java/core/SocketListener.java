package core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import core.game.Agent;
import core.message.EventMessageList;
import core.message.Message;
import core.message.MessageWrapper;
import core.messageType.PingMsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public abstract class SocketListener {
    private static final int HEARTBEAT_PERIOD = 10000; // in ms

    public String listenerName;

    public final EventMessageList eventList;
    protected final Gson gson;
    protected final BiMap<Socket, Agent> connections;

    protected abstract void onUserConnect(Socket s) throws IOException;
    protected abstract void prepareEvents();
    protected abstract boolean onMessageReceived(MessageWrapper msgRec, Socket s) throws IOException;
    protected abstract void onUserDisconnect(Agent p);

    public SocketListener(String name) {
        eventList = new EventMessageList();
        gson = new Gson();
        connections = HashBiMap.create();
        this.listenerName = name;

        prepareEvents();
    }

    /***
     * For listening to messages from a client/player.
     * Should be handled by separate threads.
     * @param client
     */
    void run_client(Socket client) {
        try {
            DataInputStream in = new DataInputStream(client.getInputStream());

            while (true) {
                String read = in.readUTF();
                System.out.println("[" + listenerName + " gets]:\t" + read);

                MessageWrapper msgRec = Message.fromJSON(read, gson);

                if (!onMessageReceived(msgRec, client))
                    continue;

                processMessages(eventList.fireEvent(
                        msgRec.getMessage(), msgRec.getMessageType(),
                        connections.inverse().keySet(),
                        connections.get(client)));
            }
        } catch (IOException e) {
            // client disconnect (most likely)\
            System.out.println("Error coming from: " + listenerName);
            e.printStackTrace();
            triggerDisconnect(client);
        }
    }

    void run_heartbeat(Socket client) {
        // TODO: Document something about write error while using TCP.
        try {
            while (true) {
                sendMessage(new PingMsg(), client);
                Thread.sleep(HEARTBEAT_PERIOD);
            }
        }  catch (IOException | InterruptedException e) {
            e.printStackTrace();
            triggerDisconnect(client);
        }
    }

    // TODO: A VERY BAD PROCESSOR
    // TODO: A VERY BAD BROADCASTER
    private void processMessages(Collection<MessageWrapper> msgList) {
        if (msgList == null || msgList.contains(null))
            return;

        for (MessageWrapper smsg : msgList) {
            processMessage(smsg);
        }
    }

    protected void processMessage(MessageWrapper smsg) {
        if (smsg == null)
            return;

        for (Agent p : smsg.getSendTo()) {
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
     * Removes player from connection list and signals to all other
     * player of the disconnect.
     * @param s Socket of player.
     */
    protected void triggerDisconnect(Socket s) {
        Agent disconnectedAgent = connections.get(s);
        // DO WE NEED THIS?

        /**
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        } */

        synchronized (connections) {
            // TODO: This may get called twice due to two threads
            //System.out.println("Agent " + disconnectedAgent.getName() + " has disconnected.");
            connections.remove(s);
        }
        onUserDisconnect(disconnectedAgent);
    }

    public void sendMessage(Message msg, Socket s) throws IOException {
        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        String json = gson.toJson(new MessageWrapper(msg));

        System.out.println("[" + listenerName + " sends]:\t" + json);

        out.writeUTF(gson.toJson(new MessageWrapper(msg)));
    }
}
