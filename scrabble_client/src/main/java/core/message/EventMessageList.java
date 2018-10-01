package core.message;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import core.message.Message;
import core.message.MessageEvent;
import core.message.MessageType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

// TODO: Handle thread-safety issues with add/removeEvent ??
public class EventMessageList {
    private Multimap<MessageType, MessageEvent> events;

    public EventMessageList() {
        events = HashMultimap.create();
    }

    public void fireEvent(Message msg) {
        for (MessageEvent e: events.get(msg.getMessageType())) {
            e.onServerReceive(msg);
        }
    }

    public <T extends Message> void addEvent(MessageEvent<T> event) {
        Type generic_type = ((ParameterizedType)
                event.getClass().getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
        Class<? extends Message> t = (Class<? extends Message>)generic_type;

        events.put(MessageType.fromMessageClass(t), event);
    }

    public <T extends Message> void removeEvent(MessageEvent<T> event) {
        Type generic_type = ((ParameterizedType)
                event.getClass().getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
        Class<? extends Message> t = (Class<? extends Message>)generic_type;

        events.remove(MessageType.fromMessageClass(t), event);
    }
}
