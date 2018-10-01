package core.message;

import core.Message;
import core.MessageType;
import core.Player;

public class ReqMessage extends Message {
    public ReqMessage(Player sender) {
        super(sender, MessageType.REQUEST);
    }
}
