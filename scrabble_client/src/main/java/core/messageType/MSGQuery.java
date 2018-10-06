package core.messageType;

import core.message.Message;

public class MSGQuery implements Message {
    public enum QueryType {
        IS_ID_UNIQUE,
        JOIN_LOBBY,
        GAME_ALREADY_MADE
    }

    private QueryType queryType;
    private boolean value;

    public MSGQuery(QueryType queryType, boolean value) {
        this.queryType = queryType;
        this.value = value;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public boolean getValue() {
        return value;
    }
}
