package core.messageType;

import core.message.Message;
import core.Player;

public class ReqMessage extends Message {
    public ReqMessage(Player sender) {
        super(sender);
    }
}
