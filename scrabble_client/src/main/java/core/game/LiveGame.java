package core.game;

import core.messageType.GameVoteMsg;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LiveGame {
    // TODO: Temporary scaffolds, terrible code
    public static final int NUM_ROWS = 20;
    public static final int NUM_COLS = 20;

    private List<Agent> players;
    private Map<Agent, Integer> scores;
    private Agent currentTurn;

    private int numFilled;
    private transient Map<GameRules.Orientation, Integer> voteScore;
    private transient Map<GameRules.Orientation, Integer> numVoted;
    private Board board;
    private transient Point lastLetterPos;
    private transient int numSkipConsecutive;

    public LiveGame(List<Agent> players) {
        // TODO: somewhat inefficient. For now, ONLY the server needs
        // to call this constructor
        scores = new LinkedHashMap<>();
        for (Agent a : players)
            scores.put(a, 0);

        this.players = players;

        // TODO: fix this
        this.board = new Board(20, 20);

        // TODO: simple for now..
    }

    private void resetVotes() {
        voteScore = new HashMap<>(2);
        numVoted = new HashMap<>(2);

        for (GameRules.Orientation o : GameRules.getValidOrientations(board, lastLetterPos).keySet()) {
            voteScore.put(o, 0);
            numVoted.put(o, 0);
        }
    }

    // also calculates score;
    public void nextTurn(boolean skipped) {
        int idx = players.indexOf(currentTurn);
        int new_idx = (idx + 1) % players.size();

        if (skipped) {
            numSkipConsecutive++;
        } else {
            numSkipConsecutive = 0;
            scores.put(currentTurn, scores.get(currentTurn) + calculateScore());
        }
        currentTurn = players.get(new_idx);
    }

    public boolean allPlayersSkipped() {
        return numSkipConsecutive == players.size();
    }

    public void addVote(boolean accepted, GameRules.Orientation orient) {
        numVoted.put(orient, numVoted.get(orient) + 1);

        if (accepted)
            voteScore.put(orient, voteScore.get(orient) + 1);
    }

    public boolean allVoted() {
        // TODO: Debug
        for (Map.Entry<GameRules.Orientation, Integer> entry : numVoted.entrySet()) {
            if (entry.getValue() != players.size())
                return false;
        }

        return true;
    }

    private int calculateScore() {
        int totalAdd = 0;
        Map<GameRules.Orientation, String> wordMap = GameRules.getValidOrientations(board, lastLetterPos);

        for (GameRules.Orientation o : wordMap.keySet()) {
            if (numVoted.get(o) == voteScore.get(o))
                totalAdd += wordMap.get(o).length();
        }

        return totalAdd;
    }

    public void incrementBoard(Point pos, Character letter) {
        if (pos == null || letter == null)
            return;

        board.set(pos, letter);
        lastLetterPos = pos;
        numFilled++;

        resetVotes();
    }

    public boolean isBoardFull() {
        return numFilled == NUM_COLS * NUM_ROWS;
    }

    public Board getBoard() {
        return board;
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
