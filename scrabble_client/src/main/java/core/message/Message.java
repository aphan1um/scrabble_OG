package core.message;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.messageType.*;

public interface Message {
    public enum MessageType {
        REQUEST,
        CHAT,
        PING,
        STATUS,
        GAME_ACTION,
        GAME_VOTE,
        GAME_STATUS_CHANGED,
        ERROR;

        private static final BiMap<MessageType,Class<? extends Message>> classMaps;

        static {
            classMaps = ImmutableBiMap.<MessageType, Class<? extends Message>>builder()
                    .put(REQUEST, RequestPDMsg.class)
                    .put(CHAT, ChatMsg.class)
                    .put(PING, PingMsg.class)
                    .put(STATUS, PlayerStatusMsg.class)
                    .put(GAME_ACTION, GameActionMsg.class)
                    .put(GAME_VOTE, GameVoteMsg.class)
                    .put(GAME_STATUS_CHANGED, GameStatusMsg.class)
                    .put(ERROR, ErrorMsg.class).build();
        }
    }

    public static <T extends Message> MessageType fromMessageClass(Class<T> cl) {
        return MessageType.classMaps.inverse().get(cl);
    }

    public static Class<? extends Message> getEnumClass(MessageType mType) {
        return MessageType.classMaps.get(mType);
    }

    /***
     * Converts the JSON string into the appropriate Message class, where
     * events will respond to that specific messageType type.
     * @param str
     * @param gson
     * @return
     */
    public static MessageWrapper fromJSON(String str, Gson gson) {
        // TODO: Thanks to https://stackoverflow.com/a/31094365 for the hint

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(str);
        JsonObject obj = element.getAsJsonObject();

        // TODO: Constants need to be better placed
        String json_msgType = obj.get("msgType").getAsString();
        JsonObject json_msg = obj.getAsJsonObject("msg");

        Class<? extends Message> enumClass = getEnumClass(MessageType.valueOf(json_msgType));

        MessageWrapper recvMsg = new MessageWrapper(
                gson.fromJson(json_msg, enumClass));

        return recvMsg;
    }
}
