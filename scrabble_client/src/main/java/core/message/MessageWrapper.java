package core.message;

import core.game.Player;

import java.util.Arrays;
import java.util.Collection;

public class MessageWrapper {
    private transient Collection<Player> sendTo;
    private Message msg;
    private Message.MessageType msgType;

    public MessageWrapper(Message msg, Collection<Player> sendTo) {
        this.msg = msg;
        this.sendTo = sendTo;
        this.msgType = Message.fromMessageClass(msg.getClass());
    }

    public MessageWrapper(Message msg, Player sendTo) {
        this.msg = msg;
        this.sendTo = Arrays.asList(new Player[] { sendTo } );
        this.msgType = Message.fromMessageClass(msg.getClass());
    }

    public MessageWrapper(Message msg) {
        this.msg = msg;
        this.msgType = Message.fromMessageClass(msg.getClass());
    }

    public Message getMessage() {
        return msg;
    }

    public Message.MessageType getMessageType() { return msgType; }

    public Collection<Player> getSendTo() {
        return sendTo;
    }
}
