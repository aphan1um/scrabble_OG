package core.messageType;

import core.game.Agent;
import core.message.Message;

public class GameStatusMsg implements Message {
    public enum GameStatus {
        STARTED,
        ENDED
    }

    private GameStatus status;
    private Agent targetAgent;

    public GameStatusMsg(GameStatus status, Agent targetAgent) {
        this.status = status;
        this.targetAgent = targetAgent;
    }

    public GameStatus getGameStatus() {
        return status;
    }

    public Agent getTargetAgent() {
        return targetAgent;
    }
}
