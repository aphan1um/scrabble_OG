package core.messageType;

import core.message.Message;
import core.Player;

public class ChatMessage extends Message {
    public ChatMessage(Player sender) {
        super(sender);
    }
}
