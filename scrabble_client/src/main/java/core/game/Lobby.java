package core.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO: Concurrency issues?
public class Lobby {
    private transient Agent owner;
    private transient LiveGame gameSession;
    private transient List<Agent> agents;
    private transient String descript;

    private transient int turnNumber = 0;

    public Lobby(Agent owner, String descript) {
        this.owner = owner;
        this.agents = new ArrayList<>();
        agents.add(owner);
    }

    public void prepareGame() {
        gameSession = new LiveGame(agents);
        gameSession.setCurrentTurn(agents.get(turnNumber));
    }

    public Agent getOwner() {
        return owner;
    }

    public void addPlayer(Agent agent) {
        synchronized (agents) {
            agents.add(agent);
        }
    }

    public boolean removePlayer(Agent agent) {
        synchronized (agents) {
            if (agent.equals(owner)) {
                return true;
            }
            agents.remove(agent);
            return agents.size() == 0;
        }
    }

    public Collection<Agent> getAgents() {
        return agents;
    }

    public String getDescription() { return descript; }

    public LiveGame getGameSession() {
        return gameSession;
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
