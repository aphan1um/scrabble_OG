package core.messageType;

import core.message.Message;

public class ErrorMsg extends Message {
    public enum ErrorType {
        DUPLICATE_ID
    }

    public ErrorType errorType;

    public ErrorMsg(ErrorType errType) {
        this.errorType = errType;
    }
}
