package client.listeners;

import client.ClientMain;
import client.Connections;
import core.ClientListener;
import core.ConnectType;
import core.game.Agent;
import core.game.GameRules;
import core.message.Message;
import core.message.MessageWrapper;
import core.messageType.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

final public class ScrabbleClientListener extends ClientListener {
    private String lobbyName;

    private DoubleProperty pingMS; // ping in milliseconds (ms)
    private ConnectType serverType;

    public ScrabbleClientListener(String name) {
        super(name);
        pingMS = new SimpleDoubleProperty();
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public DoubleProperty pingMSProperty() {
        return pingMS;
    }

    public void sendGameVote(GameRules.Orientation orient, boolean accepted) {
        try {
            sendMessage(new MSGGameVote(orient, accepted));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGameMove(Point location, Character letter) {
        try {
            sendMessage(new MSGGameAction(location, letter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGamePass() {
        sendGameMove(null, null);
    }

    // TODO: Experimental and needs to be enforced better
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public void sendChatMessage(String txt) {
        try {
            sendMessage(new MSGChat(txt, Connections.playerProperty().get()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGameStart() {
        try {
            // TODO: Host starts the game lol..
            sendMessage(new MSGGameStatus(MSGGameStatus.GameStatus.STARTED, null));
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
        if (msgRec.getMessageType() == Message.MessageType.PING &&
                msgRec.getTimeStamps().size() > 1) {
            pingMS.set(Math.abs(msgRec.getTimeStamps().get(0) - System.nanoTime())/Math.pow(10, 6));
            System.out.println(pingMS);
        }
        /**

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
        Platform.runLater(() ->
                ClientMain.endApp(
                "The server (or host) you were connected to has closed down. " +
                            "The app will now exit."));
    }

    @Override
    public void onAuthenticate() throws Exception {
        // sender player's username; server checks if its unique
        sendMessage(new MSGLogin(Connections.playerProperty().get()));

        // TODO: potential code dups, also dodgy code
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // wait response from client.listeners (ignore pings)
        while (true) {
            String read = in.readUTF();
            MessageWrapper msgRec = Message.fromJSON(read, gson);

            if (msgRec.getMessageType() == Message.MessageType.QUERY) {
                MSGQuery qmsg = (MSGQuery)msgRec.getMessage();

                serverType = qmsg.getServerType();

                switch (qmsg.getQueryType()) {
                    case GAME_ALREADY_STARTED:
                        if (qmsg.getValue() == true)
                            throw new GameInProgressException();
                        return;
                    case AUTHENTICATED:
                        if (!qmsg.getValue())
                            throw new NonUniqueNameException();
                    default:
                        if (serverType == ConnectType.LOCAL) {
                            sendMessage(new MSGJoinLobby(lobbyName));
                        } else {
                            return;
                        }
                }
            }
        }
    }

    public void requestLobbyDetails() {
        try {
            sendMessage(new MSGQuery(MSGQuery.QueryType.GET_PLAYER_LIST, true, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
