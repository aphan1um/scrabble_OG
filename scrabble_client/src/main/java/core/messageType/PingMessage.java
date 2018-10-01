package core.messageType;

import core.message.Message;
import core.Player;

public class PingMessage extends Message {
    public PingMessage(Player sender) {
        super(sender);
    }

    public PingMessage() {
        super(null);
    }
}
