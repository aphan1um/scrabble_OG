package core.messageType;

import core.game.Agent;
import core.message.Message;

public class MSGChat implements Message {
    private String text;
    private Agent sender;
    private String lobbyName;

    public MSGChat(String text, Agent sender, String lobbyName) {
        this.text = text;
        this.sender = sender;
        this.lobbyName = lobbyName;
    }

    public String getChatMsg() {
        return text;
    }
    public Agent getSender() { return sender; }

    public String getLobbyName() { return lobbyName; }
}
