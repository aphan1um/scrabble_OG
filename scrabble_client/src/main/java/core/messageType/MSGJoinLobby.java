package core.messageType;

import core.message.Message;

public class MSGJoinLobby implements Message {
    private String lobbyName;

    public MSGJoinLobby(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
