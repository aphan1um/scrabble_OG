package new_client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gson.Gson;
import core.message.Message;
import core.message.MessageType;
import core.Player;
import core.messageType.ReqMessage;
import new_server.ServerListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientListener {
    private static Gson gson = new Gson();

    // TODO: Dummy player
    private static final Player player_client = new Player("Mario");

    public static void main(String[] args) throws IOException {
        // TODO: Need to put this in another thread.
        Socket server = new Socket("localhost", ServerListener.PORT);
        DataOutputStream out = new DataOutputStream(server.getOutputStream());

        // send welcome messageType
        sendMessage(new ReqMessage(player_client), server);

        // TODO: Make this thread-safe
        SetMultimap<MessageType, Message> events = HashMultimap.create();

        new Thread(() -> {
            try {
                listener(server);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void listener(Socket s) throws IOException {
        DataInputStream in = new DataInputStream(s.getInputStream());

        while (true) {
            String txt = in.readUTF();

            Message msgrec = MessageType.fromJSON(txt, gson);
            System.out.println(msgrec.getMessageType());
        }
    }

    private static void sendMessage(Message msg, Socket dest) throws IOException {
        DataOutputStream out = new DataOutputStream(dest.getOutputStream());
        out.writeUTF(gson.toJson(msg));
    }
}
