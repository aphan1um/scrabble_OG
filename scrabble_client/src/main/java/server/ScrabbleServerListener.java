package server;

import core.ServerListener;
import core.game.Agent;
import core.message.*;
import core.messageType.*;

import java.io.IOException;
import java.net.Socket;
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
        // return list of players back to player who sent details to join lobby
        eventList.addEvent(new MessageEvent<JoinLobbyMsg>() {
            @Override
            public MessageWrapper onMsgReceive(JoinLobbyMsg recv, Set<Agent> p, Agent sender) {
                Message msg = new AgentChangedMsg(AgentChangedMsg.NewStatus.JOINED, connections.values());
                return new MessageWrapper(msg, sender);
            }
        });

        // tell other players a player has joined
        eventList.addEvent(new MessageEvent<JoinLobbyMsg>() {
            @Override
            public MessageWrapper onMsgReceive(JoinLobbyMsg recv, Set<Agent> p, Agent sender) {
                //new AgentChangedMsg()
                // return list of players back to player who sent details
                AgentChangedMsg msg = new AgentChangedMsg(AgentChangedMsg.NewStatus.JOINED, sender);

                // TODO: Is there a cleaner way to do this?
                Set<Agent> retSend = new HashSet<Agent>(p);
                retSend.remove(sender);

                return new MessageWrapper(msg, retSend);
            }
        });

        eventList.addEvent(new MessageEvent<ChatMsg>() {
            @Override
            public MessageWrapper onMsgReceive(ChatMsg recMessage, Set<Agent> agents, Agent sender) {
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
        if (connections.get(s) == null) {
            if (msgRec.getMessageType() == Message.MessageType.AGENT_CHANGED) {
                Agent player = (Agent)((AgentChangedMsg)msgRec.getMessage()).getAgents().toArray()[0];
                boolean is_unique = !connections.values().contains(player);

                synchronized (connections) {
                    if (is_unique) // register user associated with socket
                        connections.put(s, player);
                }

                sendMessage(new QueryMsg(QueryMsg.QueryType.IS_ID_UNIQUE, is_unique), s);
            }

            return false;
        }

        return true;
    }

    @Override
    protected void onUserDisconnect(Agent p) {
        sendMessage(new MessageWrapper(
                new AgentChangedMsg(AgentChangedMsg.NewStatus.DISCONNECTED, p),
                connections.values()));
    }
}
