package core;

import com.google.gson.Gson;

import java.io.IOException;

public abstract class Message {
    private Player sender;
    private transient Player sendto; // Player to send message to
    /*** If message should be sent to all players in some lobby. */
    private transient boolean broadcast;
    private MessageType msgType;

    public Message(Player sender, MessageType msgType) {
        this.sender = sender;
        this.msgType = msgType;
    }

    public Message() {
        this.sender = null;
        this.msgType = MessageType.EMPTY;
    }

    public MessageType getMessageType() {
        return msgType;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    private void setSendTo(Player p) {
        this.sendto = sendto;
    }

    public Player getSendTo() {
        return sendto;
    }
}
