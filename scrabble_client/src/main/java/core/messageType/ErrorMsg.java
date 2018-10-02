package core.messageType;

import core.message.Message;

public class ErrorMsg implements Message {
    public enum ErrorType {
        DUPLICATE_ID,
        DEMAND_PLAYER_DETAILS,
    }

    public ErrorType errorType;

    public ErrorMsg(ErrorType errType) {
        this.errorType = errType;
    }
}
