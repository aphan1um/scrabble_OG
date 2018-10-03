package server;

import core.ServerListener;
import core.game.Agent;
import core.game.Lobby;
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
                Lobby lobby = lobbyMap.get(recv.getName());

                if (lobby == null) { // if lobby hasn't been made yet, make player owner
                    lobby = new Lobby(sender);
                    lobbyMap.put(recv.getName(), lobby);
                    playerLobbyMap.put(sender, lobby);
                } else {
                    playerLobbyMap.put(sender, lobby);
                    lobby.addPlayer(sender);
                }

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

        // when Start Game is pressed
        eventList.addEvent(new MessageEvent<GameStatusMsg>() {
            @Override
            public MessageWrapper onMsgReceive(GameStatusMsg msg, Set<Agent> agents, Agent sender) {
                // TODO: This only works with one lobby..
                Lobby lobby = playerLobbyMap.get(sender);
                if (lobby != null && lobby.getOwner().equals(sender)) {
                    lobby.prepareGame();

                    // send back the clients the initial game state
                    return new MessageWrapper(new GameStatusMsg(
                            GameStatusMsg.GameStatus.STARTED,
                            lobby.getGameSession()), agents);
                }

                return null;
            }
        });
    }

    @Override
    protected boolean onMessageReceived(MessageWrapper msgRec, Socket s) throws IOException {
        if (connections.get(s) == null) {
            if (msgRec.getMessageType() == Message.MessageType.AGENT_CHANGED) {
                Agent player = (Agent)((AgentChangedMsg)msgRec.getMessage()).getAgents().toArray()[0];

                boolean is_unique = false;
                synchronized (connections) {
                    if (!connections.values().contains(player)) { // register user associated with socket
                            connections.put(s, player);
                            is_unique = true;
                    }
                }

                sendMessage(new QueryMsg(QueryMsg.QueryType.IS_ID_UNIQUE, is_unique), s);
            }

            return false;
        }

        return true;
    }

    @Override
    protected void onUserDisconnect(Agent p) {
        if (p == null)
            return;

        // TODO [LOBBY FUNCTIONALITY]: Complete this (for multiple lobbies) in the future
        boolean removeLobby = false;
        Lobby lobby = playerLobbyMap.get(p);
        synchronized (playerLobbyMap) {
            playerLobbyMap.remove(p);
        }

        if (removeLobby) {
            synchronized (lobbyMap) {
                lobbyMap.remove(lobby);
            }
        }

        sendMessage(new MessageWrapper(
                new AgentChangedMsg(AgentChangedMsg.NewStatus.DISCONNECTED, p),
                connections.values()));
    }
}
