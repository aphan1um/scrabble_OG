package core.messageType;

import core.game.Agent;
import core.message.Message;

import java.util.Arrays;
import java.util.Collection;

public class MSGAgentChanged implements Message {
    private Collection<Agent> agents;
    private NewStatus status;

    public enum NewStatus {
        DISCONNECTED,
        JOINED,
        REQUEST
    }

    public MSGAgentChanged(NewStatus status, Agent... agents) {
        super();
        this.status = status;
        this.agents = Arrays.asList(agents);
    }

    public MSGAgentChanged(NewStatus status, Collection<Agent> agents) {
        super();
        this.status = status;
        this.agents = agents;
    }

    public Collection<Agent> getAgents() {
        return agents;
    }

    public NewStatus getStatus() {
        return status;
    }
}
