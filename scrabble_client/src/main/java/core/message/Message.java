package core.message;

import core.Player;

public abstract class Message {
    private MessageType msgType;

    public Message() {
        msgType = MessageType.fromMessageClass(this.getClass());
    }

    public MessageType getMessageType() {
        return msgType;
    }
}
