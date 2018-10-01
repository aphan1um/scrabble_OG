package core.messageType;

import core.message.Message;
import core.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class RequestPDMsg extends Message {
    private Collection<Player> playerList;

    public RequestPDMsg(Player... players) {
        super();
        this.playerList = Arrays.asList(players);
    }

    public RequestPDMsg(Collection<Player> players) {
        super();
        this.playerList = players;
    }

    public Collection<Player> getPlayerList() {
        return playerList;
    }
}
