package core.messageType;

import core.game.Player;
import core.message.Message;

public class ChatMsg implements Message {
    private String text;
    private Player sender;

    public ChatMsg(String text, Player sender) {
        this.text = text;
        this.sender = sender;
    }

    public String getChatMsg() {
        return text;
    }
    public Player getSender() { return sender; }
}
