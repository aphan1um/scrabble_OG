package core.messageType;

import core.message.Message;

public class MSGQuery implements Message {
    public enum QueryType {
        NON_UNIQUE_ID,
        ACCEPTED,
        GAME_ALREADY_MADE
    }

    private QueryType queryType;

    public MSGQuery(QueryType queryType) {
        this.queryType = queryType;
    }

    public QueryType getQueryType() {
        return queryType;
    }
}
