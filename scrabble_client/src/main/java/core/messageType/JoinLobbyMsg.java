package core.messageType;

import core.message.Message;

public class JoinLobbyMsg implements Message {
    private String name;

    public JoinLobbyMsg(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
