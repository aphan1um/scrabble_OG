package core.message;

import core.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SendableMessage {
    private Collection<Player> sendTo;
    private Message msg;

    public SendableMessage(Message msg, Collection<Player> sendTo) {
        this.msg = msg;
        this.sendTo = sendTo;
    }

    public SendableMessage(Message msg, Player sendTo) {
        this.msg = msg;
        this.sendTo = Arrays.asList(new Player[] { sendTo } );
    }

    public Message getMessage() {
        return msg;
    }

    public Collection<Player> getSendTo() {
        return sendTo;
    }
}
