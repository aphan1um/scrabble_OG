package core.game;

import java.util.ArrayList;
import java.util.List;

// TODO: Concurrency issues?
public class Lobby {
    private transient Agent owner;
    private transient LiveGame gameSession;
    private transient List<Agent> agents;

    public Lobby(Agent owner) {
        this.owner = owner;
        this.agents = new ArrayList<>();

        agents.add(owner);
    }

    public boolean removePlayer(Agent agent) {
        if (agent.equals(owner)) {
            return true;
        }
        agents.remove(agent);
        return agents.size() == 0;
    }

    // this is of course under the assumption one player can host one game
    @Override
    public boolean equals(Object obj) {
        return obj instanceof  Lobby && ((Lobby)obj).owner.equals(this.owner);
    }

    @Override
    public int hashCode() {
        return owner.hashCode();
    }
}
