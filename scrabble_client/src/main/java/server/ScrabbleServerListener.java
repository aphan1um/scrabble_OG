package server;

import core.ServerListener;
import core.SocketListener;
import core.game.Agent;
import core.message.*;
import core.messageType.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ScrabbleServerListener extends ServerListener {

    public ScrabbleServerListener() {
        super("Server");
    }

    @Override
    protected void onUserConnect(Socket s) {
    }

    @Override
    protected void prepareEvents() {
        // return list of players back to player who sent details
        eventList.addEvent(new MessageEvent<RequestPDMsg>() {
            @Override
            public MessageWrapper onMsgReceive(RequestPDMsg recv, Set<Agent> p, Agent sender) {
                //new PlayerStatusMsg()
                // return list of players back to player who sent details
                Message msg = new RequestPDMsg(connections.values());
                System.out.println("Sending player list...");
                return new MessageWrapper(msg, sender);
            }
        });

        // tell other players a player has joined
        eventList.addEvent(new MessageEvent<RequestPDMsg>() {
            @Override
            public MessageWrapper onMsgReceive(RequestPDMsg recv, Set<Agent> p, Agent sender) {
                //new PlayerStatusMsg()
                // return list of players back to player who sent details
                PlayerStatusMsg msg = new PlayerStatusMsg(sender, PlayerStatusMsg.NewStatus.JOINED);

                // TODO: Is there a cleaner way to do this?
                Set<Agent> retSend = new HashSet<Agent>(p);
                retSend.remove(sender);

                return new MessageWrapper(msg, retSend);
            }
        });

        eventList.addEvent(new MessageEvent<ChatMsg>() {
            @Override
            public MessageWrapper onMsgReceive(ChatMsg recMessage, Set<Agent> agents, Agent sender) {
                System.out.println("Chat: " + recMessage.getChatMsg());
                return new MessageWrapper(recMessage, agents);
            }
        });

        // when host says to start game
        eventList.addEvent(new MessageEvent<GameStatusMsg>() {
            @Override
            public MessageWrapper onMsgReceive(GameStatusMsg recMessage, Set<Agent> agents, Agent sender) {
                return new MessageWrapper(recMessage, agents);
            }
        });
    }

    @Override
    protected boolean onMessageReceived(MessageWrapper msgRec, Socket s) throws IOException {
        // TODO: Fix this PING from client (do we need it)
        if (msgRec.getMessageType() == Message.MessageType.PING)
            return false;

        // ensure we get credientials
        if (connections.get(s) == null) {
            if (msgRec.getMessageType() != Message.MessageType.REQUEST_PLAYER_DETAILS) {
                sendMessage(
                        new ErrorMsg(ErrorMsg.ErrorType.DEMAND_PLAYER_DETAILS),
                        s);
                return false;
            }

            Agent joinedAgent = (Agent)(
                    (RequestPDMsg)msgRec.getMessage())
                    .getAgentList().toArray()[0];

            // if connecting player shares ID to another player in server
            // TODO: The player should only send their details once. This is
            // a limitation if the player wants to change their name, for example
            // TODO: We close connection in this case.
            System.out.println(Arrays.toString(connections.values().toArray()) + "\t" + joinedAgent.getName());
            if (connections.values().contains(joinedAgent)) {
                sendMessage(new ErrorMsg(ErrorMsg.ErrorType.DUPLICATE_ID), s);
            } else {
                connections.put(s, joinedAgent);
            }
        }

        return true;
    }

    @Override
    protected void onUserDisconnect(Agent p) {
        processMessage(new MessageWrapper(
                new PlayerStatusMsg(p, PlayerStatusMsg.NewStatus.DISCONNECTED),
                connections.values()));
    }
}
