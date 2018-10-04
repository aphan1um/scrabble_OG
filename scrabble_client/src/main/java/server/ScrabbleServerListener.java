package server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import core.ServerListener;
import core.game.Agent;
import core.game.Lobby;
import core.message.*;
import core.messageType.*;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ScrabbleServerListener extends ServerListener {
    private BiMap<String, Lobby> lobbyMap;
    private Map<Agent, Lobby> playerLobbyMap; // note this is not a bijection, so a BiMap can't be used

    public ScrabbleServerListener() {
        super("Server");
    }

    @Override
    protected void reset() {
        super.reset();
        lobbyMap = Maps.synchronizedBiMap(HashBiMap.create());
        playerLobbyMap = new ConcurrentHashMap<>(); // TODO: concurrent hash map
    }

    @Override
    protected void onUserConnect(Socket s) {
    }

    @Override
    protected void prepareEvents() {
        // return list of players back to player who sent details to join lobby
        eventList.addEvents(new MessageEvent<JoinLobbyMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(JoinLobbyMsg recv, Agent sender) {
                Message msg1 = new AgentChangedMsg(AgentChangedMsg.NewStatus.JOINED, connections.values());
                Lobby lobby = lobbyMap.get(recv.getName());

                if (lobby == null) { // if lobby hasn't been made yet, make player owner
                    lobby = new Lobby(sender);
                    lobbyMap.put(recv.getName(), lobby);
                } else {
                    lobby.addPlayer(sender);
                }
                playerLobbyMap.put(sender, lobby);


                AgentChangedMsg msg2 = new AgentChangedMsg(AgentChangedMsg.NewStatus.JOINED, sender);

                // TODO: Is there a cleaner way to do this?
                Set<Agent> retSend = new HashSet<Agent>(
                        playerLobbyMap.get(sender).getAgents()
                );
                retSend.remove(sender);


                return MessageWrapper.prepWraps(
                        new MessageWrapper(msg1, sender),
                        new MessageWrapper(msg2, retSend));
            }
        },

        // when some player sends chat msg, broadcast it to all other players
        new MessageEvent<ChatMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(ChatMsg recMessage, Agent sender) {
                return MessageWrapper.prepWraps(
                        new MessageWrapper(recMessage, playerLobbyMap.get(sender).getAgents()));
            }
        },

        // when Start Game is pressed
        new MessageEvent<GameStatusMsg>() {
            @Override
            public MessageWrapper[] onMsgReceive(GameStatusMsg msg, Agent sender) {
                // TODO: This only works with one lobby..
                Lobby lobby = playerLobbyMap.get(sender);
                if (lobby != null && lobby.getOwner().equals(sender)) {
                    lobby.prepareGame();

                    Message sendMsg = new GameStatusMsg(
                            GameStatusMsg.GameStatus.STARTED,
                            lobby.getGameSession());

                    // send back the clients the initial game state
                    return MessageWrapper.prepWraps(
                            new MessageWrapper(sendMsg,
                            playerLobbyMap.get(sender).getAgents()));
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
                    // register user associated with socket
                    if (!connections.values().contains(player)) {
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
