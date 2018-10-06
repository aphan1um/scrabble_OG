package core.message;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import core.game.Agent;
import javafx.scene.layout.Pane;

import java.util.*;

public class MessageWrapper {
    private Message msg;
    private Message.MessageType msgType;
    private List<Long> timeStamps;
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

    public void appendRecentTime() {
        if (timeStamps == null)
            timeStamps = Arrays.asList(new Long[] { System.nanoTime() });
        else
            timeStamps.add(System.nanoTime());
    }

    public List<Long> getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(JsonArray times, Gson gson) {
        timeStamps = gson.fromJson(times, new TypeToken<List<Long>>() {}.getType());
    }

    public void setTimeStamps(List<Long> times) {
        timeStamps = times;
    }
}
