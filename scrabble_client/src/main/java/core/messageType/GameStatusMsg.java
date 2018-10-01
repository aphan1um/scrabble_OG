package core.messageType;

import core.Player;
import core.message.Message;

public class GameStatusMsg extends Message {
    public enum GameStatus {
        START,
        ENDED
    }

    private Player targetPlayer;
    private GameStatus status;

    public GameStatusMsg(GameStatus status, Player targetPlayer) {
        this.status = status;
        this.targetPlayer = targetPlayer;
    }
}
