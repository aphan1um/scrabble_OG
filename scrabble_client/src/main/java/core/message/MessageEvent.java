package core.message;

import core.game.Player;

import java.util.Set;

public interface MessageEvent<T extends Message> {
    public MessageWrapper onMsgReceive(T recMessage, Set<Player> players, Player sender);
}
