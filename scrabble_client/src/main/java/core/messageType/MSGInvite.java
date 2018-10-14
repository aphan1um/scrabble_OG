package core.messageType;

import core.game.Agent;
import core.message.Message;

public class MSGInvite implements Message {
    private String lobbyName;
    private Agent player;

    public MSGInvite(String lobbyName, Agent player) {
        this.lobbyName = lobbyName;
        this.player = player;
    }


    public String getLobbyName() {
        return lobbyName;
    }

    public Agent getPlayer() {
        return player;
    }
}
