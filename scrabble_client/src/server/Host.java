package server;

/*  TODO: For the time being, the server is the host.
 *  Can figure out some further details later.
 */

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Host extends Player {
    // TODO: Make port dynamic
    public static final int PORT_NUMBER = 13337;

    private List<Player> players;

    // TODO: Dummy constructor.
    public Host(Player p) {
        super(p.getID(), p.getIPAddress());

        players = new ArrayList<Player>();
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
            out.writeUTF(request_id().toJSONString());
            String resp = in.readUTF();
            System.out.println("[INFO] Received request from IP: " + s.getInetAddress().getHostAddress());

            while (true) {
                handle_request(s, in.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // when player leaves (end of thread)
    }

    private void handle_request(Socket player, String s) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject req = (JSONObject)parser.parse(s);

            Operations op = Operations.valueOf(
                    req.get(JSONKeys.OPERATION.toString()).toString());

            switch (op) {
                case REQUEST_ID:
                    synchronized (players) {
                        Player p = new Player(player.getInetAddress().getHostAddress(),
                                req.get(JSONKeys.VALUE.toString()).toString());

                        players.add(p);
                        System.out.println("[INFO] Added player with details : " + p);
                    }
                    break;
                case CHAT:
                    String msg = req.get(JSONKeys.VALUE.toString()).toString();
                    System.out.println("[INFO] Message received: " + msg);
                    break;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /***
     * Requests for a player's username.
     * @return
     */
    private static JSONObject request_id() {
        JSONObject ret = new JSONObject();
        ret.put(JSONKeys.OPERATION, Operations.REQUEST_ID.toString());
        return ret;
    }

    public static void main(String[] args) {
        System.out.println(request_id().toJSONString());
    }
}
