package core.message;

import core.game.Agent;

import java.util.Set;

public interface MessageEvent<T extends Message> {
    public MessageWrapper[] onMsgReceive(T recMessage, Agent sender);
}
