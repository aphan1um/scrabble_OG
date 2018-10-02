package core.messageType;

import core.message.Message;

public class ChatMsg implements Message {
    private String text;

    public ChatMsg(String text) {
        super();
        this.text = text;
    }

    public String getChatMsg() {
        return text;
    }
}
