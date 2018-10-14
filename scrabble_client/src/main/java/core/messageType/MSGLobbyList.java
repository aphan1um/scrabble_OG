package core.messageType;

import core.message.Message;

import java.util.Collection;

public class MSGLobbyList implements Message {
    public Collection<String> lobbies;

    public MSGLobbyList(Collection<String> lobbies) {
        this.lobbies = lobbies;
    }

    public Collection<String> getLobbies() {
        return lobbies;
    }
}
