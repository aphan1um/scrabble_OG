package server;

/*  TODO: For the time being, the server is the host.
 *  Can figure out some further details later.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Host extends Player {
    // TODO: Make port dynamic
    public static final int PORT_NUMBER = 13337;
    public static final int HEARTBEAT_PERIOD = 3000;

    private Map<Player, Socket> players;

    // event listeners
    private List<ChatListener> chat_listeners;

    // TODO: Dummy constructor.
    public Host(Player p) {
        super(p.getID(), p.getIPAddress());

        players = new HashMap<>();
        chat_listeners = new ArrayList<ChatListener>();
    }

    public void add_ChatListener(ChatListener l) {
        chat_listeners.add(l);
    }

    public void createGame() {
        // create TCP connection
        try {
            ServerSocket server = new ServerSocket(PORT_NUMBER);

            while (true) {
                Socket player = server.accept();

                Thread t = new Thread(() -> handle_run(player));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle_run(Socket s) {
        try {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            DataInputStream in = new DataInputStream(s.getInputStream());

            // send request for username
            //out.writeUTF(request_id().toJSONString());
            String resp = in.readUTF();
            System.out.println("[INFO] Received request from IP: " +
                    s.getInetAddress().getHostAddress());

            while (!s.isClosed() && !s.isInputShutdown()) {
                //handle_request(s, in.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // when player leaves (end of thread; when player)
    }

    private void heartbeat_run(Player p) {
        try {
            Socket s = players.get(p);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            while (true) {
                // Refer to: https://stackoverflow.com/questions/6265731/do-java-sockets-support-full-duplex for details
                //JSONObject json_hb = new JSONObject();
                //json_hb.put(JSONKeys.OPERATION, Operation.PING.toString());
                //out.writeUTF(json_hb.toJSONString());

                Thread.sleep(HEARTBEAT_PERIOD);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        // heartbeat failure
        synchronized (players) {
            players.remove(p);
            //new Thread(() -> broadcast_message(json_player_status(p, PlayerStatus.LEFT))).start();
        }
    }

    /*
    public void broadcast_message(JSONObject j) {
        for (Map.Entry<Player, Socket> p : players.entrySet()) {
            try {
                DataOutputStream out = new DataOutputStream(p.getValue().getOutputStream());
                out.writeUTF(j.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    */

    /***
     * Check if the player's sent ID is unique.
     */
    public boolean isIDUnique(String id) {
        for (Player p : players.keySet()) {
            if (p.getID().equals(id)) {
                return false;
            }
        }

        return true;
    }

    /*
    private void handle_request(Socket player, String s) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject req = (JSONObject)parser.parse(s);

            Operation op = Operation.valueOf(
                    req.get(JSONKeys.OPERATION.toString()).toString());

            switch (op) {
                case REQUEST_ID:
                    // add player to list
                    synchronized (players) {
                        Player p = new Player(player.getInetAddress().getHostAddress(),
                                req.get(JSONKeys.VALUE.toString()).toString());

                        // TODO: Clean this mess
                        if (!isIDUnique(p.getID())) {
                            player.close();
                        } else {
                            players.put(p, player);
                            System.out.println("[INFO] Added player with details : " + p);

                            // heartbeat (separate thread)
                            Thread t = new Thread(() -> heartbeat_run(p));
                            t.start();

                            // now send other players in the game
                            // TODO: Separate this; way too messy
                            DataOutputStream out = new DataOutputStream(player.getOutputStream());
                            for (Player others : players.keySet()) {
                                out.writeUTF(json_player_status(others, PlayerStatus.JOINED).toJSONString());
                            }
                            out.flush();

                            // broadcast to other players this new player joined
                            new Thread(() -> broadcast_message(json_player_status(p, PlayerStatus.JOINED))).start();
                        }
                    }
                    break;
                case CHAT:
                    String msg = req.get(JSONKeys.VALUE.toString()).toString();
                    System.out.println("[INFO] Message received: " + msg);
                    break;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    /***
     * Requests for a player's username.
     * @return
     */
    /*
    private static JSONObject request_id() {
        JSONObject ret = new JSONObject();
        ret.put(JSONKeys.OPERATION, Operation.REQUEST_ID.toString());
        return ret;
    }

    private static JSONObject json_player_status(Player p, PlayerStatus status) {
        JSONObject ret = new JSONObject();
        ret.put(JSONKeys.OPERATION, Operation.PLAYER_STATUS_CHANGED.toString());
        ret.put(JSONKeys.PLAYER, p.toString());
        ret.put(JSONKeys.VALUE, status);
        return ret;
    }

    public static void main(String[] args) {
        System.out.println(request_id().toJSONString());
    }
    */
}
