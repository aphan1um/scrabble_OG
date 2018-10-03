package core.messageType;

import core.message.Message;

public class QueryMsg implements Message {
    public enum QueryType {
        IS_ID_UNIQUE,
        JOIN_LOBBY
    }

    private QueryType queryType;
    private boolean value;

    public QueryMsg(QueryType queryType, boolean value) {
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
