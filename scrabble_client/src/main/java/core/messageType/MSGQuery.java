package core.messageType;

import core.ConnectType;
import core.message.Message;

public class MSGQuery implements Message {
    public enum QueryType {
        AUTHENTICATED,
        GAME_ALREADY_STARTED,
        GET_PLAYER_LIST
    }

    private QueryType queryType;
    private boolean value;
    private ConnectType serverType;

    public MSGQuery(QueryType queryType, boolean value, ConnectType serverType) {
        this.queryType = queryType;
        this.value = value;
        this.serverType = serverType;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public boolean getValue() { return value; }

    public ConnectType getServerType() {
        return serverType;
    }
}
