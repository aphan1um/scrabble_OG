package core.message;

import core.Player;

import java.util.Arrays;
import java.util.Collection;

public class SendableMessage {
    private transient Collection<Player> sendTo;
    private Message msg;
    private MessageType msgType;

    public SendableMessage(Message msg, Collection<Player> sendTo) {
        this.msg = msg;
        this.sendTo = sendTo;
        this.msgType = MessageType.fromMessageClass(msg.getClass());
    }

    public SendableMessage(Message msg, Player sendTo) {
        this.msg = msg;
        this.sendTo = Arrays.asList(new Player[] { sendTo } );
        this.msgType = MessageType.fromMessageClass(msg.getClass());
    }

    public SendableMessage(Message msg) {
        this.msg = msg;
        this.msgType = MessageType.fromMessageClass(msg.getClass());
    }

    public Message getMessage() {
        return msg;
    }

    public MessageType getMessageType() { return msgType; }

    public Collection<Player> getSendTo() {
        return sendTo;
    }
}
