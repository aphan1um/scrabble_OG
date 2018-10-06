package core.messageType;

import core.game.Agent;
import core.message.Message;

// represents results from last turn, as well as indicator to new turn
public class MSGNewTurn implements Message {
    private Agent last_player;
    private Agent next_player;
    private int new_points;
    private boolean skippedTurn;

    public MSGNewTurn(Agent last_player, Agent next_player,
                      int new_points, boolean skippedTurn) {

        this.last_player = last_player;
        this.next_player = next_player;
        this.new_points = new_points;
        this.skippedTurn = skippedTurn;
    }

    public int getNewPoints() {
        return new_points;
    }

    public boolean hasSkippedTurn() {
        return skippedTurn;
    }

    public Agent getLastPlayer() {
        return last_player;
    }

    public Agent getNextPlayer() {
        return next_player;
    }
}
