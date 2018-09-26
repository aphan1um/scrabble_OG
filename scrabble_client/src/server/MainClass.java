package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// TODO: Clean this up.
// Responsible for handling queries from server
public class MainClass {
    private static Player p;

    public static void main(String[] args) {
        // TODO: Fill this code up
        // Ask for username
        p = new Player("dummy name");

        // this is for the client
        Thread t = new Thread(() -> client_run(args[0], Integer.parseInt(args[1])));
        t.start();

        // TODO: Handle if player wants to be the host
    }

    private static void client_run(String ip, int port) {
        try {
            // IP:port address of server (TCP)
            Socket s = new Socket(ip, port);

            while (true) {
                DataInputStream in = new DataInputStream(s.getInputStream());
                JSONObject json_send = parse_JSON(in.readUTF());

                // now respond back to server
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                out.writeUTF(json_send.toJSONString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject parse_JSON(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject j = (JSONObject)parser.parse(s);

        // get type of query
        Operations op = Operations.valueOf(
                j.get(JSONKeys.OPERATION.toString()).toString());

        switch (op) {
            case REQUEST_ID:
                j.put(JSONKeys.VALUE, p.getID());
                break;
            case CHAT:
                // add code here
                break;
            case PLAYER_STATUS_CHANGED:
                PlayerStatus status = PlayerStatus.valueOf(j.get(JSONKeys.VALUE.toString()).toString());
        }

        return j;
    }
}
