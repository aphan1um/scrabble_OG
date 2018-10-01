package core.message;

import core.Message;
import core.MessageType;
import core.Player;

public class ChatMessage extends Message {
    public ChatMessage(Player sender) {
        super(sender, MessageType.CHAT);
    }
}
