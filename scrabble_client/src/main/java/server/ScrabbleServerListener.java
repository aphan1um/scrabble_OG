package server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import core.ConnectType;
import core.ServerListener;
import core.game.Agent;
import core.game.Lobby;
import core.message.*;
import core.messageType.*;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class ScrabbleServerListener extends ServerListener {

    private BiMap<String, Lobby> lobbyMap;
    private Map<Agent, Lobby> playerLobbyMap; // note this is not a bijection, so a BiMap can't be used

    private class ServerEvents {
        // return list of players back to player who sent details to join lobby
        MessageEvent<MSGJoinLobby> playerJoin = new MessageEvent<MSGJoinLobby>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGJoinLobby recv, Agent sender) {
                Lobby lobby = lobbyMap.get(recv.getLobbyName());

                if (lobby == null) { // if lobby hasn't been made yet, make player owner
                    lobby = new Lobby(sender);

                    synchronized (lobbyMap) {
                        lobbyMap.put(recv.getLobbyName(), lobby);
                    }
                } else if (lobby.getGameSession() != null) {
                    // if the lobby has already started game
                    return MessageWrapper.prepWraps(new MessageWrapper(
                            new MSGQuery(MSGQuery.QueryType.GAME_ALREADY_STARTED, true, getServerType()),
                            sender));
                } else {
                    synchronized (lobby) {
                        lobby.addPlayer(sender);
                    }
                }

                synchronized (playerLobbyMap) {
                    playerLobbyMap.put(sender, lobby);
                }

                Message msg1 = new MSGQuery(MSGQuery.QueryType.GAME_ALREADY_STARTED, false, getServerType());
                MSGAgentChanged msg2 = new MSGAgentChanged(MSGAgentChanged.NewStatus.JOINED, false, sender);
                // TODO: Is there a cleaner way to do this?
                Set<Agent> retSend = new HashSet<>(lobby.getAgents());
                retSend.remove(sender);

                System.out.println("Agents size: " + lobby.getAgents().size());

                return MessageWrapper.prepWraps(
                        new MessageWrapper(msg1, sender),
                        new MessageWrapper(msg2, retSend));
            }
        };

        // when some player sends chat msg, broadcast it to all other players
        MessageEvent<MSGChat> chatReceived = new MessageEvent<MSGChat>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGChat recMessage, Agent sender) {
                Lobby lobbyChat = playerLobbyMap.get(sender);

                return MessageWrapper.prepWraps(
                        new MessageWrapper(recMessage,
                                playerLobbyMap.get(sender).getAgents()));
            }
        };

        // when Start Game is pressed by the owner (and received by server)
        MessageEvent<MSGGameStatus> startGame = new MessageEvent<MSGGameStatus>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGGameStatus msg, Agent sender) {
                // TODO: This only works with one lobby..
                Lobby lobby = playerLobbyMap.get(sender);
                if (lobby != null && lobby.getOwner().equals(sender)) {
                    lobby.prepareGame();

                    Message sendMsg = new MSGGameStatus(
                            MSGGameStatus.GameStatus.STARTED,
                            lobby.getGameSession());

                    // send back the clients the initial game state
                    return MessageWrapper.prepWraps(
                            new MessageWrapper(sendMsg,
                                    playerLobbyMap.get(sender).getAgents()));
                }

                return null;
            }
        };


        // when player makes a move, broadcast it to all other users
        MessageEvent<MSGGameAction> playerMakesMove = new MessageEvent<MSGGameAction>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGGameAction msg, Agent sender) {
                Lobby lobby = playerLobbyMap.get(sender);
                lobby.getGameSession().incrementBoard(msg.getMoveLocation(), msg.getLetter());

                if (msg.getMoveLocation() == null || msg.getLetter() == null) { // player skipped turn
                    Agent prevPlayer = lobby.getGameSession().getCurrentTurn();
                    lobby.getGameSession().nextTurn(true);

                    if (lobby.getGameSession().allPlayersSkipped()) { // when all players skipped their turn
                        Message msgEndGame = new MSGGameStatus(
                                MSGGameStatus.GameStatus.ENDED,
                                null);

                        return MessageWrapper.prepWraps(new MessageWrapper
                                (msgEndGame, lobby.getAgents()));
                    } else { // new turn
                        Message msgSkip = new MSGNewTurn(
                                prevPlayer,
                                lobby.getGameSession().getCurrentTurn(),
                                lobby.getGameSession().getScores().get(prevPlayer),
                                true);

                        return MessageWrapper.prepWraps(
                                new MessageWrapper(msgSkip, playerLobbyMap.get(sender).getAgents())
                        );
                    }
                }

                // otherwise, send this action to all other players, so they can then vote
                return MessageWrapper.prepWraps(new MessageWrapper(msg,
                        playerLobbyMap.get(sender).getAgents()));
            }
        };


        MessageEvent<MSGGameVote> voteReceived = new MessageEvent<MSGGameVote>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGGameVote msg, Agent sender) {

                // TODO: add logic here
                Lobby lobby = playerLobbyMap.get(sender);
                lobby.getGameSession().addVote(msg.isAccepted(), msg.getOrient());

                if (lobby.getGameSession().allVoted()) {
                    // all players voted, move onto next player
                    Agent prevPlayer = lobby.getGameSession().getCurrentTurn();
                    lobby.getGameSession().nextTurn(false);

                    Message msgNextTurn = new MSGNewTurn(
                            prevPlayer,
                            lobby.getGameSession().getCurrentTurn(),
                            lobby.getGameSession().getScores().get(prevPlayer),
                            false);

                    // if board is full, end the game
                    if (lobby.getGameSession().isBoardFull()) {
                        Message msgEndGame = new MSGGameStatus(MSGGameStatus.GameStatus.ENDED, null);

                        // TODO: Better structure protocol
                        // send additional message to end game
                        return MessageWrapper.prepWraps(
                                new MessageWrapper(msgNextTurn, lobby.getAgents()),
                                new MessageWrapper(msgEndGame, lobby.getAgents()));
                    } else {
                        return MessageWrapper.prepWraps(
                                new MessageWrapper(msgNextTurn, lobby.getAgents()));
                    }
                }

                return null;
            }
        };

        // ping back to the user
        MessageEvent<MSGPing> pingReceived =  new MessageEvent<MSGPing>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGPing msg, Agent sender) {
                return MessageWrapper.prepWraps(new MessageWrapper(msg, sender));
            }
        };

        MessageEvent<MSGQuery> requestPlayers = new MessageEvent<MSGQuery>() {
            @Override
            public MessageWrapper[] onMsgReceive(MSGQuery recMessage, Agent sender) {
                if (recMessage.getQueryType() != MSGQuery.QueryType.GET_PLAYER_LIST)
                    return null;

                Lobby lobby = playerLobbyMap.get(sender);
                Message msg = new MSGAgentChanged(
                        MSGAgentChanged.NewStatus.JOINED,
                        lobby.getAgents());

                return MessageWrapper.prepWraps(new MessageWrapper(msg, sender));
            }
        };
    }

    public ScrabbleServerListener(String name, ConnectType connectType) {
        super(name, connectType);

        if (connectType == ConnectType.INTERNET) {

        }
    }

    @Override
    protected void reset() {
        super.reset();
        lobbyMap = Maps.synchronizedBiMap(HashBiMap.create());
        playerLobbyMap = new HashMap<>(); // TODO: concurrent hash map
    }

    @Override
    protected void onUserConnect(Socket s) {
    }

    @Override
    protected void prepareEvents() {
        // add events
        ServerEvents events = new ServerEvents();
        eventList.addEvents(
                events.chatReceived,
                events.pingReceived,
                events.playerJoin,
                events.startGame,
                events.voteReceived,
                events.playerMakesMove,
                events.requestPlayers
        );
    }

    @Override
    protected boolean onMessageReceived(MessageWrapper msgRec, Socket s) throws IOException {
        if (connections.get(s) == null) { // if player hasn't been authenticated yet

            if (msgRec.getMessageType() == Message.MessageType.LOGIN) {
                Agent player = (Agent)((MSGLogin)msgRec.getMessage()).getPlayer();
                boolean is_unique = false;

                synchronized (connections) {
                    // register user associated with socket
                    if (!connections.values().contains(player)) {
                        connections.put(s, player);
                        is_unique = true;
                    }
                }

                // send back message if their name is unique to the server
                sendMessage(new MSGQuery(MSGQuery.QueryType.AUTHENTICATED, is_unique, getServerType()), s);
                return is_unique;
            }
        }

        return true;
    }

    @Override
    protected void onUserDisconnect(Agent p) {
        System.out.println("From server disconnect: " + p);
        if (p == null)
            return;

        // TODO [LOBBY FUNCTIONALITY]: Complete this (for multiple lobbies) in the future
        Lobby lobby = playerLobbyMap.get(p);
        System.out.println("Lobby: " + lobby);

        synchronized (lobby.getAgents()) {
            lobby.getAgents().remove(p);
        }

        if (lobby.getOwner().equals(p)) {
            synchronized (playerLobbyMap) {
                playerLobbyMap.remove(p);
            }
        }

        if (lobby.getAgents().isEmpty()) {
            synchronized (lobbyMap) {
                lobbyMap.inverse().remove(lobby);
            }
        }

        sendMessage(new MessageWrapper(
                new MSGAgentChanged(
                        MSGAgentChanged.NewStatus.DISCONNECTED,
                        lobby.getOwner().equals(p), p),
                lobby.getAgents()));
    }
}
