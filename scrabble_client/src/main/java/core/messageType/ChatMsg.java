package core.messageType;

import core.message.Message;
import core.Player;

public class ChatMsg extends Message {
    private String text;

    public ChatMsg(Player sender, String text) {
        super();
        this.text = text;
    }
}
