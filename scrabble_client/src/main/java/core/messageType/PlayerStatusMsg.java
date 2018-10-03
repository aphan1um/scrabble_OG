package core.messageType;

import core.game.Agent;
import core.message.Message;

// Request Agent Data (PD) Message
public class PlayerStatusMsg implements Message {
    private Agent agent;
    private NewStatus status;

    public enum NewStatus {
        DISCONNECTED,
        JOINED
    }

    public PlayerStatusMsg(Agent agent, NewStatus status) {
        super();
        this.agent = agent;
        this.status = status;
    }

    public Agent getAgent() {
        return agent;
    }

    public NewStatus getStatus() {
        return status;
    }
}
