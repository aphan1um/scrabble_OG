package core.messageType;

import core.LetterRange;
import core.message.Message;

/**
 * From client: From a client, the server records their decision
 * if they think the string is a word or not.
 *
 * From server: Broadcasts to all players the verdict via accepted.
 */
public class GameVoteMsg extends Message {
    private LetterRange lrange;
    private boolean accepted;

    public GameVoteMsg(LetterRange lrange, boolean accepted) {
        this.lrange = lrange;
        this.accepted = accepted;
    }
}
