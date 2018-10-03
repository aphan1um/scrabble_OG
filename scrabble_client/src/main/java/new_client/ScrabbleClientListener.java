package new_client;

import core.ClientListener;
import core.game.Agent;
import core.message.Message;
import core.message.MessageWrapper;
import core.messageType.*;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ScrabbleClientListener extends ClientListener {
    public ScrabbleClientListener() {
        super("Agent");
    }


    // TODO: Experimental and needs to be enforced better
    public void joinLobby() {
        try {
            sendMessage(new JoinLobbyMsg(), socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    }

    @Override
    protected void prepareEvents() {
    }

    @Override
    protected boolean onMessageReceived(MessageWrapper msgRec, Socket s) {
        /**
        // TODO: Debug
        if (msgRec.getMessageType() == Message.MessageType.ERROR &&
                ((QueryMsg)msgRec.getMessage()).errorType == QueryMsg.ErrorType.DUPLICATE_ID) {
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
        **/
        return true;
    }

    @Override
    protected void onUserDisconnect(Agent p) {
        // TODO: This is a simplification.
        JOptionPane.showMessageDialog(null,
                "The server you've been connected to has closed down. " +
                        "The app will now exit.");
        System.exit(-1);
    }

    @Override
    public void onAuthenticate() throws Exception {
        // sender player details
        sendMessage(new AgentChangedMsg(AgentChangedMsg.NewStatus.JOINED,
                ClientMain.agentID), socket);

        // TODO: potential code dups, also dodgy code
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // wait response from server (ignore pings)
        while (true) {
            String read = in.readUTF();
            MessageWrapper msgRec = Message.fromJSON(read, gson);

            if (msgRec.getMessageType() == Message.MessageType.QUERY) {
                QueryMsg qmsg = (QueryMsg)msgRec.getMessage();

                if (qmsg.getQueryType() == QueryMsg.QueryType.IS_ID_UNIQUE) {

                    if (qmsg.getValue() == false)
                        throw new NonUniqueNameException();
                    else
                        return;
                }
            }
        }
    }
}
