package core.messageType;

import core.game.Agent;
import core.message.Message;

public class MSGJoinLobby implements Message {
    private String lobbyName;
    private Agent player;

    public MSGJoinLobby(Agent player, String lobbyName) {
        this.lobbyName = lobbyName;
        this.player = player;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public Agent getPlayer() { return player; }
}
