package core.message;

import core.Player;

public abstract class Message {
    private MessageType msgType;
    // TODO: Debug
    private boolean superCalled = false;

    public Message() {
        msgType = MessageType.fromMessageClass(this.getClass());
        superCalled = true;
    }

    public MessageType getMessageType() {
        if (!superCalled)
            System.out.println("[WARNING] A Message has not had its super constructor called.");

        return msgType;
    }
}
