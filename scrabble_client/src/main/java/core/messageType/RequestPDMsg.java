package core.messageType;

import core.game.Agent;
import core.message.Message;

import java.util.Arrays;
import java.util.Collection;

public class RequestPDMsg implements Message {
    private Collection<Agent> agentList;

    public RequestPDMsg(Agent... agents) {
        super();
        this.agentList = Arrays.asList(agents);
    }

    public RequestPDMsg(Collection<Agent> agents) {
        super();
        this.agentList = agents;
    }

    public Collection<Agent> getAgentList() {
        return agentList;
    }
}
