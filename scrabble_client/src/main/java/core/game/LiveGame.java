package core.game;

import java.util.*;

public class LiveGame {
    private Map<Agent, Integer> scores;
    private Agent currentTurn;

    public LiveGame(Collection<Agent> players) {
        // TODO: somewhat inefficient
        scores = new LinkedHashMap<>();
        for (Agent a : players)
            scores.put(a, 0);

        // TODO: simple for now..
    }

    public Map<Agent, Integer> getScores() {
        return scores;
    }

    public Agent getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Agent currentTurn) {
        this.currentTurn = currentTurn;
    }
}
