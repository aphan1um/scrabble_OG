package core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.message.ChatMessage;
import core.message.PingMessage;
import core.message.ReqMessage;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    EMPTY(null),
    REQUEST(ReqMessage.class),
    CHAT(ChatMessage.class),
    PING(PingMessage.class);

    private final Class<? extends Message> cl;

    private MessageType(Class<? extends Message> cl) {
        this.cl = cl;
    }

    public Class<? extends  Message> getCorrespondingClass() {
        return cl;
    }

    private static Class<? extends Message> findClass(MessageType msgType) {
        for (MessageType m : MessageType.values()) {
            if (m == msgType)
                return m.cl;
        }

        return null;
    }

    /***
     * Converts the JSON string into the appropriate Message class, where
     * events will respond to that specific message type.
     * @param str
     * @param gson
     * @return
     */
    public static Message fromJSON(String str, Gson gson) {
        // TODO: Thanks to https://stackoverflow.com/a/31094365 for the hint
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(str);
        JsonObject obj = element.getAsJsonObject();

        Class<? extends Message> c = findClass(
                MessageType.valueOf(obj.get("msgType").getAsString()));

        if (c != null)
            return gson.fromJson(element, c);

        return null;
    }
}
