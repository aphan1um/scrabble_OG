package core.messageType;

import core.message.Message;

public class MSGJoinLobby implements Message {
    private String name;

    public MSGJoinLobby(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
