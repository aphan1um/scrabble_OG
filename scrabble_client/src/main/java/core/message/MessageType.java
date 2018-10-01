package core.message;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.messageType.ChatMessage;
import core.messageType.PingMessage;
import core.messageType.ReqMessage;

public enum MessageType {
    EMPTY(null),
    REQUEST(ReqMessage.class),
    CHAT(ChatMessage.class),
    PING(PingMessage.class);

    private final Class<? extends Message> cl;

    // TODO: Clean up
    private static final BiMap<MessageType, Class<? extends Message>> map;
    static {
        map = HashBiMap.create();

        for (MessageType m : MessageType.values()) {
            map.put(m, m.cl);
        }
    }

    public static <T extends Message> MessageType fromMessageClass(Class<T> cl) {
        return map.inverse().get(cl);
    }

    private MessageType(Class<? extends Message> cl) {
        this.cl = cl;
    }

    public Class<? extends Message> getCorrespondingClass() {
        return cl;
    }

    /***
     * Converts the JSON string into the appropriate Message class, where
     * events will respond to that specific messageType type.
     * @param str
     * @param gson
     * @return
     */
    public static Message fromJSON(String str, Gson gson) {
        // TODO: Thanks to https://stackoverflow.com/a/31094365 for the hint
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(str);
        JsonObject obj = element.getAsJsonObject();

        MessageType msgType = MessageType.valueOf(obj.get("msgType").getAsString());
        Class<? extends Message> c = msgType.getCorrespondingClass();

        if (c != null)
            return gson.fromJson(element, c);

        return null;
    }
}
