package core.messageType;

import core.game.Agent;
import core.message.Message;

public class ChatMsg implements Message {
    private String text;
    private Agent sender;

    public ChatMsg(String text, Agent sender) {
        this.text = text;
        this.sender = sender;
    }

    public String getChatMsg() {
        return text;
    }
    public Agent getSender() { return sender; }
}
