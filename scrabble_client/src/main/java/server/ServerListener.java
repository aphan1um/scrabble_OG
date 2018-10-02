package server;

import core.SocketListener;
import core.game.Player;
import core.message.*;
import core.messageType.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class ServerListener extends SocketListener {

    public ServerListener() {
        super("Server");
        System.out.println("Started server...");

        /**
        // TODO: Dummy test
        MessageEvent test = new MessageEvent<PingMsg>() {
            @Override
            public Message onServerReceive(PingMsg recMessage) {
                System.out.println(recMessage.getMessageType() + " IM HERE");
                return null;
            }

            @Override
            public Message onClientReceive(PingMsg recMessage) {
                return null;
            }
        };

        eventList.addEvent(test);

        eventList.fireEvent(new RequestPDMsg(dummy_player));
        eventList.fireEvent(new PingMsg());

        eventList.removeEvent(test);
        eventList.fireEvent(new PingMsg());

        System.exit(0);
        **/
    }

    @Override
    protected void onUserConnect(Socket s) {
    }

    @Override
    protected void prepareEvents() {
        // return list of players back to player who sent details
        eventList.addEvent(new MessageEvent<RequestPDMsg>() {
            @Override
            public MessageWrapper onMsgReceive(RequestPDMsg recv, Set<Player> p, Player sender) {
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
            public MessageWrapper onMsgReceive(RequestPDMsg recv, Set<Player> p, Player sender) {
                //new PlayerStatusMsg()
                // return list of players back to player who sent details
                PlayerStatusMsg msg = new PlayerStatusMsg(sender, PlayerStatusMsg.NewStatus.JOINED);

                // TODO: Is there a cleaner way to do this?
                Set<Player> retSend = new HashSet<Player>(p);
                retSend.remove(sender);

                return new MessageWrapper(msg, retSend);
            }
        });

        eventList.addEvent(new MessageEvent<ChatMsg>() {
            @Override
            public MessageWrapper onMsgReceive(ChatMsg recMessage, Set<Player> players, Player sender) {
                System.out.println("Chat: " + recMessage.getChatMsg());
                return new MessageWrapper(recMessage, players);
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

            Player joinedPlayer = (Player)(
                    (RequestPDMsg)msgRec.getMessage())
                    .getPlayerList().toArray()[0];

            // if connecting player shares ID to another player in server
            // TODO: The player should only send their details once. This is
            // a limitation if the player wants to change their name, for example
            // TODO: We close connection in this case.
            if (connections.inverse().get(joinedPlayer) != null) {
                sendMessage(new ErrorMsg(ErrorMsg.ErrorType.DUPLICATE_ID), s);
                s.close();
            } else {
                connections.put(s, joinedPlayer);
            }
        }

        return true;
    }

    @Override
    protected void onUserDisconnect(Player p) {
        processMessage(new MessageWrapper(
                new PlayerStatusMsg(p, PlayerStatusMsg.NewStatus.DISCONNECTED),
                connections.values()));
    }
}
