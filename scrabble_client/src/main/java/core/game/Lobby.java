package core.game;

import java.util.ArrayList;
import java.util.List;

// TODO: Concurrency issues?
public class Lobby {
    private List<Agent> agentList;
    private transient Agent owner;

    public Lobby(Agent owner) {
        this.owner = owner;
        agentList = new ArrayList<>();
        agentList.add(owner);
    }
}
