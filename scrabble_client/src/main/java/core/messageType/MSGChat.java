package core.messageType;

import core.game.Agent;
import core.message.Message;

public class MSGChat implements Message {
    private String text;
    private Agent sender;

    public MSGChat(String text, Agent sender) {
        this.text = text;
        this.sender = sender;
    }

    public String getChatMsg() {
        return text;
    }
    public Agent getSender() { return sender; }
}
