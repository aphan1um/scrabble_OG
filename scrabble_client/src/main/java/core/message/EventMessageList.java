package core.message;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import core.Player;
import core.message.Message;
import core.message.MessageEvent;
import core.message.MessageType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

// TODO: Handle thread-safety issues with add/removeEvent ??
public class EventMessageList {
    private Multimap<MessageType, MessageEvent> events;

    public EventMessageList() {
        events = HashMultimap.create();
    }

    public List<SendableMessage> fireEvent(Message msg, Set<Player> players, Player sender) {
        Collection<MessageEvent> eventsToFire = events.get(msg.getMessageType());
        List<SendableMessage> msgs = new ArrayList<>(eventsToFire.size());

        for (MessageEvent e: eventsToFire) {
            msgs.add(e.onMsgReceive(msg, players, sender));
        }

        return msgs;
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
