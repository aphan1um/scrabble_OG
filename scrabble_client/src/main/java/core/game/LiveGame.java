package core.game;

import java.util.*;

public class LiveGame {
    private Map<Agent, Integer> scores;
    private Agent currentTurn;

    public LiveGame(Collection<Agent> players) {
        // TODO: somewhat inefficient
        scores = new HashMap<>();
        for (Agent a : players)
            scores.put(a, 0);

        // TODO: simple for now..
    }

    public Integer getScore(Agent agent) {
        return scores.get(agent);
    }

    public Agent getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Agent currentTurn) {
        this.currentTurn = currentTurn;
    }
}
