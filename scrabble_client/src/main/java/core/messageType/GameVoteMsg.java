package core.messageType;

import core.message.Message;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static core.game.LiveGame.NUM_COLS;
import static core.game.LiveGame.NUM_ROWS;

/**
 * From client: From a client, the listeners records their decision
 * if they think the string is a word or not.
 *
 * From listeners: Broadcasts to all players the verdict via accepted.
 */
public class GameVoteMsg implements Message {
    public enum Orientation {
        HORIZONTAL,
        VERTICAL,
    }

    private Orientation orient;
    private boolean accepted;

    public GameVoteMsg(Orientation orient, boolean accepted) {
        this.orient = orient;
        this.accepted = accepted;
    }

    public Orientation getOrient() {
        return orient;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public static Map<Orientation, String> getAdjacentWords(Map<Point, Character> board, Point pos) {
        Map<Orientation, String> ret = new HashMap<>(2);

        // horizontal direction
        int[] range = new int[2];
        String word = "";

        for (int t = 0; t < range.length; t++) { // horizontal
            int dir = (int)Math.pow(-1, t);

            range[t] = pos.x;
            while (range[t] < NUM_COLS && range[t] >= 0) {
                Character letter = board.get(new Point(range[t], pos.y));

                if (letter == null)
                    break;

                word = (t == 1) ? word + letter.toString() : letter.toString() + word;
                range[t] += dir;
            }
        }
        ret.put(Orientation.HORIZONTAL, word);


        // vertical
        range = new int[2];
        word = "";
        for (int t = 0; t < range.length; t++) { // horizontal
            int dir = (int)Math.pow(-1, t);

            range[t] = pos.y;
            while (range[t] < NUM_ROWS && range[t] >= 0) {
                Character letter = board.get(new Point(pos.x, range[t]));

                if (letter == null)
                    break;

                word = (t == 1) ? word + letter.toString() : letter.toString() + word;
                range[t] += dir;
            }
        }
        ret.put(Orientation.VERTICAL, word);

        return ret;
    }
}
