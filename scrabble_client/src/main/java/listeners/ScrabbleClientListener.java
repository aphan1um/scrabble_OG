package listeners;

import client.ClientMain;
import core.ClientListener;
import core.game.Agent;
import core.message.Message;
import core.message.MessageWrapper;
import core.messageType.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ScrabbleClientListener extends ClientListener {
    private long serverTime;
    private TimeSync timeSync;

    public ScrabbleClientListener() {
        super("Agent");
        timeSync = new TimeSync();
        serverTime = -1;
    }

    public void sendGameVote(GameVoteMsg.Orientation orient, boolean accepted) {
        try {
            sendMessage(new GameVoteMsg(orient, accepted), socket, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGameMove(Point location, Character letter) {
        try {
            sendMessage(new GameActionMsg(location, letter), socket, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: Experimental and needs to be enforced better
    public void joinLobby(String lobbyName) {
        try {
            sendMessage(new JoinLobbyMsg(lobbyName), socket, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendChatMessage(String txt) {
        try {
            sendMessage(new ChatMsg(txt, ClientMain.agentID), socket, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGameStart() {
        try {
            // TODO: Host starts the game lol..
            sendMessage(new GameStatusMsg(GameStatusMsg.GameStatus.STARTED, null), socket, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onUserConnect(Socket s) throws IOException { }

    @Override
    protected void prepareEvents() { }

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
        /**
        if (msgRec.timeStamps != null) {
            if (serverTime < 0)
                serverTime = msgRec.timeStamps[0];

            if (msgRec.timeStamps.length == 2) {
                // calculate latency
                long latency = (System.nanoTime() - msgRec.timeStamps[0])/2;
                // calculate difference in time from server & client
                long client_server_delta = System.nanoTime() - msgRec.timeStamps[1];

                timeSync.addLatencyValue(latency);
            }
        }
        **/

        return true;
    }

    @Override
    protected void onUserDisconnect(Agent p) {
        // TODO: This is a simplification.
        Platform.runLater(() -> {
            new Alert(Alert.AlertType.ERROR,
                    "The listeners you've been connected to has closed down. " +
                            "The app will now exit.").showAndWait();
            System.exit(-1);

        });
    }

    @Override
    public void onAuthenticate() throws Exception {
        // sender player details
        sendMessage(new AgentChangedMsg(AgentChangedMsg.NewStatus.REQUEST,
                ClientMain.agentID), socket, null);

        // TODO: potential code dups, also dodgy code
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // wait response from listeners (ignore pings)
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
                } else if (qmsg.getQueryType() == QueryMsg.QueryType.GAME_ALREADY_MADE) {
                    throw new GameInProgressException();
                }
            }
        }
    }
}
