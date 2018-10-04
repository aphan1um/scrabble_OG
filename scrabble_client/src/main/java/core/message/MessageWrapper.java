package core.message;

import core.game.Agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MessageWrapper {
    private Message msg;
    private Message.MessageType msgType;
    public long[] timeStamps;
    private transient Collection<Agent> sendTo;

    public static MessageWrapper[] prepWraps(MessageWrapper... msgs) {
        return msgs;
    }

    public MessageWrapper(Message msg, Collection<Agent> sendTo) {
        this.msg = msg;
        this.sendTo = sendTo;
        this.msgType = Message.fromMessageClass(msg.getClass());
    }

    public MessageWrapper(Message msg, Agent... sendTo) {
        this.msg = msg;
        this.msgType = Message.fromMessageClass(msg.getClass());

        if (sendTo != null)
            this.sendTo = Arrays.asList(sendTo);
    }

    public Message getMessage() {
        return msg;
    }

    public Message.MessageType getMessageType() { return msgType; }

    public Collection<Agent> getSendTo() {
        return sendTo;
    }

    public void addTimeStamps(long... timeStamps) {
        this.timeStamps = timeStamps;
    }

    public long[] getTimeStamps() {
        return timeStamps;
    }
}
