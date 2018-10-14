package core.messageType;

import core.game.Agent;
import core.message.Message;

import java.util.Arrays;
import java.util.Collection;

public class MSGAgentChanged implements Message {
    private Collection<Agent> agents;
    private NewStatus status;
    private boolean hostLeft;

    public enum NewStatus {
        DISCONNECTED,
        JOINED
    }

    public MSGAgentChanged(NewStatus status, boolean hostLeft, Agent... agents) {
        super();
        this.status = status;
        this.agents = Arrays.asList(agents);
        this.hostLeft = hostLeft;
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

    public boolean hasHostLeft() {
        return hostLeft;
    }
}
