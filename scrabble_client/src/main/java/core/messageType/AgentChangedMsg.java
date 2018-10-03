package core.messageType;

import core.game.Agent;
import core.message.Message;

import java.util.Arrays;
import java.util.Collection;

public class AgentChangedMsg implements Message {
    private Collection<Agent> agents;
    private NewStatus status;

    public enum NewStatus {
        DISCONNECTED,
        JOINED
    }

    public AgentChangedMsg(NewStatus status, Agent... agents) {
        super();
        this.status = status;
        this.agents = Arrays.asList(agents);
    }

    public AgentChangedMsg(NewStatus status, Collection<Agent> agents) {
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
