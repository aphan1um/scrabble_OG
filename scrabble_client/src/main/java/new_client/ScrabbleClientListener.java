package new_client;

import core.ClientListener;
import core.game.Agent;
import core.message.Message;
import core.message.MessageWrapper;
import core.messageType.ChatMsg;
import core.messageType.ErrorMsg;
import core.messageType.GameStatusMsg;
import core.messageType.RequestPDMsg;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ScrabbleClientListener extends ClientListener {
    private boolean fail_res = false;

    public ScrabbleClientListener() {
        super("Agent");
    }


    public void sendChatMessage(String txt) {
        try {
            sendMessage(new ChatMsg(txt, ClientMain.agentID), socket);
        } catch (IOException e) {
            e.printStackTrace();
            triggerDisconnect(socket);
        }
    }

    public void sendGameStart() {
        try {
            // TODO: Host starts the game lol..
            sendMessage(new GameStatusMsg(GameStatusMsg.GameStatus.STARTED,
                    ClientMain.agentID), socket);
        } catch (IOException e) {
            e.printStackTrace();
            triggerDisconnect(socket);
        }
    }

    @Override
    protected void onUserConnect(Socket s) throws IOException {
        // TODO: STATIC NIGHTMARE
        sendMessage(new RequestPDMsg(ClientMain.agentID), s);
    }

    @Override
    protected void prepareEvents() {
    }

    @Override
    protected boolean onMessageReceived(MessageWrapper msgRec, Socket s) {
        // TODO: Debug
        if (msgRec.getMessageType() == Message.MessageType.ERROR &&
                ((ErrorMsg)msgRec.getMessage()).errorType == ErrorMsg.ErrorType.DUPLICATE_ID) {
            try {
                // close connection due to duplicate name
                fail_res = true;
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        return true;
    }

    @Override
    protected void onUserDisconnect(Agent p) {
        // TODO: This is a simplification.
        if (!fail_res) {
            JOptionPane.showMessageDialog(null,
                    "The server you've been connected to has closed down. " +
                            "The app will now exit.");
            System.exit(-1);
        }
    }

    @Override
    public boolean onAuthenticate() throws IOException {
        // sender player details
        sendMessage(new RequestPDMsg(ClientMain.agentID), socket);

        boolean not_ping = false;

        // TODO: potential code dups, also dodgy code
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // wait response from server (ignore pings)
        while (!not_ping) {
            String read = in.readUTF();
            MessageWrapper msgRec = Message.fromJSON(read, gson);

            if (msgRec.getMessageType() != Message.MessageType.PING) {
                not_ping = true;
                return onMessageReceived(msgRec, socket);
            }
        }

        return true;
    }
}
