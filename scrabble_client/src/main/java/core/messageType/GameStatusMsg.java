package core.messageType;

import core.game.Player;
import core.message.Message;

public class GameStatusMsg implements Message {
    public enum GameStatus {
        STARTED,
        ENDED
    }

    private GameStatus status;
    private Player targetPlayer;

    public GameStatusMsg(GameStatus status, Player targetPlayer) {
        this.status = status;
        this.targetPlayer = targetPlayer;
    }

    public GameStatus getGameStatus() {
        return status;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }
}
