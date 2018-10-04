package core.message;

import core.game.Agent;

import java.util.Arrays;
import java.util.Collection;

public class MessageWrapper {
    private transient Collection<Agent> sendTo;
    private Message msg;
    private Message.MessageType msgType;

    public static MessageWrapper[] prepWraps(MessageWrapper... msgs) {
        return msgs;
    }

    public MessageWrapper(Message msg, Collection<Agent> sendTo) {
        this.msg = msg;
        this.sendTo = sendTo;
        this.msgType = Message.fromMessageClass(msg.getClass());
    }

    public MessageWrapper(Message msg, Agent sendTo) {
        this.msg = msg;
        this.sendTo = Arrays.asList(new Agent[] { sendTo } );
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

    public Collection<Agent> getSendTo() {
        return sendTo;
    }
}
