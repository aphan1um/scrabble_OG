package new_client;

import com.google.gson.Gson;
import core.message.Message;
import core.message.MessageType;
import core.Player;
import core.messageType.ChatMsg;
import core.messageType.RequestPDMsg;
import new_server.ServerListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientListener {
    private Gson gson = new Gson();
    private Player playerID;

    public ClientListener(String name, String ip, int port) throws IOException {
        playerID = new Player(name);

        Socket server = new Socket(ip, port);
        DataOutputStream out = new DataOutputStream(server.getOutputStream());

        // send welcome messageType
        sendMessage(new RequestPDMsg(playerID), server);

        new Thread(() -> {
            try {
                listener(server);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        /**
        // TODO: For debug purposes
        System.out.println("Reading....");
        Scanner scan = new Scanner(System.in);
        while (scan.hasNextLine()) {
            sendMessage(new ChatMsg(playerID, scan.nextLine()), server);
        }
         **/
    }

    public Player getPlayer() {
        return playerID;
    }

    public void listener(Socket s) throws IOException {
        DataInputStream in = new DataInputStream(s.getInputStream());

        while (true) {
            String txt = in.readUTF();

            //System.out.println(txt);
            Message msgrec = MessageType.fromJSON(txt, gson);
            //System.out.println(msgrec.getMessageType());
        }
    }

    private void sendMessage(Message msg, Socket dest) throws IOException {
        DataOutputStream out = new DataOutputStream(dest.getOutputStream());
        out.writeUTF(gson.toJson(msg));
    }
}
