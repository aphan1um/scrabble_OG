package core.messageType;

import core.game.Agent;
import core.message.Message;

public class GameStatusMsg implements Message {
    public enum GameStatus {
        STARTED,
        ENDED
    }

    private GameStatus status;

    public GameStatusMsg(GameStatus status) {
        this.status = status;
    }

    public GameStatus getGameStatus() {
        return status;
    }
}
