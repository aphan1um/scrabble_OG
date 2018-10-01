package core.message;

import core.Player;

public abstract class Message {
    private Player sender;
    private transient Player sendto; // Player to send messageType to
    /*** If messageType should be sent to all players in some lobby. */
    private transient boolean broadcast;
    private MessageType msgType;

    public Message(Player sender) {
        this.sender = sender;
        msgType = MessageType.fromMessageClass(this.getClass());
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
