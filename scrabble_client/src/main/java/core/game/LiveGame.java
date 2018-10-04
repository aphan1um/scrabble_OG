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
    private transient Map<GameVoteMsg.Orientation, Integer> voteScore;
    private transient Map<GameVoteMsg.Orientation, Integer> numVoted;
    private transient Map<Point, Character> board;
    private transient Point lastLetterPos;
    private transient int numSkipConsecutive;

    public LiveGame(List<Agent> players) {
        // TODO: somewhat inefficient. For now, ONLY the server needs
        // to call this constructor
        scores = new LinkedHashMap<>();
        for (Agent a : players)
            scores.put(a, 0);

        this.players = players;
        this.board = new HashMap<>();;

        // TODO: simple for now..
    }

    public void resetVotes() {
        voteScore = new HashMap<>(2);
        numVoted = new HashMap<>(2);

        for (GameVoteMsg.Orientation o : GameVoteMsg.Orientation.values()) {
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
        }

        scores.put(currentTurn, scores.get(currentTurn) + calculateScore());
        currentTurn = players.get(new_idx);
    }

    public boolean allPlayersSkipped() {
        return numSkipConsecutive == players.size();
    }

    public void addVote(boolean accepted, GameVoteMsg.Orientation orient) {
        numVoted.put(orient, numVoted.get(orient) + 1);
        voteScore.put(orient, voteScore.get(orient) + (accepted ? 0 : 1));
    }

    public boolean allVoted() {
        // TODO: Debug
        for (Map.Entry<GameVoteMsg.Orientation, Integer> entry : numVoted.entrySet()) {
            if (entry.getValue() != players.size())
                return false;
        }

        return true;
    }

    private int calculateScore() {
        int totalAdd = 0;
        Map<GameVoteMsg.Orientation, String> wordMap = GameVoteMsg.getAdjacentWords(board, lastLetterPos);

        for (GameVoteMsg.Orientation o : GameVoteMsg.Orientation.values()) {
            if (numVoted.get(o) == voteScore.get(o))
                totalAdd += wordMap.get(o).length();
        }

        return totalAdd;
    }

    public void incrementBoard(Point pos, char letter) {
        if (pos == null || letter == 0)
            return;

        board.put(pos, letter);
        lastLetterPos = pos;
        numFilled++;
    }

    public boolean isBoardFull() {
        return numFilled == NUM_COLS * NUM_ROWS;
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
