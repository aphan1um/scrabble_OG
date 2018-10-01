package core.message;

import core.Message;
import core.MessageType;
import core.Player;

public class PingMessage extends Message {
    public PingMessage(Player sender) {
        super(sender, MessageType.PING);
    }

    public PingMessage() {
        super(null, MessageType.PING);
    }
}
