package core.message;

import core.Player;

import java.util.Set;

public interface MessageEvent<T extends Message> {
    public SendableMessage onMsgReceive(T recMessage, Set<Player> players, Player sender);
}
